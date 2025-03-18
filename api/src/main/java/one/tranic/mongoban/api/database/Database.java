package one.tranic.mongoban.api.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.t.base.cache.Cache;
import one.tranic.t.utils.Collections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
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
    private final Cache cache;

    private final String connectionString;
    private MongoClient client;

    public Database(String host, int port, String database, String user, String password, Cache cache) {
        this.database = database;
        this.cache = cache;

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
     * <p>
     * If an existing client connection exists, it will be closed and reset before establishing a new connection.
     * <p>
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
            MongoBanAPI.logger.info("Successfully connected to MongoDB");
        } catch (Exception e) {
            disconnect();
            MongoBanAPI.logger.error("Failed to connect to MongoDB: {}", e.getMessage());
        }
    }

    /**
     * Retrieves a MongoDB collection by its name from the connected database.
     *
     * @param collectionName the name of the MongoDB collection to retrieve
     * @return the MongoCollection object representing the specified collection
     * @throws IllegalArgumentException if the collection name is null or invalid
     */
    public MongoCollection<Document> getCollection(String collectionName) throws IllegalArgumentException {
        return getDB().getCollection(collectionName);
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
     * Updates a document in the specified MongoDB collection.
     * <p>
     * If no matching document is found, it will insert a new document (upsert behavior).
     *
     * @param collectionName the name of the MongoDB collection to update
     * @param query          the query criteria to identify the document to update
     * @param updateDoc      the document with the updated fields to apply
     * @return true if the operation was successful, false if an exception occurred
     */
    public boolean update(String collectionName, Document query, Document updateDoc) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.findOneAndUpdate(
                    query,
                    new Document("$set", updateDoc),
                    new FindOneAndUpdateOptions().upsert(true)
            );
            return true;
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Updates a single document in the specified MongoDB collection based on the provided filter.
     *
     * @param collectionName the name of the MongoDB collection to update
     * @param filter         the filter criteria to identify the document to update
     * @param update         the update operations to apply to the matched document
     * @return true if the operation was successful; false if an exception occurred
     */
    public boolean onlyUpdate(String collectionName, Bson filter, Bson update) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Updates multiple documents in the specified MongoDB collection based on the given filter and update operations.
     *
     * @param collectionName the name of the MongoDB collection where the documents will be updated
     * @param filter         the filter criteria to identify the documents to update
     * @param update         the update operations to apply to the matched documents
     * @return true if the operation was successful, false if an exception occurred
     */
    public boolean OnlyUpdateMany(String collectionName, Bson filter, Bson update) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.updateMany(filter, update);
            return true;
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a document into the specified MongoDB collection.
     *
     * @param collectionName the name of the MongoDB collection where the document will be inserted
     * @param updateDoc      the document to be inserted into the collection
     * @return true if the document was successfully inserted; false if an exception occurred
     */
    public boolean insert(String collectionName, Document updateDoc) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.insertOne(updateDoc);
            return true;
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a list of documents into a specified MongoDB collection.
     *
     * @param collectionName the name of the MongoDB collection where the documents will be inserted
     * @param updateDoc      the list of documents to be inserted into the collection
     * @return true if the documents were successfully inserted; false if an exception occurred
     */
    public boolean insert(String collectionName, List<Document> updateDoc) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.insertMany(updateDoc);
            return true;
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
            return false;
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
            MongoCollection<Document> collection = getCollection(collectionName);

            return collection.find(query).first();
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * Queries a MongoDB collection and retrieves the first document that matches the specified query.
     *
     * @param collectionName the name of the MongoDB collection to query
     * @param query the query criteria used to filter the results
     * @return the first document that matches the query, or null if no matches are found or an error occurs
     */
    public Document queryOne(String collectionName, Bson query) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            return collection.find(query).first();
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
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
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.find(query).into(resultList);
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
        }
        return resultList;
    }

    /**
     * Executes a query against the specified MongoDB collection and returns a list of documents
     * matching the query criteria.
     *
     * @param collectionName the name of the MongoDB collection to query
     * @param query the query criteria to filter the documents
     * @return a list of Document objects resulting from the query execution;
     * an empty list is returned if no documents match or in case of an exception
     */
    public List<Document> queryMany(String collectionName, Bson query) {
        List<Document> resultList = Collections.newArrayList();
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.find(query).into(resultList);
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
        }
        return resultList;
    }

    /**
     * Deletes a single document in the specified MongoDB collection that matches the query.
     *
     * @param collectionName the name of the MongoDB collection
     * @param query          the query criteria to identify the document to delete
     */
    public void delete(String collectionName, Document query) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.deleteOne(query);
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
        }
    }

    /**
     * Deletes a single document from the specified MongoDB collection
     * that matches the provided filter criteria.
     *
     * @param collectionName the name of the MongoDB collection from which the document will be deleted
     * @param filter         the filter criteria used to identify the document to delete
     */
    public void delete(String collectionName, Bson filter) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.deleteOne(filter);
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
        }
    }

    /**
     * Deletes a single document from the specified MongoDB collection that matches the given field name and value.
     *
     * @param <T>            the data type of the value used for matching the document to delete
     * @param collectionName the name of the MongoDB collection from which the document will be deleted
     * @param fieldName      the name of the field used to filter the document to delete
     * @param value          the value used to filter the document to delete, which can be null
     */
    public <T> void delete(String collectionName, @NotNull String fieldName, @Nullable T value) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.deleteOne(Filters.eq(fieldName, value));
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
        }
    }

    /**
     * Deletes multiple documents from the specified MongoDB collection that match the provided filter.
     *
     * @param collectionName the name of the MongoDB collection from which documents will be deleted
     * @param filter         the filter criteria used to identify the documents to delete
     */
    public void deleteMany(String collectionName, Bson filter) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.deleteMany(filter);
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
        }
    }

    /**
     * Deletes multiple documents from the specified collection in the MongoDB database
     * that match the given field name and value.
     *
     * @param <T>            the data type of the value used for matching the documents to delete
     * @param collectionName the name of the MongoDB collection from which documents will be deleted
     * @param fieldName      the name of the field on which the filter condition will be applied
     * @param value          the value used to filter the documents to be deleted, which can be null
     */
    public <T> void deleteMany(String collectionName, @NotNull String fieldName, @Nullable T value) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.deleteMany(Filters.eq(fieldName, value));
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
        }
    }

    /**
     * Deletes all documents in the specified MongoDB collection that match the query.
     *
     * @param collectionName the name of the MongoDB collection
     * @param query          the query criteria to identify the documents to delete
     */
    public void deleteMany(String collectionName, Document query) {
        try {
            MongoCollection<Document> collection = getCollection(collectionName);

            collection.deleteMany(query);
        } catch (Exception e) {
            MongoBanAPI.logger.error(e.getMessage());
        }
    }

    /**
     * Provides access to the DatabaseService instance associated with this Database.
     *
     * @return The DatabaseService instance used for interacting with the database.
     */
    public DatabaseService service() {
        return service;
    }

    /**
     * Provides access to the {@link DatabasePlayerApplication} instance, which is responsible for
     * handling player-related database operations, such as retrieving player information
     * and managing player data persistence.
     *
     * @return The {@code DatabasePlayerApplication} instance used for player-related database operations.
     */
    public DatabasePlayerApplication player() {
        return service.getPlayerApplication();
    }

    /**
     * Retrieves the instance of DatabaseBanApplication associated with the database.
     *
     * @return the DatabaseBanApplication instance used for handling ban-related operations.
     */
    public DatabaseBanApplication ban() {
        return service.getBanApplication();
    }

    /**
     * Retrieves the {@link DatabaseWarnApplication} instance associated with this Database.
     *
     * @return The DatabaseWarnApplication instance used for managing player warnings.
     */
    public DatabaseWarnApplication warn() {
        return service.getWarnApplication();
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
