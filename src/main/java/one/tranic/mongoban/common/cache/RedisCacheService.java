package one.tranic.mongoban.common.cache;

import one.tranic.mongoban.api.MongoBanAPI;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Optional;

public class RedisCacheService implements CacheService {
    private final JedisPool pool;

    public RedisCacheService(String host, int port, int db, String user, String passwd) {
        StringBuilder uri = new StringBuilder("redis://");
        if (user != null && !user.isEmpty() && passwd != null && !passwd.isEmpty()) {
            uri.append(user).append(":").append(passwd).append("@");
        }
        uri.append(host).append(":").append(port).append("/").append(db);
        pool = new JedisPool(uri.toString());
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        try (Jedis jedis = pool.getResource()) {
            String value = jedis.get(key);
            return value == null ? Optional.empty() : Optional.ofNullable(MongoBanAPI.jsonParser.parse(value, type));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void put(String key, Object value, long ttl) {
        try (Jedis jedis = pool.getResource()) {
            String serializedValue = MongoBanAPI.jsonParser.toJson(value);
            jedis.setex(key, ttl, serializedValue);
        }
    }

    @Override
    public void invalidate(String key) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(key);
        }
    }

    @Override
    public void close() {
        pool.close();
    }
}
