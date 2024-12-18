package one.tranic.mongoban.bukkit;

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

            unregisterCommand("ban");
            unregisterCommand("unban");
            unregisterCommand("ban-ip");
            unregisterCommand("unban-ip");
            unregisterCommand("warn");
            unregisterCommand("unwarn");

            //commandMap.register("mongoban", "mongoban", new GPiglinCommand(this));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void unregisterCommand(String name) {
        Command banCommand = commandMap.getKnownCommands().get(name);
        if (banCommand != null) banCommand.unregister(commandMap);
    }

    @Override
    public void onDisable() {}
}
