package one.tranic.mongoban.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import one.tranic.mongoban.common.Config;

@Deprecated
public class MongoBan extends Plugin {
    @Override
    public void onEnable() {
        Config.loadConfig(getDataFolder().toPath());
    }

    @Override
    public void onDisable() {
    }
}