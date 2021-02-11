package actions;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class NoSQLAction {

    String mongoURI;
    String databaseName;
    String collection;
    String document;
    MongoDatabase database;

    public NoSQLAction(String mongoURI, String databaseName, String collection, String document) {
        System.out.println("Initializing NoSQL Action");
        this.mongoURI = mongoURI;
        this.databaseName = databaseName;
        this.collection = collection;
        this.document = document;

        connect();
    }

    public void connect() {
        System.out.println("Connecting to database...");
        database = MongoClients.create(mongoURI).getDatabase(databaseName);
    }

    public void saveAlert() {
        System.out.println("Saving into database...");
        database.getCollection(collection).insertOne(Document.parse(document));
    }
}
