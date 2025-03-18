package one.tranic.mongoban.api.message;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.config.NewConfig;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.t.base.exception.UnsupportedTypeException;
import one.tranic.t.base.message.MessageFormat;
import one.tranic.t.base.parse.resource.ResourceParser;
import one.tranic.t.utils.Collections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Message {
    public static final Locale Locale_China_Yue = Locale.of("zh", "Yue"); // test
    public static final Locale Locale_China_Nan = Locale.of("zh", "Nan"); // test
    private static final Map<MessageKey, String> messages = Collections.newHashMap();
    private static final List<Locale> supportedLocales = Collections.newArrayList();

    static {
        supportedLocales.add(Locale.US);
        supportedLocales.add(Locale.GERMANY);
        supportedLocales.add(Locale.FRANCE);
        supportedLocales.add(Locale.JAPAN);
        supportedLocales.add(Locale.SIMPLIFIED_CHINESE);
        supportedLocales.add(Locale.TRADITIONAL_CHINESE);
        supportedLocales.add(Locale_China_Yue);
        supportedLocales.add(Locale_China_Nan);
    }

    public static boolean isSupportedLocale(Locale locale) {
        return supportedLocales.contains(locale);
    }

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
        MongoBanAPI.logger.info("Load {} data from the language file", i);
    }

    public static void reloadMessages() {
        if (!messages.isEmpty()) messages.clear();

        MongoBanAPI.logger.info("Searching for language packs: {}....", NewConfig.getConfig().language().toLanguageTag());
        try (InputStream data = ResourceParser.resource("language/" + NewConfig.getConfig().language().toLanguageTag() + ".yaml")) {
            getMessages(yaml(data));
        } catch (Exception ignored) {
            MongoBanAPI.logger.error("The language pack {} was not found or the file is damaged. Reading the default language pack: {}..."
                    , NewConfig.getConfig().language().toLanguageTag()
                    , Locale.US.toLanguageTag());
            try (InputStream data = ResourceParser.resource("language/" + Locale.US.toLanguageTag() + ".yaml")) {
                getMessages(yaml(data));
            } catch (IOException e) {
                MongoBanAPI.logger.error("Failed to read the default language pack: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public static Component kickMessage(@NotNull Object banInfo) {
        String reason;
        String duration;
        if (banInfo instanceof IPBanInfo info) {
            reason = info.reason();
            duration = info.duration();
        } else if (banInfo instanceof PlayerBanInfo info) {
            reason = info.reason();
            duration = info.duration();
        } else throw new UnsupportedTypeException(banInfo);

        return MessageKey.KICK_MESSAGE.format(
                new MessageFormat("reason", Component.text(reason, NamedTextColor.BLUE)),
                new MessageFormat("duration", Component.text(duration, NamedTextColor.BLUE))
        );
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
}
