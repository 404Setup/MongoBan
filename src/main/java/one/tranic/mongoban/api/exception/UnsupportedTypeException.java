package one.tranic.mongoban.api.exception;

/**
 * A custom exception that indicates an operation involving an unsupported or invalid type.
 * <p>
 * This exception is a subclass of {@code RuntimeException} and is typically used to signal
 * situations where a type provided to a method or operation is invalid or not supported.
 * <p>
 * This exception can be constructed with a custom message, an existing exception, or
 * automatically include the class name of the unsupported object to provide additional context.
 */
public class UnsupportedTypeException extends RuntimeException {
    /**
     * Constructs a new UnsupportedTypeException with the specified detail message.
     * <p>
     * The message can provide additional information about the unsupported or invalid type.
     *
     * @param message the detail message explaining the cause or context of the exception
     */
    public UnsupportedTypeException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code UnsupportedTypeException} with no detail message.
     * <p>
     * This constructor creates an instance of {@code UnsupportedTypeException} for use
     * in signaling operations involving unsupported or invalid types where no specific
     * message is required.
     */
    public UnsupportedTypeException() {
        super();
    }

    /**
     * Constructs a new {@code UnsupportedTypeException} with the specified exception as the cause.
     *
     * @param exception the exception that caused this {@code UnsupportedTypeException} to be thrown
     */
    public UnsupportedTypeException(Exception exception) {
        super(exception);
    }

    /**
     * Constructs a new {@code UnsupportedTypeException} with a detail message
     * that includes the class name of the unsupported object's type.
     *
     * @param object the object of the unsupported type
     */
    public UnsupportedTypeException(Object object) {
        this("Unsupported type: " + object.getClass().getName());
    }
}
