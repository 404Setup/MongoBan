package one.tranic.mongoban.api.player;

import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.data.PlayerInfo;
import org.geysermc.cumulus.form.Form;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MongoPlayer<C> {
    /**
     * Retrieves the username of the player.
     *
     * @return the username of the player as a string
     */
    String getUsername();

    /**
     * Retrieves the unique identifier (UUID) of the player.
     *
     * @return the universally unique identifier (UUID) of the player
     */
    UUID getUniqueId();

    /**
     * Retrieves the Xbox Unique Identifier (XUID) for the player if the player is a Bedrock player.
     * This method determines whether the player is a Bedrock player and, if so, utilizes
     * the {@link BedrockPlayer#getXUID(UUID)} implementation to fetch the XUID.
     *
     * @return the XUID as a string if the player is identified as a Bedrock player;
     * otherwise, {@code null}.
     */
    default @Nullable String getXUID() {
        return isBedrockPlayer() ? BedrockPlayer.getXUID(getUniqueId()) : null;
    }

    /**
     * Retrieves the host address to which the player is connected.
     *
     * @return the connect host as a string.
     */
    String getConnectHost();

    /**
     * Sends a form to the player associated with the current instance.
     * This method utilizes the {@link BedrockPlayer} to deliver the form,
     * associating it with the unique identifier (UUID) of the player.
     *
     * @param form the form to be sent to the player; must not be null
     * @return {@code true} if the form was successfully sent, {@code false} otherwise
     */
    default boolean sendForm(Form form) {
        return BedrockPlayer.sendForm(getUniqueId(), form);
    }

    default CompletableFuture<Boolean> sendFormAsync(Form form) {
        return MongoBanAPI.runAsync(() -> sendForm(form));
    }

    /**
     * Determines whether the player associated with this instance is a Bedrock player.
     * This method utilizes the {@link BedrockPlayer#isBedrockPlayer(UUID)} implementation.
     *
     * @return true if the player is a Bedrock player; false otherwise.
     */
    default boolean isBedrockPlayer() {
        return BedrockPlayer.isBedrockPlayer(getUniqueId());
    }

    /**
     * Asynchronously retrieves information about the player.
     *
     * @return a {@link CompletableFuture} that, when completed, provides an instance of {@link PlayerInfo}
     * containing details about the player, such as their name, UUID, and associated IP addresses.
     */
    default CompletableFuture<PlayerInfo> getPlayerInfoAsync() {
        return MongoBanAPI.runAsync(this::getPlayerInfo);
    }

    /**
     * Retrieves the information about the player, such as their name, unique identifier, and associated IP addresses.
     *
     * @return a {@link PlayerInfo} object representing the player's details
     */
    default PlayerInfo getPlayerInfo() {
        return MongoDataAPI.getDatabase().player().find(getUniqueId()).sync();
    }

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
