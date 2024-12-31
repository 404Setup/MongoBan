package one.tranic.mongoban.api.event.velocity;

import com.google.common.base.Preconditions;
import one.tranic.mongoban.api.data.Operator;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents an event that is triggered when a player is unbanned.
 * This event contains information about the affected player, the operator who issued the unban,
 * and the reason for the unban operation.
 */
public record UnbanPlayerEvent(@NotNull UUID player, @NotNull Operator operator) {
    public UnbanPlayerEvent {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(operator, "operator");
    }
}