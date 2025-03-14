package one.tranic.mongoban.bukkit;

import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.message.Message;
import one.tranic.mongoban.common.commands.*;
import one.tranic.mongoban.common.config.NewConfig;
import one.tranic.t.paper.TPaper;
import one.tranic.t.paper.command.source.PaperSource;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class MongoBan extends JavaPlugin {
    private static MongoBan instance;

    private SimpleCommandMap commandMap;

    public static MongoBan getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        TPaper.init(this);
        NewConfig.loadConfig(getDataFolder().toPath());
        Message.reloadMessages();
        try {
            MongoDataAPI.reconnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getPluginManager());

            // I don't know what to do here, so for now.
            unregisterCommand("ban");
            unregisterCommand("unban");
            unregisterCommand("ban-ip");
            unregisterCommand("unban-ip");
            unregisterCommand("warn");
            unregisterCommand("unwarn");

            new BanCommand<PaperSource>().registerWithBukkit(commandMap, "mongoban");
            new UnBanCommand<PaperSource>().registerWithBukkit(commandMap, "mongoban");
            new WarnCommand<PaperSource>().registerWithBukkit(commandMap, "mongoban");
            new UnWarnCommand<PaperSource>().registerWithBukkit(commandMap, "mongoban");
            new MongoBanCommand<PaperSource>().registerWithBukkit(commandMap, "mongoban");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void unregisterCommand(String name) {
        Command banCommand = commandMap.getKnownCommands().get(name);
        if (banCommand != null) banCommand.unregister(commandMap);
    }

    @Override
    public void onDisable() {
        TPaper.disable();
    }
}
