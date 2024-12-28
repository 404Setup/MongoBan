package one.tranic.mongoban.common.cache;

import one.tranic.mongoban.api.cache.Cache;
import one.tranic.mongoban.api.cache.CacheService;

public class CaffeineCache implements Cache {
    private final CaffeineCacheService service;

    public CaffeineCache() {
        this.service = new CaffeineCacheService();
    }

    @Override
    public CacheService getService() {
        return this.service;
    }

    @Override
    public void close() {
        this.service.close();
    }
}
