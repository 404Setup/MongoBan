package one.tranic.mongoban.common.database;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DatabaseService {
    private final Database database;
    private final String collectionBan = "mongo_ban";
    private final String collectionWarn = "mongo_warn";

    private final Executor executor = Executors.newCachedThreadPool(Thread.ofVirtual().factory());

    public DatabaseService(Database database) {
        this.database = database;

        this.database.getDB().getCollection(this.collectionBan).createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));
        this.database.getDB().getCollection(this.collectionWarn).createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));
    }


    public void addPlayerBanSync(UUID uuid, UUID operator, int duration, @Nullable String reason) {
        Document query = new Document("id", uuid);
        Document updateDoc = new Document()
                .append("operator", operator)
                .append("duration", duration)
                .append("reason", reason != null ? reason : "<Banned by the server>");

        database.update(this.collectionBan, query, updateDoc);
    }

    public CompletableFuture<Void> addBanAsync(UUID uuid, UUID operator, int duration, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addPlayerBanSync(uuid, operator, duration, reason), executor);
    }

    public void addIPBanSync(String ip, UUID operator, int duration, @Nullable String reason) {
        Document query = new Document("ip", ip);
        Document updateDoc = new Document()
                .append("operator", operator)
                .append("duration", duration)
                .append("reason", reason != null ? reason : "<Banned by the server>");

        database.update(this.collectionBan, query, updateDoc);
    }

    public CompletableFuture<Void> addIPBanAsync(String ip, UUID operator, int duration, @Nullable String reason) {
        return CompletableFuture.runAsync(() -> addIPBanSync(ip, operator, duration, reason), executor);
    }

    public PlayerBanInfo getPlayerBanSync(UUID uuid) {
        Document query = new Document("id", uuid);
        Document banDoc = database.queryOne(this.collectionBan, query);
        return banDoc != null ? new PlayerBanInfo(
                uuid,
                banDoc.get("operator", UUID.class),
                banDoc.getInteger("duration"),
                banDoc.getString("reason")
        ) : null;
    }

    public CompletableFuture<PlayerBanInfo> getPlayerBanAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getPlayerBanSync(uuid), executor);
    }

    public IPBanInfo getIPBanInfoSync(String ip) {
        Document query = new Document("ip", ip);
        Document banDoc = database.queryOne(this.collectionBan, query);
        return banDoc != null ? new IPBanInfo(
                ip,
                banDoc.get("operator", UUID.class),
                banDoc.getInteger("duration"),
                banDoc.getString("reason")
        ) : null;
    }

    public CompletableFuture<IPBanInfo> getIPBanInfoAsync(String ip) {
        return CompletableFuture.supplyAsync(() -> getIPBanInfoSync(ip), executor);
    }
}
