package one.tranic.mongoban.api.command.wrap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;
import one.tranic.mongoban.api.command.source.BungeeSource;
import one.tranic.t.base.command.simple.SimpleCommand;

/**
 * The BungeeWrap class acts as an adapter to integrate a generic Command tailored for BungeeSource
 * into the BungeeCord command system. This allows the use of a platform-agnostic command structure
 * within the BungeeCord environment.
 * <p>
 * The class extends the BungeeCord-specific {@link net.md_5.bungee.api.plugin.Command} class
 * and implements the {@link TabExecutor} interface, enabling both command execution and tab
 * completion functionalities.
 *
 * @deprecated BungeeCord is considered outdated. It is recommended to use more modern proxies such as Velocity.
 * <p>
 * Developing plugins on modern platforms like Paper and Velocity is easier and provides better support and features.
 */
@Deprecated
public class BungeeWrap extends net.md_5.bungee.api.plugin.Command implements TabExecutor {
    private final SimpleCommand<BungeeSource> command;

    /**
     * @deprecated BungeeCord is considered outdated. It is recommended to use more modern proxies such as Velocity.
     * <p>
     * Developing plugins on modern platforms like Paper and Velocity is easier and provides better support and features.
     */
    @Deprecated
    public BungeeWrap(SimpleCommand<BungeeSource> command) {
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
