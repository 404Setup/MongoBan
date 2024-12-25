package one.tranic.mongoban.api.data;

import one.tranic.mongoban.common.Parse;

/**
 * Encapsulates information related to an IP ban in a system.
 * <p>
 * This record is used to store and handle details about a ban applied to
 * a specific IP address, including the responsible operator, duration, and reason.
 * <p>
 * The class provides functionality to determine if the ban has already expired.
 * <p>
 * An expiration is determined based on the parsed duration of the ban and the current
 * time when the check is performed.
 * <p>
 * Instances of this record are immutable, ensuring reliable and safe handling of IP ban data.
 *
 * @param ip       The IP address that the ban is applied to.
 * @param operator The operator responsible for issuing the ban.
 * @param duration The duration of the ban, specified as a string.
 * @param reason   The reason for imposing the ban.
 */
public record IPBanInfo(String ip, Operator operator, String duration, String reason) {
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
