package one.tranic.mongoban.api.parse.player;

import one.tranic.t.utils.Collections;
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
        for (one.tranic.t.base.player.Player<?> player : one.tranic.t.base.player.Players.getOnlinePlayers())
            matchingPlayers.add(player.getUsername());
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
        int count = 0;
        for (one.tranic.t.base.player.Player<?> player : one.tranic.t.base.player.Players.getOnlinePlayers()) {
            if (count >= max) break;
            matchingPlayers.add(player.getUsername());
            count++;
        }
        return Collections.newUnmodifiableList(matchingPlayers);
    }
}
