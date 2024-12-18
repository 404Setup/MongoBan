package one.tranic.mongoban.common.cache;

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
     * Retrieves the underlying implementation of the {@link CacheService} interface
     * associated with this cache.
     *
     * @return the {@link CacheService} instance that this cache utilizes to manage
     *         its operations and storage mechanisms.
     */
    CacheService getService();
}
