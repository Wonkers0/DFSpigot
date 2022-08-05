package me.wonk2.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import me.wonk2.utilities.values.Parameter;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParamManager {
	public static HashMap<String, Parameter[][]> argInfo;
	
	static {
		try {
			ObjectMapper mapper = new ObjectMapper();
			argInfo = mapper.readValue(new URL("https://raw.githubusercontent.com/Wonkers0/DFSpigot/main/DONT%20IMPORT/actionparams.json"), new TypeReference<>(){});
		} catch (IOException ignored) {}
	}
	
	public static Object[] formatParameters(HashMap<Integer, DFValue> input, HashMap<String, String> tags, String methodName, HashMap<String, DFValue> localStorage) {
		HashMap<String, DFValue> args = new HashMap<>();
		Parameter[] params = getParamTemplate(input, argInfo.get(methodName), localStorage);
		
		ArrayList<Integer> keySet = new ArrayList<>(input.keySet());
		
		int i = 0;
		DFValue currentArg = null;
		
		for (Parameter param : params) {
			DFType paramType = param.type;
			
			if(keySet.contains(i)) currentArg = input.get(i);
			
			if (currentArg != null && currentArg.type == DFType.VAR && paramType != DFType.VAR)
				// Do we need the var itself here, or its value? ^
				currentArg = DFVar.getVar((DFVar) currentArg.getVal(), localStorage); // Get var value
			
			
			if (!keySet.contains(i) || (currentArg.type != paramType && paramType != DFType.ANY)) {
				// If there is no argument for this parameter, OR the argument is invalid (type doesn't match),
				// use the default value.
				args.put(param.name, new DFValue(param.defaultValue, i, param.type));
				continue; // i stays the same value because it never reaches the "i++" at the end of the loop
			}
			
			if (param.repeating) {
				ArrayList<DFValue> repeatedValues = new ArrayList<>();
				
				while ((currentArg.type == paramType || paramType == DFType.ANY) && keySet.contains(i)) {
					if (currentArg.type == DFType.VAR && paramType != DFType.VAR)
						currentArg = DFVar.getVar((DFVar) currentArg.getVal(), localStorage);
					
					if (currentArg.getVal().getClass().isArray() && paramType != DFType.LIST) {
						repeatedValues = new ArrayList<>(List.of((DFValue[]) currentArg.getVal()));
						break;
					}
					
					repeatedValues.add(currentArg);
					if(keySet.contains(++i)) currentArg = input.get(i);
				}
				// Set array as the value for this repeating parameter.
				args.put(param.name, new DFValue(repeatedValues.toArray(new DFValue[0]), i, DFType.LIST));
				continue; // If I don't continue here, "i" will be incremented twice (at the end of the loop and also
				          // below)
			} else args.put(param.name, currentArg);
			
			i++;
		}
		
		return new Object[]{args, tags};
	}
	
	private static Parameter[] getParamTemplate(HashMap<Integer, DFValue> input, Parameter[][] templates, HashMap<String, DFValue> localStorage){
		if(templates.length == 1) return templates[0];
		
		int paramIndex = -1;
		for(Parameter[] template : templates){
			for(int i = 0; i < input.size(); i++){
				if(!input.containsKey(i)) break;
				DFValue currentArg = input.get(i);
				
				while(currentArg.type == template[paramIndex].type){
					if(currentArg.type == DFType.VAR && template[i].type != DFType.VAR)
						currentArg = DFVar.getVar((DFVar) currentArg.getVal(), localStorage);
					
					if(currentArg.type != template[paramIndex].type) break;
					
					currentArg = input.get(++i);
				}
				
				if(currentArg.type != template[++paramIndex].type) break;
				if(i == template.length - 1) return template;
				
			}
		}
		
		return templates[0];
	}
}