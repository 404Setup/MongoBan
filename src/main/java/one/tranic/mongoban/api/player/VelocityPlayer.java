package one.tranic.mongoban.api.player;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import one.tranic.mongoban.velocity.MongoBan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

public class VelocityPlayer implements MongoPlayer<Player> {
    private final Player player;

    public VelocityPlayer(CommandSource commandSource) {
        this.player = (Player) commandSource;
    }

    public VelocityPlayer(Player player) {
        this.player = player;
    }

    /**
     * Creates a new instance of {@link VelocityPlayer} from the given {@link Player},
     * or returns null if the input player is null.
     *
     * @param player the Player instance to create the VelocityPlayer from; may be null
     * @return a new VelocityPlayer instance if the player is not null; otherwise null
     */
    public static @Nullable VelocityPlayer createPlayer(@Nullable Player player) {
        if (player == null) return null;
        return new VelocityPlayer(player);
    }

    /**
     * Creates a {@link VelocityPlayer} instance from a specified {@link UUID}.
     * <p>
     * If the player corresponding to the provided UUID cannot be found on the proxy,
     * the method returns {@code null}.
     *
     * @param uuid the unique identifier of the player, must not be {@code null}
     * @return a {@code VelocityPlayer} instance if the player is found, otherwise {@code null}
     */
    public static @Nullable VelocityPlayer createPlayer(@NotNull UUID uuid) {
        Player p = MongoBan.getProxy().getPlayer(uuid).orElse(null);
        return createPlayer(p);
    }

    /**
     * Creates a {@link VelocityPlayer} instance from a given username if the player exists.
     *
     * @param username the username of the player; must not be null
     * @return a {@link VelocityPlayer} instance if the player is found, otherwise null
     */
    public static @Nullable VelocityPlayer createPlayer(@NotNull String username) {
        Player p = MongoBan.getProxy().getPlayer(username).orElse(null);
        return createPlayer(p);
    }

    @Override
    public String getUsername() {
        return player.getUsername();
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public String getConnectHost() {
        return player.getRemoteAddress().getAddress().getHostAddress();
    }

    @Override
    public Locale getLocale() {
        return player.getEffectiveLocale();
    }

    @Override
    public @Nullable MongoLocation getLocation() {
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
        return player.isActive();
    }

    @Override
    public @Nullable String getClientBrand() {
        if (isBedrockPlayer()) return BedrockPlayer.getPlatform(getUniqueId());
        return player.getClientBrand();
    }

    @Override
    public Player getSourcePlayer() {
        return player;
    }

    @Override
    public boolean kick() {
        player.disconnect(Component.text("<kick by server>"));
        return true;
    }

    @Override
    public boolean kick(String reason) {
        player.disconnect(Component.text(reason));
        return true;
    }

    @Override
    public boolean kick(@NotNull Component reason) {
        player.disconnect(reason);
        return true;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(Component.text(message));
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        player.sendMessage(message);
    }
}
