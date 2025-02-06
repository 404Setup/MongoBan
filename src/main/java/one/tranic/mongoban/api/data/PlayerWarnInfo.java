package one.tranic.mongoban.api.data;

import one.tranic.t.base.command.Operator;
import one.tranic.t.base.parse.time.TimeParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents warning information associated with a player in a system or database.
 * This record encapsulates details regarding a warning issued to a player, including:
 * <p>
 * - The unique identifier (UUID) of the warned player.
 * <p>
 * - The operator responsible for issuing the warning.
 * <p>
 * - The duration of the warning (in an unspecified unit of time).
 * <p>
 * - A reason for the warning, which may be null if no reason is provided.
 * <p>
 * Instances of this record are immutable, ensuring consistency and reliability when handling
 * warning-related data within the system.
 *
 * @param uuid     The unique identifier (UUID) of the warned player.
 * @param operator The operator responsible for issuing the warning.
 * @param duration The duration of the warning, in an unspecified unit of time.
 * @param reason   The reason for the warning, or {@code null} if no reason is provided.
 */
public record PlayerWarnInfo(UUID uuid, Operator operator, @NotNull String id, @NotNull String duration,
                             @Nullable String reason) {
    public boolean expired() {
        if (duration.isBlank() || duration.equals("forever")) return true;
        try {
            return TimeParser.isTimeInPast(TimeParser.parseStringTime(duration));
        } catch (Exception e) {
            return false;
        }
    }
}
