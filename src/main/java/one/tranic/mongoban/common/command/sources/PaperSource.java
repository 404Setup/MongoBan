package one.tranic.mongoban.common.command.sources;

import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.command.source.SourceImpl;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

/**
 * Implementation of the SourceImpl interface for the Bukkit/Spigot Paper platform.
 * <p>
 * This class provides a representation of a command source within the Paper
 * environment, which can either be a Player or a generic CommandSender.
 * <p>
 * It defines methods to handle actions such as sending messages or kicking
 * players and retrieves information about the source.
 */
public class PaperSource implements SourceImpl<CommandSender> {
    private final CommandSender commandSender;
    private final String[] args;
    private final boolean isPlayer;

    public PaperSource(CommandSender commandSender, String[] args) {
        this.commandSender = commandSender;
        this.args = args;
        this.isPlayer = commandSender instanceof Player;
    }

    @Override
    public CommandSender getSource() {
        return commandSender;
    }

    @Override
    public boolean isPlayer() {
        return isPlayer;
    }

    @Override
    public boolean kick() {
        if (!isPlayer) return false;
        ((Player) commandSender).kick();
        return true;
    }

    @Override
    public boolean kick(String reason) {
        if (!isPlayer) return false;
        ((Player) commandSender).kick(Component.text(reason));
        return true;
    }

    @Override
    public boolean kick(@NotNull Component reason) {
        if (!isPlayer) return false;
        ((Player) commandSender).kick(reason);
        return true;
    }

    @Override
    public String getName() {
        return commandSender.getName();
    }

    @Override
    public @Nullable UUID getUniqueId() {
        return isPlayer ? ((Player) commandSender).getUniqueId()
                : null;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public @Nullable Locale locale() {
        return isPlayer ? ((Player) commandSender).locale() : Locale.getDefault();
    }

    @Override
    public void sendMessage(String message) {
        commandSender.sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        commandSender.sendMessage(message);
    }
}
