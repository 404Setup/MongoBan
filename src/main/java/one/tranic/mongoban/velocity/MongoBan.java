package one.tranic.mongoban.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.command.source.VelocitySource;
import one.tranic.mongoban.common.Config;
import one.tranic.mongoban.common.commands.BanCommand;
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
    private final Path dataDirectory;

    @Inject
    public MongoBan(ProxyServer proxy, @DataDirectory Path dataDirectory) {
        instance = this;
        MongoBan.proxy = proxy;

        this.dataDirectory = dataDirectory;
    }

    public static ProxyServer getProxy() {
        return proxy;
    }

    public static MongoBan getInstance() {
        return instance;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Config.loadConfig(dataDirectory);
        MongoDataAPI.reconnect();

        createCommands();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        MongoDataAPI.close();
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
