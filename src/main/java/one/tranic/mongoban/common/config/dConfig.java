package one.tranic.mongoban.common.config;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import one.tranic.mongoban.api.message.Message;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class dConfig {
    private final File configFile;

    public dConfig(@NotNull File configFile) {
        this.configFile = configFile;
    }

    public Config config() throws UnsupportedOperationException, IOException {
        YamlMapping yaml = yaml();

        String lang = yaml.string("language");
        Locale language;
        if (lang == null || lang.isBlank()) {
            language = Locale.getDefault();
            if (!Message.isSupportedLocale(language)) language = Locale.US;
        } else {
            language = Locale.forLanguageTag(lang);
            if (language == null || !Message.isSupportedLocale(language)) language = Locale.US;
        }

        int cache = yaml.integer("cache");

        YamlMapping db = yaml.yamlMapping("database");
        YamlMapping redis = yaml.yamlMapping("redis");
        YamlMapping updater = yaml.yamlMapping("updater");

        return new Config(
                language,
                cache,
                new Config.database(
                        db.string("host"),
                        db.integer("port"),
                        db.string("dbname"),
                        db.string("user"),
                        db.string("passwd")
                ),
                new Config.redis(
                        redis.string("host"),
                        redis.integer("port"),
                        redis.integer("db"),
                        redis.string("user"),
                        redis.string("passwd")
                ),
                new Config.updater(
                        updater.bool("enable"),
                        updater.bool("simple-mode")
                )
        );
    }

    public void create() {
        YamlMapping yaml = Yaml.createYamlMappingBuilder()
                .add("language", "en-US")
                .add("cache", 0)
                .add("database",
                        Yaml.createYamlMappingBuilder()
                                .add("host", "localhost")
                                .add("port", 27017)
                                .add("dbname", "MongoBan")
                                .add("user", "")
                                .add("passwd", "")
                                .build()
                ).add("redis",
                        Yaml.createYamlMappingBuilder()
                                .add("host", "localhost")
                                .add("port", 6379)
                                .add("user", "")
                                .add("passwd", "")
                                .add("db", 0)
                                .build()
                ).add("updater",
                        Yaml.createYamlMappingBuilder()
                                .add("enable", true)
                                .add("simple-mode", true)
                                .build()
                )
                .build();

        if (configFile.length() > 0) {
            throw new IllegalStateException("Configuration file is not empty.");
        }

        try (
                FileOutputStream outputStream = new FileOutputStream(configFile);
                BufferedOutputStream bufferedOutput = new BufferedOutputStream(outputStream)
        ) {
            bufferedOutput.write(yaml.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to process config file.", e);
        }
    }

    private YamlMapping yaml() throws IOException {
        return Yaml.createYamlInput(
                configFile
        ).readYamlMapping();
    }
}
