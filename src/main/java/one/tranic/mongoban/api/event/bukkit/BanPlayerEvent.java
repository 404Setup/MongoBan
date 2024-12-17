package one.tranic.mongoban.api.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * Represents an event that is triggered when a player is banned.
 * This event contains information about the affected player, the operator who issued the ban,
 * the reason for the ban, and an optional timestamp indicating when the ban occurred.
 * <p>
 * This event implements {@link Cancellable}, allowing listeners to cancel the ban operation.
 */
public class BanPlayerEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Player player;
    private final String operator;
    private final String reason;
    private final Date time;

    private boolean isCancelled;

    /**
     * Constructs a {@code BanPlayerEvent} with the specified player, operator, and reason.
     * The timestamp for the ban will be {@code null}.
     *
     * @param player   The player who is being banned. Must not be {@code null}.
     * @param operator The name of the operator who issued the ban. Must not be {@code null}.
     * @param reason   The reason for the ban. Must not be {@code null}.
     * @throws NullPointerException if any of the parameters are {@code null}.
     */
    public BanPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason) {
        this(player, operator, reason, null);
    }

    /**
     * Constructs a {@code BanPlayerEvent} with the specified player, operator, reason, and time.
     *
     * @param player   The player who is being banned. Must not be {@code null}.
     * @param operator The name of the operator who issued the ban. Must not be {@code null}.
     * @param reason   The reason for the ban. Must not be {@code null}.
     * @param time     The optional timestamp for when the ban occurred. Can be {@code null}.
     * @throws NullPointerException if {@code player}, {@code operator}, or {@code reason} is {@code null}.
     */
    public BanPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason, @Nullable Date time) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
        this.time = time;
        this.isCancelled = false;
    }

    /**
     * Gets the static handler list for this event.
     *
     * @return The {@link HandlerList} for all instances of this event.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    /**
     * Gets the player who is being banned.
     *
     * @return The {@link Player} object representing the affected player. Never {@code null}.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Gets the name of the operator who issued the ban.
     *
     * @return A {@link String} representing the operator's name. Never {@code null}.
     */
    public @NotNull String getOperator() {
        return operator;
    }

    /**
     * Gets the reason for the ban.
     *
     * @return A {@link String} representing the reason for the ban. Never {@code null}.
     */
    public @NotNull String getReason() {
        return reason;
    }

    /**
     * Gets the timestamp for when the ban occurred.
     *
     * @return A {@link Date} representing the time of the ban, or {@code null} if no timestamp was provided.
     */
    public @Nullable Date getTime() {
        return time;
    }

    /**
     * Gets the handler list for this event.
     *
     * @return The {@link HandlerList} for this event. Never {@code null}.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
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
