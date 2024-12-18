package one.tranic.mongoban.common.cache;

public class CaffeineCache implements Cache {
    private final CaffeineCacheService service;

    public CaffeineCache() {
        this.service = new CaffeineCacheService();
    }

    @Override
    public CacheService getService() {
        return this.service;
    }
}
