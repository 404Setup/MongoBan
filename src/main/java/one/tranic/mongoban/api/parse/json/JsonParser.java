package one.tranic.mongoban.api.parse.json;

import org.jetbrains.annotations.Nullable;

/**
 * An interface for parsing JSON data and converting objects to JSON.
 * <p>
 * This interface defines methods for deserializing JSON strings into
 * objects of a specified type and for serializing objects into their
 * JSON string representations.
 */
public interface JsonParser {
    /**
     * Parses the provided JSON string into an object of the specified type.
     *
     * @param text  the JSON string to parse; may be null, in which case null is returned
     * @param clazz the class of the object to be deserialized
     * @param <T>   the type of the object to be deserialized
     * @return an object of the specified type deserialized from the JSON string,
     * or null if the provided text is null
     */
    <T> T parse(@Nullable String text, Class<T> clazz);

    /**
     * Serializes the specified object into its JSON string representation.
     *
     * @param object the object to be converted to a JSON string
     * @return a JSON string representation of the specified object
     */
    String toJson(Object object);
}
