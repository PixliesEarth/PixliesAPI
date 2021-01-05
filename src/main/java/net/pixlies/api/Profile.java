package net.pixlies.api;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.Document;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author MickMMars
 */

@Data
@AllArgsConstructor
public class Profile {

    private static PixliesAPI instance = PixliesAPI.getInstance();

    private String uniqueId;
    private String discord;
    private boolean inNation;
    private double balance;
    private List<String> receipts;
    private int playTime;
    private int elo;
    private String marriagePartner;
    private List<String> marriageRequests;
    private Map<String, String> relations;
    private double energy;
    private String nationId;
    private List<String> invites;
    private List<String> homes;
    private boolean scoreboard;
    private String nationRank;
    private List<String> knownUsernames;
    private String nickname;
    private String messageSound;
    private boolean pingSound;
    private String chatColor;
    private int boosts;
    private String lastAt;
    private double pixliecoins;
    private Map<String, Map<String, String>> timers;
    private String favoriteColour;
    private String boardType;
    private String lang;
    private List<String> blocked;
    private Map<String, String> punishments;
    private Map<String, Object> extras;

    /**
     * @param uuid {@link UUID} of the {@link Player}
     * @return Profile object from the MongoDB database
     */
    public static Profile get(UUID uuid) {
        Document profile = new Document("uniqueId", uuid.toString());
        Document found = instance.getPlayerCollection().find(profile).first();
        Profile data;
        if (found == null) {
            profile.append("discord", "NONE");
            profile.append("inNation", false);
            profile.append("balance", 4000.0);
            profile.append("receipts", new ArrayList<>());
            profile.append("playTime", 0);
            profile.append("elo", 0);
            profile.append("marriagePartner", "NONE");
            profile.append("marriageRequests", new ArrayList<>());
            profile.append("relations", new HashMap<>());
            profile.append("energy", 10.0);
            profile.append("nationId", "NONE");
            profile.append("invites", new ArrayList<>());
            profile.append("homes", new ArrayList<>());
            profile.append("scoreboard", true);
            profile.append("nationRank", "NONE");
            profile.append("knownUsernames", new ArrayList<>());
            profile.append("nickname", "NONE");
            profile.append("messageSound", Sound.BLOCK_NOTE_BLOCK_PLING.name());
            profile.append("pingSound", true);
            profile.append("chatColor", "f");
            profile.append("boosts", 0);
            profile.append("lastAt", "NONE");
            profile.append("pixliecoins", 0D);
            profile.append("timers", new HashMap<>());
            profile.append("favoriteColour", "§3");
            profile.append("boardType", scoreboardType.STANDARD.name());
            profile.append("lang", "ENG");
            profile.append("blocked", new ArrayList<>());
            profile.append("banned", false);
            profile.append("extras", new HashMap<>());
            instance.getPlayerCollection().insertOne(profile);
            data = new Profile(uuid.toString(), "NONE",false, 4000, new ArrayList<>(), 0, 0,"NONE", new ArrayList<>(), new HashMap<>(), 10.0, "NONE", new ArrayList<>(), new ArrayList<>(), true, "NONE", new ArrayList<>(), "NONE", Sound.BLOCK_NOTE_BLOCK_PLING.name(), true, "f",0, "NONE", 0D, new HashMap<>(), "§3", scoreboardType.STANDARD.name(), "ENG", new ArrayList<>(), new HashMap<>(), new HashMap<>());
        } else {
            data = new Gson().fromJson(found.toJson(), Profile.class);
        }
        return data;
    }


    /**
     * Save the profile in the MongoDatabase
     */
    public void backup() {
        Document profile = new Document("uniqueId", uniqueId);
        Document found = instance.getPlayerCollection().find(profile).first();
        if (found == null) return;
        profile.append("discord", discord);
        profile.append("inNation", inNation);
        profile.append("balance", balance);
        profile.append("receipts", receipts);
        profile.append("playTime", playTime);
        profile.append("elo", elo);
        profile.append("marriagePartner", marriagePartner);
        profile.append("marriageRequests", marriageRequests);
        profile.append("relations", relations);
        profile.append("energy", energy);
        profile.append("nationId", nationId);
        profile.append("invites", invites);
        profile.append("homes", homes);
        profile.append("scoreboard", scoreboard);
        profile.append("nationRank", nationRank);
        profile.append("knownUsernames", knownUsernames);
        profile.append("nickname", nickname);
        profile.append("messageSound", messageSound);
        profile.append("pingSound", pingSound);
        profile.append("chatColor", chatColor);
        profile.append("boosts", boosts);
        profile.append("lastAt", lastAt);
        profile.append("pixliecoins", pixliecoins);
        profile.append("timers", timers);
        profile.append("favoriteColour", favoriteColour);
        profile.append("boardType", boardType);
        profile.append("lang", lang);
        profile.append("blocked", blocked);
        profile.append("punishments", punishments);
        profile.append("extras", extras);
        instance.getPlayerCollection().replaceOne(found, profile);
    }

