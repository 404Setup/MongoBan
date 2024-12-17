package one.tranic.mongoban.api.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UnWarnPlayerEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Player player;
    private final String operator;
    private final String reason;

    private boolean isCancelled;

    public UnWarnPlayerEvent(@NotNull Player player, @NotNull String operator, @NotNull String reason) {
        this.player = player;
        this.operator = operator;
        this.reason = reason;

        this.isCancelled = false;
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

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
