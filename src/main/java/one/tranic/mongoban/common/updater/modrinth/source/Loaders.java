package one.tranic.mongoban.common.updater.modrinth.source;

import org.jetbrains.annotations.NotNull;

public enum Loaders {
    BUKKIT, SPIGOT, PAPER, PURPUR, FOLIA, SPONGE,
    BUNGEECORD, WATERFALL, VELOCITY,
    FORGE, NEOFORGE, FABRIC, QUILT, LITELOADER, RIFT, RISUGAMIMODLOADER,
    DATAPACK;

    public static @NotNull Loaders of(@NotNull String value) {
        return switch (value.toLowerCase()) {
            case "bukkit" -> BUKKIT;
            case "spigot" -> SPIGOT;
            case "paper" -> PAPER;
            case "purpur" -> PURPUR;
            case "folia" -> FOLIA;
            case "sponge" -> SPONGE;
            case "bungeecord" -> BUNGEECORD;
            case "waterfall" -> WATERFALL;
            case "velocity" -> VELOCITY;
            case "forge" -> FORGE;
            case "neoforge" -> NEOFORGE;
            case "fabric" -> FABRIC;
            case "quilt" -> QUILT;
            case "liteloader" -> LITELOADER;
            case "rift" -> RIFT;
            case "modloader" -> RISUGAMIMODLOADER;
            case "datapack" -> DATAPACK;
            default -> throw new IllegalArgumentException("Unknown status: " + value);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case BUKKIT -> "bukkit";
            case SPIGOT -> "spigot";
            case PAPER -> "paper";
            case PURPUR -> "purpur";
            case FOLIA -> "folia";
            case SPONGE -> "sponge";
            case BUNGEECORD -> "bungeecord";
            case WATERFALL -> "waterfall";
            case VELOCITY -> "velocity";
            case FORGE -> "forge";
            case NEOFORGE -> "neoforge";
            case FABRIC -> "fabric";
            case QUILT -> "quilt";
            case LITELOADER -> "liteloader";
            case RIFT -> "rift";
            case RISUGAMIMODLOADER -> "modloader";
            case DATAPACK -> "datapack";
        };
    }
}
