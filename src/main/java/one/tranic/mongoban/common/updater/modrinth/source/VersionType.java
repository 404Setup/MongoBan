package one.tranic.mongoban.common.updater.modrinth.source;

import org.jetbrains.annotations.NotNull;

public enum VersionType {
    RELEASE, BETA, ALPHA;

    public @NotNull VersionType of(@NotNull final String value) {
        return switch (value.toLowerCase()) {
            case "release" -> RELEASE;
            case "beta" -> BETA;
            case "alpha" -> ALPHA;
            default -> throw new IllegalArgumentException("Invalid version type: " + value);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case RELEASE -> "release";
            case BETA -> "beta";
            case ALPHA -> "alpha";
        };
    }
}
