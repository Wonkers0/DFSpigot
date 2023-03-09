package me.wonk2.utilities.values;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.enums.SelectionType;
import me.wonk2.utilities.internals.CodeExecutor;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class TextCode {
	
	public static String getCodeValue(HashMap<String, Entity[]> targetMap, HashMap<String, DFValue> localStorage, String code, String contents){
		if (!contents.equals(""))
			switch(code){
				case "%var" -> {
					return DFUtilities.parseTxt(DFVar.getVar(new DFVar(contents, DFVar.getVarScope(contents, localStorage)), localStorage));
				}
				case "%random" -> {
					String[] nums = contents.split(",");
					int num1 = Integer.parseInt(nums[0]);
					int num2 = Integer.parseInt(nums[1]);
					
					return String.valueOf(Math.round(Math.random() * (num2 - num1) + num1));
				}
				case "%index" -> {
					String[] innerData = contents.split(",");
					String listName = innerData[0];
					int listIndex = Integer.parseInt(innerData[1]);
					DFValue[] list = (DFValue[]) DFVar.getVar(new DFVar(listName, DFVar.getVarScope(listName, localStorage)), localStorage).getVal();
					
					if(list.length < listIndex || listIndex < 0) return DFUtilities.parseTxt(DFValue.nullVar());
					else return DFUtilities.parseTxt(list[listIndex - 1]);
				}
				case "%math" -> { // TODO: %math can also be used to concatenate strings on DF
					char[] chars = contents.toCharArray();
					ArrayList<Character> operators = new ArrayList<>(List.of('+', '-', '*', '/'));
					int termIndex = 0;
					double result = 0;
					
					for(int i = 0; i < chars.length; i++)
						if(operators.contains(chars[i])){
							result = evaluateMathTerm(chars, termIndex, i - 1, result);
							termIndex = i + 1;
						}
					result = evaluateMathTerm(chars, termIndex, chars.length - 1, result);
					
					return DFUtilities.parseTxt(new DFValue(result, DFType.NUM));
				}
				case "%round" -> {
					return DFUtilities.parseTxt(new DFValue(Math.floor(Double.parseDouble(contents)), DFType.NUM));
				}
			}
		
		throw new NotImplementedException("This text code is either invalid or is not supported yet: " + code);
	}
	
	private static double evaluateMathTerm(char[] chars, int termIndex, int endIndex, double currentSum){
		double term = Double.parseDouble(new String(DFUtilities.trimArray(chars, termIndex, endIndex)));

		if(termIndex == 0) currentSum = term;
		else switch(chars[termIndex - 1]){
			case '+' -> currentSum += term;
			case '-' -> currentSum -= term;
			case '*' -> currentSum *= term;
			case '/' -> currentSum /= term;
			default -> throw new IllegalArgumentException("Invalid math operator: " + chars[endIndex]);
		}
		
		return currentSum;
	}
	
	public static String getTargetName(HashMap<String, Entity[]> targetMap, String code){
		ArrayList<String> targetCodes = new ArrayList<>(List.of(new String[]{"%default", "%victim", "%damager", "%killer", "%selected", "%shooter", "%projectile", "%uuid"}));
		if(!targetCodes.contains(code)) return code;
		
		code = code.equals("%selected") ? "%selection" : code;
		Entity[] selection = targetMap.getOrDefault("selection", null);
		boolean hasSelection = selection != null && selection.length != 0 && selection[0] != null;
		
		Entity[] defaultTarget = targetMap.getOrDefault("default", null);
		boolean hasDefault = defaultTarget != null && defaultTarget.length != 0 && defaultTarget[0] != null;
		
		String filteredCode = code.replace("%", "");
		
		// forgive me for the code below
		if(code.equals("%uuid")) return hasSelection ? selection[0].getUniqueId().toString() : (hasDefault ? defaultTarget[0].getUniqueId().toString() : code);
		else if(code.equals("%selection")) return hasSelection ? selection[0].getName() : (hasDefault ? defaultTarget[0].getName() : code);
		else return targetMap.containsKey(filteredCode) ? targetMap.get(filteredCode)[0].getName() : code;
	}
}
