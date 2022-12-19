package me.wonk2.utilities.internals;

import me.wonk2.utilities.values.DFValue;
import org.bukkit.Location;

import java.util.HashMap;

public class LoopData {
	public static HashMap<Double, LoopData> loopVars = new HashMap<>();
	
	public int iterationCount = 0;
	public DFValue[] forEach;
	public Location gridLoc;
	
	public static void clearLoopData(double id){loopVars.remove(id);}
	
	public static LoopData getLoopData(double id){
		if(!loopVars.containsKey(id)) newData(id);
		return loopVars.get(id);
	}
	
	public static void newData(double id){
		loopVars.put(id, new LoopData());
	}
}
