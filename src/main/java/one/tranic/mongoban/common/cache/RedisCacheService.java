package one.tranic.mongoban.common.cache;

import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.t.base.cache.CacheService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class RedisCacheService implements CacheService {
    private final JedisPool pool;

    public RedisCacheService(String host, int port, int db, String user, String passwd) {
        if (host == null || host.isEmpty())
            throw new IllegalArgumentException("Redis host cannot be null or empty");

        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("Port must be between 0 and 65535");

        if (db < 0)
            throw new IllegalArgumentException("Illegal Redis database id");

        StringBuilder uri = new StringBuilder("redis://");
        if (user != null && !user.isEmpty() && passwd != null && !passwd.isEmpty()) {
            String encodedUser = URLEncoder.encode(user, StandardCharsets.UTF_8);
            String encodedPasswd = URLEncoder.encode(passwd, StandardCharsets.UTF_8);
            uri.append(encodedUser).append(":").append(encodedPasswd).append("@");
        }
        uri.append(host).append(":").append(port).append("/").append(db);
        pool = new JedisPool(uri.toString());
        test();
    }

    public void test() throws RuntimeException {
        try (Jedis jedis = pool.getResource()) {
            jedis.ping();
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize Redis connection", e);
        }
    }

    @Override
    public <T> @NotNull Optional<T> get(@NotNull String key, @NotNull Class<T> type) {
        String value = get(key);
        if (value.isEmpty()) return Optional.empty();
        return Optional.ofNullable(MongoBanAPI.jsonParser.parse(value, type));
    }

    @Override
    public @NotNull String get(@NotNull String key) {
        try (Jedis jedis = pool.getResource()) {
            String value = jedis.get(key);
            if (value == null) return "";
            return value;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void put(@NotNull String key, @NotNull Object value, @Range(from = 0, to = Long.MAX_VALUE) long ttl) {
        try (Jedis jedis = pool.getResource()) {
            String serializedValue = MongoBanAPI.jsonParser.toJson(value);
            jedis.setex(key, ttl, serializedValue);
        }
    }

    @Override
    public void invalidate(@NotNull String key) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(key);
        }
    }

    @Override
    public void invalidateAll() {
        try (Jedis jedis = pool.getResource()) {
            jedis.flushDB();
        }
    }

    @Override
    public void close() {
        if (pool == null) return;
        if (pool.isClosed()) return;
        pool.close();
    }
}
