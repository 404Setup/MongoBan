package one.tranic.mongoban.api.data;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents information related to a player's warning in a system or database.
 * <p>
 * This record captures details about a warning associated with a player, such as:
 * <p>
 * - The unique identifier (UUID) of the player who received the warning.
 * <p>
 * - The unique identifier (UUID) of the operator or admin who issued the warning.
 * <p>
 * - The duration of the warning (in an unspecified unit, e.g., seconds or minutes).
 * <p>
 * - The reason for the warning, if provided (can be null).
 * <p>
 * Instances of this record are immutable and provide a structured way
 * to store and retrieve warning-related data for players.
 *
 * @param uuid     The unique identifier (UUID) of the player who received the warning.
 * @param operator The unique identifier (UUID) of the operator who issued the warning.
 * @param duration The duration of the warning.
 * @param reason   The reason for the warning, or {@code null} if not provided.
 */
public record PlayerWarnInfo(UUID uuid, UUID operator, int duration, @Nullable String reason) {
}
