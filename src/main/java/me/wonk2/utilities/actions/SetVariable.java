package me.wonk2.utilities.actions;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.enums.SelectionType;
import me.wonk2.utilities.internals.Noise;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetVariable extends Action {
	
	public SetVariable(String targetName, HashMap<String, LivingEntity[]> targetMap, ParamManager paramManager, String action, HashMap<String, DFValue> localStorage) {
		super(targetName, targetMap, paramManager, action, localStorage);
	}
	
	@Override
	public void invokeAction(){
		Object[] inputArray = paramManager.formatParameters(targetMap);
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray);
		HashMap<Integer, DFValue> primitiveInput = DFUtilities.getPrimitiveInput(inputArray);
		
		
		for(LivingEntity ignored : DFUtilities.getTargets(targetName, targetMap, SelectionType.EITHER))
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

				case "Round" -> {
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
					
					DFVar.setVar(var, new DFValue(result, DFType.LIST), localStorage);
				}

				case "JoinText" -> {
					// TODO: Implement this action (both the logic and parameter information). I am delaying this for now
					// TODO: because it requires "CreateList" support.
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
					
					StringBuilder result = new StringBuilder();
					for (int i = 0; i < amount; i++) result.append(text);
					DFVar.setVar(var, new DFValue(result.toString(), DFType.TXT), localStorage);
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
					DFVar.setVar(var, new DFValue(args.get("list").getVal(), DFType.LIST), localStorage);
				}
				
				case "AppendValue" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] values = (DFValue[]) args.get("values").getVal();
					ArrayList<DFValue> currentVals = (ArrayList<DFValue>) Arrays.asList((DFValue[]) DFVar.getVar(var, localStorage).getVal());
					
					currentVals.addAll(Arrays.asList(values));
					DFVar.setVar(var, new DFValue(currentVals.toArray(), DFType.LIST), localStorage);
				}
				
				case "AppendList" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] lists = (DFValue[]) args.get("lists").getVal();
					ArrayList<DFValue> currentVals = (ArrayList<DFValue>) Arrays.asList((DFValue[]) DFVar.getVar(var, localStorage).getVal());
					
					for (DFValue listWrapper : lists) {
						DFValue[] list = (DFValue[]) listWrapper.getVal();
						currentVals.addAll(Arrays.asList(list));
					}
					
					DFVar.setVar(var, new DFValue(currentVals.toArray(), DFType.LIST), localStorage);
				}
				
				case "GetListValue" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] list = (DFValue[]) args.get("list").getVal();
					int index = args.get("index").getInt() - 1;
					DFValue val = index >= list.length ? DFValue.nullVar() : list[index];
					
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
					DFValue val = args.get("value");
					
					DFVar.setVar(var, new DFValue(Arrays.asList(list).indexOf(val), DFType.NUM), localStorage);
				}
				
				case "ListLength" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					DFValue[] list = (DFValue[]) args.get("list").getVal();
					
					DFVar.setVar(var, new DFValue(list.length, DFType.NUM), localStorage);
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
						case "Yaw" -> value.setVal(loc.getYaw());
						case "Pitch" -> value.setVal(loc.getPitch());
					}
					DFVar.setVar(var, value, localStorage);
				}
				
				case "SetCoord" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					
					Location loc;
					if (args.get("loc").getVal() == null) loc = (Location) DFVar.getVar(var, localStorage).getRawVal();
					else loc = (Location) args.get("loc").getVal();
					
					double coord = (double) args.get("coord").getVal();
					boolean world = tags.get("Coordinate Type").equals("World coordinate");
					
					DFValue value = new DFValue(setCoord(loc, coord, tags.get("Coordinate"), world), DFType.LOC);
					DFVar.setVar(var, value, localStorage);
				}
				
				case "SetAllCoords" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					
					Location loc;
					if (args.get("loc").getVal() == null) loc = (Location) DFVar.getVar(var, localStorage).getRawVal();
					else loc = (Location) args.get("loc").getVal();
					
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
					
					Location loc;
					if (args.get("loc").getVal() == null) loc = (Location) DFVar.getVar(var, localStorage).getRawVal();
					else loc = (Location) args.get("loc").getRawVal();
					double shift = (double) args.get("shift").getVal();
					
					loc = shiftOnAxis(tags.get("Coordinate"), loc, shift);
					DFVar.setVar(var, new DFValue(loc, DFType.LOC), localStorage);
				}
				
				case "ShiftAllAxes" -> {
					DFVar var = (DFVar) args.get("var").getVal();
					
					Location loc;
					if (args.get("loc").getVal() == null) loc = (Location) DFVar.getVar(var, localStorage).getRawVal();
					else loc = (Location) args.get("loc").getRawVal();
					
					double x = (double) args.get("shiftX").getVal();
					double y = (double) args.get("shiftY").getVal();
					double z = (double) args.get("shiftZ").getVal();
					
					loc = shiftOnAxis("X", loc, x);
					loc = shiftOnAxis("Y", loc, y);
					loc = shiftOnAxis("Z", loc, z);
					DFVar.setVar(var, new DFValue(loc, DFType.LOC), localStorage);
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
			}
	}
	
	private static Location setCoord(Location loc, double coord, String coordinateTag, boolean world){
		loc = loc.clone();
		switch (coordinateTag) {
			case "X" -> loc.setX(world ? coord : coord  + DFPlugin.origin.getX());
			case "Y" -> loc.setY(world ? coord : coord + DFPlugin.origin.getY());
			case "Z" -> loc.setZ(world ? coord : coord + DFPlugin.origin.getZ());
			case "Yaw" -> loc.setYaw((float) coord);
			case "Pitch" -> loc.setPitch((float) coord);
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
	
	private static String hexToCode(String hex){
		StringBuilder builder = new StringBuilder();
		for(String c : hex.toLowerCase().replace("#", "x").split(""))
			builder.append("&").append(c);
		
		return builder.toString();
	}
	
	public static void purgeKeys(String[] varNames, HashMap<String, DFValue> storage, String matchReq, boolean ignoreCase){
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
		
	}
}