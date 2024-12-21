package one.tranic.mongoban.api.command.player;

import one.tranic.mongoban.common.Platform;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Player {
    public static @Nullable <T> T getPlayer(UUID uuid) {
        if (Platform.get() == Platform.Velocity)
            return (T) one.tranic.mongoban.velocity.MongoBan.getProxy().getPlayer(uuid).orElse(null);
        if (Platform.get() == Platform.BungeeCord)
            return (T) net.md_5.bungee.api.ProxyServer.getInstance().getPlayer(uuid);
        return (T) org.bukkit.Bukkit.getPlayer(uuid);
    }

    public static @Nullable <T> T getPlayer(String name) {
        if (Platform.get() == Platform.Velocity)
            return (T) one.tranic.mongoban.velocity.MongoBan.getProxy().getPlayer(name).orElse(null);
        if (Platform.get() == Platform.BungeeCord)
            return (T) net.md_5.bungee.api.ProxyServer.getInstance().getPlayer(name);
        return (T) org.bukkit.Bukkit.getPlayer(name);
    }
}
