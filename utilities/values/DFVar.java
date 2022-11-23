package me.wonk2.utilities.values;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.wonk2.DFPlugin;
import me.wonk2.utilities.enums.Scope;

import java.io.*;
import java.util.HashMap;

public class DFVar {
	public static HashMap<String, DFValue> globalVars = new HashMap<>();
	public static HashMap<String, DFValue> savedVars = new HashMap<>();
    /*
    Variables are mostly just DFValue instances tied to a String (their name). Instances of this class will only be used
    when formatting arguments to mimic the in-game variable data structure.
     */
	
	public Scope scope;
	public String name;
	
	public DFVar(String name, Scope scope){ // Constructor for making an instance of this class. Won't do anything by itself.
		this.name = name;
		this.scope = scope;
	}
	
	
	// Use this method to get the value of a variable
	public static DFValue getVar(DFVar var, HashMap<String, DFValue> localStorage){
		return varExists(var, localStorage) ? determineStorage(var.scope, localStorage).get(var.name) : DFValue.nullVar();
	}
	
	public static Scope getVarScope(String name, HashMap<String, DFValue> localStorage){
		if(localStorage.containsKey(name)) return Scope.LOCAL;
		else if(globalVars.containsKey(name)) return Scope.GLOBAL;
		else if(savedVars.containsKey(name)) return Scope.SAVE;
		
		return Scope.GLOBAL;
	}
	
	public static boolean varExists(DFVar var, HashMap<String, DFValue> localStorage){
		HashMap<String, DFValue> varStorage = determineStorage(var.scope, localStorage);
		return varStorage.containsKey(var.name);
	}
	
	private static HashMap<String, DFValue> determineStorage(Scope varScope, HashMap<String, DFValue> localStorage){
		HashMap<String, DFValue> varStorage = new HashMap<>();
		if(varScope == Scope.GLOBAL) varStorage = globalVars;
		else if (varScope == Scope.LOCAL) varStorage = localStorage;
		else if (varScope == Scope.SAVE) varStorage = savedVars;
		
		
		return varStorage;
	}
	
	
	// Use this to assign a value to a variable.
	public static void setVar(DFVar var, DFValue value, HashMap<String, DFValue> localStorage){
		determineStorage(var.scope, localStorage).put(var.name, value);
	}
	
	public static void serializeSavedVars(){
		HashMap<String, ValWrapper> saveVarsCopy = new HashMap<>();
		for(String key : savedVars.keySet())
			saveVarsCopy.put(key, new ValWrapper(savedVars.get(key).getRawVal(), savedVars.get(key).type));
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(DFPlugin.plugin.getDataFolder().getPath() + "/varData.json"));
			
			String serialized = new ObjectMapper().writeValueAsString(saveVarsCopy);
			writer.write(serialized);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("Error while trying to serialize saved variables: " + e);
		}
	}
	
	public static void deserializeSavedVars(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(DFPlugin.plugin.getDataFolder().getPath() + "/varData.json"));
			String fileContents = reader.readLine();
			reader.close();
			
			HashMap<String, ValWrapper> deserialized = new ObjectMapper().readValue(fileContents, new TypeReference<>() {});
			
			for(String key : deserialized.keySet())
				savedVars.put(key, new DFValue(deserialized.get(key).val, deserialized.get(key).type));
		} catch (IOException e) {
			throw new RuntimeException("Error while trying to retrieve saved variables from file: " + e);
		}
	}
	

}