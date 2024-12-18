package one.tranic.mongoban.api.data;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents ban information associated with a player in a system or database.
 * <p>
 * This record encapsulates details regarding a ban issued to a player, including:
 * <p>
 * - The unique identifier (UUID) of the banned player.
 * <p>
 * - The operator responsible for issuing the ban.
 * <p>
 * - The duration of the ban (in an unspecified unit of time).
 * <p>
 * - A reason for the ban, which may be null if no reason is provided.
 * <p>
 * Instances of this record are immutable, ensuring consistency and reliability when handling
 * ban-related data within the system.
 *
 * @param uuid     The unique identifier (UUID) of the banned player.
 * @param operator The operator responsible for issuing the ban.
 * @param duration The duration of the ban, in an unspecified unit of time.
 * @param reason   The reason for the ban.
 */
public record PlayerBanInfo(UUID uuid, Operator operator, int duration, String reason) {
}
