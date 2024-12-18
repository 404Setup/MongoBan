package one.tranic.mongoban.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import one.tranic.mongoban.common.Config;
import one.tranic.mongoban.common.database.Database;

import java.nio.file.Path;

@Plugin(
        id = "mongoban",
        name = "MongoBan",
        version = BuildConstants.VERSION,
        url = "https://tranic.one",
        authors = {"404"}
)
public class MongoBan {
    private static ProxyServer proxy;
    private static MongoBan instance;
    private static Database database;

    @Inject
    public MongoBan(ProxyServer proxy, @DataDirectory Path dataDirectory) {
        instance = this;

        Config.loadConfig(dataDirectory);
        database = new Database(Config.getDatabase().host(), Config.getDatabase().port(), Config.getDatabase().database(), Config.getDatabase().user(), Config.getDatabase().password());

        MongoBan.proxy = proxy;
    }

    public static Database getDatabase() {
        return database;
    }

    public static ProxyServer getProxy() {
        return proxy;
    }

    public static MongoBan getInstance() {
        return instance;
    }
}
