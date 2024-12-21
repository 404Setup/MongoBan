package one.tranic.mongoban.api.command.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import one.tranic.mongoban.api.data.PlayerInfo;
import org.geysermc.cumulus.form.Form;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

@Deprecated
public class BungeePlayer implements MongoPlayer<ProxiedPlayer> {
    private final ProxiedPlayer player;

    public BungeePlayer(ProxiedPlayer player) {
        this.player = player;
    }

    public BungeePlayer(CommandSender commandSender) {
        this.player = (ProxiedPlayer) commandSender;
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
    public PlayerInfo getPlayerInfo() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return player.getLocale();
    }

    @Override
    public @Nullable MongoLocation getLocation() {
        return null;
    }

    @Override
    public long getPing() {
        return player.getPing();
    }

    @Override
    public boolean isOnline() {
        return player.isConnected();
    }

    @Override
    public @Nullable String getClientBrand() {
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
        player.disconnect(reason);
        return true;
    }

    @Override
    public boolean kick(@NotNull Component reason) {
        player.disconnect(LegacyComponentSerializer.legacySection().serialize(reason));
        return true;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        player.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
    }
}
