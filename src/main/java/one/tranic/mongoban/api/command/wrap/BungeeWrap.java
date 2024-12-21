package one.tranic.mongoban.api.command.wrap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;
import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.command.source.BungeeSource;

/**
 * The BungeeWrap class acts as an adapter to integrate a generic Command tailored for BungeeSource
 * into the BungeeCord command system. This allows the use of a platform-agnostic command structure
 * within the BungeeCord environment.
 * <p>
 * The class extends the BungeeCord-specific {@link net.md_5.bungee.api.plugin.Command} class
 * and implements the {@link TabExecutor} interface, enabling both command execution and tab
 * completion functionalities.
 * <p>
 * Responsibilities of this class include:
 * <p>
 * - Adapting a generic Command designed for BungeeSource to the BungeeCord API.
 * <p>
 * - Handling command execution requests through the {@link #execute(CommandSender, String[])} method.
 * <p>
 * - Generating tab completion suggestions with the {@link #onTabComplete(CommandSender, String[])} method.
 * <p>
 * This class should be used in a BungeeCord environment. For other platforms such as Velocity or Bukkit,
 * alternative wrappers like VelocityWrap or BukkitWrap should be used respectively.
 *
 * @deprecated BungeeCord is considered outdated. It is recommended to use more modern proxies such as Velocity.
 * <p>
 * Developing plugins on modern platforms like Paper and Velocity is easier and provides better support and features.
 */
public class BungeeWrap extends net.md_5.bungee.api.plugin.Command implements TabExecutor {
    private final Command<BungeeSource> command;

    public BungeeWrap(Command<BungeeSource> command) {
        super(command.getName(), command.getPermission());
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.command.execute(new BungeeSource(sender, args));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return this.command.suggest(new BungeeSource(sender, args));
    }
}
