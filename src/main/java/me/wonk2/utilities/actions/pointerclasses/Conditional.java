package me.wonk2.utilities.actions.pointerclasses;

import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.values.DFValue;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public abstract class Conditional extends Action{
	public Object bracketPointer;
	public boolean inverted;
	
	public Conditional(String targetName, HashMap<String, Entity[]> targetMap, ParamManager paramManager, String action, boolean inverted){
		super(targetName, targetMap, paramManager, action);
		this.inverted = inverted;
	}
	
	public Conditional(String targetName, HashMap<String, Entity[]> targetMap, ParamManager paramManager, String action, HashMap<String, DFValue> localStorage, boolean inverted){
		super(targetName, targetMap, paramManager, action, localStorage);
		this.inverted = inverted;
	}
	
	public abstract boolean evaluateCondition();
}
