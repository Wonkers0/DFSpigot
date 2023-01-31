package me.wonk2;

import me.wonk2.utilities.*;
import me.wonk2.utilities.enums.*;
import me.wonk2.utilities.values.*;
import me.wonk2.utilities.actions.*;
import me.wonk2.utilities.actions.pointerclasses.brackets.*;
import me.wonk2.utilities.internals.CodeExecutor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
  public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
  public static Location origin = new Location(null, 0, 0, 0);
  public static JavaPlugin plugin;
  public static Logger logger;
  public static World world = Bukkit.getWorld("world");
  public static HashMap<String, Object[]> functions = new HashMap<>(){{
    put("consts", new Object[]{
      new SetVariable(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("swordLevels", Scope.GLOBAL), 0, DFType.VAR));
            put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:wooden_sword\",tag:{Damage:0,HideFlags:127,Unbreakable:1b,display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#29C0FF\",\"text\":\"Sword #1\"}],\"text\":\"\"}'}}}"), 1, DFType.ITEM));
            put(2, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:stone_sword\",tag:{Damage:0,HideFlags:127,Unbreakable:1b,display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#29C0FF\",\"text\":\"Sword #2\"}],\"text\":\"\"}'}}}"), 2, DFType.ITEM));
            put(3, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:iron_sword\",tag:{Damage:0,HideFlags:127,Unbreakable:1b,display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#29C0FF\",\"text\":\"Sword #3\"}],\"text\":\"\"}'}}}"), 3, DFType.ITEM));
            put(4, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:diamond_sword\",tag:{Damage:0,HideFlags:127,Unbreakable:1b,display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#29C0FF\",\"text\":\"Sword #4\"}],\"text\":\"\"}'}}}"), 4, DFType.ITEM));
          }},
          new HashMap<>(){}, "SETVAR:CreateList", null), "CreateList", null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("prices", Scope.GLOBAL), 0, DFType.VAR));
            put(1, new DFValue("0", 1, DFType.NUM));
            put(2, new DFValue("100", 2, DFType.NUM));
            put(3, new DFValue("300", 3, DFType.NUM));
            put(4, new DFValue("1000", 4, DFType.NUM));
          }},
          new HashMap<>(){}, "SETVAR:CreateList", null), "CreateList", null
      ),
      });
    put("playerLoop", new Object[]{
      new Repeat(
        null, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "REPEAT:Forever", null), "Forever", false, null
      ),
      new Control(
        "selection", null, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){{
            put("Time Unit", "Ticks");
          }}, "CONTROL:Wait", null), "Wait"
      ),
      new PlayerAction(
      "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue(ChatColor.of("#fffc1c") + "%var(%default coins) coins", 0, DFType.TXT));
        }},
        new HashMap<>(){{
          put("Text Value Merging", "No spaces");
        }}, "PLAYERACTION:ActionBar", null), "ActionBar"
      ),
      new PlayerAction(
      "selection", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("20", 0, DFType.NUM));
        }},
        new HashMap<>(){}, "PLAYERACTION:SetFoodLevel", null), "SetFoodLevel"
      ),
      new RepeatingBracket(),
      });
    put("updateSword", new Object[]{
      new PlayerAction(
        "allplayers", null, new ParamManager(
        new HashMap<>(){{
          put(0, new DFValue("%var(%default sword) | %default", 0, DFType.TXT));
          put(1, new DFValue(new DFVar("%default sword", Scope.GLOBAL), 2, DFType.VAR));
        }},
        new HashMap<>(){{
          put("Text Value Merging", "Add spaces");
          put("Alignment Mode", "Regular");
        }}, "PLAYERACTION:SendMessage", null), "SendMessage"
      ),
      new SetVariable(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("sword", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("swordLevels", Scope.GLOBAL), 1, DFType.VAR));
            put(2, new DFValue(new DFVar("%default sword", Scope.GLOBAL), 2, DFType.VAR));
          }},
          new HashMap<>(){}, "SETVAR:GetListValue", null), "GetListValue", null
      ),
      new PlayerAction(
        "selection", null, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){{
            put("Clear Crafting and Cursor", "True");
            put("Clear Mode", "Entire inventory");
          }}, "PLAYERACTION:ClearInv", null), "ClearInv"
      ),
      new PlayerAction(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("sword", Scope.LOCAL), 0, DFType.VAR));
            put(8, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:chest\",tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#3DC6FF\",\"text\":\"Upgrades \"},{\"bold\":true,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#3DC6FF\",\"text\":\"(RC)\"}],\"text\":\"\"}'}}}"), 8, DFType.ITEM));
          }},
          new HashMap<>(){}, "PLAYERACTION:SetHotbar", null), "SetHotbar"
      ),
      });
    put("loop", new Object[]{
      new Repeat(
        null, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "REPEAT:Forever", null), "Forever", false, null
      ),
      new SelectObject(
        new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "SELECTOBJ:AllEntities", null), "AllEntities", "null", false, null, null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new GameValue(Value.SelectionSize, "default"), 0, DFType.GAMEVAL));
            put(1, new DFValue("20", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "IFVAR:>", null), ">", false, null
      ),
      new SelectObject(
        new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "SELECTOBJ:Reset", null), "Reset", "null", false, null, null
      ),
      new Control(
        "selection", null, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){{
            put("Time Unit", "Ticks");
          }}, "CONTROL:Wait", null), "Wait"
      ),
      new Control(
        "selection", null, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "CONTROL:Skip", null), "Skip"
      ),
      new ClosingBracket(),
      new SelectObject(
        new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){}, "SELECTOBJ:Reset", null), "Reset", "null", false, null, null
      ),
      new Repeat(
        null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue("%random(4,10)", 0, DFType.NUM));
          }},
          new HashMap<>(){}, "REPEAT:Multiple", null), "Multiple", false, null
      ),
      new SetVariable(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("type", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue("%random(1,3)", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "SETVAR:=", null), "=", null
      ),
      new IfVariable(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("type", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue("1", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "IFVAR:=", null), "=", false, null
      ),
      new GameAction(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:zombie_spawn_egg\"}"), 0, DFType.ITEM));
            put(1, new DFValue(new Location(world, 27.5d, 50.5d, 32.5d, 0f, 0f), 1, DFType.LOC));
            put(2, new DFValue("40", 2, DFType.NUM));
            put(3, new DFValue("nerd zombie", 3, DFType.TXT));
            put(4, new DFValue(new PotionEffect(PotionEffectType.SPEED, 1000000, 1), 4, DFType.POT));
            put(19, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:player_head\",tag:{SkullOwner:{Id:[I;1059079657,996033631,-1092592784,293426541],Name:\"DF-Import-MS\",Properties:{textures:[{Signature:\"KLtJ8hnBrxqxqSwPVo0O5/CugFT05I4Fy4Hmi2aAQB5HkZQNNAL4MI18767QFNt7IYTm9iGV9SN/0Nte6sTa8ibGQTosEM7DiCdUQIoJcqJAaA5x3q5rh+J3a/MScgj2CAzSYWxCESEZ4pNjdHBvG7D2wTMAun92GsOn1Q/59xZm5M1nfCRKdeNFZtCcL9WRICVysebDzWnPzRszxqjyiBw2BkVVfoVT8tyotX1ZEmZyJRCZQnq3ZEHANWKVph06a7JSofl12ylb2LCzoze4zC2ApB921XmBNOLFxHFUWtOfGHBarZduUV/ALBI0NvIjnZIa5QSDJLcHbXWPPjzdawdA8ihDEdVYQwVylsFRYFqjhG041BJ8By+dEXj3Do4DCUb+m3CjRs3y+Tre5ROOFuf9NZMGpzLPitcOH5wtBHKo7M6Po9m90eexkmtdabOWSMHRnLf/Bib7JiAtw4Ls36SRvGhNqj+GfQrzdw1j8iD+NJRyMKSSVLPGUGXKI7afoPy72MFMITLZ1GyAI3XeEad0uUJ7UqsGZrnsGZe2eMVFYAlR9JqUjjMKm/7rQ6FopHbU1aMv+kpfdXy8ZVKGJFz7BUhiXKqe7TijYIKAzVQ4hQAiTveImYW9nlj9p9EUDMDiIRIlYops1kdg638sQlXiNRqBaBFGO8NTrpPNBWw=\",Value:\"ewogICJ0aW1lc3RhbXAiIDogMTY2ODE5ODkwMjQwOSwKICAicHJvZmlsZUlkIiA6ICI4M2EwYjZmOGVjMWI0YTU3OGQ2NmJlZDE2MmQ4M2I4MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTbmlwZWRfQnlfS2NhbHMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M5ZjVjMTIzYWM1MmQxM2QxNDgyMzM3ZTI4NDRkNDZjYjJjMzRkZTI4MDE4NmE3NWJhZTk5OTQ1NDY5MjdhIgogICAgfQogIH0KfQ==\"}]}},display:{Name:'{\"color\":\"#808080\",\"text\":\"Imported Skin\"}'}}}"), 19, DFType.ITEM));
          }},
          new HashMap<>(){}, "GAMEACTION:SpawnMob", null), "SpawnMob"
      ),
      new ClosingBracket(),
      new IfVariable(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("type", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue("2", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "IFVAR:=", null), "=", false, null
      ),
      new GameAction(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:wither_skeleton_spawn_egg\"}"), 0, DFType.ITEM));
            put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 27.5d, 50.5d, 32.5d, 0f, 0f), 1, DFType.LOC));
            put(2, new DFValue("10", 2, DFType.NUM));
            put(3, new DFValue("deez", 3, DFType.TXT));
            put(18, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:netherite_sword\",tag:{Damage:0}}"), 18, DFType.ITEM));
          }},
          new HashMap<>(){}, "GAMEACTION:SpawnMob", null), "SpawnMob"
      ),
      new ClosingBracket(),
      new IfVariable(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(new DFVar("type", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue("3", 1, DFType.NUM));
          }},
          new HashMap<>(){}, "IFVAR:=", null), "=", false, null
      ),
      new GameAction(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:slime_spawn_egg\"}"), 0, DFType.ITEM));
            put(1, new DFValue(new Location(Bukkit.getWorlds().get(0), 27.5d, 50.5d, 32.5d, 0f, 0f), 1, DFType.LOC));
          }},
          new HashMap<>(){}, "GAMEACTION:SpawnMob", null), "SpawnMob"
      ),
      new ClosingBracket(),
      new EntityAction(
        "selection", null, new ParamManager(
          new HashMap<>(){},
          new HashMap<>(){{
            put("Has Death Drops", "Disable");
          }}, "ENTITYACTION:SetDeathDrops", null), "SetDeathDrops", null
      ),
      new RepeatingBracket(),
      new Control(
        "selection", null, new ParamManager(
          new HashMap<>(){{
            put(0, new DFValue("%random(10,30)", 0, DFType.NUM));
          }},
          new HashMap<>(){{
            put("Time Unit", "Seconds");
          }}, "CONTROL:Wait", null), "Wait"
      ),
      new RepeatingBracket(),
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
        new CallFunction(
          "consts"
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("%default sword", Scope.GLOBAL), 0, DFType.VAR));
              put(1, new DFValue("1", 1, DFType.NUM));
            }},
            new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("%default coins", Scope.GLOBAL), 0, DFType.VAR));
              put(1, new DFValue("0", 1, DFType.NUM));
            }},
            new HashMap<>(){}, "SETVAR:=", localVars), "=", localVars
        ),
        new CallFunction(
          "updateSword"
        ),
        new IfVariable(
          "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new GameValue(Value.PlayerCount, "default"), 0, DFType.GAMEVAL));
              put(1, new DFValue("1", 1, DFType.NUM));
            }},
            new HashMap<>(){}, "IFVAR:=", localVars), "=", false, localVars
        ),
        new StartProcess(
          "loop", StartProcess.TargetMode.COPY_NONE, StartProcess.VarStorage.NEW
        ),
        new ClosingBracket(),
        new StartProcess(
          "playerLoop", StartProcess.TargetMode.COPY_ALL, StartProcess.VarStorage.NEW
        ),
    }, targets, localVars, null, SelectionType.PLAYER, specifics);
  }
  
  @EventHandler
  public void RightClick (PlayerInteractEvent event){
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

    if((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getHand() == EquipmentSlot.HAND)
      CodeExecutor.executeThread(
        new Object[]{
          new IfPlayer(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:chest\",tag:{display:{Name:'{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#3DC6FF\",\"text\":\"Upgrades \"},{\"bold\":true,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"#3DC6FF\",\"text\":\"(RC)\"}],\"text\":\"\"}'}}}"), 0, DFType.ITEM));
              }},
              new HashMap<>(){{
                put("Hand Slot", "Main hand");
              }}, "IFPLAYER:IsHolding", localVars), "IsHolding", false
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new DFVar("morbius", Scope.LOCAL), 0, DFType.VAR));
                put(1, new DFValue(new DFVar("%default sword", Scope.GLOBAL), 1, DFType.VAR));
                put(2, new DFValue("1", 2, DFType.NUM));
              }},
              new HashMap<>(){}, "SETVAR:+", localVars), "+", localVars
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new DFVar("price", Scope.LOCAL), 0, DFType.VAR));
                put(1, new DFValue(new DFVar("prices", Scope.GLOBAL), 1, DFType.VAR));
                put(2, new DFValue(new DFVar("morbius", Scope.LOCAL), 2, DFType.VAR));
              }},
              new HashMap<>(){}, "SETVAR:GetListValue", localVars), "GetListValue", localVars
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new DFVar("item", Scope.LOCAL), 0, DFType.VAR));
                put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:player_head\",tag:{CustomModelData:0,HideFlags:127,SkullOwner:{Id:[I;-1639526852,108677643,-1169343698,1817417363],Name:\"DF-HEAD\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19\"}]}},display:{Name:'{\"italic\":false,\"color\":\"yellow\",\"text\":\"Lime Plus\"}'}}}"), 1, DFType.ITEM));
                put(2, new DFValue(ChatColor.of("#3dc6ff") + "Upgrade Sword §l(%var(price))", 2, DFType.TXT));
              }},
              new HashMap<>(){}, "SETVAR:SetItemName", localVars), "SetItemName", localVars
          ),
          new IfVariable(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new DFVar("%default coins", Scope.GLOBAL), 0, DFType.VAR));
                put(1, new DFValue(new DFVar("price", Scope.LOCAL), 1, DFType.VAR));
              }},
              new HashMap<>(){}, "IFVAR:<", localVars), "<", false, localVars
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new DFVar("item", Scope.LOCAL), 0, DFType.VAR));
                put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:barrier\"}"), 1, DFType.ITEM));
                put(2, new DFValue(ChatColor.of("#ff1414") + "Too Poor. (%var(%default coins)/%var(price))", 2, DFType.TXT));
              }},
              new HashMap<>(){}, "SETVAR:SetItemName", localVars), "SetItemName", localVars
          ),
          new ClosingBracket(),
          new IfVariable(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new DFVar("%default sword", Scope.GLOBAL), 0, DFType.VAR));
                put(1, new DFValue("4", 1, DFType.NUM));
              }},
              new HashMap<>(){}, "IFVAR:>=", localVars), ">=", false, localVars
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new DFVar("item", Scope.LOCAL), 0, DFType.VAR));
                put(1, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:barrier\"}"), 1, DFType.ITEM));
                put(2, new DFValue(ChatColor.of("#ff1414") + "Max Sword Level Reached. cry about it", 2, DFType.TXT));
              }},
              new HashMap<>(){}, "SETVAR:SetItemName", localVars), "SetItemName", localVars
          ),
          new ClosingBracket(),
          new PlayerAction(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(4, new DFValue(new DFVar("item", Scope.LOCAL), 4, DFType.VAR));
              }},
              new HashMap<>(){}, "PLAYERACTION:ShowInv", localVars), "ShowInv"
          ),
          new PlayerAction(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue("2", 0, DFType.NUM));
              }},
              new HashMap<>(){{
                put("Row to Remove", "Bottom row");
              }}, "PLAYERACTION:RemoveInvRow", localVars), "RemoveInvRow"
          ),
          new PlayerAction(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue("ᴜᴘɢʀᴀᴅᴇꜱ", 0, DFType.TXT));
              }},
              new HashMap<>(){}, "PLAYERACTION:SetInvName", localVars), "SetInvName"
          ),
          new ClosingBracket(),
      }, targets, localVars, event, SelectionType.PLAYER, specifics);
  }
  
  @EventHandler
  public void ClickMenuSlot (InventoryClickEvent event){
    int funcStatus;
    HashMap<String, DFValue> localVars = new HashMap<>();
    HashMap<String, LivingEntity[]> targets = new HashMap<>(){{
      put("default", new LivingEntity[]{event.getWhoClicked()});
    }};

    HashMap<String, Object> specifics = new HashMap<>(){{
      put("item", event.getCurrentItem());
    }};

    if(event.getClickedInventory() == event.getWhoClicked().getOpenInventory().getTopInventory())
      CodeExecutor.executeThread(
        new Object[]{
          new IfGame(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:player_head\",tag:{CustomModelData:0,HideFlags:127,SkullOwner:{Id:[I;-1639526852,108677643,-1169343698,1817417363],Name:\"DF-HEAD\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19\"}]}},display:{Name:'{\"italic\":false,\"color\":\"yellow\",\"text\":\"Lime Plus\"}'}}}"), 0, DFType.ITEM));
              }},
              new HashMap<>(){{
                put("Comparison Mode", "Material only");
              }}, "IFGAME:EventItemEquals", localVars), "EventItemEquals", false, specifics
          ),
          new PlayerAction(
            "allplayers", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue("%default old >>> ", 0, DFType.TXT));
              put(1, new DFValue(new DFVar("%default sword", Scope.GLOBAL), 1, DFType.VAR));
            }},
            new HashMap<>(){{
              put("Text Value Merging", "Add spaces");
              put("Alignment Mode", "Regular");
            }}, "PLAYERACTION:SendMessage", localVars), "SendMessage"
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new DFVar("%default sword", Scope.GLOBAL), 0, DFType.VAR));
              }},
              new HashMap<>(){}, "SETVAR:+=", localVars), "+=", localVars
          ),
          new PlayerAction(
            "allplayers", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue("%default new >>> ", 0, DFType.TXT));
              put(1, new DFValue(new DFVar("%default sword", Scope.GLOBAL), 1, DFType.VAR));
            }},
            new HashMap<>(){{
              put("Text Value Merging", "Add spaces");
              put("Alignment Mode", "Regular");
            }}, "PLAYERACTION:SendMessage", localVars), "SendMessage"
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new DFVar("price", Scope.LOCAL), 0, DFType.VAR));
                put(1, new DFValue(new DFVar("prices", Scope.GLOBAL), 1, DFType.VAR));
                put(2, new DFValue(new DFVar("%default sword", Scope.GLOBAL), 2, DFType.VAR));
              }},
              new HashMap<>(){}, "SETVAR:GetListValue", localVars), "GetListValue", localVars
          ),
          new SetVariable(
            "selection", targets, new ParamManager(
              new HashMap<>(){{
                put(0, new DFValue(new DFVar("%default coins", Scope.GLOBAL), 0, DFType.VAR));
                put(1, new DFValue(new DFVar("price", Scope.LOCAL), 1, DFType.VAR));
              }},
              new HashMap<>(){}, "SETVAR:-=", localVars), "-=", localVars
          ),
          new PlayerAction(
            "selection", targets, new ParamManager(
              new HashMap<>(){},
              new HashMap<>(){}, "PLAYERACTION:CloseInv", localVars), "CloseInv"
          ),
          new CallFunction(
            "updateSword"
          ),
          new ClosingBracket(),
      }, targets, localVars, event, SelectionType.PLAYER, specifics);
  }
  
  @EventHandler
  public void Respawn (PlayerRespawnEvent event){
    int funcStatus;
    HashMap<String, DFValue> localVars = new HashMap<>();
    HashMap<String, LivingEntity[]> targets = new HashMap<>(){{
      put("default", new LivingEntity[]{event.getPlayer()});
    }};

    HashMap<String, Object> specifics = new HashMap<>(){{
      put("item", new ItemStack(Material.AIR));
      put("cancelled", null);
    }};

    CodeExecutor.executeThread(
      new Object[]{
        new PlayerAction(
          "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(ChatColor.of("#ff2626") + "half of your coins have been permanently removed because of your massive skill issue. cope.", 0, DFType.TXT));
            }},
            new HashMap<>(){{
              put("Text Value Merging", "Add spaces");
              put("Alignment Mode", "Regular");
            }}, "PLAYERACTION:SendMessage", localVars), "SendMessage"
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
            new HashMap<>(){{
              put(0, new DFValue(new DFVar("%default coins", Scope.GLOBAL), 0, DFType.VAR));
              put(1, new DFValue(new DFVar("%default coins", Scope.GLOBAL), 1, DFType.VAR));
              put(2, new DFValue("2", 2, DFType.NUM));
            }},
            new HashMap<>(){{
              put("Division Mode", "Floor result");
            }}, "SETVAR:/", localVars), "/", localVars
        ),
        new CallFunction(
          "updateSword"
        ),
    }, targets, localVars, event, SelectionType.PLAYER, specifics);
  }
  
  @EventHandler
  public void DropItem (PlayerDropItemEvent event){
    int funcStatus;
    HashMap<String, DFValue> localVars = new HashMap<>();
    HashMap<String, LivingEntity[]> targets = new HashMap<>(){{
      put("default", new LivingEntity[]{event.getPlayer()});
    }};

    HashMap<String, Object> specifics = new HashMap<>(){{
      put("item", event.getItemDrop().getItemStack());
      put("cancelled", event.isCancelled());
    }};

    CodeExecutor.executeThread(
      new Object[]{
        new GameAction(
          "selection", targets, new ParamManager(
            new HashMap<>(){},
            new HashMap<>(){}, "GAMEACTION:CancelEvent", localVars), "CancelEvent"
        ),
    }, targets, localVars, event, SelectionType.PLAYER, specifics);
  }
  
  @EventHandler
  public void PlayerDmgPlayer (EntityDamageByEntityEvent event){
    int funcStatus;
    HashMap<String, DFValue> localVars = new HashMap<>();
    HashMap<String, LivingEntity[]> targets = new HashMap<>(){{
      put("default", new LivingEntity[]{(LivingEntity) event.getDamager()});
    }};

    HashMap<String, Object> specifics = new HashMap<>(){{
      put("item", new ItemStack(Material.AIR));
      put("cancelled", event.isCancelled());
    }};

    if(event.getEntity() instanceof Player && event.getDamager() instanceof Player)
      CodeExecutor.executeThread(
        new Object[]{
          new GameAction(
            "selection", targets, new ParamManager(
              new HashMap<>(){},
              new HashMap<>(){}, "GAMEACTION:CancelEvent", localVars), "CancelEvent"
          ),
      }, targets, localVars, event, SelectionType.PLAYER, specifics);
  }
  
  @EventHandler
  public void KillMob (EntityDeathEvent event){
    int funcStatus;
    HashMap<String, DFValue> localVars = new HashMap<>();
    HashMap<String, LivingEntity[]> targets = new HashMap<>(){{
      put("default", new LivingEntity[]{event.getEntity().getKiller()});
    }};
    
    HashMap<String, Object> specifics = new HashMap<>(){{
      put("item", new ItemStack(Material.AIR));
      put("cancelled", false);
    }};
    if(!(event.getEntity() instanceof Player) && (event.getEntity().getKiller() != null))
     CodeExecutor.executeThread(
      new Object[]{
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>() {{
            put(0, new DFValue(new DFVar("increment", Scope.LOCAL), 0, DFType.VAR));
            put(1, new DFValue("%random(20,40)", 1, DFType.NUM));
          }},
          new HashMap<>() {
          }, "SETVAR:=", localVars), "=", localVars
        ),
        new PlayerAction(
          "selection", targets, new ParamManager(
          new HashMap<>() {{
            put(0, new DFValue(ChatColor.of("#64ff3b") + "+%var(increment) coins", 0, DFType.TXT));
          }},
          new HashMap<>() {{
            put("Text Value Merging", "Add spaces");
            put("Alignment Mode", "Regular");
          }}, "PLAYERACTION:SendMessage", localVars), "SendMessage"
        ),
        new SetVariable(
          "selection", targets, new ParamManager(
          new HashMap<>() {{
            put(0, new DFValue(new DFVar("%default coins", Scope.GLOBAL), 0, DFType.VAR));
            put(1, new DFValue(new DFVar("increment", Scope.LOCAL), 1, DFType.VAR));
          }},
          new HashMap<>() {
          }, "SETVAR:+=", localVars), "+=", localVars
        ),
        new PlayerAction(
          "selection", targets, new ParamManager(
          new HashMap<>() {{
            put(0, new DFValue("4", 0, DFType.NUM));
          }},
          new HashMap<>() {
          }, "PLAYERACTION:Heal", localVars), "Heal"
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