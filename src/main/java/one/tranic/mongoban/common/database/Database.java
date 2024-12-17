package one.tranic.mongoban.common.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import one.tranic.mongoban.common.Collections;
import one.tranic.mongoban.common.Data;
import org.bson.Document;

import java.util.List;

public class Database {
    private final String database;
    private final DatabaseService service;

    private final String connectionString;
    private MongoClient client;

    public Database(String host, int port, String database, String user, String password) {
        this.database = database;

        StringBuilder connectionString = new StringBuilder().append("mongodb://");
        if (user == null || user.isEmpty()) connectionString.append(host).append(":").append(port);
        else if (password == null || password.isEmpty())
            connectionString.append(user).append("@").append(host).append(":").append(port);
        else
            connectionString.append(user).append(":").append(password).append("@").append(host).append(":").append(port);

        this.connectionString = connectionString.toString();

        connect();

        this.service = new DatabaseService(this);
    }

    public void connect() {
        if (client != null) {
            client.close();
            client = null;
        }
        client = MongoClients.create(connectionString);
        try {
            getDB();
            Data.logger.info("Successfully connected to MongoDB");
        } catch (Exception e) {
            disconnect();
            Data.logger.error("Failed to connect to MongoDB: {}", e.getMessage());
        }
    }

    public com.mongodb.client.MongoDatabase getDB() {
        return client.getDatabase(database);
    }

    public void update(String collectionName, Document query, Document updateDoc) {
        try {
            com.mongodb.client.MongoDatabase database = getDB();
            MongoCollection<Document> collection = database.getCollection(collectionName);

            collection.findOneAndUpdate(
                    query,
                    new Document("$set", updateDoc),
                    new FindOneAndUpdateOptions().upsert(true)
            );
        } catch (Exception e) {
            Data.logger.error(e.getMessage());
        }
    }


    public Document queryOne(String collectionName, Document query) {
        try {
            com.mongodb.client.MongoDatabase database = getDB();
            MongoCollection<Document> collection = database.getCollection(collectionName);

            return collection.find(query).first();
        } catch (Exception e) {
            Data.logger.error(e.getMessage());
        }
        return null;
    }

    public List<Document> queryMany(String collectionName, Document query) {
        List<Document> resultList = Collections.newArrayList();
        try {
            com.mongodb.client.MongoDatabase database = getDB();
            MongoCollection<Document> collection = database.getCollection(collectionName);

            collection.find(query).into(resultList);
        } catch (Exception e) {
            Data.logger.error(e.getMessage());
        }
        return resultList;
    }

    public DatabaseService getService() {
        return service;
    }

    public void disconnect() {
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
