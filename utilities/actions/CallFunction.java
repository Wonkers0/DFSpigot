package me.wonk2.utilities.actions;

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

public class CallFunction extends Action implements Cloneable{
	private Object[] function;
	
	public CallFunction(Object[] func){
		this.function = func;
	}
	
	public ObjectArrWrapper getFunc(HashMap<String, LivingEntity> targetMap, HashMap<String, DFValue> localVars){
		for(Object codeBlock : function)
			if(codeBlock instanceof Action){
				((Action) codeBlock).targetMap = targetMap;
				((Action) codeBlock).localStorage = localVars;
				((Action) codeBlock).paramManager.localStorage = localVars;
			}
		
		
		ObjectArrWrapper func = CodeExecutor.assignPointers(new ObjectArrWrapper(function));
		for(int i = 0; i < func.length; i++)
			if(func.get(i) instanceof Action && ((Action) func.get(i)).pointer == null)
				((Action) func.get(i)).pointer = pointer;
		
		return func;
	}
	
}
