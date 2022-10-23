package me.wonk2.utilities;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.actions.SetVariable;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.internals.EntityData;
import me.wonk2.utilities.internals.PlayerData;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.io.File;
import java.util.HashMap;

public class DFListeners implements Listener, CommandExecutor {
	@EventHandler
	public static void PlayerLeave(PlayerQuitEvent event){
		if(Bukkit.getOnlinePlayers().size() == 1) DFVar.globalVars = new HashMap<>(); // Purge all global vars when all players leave
	}
	
	@EventHandler
	public static void ClickMenuSlot(InventoryClickEvent event){
		if(DFUtilities.inCustomInv((Player) event.getView().getPlayer())) event.setCancelled(true); // ClickSlot event triggered from inside custom GUI
	}
	
	@EventHandler
	public static void PlayerDmgPlayer(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
			if(!PlayerData.getPlayerData(event.getEntity().getUniqueId()).canPvP)
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public static void PlaceBlock(BlockPlaceEvent event){
		PlayerData playerData = PlayerData.getPlayerData(event.getPlayer().getUniqueId());
		//if(!playerData.allowedBlocks.contains(event.getBlockPlaced().getType())) event.setCancelled(true);
	}
	
	@EventHandler
	public static void BreakBlock(BlockBreakEvent event){
		PlayerData playerData = PlayerData.getPlayerData(event.getPlayer().getUniqueId());
		//if(!playerData.allowedBlocks.contains(event.getBlock().getType())) event.setCancelled(true);
	}
	
	@EventHandler
	public static void Death(PlayerDeathEvent event){
		PlayerData playerData = PlayerData.getPlayerData(event.getEntity().getUniqueId());
		if(!playerData.deathDrops) event.getDrops().clear();
		if(playerData.keepInv) event.setKeepInventory(true);
		if(playerData.instantRespawn) event.getEntity().spigot().respawn();
	}
	
	@EventHandler
	public static void EntityDeath(EntityDeathEvent event){
		EntityData data = EntityData.getEntityData(event.getEntity().getUniqueId());
		if(!data.deathDrops) event.getDrops().clear();
	}
	
	@EventHandler
	public static void Explode(EntityExplodeEvent event){
		event.setCancelled(true);
		Entity entity = event.getEntity();
		
		entity.getWorld().createExplosion(entity.getLocation(), EntityData.getEntityData(entity.getUniqueId()).tntPower);
	}
	
	@EventHandler
	public static void Respawn(PlayerRespawnEvent event){
		PlayerData playerData = PlayerData.getPlayerData(event.getPlayer().getUniqueId());
		if(playerData.respawnLoc != null) event.setRespawnLocation(playerData.respawnLoc);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player && label.equals("dfspigot") && sender.isOp())) return true;
		Player player = (Player) sender;
		if(args[0].equalsIgnoreCase("load")){
			switch(args[1].toLowerCase()){
				case "build": {
					File schematic = new File(DFPlugin.plugin.getDataFolder().getAbsolutePath() + File.separator + "/schematics/" + args[2] + ".schem");
					DFUtilities.pasteSchematic(player.getLocation(), schematic);
					
					player.sendMessage("§a‹ §2✔ §a› §a§oSuccessfully uploaded build.");
					if (args[3].equalsIgnoreCase("true")) setOrigin(player);
					break;
				}
			}
		}
		
		if(args[0].equalsIgnoreCase("set")){
			switch(args[1].toLowerCase()){
				case "origin": {
					setOrigin(player);
					break;
				}
			}
		}
		
		if(args[0].equalsIgnoreCase("test")){
			switch(args[1].toLowerCase()){
				case "particle": {
					player.spawnParticle(Particle.valueOf(args[2]), player.getEyeLocation(), 1);
				}
			}
		}
		
		return true;
	}
	
	private void setOrigin(Player player){
		DFPlugin.origin = player.getLocation();
		player.sendMessage("§2ℹ §aAll location values will now be relative to " + DFUtilities.parseTxt(new DFValue(new Location(null, 0, 0, 0), DFType.LOC)));
	}
}
