package one.tranic.mongoban.common.source.s;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import one.tranic.mongoban.common.source.SourceImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

/**
 * Implementation of the SourceImpl interface for the VelocityProxy platform.
 * <p>
 * This class serves as a representation of a command source in VelocityProxy,
 * which may be a player or the console.
 */
public class VelocitySource implements SourceImpl<CommandSource> {
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
    public boolean kick() {
        if (!isPlayer) return false;
        ((Player) commandSource).disconnect(Component.text("<kick by server>"));
        return true;
    }

    @Override
    public boolean kick(String reason) {
        if (!isPlayer) return false;
        ((Player) commandSource).disconnect(Component.text(reason));
        return true;
    }

    @Override
    public boolean kick(@NotNull Component reason) {
        if (!isPlayer) return false;
        ((Player) commandSource).disconnect(reason);
        return true;
    }

    @Override
    public String getName() {
        return isPlayer ? ((Player) commandSource).getUsername() : "Console";
    }

    @Override
    public @Nullable UUID getUniqueId() {
        return isPlayer ? ((Player) commandSource).getUniqueId()
                : null;
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
    public void sendMessage(String message) {
        commandSource.sendMessage(Component.text(message));
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        commandSource.sendMessage(message);
    }
}
