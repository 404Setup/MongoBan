package one.tranic.mongoban.api.event.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that is triggered when a player's warning is removed.
 * This event contains information about the affected player, the operator who removed the warning,
 * and the reason for the removal of the warning.
 * <p>
 * This event implements {@link Cancellable}, allowing listeners to cancel the un-warning operation.
 */
public class UnWarnPlayerEvent extends Event implements Cancellable {
    private final ProxiedPlayer player;
    private final String operator;
    private final String reason;

    private boolean isCancelled;

    /**
     * Constructs an {@code UnWarnPlayerEvent} with the specified player, operator, and reason.
     *
     * @param player   The player whose warning is being removed. Must not be {@code null}.
     * @param operator The name of the operator who removed the warning. Must not be {@code null}.
     * @param reason   The reason for removing the warning. Must not be {@code null}.
     * @throws NullPointerException if any of the parameters are {@code null}.
     */
    public UnWarnPlayerEvent(@NotNull ProxiedPlayer player, @NotNull String operator, @NotNull String reason) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
        this.isCancelled = false;
    }

    /**
     * Gets the player whose warning is being removed.
     *
     * @return The {@link ProxiedPlayer} object representing the affected player. Never {@code null}.
     */
    public @NotNull ProxiedPlayer getPlayer() {
        return player;
    }

    /**
     * Gets the name of the operator who removed the warning.
     *
     * @return A {@link String} representing the operator's name. Never {@code null}.
     */
    public @NotNull String getOperator() {
        return operator;
    }

    /**
     * Gets the reason for removing the warning.
     *
     * @return A {@link String} representing the reason for removing the warning. Never {@code null}.
     */
    public @NotNull String getReason() {
        return reason;
    }

    /**
     * Checks whether this event has been cancelled.
     *
     * @return {@code true} if the event is cancelled, {@code false} otherwise.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets whether this event is cancelled.
     *
     * @param cancel {@code true} to cancel the event, {@code false} to allow it.
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
