# 💎 [DFSpigot](https://dfspigot.wonk2.repl.co)
A tool for translating DiamondFire templates into Java Spigot Dependency Code.&nbsp;

For contributing, visit [CONTRIBUTING.md](https://github.com/Wonkers0/DFSpigot/blob/main/guides/CONTRIBUTING.md) and [docs.md](https://github.com/Wonkers0/DFSpigot/blob/main/guides/docs.md)

Here's an *example* of [this template](https://dfonline.dev/edit/?template=qbzlvlpo1zjpfvwi28xf) as a spigot plugin!
```java
package me.wonk2;

// imports hidden

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
  public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
  public static HashMap<String, Object[]> functions = new HashMap<>(){{
    put("initSpigotif", new Object[]{
      new PlayerAction(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue("§a☺ " + ChatColor.of("#c5c5c5") + "Hello! Spigotifier is a tool to translate your §bDiamondFire " + ChatColor.of("#c5c5c5") + "games to spigot plugins!", 0, DFType.TXT));
            put(1, new DFValue(ChatColor.of("#c5c5c5") + "This means that you can play your games §lwithout §cLagSlayer, §eEntity Limits " + ChatColor.of("#c5c5c5") + "or §3plot size limits!", 1, DFType.TXT));
            put(2, new DFValue("30", 2, DFType.NUM));
          }}, 
          new HashMap<>(){}, "PLAYERACTION:SendMessageSeq", null), "SendMessageSeq"
      ),
      new PlayerAction(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFSound("Experience Orb Pickup", 2f, 2f), 0, DFType.SND));
            put(1, new DFValue(new DFSound("Experience Orb Pickup", 1f, 2f), 1, DFType.SND));
            put(2, new DFValue("30", 2, DFType.NUM));
          }}, 
          new HashMap<>(){}, "PLAYERACTION:PlaySoundSeq", null), "PlaySoundSeq"
      ),
      });
  }};
  public static Location origin = new Location(null, 0, 0, 0);
  public static JavaPlugin plugin;
  
  @EventHandler
  public void Join (PlayerJoinEvent event){
    int funcStatus;
    HashMap<String, DFValue> localVars = new HashMap<>();
    HashMap<String, LivingEntity[]> targets = new HashMap<>(){{
      put("default", new LivingEntity[]{event.getPlayer()});
    }};
    
    HashMap<String, Object> specifics = new HashMap<>(){{
      put("cancelled", false);
      put("item", new ItemStack(Material.AIR));
    }};
    
    CodeExecutor.executeThread(
      new Object[]{
        new CallFunction(
          "initSpigotif"
        ),
    }, targets, localVars, null, SelectionType.PLAYER);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    return true;
  }
  
  @Override
  public void onEnable(){
    plugin = this;
    
    DFListeners.updateArgInfo();
    DFVar.deserializeSavedVars();
    DFUtilities.playerConfig = new FileManager(plugin, "playerData.yml");
    getServer().getPluginManager().registerEvents(this, this);
    getServer().getPluginManager().registerEvents(new DFListeners(), this);
    this.getCommand("dfspigot").setExecutor(new DFListeners());
  }
  @Override
  public void onDisable(){
    DFVar.serializeSavedVars();
  }
}
```

## 📚 Utility Classes
The **java classes** found in this repository need to be imported to provide the methods that your plugin needs to run. But first, read the sections below to learn how to generate your DFPlugin.java class. This class will communicate with the other classes found in this repo to move your games from DiamondFire to a *server of your own*. 👍

// TODO: Lengthen & Improve README.md
