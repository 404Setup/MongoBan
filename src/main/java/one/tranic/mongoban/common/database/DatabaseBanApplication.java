package one.tranic.mongoban.common.database;

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
    public CompletableFuture<PlayerBanInfo> findPlayerBanAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> findPlayerBanSync(uuid), service.executor);
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
                    (String[]) playerDoc.get("ip")
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
        return CompletableFuture.supplyAsync(() -> findPlayerBanSync(ip), service.executor);
    }

    /**
     * Synchronously adds a player ban to the database with a default ban reason.
     *
     * @param uuid     The UUID of the player to be banned.
     * @param operator The operator performing the ban.
     * @param duration The duration of the ban in seconds.
     */
    public void addPlayerBanSync(UUID uuid, Operator operator, int duration) {
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
    public void addPlayerBanSync(UUID uuid, Operator operator, int duration, @Nullable InetAddress ip, @Nullable String reason) {
        Document query = new Document("id", uuid);
        Document updateDoc = new Document()
                .append("operator", operator)
                .append("duration", duration)
                .append("ip", ip != null ? ip.getHostAddress() : null)
                .append("reason", reason != null ? reason : "<Banned by the server>");

        database.update(this.collection, query, updateDoc);
    }

    /**
     * Asynchronously adds a ban for a player to the database.
     * <p>
     * The ban is applied to the specified UUID, with details about the operator
     * performing the action and the duration of the ban.
     * This operation runs in a separate thread using the configured executor of the service.
     *
     * @param uuid     The unique identifier of the player to be banned.
     * @param operator The operator who is issuing the ban.
     * @param duration The duration of the ban in seconds.
     * @return A {@link CompletableFuture} that completes when the ban is successfully added.
     */
    public CompletableFuture<Void> addPlayerBanAsync(UUID uuid, Operator operator, int duration) {
        return CompletableFuture.runAsync(() -> addPlayerBanSync(uuid, operator, duration), service.executor);
    }

    /**
     * Asynchronously adds a player ban to the database. This operation is executed in a separate
     * thread using the configured executor.
     *
     * @param uuid     The unique identifier of the player to be banned.
     * @param operator The operator responsible for initiating the ban.
     * @param duration The duration of the ban in seconds.
     * @param ip       The IP address associated with the player to be banned. This parameter is
     *                 nullable.
     * @param reason   The reason for the ban. If {@code null}, a default reason ("<Banned by the
     *                 server>") will be used.
     * @return A {@link CompletableFuture} that completes when the operation is successfully finished.
     */
    public CompletableFuture<Void> addPlayerBanAsync(UUID uuid, Operator operator, int duration, @Nullable InetAddress ip, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addPlayerBanSync(uuid, operator, duration, ip, reason), service.executor);
    }

    /**
     * Adds a synchronous ban for a player or players associated with a specified IP address.
     * <p>
     * The method retrieves all players linked to the provided IP address, applies bans on each
     * player's unique identifier (UUID), and then applies an IP ban for the given IP address.
     *
     * @param ip       The IP address associated with the player(s) to be banned.
     * @param operator The operator responsible for performing the ban.
     * @param duration The duration of the ban in seconds.
     * @param reason   An optional reason for the ban. If null, a default reason will be used.
     */
    public void addPlayerBanSync(InetAddress ip, Operator operator, int duration, @Nullable String reason) {
        PlayerInfo[] players = findPlayerBanSync(ip);
        for (PlayerInfo player : players) {
            addPlayerBanSync(player.uuid(), operator, duration, ip, reason);
        }

        addIPBanSync(ip, operator, duration, reason);
    }

    /**
     * Asynchronously adds a player ban associated with a specific IP address to the database.
     * This operation is executed in a separate thread using the configured executor.
     *
     * @param ip       The IP address to be banned.
     * @param operator The operator responsible for issuing the ban.
     * @param duration The duration of the ban in seconds.
     * @param reason   The reason for the ban. If {@code null}, a default reason
     *                 will be applied.
     * @return A {@link CompletableFuture} that completes when the operation is finished.
     */
    public CompletableFuture<Void> addPlayerBanAsync(InetAddress ip, Operator operator, int duration, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addPlayerBanSync(ip, operator, duration, reason), service.executor);
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
        return CompletableFuture.runAsync(() -> removePlayerBanSync(playerId), service.executor);
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
        return CompletableFuture.runAsync(() -> removePlayerBanSync(ip), service.executor);
    }

    /**
     * Adds a synchronized IP ban with a specified duration and a default ban reason.
     *
     * @param ip       The IP address to be banned.
     * @param operator The operator or administrator imposing the ban.
     * @param duration The duration of the ban in seconds.
     */
    public void addIPBanSync(InetAddress ip, Operator operator, int duration) {
        addIPBanSync(ip, operator, duration, "<Banned by the server>");
    }

    /**
     * Synchronously adds a ban for a specified IP address to the database.
     *
     * @param ip       The IP address to be banned.
     * @param operator The operator responsible for issuing the ban.
     * @param duration The duration of the ban in seconds.
     * @param reason   An optional reason for the ban. If null, a default
     *                 reason ("<Banned by the server>") will be used.
     */
    public void addIPBanSync(InetAddress ip, Operator operator, int duration, @Nullable String reason) {
        Document query = new Document("ip", ip.getHostAddress());
        Document updateDoc = new Document()
                .append("operator", operator)
                .append("duration", duration)
                .append("reason", reason != null ? reason : "<Banned by the server>");

        database.update(this.collection, query, updateDoc);
    }

    /**
     * Asynchronously adds a ban for the specified IP address to the database.
     * The operation is executed in a separate thread using the configured executor.
     *
     * @param ip       The IP address to be banned.
     * @param operator The operator responsible for issuing the ban.
     * @param duration The duration of the ban in seconds.
     * @return A {@link CompletableFuture} that completes when the operation is finished.
     */
    public CompletableFuture<Void> addIPBanAsync(InetAddress ip, Operator operator, int duration) {
        return CompletableFuture.runAsync(() -> addIPBanSync(ip, operator, duration), service.executor);
    }

    /**
     * Asynchronously adds a ban for the specified IP address to the database.
     * The method runs in a separate thread using the configured executor of the service.
     *
     * @param ip       The IP address to be banned.
     * @param operator The operator responsible for issuing the ban.
     * @param duration The duration of the ban in seconds.
     * @param reason   An optional reason for the ban. If {@code null}, a default reason will be used.
     * @return A {@code CompletableFuture<Void>} that completes when the IP ban is successfully added.
     */
    public CompletableFuture<Void> addIPBanAsync(InetAddress ip, Operator operator, int duration, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addIPBanSync(ip, operator, duration, reason), service.executor);
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
        return CompletableFuture.supplyAsync(() -> getIPBanInfoSync(ip), service.executor);
    }
}
