package one.tranic.mongoban.api.event.velocity;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * Represents an event that is triggered when a player is issued a warning.
 * This event contains information about the affected player, the operator who issued the warning,
 * the reason for the warning, and an optional timestamp indicating when the warning was issued.
 */
public class WarnPlayerEvent {
    private final Player player;
    private final String operator;
    private final String reason;
    private final Date time;

    /**
     * Constructs a {@code WarnPlayerEvent} with the specified player, operator, and reason.
     * The timestamp for the warning will be {@code null}.
     *
     * @param player   The player who is being warned. Must not be {@code null}.
     * @param operator The name of the operator who issued the warning. Must not be {@code null}.
     * @param reason   The reason for the warning. Must not be {@code null}.
     * @throws NullPointerException if any of the parameters are {@code null}.
     */
    public WarnPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason) {
        this(player, operator, reason, null);
    }

    /**
     * Constructs a {@code WarnPlayerEvent} with the specified player, operator, reason, and time.
     *
     * @param player   The player who is being warned. Must not be {@code null}.
     * @param operator The name of the operator who issued the warning. Must not be {@code null}.
     * @param reason   The reason for the warning. Must not be {@code null}.
     * @param time     The optional timestamp for when the warning was issued. Can be {@code null}.
     * @throws NullPointerException if {@code player}, {@code operator}, or {@code reason} is {@code null}.
     */
    public WarnPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason, @Nullable Date time) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
        this.time = time;
    }

    /**
     * Gets the player who is being warned.
     *
     * @return The {@link Player} object representing the affected player. Never {@code null}.
     */
    public @NotNull Player getPlayer() {
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
     * Gets the timestamp for when the warning was issued.
     *
     * @return A {@link Date} representing the time of the warning, or {@code null} if no timestamp was provided.
     */
    public @Nullable Date getTime() {
        return time;
    }
}
