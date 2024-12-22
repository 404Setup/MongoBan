package one.tranic.mongoban.api;

import one.tranic.mongoban.api.data.Operator;
import one.tranic.mongoban.common.Collections;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class MongoBanAPI {
    /**
     * An immutable, empty list of strings.
     * <p>
     * This constant provides an empty list that can be returned or used in situations
     * where no elements are required. It is initialized with no elements and does not
     * allow modification, making it safe for shared or unmodifiable usage contexts.
     */
    public final static List<String> EMPTY_LIST = Collections.newArrayList();
    /**
     * A list containing suggested reasons for specific actions such as bans or reports.
     * <p>
     * Predefined reasons include "Griefing", "Cheating", "Spamming", "Abusing",
     * and a placeholder "<reason>" for custom input.
     */
    public final static List<String> REASON_SUGGEST = Collections.newArrayList("Griefing", "Cheating", "Spamming", "Abusing", "OtherReason");
    /**
     * A predefined immutable list of time duration suggestions for use in various contexts.
     * <p>
     * This list includes common time units such as seconds, minutes, hours, days, months,
     * and years, as well as a placeholder for customization ("<reason>").
     * <p>
     * Entries in the list are represented as strings.
     */
    public final static List<String> TIME_SUGGEST = Collections.newArrayList("10s", "1m", "1h", "1d", "1mo", "1y", "forever");

    /**
     * Represents a predefined system operator designed for administrative tasks within the application.
     * <p>
     * This constant defines a unique operator with the display name "Console" and a fixed UUID.
     * <p>
     * It serves as a representation of a non-human, system-level operator used for automated
     * actions or logging purposes, such as executing commands or issuing administrative tasks
     * on behalf of the application.
     * <p>
     * The immutability of the associated `Operator` record ensures that the data for this
     * operator remains consistent and unmodifiable throughout the application's lifecycle.
     */
    public final static Operator console = new Operator("Console", UUID.fromString("05b11eee-24db-4a21-ba9d-e12e8df9a92f"));
    /**
     * A shared executor for handling asynchronous tasks with flexible scaling capabilities.
     * <p>
     * This executor is configured to have a core pool size of 0 and a maximum pool size of 12 threads.
     * <p>
     * Threads are kept alive for 60 seconds when idle. It uses a {@link SynchronousQueue} for
     * task handoff and a virtual thread factory for lightweight, efficient threading.
     * <p>
     * Designed for use cases where tasks are short-lived and the system dynamically scales thread usage
     * within the defined limits. The use of virtual threads promotes concurrency while minimizing resource
     * consumption.
     */
    public final static Executor executor = new ThreadPoolExecutor(0, 12,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
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
}
