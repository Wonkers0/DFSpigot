package me.wonk2;

import me.wonk2.utilities.*;
import me.wonk2.utilities.enums.*;
import me.wonk2.utilities.values.*;
import me.wonk2.utilities.actions.*;
import me.wonk2.utilities.actions.pointerclasses.brackets.*;
import me.wonk2.utilities.internals.CodeExecutor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.*;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.event.player.PlayerInteractEvent;

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
  public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
  public static Location origin = new Location(null, 0, 0, 0);
  public static JavaPlugin plugin;
  public static Logger logger;
  public static World world = Bukkit.getWorld("world");
  public static HashMap<String, Object[]> functions = new HashMap<>();

  @EventHandler
  public void LeftClick (PlayerInteractEvent event){
    int funcStatus;
    HashMap<String, DFValue> localVars = new HashMap<>();
    HashMap<String, Entity[]> targets = new HashMap<>(){{
      put("default", new Entity[]{event.getPlayer()});
    }};

    HashMap<String, Object> specifics = new HashMap<>(){{
      put("block", DFUtilities.getEventLoc(event.getPlayer(), event.getClickedBlock()));
      put("item", event.getItem());
    }};

    if((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) && event.getHand() == EquipmentSlot.HAND)
      CodeExecutor.executeThread(
        new Object[]{
          new GameAction(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new GameValue(Value.Location, "default"), 0, DFType.GAMEVAL));
                put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3337,id:\"minecraft:prismarine\",tag:{CustomModelData:0,HideFlags:-1,display:{Lore:['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"gray\",\"text\":\"Used to repeat the code inside it.\"}],\"text\":\"\"}','{\"text\":\"\"}','{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"white\",\"text\":\"Example:\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"aqua\",\"text\":\"» \"},{\"italic\":false,\"color\":\"gray\",\"text\":\"Repeat code forever\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"aqua\",\"text\":\"» \"},{\"italic\":false,\"color\":\"gray\",\"text\":\"Repeat code a certain number\"}],\"text\":\"\"}','{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"gray\",\"text\":\"of times\"}],\"text\":\"\"}','{\"extra\":[{\"italic\":false,\"color\":\"aqua\",\"text\":\"» \"},{\"italic\":false,\"color\":\"gray\",\"text\":\"Repeat code until a certain\"}],\"text\":\"\"}','{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"gray\",\"text\":\"condition is met\"}],\"text\":\"\"}'],Name:'{\"italic\":false,\"color\":\"green\",\"text\":\"Repeat\"}'}}}"), 1, DFType.ITEM));
              }},
              new HashMap<>(){}, "GAMEACTION:SpawnItemDisplay", localVars), "SpawnItemDisplay"
          ),
      }, targets, localVars, event, SelectionType.PLAYER, specifics);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    return true;
  }

  @Override
  public void onEnable(){
    plugin = this;
    logger = Bukkit.getLogger();

    DFUtilities.init();
    getServer().getPluginManager().registerEvents(this, this);
    getServer().getPluginManager().registerEvents(new DFListeners(), this);
    this.getCommand("dfspigot").setExecutor(new DFListeners());
  }
  @Override
  public void onDisable(){
    DFVar.serializeSavedVars();
  }
}