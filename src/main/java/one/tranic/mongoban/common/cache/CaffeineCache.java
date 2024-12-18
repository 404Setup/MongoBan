package one.tranic.mongoban.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.mongoban.api.data.PlayerInfo;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaffeineCache implements Cache {
    private final CaffeineCacheService service;

    private final ExecutorService executor = Executors.newFixedThreadPool(4,
            new ThreadFactoryBuilder().setDaemon(true).build());

    private com.github.benmanes.caffeine.cache.Cache<UUID, PlayerInfo> cachePlayer;
    private com.github.benmanes.caffeine.cache.Cache<UUID, PlayerBanInfo> cacheBan;
    private com.github.benmanes.caffeine.cache.Cache<String, IPBanInfo> cacheIP;

    public CaffeineCache() {
        this.cachePlayer = Caffeine.newBuilder()
                .maximumSize(40)
                .executor(executor)
                .expireAfterWrite(Duration.ofMinutes(1440))
                .build();
        this.cacheBan = Caffeine.newBuilder()
                .maximumSize(30)
                .executor(executor)
                .expireAfterWrite(Duration.ofMinutes(1440))
                .build();
        this.cacheIP = Caffeine.newBuilder()
                .maximumSize(25)
                .executor(executor)
                .expireAfterWrite(Duration.ofMinutes(1440))
                .build();

        this.service = new CaffeineCacheService(this);
    }

    @Override
    public CacheService getService() {
        return this.service;
    }

    @Override
    public void close() {
        this.cachePlayer.invalidateAll();
        this.cacheBan.invalidateAll();
        this.cacheIP.invalidateAll();
        executor.shutdown();

        this.cachePlayer = null;
        this.cacheBan = null;
        this.cacheIP = null;
    }
}
