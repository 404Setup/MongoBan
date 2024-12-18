package one.tranic.mongoban.common.database;

import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.data.PlayerInfo;
import one.tranic.mongoban.common.Collections;
import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabasePlayerApplication {
    private final Database database;
    private final DatabaseService service;
    private final String collection = "mongo_player";

    /**
     * Constructs a new instance of the DatabasePlayerApplication with the specified database and service.
     *
     * @param database The database instance to be used for managing player data.
     * @param service  The database service instance to handle database operations.
     */
    public DatabasePlayerApplication(Database database, DatabaseService service) {
        this.database = database;
        this.service = service;
    }

    /**
     * Synchronously adds or updates player information in the database.
     * <p>
     * This method either creates a new entry for the specified player if no existing data is found,
     * or updates the existing document with the player's name and IP address.
     * <p>
     * IP management is handled to ensure that a player can have up to six IP addresses stored.
     * <p>
     * If the limit is exceeded, the oldest IP is removed to accommodate the new one.
     *
     * @param name The name of the player to be added or updated.
     * @param uuid The unique identifier (UUID) of the player.
     * @param ip   The IP address of the player to be included in the database.
     */
    public void addPlayerSync(String name, UUID uuid, String ip) {
        Document query = new Document("id", uuid);
        Document playerDoc = database.queryOne(this.collection, query);
        Document updateDoc;
        if (playerDoc == null) {
            updateDoc = new Document()
                    .append("name", name)
                    .append("ip", new String[]{ip});
        } else {
            List<String> ips = Collections.newArrayList((String[]) playerDoc.get("ip"));
            if (ips.size() >= 6) ips.removeFirst();
            ips.add(ip);
            updateDoc = new Document()
                    .append("name", name)
                    .append("ip", ips.toArray(new String[0]));
        }
        database.update(this.collection, query, updateDoc);
    }

    /**
     * Asynchronously adds a player to the database.
     * <p>
     * The operation is executed in a separate thread using the configured executor.
     *
     * @param name The name of the player to add.
     * @param uuid The unique identifier (UUID) of the player to add.
     * @param ip   The IP address of the player to add.
     * @return A {@code CompletableFuture<Void>} representing the completion of the operation.
     */
    public CompletableFuture<Void> addPlayerAsync(String name, UUID uuid, String ip) {
        return CompletableFuture.runAsync(() -> addPlayerSync(name, uuid, ip), MongoBanAPI.executor);
    }

    /**
     * Synchronously retrieves player information from the database based on the specified name.
     *
     * @param name The name of the player whose information is being retrieved.
     * @return A {@code PlayerInfo} object containing the player's data, including name, UUID, and IP addresses.
     * Returns {@code null} if no player matches the provided name.
     */
    public PlayerInfo getPlayerSync(String name) {
        Document query = new Document("name", name);
        Document playerDoc = database.queryOne(this.collection, query);
        return playerDoc != null ? new PlayerInfo(
                name,
                playerDoc.get("id", UUID.class),
                (String[]) playerDoc.get("ip")
        ) : null;
    }

    /**
     * Asynchronously retrieves player information associated with the specified name.
     * <p>
     * The operation is executed in a separate thread using the configured executor.
     *
     * @param name The name of the player whose information is to be retrieved.
     * @return A {@link CompletableFuture} that completes with the {@link PlayerInfo}
     * corresponding to the given name, or {@code null} if no player is found.
     */
    public CompletableFuture<PlayerInfo> getPlayerAsync(String name) {
        return CompletableFuture.supplyAsync(() -> getPlayerSync(name), MongoBanAPI.executor);
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
        Document playerDoc = database.queryOne(this.collection, query);
        return playerDoc != null ? new PlayerInfo(
                playerDoc.getString("name"),
                uuid,
                (String[]) playerDoc.get("ip")
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
        return CompletableFuture.supplyAsync(() -> getPlayerSync(uuid), MongoBanAPI.executor);
    }
}
