package one.tranic.mongoban.api.player;

/**
 * Represents a location in a specific world with precise x, y, and z coordinates.
 * <p>
 * This class is utilized for storing and transferring positional data typically
 * associated with a player or an object in a Minecraft-like environment.
 * <p>
 * Features:
 * <p>
 * - Stores the name of the world as a string.
 * <p>
 * - Stores the x, y, and z coordinates as double-precision floating-point numbers.
 * <p>
 * This record is immutable and serves as a lightweight data container.
 */
public record MongoLocation(String world, double x, double y, double z, float yaw, float pitch) {
}
