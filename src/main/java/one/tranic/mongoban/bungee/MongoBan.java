package one.tranic.mongoban.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.message.Message;
import one.tranic.mongoban.common.commands.*;
import one.tranic.mongoban.common.config.NewConfig;
import one.tranic.t.bungee.command.source.BungeeSource;

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

        new BanCommand<BungeeSource>().registerWithBungee(this);
        new UnBanCommand<BungeeSource>().registerWithBungee(this);
        new WarnCommand<BungeeSource>().registerWithBungee(this);
        new UnWarnCommand<BungeeSource>().registerWithBungee(this);
        new MongoBanCommand<BungeeSource>().registerWithBungee(this);
    }

    @Override
    public void onDisable() {
    }
}