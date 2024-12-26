package one.tranic.mongoban.api.command.source;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.data.Operator;
import one.tranic.mongoban.api.player.BungeePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * This class represents a source implementation for BungeeCord command senders.
 *
 * @deprecated BungeeCord is considered outdated. It is recommended to use more modern proxies such as Velocity.
 * <p>
 * Developing plugins on modern platforms like Paper and Velocity is easier and provides better support and features.
 */
@Deprecated
public class BungeeSource implements SourceImpl<CommandSender, ProxiedPlayer> {
    private final CommandSender commandSender;
    private final String[] args;
    private final BungeePlayer player;

    /**
     * Constructs a new BungeeSource instance.
     *
     * @param commandSender the CommandSender instance
     * @param args          the arguments passed with the command
     * @deprecated BungeeCord support is deprecated; consider migrating to modern platforms like Velocity.
     */
    @Deprecated
    public BungeeSource(CommandSender commandSender, String[] args) {
        this.commandSender = commandSender;
        this.args = args;
        this.player = commandSender instanceof ProxiedPlayer ? new BungeePlayer(commandSender) : null;
    }

    @Override
    public Operator getOperator() {
        if (player != null)
            return new Operator(player.getUsername(), player.getUniqueId());
        return MongoBanAPI.console;
    }

    @Override
    public CommandSender getSource() {
        return commandSender;
    }

    @Override
    public boolean isPlayer() {
        return player != null;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public @Nullable Locale locale() {
        return player != null ? player.getLocale() : Locale.getDefault();
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }

    @Override
    public void sendMessage(String message) {
        commandSender.sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        commandSender.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
    }

    @Override
    public BungeePlayer asPlayer() {
        return player;
    }
}
