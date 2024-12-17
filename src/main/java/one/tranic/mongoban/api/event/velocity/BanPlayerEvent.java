package one.tranic.mongoban.api.event.velocity;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * Represents an event that is triggered when a player is banned on the velocity server.
 * This event contains information about the banned player, the operator performing the ban,
 * the reason for the ban, and an optional timestamp specifying when the ban occurred.
 */
public class BanPlayerEvent {

    private final Player player;
    private final String operator;
    private final String reason;
    private final Date time;

    /**
     * Constructs a {@code BanPlayerEvent} with the specified player, operator, and reason.
     * The timestamp of the ban is set to {@code null}.
     *
     * @param player   The player who is banned. Must not be {@code null}.
     * @param operator The name or identifier of the operator performing the ban. Must not be {@code null}.
     * @param reason   The reason for banning the player. Must not be {@code null}.
     * @throws NullPointerException If any of the {@code player}, {@code operator}, or {@code reason} parameters is {@code null}.
     */
    public BanPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason) {
        this(player, operator, reason, null);
    }

    /**
     * Constructs a {@code BanPlayerEvent} with the specified player, operator, reason, and timestamp.
     *
     * @param player   The player who is banned. Must not be {@code null}.
     * @param operator The name or identifier of the operator performing the ban. Must not be {@code null}.
     * @param reason   The reason for banning the player. Must not be {@code null}.
     * @param time     The timestamp of when the ban occurred. Can be {@code null}, indicating no specific timestamp.
     * @throws NullPointerException If any of the {@code player}, {@code operator}, or {@code reason} parameters is {@code null}.
     */
    public BanPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason, @Nullable Date time) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
        this.time = time;
    }

    /**
     * Retrieves the player who has been banned.
     *
     * @return The banned player. Guaranteed to be non-null.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Retrieves the operator who performed the ban.
     *
     * @return The operator's name or identifier. Guaranteed to be non-null.
     */
    public @NotNull String getOperator() {
        return operator;
    }

    /**
     * Retrieves the reason for banning the player.
     *
     * @return The reason for the ban. Guaranteed to be non-null.
     */
    public @NotNull String getReason() {
        return reason;
    }

    /**
     * Retrieves the timestamp of when the ban occurred.
     *
     * @return The timestamp of the ban, or {@code null} if no specific timestamp is provided.
     */
    public @Nullable Date getTime() {
        return time;
    }
}