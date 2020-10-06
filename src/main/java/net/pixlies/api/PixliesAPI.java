package net.pixlies.api;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.plugin.Plugin;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.UUID;

public class PixliesAPI {

    private static @Getter PixliesAPI instance;
    private @Getter MongoCollection<Document> playerCollection;
    private @Getter MongoCollection<Document> nationCollection;
    private @Getter Config redissonConfig;
    private @Getter RedissonClient redissonClient;
    private @Getter Gson gson;
    private @Getter Plugin plugin;

    public PixliesAPI(String mongoURI, Plugin plugin) {
        instance = this;
        this.plugin = plugin;

        MongoClientURI clientURI = new MongoClientURI(mongoURI);
        MongoClient mongoClient = new MongoClient(clientURI);

        MongoDatabase mongoDatabase = mongoClient.getDatabase("admin");
        playerCollection = mongoDatabase.getCollection("users");
        nationCollection = mongoDatabase.getCollection("nations");

        redissonConfig = new Config();
        redissonConfig.useSingleServer().setAddress("redis://127.0.0.1:6379");
        redissonClient = Redisson.create(redissonConfig);

        gson = new Gson();

    }

    /**
     * @param uuid Uuid of the player
     * @return A profile object of the given playerUUID
     */
    public Profile getProfile(UUID uuid) {
/*        if (utilLists.profiles.get(uuid) == null)
            utilLists.profiles.put(uuid, Profile.get(uuid));*/
        if (!redissonClient.getBucket("profile:" + uuid.toString()).isExists())
            redissonClient.getBucket("profile:" + uuid.toString()).set(gson.toJson(Profile.get(uuid)));
        return gson.fromJson((String) redissonClient.getBucket("profile:" + uuid.toString()).get(), Profile.class);
    }

}
