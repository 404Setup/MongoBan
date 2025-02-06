package one.tranic.mongoban.api.parse.player;

import one.tranic.mongoban.api.Platform;
import one.tranic.t.util.Collections;
import org.jetbrains.annotations.Range;

import java.util.List;

/**
 * The PlayerParser class provides utility methods for retrieving online player names
 * across different server platform types such as Bukkit, Velocity, and BungeeCord.
 * <p>
 * It offers flexible options for fetching either all online player names or a limited number
 * of names up to a specified maximum count.
 */
public class PlayerParser {
    /**
     * Retrieves a list of player names currently online across different server platforms.
     * <p>
     * This method checks the platform type (Bukkit, Velocity, or BungeeCord) and gathers player names accordingly.
     *
     * @return an unmodifiable list of online player names
     */
    public static List<String> parse() {
        List<String> matchingPlayers = Collections.newArrayList();
        if (Platform.isBukkit()) for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers())
            matchingPlayers.add(player.getName());
        else if (Platform.get() == Platform.Velocity)
            for (com.velocitypowered.api.proxy.Player player : one.tranic.mongoban.velocity.MongoBan.getProxy().getAllPlayers())
                matchingPlayers.add(player.getUsername());
        else
            for (net.md_5.bungee.api.connection.ProxiedPlayer player : net.md_5.bungee.api.ProxyServer.getInstance().getPlayers())
                matchingPlayers.add(player.getName());
        return Collections.newUnmodifiableList(matchingPlayers);
    }

    /**
     * Retrieves a list of player names currently online across different server platforms,
     * limited to a specified maximum number of players.
     * <p>
     * This method determines the current server platform (Bukkit, Velocity, or BungeeCord)
     * and retrieves the player names accordingly.
     * <p>
     * The list will contain a maximum number of players as specified by the input parameter.
     * <p>
     * The returned list is unmodifiable.
     *
     * @param max the maximum number of player names to include in the list; must be greater than or equal to 1
     * @return an unmodifiable list of online player names, limited by the specified maximum
     * @throws IllegalArgumentException if the specified maximum is less than 1
     */
    public static List<String> parse(@Range(from = 1, to = Integer.MAX_VALUE) int max) throws IllegalArgumentException {
        if (max < 1) throw new IllegalArgumentException("Parameter max must be greater than or equal to 1");

        List<String> matchingPlayers = Collections.newArrayList();
        if (Platform.isBukkit()) {
            int count = 0;
            for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (count >= max) break;
                matchingPlayers.add(player.getName());
                count++;
            }
        } else if (Platform.get() == Platform.Velocity) {
            int count = 0;
            for (com.velocitypowered.api.proxy.Player player : one.tranic.mongoban.velocity.MongoBan.getProxy().getAllPlayers()) {
                if (count >= max) break;
                matchingPlayers.add(player.getUsername());
                count++;
            }
        } else {
            int count = 0;
            for (net.md_5.bungee.api.connection.ProxiedPlayer player : net.md_5.bungee.api.ProxyServer.getInstance().getPlayers()) {
                if (count >= max) break;
                matchingPlayers.add(player.getName());
                count++;
            }
        }
        return Collections.newUnmodifiableList(matchingPlayers);
    }
}
