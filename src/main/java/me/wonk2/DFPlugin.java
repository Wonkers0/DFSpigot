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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import me.wonk2.utilities.values.DFParticle;
import org.bukkit.Color;

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
  public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
  public static Location origin = new Location(null, 0, 0, 0);
  public static JavaPlugin plugin;
  public static Logger logger;
  public static World world = Bukkit.getWorld("world");
  public static HashMap<String, Object[]> functions = new HashMap<>(){{
    put("lobby", new Object[]{
      new PlayerAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(new Location(world, 25.52187615231378d, 68d, 25.520968119767986d, -89.75811f, -2.9283793f), 0, DFType.LOC));
        }},
        new HashMap<>(){{
          put("Keep Velocity", "False");
          put("Keep Current Rotation", "False");
        }}, "PLAYERACTION:Teleport", null), "Teleport"
      ),
      new PlayerAction(
        "selection", null, new ParamManager(
        new HashMap<>(){{
          put(4, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:nether_star\",tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#00DDDD\",\"text\":\">»◇ \"},{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#00FFFF\",\"text\":\"Join Game \"},{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#00DDDD\",\"text\":\"◇«<\"}],\"text\":\"\"}'}}}"), 4, DFType.ITEM));
        }},
        new HashMap<>(){}, "PLAYERACTION:SetHotbar", null), "SetHotbar"
      ),
    });
  }};
  
  @EventHandler
  public void Respawn (PlayerRespawnEvent event){
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
        new CallFunction(
          "lobby"
        ),
      }, targets, localVars, event, SelectionType.PLAYER, specifics);
  }
  
  @EventHandler
  public void RightClick (PlayerInteractEvent event){
    int funcStatus;
    HashMap<String, DFValue> localVars = new HashMap<>();
    HashMap<String, Entity[]> targets = new HashMap<>(){{
      put("default", new Entity[]{event.getPlayer()});
    }};
    
    HashMap<String, Object> specifics = new HashMap<>(){{
      put("block", DFUtilities.getEventLoc(event.getPlayer(), event.getClickedBlock()));
      put("item", event.getItem());
    }};
    
    if((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getHand() == EquipmentSlot.HAND)
      CodeExecutor.executeThread(
        new Object[]{
          new IfGame(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:nether_star\",tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#00DDDD\",\"text\":\">»◇ \"},{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#00FFFF\",\"text\":\"Join Game \"},{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#00DDDD\",\"text\":\"◇«<\"}],\"text\":\"\"}'}}}"), 0, DFType.ITEM));
            }},
            new HashMap<>(){{
              put("Comparison Mode", "Ignore stack size/durability");
            }}, "IFGAME:EventItemEquals", localVars), "EventItemEquals", false, specifics
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("loc", Scope.LOCAL), 0, DFType.VAR));
              put(1, new DFValue(new Location(world, 27.057791394852757d, 50d, 15.720768462174874d, -0.17067507f, -3.6123676f), 1, DFType.LOC));
              put(2, new DFValue(new Location(world, 13.995188410175615d, 51d, 5.307385067235373d, -34.031956f, 0.4430014f), 2, DFType.LOC));
              put(3, new DFValue(new Location(world, 45.70187156434258d, 51.0625d, 18.992268375303183d, -179.02252f, -0.08043133f), 3, DFType.LOC));
              put(4, new DFValue(new Location(world, 19.28732213432886d, 50.25d, 42.69999998807907d, -151.15646f, 0.68441015f), 4, DFType.LOC));
              put(5, new DFValue(new Location(world, 45.8764240436758d, 51d, 33.79437117029306d, 0.67993164f, -1.6911358f), 5, DFType.LOC));
              put(6, new DFValue(new Location(world, 45.91273243597061d, 50.125d, 26.910617468035298d, 90.50412f, -2.364693f), 6, DFType.LOC));
            }},
            new HashMap<>(){}, "SETVAR:RandomValue", localVars), "RandomValue", localVars
          ),
          new PlayerAction(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("loc", Scope.LOCAL), 0, DFType.VAR));
            }},
            new HashMap<>(){{
              put("Keep Velocity", "False");
              put("Keep Current Rotation", "False");
            }}, "PLAYERACTION:Teleport", localVars), "Teleport"
          ),
          new PlayerAction(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 0, DFType.ITEM));
              put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 1, DFType.ITEM));
              put(2, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 2, DFType.ITEM));
              put(3, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 3, DFType.ITEM));
              put(4, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 4, DFType.ITEM));
              put(5, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 5, DFType.ITEM));
              put(6, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 6, DFType.ITEM));
              put(7, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 7, DFType.ITEM));
              put(8, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 8, DFType.ITEM));
            }},
            new HashMap<>(){}, "PLAYERACTION:SetHotbar", localVars), "SetHotbar"
          ),
          new ClosingBracket(),
        }, targets, localVars, event, SelectionType.PLAYER, specifics);
  }
  
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
        new CallFunction(
          "lobby"
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
          new IfPlayer(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 0, DFType.ITEM));
            }},
            new HashMap<>(){{
              put("Hand Slot", "Either hand");
            }}, "IFPLAYER:IsHolding", localVars), "IsHolding", false
          ),
          new IfPlayer(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 0, DFType.ITEM));
            }},
            new HashMap<>(){}, "IFPLAYER:NoItemCooldown", localVars), "NoItemCooldown", false
          ),
          new PlayerAction(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,HideFlags:127,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 0, DFType.ITEM));
              put(1, new DFValue("20", 1, DFType.NUM));
            }},
            new HashMap<>(){}, "PLAYERACTION:SetItemCooldown", localVars), "SetItemCooldown"
          ),
          new PlayerAction(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new GameValue(Value.HeldSlot, "default"), 0, DFType.GAMEVAL));
            }},
            new HashMap<>(){}, "PLAYERACTION:SetSlotItem", localVars), "SetSlotItem"
          ),
          new PlayerAction(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:snowball\"}"), 0, DFType.ITEM));
              put(1, new DFValue("5", 1, DFType.NUM));
            }},
            new HashMap<>(){}, "PLAYERACTION:LaunchProj", localVars), "LaunchProj"
          ),
          new EntityAction(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:potion\",tag:{CustomPotionColor:2095475,Potion:\"minecraft:awkward\",display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#1FF973\",\"text\":\"Methane Gas\"}],\"text\":\"\"}'}}}"), 0, DFType.ITEM));
            }},
            new HashMap<>(){}, "ENTITYACTION:ProjectileItem", localVars), "ProjectileItem", localVars
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("uuid", Scope.LOCAL), 0, DFType.VAR));
              put(1, new DFValue(new GameValue(Value.UUID, "lastentity"), 1, DFType.GAMEVAL));
            }},
            new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
          ),
          new Repeat(
            targets, new ParamManager(
            new HashMap<>(){},
            new HashMap<>(){}, "REPEAT:Forever", localVars), "Forever", false, localVars
          ),
          new SelectObject(
            new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new DFVar("uuid", Scope.LOCAL), 0, DFType.VAR));
              }},
              new HashMap<>(){}, "SELECTOBJ:EntityName", localVars), "EntityName", "null", false, localVars, specifics
          ),
          new IfVariable(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new GameValue(Value.SelectionSize, "default"), 0, DFType.GAMEVAL));
              put(1, new DFValue("0", 1, DFType.NUM));
            }},
            new HashMap<>(){}, "IFVAR:=", localVars), "=", false, localVars
          ),
          new Control(
            "selection", targets, new ParamManager(
            new HashMap<>(){},
            new HashMap<>(){}, "CONTROL:StopRepeat", localVars), "StopRepeat"
          ),
          new ClosingBracket(),
          new SetVariable(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("loc", Scope.LOCAL), 0, DFType.VAR));
              put(1, new DFValue(new GameValue(Value.Location, "selection"), 1, DFType.GAMEVAL));
            }},
            new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
          ),
          new PlayerAction(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFParticle("Dust", 1, 0, 0, 1d, 0d, null, null, Color.fromRGB(2095475), 0d, null), 0, DFType.PART));
              put(1, new DFValue(new DFVar("loc", Scope.LOCAL), 1, DFType.VAR));
            }},
            new HashMap<>(){}, "PLAYERACTION:Particle", localVars), "Particle"
          ),
          new Control(
            "selection", targets, new ParamManager(
            new HashMap<>(){},
            new HashMap<>(){{
              put("Time Unit", "Ticks");
            }}, "CONTROL:Wait", localVars), "Wait"
          ),
          new RepeatingBracket(),
          new GameAction(
            "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("loc", Scope.LOCAL), 0, DFType.VAR));
            }},
            new HashMap<>(){}, "GAMEACTION:Explosion", localVars), "Explosion"
          ),
          new ClosingBracket(),
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