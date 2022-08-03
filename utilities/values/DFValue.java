package me.wonk2.utilities.values;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.enums.DFType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class DFValue {
	
	public static String[] castTxt(DFValue[] arr){
		return DFUtilities.parseTxt(arr);
	}
	
	public static double[] castNum(DFValue[] arr){
		Object[] values = getValues(arr);
		
		double[] result = new double[values.length];
		for(int i = 0; i < values.length; i++) result[i] = (double) values[i];
		return result;
	}
	
	public static DFSound[] castSound(DFValue[] arr){
		Object[] values = getValues(arr);
		return Arrays.copyOf(values, values.length, DFSound[].class);
	}
	
	public static Location[] castLoc(DFValue[] arr){
		Object[] values = getValues(arr);
		return Arrays.copyOf(values, values.length, Location[].class);
	}
	
	public static ItemStack[] castItem(DFValue[] arr){
		Object[] values = getValues(arr);
		return Arrays.copyOf(values, values.length, ItemStack[].class);
	}
	
	public static PotionEffect[] castPotion(DFValue[] arr){
		Object[] values = getValues(arr);
		return Arrays.copyOf(values, values.length, PotionEffect[].class);
	}
	
	public static DFVar[] castVar(DFValue[] arr){
		Object[] values = getValues(arr);
		return Arrays.copyOf(values, values.length, DFVar[].class);
	}
	
	public static Object[] getValues(DFValue[] arr){
		if(arr == null) return new Object[]{null};
		
		Object[] result = new Object[arr.length];
		for(int i = 0; i < arr.length; i++){
			result[i] = arr[i].val;
		}
		return result;
	}
	
	public static Integer[] getSlots(DFValue[] arr){
		Integer[] result = new Integer[arr.length];
		for(int i = 0; i < arr.length; i++){
			result[i] = arr[i].slot;
		}
		return result;
	}
	
	private Object val;
	public Integer slot = null;
	public DFType type;
	
	public DFValue(Object val, Integer slot, DFType type){
		this.val = sanitizeInput(val);
		this.slot = slot;
		this.type = type;
	}
	
	public DFValue(Object val, DFType type){
		this.val = sanitizeInput(val);
		this.type = type;
	}
	
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private Object sanitizeInput(Object val){
		if(val instanceof ArrayList){
			ArrayList<DFValue> result = new ArrayList<>();
			for(LinkedHashMap item : (ArrayList<LinkedHashMap>) val){
				if(item == null){
					result.add(new DFValue(null, DFType.ANY));
					continue;
				}
				
				result.add(new DFValue(item.get("val"), DFType.valueOf((String) item.get("type"))));
			}
			
			return result.toArray(new DFValue[0]);
		}
		else if(val instanceof Integer) return (double) (int) val;
		
		return val;
	}
	
	public DFValue() {}
	
	public Object getVal(){
		return sanitizeInput(val);
	}
	
	public Integer getInt(){
		if(type != DFType.NUM) throw new IllegalArgumentException("Attempt to cast non-number value to integer");
		return (int) (double) val;
	}
	
	public static DFValue nullVar(){
		return new DFValue(0, DFType.NUM);
	}
}
