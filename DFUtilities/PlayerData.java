package me.wonk2.Utilities;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.UUID;

public final class PlayerData { // Permanently borrowed PlayerData class from CheaterTim
  public boolean canPvP = true;
  public boolean deathDrops = false;
  public boolean keepInv = false;
  public boolean instantRespawn = false;
  public Material[] allowedBlocks = new Material[0];
  
  public static final HashMap<UUID, PlayerData> data = new HashMap<>();
  public static PlayerData getPlayerData(UUID uuid) {
    if(!data.containsKey(uuid))
      data.put(uuid, new PlayerData());
    return data.get(uuid);
  }
}
