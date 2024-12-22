package one.tranic.mongoban.common;

import one.tranic.mongoban.api.Platform;
import one.tranic.mongoban.api.exception.ParseException;

import java.util.List;

public class Parse {
    /**
     * Parses the provided time argument string and calculates a future time in milliseconds
     * by adding the specified time duration to the current system time.
     * <p>
     * The time argument must end with a valid unit suffix: "s" (seconds), "m" (minutes), "h" (hours),
     * "d" (days), "mo" (months), or "y" (years).
     * <p>
     * If the format is invalid or the input cannot be parsed, a {@code ParseException} is thrown.
     *
     * @param arg the time argument string ending with a time unit suffix (e.g., "10s", "5m").
     *            <p>
     *            Supported units are:
     *            <p>
     *            - "s" for seconds
     *            <p>
     *            - "m" for minutes
     *            <p>
     *            - "h" for hours
     *            <p>
     *            - "d" for days
     *            <p>
     *            - "mo" for months
     *            <p>
     *            - "y" for years
     * @return the calculated future time in milliseconds based on the provided time argument
     * @throws ParseException if the input string is null, improperly formatted, or contains
     *                        an invalid numeric value
     */
    public static long timeArg(String arg) throws ParseException {
        long parsedTime = 0;
        if (arg != null) {
            try {
                if (arg.endsWith("s")) {
                    parsedTime = System.currentTimeMillis() + Long.parseLong(arg.replace("s", "")) * 1000;
                } else if (arg.endsWith("m")) {
                    parsedTime = System.currentTimeMillis() + Long.parseLong(arg.replace("m", "")) * 60000;
                } else if (arg.endsWith("h")) {
                    parsedTime = System.currentTimeMillis() + Long.parseLong(arg.replace("h", "")) * 3600000;
                } else if (arg.endsWith("d")) {
                    parsedTime = System.currentTimeMillis() + Long.parseLong(arg.replace("d", "")) * 86400000;
                } else if (arg.endsWith("mo")) {
                    parsedTime = System.currentTimeMillis() + Long.parseLong(arg.replace("mo", "")) * 2592000000L;
                } else if (arg.endsWith("y")) {
                    parsedTime = System.currentTimeMillis() + Long.parseLong(arg.replace("y", "")) * 31536000000L;
                } else {
                    throw new ParseException();
                }
            } catch (NumberFormatException e) {
                throw new ParseException();
            }
        }
        return parsedTime;
    }

    /**
     * Retrieves a list of player names from the currently active platform.
     * <p>
     * The method detects the specific platform (Bukkit, Velocity, or BungeeCord)
     * and collects the names of online players accordingly.
     *
     * @return a list of player names currently online in the detected platform.
     *         Returns an empty list if no players are online or if the platform is unsupported.
     */
    public static List<String> players() {
        List<String> matchingPlayers = Collections.newArrayList();
        if (Platform.isBukkit()) for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers())
            matchingPlayers.add(player.getName());
        else if (Platform.get() == Platform.Velocity)
            for (com.velocitypowered.api.proxy.Player player : one.tranic.mongoban.velocity.MongoBan.getProxy().getAllPlayers())
                matchingPlayers.add(player.getUsername());
        else
            for (net.md_5.bungee.api.connection.ProxiedPlayer player : net.md_5.bungee.api.ProxyServer.getInstance().getPlayers())
                matchingPlayers.add(player.getName());
        return matchingPlayers;
    }
}
