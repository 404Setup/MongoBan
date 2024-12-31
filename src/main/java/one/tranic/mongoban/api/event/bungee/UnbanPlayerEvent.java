package one.tranic.mongoban.api.event.bungee;

import net.md_5.bungee.api.plugin.Event;
import one.tranic.mongoban.api.data.Operator;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents an event that is triggered when a player is unbanned.
 * This event contains information about the affected player, the operator who issued the unban,
 * and the reason for the unban operation.
 */
public class UnbanPlayerEvent extends Event {
    private final UUID player;
    private final Operator operator;

    /**
     * Constructs an {@code UnbanPlayerEvent} with the specified player and operator.
     * This event is triggered when a player is unbanned and provides details about
     * the player being unbanned and the operator performing the unban action.
     *
     * @param player   The unique identifier (UUID) of the player being unbanned. Must not be {@code null}.
     * @param operator The operator issuing the unban action. Must not be {@code null}.
     * @throws NullPointerException if {@code player} or {@code operator} is {@code null}.
     */
    public UnbanPlayerEvent(@NotNull UUID player, @NotNull Operator operator) {
        this.player = player;
        this.operator = operator;
    }

    /**
     * Retrieves the unique identifier (UUID) of the player associated with this event.
     *
     * @return The {@link UUID} of the player. Never {@code null}.
     */
    public @NotNull UUID getPlayer() {
        return player;
    }

    /**
     * Retrieves the operator responsible for performing the associated action in this event.
     *
     * @return The {@link Operator} who issued the corresponding action. Never {@code null}.
     */
    public @NotNull Operator getOperator() {
        return operator;
    }
}
