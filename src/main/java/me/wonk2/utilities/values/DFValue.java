package me.wonk2.utilities.values;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.enums.DFType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class DFValue implements Cloneable {
	
	public static String[] castTxt(DFValue[] arr){
		if(arr == null) return new String[0];
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
	
	public static Vector[] castVector(DFValue[] arr){
		Object[] values = getValues(arr);
		return Arrays.copyOf(values, values.length, Vector[].class);
	}
	
	public static DFParticle[] castParticle(DFValue[] arr){
		Object[] values = getValues(arr);
		return Arrays.copyOf(values, values.length, DFParticle[].class);
	}
	
	public static DFVar[] castVar(DFValue[] arr){
		Object[] values = getValues(arr);
		return Arrays.copyOf(values, values.length, DFVar[].class);
	}
	
	public static Object[] getValues(DFValue[] arr){
		if(arr == null) return new Object[]{null};
		return Arrays.stream(arr).map(value -> value == null ? null : value.getVal()).toArray();
	}
	
	public static Integer[] getSlots(DFValue[] arr){
		return Arrays.stream(arr).map(value -> value == null ? null : value.slot).toArray(Integer[]::new);
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
	
	@Override
	public DFValue clone(){
		try {
			return (DFValue) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@SuppressWarnings({"rawtypes"})
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
	
	public DFValue() {} // for jackson lib
	
	public Object getVal(){
		switch(type){
			case LOC:
				Location l = (Location) val;
				Location o = DFPlugin.origin;
				return l == null ? null : new Location(l.getWorld(), l.getX() + o.getX(), l.getY(), l.getZ() + o.getZ(), l.getYaw(), l.getPitch());
			default:
				return val;
		}
	}
	
	public Object getRawVal(){ // Warning: For internal use only!
		return val;
	}
	
	public void setVal(Object val){
		this.val = val;
	}
	
	
	public Integer getInt(){
		if(type != DFType.NUM) throw new IllegalStateException("Attempt to cast non-number value to integer");
		return (int) (double) val;
	}
	
	public static DFValue nullVar(){
		return new DFValue(0, DFType.NUM);
	}
}