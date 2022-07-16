package me.wonk2.utilities;

//import eu.endercentral.crazy_advancements.JSONMessage;
//import eu.endercentral.crazy_advancements.NameKey;
//import eu.endercentral.crazy_advancements.advancement.Advancement;
//import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
//import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
//import eu.endercentral.crazy_advancements.advancement.criteria.Criteria;
//import eu.endercentral.crazy_advancements.manager.AdvancementManager;

import me.wonk2.utilities.internals.FileManager;
import me.wonk2.utilities.internals.PlayerData;
import me.wonk2.utilities.internals.actions.PlayerAction;
import me.wonk2.utilities.internals.enums.DFType;
import me.wonk2.utilities.internals.values.DFValue;
import me.wonk2.utilities.internals.values.DFVar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

public class DFUtilities implements Listener {
    public static FileManager playerConfig;
    public static FileManager varConfig;

    public static HashMap<String, DFValue> purgeKeys(String[] varNames, HashMap<String, DFValue> storage, String matchReq, boolean ignoreCase){
        String[] storageKeys = storage.keySet().toArray(new String[0]);
        String[] matchedKeys = new String[0];

        for(String name : varNames)
            switch(matchReq) {
                case "Entire name":
                    if (!ignoreCase)
                        matchedKeys = (String[]) Arrays.stream(storageKeys).filter(val -> val.equalsIgnoreCase(name)).toArray();
                    else matchedKeys = (String[]) Arrays.stream(storageKeys).filter(val -> val.equals(name)).toArray();
                    break;
                case "Full word(s) in name":
                    String regex = name.replaceAll("[$^.+*?{}()|\\[\\]\\\\]", "\\\\$0") + "($| )";
                    Pattern pattern = !ignoreCase ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

                    matchedKeys = (String[]) Arrays.stream(storageKeys).filter(val -> pattern.matcher(val).find()).toArray();
                    break;
                case "Any part of name":
                    if(!ignoreCase) matchedKeys = (String[]) Arrays.stream(storageKeys).filter(val -> val.contains(name)).toArray();
                    else matchedKeys = (String[]) Arrays.stream(storageKeys).filter(val -> val.toLowerCase().contains(name.toLowerCase())).toArray();
                    break;
            }

        for(String key : matchedKeys) storage.remove(key);
        /*TODO: Purge global & saved vars*/

        return storage;
    }

    public static boolean inCustomInv(Player p){
        Inventory inv = p.getOpenInventory().getTopInventory();
        return inv.getType() != InventoryType.PLAYER
                && inv.getType() != InventoryType.CRAFTING
                && inv.getLocation() == null;
    }

    public static HashMap<String, DFValue> getArgs(Object obj){
        return (HashMap<String, DFValue>) obj;
    }

    public static HashMap<String, String> getTags(Object obj){
        return (HashMap<String, String>) obj;
    }

    @EventHandler
    public static void ClickMenuSlot(InventoryClickEvent event){
        if(inCustomInv((Player) event.getView().getPlayer())) event.setCancelled(true); // ClickSlot event triggered from inside custom GUI
    }

    @EventHandler
    public static void PlayerDmgPlayer(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            if(!PlayerData.getPlayerData(((Player) event.getEntity()).getUniqueId()).canPvP)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public static void PlaceBlock(BlockPlaceEvent event){
        PlayerData playerData = PlayerData.getPlayerData(event.getPlayer().getUniqueId());
        if(!playerData.allowedBlocks.contains(event.getBlockPlaced().getType())) event.setCancelled(true);
    }

    @EventHandler
    public static void BreakBlock(BlockBreakEvent event){
        PlayerData playerData = PlayerData.getPlayerData(event.getPlayer().getUniqueId());
        if(!playerData.allowedBlocks.contains(event.getBlock().getType())) event.setCancelled(true);
    }

    @EventHandler
    public static void Death(PlayerDeathEvent event){
        PlayerData playerData = PlayerData.getPlayerData(((Player) event.getEntity()).getUniqueId());
        if(!playerData.deathDrops) event.getDrops().clear();
        if(playerData.keepInv) event.setKeepInventory(true);
        if(playerData.instantRespawn) ((Player) event.getEntity()).spigot().respawn();
    }

    public static void getManagers(JavaPlugin plugin){
        playerConfig = new FileManager(plugin, "playerData.yml");
        varConfig = new FileManager(plugin, "varData.yml");
    }

}
