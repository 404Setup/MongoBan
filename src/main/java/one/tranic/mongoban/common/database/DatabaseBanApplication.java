package one.tranic.mongoban.common.database;

import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.Operator;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.mongoban.api.data.PlayerInfo;
import one.tranic.mongoban.api.task.Actions;
import one.tranic.mongoban.common.Collections;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

public class DatabaseBanApplication {
    private final Database database;
    private final DatabaseService service;
    private final String collection = "mongo_ban";
    private final ip ip;
    private final player player;

    public DatabaseBanApplication(Database database, DatabaseService service) {
        this.database = database;
        this.service = service;

        this.ip = new ip(this);
        this.player = new player(this);
    }

    /**
     * Retrieves the {@code ip} instance associated with this {@code DatabaseBanApplication}.
     *
     * @return the {@code ip} instance, which provides operations for managing IP bans.
     */
    public ip ip() {
        return this.ip;
    }

    /**
     * Retrieves the {@code player} instance associated with this application.
     *
     * @return the {@code player} instance linked to the current {@code DatabaseBanApplication}.
     */
    public player player() {
        return this.player;
    }

    public static class player {
        private final DatabaseBanApplication application;

        public player(DatabaseBanApplication application) {
            this.application = application;
        }

        /**
         * Retrieves ban information for a player identified by the given UUID.
         * <p>
         * The method queries the database for a document matching the provided UUID
         * and constructs a {@code PlayerBanInfo} object.
         * <p>
         * If no matching document is found, it returns {@code null}.
         *
         * @param uuid the unique identifier of the player to search for
         * @return an {@code Actions<PlayerBanInfo>} object containing the player's ban information,
         * or {@code null} if the player is not banned
         */
        public Actions<PlayerBanInfo> find(UUID uuid) {
            return new Actions<>(() -> {
                Document query = new Document("id", uuid);
                Document banDoc = application.database.queryOne(application.collection, query);
                if (banDoc != null) {
                    PlayerBanInfo info = new PlayerBanInfo(
                            uuid,
                            banDoc.get("operator", Operator.class),
                            banDoc.getString("duration"),
                            banDoc.getString("reason")
                    );
                    if (info.expired()) remove(uuid).async();
                    else return info;
                }
                return null;
            });
        }

        /**
         * Adds a player's ban information to the database.
         *
         * @param uuid     the unique identifier of the player to be banned
         * @param operator the operator performing this action
         * @param duration the duration of the ban
         * @param ip       the IP address associated with the player, or null if not available
         * @param reason   the reason for the ban, or null to use the default "<Banned by the server>" message
         * @return an {@code Actions<Void>} instance representing the result of the database update operation
         */
        public Actions<Void> add(UUID uuid, Operator operator, String duration, @Nullable String ip, @Nullable String reason) {
            return new Actions<>(() -> {
                Document query = new Document("id", uuid);
                Document updateDoc = new Document()
                        .append("operator", operator)
                        .append("duration", duration)
                        .append("ip", ip)
                        .append("reason", reason != null ? reason : "<Banned by the server>");

                application.database.update(application.collection, query, updateDoc);

                return null;
            });
        }

        /**
         * Removes a player's record from the database collection based on their unique identifier (UUID).
         * <p>
         * If a matching record is found, it is deleted from the database.
         *
         * @param playerId the UUID of the player whose record is to be removed
         * @return an {@link Actions} object representing the operation to remove the player's record
         */
        public Actions<Void> remove(@NotNull UUID playerId) {
            return new Actions<>(() -> {
                application.database.delete(application.collection, "id", playerId);

                return null;
            });
        }

