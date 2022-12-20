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
import org.bukkit.Bukkit;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
  public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
  public static Location origin = new Location(null, 0, 0, 0);
  public static JavaPlugin plugin;
  public static Logger logger;
  public static HashMap<String, Object[]> functions = new HashMap<>(){{
    put("terrainGen", new Object[]{
      new Control(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("10", 0, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Time Unit", "Ticks");
        }}, "CONTROL:Wait", null), "Wait"
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("seed", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("%random(-10000,10000)", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("start", Scope.GLOBAL), 0, DFType.VAR));
          put(1, new DFValue(new GameValue(Value.Timestamp, "default"), 1, DFType.GAMEVAL));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new Location(Bukkit.getWorlds().get(0), 225d, 0d, 225d, 0f, 0f), 0, DFType.LOC));
          put(1, new DFValue("menuStand", 1, DFType.TXT));
        }},
        new HashMap<>(){{
          put("Visibility", "Invisible");
        }}, "GAMEACTION:SpawnArmorStand", null), "SpawnArmorStand"
      ),
      new EntityAction(
        "selection", null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){{
          put("Name Tag Visible", "Disable");
        }}, "ENTITYACTION:SetNameVisible", null), "SetNameVisible", null
      ),
      new SelectObject(
        new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "SELECTOBJ:LastEntity", null), "LastEntity", "null", false, null, null
      ),
      new StartProcess(
        "menuStand", StartProcess.TargetMode.COPY_SELECTION, StartProcess.VarStorage.ALIAS
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 225d, 0d, 225d, 0f, 0f), 1, DFType.LOC));
          put(2, new DFValue(new Location(Bukkit.getWorlds().get(0), 75d, 0d, 75d, 0f, 0f), 2, DFType.LOC));
        }},
        new HashMap<>(){}, "GAMEACTION:SetRegion", null), "SetRegion"
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 225d, 255d, 225d, 0f, 0f), 1, DFType.LOC));
          put(2, new DFValue(new Location(Bukkit.getWorlds().get(0), 75d, 255d, 75d, 0f, 0f), 2, DFType.LOC));
        }},
        new HashMap<>(){}, "GAMEACTION:SetRegion", null), "SetRegion"
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("mapGen", Scope.GLOBAL), 0, DFType.VAR));
          put(1, new DFValue("1", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new Repeat(
        null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("y", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("254", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "REPEAT:Multiple", null), "Multiple", false, null
      ),
      new Control(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("7", 0, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Time Unit", "Ticks");
        }}, "CONTROL:Wait", null), "Wait"
      ),
      new PlayerAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("§aIn loop", 0, DFType.TXT));
        }},
        new HashMap<>(){{
          put("Text Value Merging", "Add spaces");
          put("Alignment Mode", "Regular");
        }}, "PLAYERACTION:SendMessage", null), "SendMessage"
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("progress", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("y", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("254", 2, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Division Mode", "Default");
        }}, "SETVAR:/", null), "/", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("progress", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("progress", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("100", 2, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:x", null), "x", null
      ),
      new PlayerAction(
        "allplayers", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(ChatColor.of("#6fe557") + "♯ " + ChatColor.of("#4ec936") + "Generating... " + ChatColor.of("#489339") + "(%var(progress)%)", 0, DFType.TXT));
          put(1, new DFValue(new DFVar("progress", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Sky Effect", "None");
          put("Bar Style", "Solid");
          put("Bar Color", "Green");
        }}, "PLAYERACTION:SetBossBar", null), "SetBossBar"
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("loc1", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 75d, 50d, 75d, 0f, 0f), 1, DFType.LOC));
          put(2, new DFValue(new DFVar("y", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Y");
        }}, "SETVAR:SetCoord", null), "SetCoord", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("loc2", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 225d, 50d, 225d, 0f, 0f), 1, DFType.LOC));
          put(2, new DFValue(new DFVar("y", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Y");
        }}, "SETVAR:SetCoord", null), "SetCoord", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("center", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 150d, 50d, 150d, 0f, 0f), 1, DFType.LOC));
          put(2, new DFValue(new DFVar("y", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Y");
        }}, "SETVAR:SetCoord", null), "SetCoord", null
      ),
      new CallFunction(
        "clearLayer"
      ),
      new PlayerAction(
        "allplayers", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFSound("Seagrass Place", 1f, 2f), 0, DFType.SND));
        }},
        new HashMap<>(){{
          put("Sound Source", "Blocks");
        }}, "PLAYERACTION:PlaySound", null), "PlaySound"
      ),
      new Repeat(
        null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("block", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("loc1", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("loc2", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){}, "REPEAT:Grid", null), "Grid", false, null
      ),
      new PlayerAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("§eIn second loop", 0, DFType.TXT));
        }},
        new HashMap<>(){{
          put("Text Value Merging", "Add spaces");
          put("Alignment Mode", "Regular");
        }}, "PLAYERACTION:SendMessage", null), "SendMessage"
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("i", Scope.LOCAL), 0, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:+=", null), "+=", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("noiseLoc", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("seed", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Coordinate", "X");
        }}, "SETVAR:ShiftOnAxis", null), "ShiftOnAxis", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("perlin", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("noiseLoc", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Fractal Type", "Brownian");
        }}, "SETVAR:PerlinNoise", null), "PerlinNoise", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("voronoi", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("noiseLoc", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Cell Edge Type", "Natural");
        }}, "SETVAR:VoronoiNoise", null), "VoronoiNoise", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("worley", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("noiseLoc", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Cell Edge Type", "Euclidean");
          put("Distance Calculation", "Primary");
        }}, "SETVAR:WorleyNoise", null), "WorleyNoise", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("worley", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("worley", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("2", 2, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:x", null), "x", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("noises", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("perlin", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("voronoi", Scope.LOCAL), 2, DFType.VAR));
          put(3, new DFValue(new DFVar("worley", Scope.LOCAL), 3, DFType.VAR));
          put(4, new DFValue("2", 4, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:x", null), "x", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dist", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("center", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("block", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Distance Type", "Distance 2D (X/Z)");
        }}, "SETVAR:Distance", null), "Distance", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dist", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("50", 1, DFType.NUM));
          put(2, new DFValue(new DFVar("dist", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:-", null), "-", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dist", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("dist", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("0.15", 2, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:x", null), "x", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("noises", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("noises", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("dist", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:x", null), "x", null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("noises", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("0.4", 1, DFType.NUM));
          put(2, new DFValue("0.7", 2, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:InRange", null), "InRange", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("mat", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:coarse_dirt\"}"), 1, DFType.ITEM));
          put(2, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:dirt\"}"), 2, DFType.ITEM));
        }},
        new HashMap<>(){}, "SETVAR:RandomValue", null), "RandomValue", null
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("mat", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "GAMEACTION:SetBlock", null), "SetBlock"
      ),
      new ClosingBracket(),
      new PlayerAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("§cEnd of 2nd loop", 0, DFType.TXT));
        }},
        new HashMap<>(){{
          put("Text Value Merging", "Add spaces");
          put("Alignment Mode", "Regular");
        }}, "PLAYERACTION:SendMessage", null), "SendMessage"
      ),
      new RepeatingBracket(),
      new PlayerAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("§4End of 1st loop", 0, DFType.TXT));
        }},
        new HashMap<>(){{
          put("Text Value Merging", "Add spaces");
          put("Alignment Mode", "Regular");
        }}, "PLAYERACTION:SendMessage", null), "SendMessage"
      ),
      new RepeatingBracket(),
      new PlayerAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("§2Outside loops", 0, DFType.TXT));
        }},
        new HashMap<>(){{
          put("Text Value Merging", "Add spaces");
          put("Alignment Mode", "Regular");
        }}, "PLAYERACTION:SendMessage", null), "SendMessage"
      ),
      new CallFunction(
        "colorTerrain"
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("mapGen", Scope.GLOBAL), 0, DFType.VAR));
          put(1, new DFValue("0", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new PlayerAction(
        "allplayers", null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){}, "PLAYERACTION:CreativeMode", null), "CreativeMode"
      ),
    });
    put("clearLayer", new Object[]{
      new Repeat(
        null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("locClear", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("loc1", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("loc2", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){}, "REPEAT:Grid", null), "Grid", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("iWait", Scope.LOCAL), 0, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:+=", null), "+=", null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("iWait", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("10000", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:>=", null), ">=", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("iWait", Scope.LOCAL), 0, DFType.VAR));
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
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(1, new DFValue(new DFVar("locClear", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "GAMEACTION:SetBlock", null), "SetBlock"
      ),
      new RepeatingBracket(),
    });
    put("colorTerrain", new Object[]{
      new PlayerAction(
        "allplayers", null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){}, "PLAYERACTION:RemoveBossBar", null), "RemoveBossBar"
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("gradientBlocks", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:dark_oak_planks\"}"), 1, DFType.ITEM));
          put(2, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:stripped_dark_oak_wood\"}"), 2, DFType.ITEM));
          put(3, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:brown_terracotta\"}"), 3, DFType.ITEM));
          put(4, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:note_block\"}"), 4, DFType.ITEM));
          put(5, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:brown_wool\"}"), 5, DFType.ITEM));
          put(6, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:stripped_spruce_wood\"}"), 6, DFType.ITEM));
          put(7, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:spruce_planks\"}"), 7, DFType.ITEM));
          put(8, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:coarse_dirt\"}"), 8, DFType.ITEM));
          put(9, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:dirt\"}"), 9, DFType.ITEM));
        }},
        new HashMap<>(){}, "SETVAR:CreateList", null), "CreateList", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("gradientBlocks", Scope.LOCAL), 0, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:ReverseList", null), "ReverseList", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("gradientLength", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("gradientBlocks", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:ListLength", null), "ListLength", null
      ),
      new Repeat(
        null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("block", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 75d, 255.5d, 75d, 0f, 0f), 1, DFType.LOC));
          put(2, new DFValue(new Location(Bukkit.getWorlds().get(0), 225d, 255.5d, 225d, 0f, 0f), 2, DFType.LOC));
        }},
        new HashMap<>(){}, "REPEAT:Grid", null), "Grid", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("i", Scope.LOCAL), 0, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:+=", null), "+=", null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("i", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("50", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:>=", null), ">=", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("i", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("0", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new Control(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("2", 0, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Time Unit", "Ticks");
        }}, "CONTROL:Wait", null), "Wait"
      ),
      new ClosingBracket(),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("ignore", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("air", 1, DFType.TXT));
          put(2, new DFValue("deepslate", 2, DFType.TXT));
          put(3, new DFValue("cobbled_deepslate", 3, DFType.TXT));
          put(4, new DFValue("polished_deepslate", 4, DFType.TXT));
          put(5, new DFValue("red_concrete", 5, DFType.TXT));
          put(6, new DFValue("red_concrete_powder", 6, DFType.TXT));
          put(7, new DFValue("chain", 7, DFType.TXT));
        }},
        new HashMap<>(){}, "SETVAR:CreateList", null), "CreateList", null
      ),
      new Repeat(
        null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("256", 0, DFType.NUM));
        }},
        new HashMap<>(){}, "REPEAT:Multiple", null), "Multiple", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("mat", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Return Value Type", "Block ID (oak_log)");
        }}, "SETVAR:GetBlockType", null), "GetBlockType", null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("ignore", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("mat", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "IFVAR:ListContains", null), "ListContains", true, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("y", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Y");
        }}, "SETVAR:GetCoord", null), "GetCoord", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("check", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("1", 2, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Coordinate", "Y");
        }}, "SETVAR:ShiftOnAxis", null), "ShiftOnAxis", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("checkMat", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("check", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Return Value Type", "Block ID (oak_log)");
        }}, "SETVAR:GetBlockType", null), "GetBlockType", null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("ignore", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("checkMat", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "IFVAR:ListContains", null), "ListContains", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("topY", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("y", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("temp", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:moss_block\"}"), 0, DFType.ITEM));
          put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "GAMEACTION:SetBlock", null), "SetBlock"
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("temp", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("-1", 2, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Coordinate", "Y");
        }}, "SETVAR:ShiftOnAxis", null), "ShiftOnAxis", null
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:dirt\"}"), 0, DFType.ITEM));
          put(1, new DFValue(new DFVar("temp", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "GAMEACTION:SetBlock", null), "SetBlock"
      ),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("y", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("254", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:<", null), "<", false, null
      ),
      new CallFunction(
        "spawnVeg"
      ),
      new ClosingBracket(),
      new Repeat(
        null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("256", 0, DFType.NUM));
        }},
        new HashMap<>(){}, "REPEAT:Multiple", null), "Multiple", false, null
      ),
      new IfGame(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("temp", Scope.LOCAL), 0, DFType.VAR));
        }},
        new HashMap<>(){}, "IFGAME:BlockEquals", null), "BlockEquals", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("tempY", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("temp", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Y");
        }}, "SETVAR:GetCoord", null), "GetCoord", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("totalY", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("topY", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("%math(%var(tempY)+1)", 2, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:-", null), "-", null
      ),
      new Control(
        "selection", null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){}, "CONTROL:StopRepeat", null), "StopRepeat"
      ),
      new ClosingBracket(),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("temp", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("-1", 1, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Coordinate", "Y");
        }}, "SETVAR:ShiftOnAxis", null), "ShiftOnAxis", null
      ),
      new RepeatingBracket(),
      new ClosingBracket(),
      new Else(),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("yDiff", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("topY", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("y", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:-", null), "-", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("yDiff", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("yDiff", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("totalY", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Division Mode", "Default");
        }}, "SETVAR:/", null), "/", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("gradientIndex", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("%math(%var(yDiff)*%var(gradientLength))", 1, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Round Mode", "Nearest");
        }}, "SETVAR:RoundNumber", null), "RoundNumber", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("gradientBlock", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("gradientBlocks", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("gradientIndex", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:GetListValue", null), "GetListValue", null
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("gradientBlock", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "GAMEACTION:SetBlock", null), "SetBlock"
      ),
      new ClosingBracket(),
      new ClosingBracket(),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("block", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("-1", 1, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Coordinate", "Y");
        }}, "SETVAR:ShiftOnAxis", null), "ShiftOnAxis", null
      ),
      new RepeatingBracket(),
      new RepeatingBracket(),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("time", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new GameValue(Value.Timestamp, "default"), 1, DFType.GAMEVAL));
          put(2, new DFValue(new DFVar("start", Scope.GLOBAL), 2, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:-", null), "-", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("time", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("time", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("m:ss", 2, DFType.TXT));
        }},
        new HashMap<>(){{
          put("Format", "Custom");
        }}, "SETVAR:FormatTime", null), "FormatTime", null
      ),
      new PlayerAction(
        "allplayers", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("§a✔ " + ChatColor.of("#8fff70") + "Generation finished in " + ChatColor.of("#5ab441") + "%var(time)", 0, DFType.TXT));
          put(1, new DFValue("§a♧ " + ChatColor.of("#8fff70") + "Generation Seed: " + ChatColor.of("#5ab441") + "%var(seed)", 1, DFType.TXT));
          put(2, new DFValue("0", 2, DFType.NUM));
        }},
        new HashMap<>(){}, "PLAYERACTION:SendMessageSeq", null), "SendMessageSeq"
      ),
      new PlayerAction(
        "allplayers", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFSound("Chime", 1f, 2f), 0, DFType.SND));
          put(1, new DFValue(new DFSound("Lodestone Lock Compass", 1f, 2f), 1, DFType.SND));
          put(2, new DFValue(new DFSound("Player Level Up", 1f, 2f), 2, DFType.SND));
        }},
        new HashMap<>(){{
          put("Sound Source", "Master");
        }}, "PLAYERACTION:PlaySound", null), "PlaySound"
      ),
    });
    put("spawnVeg", new Object[]{
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("%random(1,75)", 0, DFType.NUM));
          put(1, new DFValue("1", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:=", null), "=", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("veg", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:small_dripleaf\"}"), 1, DFType.ITEM));
          put(2, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:big_dripleaf\"}"), 2, DFType.ITEM));
          put(3, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:fern\"}"), 3, DFType.ITEM));
          put(4, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:grass\"}"), 4, DFType.ITEM));
          put(5, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:azalea\"}"), 5, DFType.ITEM));
          put(6, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:azalea_leaves\"}"), 6, DFType.ITEM));
          put(7, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:flowering_azalea_leaves\"}"), 7, DFType.ITEM));
        }},
        new HashMap<>(){}, "SETVAR:RandomValue", null), "RandomValue", null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("veg", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:small_dripleaf\"}"), 1, DFType.ITEM));
        }},
        new HashMap<>(){}, "IFVAR:=", null), "=", false, null
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:small_dripleaf\"}"), 0, DFType.ITEM));
          put(1, new DFValue(new DFVar("check", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("half=upper", 2, DFType.TXT));
        }},
        new HashMap<>(){}, "GAMEACTION:SetBlock", null), "SetBlock"
      ),
      new Control(
        "selection", null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){}, "CONTROL:Return", null), "Return"
      ),
      new ClosingBracket(),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("veg", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:big_dripleaf\"}"), 1, DFType.ITEM));
        }},
        new HashMap<>(){}, "IFVAR:=", null), "=", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("check", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new Vector(0, 1, 0), 1, DFType.VEC));
        }},
        new HashMap<>(){}, "SETVAR:SetDirection", null), "SetDirection", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("ray", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("check", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("%random(3,10)", 2, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Entity Collision", "False");
          put("Block Collision", "All blocks");
        }}, "SETVAR:Raycast", null), "Raycast", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("ray", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("-1", 1, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Coordinate", "Y");
        }}, "SETVAR:ShiftOnAxis", null), "ShiftOnAxis", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dripLength", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("check", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("ray", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Distance Type", "Altitude (Y)");
        }}, "SETVAR:Distance", null), "Distance", null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dripLength", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("2", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:>=", null), ">=", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dripLoc", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("check", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dir", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("east", 1, DFType.TXT));
          put(2, new DFValue("west", 2, DFType.TXT));
          put(3, new DFValue("north", 3, DFType.TXT));
          put(4, new DFValue("south", 4, DFType.TXT));
        }},
        new HashMap<>(){}, "SETVAR:RandomValue", null), "RandomValue", null
      ),
      new Repeat(
        null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("%math(%var(dripLength)-1)", 0, DFType.NUM));
        }},
        new HashMap<>(){}, "REPEAT:Multiple", null), "Multiple", false, null
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("big_dripleaf_stem", 0, DFType.TXT));
          put(1, new DFValue(new DFVar("dripLoc", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("facing=%var(dir)", 2, DFType.TXT));
        }},
        new HashMap<>(){}, "GAMEACTION:SetBlock", null), "SetBlock"
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dripLoc", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("1", 1, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Coordinate", "Y");
        }}, "SETVAR:ShiftOnAxis", null), "ShiftOnAxis", null
      ),
      new RepeatingBracket(),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:big_dripleaf\"}"), 0, DFType.ITEM));
          put(1, new DFValue(new DFVar("dripLoc", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("facing=%var(dir)", 2, DFType.TXT));
        }},
        new HashMap<>(){}, "GAMEACTION:SetBlock", null), "SetBlock"
      ),
      new ClosingBracket(),
      new Control(
        "selection", null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){}, "CONTROL:Return", null), "Return"
      ),
      new ClosingBracket(),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("veg", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("check", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "GAMEACTION:SetBlock", null), "SetBlock"
      ),
      new ClosingBracket(),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("%random(1,750)", 0, DFType.NUM));
          put(1, new DFValue("1", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:=", null), "=", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("rayDir", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new Vector(0, 1, 0), 1, DFType.VEC));
          put(2, new DFValue("%random(-90, 0)", 2, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Axis", "X");
          put("Angle Units", "Degrees");
        }}, "SETVAR:RotateAroundAxis", null), "RotateAroundAxis", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("rayDir", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("rayDir", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("%random(0,360)", 2, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Axis", "Y");
          put("Angle Units", "Degrees");
        }}, "SETVAR:RotateAroundAxis", null), "RotateAroundAxis", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("check", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("rayDir", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:SetDirection", null), "SetDirection", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("ray", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("check", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue("99", 2, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Entity Collision", "False");
          put("Block Collision", "Solid blocks");
        }}, "SETVAR:Raycast", null), "Raycast", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("mat", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("ray", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Return Value Type", "Block ID (oak_log)");
        }}, "SETVAR:GetBlockType", null), "GetBlockType", null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("mat", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("air", 1, DFType.TXT));
          put(2, new DFValue("void_air", 2, DFType.TXT));
        }},
        new HashMap<>(){}, "IFVAR:!=", null), "!=", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dist", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("check", Scope.LOCAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("ray", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Distance Type", "Distance 3D (X/Y/Z)");
        }}, "SETVAR:Distance", null), "Distance", null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("dist", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("30", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:>", null), ">", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("loc1", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("check", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("loc2", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("ray", Scope.LOCAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new CallFunction(
        "connectPoints"
      ),
      new ClosingBracket(),
      new ClosingBracket(),
      new ClosingBracket(),
      new Else(),
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("%random(1,500)", 0, DFType.NUM));
          put(1, new DFValue("1", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:=", null), "=", false, null
      ),
      new CallFunction(
        "spawnRing"
      ),
      new ClosingBracket(),
      new ClosingBracket(),
    });
  }};
  
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
        new PlayerAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0), 0, DFType.POT));
            put(1, new DFValue(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 49), 1, DFType.POT));
          }},
          new HashMap<>(){{
            put("Show Icon", "False");
            put("Overwrite Effect", "True");
            put("Effect Particles", "None");
          }}, "PLAYERACTION:GivePotion", localVars), "GivePotion"
        ),
        new PlayerAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue("18000", 0, DFType.NUM));
          }},
          new HashMap<>(){}, "PLAYERACTION:SetPlayerTime", localVars), "SetPlayerTime"
        ),
        new PlayerAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(ChatColor.of("#9edfff") + "🌊 " + ChatColor.of("#a48f56") + "◦› ", 0, DFType.TXT));
          }},
          new HashMap<>(){}, "PLAYERACTION:SetChatTag", localVars), "SetChatTag"
        ),
        new PlayerAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(ChatColor.of("#c6ab71") , 0, DFType.TXT));
          }},
          new HashMap<>(){}, "PLAYERACTION:ChatColor", localVars), "ChatColor"
        ),
        new PlayerAction(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue("§o", 0, DFType.TXT));
          }},
          new HashMap<>(){}, "PLAYERACTION:SetNameColor", localVars), "SetNameColor"
        ),
        new PlayerAction(
          "allplayers", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new Location(Bukkit.getWorlds().get(0), 150d, 50d, 150d, 0f, 0f), 0, DFType.LOC));
            put(1, new DFValue("150", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "PLAYERACTION:SetWorldBorder", localVars), "SetWorldBorder"
        ),
        new IfVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("%default joinedBefore", Scope.SAVE), 0, DFType.VAR));
            put(1, new DFValue("0", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "IFVAR:=", localVars), "=", false, localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("%default joinedBefore", Scope.SAVE), 0, DFType.VAR));
            put(1, new DFValue("1", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("%default instantRespawn", Scope.SAVE), 0, DFType.VAR));
            put(1, new DFValue("1", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("%default voidDeath", Scope.SAVE), 0, DFType.VAR));
            put(1, new DFValue("1", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
        ),
        new ClosingBracket(),
        new IfVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new GameValue(Value.PlayerCount, "default"), 0, DFType.GAMEVAL));
            put(1, new DFValue("1", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "IFVAR:=", localVars), "=", false, localVars
        ),
        new CallFunction(
          "getValues"
        ),
        new StartProcess(
          "terrainGen", StartProcess.TargetMode.COPY_NONE, StartProcess.VarStorage.NEW
        ),
        new ClosingBracket(),
      }, targets, localVars, null, SelectionType.PLAYER, specifics);
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