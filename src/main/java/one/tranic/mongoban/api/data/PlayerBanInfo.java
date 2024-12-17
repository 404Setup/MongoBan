package one.tranic.mongoban.api.data;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record PlayerBanInfo(UUID uuid, UUID operator, int duration, @Nullable String reason) {
}
