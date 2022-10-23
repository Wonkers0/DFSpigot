package me.wonk2.utilities.actions.pointerclasses;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.values.DFValue;
import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.HashMap;

public abstract class Conditional extends Action{
	public Action bracketPointer;
	
	public Conditional(String targetName, HashMap<String, LivingEntity> targetMap, Object[] inputArray, String action){
		super(targetName, targetMap, inputArray, action);
	}
	
	public boolean evaluateCondition(){ return false; }
}
