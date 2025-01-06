package one.tranic.mongoban.api.command.message;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import one.tranic.mongoban.api.Platform;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.Operator;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.mongoban.api.exception.UnsupportedTypeException;
import one.tranic.mongoban.api.parse.network.NetworkParser;
import one.tranic.mongoban.common.Collections;
import one.tranic.mongoban.common.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

public class Message {
    public static final TextComponent DEFAULT_KICK_MESSAGE = Component.text("Disconnecting...");
    private static final Map<MessageKey, String> messages = Collections.newHashMap();

    private static YamlMapping yaml(InputStream data) throws IOException {
        return Yaml.createYamlInput(
                data
        ).readYamlMapping();
    }

    public static @Nullable String get(MessageKey key) {
        return messages.get(key);
    }

    private static void getMessages(YamlMapping yaml) {
        int i = 0;
        for (MessageKey key : MessageKey.values()) {
            String value = yaml.string(key.getKey());
            if (value != null) {
                messages.put(key, value);
                i++;
            }
        }
        Data.logger.info("Load {} data from the language file", i);
    }

    public static void reloadMessages() {
        if (!messages.isEmpty()) messages.clear();

        Data.logger.info("Searching for language packs: {}....", Locale.getDefault().toLanguageTag());
        try (InputStream data = NetworkParser.resource("language/" + Locale.getDefault().toLanguageTag() + ".yaml")) {
            getMessages(yaml(data));
        } catch (Exception ignored) {
            Data.logger.error("The language pack {} was not found or the file is damaged. Reading the default language pack: {}..."
                    , Locale.getDefault().toLanguageTag()
                    , Locale.US.toLanguageTag());
            try (InputStream data = NetworkParser.resource("language/" + Locale.US.toLanguageTag() + ".yaml")) {
                getMessages(yaml(data));
            } catch (IOException e) {
                Data.logger.error("Failed to read the default language pack: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public static TextComponent failedPrivateIPMessage(@NotNull String ip) {
        TextComponent.Builder message = Component.text();

        message.append(Component.text("Selected IP Address: ", NamedTextColor.YELLOW));
        message.append(Component.text(ip, NamedTextColor.BLUE));
        message.append(Component.text("is not a usable public address.", NamedTextColor.YELLOW));

        return message.build();
    }

    public static TextComponent alreadyBannedMessage(@NotNull String target, @NotNull Operator operator, String duration, String reason) {
        TextComponent.Builder message = Component.text();

        message.append(Component.text("The specified target has been banned:", NamedTextColor.YELLOW));
        message.append(Component.text("\nTarget: ", NamedTextColor.GREEN));
        message.append(Component.text(target, NamedTextColor.BLUE));
        message.append(Component.text("\nOperator: ", NamedTextColor.GREEN));
        message.append(Component.text(operator.name(), NamedTextColor.BLUE));
        message.append(Component.text("\nDuration: ", NamedTextColor.GREEN));
        message.append(Component.text(duration, NamedTextColor.BLUE));
        message.append(Component.text("\nReason: ", NamedTextColor.GREEN));
        message.append(Component.text(reason, NamedTextColor.BLUE));

        return message.build();
    }

    public static TextComponent banMessage(@NotNull String target, @NotNull String duration, @Nullable String reason, @NotNull Operator operator) {
        TextComponent.Builder message = Component.text();

        message.append(Component.text("Target ", NamedTextColor.GREEN));
        message.append(Component.text(target, NamedTextColor.BLUE));
        message.append(Component.text(" has been banned.\n", NamedTextColor.GREEN));
        message.append(Component.text("Operator: ", NamedTextColor.GREEN));
        message.append(Component.text(operator.name(), NamedTextColor.BLUE));
        message.append(Component.text("\nDuration: ", NamedTextColor.GREEN));
        message.append(Component.text(duration, NamedTextColor.BLUE));
        message.append(Component.text("\nReason: ", NamedTextColor.GREEN));
        message.append(Component.text(reason != null ? reason : "<None>", NamedTextColor.BLUE));

        return message.build();
    }

    public static TextComponent kickMessage(@NotNull Object banInfo) {
        TextComponent.Builder message = Component.text();
        message.append(Component.text("Being kicked from the server\n", NamedTextColor.RED));

        String reason;
        String duration;
        if (banInfo instanceof IPBanInfo info) {
            reason = info.reason();
            duration = info.duration();
        } else if (banInfo instanceof PlayerBanInfo info) {
            reason = info.reason();
            duration = info.duration();
        } else throw new UnsupportedTypeException(banInfo);

        message.append(Component.text("Reason: ", NamedTextColor.GREEN));
        message.append(Component.text(reason, NamedTextColor.BLUE));
        message.append(Component.text("\nDuration: ", NamedTextColor.GREEN));
        message.append(Component.text(duration, NamedTextColor.BLUE));

        return message.build();
    }

    /**
     * Converts a {@link Component} into its string representation using the legacy section
     * serialization format.
     *
     * @param component the {@link Component} to be serialized; must not be null
     * @return the serialized string representation of the given {@link Component}
     */
    public static String toString(@NotNull Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
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
        return toBaseComponent(toString(component));
    }
}