    /**
     * Save the Profile in Redis cache
     */
    public void save() {
        instance.getJedis().set("profile:" + uniqueId, instance.getGson().toJson(this));
    }

    /**
     * @return nickname of the {@link Profile}
     */
    public String getDisplayName() {
        if (nickname.equalsIgnoreCase("NONE") || nickname.equalsIgnoreCase(""))
            return knownUsernames.get(knownUsernames.size() -1);
        return nickname;
    }

    /**
     * @return get player as {@link OfflinePlayer}
     */
    public OfflinePlayer getAsOfflinePlayer() {
        return instance.getPlugin().getServer().getOfflinePlayer(UUID.fromString(uniqueId));
    }

    /**
     * @return is nation leader
     */
    public boolean isLeader() {
        return nationRank.equalsIgnoreCase("leader");
    }

    /**
     * Remove from nation.
     */
    public void removeFromNation() {
        if (!isInNation()) return;
        this.inNation = false;
        this.nationId = "NONE";
        save();
    }

    /**
     * Withdraw money from the Profile
     * @param amount transaction amount
     * @param reason reason e.g. "Tax from nation"
     * @return if transaction was successful
     */
    public boolean withdrawMoney(double amount, String reason) {
        if (balance - amount < 0)
            return false;
        this.balance = this.balance - amount;
        String receipt = Receipt.create(amount, true, reason);
        if (this.receipts == null)
            this.receipts = new ArrayList<>();
        this.receipts.add(receipt);
        save();
        return true;
    }

    /**
     * @param amount amount of transaction
     * @param reason reason e.g. "Gift from Eliza"
     */
    public void depositMoney(double amount, String reason) {
        this.balance = this.balance + amount;
        String receipt = Receipt.create(amount, false, reason);
        if (this.receipts == null)
            this.receipts = new ArrayList<>();
        receipts.add(receipt);
        save();
    }

    /**
     * @return if {@link Player} is online.
     */
    public boolean isOnline() {
        return instance.getPlugin().getServer().getPlayer(UUID.fromString(uniqueId)) != null;
    }

    /**
     * @return if {@link Profile} is married.
     */
    public boolean isMarried() { return !marriagePartner.equals("NONE"); }

    /**
     * @param uuid target {@link UUID}
     * @return if two players are related.
     */
    public boolean areRelated(UUID uuid) {
        if (marriagePartner.equals(uuid.toString()))
            return true;
        if (relations.get(uuid.toString()) == null)
            return false;
        return relations.get(uuid.toString()) == null || !relations.get(uuid.toString()).startsWith("REQ=");
    }

    /**
     * @return get as {@link Player} object.
     */
    public Player getAsPlayer() {
        return getAsOfflinePlayer().isOnline() ? getAsOfflinePlayer().getPlayer() : null;
    }

    /**
     * @return if Discord and Minecraft are synced
     */
    public boolean discordIsSynced() {
        return !discord.equalsIgnoreCase("NONE");
    }

    /**
     * @param discordId Discord Id (e.g. "280798475946426369")
     * @return {@link Profile} that belongs to the Discord Id
     */
    public static Profile getByDiscord(String discordId) {
        Document found = instance.getPlayerCollection().find(new Document("discord", discordId)).first();
        if (found != null)
            return new Gson().fromJson(found.toJson(), Profile.class);
        return null;
    }

    /**
     * @param nickname Nickname (e.g. "Minecrafter3000")
     * @return {@link Profile} that belongs to the nickname
     */
    public static Profile getByNickname(String nickname) {
        Document found = instance.getPlayerCollection().find(new Document("nickname", nickname)).first();
        if (found != null)
            return new Gson().fromJson(found.toJson(), Profile.class);
        return null;
    }

    /**
     * @return Profiles of all online players.
     */
    public static Map<UUID, Profile> onlineProfiles() {
        Map<UUID, Profile> returner = new HashMap<>();
        for (Player player : instance.getPlugin().getServer().getOnlinePlayers())
            returner.put(player.getUniqueId(), instance.getProfile(player.getUniqueId()));
        return returner;
    }

    /**
     * Scoreboard types
     */
    public enum scoreboardType {

        STANDARD,
        COMPACT

    }

}
