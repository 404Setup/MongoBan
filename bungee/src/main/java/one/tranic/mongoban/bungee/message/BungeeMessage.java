package one.tranic.mongoban.bungee.message;

import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.message.MessageKey;
import one.tranic.t.base.message.Message;
import one.tranic.t.base.message.MessageFormat;
import one.tranic.t.utils.Platform;
import org.jetbrains.annotations.NotNull;

public class BungeeMessage {
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
    public static net.md_5.bungee.api.chat.BaseComponent[] formatBungee(MessageKey key, MessageFormat... args) {
        if (Platform.get() != Platform.BungeeCord) return null;
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText(key.formatString(args));
    }

    /**
     * Converts a legacy text string into an array of BaseComponent objects.
     * If the current platform is not BungeeCord, the method returns null.
     *
     * @param message the legacy text string to be converted; must not be null
     * @return an array of BaseComponent objects representing the converted text,
     * or null if the platform is not BungeeCord
     */
    public static net.md_5.bungee.api.chat.BaseComponent[] toBaseComponent(@NotNull String message) {
        if (Platform.get() != Platform.BungeeCord) return null;
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message);
    }

    /**
     * Converts a {@link Component} to an array of {@link net.md_5.bungee.api.chat.BaseComponent}.
     * <p>
     * This method leverages the {@code toString(Component)} method to serialize the input
     * {@link Component} into a legacy string format and then transforms it into BungeeCord's
     * {@link Component} array representation.
     *
     * @param component The {@link Component} to be converted. Must not be null.
     * @return An array of {@link net.md_5.bungee.api.chat.BaseComponent} representing the provided
     * {@link Component}, or null if a non-BungeeCord platform is detected.
     */
    public static net.md_5.bungee.api.chat.BaseComponent[] toBaseComponent(@NotNull Component component) {
        return toBaseComponent(Message.toString(component));
    }
}
