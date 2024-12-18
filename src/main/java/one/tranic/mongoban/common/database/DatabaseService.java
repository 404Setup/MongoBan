package one.tranic.mongoban.common.database;

/**
 * The DatabaseService class is responsible for managing the various database-related operations
 * for applications interacting with player data, bans, and warnings.
 * This service acts as a central component to access and operate on these databases,
 * providing specific applications for each database operation.
 * <p>
 * This service uses an asynchronous processing model, employing virtual threads via an executor,
 * to help manage concurrent database tasks efficiently. Each application handles a distinct
 * aspect of the database interaction, allowing modular and focused operations.
 * <p>
 * The DatabaseService initializes and provides access to:
 * <p>
 * - DatabasePlayerApplication: For managing player details and operations.
 * <p>
 * - DatabaseBanApplication: For handling bans and related actions.
 * <p>
 * - DatabaseWarnApplication: For managing warning system interactions.
 * <p>
 * Indexes were initially considered for collections, but they are currently commented out due to
 * performance concerns identified in some deployments.
 */
public class DatabaseService {
    private final DatabasePlayerApplication playerApplication;
    private final DatabaseBanApplication banApplication;
    private final DatabaseWarnApplication warnApplication;

    public DatabaseService(Database database) {
        this.playerApplication = new DatabasePlayerApplication(database, this);
        this.banApplication = new DatabaseBanApplication(database, this);
        this.warnApplication = new DatabaseWarnApplication(database, this);
    }

    /**
     * Provides access to the {@link DatabasePlayerApplication} instance, which is responsible for
     * managing player-related database operations, such as retrieving player information,
     * handling data persistence, and querying the player database.
     *
     * @return The {@code DatabasePlayerApplication} instance associated with this service.
     */
    public DatabasePlayerApplication getPlayerApplication() {
        return playerApplication;
    }

    /**
     * Retrieves the DatabaseBanApplication instance, which is responsible for handling
     * database interactions related to bans. This includes operations such as adding,
     * updating, or removing bans from the database.
     *
     * @return the DatabaseBanApplication instance used for ban-related database operations.
     */
    public DatabaseBanApplication getBanApplication() {
        return banApplication;
    }

    /**
     * Retrieves the {@code DatabaseWarnApplication} instance, which provides
     * functionality for managing the warning system interactions within the database.
     *
     * @return the {@code DatabaseWarnApplication} instance associated with this service.
     */
    public DatabaseWarnApplication getWarnApplication() {
        return warnApplication;
    }
}
