package me.wonk2.utilities.internals.values;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.wonk2.utilities.internals.enums.DFType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;

public class DFValue {

    public static ItemStack parseItemNBT(String rawNBT){
        if(rawNBT == "null") return null;
        CompoundTag nbt = null;
        try{nbt = TagParser.parseTag(rawNBT);}
        catch(CommandSyntaxException e){}

        net.minecraft.world.item.ItemStack nmsItem = net.minecraft.world.item.ItemStack.of(nbt);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static String[] castTxt(DFValue[] arr){
        Object[] values = getValues(arr);
        return Arrays.copyOf(values, values.length, String[].class);
    }

    public static Float[] castNum(DFValue[] arr){
        Object[] values = getValues(arr);
        return Arrays.copyOf(values, values.length, Float[].class);
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

    public Object getVal(){
        return val;
    }

    public Integer getInt(){
        if(type != DFType.NUM) throw new IllegalArgumentException("Attempt to cast non-number value to integer");
        return (int) (float) val;
    }
}

