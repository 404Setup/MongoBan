package one.tranic.mongoban.api;

import one.tranic.mongoban.api.cache.CaffeineCache;
import one.tranic.mongoban.api.cache.RedisCache;
import one.tranic.mongoban.api.config.NewConfig;
import one.tranic.mongoban.api.database.Database;
import one.tranic.t.base.cache.Cache;
import org.jetbrains.annotations.NotNull;

/**
 * MongoDataAPI provides a static API for managing database and cache instances.
 */
public class MongoDataAPI {
    private static Database database;
    private static Cache cache;

    /**
     * Retrieves the current instance of the database being used by the application.
     *
     * @return the current {@code Database} instance used by the application, or {@code null} if no database is set.
     */
    public static Database getDatabase() {
        return database;
    }

    /**
     * Sets the database connection for the MongoDataAPI.
     *
     * @param database the new database to be set; must not be null
     * @throws SecurityException        if the caller is not authorized to modify the database connection
     * @throws IllegalArgumentException if the provided database is null
     */
    public synchronized static void setDatabase(@NotNull Database database) throws SecurityException, IllegalArgumentException {
        if (!isCallerAllowed()) throw new SecurityException("Unauthorized access to setDatabase method.");
        if (MongoDataAPI.database != null) {
            MongoDataAPI.database.disconnect();
        }
        MongoDataAPI.database = database;
    }

    /**
     * Retrieves the currently set cache instance.
     *
     * @return the currently set {@link Cache} instance, or null if no cache is available
     */
    public static Cache getCache() {
        return cache;
    }

    /**
     * Sets the cache instance for the MongoDataAPI class. If a cache is already set,
     * it will be closed and replaced with the provided cache instance.
     *
     * @param cache the cache instance to be set; must not be null
     * @throws SecurityException        if the caller is not authorized to invoke this method
     * @throws IllegalArgumentException if the provided cache instance is null
     */
    public synchronized static void setCache(@NotNull Cache cache) throws Exception {
        if (!isCallerAllowed()) throw new SecurityException("Unauthorized access to setCache method.");
        if (MongoDataAPI.cache != null) {
            MongoDataAPI.cache.close();
        }
        MongoDataAPI.cache = cache;
    }

    /**
     * Closes and releases the resources associated with the current database and cache instances.
     *
     * @throws SecurityException if the caller is not authorized to use this method
     */
    public synchronized static void close() throws Exception {
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
     *
     * @throws SecurityException if the caller is not authorized to invoke this method.
     */
    public synchronized static void reconnect() throws Exception {
        if (!isCallerAllowed()) throw new SecurityException("Unauthorized access to reconnect method.");

        Cache cache = NewConfig.getConfig().cache() == 0 ? new CaffeineCache() :
                new RedisCache(
                        NewConfig.getConfig().redis().host(),
                        NewConfig.getConfig().redis().port(),
                        NewConfig.getConfig().redis().db(),
                        NewConfig.getConfig().redis().user(),
                        NewConfig.getConfig().redis().password());
        MongoDataAPI.setCache(cache);
        Database database = new Database(
                NewConfig.getConfig().database().host(),
                NewConfig.getConfig().database().port(),
                NewConfig.getConfig().database().database(),
                NewConfig.getConfig().database().user(),
                NewConfig.getConfig().database().password(),
                cache);
        MongoDataAPI.setDatabase(database);
    }
}
