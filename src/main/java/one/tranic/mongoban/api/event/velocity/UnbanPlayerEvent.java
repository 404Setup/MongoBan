package one.tranic.mongoban.api.event.velocity;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This event is triggered when a player is unbanned on a Velocity server.
 * <p>
 * It contains details about the unbanned player, the operator who performed the unban,
 * and the reason for the unban.
 * </p>
 */
public record UnbanPlayerEvent(Player player, String operator, String reason) {

    /**
     * Constructs an {@code UnbanPlayerEvent} with the specified player, operator, and reason.
     *
     * @param player   the {@link Player} who was unbanned, must not be null
     * @param operator the name of the operator who performed the unban, must not be null
     * @param reason   the reason for the unban, must not be null
     */
    public UnbanPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
    }

    /**
     * Returns the unbanned player.
     *
     * @return the {@link Player} involved in this event, never null
     */
    @Override
    public @NotNull Player player() {
        return player;
    }

    /**
     * Returns the operator's name.
     *
     * @return a {@link String} representing the name of the operator who performed the unban, never null
     */
    @Override
    public @NotNull String operator() {
        return operator;
    }

    /**
     * Returns the reason for the unban.
     *
     * @return a {@link String} describing the reason for the unban, never null
     */
    @Override
    public @NotNull String reason() {
        return reason;
    }
}