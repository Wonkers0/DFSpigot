package me.wonk2;

import me.wonk2.utilities.*;
import me.wonk2.utilities.enums.*;
import me.wonk2.utilities.values.*;
import me.wonk2.utilities.actions.*;
import me.wonk2.utilities.actions.pointerclasses.brackets.*;
import me.wonk2.utilities.internals.CodeExecutor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{
	public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();
	public static Location origin = new Location(null, 0, 0, 0);
	public static JavaPlugin plugin;
	public static Logger logger;
	public static HashMap<String, Object[]> functions = new HashMap<>();
	
	@EventHandler
	public void LeftClick (PlayerInteractEvent event){
		int funcStatus;
		HashMap<String, DFValue> localVars = new HashMap<>();
		HashMap<String, LivingEntity[]> targets = new HashMap<>(){{
			put("default", new LivingEntity[]{event.getPlayer()});
		}};
		
		HashMap<String, Object> specifics = new HashMap<>(){{
			put("block", DFUtilities.getEventLoc(event.getPlayer(), event.getClickedBlock()));
			put("item", event.getItem());
			put("cancelled", event.isCancelled());
		}};
		
		if((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) && event.getHand() == EquipmentSlot.HAND)
			CodeExecutor.executeThread(
				new Object[]{
					new SetVariable(
						"selection", targets, new ParamManager(
						new HashMap<>(){{
							put(0, new DFValue(new DFVar("loc", Scope.LOCAL), 0, DFType.VAR));
							put(1, new DFValue(new GameValue(Value.Location, "default"), 1, DFType.GAMEVAL));
							put(2, new DFValue("100", 2, DFType.NUM));
							put(3, new DFValue("100", 3, DFType.NUM));
							put(4, new DFValue("100", 4, DFType.NUM));
						}},
						new HashMap<>(){}, "SETVAR:ShiftAllAxes", localVars), "ShiftAllAxes", localVars
					),
//					new Control(
//						"selection", targets, new ParamManager(
//						new HashMap<>(){},
//						new HashMap<>(){}, "CONTROL:End", localVars), "End"
//					),
					new Repeat(
						null, targets, new ParamManager(
						new HashMap<>(){{
							put(0, new DFValue(new DFVar("block", Scope.LOCAL), 0, DFType.VAR));
							put(1, new DFValue(new GameValue(Value.Location, "default"), 1, DFType.GAMEVAL));
							put(2, new DFValue(new DFVar("loc", Scope.LOCAL), 2, DFType.VAR));
						}},
						new HashMap<>(){}, "REPEAT:Grid", localVars), "Grid", false, localVars
					),
					new SetVariable(
						"selection", targets, new ParamManager(
						new HashMap<>(){{
							put(0, new DFValue(new DFVar("perlin", Scope.LOCAL), 0, DFType.VAR));
							put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
						}},
						new HashMap<>(){{
							put("Cell Edge Type", "Euclidean");
						}}, "SETVAR:VoronoiNoise", localVars), "VoronoiNoise", localVars
					),
					new IfVariable(
						"selection", targets, new ParamManager(
						new HashMap<>(){{
							put(0, new DFValue(new DFVar("perlin", Scope.LOCAL), 0, DFType.VAR));
							put(1, new DFValue("0.5", 1, DFType.NUM));
						}},
						new HashMap<>(){}, "IFVAR:>=", localVars), ">=", false, localVars
					),
					new GameAction(
						"selection", targets, new ParamManager(
						new HashMap<>(){{
							put(0, new DFValue(DFUtilities.parseItemNBT("{Count:1b,DF_NBT:3105,id:\"minecraft:tuff\"}"), 0, DFType.ITEM));
							put(1, new DFValue(new DFVar("block", Scope.LOCAL), 1, DFType.VAR));
						}},
						new HashMap<>(){}, "GAMEACTION:SetBlock", localVars), "SetBlock"
					),
					new ClosingBracket(),
					new RepeatingBracket(),
					new PlayerAction(
						"selection", targets, new ParamManager(
						new HashMap<>(){},
						new HashMap<>(){{
							put("Text Value Merging", "Add spaces");
							put("Alignment Mode", "Regular");
						}}, "PLAYERACTION:SendMessage", localVars), "SendMessage"
					),
				}, targets, localVars, event, SelectionType.PLAYER, specifics);
	}
	
	@EventHandler
	public void Join (PlayerJoinEvent event){
		int funcStatus;
		HashMap<String, DFValue> localVars = new HashMap<>();
		HashMap<String, LivingEntity[]> targets = new HashMap<>(){{
			put("default", new LivingEntity[]{event.getPlayer()});
		}};
		
		HashMap<String, Object> specifics = new HashMap<>(){{
			put("cancelled", false);
			put("item", new ItemStack(Material.AIR));
		}};
		
		CodeExecutor.executeThread(
			new Object[]{
				new PlayerAction(
					"selection", targets, new ParamManager(
					new HashMap<>(){},
					new HashMap<>(){}, "PLAYERACTION:SpectatorMode", localVars), "SpectatorMode"
				),
			}, targets, localVars, null, SelectionType.PLAYER, specifics);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		return true;
	}
	
	@Override
	public void onEnable(){
		plugin = this;
		logger = Bukkit.getLogger();
		
		DFUtilities.init();
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new DFListeners(), this);
		this.getCommand("dfspigot").setExecutor(new DFListeners());
	}
	@Override
	public void onDisable(){
		DFVar.serializeSavedVars();
	}
}