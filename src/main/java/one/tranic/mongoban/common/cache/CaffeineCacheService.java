package one.tranic.mongoban.common.cache;


public class CaffeineCacheService implements CacheService {
    private final Cache cache;

    public CaffeineCacheService(Cache cache) {
        this.cache = cache;
    }
}
