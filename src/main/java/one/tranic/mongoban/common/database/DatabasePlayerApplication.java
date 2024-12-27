package one.tranic.mongoban.common.database;

import one.tranic.mongoban.api.Actions;
import one.tranic.mongoban.api.data.PlayerInfo;
import one.tranic.mongoban.common.Collections;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

public class DatabasePlayerApplication {
    private final Database database;
    private final DatabaseService service;
    private final String collection = "mongo_player";

    /**
     * Constructs an instance of the DatabasePlayerApplication.
     *
     * @param database the Database instance used for performing database operations
     * @param service  the DatabaseService instance for accessing additional database-related functionality
     */
    public DatabasePlayerApplication(Database database, DatabaseService service) {
        this.database = database;
        this.service = service;
    }

    /**
     * Adds or updates a player's information in the database, including their name and associated IP address.
     * <p>
     * If the player does not already exist, a new entry is created.
     * <p>
     * If they exist, their IP list is updated,
     * ensuring it maintains a maximum size of 6 by removing the oldest entry if necessary.
     *
     * @param name the name of the player to add or update
     * @param uuid the unique identifier (UUID) of the player
     * @param ip   the IP address to associate with the player
     * @return an {@code Actions<Void>} instance encapsulating this operation
     */
    public Actions<Void> add(String name, UUID uuid, String ip) {
        return new Actions<>(() -> {
            Document query = new Document("id", uuid);
            Document playerDoc = database.queryOne(this.collection, query);
            Document updateDoc;
            if (playerDoc == null) {
                updateDoc = new Document("id", uuid)
                        .append("name", name)
                        .append("ip", Collections.newArrayList(ip));

                database.insert(this.collection, updateDoc);
            } else {
                List<String> ips = playerDoc.getList("ip", String.class);
                if (ips.size() >= 6) ips.removeFirst();
                if (!ips.isEmpty()) ips.remove(ip);
                ips.add(ip);
                updateDoc = new Document()
                        .append("name", name)
                        .append("ip", ips);
            }
            database.update(this.collection, query, updateDoc);

            return null;
        });
    }

    /**
     * Retrieves player information from the database by the player's name.
     *
     * @param name The name of the player to search for in the database.
     * @return An {@code Actions<PlayerInfo>} containing a task that, when executed,
     * will return a {@code PlayerInfo} object representing the player's details,
     * or {@code null} if no player with the specified name is found.
     */
    public Actions<PlayerInfo> find(String name) {
        return new Actions<>(() -> {
            Document query = new Document("name", name);
            Document playerDoc = database.queryOne(this.collection, query);
            return playerDoc != null ? new PlayerInfo(
                    name,
                    playerDoc.get("id", UUID.class),
                    playerDoc.getList("ip", String.class)
            ) : null;
        });
    }

    /**
     * Finds a player's information based on their unique identifier (UUID).
     *
     * @param uuid The unique identifier of the player to search for.
     * @return An {@code Actions<PlayerInfo>} object encapsulating the operation to retrieve
     * the player's information.
     * <p>
     * The result includes the player's name, UUID, and list of associated IP addresses, or {@code null} if no player is found
     * with the given UUID.
     */
    public Actions<PlayerInfo> find(UUID uuid) {
        return new Actions<>(() -> {
            Document query = new Document("id", uuid);
            Document playerDoc = database.queryOne(this.collection, query);
            return playerDoc != null ? new PlayerInfo(
                    playerDoc.getString("name"),
                    uuid,
                    playerDoc.getList("ip", String.class)
            ) : null;
        });
    }
}
