package me.wonk2.utilities.actions;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.enums.SelectionType;
import me.wonk2.utilities.internals.EntityData;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R1.entity.*;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class EntityAction extends Action {
	LivingEntity target;
	
	public EntityAction(String targetName, HashMap<String, Entity[]> targetMap, ParamManager paramManager, String action, HashMap<String, DFValue> localStorage) {
		super(targetName, targetMap, paramManager, action, localStorage);
	}
	
	@Override
	public void invokeAction() {
		Object[] inputArray = paramManager.formatParameters(targetMap);
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray);
		
		for(Entity entity : DFUtilities.getTargets(targetName, targetMap, SelectionType.ENTITY)) {
			LivingEntity target = entity instanceof LivingEntity livingEntity ? livingEntity : null;
			switch (action) {
				case "Damage" -> target.damage((double) args.get("amount").getVal());
				
				case "Heal" -> {
					Integer amount = args.get("amount").getInt();
					if (amount == null) target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
					else target.setHealth(target.getHealth() + amount);
				}
				
				case "SetHealth" -> target.setHealth(args.get("amount").getInt());
				
				case "SetMaxHealth" -> {
					target.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue((double) args.get("amount").getVal());
					if (tags.get("Heal Player to Max Health") == "True")
						target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				}
				
				case "SetAbsorption" -> target.setAbsorptionAmount((double) args.get("amount").getVal());
				
				case "SetFireTicks" -> target.setFireTicks(args.get("ticks").getInt());
				
				case "SetFreezeTicks" -> target.setFreezeTicks(args.get("ticks").getInt());
				
				case "SetInvulTicks" -> target.setNoDamageTicks(args.get("ticks").getInt());
				
				case "GivePotion" -> target.addPotionEffects(Arrays.asList(DFValue.castPotion((DFValue[]) args.get("effects").getVal())));
				
				case "RemovePotion" -> DFUtilities.removePotions(target, DFValue.castPotion((DFValue[]) args.get("effects").getVal()));
				
				case "ClearPotions" -> DFUtilities.removePotions(target, target.getActivePotionEffects().toArray(new PotionEffect[0]));
				
				case "SetAge" -> {
					Breedable breedable = (Breedable) target;
					String ageLock = tags.get("Age Lock");
					
					breedable.setAge(args.get("age").getInt());
					if (!ageLock.equals("Don't change")) breedable.setAgeLock(ageLock.equals("Enable"));
				}
				
				case "SetFallDistance" -> target.setFallDistance((float) (double) args.get("distance").getVal());
				
				case "SetCreeperFuse" -> ((Creeper) target).setFuseTicks(args.get("ticks").getInt());
				
				case "SetCreeperPower" -> {
					//TODO
				}
				
				case "SetCloudRadius" -> {
					AreaEffectCloud cloud = (AreaEffectCloud) target;
					float radius = (float) (double) args.get("radius").getVal();
					Object shrinkSpeed = args.get("shrinkSpeed").getVal();
					
					cloud.setRadius(radius);
					if (shrinkSpeed != null) cloud.setRadiusPerTick(Math.abs((float) (double) shrinkSpeed));
				}
				
				case "SetVillagerExp" -> ((Villager) target).setVillagerExperience(args.get("amount").getInt());
				
				case "SetWitherInvul" -> ((WitherBoss) target).setInvulnerableTicks(args.get("ticks").getInt()); // Need to test
				
				case "SetHorseJump" -> ((Horse) target).setJumpStrength((double) args.get("strength").getVal());
				
				case "SetPickupDelay" -> ((Item) target).setPickupDelay(args.get("ticks").getInt());
				
				case "SetFishingTime" -> ((FishHook) target).setMaxWaitTime(args.get("ticks").getInt());
				
				case "Teleport" -> {
					Location loc = (Location) args.get("loc").getVal();
					if (tags.get("Keep Current Rotation").equals("False")) target.teleport(loc);
					else target.teleport(loc.setDirection(target.getEyeLocation().getDirection()));
				}
				
				case "LaunchUp" -> {
					double launchVelocity = (double) args.get("launch").getVal() / 10;
					if (tags.get("Add to Current Velocity").equals("False"))
						target.setVelocity(target.getVelocity().setY(launchVelocity));
					else target.setVelocity(target.getVelocity().add(new Vector(0, launchVelocity, 0)));
				}
				
				case "LaunchFwd" -> {
					double launchVelocity = (double) args.get("launch").getVal() / 10;
					Location eyeLoc = target.getEyeLocation();
					if (tags.get("Launch Axis").equals("Yaw Only")) eyeLoc.setPitch(0);
					
					Vector launchVector = eyeLoc.getDirection().normalize();
					if (tags.get("Add to Current Velocity").equals("True"))
						target.setVelocity(target.getVelocity().add(launchVector.multiply(launchVelocity)));
					else target.setVelocity(launchVector.multiply(launchVelocity));
				}
				
				case "LaunchToward" -> {
					Location loc = (Location) args.get("loc").getVal();
					double launchVelocity = (double) args.get("launch").getVal() / 10;
					Vector launchVector = loc.toVector().subtract(target.getLocation().toVector());
					if (tags.get("Ignore Distance").equals("True")) launchVector.normalize();
					
					if (tags.get("Add to Current Velocity").equals("True"))
						target.setVelocity(target.getVelocity().add(launchVector.multiply(launchVelocity)));
					else target.setVelocity(launchVector.multiply(launchVelocity));
				}
				
				case "RideEntity" -> {
					if (args.get("uuid").getVal() == null) Objects.requireNonNull(target.getVehicle()).eject();
					else DFUtilities.getEntity((String) args.get("uuid").getVal());
				}
				
				case "SetGliding" -> target.setGliding(tags.get("Gliding").equals("Enable"));
				
				case "SetGravity" -> target.setGravity(tags.get("Gravity").equals("Enable"));
				
				case "AttachLead" -> {
					break; //TODO
				}
				
				case "SetRotation" -> {
					float pitch = (float) (double) args.get("pitch").getVal();
					float yaw = (float) (double) args.get("yaw").getVal();
					target.setRotation(pitch, yaw);
				}
				
				case "SetVelocity" -> {
					Vector vector = (Vector) args.get("vector").getVal();
					if (tags.get("Add to Current Velocity") == "False") target.setVelocity(vector);
					else target.setVelocity(target.getVelocity().add(vector));
				}
				
				case "SetName" -> {
					Object name = args.get("name").getVal();
					
					if (name == null) ; //TODO: Remove custom name
					target.setCustomName((String) name);
					target.setCustomNameVisible(true);
				}
				
				case "SetNameVisible" -> target.setCustomNameVisible(tags.get("Name Tag Visible").equals("Enable"));
				
				case "SetNameColor" -> {
					break; //TODO
				}
				
				case "SetAI" -> {
					((Mob) target).setAware(tags.get("AI").equals("Sentient"));
					if (tags.get("AI").equals("None")) target.setAI(false);
				}
				
				case "SetSilenced" -> target.setSilent(tags.get("Silenced").equals("Enable"));
				
				case "SetDeathDrops" -> EntityData.getEntityData(target.getUniqueId()).deathDrops = tags.get("Has Death Drops").equals("Enable");
				
				case "SetCollidable" -> target.setCollidable(tags.get("Collision").equals("Enable"));
				
				case "SetInvulnerable" -> target.setInvulnerable(tags.get("Invulnerable").equals("Enable"));
				
				case "SetBaby" -> {
					Breedable breedable = (Breedable) target;
					
					if (tags.get("Baby").equals("Enable")) breedable.setBaby();
					else breedable.setAdult();
				}
				
				case "SetSize" -> {
					int size = args.get("size").getInt();
					if (target instanceof Slime) ((Slime) target).setSize(size); // MagmaCube extends Slime
					else if (target instanceof PufferFish) ((PufferFish) target).setPuffState(size);
					else if (target instanceof Phantom) ((Phantom) target).setSize(size);
				}
				
				case "SetSheepSheared" -> ((Sheep) target).setSheared(tags.get("Sheared").equals("Enable"));
				
				case "SetSaddle" -> setSaddle(target, tags.get("Saddle").equals("Enable") ? new ItemStack(Material.SADDLE) : null);
				
				case "SetCarryingChest" -> ((ChestedHorse) target).setCarryingChest(tags.get("Carrying Chest").equals("Enable"));
				
				case "ArmorStandSlots" -> {
					break; //TODO
				}
				
				case "SetMarker" -> ((ArmorStand) target).setMarker(tags.get("Marker").equals("Enable"));
				
				case "SetAngry" -> ((Bee) target).setAnger(tags.get("Angry").equals("Enable") ? 1 : 0); // Need to test
				
				case "SetRearing" -> {
					break; //TODO
				}
				
				case "SetRiptiding" -> {
					break; //TODO
				}
				
				case "CreeperCharged" -> ((Creeper) target).setPowered(tags.get("Charged").equals("Enable"));
				
				case "SetInvisible" -> target.setInvisible(tags.get("Invisible").equals("Enable"));
				
				case "SetGoatScreaming" -> ((Goat) target).setScreaming(tags.get("Screams").equals("Enable"));
				
				case "Tame" -> {
					Tameable tameable = (Tameable) target;
					Object owner = args.get("owner").getVal();
					
					tameable.setTamed(false); // Tameable#setOwner automatically tames an entity
					if (owner != null) tameable.setOwner(DFUtilities.getPlayer((String) owner));
				}
				
				case "EndCrystalBeam" -> ((EnderCrystal) target).setBeamTarget((Location) args.get("loc").getVal());
				
				case "SetPandaGene" -> {
					HashMap<String, Panda.Gene> pandaGenes = new HashMap<>() {{
						put("Aggressive", Panda.Gene.AGGRESSIVE);
						put("Lazy", Panda.Gene.LAZY);
						put("Weak", Panda.Gene.WEAK);
						put("Worried", Panda.Gene.WORRIED);
						put("Playful", Panda.Gene.PLAYFUL);
						put("Normal", Panda.Gene.NORMAL);
						put("Brown", Panda.Gene.BROWN);
					}};
					Panda.Gene gene = pandaGenes.get(tags.get("Gene Type"));
					
					if (tags.get("Set Gene").equals("Main gene") || tags.get("Set Gene").equals("Both"))
						((Panda) target).setMainGene(gene);
					if (tags.get("Set Gene").equals("Hidden gene") || tags.get("Set Gene").equals("Both"))
						((Panda) target).setHiddenGene(gene);
				}
				
				case "SetProfession" -> {
					HashMap<String, Villager.Profession> villagerProfessions = new HashMap<>() {{
						put("Unemployed", Villager.Profession.NONE);
						put("Armorer", Villager.Profession.ARMORER);
						put("Butcher", Villager.Profession.BUTCHER);
						put("Cartographer", Villager.Profession.CARTOGRAPHER);
						put("Cleric", Villager.Profession.CLERIC);
						put("Farmer", Villager.Profession.FARMER);
						put("Fisherman", Villager.Profession.FISHERMAN);
						put("Fletcher", Villager.Profession.FLETCHER);
						put("Leatherworker", Villager.Profession.LEATHERWORKER);
						put("Librarian", Villager.Profession.LIBRARIAN);
						put("Mason", Villager.Profession.MASON);
						put("Nitwit", Villager.Profession.NITWIT);
						put("Shepherd", Villager.Profession.SHEPHERD);
						put("Toolsmith", Villager.Profession.TOOLSMITH);
						put("Weaponsmith", Villager.Profession.WEAPONSMITH);
					}};
					
					((Villager) target).setProfession(villagerProfessions.get(tags.get("Profession")));
				}
				
				case "SetProjSource" -> {
					Object shooter = args.get("shooter").getVal();
					LivingEntity projectileSrc = shooter == null ? null : (LivingEntity) DFUtilities.getEntityOrPlayer((String) shooter);
					((Projectile) target).setShooter(projectileSrc);
				}
				
				case "SetPersistent" -> target.setPersistent(tags.get("Persistent").equals("Enable"));
				
				case "SetGlowing" -> target.setGlowing(tags.get("Glowing").equals("Enable"));
				
				case "SetDyeColor" -> {
					HashMap<String, DyeColor> colors = new HashMap<>() {{
						put("White", DyeColor.WHITE);
						put("Orange", DyeColor.ORANGE);
						put("Magenta", DyeColor.MAGENTA);
						put("Light blue", DyeColor.LIGHT_BLUE);
						put("Yellow", DyeColor.YELLOW);
						put("Lime", DyeColor.LIME);
						put("Pink", DyeColor.PINK);
						put("Gray", DyeColor.GRAY);
						put("Light gray", DyeColor.LIGHT_GRAY);
						put("Cyan", DyeColor.CYAN);
						put("Purple", DyeColor.PURPLE);
						put("Blue", DyeColor.BLUE);
						put("Brown", DyeColor.BROWN);
						put("Green", DyeColor.GREEN);
						put("Red", DyeColor.RED);
						put("Black", DyeColor.BLACK);
					}};
					
					DyeColor color = colors.get(tags.get("Dye"));
					if (target instanceof Sheep) ((Sheep) target).setColor(color);
					else if (target instanceof Shulker) ((Shulker) target).setColor(color);
					else if (target instanceof Cat) ((Cat) target).setCollarColor(color);
					else if (target instanceof Wolf) ((Wolf) target).setCollarColor(color);
				}
				
				case "SetFishPattern" -> {
					break; //TODO
				}
				
				case "SetRabbitType" -> {
					HashMap<String, Rabbit.Type> rabbitTypes = new HashMap<>() {{
						put("Brown", Rabbit.Type.BROWN);
						put("White", Rabbit.Type.WHITE);
						put("Black", Rabbit.Type.BLACK);
						put("Black and White", Rabbit.Type.BLACK_AND_WHITE);
						put("Gold", Rabbit.Type.GOLD);
						put("Salt and Pepper", Rabbit.Type.SALT_AND_PEPPER);
						put("Killer", Rabbit.Type.THE_KILLER_BUNNY);
					}};
					
					((Rabbit) target).setRabbitType(rabbitTypes.get(tags.get("Skin Type")));
				}
				
				case "SetCatType" -> {
					HashMap<String, Cat.Type> catTypes = new HashMap<>() {{
						put("Tabby", Cat.Type.TABBY);
						put("Tuxedo", Cat.Type.BLACK);
						put("Red", Cat.Type.RED);
						put("Siamese", Cat.Type.SIAMESE);
						put("British Shorthair", Cat.Type.BRITISH_SHORTHAIR);
						put("Calico", Cat.Type.CALICO);
						put("Persian", Cat.Type.PERSIAN);
						put("Ragdoll", Cat.Type.RAGDOLL);
						put("White", Cat.Type.WHITE);
						put("Jellie", Cat.Type.JELLIE);
						put("Black", Cat.Type.ALL_BLACK);
					}};
					
					
					((Cat) target).setCatType(catTypes.get(tags.get("Skin Type")));
				}
				
				case "MooshroomType" -> {
					HashMap<String, MushroomCow.Variant> mooshroomVariants = new HashMap<>() {{
						put("Red", MushroomCow.Variant.RED);
						put("Brown", MushroomCow.Variant.BROWN);
					}};
					
					((MushroomCow) target).setVariant(mooshroomVariants.get("Mooshroom Variant"));
				}
				
				case "SetFoxType" -> {
					HashMap<String, Fox.Type> foxTypes = new HashMap<>() {{
						put("Red", Fox.Type.RED);
						put("Snow", Fox.Type.SNOW);
					}};
					
					((Fox) target).setFoxType(foxTypes.get(tags.get("Fox Type")));
				}
				
				case "SetParrotColor" -> {
					HashMap<String, Parrot.Variant> parrotVariants = new HashMap<>() {{
						put("Red", Parrot.Variant.RED);
						put("Blue", Parrot.Variant.BLUE);
						put("Green", Parrot.Variant.GREEN);
						put("Cyan", Parrot.Variant.CYAN);
						put("Gray", Parrot.Variant.GRAY);
					}};
					
					((Parrot) target).setVariant(parrotVariants.get(tags.get("Parrot Color")));
				}
				
				case "SetHorsePattern" -> {
					break; //TODO
				}
				
				case "SetAxolotlColor" -> {
					HashMap<String, Axolotl.Variant> axolotlVariants = new HashMap<>() {{
						put("Pink", Axolotl.Variant.LUCY);
						put("Brown", Axolotl.Variant.WILD);
						put("Yellow", Axolotl.Variant.GOLD);
						put("Cyan", Axolotl.Variant.CYAN);
						put("Blue", Axolotl.Variant.BLUE);
					}};
					
					((Axolotl) target).setVariant(axolotlVariants.get(tags.get("Axolotl Color")));
				}
				
				case "SetLlamaColor" -> {
					HashMap<String, Llama.Color> llamaColors = new HashMap<>() {{
						put("Brown", Llama.Color.BROWN);
						put("Creamy", Llama.Color.CREAMY);
						put("White", Llama.Color.WHITE);
						put("Gray", Llama.Color.GRAY);
					}};
					
					((Llama) target).setColor(llamaColors.get(tags.get("Llama Color")));
				}
				
				case "SetVillagerBiome" -> {
					HashMap<String, Villager.Type> villagerTypes = new HashMap<>() {{
						put("Desert", Villager.Type.DESERT);
						put("Jungle", Villager.Type.JUNGLE);
						put("Plains", Villager.Type.PLAINS);
						put("Savanna", Villager.Type.SAVANNA);
						put("Snow", Villager.Type.SNOW);
						put("Swamp", Villager.Type.SWAMP);
						put("Taiga", Villager.Type.TAIGA);
					}};
					
					((Villager) target).setVillagerType(villagerTypes.get(tags.get("Biome")));
				}
				
				case "SnowmanPumpkin" -> ((Snowman) target).setDerp(tags.get("Pumpkin").equals("Enable"));
				
				case "SetEndermanBlock" -> ((Enderman) target).setCarriedMaterial(Objects.requireNonNull(((ItemStack) args.get("item").getVal()).getData()));
				
				case "SetMinecartBlock" -> {
					((Minecart) target).setDisplayBlock(((ItemStack) args.get("item").getVal()).getData());
					
					DFValue offset = args.get("offset");
					if (offset.getVal() != null) ((Minecart) target).setDisplayBlockOffset(offset.getInt());
				}
				
				case "ArmorStandParts" -> {
					ArmorStand armorStand = (ArmorStand) target;
					String arms = tags.get("Arms");
					String baseplate = tags.get("Base Plate");
					
					if (!arms.equals("Don't change")) armorStand.setArms(arms.equals("Enable"));
					if (!baseplate.equals("Don't change")) armorStand.setBasePlate(baseplate.equals("Enable"));
				}
				
				case "SetBeeNectar" -> ((Bee) target).setHasNectar(tags.get("Has Nectar").equals("Enable"));
				
				case "ProjectileItem" -> {
					net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy((ItemStack) args.get("item").getVal());
					
					if(entity instanceof Snowball) ((CraftSnowball) entity).getHandle().setItem(item);
					else if(entity instanceof Egg) ((CraftEgg) entity).getHandle().setItem(item);
					else if(entity instanceof SmallFireball) ((CraftSmallFireball) entity).getHandle().setItem(item);
					else if(entity instanceof LargeFireball) ((CraftLargeFireball) entity).getHandle().setItem(item);
					else if(entity instanceof EnderPearl) ((CraftEnderPearl) entity).getHandle().setItem(item);
					else if(entity instanceof ThrownExpBottle) ((CraftThrownExpBottle) entity).getHandle().setItem(item);
					else if(entity instanceof EyeOfEnder eye) eye.setItem(item);
				}
				
				case "SetVisualFire" -> target.setVisualFire(tags.get("On Fire").equals("True"));
				
				case "SendAnimation" -> {
					break; //TODO
				}
				
				case "AttackAnimation" -> {
					if (tags.get("Animation Arm").equals("Swing main arm")) target.swingMainHand();
					else target.swingOffHand();
				}
				
				case "ArmorStandPose" -> {
					Vector vector = new Vector(0, 0, 0);
					if (args.get("vector") != null) vector = (Vector) args.get("vector").getVal();
					
					double x = 0;
					double y = 0;
					double z = 0;
					
					if (args.get("x") != null) x = (double) args.get("x").getVal();
					if (args.get("y") != null) y = (double) args.get("y").getVal();
					if (args.get("z") != null) z = (double) args.get("z").getVal();
					
					ArmorStand a = (ArmorStand) target;
					EulerAngle angle = vector != null ? new EulerAngle(vector.getX(), vector.getY(), vector.getZ()) : new EulerAngle(x, y, z);
					
					switch (tags.get("Armor Stand Part")) {
						case "Head" -> a.setHeadPose(angle);
						case "Body" -> a.setBodyPose(angle);
						case "Left Arm" -> a.setLeftArmPose(angle);
						case "Right Arm" -> a.setRightArmPose(angle);
						case "Left Leg" -> a.setLeftLegPose(angle);
						case "Right Leg" -> a.setRightLegPose(angle);
					}
				}
				
				case "SetPose" -> {
					switch (tags.get("Pose")) {
						case "Standing":
							break; //TODO
						case "Sleeping":
							break; //TODO
						case "Swimming":
							target.setSwimming(true);
							break;
						case "Sneaking":
							break; //TODO
					}
				}
				
				case "SetFoxLeaping" -> {
					//TODO: I don't even know if this works in DiamondFire...
				}
				
				case "SetArmsRaised" -> {/*TODO*/}
				
				case "SetCatResting" -> {/*TODO*/}
				
				case "SetGlowSquidDark" -> ((GlowSquid) target).setDarkTicksRemaining(args.get("ticks").getInt());
				
				case "SetCelebrating" -> {/*TODO*/}
				
				case "SetTarget" -> ((Mob) target).setTarget((LivingEntity) DFUtilities.getEntityOrPlayer((String) args.get("target").getVal()));
				
				case "MoveToLoc" -> {/*TODO*/}
				
				case "Jump" -> {/*TODO*/}
				
				case "Ram" -> {/*TODO*/}
				
				case "SheepEat" -> {/*TODO*/}
				
				case "IgniteCreeper" -> ((Creeper) target).ignite();
				
				case "Explode" -> {
					if (target instanceof Creeper) ((Creeper) target).explode();
					else if (target instanceof PrimedTnt) ((PrimedTnt) target).setFuse(0);
					else if (target instanceof Firework) ((Firework) target).detonate();
				}
				
				case "FoxSleeping" -> ((Fox) target).setSleeping(tags.get("Sleeping").equals("Enable"));
				
				case "SetDragonPhase" -> {
					HashMap<String, EnderDragon.Phase> dragonPhases = new HashMap<>() {{
						put("Flying", EnderDragon.Phase.FLY_TO_PORTAL);
						put("Hovering", EnderDragon.Phase.HOVER);
						put("Breath Attack", EnderDragon.Phase.BREATH_ATTACK);
						put("Dying", EnderDragon.Phase.DYING);
					}};
					
					((EnderDragon) target).setPhase(dragonPhases.get(tags.get("Phase")));
				}
				
				case "SetBulletTarget" -> ((ShulkerBullet) target).setTarget(DFUtilities.getEntityOrPlayer((String) args.get("target").getVal()));
				
				case "UseItem" -> {/*TODO*/}
				
				case "Remove" -> target.remove();
				
				case "SetEquipment" -> {
					HashMap<String, EquipmentSlot> equipmentSlots = new HashMap<>() {{
						put("Main hand", EquipmentSlot.HAND);
						put("Off hand", EquipmentSlot.OFF_HAND);
						put("Head", EquipmentSlot.HEAD);
						put("Body", EquipmentSlot.CHEST);
						put("Legs", EquipmentSlot.LEGS);
						put("Feet", EquipmentSlot.FEET);
					}};
					
					String tag = tags.get("Equipment Slot");
					ItemStack item = (ItemStack) args.get("item").getVal();
					
					switch (tag) {
						case "Saddle" -> setSaddle(target, item);
						case "Horse armor" -> ((Horse) target).getInventory().setArmor(item);
						case "Decor" -> ((Llama) target).getInventory().setDecor(item);
						default -> Objects.requireNonNull(target.getEquipment()).setItem(equipmentSlots.get(tag), item);
					}
				}
				
				case "SetArmor" -> {
					ItemStack[] armorItems = DFValue.castItem((DFValue[]) args.get("armor").getVal());
					
					Objects.requireNonNull(target.getEquipment()).setArmorContents(armorItems);
				}
				
				case "LaunchProj" -> {/*TODO*/}
				
				case "ShearSheep" -> ((Sheep) target).setSheared(true);
				
				case "SetCustomTag" -> {
					String key = (String) args.get("key").getVal();
					String txtVal = (String) args.get("txtVal").getVal();
					Double numVal = args.get("numVal").getVal() == null ? null : (double) args.get("numVal").getVal();
					target.setMetadata(key, new FixedMetadataValue(DFPlugin.plugin, txtVal == null ? numVal : txtVal));
				}
				
				case "GetCustomTag" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					String tagName = (String) args.get("name").getVal();
					
					MetadataValue metadata = target.getMetadata(tagName).get(0);
					DFValue val;
					
					try {
						val = new DFValue(metadata.asDouble(), DFType.NUM);
					} catch (NumberFormatException e) {
						val = new DFValue(metadata.asString(), DFType.TXT);
					} catch (NullPointerException e) {
						val = DFValue.nullVar();
					}
					
					DFVar.setVar(var, val, localStorage);
				}
				
				case "RemoveCustomTag" -> {
					String tagName = (String) args.get("name").getVal();
					target.removeMetadata(tagName, DFPlugin.plugin);
				}
				
				case "SetItem" -> ((Item) target).setItemStack((ItemStack) args.get("item").getVal());
			}
		}
	}
	
	
	private static void setSaddle(LivingEntity entity, ItemStack saddle){
		if(entity instanceof Horse) ((Horse) entity).getInventory().setSaddle(saddle);
		else if(entity instanceof Strider) ((Strider) entity).setSaddle(saddle != null);
		else if(entity instanceof Pig) ((Pig) entity).setSaddle(saddle != null);
	}
}

