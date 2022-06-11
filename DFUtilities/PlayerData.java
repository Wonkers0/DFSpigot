package me.wonk2.Utilities;

import org.bukkit.Material;

import java.util.*;

public final class PlayerData { // Permanently borrowed PlayerData class from CheaterTim
  public boolean canPvP = true;
  public boolean deathDrops = false;
  public boolean keepInv = false;
  public boolean instantRespawn = false;
  public List<Material> allowedBlocks = new LinkedList<>();
  
  public static final HashMap<UUID, PlayerData> data = new HashMap<>();
  public static PlayerData getPlayerData(UUID uuid) {
    if(!data.containsKey(uuid))
      data.put(uuid, new PlayerData());
    return data.get(uuid);
  }
}
