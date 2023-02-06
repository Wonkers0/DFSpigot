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
import org.bukkit.block.data.Ageable;
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
import java.util.Objects;

public class GameAction extends Action {
	LivingEntity target;
	Object[] inputArray;
	public GameAction(String targetName, HashMap<String, Entity[]> targetMap, ParamManager paramManager, String action) {
		super(targetName, targetMap, paramManager, action);
	}
	
	
	@Override
	public void invokeAction() {
		inputArray = paramManager.formatParameters(targetMap);
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray);
		target = (LivingEntity) DFUtilities.getTargets(targetName, targetMap, SelectionType.EITHER)[0];
		
		//TODO: Because of how the target system is set up, certain game actions may not work in entity events.
		HashMap<Integer, DFValue> primitiveInput = DFUtilities.getPrimitiveInput(inputArray);
		switch (action) {
			case "SpawnMob" -> {
				HashMap<Material, EntityType> mobTypes = new HashMap<>() {{
					put(Material.ALLAY_SPAWN_EGG, EntityType.ALLAY);
					put(Material.AXOLOTL_SPAWN_EGG, EntityType.AXOLOTL);
					put(Material.BAT_SPAWN_EGG, EntityType.BAT);
					put(Material.BEE_SPAWN_EGG, EntityType.BEE);
					put(Material.BLAZE_SPAWN_EGG, EntityType.BLAZE);
					put(Material.CAT_SPAWN_EGG, EntityType.CAT);
					put(Material.CAVE_SPIDER_SPAWN_EGG, EntityType.CAVE_SPIDER);
					put(Material.CHICKEN_SPAWN_EGG, EntityType.CHICKEN);
					put(Material.COD_SPAWN_EGG, EntityType.COD);
					put(Material.COW_SPAWN_EGG, EntityType.COW);
					put(Material.CREEPER_SPAWN_EGG, EntityType.CREEPER);
					put(Material.DOLPHIN_SPAWN_EGG, EntityType.DOLPHIN);
					put(Material.DONKEY_SPAWN_EGG, EntityType.DONKEY);
					put(Material.DROWNED_SPAWN_EGG, EntityType.DROWNED);
					put(Material.ELDER_GUARDIAN_SPAWN_EGG, EntityType.ELDER_GUARDIAN);
					put(Material.ENDERMAN_SPAWN_EGG, EntityType.ENDERMAN);
					put(Material.ENDERMITE_SPAWN_EGG, EntityType.ENDERMITE);
					put(Material.EVOKER_SPAWN_EGG, EntityType.EVOKER);
					put(Material.FOX_SPAWN_EGG, EntityType.FOX);
					put(Material.FROG_SPAWN_EGG, EntityType.FROG);
					put(Material.GHAST_SPAWN_EGG, EntityType.GHAST);
					put(Material.GLOW_SQUID_SPAWN_EGG, EntityType.GLOW_SQUID);
					put(Material.GOAT_SPAWN_EGG, EntityType.GOAT);
					put(Material.GUARDIAN_SPAWN_EGG, EntityType.GUARDIAN);
					put(Material.HOGLIN_SPAWN_EGG, EntityType.HOGLIN);
					put(Material.HORSE_SPAWN_EGG, EntityType.HORSE);
					put(Material.HUSK_SPAWN_EGG, EntityType.HUSK);
					put(Material.LLAMA_SPAWN_EGG, EntityType.LLAMA);
					put(Material.MAGMA_CUBE_SPAWN_EGG, EntityType.MAGMA_CUBE);
					put(Material.MOOSHROOM_SPAWN_EGG, EntityType.MUSHROOM_COW);
					put(Material.MULE_SPAWN_EGG, EntityType.MULE);
					put(Material.OCELOT_SPAWN_EGG, EntityType.OCELOT);
					put(Material.PANDA_SPAWN_EGG, EntityType.PANDA);
					put(Material.PARROT_SPAWN_EGG, EntityType.PARROT);
					put(Material.PHANTOM_SPAWN_EGG, EntityType.PHANTOM);
					put(Material.PIG_SPAWN_EGG, EntityType.PIG);
					put(Material.PIGLIN_SPAWN_EGG, EntityType.PIGLIN);
					put(Material.PIGLIN_BRUTE_SPAWN_EGG, EntityType.PIGLIN_BRUTE);
					put(Material.PILLAGER_SPAWN_EGG, EntityType.PILLAGER);
					put(Material.POLAR_BEAR_SPAWN_EGG, EntityType.POLAR_BEAR);
					put(Material.PUFFERFISH_SPAWN_EGG, EntityType.PUFFERFISH);
					put(Material.RABBIT_SPAWN_EGG, EntityType.RABBIT);
					put(Material.RAVAGER_SPAWN_EGG, EntityType.RAVAGER);
					put(Material.SALMON_SPAWN_EGG, EntityType.SALMON);
					put(Material.SHEEP_SPAWN_EGG, EntityType.SHEEP);
					put(Material.SHULKER_SPAWN_EGG, EntityType.SHULKER);
					put(Material.SILVERFISH_SPAWN_EGG, EntityType.SILVERFISH);
					put(Material.SKELETON_SPAWN_EGG, EntityType.SKELETON);
					put(Material.SKELETON_HORSE_SPAWN_EGG, EntityType.SKELETON_HORSE);
					put(Material.SLIME_SPAWN_EGG, EntityType.SLIME);
					put(Material.SPIDER_SPAWN_EGG, EntityType.SPIDER);
					put(Material.SQUID_SPAWN_EGG, EntityType.SQUID);
					put(Material.STRAY_SPAWN_EGG, EntityType.STRAY);
					put(Material.STRIDER_SPAWN_EGG, EntityType.STRIDER);
					put(Material.TADPOLE_SPAWN_EGG, EntityType.TADPOLE);
					put(Material.TRADER_LLAMA_SPAWN_EGG, EntityType.TRADER_LLAMA);
					put(Material.TROPICAL_FISH_SPAWN_EGG, EntityType.TROPICAL_FISH);
					put(Material.TURTLE_SPAWN_EGG, EntityType.TURTLE);
					put(Material.VEX_SPAWN_EGG, EntityType.VEX);
					put(Material.VILLAGER_SPAWN_EGG, EntityType.VILLAGER);
					put(Material.VINDICATOR_SPAWN_EGG, EntityType.VINDICATOR);
					put(Material.WANDERING_TRADER_SPAWN_EGG, EntityType.WANDERING_TRADER);
					put(Material.WARDEN_SPAWN_EGG, EntityType.WARDEN);
					put(Material.WITCH_SPAWN_EGG, EntityType.WITCH);
					put(Material.WITHER_SKELETON_SPAWN_EGG, EntityType.WITHER_SKELETON);
					put(Material.WOLF_SPAWN_EGG, EntityType.WOLF);
					put(Material.ZOGLIN_SPAWN_EGG, EntityType.ZOGLIN);
					put(Material.ZOMBIE_SPAWN_EGG, EntityType.ZOMBIE);
					put(Material.ZOMBIE_HORSE_SPAWN_EGG, EntityType.ZOMBIE_HORSE);
					put(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG, EntityType.ZOMBIFIED_PIGLIN);
				}};
				Material spawnEgg = ((ItemStack) args.get("mob").getVal()).getType();
				Location loc = (Location) args.get("loc").getVal();
				
				LivingEntity entity = (LivingEntity) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, mobTypes.get(spawnEgg));
				
				if (args.get("health").getVal() == null)
					entity.setHealth(Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
				else {
					Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue((Double) args.get("health").getVal());
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
				Objects.requireNonNull(entity.getEquipment()).setArmorContents(new ItemStack[]{equipment[4], equipment[3], equipment[2], equipment[1]});
				entity.getEquipment().setItemInMainHand(equipment[0]);
				entity.getEquipment().setItemInOffHand(equipment[5]);
				
				DFUtilities.lastEntity = entity;
			}
			case "SpawnItem" -> {
				ItemStack[] items = DFValue.castItem((DFValue[]) args.get("items").getVal());
				Location loc = (Location) args.get("loc").getVal();
				String customName = (String) args.get("customName").getVal();
				
				for (ItemStack item : items) {
					Entity itemEntity = target.getWorld().dropItem(loc, item);
					if (customName != null) itemEntity.setCustomName(customName);
					if (tags.get("Apply Item Motion").equals("False")) itemEntity.setVelocity(new Vector());
					itemEntity.setCustomNameVisible(true);
				}
			}
			case "SpawnVehicle" -> {
				Material vehicleType = ((ItemStack) args.get("vehicle").getVal()).getType();
				Location loc = (Location) args.get("loc").getVal();
				String customName = (String) args.get("customName").getVal();
				
				HashMap<Material, EntityType> vehicleTypes = new HashMap<>() {{
					put(Material.OAK_BOAT, EntityType.BOAT);
					put(Material.BIRCH_BOAT, EntityType.BOAT);
					put(Material.SPRUCE_BOAT, EntityType.BOAT);
					put(Material.DARK_OAK_BOAT, EntityType.BOAT);
					put(Material.ACACIA_BOAT, EntityType.BOAT);
					put(Material.MANGROVE_BOAT, EntityType.BOAT);
					put(Material.JUNGLE_BOAT, EntityType.BOAT);
					put(Material.OAK_CHEST_BOAT, EntityType.CHEST_BOAT);
					put(Material.BIRCH_CHEST_BOAT, EntityType.CHEST_BOAT);
					put(Material.SPRUCE_CHEST_BOAT, EntityType.CHEST_BOAT);
					put(Material.DARK_OAK_CHEST_BOAT, EntityType.CHEST_BOAT);
					put(Material.ACACIA_CHEST_BOAT, EntityType.CHEST_BOAT);
					put(Material.MANGROVE_CHEST_BOAT, EntityType.CHEST_BOAT);
					put(Material.JUNGLE_CHEST_BOAT, EntityType.CHEST_BOAT);
					put(Material.MINECART, EntityType.MINECART);
					put(Material.CHEST_MINECART, EntityType.MINECART_CHEST);
					put(Material.COMMAND_BLOCK_MINECART, EntityType.MINECART_COMMAND);
					put(Material.FURNACE_MINECART, EntityType.MINECART_FURNACE);
					put(Material.HOPPER_MINECART, EntityType.MINECART_HOPPER);
					put(Material.TNT_MINECART, EntityType.MINECART_TNT);
				}};
				
				Vehicle vehicle = (Vehicle) target.getWorld().spawnEntity(loc, vehicleTypes.get(vehicleType));
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
					ExperienceOrb orb = (ExperienceOrb) target.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
					if (customName != null) orb.setCustomName(customName);
					orb.setCustomNameVisible(true);
				}
			}
			case "Explosion" -> {
				Location loc = (Location) args.get("loc").getVal();
				float power = (float) DFUtilities.clampNum((double) args.get("power").getVal(), 0, 4);
				
				target.getWorld().createExplosion(loc, power);
			}
			case "SpawnTNT" -> {
				Location loc = (Location) args.get("loc").getVal();
				float power = (float) DFUtilities.clampNum((double) args.get("power").getVal(), 0, 4);
				int fuse = args.get("fuse").getInt();
				String customName = (String) args.get("customName").getVal();
				
				TNTPrimed tnt = (TNTPrimed) target.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
				
				EntityData.getEntityData(tnt.getUniqueId()).tntPower = power;
				tnt.setFuseTicks(fuse);
				if (customName != null) tnt.setCustomName(customName);
			}
			case "SpawnFangs" -> {
				Location loc = (Location) args.get("loc").getVal();
				String customName = (String) args.get("customName").getVal();
				
				EvokerFangs evokerFangs = (EvokerFangs) target.getWorld().spawnEntity(loc, EntityType.EVOKER_FANGS);
				if (customName != null) evokerFangs.setCustomName(customName);
			}
			case "Firework" -> {
				ItemStack fireworkType = (ItemStack) args.get("firework").getVal();
				Location loc = (Location) args.get("loc").getVal();
				
				Firework firework = (Firework) target.getWorld().spawnEntity(loc, EntityType.FIREWORK);
				FireworkMeta meta = (FireworkMeta) fireworkType.getItemMeta();
				assert meta != null;
				firework.setFireworkMeta(meta);
				if (tags.get("Instant").equalsIgnoreCase("true")) firework.detonate();
				if (tags.get("Movement").equalsIgnoreCase("directional")) firework.setShotAtAngle(true);
			}
			case "LaunchProj" -> {
				HashMap<Material, EntityType> projectiles = new HashMap<>() {{
					put(Material.SNOWBALL, EntityType.SNOWBALL);
					put(Material.EGG, EntityType.EGG);
					put(Material.ENDER_PEARL, EntityType.ENDER_PEARL);
					put(Material.TRIDENT, EntityType.TRIDENT);
					put(Material.ARROW, EntityType.ARROW);
					put(Material.SPECTRAL_ARROW, EntityType.SPECTRAL_ARROW);
					put(Material.MILK_BUCKET, EntityType.LLAMA_SPIT);
					put(Material.DRAGON_BREATH, EntityType.DRAGON_FIREBALL);
				}};
				
				
				Material projType = ((ItemStack) args.get("projectile").getVal()).getType();
				ItemStack specialProj = (ItemStack) args.get("projectile").getVal();
				Location loc = (Location) args.get("loc").getVal();
				String customName = (String) args.get("customName").getVal();
				double speed = (double) args.get("speed").getVal();
				double inaccuracy = (double) args.get("inaccuracy").getVal();
				
				
				EntityType type = projectiles.get(projType);
				Arrow arrow = target.getWorld().spawnArrow(loc, loc.getDirection(), (float) speed, (float) inaccuracy);
				switch (specialProj.getType()) {
					case FIRE_CHARGE -> {
						if (specialProj.getAmount() >= 2) {
							Fireball proj = (Fireball) target.getWorld().spawnEntity(loc, EntityType.FIREBALL);
							proj.setDirection(arrow.getVelocity());
							
							if (customName != null) proj.setCustomName(customName);
							proj.setCustomNameVisible(true);
						} else {
							SmallFireball proj = (SmallFireball) target.getWorld().spawnEntity(loc, EntityType.SMALL_FIREBALL);
							proj.setDirection(arrow.getVelocity());
							
							if (customName != null) proj.setCustomName(customName);
							proj.setCustomNameVisible(true);
						}
						
					}
					case WITHER_SKELETON_SKULL -> {
						WitherSkull proj = (WitherSkull) arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.WITHER_SKULL);
						proj.setCharged(specialProj.getAmount() >= 2);
						proj.setDirection(arrow.getVelocity());
						
						if (customName != null) proj.setCustomName(customName);
						proj.setCustomNameVisible(true);
					}
					case TIPPED_ARROW -> {
						Arrow proj = (Arrow) arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.ARROW);
						proj.setBasePotionData(((PotionMeta) Objects.requireNonNull(specialProj.getItemMeta())).getBasePotionData());
						proj.setVelocity(arrow.getVelocity());
						
						if (customName != null) proj.setCustomName(customName);
						proj.setCustomNameVisible(true);
					}
					case SPLASH_POTION, LINGERING_POTION -> {
						ThrownPotion proj = (ThrownPotion) target.getWorld().spawnEntity(loc, EntityType.SPLASH_POTION);
						proj.setItem(specialProj);
						proj.setVelocity(arrow.getVelocity());
						
						if (customName != null) proj.setCustomName(customName);
						proj.setCustomNameVisible(true);
					}
					case EXPERIENCE_BOTTLE -> {
						ThrownExpBottle proj = (ThrownExpBottle) target.getWorld().spawnEntity(loc, EntityType.THROWN_EXP_BOTTLE);
						proj.setVelocity(arrow.getVelocity());
						
						if (customName != null) proj.setCustomName(customName);
						proj.setCustomNameVisible(true);
					}
					default -> {
						if (projectiles.containsKey(projType)) {
							Entity proj = target.getWorld().spawnEntity(loc, type);
							proj.setVelocity(arrow.getVelocity());
							
							if (customName != null) proj.setCustomName(customName);
							proj.setCustomNameVisible(true);
						}
					}
				}
				