        /**
         * Removes a list of players' ban records from the database.
         * <p>
         * Iterates through the provided {@code banInfos} list and deletes each player's
         * associated record from the database, using the player's unique identifier (UUID).
         *
         * @param banInfos a list of {@link PlayerBanInfo} objects representing the players
         *                 whose ban records are to be removed. Must not be null.
         * @return an {@link Actions} object encapsulating the database operation to remove
         * the specified players' ban records.
         */
        public Actions<Void> remove(@NotNull List<PlayerBanInfo> banInfos) {
            return new Actions<>(() -> {
                for (PlayerBanInfo banInfo : banInfos)
                    application.database.delete(application.collection, "id", banInfo.uuid());

                return null;
            });
        }

        /**
         * Removes records from the database associated with the specified player's IP address.
         * <p>
         * This method deletes all documents matching the given IP address from the relevant
         * collection in the database.
         *
         * @param playerIp the {@link InetAddress} representing the player's IP address
         *                 whose records are to be removed.
         *                 <p>
         *                 Must not be null.
         * @return an {@link Actions} object encapsulating the database operation to remove
         * the records associated with the specified IP address
         */
        public Actions<Void> remove(@NotNull InetAddress playerIp) {
            return remove(playerIp.getHostAddress());
        }

        /**
         * Removes all records associated with the given player's IP address from the database.
         * <p>
         * This method deletes all documents in the database collection that match the specified IP address.
         *
         * @param playerIp the IP address of the player whose records are to be removed; must not be null
         * @return an {@code Actions<Void>} instance representing the operation to remove the records
         */
        public Actions<Void> remove(@NotNull String playerIp) {
            return new Actions<>(() -> {
                application.database.deleteMany(application.collection, "ip", playerIp);
                return null;
            });
        }
    }

    public static class ip {
        private final DatabaseBanApplication application;

        public ip(DatabaseBanApplication application) {
            this.application = application;
        }

        /**
         * Adds a ban record for a specified IP address along with any associated players.
         * <p>
         * The method updates the database with the provided ban details and propagates
         * the ban information to players linked to the given IP address.
         *
         * @param ip       the IP address to ban
         * @param operator the operator responsible for issuing the ban
         * @param duration the duration of the ban (e.g., a timestamp or duration string)
         * @param reason   an optional reason for the ban; defaults to "Banned by the server" if null
         * @return an {@code Actions<List<PlayerInfo>>} containing a list of {@code PlayerInfo}
         * representing players associated with the banned IP address
         */
        public Actions<List<PlayerInfo>> add(String ip, Operator operator, String duration, @Nullable String reason) {
            return new Actions<>(() -> {
                Document query = new Document("ip", ip);
                Document updateDoc = new Document()
                        .append("operator", operator)
                        .append("duration", duration)
                        .append("reason", reason != null ? reason : "<Banned by the server>");

                application.database.update(application.collection, query, updateDoc);

                List<PlayerInfo> playerList = MongoDataAPI.getDatabase().player().finds(ip).sync();
                if (!playerList.isEmpty()) for (PlayerInfo player : playerList)
                    application.player.add(player.uuid(), operator, duration, ip, reason).sync();

                return playerList;
            });
        }

        /**
         * Adds an entry for banning a specified IP address along with associated information.
         * <p>
         * This method processes the IP address, operator details, ban duration, and optional ban reason,
         * updating the database and, if applicable, associating the ban with player records.
         *
         * @param ip       the IP address to be banned
         * @param operator the operator issuing the ban
         * @param duration the duration of the ban
         * @param reason   an optional reason for the ban; if null, a default reason will be used
         * @return an {@code Actions<List<PlayerInfo>>} object
         * containing a list of {@code PlayerInfo} for players associated with the specified IP address
         */
        public Actions<List<PlayerInfo>> add(InetAddress ip, Operator operator, String duration, @Nullable String reason) {
            return add(ip.getHostAddress(), operator, duration, reason);
        }

