package one.tranic.mongoban.api.player;

import one.tranic.t.paper.player.PaperPlayer;
import one.tranic.t.utils.Platform;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Player {
    public static @Nullable one.tranic.t.base.player.Player<?> getPlayer(String name) {
        if (Platform.get() == Platform.Velocity) {
            return VelocityPlayer.createPlayer(name);
        }
        if (Platform.get() == Platform.BungeeCord) {
            return BungeePlayer.createPlayer(name);
        }
        return PaperPlayer.createPlayer(name);
    }

    public static @Nullable one.tranic.t.base.player.Player<?> getPlayer(UUID uuid) {
        if (Platform.get() == Platform.Velocity) {
            return VelocityPlayer.createPlayer(uuid);
        }
        if (Platform.get() == Platform.BungeeCord) {
            return BungeePlayer.createPlayer(uuid);
        }
        return PaperPlayer.createPlayer(uuid);
    }
}
