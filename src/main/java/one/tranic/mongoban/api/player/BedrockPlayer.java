package one.tranic.mongoban.api.player;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;

import java.util.UUID;

public class BedrockPlayer {
    private static boolean geyser = false;
    private static boolean floodgate = false;

    static {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            floodgate = true;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("org.geysermc.geyser.api.GeyserApi");
            geyser = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    /**
     * Determines whether the player associated with the given UUID is a Bedrock player.
     * <p>
     * This method checks if the UUID belongs to a Bedrock player, utilizing either the
     * Floodgate or Geyser API based on the availability of these integrations.
     *
     * @param uuid The UUID of the player to check.
     * @return {@code true} if the player is a Bedrock player; {@code false} otherwise.
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        if (floodgate)
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        if (geyser) return GeyserApi.api().isBedrockPlayer(uuid);
        return false;
    }

    /**
     * Gets the platform of the player associated with the given UUID.
     * <p>
     * This method determines the player's platform using either the Floodgate or Geyser API,
     * based on their availability. If neither API recognizes the UUID, it returns "Java Edition".
     *
     * @param uuid the UUID of the player whose platform is being determined
     * @return the platform of the player as a string, or "Java Edition" if the platform cannot be determined
     */
    public static @NonNull String getPlatform(UUID uuid) {
        if (floodgate) {
            FloodgatePlayer player = FloodgateApi.getInstance().getPlayer(uuid);
            if (player != null) return player.getDeviceOs().toString();
        } else if (geyser) {
            @Nullable GeyserConnection player = GeyserApi.api().connectionByUuid(uuid);
            if (player != null) return player.platform().toString();
        }
        return "Java Edition";
    }

    /**
     * Retrieves the XUID (Xbox Unique Identifier) of a player identified by their UUID.
     * <p>
     * This method checks for the availability of the Floodgate or Geyser API and
     * uses the corresponding integration to obtain the player's XUID.
     *
     * @param uuid the unique identifier (UUID) of the player whose XUID is to be retrieved
     * @return the XUID of the player if available; otherwise, {@code null}
     */
    public static @Nullable String getXUID(UUID uuid) {
        if (floodgate) {
            FloodgatePlayer player = FloodgateApi.getInstance().getPlayer(uuid);
            if (player != null) return player.getXuid();
        } else if (geyser) {
            @Nullable GeyserConnection player = GeyserApi.api().connectionByUuid(uuid);
            if (player != null) return player.xuid();
        }
        return null;
    }

    /**
     * Sends a form to a player identified by their UUID.
     * <p>
     * This method leverages either the Floodgate or Geyser API, depending on their availability, to deliver the form.
     * <p>
     * If neither API is available, the method will return false.
     *
     * @param uuid the unique identifier of the player to whom the form should be sent
     * @param form the form object to be sent to the player
     * @return true if the form was successfully sent using Floodgate or Geyser; false otherwise
     */
    public static boolean sendForm(UUID uuid, Form form) {
        if (floodgate)
            return FloodgateApi.getInstance().sendForm(uuid, form);
        if (geyser) return GeyserApi.api().sendForm(uuid, form);
        return false;
    }

    /**
     * Retrieves the ping of a player identified by their UUID.
     * <p>
     * This method uses the Geyser API to determine the ping of a Bedrock player if available.
     * If the player is not a Bedrock player or the Geyser API is not available, it returns -1.
     *
     * @param uuid the unique identifier of the player whose ping is to be retrieved
     * @return the ping of the player, or -1 if unable to determine
     */
    public static long getPing(UUID uuid) {
        if (geyser) {
            @Nullable GeyserConnection geyserPlayer = GeyserApi.api().connectionByUuid(uuid);
            if (geyserPlayer != null) return geyserPlayer.ping();
        }
        return -1;
    }
}
