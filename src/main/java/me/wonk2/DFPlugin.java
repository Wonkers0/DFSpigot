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

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
  public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
  public static Location origin = new Location(null, 0, 0, 0);
  public static JavaPlugin plugin;
  public static Logger logger;
  public static HashMap<String, Object[]> functions = new HashMap<>(){{
    put("spawnReason", new Object[]{
      new Control(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("10", 0, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Time Unit", "Ticks");
        }}, "CONTROL:Wait", null), "Wait"
      ),
      new CallFunction(
        "spawnReason"
      ),
    });
    put("spawnReason", new Object[]{
      new IfVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("reason", Scope.GLOBAL), 0, DFType.VAR));
          put(1, new DFValue("130", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "IFVAR:>=", null), ">=", false, null
      ),
      new StartProcess(
        "mainLoop", StartProcess.TargetMode.COPY_NONE, StartProcess.VarStorage.NEW
      ),
      new Control(
        "selection", null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){}, "CONTROL:End", null), "End"
      ),
      new ClosingBracket(),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("reason", Scope.GLOBAL), 0, DFType.VAR));
          put(1, new DFValue("1", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:+=", null), "+=", null
      ),
      new GameAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("loc", Scope.GLOBAL), 0, DFType.VAR));
          put(1, new DFValue("reason%var(reason)", 1, DFType.TXT));
          put(18, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:player_head\",tag:{SkullOwner:{Id:[I;978475807,-1570550556,-2099347046,-1798781186],Name:\"Reasonless\",Properties:{textures:[{Signature:\"HWDTEXjuIyF8/w7XJ70IMLXItL0sYjaZxcrk+YJKGo1rELYqQPDN7/XGQFw2KS8tZLr+wTgFd1zGEFoyA230nPYW2Oa2gljg8ry8IRSu6bjJXmm15GujXTWNk6W8BF/W2htVE18iUrxGYNVnOeI8AV4UwXG8ZORK32KMSKzZN7lLnodDQKgBCKZUYBSDwCWciWUNygttq6XeNTCe4cSsTILQD4fbamVoez2bo0H7lnkDn58lJhV8ujixI9fCe/vgj2hxsWyueAkHid5XlVu4VMhJ0V8Ac0j7AZbx2xStOnovolbzI1erwoFtK7Oalf6D2LoIy8d/RjjUPttqCQNIrsbE9DRFOJKptVX1UB9ZZjy1+63nBrxHK6bd3uyPAcANMSiiPQSHdHtPHPADekb9+OorvhMJy01G37OfL8ZyZq0QU3wdXNyu05HLYxMSvhwNUZ3YZaOEkkvBzJWJv9e4Z+CLbQFssZryRIV9pJbXews6gst65ywlNzauvIlsqWXbq8Ov1Pz2kbqWOCaeGaZCzTcHb5b4rm7Vj0VED20o4WS4KDsCK9QNNqR8+4Lwo7+0DKi3pQoT8xEuva+cDgPvjyjy5J+4EzNG5n2JoH37XZBJYIsM2uXBFDDNOju9tazK2H59AWRQIlFrOhOUOks9udNyDLMExQoFoyl2RxaUP7Q=\",Value:\"ewogICJ0aW1lc3RhbXAiIDogMTY2MDY4MTI0NTc1NSwKICAicHJvZmlsZUlkIiA6ICIzYTUyNWIxZmEyNjM0Y2U0ODJkZTgxOWE5NGM4YzZmZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZWFzb25sZXNzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk5ZjA5NzhhYzk2N2U4NGY5NWY4OTFmN2MwY2E4MzcyNDM2NGRmODdiOTNlZTA4NjEzMDQ2YmVjODI0ZmI0NDkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==\"}]}}}}"), 18, DFType.ITEM));
        }},
        new HashMap<>(){{
          put("Visibility", "Invisible (No hitbox)");
        }}, "GAMEACTION:SpawnArmorStand", null), "SpawnArmorStand"
      ),
      new EntityAction(
        "lastentity", null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){{
          put("Name Tag Visible", "Disable");
        }}, "ENTITYACTION:SetNameVisible", null), "SetNameVisible", null
      ),
      new EntityAction(
        "lastentity", null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){{
          put("Collision", "Disable");
        }}, "ENTITYACTION:SetCollidable", null), "SetCollidable", null
      ),
      new EntityAction(
        "lastentity", null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){{
          put("Invulnerable", "Enable");
        }}, "ENTITYACTION:SetInvulnerable", null), "SetInvulnerable", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("loc", Scope.GLOBAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("loc", Scope.GLOBAL), 1, DFType.VAR));
          put(2, new DFValue("0.5", 2, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Coordinate", "Y");
        }}, "SETVAR:ShiftOnAxis", null), "ShiftOnAxis", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("yaw", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("loc", Scope.GLOBAL), 1, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Yaw");
        }}, "SETVAR:GetCoord", null), "GetCoord", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("yaw", Scope.GLOBAL), 0, DFType.VAR));
          put(1, new DFValue("8", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:+=", null), "+=", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("loc", Scope.GLOBAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("loc", Scope.GLOBAL), 1, DFType.VAR));
          put(2, new DFValue(new DFVar("yaw", Scope.GLOBAL), 2, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Yaw");
        }}, "SETVAR:SetCoord", null), "SetCoord", null
      ),
      new CallFunction(
        "spawnReason"
      ),
    });
    put("mainLoop", new Object[]{
      new Control(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("11", 0, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Time Unit", "Ticks");
        }}, "CONTROL:Wait", null), "Wait"
      ),
      new Repeat(
        null, new ParamManager(
        new HashMap<>(){},
        new HashMap<>(){}, "REPEAT:Forever", null), "Forever", false, null
      ),
      new Control(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("1", 0, DFType.NUM));
        }},
        new HashMap<>(){{
          put("Time Unit", "Ticks");
        }}, "CONTROL:Wait", null), "Wait"
      ),
      new Repeat(
        null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("i", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new DFVar("reason", Scope.GLOBAL), 1, DFType.VAR));
        }},
        new HashMap<>(){}, "REPEAT:Multiple", null), "Multiple", false, null
      ),
      new SelectObject(
        new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "SELECTOBJ:AllEntities", null), "AllEntities", "null", false, null, null
      ),
      new StartProcess(
        "reasonLoop", StartProcess.TargetMode.FOR_EACH, StartProcess.VarStorage.NEW
      ),
      new SelectObject(
        new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "SELECTOBJ:Reset", null), "Reset", "null", false, null, null
      ),
      new RepeatingBracket(),
      new RepeatingBracket(),
    });
    put("reasonLoop", new Object[]{
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("yaw", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new GameValue(Value.Location, "default"), 1, DFType.GAMEVAL));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Yaw");
        }}, "SETVAR:GetCoord", null), "GetCoord", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("yaw", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue("8", 1, DFType.NUM));
        }},
        new HashMap<>(){}, "SETVAR:+=", null), "+=", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("rLoc", Scope.LOCAL), 0, DFType.VAR));
          put(1, new DFValue(new GameValue(Value.Location, "default"), 1, DFType.GAMEVAL));
          put(2, new DFValue(new DFVar("yaw", Scope.LOCAL), 2, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Coordinate Type", "Plot coordinate");
          put("Coordinate", "Yaw");
        }}, "SETVAR:SetCoord", null), "SetCoord", null
      ),
      new EntityAction(
        "default", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new DFVar("rLoc", Scope.LOCAL), 0, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Keep Current Rotation", "False");
        }}, "ENTITYACTION:Teleport", null), "Teleport", null
      ),
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
        new IfVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new GameValue(Value.PlayerCount, "default"), 0, DFType.GAMEVAL));
            put(1, new DFValue("1", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "IFVAR:=", localVars), "=", false, localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("reason", Scope.GLOBAL), 0, DFType.VAR));
            put(1, new DFValue("0", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("loc", Scope.GLOBAL), 0, DFType.VAR));
            put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 25.5d, 1.5d, 25.5d, 0f, 0f), 1, DFType.LOC));
          }},
          new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("rot", Scope.GLOBAL), 0, DFType.VAR));
            put(1, new DFValue("0", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
        ),
        new StartProcess(
          "spawnReason", StartProcess.TargetMode.COPY_NONE, StartProcess.VarStorage.NEW
        ),
        new ClosingBracket(),
        new PlayerAction(
          "default", targets, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "PLAYERACTION:AdventureMode", localVars), "AdventureMode"
        ),
        new PlayerAction(
          "default", targets, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){{
            put("Allow Flight", "Enable");
          }}, "PLAYERACTION:SetAllowFlight", localVars), "SetAllowFlight"
        ),
        new PlayerAction(
          "default", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue("500", 0, DFType.NUM));
          }},
          new HashMap<>(){{
            put("Speed Type", "Flight speed");
          }}, "PLAYERACTION:SetSpeed", localVars), "SetSpeed"
        ),
        new PlayerAction(
          "default", targets, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 254), 0, DFType.POT));
            put(1, new DFValue(new PotionEffect(PotionEffectType.SATURATION, 1000000, 254), 1, DFType.POT));
            put(2, new DFValue(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 254), 2, DFType.POT));
          }},
          new HashMap<>(){{
            put("Show Icon", "False");
            put("Overwrite Effect", "True");
            put("Effect Particles", "None");
          }}, "PLAYERACTION:GivePotion", localVars), "GivePotion"
        ),
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