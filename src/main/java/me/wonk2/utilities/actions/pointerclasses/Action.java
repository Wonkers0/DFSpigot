package me.wonk2.utilities.actions.pointerclasses;

import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.values.DFValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public abstract class Action implements Cloneable {
	public Object pointer;
	public String targetName;
	public String action;
	public ParamManager paramManager;
	public HashMap<String, LivingEntity[]> targetMap;
	public HashMap<String, DFValue> localStorage;
	
	public Action(String targetName, HashMap<String, LivingEntity[]> targetMap, ParamManager paramManager, String action, HashMap<String, DFValue> localStorage){
		this.targetName = targetName;
		this.targetMap = targetMap;
		this.paramManager = paramManager;
		this.action = action;
		this.localStorage = localStorage;
	}
	
	public Action(String targetName, HashMap<String, LivingEntity[]> targetMap, ParamManager paramManager, String action){
		this.targetName = targetName;
		this.targetMap = targetMap;
		this.paramManager = paramManager;
		this.action = action;
	}
	
	public Action(ParamManager paramManager, String action, HashMap<String, DFValue> localStorage){
		this.paramManager = paramManager;
		this.action = action;
		this.localStorage = localStorage;
	}
	
	public void setPointer(Object pointer){
		this.pointer = pointer;
	}
	
	
	public Action(){}
	
	public void invokeAction(){
		throw new IllegalStateException("Error: An action was supposed to run here, but it appears that its class " +
			"is not overriding the \"invokeAction\" method correctly. You should NOT be seeing this, please report " +
			"this to a developer!");
	}
	
	@Override
	public Action clone() {
		try {
			Action clone = (Action) super.clone();
			clone.paramManager = paramManager.clone();
			clone.targetMap = targetMap == null ? null : new HashMap<>(targetMap);
			clone.localStorage = localStorage == null ? null : new HashMap<>(localStorage);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
