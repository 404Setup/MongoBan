package one.tranic.mongoban.bukkit;

import one.tranic.mongoban.common.commands.BanCommand;
import one.tranic.mongoban.api.command.source.PaperSource;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class MongoBan extends JavaPlugin {
    private SimpleCommandMap commandMap;

    @Override
    public void onEnable() {
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
    }
}
