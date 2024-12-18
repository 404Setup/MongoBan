package one.tranic.mongoban.common.database;

import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.mongoban.api.data.PlayerInfo;
import one.tranic.mongoban.api.data.PlayerWarnInfo;
import one.tranic.mongoban.common.Collections;
import one.tranic.mongoban.common.Rand;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * The {@code DatabaseService} class provides functionality for managing player data, bans,
 * and warnings in a database. It supports both synchronous and asynchronous operations for
 * retrieving and modifying player information, as well as imposing bans based on player UUIDs
 * or IP addresses.
 * <p>
 * This class is designed for efficient interaction with a database, using custom collections for bans,
 * warnings, and player records, executed with a configurable executor for asynchronous operations.
 * <p>
 * Fields:
 * <p>
 * - {@code database}: The database instance used to store and retrieve data.
 * <p>
 * - {@code collectionBan}: The collection for storing player bans data.
 * <p>
 * - {@code collectionWarn}: The collection for storing player warning data.
 * <p>
 * - {@code collectionPlayer}: The collection for storing player information.
 * <p>
 * - {@code executor}: The {@link Executor} instance used to run asynchronous tasks.
 * <p>
 * Constructor:
 * - {@link #DatabaseService(Database)}: Initializes a new {@code DatabaseService} instance
 * with the specified database.
 * <p>
 * Key Features:
 * <p>
 * - Synchronous and asynchronous retrieval of player data by IP address.
 * <p>
 * - Synchronous and asynchronous retrieval of player data by UUID.
 * <p>
 * - Synchronous and asynchronous methods for adding player bans with configurable reasons, durations, and IP bindings.
 * <p>
 * - Capability to impose IP-wide bans, blocking associated players.
 * <p>
 * - Retrieval of ban details for banned players.
 */
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

    /**
     * Synchronously adds a warning to the database for a player, with a generated warning ID.
     *
     * @param playerId The UUID of the player to be warned.
     * @param duration The duration of the warning in seconds.
     * @param reason   The reason for the warning.
     */
    public void addPlayerWarnSync(UUID playerId, int duration, String reason) {
        String warnId = Rand.generateRandomWarnId(21);
        Document query = new Document("id", warnId);
        Document warnDoc = new Document()
                .append("playerId", playerId)
                .append("duration", duration)
                .append("reason", reason);

        database.update(this.collectionWarn, query, warnDoc);
    }

    /**
     * Asynchronously adds a warning for a player with the specified duration and reason.
     *
     * @param playerId The unique identifier of the player to warn.
     * @param duration The duration of the warning in minutes.
     * @param reason   The reason for the warning.
     * @return A CompletableFuture that completes when the warning has been successfully added.
     */
    public CompletableFuture<Void> addPlayerWarnAsync(UUID playerId, int duration, String reason) {
        return CompletableFuture.runAsync(() -> addPlayerWarnSync(playerId, duration, reason), executor);
    }

    /**
     * Synchronously finds the warning in the database using the provided warnId.
     *
     * @param warnId The warning ID to search for.
     * @return The {@code PlayerWarnInfo} record containing the warning details, or {@code null} if none is found.
     */
    public PlayerWarnInfo findPlayerWarnSync(String warnId) {
        Document query = new Document("id", warnId);
        Document warnDoc = database.queryOne(this.collectionWarn, query);
        return warnDoc != null ? new PlayerWarnInfo(
                warnDoc.get("playerId", UUID.class),
                warnDoc.get("operator", UUID.class),
                warnDoc.getInteger("duration"),
                warnDoc.getString("reason")
        ) : null;
    }

    /**
     * Asynchronously retrieves warning information for a specific player based on the provided warning ID.
     *
     * @param warnId The unique identifier of the player's warning.
     * @return A CompletableFuture containing the PlayerWarnInfo associated with the specified warning ID.
     */
    public CompletableFuture<PlayerWarnInfo> findPlayerWarnAsync(String warnId) {
        return CompletableFuture.supplyAsync(() -> findPlayerWarnSync(warnId), executor);
    }

    /**
     * Synchronously finds all warnings in the database for the specified playerId.
     *
     * @param playerId The UUID of the player to search for.
     * @return An array of {@code PlayerWarnInfo} records containing the player's warning details.
     */
    public PlayerWarnInfo[] findPlayerWarnSync(UUID playerId) {
        Document query = new Document("playerId", playerId);
        List<Document> warnDocs = database.queryMany(this.collectionWarn, query);
        List<PlayerWarnInfo> warnings = Collections.newArrayList();
        for (Document warnDoc : warnDocs) {
            warnings.add(new PlayerWarnInfo(
                    playerId,
                    warnDoc.get("operator", UUID.class),
                    warnDoc.getInteger("duration"),
                    warnDoc.getString("reason")
            ));
        }
        return warnings.toArray(new PlayerWarnInfo[0]);
    }

    /**
     * Asynchronously retrieves the warning information for a player based on their unique ID.
     *
     * @param playerId the unique identifier (UUID) of the player whose warnings are to be retrieved
     * @return a CompletableFuture that, when completed, will contain an array of PlayerWarnInfo objects representing the player's warning information
     */
    public CompletableFuture<PlayerWarnInfo[]> findPlayerWarnAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> findPlayerWarnSync(playerId), executor);
    }

    /**
     * Synchronously removes a warning from the database using the provided warning ID.
     *
     * @param warnId The unique identifier of the warning to be removed.
     */
    public void removePlayerWarnSync(String warnId) {
        Document query = new Document("id", warnId);
        database.delete(this.collectionWarn, query);
    }

    /**
     * Asynchronously removes a warning assigned to a player.
     *
     * @param warnId The unique identifier of the player's warning to be removed.
     * @return A CompletableFuture that completes when the warning has been removed.
     */
    public CompletableFuture<Void> removePlayerWarnAsync(String warnId) {
        return CompletableFuture.runAsync(() -> removePlayerWarnSync(warnId), executor);
    }

    /**
     * Synchronously removes all warnings associated with the specified player ID from the database.
     *
     * @param playerId The UUID of the player whose warnings are to be removed.
     */
    public void removePlayerWarnSync(UUID playerId) {
        Document query = new Document("playerId", playerId);
        database.deleteMany(this.collectionWarn, query);
    }

    /**
     * Asynchronously removes a warning for a player identified by their unique ID.
     *
     * @param playerId the unique identifier of the player whose warning is to be removed
     * @return a CompletableFuture that completes when the player's warning has been removed
     */
    public CompletableFuture<Void> removePlayerWarnAsync(UUID playerId) {
        return CompletableFuture.runAsync(() -> removePlayerWarnSync(playerId), executor);
    }

    /**
     * Synchronously retrieves player information from the database based on the specified UUID.
     *
     * @param uuid The UUID of the player whose information is being retrieved.
     * @return A {@code PlayerInfo} object containing the player's data, including name, UUID, and IP addresses.
     * Returns {@code null} if no player matches the provided UUID.
     */
    public PlayerInfo getPlayerSync(UUID uuid) {
        Document query = new Document("id", uuid);
        Document playerDoc = database.queryOne(this.collectionPlayer, query);
        return playerDoc != null ? new PlayerInfo(
                playerDoc.getString("name"),
                uuid,
                (InetAddress[]) playerDoc.get("ip")
        ) : null;
    }

    /**
     * Asynchronously retrieves player information associated with the specified UUID.
     * The operation is executed in a separate thread using the configured executor.
     *
     * @param uuid The UUID of the player whose information is to be retrieved.
     * @return A {@link CompletableFuture} that completes with the {@link PlayerInfo}
     * corresponding to the given UUID, or {@code null} if no player is found.
     */
    public CompletableFuture<PlayerInfo> getPlayerAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getPlayerSync(uuid), executor);
    }

    /**
     * Synchronously adds a player ban to the database with a default ban reason.
     *
     * @param uuid     The UUID of the player to ban.
     * @param operator The UUID of the operator performing the ban.
     * @param duration The duration of the ban in seconds.
     */
    public void addPlayerBanSync(UUID uuid, UUID operator, int duration) {
        addPlayerBanSync(uuid, operator, duration, null, "<Banned by the server>");
    }

    /**
     * Synchronously adds a player ban to the database with a specified ban reason
     * and optionally an associated IP address.
     *
     * @param uuid     The UUID of the player to ban.
     * @param operator The UUID of the operator performing the ban.
     * @param duration The duration of the ban in seconds.
     * @param ip       The IP address associated with the ban, or {@code null} if none.
     * @param reason   The reason for the ban, or {@code null} to use the default reason ("<Banned by the server>").
     */
    public void addPlayerBanSync(UUID uuid, UUID operator, int duration, @Nullable InetAddress ip, @Nullable String reason) {
        Document query = new Document("id", uuid);
        Document updateDoc = new Document()
                .append("operator", operator)
                .append("duration", duration)
                .append("ip", ip)
                .append("reason", reason != null ? reason : "<Banned by the server>");

        database.update(this.collectionBan, query, updateDoc);
    }

    /**
     * Asynchronously adds a player ban to the database with a default ban reason.
     * The operation runs in a separate thread.
     *
     * @param uuid     The UUID of the player to ban.
     * @param operator The UUID of the operator performing the ban.
     * @param duration The duration of the ban in seconds.
     * @return A {@link CompletableFuture} that completes when the operation is finished.
     */
    public CompletableFuture<Void> addPlayerBanAsync(UUID uuid, UUID operator, int duration) {
        return CompletableFuture.runAsync(() -> addPlayerBanSync(uuid, operator, duration), executor);
    }

    /**
     * Asynchronously adds a player ban to the database with a specified ban reason
     * and optionally an associated IP address. The operation runs in a separate thread.
     *
     * @param uuid     The UUID of the player to ban.
     * @param operator The UUID of the operator performing the ban.
     * @param duration The duration of the ban in seconds.
     * @param ip       The IP address associated with the ban, or {@code null} if none.
     * @param reason   The reason for the ban, or {@code null} to use the default reason ("<Banned by the server>").
     * @return A {@link CompletableFuture} that completes when the operation is finished.
     */
    public CompletableFuture<Void> addPlayerBanAsync(UUID uuid, UUID operator, int duration, @Nullable InetAddress ip, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addPlayerBanSync(uuid, operator, duration, ip, reason), executor);
    }

    /**
     * Synchronously retrieves all players matching the specified IP address from the database.
     *
     * @param ip The IP address to search for within the player's record.
     * @return An array of {@code PlayerInfo} objects representing the players associated with the given IP address.
     * If no players are found, an empty array is returned.
     */
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

    /**
     * Asynchronously retrieves an array of {@link PlayerInfo} objects representing players
     * associated with the specified IP address.
     *
     * @param ip The {@link InetAddress} used to look up players.
     * @return A {@link CompletableFuture} that completes with an array of {@link PlayerInfo}
     * objects corresponding to the players associated with the given IP address.
     */
    public CompletableFuture<PlayerInfo[]> findPlayersByIPAsync(InetAddress ip) {
        return CompletableFuture.supplyAsync(() -> findPlayersByIPSync(ip), executor);
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
     * @return A {@link CompletableFuture} that completes when the IP ban and all associated operations are finished.
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

    /**
     * Synchronously retrieves ban information for the specified player UUID from the database.
     *
     * @param uuid The UUID of the player whose ban information is to be retrieved.
     * @return A {@code PlayerBanInfo} object containing the player's ban details, including
     * operator, duration, and reason. Returns {@code null} if no ban information
     * exists for the given UUID.
     */
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

    /**
     * Asynchronously retrieves ban information associated with the specified player UUID.
     * The operation is executed in a separate thread using the configured executor.
     *
     * @param uuid The UUID of the player whose ban information is being retrieved.
     * @return A {@link CompletableFuture} that completes with a {@link PlayerBanInfo}
     * object containing the player's ban details. If no ban is found, the future
     * may complete with {@code null}.
     */
    public CompletableFuture<PlayerBanInfo> getPlayerBanAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getPlayerBanSync(uuid), executor);
    }

    /**
     * Synchronously retrieves information about a banned IP address from the database.
     *
     * @param ip The IP address for which the ban information is to be retrieved.
     * @return An {@code IPBanInfo} record containing the IP address, operator UUID,
     * ban duration in seconds, and the reason for the ban. Returns {@code null}
     * if no ban record is found for the given IP address.
     */
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

    /**
     * Asynchronously retrieves the ban information associated with the specified IP address.
     * The operation is executed in a separate thread using the configured executor.
     *
     * @param ip The {@link InetAddress} of the IP address to retrieve ban information for.
     * @return A {@link CompletableFuture} that completes with an {@link IPBanInfo} object
     * containing the details of the ban, including the IP address, operator, duration, and reason.
     */
    public CompletableFuture<IPBanInfo> getIPBanInfoAsync(InetAddress ip) {
        return CompletableFuture.supplyAsync(() -> getIPBanInfoSync(ip), executor);
    }
}
