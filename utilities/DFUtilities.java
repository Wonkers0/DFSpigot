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
import me.wonk2.utilities.values.DFSound;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.codehaus.plexus.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

// Class to store general-use methods and implement event handlers to assist other action implementations
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
    
    public static boolean locationEquals(Location loc, Location loc2, boolean ignoreRotation) {
        return loc.getX() == loc2.getX() && loc.getY() == loc2.getY() && loc.getZ() == loc2.getZ() && (ignoreRotation || loc.getYaw() == loc2.getYaw() && loc.getPitch() == loc2.getPitch());
    }

    public static double clampNum(double num, double min, double max){
        if(num > max) num = max;
        else if (num < min) num = min;

        return num;
    }

    public static double wrapNum(double num, double min, double max){
        if(num > max) num = min;
        else if (num < min) num = max;

        return num;
    }

    public static String parseTxt(DFValue val){
        switch(val.type){
            case LIST: {
                DFValue[] elements = (DFValue[]) val.getVal();
                String[] result = new String[elements.length];
                for(int i = 0; i < elements.length; i++) result[i] = parseTxt(elements[i]);
                return "[" +  String.join(", ", result) + "]";
            }
            case ITEM: {
                ItemMeta meta = ((ItemStack) val.getVal()).getItemMeta();

                if(meta != null && meta.hasDisplayName())
                    return meta.getDisplayName();
                else return "";
            }
            case POT: {
                PotionEffect effect = (PotionEffect) val.getVal();
                String name = StringUtils.capitalise(effect.getType().getName().replace('_', ' '));
                long timestamp = effect.getDuration() / 20 * 1000L;
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:s");

                return name + " " + effect.getAmplifier() + " - " + dateFormat.format(new Date(timestamp));
            }
            case LOC: {
                Location loc = (Location) val.getVal();
                if(loc.getYaw() == 0f && loc.getPitch() == 0f)
                    return "[" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + "]";
                else return "[" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ", " + loc.getPitch() + ", " + loc.getYaw() + "]";
            }
            case SND: {
                DFSound sound = (DFSound) val.getVal();
                return sound.getName() + "[" + sound.pitch + "]" + "[" + sound.volume + "]";
            }
            default: {
                return String.valueOf(val.getVal());
            }
        }
    }
    public static HashMap<String, DFValue> getArgs(Object obj){
        return (HashMap<String, DFValue>) obj;
    }

    public static HashMap<String, String> getTags(Object obj){
        return (HashMap<String, String>) obj;
    }

    @EventHandler
    public static void PlayerLeave(PlayerQuitEvent event){
        if(Bukkit.getOnlinePlayers().size() == 1) DFVar.globalVars = new HashMap<>(); // Purge all global vars when all players leave
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
