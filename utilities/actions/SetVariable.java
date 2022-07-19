package me.wonk2.utilities.actions;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import org.bukkit.entity.LivingEntity;

import java.util.Arrays;
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
                    double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    double result = nums[0];

                    for(int i = 1; i < nums.length; i++) result += nums[i];
                    DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, DFType.NUM),localStorage);
                    break;
                }

                case "-": {
                    double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    double result = nums[0];

                    for(int i = 1; i < nums.length; i++) result -= nums[i];
                    DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, DFType.NUM),localStorage);
                    break;
                }

                case "x": {
                    double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    double result = nums[0];

                    for(int i = 1; i < nums.length; i++) result *= nums[i];
                    DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, DFType.NUM),localStorage);
                    break;
                }

                case "/": {
                    double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    double result = nums[0];

                    for(int i = 1; i < nums.length; i++) result /= nums[i];
                    DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, DFType.NUM),localStorage);
                    break;
                }

                case "%": {
                    DFValue val = new DFValue((double) args.get("dividend").getVal() % (double) args.get("divisor").getVal(), DFType.NUM);
                    DFVar.setVar((DFVar) args.get("var").getVal(), val, localStorage);
                    break;
                }

                case "+=": {
                    DFVar var = (DFVar) args.get("var").getVal();

                    double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    double num = (double) DFVar.getVar(var, localStorage).getVal();

                    for(double increment : nums) num += increment;
                    DFVar.setVar(var, new DFValue(num, DFType.NUM), localStorage);
                    break;
                }

                case "-=": {
                    DFVar var = (DFVar) args.get("var").getVal();

                    double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    double num = (double) DFVar.getVar(var, localStorage).getVal();

                    for(double decrement : nums) num -= decrement;
                    DFVar.setVar(var, new DFValue(num, DFType.NUM), localStorage);
                    break;
                }

                case "Exponent": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    DFValue val = new DFValue(Math.pow((Float) args.get("num").getVal(), (Float) args.get("exponent").getVal()), DFType.NUM);

                    DFVar.setVar(var, val, localStorage);
                    break;
                }

                case "Root": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    DFValue val = new DFValue(Math.pow((Float) args.get("num").getVal(), 1/((Float) args.get("rootIndex").getVal())), DFType.NUM);

                    DFVar.setVar(var, val, localStorage);
                    break;
                }

                case "Logarithm": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    Float num = (Float) args.get("num").getVal();
                    Float base = (Float) args.get("base").getVal();

                    DFValue val = new DFValue(Math.log(num) / Math.log(base), DFType.NUM);

                    DFVar.setVar(var, val, localStorage);
                    break;
                }

                case "ParseNumber": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    String txt = (String) (args.get("txt").getVal() == null ? DFVar.getVar(var, localStorage).getVal() : args.get("txt").getVal());

                    DFVar.setVar(var, new DFValue(Float.parseFloat(txt), DFType.NUM), localStorage);
                    break;
                }

                case "AbsoluteValue": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    Float num = (Float) (args.get("num").getVal() == null ? DFVar.getVar(var, localStorage).getVal() : args.get("num").getVal());

                    DFVar.setVar(var, new DFValue(Math.abs(num), DFType.NUM), localStorage);
                    break;
                }

                case "ClampNumber": {
                    double numToClamp;
                    DFVar var = (DFVar) args.get("var").getVal();
                    numToClamp = args.containsKey("clampNum") ? (double) args.get("clampNum").getVal() : (double) DFVar.getVar(var, localStorage).getVal();

                    DFValue val = new DFValue(DFUtilities.clampNum(numToClamp, (double) args.get("min").getVal(), (double) args.get("max").getVal()), DFType.NUM);
                    DFVar.setVar(var, val, localStorage);
                    break;
                }

                case "WrapNumber": {
                    double numToWrap;
                    DFVar var = (DFVar) args.get("var").getVal();
                    numToWrap = args.containsKey("wrapNum") ? (double) args.get("wrapNum").getVal() : (double) DFVar.getVar(var, localStorage).getVal();

                    DFValue val = new DFValue(DFUtilities.wrapNum(numToWrap, (double) args.get("min").getVal(), (double) args.get("max").getVal()), DFType.NUM);
                    DFVar.setVar(var, val, localStorage);
                    break;
                }

                case "Average": {
                    double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
                    double sum = Arrays.stream(nums).sum();

                    DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(sum/nums.length, DFType.NUM), localStorage);
                    break;
                }

                case "RandomNumber": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    double min = (double) args.get("min").getVal();
                    double max = (double) args.get("max").getVal();

                    double value;
                    if(tags.get("Rounding Mode") == "Whole number") value = Math.floor(Math.random()*(max-min+1)+min);
                    else value = Math.random()*(max-min+1)+min;

                    DFVar.setVar(var, new DFValue(value, DFType.NUM), localStorage);
                    break;
                }

                case "Round": {
                    DFVar var = (DFVar) args.get("var").getVal();
                    double numToRound = (double) (args.get("roundNum").getVal() == null ? args.get("roundNum").getVal() : DFVar.getVar(var, localStorage).getVal());
                    double value = numToRound;

                    switch(tags.get("Round Mode")){
                        case "Floor":
                            value = Math.floor(numToRound); break;
                        case "Nearest":
                            value = Math.round(numToRound); break;
                        case "Ceiling":
                            value = Math.ceil(numToRound); break;
                    }

                    DFVar.setVar(var, new DFValue(value, DFType.NUM), localStorage);
                }
            }
    }
}
