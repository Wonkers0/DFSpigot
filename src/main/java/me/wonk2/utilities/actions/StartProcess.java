package me.wonk2.utilities.actions;

import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.internals.CodeExecutor;
import me.wonk2.utilities.internals.ObjectArrWrapper;
import me.wonk2.utilities.values.DFValue;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class StartProcess extends Action {
	private final Object[] process;
	public final TargetMode targetMode;
	public final VarStorage varStorage;
	
	public StartProcess(Object[] func, TargetMode targetMode, VarStorage varStorage){
		this.process = func;
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
	
	public ObjectArrWrapper getProcess(HashMap<String, LivingEntity[]> targetMap, HashMap<String, DFValue> localVars){
		for(Object codeBlock : process)
			if(codeBlock instanceof Action){
				((Action) codeBlock).targetMap = targetMap;
				((Action) codeBlock).localStorage = localVars;
				((Action) codeBlock).paramManager.localStorage = localVars;
			}
		
		
		ObjectArrWrapper func = CodeExecutor.assignPointers(new ObjectArrWrapper(process));
		for(int i = 0; i < func.length; i++)
			if(func.get(i) instanceof Action && ((Action) func.get(i)).pointer == null)
				((Action) func.get(i)).pointer = pointer;
		
		return func;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, LivingEntity[]> getTargets(HashMap<String, LivingEntity[]> targetMap){
		targetMap = (HashMap<String, LivingEntity[]>) targetMap.clone();
		switch(targetMode){
			case COPY_ALL:
				return targetMap;
			case COPY_SELECTION:
				final LivingEntity[] temp = targetMap.get("selection");
				return new HashMap<>(){{
					put("selection", temp);
				}};
			case COPY_NONE:
				return new HashMap<>() {{
					put("selection", new LivingEntity[]{null});
				}};
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
