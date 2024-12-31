package one.tranic.mongoban.api.event.velocity;

import com.google.common.base.Preconditions;
import one.tranic.mongoban.api.data.Operator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents an event that is triggered when a player is banned.
 * This event contains details about the affected player, the operator who issued the ban,
 * the reason for the ban, and the optional duration of the ban.
 */
public record BanPlayerEvent(@NotNull UUID player, @NotNull Operator operator, @NotNull String reason,
                             @Nullable String duration) {
    public BanPlayerEvent {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(operator, "operator");
        Preconditions.checkNotNull(reason, "reason");
    }
}