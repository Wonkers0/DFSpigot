package me.wonk2.utilities.actions;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.enums.SelectionType;
import me.wonk2.utilities.internals.EntityData;
import me.wonk2.utilities.values.DFValue;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;

public class GameAction extends Action {
	Object[] inputArray;
	public GameAction(String targetName, HashMap<String, Entity[]> targetMap, ParamManager paramManager, String action) {
		super(targetName, targetMap, paramManager, action);
	}
	
	
	@Override
	public void invokeAction() {
		inputArray = paramManager.formatParameters(targetMap);
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray);
		
		//TODO: Because of how the target system is set up, certain game actions may not work in entity events.
		HashMap<Integer, DFValue> primitiveInput = DFUtilities.getPrimitiveInput(inputArray);
		switch (action) {
			case "SpawnMob" -> {
				Material spawnEgg = ((ItemStack) args.get("mob").getVal()).getType();
				Location loc = (Location) args.get("loc").getVal();
				
				LivingEntity entity = (LivingEntity) DFPlugin.world.spawnEntity(loc, DFUtilities.mobTypes.get(spawnEgg));
				
				if (args.get("health").getVal() == null)
					entity.setHealth(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				else {
					entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue((Double) args.get("health").getVal());
					entity.setHealth((Double) args.get("health").getVal());
				}
				
				String customName = (String) args.get("customName").getVal();
				if (customName != null) {
					entity.setCustomName(customName);
					entity.setCustomNameVisible(true);
				}
				
				if (args.get("effects").getVal() != null) {
					PotionEffect[] effects = DFValue.castPotion((DFValue[]) args.get("effects").getVal());
					entity.addPotionEffects(Arrays.asList(effects));
				}
				
				ItemStack[] equipment = DFValue.castItem(new DFValue[]{primitiveInput.get(18), primitiveInput.get(19), primitiveInput.get(20), primitiveInput.get(21), primitiveInput.get(22), primitiveInput.get(23)});
				entity.getEquipment().setArmorContents(new ItemStack[]{equipment[4], equipment[3], equipment[2], equipment[1]});
				entity.getEquipment().setItemInMainHand(equipment[0]);
				entity.getEquipment().setItemInOffHand(equipment[5]);
				
				DFUtilities.lastEntity = entity;
			}
			case "SpawnItem" -> {
				ItemStack[] items = DFValue.castItem((DFValue[]) args.get("items").getVal());
				Location loc = (Location) args.get("loc").getVal();
				String customName = (String) args.get("customName").getVal();
				
				for (ItemStack item : items) {
					Entity itemEntity = DFPlugin.world.dropItem(loc, item);
					if (customName != null) itemEntity.setCustomName(customName);
					if (tags.get("Apply Item Motion").equals("False")) itemEntity.setVelocity(new Vector());
					itemEntity.setCustomNameVisible(true);
				}
			}
			case "SpawnVehicle" -> {
				Material vehicleType = ((ItemStack) args.get("vehicle").getVal()).getType();
				Location loc = (Location) args.get("loc").getVal();
				String customName = (String) args.get("customName").getVal();
				
				Vehicle vehicle = (Vehicle) DFPlugin.world.spawnEntity(loc, DFUtilities.vehicleTypes.get(vehicleType));
				switch (vehicleType) {
					case OAK_BOAT, OAK_CHEST_BOAT -> ((Boat) vehicle).setBoatType(Boat.Type.OAK);
					case SPRUCE_BOAT, SPRUCE_CHEST_BOAT -> ((Boat) vehicle).setBoatType(Boat.Type.SPRUCE);
					case DARK_OAK_BOAT, DARK_OAK_CHEST_BOAT -> ((Boat) vehicle).setBoatType(Boat.Type.DARK_OAK);
					case BIRCH_BOAT, BIRCH_CHEST_BOAT -> ((Boat) vehicle).setBoatType(Boat.Type.BIRCH);
					case JUNGLE_BOAT, JUNGLE_CHEST_BOAT -> ((Boat) vehicle).setBoatType(Boat.Type.JUNGLE);
					case ACACIA_BOAT, ACACIA_CHEST_BOAT -> ((Boat) vehicle).setBoatType(Boat.Type.ACACIA);
					case MANGROVE_BOAT, MANGROVE_CHEST_BOAT -> ((Boat) vehicle).setBoatType(Boat.Type.MANGROVE);
				}
				
				if (customName != null) vehicle.setCustomName(customName);
				vehicle.setCustomNameVisible(true);
			}
			case "SpawnExpOrb" -> {
				Location loc = (Location) args.get("loc").getVal();
				int amount = args.get("amount").getInt();
				String customName = (String) args.get("customName").getVal();
				
				for (int i = 0; i < amount; i++) {
					ExperienceOrb orb = (ExperienceOrb) DFPlugin.world.spawnEntity(loc, EntityType.EXPERIENCE_ORB);
					if (customName != null) orb.setCustomName(customName);
					orb.setCustomNameVisible(true);
				}
			}
			case "Explosion" -> {
				Location loc = (Location) args.get("loc").getVal();
				float power = (float) DFUtilities.clampNum((double) args.get("power").getVal(), 0, 4);
				
				DFPlugin.world.createExplosion(loc, power, false, false);
			}
			case "SpawnTNT" -> {
				Location loc = (Location) args.get("loc").getVal();
				float power = (float) DFUtilities.clampNum((double) args.get("power").getVal(), 0, 4);
				int fuse = args.get("fuse").getInt();
				String customName = (String) args.get("customName").getVal();
				
				TNTPrimed tnt = (TNTPrimed) DFPlugin.world.spawnEntity(loc, EntityType.PRIMED_TNT);
				
				EntityData.getEntityData(tnt.getUniqueId()).tntPower = power;
				tnt.setFuseTicks(fuse);
				if (customName != null) tnt.setCustomName(customName);
			}
			case "SpawnFangs" -> {
				Location loc = (Location) args.get("loc").getVal();
				String customName = (String) args.get("customName").getVal();
				
				EvokerFangs evokerFangs = (EvokerFangs) DFPlugin.world.spawnEntity(loc, EntityType.EVOKER_FANGS);
				if (customName != null) evokerFangs.setCustomName(customName);
			}
			case "Firework" -> {
				ItemStack fireworkType = (ItemStack) args.get("firework").getVal();
				Location loc = (Location) args.get("loc").getVal();
				
				Firework firework = (Firework) DFPlugin.world.spawnEntity(loc, EntityType.FIREWORK);
				FireworkMeta meta = (FireworkMeta) fireworkType.getItemMeta();
				firework.setFireworkMeta(meta);
				if (tags.get("Instant").equalsIgnoreCase("true")) firework.detonate();
				if (tags.get("Movement").equalsIgnoreCase("directional")) firework.setShotAtAngle(true);
			}
			case "LaunchProj" -> {
				ItemStack projectile = (ItemStack) args.get("projectile").getVal();
				Location loc = (Location) args.get("loc").getVal();
				String customName = (String) args.get("customName").getVal();
				Double speed = (Double) args.get("speed").getVal();
				double inaccuracy = (double) args.get("inaccuracy").getVal();
				
				DFUtilities.lastEntity = DFUtilities.launchProjectile(projectile, loc, speed == null ? null : (float) (double) speed, (float) inaccuracy, customName);
			}
			case "Lightning" -> DFPlugin.world.strikeLightning((Location) args.get("loc").getVal());
			case "SpawnPotionCloud" -> {
				Location loc = (Location) args.get("loc").getVal();
				double rad = (double) args.get("radius").getVal();
				double dur = (double) args.get("duration").getVal();
				
				AreaEffectCloud cloud = (AreaEffectCloud) DFPlugin.world.spawnEntity(loc, EntityType.AREA_EFFECT_CLOUD);
				for (PotionEffect effect : DFValue.castPotion((DFValue[]) args.get("potion").getVal()))
					cloud.addCustomEffect(effect, false);
				
				cloud.setRadius((float) rad);
				cloud.setDuration((int) dur);
			}
			case "FallingBlock" -> {
				Location loc = (Location) args.get("loc").getVal();
				Material block = ((ItemStack) args.get("block").getVal()).getType();
				
				BlockData finalData = block.createBlockData();
				if (args.get("blockData").getVal() != null) {
					StringBuilder builder = new StringBuilder();
					builder.append("[");
					for (String dblockData : DFValue.castTxt((DFValue[]) args.get("blockData").getVal()))
						builder.append(dblockData).append(",");
					
					builder.delete(builder.length() - 1, builder.length());
					builder.append("]");
					
					BlockData data = block.createBlockData(String.valueOf(builder));
					finalData = data.merge(finalData);
				}
				FallingBlock fb = DFPlugin.world.spawnFallingBlock(loc, finalData);
				
				if (tags.get("Reform on Impact").equalsIgnoreCase("false"))
					fb.setMetadata("dontreform1176", new FixedMetadataValue(DFPlugin.plugin, "1")); //TODO: This tag does not work properly!
				if (tags.get("Hurt Hit Entities").equalsIgnoreCase("true"))
					fb.setHurtEntities(true);
			}
			case "SpawnArmorStand" -> {
				Location loc = (Location) args.get("loc").getVal();
				String name = (String) args.get("customName").getVal();
				ArmorStand entity = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
				ItemStack[] equipment = DFValue.castItem(new DFValue[]{primitiveInput.get(18), primitiveInput.get(19), primitiveInput.get(20), primitiveInput.get(21), primitiveInput.get(22), primitiveInput.get(23)});
				entity.getEquipment().setArmorContents(new ItemStack[]{equipment[3], equipment[2], equipment[1], equipment[0]});
				entity.getEquipment().setItemInMainHand(equipment[4]);
				entity.getEquipment().setItemInOffHand(equipment[5]);
				entity.setGravity(false);
				entity.setInvulnerable(true);
				entity.setBasePlate(false);
				if (name != null) {
					entity.setCustomName(name);
					entity.setCustomNameVisible(true);
				}
				
				if (tags.get("Visibility").equals("Visible (No hitbox)")) entity.setMarker(true);
				if (tags.get("Visibility").equals("Invisible")) entity.setVisible(false);
				if (tags.get("Visibility").equals("Invisible (No hitbox)")) {
					entity.setMarker(true);
					entity.setVisible(false);
				}
				
				DFUtilities.lastEntity = entity;
			}
			case "SetBlock" -> {
				ItemStack block = (ItemStack) args.get("block").getVal();
				Material material = block == null ? Material.AIR : DFUtilities.interpretItem(block.getType());
				Location[] locs = DFValue.castLoc((DFValue[]) args.get("locs").getVal());
				String[] blockTags = DFValue.castTxt((DFValue[]) args.get("tags").getVal());
				
				for (Location loc : locs) {
					loc.getBlock().setType(material);
					if (args.get("tags").getVal() != null) {
						BlockData finalData = material.createBlockData();
						StringBuilder builder = new StringBuilder();
						builder.append("[");
						for (String dblockData : blockTags)
							builder.append(dblockData).append(",");
						
						builder.delete(builder.length() - 1, builder.length());
						builder.append("]");
						BlockData data = material.createBlockData(String.valueOf(builder));
						finalData = data.merge(finalData);
						loc.getBlock().setBlockData(finalData);
					}
				}
			}
			case "SetRegion" -> {
				Location loc1 = (Location) args.get("loc1").getVal();
				ItemStack temp = (ItemStack) args.get("block").getVal();
				Material material = temp == null ? Material.AIR : DFUtilities.interpretItem(temp.getType());
				String[] blockTags = DFValue.castTxt((DFValue[]) args.get("tags").getVal());
				BlockData finalData = material.createBlockData();
				StringBuilder builder = new StringBuilder();
				builder.append("[");
				for (String dblockData : blockTags)
					builder.append(dblockData).append(",");
				
				if (blockTags.length != 0) builder.delete(builder.length() - 1, builder.length());
				builder.append("]");
				BlockData data = material.createBlockData(String.valueOf(builder));
				finalData = data.merge(finalData);
				Location loc2 = (Location) args.get("loc2").getVal();
				World world = BukkitAdapter.adapt(loc1.getWorld());
				CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ()), BlockVector3.at(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ()));
				
				try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
					BlockState block = BukkitAdapter.adapt(finalData);
					
					try {
						editSession.setBlocks(selection, block);
					} catch (MaxChangedBlocksException e) {
						throw new RuntimeException(e);
					}
				}
				
			}
			case "CloneRegion" -> {
				try {
					Location loc1 = (Location) args.get("loc1").getVal();
					Location loc2 = (Location) args.get("loc2").getVal();
					Location copyLoc = (Location) args.get("copyLoc").getVal();
					Location pasteLoc = (Location) args.get("pasteLoc").getVal();
					World world = BukkitAdapter.adapt(loc1.getWorld());
					
					CuboidRegion region = new CuboidRegion(world, BlockVector3.at(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ()), BlockVector3.at(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ()));
					BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
					
					ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
						world, region, clipboard, BlockVector3.at(copyLoc.getBlockX(), copyLoc.getBlockY(), copyLoc.getBlockZ())
					);
					
					Operations.complete(forwardExtentCopy);
					
					if (tags.get("Ignore Air").equals("True")) {
						try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
							Operation operation = new ClipboardHolder(clipboard)
								.createPaste(editSession)
								.to(BlockVector3.at(pasteLoc.getBlockX(), pasteLoc.getBlockY(), pasteLoc.getBlockZ()))
								.ignoreAirBlocks(true)
								.build();
							Operations.complete(operation);
						}
					} else {
						
						try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
							Operation operation = new ClipboardHolder(clipboard)
								.createPaste(editSession)
								.to(BlockVector3.at(pasteLoc.getBlockX(), pasteLoc.getBlockY(), pasteLoc.getBlockZ()))
								.build();
							
							Operations.complete(operation);
						}
					}
				} catch (WorldEditException e) {
					e.printStackTrace();
				}
				
			}
			case "BreakBlock" -> {
				Location loc = (Location) args.get("loc").getVal();
				assert loc.getWorld() != null;
				for (Entity ps : loc.getWorld().getNearbyEntities(loc, 64, 64, 64))
					if (ps instanceof Player)
						((CraftPlayer) ps).getHandle().connection.send(new ClientboundLevelEventPacket(2001, new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), Block.getId(((CraftBlockData) loc.getBlock().getBlockData()).getState()), false));
				
				loc.getBlock().setType(Material.AIR);
			}
			case "SetBlockData" -> {
				String[] blockTags = DFValue.castTxt((DFValue[]) args.get("tags").getVal());
				Location[] locs = DFValue.castLoc((DFValue[]) args.get("loc").getVal());
				for (Location loc : locs) {

					Material material = loc.getBlock().getType();

					if (args.get("tags").getVal() != null) {
						if (tags.get("Overwrite Existing Data").equalsIgnoreCase("false")) {
							BlockData finalData = material.createBlockData();
							StringBuilder builder = new StringBuilder();
							builder.append("[");
							for (String dblockData : blockTags)
								builder.append(dblockData).append(",");

							builder.delete(builder.length() - 1, builder.length());
							builder.append("]");
							BlockData data = material.createBlockData(String.valueOf(builder));
							finalData = data.merge(finalData);
							loc.getBlock().setBlockData(finalData);
						} else {
							//TODO keep existing block data
						}
					}
				}
			}
			case "BoneMeal" -> {
				Location[] locs = DFValue.castLoc((DFValue[]) args.get("loc").getVal());
				Double itr = (Double) args.get("num").getVal();
				for (Location loc : locs)
				{
					for (int i = 0; i < itr; i++)
					{
						loc.getBlock().applyBoneMeal(BlockFace.DOWN);

						//TODO remove particles?
					}
				}

			}
			case "SetBlockGrowth" -> {
				Location loc = (Location) args.get("loc").getVal();
				Double num = (Double) args.get("num").getVal();
				Ageable age = (Ageable) loc.getBlock().getBlockData();
				age.setAge(num.intValue());
				
				loc.getBlock().setBlockData((BlockData) age);
			}
			case "FillContainer" -> {
				Location loc = (Location) args.get("loc").getVal();
				org.bukkit.block.BlockState blockState = loc.getBlock().getState();
				Container block = (Container) blockState;
				DFValue[] items = (DFValue[]) args.get("items").getVal();
				int itemIndex = 0;

				for (int i = 0; i < 27; i++) {
					ItemStack item = (ItemStack) items[i].getVal();

					if(item.getType() != Material.AIR){
						block.getInventory().setItem(i, (ItemStack) items[itemIndex].getVal());
						itemIndex++;
					}

				}

			}
			//TODO Tick block
			case "GenerateTree" -> {
				Location loc = (Location) args.get("loc").getVal();
				StringBuilder builder = new StringBuilder();
				builder.append(tags.get("Tree Type").toString());

				World world = BukkitAdapter.adapt(loc.getWorld());
				try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
					world.generateTree(TreeGenerator.TreeType.MEGA_REDWOOD,editSession,BlockVector3.at(loc.getX(),loc.getY(), loc.getZ()));
				} catch (MaxChangedBlocksException e) {
					throw new RuntimeException(e);
				}
				//TODO this doesnt work

			}
			case "SetContainer" -> {
				Location loc = (Location) args.get("loc").getVal();
				org.bukkit.block.BlockState blockState = loc.getBlock().getState();
				Container block = (Container) blockState;
				DFValue[] items = (DFValue[]) args.get("items").getVal();
				int itemIndex = 0;
				for (int i = 0; i < 27; i++) {
					if (items[itemIndex].slot != i + 1) block.getInventory().clear(i);
					else {
						Bukkit.broadcastMessage(i + "");
						block.getInventory().setItem(i, (ItemStack) items[itemIndex].getVal());
						if (itemIndex != items.length - 1) itemIndex++;
					}
				}
			}
			case "SetItemInSlot" ->{
				Location loc = (Location) args.get("loc").getVal();
				ItemStack item = (ItemStack) args.get("item").getVal();
				double slot = (double) args.get("slot").getVal();
				org.bukkit.block.BlockState blockState = loc.getBlock().getState();
				Container block = (Container) blockState;
				block.getInventory().setItem((int) slot,item);

			}
		}
	}
}
