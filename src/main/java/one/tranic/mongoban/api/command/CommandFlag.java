package one.tranic.mongoban.api.command;

public enum CommandFlag {
    RESULT("result", "r"),
    TIME("time", "t");

    private final String fullName;
    private final String shortName;

    CommandFlag(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public static CommandFlag fromString(String flag) {
        for (CommandFlag f : values()) {
            if (f.fullName.equals(flag) || f.shortName.equals(flag)) {
                return f;
            }
        }
        return null;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }
}
