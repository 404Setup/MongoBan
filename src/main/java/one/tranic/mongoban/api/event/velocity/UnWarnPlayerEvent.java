package one.tranic.mongoban.api.event.velocity;

import com.google.common.base.Preconditions;
import one.tranic.mongoban.api.data.Operator;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents an event that is triggered when a player's warning is removed.
 * This event contains information about the affected player, the operator who removed the warning,
 * and the reason for the removal of the warning.
 */
public record UnWarnPlayerEvent(@NotNull UUID player, @NotNull Operator operator) {
    public UnWarnPlayerEvent {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(operator, "operator");
    }
}
