package one.tranic.mongoban.api.event.velocity;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that is triggered when a player's warning is removed.
 * This event contains information about the affected player, the operator who removed the warning,
 * and the reason for the removal.
 * <p>
 * This is a record class, which provides immutable data storage for the event details.
 */
public record UnWarnPlayerEvent(Player player, String operator, String reason) {

    /**
     * Constructs an {@code UnWarnPlayerEvent} with the specified player, operator, and reason.
     *
     * @param player   The player whose warning is being removed. Must not be {@code null}.
     * @param operator The name of the operator who removed the warning. Must not be {@code null}.
     * @param reason   The reason for removing the warning. Must not be {@code null}.
     * @throws NullPointerException if any of the parameters are {@code null}.
     */
    public UnWarnPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
    }

    /**
     * Gets the player whose warning is being removed.
     *
     * @return The {@link Player} object representing the affected player. Never {@code null}.
     */
    @Override
    public @NotNull Player player() {
        return player;
    }

    /**
     * Gets the name of the operator who removed the warning.
     *
     * @return A {@link String} representing the operator's name. Never {@code null}.
     */
    @Override
    public @NotNull String operator() {
        return operator;
    }

    /**
     * Gets the reason for removing the warning.
     *
     * @return A {@link String} representing the reason for the removal. Never {@code null}.
     */
    @Override
    public @NotNull String reason() {
        return reason;
    }
}
