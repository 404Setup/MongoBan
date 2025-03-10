package one.tranic.mongoban.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.message.Message;
import one.tranic.mongoban.common.commands.*;
import one.tranic.mongoban.common.config.NewConfig;
import one.tranic.t.velocity.TVelocity;
import one.tranic.t.velocity.command.source.VelocitySource;

import java.nio.file.Path;

@Plugin(
        id = "mongoban",
        name = "MongoBan",
        version = BuildConstants.VERSION,
        url = "https://tranic.one",
        dependencies = {
                @Dependency(id = "geyser", optional = true),
                @Dependency(id = "floodgate", optional = true)},
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
        NewConfig.loadConfig(dataDirectory);
        Message.reloadMessages();
        TVelocity.init(proxy);

        try {
            MongoDataAPI.reconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        createCommands();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        TVelocity.disable();
        try {
            MongoDataAPI.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createCommands() {
        new BanCommand<VelocitySource>().registerWithVelocity(this, proxy);
        new UnBanCommand<VelocitySource>().registerWithVelocity(this, proxy);
        new WarnCommand<VelocitySource>().registerWithVelocity(this, proxy);
        new UnWarnCommand<VelocitySource>().registerWithVelocity(this, proxy);
        new MongoBanCommand<VelocitySource>().registerWithVelocity(this, proxy);
    }
}
