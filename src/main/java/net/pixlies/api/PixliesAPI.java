package net.pixlies.api;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 * @author MickMMars
 * <h3>This is the main class, just instanciate that in your Plugin.</h3>
 */
public class PixliesAPI {

    private static @Getter PixliesAPI instance;
    private @Getter final MongoCollection<Document> playerCollection;
    private @Getter final MongoCollection<Document> nationCollection;
    private @Getter final Jedis jedis;
    private @Getter final Gson gson;
    private @Getter final Plugin plugin;

    /**
     * @param mongoURI MongoDB URI
     * @param plugin A bukkit {@link org.bukkit.plugin.java.JavaPlugin} instance
     */
    public PixliesAPI(String mongoURI, Plugin plugin, String database, String playerCollectionName, String nationCollectionName) {
        instance = this;
        this.plugin = plugin;

        MongoClientURI clientURI = new MongoClientURI(mongoURI);
        MongoClient mongoClient = new MongoClient(clientURI);

        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        playerCollection = mongoDatabase.getCollection(playerCollectionName);
        nationCollection = mongoDatabase.getCollection(nationCollectionName);

        jedis = new Jedis("localhost");

        gson = new Gson();

    }

    /**
     * @param uuid Uuid of the player
     * @return A profile object of the given playerUUID
     */
    public Profile getProfile(UUID uuid) {
        if (!jedis.exists("profile:" + uuid.toString()))
            jedis.set("profile:" + uuid.toString(), gson.toJson(Profile.get(uuid)));
        return gson.fromJson(jedis.get("profile:" + uuid.toString()), Profile.class);
    }

}
