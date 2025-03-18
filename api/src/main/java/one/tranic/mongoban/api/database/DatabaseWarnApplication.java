package one.tranic.mongoban.api.database;

import one.tranic.mongoban.api.data.PlayerWarnInfo;
import one.tranic.t.base.command.Operator;
import one.tranic.t.base.task.Actions;
import one.tranic.t.utils.Collections;
import one.tranic.t.utils.Rand;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class DatabaseWarnApplication {
    private final Database database;
    private final DatabaseService service;
    private final String collection = "mongo_warn";

    public DatabaseWarnApplication(Database database, DatabaseService service) {
        this.database = database;
        this.service = service;
    }

    /**
     * Adds a warning to the database for a specified player with the given details.
     *
     * @param playerId the unique identifier of the player to be warned
     * @param operator the operator issuing the warning
     * @param duration the duration of the warning in integer format
     * @param reason   the reason for issuing the warning
     * @return an {@code Actions<Void>} instance representing the operation to add the warning
     */
    public Actions<Void> add(@NotNull UUID playerId, @NotNull Operator operator, @Nullable String duration, @Nullable String reason) {
        return new Actions<>(() -> {
            String warnId = Rand.generateRandomId(21);
            Document warnDoc = new Document("id", warnId)
                    .append("playerId", playerId)
                    .append("operator", operator)
                    .append("duration", duration != null ? duration : "forever")
                    .append("reason", reason != null ? reason : "<No reason provided>");

            database.insert(this.collection, warnDoc);

            return null;
        });
    }

    /**
     * Finds and retrieves a player's warning information by its unique warning ID.
     *
     * @param warnId The unique identifier for the warning.
     * @return An {@code Actions<PlayerWarnInfo>} object that, when executed, returns
     * a {@code PlayerWarnInfo} instance containing the details of the warning
     * if found, or {@code null} if no warning matches the provided ID.
     */
    public Actions<PlayerWarnInfo> find(String warnId) {
        return new Actions<>(() -> {
            Document query = new Document("id", warnId);
            Document warnDoc = database.queryOne(this.collection, query);
            return warnDoc != null ? new PlayerWarnInfo(
                    warnDoc.get("playerId", UUID.class),
                    warnDoc.get("operator", Operator.class),
                    warnDoc.getString("id"),
                    warnDoc.getString("duration"),
                    warnDoc.getString("reason")
            ) : null;
        });
    }

    /**
     * Retrieves all warning information associated with a specific player, identified by their unique player ID.
     * This method queries the underlying database for all documents where the player ID matches the specified ID,
     * and converts these documents into an array of {@link PlayerWarnInfo} objects representing the warnings
     * issued to the player.
     *
     * @param playerId the unique identifier (UUID) of the player whose warning information is to be retrieved
     * @return an {@link Actions} object wrapping an array of {@link PlayerWarnInfo} objects containing
     * the warnings issued to the specified player; if no warnings are found, an empty array is returned
     */
    public Actions<PlayerWarnInfo[]> finds(UUID playerId) {
        return new Actions<>(() -> {
            Document query = new Document("playerId", playerId);
            List<Document> warnDocs = database.queryMany(this.collection, query);
            List<PlayerWarnInfo> warnings = Collections.newArrayList();
            for (Document warnDoc : warnDocs) {
                warnings.add(new PlayerWarnInfo(
                        playerId,
                        warnDoc.get("operator", Operator.class),
                        warnDoc.getString("id"),
                        warnDoc.getString("duration"),
                        warnDoc.getString("reason")
                ));
            }
            return warnings.toArray(new PlayerWarnInfo[0]);
        });
    }

    /**
     * Removes a warning from the database based on the given warning ID.
     *
     * @param warnId the unique identifier of the warning to be removed
     * @return an {@code Actions<Void>} instance representing the operation
     */
    public Actions<Void> remove(String warnId) {
        return new Actions<>(() -> {
            database.delete(this.collection, "id", warnId);

            return null;
        });
    }

    /**
     * Removes all warning records associated with the specified player's UUID from the database.
     *
     * @param playerId the UUID of the player whose warning records are to be removed
     * @return an {@code Actions<Void>} object representing the execution task of the removal operation
     */
    public Actions<Void> remove(UUID playerId) {
        return new Actions<>(() -> {
            database.deleteMany(this.collection, "playerId", playerId);

            return null;
        });
    }
}
