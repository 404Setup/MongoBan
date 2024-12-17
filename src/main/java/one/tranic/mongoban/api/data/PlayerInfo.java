package one.tranic.mongoban.api.data;

import java.util.UUID;

public record PlayerInfo(String name, UUID uuid, java.net.InetAddress[] ip) {
}
