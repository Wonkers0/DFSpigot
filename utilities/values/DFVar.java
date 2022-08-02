package me.wonk2.utilities.values;

import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.enums.Scope;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

import static me.wonk2.utilities.DFUtilities.varConfig;
import static me.wonk2.utilities.enums.DFType.*;

public class DFVar {
	public static HashMap<String, DFValue> globalVars = new HashMap<>();
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
	
	public static DFValue getVar(DFVar var, HashMap<String, DFValue> localStorage){
		// Use this method to get the value of a variable
		return varExists(var, localStorage) ? determineStorage(var.scope, localStorage).get(var.name) : DFValue.nullVar();
	}
	
	public static boolean varExists(DFVar var, HashMap<String, DFValue> localStorage){
		HashMap<String, DFValue> varStorage = determineStorage(var.scope, localStorage);
		return varStorage.containsKey(var.name);
	}
	
	private static HashMap<String, DFValue> determineStorage(Scope varScope, HashMap<String, DFValue> localStorage){
		HashMap<String, DFValue> varStorage = new HashMap<>();
		if(varScope == Scope.GLOBAL) varStorage = globalVars;
		else if (varScope == Scope.LOCAL) varStorage = localStorage;
		else if (varScope == Scope.SAVE) /*TODO: Grab saved variable storage from .yml file*/;
		
		return varStorage;
	}
	
	private static final String DOT = "§";
	
	public static void setVar(DFVar var, DFValue value, HashMap<String, DFValue> localStorage){
		// Use this to assign a value to a variable.
		if(var.scope == Scope.GLOBAL) globalVars.put(var.name, value);
		else if(var.scope == Scope.LOCAL) localStorage.put(var.name, value);
		else if(var.scope == Scope.SAVE) {
			FileConfiguration config = varConfig.getConfig();
			
			String poorlySanitizedName = var.name.replace(".", DOT);
			
			String varPath = "vars." + poorlySanitizedName;
			
			//config.set("vars." + var.name.replace(".", DOT) + ".val", value.getVal());
			
			switch(value.type) {
				case TXT:
				case NUM:
					config.set(varPath + ".val", value.getVal());
					break;
				case SND:
					DFSound sound = (DFSound) value.getVal();
					config.set(varPath + ".val.name", sound.sound.toString());
					config.set(varPath + ".val.volume", sound.volume);
					config.set(varPath + ".val.pitch", sound.pitch);
					break;
				case LOC:
					Location location = (Location) value.getVal();
					config.set(varPath + ".val.x", location.getX());
					config.set(varPath + ".val.y", location.getY());
					config.set(varPath + ".val.z", location.getZ());
					config.set(varPath + ".val.pitch", location.getPitch());
					config.set(varPath + ".val.yaw", location.getYaw());
					config.set(varPath + ".val.world", location.getWorld().getName());
					break;
				case ITEM:
					ItemStack item = (ItemStack) value.getVal();
					Map<String, Object> serialized = item.serialize();
					for(String key : serialized.keySet()) {
						config.set(varPath + ".val." + key, serialized.get(key));
					}
					break;
				case POT:
					PotionEffect potion = (PotionEffect) value.getVal();
					config.set(varPath + ".val.type", potion.getType().getName());
					config.set(varPath + ".val.duration", potion.getDuration());
					config.set(varPath + ".val.amplifier", potion.getAmplifier());
					config.set(varPath + ".val.ambient", potion.isAmbient());
					config.set(varPath + ".val.particles", potion.hasParticles());
					break;
			}
			
			config.set("vars." + var.name.replace(".", DOT) + ".type", value.type.toString());
			varConfig.saveConfig();
		}
	}
	

}