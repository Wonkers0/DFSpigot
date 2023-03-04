package me.wonk2.utilities.actions;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.enums.SelectionType;
import me.wonk2.utilities.internals.Noise;
import me.wonk2.utilities.values.DFSound;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
public class SetVariable extends Action {
	
	public SetVariable(String targetName, HashMap<String, Entity[]> targetMap, ParamManager paramManager, String action, HashMap<String, DFValue> localStorage) {
		super(targetName, targetMap, paramManager, action, localStorage);
	}
	
	@Override
	public void invokeAction(){
		Entity[] selection = targetMap.get("selection");
		for(Entity ignored : DFUtilities.getTargets(targetName, targetMap, SelectionType.EITHER)) {
			if(targetName.equals("selection") && selection != null) targetMap.put("selection", new Entity[]{ignored});
			
			Object[] inputArray = paramManager.formatParameters(targetMap);
			HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
			HashMap<String, String> tags = DFUtilities.getTags(inputArray);
			HashMap<Integer, DFValue> primitiveInput = DFUtilities.getPrimitiveInput(inputArray);
			switch (action) {
				case "=" -> DFVar.setVar((DFVar) args.get("var").getVal(), args.get("value"), localStorage);
				
				case "RandomValue" -> {
					DFValue[] values = (DFValue[]) args.get("values").getVal();
					DFValue value = values[new Random().nextInt(values.length)];
					DFVar.setVar((DFVar) args.get("var").getVal(), value, localStorage);
				}
				
				case "PurgeVars" -> {
					String[] varNames = DFValue.castTxt((DFValue[]) args.get("varNames").getVal());
					String matchReq = tags.get("Match Requirement");
					boolean ignoreCase = tags.get("Ignore Case").equals("True");
					
					purgeKeys(varNames, DFVar.globalVars, matchReq, ignoreCase);
					purgeKeys(varNames, localStorage, matchReq, ignoreCase);
					purgeKeys(varNames, DFVar.savedVars, matchReq, ignoreCase);
				}
				
				case "+" -> {
					double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
					double result = nums[0];
					
					for (int i = 1; i < nums.length; i++) result += nums[i];
					DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, DFType.NUM), localStorage);
				}

				case "-" -> {
					double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
					double result = nums[0];
					
					for (int i = 1; i < nums.length; i++) result -= nums[i];
					DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, DFType.NUM), localStorage);
				}

				case "x" -> {
					double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
					double result = nums[0];
					
					for (int i = 1; i < nums.length; i++) result *= nums[i];
					DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, DFType.NUM), localStorage);
				}

				case "/" -> {
					double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
					double result = nums[0];
					
					for (int i = 1; i < nums.length; i++) result /= nums[i];
					DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(result, DFType.NUM), localStorage);
				}

				case "%" -> {
					DFValue val = new DFValue((double) args.get("dividend").getVal() % (double) args.get("divisor").getVal(), DFType.NUM);
					DFVar.setVar((DFVar) args.get("var").getVal(), val, localStorage);
				}

				case "+=" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					
					double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
					double num = (double) DFVar.getVar(var, localStorage).getVal();
					
					for (double increment : nums) num += increment;
					DFVar.setVar(var, new DFValue(num, DFType.NUM), localStorage);
				}

				case "-=" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					
					double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
					double num = (double) DFVar.getVar(var, localStorage).getVal();
					
					for (double decrement : nums) num -= decrement;
					DFVar.setVar(var, new DFValue(num, DFType.NUM), localStorage);
				}

				case "Exponent" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue val = new DFValue(Math.pow((float) (double) args.get("num").getVal(), (float) (double) args.get("exponent").getVal()), DFType.NUM);
					
					DFVar.setVar(var, val, localStorage);
				}

				case "Root" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue val = new DFValue(Math.pow((float) (double) args.get("num").getVal(), 1 / ((float) (double) args.get("rootIndex").getVal())), DFType.NUM);
					
					DFVar.setVar(var, val, localStorage);
				}

				case "Logarithm" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					float num = (float) (double) args.get("num").getVal();
					float base = (float) (double) args.get("base").getVal();
					
					DFValue val = new DFValue(Math.log(num) / Math.log(base), DFType.NUM);
					
					DFVar.setVar(var, val, localStorage);
				}

				case "ParseNumber" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					String txt = (String) (args.get("txt").getVal() == null ? DFVar.getVar(var, localStorage).getVal() : args.get("txt").getVal());
					
					DFVar.setVar(var, new DFValue(Float.parseFloat(txt), DFType.NUM), localStorage);
				}

				case "AbsoluteValue" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					float num = (float) (double) (args.get("num").getVal() == null ? DFVar.getVar(var, localStorage).getVal() : args.get("num").getVal());
					
					DFVar.setVar(var, new DFValue(Math.abs(num), DFType.NUM), localStorage);
				}

				case "ClampNumber" -> {
					double numToClamp;
					DFVar var = (DFVar) args.get("var").getVal();
					numToClamp = args.get("clampNum") != null ? (double) args.get("clampNum").getVal() : (double) DFVar.getVar(var, localStorage).getVal();

					DFValue val = new DFValue(DFUtilities.clampNum(numToClamp, (double) args.get("min").getVal(), (double) args.get("max").getVal()), DFType.NUM);
					DFVar.setVar(var, val, localStorage);
				}

				case "WrapNumber" -> {
					double numToWrap;
					DFVar var = (DFVar) args.get("var").getVal();
					numToWrap = args.containsKey("wrapNum") ? (double) args.get("wrapNum").getVal() : (double) DFVar.getVar(var, localStorage).getVal();
					
					DFValue val = new DFValue(DFUtilities.wrapNum(numToWrap, (double) args.get("min").getVal(), (double) args.get("max").getVal()), DFType.NUM);
					DFVar.setVar(var, val, localStorage);
				}

				case "Average" -> {
					double[] nums = DFValue.castNum((DFValue[]) args.get("nums").getVal());
					double sum = Arrays.stream(nums).sum();
					
					DFVar.setVar((DFVar) args.get("var").getVal(), new DFValue(sum / nums.length, DFType.NUM), localStorage);
				}

				case "RandomNumber" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					double min = (double) args.get("min").getVal();
					double max = (double) args.get("max").getVal();
					
					double value;
					if (tags.get("Rounding Mode").equals("Whole number")) value = Math.floor(Math.random() * (max - min + 1) + min);
					else value = Math.random() * (max - min + 1) + min;
					
					DFVar.setVar(var, new DFValue(value, DFType.NUM), localStorage);
				}
	
				case "RoundNumber" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					double numToRound = (double) (args.get("roundNum").getVal() == null ? args.get("roundNum").getVal() : DFVar.getVar(var, localStorage).getVal());
					double value = switch (tags.get("Round Mode")) {
						case "Floor" -> Math.floor(numToRound);
						case "Nearest" -> Math.round(numToRound);
						case "Ceiling" -> Math.ceil(numToRound);
						default -> numToRound;
					};
					
					DFVar.setVar(var, new DFValue(value, DFType.NUM), localStorage);
				}
				
				case "Sine" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					double angle = (double) args.get("angle").getVal();
					
					String inputMethod = tags.get("Input");
					
					double val;
					val = switch(tags.get("Cosine Variant")){
						case "Sine" -> inputMethod.equals("Radians") ? Math.cos(angle) : Math.cos(Math.toRadians(angle));
						case "Inverse sine (arcsine)" -> inputMethod.equals("Radians") ? Math.acos(angle) : Math.toDegrees(Math.acos(angle));
						case "Hyperbolic sine" -> inputMethod.equals("Radians") ? Math.cosh(angle) : Math.toDegrees(Math.cosh(angle));
						default -> 0;
					};
					
					DFVar.setVar(var, new DFValue(val, DFType.NUM), localStorage);
				}
				
				case "Cosine" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					double angle = (double) args.get("angle").getVal();
					
					String inputMethod = tags.get("Input");
					
					double val;
					val = switch(tags.get("Cosine Variant")){
						case "Cosine" -> inputMethod.equals("Radians") ? Math.cos(angle) : Math.cos(Math.toRadians(angle));
						case "Inverse cosine (arccosine)" -> inputMethod.equals("Radians") ? Math.acos(angle) : Math.toDegrees(Math.acos(angle));
						case "Hyperbolic cosine" -> inputMethod.equals("Radians") ? Math.cosh(angle) : Math.toDegrees(Math.cosh(angle));
						default -> 0;
					};
					
					DFVar.setVar(var, new DFValue(val, DFType.NUM), localStorage);
				}
				
				case "Tangent" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					double angle = (double) args.get("angle").getVal();
					
					String inputMethod = tags.get("Input");
					
					double val;
					val = switch(tags.get("Cosine Variant")){
						case "Tangent" -> inputMethod.equals("Radians") ? Math.cos(angle) : Math.cos(Math.toRadians(angle));
						case "Inverse tangent (arctangent)" -> inputMethod.equals("Radians") ? Math.acos(angle) : Math.toDegrees(Math.acos(angle));
						case "Hyperbolic tangent" -> inputMethod.equals("Radians") ? Math.cosh(angle) : Math.toDegrees(Math.cosh(angle));
						default -> 0;
					};
					
					DFVar.setVar(var, new DFValue(val, DFType.NUM), localStorage);
				}

				case "Text" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] parseVals;
					
					if (args.get("parseVals").getVal() == null) {
						DFValue varVal = DFVar.getVar(var, localStorage);
						parseVals = varVal.getVal().getClass().isArray() ? (DFValue[]) varVal.getVal() : new DFValue[]{varVal};
					} else parseVals = (DFValue[]) args.get("parseVals").getVal();
					
					
					String delimiter = tags.get("Text Value Merging").equals("No spaces") ? "" : " ";
					DFValue val = new DFValue(String.join(delimiter, DFUtilities.parseTxt(parseVals)), DFType.TXT);
					
					DFVar.setVar(var, val, localStorage);
				}

				case "ReplaceText" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					String txt = (String) args.get("txt").getVal();
					String replaceable = (String) args.get("replaceable").getVal();
					String replacement = (String) args.get("replacement").getVal();
					
					String replaced = tags.get("Replacement Type").equals("First occurrence") ? txt.replaceFirst(replaceable, replacement) : txt.replaceAll(replaceable, replacement);
					DFVar.setVar(var, new DFValue(replaced, DFType.TXT), localStorage);
				}

				case "RemoveText" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					String txt = (String) (args.get("txt").getVal() != null ? args.get("txt").getVal() : DFVar.getVar(var, localStorage).getVal());
					String[] removables = DFValue.castTxt((DFValue[]) args.get("removables").getVal());
					
					for (String remove : removables) txt = txt.replaceAll(remove, "");
					DFVar.setVar(var, new DFValue(txt, DFType.TXT), localStorage);
				}

				case "TrimText" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					String trimTxt = (String) (args.get("txt") != null ? args.get("txt").getVal() : DFVar.getVar(var, localStorage).getVal());
					int endIndex = args.get("endIndex") != null ? args.get("endIndex").getInt() : trimTxt.length();
					
					DFValue val = new DFValue(trimTxt.substring(args.get("beginIndex").getInt(), endIndex), DFType.TXT);
					DFVar.setVar(var, val, localStorage);
				}

				case "SplitText" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					String splitTxt = (String) args.get("splitTxt").getVal();
					String splitter = (String) args.get("splitter").getVal();
					
					String[] result = splitTxt.split(splitter);
					for (int i = 0; i < result.length; i++) result[i] = result[i].replaceAll("^ | $", "");
					// ↑ Remove leading and trailing spaces, this is apparently a feature when splitting text in DF ↑
					
					DFVar.setVar(var, DFValue.castArray(result, DFType.TXT), localStorage);
				}

				case "JoinText" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					String[] texts = DFValue.castTxt((DFValue[]) args.get("texts").getVal());
					String joiningText = (String) args.get("joiningTxt").getVal();
					String finalJoiningText = (String) args.get("finalJoiningTxt").getVal();
					
					StringBuilder result = new StringBuilder();
					for(int i = 0; i < texts.length - 1; i++){
						result.append(texts[i]);
						result.append(i == texts.length - 2 ? finalJoiningText : joiningText);
					}
					result.append(texts[texts.length - 1]);
					
					DFVar.setVar(var, new DFValue(result.toString(), DFType.TXT), localStorage);
				}

				case "SetCase" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					String text = (String) (args.get("txt").getVal() == null ? DFVar.getVar(var, localStorage).getVal() : args.get("txt").getVal());
					
					switch (tags.get("Capitalization Type")) {
						case "UPPERCASE" -> text = text.toUpperCase();
						case "lowercase" -> text = text.toLowerCase();
						case "Proper Case" -> StringUtils.capitalize(text);
						case "iNVERT CASE" -> StringUtils.swapCase(text);
						case "RAnDoM cASe" -> text = randomCase(text);
					}
					
					DFVar.setVar(var, new DFValue(text, DFType.TXT), localStorage);
				}

				case "TranslateColors" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					String text = (String) (args.get("txt").getVal() == null ? DFVar.getVar(var, localStorage).getVal() : args.get("txt").getVal());
					
					switch (tags.get("Translation Type")) {
						case "From hex to color" -> {
							Pattern p = Pattern.compile("#[a-fA-F0-9]{6}");
							Matcher matcher = p.matcher("#ffffff");
							while (matcher.find()) text = text.replace(matcher.group(), hexToCode(matcher.group()));
						}
						
						case "From & to color" -> text = ChatColor.translateAlternateColorCodes('&', text);
						
						case "From color to &" -> {
							Matcher matcher = Pattern.compile("(?i)" + ChatColor.COLOR_CHAR + "[0-9A-FK-ORX]").matcher(text);
							while (matcher.find())
								text = text.replace(matcher.group(), "&" + matcher.group().replace(ChatColor.COLOR_CHAR, Character.MIN_VALUE));
						}
						
						case "Strip color" -> text = ChatColor.stripColor(text);
					}
					
					DFVar.setVar(var, new DFValue(text, DFType.TXT), localStorage);
				}

				case "TextLength" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					String text = (String) args.get("txt").getVal();
					
					DFVar.setVar(var, new DFValue((double) text.length(), DFType.NUM), localStorage);
				}
				
				case "RepeatText" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					String text = (String) args.get("txt").getVal();
					int amount = args.get("amount").getInt();
					
					DFVar.setVar(var, new DFValue(text.repeat(Math.max(0, amount)), DFType.TXT), localStorage);
				}
				
				case "FormatTime" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					double seconds = (double) args.get("time").getVal();
					String customFormat = (String) args.get("format").getVal();
					
					HashMap<String, String> timeFormats = new HashMap<>() {{
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
				}
				
				case "CreateList" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] values = (DFValue[]) args.get("list").getVal();
					
					DFVar.setVar(var, new DFValue(values == null ? new DFValue[0] : values, DFType.LIST), localStorage);
				}
				
				case "AppendValue" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] values = (DFValue[]) args.get("values").getVal();
					
					DFValue[] temp = DFVar.varExists(var, localStorage) ? (DFValue[]) DFVar.getVar(var, localStorage).getVal() : new DFValue[0];
					
					ArrayList<DFValue> currentVals = new ArrayList<>(Arrays.asList(temp));
					currentVals.addAll(Arrays.asList(values));
					
					DFVar.setVar(var, new DFValue(currentVals.toArray(DFValue[]::new), DFType.LIST), localStorage);
				}
				
				case "AppendList" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] lists = (DFValue[]) args.get("lists").getVal();
					
					DFValue[] temp = DFVar.varExists(var, localStorage) ? (DFValue[]) DFVar.getVar(var, localStorage).getVal() : new DFValue[0];
					
					ArrayList<DFValue> currentVals = (ArrayList<DFValue>) Arrays.asList(temp);
					
					for (DFValue listWrapper : lists) {
						DFValue[] list = (DFValue[]) listWrapper.getVal();
						currentVals.addAll(Arrays.asList(list));
					}
					
					DFVar.setVar(var, new DFValue(currentVals.toArray(DFValue[]::new), DFType.LIST), localStorage);
				}
				
				case "GetListValue" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] list = (DFValue[]) args.get("list").getVal();
					
					int index = args.get("index").getInt() - 1;
					
					boolean outsideBounds = index >= list.length || index < 0;
					DFValue val = outsideBounds ? DFValue.nullVar() : list[index];
					
					DFVar.setVar(var, val, localStorage);
				}
				
				case "SetListValue" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] currentVals = (DFValue[]) DFVar.getVar(var, localStorage).getVal();
					int index = (int) DFUtilities.clampNum(args.get("index").getInt() - 1, 0, currentVals.length - 1);
					
					currentVals[index] = args.get("value");
					DFVar.setVar(var, new DFValue(currentVals, DFType.LIST), localStorage);
				}
				
				case "GetValueIndex" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] list = (DFValue[]) args.get("list").getVal();
					Object val = args.get("value").getVal();
					
					int index = 0;
					for(int i = 0; i < list.length; i++)
						if(list[i].getVal().equals(val)){
							index = i + 1;
							break;
						}
					
					//Bukkit.broadcastMessage(DFUtilities.parseTxt(new DFValue(list, DFType.LIST)));
					//Bukkit.broadcastMessage(DFUtilities.parseTxt(args.get("value")));
					
					DFVar.setVar(var, new DFValue(index, DFType.NUM), localStorage);
				}
				
				case "ListLength" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] list = (DFValue[]) args.get("list").getVal();
					
					DFVar.setVar(var, new DFValue(list.length, DFType.NUM), localStorage);
				}
				
				case "InsertListValue" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					int index = args.get("index").getInt();
					DFValue value = args.get("value");
					
					List<DFValue> list = new ArrayList<>(List.of(((DFValue[]) DFVar.getVar(var, localStorage).getVal())));
					list.add(index, value);
					
					DFVar.setVar(var, new DFValue(list.toArray(DFValue[]::new), DFType.LIST), localStorage);
				}
				
				case "RemoveListValue" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] values = (DFValue[]) args.get("values").getVal();
					
					List<DFValue> list = new ArrayList<>(List.of(((DFValue[]) DFVar.getVar(var, localStorage).getVal())));
					for(DFValue value : values) list.remove(value);
					
					DFVar.setVar(var, new DFValue(list.toArray(DFValue[]::new), DFType.LIST), localStorage);
				}
				
				case "RemoveListIndex" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					double[] indices = DFValue.castNum((DFValue[]) args.get("indices").getVal());
					
					List<DFValue> list = new ArrayList<>(List.of(((DFValue[]) DFVar.getVar(var, localStorage).getVal())));
					for(double index : indices) list.remove((int) index);
					
					DFVar.setVar(var, new DFValue(list.toArray(DFValue[]::new), DFType.LIST), localStorage);
				}
				
				case "Trimlist" -> {/*TODO*/}
				
				case "SortList" -> {/*TODO*/}
				
				case "ReverseList" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] list = (DFValue[]) args.get("list").getVal();
					if(list == null) list = (DFValue[]) DFVar.getVar(var, localStorage).getVal();
					ArrayList<DFValue> result = new ArrayList<>(Arrays.asList(list));
					Collections.reverse(result);
					
					DFVar.setVar(var, new DFValue(result.toArray(DFValue[]::new), DFType.LIST), localStorage);
				}
				
				case "RandomizeList" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] list = (DFValue[]) args.get("list").getVal();
					if(list == null) list = (DFValue[]) DFVar.getVar(var, localStorage).getVal();
					ArrayList<DFValue> result = new ArrayList<>(Arrays.asList(list));
					Collections.shuffle(result);
					
					DFVar.setVar(var, new DFValue(result.toArray(DFValue[]::new), DFType.LIST), localStorage);
				}
				
				case "GetItemTag" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					String tagName = (String) args.get("tag").getVal();
					NamespacedKey key = new NamespacedKey("hypercube", tagName);
					
					ItemMeta itemMeta = item.getItemMeta();
					assert itemMeta != null;
					PersistentDataContainer dataHolder = itemMeta.getPersistentDataContainer();
					
					DFValue value = DFValue.nullVar();
					if (dataHolder.has(key, PersistentDataType.STRING))
						value = new DFValue(dataHolder.get(key, PersistentDataType.STRING), DFType.TXT);
					else if (dataHolder.has(key, PersistentDataType.DOUBLE))
						value = new DFValue(dataHolder.get(key, PersistentDataType.DOUBLE), DFType.NUM);
					
					DFVar.setVar(var, value, localStorage);
				}
				
				case "GetCoord" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = (Location) args.get("loc").getVal();
					if (tags.get("Coordinate Type").equals("Plot coordinate"))
						loc = DFUtilities.subtractLocs(loc, DFPlugin.origin);
					DFValue value = new DFValue(null, DFType.NUM);
					
					switch (tags.get("Coordinate")) {
						case "X" -> value.setVal(loc.getX());
						case "Y" -> value.setVal(loc.getY());
						case "Z" -> value.setVal(loc.getZ());
						case "Yaw" -> value.setVal((double) loc.getYaw());
						case "Pitch" -> value.setVal((double) loc.getPitch());
					}
					DFVar.setVar(var, value, localStorage);
				}
				
				case "SetCoord" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();

					
					double coord = (double) args.get("coord").getVal();
					boolean world = tags.get("Coordinate Type").equals("World coordinate");
					
					DFValue value = new DFValue(setCoord(loc, coord, tags.get("Coordinate"), world), DFType.LOC);
					DFVar.setVar(var, value, localStorage);
				}
				
				case "SetAllCoords" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();

					
					// Slot numbers matter because for some reason that's how it behaves in-game... weird
					Object x = primitiveInput.get(1).getVal();
					Object y = primitiveInput.get(2).getVal();
					Object z = primitiveInput.get(3).getVal();
					Object yaw = primitiveInput.get(4).getVal();
					Object pitch = primitiveInput.get(5).getVal();
					
					boolean world = tags.get("Coordinate Type").equals("World coordinate");
					if (x != null) loc = setCoord(loc, (double) x, "X", world);
					if (y != null) loc = setCoord(loc, (double) y, "Y", world);
					if (z != null) loc = setCoord(loc, (double) z, "Z", world);
					if (yaw != null) loc = setCoord(loc, (double) yaw, "Yaw", world);
					if (pitch != null) loc = setCoord(loc, (double) pitch, "Pitch", world);
					DFVar.setVar(var, new DFValue(loc, DFType.LOC), localStorage);
				}
				
				case "ShiftOnAxis" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();
					double shift = (double) args.get("shift").getVal();
					
					loc = shiftOnAxis(tags.get("Coordinate"), loc, shift);
					DFVar.setVar(var, new DFValue(loc, DFType.LOC), localStorage);
				}
				
				case "ShiftAllAxes" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();
					
					double x = (double) args.get("shiftX").getVal();
					double y = (double) args.get("shiftY").getVal();
					double z = (double) args.get("shiftZ").getVal();
					
					loc = shiftOnAxis("X", loc, x);
					loc = shiftOnAxis("Y", loc, y);
					loc = shiftOnAxis("Z", loc, z);
					DFVar.setVar(var, new DFValue(loc, DFType.LOC), localStorage);
				}
				
				case "ShiftInDirection" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();
					double shift = (double) args.get("shift").getVal();
					
					DFVar.setVar(var, new DFValue(shiftInDirection(tags.get("Direction"), loc, shift), DFType.LOC), localStorage);
				}
				
				case "ShiftAllDirections" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();
					double forwardShift = (double) args.get("fwdShift").getVal();
					double upwardShift = (double) args.get("upwardShift").getVal();
					double sidewaysShift = (double) args.get("sideShift").getVal();
					
					shiftInDirection("Forward", loc, forwardShift);
					shiftInDirection("Upward", loc, upwardShift);
					shiftInDirection("Sideways", loc, sidewaysShift);
					DFVar.setVar(var, new DFValue(loc, DFType.LOC), localStorage);
				}
				
				case "ShiftToward" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();
					Location targetLoc = (Location) args.get("target").getVal();
					double shift = (double) args.get("shift").getVal();
					
					loc.add(loc.clone().subtract(targetLoc).toVector().normalize().multiply(shift));
					DFVar.setVar(var, new DFValue(loc, DFType.LOC), localStorage);
				}
				
				case "ShiftOnVector" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();
					Vector vector = (Vector) args.get("vector").getVal();
					Double shift = (Double) args.get("shift").getVal();
					
					if(shift != null) vector.normalize().multiply(shift);
					DFVar.setVar(var, new DFValue(loc.add(vector), DFType.LOC), localStorage);
				}
				
				case "GetDirection" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = (Location) args.get("loc").getRawVal();
					DFVar.setVar(var, new DFValue(loc.getDirection(), DFType.VEC), localStorage);
				}
				
				case "SetDirection" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();
					Vector dir = (Vector) args.get("direction").getVal();
					
					DFVar.setVar(var, new DFValue(loc.setDirection(dir), DFType.LOC), localStorage);
				}
				
				case "ShiftRotation" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();
					double shift = (double) args.get("shift").getVal();
					String rotAxis = tags.get("Rotation Axis");
					
					if(rotAxis.equals("Pitch")) loc.setPitch(loc.getPitch() + (float) shift);
					else if(rotAxis.equals("Yaw")) loc.setYaw(loc.getYaw() + (float) shift);
					
					DFVar.setVar(var, new DFValue(loc, DFType.LOC), localStorage);
				}
				
				case "FaceLocation" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();
					Location targetLoc = (Location) args.get("target").getVal();
					
					Vector dir = loc.clone().subtract(targetLoc).toVector();
					
					if(tags.get("Face Direction").equals("Toward location")) dir.multiply(-1);
					DFVar.setVar(var, new DFValue(loc.setDirection(dir), DFType.LOC), localStorage);
				}
				
				case "AlignLoc" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = args.get("loc").getVal() == null ? (Location) DFVar.getVar(var, localStorage).getRawVal() : (Location) args.get("loc").getRawVal();
					
					if(tags.get("Rotation").equals("Remove rotation")) loc.setDirection(new Vector(0, 0, 0));
					String alignMode = tags.get("Alignment Mode");
					
					switch(tags.get("Coordinates")){
						case "All coordinates" -> {
							alignCoord(loc, "X", alignMode);
							alignCoord(loc, "Y", alignMode);
							alignCoord(loc, "Z", alignMode);
						}
						
						case "X and Z" -> {
							alignCoord(loc, "X", alignMode);
							alignCoord(loc, "Z", alignMode);
						}
						
						case "Only Y" -> alignCoord(loc, "Y", alignMode);
					}
					
					DFVar.setVar(var, new DFValue(loc, DFType.LOC), localStorage);
				}
				
				case "GetCenterLoc" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location[] locs = (Location[]) args.get("locs").getVal();
					double xSum = 0, ySum = 0, zSum = 0;
					
					
					for(Location loc : locs){
						xSum += loc.getX();
						ySum += loc.getY();
						zSum += loc.getZ();
					}
					
					Location loc = new Location(DFPlugin.world, xSum/locs.length, ySum/locs.length, zSum/locs.length);
					DFVar.setVar(var, new DFValue(loc, DFType.LOC), localStorage);
				}
				
				case "RandomLoc" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc1 = (Location) args.get("loc1").getVal();
					Location loc2 = (Location) args.get("loc2").getVal();
					
					Random r = new Random();
					double x = r.nextDouble() * (loc2.getX() - loc1.getX()) + loc1.getX();
					double y = r.nextDouble() * (loc2.getY() - loc1.getY()) + loc1.getY();
					double z = r.nextDouble() * (loc2.getZ() - loc1.getZ()) + loc1.getZ();
					
					Location loc = new Location(DFPlugin.world, x, y, z);
					DFVar.setVar(var, new DFValue(loc, DFType.LOC), localStorage);
				}
				
				case "GetItemType" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					Material material = item.getType();
					
					switch(tags.get("Return Value Type")){
						case "Item ID (golden_apple)" -> DFVar.setVar(var, new DFValue(material.toString().toLowerCase(), DFType.TXT), localStorage);
						case "Item Name (Golden Apple)" -> DFVar.setVar(var, new DFValue(new ItemStack(material).getItemMeta().getDisplayName(), DFType.TXT), localStorage);
						case "Item" -> DFVar.setVar(var, new DFValue(new ItemStack(material), DFType.ITEM), localStorage);
					}
				}
				
				case "SetItemType" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					String material = (String) args.get("material").getVal();
					
					item.setType(Material.valueOf(material));
					DFVar.setVar(var, new DFValue(item, DFType.ITEM), localStorage);
				}
				
				case "GetItemName" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					
					DFVar.setVar(var, new DFValue(item.getItemMeta().getDisplayName(), DFType.TXT), localStorage);
				}
				
				case "SetItemName" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					String name = (String) args.get("name").getVal();
					
					ItemMeta newItemMeta = item.getItemMeta();
					newItemMeta.setDisplayName(name);
					item.setItemMeta(newItemMeta);
					
					DFVar.setVar(var, new DFValue(item, DFType.ITEM), localStorage);
				}
				
				case "GetItemLore" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					
					String[] lore = item.getItemMeta().getLore().toArray(new String[0]);
					DFVar.setVar(var, new DFValue(DFValue.castArray(lore, DFType.TXT), DFType.LIST), localStorage);
				}
				
				case "SetItemLore" -> { /* TODO */}
				
				case "GetItemAmount" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					
					DFVar.setVar(var, new DFValue(item.getAmount(), DFType.NUM), localStorage);
				}
				
				case "SetItemAmount" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					
					item.setAmount(args.get("amount").getInt());
					DFVar.setVar(var, new DFValue(item, DFType.ITEM), localStorage);
				}
				
				case "GetMaxItemAmount" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					
					DFVar.setVar(var, new DFValue(item.getMaxStackSize(), DFType.NUM), localStorage);
				}
				
				case "GetItemDura" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(!(item instanceof Damageable damageable)) break;
					
					double result = switch(tags.get("Durability Type")) {
						case "Get Damage" -> damageable.getDamage();
						case "Get Damage Percentage" -> (float) damageable.getDamage() / item.getType().getMaxDurability();
						case "Get Remaining" -> item.getType().getMaxDurability() - damageable.getDamage();
						case "Get Remaining Percentage" -> (float) (item.getType().getMaxDurability() - damageable.getDamage()) / item.getType().getMaxDurability();
						case "Get Maximum" -> item.getType().getMaxDurability();
						default -> 0;
					};
					DFVar.setVar(var, new DFValue(result, DFType.NUM), localStorage);
				}
				
				case "SetItemDura" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					double durability = (double) args.get("durability").getVal();
					
					if(!(item instanceof Damageable damageable)) break;
					
					double result = switch(tags.get("Durability Type")) {
						case "Set Damage" -> item.getType().getMaxDurability() - durability;
						case "Set Damage Percentage" -> item.getType().getMaxDurability() - ((durability / 100) * item.getType().getMaxDurability());
						case "Set Remaining" -> durability;
						case "Set Remaining Percentage" -> (durability / 100) * item.getType().getMaxDurability();
						default -> 0;
					};
					
					damageable.setDamage((int) Math.floor(result));
					DFVar.setVar(var, new DFValue(damageable, DFType.ITEM), localStorage);
				}
				
				case "SetBreakability" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					
					ItemMeta itemMeta = item.getItemMeta();
					assert itemMeta != null;
					
					itemMeta.setUnbreakable(tags.get("Breakability").equals("Unbreakable"));
					item.setItemMeta(itemMeta);
					DFVar.setVar(var, new DFValue(item, DFType.ITEM), localStorage);
				}
				
				case "GetItemEnchants" -> {/*TODO*/}
				
				case "SetItemEnchants" -> {/*TODO*/}
				
				case "AddItemEnchant" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					String enchantName = (String) args.get("enchant").getVal();
					int level = args.get("level").getInt();
					
					item.addEnchantment(new EnchantmentWrapper(enchantName), level);
					DFVar.setVar(var, new DFValue(item, DFType.ITEM), localStorage);
				}
				
				case "RemItemEnchant" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					String enchantName = (String) args.get("enchant").getVal();
					
					item.removeEnchantment(new EnchantmentWrapper(enchantName));
					DFVar.setVar(var, new DFValue(item, DFType.ITEM), localStorage);
				}
				
				case "GetHeadOwner" -> {/*TODO*/}
				
				case "SetHeadTexture" -> {/*TODO*/}
				
				case "GetBookText" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack book = (ItemStack) args.get("item").getVal();
					if(book == null) book = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					Object pageNum = args.get("page").getVal();
					
					BookMeta bookMeta = (BookMeta) book.getItemMeta();
					if(pageNum == null){
						String[] pages = bookMeta.getPages().toArray(String[]::new);
						DFVar.setVar(var, DFValue.castArray(pages, DFType.TXT), localStorage);
					}
					else DFVar.setVar(var, new DFValue(bookMeta.getPage((int) pageNum), DFType.TXT), localStorage);
				}
				
				case "SetBookText" -> {/*TODO*/}
				
				case "GetAllItemTags" -> {/*TODO*/}
				
				case "SetItemTag" -> {/*TODO*/}
				
				case "RemoveItemTag" -> {/*TODO*/}
				
				case "SetModelData" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					Integer modelData = args.get("modelData").getInt();
					
					ItemMeta itemMeta = item.getItemMeta();
					assert itemMeta != null;
					itemMeta.setCustomModelData(modelData);
					item.setItemMeta(itemMeta);
					DFVar.setVar(var, new DFValue(item, DFType.ITEM), localStorage);
				}
				
				case "GetItemEffects" -> {/*TODO*/}
				
				case "SetItemEffects" -> {/*TODO*/}
				
				case "SetItemFlags" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					
					ItemMeta itemMeta = item.getItemMeta();
					assert itemMeta != null;
					
					HashMap<String, ItemFlag> flags = new HashMap<>(){{
						put("Hide Color", ItemFlag.HIDE_DYE);
						put("Hide Enchantments", ItemFlag.HIDE_ENCHANTS);
						put("Hide Attributes", ItemFlag.HIDE_ATTRIBUTES);
						put("Hide Unbreakable", ItemFlag.HIDE_UNBREAKABLE);
						put("Hide Can Destroy", ItemFlag.HIDE_DESTROYS);
						put("Hide Can Place On", ItemFlag.HIDE_PLACED_ON);
						put("Hide Potion Effects", ItemFlag.HIDE_POTION_EFFECTS);
					}};
					
					for(String tag : flags.keySet()){
						String tagValue = tags.get(tag);
						if(!tagValue.equals("No Change")){
							if(tagValue.equals("True")) itemMeta.addItemFlags(flags.get(tag));
							else itemMeta.removeItemFlags(flags.get(tag));
						}
					}
					item.setItemMeta(itemMeta);
					DFVar.setVar(var, new DFValue(item, DFType.ITEM), localStorage);
				}
				
				case "SetCanPlaceOn" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					ItemStack[] placeables = DFValue.castItem((DFValue[]) args.get("placeables").getVal());
					// TODO
				}
				
				case "SetCanDestroy" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					ItemStack[] breakables = DFValue.castItem((DFValue[]) args.get("breakables").getVal());
					// TODO
				}
				
				case "GetItemRarity" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					
					// TODO
				}
				
				case "GetLodestoneLoc" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					
					CompassMeta compassMeta = (CompassMeta) item.getItemMeta();
					assert compassMeta != null;
					DFVar.setVar(var, new DFValue(compassMeta.getLodestone(), DFType.LOC), localStorage);
				}
				
				case "SetLodestoneLoc" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) item = (ItemStack) DFVar.getVar(var, localStorage).getVal();
					Location loc = (Location) args.get("loc").getVal();
					
					CompassMeta compassMeta = (CompassMeta) item.getItemMeta();
					assert compassMeta != null;
					
					compassMeta.setLodestone(loc);
					item.setItemMeta(compassMeta);
					DFVar.setVar(var, new DFValue(item, DFType.ITEM), localStorage);
				}
				
				case "SetSoundType" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFSound sound = (DFSound) args.get("sound").getVal();
					if(sound == null) sound = (DFSound) DFVar.getVar(var, localStorage).getVal();
					String soundName = (String) args.get("soundName").getVal();
					
					sound.setSound(soundName);
					DFVar.setVar(var, new DFValue(sound, DFType.SND), localStorage);
				}
				
				case "SetSoundPitch" -> { // TODO: You should also be able to set pitch using a txt value for "Note"
					DFVar var = (DFVar) args.get("var").getVal();
					DFSound sound = (DFSound) args.get("sound").getVal();
					if(sound == null) sound = (DFSound) DFVar.getVar(var, localStorage).getVal();
					
					double pitch = (double) args.get("pitch").getVal();
					
					sound.pitch = (float) pitch;
					DFVar.setVar(var, new DFValue(sound, DFType.SND), localStorage);
				}
				
				case "SetSoundVolume" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFSound sound = (DFSound) args.get("sound").getVal();
					if(sound == null) sound = (DFSound) DFVar.getVar(var, localStorage).getVal();
					double volume = (double) args.get("volume").getVal();
					
					sound.volume = (float) volume;
					DFVar.setVar(var, new DFValue(sound, DFType.SND), localStorage);
				}
				
				case "Vector" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					double x = (double) args.get("x").getVal();
					double y = (double) args.get("y").getVal();
					double z = (double) args.get("z").getVal();
					
					DFVar.setVar(var, new DFValue(new Vector(x, y, z), DFType.VEC), localStorage);
				}
				
				case "Distance" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc1 = (Location) args.get("loc1").getVal();
					Location loc2 = (Location) args.get("loc2").getVal();
					double dist = 0;
					
					switch(tags.get("Distance Type")){
						case "Distance 2D (X/Z)" -> {
							loc2.setY(loc1.getY());
							dist = loc1.distance(loc2);
						}
						case "Distance 3D (X/Y/Z)" -> dist = loc1.distance(loc2);
						
						case "Altitude (Y)" -> {
							loc2.setX(loc1.getX());
							loc2.setZ(loc1.getZ());
							dist = loc1.distance(loc2);
						}
					}
					
					DFVar.setVar(var, new DFValue(dist, DFType.NUM), localStorage);
				}
				
				case "PerlinNoise" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = (Location) args.get("loc").getVal();
					double frequency = (double) args.get("frequency").getVal() / 20;
					int octaves = (int) DFUtilities.clampNum(args.get("octaves").getInt(), 1, 8);
					double octaveFreq = (double) args.get("octaveFrequency").getVal();
					double octaveAmp = (double) args.get("octaveAmplitude").getVal();
					int seed = (int) Math.round((double) args.get("seed").getVal());
					
					HashMap<String, Noise.FractalType> fractalTypeHashMap = new HashMap<>() {{
						put("Brownian", Noise.FractalType.BROWNIAN);
						put("Billow (Dark edges)", Noise.FractalType.BILLOW);
						put("Rigid (Light edges)", Noise.FractalType.RIGID);
					}};
					
					double noiseValue = Noise.getPerlinFractal(loc, frequency, octaves, octaveFreq, octaveAmp, seed, fractalTypeHashMap.get(tags.get("Fractal Type")));
					DFVar.setVar(var, new DFValue(noiseValue, DFType.NUM), localStorage);
				}
				
				case "VoronoiNoise" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = (Location) args.get("loc").getVal();
					double frequency = (double) args.get("frequency").getVal() / 50;
					double scatter = DFUtilities.clampNum((double) args.get("scatter").getVal(), 5, 15);
					int seed = args.get("seed").getInt();
					
					HashMap<String, Noise.CellEdgeType> cellEdgeTypes = new HashMap<>(){{
						put("Euclidean", Noise.CellEdgeType.EUCLIDEAN);
						put("Manhattan", Noise.CellEdgeType.MANHATTAN);
						put("Natural", Noise.CellEdgeType.NATURAL);
					}};
					Noise.CellEdgeType cellEdgeType = cellEdgeTypes.get(tags.get("Cell Edge Type"));
					
					double noiseValue = Noise.getCellular(loc, frequency, (float) scatter, seed, Noise.CellularReturnType.VORONOI, cellEdgeType);
					DFVar.setVar(var, new DFValue(noiseValue, DFType.NUM), localStorage);
				}
				
				case "WorleyNoise" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					Location loc = (Location) args.get("loc").getVal();
					double frequency = (double) args.get("frequency").getVal() / 50;
					double scatter = DFUtilities.clampNum((double) args.get("scatter").getVal(), 5, 15) / 10;
					int seed = (int) Math.round((double) args.get("seed").getVal());
					
					HashMap<String, Noise.CellEdgeType> cellEdgeTypes = new HashMap<>(){{
						put("Euclidean", Noise.CellEdgeType.EUCLIDEAN);
						put("Manhattan", Noise.CellEdgeType.MANHATTAN);
						put("Natural", Noise.CellEdgeType.NATURAL);
					}};
					Noise.CellEdgeType cellEdgeType = cellEdgeTypes.get(tags.get("Cell Edge Type"));
					
					HashMap<String, Noise.CellularReturnType> cellularReturnTypes = new HashMap<>(){{
						put("Primary", Noise.CellularReturnType.PRIMARY_DISTANCE);
						put("Secondary", Noise.CellularReturnType.SECONDARY_DISTANCE);
						put("Additive", Noise.CellularReturnType.ADDITIVE_DISTANCES);
						put("Subtractive", Noise.CellularReturnType.SUBTRACTIVE_DISTANCES);
						put("Multiplicative", Noise.CellularReturnType.MULTIPLICATIVE_DISTANCES);
						put("Divisive", Noise.CellularReturnType.DIVISIVE_DISTANCES);
					}};
					Noise.CellularReturnType cellularReturnType = cellularReturnTypes.get(tags.get("Distance Calculation"));
					
					double noiseValue = Noise.getCellular(loc, frequency, (float) scatter, seed, cellularReturnType, cellEdgeType);
					DFVar.setVar(var, new DFValue(noiseValue, DFType.NUM), localStorage);
				}
				
				case "CreateDict" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] keys = (DFValue[]) args.get("keys").getVal();
					DFValue[] values = (DFValue[]) args.get("values").getVal();
					
					HashMap<DFValue, DFValue> result = new HashMap<>();
					if(keys != null && values != null)
						for(int i = 0; i < keys.length; i++){
							if(keys[i].type != DFType.TXT) throw new IllegalArgumentException("Attempt to set dictionary key to non-text value");
							result.put(keys[i], values[i]);
						}
					
					DFVar.setVar(var, new DFValue(result, DFType.DICT), localStorage);
				}
				
				case "SetDictValue" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue key = args.get("key");
					DFValue value = args.get("value");
					
					if(key.type != DFType.TXT) throw new IllegalArgumentException("Attempt to set dictionary key to non-text value");
					
					HashMap<DFValue, DFValue> dict = (HashMap<DFValue, DFValue>) DFVar.getVar(var, localStorage).getVal();
					dict.put(key, value);
					
					DFVar.setVar(var, new DFValue(dict, DFType.DICT), localStorage);
				}
				
				case "GetDictValue" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					HashMap<DFValue, DFValue> dict = (HashMap<DFValue, DFValue>) args.get("dict").getVal();
					DFValue key = args.get("key");
					
					if(!dict.containsKey(key)) DFVar.setVar(var, DFValue.nullVar(), localStorage);
					else DFVar.setVar(var, dict.get(key),localStorage);
				}
				
				case "GetDictSize" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					HashMap<DFValue, DFValue> dict = (HashMap<DFValue, DFValue>) args.get("dict").getVal();
					
					DFVar.setVar(var, new DFValue(dict.keySet().size(), DFType.NUM), localStorage);
				}
				
				case "RemoveDictEntry" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue key = args.get("key");
					DFValue[] values = (DFValue[]) args.get("values").getVal();
					
					HashMap<DFValue, DFValue> dict = (HashMap<DFValue, DFValue>) DFVar.getVar(var, localStorage).getVal();
					if(values == null) dict.remove(key);
					else
						for(DFValue val : values)
							if(dict.get(key).equals(val)){
								dict.remove(key);
								break;
							}
					
					DFVar.setVar(var, new DFValue(dict, DFType.DICT), localStorage);
				}
				
				case "ClearDict" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFVar.setVar(var, new DFValue(new HashMap<DFValue, DFValue>(), DFType.DICT), localStorage);
				}
				
				case "GetDictKeys" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					HashMap<DFValue, DFValue> dict = (HashMap<DFValue, DFValue>) args.get("dict").getVal();
					
					DFValue[] keys = dict.keySet().toArray(DFValue[]::new);
					DFVar.setVar(var, new DFValue(keys, DFType.LIST), localStorage);
				}
				
				case "GetDictValues" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					HashMap<DFValue, DFValue> dict = (HashMap<DFValue, DFValue>) args.get("dict").getVal();
					
					DFValue[] values = dict.values().toArray(DFValue[]::new);
					DFVar.setVar(var, new DFValue(values, DFType.LIST), localStorage);
				}
				
				case "AppendDict" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					HashMap<DFValue, DFValue> dict = (HashMap<DFValue, DFValue>) args.get("dict").getVal();
					
					HashMap<DFValue, DFValue> oldDict = (HashMap<DFValue, DFValue>) DFVar.getVar(var, localStorage).getVal();
					for(DFValue key : dict.keySet()) oldDict.put(key, dict.get(key));
				}
				
				case "SortDict" -> {/*TODO*/}
			}
		}
		
		targetMap.put("selection", selection);
	}
	
	private static Location setCoord(Location loc, double coord, String coordinateTag, boolean world){
		loc = loc.clone();
		switch (coordinateTag) {
			case "X" -> loc.setX(world ? coord : coord  - DFPlugin.origin.getX());
			case "Y" -> loc.setY(world ? coord : coord - DFPlugin.origin.getY());
			case "Z" -> loc.setZ(world ? coord : coord - DFPlugin.origin.getZ());
			case "Yaw" -> loc.setYaw((float) coord);
			case "Pitch" -> loc.setPitch((float) coord);
		}
		
		return loc;
	}
	
	private static Location alignCoord(Location loc, String coord, String alignMode){
		double aligning = alignMode.equals("Block center") ? 0.5d : 0;
		
		switch(coord){
			case "X" -> loc.setX(Math.floor(loc.getX()) + aligning);
			case "Y" -> loc.setY(Math.floor(loc.getY()) + aligning);
			case "Z" -> loc.setZ(Math.floor(loc.getZ()) + aligning);
		}
		
		return loc;
	}
	
	private static String randomCase(String text){
		char[] result = new char[text.length()];
		for(int i = 0; i < result.length; i++) result[i] = Math.random() <= 0.5d ? Character.toUpperCase(result[i]) : result[i];
		return String.valueOf(result);
	}
	
	private static Location shiftOnAxis(String axis, Location loc, double shift){
		loc = loc.clone();
		switch (axis) {
			case "X" -> loc.setX(loc.getX() + shift);
			case "Y" -> loc.setY(loc.getY() + shift);
			case "Z" -> loc.setZ(loc.getZ() + shift);
		}
		return loc;
	}
	
	private static Location shiftInDirection(String axis, Location loc, double shift){
		switch(axis){
			case "Forward" -> loc.add(loc.getDirection().normalize().multiply(shift));
			case "Sideways" -> loc.add(DFUtilities.getTangent(loc.getDirection()).normalize().multiply(shift));
			case "Upward" -> loc.add(DFUtilities.getBinormal(loc.getDirection()).normalize().multiply(shift));
		}
		return loc;
	}
	
	private static String hexToCode(String hex){
		StringBuilder builder = new StringBuilder();
		for(String c : hex.toLowerCase().replace("#", "x").split(""))
			builder.append("&").append(c);
		
		return builder.toString();
	}
	
	public static void purgeKeys(String[] varNames, HashMap<String, DFValue> storage, String matchReq, boolean ignoreCase){
		String[] storageKeys = storage.keySet().toArray(String[]::new);
		String[] matchedKeys = new String[0];
		
		for(String name : varNames)
			switch(matchReq) {
				case "Entire name":
					if (!ignoreCase)
						matchedKeys = Arrays.stream(storageKeys).filter(val -> val.equalsIgnoreCase(name)).toArray(String[]::new);
					else matchedKeys = Arrays.stream(storageKeys).filter(val -> val.equals(name)).toArray(String[]::new);
					break;
				case "Full word(s) in name":
					String regex = DFUtilities.escapeRegex(name) + "($| )";
					Pattern pattern = !ignoreCase ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
					
					matchedKeys = Arrays.stream(storageKeys).filter(val -> pattern.matcher(val).find()).toArray(String[]::new);
					break;
				case "Any part of name":
					if(!ignoreCase) matchedKeys = Arrays.stream(storageKeys).filter(val -> val.contains(name)).toArray(String[]::new);
					else matchedKeys = Arrays.stream(storageKeys).filter(val -> val.toLowerCase().contains(name.toLowerCase())).toArray(String[]::new);
					break;
			}
		
		for(String key : matchedKeys) storage.remove(key);
		
	}
}