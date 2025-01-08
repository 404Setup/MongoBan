package one.tranic.mongoban.api.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import one.tranic.mongoban.api.Platform;
import one.tranic.mongoban.common.Collections;

import java.util.List;
import java.util.Objects;

/**
 * Represents a key used to retrieve localized or predefined messages from a storage mechanism.
 * <p>
 * Each constant in this enum maps to a specific key representing a message.
 */
public enum MessageKey {
    DEFAULT_KICK("kick.default"), KICK_MESSAGE("kick.message"),
    BAN_INVALID_USAGE("ban.invalid-usage"), BAN_MESSAGE("ban.message"), ALREADY_BANNED("ban.already"),
    PRIVATE_IP("failed.private-ip"), TARGET_NOT_FOUND("failed.target-not-found"), TARGET_MISSIONG("failed.target-missing"), PERMISSION_DENIED("failed.permission");

    private final String key;

    MessageKey(String key) {
        this.key = key;
    }

    /**
     * Retrieves the key associated with this enum constant.
     *
     * @return the string representation of the key corresponding to this MessageKey
     */
    public String getKey() {
        return key;
    }

    /**
     * Formats the message associated with the current {@link MessageKey} using the provided
     * {@link MessageFormat} arguments. If no arguments are provided, only the raw message will be
     * formatted and returned.
     *
     * @param args an array of {@link MessageFormat} objects containing key-value pairs for placeholders
     *             to replace in the message; if no arguments are provided, placeholders will not be
     *             substituted.
     * @return a formatted {@link Component} containing the resolved message with placeholders replaced
     * by the provided values, or the raw message if no arguments are supplied.
     */
    public Component format(MessageFormat... args) {
        if (args.length == 0)
            return MiniMessage.miniMessage().deserialize(
                    Objects.requireNonNull(Message.get(this))
            );

        List<TagResolver> list = Collections.newArrayList();
        for (MessageFormat arg : args) list.add(Placeholder.component(arg.key(), arg.value()));

        return MiniMessage.miniMessage().deserialize(
                Objects.requireNonNull(Message.get(this)),
                list.toArray(new TagResolver[0])
        );
    }

    /**
     * Retrieves a non-null value associated with the current instance, as defined in
     * the {@code Message} class.
     *
     * @return the non-null value corresponding to this instance
     * @throws NullPointerException if the retrieved value is null
     */
    public String getValue() {
        return Objects.requireNonNull(Message.get(this));
    }

    /**
     * Converts the result of the {@link #format(MessageFormat...)} method into a serialized legacy string format.
     *
     * @param args an array of {@link MessageFormat} objects containing key-value pairs for placeholders
     *             to replace in the message; if no arguments are provided, placeholders will not be
     *             substituted.
     * @return the serialized string representation of the formatted message with placeholders resolved
     * or the raw message if no arguments are provided.
     */
    public String formatString(MessageFormat... args) {
        return LegacyComponentSerializer.legacySection().serialize(format(args));
    }

    /**
     * Formats a message into a BungeeCord-compatible BaseComponent array by resolving placeholders
     * in the message using the provided {@link MessageFormat} arguments.
     * <p>
     * This method is specifically designed for usage with the BungeeCord platform and will return null
     * if the current platform is not BungeeCord.
     *
     * @param args an array of {@link MessageFormat} objects containing key-value pairs for placeholders
     *             to replace in the message; if no arguments are provided, placeholders will not be
     *             substituted.
     * @return an array of {@link net.md_5.bungee.api.chat.BaseComponent} objects representing
     * the formatted message for BungeeCord, or null if the platform is not BungeeCord.
     */
    public net.md_5.bungee.api.chat.BaseComponent[] formatBungee(MessageFormat... args) {
        if (Platform.get() != Platform.BungeeCord) return null;
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText(formatString(args));
    }
}
