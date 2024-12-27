package one.tranic.mongoban.api.player;

import one.tranic.mongoban.api.Platform;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Player {
    public static @Nullable MongoPlayer<?> getPlayer(String name) {
        if (Platform.get() == Platform.Velocity) {
            return VelocityPlayer.createPlayer(name);
        }
        if (Platform.get() == Platform.BungeeCord) {
            return BungeePlayer.createPlayer(name);
        }
        return PaperPlayer.createPlayer(name);
    }

    public static @Nullable MongoPlayer<?> getPlayer(UUID uuid) {
        if (Platform.get() == Platform.Velocity) {
            return VelocityPlayer.createPlayer(uuid);
        }
        if (Platform.get() == Platform.BungeeCord) {
            return BungeePlayer.createPlayer(uuid);
        }
        return PaperPlayer.createPlayer(uuid);
    }
}
