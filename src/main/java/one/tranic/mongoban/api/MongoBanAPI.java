package one.tranic.mongoban.api;

import one.tranic.mongoban.api.data.Operator;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;
import java.util.concurrent.*;

public class MongoBanAPI {
    public final static Operator console = new Operator("Console", UUID.fromString("05b11eee-24db-4a21-ba9d-e12e8df9a92f"));
    public final static Executor executor = new ThreadPoolExecutor(0, 12,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            Thread.ofVirtual().factory());

    private static boolean geyser = false;
    private static boolean floodgate = false;

    static {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            floodgate = true;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("org.geysermc.geyser.api.GeyserApi");
            geyser = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static boolean isBedrockPlayer(UUID uuid) {
        if (floodgate)
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        if (geyser) return GeyserApi.api().isBedrockPlayer(uuid);
        return false;
    }

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executor);
    }
}