        /**
         * Finds an IP ban record associated with the specified IP address.
         * <p>
         * The method queries the database for an IP ban document containing details such as
         * the responsible operator, the duration of the ban, and the reason for the ban.
         * <p>
         * If the ban is found to be expired, it is removed asynchronously.
         *
         * @param address the IP address to query the ban record for
         * @return an {@code Actions<IPBanInfo>} object containing an {@code IPBanInfo} instance if a ban is found,
         * or null if no ban is associated with the specified address
         */
        public Actions<IPBanInfo> find(String address) {
            return new Actions<>(() -> {
                Document query = new Document("ip", address);
                Document banDoc = application.database.queryOne(application.collection, query);
                if (banDoc != null) {
                    IPBanInfo info = new IPBanInfo(
                            address,
                            banDoc.get("operator", Operator.class),
                            banDoc.getString("duration"),
                            banDoc.getString("reason")
                    );
                    if (info.expired()) remove(address).async();
                    else return info;
                }
                return null;
            });
        }

        /**
         * Finds an IP ban record associated with the specified IP address.
         * The method queries the database for an IP ban document containing details such as
         * the responsible operator, the duration of the ban, and the reason for the ban.
         *
         * @param address the IP address to query the ban record for
         * @return an Actions object containing an IPBanInfo instance if a ban is found,
         * or null if no ban is associated with the specified address
         */
        public Actions<IPBanInfo> find(InetAddress address) {
            return find(address.getHostAddress());
        }

        /**
         * Removes an entry associated with the specified IP address from the database
         * and any players linked to the IP address.
         *
         * @param address the IP address to be removed from the database and associated player entries
         * @return an {@code Actions<Void>} representing the completion of the removal process
         */
        public Actions<Void> remove(String address) {
            return new Actions<>(() -> {
                application.database.deleteMany(application.collection, "ip", address);
                application.player.remove(address).sync();

                return null;
            });
        }

        /**
         * Removes an entry associated with the specified IP address from the database
         * and any players linked to the IP address.
         *
         * @param address the IP address to be removed from the database and associated player entries
         * @return an {@code Actions<Void>} representing the completion of the removal process
         */
        public Actions<Void> remove(InetAddress address) {
            return remove(address.getHostAddress());
        }

        /**
         * Retrieves multiple player ban information records associated with a specific IP address.
         * This method queries the database to find bans related to the provided IP address and returns
         * an array of {@code PlayerBanInfo} objects representing the bans. Expired bans are removed
         * asynchronously from the database.
         *
         * @param address the IP address used to query the database and retrieve associated player ban information
         * @return an {@code Actions<PlayerBanInfo[]>} object containing an array of {@code PlayerBanInfo} objects
         * corresponding to players whose IP addresses match the provided address
         */
        public Actions<PlayerBanInfo[]> finds(String address) {
            return new Actions<>(() -> {
                Document query = new Document("ip", new Document("$elemMatch", address));
                List<Document> playerDocs = application.database.queryMany(application.collection, query);
                List<PlayerBanInfo> players = Collections.newArrayList();
                List<PlayerBanInfo> removePlayers = Collections.newArrayList();
                for (Document playerDoc : playerDocs) {
                    PlayerBanInfo info = new PlayerBanInfo(
                            playerDoc.get("uuid", UUID.class),
                            playerDoc.get("operator", Operator.class),
                            playerDoc.getString("duration"),
                            playerDoc.getString("reason")
                    );
                    if (info.expired()) removePlayers.add(info);
                    else players.add(info);
                }
                if (!removePlayers.isEmpty()) application.player.remove(removePlayers).async();
                return players.toArray(new PlayerBanInfo[0]);
            });
        }

        /**
         * Retrieves multiple player ban information records associated with a specific IP address.
         *
         * @param address the IP address used to query the database and retrieve associated player ban information
         * @return an {@code Actions<PlayerBanInfo[]>} object containing an array of {@code PlayerBanInfo} objects
         * corresponding to players whose IP addresses match the provided address
         */
        public Actions<PlayerBanInfo[]> finds(InetAddress address) {
            return finds(address.getHostAddress());
        }
    }
}
