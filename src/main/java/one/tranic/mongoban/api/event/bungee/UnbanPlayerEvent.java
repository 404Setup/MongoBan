package one.tranic.mongoban.api.event.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that is triggered when a player is unbanned.
 * This event contains information about the affected player, the operator who issued the unban,
 * and the reason for the unban operation.
 * <p>
 * This event implements {@link Cancellable}, allowing listeners to cancel the unban operation.
 */
public class UnbanPlayerEvent extends Event implements Cancellable {
    private final ProxiedPlayer player;
    private final String operator;
    private final String reason;

    private boolean isCancelled;

    /**
     * Constructs an {@code UnbanPlayerEvent} with the specified player, operator, and reason.
     *
     * @param player   The player who is being unbanned. Must not be {@code null}.
     * @param operator The name of the operator who issued the unban. Must not be {@code null}.
     * @param reason   The reason for the unban. Must not be {@code null}.
     * @throws NullPointerException if any of the parameters are {@code null}.
     */
    public UnbanPlayerEvent(@NotNull ProxiedPlayer player, @NotNull String operator, @NotNull String reason) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
        this.isCancelled = false;
    }

    /**
     * Gets the player who is being unbanned.
     *
     * @return The {@link ProxiedPlayer} object representing the affected player. Never {@code null}.
     */
    public @NotNull ProxiedPlayer getPlayer() {
        return player;
    }

    /**
     * Gets the name of the operator who issued the unban.
     *
     * @return A {@link String} representing the operator's name. Never {@code null}.
     */
    public @NotNull String getOperator() {
        return operator;
    }

    /**
     * Gets the reason for the unban.
     *
     * @return A {@link String} representing the reason for the unban. Never {@code null}.
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
