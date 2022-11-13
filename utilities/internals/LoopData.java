package me.wonk2.utilities.internals;

import me.wonk2.utilities.values.DFValue;
import org.bukkit.Location;

import java.util.HashMap;

public class LoopData {
	public static HashMap<Pair, LoopData> loopVars = new HashMap<>();
	
	public int iterationCount = 0;
	public DFValue[] forEach;
	public Location gridLoc;
	
	public static LoopData getLoopData(double id, double threadID){
		if(!loopVars.containsKey(new Pair(id, threadID))) newData(id, threadID);
		return loopVars.get(new Pair(id, threadID));
	}
	
	public static void newData(double id, double threadID){
		loopVars.put(new Pair(id, threadID), new LoopData());
	}
}
