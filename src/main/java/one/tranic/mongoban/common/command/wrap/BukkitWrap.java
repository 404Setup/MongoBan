package one.tranic.mongoban.common.command.wrap;

import one.tranic.mongoban.common.source.s.PaperSource;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BukkitWrap extends Command {
    private final one.tranic.mongoban.common.command.s.Command<PaperSource> command;

    public BukkitWrap(one.tranic.mongoban.common.command.s.Command<PaperSource> command) {
        super(command.getName());
        if (command.getPermission() != null) setPermission(command.getPermission());
        if (command.getDescription() != null) setDescription(command.getDescription());
        if (command.getUsage() != null) setUsage(command.getUsage());

        this.command = command;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        command.execute(new PaperSource(sender, args));
        return true;
    }

    @Override
    public @NotNull java.util.List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return command.suggest(new PaperSource(sender, args));
    }
}
