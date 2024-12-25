package one.tranic.mongoban.common.database;

import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.Operator;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.mongoban.api.data.PlayerInfo;
import one.tranic.mongoban.common.Collections;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseBanApplication {
    private final Database database;
    private final DatabaseService service;
    private final String collection = "mongo_ban";

    public DatabaseBanApplication(Database database, DatabaseService service) {
        this.database = database;
        this.service = service;
    }

    /**
     * Synchronously retrieves ban information for the specified player UUID from the database.
     *
     * @param uuid The UUID of the player whose ban information is to be retrieved.
     * @return A {@code PlayerBanInfo} object containing the player's ban details, including
     * operator, duration, and reason. Returns {@code null} if no ban information
     * exists for the given UUID.
     */
    public PlayerBanInfo findPlayerBanSync(UUID uuid) {
        Document query = new Document("id", uuid);
        Document banDoc = database.queryOne(this.collection, query);
        return banDoc != null ? new PlayerBanInfo(
                uuid,
                banDoc.get("operator", Operator.class),
                banDoc.getString("duration"),
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
    public CompletableFuture<PlayerBanInfo> findPlayerBanAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> findPlayerBanSync(uuid), MongoBanAPI.executor);
    }

    /**
     * Synchronously retrieves all players matching the specified IP address from the database.
     *
     * @param ip The IP address to search for within the player's record.
     * @return An array of {@code PlayerInfo} objects representing the players associated with the given IP address.
     * If no players are found, an empty array is returned.
     */
    public PlayerInfo[] findPlayerBanSync(InetAddress ip) {
        Document query = new Document("ip", new Document("$elemMatch", ip.getHostAddress()));
        List<Document> playerDocs = database.queryMany(this.collection, query);
        List<PlayerInfo> players = Collections.newArrayList();
        for (Document playerDoc : playerDocs) {
            players.add(new PlayerInfo(
                    playerDoc.getString("name"),
                    playerDoc.get("id", UUID.class),
                    playerDoc.getList("ip", String.class)
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
    public CompletableFuture<PlayerInfo[]> findPlayerBanAsync(InetAddress ip) {
        return CompletableFuture.supplyAsync(() -> findPlayerBanSync(ip), MongoBanAPI.executor);
    }

    /**
     * Bans a player synchronously with the specified details.
     *
     * @param uuid     The unique identifier of the player to be banned.
     * @param operator The operator or entity initiating the ban.
     * @param duration The duration of the ban as a string.
     */
    public void addPlayerBanSync(UUID uuid, Operator operator, String duration) {
        addPlayerBanSync(uuid, operator, duration, null, "<Banned by the server>");
    }

    /**
     * Synchronously adds a player ban to the database with the specified details.
     *
     * @param uuid     The UUID of the player to be banned.
     * @param operator The operator responsible for issuing the ban.
     * @param duration The duration of the ban in seconds.
     * @param ip       The IP address of the player being banned. Can be {@code null}.
     * @param reason   The reason for the ban. If {@code null}, a default reason
     *                 ("<Banned by the server>") will be used.
     */
    public void addPlayerBanSync(UUID uuid, Operator operator, String duration, @Nullable InetAddress ip, @Nullable String reason) {
        Document query = new Document("id", uuid);
        Document updateDoc = new Document()
                .append("operator", operator)
                .append("duration", duration)
                .append("ip", ip != null ? ip.getHostAddress() : null)
                .append("reason", reason != null ? reason : "<Banned by the server>");

        database.update(this.collection, query, updateDoc);
    }

    /**
     * Asynchronously adds a ban for a player identified by their UUID.
     *
     * @param uuid     the unique identifier of the player to be banned
     * @param operator the operator or moderator issuing the ban
     * @param duration the duration for which the ban will be effective
     * @return a CompletableFuture representing the pending completion of the ban operation
     */
    public CompletableFuture<Void> addPlayerBanAsync(UUID uuid, Operator operator, String duration) {
        return CompletableFuture.runAsync(() -> addPlayerBanSync(uuid, operator, duration), MongoBanAPI.executor);
    }

    /**
     * Adds a player ban asynchronously. This method schedules the ban operation to run on a separate thread
     * to avoid blocking the main thread.
     *
     * @param uuid     the unique identifier of the player to be banned
     * @param operator the operator performing the ban
     * @param duration the duration of the ban (e.g., permanent or specific time period)
     * @param ip       the IP address of the player being banned, or null if not applicable
     * @param reason   the reason for the ban, or null if no reason is specified
     * @return a CompletableFuture that completes when the ban operation has finished
     */
    public CompletableFuture<Void> addPlayerBanAsync(UUID uuid, Operator operator, String duration, @Nullable InetAddress ip, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addPlayerBanSync(uuid, operator, duration, ip, reason), MongoBanAPI.executor);
    }

    /**
     * Bans a player by their IP address synchronously.
     * <p>
     * This method first retrieves all players associated with the specified IP address
     * and applies the ban to each player's UUID.
     * <p>
     * Additionally, it adds the IP to the banned IP list.
     *
     * @param ip       The IP address of the player to be banned.
     * @param operator The operator initiating the ban.
     * @param duration The duration of the ban in a string format.
     * @param reason   The reason for the ban, or null if no reason is provided.
     */
    public void addPlayerBanSync(InetAddress ip, Operator operator, String duration, @Nullable String reason) {
        PlayerInfo[] players = findPlayerBanSync(ip);
        for (PlayerInfo player : players) {
            addPlayerBanSync(player.uuid(), operator, duration, ip, reason);
        }

        addIPBanSync(ip, operator, duration, reason);
    }

    /**
     * Asynchronously adds a ban for a player based on their IP address.
     *
     * @param ip       the IP address of the player to be banned
     * @param operator the operator issuing the ban
     * @param duration the duration of the ban
     * @param reason   the reason for the ban; can be null if no reason is provided
     * @return a CompletableFuture that completes when the ban operation is finished
     */
    public CompletableFuture<Void> addPlayerBanAsync(InetAddress ip, Operator operator, String duration, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addPlayerBanSync(ip, operator, duration, reason), MongoBanAPI.executor);
    }

    /**
     * Synchronously removes a player ban for the specified player UUID.
     *
     * @param playerId The {@link UUID} of the player to be unbanned.
     */
    public void removePlayerBanSync(@NotNull UUID playerId) {
        Document query = new Document("id", playerId);
        Document result = database.queryOne(this.collection, query);
        if (result != null) database.delete(this.collection, query);
    }

    /**
     * Asynchronously removes a ban for the specified player.
     *
     * @param playerId the unique identifier of the player whose ban is to be removed; must not be null
     * @return a CompletableFuture representing the asynchronous operation that completes when the ban is removed
     */
    public CompletableFuture<Void> removePlayerBanAsync(@NotNull UUID playerId) {
        return CompletableFuture.runAsync(() -> removePlayerBanSync(playerId), MongoBanAPI.executor);
    }

    /**
     * Synchronously removes the ban for the specified IP address and all players associated with it.
     *
     * @param ip The {@link InetAddress} to remove the ban for.
     */
    public void removePlayerBanSync(@NotNull InetAddress ip) {
        PlayerInfo[] players = findPlayerBanSync(ip);
        for (PlayerInfo player : players) removePlayerBanSync(player.uuid());

        Document query = new Document("ip", ip.getHostAddress());
        Document result = database.queryOne(this.collection, query);
        if (result != null) database.delete(this.collection, query);
    }

    /**
     * Asynchronously removes a player's ban identified by their IP address.
     *
     * @param ip The IP address of the player whose ban is to be removed. Must not be null.
     * @return A CompletableFuture that completes when the ban is successfully removed.
     */
    public CompletableFuture<Void> removePlayerBanAsync(@NotNull InetAddress ip) {
        return CompletableFuture.runAsync(() -> removePlayerBanSync(ip), MongoBanAPI.executor);
    }

    /**
     * Adds an IP ban synchronously with the specified parameters.
     *
     * @param ip       The IP address to be banned.
     * @param operator The operator issuing the ban.
     * @param duration The duration of the ban.
     */
    public void addIPBanSync(InetAddress ip, Operator operator, String duration) {
        addIPBanSync(ip, operator, duration, "<Banned by the server>");
    }

    /**
     * Bans an IP address synchronously in the database with the provided details.
     *
     * @param ip       The IP address to be banned.
     * @param operator The operator initiating the ban.
     * @param duration The duration of the ban.
     * @param reason   The reason for the ban. If null, a default message "<Banned by the server>" will be used.
     */
    public void addIPBanSync(InetAddress ip, Operator operator, String duration, @Nullable String reason) {
        Document query = new Document("ip", ip.getHostAddress());
        Document updateDoc = new Document()
                .append("operator", operator)
                .append("duration", duration)
                .append("reason", reason != null ? reason : "<Banned by the server>");

        database.update(this.collection, query, updateDoc);
    }

    /**
     * Asynchronously adds a ban to the specified IP address with the provided details.
     * <p>
     * This method runs the operation in a separate thread to avoid blocking the main thread.
     *
     * @param ip       The IP address to be banned.
     * @param operator The operator responsible for issuing the ban.
     * @param duration The duration of the ban represented as a string.
     * @return A {@link CompletableFuture} that completes when the IP ban operation has finished.
     */
    public CompletableFuture<Void> addIPBanAsync(InetAddress ip, Operator operator, String duration) {
        return CompletableFuture.runAsync(() -> addIPBanSync(ip, operator, duration), MongoBanAPI.executor);
    }

    /**
     * Asynchronously adds a ban for a specified IP address.
     * The operation is performed on a separate thread to avoid blocking the main execution.
     * <p>
     * The ban details include the operator, duration, and an optional reason for the ban.
     *
     * @param ip       The IP address to be banned.
     * @param operator The operator issuing the ban.
     * @param duration The duration of the ban in a string format.
     * @param reason   The optional reason for the ban. If null, a default reason may be applied.
     * @return A {@link CompletableFuture} that completes when the ban operation is finished.
     */
    public CompletableFuture<Void> addIPBanAsync(InetAddress ip, Operator operator, String duration, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addIPBanSync(ip, operator, duration, reason), MongoBanAPI.executor);
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
        Document query = new Document("ip", ip.getHostAddress());
        Document banDoc = database.queryOne(this.collection, query);
        return banDoc != null ? new IPBanInfo(
                ip.getHostAddress(),
                banDoc.get("operator", Operator.class),
                banDoc.getString("duration"),
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
        return CompletableFuture.supplyAsync(() -> getIPBanInfoSync(ip), MongoBanAPI.executor);
    }
}
