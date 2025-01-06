package one.tranic.mongoban.api.message;

import net.kyori.adventure.text.Component;

/**
 * Record representing a formatted message with a key and a value.
 * <p>
 * The {@code MessageFormat} record is used to pair a unique key with a
 * {@link Component} value. This is helpful in scenarios
 * where messages or texts need to be associated with localization keys or identifiers.
 *
 * @param key   the unique identifier for the message
 * @param value the {@link Component} value representing the message content
 */
public record MessageFormat(String key, Component value) {
}
