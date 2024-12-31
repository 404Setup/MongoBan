package one.tranic.mongoban.api.event.velocity;

import com.google.common.base.Preconditions;
import one.tranic.mongoban.api.data.Operator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents an event that is triggered when a player is issued a warning.
 * This event contains information about the affected player, the operator who issued the warning,
 * the reason for the warning, and an optional timestamp indicating when the warning was issued.
 */
public record WarnPlayerEvent(@NotNull UUID player, @NotNull Operator operator, @NotNull String reason,
                              @Nullable String duration) {
    public WarnPlayerEvent {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(operator, "operator");
        Preconditions.checkNotNull(reason, "reason");
    }
}
