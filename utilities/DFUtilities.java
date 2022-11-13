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
import me.wonk2.utilities.internals.FileManager;
import me.wonk2.utilities.internals.PlayerData;
import me.wonk2.utilities.values.DFSound;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.TextCode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Class to store general-use methods and implement event handlers to assist other action implementations
public abstract class DFUtilities {
	public static FileManager playerConfig;
	public static LivingEntity lastEntity = null;
	
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
				return val.getVal() == null ? "Air" : ((ItemStack) val.getVal()).getType().name().toLowerCase();
			}
			case POT: {
				PotionEffect effect = (PotionEffect) val.getVal();
				String name = StringUtils.capitalise(effect.getType().getName().replace('_', ' '));
				
				return name + " " + effect.getAmplifier() + " - " + formatTime("mm:s", effect.getDuration()/20d);
			}
			case LOC: {
				Location loc = (Location) val.getVal();
				return locToString(loc);
			}
			case SND: {
				DFSound sound = (DFSound) val.getVal();
				return sound.getName() + "[" + sound.pitch + "]" + "[" + sound.volume + "]";
			}
			case NUM: {
				if(Math.round((double) val.getVal()) == val.getInt()) return val.getInt().toString();
				else return String.valueOf((double) val.getVal());
			}
			case VEC: {
				DecimalFormat df = new DecimalFormat("#.##");
				
				Vector vec = (Vector) val.getVal();
				return "<" + df.format(vec.getX()) + ", " + df.format(vec.getY()) + ", " + df.format(vec.getZ()) + ">";
			}
			default: {
				return String.valueOf(val.getVal());
			}
		}
	}
	
	public static String textCodes(String str, HashMap<String, LivingEntity[]> targetMap, HashMap<String, DFValue> localStorage){
		boolean foundPercentage = false;
		int brackets = 0;
		int percentIndex = 0;
		int startIndex = 0;
		
		
		for(int i = 0; i < str.length(); i++){
			char chr = str.charAt(i);
			
			switch(chr){
				case '%':
					if(!foundPercentage){
						foundPercentage = true;
						percentIndex = i;
					}
					break;
				case '(':
					if(foundPercentage){
						if(brackets == 0) startIndex = i;
						brackets++;
					}
					break;
				case ')':
					if(foundPercentage){
						brackets--;
						if(brackets == 0)
							str = str.substring(0, percentIndex) +
								TextCode.getCodeValue(
									targetMap,
									localStorage,
									str.substring(percentIndex, startIndex),
									textCodes(str.substring(startIndex + 1, i), targetMap, localStorage)) +
								str.substring(i + 1);
						
					}
					break;
			}
		}
		
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
		for(int i = 0; i < vals.length; i++){
			result[i] = parseTxt(vals[i]);
		}
		
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
	
	public static LivingEntity[] getTargets(String targetName, HashMap<String, LivingEntity[]> targetMap){
		switch(targetName){
			case "LastEntity":
				return new LivingEntity[]{DFUtilities.lastEntity};
			case "AllPlayers":
				Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
				return Arrays.copyOf(players, players.length, LivingEntity[].class);
			default:
				return targetMap.get(targetName);
		}
	}
	
	public static Material interpretItem(Material item){
		HashMap<Material, Material> specifics = new HashMap<>(){{
			put(Material.WHEAT_SEEDS, Material.WHEAT);
		}};
		return specifics.getOrDefault(item, item);
	}
	
	public static ItemStack parseItemNBT(String rawNBT){
		if(rawNBT == "null") return null;
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
			for(Chunk chunk : Bukkit.getWorlds().get(0).getLoadedChunks())
				for(Entity entity : chunk.getEntities())
					if(entity.getName() == uuidOrName) return entity;
		}
		return null;
	}
	
	public static Location getTargetBlock(LivingEntity e){
		return getRelativeLoc(e.getTargetBlock(null, 5).getLocation());
	}
	
	public static Entity getEntityOrPlayer(String uuidOrName){
		return getPlayer(uuidOrName) == null ? getEntity(uuidOrName) : getPlayer(uuidOrName);
	}
	
	public static void removePotions(LivingEntity entity, PotionEffect[] effects){
		for(PotionEffect effect : effects) entity.removePotionEffect(effect.getType());
	}
	
	public static long getWait(HashMap<String, DFValue> args, HashMap<String, String> tags){
		switch(tags.get("Time Unit")){
			case "Ticks": return args.get("wait").getInt();
			case "Seconds": return (long) (double) args.get("wait").getVal() * 20L;
			case "Minutes": return (long) (double) args.get("wait").getVal() * 1200L;
		}
		
		throw new IllegalArgumentException("The tag \"Time Unit\" on CONTROL:Wait yields none of the expected fields. This may be the result of an unsupported DiamondFire Update.");
	}
	
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
	
	public static boolean cloudAffectedPlayer(List<LivingEntity> entities){
		for(LivingEntity entity : entities) if(entity instanceof Player) return true;
		return false;
	}
	
	public static Location getRelativeLoc(Location l1){
		Location l2 = DFPlugin.origin;
		return new Location(l1.getWorld(), l1.getX() - l2.getX(), l1.getY(), l1.getZ() - l2.getZ(), l1.getYaw(), l1.getPitch());
	}
	
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
		
		Pattern p = Pattern.compile(pattern);
		
		Matcher m = p.matcher(str);
		while(m.find()) {
			result.add(m.group());
		}
		
		String[] r = new String[result.size()];
		r = result.toArray(r);
		
		return r;
	}
	
}