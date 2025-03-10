package one.tranic.mongoban.api.command;

import one.tranic.t.utils.Collections;

import java.util.List;

/**
 * Represents command flags that can be used to identify and handle
 * specific options in a command-line parsing context.
 * <p>
 * Each flag has a full name and a short name, which can be utilized interchangeably
 * when processing command-line arguments.
 */
@Deprecated
public enum CommandFlag {
    TARGET("target", "e"),
    REASON("reason"),
    TIME("time"),
    // In strict mode, if the selector is a player (not an IP), the player's IP will be banned
    STRICT("strict");

    private final String fullName;
    private final String shortName;

    CommandFlag(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    CommandFlag(String fullName) {
        this.fullName = fullName;
        this.shortName = fullName.substring(0, 1);
    }

    /**
     * Converts a given string to a corresponding {@code CommandFlag} instance.
     * <p>
     * The provided string is compared with both the full name and short name
     * of each {@code CommandFlag} to determine a match.
     *
     * @param flag the string representation of the flag, which can be either the full name
     *             or the short name of a {@code CommandFlag}
     * @return the matching {@code CommandFlag} if a match is found; {@code null} otherwise
     */
    public static CommandFlag fromString(String flag) {
        for (CommandFlag f : values()) {
            if (f.fullName.equals(flag) || f.shortName.equals(flag)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Retrieves an unmodifiable list of the full names of all command flags.
     *
     * @return a list containing the full names of all command flags in the order they are defined
     */
    public static List<String> getFullNames() {
        List<String> list = Collections.newArrayList();
        for (CommandFlag f : values()) list.add(f.fullName);
        return Collections.newUnmodifiableList(list);
    }

    /**
     * Retrieves a list of all the short names associated with the available command flags.
     *
     * @return an unmodifiable list of short names for all defined command flags
     */
    public static List<String> getShortNames() {
        List<String> list = Collections.newArrayList();
        for (CommandFlag f : values()) list.add(f.shortName);
        return Collections.newUnmodifiableList(list);
    }

    /**
     * Retrieves the full name representation associated with the command flag.
     *
     * @return the full name of the command flag as a string
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Retrieves the short name of the command flag.
     *
     * @return the short name associated with this command flag.
     */
    public String getShortName() {
        return shortName;
    }
}
