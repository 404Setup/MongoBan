package one.tranic.mongoban.api.command.source;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.player.VelocityPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Implementation of the SourceImpl interface for the VelocityProxy platform.
 * <p>
 * This class serves as a representation of a command source in VelocityProxy,
 * which may be a player or the console.
 */
public class VelocitySource implements SourceImpl<CommandSource, Player> {
    private final SimpleCommand.Invocation invocation;
    private final CommandSource commandSource;
    private final boolean isPlayer;

    public VelocitySource(SimpleCommand.Invocation invocation) {
        this.invocation = invocation;
        this.commandSource = invocation.source();
        this.isPlayer = (commandSource instanceof Player);
    }

    @Override
    public CommandSource getSource() {
        return commandSource;
    }

    @Override
    public boolean isPlayer() {
        return isPlayer;
    }

    @Override
    public String[] getArgs() {
        return invocation.arguments();
    }

    @Override
    public @Nullable Locale locale() {
        return isPlayer ? ((Player) commandSource).getEffectiveLocale()
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
        if (!isPlayer) return null;
        return new VelocityPlayer(commandSource);
    }
}
