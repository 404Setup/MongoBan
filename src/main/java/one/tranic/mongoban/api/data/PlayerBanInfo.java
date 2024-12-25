package one.tranic.mongoban.api.data;

import one.tranic.mongoban.common.Parse;

import java.util.UUID;

/**
 * Represents information regarding a player's ban, including details such as the player's unique
 * identifier, the operator responsible for issuing the ban, the duration of the ban, and the reason
 * for the ban.
 * <p>
 * Instances of this record are immutable, ensuring consistent handling of ban-related data.
 *
 * @param uuid     The unique identifier of the banned player.
 * @param operator The operator responsible for issuing the ban.
 * @param duration The duration of the ban, specified as a string.
 * @param reason   The reason for the ban.
 */
public record PlayerBanInfo(UUID uuid, Operator operator, String duration, String reason) {
    public boolean expired() {
        if (duration == null || duration.isBlank()) return true;
        if (duration.equals("forever")) return false;
        try {
            return Parse.isTimeInPast(Parse.parseStringTime(duration));
        } catch (Exception e) {
            return false;
        }
    }
}
