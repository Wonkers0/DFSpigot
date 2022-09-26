package me.wonk2.utilities.internals;

import java.util.HashMap;
import java.util.UUID;

public class EntityData {
	private static final HashMap<UUID, EntityData> entityData = new HashMap<>();
	
	public static EntityData getEntityData(UUID uuid){
		if(!entityData.containsKey(uuid))
			entityData.put(uuid, new EntityData());
		
		return entityData.get(uuid);
	}
	
	public float tntPower;
}