				arrow.remove();
			}
			case "Lightning" -> target.getWorld().strikeLightning((Location) args.get("loc").getVal());
			case "SpawnPotionCloud" -> {
				Location loc = (Location) args.get("loc").getVal();
				double rad = (double) args.get("radius").getVal();
				double dur = (double) args.get("duration").getVal();
				
				AreaEffectCloud cloud = (AreaEffectCloud) target.getWorld().spawnEntity(loc, EntityType.AREA_EFFECT_CLOUD);
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
				FallingBlock fb = target.getWorld().spawnFallingBlock(loc, finalData);
				
				if (tags.get("Reform on Impact").equalsIgnoreCase("false"))
					fb.setMetadata("dontreform1176", new FixedMetadataValue(DFPlugin.plugin, "1")); //TODO: This tag does not work properly!
				if (tags.get("Hurt Hit Entities").equalsIgnoreCase("true"))
					fb.setHurtEntities(true);
			}
			case "SpawnArmorStand" -> {
				Location loc = (Location) args.get("loc").getVal();
				String name = (String) args.get("customName").getVal();
				ArmorStand entity = (ArmorStand) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, EntityType.ARMOR_STAND);
				ItemStack[] equipment = DFValue.castItem(new DFValue[]{primitiveInput.get(18), primitiveInput.get(19), primitiveInput.get(20), primitiveInput.get(21), primitiveInput.get(22), primitiveInput.get(23)});
				Objects.requireNonNull(entity.getEquipment()).setArmorContents(new ItemStack[]{equipment[3], equipment[2], equipment[1], equipment[0]});
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
				World world = BukkitAdapter.adapt(Objects.requireNonNull(loc1.getWorld()));
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
					World world = BukkitAdapter.adapt(Objects.requireNonNull(loc1.getWorld()));
					
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
						((CraftPlayer) target).getHandle().connection.send(new ClientboundLevelEventPacket(2001, new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), Block.getId(((CraftBlockData) loc.getBlock().getBlockData()).getState()), false));
				
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
				loc.getBlock().setBlockData(age);

			}
			case "FillContainer" -> {
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
		}
	}
}
