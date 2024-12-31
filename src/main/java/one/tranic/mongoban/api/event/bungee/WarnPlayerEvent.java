package one.tranic.mongoban.api.event.bungee;

import net.md_5.bungee.api.plugin.Event;
import one.tranic.mongoban.api.data.Operator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents an event that is triggered when a player is issued a warning.
 * This event contains information about the affected player, the operator who issued the warning,
 * the reason for the warning, and an optional timestamp indicating when the warning was issued.
 */
public class WarnPlayerEvent extends Event {
    private final UUID player;
    private final Operator operator;
    private final String reason;
    private final String duration;

    /**
     * Constructs a {@code WarnPlayerEvent} with the specified player, operator, reason, and optional duration.
     * This event is triggered when a player receives a warning and provides details about
     * the involved player, the operator issuing the warning, the reason for the warning,
     * and an optional duration for how long the warning should last.
     *
     * @param player   The unique identifier (UUID) of the player being warned. Must not be {@code null}.
     * @param operator The operator issuing the warning. Must not be {@code null}.
     * @param reason   The reason for the warning. Must not be {@code null}.
     * @param duration The optional duration of the warning. Can be {@code null} for warnings without a specified duration.
     */
    public WarnPlayerEvent(@NotNull UUID player, @NotNull Operator operator, @NotNull String reason, @Nullable String duration) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
        this.duration = duration;
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
     * Retrieves the operator responsible for the associated action in this event.
     *
     * @return The {@link Operator} object representing the operator who performed the action. Never {@code null}.
     */
    public @NotNull Operator getOperator() {
        return operator;
    }

    /**
     * Retrieves the reason associated with this event.
     *
     * @return A non-null string representing the reason for the event.
     */
    public @NotNull String getReason() {
        return reason;
    }

    /**
     * Retrieves the duration associated with this event.
     * The duration represents the length of time the event action (e.g., a warning or ban) is applicable.
     * It may be null, indicating that the action is permanent or unspecified.
     *
     * @return A {@link String} representing the duration, or {@code null} if no duration is specified.
     */
    public @Nullable String getDuration() {
        return duration;
    }
}
