package me.wonk2.utilities.actions;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.internals.EntityData;
import me.wonk2.utilities.values.DFValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.TippedArrowItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.type.Fire;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftProjectile;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GameAction {
	public static void invokeAction(Object[] inputArray, String action, LivingEntity target) {
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray[0]);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray[1]);
		
		switch (action) {
				case "SpawnMob": {
					HashMap<Material, EntityType> mobTypes = new HashMap<>(){{
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
					
					LivingEntity entity = (LivingEntity) target.getWorld().spawnEntity((Location) args.get("loc").getVal(), mobTypes.get(spawnEgg));
					if (args.get("health").getVal() == null)
						entity.setHealth(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
					else {
						entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue((Double) args.get("health").getVal());
						entity.setHealth((Double) args.get("health").getVal());
					}
					
					String customName = (String) args.get("customName").getVal();
					if(customName != null) entity.setCustomName(customName);
					
					if(args.get("effects").getVal() != null){
						PotionEffect[] effects = DFValue.castPotion((DFValue[]) args.get("effects").getVal());
						entity.addPotionEffects(Arrays.asList(effects));
					}
					
					ItemStack[] equipment = DFValue.castItem((DFValue[]) args.get("equipment").getVal());
					entity.getEquipment().setArmorContents(new ItemStack[]{equipment[4], equipment[3], equipment[2], equipment[1]});
					entity.getEquipment().setItemInMainHand(equipment[0]);
					entity.getEquipment().setItemInOffHand(equipment[5]);
					
					entity.setCustomNameVisible(true);
					break;
				}
				
				case "SpawnItem": {
					ItemStack[] items = DFValue.castItem((DFValue[]) args.get("items").getVal());
					Location loc = (Location) args.get("loc").getVal();
					String customName = (String) args.get("customName").getVal();
					
					for(ItemStack item : items){
						Entity itemEntity = target.getWorld().dropItem(loc, item);
						if(customName != null) itemEntity.setCustomName(customName);
						if(tags.get("Apply Item Motion") == "False") itemEntity.setVelocity(new Vector());
						itemEntity.setCustomNameVisible(true);
					}
					break;
				}
				
				case "SpawnVehicle": {
					Material vehicleType = ((ItemStack) args.get("vehicle").getVal()).getType();
					Location loc = (Location) args.get("loc").getVal();
					String customName = (String) args.get("customName").getVal();
					
					HashMap<Material, EntityType> vehicleTypes = new HashMap<>(){{
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
					switch(vehicleType){
						case OAK_BOAT:
						case OAK_CHEST_BOAT:
							((Boat) vehicle).setBoatType(Boat.Type.OAK);
							break;
						case SPRUCE_BOAT:
						case SPRUCE_CHEST_BOAT:
							((Boat) vehicle).setBoatType(Boat.Type.SPRUCE);
							break;
						case DARK_OAK_BOAT:
						case DARK_OAK_CHEST_BOAT:
							((Boat) vehicle).setBoatType(Boat.Type.DARK_OAK);
							break;
						case BIRCH_BOAT:
						case BIRCH_CHEST_BOAT:
							((Boat) vehicle).setBoatType(Boat.Type.BIRCH);
							break;
						case JUNGLE_BOAT:
						case JUNGLE_CHEST_BOAT:
							((Boat) vehicle).setBoatType(Boat.Type.JUNGLE);
							break;
						case ACACIA_BOAT:
						case ACACIA_CHEST_BOAT:
							((Boat) vehicle).setBoatType(Boat.Type.ACACIA);
							break;
						case MANGROVE_BOAT:
						case MANGROVE_CHEST_BOAT:
							((Boat) vehicle).setBoatType(Boat.Type.MANGROVE);
							break;
					}
					
					if(customName != null) vehicle.setCustomName(customName);
					vehicle.setCustomNameVisible(true);
					break;
				}
				
				case "SpawnExpOrb": {
					Location loc = (Location) args.get("loc").getVal();
					int amount = args.get("amount").getInt();
					String customName = (String) args.get("customName").getVal();
					
					for(int i = 0; i < amount; i++){
						ExperienceOrb orb = (ExperienceOrb) target.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
						if(customName != null) orb.setCustomName(customName);
						orb.setCustomNameVisible(true);
					}
					break;
				}
				
				case "Explosion": {
					Location loc = (Location) args.get("loc").getVal();
					float power = (float) DFUtilities.clampNum((double) args.get("power").getVal(), 0, 4);
					
					target.getWorld().createExplosion(loc, power);
					break;
				}
				
				case "SpawnTNT": {
					Location loc = (Location) args.get("loc").getVal();
					float power = (float) DFUtilities.clampNum((double) args.get("power").getVal(), 0, 4);
					int fuse = args.get("fuse").getInt();
					String customName = (String) args.get("customName").getVal();
					
					TNTPrimed tnt = (TNTPrimed) target.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);

					EntityData.getEntityData(tnt.getUniqueId()).tntPower = power;
					tnt.setFuseTicks(fuse);
					if(customName != null) tnt.setCustomName(customName);
					break;
				}
				
				case "SpawnFangs": {
					Location loc = (Location) args.get("loc").getVal();
					String customName = (String) args.get("customName").getVal();
					
					EvokerFangs evokerFangs = (EvokerFangs) target.getWorld().spawnEntity(loc, EntityType.EVOKER_FANGS);
					if(customName != null) evokerFangs.setCustomName(customName);
					break;
				}
				
				case "Firework": {
					ItemStack fireworkType = (ItemStack) args.get("firework").getVal();
					Location loc = (Location) args.get("loc").getVal();
					
					Firework firework = (Firework) target.getWorld().spawnEntity(loc, EntityType.FIREWORK);
					FireworkMeta fireworkMeta = firework.getFireworkMeta();
					break;
					//TODO: Modify firework meta or something
				}
				
				case "LaunchProj": {
					Material projType = ((ItemStack) args.get("projectile").getVal()).getType();
					ItemStack specialProj = (ItemStack) args.get("projectile").getVal();
					Location loc = (Location) args.get("loc").getVal();
					String customName = (String) args.get("customName").getVal();
					double speed = (double) args.get("speed").getVal();
					double inaccuracy = (double) args.get("inaccuracy").getVal();
					HashMap<Material,EntityType> projectiles= new HashMap<>(){{
						put(Material.SNOWBALL, EntityType.SNOWBALL);
						put(Material.EGG, EntityType.EGG);
						put(Material.ENDER_PEARL, EntityType.ENDER_PEARL);
						put(Material.TRIDENT, EntityType.TRIDENT);
						put(Material.ARROW, EntityType.ARROW);
						put(Material.SPECTRAL_ARROW, EntityType.SPECTRAL_ARROW);
						put(Material.MILK_BUCKET, EntityType.LLAMA_SPIT);
						put(Material.DRAGON_BREATH, EntityType.DRAGON_FIREBALL);
					}};
					EntityType type = projectiles.get(projType);
					Arrow projectile = target.getWorld().spawnArrow(loc,loc.getDirection(), (float) speed, (float) inaccuracy);
					switch (specialProj.getType()) {
						case FIRE_CHARGE: {
							if (specialProj.getAmount() == 2) {
								type = EntityType.FIREBALL;
								Fireball projectile2 = (Fireball) projectile.getWorld().spawnEntity(projectile.getLocation(), type);
								if (customName != null)
									projectile2.setCustomName(customName);
								projectile2.setDirection(projectile.getVelocity());
								projectile2.setCustomNameVisible(true);
								projectile.remove();
							} else {
								type = EntityType.SMALL_FIREBALL;
								SmallFireball projectile2 = (SmallFireball) projectile.getWorld().spawnEntity(projectile.getLocation(), type);
								if (customName != null)
									projectile2.setCustomName(customName);
								projectile2.setDirection(projectile.getVelocity());
								projectile2.setCustomNameVisible(true);
								projectile.remove();
							}
						}
						case WITHER_SKELETON_SKULL: {
							type = EntityType.WITHER_SKULL;
							WitherSkull projectile2 = (WitherSkull) projectile.getWorld().spawnEntity(projectile.getLocation(), type);
							if (specialProj.getAmount() == 2)
								projectile2.setCharged(true);
							if (customName != null)
								projectile2.setCustomName(customName);
							projectile2.setDirection(projectile.getVelocity());
							projectile2.setCustomNameVisible(true);
							projectile.remove();
						}
						case TIPPED_ARROW: {
							type = EntityType.ARROW;
							Arrow prj2 = (Arrow) projectile.getWorld().spawnEntity(projectile.getLocation(),type);
							PotionMeta meta = (PotionMeta) specialProj.getItemMeta();
							target.sendMessage(meta.getBasePotionData().toString());
							prj2.setBasePotionData(meta.getBasePotionData());
							if (customName != null)
								prj2.setCustomName(customName);
							prj2.setVelocity(projectile.getVelocity());
							prj2.setCustomNameVisible(true);
							projectile.remove();
						}
						case SPLASH_POTION: {
							type = EntityType.SPLASH_POTION;
							ThrownPotion prj2 = (ThrownPotion) projectile.getWorld().spawnEntity(projectile.getLocation(),type);
							prj2.setItem(specialProj);
							if (customName != null)
								prj2.setCustomName(customName);
							prj2.setVelocity(projectile.getVelocity());
							prj2.setCustomNameVisible(true);
							projectile.remove();
						}
						case LINGERING_POTION: {
							type = EntityType.SPLASH_POTION;
							ThrownPotion prj2 = (ThrownPotion) projectile.getWorld().spawnEntity(projectile.getLocation(),type);
							prj2.setItem(specialProj);
							if (customName != null)
								prj2.setCustomName(customName);
							prj2.setVelocity(projectile.getVelocity());
							prj2.setCustomNameVisible(true);
							projectile.remove();
						}
						case EXPERIENCE_BOTTLE:{
							ThrownExpBottle projectile2 = (ThrownExpBottle) projectile.getWorld().spawnEntity(projectile.getLocation(), EntityType.THROWN_EXP_BOTTLE);
							if (customName != null)
								projectile2.setCustomName(customName);
							projectile2.setVelocity(projectile.getVelocity());
							projectile2.setCustomNameVisible(true);
							projectile.remove();
						}
						if (projectiles.containsKey(projType)){
							Entity projectile2 = projectile.getWorld().spawnEntity(projectile.getLocation(), type);
							if (customName != null)
								projectile2.setCustomName(customName);
							projectile2.setVelocity(projectile.getVelocity());
							projectile2.setCustomNameVisible(true);
							projectile.remove();
						}
					}
					break;
				}
				case "Lightning": {
					target.getWorld().strikeLightning((Location) args.get("loc").getVal());
				}
				
			}
	}
}
