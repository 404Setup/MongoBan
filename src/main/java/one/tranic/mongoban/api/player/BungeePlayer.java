package one.tranic.mongoban.api.player;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import one.tranic.mongoban.api.message.Message;
import one.tranic.t.base.player.BedrockPlayer;
import one.tranic.t.base.player.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

@Deprecated
public class BungeePlayer implements one.tranic.t.base.player.Player<ProxiedPlayer> {
    private final ProxiedPlayer player;

    public BungeePlayer(ProxiedPlayer player) {
        this.player = player;
    }

    public BungeePlayer(CommandSender commandSender) {
        this.player = (ProxiedPlayer) commandSender;
    }

    /**
     * Creates a new instance of {@link BungeePlayer} based on the given {@link ProxiedPlayer}.
     * <p>
     * If the provided player is null, this method returns null.
     *
     * @param player the {@link ProxiedPlayer} instance to wrap, may be null
     * @return a new {@link BungeePlayer} instance wrapping the given player, or null if the input is null
     */
    public static @Nullable BungeePlayer createPlayer(@Nullable ProxiedPlayer player) {
        if (player == null) return null;
        return new BungeePlayer(player);
    }

    /**
     * Creates a new {@link BungeePlayer} instance for the specified UUID.
     *
     * @param uuid the UUID of the player to create a {@link BungeePlayer} for; must not be null
     * @return a {@link BungeePlayer} instance representing the player with the given UUID,
     * or null if no player is found
     */
    public static @Nullable BungeePlayer createPlayer(@NotNull UUID uuid) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
        return createPlayer(p);
    }

    /**
     * Creates a {@link BungeePlayer} instance based on the provided username.
     * <p>
     * If no player with the given username is found, this method will return null.
     *
     * @param username the username of the player to create a {@link BungeePlayer} instance for; must not be null
     * @return a {@link BungeePlayer} instance if the username maps to an online player, or null if no such player is found
     */
    public static @Nullable BungeePlayer createPlayer(@NotNull String username) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(username);
        return createPlayer(p);
    }

    @Override
    public String getUsername() {
        return player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public String getConnectHost() {
        return player.getAddress().getAddress().getHostAddress();
    }

    @Override
    public Locale getLocale() {
        return player.getLocale();
    }

    @Override
    public @Nullable Location getLocation() {
        return null;
    }

    @Override
    public long getPing() {
        if (isBedrockPlayer()) {
            long ping = BedrockPlayer.getPing(getUniqueId());
            if (ping != -1) return ping;
        }
        return player.getPing();
    }

    @Override
    public boolean isOnline() {
        return player.isConnected();
    }

    @Override
    public @Nullable String getClientBrand() {
        if (isBedrockPlayer()) return BedrockPlayer.getPlatform(getUniqueId());
        return null;
    }

    @Override
    public ProxiedPlayer getSourcePlayer() {
        return player;
    }

    @Override
    public boolean kick() {
        player.disconnect();
        return true;
    }

    @Override
    public boolean kick(String reason) {
        player.disconnect(Message.toBaseComponent(reason));
        return true;
    }

    @Override
    public boolean kick(@NotNull Component reason) {
        player.disconnect(Message.toBaseComponent(reason));
        return true;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(Message.toBaseComponent(message));
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        player.sendMessage(Message.toBaseComponent(message));
    }
}
