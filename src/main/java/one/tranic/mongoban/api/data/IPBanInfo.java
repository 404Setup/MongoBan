package one.tranic.mongoban.api.data;

/**
 * Represents information about an IP ban in a system or database.
 * <p>
 * This record encapsulates details specifically related to banning an IP address.
 * It is used to store and retrieve the details of an IP ban, including:
 * <p>
 * - The IP address that is banned.
 * <p>
 * - The operator responsible for issuing the ban.
 * <p>
 * - The duration of the ban.
 * <p>
 * - The reason for the ban, providing context or justification.
 * <p>
 * Instances of this record are immutable and provide a consistent structure
 * for handling IP ban-related data, ensuring reliability and accuracy when
 * managing bans on specific IPs.
 *
 * @param ip       The IP address that is banned.
 * @param operator The operator responsible for issuing the ban.
 * @param duration The duration of the ban, in an unspecified unit of time.
 * @param reason   The reason for the ban, providing context or justification.
 */
public record IPBanInfo(String ip, Operator operator, int duration, String reason) {
}
