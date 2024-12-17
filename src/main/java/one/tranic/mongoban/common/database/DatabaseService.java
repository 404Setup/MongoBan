package one.tranic.mongoban.common.database;

import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.mongoban.api.data.PlayerInfo;
import one.tranic.mongoban.common.Collections;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DatabaseService {
    private final Database database;
    private final String collectionBan = "mongo_ban";
    private final String collectionWarn = "mongo_warn";
    private final String collectionPlayer = "mongo_player";

    private final Executor executor = Executors.newCachedThreadPool(Thread.ofVirtual().factory());

    public DatabaseService(Database database) {
        this.database = database;

        // I don't think they are needed...
        // In one of my forks, I found through the database control panel that the indexes were not used,
        // so I commented them out for now.
        /* this.database.getDB().getCollection(this.collectionBan).createIndex(Indexes.ascending("id"),
        new IndexOptions().unique(true));
        this.database.getDB().getCollection(this.collectionBan).createIndex(Indexes.ascending("ip"),
        new IndexOptions().unique(true));
        this.database.getDB().getCollection(this.collectionWarn).createIndex(Indexes.ascending("id"),
        new IndexOptions().unique(true));*/
    }

    public PlayerInfo[] findPlayersByIPSync(InetAddress ip) {
        Document query = new Document("ip", new Document("$elemMatch", ip));
        List<Document> playerDocs = database.queryMany(this.collectionPlayer, query);
        List<PlayerInfo> players = Collections.newArrayList();
        for (Document playerDoc : playerDocs) {
            players.add(new PlayerInfo(
                    playerDoc.getString("name"),
                    playerDoc.get("id", UUID.class),
                    (InetAddress[]) playerDoc.get("ip")
            ));
        }
        return players.toArray(new PlayerInfo[0]);
    }

    public CompletableFuture<PlayerInfo[]> findPlayersByIPAsync(InetAddress ip) {
        return CompletableFuture.supplyAsync(() -> findPlayersByIPSync(ip), executor);
    }

    public PlayerInfo getPlayerSync(UUID uuid) {
        Document query = new Document("id", uuid);
        Document playerDoc = database.queryOne(this.collectionPlayer, query);
        return playerDoc != null ? new PlayerInfo(
                playerDoc.getString("name"),
                uuid,
                (InetAddress[]) playerDoc.get("ip")
        ) : null;
    }

    public CompletableFuture<PlayerInfo> getPlayerAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getPlayerSync(uuid), executor);
    }


    public void addPlayerBanSync(UUID uuid, UUID operator, int duration) {
        addPlayerBanSync(uuid, operator, duration, null, "<Banned by the server>");
    }

    public void addPlayerBanSync(UUID uuid, UUID operator, int duration, @Nullable InetAddress ip, @Nullable String reason) {
        Document query = new Document("id", uuid);
        Document updateDoc = new Document()
                .append("operator", operator)
                .append("duration", duration)
                .append("ip", ip)
                .append("reason", reason != null ? reason : "<Banned by the server>");

        database.update(this.collectionBan, query, updateDoc);
    }

    public CompletableFuture<Void> addBanAsync(UUID uuid, UUID operator, int duration) {
        return CompletableFuture.runAsync(() -> addPlayerBanSync(uuid, operator, duration), executor);
    }

    public CompletableFuture<Void> addBanAsync(UUID uuid, UUID operator, int duration, @Nullable InetAddress ip, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addPlayerBanSync(uuid, operator, duration, ip, reason), executor);
    }

    /**
     * Blocks an IP address and bans all players associated with the IP synchronously.
     *
     * <p>This method finds all players linked to the provided IP address and bans them individually,
     * then applies an IP-wide ban, all executed in a blocking manner.</p>
     *
     * @param ip       The {@link InetAddress} of the IP to be banned.
     * @param operator The {@link UUID} of the operator performing the ban.
     * @param duration The duration (in seconds) for which the ban should last.
     * @param reason   The reason for the ban, or {@code null} if no specific reason is provided.
     *
     * @throws IllegalArgumentException If the provided IP or operator is {@code null}.
     */
    public void addIPBanWithAllSync(InetAddress ip, UUID operator, int duration, @Nullable String reason) {
        PlayerInfo[] players = findPlayersByIPSync(ip);
        for (PlayerInfo player : players) {
            addPlayerBanSync(player.uuid(), operator, duration, ip, reason);
        }

        addIPBanSync(ip, operator, duration, reason);
    }

    /**
     * Asynchronously blocks an IP address and bans all players associated with the IP.
     *
     * <p>This method schedules a task to find all players linked to the provided IP address and bans them individually,
     * then applies an IP-wide ban. The process runs in a separate thread using the pre-configured executor.</p>
     *
     * <p>Because it operates asynchronously, this method returns a {@link CompletableFuture} that can be used to
     * monitor the completion of the entire operation.</p>
     *
     * @param ip       The {@link InetAddress} of the IP to be banned.
     * @param operator The {@link UUID} of the operator performing the ban.
     * @param duration The duration (in seconds) for which the ban should last.
     * @param reason   The reason for the ban, or {@code null} if no specific reason is provided.
     *
     * @return A {@link CompletableFuture} that completes when the IP ban and all associated operations are finished.
     *
     * @throws IllegalArgumentException If the provided IP or operator is {@code null}.
     */
    public CompletableFuture<Void> addIPBanWithAllAsync(InetAddress ip, UUID operator, int duration, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addIPBanWithAllSync(ip, operator, duration, reason), executor);
    }

    /**
     * Synchronously adds an IP ban to the database with a default ban reason.
     *
     * @param ip       The IP address to be banned.
     * @param operator The UUID of the operator performing the ban.
     * @param duration The duration of the ban in seconds.
     */
    public void addIPBanSync(InetAddress ip, UUID operator, int duration) {
        addIPBanSync(ip, operator, duration, "<Banned by the server>");
    }

    /**
     * Synchronously adds an IP ban to the database with a specified ban reason.
     *
     * @param ip       The IP address to be banned.
     * @param operator The UUID of the operator performing the ban.
     * @param duration The duration of the ban in seconds.
     * @param reason   The reason for the ban. If {@code null}, a default reason
     *                 ("<Banned by the server>") will be used.
     */
    public void addIPBanSync(InetAddress ip, UUID operator, int duration, @Nullable String reason) {
        Document query = new Document("ip", ip);
        Document updateDoc = new Document()
                .append("operator", operator)
                .append("duration", duration)
                .append("reason", reason != null ? reason : "<Banned by the server>");

        database.update(this.collectionBan, query, updateDoc);
    }

    /**
     * Asynchronously adds an IP ban to the database with a default ban reason.
     * This operation runs in a separate thread.
     *
     * @param ip       The IP address to be banned.
     * @param operator The UUID of the operator performing the ban.
     * @param duration The duration of the ban in seconds.
     * @return A {@link CompletableFuture} that completes when the operation is finished.
     */
    public CompletableFuture<Void> addIPBanAsync(InetAddress ip, UUID operator, int duration) {
        return CompletableFuture.runAsync(() -> addIPBanSync(ip, operator, duration), executor);
    }

    /**
     * Asynchronously adds an IP ban to the database with a specified ban reason.
     * This operation runs in a separate thread.
     *
     * @param ip       The IP address to be banned.
     * @param operator The UUID of the operator performing the ban.
     * @param duration The duration of the ban in seconds.
     * @param reason   The reason for the ban. If {@code null}, a default reason
     *                 ("<Banned by the server>") will be used.
     * @return A {@link CompletableFuture} that completes when the operation is finished.
     */
    public CompletableFuture<Void> addIPBanAsync(InetAddress ip, UUID operator, int duration, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addIPBanSync(ip, operator, duration, reason), executor);
    }

    public PlayerBanInfo getPlayerBanSync(UUID uuid) {
        Document query = new Document("id", uuid);
        Document banDoc = database.queryOne(this.collectionBan, query);
        return banDoc != null ? new PlayerBanInfo(
                uuid,
                banDoc.get("operator", UUID.class),
                banDoc.getInteger("duration"),
                banDoc.getString("reason")
        ) : null;
    }

    public CompletableFuture<PlayerBanInfo> getPlayerBanAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getPlayerBanSync(uuid), executor);
    }

    public IPBanInfo getIPBanInfoSync(InetAddress ip) {
        Document query = new Document("ip", ip);
        Document banDoc = database.queryOne(this.collectionBan, query);
        return banDoc != null ? new IPBanInfo(
                ip.getHostAddress(),
                banDoc.get("operator", UUID.class),
                banDoc.getInteger("duration"),
                banDoc.getString("reason")
        ) : null;
    }

    public CompletableFuture<IPBanInfo> getIPBanInfoAsync(InetAddress ip) {
        return CompletableFuture.supplyAsync(() -> getIPBanInfoSync(ip), executor);
    }
}
