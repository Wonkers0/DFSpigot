package me.wonk2;

import me.wonk2.utilities.*;
import me.wonk2.utilities.enums.*;
import me.wonk2.utilities.values.*;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import java.util.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import me.wonk2.utilities.actions.GameAction;

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
  public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
  public static JavaPlugin plugin;

  @EventHandler
  public void LeftClick(PlayerInteractEvent event){
    HashMap<String, DFValue> localVars = new HashMap<>();
    if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)
      GameAction.invokeAction(ParamManager.formatParameters(new HashMap<>(){{
        put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:2975,id:\"minecraft:acacia_stairs\"}"), 0, DFType.ITEM));
        put(1, new DFValue(event.getPlayer().getLocation(), DFType.LOC));
        put(2, new DFValue("facing=north", 2, DFType.TXT));
        put(3, new DFValue("waterlogged=true", 3, DFType.TXT));
      }},
      new HashMap<>(){{
        put("Reform on Impact", "True");
        put("Hurt Hit Entities", "False");
      }}, "GAMEACTION:FallingBlock", localVars), "FallingBlock", new LivingEntity[]{event.getPlayer()}[0]);

  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    return true;
  }

  @Override
  public void onEnable(){
    plugin = this;

    DFUtilities.getManagers(this);
    getServer().getPluginManager().registerEvents(this, this);
    getServer().getPluginManager().registerEvents(new DFUtilities(), this);
  }
}