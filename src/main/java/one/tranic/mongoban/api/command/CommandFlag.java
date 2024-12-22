package one.tranic.mongoban.api.command;

/**
 * Represents command flags that can be used to identify and handle
 * specific options in a command-line parsing context.
 * Each flag has a full name and a short name, which can be utilized interchangeably
 * when processing command-line arguments.
 */
public enum CommandFlag {
    RESULT("result", "r"),
    TIME("time", "t");

    private final String fullName;
    private final String shortName;

    CommandFlag(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
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
