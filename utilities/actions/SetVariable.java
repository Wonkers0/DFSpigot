package me.wonk2.utilities.actions;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Random;

public class SetVariable {
    public static void invokeAction(Object[] inputArray, String action, LivingEntity[] targets, HashMap<String, DFValue> localStorage){
        HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray[0]);
        HashMap<String, String> tags = DFUtilities.getTags(inputArray[1]);

        for(LivingEntity target : targets)
            switch(action){
                case "=": {
                    DFVar.setVar((DFVar) args.get("var").getVal(), args.get("value"), localStorage);
                    break;
                }

                case "RandomValue": {
                    DFValue[] values = (DFValue[]) args.get("values").getVal();
                    DFValue value = values[new Random().nextInt(values.length)];
                    DFVar.setVar((DFVar) args.get("var").getVal(), value, localStorage);
                    break;
                }

                case "PurgeVars": {
                    String[] varNames = DFValue.castTxt((DFValue[]) args.get("varNames").getVal());
                    String matchReq = tags.get("Match Requirement");
                    boolean ignoreCase = tags.get("Ignore Case") == "True";

                    DFUtilities.purgeKeys(varNames, DFVar.globalVars, matchReq, ignoreCase);
                    DFUtilities.purgeKeys(varNames, localStorage, matchReq, ignoreCase);
                    /*TODO: Purge save vars once implemented*/
                    break;
                }

                case "+": {
                    Float[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    float result = nums[0];

                    for(int i = 1; i < nums.length; i++) result += nums[i];
                    DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, null, DFType.NUM),localStorage);
                    break;
                }

                case "-": {
                    Float[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    float result = nums[0];

                    for(int i = 1; i < nums.length; i++) result -= nums[i];
                    DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, null, DFType.NUM),localStorage);
                    break;
                }

                case "x": {
                    Float[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    float result = nums[0];

                    for(int i = 1; i < nums.length; i++) result *= nums[i];
                    DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, null, DFType.NUM),localStorage);
                    break;
                }

                case "/": {
                    Float[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    float result = nums[0];

                    for(int i = 1; i < nums.length; i++) result /= nums[i];
                    DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, null, DFType.NUM),localStorage);
                    break;
                }

                case "%": {
                    DFValue val = new DFValue((Float) args.get("dividend").getVal() % (Float) args.get("divisor").getVal(), null, DFType.NUM);
                    DFVar.setVar((DFVar) args.get("var").getVal(), val, localStorage);
                    break;
                }

                case "+=": {
                    DFVar var = (DFVar) args.get("var").getVal();

                    Float[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    Float num = (Float) DFVar.getVar(var, localStorage).getVal();

                    for(float increment : nums) num += increment;
                    DFVar.setVar(var, new DFValue(num, null, DFType.NUM), localStorage);
                    break;
                }

                case "-=": {
                    DFVar var = (DFVar) args.get("var").getVal();

                    Float[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    Float num = (Float) DFVar.getVar(var, localStorage).getVal();

                    for(float decrement : nums) num -= decrement;
                    DFVar.setVar(var, new DFValue(num, null, DFType.NUM), localStorage);
                    break;
                }

                case "Exponent": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    DFValue val = new DFValue(Math.pow((Float) args.get("num").getVal(), (Float) args.get("exponent").getVal()), null, DFType.NUM);

                    DFVar.setVar(var, val, localStorage);
                }

                case "Root": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    DFValue val = new DFValue(Math.pow((Float) args.get("num").getVal(), 1/((Float) args.get("rootIndex").getVal())), null, DFType.NUM);

                    DFVar.setVar(var, val, localStorage);
                }

                case "Logarithm": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    Float num = (Float) args.get("num").getVal();
                    Float base = (Float) args.get("base").getVal();

                    DFValue val = new DFValue(Math.log(num) / Math.log(base), null, DFType.NUM);

                    DFVar.setVar(var, val, localStorage);
                }

                case "ParseNumber": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    String txt = (String) (args.get("txt").getVal() == null ? DFVar.getVar(var, localStorage).getVal() : args.get("txt").getVal());

                    DFVar.setVar(var, new DFValue(Float.parseFloat(txt), null, DFType.NUM), localStorage);
                }

                case "AbsoluteValue": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    Float num = (Float) (args.get("num").getVal() == null ? DFVar.getVar(var, localStorage).getVal() : args.get("num").getVal());

                    DFVar.setVar(var, new DFValue(Math.abs(num), null, DFType.NUM), localStorage);
                }
            }
    }
}
