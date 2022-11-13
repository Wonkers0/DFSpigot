package me.wonk2.utilities.actions;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.internals.PlayerData;
import me.wonk2.utilities.internals.TextAligning;
import me.wonk2.utilities.internals.Hologram;
import me.wonk2.utilities.values.DFParticle;
import me.wonk2.utilities.values.DFSound;
import me.wonk2.utilities.values.DFValue;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class PlayerAction extends Action {
	LivingEntity target;
	
	
	public PlayerAction(String targetName, HashMap<String, LivingEntity> targetMap, ParamManager paramManager, String action) {
		super(targetName, targetMap, paramManager, action);
		this.action = action;
	}
	
	@Override
	public void invokeAction(){
		Object[] inputArray = paramManager.formatParameters(targetMap);
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray);
		target = DFUtilities.getTargets(targetName, targetMap)[0]; //TODO: Multiple targets
		
		HashMap<String, SoundCategory> categories = new HashMap<>(){{
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
		
		switch(action){
			case "SendMessage": {
				String[] txtArray = DFValue.castTxt((DFValue[]) args.get("msg").getVal());
				
				String msg = tags.get("Text Value Merging").equals("Add spaces") ?
					String.join(" ", txtArray) :
					String.join("", txtArray);
				
				msg = tags.get("Alignment Mode").equals("Regular") ? msg : TextAligning.centerString(msg);
				
				target.sendMessage(msg);
				break;
			}
			case "PlaySound": {
				DFSound[] soundArr = DFValue.castSound((DFValue[]) args.get("sounds").getVal());
				
				for(DFSound sound : soundArr){
					if(args.get("playbackLoc").getVal() == null)
						((Player) target).playSound(target.getLocation(), sound.sound, SoundCategory.MASTER, sound.volume, sound.pitch); //TODO: Sound category tag
					else ((Player) target).playSound((Location) args.get("playbackLoc").getVal(), sound.sound, categories.get(tags.get("Sound Source")), sound.volume, sound.pitch);
				}
				break;
			}
			
			case "SendTitle":{
				((Player) target).sendTitle((String) args.get("title").getVal(), (String) args.get("subtitle").getVal(), args.get("fadeIn").getInt(), args.get("duration").getInt(), args.get("fadeOut").getInt());
				break;
			}
			
			case "SetBossBar":{
				HashMap<String, BarStyle> barStyles = new HashMap<>(){{
					put("Solid", BarStyle.SOLID);
					put("6 segments", BarStyle.SEGMENTED_6);
					put("10 segments", BarStyle.SEGMENTED_10);
					put("12 segments", BarStyle.SEGMENTED_12);
					put("20 segments", BarStyle.SEGMENTED_20);
				}};
				
				BarColor color = BarColor.valueOf(tags.get("Bar Color").toUpperCase());
				double barHealth = (float) (double) args.get("health").getVal() / (float) (double) args.get("maxHealth").getVal();;
				ArrayList<BarFlag> barFlags = new ArrayList<>();
				if(tags.get("Sky Effect").equals("Both") || tags.get("Sky Effect").equals("Create fog"))
					barFlags.add(BarFlag.CREATE_FOG);
				if(tags.get("Sky Effect").equals("Both") || tags.get("Sky Effect").equals("Darken sky"))
					barFlags.add(BarFlag.DARKEN_SKY);
				
				setBossBar((Player) target, (String) args.get("title").getVal(), args.get("index").getInt(), barHealth, color, barStyles.get(tags.get("Bar Style")), barFlags.toArray(new BarFlag[0]));
				break;
			}
			
			case "RemoveBossBar": {
				removeBossbar((Player) target, args.get("index").getInt());
				break;
			}
			
			case "ActionBar":{
				String[] txtArray = DFValue.castTxt((DFValue[]) args.get("msg").getVal());
				
				String msg = tags.get("Text Value Merging").equals("Add spaces") ?
					String.join(" ", txtArray) :
					String.join("", txtArray);
				
				((Player) target).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
				break;
			}
			
			case "SendMessageSeq":{
				sendMessageSeq((Player) target, DFValue.castTxt((DFValue[]) args.get("msgs").getVal()), (long) (double) args.get("delay").getVal(), DFPlugin.plugin);
				//return null; // This method already affects all targets, no need to continue main loop
			}
			
			case "PlaySoundSeq":{
				Location loc = args.get("playbackLoc").getVal() != null ?
					(Location) args.get("playbackLoc").getVal() :
					null;
				
				playSoundSeq((Player) target, DFValue.castSound((DFValue[]) args.get("sounds").getVal()), loc, (long) (double) args.get("delay").getVal(), DFPlugin.plugin);
				//return null; // This method already affects all targets, no need to continue main loop
			}
			
			case "SendHover": {
				sendHover((Player) target, (String) args.get("msg").getVal(), (String) args.get("hover").getVal());
				break;
			}
			
			case "StopSound": {
				stopSounds((Player) target, DFValue.castSound((DFValue[]) args.get("sounds").getVal()), categories.get(tags.get("Sound Source")));
				break;
			}
			
			case "SetTabListInfo": {
				if(tags.get("Player List Field") == "Header") ((Player) target).setPlayerListHeader((String) args.get("tabInfo").getVal());
				else ((Player) target).setPlayerListFooter((String) args.get("tabInfo").getVal());
				break;
			}
			
			case "GiveItems": {
				for(ItemStack item : DFValue.castItem((DFValue[]) args.get("items").getVal()))
					for(int i = 0; i < args.get("stack").getInt(); i++)
						((Player) target).getInventory().addItem(item);
				
				break;
			}
			
			case "SetHotbar": {
				DFValue[] items = (DFValue[]) args.get("items").getVal();
				int itemIndex = 0;
				for(int i = 0; i < 9; i++)
					if(items[itemIndex].slot != i) ((Player) target).getInventory().clear(i);
					else {
						((Player) target).getInventory().setItem(i, (ItemStack) items[itemIndex].getVal());
						if(itemIndex != items.length - 1) itemIndex++;
					}
				
				break;
			}
			
			case "SetInventory": {
				DFValue[] items = (DFValue[]) args.get("items").getVal();
				int itemIndex = 0;
				for(int i = 9; i < 36; i++)
					if(items[itemIndex].slot != i) ((Player) target).getInventory().clear(i);
					else {
						((Player) target).getInventory().setItem(i, (ItemStack) items[itemIndex].getVal());
						if(itemIndex != items.length - 1) itemIndex++;
					}
				
				break;
			}
			
			case "SetSlotItem": {
				((Player) target).getInventory().setItem(args.get("slot").getInt() - 1, (ItemStack) args.get("item").getVal());
				break;
			}
			
			case "SetEquipment": {
				HashMap<String, EquipmentSlot> equipmentSlots = new HashMap<>(){{
					put("Main hand", EquipmentSlot.HAND);
					put("Off hand", EquipmentSlot.OFF_HAND);
					put("Head", EquipmentSlot.HEAD);
					put("Chest", EquipmentSlot.CHEST);
					put("Legs", EquipmentSlot.LEGS);
					put("Feet", EquipmentSlot.FEET);
				}};
				
				((Player) target).getInventory().setItem(equipmentSlots.get(tags.get("Equipment Slot")), (ItemStack) args.get("item").getVal());
				break;
			}
			
			case "SetArmor": {
				Player player = (Player) target;
				
				player.getInventory().setArmorContents(
					new ItemStack[]{(ItemStack) args.get("head").getVal(), (ItemStack) args.get("chest").getVal(), (ItemStack) args.get("leggings").getVal(), (ItemStack) args.get("feet").getVal()}
				);
				break;
			}
			
			case "ReplaceItems": {
				replaceItems((Player) target, DFValue.castItem((DFValue[]) args.get("replaceables").getVal()), (ItemStack) args.get("replacement").getVal(), (byte) args.get("amount").getVal());
				break;
			}
			
			case "RemoveItems": {
				for(ItemStack item : DFValue.castItem((DFValue[]) args.get("removals").getVal()))
					((Player) target).getInventory().removeItem(item);
				break;
			}
			
			case "ClearItems": {
				clearItems((Player) target, DFValue.castItem((DFValue[]) args.get("items").getVal()));
				break;
			}
			
			case "SetCursorItem": {
				((Player) target).setItemOnCursor((ItemStack) args.get("item").getVal());
				break;
			}
			
			case "ClearInv": {
				HashMap<String, Integer[]> clearTypes = new HashMap<>(){{
					put("Entire inventory", new Integer[]{0, 41});
					put("Main inventory", new Integer[]{0, 35});
					put("Upper inventory", new Integer[]{8, 35});
					put("Hotbar", new Integer[]{0, 8});
					put("Armor", new Integer[]{36,39});
				}};
				
				Integer[] clearIndices = clearTypes.get(tags.get("Clear Mode"));
				
				clearInv((Player) target, clearIndices[1], clearIndices[2], tags.get("Clear Crafting and Cursor") == "True");
				break;
			}
			
			case "SetItemCooldown": {
				Material material = ((ItemStack) args.get("item").getVal()).getType();
				((Player) target).setCooldown(material, args.get("ticks").getInt());
				break;
			}
			
			case "SaveInv": {
				saveInv((Player) target);
				break;
			}
			
			case "LoadInv": {
				loadInv((Player) target);
				break;
			}
			
			case "ShowInv": {
				((Player) target).openInventory(createInventory((Player) target, (DFValue[]) args.get("items").getVal(), 27));
				break;
			}
			
			case "ExpandInv": {
				expandInv((Player) target, (DFValue[]) args.get("items").getVal(), 27);
				break;
			}
			
			case "SetMenuItem": {
				((Player) target).getOpenInventory().setItem( args.get("slot").getInt() - 1, (ItemStack) args.get("item").getVal());
				break;
			}
			
			case "SetInvName": {
				setInvName((Player) target, (String) args.get("invName").getVal());
				break;
			}
			
			case "CloseInv": {
				((Player) target).closeInventory();
				break;
			}
			
			case "RemoveInvRow": {
				removeInvRow((Player) target, args.get("rows").getInt());
				break;
			}
			
			case "AddInvRow": {
				expandInv((Player) target, (DFValue[]) args.get("items").getVal(), 9);
				break;
			}
			
			case "OpenBlockInv": {
				openContainerInv((Player) target, (Location) args.get("loc").getVal());
				break;
			}
			
			case "Damage": {
				if(args.get("source").getVal() == null) target.damage((double) args.get("amount").getVal());
				else target.damage((double) args.get("amount").getVal(), Bukkit.getEntity(UUID.fromString((String) args.get("source").getVal())));
				break;
			}
			
			case "Heal": {
				Integer amount = args.get("amount").getInt();
				if(amount == null) target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				else target.setHealth(target.getHealth() + amount);
				break;
			}
			
			case "SetHealth": {
				target.setHealth(args.get("amount").getInt());
				break;
			}
			
			case "SetMaxHealth": {
				target.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue((double) args.get("amount").getVal());
				if(tags.get("Heal Player to Max Health") == "True")
					target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				break;
			}
			
			case "SetAbsorption": {
				target.setAbsorptionAmount((double) args.get("amount").getVal());
				break;
			}
			
			case "SetFoodLevel": {
				((Player) target).setFoodLevel(args.get("amount").getInt());
				break;
			}
			
			case "SetSaturation": {
				((Player) target).setSaturation((float) (double) args.get("amount").getVal());
				break;
			}
			
			case "GiveExp": {
				Player player = (Player) target;
				switch(tags.get("Give Experience")){
					case "Points":
						player.giveExp(args.get("amount").getInt());
						break;
					case "Level":
						player.giveExpLevels(args.get("amount").getInt());
						break;
					case "Level Percentage":
						player.setExp(player.getExp() + Math.min((float) (double) args.get("amount").getVal()/100f, 1f));
						break;
				}
				break;
			}
			
			case "SetExp": {
				Player player = (Player) target;
				switch(tags.get("Set Experience")){
					case "Points":
						player.setTotalExperience(args.get("amount").getInt());
						break;
					case "Level":
						player.setLevel(args.get("amount").getInt());
						break;
					case "Level Percentage":
						player.setExp(Math.min((float) (double) args.get("amount").getVal()/100, 1));
						break;
				}
				break;
			}
			
			case "GivePotion": {
				target.addPotionEffects(Arrays.asList(DFValue.castPotion((DFValue[]) args.get("effects").getVal())));
				break;
			}
			
			case "RemovePotion": {
				DFUtilities.removePotions(target, DFValue.castPotion((DFValue[]) args.get("effects").getVal()));
				break;
			}
			
			case "ClearPotions": {
				DFUtilities.removePotions(target, target.getActivePotionEffects().toArray(new PotionEffect[0]));
				break;
			}
			
			case "SetSlot": {
				((Player) target).getInventory().setHeldItemSlot(args.get("slot").getInt());
				break;
			}
			
			case "SetAtkSpeed": {
				target.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue((double) args.get("amount").getVal());
				break;
			}
			
			case "SetFireTicks": {
				target.setFireTicks(args.get("ticks").getInt());
				break;
			}
			
			case "SetFreezeTicks": {
				target.setFreezeTicks(args.get("ticks").getInt());
				break;
			}
			
			case "SetAirTicks": {
				target.setRemainingAir(args.get("ticks").getInt());
				break;
			}
			
			case "SetInvulTicks": {
				target.setNoDamageTicks(args.get("ticks").getInt());
				break;
			}
			
			case "SetFallDistance": {
				target.setFallDistance((float) (double) args.get("distance").getVal());
				break;
			}
			
			case "SetSpeed": {
				String speedTag = tags.get("Speed Type");
				Float speedAmount = (float) (double) args.get("speed").getVal();
				if(speedTag == "Ground speed" || speedTag == "Both") ((Player) target).setWalkSpeed(speedAmount/1000f);
				if(speedTag == "Flight speed" || speedTag == "Both") ((Player) target).setFlySpeed(speedAmount/1000f);
				break;
			}
			
			case "SurvivalMode": {
				((Player) target).setGameMode(GameMode.SURVIVAL);
				break;
			}
			
			case "AdventureMode": {
				((Player) target).setGameMode(GameMode.ADVENTURE);
				break;
			}
			
			case "CreativeMode": {
				((Player) target).setGameMode(GameMode.CREATIVE);
				break;
			}
			
			case "SetAllowFlight": {
				((Player) target).setAllowFlight(tags.get("Allow Flight") == "Enable");
				break;
			}
			
			case "SetAllowPvP": {
				PlayerData.getPlayerData(target.getUniqueId()).canPvP = tags.get("PVP") == "Enable";
				break;
			}
			
			case "SetDropsEnabled": {
				PlayerData.getPlayerData(target.getUniqueId()).deathDrops = tags.get("Spawn Death Drops") == "Enable";
				break;
			}
			
			case "SetInventoryKept": {
				PlayerData.getPlayerData(target.getUniqueId()).keepInv = tags.get("Inventory Kept") == "Enable";
				break;
			}
			
			case "SetCollidable": {
				target.setCollidable(tags.get("Collision") == "Enable");
				break;
			}
			
			case "InstantRespawn": {
				PlayerData.getPlayerData(target.getUniqueId()).instantRespawn = tags.get("Instant Respawn") == "Enable";
				break;
			}

			case "SetReducedDebug": {
				ServerPlayer nmsPlayer = ((CraftPlayer) target).getHandle();
				ServerGamePacketListenerImpl connection = nmsPlayer.connection;

				connection.send(new ClientboundEntityEventPacket(nmsPlayer, (byte)(tags.get("Reduced Debug Info Enabled") == "True" ? 22 : 23)));

				break;
			}
			
			case "EnableBlocks": {
				PlayerData playerData = PlayerData.getPlayerData(target.getUniqueId());
				
				if(args.get("blocks").getVal() == null) playerData.allowedBlocks = (ArrayList<Material>) Arrays.asList(Material.values());
				else playerData.allowBlocks(getStackTypes(DFValue.castItem( (DFValue[]) args.get("blocks").getVal())));
				break;
			}
			
			case "DisableBlocks": {
				
				PlayerData playerData = PlayerData.getPlayerData(target.getUniqueId());
				
				if(args.get("blocks").getVal() == null) playerData.allowedBlocks = new ArrayList<>();
				else playerData.allowedBlocks.removeAll(Arrays.asList(getStackTypes(DFValue.castItem((DFValue[]) args.get("blocks").getVal()))));
				break;
			}
			
			case "DisplayHologram": { //TODO
				if(!(target instanceof Player)) break;
				Location loc = (Location) args.get("location").getVal();
				String text = (String) args.get("text").getVal();
				
				Hologram.showHologram(loc, text, target);
			
				break;
			}
			
			case "Teleport": {
				Location loc = (Location) args.get("loc").getVal();
				if(tags.get("Keep Current Rotation") == "False") target.teleport(loc);
				else target.teleport(loc.setDirection(target.getEyeLocation().getDirection()));
				break;
			}
			
			case "LaunchUp": {
				double launchVelocity = (double) args.get("launch").getVal() / 10;
				if(tags.get("Add to Current Velocity") == "False") target.setVelocity(target.getVelocity().setY(launchVelocity));
				else target.setVelocity(target.getVelocity().add(new Vector(0, launchVelocity, 0)));
				break;
			}
			
			case "LaunchFwd": {
				double launchVelocity = (double) args.get("launch").getVal() / 10;
				Location eyeLoc = target.getEyeLocation();
				if(tags.get("Launch Axis") == "Yaw Only") eyeLoc.setPitch(0);
				
				Vector launchVector = eyeLoc.getDirection().normalize();
				if(tags.get("Add to Current Velocity") == "True") target.setVelocity(target.getVelocity().add(launchVector.multiply(launchVelocity)));
				else target.setVelocity(launchVector.multiply(launchVelocity));
				break;
			}
			
			case "LaunchToward": {
				Location loc = (Location) args.get("loc").getVal();
				double launchVelocity = (double) args.get("launch").getVal() / 10;
				Vector launchVector = loc.toVector().subtract(target.getLocation().toVector());
				if(tags.get("Ignore Distance") == "True") launchVector.normalize();
				
				if(tags.get("Add to Current Velocity") == "True") target.setVelocity(target.getVelocity().add(launchVector.multiply(launchVelocity)));
				else target.setVelocity(launchVector.multiply(launchVelocity));
				break;
			}
			
			case "RideEntity": {
				if(args.get("uuid").getVal() == null) Objects.requireNonNull(target.getVehicle()).eject();
				else DFUtilities.getEntity((String) args.get("uuid").getVal());
				break;
			}
			
			case "SetFlying": {
				((Player) target).setFlying(tags.get("Flying") == "Enable");
				break;
			}
			
			case "SetGliding": {
				target.setGliding(tags.get("Gliding") == "Enable");
				break;
			}
			
			case "BoostElytra": {
				//TODO
				break;
			}
			
			case "SetRotation": {
				float pitch = (float) (double) args.get("pitch").getVal();
				float yaw = (float) (double) args.get("yaw").getVal();
				target.setRotation(pitch, yaw);
				break;
			}
			
			case "FaceLocation": {
				Location loc = (Location) args.get("loc").getVal();
				Vector lookVector = loc.toVector().subtract(target.getEyeLocation().toVector());
				Location playerLoc = target.getEyeLocation().setDirection(lookVector);
				target.setRotation(playerLoc.getPitch(), playerLoc.getYaw());
				break;
			}
			
			case "SetVelocity": {
				Vector vector = (Vector) args.get("vector").getVal();
				if(tags.get("Add to Current Velocity") == "False") target.setVelocity(vector);
				else target.setVelocity(target.getVelocity().add(vector));
				break;
			}
			
			case "SpectateTarget": {
				break; //TODO: Annoying packet stuff...
			}
			
			case "SetSpawnPoint": {
				PlayerData playerData = PlayerData.getPlayerData(target.getUniqueId());
				if(args.get("loc").getVal() == null) playerData.respawnLoc = null;
				else playerData.respawnLoc = (Location) args.get("loc").getVal();
				break;
			}
			
			case "LaunchProj": {
				ItemStack proj = (ItemStack) args.get("projectile").getVal();
				Location launchPoint = args.get("origin").getVal() == null ? target.getEyeLocation() : (Location) args.get("origin").getVal();
				String projectileName = (String) args.get("name").getVal();
				double speed = (double) args.get("speed").getVal();
				double inaccuracy = (double) args.get("inaccuracy").getVal();
				//TODO
				break;
			}
			
			case "SetPlayerTime": {
				Integer time = args.get("time").getInt();
				if(time == null) ((Player) target).resetPlayerTime();
				else ((Player) target).setPlayerTime((long) time, false);
				break;
			}
			
			case "SetPlayerWeather": {
				Player player = (Player) target;
				String weatherTag = tags.get("Weather");
				if(weatherTag != "Reset") player.setPlayerWeather(WeatherType.valueOf(weatherTag.toUpperCase()));
				else player.resetPlayerWeather();
				break;
			}
			
			
			case "Kick": {
				((Player) target).kickPlayer("");
				break;
			}
			
			case "Particle": {
				DFParticle[] particles = DFValue.castParticle((DFValue[]) args.get("particles").getVal());
				Location loc = (Location) args.get("loc").getVal();
				Player player = (Player) target;
				
				for(DFParticle particle : particles)
					particle.play(player, loc);
				break;
			}
			
			case "ParticleLine": {
				DFParticle particle = (DFParticle) args.get("particle").getVal();
				Location loc1 = (Location) args.get("loc1").getVal();
				Location loc2 = (Location) args.get("loc2").getVal();
				double spacing = (double) args.get("spacing").getVal();
				
				drawParticleLine((Player) target, particle, loc1, loc2, spacing);
				break;
			}
			
			case "ParticleLineA": {
				DFParticle particle = (DFParticle) args.get("particle").getVal();
				Location loc1 = (Location) args.get("loc1").getVal();
				Location loc2 = (Location) args.get("loc2").getVal();
				double spacing = (double) args.get("spacing").getVal();
				long ticks = (long) (double) args.get("ticks").getVal();
				
				drawParticleLine((Player) target, particle, loc1, loc2, spacing, ticks);
				break;
			}
			
			case "ParticleCuboid": {
				DFParticle particle = (DFParticle) args.get("particle").getVal();
				Location loc1 = (Location) args.get("loc1").getVal();
				Location loc2 = (Location) args.get("loc2").getVal();
				double spacing = (double) args.get("spacing").getVal();
				
				switch(tags.get("Fill Type")){
					case "Wireframe":
						drawCuboidWireframe((Player) target, particle, loc1, loc2, spacing);
						break;
					case "Hollow":
						drawHollowCuboid((Player) target, particle, loc1, loc2, spacing);
						break;
					case "Solid":
						drawSolidCuboid((Player) target, particle, loc1, loc2, spacing);
						break;
				}
				break;
			}
			
			case "ParticleCuboidA": {
				DFParticle particle = (DFParticle) args.get("particle").getVal();
				Location loc1 = (Location) args.get("loc1").getVal();
				Location loc2 = (Location) args.get("loc2").getVal();
				double spacing = (double) args.get("spacing").getVal();
				long ticks = (long) (double) args.get("ticks").getVal();
				
				switch(tags.get("Fill Type")){
					case "Wireframe":
						drawCuboidWireframe((Player) target, particle, loc1, loc2, spacing, ticks);
						break;
					case "Hollow":
						drawHollowCuboid((Player) target, particle, loc1, loc2, spacing, ticks);
						break;
					case "Solid":
						drawSolidCuboid((Player) target, particle, loc1, loc2, spacing, ticks);
						break;
				}
				break;
			}
			
			case "ParticleCircle": {
				DFParticle particle = (DFParticle) args.get("particle").getVal();
				Location loc = (Location) args.get("loc").getVal();
				double diameter = (double) args.get("diameter").getVal();
				drawParticleCircle((Player) target, particle, loc, diameter);
				break;
			}
			
			case "ParticleCircleA": {
				DFParticle particle = (DFParticle) args.get("particle").getVal();
				Location loc = (Location) args.get("loc").getVal();
				double diameter = (double) args.get("diameter").getVal();
				long ticks = (long) (double) args.get("ticks").getVal();
				drawParticleCircle((Player) target, particle, loc, diameter, ticks);
				break;
			}


			// Appearance
			case "SetChatTag": {
				PlayerData playerData = PlayerData.getPlayerData(target.getUniqueId());
				playerData.chatTag = DFValue.castTxt((DFValue[]) args.get("tag").getVal()).join("");
				break;
			}

			case "ChatColor": {
				PlayerData playerData = PlayerData.getPlayerData(target.getUniqueId());
				playerData.chatColor = DFUtilities.regex("§[xXa-fA-Fk-oK-O0-9]", (String)args.get("tag").getVal()).join("");
			}
		}
	}
	
	private static void drawParticleLine(Player player, DFParticle particle, Location loc1, Location loc2, double spacing){
		particle.play(player, loc1); // Since the spacing is added to the first corner before a particle is played, there is often no particle here.
		if(loc1 == loc2) return;
		
		
		double distance = loc1.distance(loc2);
		double distanceTravelled = 0;
		Vector direction = loc2.toVector().subtract(loc1.toVector()).normalize().multiply(spacing);
		
		loc1 = loc1.clone();
		while(distanceTravelled < distance){
			distanceTravelled += spacing;
			loc1.add(direction);
			particle.play(player, loc1);
		}
	}
	
	private static void drawParticleLine(Player player, DFParticle particle, Location loc1, Location loc2, double spacing, long ticks){
		particle.play(player, loc1); // Since the spacing is added to the first corner before a particle is played, there is often no particle here.
		if(loc1 == loc2) return;
		
		
		final double distance = loc1.distance(loc2);
		final Vector direction = loc2.toVector().subtract(loc1.toVector()).normalize().multiply(spacing);
		
		Location temp = loc1.clone();
		new BukkitRunnable() {
			double distanceTravelled = 0;
			
			@Override
			public void run() {
				distanceTravelled += spacing;
				temp.add(direction);
				particle.play(player, temp);
				if(distanceTravelled >= distance) this.cancel();
			}
		}.runTaskTimer(DFPlugin.plugin, 0L, (long) (ticks/(distance/spacing)));
	}
	
	private static void drawCuboidWireframe(Player player, DFParticle particle, Location loc1, Location loc2, double spacing){
		drawParticleLine(player, particle, loc1, setY(setZ(loc2, loc1.getZ()), loc1.getY()), spacing);
		
		drawParticleLine(player, particle, loc1, setY(setX(loc2, loc1.getX()), loc1.getY()), spacing);
		drawParticleLine(player, particle, setX(loc1, loc2.getX()), setY(loc2, loc1.getY()), spacing);
		
		drawParticleLine(player, particle, setZ(loc1, loc2.getZ()), setY(loc2, loc1.getY()), spacing);
		
		drawParticleLine(player, particle, loc1, setX(setZ(loc2, loc1.getZ()), loc1.getX()), spacing);
		drawParticleLine(player, particle, setX(loc1, loc2.getX()), setZ(loc2, loc1.getZ()), spacing);
		drawParticleLine(player, particle, setZ(loc1, loc2.getZ()), setX(loc2, loc1.getX()), spacing);
		drawParticleLine(player, particle, setX(setZ(loc1, loc2.getZ()), loc2.getX()), loc2, spacing);
		
		drawParticleLine(player, particle, setY(loc1, loc2.getY()), setZ(loc2, loc1.getZ()), spacing);
		
		drawParticleLine(player, particle, setY(loc1, loc2.getY()), setX(loc2, loc1.getX()), spacing);
		drawParticleLine(player, particle, setY(setX(loc1, loc2.getX()), loc2.getY()), loc2, spacing);
		
		drawParticleLine(player, particle, setY(setZ(loc1, loc2.getZ()), loc2.getY()), loc2, spacing);
	}
	
	private static void drawCuboidWireframe(Player player, DFParticle particle, Location loc1, Location loc2, double spacing, long ticks){
		// Literally blame spigot for this monstrosity because stacking runnables looks disgusting
		drawParticleLine(player, particle, loc1, setY(setZ(loc2, loc1.getZ()), loc1.getY()), spacing, ticks/7);
		new BukkitRunnable(){
			@Override
			public void run() {
				drawParticleLine(player, particle, loc1, setY(setX(loc2, loc1.getX()), loc1.getY()), spacing, ticks/7);
				drawParticleLine(player, particle, setX(loc1, loc2.getX()), setY(loc2, loc1.getY()), spacing, ticks/7);
				new BukkitRunnable(){
					@Override
					public void run() {
						drawParticleLine(player, particle, setZ(loc1, loc2.getZ()), setY(loc2, loc1.getY()), spacing, ticks/7);
						new BukkitRunnable(){
							@Override
							public void run() {
								drawParticleLine(player, particle, loc1, setX(setZ(loc2, loc1.getZ()), loc1.getX()), spacing, ticks/7);
								drawParticleLine(player, particle, setX(loc1, loc2.getX()), setZ(loc2, loc1.getZ()), spacing, ticks/7);
								drawParticleLine(player, particle, setZ(loc1, loc2.getZ()), setX(loc2, loc1.getX()), spacing, ticks/7);
								drawParticleLine(player, particle, setX(setZ(loc1, loc2.getZ()), loc2.getX()), loc2, spacing, ticks/7);
								new BukkitRunnable(){
									@Override
									public void run() {
										drawParticleLine(player, particle, setY(loc1, loc2.getY()), setZ(loc2, loc1.getZ()), spacing, ticks/7);
										new BukkitRunnable(){
											@Override
											public void run() {
												drawParticleLine(player, particle, setY(loc1, loc2.getY()), setX(loc2, loc1.getX()), spacing, ticks/7);
												drawParticleLine(player, particle, setY(setX(loc1, loc2.getX()), loc2.getY()), loc2, spacing, ticks/7);
												new BukkitRunnable(){
													@Override
													public void run() {
														drawParticleLine(player, particle, setY(setZ(loc1, loc2.getZ()), loc2.getY()), loc2, spacing, ticks/7);
													}
												}.runTaskLater(DFPlugin.plugin, ticks/7);
											}
										}.runTaskLater(DFPlugin.plugin, ticks/7);
									}
								}.runTaskLater(DFPlugin.plugin, ticks/7);
							}
						}.runTaskLater(DFPlugin.plugin, ticks/7);
					}
				}.runTaskLater(DFPlugin.plugin, ticks/7);
			}
		}.runTaskLater(DFPlugin.plugin, ticks/7);
	}
	
	private static void drawHollowCuboid(Player player, DFParticle particle, Location loc1, Location loc2, double spacing){
		Vector max = Vector.getMaximum(loc1.toVector(), loc2.toVector());
		Vector min = Vector.getMinimum(loc1.toVector(), loc2.toVector());
		double length = Math.ceil((max.getZ() - min.getZ())/spacing);
		double width = Math.ceil((max.getX() - min.getX())/spacing);
		double height = Math.ceil((max.getY() - min.getY())/spacing);
		for (double y = min.getY(); y <= max.getY() + Math.ceil(height/spacing - Math.floor(height/spacing)); y += spacing)
			for (double z = min.getZ(); z <= max.getZ() + Math.ceil(length/spacing - Math.floor(length/spacing)); z += spacing)
				for (double x = min.getX(); x <= max.getX() + Math.ceil(width/spacing - Math.floor(width/spacing)); x += spacing)
					if(x <= min.getX() || x >= max.getX() ||
						 y <= min.getY() || y >= max.getY() ||
						 z <= min.getZ() || z >= max.getZ())
						particle.play(player, new Location(player.getWorld(),x,y,z));
	}
	
	private static void drawHollowCuboid(Player player, DFParticle particle, Location loc1, Location loc2, double spacing, long ticks){
		Vector max = Vector.getMaximum(loc1.toVector(), loc2.toVector());
		Vector min = Vector.getMinimum(loc1.toVector(), loc2.toVector());
		double length = Math.ceil((max.getZ() - min.getZ())/spacing);
		double width = Math.ceil((max.getX() - min.getX())/spacing);
		double height = Math.ceil((max.getY() - min.getY())/spacing);
		double totalIterations = (int) Math.ceil(2 * length * width + 2 * length * height + 2 * height * width);
		double iterations = 0;
		double wait = 0;
		
		for (double y = min.getY(); y <= max.getY() + Math.ceil(height/spacing - Math.floor(height/spacing)); y += spacing)
			for (double z = min.getZ(); z <= max.getZ() + Math.ceil(length/spacing - Math.floor(length/spacing)); z += spacing)
				for (double x = min.getX(); x <= max.getX() + Math.ceil(width/spacing - Math.floor(width/spacing)); x += spacing)
					if(x <= min.getX() || x >= max.getX() ||
						 y <= min.getY() || y >= max.getY() ||
						 z <= min.getZ() || z >= max.getZ()) {
						
						double finalY = y;
						double finalX = x;
						double finalZ = z;
						
						iterations++;
						wait += ticks / totalIterations * iterations;
						new BukkitRunnable(){
							@Override
							public void run() {
								particle.play(player, new Location(player.getWorld(), finalX, finalY, finalZ));
							}
						}.runTaskLater(DFPlugin.plugin, (long) wait);
						if(wait >= 1) wait -= Math.floor(wait);
					}
	}
	
	private static void drawSolidCuboid(Player player, DFParticle particle, Location loc1, Location loc2, double spacing){
		Vector max = Vector.getMaximum(loc1.toVector(), loc2.toVector());
		Vector min = Vector.getMinimum(loc1.toVector(), loc2.toVector());
		double length = Math.ceil((max.getZ() - min.getZ())/spacing);
		double width = Math.ceil((max.getX() - min.getX())/spacing);
		double height = Math.ceil((max.getY() - min.getY())/spacing);
		for (double y = min.getY(); y <= max.getY() + Math.ceil(height/spacing - Math.floor(height/spacing)); y += spacing)
			for (double z = min.getZ(); z <= max.getZ() + Math.ceil(length/spacing - Math.floor(length/spacing)); z += spacing)
				for (double x = min.getX(); x <= max.getX() + Math.ceil(width/spacing - Math.floor(width/spacing)); x += spacing)
						particle.play(player, new Location(player.getWorld(), x, y, z));
	}
	
	private static void drawSolidCuboid(Player player, DFParticle particle, Location loc1, Location loc2, double spacing, long ticks){
		Vector max = Vector.getMaximum(loc1.toVector(), loc2.toVector());
		Vector min = Vector.getMinimum(loc1.toVector(), loc2.toVector());
		double length = Math.ceil((max.getZ() - min.getZ())/spacing);
		double width = Math.ceil((max.getX() - min.getX())/spacing);
		double height = Math.ceil((max.getY() - min.getY())/spacing);
		double totalIterations = (int) Math.ceil(length*width*height);
		double iterations = 0;
		double wait = 0;
		
		for (double y = min.getY(); y <= max.getY() + Math.ceil(height/spacing - Math.floor(height/spacing)); y += spacing)
			for (double z = min.getZ(); z <= max.getZ() + Math.ceil(length/spacing - Math.floor(length/spacing)); z += spacing)
				for (double x = min.getX(); x <= max.getX() + Math.ceil(width/spacing - Math.floor(width/spacing)); x += spacing){
					double finalY = y;
					double finalZ = z;
					double finalX = x;
					
					iterations++;
					wait += ticks / totalIterations * iterations;
					new BukkitRunnable(){
						@Override
						public void run() {
							particle.play(player, new Location(player.getWorld(), finalX, finalY, finalZ));
						}
					}.runTaskLater(DFPlugin.plugin, (long) wait);
					if(wait >= 1) wait -= Math.floor(wait);
				}
	}
	
	private static void drawParticleCircle(Player player, DFParticle particle, Location loc, double diameter){
		Vector dir = loc.getDirection().normalize();
		
		for(int n = 1; n <= 360; n++) {
			double x = Math.sin(n);
			double z = Math.cos(n);
			Location temp = loc.clone();
			temp.add(dir.clone().multiply(x * diameter / 2));
			temp.add(DFUtilities.getTangent(dir).multiply(z * diameter / 2));
			particle.play(player, temp);
		}
	}
	
	private static void drawParticleCircle(Player player, DFParticle particle, Location loc, double diameter, long ticks){
		// TODO
//		Vector dir = loc.getDirection().normalize();
//		double wait = 0;
//
//		for(int n = 1; n <= 360; n++) {
//			wait += ticks/360d * n;
//			int finalN = n;
//			new BukkitRunnable(){
//				@Override public void run() {
//					double angle = 2 * Math.PI * finalN / 360;
//					Vector offset = dir.clone().multiply(Math.cos(angle) * diameter/2);
//					offset.setY(Math.sin(angle) * diameter/2);
//					particle.play(player, loc.clone().add(offset));
//					Bukkit.broadcastMessage(finalN + "");
//				}
//			}.runTaskLater(DFPlugin.plugin, (long) wait);
//			if(wait >= 1) wait -= Math.floor(wait);
//		}
	}
	
	private static Location setZ(Location loc, double z){
		Location result = loc.clone();
		result.setZ(z);
		return result;
	}
	
	private static Location setY(Location loc, double y){
		Location result = loc.clone();
		result.setY(y);
		return result;
	}
	
	private static Location setX(Location loc, double x){
		Location result = loc.clone();
		result.setX(x);
		return result;
	}
	
	private static void setBossBar(Player barPlayer, String title, Integer id, double barHealth, BarColor color, BarStyle style, BarFlag[] flags){
		HashMap<String, TreeMap<Integer, BossBar>> handler = DFPlugin.bossbarHandler;
		
		BossBar bar = Bukkit.createBossBar(title, color, style);
		for(BarFlag flag : flags) bar.addFlag(flag);
		bar.setProgress(barHealth);
		TreeMap<Integer, BossBar> currentBars = new TreeMap<>();
		if(handler.containsKey(barPlayer.getName())) currentBars = handler.get(barPlayer.getName());
		else bar.addPlayer(barPlayer);
		
		
		for(Integer key : currentBars.keySet().toArray(new Integer[0])) currentBars.get(key).removePlayer(barPlayer);
		currentBars.put(id, bar);
		for(Integer key : currentBars.keySet().toArray(new Integer[0])) currentBars.get(key).addPlayer(barPlayer);
		
		handler.put(barPlayer.getName(), currentBars);
	}
	
	private static void removeBossbar(Player p, Integer id){
		HashMap<String, TreeMap<Integer, BossBar>> handler = DFPlugin.bossbarHandler;
		
		TreeMap<Integer, BossBar> bars = new TreeMap<>();
		if(handler.containsKey(p.getName())) bars = handler.get(p.getName());
		else{
			handler.put(p.getName(), bars);
			return;
		}
		
		if(!bars.containsKey(id)) return;
		
		for(Integer key : bars.keySet().toArray(new Integer[0])) bars.get(key).removePlayer(p);
		
		if(id != null) bars.remove(id);
		else{
			handler.put(p.getName(), new TreeMap<>());
			return;
		}
		
		for(Integer key : bars.keySet().toArray(new Integer[0])) bars.get(key).addPlayer(p);
		handler.put(p.getName(), bars);
	}
	
	private static void sendMessageSeq(Player player, String[] msgs, long delay, JavaPlugin plugin){
		new BukkitRunnable() {
			int index = 0;
			@Override
			public void run(){
				player.sendMessage(msgs[index++]);
				if(index >= msgs.length) this.cancel();
			}
		}.runTaskTimerAsynchronously(plugin, 0, delay);
	}
	
	private static void playSoundSeq(Player player, DFSound[] sounds, Location loc, long delay, JavaPlugin plugin){
		new BukkitRunnable(){
			int index = 0;
			@Override
			public void run(){
				if(loc != null) player.playSound(loc, sounds[index].sound, sounds[index].volume, sounds[index].pitch);
				else player.playSound(player, sounds[index].sound, sounds[index].volume, sounds[index].pitch);
				
				index++;
				if(index >= sounds.length) cancel();
			}
		}.runTaskTimer(plugin, 0, delay);
	}
	
	private static void sendHover(Player p, String msg, String hoverMsg){
		TextComponent component = new TextComponent(msg);
		component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverMsg)));
		p.spigot().sendMessage(component);
	}
	
	private static void stopSounds(Player p, DFSound[] sounds, SoundCategory category){
		if(category == SoundCategory.MASTER && sounds.length == 0) {
			p.stopAllSounds();
			return;
		}
		
		for (DFSound sound : sounds) p.stopSound(sound.sound, category);
	}

