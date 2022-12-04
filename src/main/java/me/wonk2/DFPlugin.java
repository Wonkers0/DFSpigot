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
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;
import me.wonk2.utilities.internals.FileManager;
import java.util.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Bukkit;

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
  public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
  public static HashMap<String, Object[]> functions = new HashMap<>(){{
    put("wait", new Object[]{
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("wait", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("1", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:+=", null), "+=", null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("wait", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("40", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:>", null), ">", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("wait", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("0", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new Control(
        "selection", null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){{
          put("Time Unit", "Ticks");
        }}, "CONTROL:Wait", null), "Wait"
      ),
      new ClosingBracket(),
      new PlayerAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("Y", Scope.LOCAL), 0, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Text Value Merging", "No spaces");
        }}, "PLAYERACTION:ActionBar", null), "ActionBar"
      ),
    });
    put("Sand", new Object[]{
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("Y", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("88", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:<", null), "<", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dirt", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:sand\"}"), 1, DFType.ITEM));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:sand\"}"), 0, DFType.ITEM));
          put(1, new DFValue(new DFVar("pos", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "GAMEACTION:SetBlock", null), "SetBlock"
      ),
      new ClosingBracket(),
      new PlayerAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("Y", Scope.LOCAL), 0, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Text Value Merging", "No spaces");
        }}, "PLAYERACTION:ActionBar", null), "ActionBar"
      ),
    });
    put("Water", new Object[]{
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("Y", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("85", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:<", null), "<", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("waterPos", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("pos", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("85", 2, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Y");
        }}, "SETVAR:SetCoord", null), "SetCoord", null
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:water_bucket\"}"), 0, DFType.ITEM));
          put(1, new DFValue(new DFVar("waterPos", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("pos", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){}, "GAMEACTION:SetRegion", null), "SetRegion"
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dirt", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:sand\"}"), 1, DFType.ITEM));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new ClosingBracket(),
      new PlayerAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("Y", Scope.LOCAL), 0, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Text Value Merging", "No spaces");
        }}, "PLAYERACTION:ActionBar", null), "ActionBar"
      ),
    });
    put("clear", new Object[]{
      new Repeat(
        null, null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("gridPos", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 0.5d, 49.5d, 0.5d, 0f, 0f), 1, DFType.LOC));
          put(2, new DFValue(new Location(Bukkit.getWorlds().get(0), 500d, 49.5d, 500d, 0f, 0f), 2, DFType.LOC));
        }},
        new HashMap<>(){}, "REPEAT:Grid", null), "Grid", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("pos1", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("gridPos", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("0", 2, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Y");
        }}, "SETVAR:SetCoord", null), "SetCoord", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("pos2", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("gridPos", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("255", 2, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Y");
        }}, "SETVAR:SetCoord", null), "SetCoord", null
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(1, new DFValue(new DFVar("pos1", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("pos2", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){}, "GAMEACTION:SetRegion", null), "SetRegion"
      ),
      new CallFunction(
        "wait"
      ),
      new RepeatingBracket(),
      new PlayerAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("Y", Scope.LOCAL), 0, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Text Value Merging", "No spaces");
        }}, "PLAYERACTION:ActionBar", null), "ActionBar"
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
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("seed", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue("0", 1, DFType.NUM));
            put(2, new DFValue("999999", 2, DFType.NUM));
          }},
          new HashMap<>(){{
            put("Rounding Mode", "Whole number");
          }}, "SETVAR:RandomNumber", localVars), "RandomNumber", localVars
        ),
        new PlayerAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(ChatColor.of("#ccffe7") + "Generating on seed " + ChatColor.of("#ccecff") + "%var(seed)", 0, DFType.TXT));
          }},
          new HashMap<>(){{
            put("Text Value Merging", "Add spaces");
            put("Alignment Mode", "Regular");
          }}, "PLAYERACTION:SendMessage", localVars), "SendMessage"
        ),
        new PlayerAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "PLAYERACTION:SpectatorMode", localVars), "SpectatorMode"
        ),
        new Repeat(
          null, null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("gridPos", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 0d, 49.5d, 0d, 0f, 0f), 1, DFType.LOC));
            put(2, new DFValue(new Location(Bukkit.getWorlds().get(0), 500d, 49.5d, 500d, 0f, 0f), 2, DFType.LOC));
          }},
          new HashMap<>(){}, "REPEAT:Grid", localVars), "Grid", false, localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("perlin", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("gridPos", Scope.LOCAL), 1, DFType.VAR));
            put(2, new DFValue("0.75", 2, DFType.NUM));
            put(3, new DFValue("2", 3, DFType.NUM));
            put(4, new DFValue("0.75", 4, DFType.NUM));
            put(5, new DFValue("0.75", 5, DFType.NUM));
            put(6, new DFValue(new DFVar("seed", Scope.LOCAL), 6, DFType.VAR));
          }},
          new HashMap<>(){{
            put("Fractal Type", "Brownian");
          }}, "SETVAR:PerlinNoise", localVars), "PerlinNoise", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("Y", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("perlin", Scope.LOCAL), 1, DFType.VAR));
            put(2, new DFValue("65", 2, DFType.NUM));
          }},
          new HashMap<>(){}, "SETVAR:x", localVars), "x", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("pos", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("gridPos", Scope.LOCAL), 1, DFType.VAR));
            put(2, new DFValue(new DFVar("Y", Scope.LOCAL), 2, DFType.VAR));
          }},
          new HashMap<>(){{
            put("Coordinate", "Y");
          }}, "SETVAR:ShiftOnAxis", localVars), "ShiftOnAxis", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("Y", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("pos", Scope.LOCAL), 1, DFType.VAR));
          }},
          new HashMap<>(){{
            put("Coordinate Type", "Plot coordinate");
            put("Coordinate", "Y");
          }}, "SETVAR:GetCoord", localVars), "GetCoord", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("dirtPos", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("pos", Scope.LOCAL), 1, DFType.VAR));
            put(2, new DFValue("-1", 2, DFType.NUM));
          }},
          new HashMap<>(){{
            put("Coordinate", "Y");
          }}, "SETVAR:ShiftOnAxis", localVars), "ShiftOnAxis", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("dirtPos2", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("dirtPos", Scope.LOCAL), 1, DFType.VAR));
            put(2, new DFValue("-3", 2, DFType.NUM));
          }},
          new HashMap<>(){{
            put("Coordinate", "Y");
          }}, "SETVAR:ShiftOnAxis", localVars), "ShiftOnAxis", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("stonePos", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("dirtPos2", Scope.LOCAL), 1, DFType.VAR));
            put(2, new DFValue("-20", 2, DFType.NUM));
          }},
          new HashMap<>(){{
            put("Coordinate", "Y");
          }}, "SETVAR:ShiftOnAxis", localVars), "ShiftOnAxis", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("darkStonePos", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("stonePos", Scope.LOCAL), 1, DFType.VAR));
            put(2, new DFValue("1", 2, DFType.NUM));
          }},
          new HashMap<>(){{
            put("Coordinate Type", "Plot coordinate");
            put("Coordinate", "Y");
          }}, "SETVAR:SetCoord", localVars), "SetCoord", localVars
        ),
        new GameAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:grass_block\"}"), 0, DFType.ITEM));
            put(1, new DFValue(new DFVar("pos", Scope.LOCAL), 1, DFType.VAR));
          }},
          new HashMap<>(){}, "GAMEACTION:SetBlock", localVars), "SetBlock"
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("dirt", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:dirt\"}"), 1, DFType.ITEM));
          }},
          new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
        ),
        new CallFunction(
          "Sand"
        ),
        new CallFunction(
          "Water"
        ),
        new GameAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("dirt", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("dirtPos", Scope.LOCAL), 1, DFType.VAR));
            put(2, new DFValue(new DFVar("dirtPos2", Scope.LOCAL), 2, DFType.VAR));
          }},
          new HashMap<>(){}, "GAMEACTION:SetRegion", localVars), "SetRegion"
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("stone", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:stone\"}"), 1, DFType.ITEM));
            put(2, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:stone\"}"), 2, DFType.ITEM));
            put(3, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:stone\"}"), 3, DFType.ITEM));
            put(4, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:stone\"}"), 4, DFType.ITEM));
            put(5, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:stone\"}"), 5, DFType.ITEM));
          }},
          new HashMap<>(){}, "SETVAR:RandomValue", localVars), "RandomValue", localVars
        ),
        new GameAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("stone", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("dirtPos2", Scope.LOCAL), 1, DFType.VAR));
            put(2, new DFValue(new DFVar("stonePos", Scope.LOCAL), 2, DFType.VAR));
          }},
          new HashMap<>(){}, "GAMEACTION:SetRegion", localVars), "SetRegion"
        ),
        new GameAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:blackstone\"}"), 0, DFType.ITEM));
            put(1, new DFValue(new DFVar("stonePos", Scope.LOCAL), 1, DFType.VAR));
            put(2, new DFValue(new DFVar("darkStonePos", Scope.LOCAL), 2, DFType.VAR));
          }},
          new HashMap<>(){}, "GAMEACTION:SetRegion", localVars), "SetRegion"
        ),
        new CallFunction(
          "wait"
        ),
        new RepeatingBracket(),
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