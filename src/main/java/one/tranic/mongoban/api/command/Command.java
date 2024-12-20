package one.tranic.mongoban.api.command;

import one.tranic.mongoban.api.command.source.BungeeSource;
import one.tranic.mongoban.api.command.source.PaperSource;
import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.api.command.source.VelocitySource;
import one.tranic.mongoban.api.command.wrap.BungeeWrap;
import one.tranic.mongoban.api.command.wrap.PaperWrap;
import one.tranic.mongoban.api.command.wrap.VelocityWrap;
import one.tranic.mongoban.common.Platform;
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
        if (Platform.get() == Platform.Paper ||
                Platform.get() == Platform.Folia ||
                Platform.get() == Platform.ShreddedPaper) return new PaperWrap((Command<PaperSource>) this);
        return null;
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
}
