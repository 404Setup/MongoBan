package one.tranic.mongoban.api.player;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.data.PlayerInfo;
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

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public String getConnectHost() {
        return player.getRemoteAddress().getAddress().getHostAddress();
    }

    @Override
    public PlayerInfo getPlayerInfo() {
        return MongoBan.getDatabase().getPlayerApplication().getPlayerSync(getUniqueId());
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
        return player.getPing();
    }

    @Override
    public boolean isOnline() {
        return player.isActive();
    }

    @Override
    public @Nullable String getClientBrand() {
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
