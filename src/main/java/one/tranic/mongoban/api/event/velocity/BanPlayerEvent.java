package one.tranic.mongoban.api.event.velocity;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class BanPlayerEvent {
    private final Player player;
    private final String operator;
    private final String reason;
    private final Date time;

    public BanPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason) {
        this(player, operator, reason, null);
    }

    public BanPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason, @Nullable Date time) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
        this.time = time;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull String getOperator() {
        return operator;
    }

    public @NotNull String getReason() {
        return reason;
    }

    public @Nullable Date getTime() {
        return time;
    }
}
