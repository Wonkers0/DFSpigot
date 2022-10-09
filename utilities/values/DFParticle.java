package me.wonk2.utilities.values;

import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.enums.DFType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class DFParticle {
	public static HashMap<String, Particle> particleMap = new HashMap<>(){{
		put("Rain", Particle.WATER_DROP);
		put("Underwater", Particle.SUSPENDED_DEPTH);
		put("Ash", Particle.ASH);
		put("White Ash", Particle.WHITE_ASH);
		put("Crimson Spore", Particle.CRIMSON_SPORE);
		put("Warped Spore", Particle.WARPED_SPORE);
		put("Effect", Particle.SPELL);
		put("Instant Effect", Particle.SPELL_INSTANT);
		put("Entity Effect", Particle.SPELL_MOB);
		put("Ambient Entity Effect", Particle.SPELL_MOB_AMBIENT);
		put("Dolphin", Particle.DOLPHIN);
		put("Glowing Squid Glow", Particle.GLOW);
		put("Falling Nectar", Particle.FALLING_NECTAR);
		put("Angry Villager", Particle.VILLAGER_ANGRY);
		put("Happy Villager", Particle.VILLAGER_HAPPY);
		put("Spit", Particle.SPIT);
		put("Sneeze", Particle.SNEEZE);
		put("Heart", Particle.HEART);
		put("Witch", Particle.SPELL_WITCH);
		put("Explosion", Particle.EXPLOSION_LARGE);
		put("Explosion Emitter", Particle.EXPLOSION_HUGE);
		put("Flash", Particle.FLASH);
		put("Splash", Particle.WATER_SPLASH);
		put("Fishing", Particle.WATER_WAKE);
		put("Firework", Particle.FIREWORKS_SPARK);
		put("Bubble", Particle.WATER_BUBBLE);
		put("Bubble Pop", Particle.BUBBLE_POP);
		put("Snowflake", Particle.SNOWFLAKE);
		put("Snowball", Particle.SNOWBALL);
		put("Slime", Particle.SLIME);
		put("Item", Particle.ITEM_CRACK);
		put("Critical Hit", Particle.CRIT);
		put("Enchanted Hit", Particle.CRIT_MAGIC);
		put("Damage Indicator", Particle.DAMAGE_INDICATOR);
		put("Sweep Attack", Particle.SWEEP_ATTACK);
		put("Squid Ink", Particle.SQUID_INK);
		put("Glowing Squid Ink", Particle.GLOW_SQUID_INK);
		put("Poof", Particle.CLOUD);
		put("Elder Guardian", Particle.MOB_APPEARANCE);
		put("Dragon Breath", Particle.DRAGON_BREATH);
		put("Totem of Undying", Particle.TOTEM);
		put("Cloud", Particle.CLOUD);
		put("Lava", Particle.LAVA);
		put("Mycelium", Particle.ASH); //TODO
		put("Spore Blossom Fall", Particle.FALLING_SPORE_BLOSSOM);
		put("Spore Blossom Air", Particle.SPORE_BLOSSOM_AIR);
		put("Portal", Particle.PORTAL);
		put("Reverse Portal", Particle.REVERSE_PORTAL);
		put("Enchant", Particle.ENCHANTMENT_TABLE);
		put("Small Flame", Particle.SMALL_FLAME);
		put("Flame", Particle.FLAME);
		put("Soul Flame", Particle.SOUL_FIRE_FLAME);
		put("Nautilus", Particle.NAUTILUS);
		put("End Rod", Particle.END_ROD);
		put("Falling Dust", Particle.FALLING_DUST);
		put("Whirlpool Bubble Column", Particle.CURRENT_DOWN);
		put("Upward Bubble Column", Particle.BUBBLE_COLUMN_UP);
		put("Campfire Smoke", Particle.CAMPFIRE_COSY_SMOKE);
		put("Campfire Signal Smoke", Particle.CAMPFIRE_SIGNAL_SMOKE);
		put("Smoke", Particle.SMOKE_NORMAL);
		put("Large Smoke", Particle.SMOKE_LARGE);
		put("Note", Particle.NOTE);
		put("Wax On", Particle.WAX_ON);
		put("Wax Off", Particle.WAX_OFF);
		put("Scrape Oxidization", Particle.SCRAPE);
		put("Composter", Particle.COMPOSTER);
		put("Block Marker", Particle.BLOCK_MARKER);
		put("Dust", Particle.REDSTONE);
		put("Soul", Particle.SOUL);
		put("Block", Particle.BLOCK_CRACK); // TODO
		put("Electric Spark", Particle.ELECTRIC_SPARK);
		put("Dripping Obsidian Tear", Particle.DRIPPING_OBSIDIAN_TEAR);
		put("Falling Obsidian Tear", Particle.FALLING_OBSIDIAN_TEAR);
		put("Landing Obsidian Tear", Particle.LANDING_OBSIDIAN_TEAR);
		put("Dripping Water", Particle.DRIP_WATER);
		put("Dripstone Dripping Water", Particle.DRIPPING_DRIPSTONE_WATER);
		put("Falling Water", Particle.FALLING_WATER);
		put("Dripstone Falling Water", Particle.FALLING_DRIPSTONE_WATER);
		put("Dripping Lava", Particle.DRIP_LAVA);
		put("Dripstone Dripping Lava", Particle.DRIPPING_DRIPSTONE_LAVA);
		put("Falling Lava", Particle.FALLING_LAVA);
		put("Dripstone Falling Lava", Particle.FALLING_DRIPSTONE_LAVA);
		put("Landing Lava", Particle.LANDING_LAVA);
		put("Dripping Honey", Particle.DRIPPING_HONEY);
		put("Falling Honey", Particle.FALLING_HONEY);
		put("Landing Honey", Particle.LANDING_HONEY);
	}};
	
	public String name;
	public double size = 1;
	public double hSpread;
	public double vSpread;
	public int amount;
	public Vector motion = new Vector(0, 0, 0);
	public Material material;
	public Color color;
	
	public DFParticle(String name, double amount, double hSpread, double vSpread, Double size, Double sizeVariation, Vector motion, Double motionVariation, Color color, Double colorVariation, Material material){
		this.name = name;
		this.amount = (int) amount;
		this.hSpread = hSpread;
		this.vSpread = vSpread;
		if(size != null){
			this.size = size;
			// TODO: Apply size variation here
		}
		if(motion != null){
			this.motion = motion;
			// TODO: Apply motion variation here
		}
		if(color != null){
			this.color = color;
			// TODO: Apply color variation here
		}
	}
	
	public Particle getParticle(){
		Particle returnVal = particleMap.get(name);
		return returnVal;
	}
	
	public Object getData(){
		switch(getParticle()){
			case REDSTONE:
				return new Particle.DustOptions(color, (float) size);
			case BLOCK_CRACK:
			case BLOCK_DUST:
			case BLOCK_MARKER:
			case FALLING_DUST:
				return material.createBlockData();
			case ITEM_CRACK:
				return new ItemStack(material);
		}
		return null;
	}
	
	public void play(Player player, Location loc){
		Particle p = getParticle();
		Object data = getData();
		for(int i = 0; i < amount; i++){
			if(data != null)
				player.spawnParticle(p, loc, amount, hSpread, vSpread, hSpread, data);
			else player.spawnParticle(p, loc, amount, hSpread, vSpread, hSpread);
		}
	}
}
