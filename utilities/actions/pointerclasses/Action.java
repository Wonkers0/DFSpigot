package me.wonk2.utilities.actions.pointerclasses;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.values.DFValue;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.HashMap;

public abstract class Action {
	public Object pointer;
	public String targetName;
	public String action;
	public HashMap<String, DFValue> args;
	public HashMap<String, String> tags;
	public HashMap<String, LivingEntity> targetMap;
	
	public Action(String targetName, HashMap<String, LivingEntity> targetMap, Object[] inputArray, String action){
		this.targetName = targetName;
		this.targetMap = targetMap;
		this.args = DFUtilities.getArgs(inputArray);
		this.tags = DFUtilities.getTags(inputArray);
		this.action = action;
	}
	
	public Action setPointer(Object pointer){
		this.pointer = pointer;
		return this;
	}
	
	public LivingEntity getTarget(){
		return targetMap.get(targetName);
	}
	
	public Action(){}
	
	public void invokeAction(){};
}
