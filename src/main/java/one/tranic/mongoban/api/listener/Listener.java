package one.tranic.mongoban.api.listener;

import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.command.message.Message;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.UUID;

/**
 * The Listener abstract class provides a framework for handling pre-login events
 * and managing access control for incoming connections.
 * <p>
 * It is designed to work with event objects of a specified generic type and to enforce ban policies
 * based on IP and player information stored in a database.
 *
 * @param <T> the type of event this Listener will handle
 */
public abstract class Listener<T> {
    /**
     * Handles a pre-login event, allowing for custom operations such as access control
     * validation, ban enforcement, or event modification before a user is fully logged in.
     *
     * @param event the event object representing the pre-login action; contains
     *              information such as the user's IP address, username, or UUID
     */
    public abstract void onPreLoginEvent(T event);

    /**
     * Determines whether a specific event is allowed to proceed based on custom criteria.
     * <p>
     * This method is typically used for access control and validation logic.
     *
     * @param event the event object representing the action to be validated; contains relevant
     *              information such as user details or request parameters
     * @return true if the event is allowed, false otherwise
     */
    public abstract boolean isAllowed(T event);

    /**
     * Denies access to a specific event, providing an optional reason for the denial.
     * <p>
     * This method is typically used to enforce access control policies, such as bans,
     * during a pre-login or connection process.
     *
     * @param event  the event object representing the action being denied;
     *               provides context regarding the connection or user being restricted
     * @param reason an optional reason for the denial; may provide additional context or
     *               an explanatory message for the disallowed action; can be null
     */
    public abstract void disallow(T event, @Nullable Component reason);

    /**
     * Processes a pre-login event to handle user access based on IP and player ban information.
     * <p>
     * If the IP or player UUID is found in the ban records, the login attempt is disallowed
     * with an appropriate kick message. If no bans are found, the user data is added to the database.
     *
     * @param event    The event object representing the pre-login action to be processed.
     * @param username The username of the player attempting to log in.
     * @param uuid     The universally unique identifier (UUID) of the player.
     * @param ip       The IP address of the player attempting to log in.
     */
    public void doIt(T event, String username, UUID uuid, InetAddress ip) {
        IPBanInfo result = MongoDataAPI.getDatabase().ban().ip().find(ip.getHostAddress()).sync();
        if (result != null) {
            MongoDataAPI.getDatabase().ban()
                    .player().find(uuid)
                    .async()
                    .thenAcceptAsync((p) -> {
                        if (p != null) return;
                        MongoDataAPI.getDatabase().ban()
                                .player()
                                .add(uuid, MongoBanAPI.console, result.duration(), ip.getHostAddress(), result.reason())
                                .sync();
                    }, MongoBanAPI.executor);
            disallow(event, Message.kickMessage(result));
            return;
        } else {
            PlayerBanInfo playerResult = MongoDataAPI.getDatabase().ban().player()
                    .find(uuid)
                    .sync();
            if (playerResult != null) {
                disallow(event, Message.kickMessage(playerResult));
                return;
            }
        }

        MongoDataAPI.getDatabase().player().add(username, uuid, ip.getHostAddress());
    }
}
