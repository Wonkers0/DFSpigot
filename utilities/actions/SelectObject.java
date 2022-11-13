package me.wonk2.utilities.actions;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.values.DFValue;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectObject extends Action {
	
	public SelectObject(ParamManager paramManager, String action) {
		super(paramManager, action);
	}
	
	public LivingEntity[] getSelectedEntities() {
		Object[] inputArray = paramManager.formatParameters(targetMap);
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray);
		
		switch(action){
			case "AllEntities": {
				return getAllLivingEntities().toArray(LivingEntity[]::new);
			}
			
			case "AllPlayers": {
				return Bukkit.getOnlinePlayers().toArray(LivingEntity[]::new);
			}
			
			case "EventTarget": {
				return targetMap.get(tags.get("Event Target").toLowerCase());
			}
			
			case "RandomPlayer": {
			
			}
			
			default:
				throw new NotImplementedException("This select action is either not supported yet, or it is invalid.");
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
}