//    public static void createAdvManager(ItemStack[] icons, JSONMessage[] titles, AdvancementDisplay.AdvancementFrame[] frames, boolean save){
//        AdvancementManager manager = new AdvancementManager(new NameKey("DF", "DFManager"));
//        for(int i = 0; i < icons.length; i++){
//            AdvancementDisplay display = new AdvancementDisplay(icons[i], titles[i], new JSONMessage(new TextComponent("")), frames[i], AdvancementVisibility.ALWAYS);
//            int id = (int) Math.floor(Math.random() * 99999999);
//            Advancement adv = new Advancement(new NameKey("DF", "DF" + id), display);
//            adv.setCriteria(new Criteria(1));
//            manager.addAdvancement(adv);
//        }
//
//        Bukkit.getOnlinePlayers().forEach(manager::addPlayer);
//    }
	
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
		DFUtilities.playerConfig.getConfig().set("players." + p.getUniqueId() + ".inventory", String.join("|", result));
		DFUtilities.playerConfig.saveConfig();
	}
	
	private static void loadInv(Player p){
		if(!DFUtilities.playerConfig.getConfig().contains("players." + p.getUniqueId() + ".inventory")) return;
		String[] inv = DFUtilities.playerConfig.getConfig().getString("players." + p.getUniqueId() + ".inventory").split("\\|");
		for(int i = 0; i < inv.length; i++){
			if(inv[i] != null) p.getInventory().setItem(i, DFUtilities.parseItemNBT(inv[i]));
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
}