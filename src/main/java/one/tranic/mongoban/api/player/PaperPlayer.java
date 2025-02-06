package one.tranic.mongoban.api.player;

import net.kyori.adventure.text.Component;
import one.tranic.irs.PluginSchedulerBuilder;
import one.tranic.mongoban.api.Platform;
import one.tranic.t.base.player.BedrockPlayer;
import one.tranic.t.base.player.Location;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.UUID;

public class PaperPlayer implements one.tranic.t.base.player.Player<Player> {
    private final Player player;

    public PaperPlayer(Player player) {
        this.player = player;
    }

    public PaperPlayer(CommandSender commandSender) {
        this.player = (Player) commandSender;
    }

    /**
     * Creates an instance of {@link PaperPlayer} from the given {@link Player} instance.
     *
     * @param player The player instance to base the {@link PaperPlayer} on. Can be {@code null}.
     * @return A new {@link PaperPlayer} instance if the given player is not {@code null},
     * otherwise {@code null}.
     */
    public static @Nullable PaperPlayer createPlayer(@Nullable Player player) {
        if (player == null) return null;
        return new PaperPlayer(player);
    }

    /**
     * Creates a {@link PaperPlayer} instance from a {@link UUID}.
     *
     * @param uuid the unique identifier of the player; must not be null
     * @return a {@link PaperPlayer} instance if a corresponding player is found, or null if no player is found
     */
    public static @Nullable PaperPlayer createPlayer(@NotNull UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        return createPlayer(p);
    }

    /**
     * Creates a PaperPlayer instance for the specified username if the player is found.
     *
     * @param username The username of the player to create a PaperPlayer instance for.
     *                 Must not be null.
     * @return A PaperPlayer instance corresponding to the player with the provided username,
     * or null if no player with the given username is found.
     */
    public static @Nullable PaperPlayer createPlayer(@NotNull String username) {
        Player p = Bukkit.getPlayer(username);
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
        @Nullable InetSocketAddress addr = player.getAddress();
        if (addr == null) return null;
        return addr.getAddress().getHostAddress();
    }

    @Override
    public Locale getLocale() {
        return player.locale();
    }

    @Override
    public @Nullable Location getLocation() {
        @NotNull var l = player.getLocation();
        return new Location(l.getWorld().getName(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
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
        return player.isOnline();
    }

    @Override
    public @Nullable String getClientBrand() {
        if (isBedrockPlayer()) return BedrockPlayer.getPlatform(getUniqueId());
        return player.getClientBrandName();
    }

    @Override
    public Player getSourcePlayer() {
        return player;
    }

    @Override
    public boolean kick() {
        PluginSchedulerBuilder.builder(one.tranic.mongoban.bukkit.MongoBan.getInstance())
                .sync(player)
                .task(() -> player.kick()).run();
        return true;
    }

    @Override
    public boolean kick(String reason) {
        return kick(Component.text(reason));
    }

    @Override
    public boolean kick(@NotNull Component reason) {
        if (Platform.get() == Platform.Folia || Platform.get() == Platform.ShreddedPaper) {
            PluginSchedulerBuilder.builder(one.tranic.mongoban.bukkit.MongoBan.getInstance())
                    .sync(player)
                    .task(() -> player.kick(reason)).run();
        } else player.kick(reason);
        return true;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        player.sendMessage(message);
    }
}
