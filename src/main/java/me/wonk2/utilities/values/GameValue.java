package me.wonk2.utilities.values;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.enums.SelectionType;
import me.wonk2.utilities.enums.Value;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.Date;
import java.util.HashMap;

public class GameValue {
	public Value val;
	public String targetName;
	
	public GameValue(Value val, String targetName){
		this.val = val;
		this.targetName = targetName;
	}
	
	public DFValue getVal(HashMap<String, LivingEntity[]> targetMap){
		LivingEntity target = DFUtilities.getTargets(targetName, targetMap, SelectionType.EITHER)[0];
		return switch (val) {
			case Location -> new DFValue(DFUtilities.getRelativeLoc(target.getLocation()), DFType.LOC);
			case EyeLocation -> new DFValue(DFUtilities.getRelativeLoc(target.getEyeLocation()), DFType.LOC);
			case PlayerCount -> new DFValue(Bukkit.getOnlinePlayers().size(), DFType.NUM);
			case Name -> new DFValue(target.getCustomName(), DFType.TXT);
			case Timestamp -> new DFValue(new Date().getTime(), DFType.NUM);
			case SelectionSize -> new DFValue(targetMap.get("selection") == null ? 0 : targetMap.get("selection").length, DFType.NUM);
			default -> throw new NotImplementedException("This game value is not implemented yet: " + val);
		};
	}
}
