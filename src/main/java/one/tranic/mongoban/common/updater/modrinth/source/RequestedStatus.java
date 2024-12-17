package one.tranic.mongoban.common.updater.modrinth.source;

import org.jetbrains.annotations.Nullable;

public enum RequestedStatus {
    LISTED, ARCHIVED, DRAFT, UNLISTED;

    public static @Nullable RequestedStatus of(@Nullable String value) {
        if (value == null) {
            return null;
        }
        return switch (value.toLowerCase()) {
            case "listed" -> LISTED;
            case "archived" -> ARCHIVED;
            case "draft" -> DRAFT;
            case "unlisted" -> UNLISTED;
            default -> throw new IllegalArgumentException("Unknown requested status: " + value);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case LISTED -> "listed";
            case ARCHIVED -> "archived";
            case DRAFT -> "draft";
            case UNLISTED -> "unlisted";
        };
    }
}
