package me.wonk2.utilities.actions;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.actions.pointerclasses.Conditional;
import me.wonk2.utilities.values.DFValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IfGame extends Conditional {
	public HashMap<String, Object> specifics;
	
	public IfGame(String targetName, HashMap<String, LivingEntity> targetMap, Object[] inputArray, String action, HashMap<String, Object> specifics) {
		super(targetName, targetMap, inputArray, action);
		this.specifics = specifics;
	}
	
	@Override
	public boolean evaluateCondition() {
		switch(action){
			case "BlockEquals": {
				Block block = DFPlugin.origin.getWorld().getBlockAt((Location) args.get("loc").getVal());
				List<Material> checks = Arrays.stream(DFValue.castItem(((DFValue[]) args.get("blocks").getVal()))).map(ItemStack::getType).collect(Collectors.toList());
				String[] blockTags = DFValue.castTxt((DFValue[]) args.get("blockTags").getVal());
				
				if(checks.contains(block.getType())){
					for(String blockTag : blockTags)
						if(!block.getBlockData().getAsString().contains(blockTag) || blockTag.split("=").length >= 2) return false;
				}
				else return false;
			}
			
			case "BlockPowered": {
				Location[] locs = DFValue.castLoc((DFValue[]) args.get("locs").getVal());
				String tag = tags.get("Redstone Power Mode");
				
				for(Location loc : locs){
					Block block = DFPlugin.origin.getWorld().getBlockAt(loc);
					if(block.isBlockPowered() && tag == "Direct power") continue;
					if(block.isBlockIndirectlyPowered() && tag == "Indirect power") continue;
				}
				return false;
			}
			
			case "ContainerHas": { // TODO: Test this action, I'm quite uncertain whether it'll work or not
				Block container = DFPlugin.origin.getWorld().getBlockAt((Location) args.get("loc").getVal());
				ItemStack[] contents = DFValue.castItem((DFValue[]) args.get("items").getVal());
				
				if(!(container.getState() instanceof Container)) return false;
				
				Inventory containerInv = ((Container) container.getState()).getInventory();
				for(ItemStack content : contents) if(containerInv.containsAtLeast(content, content.getAmount())) return true;
				
				return false;
			}
			
			case "ContainerHasAll": { // TODO: Test this action, I'm quite uncertain whether it'll work or not
				Block container = DFPlugin.origin.getWorld().getBlockAt((Location) args.get("loc").getVal());
				ItemStack[] contents = DFValue.castItem((DFValue[]) args.get("items").getVal());
				
				if(!(container.getState() instanceof Container)) return false;
				HashMap<ItemStack, Integer> sanitizedContents = new HashMap<>();
				for(ItemStack content : contents){
					int amount = content.getAmount();
					content.setAmount(1);
					
					if(!sanitizedContents.containsKey(content)) sanitizedContents.put(content, amount);
					else sanitizedContents.put(content, sanitizedContents.get(content) + amount);
				}
				
				Inventory containerInv = ((Container) container.getState()).getInventory();
				for(Map.Entry<ItemStack, Integer> entry : sanitizedContents.entrySet())
					if(!containerInv.containsAtLeast(entry.getKey(), entry.getValue())) return false;
				return true;
			}
			
			case "SignHasTxt": {
				Block block = DFPlugin.origin.getWorld().getBlockAt((Location) args.get("loc").getVal());
				String[] checks = DFValue.castTxt((DFValue[]) args.get("txts").getVal());
				
				String line = tags.get("Sign Line");
				String checkMode = tags.get("Check Mode");
				if(!(block.getState() instanceof Sign)) return false;
				
				Sign sign = (Sign) block.getState();
				for(String check : checks){
					if(line != "All lines" && lineHasTxt(sign.getLine(Integer.parseInt(line)), check, checkMode == "Equals")) return true;
					else if(line == "All lines")
						for(String signLine : sign.getLines())
							if(lineHasTxt(signLine, check, checkMode == "Equals"))
								return true;
				}
				
				return false;
			}
			
			case "HasPlayer": {
				String[] names = DFValue.castTxt((DFValue[]) args.get("names").getVal());
				for(String name : names) if(Bukkit.getOnlinePlayers().contains(DFUtilities.getPlayer(name))) return true;
				
				return false;
			}
			
			case "EventBlockEquals": {
				for(ItemStack itm : DFValue.castItem((DFValue[]) args.get("blocks").getVal())){
					if(itm.getType() == specifics.get("block")) return true;
				}
				
				return false;
			}
			
			case "EventItemEquals": {
				ItemStack checkItem = (ItemStack) specifics.get("item");
				
				for(ItemStack itm : DFValue.castItem((DFValue[]) args.get("items").getVal()))
					switch(tags.get("Comparison Mode")){
						case "Exactly equals":
							if(itm == checkItem) return true;
							break;
						case "Ignore stack size":
							if(itm.isSimilar(checkItem)) return true;
							break;
						case "Ignore durability and stack size": {
							((Damageable) checkItem).setDamage(((Damageable) itm).getDamage());
							if(itm.isSimilar(checkItem)) return true;
							break;
						}
						case "Material only": {
							if(itm.getType() == checkItem.getType()) return true;
							break;
						}
					}
				
				return false;
			}
			
			case "EventCancelled": {
				return (boolean) specifics.get("cancelled");
			}
	}
		
		return false;
	}
	
	static private boolean lineHasTxt(String line, String txt, boolean strict){
		if(strict) return line.equals(txt);
		else return line.contains(txt);
	}
}
