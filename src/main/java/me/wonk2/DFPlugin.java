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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
  public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
  public static Location origin = new Location(null, 0, 0, 0);
  public static JavaPlugin plugin;
  public static Logger logger;
  public static World world = Bukkit.getWorld("world");
  public static HashMap<String, Object[]> functions = new HashMap<>();
  
  @EventHandler
  public void SwapHands (PlayerSwapHandItemsEvent event){
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
        new GameAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "GAMEACTION:CancelEvent", localVars), "CancelEvent"
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("t", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue("99", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("t", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue("1", 1, DFType.NUM));
            put(2, new DFValue("5", 2, DFType.NUM));
          }},
          new HashMap<>(){}, "SETVAR:ClampNumber", localVars), "ClampNumber", localVars
        ),
        new PlayerAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("t", Scope.LOCAL), 0, DFType.VAR));
          }},
          new HashMap<>(){{
            put("Text Value Merging", "Add spaces");
            put("Alignment Mode", "Regular");
          }}, "PLAYERACTION:SendMessage", localVars), "SendMessage"
        ),
      }, targets, localVars, event, SelectionType.PLAYER, specifics);
  }
  
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
          new SetVariable(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("t", Scope.LOCAL), 0, DFType.VAR));
              put(1, new DFValue("99", 1, DFType.NUM));
              put(2, new DFValue("1", 2, DFType.NUM));
              put(3, new DFValue("5", 3, DFType.NUM));
            }},
            new HashMap<>(){}, "SETVAR:ClampNumber", localVars), "ClampNumber", localVars
          ),
          new PlayerAction(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("t", Scope.LOCAL), 0, DFType.VAR));
            }},
            new HashMap<>(){{
              put("Text Value Merging", "Add spaces");
              put("Alignment Mode", "Regular");
            }}, "PLAYERACTION:SendMessage", localVars), "SendMessage"
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