package one.tranic.mongoban.common.source;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

/**
 * Represents a generic source implementation that defines common behaviors
 * and attributes for sources such as players or other entities.
 *
 * @param <C> the type of the extending source implementation
 */
public interface SourceImpl<C> {
    /**
     * Retrieves the source entity that this implementation represents.
     *
     * @return the source entity of type C
     */
    C getSource();

    /**
     * Determines if the entity represented by this source is a player.
     *
     * @return true if the source is a player, otherwise false
     */
    boolean isPlayer();

    /**
     * Kicks this source from the system or server without providing a specific reason.
     * <p>
     * The behavior and implementation depend on the type of the source (e.g., player or entity).
     */
    boolean kick();

    /**
     * Kicks the source from the server with the specified reason.
     *
     * @param reason the reason for the kick
     */
    boolean kick(String reason);

    /**
     * Kicks the source with a specified reason.
     *
     * @param reason the reason for kicking the source; must not be null
     */
    boolean kick(@NotNull Component reason);

    /**
     * Retrieves the name associated with the source.
     *
     * @return the name of the source as a String
     */
    String getName();

    /**
     * Retrieves the unique identifier associated with this source.
     * <p>
     * If the source is not a player, this method may return null.
     *
     * @return a UUID representing the unique identifier of this source, or null if unavailable
     */
    @Nullable UUID getUniqueId();

    /**
     * Retrieves the arguments associated with the source. The arguments are typically
     * used to provide additional information or parameters related to a command or action.
     *
     * @return an array of strings representing the arguments associated with the source
     */
    String[] getArgs();

    /**
     * Retrieves the locale associated with this source, identifying the language
     * and regional preferences set for the source.
     *
     * @return the locale representing the language and region preferences, or null
     * if the client is not connected to any server.
     * <p>
     * If the source is not a player, it returns the environment's default locale.
     */
    @Nullable Locale locale();

    /**
     * Sends a message to the source.
     *
     * @param message the message to be sent
     */
    void sendMessage(String message);

    /**
     * Sends a message to the source.
     *
     * @param message the message to be sent; must not be null
     */
    void sendMessage(@NotNull Component message);
}
