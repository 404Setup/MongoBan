package one.tranic.mongoban.api.exception;

/**
 * A custom exception that indicates a parsing error in the application.
 * This exception is a subclass of {@code RuntimeException} and is typically
 * used to signal invalid or unexpected input that cannot be correctly parsed.
 * <p>
 * The {@code ParseException} can be used in scenarios such as parsing time
 * arguments or other user-provided data that fails to meet the expected format
 * or value constraints.
 */
public class ParseException extends RuntimeException {
    /**
     * Constructs a new ParseException with the specified detail message.
     * The message can be used to provide additional information about
     * the parsing error.
     *
     * @param message the detail message indicating the cause of the parsing error
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ParseException} with no detail message.
     * <p>
     * This constructor creates an instance of {@code ParseException} for use in signaling
     * an error during parsing operations where specific detail about the error is not needed.
     */
    public ParseException() {
        super();
    }
}
