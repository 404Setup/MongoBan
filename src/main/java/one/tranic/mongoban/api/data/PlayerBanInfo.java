package one.tranic.mongoban.api.data;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents ban information associated with a player in a system or database.
 * <p>
 * This record encapsulates relevant details regarding a ban issued to a player, including:
 * <p>
 * - The unique identifier (UUID) of the banned player.
 * <p>
 * - The unique identifier (UUID) of the operator or authority responsible for the ban.
 * <p>
 * - The duration of the ban (in an unspecified unit, e.g., seconds or minutes).
 * <p>
 * - A reason for the ban, which may be null if no reason is provided.
 * <p>
 * Instances of this record are immutable and provide a consistent structure for
 * handling ban-related data, enhancing reliability when managing player bans.
 *
 * @param uuid     The unique identifier (UUID) of the banned player.
 * @param operator The unique identifier (UUID) of the operator responsible for issuing the ban.
 * @param duration The duration of the ban.
 * @param reason   The reason for the ban, or {@code null} if not specified.
 */
public record PlayerBanInfo(UUID uuid, UUID operator, int duration, @Nullable String reason) {
}
