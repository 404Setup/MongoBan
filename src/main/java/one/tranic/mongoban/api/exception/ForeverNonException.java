package one.tranic.mongoban.api.exception;

/**
 * A custom exception that serves as a warning that a "forever" check
 * should have been performed earlier in the workflow.
 * <p>
 * This exception should not be thrown during runtime. Instead, its
 * presence indicates that the calling code should ensure the relevant
 * "forever" condition is handled properly in advance.
 */
public class ForeverNonException extends RuntimeException {
    public ForeverNonException() {
        super();
    }
}
