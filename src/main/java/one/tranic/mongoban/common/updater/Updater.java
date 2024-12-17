package one.tranic.mongoban.common.updater;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * This interface defines the structure for an Updater.
 * Implementations of this interface provide functionality to retrieve and handle update records.
 */
public interface Updater {
    /**
     * A default empty {@link UpdateRecord} used when no updates are available.
     */
    UpdateRecord empty = new UpdateRecord(false, "", "", "");

    /**
     * Retrieves the latest {@link UpdateRecord}.
     *
     * @return the {@link UpdateRecord} containing update details.
     * @throws IOException if there is an issue retrieving the update information.
     */
    UpdateRecord getUpdate() throws IOException;

    /**
     * Asynchronously retrieves the latest {@link UpdateRecord} and passes it to the provided {@link Consumer}.
     * <p>
     * This method runs in a separate thread. If an {@link IOException} occurs during the update retrieval,
     * it is caught and the exception stack trace is printed, and {@code null} may be passed to the consumer
     * in case of failure.
     * </p>
     *
     * @param consumer a {@link Consumer} that will process the retrieved {@link UpdateRecord} or {@code null}
     *                 if an error occurs.
     */
    default void getUpdateAsync(@NotNull Consumer<@Nullable UpdateRecord> consumer) {
        Thread.ofVirtual().name("MongoBan Async Updater").start(() -> {
            try {
                consumer.accept(getUpdate());
            } catch (IOException e) {
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }
}
