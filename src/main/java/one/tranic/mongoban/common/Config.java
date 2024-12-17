package one.tranic.mongoban.common;

import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Config {
    private static File configFile;
    private static YamlConfiguration configuration;

    private static database database;
    private static boolean updaterCheck = true;
    private static boolean updaterSimpleMode = true;

    public static database getDatabase() {
        return database;
    }

    public static boolean isUpdaterCheck() {
        return updaterCheck;
    }

    public static boolean isUpdaterSimpleMode() {
        return updaterSimpleMode;
    }

    public static void loadConfig(@NotNull Path dataDirectory) {
        configFile = dataDirectory.getParent().resolve("MongoBan").resolve("config.yml").toFile();
        try {
            if (!configFile.exists()) {
                if (!configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdir();
                }
                configFile.createNewFile();
            }
            configuration = YamlConfiguration.loadConfiguration(configFile);
            save();
            read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void read() {
        String database_host = configuration.getString("database.host");
        int database_port = configuration.getInt("database.port");
        String database_dbname = configuration.getString("database.dbname");
        String database_user = configuration.getString("database.user");
        String database_passwd = configuration.getString("database.passwd");

        database = new database(database_host, database_port, database_dbname, database_user, database_passwd);

        updaterCheck = configuration.getBoolean("updater.check");
        updaterSimpleMode = configuration.getBoolean("updater.simple-mode");
    }

    private static void save() throws IOException {
        configuration.addDefault("database.host", "localhost");
        configuration.addDefault("database.port", 27017);
        configuration.addDefault("database.dbname", "MongoBan");
        configuration.addDefault("database.user", "");
        configuration.addDefault("database.passwd", "");

        configuration.addDefault("updater.check", true);
        configuration.addDefault("updater.simple-mode", true);

        configuration.options().copyDefaults(true);
        configuration.save(configFile);
    }

    public record database(String host, int port, String database, String user, String password) {
    }
}
