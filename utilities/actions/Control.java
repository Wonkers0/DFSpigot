package me.wonk2.utilities.actions;

import me.wonk2.utilities.actions.pointerclasses.Action;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class Control extends Action {
	public Control(String targetName, HashMap<String, LivingEntity> targetMap, Object[] inputArray, String action){
		super(targetName, targetMap, inputArray, action);
	}
}
