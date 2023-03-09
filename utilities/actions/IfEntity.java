package me.wonk2.utilities.actions;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.actions.pointerclasses.Conditional;
import me.wonk2.utilities.enums.SelectionType;
import me.wonk2.utilities.values.DFValue;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class IfEntity extends Conditional {
	public Entity[] targets;
	
	public IfEntity(String targetName, HashMap<String, Entity[]> targetMap, ParamManager paramManager, String action, boolean inverted){
		super(targetName, targetMap, paramManager, action, inverted);
	}
	
	public boolean evaluateCondition(){
		Object[] inputArray = paramManager.formatParameters(targetMap);
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray);
		targets = DFUtilities.getTargets(targetName, targetMap, SelectionType.PLAYER);
		
		for(Entity target : Arrays.copyOf(targets, targets.length, Player[].class)) if(checkForTarget(target, args, tags)) return true;
		return false;
	}
	
	protected boolean checkForTarget(Entity target, HashMap<String, DFValue> args, HashMap<String, String> tags){
		switch(action){
			case "IsType" -> {
				ItemStack[] typeItems = DFValue.castItem((DFValue[]) args.get("typeItems").getVal());
				for(ItemStack typeItem : typeItems) if(target.getType() == DFUtilities.entityTypes.get(typeItem.getType())) return true;
				
				return false;
			}
			
			case "NameEquals" -> {
				String[] names = DFValue.castTxt((DFValue[]) args.get("names").getVal());
				
				for (String name : names)
					try {
						if (target.getUniqueId() == UUID.fromString(name)) return true;
					} catch (IllegalArgumentException ignored) {
						if (target.getName().equals(name)) return true;
					}
			}
			
			case "StandingOn" -> {
				Block standingBlock = target.getWorld().getBlockAt(target.getLocation().subtract(new Location(target.getWorld(), 0f, -1f, 0f)));
				if(args.get("blocks") != null){
					Material[] blocks = (Material[]) Arrays.stream(DFValue.castItem((DFValue[]) args.get("blocks").getVal())).map(ItemStack::getType).toArray();
					return Arrays.asList(blocks).contains(standingBlock.getType());
				}
				else{
					Block[] locs = (Block[]) Arrays.stream(DFValue.castLoc((DFValue[]) args.get("locs").getVal())).map(Location::getBlock).toArray();
					return Arrays.asList(locs).contains(standingBlock);
				}
			}
			
			case "IsGrounded" -> {
				return target.isOnGround();
			}
			
			case "IsNear", "EIsNear" -> {
				Location[] locs = DFValue.castLoc((DFValue[]) args.get("locs").getVal());
				double radius = (double) args.get("radius").getVal();
				
				for(Location loc : locs)
					if(DFUtilities.locIsNear(target.getWorld(), target.getLocation(), loc, radius, tags.get("Shape"))) return true;
				
				return false;
			}
			
			case "IsMob" -> {
				return DFUtilities.mobTypes.containsValue(target.getType());
			}
			
			case "IsProjectile" -> {
				return DFUtilities.projTypes.containsValue(target.getType());
			}
			
			case "IsVehicle" -> {
				return DFUtilities.vehicleTypes.containsValue(target.getType());
			}
			
			case "IsItem" -> {
				return target.getType() == EntityType.DROPPED_ITEM;
			}
			
			case "Exists" -> {
				return !target.isValid();
			}
			
			case "IsSheared" -> {
				return target.getType() == EntityType.SHEEP && ((Sheep) target).isSheared();
			}
		}
		
		return false;
	}
}