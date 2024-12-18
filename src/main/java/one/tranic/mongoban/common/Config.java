package one.tranic.mongoban.common;

import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Config {
    private static File configFile;
    private static YamlConfiguration configuration;

    private static int cache;
    private static database database;
    private static redis redis;
    private static boolean updaterCheck = true;
    private static boolean updaterSimpleMode = true;

    public static int getCache() {
        return cache;
    }

    public static database getDatabase() {
        return database;
    }

    public static redis getRedis() {
        return redis;
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
        cache = configuration.getInt("cache");

        {
            String database_host = configuration.getString("database.host");
            int database_port = configuration.getInt("database.port");
            String database_dbname = configuration.getString("database.dbname");
            String database_user = configuration.getString("database.user");
            String database_passwd = configuration.getString("database.passwd");

            database = new database(database_host, database_port, database_dbname, database_user, database_passwd);
        }

        {
            String redis_host = configuration.getString("redis.host");
            int redis_port = configuration.getInt("redis.port");
            int redis_db = configuration.getInt("redis.db");
            String redis_user = configuration.getString("redis.user");
            String redis_passwd = configuration.getString("redis.passwd");

            redis = new redis(redis_host, redis_port, redis_db, redis_user, redis_passwd);
        }

        updaterCheck = configuration.getBoolean("updater.check");
        updaterSimpleMode = configuration.getBoolean("updater.simple-mode");
    }

    private static void save() throws IOException {
        configuration.addDefault("cache", 0);

        configuration.addDefault("database.host", "localhost");
        configuration.addDefault("database.port", 27017);
        configuration.addDefault("database.dbname", "MongoBan");
        configuration.addDefault("database.user", "");
        configuration.addDefault("database.passwd", "");

        configuration.addDefault("redis.host", "localhost");
        configuration.addDefault("redis.port", 6379);
        configuration.addDefault("redis.user", "");
        configuration.addDefault("redis.passwd", "");
        configuration.addDefault("redis.db", 0);

        configuration.addDefault("updater.check", true);
        configuration.addDefault("updater.simple-mode", true);

        configuration.options().copyDefaults(true);
        configuration.save(configFile);
    }

    public record database(String host, int port, String database, String user, String password) {
    }

    public record redis(String host, int port, int db, String user, String password) {
    }
}
