package me.wonk2.utilities;

//import eu.endercentral.crazy_advancements.JSONMessage;
//import eu.endercentral.crazy_advancements.NameKey;
//import eu.endercentral.crazy_advancements.advancement.Advancement;
//import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
//import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
//import eu.endercentral.crazy_advancements.advancement.criteria.Criteria;
//import eu.endercentral.crazy_advancements.manager.AdvancementManager;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.wonk2.DFPlugin;
import me.wonk2.utilities.actions.IfGame;
import me.wonk2.utilities.actions.IfPlayer;
import me.wonk2.utilities.actions.IfVariable;
import me.wonk2.utilities.actions.pointerclasses.Conditional;
import me.wonk2.utilities.enums.SelectionType;
import me.wonk2.utilities.internals.FileManager;
import me.wonk2.utilities.internals.PlayerData;
import me.wonk2.utilities.values.DFSound;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import me.wonk2.utilities.values.TextCode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.codehaus.plexus.util.StringUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Class to store general-use methods and implement event handlers to assist other action implementations
@SuppressWarnings("unchecked")
public abstract class DFUtilities {
	public static HashMap<String, String> condInfo;
	public static FileManager playerConfig;
	public static Entity lastEntity = null;
	
	public static void init(){
		playerConfig = new FileManager(DFPlugin.plugin, "playerData.yml");
		DFListeners.updateArgInfo();
		DFVar.deserializeSavedVars();
	}
	
	public static void log(String msg){
		DFPlugin.logger.log(Level.INFO, msg);
	}
	
	public static void logWarning(String msg){
		DFPlugin.logger.log(Level.WARNING, msg);
	}
	
	public static void logError(String msg){
		DFPlugin.logger.log(Level.SEVERE, msg);
	}
	
	public static Vector getTangent(Vector normal){
		normal = normal.clone();
		return normal.crossProduct(getBinormal(normal)).normalize();
	}
	
	public static Vector getBinormal(Vector normal){
		normal = normal.clone();
		return normal.crossProduct(normal.clone().add(new Vector(1, 0, 0))).normalize();
	}
	
	public static boolean inCustomInv(Player p){
		Inventory inv = p.getOpenInventory().getTopInventory();
		return inv.getType() != InventoryType.PLAYER
			&& inv.getType() != InventoryType.CRAFTING
			&& inv.getLocation() == null;
	}
	
	public static LivingEntity getDamager(Entity damager){
		return damager instanceof Arrow arrow ? (Player) arrow.getShooter() : (LivingEntity) damager;
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
		switch (val.type) {
			case LIST -> {
				DFValue[] elements = (DFValue[]) val.getVal();
				String[] result = new String[elements.length];
				for (int i = 0; i < elements.length; i++) result[i] = parseTxt(elements[i]);
				return "[" + String.join(", ", result) + "]";
			}
			case ITEM -> {
				return val.getVal() == null ? "Air" : ((ItemStack) val.getVal()).getType().name().toLowerCase();
			}
			case POT -> {
				PotionEffect effect = (PotionEffect) val.getVal();
				String name = StringUtils.capitalise(effect.getType().getName().replace('_', ' '));
				
				return name + " " + effect.getAmplifier() + " - " + formatTime("mm:s", effect.getDuration() / 20d);
			}
			case LOC -> {
				Location loc = (Location) val.getVal();
				return locToString(loc);
			}
			case SND -> {
				DFSound sound = (DFSound) val.getVal();
				return sound.getName() + "[" + sound.pitch + "]" + "[" + sound.volume + "]";
			}
			case NUM -> {
				DecimalFormat df = new DecimalFormat("#.###");
				if (Math.floor((double) val.getVal()) == (double) val.getVal()) return val.getInt().toString();
				else return String.valueOf(df.format((double) val.getVal()));
			}
			case VEC -> {
				DecimalFormat df = new DecimalFormat("#.##");
				
				Vector vec = (Vector) val.getVal();
				return "<" + df.format(vec.getX()) + ", " + df.format(vec.getY()) + ", " + df.format(vec.getZ()) + ">";
			}
			case DICT -> {
				ArrayList<String> contents = new ArrayList<>();
				HashMap<DFValue, DFValue> dict = (HashMap<DFValue, DFValue>) val.getVal();
				
				for(DFValue key : dict.keySet()) contents.add(key.getVal().toString() + ": " + parseTxt(dict.get(key)));
				return "{" + String.join(", ", contents) + "}";
			}
			default -> {
				return String.valueOf(val.getVal());
			}
		}
	}
	
