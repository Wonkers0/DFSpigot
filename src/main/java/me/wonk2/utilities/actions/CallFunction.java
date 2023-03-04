package me.wonk2.utilities.actions;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.actions.pointerclasses.Conditional;
import me.wonk2.utilities.actions.pointerclasses.brackets.Bracket;
import me.wonk2.utilities.actions.pointerclasses.brackets.ClosingBracket;
import me.wonk2.utilities.internals.CodeExecutor;
import me.wonk2.utilities.internals.ObjectArrWrapper;
import me.wonk2.utilities.values.DFValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
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
	
	public ObjectArrWrapper getFunc(HashMap<String, Entity[]> targetMap, HashMap<String, DFValue> localVars, HashMap<String, Object> specifics){
		if(!DFPlugin.functions.containsKey(funcName)) return new ObjectArrWrapper(new Object[0]);
		
		Object[] function = DFPlugin.functions.get(funcName).clone();
		
		for(int i = 0; i < function.length; i++){
			Object codeBlock = function[i];
			if(codeBlock instanceof Action action){
				action = action.clone();
				
				action.targetMap = targetMap;
				action.localStorage = localVars;
				if(action.paramManager != null) action.paramManager.localStorage = localVars; // Call Function & Start Process don't have param managers
				if(codeBlock instanceof IfGame g) g.specifics = specifics;
				if(codeBlock instanceof SelectObject s) s.specifics = specifics;
				if(action instanceof Repeat r){
					r.specifics = specifics;
					r.id = Math.random();
				}
				
				
				function[i] = action;
			}
			else if(codeBlock instanceof Bracket bracket) function[i] = bracket.clone();
		}
		
		
		ObjectArrWrapper func = CodeExecutor.assignPointers(new ObjectArrWrapper(function));
		for(int i = 0; i < func.length; i++){
			if(func.get(i) instanceof Action x && x.pointer == null)
				x.pointer = pointer;
			if(func.get(i) instanceof Conditional x && x.bracketPointer == null)
				x.bracketPointer = pointer;
		}
		
		return func;
	}
	
}
