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
import org.bukkit.event.player.PlayerJoinEvent;

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
  public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
  public static Location origin = new Location(null, 0, 0, 0);
  public static JavaPlugin plugin;
  public static Logger logger;
  public static World world = Bukkit.getWorld("world");
  public static HashMap<String, Object[]> functions = new HashMap<>();
  
  @EventHandler
  public void Join (PlayerJoinEvent event){
    int funcStatus;
    HashMap<String, DFValue> localVars = new HashMap<>();
    HashMap<String, Entity[]> targets = new HashMap<>(){{
      put("default", new Entity[]{event.getPlayer()});
    }};
    
    HashMap<String, Object> specifics = new HashMap<>(){{
      put("item", new ItemStack(Material.AIR));
    }};
    
    CodeExecutor.executeThread(
      new Object[]{
        new IfPlayer(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue("Wonk0", 0, DFType.TXT));
          }},
          new HashMap<>(){}, "IFPLAYER:NameEquals", localVars), "NameEquals", false
        ),
        new SelectObject(
          new ParamManager(
            new HashMap<>(){},
            new HashMap<>(){}, "SELECTOBJ:AllPlayers", localVars), "AllPlayers", "null", false, localVars, specifics
        ),
        new Repeat(
          targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new Location(world, 9.5d, 79.5d, 6.5d, 0f, 0f), 0, DFType.LOC));
          }},
          new HashMap<>(){{
            put("Shape", "Sphere");
          }}, "REPEAT:While", localVars), "While", "PIsNear", false, localVars, specifics
        ),
        new Control(
          "selection", targets, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){{
            put("Time Unit", "Ticks");
          }}, "CONTROL:Wait", localVars), "Wait"
        ),
        new PlayerAction(
          "allplayers", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue("balls", 0, DFType.TXT));
          }},
          new HashMap<>(){{
            put("Text Value Merging", "No spaces");
          }}, "PLAYERACTION:ActionBar", localVars), "ActionBar"
        ),
        new RepeatingBracket(),
        new ClosingBracket(),
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