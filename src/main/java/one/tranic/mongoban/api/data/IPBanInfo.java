package one.tranic.mongoban.api.data;

import java.util.UUID;

public record IPBanInfo(String ip, UUID operator, int duration, String reason) {
}
