package one.tranic.mongoban.api.command;

import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.Platform;
import one.tranic.mongoban.api.command.source.BungeeSource;
import one.tranic.mongoban.api.command.source.PaperSource;
import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.api.command.source.VelocitySource;
import one.tranic.mongoban.api.command.wrap.BungeeWrap;
import one.tranic.mongoban.api.command.wrap.PaperWrap;
import one.tranic.mongoban.api.command.wrap.VelocityWrap;
import one.tranic.mongoban.api.parse.player.PlayerParser;
import one.tranic.mongoban.common.form.GeyserForm;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Abstract base class representing a command in a multi-platform environment.
 * <p>
 * This class provides methods to manage command properties such as name, description, usage,
 * and permissions, as well as utilities to handle platform-specific command registration and unwrapping.
 *
 * @param <C> the type of the command source, extending from {@link SourceImpl}
 */
public abstract class Command<C extends SourceImpl<?, ?>> implements CommandImpl<C> {
    private String name;
    private String description;
    private String usage;
    private String permission;

    @Override
    public boolean hasPermission(C source) {
        return source.hasPermission(getPermission());
    }

    @Override
    public List<String> suggest(C source) {
        if (!hasPermission(source)) return MongoBanAPI.EMPTY_LIST;

        String[] args = source.getArgs();
        int size = source.argSize();
        if (size == 1) {
            return MongoBanAPI.FLAG_LIST.stream()
                    .filter(flag -> flag.startsWith(args[0]))
                    .toList();
        }
        if (size > 1) {
            String previousArg = args[size - 2];
            String currentArg = args[size - 1];
            if ("--target".equals(previousArg)) {
                return PlayerParser.parse(30).stream()
                        .filter(player -> player.startsWith(currentArg))
                        .toList();
            }
            if ("--duration".equals(previousArg)) {
                return MongoBanAPI.TIME_SUGGEST.stream()
                        .filter(time -> time.startsWith(currentArg))
                        .toList();
            }
            if ("--reason".equals(previousArg)) {
                return MongoBanAPI.REASON_SUGGEST.stream()
                        .filter(reason -> reason.startsWith(currentArg))
                        .toList();
            }
            if (MongoBanAPI.FLAG_LIST.contains(previousArg)) {
                return MongoBanAPI.FLAG_LIST.stream()
                        .filter(flag -> flag.startsWith(currentArg))
                        .toList();
            }
        }
        return MongoBanAPI.EMPTY_LIST;
    }

    /**
     * Sends a result message to a specified source.
     *
     * @param source the command source who will receive the message
     * @param msg    the message to be sent to the source
     */
    public void sendResult(C source, Component msg) {
        sendResult(source, msg, true);
    }

    /**
     * Sends a message result to a given source, taking into account whether the source is
     * a Bedrock player, a standard player, or whether the message should also be sent to the console.
     *
     * @param source      the source to which the result should be sent; can be a player or other entity
     * @param msg         the message to be sent, represented as a {@link Component}
     * @param withConsole if true, the message will also be sent to the console
     */
    public void sendResult(C source, Component msg, boolean withConsole) {
        if (source.isBedrockPlayer()) source.asPlayer().sendFormAsync(GeyserForm.getMessageForm(msg));
        else if (source.isPlayer()) source.sendMessage(msg);
        if (withConsole) MongoBanAPI.CONSOLE_SOURCE.sendMessage(msg);
    }

    /**
     * Sends a message result to a specified source.
     *
     * @param source the source (e.g., player or console) to which the result will be sent
     * @param msg    the message to be sent to the source
     */
    public void sendResult(C source, String msg) {
        sendResult(source, msg, true);
    }

    /**
     * Sends a result message to the specified source and optionally to the console.
     *
     * @param source      the source to which the result is sent; it can represent a player or another entity
     * @param msg         the message to be sent
     * @param withConsole whether the message should also be sent to the console
     */
    public void sendResult(C source, String msg, boolean withConsole) {
        if (source.isBedrockPlayer()) source.asPlayer().sendFormAsync(GeyserForm.getMessageForm(msg));
        else if (source.isPlayer()) source.sendMessage(msg);
        if (withConsole) MongoBanAPI.CONSOLE_SOURCE.sendMessage(msg);
    }

    /**
     * Retrieves the name of the command.
     *
     * @return the name of the command
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the command, prefixing it with platform-specific identifiers.
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

    /**
     * Retrieves the description of the command.
     *
     * @return the description text for this command
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for this command.
     *
     * @param description the description to set for this command
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the usage string for the command.
     *
     * @return the usage string for the command
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Sets the usage description for this command.
     *
     * @param usage the usage description to set for this command
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Retrieves the permission required to execute this command.
     *
     * @return the permission string associated with this command
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Sets the permission for the command with platform-specific modifications.
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
