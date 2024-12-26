package one.tranic.mongoban.api.player;

import net.kyori.adventure.text.Component;
import one.tranic.irs.PluginSchedulerBuilder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.UUID;

public class PaperPlayer implements MongoPlayer<Player> {
    private final Player player;

    public PaperPlayer(Player player) {
        this.player = player;
    }

    public PaperPlayer(CommandSender commandSender) {
        this.player = (Player) commandSender;
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
    public @Nullable MongoLocation getLocation() {
        @NotNull Location l = player.getLocation();
        return new MongoLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
    }

    @Override
    public long getPing() {
        return player.getPing();
    }

    @Override
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public @Nullable String getClientBrand() {
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
        PluginSchedulerBuilder.builder(one.tranic.mongoban.bukkit.MongoBan.getInstance())
                .sync(player)
                .task(() -> player.kick(Component.text(reason))).run();
        return true;
    }

    @Override
    public boolean kick(@NotNull Component reason) {
        PluginSchedulerBuilder.builder(one.tranic.mongoban.bukkit.MongoBan.getInstance())
                .sync(player)
                .task(() -> player.kick(reason)).run();
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
