package me.wonk2.utilities.actions;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.values.DFValue;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class IfGame {
	public static void invokeAction(Object[] inputArray, String action, LivingEntity target) {
		//TODO: Because of how the target system is set up, certain game actions may not work in entity events.
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray[0]);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray[1]);
		
		switch(action){
			case "BlockEquals": {
			
			}
			
			case "BlockPowered": {
			
			}
		}
	}
}
