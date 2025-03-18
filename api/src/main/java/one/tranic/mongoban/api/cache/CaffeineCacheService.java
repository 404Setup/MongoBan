package one.tranic.mongoban.api.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import one.tranic.t.base.cache.CacheService;
import org.jetbrains.annotations.NotNull;

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
    public <T> @NotNull Optional<T> get(@NotNull String key, @NotNull Class<T> type) {
        return Optional.ofNullable((T) objectCache.getIfPresent(key));
    }

    @Override
    public @NotNull String get(@NotNull String key) {
        Object value = objectCache.getIfPresent(key);
        if (value == null) return "";
        if (value instanceof String) return (String) value;
        return value.toString();
    }

    @Override
    public void put(@NotNull String key, @NotNull Object value, long ttl) {
        if (get(key, value.getClass()).isPresent())
            invalidate(key);
        if (ttl > 0) {
            objectCache.policy().expireVariably().ifPresent(
                    policy -> policy.put(key, value, Duration.ofSeconds(ttl))
            );
        } else {
            objectCache.put(key, value);
        }
    }

    @Override
    public void invalidate(@NotNull String key) {
        objectCache.invalidate(key);
    }

    @Override
    public void invalidateAll() {
        objectCache.invalidateAll();
    }

    @Override
    public void close() {
        if (executor.isShutdown()) return;
        invalidateAll();
        executor.shutdown();
    }
}
