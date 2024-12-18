package one.tranic.mongoban.api.data;

import java.util.UUID;

/**
 * Represents information about an IP address-based ban in a system or database.
 * <p>
 * This record contains details related to a ban issued to a specific IP address,
 * including the IP address itself, the unique identifier (UUID) of the operator
 * responsible for issuing the ban, the duration of the ban, and the reason for the ban.
 * <p>
 * Instances of this record are immutable and designed to provide a clear and consistent
 * structure for managing and processing IP ban data.
 *
 * @param ip       The IP address that has been banned.
 * @param operator The unique identifier (UUID) of the operator responsible for issuing the ban.
 * @param duration The duration of the ban in an unspecified unit.
 * @param reason   The reason for the ban.
 */
public record IPBanInfo(String ip, UUID operator, int duration, String reason) {
}
