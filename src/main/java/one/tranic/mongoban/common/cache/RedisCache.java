package one.tranic.mongoban.common.cache;

import one.tranic.t.base.cache.Cache;
import one.tranic.t.base.cache.CacheService;

public class RedisCache implements Cache {
    private final RedisCacheService service;

    public RedisCache(String host, int port, int db, String user, String passwd) {
        this.service = new RedisCacheService(host, port, db, user, passwd);

    }

    @Override
    public CacheService getService() {
        return service;
    }

    @Override
    public void close() {
        service.close();
    }
}
