package me.wonk2.utilities.values;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.enums.DFType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;

public class DFValue {

    public static String[] castTxt(DFValue[] arr){
        String[] result = new String[arr.length];
        for(int i = 0; i < arr.length; i++) result[i] = DFUtilities.parseTxt(arr[i]);
        
        return result;
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

    private final Object val;
    public final Integer slot;
    public final DFType type;

    public DFValue(Object val, Integer slot, DFType type){
        this.val = val;
        this.slot = slot;
        this.type = type;
    }

    public DFValue(Object val, DFType type){
        this.val = val;
        this.type = type;
        this.slot = null;
    }

    public Object getVal(){
        return val;
    }

    public Integer getInt(){
        if(type != DFType.NUM) throw new IllegalArgumentException("Attempt to cast non-number value to integer");
        return (int) (double) val;
    }
}

