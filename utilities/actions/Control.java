package me.wonk2.utilities.actions;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.values.DFValue;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class Control extends Action {
	ParamManager paramManager;
	public HashMap<String, DFValue> args;
	public HashMap<String, String> tags;
	public Control(String targetName, HashMap<String, LivingEntity[]> targetMap, ParamManager paramManager, String action){
		super(targetName, targetMap, paramManager, action);
		
		this.paramManager = paramManager;
	}
	
	public void formatParams(){
		Object[] inputArray = paramManager.formatParameters(targetMap);
		args = DFUtilities.getArgs(inputArray);
		tags = DFUtilities.getTags(inputArray);
	}
}
