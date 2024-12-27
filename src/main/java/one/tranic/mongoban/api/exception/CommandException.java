package one.tranic.mongoban.api.exception;

/**
 * A custom exception that represents errors occurring during the execution
 * or processing of commands in the application.
 * <p>
 * This exception is a subclass of {@code RuntimeException} and is designed to signal command-specific failures
 * that developers or users need to be made aware of.
 * <p>
 * This exception can be used in scenarios where command validation, execution,
 * or configuration fails or encounters issues.
 * <p>
 * Developers can provide additional details about the failure by using the constructor that accepts a message.
 */
public class CommandException extends RuntimeException {
    public CommandException(String message) {
        super(message);
    }

    public CommandException() {
        super();
    }

    public CommandException(Exception exception) {
        super(exception);
    }
}
