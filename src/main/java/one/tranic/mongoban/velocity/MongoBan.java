package one.tranic.mongoban.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import one.tranic.mongoban.api.command.source.VelocitySource;
import one.tranic.mongoban.common.Config;
import one.tranic.mongoban.common.cache.Cache;
import one.tranic.mongoban.common.cache.CaffeineCache;
import one.tranic.mongoban.common.cache.RedisCache;
import one.tranic.mongoban.common.commands.BanCommand;
import one.tranic.mongoban.common.database.Database;
import org.jetbrains.annotations.Nullable;

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
    private static Cache cache;

    @Inject
    public MongoBan(ProxyServer proxy, @DataDirectory Path dataDirectory) {
        instance = this;

        Config.loadConfig(dataDirectory);
        cache = Config.getCache() == 0 ? new CaffeineCache() :
                new RedisCache(
                        Config.getRedis().host(),
                        Config.getRedis().port(),
                        Config.getRedis().db(),
                        Config.getRedis().user(),
                        Config.getRedis().password());
        database = new Database(
                Config.getDatabase().host(),
                Config.getDatabase().port(),
                Config.getDatabase().database(),
                Config.getDatabase().user(),
                Config.getDatabase().password(),
                cache);

        MongoBan.proxy = proxy;
    }

    public static Cache getCache() {
        return cache;
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

    private void createCommands() {
        CommandManager commandManager = proxy.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("vban")
                .plugin(this)
                .build();

        @Nullable Command banCommand = new BanCommand<VelocitySource>().unwrapVelocity();

        commandManager.register(commandMeta, banCommand);
    }
}
