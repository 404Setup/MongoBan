package one.tranic.mongoban.common.cache;

import java.util.Optional;

public interface CacheService {
    <T> Optional<T> get(String key, Class<T> type);
    void put(String key, Object value, long ttl);
    void invalidate(String key);
    void close();
}
