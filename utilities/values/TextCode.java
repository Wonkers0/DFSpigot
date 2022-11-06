package me.wonk2.utilities.values;

import me.wonk2.utilities.DFUtilities;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public abstract class TextCode {
	
	public static Object getCodeValue(HashMap<String, LivingEntity> targetMap, HashMap<String, DFValue> localStorage, String code, String contents){
		switch(code){
			case "%victim":
			case "%damager":
			case "%killer":
			case "%selected":
			case "%default":
				return targetMap.get(code.replace("%", "")).getName();
			case "%var":
				return DFUtilities.parseTxt(DFVar.getVar(new DFVar(contents, DFVar.getVarScope(contents, localStorage)), localStorage));
		}
		
		throw new NotImplementedException("This text code is either invalid or is not supported yet!");
	}
}
