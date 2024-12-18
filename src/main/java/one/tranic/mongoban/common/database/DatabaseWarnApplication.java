package one.tranic.mongoban.common.database;

import one.tranic.mongoban.api.data.PlayerWarnInfo;
import one.tranic.mongoban.common.Collections;
import one.tranic.mongoban.common.Rand;
import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseWarnApplication {
    private final Database database;
    private final DatabaseService service;
    private final String collection = "mongo_warn";

    public DatabaseWarnApplication(Database database, DatabaseService service) {
        this.database = database;
        this.service = service;
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

        database.update(this.collection, query, warnDoc);
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
        return CompletableFuture.runAsync(() -> addPlayerWarnSync(playerId, duration, reason), service.executor);
    }

    /**
     * Synchronously finds the warning in the database using the provided warnId.
     *
     * @param warnId The warning ID to search for.
     * @return The {@code PlayerWarnInfo} record containing the warning details, or {@code null} if none is found.
     */
    public PlayerWarnInfo findPlayerWarnSync(String warnId) {
        Document query = new Document("id", warnId);
        Document warnDoc = database.queryOne(this.collection, query);
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
        return CompletableFuture.supplyAsync(() -> findPlayerWarnSync(warnId), service.executor);
    }

    /**
     * Synchronously finds all warnings in the database for the specified playerId.
     *
     * @param playerId The UUID of the player to search for.
     * @return An array of {@code PlayerWarnInfo} records containing the player's warning details.
     */
    public PlayerWarnInfo[] findPlayerWarnSync(UUID playerId) {
        Document query = new Document("playerId", playerId);
        List<Document> warnDocs = database.queryMany(this.collection, query);
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
        return CompletableFuture.supplyAsync(() -> findPlayerWarnSync(playerId), service.executor);
    }

    /**
     * Synchronously removes a warning from the database using the provided warning ID.
     *
     * @param warnId The unique identifier of the warning to be removed.
     */
    public void removePlayerWarnSync(String warnId) {
        Document query = new Document("id", warnId);
        database.delete(this.collection, query);
    }

    /**
     * Asynchronously removes a warning assigned to a player.
     *
     * @param warnId The unique identifier of the player's warning to be removed.
     * @return A CompletableFuture that completes when the warning has been removed.
     */
    public CompletableFuture<Void> removePlayerWarnAsync(String warnId) {
        return CompletableFuture.runAsync(() -> removePlayerWarnSync(warnId), service.executor);
    }

    /**
     * Synchronously removes all warnings associated with the specified player ID from the database.
     *
     * @param playerId The UUID of the player whose warnings are to be removed.
     */
    public void removePlayerWarnSync(UUID playerId) {
        Document query = new Document("playerId", playerId);
        database.deleteMany(this.collection, query);
    }

    /**
     * Asynchronously removes a warning for a player identified by their unique ID.
     *
     * @param playerId the unique identifier of the player whose warning is to be removed
     * @return a CompletableFuture that completes when the player's warning has been removed
     */
    public CompletableFuture<Void> removePlayerWarnAsync(UUID playerId) {
        return CompletableFuture.runAsync(() -> removePlayerWarnSync(playerId), service.executor);
    }
}
