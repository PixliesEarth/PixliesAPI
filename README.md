# PixliesAPI - hook into Pixlies with your plugin
## Basic usage
Implementation
```java
public class myPlugin extends JavaPlugin {
    
    private PixliesAPI api;

    @Override
    public void onEnable() {
        api = new PixliesAPI("mongodb://mongodb0.example.com:27017", this);
    }
    
    public PixliesAPI getApi() {
        return api;
    }

}
```
get profile
```java
public class myPlugin extends JavaPlugin implements Listener {
    
    private PixliesAPI api;

    @Override
    public void onEnable() {
        api = new PixliesAPI("mongodb://mongodb0.example.com:27017", this);
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Profile profile = api.getProfile(event.getPlayer().getUUID());
        profile.depositMoney(2000, "Free money from server");
    }
    
    public PixliesAPI getApi() {
        return api;
    }

}
```
