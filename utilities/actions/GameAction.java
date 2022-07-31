package me.wonk2.utilities.actions;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.values.DFValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class GameAction {
    public static void invokeAction(Object[] inputArray, String action, LivingEntity[] targets) {
        HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray[0]);
        HashMap<String, String> tags = DFUtilities.getTags(inputArray[1]);

        HashMap<String, SoundCategory> categories = new HashMap<>() {{
            put("Master", SoundCategory.MASTER);
            put("Music", SoundCategory.MUSIC);
            put("Jukebox/Note Blocks", SoundCategory.RECORDS);
            put("Weather", SoundCategory.WEATHER);
            put("Blocks", SoundCategory.BLOCKS);
            put("Hostile Creatures", SoundCategory.HOSTILE);
            put("Friendly Creatures", SoundCategory.NEUTRAL);
            put("Players", SoundCategory.PLAYERS);
            put("Ambient/Environment", SoundCategory.AMBIENT);
            put("Voice/Speech", SoundCategory.VOICE);
        }};

        for (LivingEntity target : targets)
            switch (action) {
                case "SpawnMob": {
                    ItemStack[] spawnEgg = DFValue.castItem((DFValue[]) args.get("mob").getVal());
                    target.sendMessage(spawnEgg.toString());
                    break;
                }


            }
    }
    public static ItemStack parseItemNBT(String rawNBT){
        if(rawNBT == "null") return null;
        CompoundTag nbt = null;
        try{nbt = TagParser.parseTag(rawNBT);}
        catch(CommandSyntaxException e){}

        net.minecraft.world.item.ItemStack nmsItem = net.minecraft.world.item.ItemStack.of(nbt);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    private static Material[] getStackTypes(ItemStack[] items){
        Material[] result = new Material[items.length];
        for(int i = 0; i < items.length; i++) result[i] = items[i].getType();
        return result;
    }

    private static void replaceItems(Player p, ItemStack[] replaceables, ItemStack replaceItem, byte amount){
        ItemStack[] items = p.getInventory().getContents();
        ItemStack removalItem;

        short itemsReplaced = 0;
        for(int i = 0; i < items.length; i++){
            for(int k = 0; k < replaceables.length; k++){
                if(items[i].isSimilar(replaceables[k])){
                    byte stack = (byte) (items[i].getAmount()/replaceables[k].getAmount());
                    if(stack > (amount - itemsReplaced)) stack = (byte) (amount - itemsReplaced);
                    if(stack > 0){
                        itemsReplaced += stack;
                        replaceItem.setAmount(replaceItem.getAmount() * stack);
                        removalItem = items[i].clone();
                        removalItem.setAmount(replaceables[k].getAmount() * stack);
                        p.getInventory().addItem(replaceItem);
                        p.getInventory().removeItem(removalItem);
                        if(itemsReplaced >= amount) return;
                    }
                    break;
                }
            }

        }
    }


    private static void clearItems(Player p, ItemStack[] items){
        ItemStack[] invContents = p.getInventory().getContents();
        for(int i = 0; i < invContents.length; i++){
            for(int j = 0; j < items.length; j++){
                if(invContents[i].isSimilar(items[j])) p.getInventory().clear(i);
            }
        }
    }

    private static void clearInv(Player p, int min, int max, boolean clearCrafting){
        for(int i = min; i < max; i++){
            p.getInventory().clear(i);
        }
        if(clearCrafting){
            p.setItemOnCursor(null);
            Inventory topInv = p.getOpenInventory().getTopInventory();
            if(topInv.getType() == InventoryType.CRAFTING)
                topInv.clear();
        }
    }

    private static void saveInv(Player p){
        ItemStack[] inv = p.getInventory().getContents();
        String[] result = new String[inv.length];
        for(int i = 0; i < inv.length; i++){
            if(inv[i] == null){
                result[i] = "null";
                continue;
            }

            if(!CraftItemStack.asNMSCopy(inv[i]).hasTag()){
                result[i] = "{Count:" + inv[i].getAmount() + "b,id:\"minecraft:" + inv[i].getType().toString().toLowerCase() + "\"}";
                continue;
            }
            result[i] = formatCompoundTags(inv[i], CraftItemStack.asNMSCopy(inv[i]).getTag().toString());
        }
        p.sendMessage(String.join("|", result));
        DFUtilities.playerConfig.getConfig().set("players." + p.getUniqueId() + ".inventory", String.join("|", result));
        DFUtilities.playerConfig.saveConfig();
    }

    private static void loadInv(Player p){
        if(!DFUtilities.playerConfig.getConfig().contains("players." + p.getUniqueId() + ".inventory")) return;
        String[] inv = DFUtilities.playerConfig.getConfig().getString("players." + p.getUniqueId() + ".inventory").split("\\|");
        for(int i = 0; i < inv.length; i++){
            if(inv[i] != null) p.getInventory().setItem(i, parseItemNBT(inv[i]));
            else p.getInventory().setItem(i, null);
        }
    }

    private static String formatCompoundTags(ItemStack item, String tags){
        String result = "{Count:" + item.getAmount() + "b, id:\"minecraft:" + item.getType().toString().toLowerCase() + "\",";
        result += "tag:{" + tags.substring(1, tags.length() - 1) + "}}"; // Remove opening and ending brackets from the CompoundTag, then add closing bracket of main nbt.
        return result;
    }

    private static Inventory createInventory(Player p, DFValue[] items, Integer length){
        Inventory inv = Bukkit.createInventory(p, length, "Menu");

        for (DFValue item : items){
            if(item.slot > length) break;
            inv.setItem(item.slot, (ItemStack) item.getVal());
        }

        return inv;
    }

    private static void expandInv(Player p, DFValue[] items, Integer expandLength){
        if(p.getOpenInventory().getType() == InventoryType.PLAYER) return; // Cannot expand player inventory!
        ItemStack[] invItems = (ItemStack[]) ArrayUtils.addAll(p.getOpenInventory().getTopInventory().getContents(), createInventory(p, items, expandLength).getContents());
        byte length = (byte) Math.min(invItems.length, 54);
        Inventory newInv = Bukkit.createInventory(p, length, p.getOpenInventory().getTitle());
        for(int i = 0; i < length; i++){
            newInv.setItem(i, invItems[i]);
        }
        p.openInventory(newInv);
    }

    private static void setInvName(Player p, String name){
        if(p.getOpenInventory().getType() == InventoryType.PLAYER) return;
        ItemStack[] currentInvItems = p.getOpenInventory().getTopInventory().getContents();
        Inventory newInv = Bukkit.createInventory(p, currentInvItems.length, name);
        newInv.setContents(currentInvItems);
        p.openInventory(newInv);
    }

    private static void removeInvRow(Player p, Integer rows){
        if(!DFUtilities.inCustomInv(p)) return;

        InventoryView inv = p.getOpenInventory();
        List<ItemStack> invItems = Arrays.asList(inv.getTopInventory().getContents());
        Integer invSize = invItems.size() - rows * 9;
        if(invSize < 9) return;
        Inventory newInv = Bukkit.createInventory(p, invSize, inv.getTitle());
        newInv.setContents(invItems.subList(0, invSize).toArray(new ItemStack[0]));
        p.openInventory(newInv);
    }

    private static void openContainerInv(Player p, Location loc){
        if(loc == null) return;
        Block block = p.getWorld().getBlockAt(loc);
        switch(block.getType()){
            case CRAFTING_TABLE:
                p.openWorkbench(loc, true);
                break;
            case ENCHANTING_TABLE:
                p.openEnchanting(loc, true);
                break;
            case OAK_SIGN:
            case OAK_WALL_SIGN:
            case SPRUCE_SIGN:
            case SPRUCE_WALL_SIGN:
            case BIRCH_SIGN:
            case BIRCH_WALL_SIGN:
            case DARK_OAK_SIGN:
            case DARK_OAK_WALL_SIGN:
            case ACACIA_SIGN:
            case ACACIA_WALL_SIGN:
            case JUNGLE_SIGN:
            case JUNGLE_WALL_SIGN:
            case CRIMSON_SIGN:
            case CRIMSON_WALL_SIGN:
            case WARPED_SIGN:
            case WARPED_WALL_SIGN:
                p.openSign((Sign) block);
                break;
            case GRINDSTONE:
                p.openInventory(Bukkit.createInventory(p, InventoryType.GRINDSTONE));
                break;
            case ENDER_CHEST:
                p.openInventory(p.getEnderChest());
                break;
            case BEACON:
                p.openInventory(Bukkit.createInventory(p, InventoryType.BEACON));
                break;
            case CARTOGRAPHY_TABLE:
                p.openInventory(Bukkit.createInventory(p, InventoryType.CARTOGRAPHY));
                break;
            case SMITHING_TABLE:
                p.openInventory(Bukkit.createInventory(p, InventoryType.SMITHING));
                break;
            case ANVIL:
            case CHIPPED_ANVIL:
            case DAMAGED_ANVIL:
                p.openInventory(Bukkit.createInventory(p, InventoryType.ANVIL));
                //TODO: Using this menu will not damage the actual anvil at the location! Figure out how to use HumanEntity#openAnvil() instead!
                break;
            default:
                Inventory inv = ((InventoryHolder) block.getState()).getInventory();
                p.openInventory(inv);
                break;
        }
    }

    private static void removePotions(Player p, PotionEffect[] effects){
        for(PotionEffect effect : effects) p.removePotionEffect(effect.getType());
    }
}
