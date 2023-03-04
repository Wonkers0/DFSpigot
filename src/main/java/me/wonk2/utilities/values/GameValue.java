package me.wonk2.utilities.values;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.actions.SetVariable;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.enums.SelectionType;
import me.wonk2.utilities.enums.Value;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class GameValue {
	public Value val;
	public String targetName;
	
	public GameValue(Value val, String targetName){
		this.val = val;
		this.targetName = targetName.equals("last-spawned entity") ? "lastentity" : targetName;
	}
	
	public DFValue getVal(HashMap<String, Entity[]> targetMap){
		Entity target = DFUtilities.getTargets(targetName, targetMap, SelectionType.EITHER)[0];
		
		try {
			return switch (val) {
				case PlayerCount -> new DFValue(Bukkit.getOnlinePlayers().size(), DFType.NUM);
				case Timestamp -> new DFValue(new Date().getTime(), DFType.NUM);
				case SelectionSize -> new DFValue(targetMap.get("selection") == null ? 0 : targetMap.get("selection").length, DFType.NUM);
				case CurrentHealth -> new DFValue(((LivingEntity) target).getHealth(), DFType.NUM);
				case MaximumHealth -> new DFValue(((LivingEntity) target).getAttribute(Attribute.GENERIC_MAX_HEALTH), DFType.NUM);
				case AbsorptionHealth -> new DFValue(((LivingEntity) target).getAbsorptionAmount(), DFType.NUM);
				case FoodLevel -> new DFValue(((Player) target).getFoodLevel(), DFType.NUM);
				case FoodSaturation -> new DFValue(((Player) target).getSaturation(), DFType.NUM);
				case FoodExhaustion -> new DFValue(((Player) target).getExhaustion(), DFType.NUM);
				case AttackDamage -> new DFValue(((LivingEntity) target).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE), DFType.NUM);
				case AttackSpeed -> new DFValue(((LivingEntity) target).getAttribute(Attribute.GENERIC_ATTACK_SPEED), DFType.NUM);
				case ArmorPoints -> new DFValue(((LivingEntity) target).getAttribute(Attribute.GENERIC_ARMOR), DFType.NUM);
				case ArmorToughness -> new DFValue(((LivingEntity) target).getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS), DFType.NUM);
				case InvulnerabilityTicks -> new DFValue(((LivingEntity) target).getNoDamageTicks(), DFType.NUM);
				case ExperienceLevel -> new DFValue(((Player) target).getLevel(), DFType.NUM);
				case ExperienceProgress -> new DFValue(((Player) target).getExp() * 100, DFType.NUM);
				case FireTicks -> new DFValue(target.getFireTicks(), DFType.NUM);
				case FreezeTicks -> new DFValue(target.getFreezeTicks(), DFType.NUM);
				case RemainingAir -> new DFValue(((Player) target).getRemainingAir(), DFType.NUM);
				case FallDistance -> new DFValue(target.getFallDistance(), DFType.NUM);
				case HeldSlot -> new DFValue(((Player) target).getInventory().getHeldItemSlot() + 1, DFType.NUM);
				case Ping -> new DFValue(((Player) target).getPing(), DFType.NUM);
				case SteerSidewaysMovement -> new DFValue(0, DFType.NUM); // TODO
				case SteerForwardMovement -> new DFValue(0, DFType.NUM); // TODO
				case ItemUsageProgress -> new DFValue(0, DFType.NUM); // TODO
				case Location -> new DFValue(DFUtilities.getRelativeLoc(target.getLocation()), DFType.LOC);
				case TargetBlockLocation -> new DFValue(((Player) target).getTargetBlockExact(5).getLocation(), DFType.LOC);
				case TargetBlockSide -> new DFValue(DFUtilities.blockFaceToVector(getBlockFace((Player) target)), DFType.VEC);
				case EyeLocation -> new DFValue(DFUtilities.getRelativeLoc(((LivingEntity) target).getEyeLocation()), DFType.LOC);
				case XCoordinate -> new DFValue(target.getLocation().getX(), DFType.LOC);
				case YCoordinate -> new DFValue(target.getLocation().getY(), DFType.LOC);
				case ZCoordinate -> new DFValue(target.getLocation().getZ(), DFType.LOC);
				case Pitch -> new DFValue(target.getLocation().getPitch(), DFType.LOC);
				case Yaw -> new DFValue(target.getLocation().getYaw(), DFType.LOC);
				case SpawnLocation -> new DFValue(((Player) target).getBedSpawnLocation(), DFType.LOC);
				case Velocity -> new DFValue(target.getVelocity(), DFType.VEC);
				case Direction -> new DFValue(target.getLocation().getDirection(), DFType.VEC);
				case MainHandItem -> new DFValue(((LivingEntity) target).getEquipment().getItemInMainHand(), DFType.ITEM);
				case OffHandItem -> new DFValue(((LivingEntity) target).getEquipment().getItemInOffHand(), DFType.ITEM);
				case ArmorItems -> DFValue.castArray(((Player) target).getInventory().getArmorContents(), DFType.ITEM);
				case HotbarItems -> getHotbar((Player) target);
				case InventoryItems -> DFValue.castArray(((Player) target).getInventory().getStorageContents(), DFType.ITEM);
				case CursorItem -> new DFValue(((Player) target).getItemOnCursor(), DFType.ITEM);
				case InventoryMenuItems -> DFValue.castArray(((Player) target).getOpenInventory().getTopInventory().getContents(), DFType.ITEM);
				case SaddleItem -> getSaddleItem((LivingEntity) target);
				case EntityItem -> new DFValue(((Item) target).getItemStack(), DFType.ITEM);
				case Name -> new DFValue(target.getCustomName(), DFType.TXT);
				case UUID -> new DFValue(target.getUniqueId().toString(), DFType.TXT);
				case EntityType -> new DFValue(target.getType().toString().toLowerCase(), DFType.TXT);
				case OpenInventoryTitle -> {
					InventoryView inventoryView = ((Player) target).getOpenInventory();
					yield new DFValue(inventoryView.getType() == InventoryType.PLAYER ? "none" : inventoryView.getTitle(), DFType.TXT);
				}
				case PotionEffects -> DFValue.castArray(((LivingEntity) target).getActivePotionEffects().toArray(new PotionEffect[0]), DFType.POT);
				case Vehicle -> new DFValue(target.getVehicle() == null ? "none" : target.getVehicle().getUniqueId().toString().toLowerCase(), DFType.TXT);
				case Passengers -> getUUIDs(target.getPassengers());
				case LeadHolder -> new DFValue(((Tameable) target).isLeashed() ?
					((Tameable) target).getLeashHolder().getUniqueId().toString().toLowerCase() : "none", DFType.TXT);
				case AttachedLeads -> new DFValue(0, DFType.NUM); // TODO
				default -> throw new NotImplementedException("This game value is not implemented yet: " + val);
			};
		} catch(ClassCastException ignored){
			return DFValue.nullVar();
		}
	}
	
	private DFValue getHotbar(Player p) {
		ArrayList<DFValue> result = new ArrayList<>();
		for(int i = 0; i < 9; i++) result.add(new DFValue(p.getInventory().getItem(i), DFType.ITEM));
		return new DFValue(result.toArray(DFValue[]::new), DFType.LIST);
	}
	
	private DFValue getUUIDs(List<Entity> entities){
		ArrayList<DFValue> result = new ArrayList<>();
		for(Entity entity : entities) result.add(new DFValue(entity.getUniqueId().toString().toLowerCase(), DFType.TXT));
		return new DFValue(result.toArray(), DFType.LIST);
	}
	
	private DFValue getSaddleItem(LivingEntity target){
		return new DFValue(switch(target.getType()){
			case LLAMA -> ((Llama) target).getInventory().getDecor();
			case HORSE,SKELETON_HORSE,ZOMBIE_HORSE -> ((AbstractHorse) target).getInventory().getSaddle();
			case PIG -> ((Pig) target).hasSaddle() ? new ItemStack(Material.SADDLE) : null;
			default -> null;
		}, DFType.ITEM);
	}
	
	private BlockFace getBlockFace(Player player) {
		List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
		if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
		Block targetBlock = lastTwoTargetBlocks.get(1);
		Block adjacentBlock = lastTwoTargetBlocks.get(0);
		return targetBlock.getFace(adjacentBlock);
	}
}


