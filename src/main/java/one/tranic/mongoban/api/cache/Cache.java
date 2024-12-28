package one.tranic.mongoban.api.cache;

/**
 * Represents a generic cache interface that provides access
 * to the underlying {@link CacheService} implementation.
 * <p>
 * Classes implementing this interface are responsible for
 * defining specific cache behaviors while encapsulating their
 * implementation details through the provided service.
 */
public interface Cache {
    /**
     * Retrieves the underlying CacheService instance associated with the current Cache implementation.
     * This method provides access to the internal caching service used for managing cache operations.
     *
     * @return the CacheService instance backing the current Cache implementation.
     */
    CacheService getService();

    /**
     * Closes the cache and releases any resources associated with it.
     * <p>
     * Implementations of this method are responsible for safely cleaning up
     * resources or connections held by the cache.
     * For example,
     * <p>
     * - In-memory cache implementations might clear or invalidate cached entries.
     * <p>
     * - Distributed cache implementations might close network connections or pools.
     * <p>
     * After calling this method, the cache instance should no longer be used.
     */
    void close();
}
