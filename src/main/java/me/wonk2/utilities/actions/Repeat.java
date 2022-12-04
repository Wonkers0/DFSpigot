package me.wonk2.utilities.actions;

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
	public Repeat(String targetName, HashMap<String, LivingEntity[]> targetMap, ParamManager paramManager, String action, boolean inverted, HashMap<String, DFValue> localStorage) {
		super(targetName, targetMap, paramManager, action, localStorage, inverted);
		id = Math.random();
	}
	
	@Override
	public boolean evaluateCondition(){
		
		Object[] inputArray = paramManager.formatParameters(targetMap);
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray);
		
		LoopData loopData = LoopData.getLoopData(id);
		if(Bukkit.getOnlinePlayers().toArray().length == 0) return false;
		
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
					loopData.forEach = (DFValue[]) args.get("list").getVal();
				
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
				else if(loopData.gridLoc.getX() != loc2.getX())
					loopData.gridLoc.setX(loopData.gridLoc.getX() + DFUtilities.clampNum(loc2.getX() - loopData.gridLoc.getX(), -1, 1));
				else if(loopData.gridLoc.getZ() != loc2.getZ()){
					loopData.gridLoc.setZ(loopData.gridLoc.getZ() + DFUtilities.clampNum(loc2.getZ() - loopData.gridLoc.getZ(), -1, 1));
					loopData.gridLoc.setX(loc1.getX());
				}
				else if(loopData.gridLoc.getY() != loc2.getY()){
					loopData.gridLoc = loc1.clone().add(0d, DFUtilities.clampNum(loc2.getZ() - loopData.gridLoc.getZ(), -1, 1), 0d);
					loopData.gridLoc.setX(loc1.getX());
					loopData.gridLoc.setZ(loc1.getZ());
				}
				else return false;
				
				DFVar.setVar(var, new DFValue(DFUtilities.getRelativeLoc(loopData.gridLoc), DFType.LOC), localStorage);
				return true;
			}
		}
		
		return false;
	}
}