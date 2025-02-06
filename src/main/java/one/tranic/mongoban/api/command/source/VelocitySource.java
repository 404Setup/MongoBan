package one.tranic.mongoban.api.command.source;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.player.VelocityPlayer;
import one.tranic.t.base.TBase;
import one.tranic.t.base.command.Operator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Implementation of the CommandSource interface for the VelocityProxy platform.
 */
public class VelocitySource implements one.tranic.t.base.command.source.CommandSource<CommandSource, Player> {
    private final SimpleCommand.Invocation invocation;
    private final CommandSource commandSource;
    private final VelocityPlayer player;

    public VelocitySource(CommandSource commandSource) {
        this.invocation = null;
        this.commandSource = commandSource;
        this.player = commandSource instanceof Player ? new VelocityPlayer(commandSource) : null;
    }

    public VelocitySource(SimpleCommand.Invocation invocation) {
        this.invocation = invocation;
        this.commandSource = invocation.source();
        this.player = commandSource instanceof Player ? new VelocityPlayer(commandSource) : null;
    }

    @Override
    public Operator getOperator() {
        if (player != null)
            return new Operator(player.getUsername(), player.getUniqueId());
        return TBase.console();
    }

    @Override
    public CommandSource getSource() {
        return commandSource;
    }

    @Override
    public boolean isPlayer() {
        return player != null;
    }

    @Override
    public String[] getArgs() {
        return invocation.arguments();
    }

    @Override
    public int argSize() {
        return invocation.arguments().length;
    }

    @Override
    public @Nullable Locale locale() {
        return player != null ? player.getLocale()
                : Locale.getDefault();
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSource.hasPermission(permission);
    }

    @Override
    public void sendMessage(String message) {
        commandSource.sendMessage(Component.text(message));
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        commandSource.sendMessage(message);
    }

    @Override
    public @Nullable VelocityPlayer asPlayer() {
        return player;
    }
}
