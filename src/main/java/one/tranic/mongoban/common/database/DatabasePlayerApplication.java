package one.tranic.mongoban.common.database;

import one.tranic.mongoban.api.data.PlayerInfo;
import org.bson.Document;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabasePlayerApplication {
    private final Database database;
    private final DatabaseService service;
    private final String collection = "mongo_player";

    public DatabasePlayerApplication(Database database, DatabaseService service) {
        this.database = database;
        this.service = service;
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
        return CompletableFuture.supplyAsync(() -> getPlayerSync(uuid), service.executor);
    }
}
