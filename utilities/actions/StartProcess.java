package me.wonk2.utilities.actions;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.actions.pointerclasses.brackets.Bracket;
import me.wonk2.utilities.internals.CodeExecutor;
import me.wonk2.utilities.internals.ObjectArrWrapper;
import me.wonk2.utilities.values.DFValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class StartProcess extends Action {
	private final String processName;
	public final TargetMode targetMode;
	public final VarStorage varStorage;
	
	
	public StartProcess(String processName, TargetMode targetMode, VarStorage varStorage){
		this.processName = processName;
		this.targetMode = targetMode;
		this.varStorage = varStorage;
	}
	
	public enum TargetMode{
		COPY_ALL, // With current targets
		COPY_SELECTION, // With current selection
		COPY_NONE, // No targets
		FOR_EACH // For each in selection
	}
	
	public enum VarStorage{
		NEW, // Don't copy
		COPY, // Copy
		ALIAS // Share
	}
	
	public ObjectArrWrapper getProcess(HashMap<String, Entity[]> targetMap, HashMap<String, DFValue> localVars, HashMap<String, Object> specifics){
		Object[] process = DFPlugin.functions.get(processName).clone();
		
		for(int i = 0; i < process.length; i++){
			Object codeBlock = process[i];
			if(codeBlock instanceof Action action){
				action = action.clone();
				
				action.targetMap = targetMap;
				action.localStorage = localVars;
				if(action.paramManager != null){ // Call Function & Start Process don't have param managers
					action.paramManager.localStorage = localVars;
					action.paramManager.specifics = specifics;
				}
				if(codeBlock instanceof IfGame g) g.specifics = specifics;
				if(codeBlock instanceof SelectObject s) s.specifics = specifics;
				if(action instanceof Repeat r){
					r.specifics = specifics;
					r.id = Math.random();
				}

				
				process[i] = action;
			}
			else if(codeBlock instanceof Bracket bracket) process[i] = bracket.clone();
		}
		
		
		return CodeExecutor.assignPointers(new ObjectArrWrapper(process));
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Entity[]> getTargets(HashMap<String, Entity[]> targetMap){
		targetMap = (HashMap<String, Entity[]>) targetMap.clone();
		switch(targetMode){
			case COPY_ALL:
				return targetMap;
			case COPY_SELECTION:
				final Entity[] temp = targetMap.get("selection");
				return new HashMap<>(){{
					put("selection", temp);
				}};
			case COPY_NONE:
				return new HashMap<>() {{}};
			case FOR_EACH:
				return null; // Handled in CodeExecutor.java
			default:
				throw new IllegalStateException("Error: Process target mode not recognized");
		}
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, DFValue> getVars(HashMap<String, DFValue> localStorage){
		switch(varStorage){
			case NEW:
				return new HashMap<>();
			case COPY:
				return (HashMap<String, DFValue>) localStorage.clone();
			case ALIAS:
				return localStorage;
			default:
				throw new IllegalStateException("Error: Process variable storage not recognized");
		}
	}
}
