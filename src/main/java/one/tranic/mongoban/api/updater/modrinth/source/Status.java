package one.tranic.mongoban.api.updater.modrinth.source;

import org.jetbrains.annotations.NotNull;

public enum Status {
    LISTED, ARCHIVED, DRAFT, UNLISTED, SCHEDULED, UNKNOWN;

    public static @NotNull Status of(@NotNull String value) {
        return switch (value.toLowerCase()) {
            case "listed" -> LISTED;
            case "archived" -> ARCHIVED;
            case "draft" -> DRAFT;
            case "unlisted" -> UNLISTED;
            case "scheduled" -> SCHEDULED;
            case "unknown" -> UNKNOWN;
            default -> throw new IllegalArgumentException("Unknown status: " + value);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case LISTED -> "listed";
            case ARCHIVED -> "archived";
            case DRAFT -> "draft";
            case UNLISTED -> "unlisted";
            case SCHEDULED -> "scheduled";
            case UNKNOWN -> "unknown";
        };
    }
}
