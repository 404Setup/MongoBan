package one.tranic.mongoban.api;

import one.tranic.mongoban.common.Config;
import one.tranic.mongoban.common.cache.Cache;
import one.tranic.mongoban.common.cache.CaffeineCache;
import one.tranic.mongoban.common.cache.RedisCache;
import one.tranic.mongoban.common.database.Database;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * MongoDataAPI provides a static API for managing database and cache instances.
 * <p>
 * This class handles connections to a database and a caching system, allowing
 * the configuration, retrieval, and closure of these resources.
 * <p>
 * Methods in this class ensure controlled and synchronized access to resources
 * by enforcing security restrictions and validating inputs.
 * <p>
 * The API is designed to be used internally within packages that match a
 * specific naming convention, ensuring restricted access to critical methods
 * such as `setDatabase`, `setCache`, `close`, and `reconnect`.
 */
public class MongoDataAPI {
    private static Database database;
    private static Cache cache;

    /**
     * Retrieves the current instance of the database being used by the application.
     * <p>
     * The database instance provides access to MongoDB operations and configurations.
     *
     * @return the current {@code Database} instance used by the application, or {@code null} if no database is set.
     */
    public static @Nullable Database getDatabase() {
        return database;
    }

    /**
     * Sets the database connection for the MongoDataAPI.
     * <p>
     * If a previous database connection exists, it disconnects the existing connection before assigning the new one.
     * <p>
     * Ensures that the caller has the appropriate permissions to modify the database connection.
     *
     * @param database the new database to be set; must not be null
     * @throws SecurityException        if the caller is not authorized to modify the database connection
     * @throws IllegalArgumentException if the provided database is null
     */
    public synchronized static void setDatabase(@NotNull Database database) throws SecurityException, IllegalArgumentException {
        if (database == null) throw new IllegalArgumentException("Value cannot be null");
        if (!isCallerAllowed()) throw new SecurityException("Unauthorized access to setDatabase method.");
        if (MongoDataAPI.database != null) {
            MongoDataAPI.database.disconnect();
            MongoDataAPI.database = null;
        }
        MongoDataAPI.database = database;
    }

    /**
     * Retrieves the currently set cache instance.
     * <p>
     * If no cache has been set, returns null.
     *
     * @return the currently set {@link Cache} instance, or null if no cache is available
     */
    public static @Nullable Cache getCache() {
        return cache;
    }

    /**
     * Sets the cache instance for the MongoDataAPI class. If a cache is already set,
     * it will be closed and replaced with the provided cache instance.
     * <p>
     * This method ensures that only authorized callers can set a new cache.
     *
     * @param cache the cache instance to be set; must not be null
     * @throws SecurityException        if the caller is not authorized to invoke this method
     * @throws IllegalArgumentException if the provided cache instance is null
     */
    public synchronized static void setCache(@NotNull Cache cache) throws SecurityException, IllegalArgumentException {
        if (cache == null) throw new IllegalArgumentException("Value cannot be null");
        if (!isCallerAllowed()) throw new SecurityException("Unauthorized access to setCache method.");
        if (MongoDataAPI.cache != null) {
            MongoDataAPI.cache.close();
            MongoDataAPI.cache = null;
        }
        MongoDataAPI.cache = cache;
    }

    /**
     * Closes and releases the resources associated with the current database and cache instances.
     * <p>
     * This method is synchronized to ensure thread safety when accessing and updating the `database`
     * and `cache` fields.
     *
     * @throws SecurityException if the caller is not authorized to use this method
     */
    public synchronized static void close() throws SecurityException {
        if (!isCallerAllowed()) throw new SecurityException("Unauthorized access to close method.");
        if (MongoDataAPI.database != null) {
            MongoDataAPI.database.disconnect();
            MongoDataAPI.database = null;
        }
        if (MongoDataAPI.cache != null) {
            MongoDataAPI.cache.close();
            MongoDataAPI.cache = null;
        }
    }

    /**
     * Checks whether the caller of this method belongs to the "one.tranic.mongoban" package.
     * <p>
     * This method examines the stack trace of the current thread to verify
     * if any class in the invocation chain belongs to the specified package.
     * <p>
     * If a match is found, the caller is considered allowed.
     *
     * @return true if the caller belongs to the "one.tranic.mongoban" package, false otherwise
     */
    private static boolean isCallerAllowed() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            for (int i = 2; i < stackTrace.length; i++) {
                String className = stackTrace[i].getClassName();

                if (className.startsWith("one.tranic.mongoban")) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Reinitializes the cache and database connections for the system.
     * <p>
     * This method is synchronized to ensure thread safety during the reconnection process.
     * The reconnect method fetches the necessary configurations for the cache and database
     * from the {@link Config} class and creates instances of {@link CaffeineCache} or {@link RedisCache}
     * based on the cache configuration. It also establishes a database connection using
     * the specified database configuration details such as host, port, database name, user,
     * and password. Both the cache and database instances are set in the {@link MongoDataAPI}.
     * <p>
     * The method validates the caller's permissions before execution to ensure only authorized
     * invocations by utilizing the {@code isCallerAllowed} method.
     *
     * @throws SecurityException if the caller is not authorized to invoke this method.
     */
    public synchronized static void reconnect() throws SecurityException {
        if (!isCallerAllowed()) throw new SecurityException("Unauthorized access to reconnect method.");
        Cache cache = Config.getCache() == 0 ? new CaffeineCache() :
                new RedisCache(
                        Config.getRedis().host(),
                        Config.getRedis().port(),
                        Config.getRedis().db(),
                        Config.getRedis().user(),
                        Config.getRedis().password());
        MongoDataAPI.setCache(cache);
        Database database = new Database(
                Config.getDatabase().host(),
                Config.getDatabase().port(),
                Config.getDatabase().database(),
                Config.getDatabase().user(),
                Config.getDatabase().password(),
                cache);
        MongoDataAPI.setDatabase(database);
    }
}
