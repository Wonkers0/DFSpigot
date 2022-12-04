package me.wonk2.utilities.internals;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public final class PlayerData { // Permanently borrowed PlayerData class from CheaterTim
	public boolean canPvP = true;
	public boolean deathDrops = false;
	public boolean keepInv = false;
	public boolean instantRespawn = false;
	public boolean wasGrounded = true;
	public Location respawnLoc;
	public ArrayList<Material> allowedBlocks = new ArrayList<>();
	public String chatTag = "";
	public String chatColor = "";
	
	private static final HashMap<UUID, PlayerData> data = new HashMap<>();
	
	public static PlayerData getPlayerData(UUID uuid) {
		if(!data.containsKey(uuid))
			data.put(uuid, new PlayerData());
		return data.get(uuid);
	}
	
	public void allowBlocks(Material[] blocks){
		for(Material block : blocks)
			if(!allowedBlocks.contains(block))
				allowedBlocks.add(block);
	}
}