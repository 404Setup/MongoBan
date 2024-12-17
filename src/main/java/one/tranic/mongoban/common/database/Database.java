package one.tranic.mongoban.common.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import one.tranic.mongoban.common.Collections;
import one.tranic.mongoban.common.Data;
import org.bson.Document;

import java.util.List;

/**
 * The Database class provides a wrapper for handling MongoDB database connections
 * and operations.
 * It enables creating a connection to a MongoDB instance and
 * performing database operations like querying and updating.
 */
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

    /**
     * Establishes a connection to the MongoDB database using the configured connection string.
     * If an existing client connection exists, it will be closed and reset before establishing a new connection.
     * Logs the success or failure of the connection attempt.
     * In case of a failure, the connection will be cleaned up by calling the {@link #disconnect()} method.
     */
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

    /**
     * Retrieves the MongoDatabase instance associated with the current connection.
     *
     * @return a MongoDatabase object representing the database.
     */
    public com.mongodb.client.MongoDatabase getDB() {
        return client.getDatabase(database);
    }

    /**
     * Updates a document in the specified MongoDB collection by finding a document that matches the query
     * and applying the update document.
     * If no matching document is found, this method inserts a new document.
     *
     * @param collectionName the name of the MongoDB collection in which the operation is to be performed
     * @param query          the document specifying the criteria used to match the target document for update
     * @param updateDoc      the document containing the fields and values to update in the matched document
     */
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


    /**
     * Queries a MongoDB collection and retrieves the first document that matches the specified query.
     *
     * @param collectionName the name of the collection to query
     * @param query          the query criteria used to filter the results
     * @return the first document that matches the query, or null if no matches are found or an error occurs
     */
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

    /**
     * Executes a query against the specified MongoDB collection and returns a list of documents
     * matching the query criteria.
     *
     * @param collectionName the name of the MongoDB collection to query
     * @param query          the query criteria to filter the documents
     * @return a list of Document objects resulting from the query execution;
     * an empty list is returned if no documents match or in case of an exception
     */
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

    /**
     * Provides access to the DatabaseService instance associated with this Database.
     *
     * @return The DatabaseService instance used for interacting with the database.
     */
    public DatabaseService getService() {
        return service;
    }

    /**
     * Closes the existing database client connection and releases any allocated resources.
     * This method ensures that the database client is properly disconnected
     * by setting the client object to null after closing it.
     * If no connection exists, the method performs no action.
     */
    public void disconnect() {
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
