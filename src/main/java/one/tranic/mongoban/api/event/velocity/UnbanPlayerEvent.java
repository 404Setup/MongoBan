package one.tranic.mongoban.api.event.velocity;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

public class UnbanPlayerEvent {
    private final Player player;
    private final String operator;
    private final String reason;

    public UnbanPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;
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
}
