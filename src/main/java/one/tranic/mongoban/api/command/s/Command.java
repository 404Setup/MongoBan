package one.tranic.mongoban.api.command.s;

import one.tranic.mongoban.api.command.CommandImpl;
import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.api.command.wrap.BukkitWrap;
import one.tranic.mongoban.api.command.wrap.VelocityWrap;
import one.tranic.mongoban.common.Platform;
import one.tranic.mongoban.common.command.sources.PaperSource;
import one.tranic.mongoban.common.command.sources.VelocitySource;
import org.jetbrains.annotations.Nullable;

public abstract class Command<C extends SourceImpl<?>> implements CommandImpl<C> {
    private String name;
    private String description;
    private String usage;
    private String permission;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * Unwraps this command into a Bukkit-compatible {@link org.bukkit.command.Command} implementation
     * if the current platform is compatible with Bukkit-based servers.
     * <p>
     * This method checks if the current platform is one of the Bukkit-compatible platforms
     * (e.g., Paper, Folia, ShreddedPaper).
     * <p>
     * If the platform matches, it wraps the command and returns a new {@link BukkitWrap} instance.
     * <p>
     * If the platform is not compatible, this method returns null.
     *
     * @return a {@link org.bukkit.command.Command} instance representing this command
     * for Bukkit-based platforms, or null if the platform is not compatible.
     */
    public @Nullable org.bukkit.command.Command unwrapBukkit() {
        if (Platform.get() == Platform.Paper ||
                Platform.get() == Platform.Folia ||
                Platform.get() == Platform.ShreddedPaper) return new BukkitWrap((Command<PaperSource>) this);
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
