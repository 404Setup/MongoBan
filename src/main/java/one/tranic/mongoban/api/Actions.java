package one.tranic.mongoban.api;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * A utility class designed to handle asynchronous and synchronous actions,
 * wrapping tasks defined by a {@link Supplier}.
 *
 * @param <T> the type of the result produced by the encapsulated task
 */
public class Actions<T> {
    /**
     * A {@link Supplier} instance representing the task to be executed.
     * <p>
     * The task produces a result of type {@code T} when executed.
     * <p>
     * This field is immutable and intended to encapsulate asynchronous
     * and synchronous operations.
     */
    private final Supplier<T> task;

    /**
     * Constructs an instance of {@code Actions} with the specified task.
     *
     * @param task a {@link Supplier} representing the task to be executed. The task
     *             produces a result of type {@code T}.
     */
    public Actions(Supplier<T> task) {
        this.task = task;
    }

    /**
     * Executes the encapsulated task synchronously and retrieves its result.
     *
     * @return the result of the task execution
     */
    public T sync() {
        return task.get();
    }

    /**
     * Executes the encapsulated task asynchronously using a shared executor.
     * <p>
     * This method utilizes the pre-configured executor from {@code MongoBanAPI}
     * to run the specified task in a non-blocking manner, returning a
     * {@code CompletableFuture} that will complete with the result of the task.
     *
     * @return a {@code CompletableFuture<T>} that completes with the result of the task
     * once it has been executed asynchronously.
     */
    public CompletableFuture<T> async() {
        return CompletableFuture.supplyAsync(task, MongoBanAPI.executor);
    }
}
