package one.tranic.mongoban.api.data;

import one.tranic.t.base.command.Operator;
import one.tranic.t.base.parse.time.TimeParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents information regarding a player's ban, including details such as the player's unique
 * identifier, the operator responsible for issuing the ban, the duration of the ban, and the reason
 * for the ban.
 * <p>
 * Instances of this record are immutable, ensuring consistent handling of ban-related data.
 *
 * @param uuid     The unique identifier of the banned player.
 * @param name     The player name
 * @param operator The operator responsible for issuing the ban.
 * @param duration The duration of the ban, specified as a string.
 * @param reason   The reason for the ban.
 */
public record PlayerBanInfo(@Nullable UUID uuid, @NotNull String name, @NotNull Operator operator,
                            @NotNull String duration,
                            @NotNull String reason) {
    public boolean expired() {
        if (duration == null || duration.isBlank()) return true;
        if (duration.equals("forever")) return false;
        try {
            return TimeParser.isTimeInPast(TimeParser.parseStringTime(duration));
        } catch (Exception e) {
            return false;
        }
    }
}
