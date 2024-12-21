package one.tranic.mongoban.api.command.player;

import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.data.PlayerInfo;
import org.geysermc.cumulus.form.Form;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

public interface MongoPlayer<C> {
    /**
     * Retrieves the unique identifier (UUID) of the player.
     *
     * @return the universally unique identifier (UUID) of the player
     */
    UUID getUniqueId();

    /**
     * Retrieves the host address to which the player is connected.
     *
     * @return the connect host as a string.
     */
    String getConnectHost();

    /**
     * Sends a form to the player associated with the current instance.
     * This method utilizes the {@link MongoBanAPI} to deliver the form,
     * associating it with the unique identifier (UUID) of the player.
     *
     * @param form the form to be sent to the player; must not be null
     * @return {@code true} if the form was successfully sent, {@code false} otherwise
     */
    default boolean sendForm(Form form) {
        return MongoBanAPI.sendForm(getUniqueId(), form);
    }

    /**
     * Determines whether the player associated with this instance is a Bedrock player.
     * This method utilizes the {@link MongoBanAPI#isBedrockPlayer(UUID)} implementation.
     *
     * @return true if the player is a Bedrock player; false otherwise.
     */
    default boolean isBedrockPlayer() {
        return MongoBanAPI.isBedrockPlayer(getUniqueId());
    }

    /**
     * Retrieves the information about the player, such as their name, unique identifier, and associated IP addresses.
     *
     * @return a {@link PlayerInfo} object representing the player's details
     */
    PlayerInfo getPlayerInfo();

    /**
     * Retrieves the locale associated with the player.
     *
     * @return the locale representing the player's language and regional preferences.
     */
    Locale getLocale();

    /**
     * Retrieves the location associated with the player.
     *
     * @return the {@code MongoLocation} of the player if available, or {@code null} if the location is not set.
     */
    @Nullable MongoLocation getLocation();

    /**
     * Retrieves the latency or ping value for the player in milliseconds.
     * <p>
     * The ping represents the time taken for data packets to travel
     * from the player to the server and back. It serves as a measure
     * of network latency between the player and the server.
     *
     * @return the ping value in milliseconds
     */
    long getPing();

    /**
     * Checks whether the player is currently online.
     *
     * @return true if the player is online, otherwise false
     */
    boolean isOnline();

    /**
     * Retrieves the client brand name associated with the player.
     * <p>
     * The client brand name typically refers to the branding or specific
     * modification of the client being used by the player (e.g., "vanilla" or a modified client).
     * This method may return null if the client brand is not available or not applicable
     * for the player.
     *
     * @return the client brand name as a string, or null if not available.
     */
    @Nullable String getClientBrand();

    /**
     * Retrieves the underlying source object associated with this player.
     *
     * @return the source player object of type C
     */
    C getSourcePlayer();

    /**
     * Removes the player from the server or disconnects the player for an unspecified reason.
     *
     * @return true if the player was successfully kicked, false otherwise
     */
    boolean kick();

    /**
     * Kicks the player from the game or server with a specified reason.
     *
     * @param reason the message or reason for the kick
     * @return true if the player was successfully kicked, false otherwise
     */
    boolean kick(String reason);

    /**
     * Disconnects the player from the server with the specified reason.
     *
     * @param reason the reason for disconnecting the player; must not be null
     * @return true if the player was successfully disconnected, false otherwise
     */
    boolean kick(@NotNull Component reason);

    /**
     * Sends a text message to the player.
     *
     * @param message the message to be sent, must not be null
     */
    void sendMessage(String message);

    /**
     * Sends a message to the player represented by this instance.
     *
     * @param message the message to be sent; must not be null
     */
    void sendMessage(@NotNull Component message);
}
