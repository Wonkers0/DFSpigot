package me.wonk2.utilities.actions;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.internals.CodeExecutor;
import me.wonk2.utilities.internals.ObjectArrWrapper;
import me.wonk2.utilities.values.DFValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CallFunction extends Action{
	private final String funcName;
	
	public CallFunction(String func){
		this.funcName = func;
	}
	
	public ObjectArrWrapper getFunc(HashMap<String, LivingEntity[]> targetMap, HashMap<String, DFValue> localVars, HashMap<String, Object> specifics){
		if(!DFPlugin.functions.containsKey(funcName)) return new ObjectArrWrapper(new Object[0]);
		
		Object[] function = DFPlugin.functions.get(funcName);
		for(Object codeBlock : function)
			if(codeBlock instanceof Action action){
				action.targetMap = targetMap;
				action.localStorage = localVars;
				if(action.paramManager != null) action.paramManager.localStorage = localVars; // Call Function & Start Process don't have param managers
				if(action instanceof IfGame x) x.specifics = specifics;
			}
		
		
		ObjectArrWrapper func = CodeExecutor.assignPointers(new ObjectArrWrapper(function));
		for(int i = 0; i < func.length; i++)
			if(func.get(i) instanceof Action && ((Action) func.get(i)).pointer == null)
				((Action) func.get(i)).pointer = pointer;
		
		return func;
	}
	
}
