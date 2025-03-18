package one.tranic.mongoban.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.commands.*;
import one.tranic.mongoban.api.config.NewConfig;
import one.tranic.mongoban.api.message.Message;
import one.tranic.t.bungee.command.warp.BungeeWrap;

@Deprecated
public class MongoBan extends Plugin {
    @Override
    public void onEnable() {
        NewConfig.loadConfig(getDataFolder().toPath());
        Message.reloadMessages();
        try {
            MongoDataAPI.reconnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        registerCommand(new BungeeWrap(new BanCommand<>()));
        registerCommand(new BungeeWrap(new UnBanCommand<>()));
        registerCommand(new BungeeWrap(new WarnCommand<>()));
        registerCommand(new BungeeWrap(new UnWarnCommand<>()));
        registerCommand(new BungeeWrap(new MongoBanCommand<>()));
    }

    private void registerCommand(net.md_5.bungee.api.plugin.Command command) {
        getProxy().getPluginManager().registerCommand(this, command);
    }

    @Override
    public void onDisable() {
    }
}