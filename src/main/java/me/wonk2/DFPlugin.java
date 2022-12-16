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
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;
import me.wonk2.utilities.internals.FileManager;
import java.util.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Bukkit;

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
  public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
  public static HashMap<String, Object[]> functions = new HashMap<>();
  public static Location origin = new Location(null, 0, 0, 0);
  public static JavaPlugin plugin;
  
  @EventHandler
  public void LeftClick (PlayerInteractEvent event){
    int funcStatus;
    HashMap<String, DFValue> localVars = new HashMap<>();
    HashMap<String, LivingEntity[]> targets = new HashMap<>(){{
      put("default", new LivingEntity[]{event.getPlayer()});
    }};
    
    HashMap<String, Object> specifics = new HashMap<>(){{
      put("block", DFUtilities.getEventLoc(event.getPlayer(), event.getClickedBlock()));
      put("item", event.getItem());
      put("cancelled", event.isCancelled());
    }};
    
    if((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) && event.getHand() == EquipmentSlot.HAND)
      CodeExecutor.executeThread(
        new Object[]{
          new Repeat(
            null, null, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("block", Scope.LOCAL), 0, DFType.VAR));
              put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 0d, 50.5d, 0d, 0f, 0f), 1, DFType.LOC));
              put(2, new DFValue(new Location(Bukkit.getWorlds().get(0), 80d, 50.5d, 80d, 0f, 0f), 2, DFType.LOC));
            }},
            new HashMap<>(){}, "REPEAT:Grid", localVars), "Grid", false, localVars
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("voronoi", Scope.LOCAL), 0, DFType.VAR));
              put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
            }},
            new HashMap<>(){{
              put("Cell Edge Type", "Euclidean");
              put("Distance Calculation", "Primary");
            }}, "SETVAR:VoronoiNoise", localVars), "VoronoiNoise", localVars
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("voronoi", Scope.LOCAL), 0, DFType.VAR));
              put(1, new DFValue(new DFVar("voronoi", Scope.LOCAL), 1, DFType.VAR));
              put(2, new DFValue("5", 2, DFType.NUM));
            }},
            new HashMap<>(){}, "SETVAR:x", localVars), "x", localVars
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("block", Scope.LOCAL), 0, DFType.VAR));
              put(1, new DFValue(new DFVar("voronoi", Scope.LOCAL), 1, DFType.VAR));
            }},
            new HashMap<>(){{
              put("Coordinate", "Y");
            }}, "SETVAR:ShiftOnAxis", localVars), "ShiftOnAxis", localVars
          ),
          new GameAction(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:white_wool\"}"), 0, DFType.ITEM));
              put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
            }},
            new HashMap<>(){}, "GAMEACTION:SetBlock", localVars), "SetBlock"
          ),
          new RepeatingBracket()
        }, targets, localVars, event, SelectionType.PLAYER, specifics);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    return true;
  }
  
  @Override
  public void onEnable(){
    plugin = this;
    
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