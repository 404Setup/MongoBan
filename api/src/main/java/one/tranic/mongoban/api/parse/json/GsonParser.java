package one.tranic.mongoban.api.parse.json;

import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of the {@link JsonParser} interface that uses Gson library
 * for JSON parsing and serialization.
 * <p>
 * This class provides methods to parse JSON strings into objects of a specified
 * type and to convert objects into their JSON string representations using Gson.
 */
public class GsonParser implements JsonParser {
    private final Gson gson = new Gson();

    @Override
    public <T> T parse(@Nullable String text, Class<T> clazz) {
        return gson.fromJson(text, clazz);
    }

    @Override
    public String toJson(Object object) {
        return gson.toJson(object);
    }
}
