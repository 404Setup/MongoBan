package one.tranic.mongoban.api;

import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.api.data.Operator;
import one.tranic.mongoban.api.parse.json.FastJsonParser;
import one.tranic.mongoban.api.parse.json.GsonParser;
import one.tranic.mongoban.api.parse.json.JsonParser;
import one.tranic.mongoban.common.Collections;
import one.tranic.mongoban.common.Config;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class MongoBanAPI {
    /**
     * Represents the console source used for command execution and interaction in the system.
     */
    public final static SourceImpl<?, ?> CONSOLE_SOURCE = getConsoleSource();
    /**
     * An immutable and unmodifiable list that is intended to represent an empty list of strings.
     */
    public final static List<String> EMPTY_LIST = Collections.newUnmodifiableList();

    /**
     * A predefined unmodifiable list of suggested reasons for reporting or moderating a player in the system.
     */
    public final static List<String> REASON_SUGGEST = Collections.newUnmodifiableList("Griefing", "Cheating", "Spamming", "Abusing", "OtherReason");
    /**
     * A predefined, unmodifiable list of suggested time durations for banning or restricting users.
     */
    public final static List<String> TIME_SUGGEST = Collections.newUnmodifiableList("10s", "1m", "1h", "1d", "1mo", "1y", "forever");

    /**
     * Represents a predefined system operator designed for administrative tasks within the application.
     */
    public final static Operator console = new Operator("Console", UUID.fromString("05b11eee-24db-4a21-ba9d-e12e8df9a92f"));

    /**
     * A shared executor for handling asynchronous tasks with flexible scaling capabilities.
     */
    public final static Executor executor = new ThreadPoolExecutor(0, 12,
            45L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            Thread.ofVirtual().factory());

    /**
     * A static instance of {@link JsonParser} that provides JSON parsing and serialization functionalities.
     */
    public final static JsonParser jsonParser = Config.isFastjson() ? new FastJsonParser() : new GsonParser();

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

    /**
     * Retrieves the console source implementation based on the platform being used.
     * <p>
     * This method provides a platform-specific {@link SourceImpl} instance to represent
     * the system's console command sender for issuing commands and receiving responses.
     *
     * @return a {@code SourceImpl<?, ?>} implementation for the console source,
     * depending on the detected platform.
     */
    private static SourceImpl<?, ?> getConsoleSource() {
        if (Platform.get() == Platform.BungeeCord) {
            return new one.tranic.mongoban.api.command.source.BungeeSource(
                    net.md_5.bungee.api.ProxyServer.getInstance().getConsole(), null);
        }
        if (Platform.get() == Platform.Velocity) {
            return new one.tranic.mongoban.api.command.source.VelocitySource(one.tranic.mongoban.velocity.MongoBan.getProxy().getConsoleCommandSource());
        }
        return new one.tranic.mongoban.api.command.source.PaperSource(org.bukkit.Bukkit.getConsoleSender(), null);
    }

    /**
     * Determines whether the player associated with the given UUID is a Bedrock player.
     * <p>
     * This method checks if the UUID belongs to a Bedrock player, utilizing either the
     * Floodgate or Geyser API based on the availability of these integrations.
     *
     * @param uuid The UUID of the player to check.
     * @return {@code true} if the player is a Bedrock player; {@code false} otherwise.
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        if (floodgate)
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        if (geyser) return GeyserApi.api().isBedrockPlayer(uuid);
        return false;
    }

    /**
     * Sends a form to a player identified by their UUID.
     * <p>
     * This method leverages either the Floodgate or Geyser API, depending on their availability, to deliver the form.
     * <p>
     * If neither API is available, the method will return false.
     *
     * @param uuid the unique identifier of the player to whom the form should be sent
     * @param form the form object to be sent to the player
     * @return true if the form was successfully sent using Floodgate or Geyser; false otherwise
     */
    public static boolean sendForm(UUID uuid, Form form) {
        if (floodgate)
            return FloodgateApi.getInstance().sendForm(uuid, form);
        if (geyser) return GeyserApi.api().sendForm(uuid, form);
        return false;
    }

    /**
     * Executes the given {@code Runnable} asynchronously using a pre-configured executor.
     *
     * @param runnable the {@code Runnable} task to be executed asynchronously
     * @return a {@code CompletableFuture<Void>} that completes once the specified task has been executed
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executor);
    }

    /**
     * Executes the given supplier asynchronously using a pre-configured executor.
     * <p>
     * This method is intended for running tasks that return a result in an asynchronous manner.
     *
     * @param <T>      the type of the result produced by the supplier
     * @param supplier the {@code Supplier} task to be executed asynchronously
     * @return a {@code CompletableFuture<T>} that completes with the result of the supplier execution
     */
    public static <T> CompletableFuture<T> runAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executor);
    }
}
