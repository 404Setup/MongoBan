package one.tranic.mongoban.api.data;

import java.util.UUID;

/**
 * Represents information about a player, including their name, unique identifier,
 * and associated IP addresses.
 * <p>
 * This record encapsulates key details related to a player in a database or system.
 * It is used to store and retrieve information about players, such as their display name,
 * universally unique identifier (UUID), and the list of IP addresses associated with the player.
 * <p>
 * Use cases may include retrieving a player's details for authentication,
 * associating bans or penalties, or for general record-keeping purposes.
 * <p>
 * Immutable by design to ensure safety and consistency when handling player data.
 *
 * @param name The display name of the player.
 * @param uuid The unique identifier of the player.
 * @param ip   An array of IP addresses associated with the player.
 */
public record PlayerInfo(String name, UUID uuid, java.net.InetAddress[] ip) {
}
