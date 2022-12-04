package me.wonk2.utilities.internals;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import me.wonk2.utilities.DFUtilities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Simple Single Line clientside hologram with nms
 * @author CheaterTim
 */
public class Hologram {
	protected final Component text;
	protected final Location location;
	
	protected final ArmorStand entity;
	
	public Component getText() {
		return text;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Hologram(String text, Location location) {
		this.text = DFUtilities.componentFromString(text);
		this.location = location;
		this.location.setYaw(0);
		this.location.setPitch(0);
		
		ServerLevel level = ((CraftWorld) this.location.getWorld()).getHandle();
		
		
		this.entity = new ArmorStand(level, location.getX(), location.getY(), location.getZ());
		
		entity.setCustomNameVisible(true);
		entity.setInvisible(true);
		entity.setSmall(true);
		entity.setNoBasePlate(true); // baseplate bad
		entity.setNoGravity(true); // technically not necessary since client side entities don't fall.
		
		
		entity.setCustomName(this.text);
	}
	
	/**
	 * shows the hologram to a player
	 * @param p the player to show the hologram to
	 */
	public void spawn(Player p) {
		ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
		connection.send(new ClientboundAddEntityPacket(entity));
		connection.send(new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData(), false));
	}
	
	/**
	 * hides the hologram from a player
	 * <h2 style="color:red">ONCE A HOLOGRAM IS HIDDEN YOU CANT SHOW IT TO THE PLAYER AGAIN</h2>
	 * @param p the player to hide the hologram from
	 */
	public void hide(Player p) {
		ServerGamePacketListenerImpl connection = ((CraftPlayer) p).getHandle().connection;
		connection.send(new ClientboundRemoveEntitiesPacket(entity.getId()));
	}
	
	
	private static final HashMap<UUID, ArrayList<Hologram>> holograms = new HashMap<>();
	
	private static void hideHologram(Location loc, Player p) {
		loc.setYaw(0);
		loc.setPitch(0);
		UUID uuid = p.getUniqueId();
		if(holograms.containsKey(uuid)) {
			for(Hologram hologram : holograms.get(uuid)) {
				if(hologram.getLocation().equals(loc))
					hologram.hide(p);
			}
			
			holograms.put(uuid, holograms.get(uuid).stream().filter(h -> !h.getLocation().equals(loc)).collect(Collectors.toCollection(ArrayList::new)));
			if(holograms.get(uuid).size() == 0) holograms.remove(uuid);
		}
	}
	
	private static void addHologram(Hologram h, Player p) {
		UUID uuid = p.getUniqueId();
		if(!holograms.containsKey(uuid)) {
			holograms.put(uuid, new ArrayList<>());
		}
		
		holograms.get(uuid).add(h);
	}
	
	/**
	 * should behave just like DiamondFire's DisplayHologram
	 * @param loc the location of the hologram
	 * @param text the text of the hologram, supports hex cods and stuff
	 * @param p the player to show the hologram to
	 */
	public static void showHologram(Location loc, String text, Player p) {
		loc = loc.clone().add(0D, -1.5d, 0d);
		boolean hasText = text != null && text.length() > 0;
		Hologram hologram = hasText ? new Hologram(text, loc) : null;
		
		hideHologram(loc, p);
		if(hasText) {
			hologram.spawn(p);
			addHologram(hologram, p);
		}
	}
}