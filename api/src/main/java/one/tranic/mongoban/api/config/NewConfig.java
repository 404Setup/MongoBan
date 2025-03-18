package one.tranic.mongoban.api.config;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

public class NewConfig {
    private static Config config;
    private static File configFile;

    public static void loadConfig(@NotNull Path dataDirectory) {
        Path localPath = dataDirectory.getParent().resolve("MongoBan");
        configFile = localPath.resolve("config.yml").toFile();
        try {
            if (!configFile.exists()) {
                if (!configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdir();
                }
                configFile.createNewFile();

                dConfig cfg = new dConfig(configFile);
                if (configFile.length() < 1)
                    cfg.create();
                config = cfg.config();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Config getConfig() {
        return config;
    }
}