	public static Projectile launchProjectile(ItemStack projectile, Location loc, Float speed, float inaccuracy, String customName){
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
		
		HashMap<Material, Float> defaultSpeedValues = new HashMap<>() {{
			put(Material.SNOWBALL, 7f); // TODO
			put(Material.EGG, 7f); // TODO
			put(Material.ENDER_PEARL, 7f); // TODO
			put(Material.TRIDENT, 7f); // TODO
			put(Material.ARROW, 7f); // TODO
			put(Material.SPECTRAL_ARROW, 7f); // TODO
			put(Material.MILK_BUCKET, 7f); // TODO
			put(Material.DRAGON_BREATH, 7f); // TODO
			put(Material.FIRE_CHARGE, 7f); // TODO
			put(Material.WITHER_SKELETON_SKULL, 7f); // TODO
			put(Material.TIPPED_ARROW, 7f); // TODO
			put(Material.SPLASH_POTION, 7f); // TODO
			put(Material.LINGERING_POTION, 7f); // TODO
			put(Material.EXPERIENCE_BOTTLE, 7f); // done xd
		}};
		
		Material projType = projectile.getType();
		
		if(speed == null) speed = defaultSpeedValues.get(projType);
		speed /= 10;
		inaccuracy /= 10;
		
		EntityType type = projectiles.get(projType);
		Arrow arrow = DFPlugin.world.spawnArrow(loc, loc.getDirection(), speed, inaccuracy);
		Projectile proj = null;
		switch (projType) {
			case FIRE_CHARGE -> {
				if (projectile.getAmount() >= 2) {
					Fireball fireball = (Fireball) DFPlugin.world.spawnEntity(loc, EntityType.FIREBALL);
					fireball.setDirection(arrow.getVelocity());
					
					if (customName != null) fireball.setCustomName(customName);
					fireball.setCustomNameVisible(true);
					proj = fireball;
				} else {
					SmallFireball smallFireball = (SmallFireball) DFPlugin.world.spawnEntity(loc, EntityType.SMALL_FIREBALL);
					smallFireball.setDirection(arrow.getVelocity());
					
					if (customName != null) smallFireball.setCustomName(customName);
					smallFireball.setCustomNameVisible(true);
					proj = smallFireball;
				}
				
			}
			case WITHER_SKELETON_SKULL -> {
				WitherSkull witherSkull = (WitherSkull) arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.WITHER_SKULL);
				witherSkull.setCharged(projectile.getAmount() >= 2);
				witherSkull.setDirection(arrow.getVelocity());
				
				if (customName != null) witherSkull.setCustomName(customName);
				witherSkull.setCustomNameVisible(true);
				proj = witherSkull;
			}
			
			case TIPPED_ARROW -> {
				Arrow tippedArrow = (Arrow) arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.ARROW);
				tippedArrow.setBasePotionData(((PotionMeta) projectile.getItemMeta()).getBasePotionData());
				tippedArrow.setVelocity(arrow.getVelocity());
				
				if (customName != null) tippedArrow.setCustomName(customName);
				tippedArrow.setCustomNameVisible(true);
				proj = tippedArrow;
			}
			case SPLASH_POTION, LINGERING_POTION -> {
				ThrownPotion thrownPotion = (ThrownPotion) DFPlugin.world.spawnEntity(loc, EntityType.SPLASH_POTION);
				thrownPotion.setItem(projectile);
				thrownPotion.setVelocity(arrow.getVelocity());
				
				if (customName != null) thrownPotion.setCustomName(customName);
				thrownPotion.setCustomNameVisible(true);
				proj = thrownPotion;
			}
			case EXPERIENCE_BOTTLE -> {
				ThrownExpBottle thrownExpBottle = (ThrownExpBottle) DFPlugin.world.spawnEntity(loc, EntityType.THROWN_EXP_BOTTLE);
				thrownExpBottle.setVelocity(arrow.getVelocity());
				
				if (customName != null) thrownExpBottle.setCustomName(customName);
				thrownExpBottle.setCustomNameVisible(true);
				proj = thrownExpBottle;
			}
			default -> {
				if (projectiles.containsKey(projType)) {
					proj = (Projectile) DFPlugin.world.spawnEntity(loc, type);
					proj.setVelocity(arrow.getVelocity());
					
					if (customName != null) proj.setCustomName(customName);
					proj.setCustomNameVisible(true);
				}
			}
		}
		
		arrow.remove();
		return proj;
	}
	
	public static char[] trimArray(@NotNull char[] arr, int start, int end){
		if (end - start < 0) throw new IllegalArgumentException("Start index is bigger than end index you goof");
		end++;
		
		char[] result = new char[end - start];
		for(int i = start; i < end; i++) result[i - start] = arr[i];
		
		return result;
	}
	
	public static String textCodes(String str, HashMap<String, Entity[]> targetMap, HashMap<String, DFValue> localStorage, boolean debug){
		if(debug) Bukkit.broadcastMessage("Evaluating text codes of " + str);
		String[] targetCodes = Arrays.stream(removeDuplicates(regex("%([^\\s]+)", str))).toArray(String[]::new);
		for(String code : targetCodes) str = str.replace(code, TextCode.getTargetName(targetMap, code));
		
		
		char[] chars = str.toCharArray();
		ArrayList<String> contextCodes = new ArrayList<>();
		boolean foundPercent = false;
		int percentIndex = 0;
		int brackets = 0;
		for(int i = 0; i < str.length(); i++){
			char c = chars[i];
			
			if(c == '%' && !foundPercent){
				foundPercent = true;
				percentIndex = i;
			}
			if(c == '(' && foundPercent) brackets++;
			if(c == ')' && foundPercent){
				brackets--;
				if(brackets == 0) {
					contextCodes.add(new String(trimArray(chars, percentIndex, i)));
					foundPercent = false;
				}
			}
		}
		
		for(String code : contextCodes){
			if(debug) Bukkit.broadcastMessage("Analyzing code " + code);
			String prefix = regex("%[^\\(]+", code)[0];
			String contents = code.substring(prefix.length() + 1, code.length() - 1); // - 1 & + 1 help get rid of outer brackets
			
			if(debug) Bukkit.broadcastMessage(": " + contents);
			contents = textCodes(contents, targetMap, localStorage, debug);
			
			if(debug) Bukkit.broadcastMessage("Replacing " + code + " from " + str + "...");
			str = str.replace(code, TextCode.getCodeValue(targetMap, localStorage, prefix, contents));
		}
		
		if(debug) Bukkit.broadcastMessage("Returned value: " + str);
		return str;
	}
	
	
	public static String locToString(Location loc){
		DecimalFormat df = new DecimalFormat("#.##");
		
		loc.setX(Double.parseDouble(df.format(loc.getX())));
		loc.setY(Double.parseDouble(df.format(loc.getY())));
		loc.setZ(Double.parseDouble(df.format(loc.getZ())));
		loc.setYaw(Float.parseFloat(df.format(loc.getYaw())));
		loc.setPitch(Float.parseFloat(df.format(loc.getPitch())));
		
		if(loc.getYaw() == 0f && loc.getPitch() == 0f)
			return "[" +
				roundNum(loc.getX()) + ", " +
				roundNum(loc.getY()) + ", " +
				roundNum(loc.getZ()) + "]";
		else return "[" +
			roundNum(loc.getX()) + ", " +
			roundNum(loc.getY()) + ", " +
			roundNum(loc.getZ()) + ", " +
			roundNum(loc.getPitch()) + ", " +
			roundNum(loc.getYaw()) + "]";
	}
	
	private static String roundNum(double num){
		return Math.floor(num) == num ? String.valueOf((int) num) : String.valueOf(num);
	}
	
	public static String[] parseTxt(DFValue[] vals){
		String[] result = new String[vals.length];
		for(int i = 0; i < vals.length; i++)
			result[i] = parseTxt(vals[i]);
		
		
		return result;
	}
	
	public static String formatTime(String format, double seconds){
		long timestamp = (long) seconds * 1000;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(new Date(timestamp));
	}
	
	public static String escapeRegex(String input){
		return input.replaceAll("[-.+*?\\[^\\]$(){}=!<>|:\\\\]", "\\\\$0");
	}
	
	public static HashMap<String, DFValue> getArgs(Object[] inputArray){
		return (HashMap<String, DFValue>) inputArray[0];
	}
	
	public static HashMap<String, String> getTags(Object[] inputArray){
		return (HashMap<String, String>) inputArray[1];
	}
	
	public static HashMap<Integer, DFValue> getPrimitiveInput(Object[] inputArray){
		return (HashMap<Integer, DFValue>) inputArray[2];
	}
	
	public static Entity[] getTargets(String targetName, HashMap<String, Entity[]> targetMap, SelectionType selectionType){
		switch(targetName){
			case "lastentity":
				return new Entity[]{DFUtilities.lastEntity};
			case "allplayers":
				Player[] players = Bukkit.getOnlinePlayers().toArray(Player[]::new);
				return Arrays.copyOf(players, players.length, LivingEntity[].class);
			default:
				String[] targetSet = new String[]{
					"selection",
					"default",
					"lastentity"
				};
				
				
				if(DFUtilities.lastEntity != null) targetMap.put("lastentity", new Entity[]{DFUtilities.lastEntity}); // Otherwise "lastentity" will always be considered invalid even when present
				if(targetName.equals("selection"))
					for (String target : targetSet){
						if (isTargetValid(target, targetMap, selectionType)) {
							targetName = target;
							break;
						}
						targetName = "notargets"; // Will result in empty LivingEntity array
					}
					
				return !targetMap.containsKey(targetName) ? new LivingEntity[1] : targetMap.get(targetName);
		}
	}
	
	public static boolean isTargetValid(String targetName, HashMap<String, Entity[]> targetMap, SelectionType selectionType){
		if(!targetMap.containsKey(targetName) || targetMap.get(targetName) == null) return false; // We want null selections (selections that don't exist, not empty selections) to default to the "default" target
		if(selectionType == SelectionType.EITHER) return true; // Can't be invalid
		if(targetMap.get(targetName).length == 0) return true; // Empty selections can't be invalid, because they don't have mobs/entities nor players
		
		SelectionType selectionSelType = targetMap.get(targetName)[0] instanceof Player ? SelectionType.PLAYER : SelectionType.ENTITY; // The selection type of the current selection
		return selectionSelType == selectionType;
	}
	
	public static Material interpretItem(Material item){
		HashMap<Material, Material> specifics = new HashMap<>(){{
			put(Material.WHEAT_SEEDS, Material.WHEAT);
			put(Material.WATER_BUCKET, Material.WATER);
			put(Material.LAVA_BUCKET, Material.LAVA);
		}};
		return specifics.getOrDefault(item, item);
	}
	
	public static ItemStack interpretText(String txt){
		Material returnVal;
		switch(txt){
			case "air" -> returnVal = Material.AIR;
			default -> returnVal = Material.valueOf(txt);
		}
		
		return new ItemStack(returnVal);
	}
	
	public static ItemStack parseItemNBT(String rawNBT){
		if(rawNBT.equals("null")) return null;
		CompoundTag nbt = null;
		try{nbt = TagParser.parseTag(rawNBT);}
		catch(CommandSyntaxException e){e.printStackTrace();}
		
		net.minecraft.world.item.ItemStack nmsItem = net.minecraft.world.item.ItemStack.of(nbt);
		return CraftItemStack.asBukkitCopy(nmsItem);
	}
	
	public static boolean locIsNear(World world, Location checkLoc, Location loc, double radius, String shape){
		switch(shape){
			case "Sphere": {
				if(Math.sqrt(Math.pow(loc.getX() - checkLoc.getX(), 2) + Math.pow(loc.getY() - checkLoc.getY(), 2) + Math.pow(loc.getZ() - checkLoc.getZ(), 2)) <= radius) return true;
				break;
			}
			case "Circle": {
				if(Math.sqrt(Math.pow(loc.getX() - checkLoc.getX(), 2) + Math.pow(loc.getZ() - checkLoc.getZ(), 2)) <= radius) return true;
				break;
			}
			case "Cube": {
				if(Math.abs(loc.getX() - checkLoc.getX()) <= radius) return true;
				if(Math.abs(loc.getY() - checkLoc.getY()) <= radius) return true;
				if(Math.abs(loc.getZ() - checkLoc.getZ()) <= radius) return true;
				break;
			}
			case "Square": {
				if(Math.abs(loc.getX() - checkLoc.getX()) <= radius) return true;
				if(Math.abs(loc.getZ() - checkLoc.getZ()) <= radius) return true;
				break;
			}
		}
		
		return false;
	}
	
	public static Block getEventLoc(Player player, Block clickedBlock){
		return clickedBlock == null ? rayForward(player.getEyeLocation()).getBlock() : clickedBlock;
	}
	
	private static Location rayForward(Location origin){
		RayTraceResult ray = Objects.requireNonNull(origin.getWorld()).rayTraceBlocks(origin, origin.getDirection(), 5d);
		return ray == null ? origin.add(origin.getDirection().multiply(5)) : ray.getHitPosition().toLocation(origin.getWorld());
	}
	
	public static Location centerLoc(Location l){
		return new Location(l.getWorld(), roundToHalf(l.getX()), roundToHalf(l.getY()), roundToHalf(l.getZ()));
	}
	
	public static double roundToHalf(double d) {
		return Math.floor(d) + 0.5d;
	}
	
	public static Location floorLoc(Location l){
		return new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	public static Player getPlayer(String uuidOrName){
		try{return Bukkit.getPlayer(UUID.fromString(uuidOrName));}
		catch(IllegalArgumentException exception){return Bukkit.getPlayer(uuidOrName);}
	}
	
	public static Entity getEntity(String uuidOrName){
		try{return Bukkit.getEntity(UUID.fromString(uuidOrName));}
		catch(IllegalArgumentException exception){
			for(Chunk chunk : DFPlugin.world.getLoadedChunks())
				for(Entity entity : chunk.getEntities())
					if(entity.getName().equals(uuidOrName)) return entity;
		}
		return null;
	}
	
	/**
	 * Gets the block that an entity is looking at, relative to the origin location
	 * @param e The entity to get the target block of
	 * @return The location of the block that the entity is looking at
	 * @author Wonk0
	 */
	public static Location getTargetBlock(LivingEntity e){
		return getRelativeLoc(e.getTargetBlock(null, 5).getLocation());
	}
	
	/**
	 * Gets the entity or player that the uuid / name inputted belongs to
	 * @param uuidOrName The uuid or player name of
	 * @return A player if the uuid or name belongs to one, an entity if the uuid or name belongs to one, or null if neither
	 * @author Wonk0
	 */
	@Nullable
	public static Entity getEntityOrPlayer(String uuidOrName){
		return getPlayer(uuidOrName) == null ? getEntity(uuidOrName) : getPlayer(uuidOrName);
	}
	
	/**
	 * Removes all of the specified potion effects from an entity
	 * @param entity The entity to remove the effects from
	 * @param effects An array of the PotionEffects to remove
	 * @author Wonk0
	 */
	public static void removePotions(LivingEntity entity, PotionEffect[] effects){
		for(PotionEffect effect : effects) entity.removePotionEffect(effect.getType());
	}
	
	/**
	 * Returns the amount of ticks to wait based on the time unit from the block tag
	 * @param args Formatted block arguments
	 * @param tags Block Tags
	 * @return The wait time in ticks
	 * @author Wonk0
	 */
	public static long getWait(HashMap<String, DFValue> args, HashMap<String, String> tags){
		switch(tags.get("Time Unit")){
			case "Ticks": return args.get("wait").getInt();
			case "Seconds": return (long) (double) args.get("wait").getVal() * 20L;
			case "Minutes": return (long) (double) args.get("wait").getVal() * 1200L;
		}
		
		throw new IllegalArgumentException("The tag \"Time Unit\" on CONTROL:Wait yields none of the expected fields. This may be the result of an unsupported DiamondFire Update. " + tags.get("Time Unit"));
	}
	
	/**
	 * Returns a boolean determining if a PlayerMoveEvent was triggered by a jump
	 * @param e The PlayerMoveEvent to check
	 * @return True if the player jumped, otherwise False
	 * @author Wonk0
	 */
	public static boolean playerDidJump(PlayerMoveEvent e){ //TODO: Find a better alternative, this check is unreliable.
		Player player = e.getPlayer();
		PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId());
		if (player.getVelocity().getY() > 0) {
			double jumpVelocity = 0.42F;
			if (player.hasPotionEffect(PotionEffectType.JUMP))
				jumpVelocity += (float) (player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() + 1) * 0.1F;
			
			if (e.getPlayer().getLocation().getBlock().getType() != Material.LADDER && playerData.wasGrounded)
				if (!((LivingEntity) player).isOnGround() && Double.compare(player.getVelocity().getY(), jumpVelocity) == 0)
					return true;
		}
		
		playerData.wasGrounded = ((LivingEntity) player).isOnGround();
		return false;
	}
	
	/**
	 * I have no idea why this exists 💀
	 * @author Wonk0
	 */
	public static boolean cloudAffectedPlayer(List<LivingEntity> entities){
		for(LivingEntity entity : entities) if(entity instanceof Player) return true;
		return false;
	}
	
	/**
	 * @param subAction - The action on the while loop or select object (e.g. PIsNear)
	 * @param inverted - Whether or not the conditional is inverted using the NOT arrow
	 * @param paramManager - Param manager of the while loop or select object
	 * @param localStorage - Local storage of the thread that the while loop / select object is running on
	 * @param specifics - Specifics of the thread that the while loop / select object is running on
	 * @param targetMap - Target map of the the thread that the while loop / select object is running on
	 * @param p - Target to apply the conditional on
	 * @return A Conditional Object (IfPlayer/IfVariable/IfGame)
	 * @author Wonk0
	 */
	public static Conditional getConditional(String subAction, boolean inverted, ParamManager paramManager, HashMap<String, DFValue> localStorage, HashMap<String, Object> specifics, HashMap<String, Entity[]> targetMap, Entity p){
		paramManager.actionName = condInfo.get(subAction) + ":" + subAction;
		String condClass = condInfo.get(subAction);
		targetMap.put("selection", new Entity[]{p});
		
		return switch (condClass) {
			case "IFPLAYER" -> new IfPlayer("selection", targetMap, paramManager, subAction, inverted);
			case "IFVAR" -> new IfVariable("selection", targetMap, paramManager, subAction, inverted, localStorage);
			case "IFGAME" -> new IfGame("selection", targetMap, paramManager, subAction, inverted, specifics);
			default ->
				throw new IllegalStateException("Error whilst trying to select objects: This type of conditional is not supported yet: " + condClass);
		};
	}
	
	/**
	 * Subtracts the origin location from another location whilst preserving its Y coordinate
	 * @param loc The location to be subtracted from
	 * @return The relative location
	 * @author Wonk0
	 */
	public static Location getRelativeLoc(Location loc){
		Location temp = subtractLocs(loc, DFPlugin.origin);
		temp.setY(loc.getY());
		return temp;
	}
	
	/**
	 * Pastes a schematic from a file at a specific location
	 * @param loc The position that the corner of the build should be pasted at
	 * @param schematic A .schem file referencing the schematic that should be pasted
	 * @author Wonk0
	 */
	public static void pasteSchematic(Location loc, File schematic){
		ClipboardFormat format = ClipboardFormats.findByFile(schematic);
		try {
			assert format != null;
			ClipboardReader reader = format.getReader(new FileInputStream(schematic));
			ClipboardHolder clipboard = new ClipboardHolder(reader.read());
			
			assert loc.getWorld() != null;
			EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(loc.getWorld()), -1);
			Operation operation = clipboard.createPaste(editSession).
				to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
				.ignoreAirBlocks(false)
				.build();
			Operations.complete(operation);
			editSession.flushSession();
		} catch (IOException | WorldEditException e) { e.printStackTrace(); }
	}
	
	/**
	 * converts any string to a nms net.minecraft.network.chat.Component
	 * @param str a DiamondFire text string with §
	 * @return a nms component
	 * @author CheaterTim
	 */
	public static MutableComponent componentFromString(String str) {
		String[] parts = str.replaceAll("§x(§[a-fA-F0-9]){6}(?=§x(§[a-fA-F0-9]){6})", "") // §x§5§2§f§f&§&1§x§c§c§2§7§2§7 -> §x§c§c§2§7§2§7 (to fix an edge case where it uses the second color as the text content)
			.replaceAll("§x(§[a-fA-F0-9]){6}", "\uE000$0\uE000") // hell&x&c&c&2&7&2&7world -> hell(U+E000)&x&c&c&2&7&2&7(U+E000)world
			.split("\uE000"); // hell(U+E000)&x&c&c&2&7&2&7(U+E000)world -> [hell, &x&c&c&2&7&2&7, world]
		
		MutableComponent comp = Component.empty();
		
		
		int i = -1;
		boolean didHexCod = false;
		for(String part : parts) {
			i++;
			boolean end = i == parts.length - 1;
			String next = end ? null : parts[i + 1];
			
			if(part.length() == 0) continue;
			
			if(didHexCod) {
				didHexCod = false;
				continue;
			}
			
			if(part.matches("§x(§[a-fA-F0-9]){6}") && !end) { // if the part is a hex code, and we're not at the last part
				String hex = part.replace("§", "").replace("x", "#"); // convert it to a normal hex code
				comp.append(Component.literal(next).withStyle(style -> { // add the next part
					return style.withColor(TextColor.parseColor(hex)); // with our hex color
				}));
				didHexCod = true; // make sure the next part is ignored, because we already appended that
			} else {
				comp.append(part);
			}
		}
		
		return comp;
	}
	
	public static String[] regex(String pattern, String str) {
		ArrayList<String> result = new ArrayList<>();
		Matcher m = Pattern.compile(pattern).matcher(str);
		while(m.find()) result.add(m.group());
		
		return result.toArray(String[]::new);
	}
	
	public static Object[] removeDuplicates(Object[] arr){
		ArrayList<Object> list = new ArrayList<>();
		ArrayList<Object> duplicates = new ArrayList<>();
		
		for(Object obj : arr){
			if(!duplicates.contains(obj)) list.add(obj);
			else continue;
			duplicates.add(obj);
		}
		
		return list.toArray();
 }
 
 public static Location addLocs(Location l1, Location l2){
		return new Location(l1.getWorld(), l1.getX() + l2.getX(), l1.getY() + l2.getY(), l1.getZ() + l2.getZ(), l1.getYaw(), l1.getPitch());
 }
	
	public static Location subtractLocs(Location l1, Location l2){
		return new Location(l1.getWorld(), l1.getX() - l2.getX(), l1.getY() - l2.getY(), l1.getZ() - l2.getZ(), l1.getYaw(), l1.getPitch());
	}
	
}