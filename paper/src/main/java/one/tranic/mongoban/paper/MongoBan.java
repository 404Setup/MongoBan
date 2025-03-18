package one.tranic.mongoban.paper;

import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.commands.*;
import one.tranic.mongoban.api.config.NewConfig;
import one.tranic.mongoban.api.message.Message;
import one.tranic.t.bukkit.command.warp.BukkitWrap;
import one.tranic.t.paper.TPaper;
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

            registerCommand(new BukkitWrap(new BanCommand<>()));
            registerCommand(new BukkitWrap(new UnBanCommand<>()));
            registerCommand(new BukkitWrap(new WarnCommand<>()));
            registerCommand(new BukkitWrap(new UnWarnCommand<>()));
            registerCommand(new BukkitWrap(new MongoBanCommand<>()));
            registerCommand(new BukkitWrap(new MongoBanCommand<>()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void registerCommand(Command command) {
        commandMap.register("mongoban", command);
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
