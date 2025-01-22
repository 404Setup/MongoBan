package one.tranic.mongoban.api;

import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.api.data.Operator;
import one.tranic.mongoban.api.parse.json.GsonParser;
import one.tranic.mongoban.api.parse.json.JsonParser;
import one.tranic.mongoban.common.Collections;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class MongoBanAPI {
    /**
     * Represents the console source used for command execution and interaction in the system.
     */
    public final static SourceImpl<?, ?> CONSOLE_SOURCE = getConsoleSource();

    public final static List<String> EMPTY_LIST = Collections.newUnmodifiableList();
    public final static List<String> FLAG_LIST = Collections.newUnmodifiableList("--target", "--duration", "--reason", "--strict");
    public final static List<String> REASON_SUGGEST = Collections.newUnmodifiableList("Griefing", "Cheating", "Spamming", "Abusing", "OtherReason");
    public final static List<String> TIME_SUGGEST = Collections.newUnmodifiableList("s", "m", "h", "d", "mo", "y", "forever");

    /**
     * Represents a predefined system operator designed for administrative tasks within the application.
     */
    public final static Operator console = new Operator("Console", UUID.fromString("05b11eee-24db-4a21-ba9d-e12e8df9a92f"));

    /**
     * A shared executor for handling asynchronous tasks with flexible scaling capabilities.
     */
    public final static Executor executor = new ThreadPoolExecutor(0, 8,
            45L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            Thread.ofVirtual().factory());

    /**
     * A static instance of {@link JsonParser} that provides JSON parsing and serialization functionalities.
     */
    public final static JsonParser jsonParser = new GsonParser();

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
        if (Platform.get() == Platform.BungeeCord)
            return new one.tranic.mongoban.api.command.source.BungeeSource(
                    net.md_5.bungee.api.ProxyServer.getInstance().getConsole(), null
            );
        if (Platform.get() == Platform.Velocity)
            return new one.tranic.mongoban.api.command.source.VelocitySource(
                    one.tranic.mongoban.velocity.MongoBan.getProxy().getConsoleCommandSource()
            );
        return new one.tranic.mongoban.api.command.source.PaperSource(
                org.bukkit.Bukkit.getConsoleSender(), null
        );
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
