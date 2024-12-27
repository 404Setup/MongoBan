package one.tranic.mongoban.api.command;

import one.tranic.mongoban.api.command.source.BungeeSource;
import one.tranic.mongoban.api.command.source.PaperSource;
import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.api.command.source.VelocitySource;
import one.tranic.mongoban.api.command.wrap.BungeeWrap;
import one.tranic.mongoban.api.command.wrap.PaperWrap;
import one.tranic.mongoban.api.command.wrap.VelocityWrap;
import one.tranic.mongoban.api.Platform;
import org.jetbrains.annotations.Nullable;

public abstract class Command<C extends SourceImpl<?, ?>> implements CommandImpl<C> {
    private String name;
    private String description;
    private String usage;
    private String permission;

    public String getName() {
        return name;
    }

    /**
     * Sets the name of the command, prefixing it with platform-specific identifiers
     * based on the current platform (e.g., "b" for BungeeCord, "v" for Velocity).
     *
     * @param name the base name to set for this command
     */
    public void setName(String name) {
        if (Platform.get() == Platform.BungeeCord) {
            this.name = "b" + name;
        } else if (Platform.get() == Platform.Velocity) {
            this.name = "v" + name;
        } else {
            this.name = name;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getPermission() {
        return permission;
    }

    /**
     * Sets the permission for the command with platform-specific modifications.
     * <p>
     * If the current platform is BungeeCord or Velocity, the permission string
     * is modified by appending a platform-specific suffix; otherwise, the
     * permission is set as is.
     *
     * @param permission the base permission string to set for this command
     */
    public void setPermission(String permission) {
        if (Platform.get() == Platform.BungeeCord) {
            this.permission = permission.replaceFirst("\\.([^.]+)$", ".b$1");
        } else if (Platform.get() == Platform.Velocity) {
            this.permission = permission.replaceFirst("\\.([^.]+)$", ".v$1");
        } else {
            this.permission = permission;
        }
    }

    /**
     * Unwraps this command into a Bukkit-compatible {@link org.bukkit.command.Command} implementation
     * if the current platform is compatible with Bukkit-based servers.
     * <p>
     * This method checks if the current platform is one of the Bukkit-compatible platforms
     * (e.g., Paper, Folia, ShreddedPaper).
     * <p>
     * If the platform matches, it wraps the command and returns a new {@link PaperWrap} instance.
     * <p>
     * If the platform is not compatible, this method returns null.
     *
     * @return a {@link org.bukkit.command.Command} instance representing this command
     * for Bukkit-based platforms, or null if the platform is not compatible.
     */
    public @Nullable org.bukkit.command.Command unwrapBukkit() {
        if (Platform.isBukkit()) return new PaperWrap((Command<PaperSource>) this);
        return null;
    }

    /**
     * Registers this command with a Bukkit-compatible {@link org.bukkit.command.SimpleCommandMap}.
     * <p>
     * This method checks if the current platform is one of the Bukkit-compatible platforms
     * (e.g., Paper, Folia, ShreddedPaper).
     * <p>
     * If the platform matches,
     * the command is registered using the provided {@code simpleCommandMap} and the specified {@code prefix}.
     * <p>
     * If the platform is not compatible, the method does not register the command and returns false.
     *
     * @param simpleCommandMap the {@link org.bukkit.command.SimpleCommandMap} where the command should be registered
     * @param prefix           a string prefix to use for the registration
     * @return {@code true} if the command is successfully registered on a supported platform;
     * <p>
     * {@code false} if the platform is not compatible or registration fails
     */
    public boolean registerWithBukkit(org.bukkit.command.SimpleCommandMap simpleCommandMap, String prefix) {
        if (Platform.isBukkit()) {
            simpleCommandMap.register(getName(), prefix, unwrapBukkit());
            return true;
        }
        return false;
    }

    /**
     * Unwraps this command into a BungeeCord-specific {@link net.md_5.bungee.api.plugin.Command} instance
     * if the current platform is detected as BungeeCord.
     * <p>
     * This method checks the runtime platform using {@link Platform#get()}.
     * If the platform is BungeeCord, it creates a new {@link BungeeWrap} instance for this command.
     * Otherwise, it returns {@code null}.
     *
     * @return a BungeeCord-compatible {@link net.md_5.bungee.api.plugin.Command} instance if the
     * current platform is BungeeCord, or {@code null} if the platform is not BungeeCord.
     * @deprecated BungeeCord is considered outdated. It is recommended to use more modern proxies such as Velocity.
     * <p>
     * Developing plugins on modern platforms like Paper and Velocity is easier
     * and provides better support and features.
     */
    @Deprecated
    public @Nullable net.md_5.bungee.api.plugin.Command unwrapBungee() {
        if (Platform.get() == Platform.BungeeCord) return new BungeeWrap((Command<BungeeSource>) this);
        return null;
    }

    /**
     * Registers this command with the BungeeCord plugin's command manager.
     * <p>
     * This method checks if the current platform is BungeeCord.
     * If true, it registers the command using BungeeCord's {@link net.md_5.bungee.api.plugin.PluginManager}.
     * Otherwise, no action is taken, and the method returns {@code false}.
     *
     * @param bungeePlugin an instance of the BungeeCord plugin used to register the command
     * @return {@code true} if the command is registered successfully on BungeeCord;
     * <p>
     * {@code false} if the platform is not BungeeCord or registration fails
     * @deprecated BungeeCord is considered outdated, and using newer platforms is recommended.
     */
    @Deprecated
    public boolean registerWithBungee(net.md_5.bungee.api.plugin.Plugin bungeePlugin) {
        if (Platform.get() == Platform.BungeeCord) {
            bungeePlugin.getProxy().getPluginManager().registerCommand(bungeePlugin, unwrapBungee());
            return true;
        }
        return false;
    }

    /**
     * Attempts to unwrap the current command into a Velocity-specific command.
     * <p>
     * This method is intended for use on the Velocity platform, where it converts
     * the generic command instance into a Velocity-compatible command object.
     *
     * @return a Velocity-specific {@link com.velocitypowered.api.command.Command} instance
     * if the current platform is Velocity; otherwise, returns {@code null}.
     */
    public @Nullable com.velocitypowered.api.command.Command unwrapVelocity() {
        if (Platform.get() == Platform.Velocity)
            return new VelocityWrap((Command<VelocitySource>) this);
        return null;
    }

    /**
     * Registers this command with the Velocity platform.
     * <p>
     * This method ensures the current platform is Velocity before proceeding
     * with command registration using the provided Velocity plugin and ProxyServer.
     *
     * @param velocityPlugin the plugin instance used for command registration
     * @param proxy          the {@link com.velocitypowered.api.proxy.ProxyServer} instance representing the Velocity server
     * @return {@code true} if the command is successfully registered on the Velocity platform;
     * <p>
     * {@code false} if the current platform is not Velocity or registration fails
     */
    public boolean registerWithVelocity(Object velocityPlugin, com.velocitypowered.api.proxy.ProxyServer proxy) {
        if (Platform.get() != Platform.Velocity) return false;

        com.velocitypowered.api.command.CommandManager commandManager = proxy.getCommandManager();
        com.velocitypowered.api.command.CommandMeta commandMeta = commandManager.metaBuilder(getName())
                .plugin(velocityPlugin)
                .build();

        commandManager.register(commandMeta, unwrapVelocity());
        return true;
    }
}
