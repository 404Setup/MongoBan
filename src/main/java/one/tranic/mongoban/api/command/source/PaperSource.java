package one.tranic.mongoban.api.command.source;

import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.player.PaperPlayer;
import one.tranic.t.base.TBase;
import one.tranic.t.base.command.Operator;
import one.tranic.t.base.command.source.CommandSource;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Implementation of the CommandSource interface for the Bukkit/Spigot Paper platform.
 * <p>
 * It defines methods to handle actions such as sending messages or kicking
 * players and retrieves information about the source.
 */
public class PaperSource implements CommandSource<CommandSender, Player> {
    private final CommandSender commandSender;
    private final String[] args;
    private final PaperPlayer player;

    public PaperSource(CommandSender commandSender, String[] args) {
        this.commandSender = commandSender;
        this.args = args;
        this.player = commandSender instanceof Player ? new PaperPlayer(commandSender) : null;
    }

    @Override
    public Operator getOperator() {
        if (player != null)
            return new Operator(player.getUsername(), player.getUniqueId());
        return TBase.console();
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
    public int argSize() {
        return args.length;
    }

    @Override
    public @Nullable Locale getLocale() {
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
        commandSender.sendMessage(message);
    }

    @Override
    public @Nullable PaperPlayer asPlayer() {
        return player;
    }
}
