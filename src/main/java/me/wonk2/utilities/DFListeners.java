package me.wonk2.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.wonk2.DFPlugin;
import me.wonk2.utilities.actions.SelectObject;
import me.wonk2.utilities.internals.EntityData;
import me.wonk2.utilities.internals.PlayerData;
import me.wonk2.utilities.values.DFVar;
import org.bukkit.*;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DFListeners implements Listener, CommandExecutor {
	@EventHandler
	public static void PlayerLeave(PlayerQuitEvent event){
		if(Bukkit.getOnlinePlayers().size() == 1){
			DFVar.globalVars = new HashMap<>(); 	// Purge all global vars when all players leave
			for(Entity e : SelectObject.getAllEntities()) e.remove(); // Remove all entities
		}
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
	
	@EventHandler
	public static void BlockBreak(BlockBreakEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public static void BlockPlace(BlockPlaceEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public static void PlayerChatEvent(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId());
		
		
		String format = "<chat-tag><player>: <message>";
		format = format.replace("<chat-tag>", playerData.chatTag);
		format = format.replace("<player>", ChatColor.RESET + player.getDisplayName());
		format = format.replace("<message>", playerData.chatColor + event.getMessage());
		event.setFormat(format);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player && label.equals("dfspigot") && sender.isOp())) return true;
		Player player = (Player) sender;
		if(args[0].equalsIgnoreCase("load")){
			switch(args[1].toLowerCase()){
				case "build": {
					File schematic = new File(DFPlugin.plugin.getDataFolder().getAbsolutePath() + File.separator + "/schematics/" + args[2] + ".schem");
					Location origin = DFUtilities.floorLoc(player.getLocation());
					DFUtilities.pasteSchematic(origin, schematic);
					
					player.sendMessage("§a‹ §2✔ §a› §a§oSuccessfully uploaded build.");
					if (args[3].equalsIgnoreCase("true")) setOrigin(player, origin);
					break;
				}
			}
		}
		
		if(args[0].equalsIgnoreCase("set")){
			switch(args[1].toLowerCase()){
				case "origin": {
					setOrigin(player, DFUtilities.floorLoc(player.getLocation()));
					break;
				}
			}
		}
		
		if(args[0].equalsIgnoreCase("test")){
			switch(args[1].toLowerCase()){
				case "particle": {
					player.spawnParticle(Particle.valueOf(args[2]), player.getEyeLocation(), 1);
					break;
				}
			}
		}
		
		if(args[0].equalsIgnoreCase("reload")){
			switch(args[1].toLowerCase()){
				case "args": {
					player.sendMessage("§2✔ §aReloaded args!");
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
					updateArgInfo();
					break;
				}
			}
		}
		
		return true;
	}
	
	private void setOrigin(Player player, Location origin){
		DFPlugin.origin = origin;
		origin.setY(0);
		player.sendMessage("§2ℹ §aAll location values will now be relative to " + DFUtilities.locToString(DFPlugin.origin));
	}
	
	public static void updateArgInfo(){
		try {
			ParamManager.argInfo = new ObjectMapper().readValue(new URL("https://raw.githubusercontent.com/Wonkers0/DFSpigot/main/DONT%20IMPORT/actionparams.json"), new TypeReference<>(){});
			
			HashMap<String, String> condInfo = new HashMap<>();
			ArrayList<String> validConditionals = new ArrayList<>(List.of(new String[]{"IFPLAYER", "IFENTITY", "IFGAME", "IFVAR"}));
			
			for(String action : ParamManager.argInfo.keySet()){
				String[] actionInfo = action.split(":");
				if(validConditionals.contains(actionInfo[0])) condInfo.put(actionInfo[1], actionInfo[0]);
			}
			SelectObject.condInfo = condInfo;
		}
		catch (IOException e) {throw new RuntimeException(e);}
	}
}
