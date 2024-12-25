package one.tranic.mongoban.api.event.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * Represents an event that is triggered when a player receives a warning.
 * This event contains details about the affected player, the operator who issued the warning,
 * the reason for the warning, and the optional time when the warning was issued.
 * <p>
 * This event implements {@link Cancellable}, allowing listeners to cancel the warning operation.
 */
public class WarnPlayerEvent extends Event implements Cancellable {
    private final ProxiedPlayer player;
    private final String operator;
    private final String reason;
    private final Date time;

    private boolean isCancelled;

    /**
     * Constructs a {@code WarnPlayerEvent} with the specified player, operator, and reason.
     *
     * @param player   The player who is being warned. Must not be {@code null}.
     * @param operator The name of the operator who issued the warning. Must not be {@code null}.
     * @param reason   The reason for the warning. Must not be {@code null}.
     * @throws NullPointerException if any of the required parameters are {@code null}.
     */
    public WarnPlayerEvent(@NotNull ProxiedPlayer player, @NotNull String operator, @NotNull String reason) {
        this(player, operator, reason, null);
    }

    /**
     * Constructs a {@code WarnPlayerEvent} with the specified player, operator, reason, and time.
     *
     * @param player   The player who is being warned. Must not be {@code null}.
     * @param operator The name of the operator who issued the warning. Must not be {@code null}.
     * @param reason   The reason for the warning. Must not be {@code null}.
     * @param time     The optional time when the warning was issued. Can be {@code null}.
     * @throws NullPointerException if {@code player}, {@code operator}, or {@code reason} is {@code null}.
     */
    public WarnPlayerEvent(@NotNull ProxiedPlayer player, @NotNull String operator, @NotNull String reason, @Nullable Date time) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
        this.time = time;
        this.isCancelled = false;
    }

    /**
     * Gets the player who is being warned.
     *
     * @return The {@link ProxiedPlayer} object representing the warned player. Never {@code null}.
     */
    public @NotNull ProxiedPlayer getPlayer() {
        return player;
    }

    /**
     * Gets the name of the operator who issued the warning.
     *
     * @return A {@link String} representing the operator's name. Never {@code null}.
     */
    public @NotNull String getOperator() {
        return operator;
    }

    /**
     * Gets the reason for the warning.
     *
     * @return A {@link String} representing the reason for the warning. Never {@code null}.
     */
    public @NotNull String getReason() {
        return reason;
    }

    /**
     * Gets the time when the warning was issued.
     *
     * @return A {@link Date} representing the time of the warning, or {@code null} if not specified.
     */
    public @Nullable Date getTime() {
        return time;
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
