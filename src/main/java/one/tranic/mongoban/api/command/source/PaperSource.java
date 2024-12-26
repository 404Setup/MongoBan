package one.tranic.mongoban.api.command.source;

import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.data.Operator;
import one.tranic.mongoban.api.player.PaperPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Implementation of the SourceImpl interface for the Bukkit/Spigot Paper platform.
 * <p>
 * This class provides a representation of a command source within the Paper
 * environment, which can either be a Player or a generic CommandSender.
 * <p>
 * It defines methods to handle actions such as sending messages or kicking
 * players and retrieves information about the source.
 */
public class PaperSource implements SourceImpl<CommandSender, Player> {
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
        commandSender.sendMessage(message);
    }

    @Override
    public @Nullable PaperPlayer asPlayer() {
        return player;
    }
}
