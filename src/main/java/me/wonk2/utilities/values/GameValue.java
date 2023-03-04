package me.wonk2.utilities.values;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.enums.DFType;
import me.wonk2.utilities.enums.SelectionType;
import me.wonk2.utilities.enums.Value;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GameValue {
	public Value val;
	public String targetName;
	
	public GameValue(Value val, String targetName){
		this.val = val;
		this.targetName = targetName.equals("last-spawned entity") ? "lastentity" : targetName;
	}
	
	public DFValue getVal(HashMap<String, Entity[]> targetMap){
		Entity target = DFUtilities.getTargets(targetName, targetMap, SelectionType.EITHER)[0];
		return switch (val) {
			case Location -> new DFValue(DFUtilities.getRelativeLoc(target.getLocation()), DFType.LOC);
			case EyeLocation -> new DFValue(DFUtilities.getRelativeLoc(((LivingEntity) target).getEyeLocation()), DFType.LOC);
			case PlayerCount -> new DFValue(Bukkit.getOnlinePlayers().size(), DFType.NUM);
			case Name -> new DFValue(target.getCustomName(), DFType.TXT);
			case UUID -> new DFValue(target.getUniqueId().toString(), DFType.TXT);
			case Timestamp -> new DFValue(new Date().getTime(), DFType.NUM);
			case SelectionSize -> new DFValue(targetMap.get("selection") == null ? 0 : targetMap.get("selection").length, DFType.NUM);
			case MainHandItem -> new DFValue(((LivingEntity) target).getEquipment().getItemInMainHand(), DFType.ITEM);
			case HeldSlot -> new DFValue(((Player) target).getInventory().getHeldItemSlot() + 1, DFType.NUM);
			case HotbarItems ->  getHotbar((Player) target);
			default -> throw new NotImplementedException("This game value is not implemented yet: " + val);
		};
	}
	
	public DFValue getHotbar(Player p) {
		ArrayList<DFValue> result = new ArrayList<>();
		for(int i = 0; i < 9; i++) result.add(new DFValue(p.getInventory().getItem(i), DFType.ITEM));
		return new DFValue(result.toArray(DFValue[]::new), DFType.LIST);
	}
}


