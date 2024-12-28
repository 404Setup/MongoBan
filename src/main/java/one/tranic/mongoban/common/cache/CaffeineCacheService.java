package one.tranic.mongoban.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import one.tranic.mongoban.api.cache.CacheService;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaffeineCacheService implements CacheService {
    private final ExecutorService executor = Executors.newFixedThreadPool(2,
            new ThreadFactoryBuilder().setDaemon(true).build());

    private com.github.benmanes.caffeine.cache.Cache<String, Object> objectCache;

    public CaffeineCacheService() {
        this.objectCache = Caffeine.newBuilder()
                .maximumSize(70)
                .executor(executor)
                .expireAfterWrite(Duration.ofMinutes(1440))
                .build();
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        return (Optional<T>) Optional.ofNullable(objectCache.getIfPresent(key));
    }

    @Override
    public void put(String key, Object value, long ttl) {
        if (get(key, value.getClass()).isPresent())
            invalidate(key);
        objectCache.put(key, value);
    }

    @Override
    public void invalidate(String key) {
        objectCache.invalidate(key);
    }

    @Override
    public void close() {
        this.objectCache.invalidateAll();
        executor.shutdown();

        this.objectCache = null;
    }
}
