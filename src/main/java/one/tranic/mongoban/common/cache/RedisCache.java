package one.tranic.mongoban.common.cache;

import redis.clients.jedis.JedisPool;

public class RedisCache implements Cache {
    private final RedisCacheService service;
    private final JedisPool pool;

    public RedisCache(String host, int port, int db, String user, String passwd) {
        this.service = new RedisCacheService();
        StringBuilder uri = new StringBuilder("redis://");
        if (user != null && !user.isEmpty() && passwd != null && !passwd.isEmpty()) {
            uri.append(user).append(":").append(passwd).append("@");
        }
        uri.append(host).append(":").append(port).append("/").append(db);
        pool = new JedisPool(uri.toString());
    }

    @Override
    public CacheService getService() {
        return service;
    }

    @Override
    public void close() {
        pool.close();
    }
}
