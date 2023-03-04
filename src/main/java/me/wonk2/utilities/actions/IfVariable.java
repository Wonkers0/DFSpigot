package me.wonk2.utilities.actions;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.ParamManager;
import me.wonk2.utilities.actions.pointerclasses.Conditional;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.enums.SelectionType;
import me.wonk2.utilities.values.DFValue;
import me.wonk2.utilities.values.DFVar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
public class IfVariable extends Conditional {
	Entity[] targets;
	
	public IfVariable(String targetName, HashMap<String, Entity[]> targetMap, ParamManager paramManager, String action, boolean inverted, HashMap<String, DFValue> localStorage){
		super(targetName, targetMap, paramManager, action, localStorage, inverted);
	}
	
	@Override
	public boolean evaluateCondition(){
		Object[] inputArray = paramManager.formatParameters(targetMap);
		HashMap<String, DFValue> args = DFUtilities.getArgs(inputArray);
		HashMap<String, String> tags = DFUtilities.getTags(inputArray);
		this.targets = DFUtilities.getTargets(targetName, targetMap, SelectionType.EITHER);
		
		for(int i = 0; i < (targets.length == 0 ? 1 : targets.length); i++) if(checkForTarget(args, tags)) return true;
		return false;
	}
	
	protected boolean checkForTarget(HashMap<String, DFValue> args, HashMap<String, String> tags){
		switch(action){
				case "!=":
				case "=": {
					DFValue value = args.get("value");
					DFValue[] checks = (DFValue[]) args.get("checks").getVal();
					
					for(DFValue check : checks){
						if(value.getVal().equals(check.getVal())){
							if(action.equals("=")) return true;
							if(action.equals("!=")) return false;
						}
					}
					
					return action.equals("!=");
				}
				
				case ">":
				case ">=":
				case "<":
				case "<=": {
					double num = (double) args.get("num").getVal();
					double check = (double) args.get("check").getVal();
					
					switch(action){
						case ">": return num > check;
						case ">=": return num >= check;
						case "<": return num < check;
						case "<=": return num <= check;
					}
				}
				
				case "InRange": {
					DFValue val = args.get("value");
					DFValue lower = args.get("lower");
					DFValue upper = args.get("upper");
					
					if(val.type == DFType.NUM) return inRange((double) val.getVal(), (double) lower.getVal(), (double) upper.getVal());
					else if (val.type == DFType.LOC) return inRange((Location) val.getVal(), (Location) lower.getVal(), (Location) upper.getVal());
					
					return false;
				}
				
				case "LocIsNear": {
					Location loc = (Location) args.get("loc").getVal();
					Location[] checkLocs = DFValue.castLoc((DFValue[]) args.get("checkLocs").getVal());
					double radius = (double) args.get("radius").getVal();
					
					for(Location checkLoc : checkLocs)
						if(DFUtilities.locIsNear(DFPlugin.world, checkLoc, loc, radius, tags.get("Shape"))) return true;
					
					return false;
				}
				
				case "TextMatches": {
					//TODO: RegEx
					String match = (String) args.get("match").getVal();
					String[] checks = DFValue.castTxt((DFValue[]) args.get("checks").getVal());
					
					for(String check : checks)
						if(match.equals(check) || (match.equalsIgnoreCase(check) && tags.get("Ignore Case").equals("True"))) return true;
					
					return false;
				}
				
				case "Contains": {
					boolean ignoreCase = tags.get("Ignore Case").equals("True");
					String text = (String) args.get("txt").getVal();
					if(ignoreCase) text = text.toLowerCase();
					
					String[] checkTxts = DFValue.castTxt((DFValue[]) args.get("checkTxts").getVal());
					
					for(String checkTxt : checkTxts)
						if(text.contains(ignoreCase ? checkTxt.toLowerCase() : checkTxt)) return true;
					
					
					return false;
				}
				
				case "StartsWith": {
					boolean ignoreCase = tags.get("Ignore Case").equals("True");
					String text = ((String) args.get("txt").getVal()).split(" ")[0];
					if(ignoreCase) text = text.toLowerCase();
					
					String[] checkTxts = DFValue.castTxt((DFValue[]) args.get("checkTxts").getVal());
					
					for(String checkTxt : checkTxts)
						if(text.equals(ignoreCase ? checkTxt.toLowerCase() : checkTxt)) return true;
					
					return false;
				}
				
				case "EndsWidth": {
					boolean ignoreCase = tags.get("Ignore Case").equals("True");
					String[] splitText = ((String) args.get("txt").getVal()).split(" ");
					String text = splitText[splitText.length - 1];
					if(ignoreCase) text = text.toLowerCase();
					
					String[] checkTxts = DFValue.castTxt((DFValue[]) args.get("checkTxts").getVal());
					
					for(String checkTxt : checkTxts)
						if(text.equals(ignoreCase ? checkTxt.toLowerCase() : checkTxt)) return true;
					
					return false;
				}
				
				case "VarExists": {
					DFVar var = (DFVar) args.get("var").getVal();
					return DFVar.varExists(var, localStorage);
				}
				
				case "VarIsType": {
					String typeTag = tags.get("Variable Type");
					DFValue value = args.get("value");
					
					HashMap<String, DFType> types = new HashMap<>(){{
						put("Number", DFType.NUM);
						put("Text", DFType.TXT);
						put("Location", DFType.LOC);
						put("Item", DFType.ITEM);
						put("List", DFType.LIST);
						put("Potion effect", DFType.POT);
						put("Sound", DFType.SND);
						put("Particle", DFType.PART);
						put("Vector", DFType.VEC);
						put("Dictionary", DFType.DICT);
					}};
					
					return value.type == types.get(typeTag);
				}
				
				case "ItemEquals": {
					ItemStack item = (ItemStack) args.get("item").getVal();
					ItemStack[] checkItems = DFValue.castItem((DFValue[]) args.get("checkItems").getVal());
					
					for(ItemStack checkItem : checkItems)
						switch(tags.get("Comparison Mode")){
							case "Exactly equals":
								if(item == checkItem) return true;
								break;
							case "Ignore stack size":
								if(item.isSimilar(checkItem)) return true;
								break;
							case "Ignore durability and stack size": {
								if(checkItem == null || item == null) return item == checkItem;
								
								((Damageable) checkItem).setDamage(((Damageable) item).getDamage());
								if(item.isSimilar(checkItem)) return true;
								break;
							}
							case "Material only": {
								if(checkItem == null || item == null) return item == checkItem;
								
								if(item.getType() == checkItem.getType()) return true;
								break;
							}
						}
					
					return false;
				}
				
				case "DictHasKey": {
					HashMap<DFValue, DFValue> dict = (HashMap<DFValue, DFValue>) args.get("dict").getVal();
					DFValue key = args.get("key");
					
					return key.type == DFType.TXT && dict.containsKey(key);
				}
				
				case "ItemHasTag": {
					ItemStack item = (ItemStack) args.get("item").getVal();
					if(item == null) return false;
					
					String tagName = (String) args.get("tag").getVal();
					DFValue check = args.get("check");
					NamespacedKey key = new NamespacedKey("hypercube", tagName);
					
					ItemMeta itemMeta = item.getItemMeta();
					if(itemMeta == null) return false;
					PersistentDataContainer dataHolder = itemMeta.getPersistentDataContainer();
					
					DFValue value = DFValue.nullVar();
					if(dataHolder.has(key, PersistentDataType.STRING))
						value = new DFValue(dataHolder.get(key, PersistentDataType.STRING), DFType.TXT);
					else if(dataHolder.has(key, PersistentDataType.DOUBLE))
						value = new DFValue(dataHolder.get(key, PersistentDataType.DOUBLE), DFType.NUM);
					
					if(check.getVal() == null)
						return dataHolder.has(key, PersistentDataType.STRING) || dataHolder.has(key, PersistentDataType.DOUBLE);
					else return value.equals(check);
				}
				
				case "ListContains": {
					List<DFValue> list = Arrays.asList((DFValue[]) args.get("list").getVal());
					DFValue[] values = (DFValue[]) args.get("values").getVal();
					
					for(DFValue value : values)
						if(list.contains(value)) return true;
					
					return false;
				}
				
				case "ListValueEq": {
					DFValue[] list = (DFValue[]) args.get("list").getVal();
					int index = args.get("index").getInt() - 1;
					DFValue[] values = (DFValue[]) args.get("values").getVal();
					
					for(DFValue value : values)
						if(list[index] == value) return true;
					
					return false;
				}
			}
		
		return false;
	}
	
	private static boolean inRange(double num, double lower, double upper){
		return num >= lower && num <= upper;
	}
	
	private static boolean inRange(Location loc, Location lowerLeft, Location upperRight){
		return (loc.getX() >= lowerLeft.getX() && loc.getX() <= upperRight.getX())
			&& (loc.getY() >= lowerLeft.getY() && loc.getY() <= upperRight.getY())
			&& (loc.getZ() >= lowerLeft.getZ() && loc.getZ() <= upperRight.getZ());
	}
}