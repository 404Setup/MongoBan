package one.tranic.mongoban.common;

import one.tranic.mongoban.api.Platform;
import one.tranic.mongoban.api.exception.ParseException;
import org.jetbrains.annotations.Range;

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
     * Retrieves a list of player names currently online across different server platforms.
     * <p>
     * This method checks the platform type (Bukkit, Velocity, or BungeeCord) and gathers player names accordingly.
     *
     * @return an unmodifiable list of online player names
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
    public static List<String> players(@Range(from = 1, to = Integer.MAX_VALUE) int max) throws IllegalArgumentException {
        if (max < 1) {
            throw new IllegalArgumentException("Parameter max must be greater than or equal to 1");
        }
        List<String> matchingPlayers;
        if (Platform.isBukkit()) {
            matchingPlayers = org.bukkit.Bukkit.getOnlinePlayers().stream()
                    .map(org.bukkit.entity.Player::getName)
                    .limit(max)
                    .collect(Collections::newArrayList, List::add, List::addAll);
        } else if (Platform.get() == Platform.Velocity) {
            matchingPlayers = one.tranic.mongoban.velocity.MongoBan.getProxy().getAllPlayers().stream()
                    .map(com.velocitypowered.api.proxy.Player::getUsername)
                    .limit(max)
                    .collect(Collections::newArrayList, List::add, List::addAll);
        } else {
            matchingPlayers = net.md_5.bungee.api.ProxyServer.getInstance().getPlayers().stream()
                    .map(net.md_5.bungee.api.connection.ProxiedPlayer::getName)
                    .limit(max)
                    .collect(Collections::newArrayList, List::add, List::addAll);
        }
        return Collections.newUnmodifiableList(matchingPlayers);
    }
}
