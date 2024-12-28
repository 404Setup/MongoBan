package one.tranic.mongoban.api.parse.time;

import one.tranic.mongoban.api.exception.ForeverNonException;
import one.tranic.mongoban.api.exception.ParseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * The TimeParser class provides functionality for parsing time-related string inputs
 * and performing operations such as calculating future times, validating time formats,
 * and checking time conditions.
 */
public class TimeParser {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Parses a time argument string and returns the formatted future time or a special value such as "forever".
     * <p>
     * The input string must specify a time duration and unit (e.g., "10s" for 10 seconds, "2d" for 2 days, etc.),
     * or the literal "forever".
     * <p>
     * Invalid input formats or unsupported time units will result in a {@link ParseException}.
     * <p>
     * Example usage:
     * <pre>
     *     String result1 = TimeParser.parse("10s"); // Returns a formatted future time 10 seconds from now
     *     String result2 = TimeParser.parse("forever"); // Returns "forever"
     *     String result3 = TimeParser.parse("2d"); // Returns a formatted future time 2 days from now
     * </pre>
     *
     * @param arg the time argument as a string; can be literal "forever" or a duration string with a valid unit
     * @return a formatted string representing the calculated future time, or the literal "forever" for the special case
     * @throws ParseException if the argument is null, empty, contains invalid characters, has an unsupported unit,
     *                        or if the numeric portion of the duration is invalid
     */
    public static String parse(String arg) throws ParseException {
        if (arg == null || arg.isBlank())
            throw new ParseException("Time argument cannot be null, empty, or only whitespace.");

        if (arg.equals("forever")) return "forever";

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime future;
            if (arg.endsWith("s")) future = now.plusSeconds(parseTimeValue("s", arg));
            else if (arg.endsWith("m"))
                future = now.plusMinutes(parseTimeValue("m", arg));
            else if (arg.endsWith("h"))
                future = now.plusHours(parseTimeValue("h", arg));
            else if (arg.endsWith("d"))
                future = now.plusDays(parseTimeValue("d", arg));
            else if (arg.endsWith("mo"))
                future = now.plusMonths(parseTimeValue("mo", arg));
            else if (arg.endsWith("y"))
                future = now.plusYears(parseTimeValue("y", arg));
            else
                throw new ParseException("Invalid time format or unsupported time unit: " + arg);
            return future.format(FORMATTER);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid numeric value in argument: '" + arg + "' - " + e.getMessage());
        }
    }

    /**
     * Parses a time value string with a specified unit tag and converts it into a numeric value.
     * The method ensures that the provided string ends with the given tag and that the numeric portion
     * of the string is non-negative.
     *
     * @param tag the unit tag that the time value must end with (e.g., "s" for seconds, "m" for minutes)
     * @param arg the time value string to parse; must end with the specified tag
     * @return the numeric value of the time, extracted from the string
     * @throws ParseException if the provided string does not end with the specified tag,
     *                        if the numeric value cannot be parsed, or if the value is negative
     */
    private static long parseTimeValue(String tag, String arg) throws ParseException {
        if (!arg.endsWith(tag)) {
            throw new ParseException("Time unit '" + tag + "' not found at the end of argument: " + arg);
        }
        String numberPart = arg.substring(0, arg.length() - tag.length());
        long t = Long.parseLong(numberPart);
        if (t < 0) throw new ParseException("Time value cannot be negative: " + arg);
        return t;
    }

    /**
     * Parses a string representation of a time into a {@code LocalDateTime} object.
     * <p>
     * If the input string is "forever", a {@code ForeverNonException} is thrown.
     * <p>
     * If the input string does not match the expected time format, a {@code ParseException} is thrown.
     *
     * @param timeString the string representing the time to parse; must conform to the expected format
     *                   or be the literal "forever"
     * @return the parsed {@code LocalDateTime} object based on the input string
     * @throws ParseException      if the input string is invalid or cannot be parsed into a {@code LocalDateTime}
     * @throws ForeverNonException if the input string is the literal "forever"
     */
    public static LocalDateTime parseStringTime(String timeString) throws ParseException, ForeverNonException {
        if (Objects.equals(timeString, "forever")) {
            throw new ForeverNonException();
        }

        try {
            return LocalDateTime.parse(timeString, FORMATTER);
        } catch (Exception e) {
            throw new ParseException("Invalid time string format: '" + timeString + "'");
        }
    }

    /**
     * Checks if the given LocalDateTime is earlier than the current time.
     *
     * @param dateTime the LocalDateTime object to check
     * @return true if the specified time is in the past; false otherwise
     */
    public static boolean isTimeInPast(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now());
    }
}
