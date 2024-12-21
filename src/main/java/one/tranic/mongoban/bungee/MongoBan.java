package one.tranic.mongoban.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import one.tranic.mongoban.common.Config;
import one.tranic.mongoban.common.commands.BanCommand;

@Deprecated
public class MongoBan extends Plugin {
    @Override
    public void onEnable() {
        Config.loadConfig(getDataFolder().toPath());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BanCommand<>().unwrapBungee());
    }

    @Override
    public void onDisable() {
    }
}