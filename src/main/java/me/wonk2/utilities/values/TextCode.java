package me.wonk2.utilities.values;

import me.wonk2.utilities.DFUtilities;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class TextCode {
	
	public static String getCodeValue(HashMap<String, LivingEntity[]> targetMap, HashMap<String, DFValue> localStorage, String code, String contents){
		switch(code){
			case "%var" -> {
				if (contents.equals("")) break;
				return DFUtilities.parseTxt(DFVar.getVar(new DFVar(contents, DFVar.getVarScope(contents, localStorage)), localStorage));
			}
			case "%random" -> {
				if (contents.equals("")) break;
				
				String[] nums = contents.split(",");
				int num1 = Integer.parseInt(nums[0]);
				int num2 = Integer.parseInt(nums[1]);
				
				return String.valueOf(Math.round(Math.random() * (num2 - num1) + num1));
			}
		}
		
		throw new NotImplementedException("This text code is either invalid or is not supported yet: " + code);
	}
	
	public static String getTargetName(HashMap<String, LivingEntity[]> targetMap, String code){
		ArrayList<String> targetCodes = new ArrayList<>(List.of(new String[]{"%default", "%victim", "%damager", "%killer", "%selected", "%shooter", "%projectile", "%uuid"}));
		if(!targetCodes.contains(code)) return code;
		
		code = code.equals("%selected") ? "%selection" : code;
		String filteredCode = code.replace("%", "");
		if(code.equals("%uuid")) return targetMap.containsKey("selection") ? targetMap.get("selection")[0].getUniqueId().toString() : code;
		else return targetMap.containsKey(filteredCode) ? targetMap.get(filteredCode)[0].getName() : code;
	}
}
