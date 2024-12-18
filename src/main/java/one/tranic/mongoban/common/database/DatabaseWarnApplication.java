package one.tranic.mongoban.common.database;

import one.tranic.mongoban.api.data.Operator;
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
     * Adds a warning to a player synchronously by creating a warning document and saving it to the database.
     * <p>
     * The warning is uniquely identified with a generated warning ID.
     *
     * @param playerId the unique identifier of the player being warned
     * @param operator the operator issuing the warning
     * @param duration the duration of the warning in an appropriate time unit
     * @param reason   the reason for the warning being issued
     */
    public void addPlayerWarnSync(UUID playerId, Operator operator, int duration, String reason) {
        String warnId = Rand.generateRandomWarnId(21);
        Document query = new Document("id", warnId);
        Document warnDoc = new Document()
                .append("playerId", playerId)
                .append("operator", operator)
                .append("duration", duration)
                .append("reason", reason);

        database.update(this.collection, query, warnDoc);
    }

    /**
     * Asynchronously adds a warning to a player by delegating the creation of a warning document
     * and saving it to the database to a separate thread.
     * <p>
     * This allows for non-blocking execution on the caller's end.
     *
     * @param playerId the unique identifier of the player being warned
     * @param operator the operator issuing the warning
     * @param duration the duration of the warning in an appropriate time unit
     * @param reason   the reason for the warning being issued
     * @return a CompletableFuture that completes when the warning has been successfully created
     */
    public CompletableFuture<Void> addPlayerWarnAsync(UUID playerId, Operator operator, int duration, String reason) {
        return CompletableFuture.runAsync(() -> addPlayerWarnSync(playerId, operator, duration, reason), service.executor);
    }

    /**
     * Retrieves a player's warning information synchronously by the warning ID.
     * <p>
     * If a warning is found, returns a {@code PlayerWarnInfo} containing details
     * about the warning such as the player ID, operator, duration, and reason.
     * <p>
     * If no matching warning is found, returns {@code null}.
     *
     * @param warnId The unique identifier for the warning to retrieve.
     * @return A {@code PlayerWarnInfo} object containing the details of the warning
     * if found, or {@code null} if no warning matches the specified ID.
     */
    public PlayerWarnInfo findPlayerWarnSync(String warnId) {
        Document query = new Document("id", warnId);
        Document warnDoc = database.queryOne(this.collection, query);
        return warnDoc != null ? new PlayerWarnInfo(
                warnDoc.get("playerId", UUID.class),
                warnDoc.get("operator", Operator.class),
                warnDoc.getInteger("duration"),
                warnDoc.getString("reason")
        ) : null;
    }

    /**
     * Asynchronously retrieves warning information associated with a specified warning ID.
     * <p>
     * This method runs the operation in a separate thread using the provided executor service.
     *
     * @param warnId The unique identifier of the warning to retrieve.
     * @return A CompletableFuture containing the PlayerWarnInfo associated with the given warning ID,
     * or null if no matching warning is found.
     */
    public CompletableFuture<PlayerWarnInfo> findPlayerWarnAsync(String warnId) {
        return CompletableFuture.supplyAsync(() -> findPlayerWarnSync(warnId), service.executor);
    }

    /**
     * Retrieves all warning information associated with a specific player identified by their UUID
     * from the database in a synchronous manner.
     *
     * @param playerId The unique identifier (UUID) of the player whose warnings are to be retrieved.
     * @return An array of {@code PlayerWarnInfo} objects containing details about the warnings
     * associated with the specified player. If no warnings are found, an empty array is returned.
     */
    public PlayerWarnInfo[] findPlayerWarnSync(UUID playerId) {
        Document query = new Document("playerId", playerId);
        List<Document> warnDocs = database.queryMany(this.collection, query);
        List<PlayerWarnInfo> warnings = Collections.newArrayList();
        for (Document warnDoc : warnDocs) {
            warnings.add(new PlayerWarnInfo(
                    playerId,
                    warnDoc.get("operator", Operator.class),
                    warnDoc.getInteger("duration"),
                    warnDoc.getString("reason")
            ));
        }
        return warnings.toArray(new PlayerWarnInfo[0]);
    }

    /**
     * Asynchronously fetches all warnings associated with a specific player from the database.
     * The method performs the operation in a non-blocking manner, leveraging a separate thread
     * and returning a CompletableFuture encapsulating the result.
     *
     * @param playerId The unique identifier (UUID) of the player whose warnings are to be retrieved.
     * @return A CompletableFuture that, when completed, contains an array of PlayerWarnInfo
     * objects representing the warnings associated with the specified player.
     */
    public CompletableFuture<PlayerWarnInfo[]> findPlayerWarnAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> findPlayerWarnSync(playerId), service.executor);
    }

    /**
     * Removes a player warning synchronously based on the provided warning ID.
     * This method constructs a query using the warning ID and deletes the corresponding document
     * from the MongoDB collection associated with player warnings.
     *
     * @param warnId the unique identifier of the player warning to be removed
     */
    public void removePlayerWarnSync(String warnId) {
        Document query = new Document("id", warnId);
        database.delete(this.collection, query);
    }

    /**
     * Asynchronously removes a player warning from the database based on the provided warning ID.
     * <p>
     * This method uses a separate thread to execute the removal, ensuring non-blocking behavior
     * for the caller.
     *
     * @param warnId the unique identifier of the warning to be removed from the database
     * @return a CompletableFuture<Void> that completes once the warning has been removed
     */
    public CompletableFuture<Void> removePlayerWarnAsync(String warnId) {
        return CompletableFuture.runAsync(() -> removePlayerWarnSync(warnId), service.executor);
    }

    /**
     * Removes all warnings associated with a specific player from the database in a synchronous manner.
     *
     * @param playerId the unique identifier of the player whose warnings are to be removed
     */
    public void removePlayerWarnSync(UUID playerId) {
        Document query = new Document("playerId", playerId);
        database.deleteMany(this.collection, query);
    }

    /**
     * Asynchronously removes all warnings associated with the specified player from the database.
     * <p>
     * This method executes the removal operation on a separate thread using the service's executor.
     *
     * @param playerId the unique identifier of the player whose warnings should be removed
     * @return a CompletableFuture that completes when the warnings have been successfully removed
     */
    public CompletableFuture<Void> removePlayerWarnAsync(UUID playerId) {
        return CompletableFuture.runAsync(() -> removePlayerWarnSync(playerId), service.executor);
    }
}
