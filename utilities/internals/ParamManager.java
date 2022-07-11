package me.wonk2.utilities.internals;

import me.wonk2.utilities.internals.enums.*;
import me.wonk2.utilities.internals.values.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ParamManager {
    public static HashMap<String, Parameter[]> argInfo = new HashMap<>(){{
        put("SendMessage", new Parameter[]{
            new Parameter("msg", DFType.TXT, null, true)
        });

        put("PlaySound", new Parameter[]{
            new Parameter("sounds", DFType.SND, null, true),
            new Parameter("playbackLoc", DFType.LOC, null, false)
        });

        put("SendTitle", new Parameter[]{
            new Parameter("title", DFType.TXT, null, false),
            new Parameter("subtitle", DFType.TXT, "", false),
            new Parameter("duration", DFType.NUM, 60, false),
            new Parameter("fadeIn", DFType.NUM, 20, false),
            new Parameter("fadeOut", DFType.NUM, 20, false)
        });

        put("SetBossBar", new Parameter[]{
            new Parameter("title", DFType.TXT, "", false),
            new Parameter("health", DFType.NUM, 100d, false),
            new Parameter("maxHealth", DFType.NUM, 100d, false),
            new Parameter("index", DFType.NUM, 1, false)
        });

        put("RemoveBossBar", new Parameter[]{
           new Parameter("index", DFType.NUM, null, false)
        });

        put("ActionBar", new Parameter[]{
            new Parameter("msg", DFType.TXT, null, true)
        });

        put("SendMessageSeq", new Parameter[]{
            new Parameter("msgs", DFType.TXT, null, true),
            new Parameter("delay", DFType.NUM, 60, false)
        });

        put("PlaySoundSeq", new Parameter[]{
           new Parameter("sounds", DFType.SND, null, true),
           new Parameter("delay", DFType.NUM,  60, false),
           new Parameter("playbackLoc", DFType.LOC, null, false)
        });

        put("SendHover", new Parameter[]{
           new Parameter("msg", DFType.TXT, null, false),
           new Parameter("hover", DFType.TXT, null, false)
        });

        put("StopSound", new Parameter[]{
           new Parameter("sounds", DFType.SND, null, true)
        });

        put("SetTabListInfo", new Parameter[]{
           new Parameter("tabInfo", DFType.TXT, null, false)
        });

        put("SetHotbar", new Parameter[]{
           new Parameter("items", DFType.ITEM, new ItemStack[9], true)
        });

        put("GiveItems", new Parameter[]{
           new Parameter("items", DFType.ITEM, null, true)
        });

        put("SetInventory", new Parameter[]{
           new Parameter("items", DFType.ITEM, new ItemStack[27], true)
        });

        put("SetSlotItem", new Parameter[]{
           new Parameter("item", DFType.ITEM, null, false),
           new Parameter("slot", DFType.NUM, null, false)
        });

        put("SetEquipment", new Parameter[]{
           new Parameter("item", DFType.ITEM, null, false)
        });

        put("SetArmor", new Parameter[]{
           new Parameter("head", DFType.ITEM, null, false),
           new Parameter("chest", DFType.ITEM, null, false),
           new Parameter("leggings", DFType.ITEM, null, false),
           new Parameter("feet", DFType.ITEM, null, false)
        });

        put("ReplaceItems", new Parameter[]{
           new Parameter("replaceables", DFType.ITEM, new ItemStack[1], true),
           new Parameter("replacement", DFType.ITEM, null, false),
           new Parameter("amount", DFType.NUM, 1, false)
        });

        put("RemoveItems", new Parameter[]{
           new Parameter("removals", DFType.ITEM, null, true)
        });

        put("ClearItems", new Parameter[]{
           new Parameter("items", DFType.ITEM, null, true)
        });

        put("SetCursorItem", new Parameter[]{
           new Parameter("item", DFType.ITEM, null, false)
        });

        put("ClearInv", new Parameter[0]);

        put("SetItemCooldown", new Parameter[]{
           new Parameter("item", DFType.ITEM, null, false),
           new Parameter("ticks", DFType.NUM, null, false)
        });

        put("SaveInv", new Parameter[0]);

        put("LoadInv", new Parameter[0]);

        put("ShowInv", new Parameter[]{
           new Parameter("items", DFType.ITEM, new ItemStack[0], true)
        });

        put("ExpandInv", new Parameter[]{
           new Parameter("items", DFType.ITEM, new ItemStack[0], true)
        });

        put("SetMenuItem", new Parameter[]{
           new Parameter("slot", DFType.NUM, null, false),
           new Parameter("item", DFType.ITEM, null, false)
        });

        put("SetInvName", new Parameter[]{
           new Parameter("invName", DFType.TXT, null, false)
        });

        put("CloseInv", new Parameter[0]);

        put("RemoveInvRow", new Parameter[]{
           new Parameter("rows", DFType.NUM, 1, false)
        });

        put("AddInvRow", new Parameter[]{
           new Parameter("items", DFType.ITEM, new ItemStack[9], true)
        });

        put("OpenBlockInv", new Parameter[]{
           new Parameter("loc", DFType.LOC, null, false)
        });

        put("Damage", new Parameter[]{
           new Parameter("amount", DFType.NUM, null, false),
           new Parameter("source", DFType.TXT, null, false)
        });

        put("Heal", new Parameter[]{
           new Parameter("amount", DFType.NUM, null, false)
        });

        put("SetHealth", new Parameter[]{
           new Parameter("amount", DFType.NUM, null, false)
        });

        put("SetMaxHealth", new Parameter[]{
           new Parameter("amount", DFType.NUM, null, false)
        });

        put("SetAbsorption", new Parameter[]{
           new Parameter("amount", DFType.NUM, null, false)
        });

        put("SetFoodLevel", new Parameter[]{
           new Parameter("amount", DFType.NUM, null, false)
        });

        put("SetSaturation", new Parameter[]{
           new Parameter("amount", DFType.NUM, null, false)
        });

        put("GiveExp", new Parameter[]{
           new Parameter("amount", DFType.NUM, null, false)
        });

        put("SetExp", new Parameter[]{
           new Parameter("amount", DFType.NUM, null, false)
        });

        put("GivePotion", new Parameter[]{
           new Parameter("effects", DFType.POT, null, true)
        });

        put("RemovePotion", new Parameter[]{
           new Parameter("effects", DFType.POT, null, true)
        });

        put("ClearPotions", new Parameter[0]);

        put("SetSlot", new Parameter[]{
           new Parameter("slot", DFType.NUM, null, false)
        });

        put("SetAtkSpeed", new Parameter[]{
           new Parameter("amount", DFType.NUM, null, false)
        });

        put("SetFireTicks", new Parameter[]{
           new Parameter("ticks", DFType.NUM, null, false)
        });

        put("SetFreezeTicks", new Parameter[]{
           new Parameter("ticks", DFType.NUM, null, false)
        });

        put("SetAirTicks", new Parameter[]{
           new Parameter("ticks", DFType.NUM, null, false)
        });

        put("SetInvulTicks", new Parameter[]{
           new Parameter("ticks", DFType.NUM, null, false)
        });

        put("SetFallDistance", new Parameter[]{
           new Parameter("distance", DFType.NUM, null, false)
        });

        put("SetSpeed", new Parameter[]{
           new Parameter("speed", DFType.NUM, null, false)
        });

        put("SetAllowFlight", new Parameter[0]);

        put("SetDropsEnabled", new Parameter[0]);

        put("SetInventoryKept", new Parameter[0]);

        put("SetCollidable", new Parameter[0]);

        put("InstantRespawn", new Parameter[0]);

        put("EnableBlocks", new Parameter[]{
           new Parameter("blocks", DFType.ITEM, null, true)
        });

        put("DisableBlocks", new Parameter[]{
           new Parameter("blocks", DFType.ITEM, null, true)
        });

        // ↑ Player Action ////////////////////////////////////////////////////////////////////////
        // ↓ Set Variable  ////////////////////////////////////////////////////////////////////////

        put("=", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("value", DFType.ANY, null, false)
        });

        put("RandomValue", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("values", DFType.ANY, null, true)
        });

        put("PurgeVars", new Parameter[]{
           new Parameter("varNames", DFType.TXT, null, true)
        });
        
        put("+", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("nums", DFType.NUM, null, true)
        });

        put("-", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("nums", DFType.NUM, null, true)
        });

        put("x", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("nums", DFType.NUM, null, true)
        });

        put("/", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("nums", DFType.NUM, null, true)
        });

        put("%", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("dividend", DFType.NUM, null, false),
           new Parameter("divisor", DFType.NUM, null, false)
        });

        put("+=", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("nums", DFType.NUM, new Integer[]{1}, true)
        });

        put("-=", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("nums", DFType.NUM, new Integer[]{1}, true)
        });

        put("Exponent", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("num", DFType.NUM, null, false),
           new Parameter("exponent", DFType.NUM, 2, false)
        });

        put("Root", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("num", DFType.NUM, null, false),
           new Parameter("rootIndex", DFType.NUM, 2, false)
        });

        put("Logarithm", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("num", DFType.NUM, null, false),
           new Parameter("base", DFType.NUM, null, false)
        });

        put("ParseNumber", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("txt", DFType.TXT, null, false)
        });

        put("AbsoluteValue", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("num", DFType.NUM, null, false)
        });

        put("ClampNumber", new Parameter[]{
           new Parameter("var", DFType.VAR, null, false),
           new Parameter("num", DFType.VAR, null, false)
        });
    }};

    public static Object[] formatParameters(HashMap<Integer, DFValue> input, HashMap<String, String> tags, String methodName, HashMap<String, DFValue> localStorage) {
        HashMap<String, DFValue> args = new HashMap<>();
        Parameter[] params = argInfo.get(methodName);

        ArrayList<Integer> keySet = new ArrayList<>(input.keySet());
        int paramIndex = 0;
        for (int i = 0; i < input.size(); i++) {
            DFValue currentArg = input.get(i);
            DFType paramType = params[paramIndex].type;

            if(currentArg.type == DFType.VAR && paramType != DFType.VAR)
                // Do we need the var itself here, or its value? ^
                currentArg = DFVar.getVar((DFVar) currentArg.getVal(), localStorage); // Get var value

            if(!keySet.contains(i) || (currentArg.type != paramType && paramType != DFType.ANY)){
                // If there is no argument for this parameter, OR the argument is invalid (type doesn't match),
                // use the default value.
                args.put(params[paramIndex].paramName, new DFValue(params[paramIndex].defaultValue, i--, params[paramIndex].type));
                continue;
            }


            if(params[paramIndex].repeating){
                ArrayList<DFValue> repeatedValues = new ArrayList<>();

                while((currentArg.type == paramType || paramType == DFType.ANY)&& keySet.contains(i)){
                    currentArg = input.get(i);

                    if(currentArg.type == DFType.VAR && paramType != DFType.VAR)
                        currentArg = DFVar.getVar((DFVar) currentArg.getVal(), localStorage);

                    if(currentArg.getVal().getClass().isArray()){
                        repeatedValues = (ArrayList<DFValue>) List.of((DFValue[]) currentArg.getVal());
                        break;
                    }

                    repeatedValues.add(currentArg);

                    i++;
                }
                // Set array as the value for this repeating parameter.
                args.put(params[paramIndex].paramName, new DFValue(repeatedValues.toArray(new DFValue[0]), i, DFType.REPEATING));
            }
            else args.put(params[paramIndex].paramName, currentArg);

            paramIndex++;
        }

        return new Object[]{args, tags};
    }
}
