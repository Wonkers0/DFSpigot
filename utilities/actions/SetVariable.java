package me.wonk2.utilities.actions;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
					
					purgeKeys(varNames, DFVar.globalVars, matchReq, ignoreCase);
					purgeKeys(varNames, localStorage, matchReq, ignoreCase);
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
					DFValue val = new DFValue(Math.pow((float) (double) args.get("num").getVal(), (float) (double) args.get("exponent").getVal()), DFType.NUM);
					
					DFVar.setVar(var, val, localStorage);
					break;
				}
				
				case "Root": {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue val = new DFValue(Math.pow((float) (double) args.get("num").getVal(), 1/((float) (double) args.get("rootIndex").getVal())), DFType.NUM);
					
					DFVar.setVar(var, val, localStorage);
					break;
				}
				
				case "Logarithm": {
					DFVar var = (DFVar) args.get("var").getVal();
					Float num = (float) (double) args.get("num").getVal();
					Float base = (float) (double) args.get("base").getVal();
					
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
					Float num = (float) (double) (args.get("num").getVal() == null ? DFVar.getVar(var, localStorage).getVal() : args.get("num").getVal());
					
					DFVar.setVar(var, new DFValue(Math.abs(num), DFType.NUM), localStorage);
					break;
				}
				
				case "ClampNumber": {
					double numToClamp;
					DFVar var = (DFVar) args.get("var").getVal();
					numToClamp = args.get("clampNum") != null ? (double) args.get("clampNum").getVal() : (double) DFVar.getVar(var, localStorage).getVal();
					
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
					break;
				}
				
				case "Text": {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] parseVals;
					
					if(args.get("parseVals").getVal() == null){
						DFValue varVal = DFVar.getVar(var, localStorage);
						parseVals = varVal.getVal().getClass().isArray() ? (DFValue[]) varVal.getVal() : new DFValue[]{varVal};
					}
					else parseVals = (DFValue[]) args.get("parseVals").getVal();
					
					
					String delimiter = tags.get("Text Value Merging").equals("No spaces") ? "" : " ";
					DFValue val = new DFValue(String.join(delimiter, DFUtilities.parseTxt(parseVals)), DFType.TXT);
					
					DFVar.setVar(var, val, localStorage);
					break;
				}
				
				case "ReplaceText": {
					DFVar var = (DFVar) args.get("var").getVal();
					String txt = (String) args.get("txt").getVal();
					String replaceable = (String) args.get("replaceable").getVal();
					String replacement = (String) args.get("replacement").getVal();
					
					String replaced = tags.get("Replacement Type") == "First occurrence" ? txt.replaceFirst(replaceable, replacement) : txt.replaceAll(replaceable, replacement);
					DFVar.setVar(var, new DFValue(replaced, DFType.TXT), localStorage);
					break;
				}
				
				case "RemoveText": {
					DFVar var = (DFVar) args.get("var").getVal();
					String txt = (String) (args.get("txt").getVal() != null ? args.get("txt").getVal() : DFVar.getVar(var, localStorage).getVal());
					String[] removables = DFValue.castTxt((DFValue[]) args.get("removables").getVal());
					
					for(String remove : removables) txt = txt.replaceAll(remove, "");
					DFVar.setVar(var, new DFValue(txt, DFType.TXT), localStorage);
					break;
				}
				
				case "TrimText": {
					DFVar var = (DFVar) args.get("var").getVal();
					String trimTxt = (String) (args.get("txt") != null ? args.get("txt").getVal() : DFVar.getVar(var, localStorage).getVal());
					int endIndex = args.get("endIndex") != null ? args.get("endIndex").getInt() : trimTxt.length();
					
					DFValue val = new DFValue(trimTxt.substring(args.get("beginIndex").getInt(), endIndex), DFType.TXT);
					DFVar.setVar(var, val, localStorage);
					break;
				}
				
				case "SplitText": {
					DFVar var = (DFVar) args.get("var").getVal();
					String splitTxt = (String) args.get("splitTxt").getVal();
					String splitter = (String) args.get("splitter").getVal();
					
					String[] result = splitTxt.split(splitter);
					for(int i = 0; i < result.length; i++) result[i] = result[i].replaceAll("^ | $", "");
					// ↑ Remove leading and trailing spaces, this is apparently a feature when splitting text in DF ↑
					
					DFVar.setVar(var, new DFValue(result, DFType.LIST), localStorage);
					break;
				}
				
				case "JoinText": {
					// TODO: Implement this action (both the logic and parameter information). I am delaying this for now
					// TODO: because it requires "CreateList" support.
					break;
				}
				
				case "SetCase": {
					DFVar var = (DFVar) args.get("var").getVal();
					String text = (String) (args.get("txt").getVal() == null ? DFVar.getVar(var, localStorage).getVal() : args.get("txt").getVal());
					
					switch(tags.get("Capitalization Type")){
						case "UPPERCASE":
							text = text.toUpperCase(); break;
						case "lowercase":
							text = text.toLowerCase(); break;
						case "Proper Case":
							StringUtils.capitalize(text); break;
						case "iNVERT CASE":
							StringUtils.swapCase(text); break;
						case "RAnDoM cASe":
							text = randomCase(text);
					}
					
					DFVar.setVar(var, new DFValue(text, DFType.TXT), localStorage);
					break;
				}
				
				case "TranslateColors": {
					DFVar var = (DFVar) args.get("var").getVal();
					String text = (String) (args.get("txt").getVal() == null ? DFVar.getVar(var, localStorage).getVal() : args.get("txt").getVal());
					
					switch(tags.get("Translation Type")){
						case "From hex to color": {
							Pattern p = Pattern.compile("#[a-fA-F0-9]{6}");
							Matcher matcher = p.matcher("#ffffff");
							while(matcher.find()) text = text.replace(matcher.group(), hexToCode(matcher.group()));
						}
						case "From & to color": {
							text = ChatColor.translateAlternateColorCodes('&', text);
						}
						case "From color to &": {
							Matcher matcher = Pattern.compile("(?i)" + ChatColor.COLOR_CHAR + "[0-9A-FK-ORX]").matcher(text);
							while(matcher.find()) text = text.replace(matcher.group(), "&" + matcher.group().replace(ChatColor.COLOR_CHAR, Character.MIN_VALUE));
						}
						case "Strip color": {
							text = ChatColor.stripColor(text);
						}
					}
					
					DFVar.setVar(var, new DFValue(text, DFType.TXT), localStorage);
					break;
				}
				
				case "TextLength": {
					DFVar var = (DFVar) args.get("var").getVal();
					String text = (String) args.get("txt").getVal();
					
					DFVar.setVar(var, new DFValue((double) text.length(), DFType.NUM), localStorage);
					break;
				}
				
				case "RepeatText": {
					DFVar var = (DFVar) args.get("var").getVal();
					String text = (String) args.get("txt").getVal();
					int amount = args.get("amount").getInt();
					
					StringBuilder result = new StringBuilder();
					for(int i = 0; i < amount; i++) result.append(text);
					DFVar.setVar(var, new DFValue(result.toString(), DFType.TXT), localStorage);
					break;
				}
				
				case "FormatTime": {
					DFVar var = (DFVar) args.get("var").getVal();
					double seconds = (double) args.get("time").getVal();
					String customFormat = (String) args.get("format").getVal();
					
					HashMap<String, String> timeFormats = new HashMap<>(){{
						put("Custom", customFormat);
						put("2020/08/17 17:20:54", "yyyy/MM/dd HH:mm:s");
						put("2020/08/17", "yyyy/MM/dd");
						put("Mon, August 17", "E, MMMM d");
						put("Monday", "EEEE");
						put("17:20:54", "HH:mm:s");
						put("5:20 PM", "HH:mma");
						put("17h20m54s", "H'h'M'm's's'");
						put("54.229 seconds", "s.S seconds");
					}};
					
					DFValue val = new DFValue(DFUtilities.formatTime(timeFormats.get(tags.get("Format")), seconds), DFType.TXT);
					DFVar.setVar(var, val, localStorage);
					break;
				}
				
				case "CreateList": {
					DFVar var = (DFVar) args.get("var").getVal();
					DFVar.setVar(var, new DFValue(args.get("list").getVal(), DFType.LIST), localStorage);
					break;
				}
				
				case "AppendValue": {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] values = (DFValue[]) args.get("values").getVal();
					ArrayList<DFValue> currentVals = (ArrayList<DFValue>) Arrays.asList((DFValue[]) DFVar.getVar(var, localStorage).getVal());
					
					currentVals.addAll(Arrays.asList(values));
					DFVar.setVar(var, new DFValue(currentVals.toArray(), DFType.LIST), localStorage);
					break;
				}
				
				case "AppendList": {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] lists = (DFValue[]) args.get("lists").getVal();
					ArrayList<DFValue> currentVals = (ArrayList<DFValue>) Arrays.asList((DFValue[]) DFVar.getVar(var, localStorage).getVal());
					
					for(DFValue listWrapper : lists){
						DFValue[] list = (DFValue[]) listWrapper.getVal();
						currentVals.addAll(Arrays.asList(list));
					}
					
					DFVar.setVar(var, new DFValue(currentVals.toArray(), DFType.LIST), localStorage);
					break;
				}
				
				case "GetListValue": {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] list = (DFValue[]) args.get("list").getVal();
					int index = args.get("index").getInt() - 1;
					DFValue val = index >= list.length ? DFValue.nullVar() : list[index];
					
					DFVar.setVar(var, val, localStorage);
					break;
				}
				
				case "SetListValue": {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] currentVals = (DFValue[]) DFVar.getVar(var, localStorage).getVal();
					int index = (int) DFUtilities.clampNum(args.get("index").getInt() - 1, 0, currentVals.length - 1);
					
					currentVals[index] = args.get("value");
					DFVar.setVar(var, new DFValue(currentVals, DFType.LIST), localStorage);
					break;
				}
				
				case "GetValueIndex": {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] list = (DFValue[]) args.get("list").getVal();
					DFValue val = args.get("value");
					
					DFVar.setVar(var, new DFValue(Arrays.asList(list).indexOf(val), DFType.NUM), localStorage);
					break;
				}
				
				case "ListLength": {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] list = (DFValue[]) args.get("list").getVal();
					
					DFVar.setVar(var, new DFValue(list.length, DFType.NUM), localStorage);
					break;
				}
			}
	}
	
	private static String randomCase(String text){
		char[] result = new char[text.length()];
		for(int i = 0; i < result.length; i++) result[i] = Math.random() <= 0.5d ? Character.toUpperCase(result[i]) : result[i];
		return String.valueOf(result);
	}
	
	private static String hexToCode(String hex){
		StringBuilder builder = new StringBuilder();
		for(String c : hex.toLowerCase().replace("#", "x").split(""))
			builder.append("&").append(c);
		
		return builder.toString();
	}
	
	public static HashMap<String, DFValue> purgeKeys(String[] varNames, HashMap<String, DFValue> storage, String matchReq, boolean ignoreCase){
		String[] storageKeys = storage.keySet().toArray(new String[0]);
		String[] matchedKeys = new String[0];
		
		for(String name : varNames)
			switch(matchReq) {
				case "Entire name":
					if (!ignoreCase)
						matchedKeys = (String[]) Arrays.stream(storageKeys).filter(val -> val.equalsIgnoreCase(name)).toArray();
					else matchedKeys = (String[]) Arrays.stream(storageKeys).filter(val -> val.equals(name)).toArray();
					break;
				case "Full word(s) in name":
					String regex = DFUtilities.escapeRegex(name) + "($| )";
					Pattern pattern = !ignoreCase ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
					
					matchedKeys = (String[]) Arrays.stream(storageKeys).filter(val -> pattern.matcher(val).find()).toArray();
					break;
				case "Any part of name":
					if(!ignoreCase) matchedKeys = (String[]) Arrays.stream(storageKeys).filter(val -> val.contains(name)).toArray();
					else matchedKeys = (String[]) Arrays.stream(storageKeys).filter(val -> val.toLowerCase().contains(name.toLowerCase())).toArray();
					break;
			}
		
		for(String key : matchedKeys) storage.remove(key);
		/*TODO: Purge global & saved vars*/
		
		return storage;
	}
}