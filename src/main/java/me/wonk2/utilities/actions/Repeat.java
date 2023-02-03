package me.wonk2.utilities.actions;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.actions.pointerclasses.Conditional;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.internals.LoopData;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class Repeat extends Conditional {
	public double id;
	public Repeat(HashMap<String, LivingEntity[]> targetMap, ParamManager paramManager, String action, boolean inverted, HashMap<String, DFValue> localStorage) {
		super(null, targetMap, paramManager, action, localStorage, inverted);
		id = Math.random();
	}
	
	@Override
	public boolean evaluateCondition(){
		Object[] inputArray = paramManager.formatParameters(targetMap);
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray);
		
		LoopData loopData = LoopData.getLoopData(id);
		if(Bukkit.getOnlinePlayers().toArray().length == 0) return false; // This might not be needed, but testing is needed before removal
		
		switch(action){
			case "Forever": return true;
			
			case "Multiple": {
				loopData.iterationCount++;
				
				if(args.containsKey("var"))
					DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(loopData.iterationCount, DFType.NUM), localStorage);
				
				return loopData.iterationCount <= args.get("iterations").getInt();
			}
			
			case "ForEach": {
				loopData.iterationCount++;
				
				if(loopData.forEach == null || tags.get("Allow List Changes").equals("True"))
					loopData.forEach = (DFValue[]) DFVar.getVar((DFVar) args.get("list").getVal(), localStorage).getVal();
				
				DFVar var = (DFVar) args.get("var").getVal();
				
				if(var != null && loopData.iterationCount <= loopData.forEach.length)
					DFVar.setVar(var, loopData.forEach[loopData.iterationCount - 1], localStorage);
				
				return loopData.iterationCount <= loopData.forEach.length;
			}
			
			case "Grid": {
				DFVar var = (DFVar) args.get("var").getVal();
				Location loc1 = (Location) args.get("loc1").getVal();
				Location loc2 = (Location) args.get("loc2").getVal();
				
				if(loopData.gridLoc == null) loopData.gridLoc = loc1;
				else if(loopData.gridLoc.getBlockX() != loc2.getBlockX())
					loopData.gridLoc.setX(loopData.gridLoc.getBlockX() + DFUtilities.clampNum(loc2.getBlockX() - loopData.gridLoc.getBlockX(), -1, 1));
				else if(loopData.gridLoc.getBlockZ() != loc2.getBlockZ()){
					loopData.gridLoc.setZ(loopData.gridLoc.getBlockZ() + DFUtilities.clampNum(loc2.getBlockZ() - loopData.gridLoc.getBlockZ(), -1, 1));
					loopData.gridLoc.setX(loc1.getBlockX());
				}
				else if(loopData.gridLoc.getBlockY() != loc2.getBlockY()){
					loopData.gridLoc.setY(loopData.gridLoc.getBlockY() + DFUtilities.clampNum(loc2.getBlockY() - loopData.gridLoc.getBlockY(), -1, 1));
					loopData.gridLoc.setX(loc1.getBlockX());
					loopData.gridLoc.setZ(loc1.getBlockZ());
				}
				else return false;
				
				DFVar.setVar(var, new DFValue(DFUtilities.getRelativeLoc(loopData.gridLoc), DFType.LOC), localStorage);
				return true;
			}
		}
		
		return false;
	}
}