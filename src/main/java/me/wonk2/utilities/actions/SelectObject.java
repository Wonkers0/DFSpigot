package me.wonk2.utilities.actions;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.actions.pointerclasses.Conditional;
import me.wonk2.utilities.values.DFValue;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class SelectObject extends Action {
	// Used to tell which class conditional actions belong to; See "updateArgInfo()" in DFListeners.java
	List<Entity> existingSelection;
	HashMap<String, Object> specifics;
	String subAction;
	boolean inverted;
	
	public SelectObject(ParamManager paramManager, String action, String subAction, boolean inverted, HashMap<String, DFValue> localStorage, HashMap<String, Object> specifics){
		super(paramManager, action, localStorage);
		this.subAction = subAction;
		this.inverted = inverted;
		this.specifics = specifics;
	}
	
	public Entity[] getSelectedEntities(HashMap<String, Entity[]> targetMap) {
		Object[] inputArray = paramManager.formatParameters(targetMap);
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray);
		
		super.targetMap = targetMap;
		existingSelection = targetMap.get("selection") == null ? new ArrayList<>() : Arrays.asList(targetMap.get("selection"));
		
		switch (action) {
			case "AllEntities" -> {
				return getAllEntities().toArray(Entity[]::new);
			}
			case "AllPlayers" -> {
				return Bukkit.getOnlinePlayers().toArray(Entity[]::new);
			}
			case "EventTarget" -> {
				return targetMap.get(tags.get("Event Target").toLowerCase());
			}
			case "RandomPlayer" -> {
				int size = args.get("size").getInt();
				Entity[] selectedPlayers = new Entity[size];
				ArrayList<Player> players = getOnlinePlayers();
				
				for (int i = 0; i < size; i++) {
					Player player = players.get(new Random().nextInt(size));
					players.remove(player);
					
					selectedPlayers[i] = player;
				}
				
				return selectedPlayers;
			}
			case "LastEntity" -> {
				return new Entity[]{DFUtilities.lastEntity};
			}
			case "PlayerName" -> {
				String[] names = DFValue.castTxt((DFValue[]) args.get("names").getVal());
				for (Player p : Bukkit.getOnlinePlayers())
					for (String name : names)
						if (p.getName().equals(name)) return new LivingEntity[]{p};
				
				return new LivingEntity[0];
			}
			case "EntityName" -> {
				String[] names = DFValue.castTxt((DFValue[]) args.get("names").getVal());
				ArrayList<Entity> result = new ArrayList<>();
				
				for (Entity e : getAllEntities())
					for (String name : names)
						if (e.getName().equals(name) || e.getUniqueId().toString().equals(name))
							result.add(e);
				
				return result.toArray(Entity[]::new);
			}
			case "Invert" -> {
				if (existingSelection.size() == 0) return targetMap.get("default");
				ArrayList<? extends Entity> newSelection = existingSelection.get(0) instanceof Player ? getOnlinePlayers() : getAllLivingEntities();
				
				newSelection.removeIf(e -> existingSelection.contains(e));
				return newSelection.toArray(Entity[]::new);
			}
			case "PlayersCond" -> {
				return filterSelection(Bukkit.getOnlinePlayers().toArray(Entity[]::new));
			}
			case "FilterCondition" -> {
				return filterSelection(existingSelection.toArray(Entity[]::new));
			}
			case "Reset" -> {
				return null;
			}
			default -> throw new NotImplementedException("This select action is either not supported yet, or it is invalid.");
		}
	}
	
	public static ArrayList<Entity> getAllEntities(){
		ArrayList<Entity> temp = new ArrayList<>();
		for(World world : Bukkit.getWorlds())
			for(Entity e : world.getEntities())
				if(!(e instanceof Player))
					temp.add(e);
		
		return temp;
	}
	
	public static ArrayList<LivingEntity> getAllLivingEntities(){
		ArrayList<LivingEntity> temp = new ArrayList<>();
		for(World world : Bukkit.getWorlds())
			for(LivingEntity e : world.getLivingEntities())
				if(!(e instanceof Player))
					temp.add(e);
		
		return temp;
	}
	
	public static ArrayList<Player> getOnlinePlayers(){
		return new ArrayList<>(Arrays.asList(Bukkit.getOnlinePlayers().toArray(Player[]::new)));
	}
	
	public Entity[] filterSelection(@NotNull Entity[] selection){
		if(subAction == null) return selection;
		
		ArrayList<Entity> selectedPlayers = new ArrayList<>();
		
		for(Entity e : selection)
			if(DFUtilities.getConditional(subAction, inverted, paramManager, localStorage, specifics, targetMap, e).evaluateCondition()) selectedPlayers.add(e);
		
		return selectedPlayers.toArray(Entity[]::new);
	}
}
