package one.tranic.mongoban.api.event.bungee;

import net.md_5.bungee.api.plugin.Event;
import one.tranic.mongoban.api.data.Operator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents an event that is triggered when a player is banned.
 * This event contains details about the affected player, the operator who issued the ban,
 * the reason for the ban, and the optional duration of the ban.
 */
public class BanPlayerEvent extends Event {
    private final UUID player;
    private final Operator operator;
    private final String reason;
    private final String duration;

    /**
     * Constructs a {@code BanPlayerEvent} with the specified player, operator, reason, and optional duration.
     *
     * @param player   The unique identifier (UUID) of the player being banned. Must not be {@code null}.
     * @param operator The operator issuing the ban. Must not be {@code null}.
     * @param reason   The reason for the ban. Must not be {@code null}.
     * @param duration The optional duration of the ban. Can be {@code null} for permanent bans.
     * @throws NullPointerException if {@code player}, {@code operator}, or {@code reason} is {@code null}.
     */
    public BanPlayerEvent(@NotNull UUID player, @NotNull Operator operator, @NotNull String reason, @Nullable String duration) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
        this.duration = duration;
    }

    /**
     * Retrieves the unique identifier (UUID) of the player associated with this event.
     *
     * @return The {@link UUID} of the player. Never {@code null}.
     */
    public @NotNull UUID getPlayer() {
        return player;
    }

    /**
     * Retrieves the operator responsible for managing the event.
     *
     * @return The {@link Operator} object representing the operator who performed the action. Never {@code null}.
     */
    public @NotNull Operator getOperator() {
        return operator;
    }

    /**
     * Retrieves the reason associated with this event.
     *
     * @return A {@link String} representing the reason. Never {@code null}.
     */
    public @NotNull String getReason() {
        return reason;
    }

    /**
     * Retrieves the duration associated with the event.
     *
     * @return A {@link String} representing the duration, or {@code null} if no duration is specified.
     */
    public @Nullable String getDuration() {
        return duration;
    }
}
