package one.tranic.mongoban.api.data;

import java.util.List;
import java.util.UUID;

/**
 * Represents information about a player, including their name, unique identifier,
 * and associated IP addresses.
 *
 * @param name The display name of the player.
 * @param uuid The unique identifier of the player.
 * @param ip   An array of IP addresses associated with the player.
 */
public record PlayerInfo(String name, UUID uuid, List<String> ip) {
}
