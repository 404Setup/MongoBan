package one.tranic.mongoban.api.event.bungee;

import net.md_5.bungee.api.plugin.Event;
import one.tranic.mongoban.api.data.Operator;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents an event that is triggered when a player's warning is removed.
 * This event contains information about the affected player, the operator who removed the warning,
 * and the reason for the removal of the warning.
 */
public class UnWarnPlayerEvent extends Event {
    private final UUID player;
    private final Operator operator;

    /**
     * Constructs an {@code UnWarnPlayerEvent} with the specified player and operator.
     * This event is triggered when a warning issued to a player is removed.
     *
     * @param player   The unique identifier (UUID) of the player whose warning is being removed. Must not be {@code null}.
     * @param operator The operator responsible for removing the warning. Must not be {@code null}.
     * @throws NullPointerException if {@code player} or {@code operator} is {@code null}.
     */
    public UnWarnPlayerEvent(@NotNull UUID player, @NotNull Operator operator) {
        this.player = player;
        this.operator = operator;
    }

    /**
     * Retrieves the unique identifier (UUID) of the player associated with this event.
     *
     * @return The {@link UUID} of the player. Never null.
     */
    public @NotNull UUID getPlayer() {
        return player;
    }

    /**
     * Retrieves the operator responsible for executing the action associated with this event.
     *
     * @return The {@link Operator} object representing the operator. Never {@code null}.
     */
    public @NotNull Operator getOperator() {
        return operator;
    }
}
