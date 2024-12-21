package one.tranic.mongoban.common.command;

import one.tranic.mongoban.common.source.SourceImpl;

import java.util.List;

/**
 * Represents a command implementation designed to be executed by or for a specific source type.
 * <p>
 * Commands implementing this interface can be executed, provide suggestions for input, and verify permissions
 * based on the context of the source.
 *
 * @param <C> the type of the source implementing {@link SourceImpl}, representing the context in which the command is executed
 */
public interface CommandImpl<C extends SourceImpl<?>> {
    /**
     * Executes the command for the provided source.
     *
     * @param source the source context in which the command is executed, must not be null
     */
    void execute(C source);

    /**
     * Provides a list of suggestions based on the given source context.
     * <p>
     * The suggestions are typically utilized for auto-completion or assistance
     * purposes while interacting with commands associated with a specific source type.
     *
     * @param source the context of the source from which the suggestions are generated; must implement {@link SourceImpl}
     * @return a list of suggested strings, or an empty list if no suggestions are available
     */
    List<String> suggest(C source);

    /**
     * Checks if the given source has the required permissions to execute a command or perform an action.
     *
     * @param source the contextual source for which the permission check is being performed
     * @return true if the source has the appropriate permissions, otherwise false
     */
    boolean hasPermission(C source);
}
