package me.wonk2.Utilities;

//import eu.endercentral.crazy_advancements.JSONMessage;
//import eu.endercentral.crazy_advancements.NameKey;
//import eu.endercentral.crazy_advancements.advancement.Advancement;
//import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
//import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
//import eu.endercentral.crazy_advancements.advancement.criteria.Criteria;
//import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.nbt.TagParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.alchemy.Potion;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;

public class DFUtilities implements Listener {
		public static FileManager managerClass;
		/*
				** Big thanks to Infernity for helping with sounds **
				** Big thanks to shermy_the_cat for helping with sounds **
		*/
		static HashMap<String, String> soundMap = new HashMap<String, String>(){{
						put("Pling", "block.note_block.pling");
						put("Chime", "block.note_block.chime");
						put("Bit", "block.note_block.bit");
						put("Bell", "block.note_block.bell");
						put("Bass", "block.note_block.bass");
						put("Hat", "block.note_block.hat");
						put("Cow Bell", "block.note_block.cow_bell");
						put("Flute", "block.note_block.flute");
						put("Didgeridoo", "block.note_block.didgeridoo");
						put("Harp", "block.note_block.harp");
						put("Snare Drum", "block.note_block.snare");
						put("Iron Xylophone", "block.note_block.iron_xylophone");
						put("Xylophone", "block.note_block.xylophone");
						put("Bass Drum", "block.note_block.basedrum");
						put("Guitar", "block.note_block.guitar");
						put("Banjo", "block.note_block.banjo");
						put("Amethyst Block Break", "block.amethyst_block.break");
						put("Amethyst Block Chime", "block.amethyst_block.chime");
						put("Amethyst Block Fall", "block.amethyst_block.fall");
						put("Amethyst Block Hit", "block.amethyst_block.hit");
						put("Amethyst Block Place", "block.amethyst_block.place");
						put("Amethyst Block Step", "block.amethyst_block.step");
						put("Amethyst Cluster Break", "block.amethyst_cluster.break");
						put("Amethyst Cluster Fall", "block.amethyst_cluster.fall");
						put("Amethyst Cluster Hit", "block.amethyst_cluster.hit");
						put("Amethyst Cluster Place", "block.amethyst_cluster.place");
						put("Amethyst Cluster Step", "block.amethyst_cluster.step");
						put("Large Amethyst Bud Break", "block.large_amethyst_bud.break");
						put("Large Amethyst Bud Place", "block.large_amethyst_bud.place");
						put("Medium Amethyst Bud Break", "block.medium_amethyst_bud.break");
						put("Medium Amethyst Bud Place", "block.medium_amethyst_bud.place");
						put("Small Amethyst Bud Break", "block.small_amethyst_bud.break");
						put("Small Amethyst Bud Place", "block.small_amethyst_bud.place");
						put("Azalea Place", "block.azalea.place");
						put("Azalea Step", "block.azalea.step");
						put("Azalea Break", "block.azalea.break");
						put("Azalea Fall", "block.azalea.fall");
						put("Azalea Hit", "block.azalea.hit");
						put("Azalea Leaves Break", "block.azalea_leaves.break");
						put("Azalea Leaves Fall", "block.azalea_leaves.fall");
						put("Azalea Leaves Hit", "block.azalea_leaves.hit");
						put("Azalea Leaves Place", "block.azalea_leaves.place");
						put("Azalea Leaves Step", "block.azalea_leaves.step");
						put("Bamboo Break", "block.bamboo.break");
						put("Bamboo Fall", "block.bamboo.fall");
						put("Bamboo Hit", "block.bamboo.hit");
						put("Bamboo Place", "block.bamboo.place");
						put("Bamboo Sapling Break", "block.bamboo_sapling.break");
						put("Bamboo Sapling Hit", "block.bamboo_sapling.hit");
						put("Bamboo Sapling Place", "block.bamboo_sapling.place");
						put("Bamboo Step", "block.bamboo.step");
						put("Beehive Drip", "block.beehive.drip");
						put("Beehive Enter", "block.beehive.enter");
						put("Beehive Exit", "block.beehive.exit");
						put("Beehive Shear", "block.beehive.shear");
						put("Beehive Work", "block.beehive.work");
						put("Big Dripleaf Break", "block.big_dripleaf.break");
						put("Big Dripleaf Fall", "block.big_dripleaf.fall");
						put("Big Dripleaf Hit", "block.big_dripleaf.hit");
						put("Big Dripleaf Place", "block.big_dripleaf.place");
						put("Big Dripleaf Step", "block.big_dripleaf.step");
						put("Big Dripleaf Tilt Down", "block.big_dripleaf.tilt_down");
						put("Big Dripleaf Tilt Up", "block.big_dripleaf.tilt_up");
						put("Bone Block Break", "block.bone_block.break");
						put("Bone Block Fall", "block.bone_block.fall");
						put("Bone Block Hit", "block.bone_block.hit");
						put("Bone Block Place", "block.bone_block.place");
						put("Bone Block Step", "block.bone_block.step");
						put("Bubble Column Bubble Pop", "block.bubble_column.bubble_pop");
						put("Bubble Column Ambient (Up)", "block.bubble_column.upwards_ambient");
						put("Bubble Column Inside (Up)", "block.bubble_column.upwards_inside");
						put("Bubble Column Ambient (Down)", "block.bubble_column.whirlpool_ambient");
						put("Bubble Column Inside (Down)", "block.bubble_column.whirlpool_inside");
						put("Calcite Break", "block.calcite.break");
						put("Calcite Fall", "block.calcite.fall");
						put("Calcite Hit", "block.calcite.hit");
						put("Calcite Place", "block.calcite.place");
						put("Calcite Step", "block.calcite.step");
						put("Cave Vines Break", "block.cave_vines.break");
						put("Cave Vines Fall", "block.cave_vines.fall");
						put("Cave Vines Hit", "block.cave_vines.hit");
						put("Cave Vines Pick", "block.cave_vines.pick_berries");
						put("Cave Vines Place", "block.cave_vines.place");
						put("Cave Vines Step", "block.cave_vines.step");
						put("Coral Block Break", "block.coral_block.break");
						put("Coral Block Fall", "block.coral_block.fall");
						put("Coral Block Hit", "block.coral_block.hit");
						put("Coral Block Place", "block.coral_block.place");
						put("Coral Block Step", "block.coral_block.step");
						put("Deepslate Break", "block.deepslate.break");
						put("Deepslate Fall", "block.deepslate.fall");
						put("Deepslate Hit", "block.deepslate.hit");
						put("Deepslate Place", "block.deepslate.place");
						put("Deepslate Step", "block.deepslate.step");
						put("Dripstone Break", "block.dripstone.break");
						put("Dripstone Fall", "block.dripstone.fall");
						put("Dripstone Hit", "block.dripstone.hit");
						put("Dripstone Place", "block.dripstone.place");
						put("Dripstone Step", "block.dripstone.step");
						put("Flowering Azalea Break", "block.flowering_azalea.break");
						put("Flowering Azalea Fall", "block.flowering_azalea.fall");
						put("Flowering Azalea Hit", "block.flowering_azalea.hit");
						put("Flowering Azalea Place", "block.flowering_azalea.place");
						put("Flowering Azalea Step", "block.flowering_azalea.step");
						put("Grass Break", "block.grass.break");
						put("Grass Fall", "block.grass.fall");
						put("Grass Hit", "block.grass.hit");
						put("Grass Place", "block.grass.place");
						put("Grass Step", "block.grass.step");
						put("Gravel Break", "block.gravel.break");
						put("Gravel Fall", "block.gravel.fall");
						put("Gravel Hit", "block.gravel.hit");
						put("Gravel Place", "block.gravel.place");
						put("Gravel Step", "block.gravel.step");
						put("Hanging Roots Break", "block.hanging_roots.break");
						put("Hanging Roots Fall", "block.hanging_roots.fall");
						put("Hanging Roots Hit", "block.hanging_roots.hit");
						put("Hanging Roots Place", "block.hanging_roots.place");
						put("Hanging Roots Step", "block.hanging_roots.step");
						put("Lava Bucket Empty", "item.bucket.empty_lava");
						put("Lava Bucket Fill", "item.bucket.fill_lava");
						put("Lava Ambient", "block.lava.ambient");
						put("Lava Extinguish", "block.lava.extinguish");
						put("Lava Pop", "block.lava.pop");
						put("Moss Block Break", "block.moss_block.break");
						put("Moss Block Fall", "block.moss_block.fall");
						put("Moss Block Hit", "block.moss_block.hit");
						put("Moss Block Place", "block.moss_block.place");
						put("Moss Block Step", "block.moss_block.step");
						put("Moss Carpet Break", "block.moss_carpet.break");
						put("Moss Carpet Fall", "block.moss_carpet.fall");
						put("Moss Carpet Hit", "block.moss_carpet.hit");
						put("Moss Carpet Place", "block.moss_carpet.place");
						put("Moss Carpet Step", "block.moss_carpet.step");
						put("Crop Plant", "item.crop.plant");
						put("Crop Break", "item.crop.break");
						put("Lily Pad Place", "block.lily_pad.place");
						put("Pumpkin Carve", "block.pumpkin.carve");
						put("Sweet Berry Bush Break", "block.sweet_berry_bush.break");
						put("Sweet Berry Bush Pick", "item.sweet_berries.pick_from_bush");
						put("Sweet Berry Bush Place", "block.sweet_berry_bush.place");
						put("Vine Climb", "block.vine.step");
						put("Pointed Dripstone Break", "block.pointed_dripstone.break");
						put("Pointed Dripstone Drip Lava", "block.pointed_dripstone.drip_lava");
						put("Pointed Dripstone Drip Lava Into Cauldron", "block.pointed_dripstone.drip_lava_into_cauldron");
						put("Pointed Dripstone Drip Water", "block.pointed_dripstone.drip_water");
						put("Pointed Dripstone Drip Water Into Cauldron", "block.pointed_dripstone.drip_water_into_cauldron");
						put("Pointed Dripstone Fall", "block.pointed_dripstone.fall");
						put("Pointed Dripstone Hit", "block.pointed_dripstone.hit");
						put("Pointed Dripstone Land", "block.pointed_dripstone.land");
						put("Pointed Dripstone Place", "block.pointed_dripstone.place");
						put("Pointed Dripstone Step", "block.pointed_dripstone.step");
						put("Powder Snow Break", "block.powder_snow.break");
						put("Powder Snow Fall", "block.powder_snow.fall");
						put("Powder Snow Hit", "block.powder_snow.hit");
						put("Powder Snow Place", "block.powder_snow.place");
						put("Powder Snow Step", "block.powder_snow.step");
						put("Rooted Dirt Break", "block.rooted_dirt.break");
						put("Rooted Dirt Fall", "block.rooted_dirt.fall");
						put("Rooted Dirt Hit", "block.rooted_dirt.hit");
						put("Rooted Dirt Place", "block.rooted_dirt.place");
						put("Rooted Dirt Step", "block.rooted_dirt.step");
						put("Sand Break", "block.sand.break");
						put("Sand Fall", "block.sand.fall");
						put("Sand Hit", "block.sand.hit");
						put("Sand Place", "block.sand.place");
						put("Sand Step", "block.sand.step");
						put("Seagrass Break", "block.seagrass.break");
						put("Seagrass Fall", "block.seagrass.fall");
						put("Seagrass Hit", "block.seagrass.hit");
						put("Seagrass Place", "block.seagrass.place");
						put("Seagrass Step", "block.seagrass.step");
						put("Small Dripleaf Break", "block.small_dripleaf.break");
						put("Small Dripleaf Fall", "block.small_dripleaf.fall");
						put("Small Dripleaf Hit", "block.small_dripleaf.hit");
						put("Small Dripleaf Place", "block.small_dripleaf.place");
						put("Small Dripleaf Step", "block.small_dripleaf.step");
						put("Spore Blossom Break", "block.spore_blossom.break");
						put("Spore Blossom Fall", "block.spore_blossom.fall");
						put("Spore Blossom Hit", "block.spore_blossom.hit");
						put("Spore Blossom Place", "block.spore_blossom.place");
						put("Spore Blossom Step", "block.spore_blossom.step");
						put("Snow Break", "block.snow.break");
						put("Snow Fall", "block.snow.fall");
						put("Snow Hit", "block.snow.hit");
						put("Snow Place", "block.snow.place");
						put("Snow Step", "block.snow.step");
						put("Stone Break", "block.stone.break");
						put("Stone Fall", "block.stone.fall");
						put("Stone Hit", "block.stone.hit");
						put("Stone Place", "block.stone.place");
						put("Stone Step", "block.stone.step");
						put("Tuff Break", "block.tuff.break");
						put("Tuff Fall", "block.tuff.fall");
						put("Tuff Hit", "block.tuff.hit");
						put("Tuff Place", "block.tuff.place");
						put("Tuff Step", "block.tuff.step");
						put("Vine Break", "block.vine.break");
						put("Vine Fall", "block.vine.fall");
						put("Vine Hit", "block.vine.hit");
						put("Vine Place", "block.vine.place");
						put("Water Bucket Empty", "item.bucket.empty");
						put("Water Bucket Fill", "item.bucket.fill");
						put("Water Ambient", "block.water.ambient");
						put("Water Enter", "ambient.underwater.enter");
						put("Water Exit", "ambient.underwater.exit");
						put("Wood Break", "block.wood.break");
						put("Wood Fall", "block.wood.fall");
						put("Wood Hit", "block.wood.hit");
						put("Wood Place", "block.wood.place");
						put("Wood Step", "block.wood.step");
						put("Ancient Debris Break", "block.ancient_debris.break");
						put("Ancient Debris Fall", "block.ancient_debris.fall");
						put("Ancient Debris Hit", "block.ancient_debris.hit");
						put("Ancient Debris Place", "block.ancient_debris.place");
						put("Ancient Debris Step", "block.ancient_debris.step");
						put("Basalt Break", "block.basalt.break");
						put("Basalt Fall", "block.basalt.fall");
						put("Basalt Hit", "block.basalt.hit");
						put("Basalt Place", "block.basalt.place");
						put("Basalt Step", "block.basalt.step");
						put("Gilded Blackstone Break", "block.gilded_blackstone.break");
						put("Gilded Blackstone Fall", "block.gilded_blackstone.fall");
						put("Gilded Blackstone Hit", "block.gilded_blackstone.hit");
						put("Gilded Blackstone Place", "block.gilded_blackstone.place");
						put("Gilded Blackstone Step", "block.gilded_blackstone.step");
						put("Netherrack Break", "block.netherrack.break");
						put("Netherrack Fall", "block.netherrack.fall");
						put("Netherrack Hit", "block.netherrack.hit");
						put("Netherrack Place", "block.netherrack.place");
						put("Netherrack Step", "block.netherrack.step");
						put("Nether Gold Ore Break", "block.nether_gold_ore.break");
						put("Nether Gold Ore Fall", "block.nether_gold_ore.fall");
						put("Nether Gold Ore Hit", "block.nether_gold_ore.hit");
						put("Nether Gold Ore Place", "block.nether_gold_ore.place");
						put("Nether Gold Ore Step", "block.nether_gold_ore.step");
						put("Nether Fungus Break", "block.fungus.break");
						put("Nether Fungus Fall", "block.fungus.fall");
						put("Nether Fungus Hit", "block.fungus.hit");
						put("Nether Fungus Place", "block.fungus.place");
						put("Nether Fungus Step", "block.fungus.step");
						put("Nether Quartz Ore Break", "block.nether_ore.break");
						put("Nether Quartz Ore Fall", "block.nether_ore.fall");
						put("Nether Quartz Ore Hit", "block.nether_ore.hit");
						put("Nether Quartz Ore Place", "block.nether_ore.place");
						put("Nether Quartz Ore Step", "block.nether_ore.step");
						put("Nether Roots Break", "block.roots.break");
						put("Nether Roots Fall", "block.roots.fall");
						put("Nether Roots Hit", "block.roots.hit");
						put("Nether Roots Place", "block.roots.place");
						put("Nether Roots Step", "block.roots.step");
						put("Nether Sprouts Break", "block.nether_sprouts.break");
						put("Nether Sprouts Fall", "block.nether_sprouts.fall");
						put("Nether Sprouts Hit", "block.nether_sprouts.hit");
						put("Nether Sprouts Place", "block.nether_sprouts.place");
						put("Nether Sprouts Step", "block.nether_sprouts.step");
						put("Nether Stem Break", "block.stem.break");
						put("Nether Stem Fall", "block.stem.fall");
						put("Nether Stem Hit", "block.stem.hit");
						put("Nether Stem Place", "block.stem.place");
						put("Nether Stem Step", "block.stem.step");
						put("Nether Vine Break", "block.weeping_vines.break");
						put("Nether Vine Fall", "block.weeping_vines.fall");
						put("Nether Vine Hit", "block.weeping_vines.hit");
						put("Nether Vine Place", "block.weeping_vines.place");
						put("Nether Vine Step", "block.weeping_vines.step");
						put("Nether Wart Break", "block.nether_wart.break");
						put("Nether Wart Plant", "item.nether_wart.plant");
						put("Nether Wart Block Break", "block.wart_block.break");
						put("Nether Wart Block Fall", "block.wart_block.fall");
						put("Nether Wart Block Hit", "block.wart_block.hit");
						put("Nether Wart Block Place", "block.wart_block.place");
						put("Nether Wart Block Step", "block.wart_block.step");
						put("Nylium Break", "block.nylium.break");
						put("Nylium Fall", "block.nylium.fall");
						put("Nylium Hit", "block.nylium.hit");
						put("Nylium Place", "block.nylium.place");
						put("Nylium Step", "block.nylium.step");
						put("Shroomlight Break", "block.shroomlight.break");
						put("Shroomlight Fall", "block.shroomlight.fall");
						put("Shroomlight Hit", "block.shroomlight.hit");
						put("Shroomlight Place", "block.shroomlight.place");
						put("Shroomlight Step", "block.shroomlight.step");
						put("Soul Sand Break", "block.soul_sand.break");
						put("Soul Sand Fall", "block.soul_sand.fall");
						put("Soul Sand Hit", "block.soul_sand.hit");
						put("Soul Sand Place", "block.soul_sand.place");
						put("Soul Sand Step", "block.soul_sand.step");
						put("Soul Soil Break", "block.soul_soil.break");
						put("Soul Soil Fall", "block.soul_soil.fall");
						put("Soul Soil Hit", "block.soul_soil.hit");
						put("Soul Soil Place", "block.soul_soil.place");
						put("Soul Soil Step", "block.soul_soil.step");
						put("Chorus Flower Death", "block.chorus_flower.death");
						put("Chorus Flower Grow", "block.chorus_flower.grow");
						put("Add Cake Candle", "block.cake.add_candle");
						put("Candle Ambient", "block.candle.ambient");
						put("Candle Break", "block.candle.break");
						put("Candle Extinguish", "block.candle.extinguish");
						put("Candle Fall", "block.candle.fall");
						put("Candle Hit", "block.candle.hit");
						put("Candle Place", "block.candle.place");
						put("Candle Step", "block.candle.step");
						put("Chain Break", "block.chain.break");
						put("Chain Fall", "block.chain.fall");
						put("Chain Hit", "block.chain.hit");
						put("Chain Place", "block.chain.place");
						put("Chain Step", "block.chain.step");
						put("Copper Break", "block.copper.break");
						put("Copper Fall", "block.copper.fall");
						put("Copper Hit", "block.copper.hit");
						put("Copper Place", "block.copper.place");
						put("Copper Step", "block.copper.step");
						put("Deepslate Bricks Break", "block.deepslate_bricks.break");
						put("Deepslate Bricks Fall", "block.deepslate_bricks.fall");
						put("Deepslate Bricks Hit", "block.deepslate_bricks.hit");
						put("Deepslate Bricks Place", "block.deepslate_bricks.place");
						put("Deepslate Bricks Step", "block.deepslate_bricks.step");
						put("Deepslate Tile Break", "block.deepslate_tile.break");
						put("Deepslate Tile Fall", "block.deepslate_tile.fall");
						put("Deepslate Tile Hit", "block.deepslate_tile.hit");
						put("Deepslate Tile Place", "block.deepslate_tile.place");
						put("Deepslate Tile Step", "block.deepslate_tile.step");
						put("Glass Break", "block.glass.break");
						put("Glass Fall", "block.glass.fall");
						put("Glass Hit", "block.glass.hit");
						put("Glass Place", "block.glass.place");
						put("Glass Step", "block.glass.step");
						put("Honey Block Break", "block.honey_block.break");
						put("Honey Block Fall", "block.honey_block.fall");
						put("Honey Block Hit", "block.honey_block.hit");
						put("Honey Block Place", "block.honey_block.place");
						put("Honey Block Step", "block.honey_block.step");
						put("Honey Block Slide", "block.honey_block.slide");
						put("Ladder Break", "block.ladder.break");
						put("Ladder Fall", "block.ladder.fall");
						put("Ladder Hit", "block.ladder.hit");
						put("Ladder Place", "block.ladder.place");
						put("Ladder Step", "block.ladder.step");
						put("Lantern Break", "block.lantern.break");
						put("Lantern Fall", "block.lantern.fall");
						put("Lantern Hit", "block.lantern.hit");
						put("Lantern Place", "block.lantern.place");
						put("Lantern Step", "block.lantern.step");
						put("Metal Break", "block.metal.break");
						put("Metal Fall", "block.metal.fall");
						put("Metal Hit", "block.metal.hit");
						put("Metal Place", "block.metal.place");
						put("Metal Step", "block.metal.step");
						put("Netherite Block Break", "block.netherite_block.break");
						put("Netherite Block Fall", "block.netherite_block.fall");
						put("Netherite Block Hit", "block.netherite_block.hit");
						put("Netherite Block Place", "block.netherite_block.place");
						put("Netherite Block Step", "block.netherite_block.step");
						put("Nether Bricks Break", "block.nether_bricks.break");
						put("Nether Bricks Fall", "block.nether_bricks.fall");
						put("Nether Bricks Hit", "block.nether_bricks.hit");
						put("Nether Bricks Place", "block.nether_bricks.place");
						put("Nether Bricks Step", "block.nether_bricks.step");
						put("Polished Deepslate Break", "block.deepslate.break");
						put("Polished Deepslate Fall", "block.deepslate.fall");
						put("Polished Deepslate Hit", "block.deepslate.hit");
						put("Polished Deepslate Place", "block.deepslate.place");
						put("Polished Deepslate Step", "block.deepslate.step");
						put("Scaffolding Break", "block.scaffolding.break");
						put("Scaffolding Fall", "block.scaffolding.fall");
						put("Scaffolding Hit", "block.scaffolding.hit");
						put("Scaffolding Place", "block.scaffolding.place");
						put("Scaffolding Step", "block.scaffolding.step");
						put("Slime Block Break", "block.slime_block.break");
						put("Slime Block Fall", "block.slime_block.fall");
						put("Slime Block Hit", "block.slime_block.hit");
						put("Slime Block Place", "block.slime_block.place");
						put("Slime Block Step", "block.slime_block.step");
						put("Wool Break", "block.wool.break");
						put("Wool Fall", "block.wool.fall");
						put("Wool Hit", "block.wool.hit");
						put("Wool Place", "block.wool.place");
						put("Wool Step", "block.wool.step");
						put("Anvil Break", "block.anvil.break");
						put("Anvil Fall", "block.anvil.fall");
						put("Anvil Hit", "block.anvil.hit");
						put("Anvil Place", "block.anvil.place");
						put("Anvil Step", "block.anvil.step");
						put("Anvil Destroy", "block.anvil.destroy");
						put("Anvil Land", "block.anvil.land");
						put("Anvil Use", "block.anvil.use");
						put("Composter Empty", "block.composter.empty");
						put("Composter Fill Success", "block.composter.fill_success");
						put("Composter Fill", "block.composter.fill");
						put("Composter Ready", "block.composter.ready");
						put("Blast Furnace Fire Crackle", "block.blastfurnace.fire_crackle");
						put("Campfire Crackle", "block.campfire.crackle");
						put("Furnace Fire Crackle", "block.furnace.fire_crackle");
						put("Smoker Smoke", "block.smoker.smoke");
						put("Barrel Close", "block.barrel.close");
						put("Barrel Open", "block.barrel.open");
						put("Chest Close", "block.chest.close");
						put("Chest Locked", "block.chest.locked");
						put("Chest Open", "block.chest.open");
						put("Ender Chest Close", "block.ender_chest.close");
						put("Ender Chest Open", "block.ender_chest.open");
						put("Shulker Box Close", "block.shulker_box.close");
						put("Shulker Box Open", "block.shulker_box.open");
						put("Comparator Click", "block.comparator.click");
						put("Lever Click", "block.lever.click");
						put("Stone Button Click Off", "block.stone_button.click_off");
						put("Stone Button Click On", "block.stone_button.click_on");
						put("Wooden Button Click Off", "block.wooden_button.click_off");
						put("Wooden Button Click On", "block.wooden_button.click_on");
						put("Fence Gate Close", "block.fence_gate.close");
						put("Fence Gate Open", "block.fence_gate.open");
						put("Iron Door Close", "block.iron_door.close");
						put("Iron Door Open", "block.iron_door.open");
						put("Iron Trapdoor Close", "block.iron_trapdoor.close");
						put("Iron Trapdoor Open", "block.iron_trapdoor.open");
						put("Wooden Door Close", "block.wooden_door.close");
						put("Wooden Door Open", "block.wooden_door.open");
						put("Wooden Trapdoor Close", "block.wooden_trapdoor.close");
						put("Wooden Trapdoor Open", "block.wooden_trapdoor.open");
						put("Metal Pressure Plate Click Off", "block.metal_pressure_plate.click_off");
						put("Metal Pressure Plate Click On", "block.metal_pressure_plate.click_on");
						put("Stone Pressure Plate Click Off", "block.stone_pressure_plate.click_off");
						put("Stone Pressure Plate Click On", "block.stone_pressure_plate.click_on");
						put("Wooden Pressure Plate Click Off", "block.wooden_pressure_plate.click_off");
						put("Wooden Pressure Plate Click On", "block.wooden_pressure_plate.click_on");
						put("Sculk Sensor Break", "block.sculk_sensor.break");
						put("Sculk Sensor Fall", "block.sculk_sensor.fall");
						put("Sculk Sensor Hit", "block.sculk_sensor.hit");
						put("Sculk Sensor Place", "block.sculk_sensor.place");
						put("Sculk Sensor Step", "block.sculk_sensor.step");
						put("Sculk Sensor Click", "block.sculk_sensor.clicking");
						put("Sculk Sensor Click Stop", "block.sculk_sensor.clicking_stop");
						put("Tripwire Attach", "block.tripwire.attach");
						put("Tripwire Click Off", "block.tripwire.click_off");
						put("Tripwire Click On", "block.tripwire.click_on");
						put("Tripwire Detach", "block.tripwire.detach");
						put("Dispenser Dispense", "block.dispenser.dispense");
						put("Dispenser Fail", "block.dispenser.fail");
						put("Dispenser Launch", "block.dispenser.launch");
						put("Piston Retract", "block.piston.contract");
						put("Piston Extend", "block.piston.extend");
						put("Redstone Torch Burnout", "block.redstone_torch.burnout");
						put("Beacon Activate", "block.beacon.activate");
						put("Beacon Ambient", "block.beacon.ambient");
						put("Beacon Deactivate", "block.beacon.deactivate");
						put("Beacon Power Select", "block.beacon.power_select");
						put("Bell Resonate", "block.bell.resonate");
						put("Bell Use", "block.bell.use");
						put("Conduit Activate", "block.conduit.activate");
						put("Conduit Ambient (Short)", "block.conduit.ambient.short");
						put("Conduit Ambient", "block.conduit.ambient");
						put("Conduit Attack Target", "block.conduit.attack.target");
						put("Conduit Deactivate", "block.conduit.deactivate");
						put("Fire Ambient", "block.fire.ambient");
						put("Fire Extinguish", "block.fire.extinguish");
						put("Lodestone Break", "block.lodestone.break");
						put("Lodestone Fall", "block.lodestone.fall");
						put("Lodestone Hit", "block.lodestone.hit");
						put("Lodestone Place", "block.lodestone.place");
						put("Lodestone Step", "block.lodestone.step");
						put("Lodestone Lock Compass", "item.lodestone_compass.lock");
						put("End Gateway Spawn", "block.end_gateway.spawn");
						put("End Portal Fill Frame", "block.end_portal_frame.fill");
						put("End Portal Spawn", "block.end_portal.spawn");
						put("Nether Portal Ambient", "block.portal.ambient");
						put("Nether Portal Travel", "block.portal.travel");
						put("Nether Portal Trigger", "block.portal.trigger");
						put("Respawn Anchor Ambient", "block.respawn_anchor.ambient");
						put("Respawn Anchor Charge", "block.respawn_anchor.charge");
						put("Respawn Anchor Deplete", "block.respawn_anchor.deplete");
						put("Respawn Anchor Set Spawnpoint", "block.respawn_anchor.set_spawn");
						// End of Blocks, Ambience below
						put("Cave Ambience", "ambient.cave");
						put("Lightning Impact", "entity.lightning_bolt.impact");
						put("Lightning Thunder", "entity.lightning_bolt.thunder");
						put("Raid Horn", "event.raid.horn");
						put("Underwater Ambience", "ambient.underwater.loop");
						put("Underwater Ambience Additions", "ambient.underwater.loop.additions");
						put("Underwater Ambience Additions (Rare)", "ambient.underwater.loop.additions.rare");
						put("Underwater Ambience Additions (Ultra Rare)", "ambient.underwater.loop.additions.ultra_rare");
						put("Weather Rain", "weather.rain");
						put("Weather Rain Above", "weather.rain.above");
						// End of Ambience, Item Sounds below
						put("Player Attack (Critical)", "entity.player.attack.crit");
						put("Player Attack (Knockback)", "entity.player.attack.knockback");
						put("Player Attack (No Damage)", "entity.player.attack.nodamage");
						put("Player Attack (Strong)", "entity.player.attack.strong");
						put("Player Attack (Sweep", "entity.player.attack.sweep");
						put("Player Attack (Weak)", "entity.player.attack.weak");
						put("Shield Block", "item.shield.block");
						put("Shield Break", "item.shield.break");
						put("Crossbow Hit", "item.crossbow.hit");
						put("Crossbow Loading (End)", "item.crossbow.loading_end");
						put("Crossbow Loading (Middle)", "item.crossbow.loading_middle");
						put("Crossbow Loading (Start)", "item.crossbow.loading_start");
						put("Crossbow Quick Charge (I)", "item.crossbow.quick_charge_1");
						put("Crossbow Quick Charge (II)", "item.crossbow.quick_charge_2");
						put("Crossbow Quick Charge (III)", "item.crossbow.quick_charge_3");
						put("Crossbow Shoot", "item.crossbow.shoot");
						put("Horse Equip Saddle", "entity.horse.saddle");
						put("Horse Equip Armor", "entity.horse.armor");
						put("Armor Equip", "entity.horse.armor");
						put("Armor Equip (Elytra)", "item.armor.equip_elytra");
						put("Armor Equip (Leather)", "item.armor.equip_leather");
						put("Armor Equip (Chain)", "item.armor.equip_chain");
						put("Armor Equip (Iron)", "item.armor.equip_iron");
						put("Armor Equip (Gold)", "item.armor.equip_gold");
						put("Armor Equip (Diamond)", "item.armor.equip_diamond");
						put("Armor Equip (Netherite)", "item.armor.equip_netherite");
						put("Armor Equip (Turtle)", "item.armor.equip_turtle");
						put("Elytra Flying", "item.elytra.flying");
						put("Thorns Hit", "enchant.thorns.hit");
						put("Trident Throw", "item.trident.throw");
						put("Trident Hit", "item.trident.hit");
						put("Trident Hit Ground", "item.trident.hit_ground");
						put("Trident Return", "item.trident.return");
						put("Trident Riptide (I)", "item.trident.riptide_1");
						put("Trident Riptide (II)", "item.trident.riptide_2");
						put("Trident Riptide (III)", "item.trident.riptide_3");
						put("Trident Thunder", "item.trident.thunder");
						put("Fire Charge Use", "item.firecharge.use");
						put("Flint and Steel Use", "item.flintandsteel.use");
						put("Axe Scrape", "scrape");
						put("Axe Strip", "item.axe.strip");
						put("Wax Off", "item.axe.wax_off");
						put("Hoe Till", "item.hoe.till");
						put("Shovel Flatten", "item.shovel.flatten");
						put("Fishing Retrieve", "entity.fishing_bobber.retrieve");
						put("Fishing Splash", "entity.fishing_bobber.splash");
						put("Fishing Throw", "entity.fishing_bobber.throw");
						put("Bottle Empty", "item.bottle.empty");
						put("Bottle Fill", "item.bottle.fill");
						put("Dragon Breath Fill", "item.bottle.fill_dragonbreath");
						put("Honey Bottle Drink", "item.honey_bottle.drink");
						put("Water Bucket Empty", "item.bucket.empty");
						put("Water Bucket Fill", "item.bucket.fill");
						put("Axolotl Bucket Empty", "item.bucket.empty_fish");
						put("Axolotl Bucket Fill", "subtitles.item.bucket.fill_axolotl");
						put("Fish Bucket Empty", "item.bucket.empty_fish");
						put("Fish Bucket Fill", "item.bucket.fill_fish");
						put("Lava Bucket Empty", "item.bucket.empty_lava");
						put("Lava Bucket Fill", "item.bucket.fill_lava");
						put("Powder Bucket Empty", "item.bucket.empty_powder_snow");
						put("Powder Bucket Fill", "item.bucket.fill_powder_snow");
						put("Drink", "entity.generic.drink");
						put("Eat", "entity.generic.eat");
						put("Item Break", "entity.item.break");
						put("Item Pickup", "entity.item.pickup");
						put("Book Page Turn", "item.book.page_turn");
						put("Book Put in Lectern", "item.book.put");
						put("Bone Meal Use", "item.bone_meal.use");
						put("Chorus Fruit Teleport", "item.chorus_fruit.teleport");
						put("Dye Use", "item.dye.use");
						put("Ink Sac Use", "item.ink_sac.use");
						put("Glow Ink Sac Use", "item.minecraft.glow_ink_sac");
						put("Spyglass Use", "item.minecraft.spyglass");
						put("Spyglass Stop Using", "item.spyglass.stop_using");
						put("Bundle Drop Contents", "item.bundle.drop_contents");
						put("Bundle Remove One", "item.bundle.remove_one");
						put("Bundle Item Insert", "item.bundle.insert");
						put("Totem Use", "item.totem.use");
						put("Wax Copper", "item.honeycomb.wax_on");
				}};

		public static TreeMap<Integer, BossBar> setBossBar(Player barPlayer, String title, Integer id, double barHealth, BarColor color, BarStyle style, BarFlag[] flags, TreeMap<String, TreeMap<Integer, BossBar>> handler){
				BossBar bar = Bukkit.createBossBar(title, color, style);
				for(int i = 0; i < flags.length; i++){ bar.addFlag(flags[i]); }
				bar.setProgress(barHealth);
				TreeMap<Integer, BossBar> currentBars = new TreeMap<>();
				if(handler.containsKey(barPlayer.getName())) currentBars = handler.get(barPlayer.getName());
				else bar.addPlayer(barPlayer);


				int k = 0;
				Integer[] keys = currentBars.keySet().toArray(new Integer[0]);
				for(int i = currentBars.size() - 1; i >= 0; i--){ // Re-display the bars to adjust to correct order.
						currentBars.get(keys[i]).removePlayer(barPlayer);
						if(keys[i] == id) bar.addPlayer(barPlayer);
						else currentBars.get(keys[k]).addPlayer(barPlayer);
						k++;
				}

				currentBars.put(id, bar);
				return currentBars;
		}

		public static TreeMap<Integer, BossBar> removeBossbar(Player p, Integer id, TreeMap<String, TreeMap<Integer, BossBar>> handler){
				TreeMap<Integer, BossBar> bars = new TreeMap<>();
				if(handler.containsKey(p.getName())) bars = handler.get(p.getName());
				else return bars;

				int k = 0;
				Integer[] keys = bars.keySet().toArray(new Integer[0]);
				for(int i = bars.size() - 1; i >= 0; i--){ // Re-display the bars to adjust to correct order.
						bars.get(keys[i]).removePlayer(p);
						if(keys[k] != id && id != null) bars.get(keys[k]).addPlayer(p);
						k++;
				}
				if(id != null) bars.remove(id);
				else bars = new TreeMap<Integer, BossBar>();
				return bars;
		}

		public static void sendMessageSeq(Player player, String[] msgs, long delay, JavaPlugin plugin){
				new BukkitRunnable() {
						int index = 0;
						@Override
						public void run(){
								player.sendMessage(msgs[index++]);
								if(index >= msgs.length) this.cancel();
						}
				}.runTaskTimerAsynchronously(plugin, 0, delay);
		}

		public static void playSounds(Player player, String[] sounds, Location loc, float[][] soundInfo, long delay, JavaPlugin plugin){
				new BukkitRunnable(){
						int index = 0;
						@Override
						public void run(){
								Sound sound = Sound.valueOf(soundMap.get(sounds[index]).replace('.', '_').toUpperCase());
								player.playSound(loc, sound, soundInfo[index][0], soundInfo[index][1]);
								index++;
								if(index >= sounds.length) cancel();
						}
				}.runTaskTimer(plugin, 0, delay);
		}

		public static void sendHover(Player p, String msg, String hoverMsg){
				TextComponent component = new TextComponent(msg);
				component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverMsg)));
				p.spigot().sendMessage(component);
		}

		public static void stopSounds(Player p, String[] sounds, SoundCategory category){
				if(category == SoundCategory.MASTER && sounds.length == 0) {
						p.stopAllSounds();
						return;
				}

				for(int i = 0; i < sounds.length; i++){
						Sound sound = Sound.valueOf(soundMap.get(sounds[i]).replace('.', '_').toUpperCase());
						p.stopSound(sound, category);
				}
		}

//    public static void createManager(ItemStack[] icons, JSONMessage[] titles, AdvancementDisplay.AdvancementFrame[] frames, boolean save){
//        AdvancementManager manager = new AdvancementManager(new NameKey("DF", "DFManager"));
//        for(int i = 0; i < icons.length; i++){
//            AdvancementDisplay display = new AdvancementDisplay(icons[i], titles[i], new JSONMessage(new TextComponent("")), frames[i], AdvancementVisibility.ALWAYS);
//            int id = (int) Math.floor(Math.random() * 99999999);
//            Advancement adv = new Advancement(new NameKey("DF", "DF" + id), display);
//            adv.setCriteria(new Criteria(1));
//            manager.addAdvancement(adv);
//        }
//
//        Bukkit.getOnlinePlayers().forEach(manager::addPlayer);
//    }

		public static ItemStack parseItemNBT(String rawNBT){
				if(rawNBT == "null") return null;
				CompoundTag nbt = null;
				try{nbt = TagParser.parseTag(rawNBT);}
				catch(CommandSyntaxException e){}

				net.minecraft.world.item.ItemStack nmsItem = net.minecraft.world.item.ItemStack.of(nbt);
				return CraftItemStack.asBukkitCopy(nmsItem);
		}
		
		public static ItemStack[] parseItemNBTs (String[] NBTArray){
			 ItemStack[] result = new ItemStack[NBTArray.length];
			 for(int i = 0; i < NBTArray.length; i++) result[i] = (parseItemNBT(NBTArray[i]));
			 return result;
		}
		
		public static Material[] getStackTypes(ItemStack[] items){
			Material[] result = new Material[items.length];
			for(int i = 0; i < items.length; i++) result[i] = items[i].getType();
			return result;
		}

		public static void giveItems(Player p, String[] items, byte stack) {
				for(int i = 0; i < items.length; i++){
						for(int k = 0; k < stack; k++){
								p.getInventory().addItem(parseItemNBT(items[i]));
						}
				}
		}

		public static void setItems(Player p, HashMap<Integer, String> items){
						items.forEach((slot, item) -> {
								p.getInventory().setItem(slot, parseItemNBT(item));
						});
		}

		public static void replaceItems(Player p, String[] replaceablesRaw, ItemStack replaceItem, short amount){
				ItemStack[] items = p.getInventory().getContents();
				ItemStack[] replaceables = new ItemStack[replaceablesRaw.length];
				ItemStack removalItem;

				for(int i = 0; i < replaceablesRaw.length; i++){
						replaceables[i] = parseItemNBT(replaceablesRaw[i]);
				}

				short itemsReplaced = 0;
				for(int i = 0; i < items.length; i++){
						for(int k = 0; k < replaceables.length; k++){
								if(items[i].isSimilar(replaceables[k])){
										byte stack = (byte) Math.floor(items[i].getAmount()/replaceables[k].getAmount());
										if(stack > (amount - itemsReplaced)) stack = (byte) (amount - itemsReplaced);
										if(stack > 0){
												itemsReplaced += stack;
												replaceItem.setAmount(replaceItem.getAmount() * stack);
												removalItem = items[i].clone();
												removalItem.setAmount(replaceables[k].getAmount() * stack);
												p.getInventory().addItem(replaceItem);
												p.getInventory().removeItem(removalItem);
												if(itemsReplaced >= amount) return;
										}
										break;
								}
						}

				}
		}

		public static void removeItems(Player p, String[] items){
				for(int i = 0; i < items.length; i++){
						p.getInventory().removeItem(parseItemNBT(items[i]));
				}
		}

		public static void clearItems(Player p, String[] items){
				ItemStack[] invContents = p.getInventory().getContents();
				for(int i = 0; i < invContents.length; i++){
						for(int j = 0; j < items.length; j++){
								if(invContents[i].isSimilar(parseItemNBT(items[j]))) p.getInventory().clear(i);
						}
				}
		}

		public static void clearInv(Player p, int min, int max, boolean clearCrafting){
				for(int i = min; i < max; i++){
						p.getInventory().clear(i);
				}
				if(clearCrafting){
						p.setItemOnCursor(null);
					  Inventory topInv = p.getOpenInventory().getTopInventory();
						if(topInv.getType() == InventoryType.CRAFTING)
							for(int i = 1; i <= 4; i++) topInv.setItem(i, null);
				}
		}

		public static void saveInv(Player p){
				ItemStack[] inv = p.getInventory().getContents();
				String[] result = new String[inv.length];
				for(int i = 0; i < inv.length; i++){
					 if(inv[i] == null){
							result[i] = "null";
							continue;
					 }
					 
					 if(!CraftItemStack.asNMSCopy(inv[i]).hasTag()){
							result[i] = "{Count:" + inv[i].getAmount() + "b,id:\"minecraft:" + inv[i].getType().toString().toLowerCase() + "\"}";
							continue;
					 }
					 result[i] = formatCompoundTags(inv[i], CraftItemStack.asNMSCopy(inv[i]).getTag().toString());
				}
				p.sendMessage(String.join("|", result));
				managerClass.getConfig().set("players." + p.getUniqueId() + ".inventory", String.join("|", result));
				managerClass.saveConfig();
		}
		
		public static void loadInv(Player p){
				if(!managerClass.getConfig().contains("players." + p.getUniqueId() + ".inventory")) return;
				String[] inv = managerClass.getConfig().getString("players." + p.getUniqueId() + ".inventory").split("\\|");
				for(int i = 0; i < inv.length; i++){
						if(inv[i] != null) p.getInventory().setItem(i, parseItemNBT(inv[i]));
						else p.getInventory().setItem(i, null);
				}
		}
		
		public static String formatCompoundTags(ItemStack item, String tags){
				String result = "{Count:" + item.getAmount() + "b, id:\"minecraft:" + item.getType().toString().toLowerCase() + "\",";
				result += "tag:{" + tags.substring(1, tags.length() - 1) + "}}"; // Remove opening and ending brackets from the CompoundTag, then add closing bracket of main nbt.
				return result;
		}
		
		public static Inventory createInventory(Player p, TreeMap<Integer, String> items){
				Inventory inv = Bukkit.createInventory(p, 27, "Menu");
				Integer[] keys = items.keySet().toArray(new Integer[0]);
				TreeMap<Integer, String> test = new TreeMap<Integer, String>(){{put(1, "test");}};
				
				for(int i = 0; i < keys.length; i++){
						inv.setItem(keys[i], parseItemNBT(items.get(keys[i])));
				}
				return inv;
		}
		
		public static void openInv(Player p, TreeMap<Integer, String> items){
			 p.openInventory(createInventory(p, items));
		}
		
		public static void expandInv(Player p, TreeMap<Integer, String> items){
				if(p.getOpenInventory().getType() == InventoryType.PLAYER) return; // Cannot expand player inventory!
				ItemStack[] invItems = (ItemStack[]) ArrayUtils.addAll(p.getOpenInventory().getTopInventory().getContents(), createInventory(p, items).getContents());
				byte length = (byte) Math.min(invItems.length, 54);
				Inventory newInv = Bukkit.createInventory(p, length, p.getOpenInventory().getTitle());
				for(int i = 0; i < length; i++){
						newInv.setItem(i, invItems[i]);
				}
				p.openInventory(newInv);
		}
		
		public static void setMenuItem(Player p, int slot, String item){
				if(p.getOpenInventory().getType() == InventoryType.PLAYER) return; // Don't set player inventory slots with this, use SetSlotItem instead!
				p.getOpenInventory().getTopInventory().setItem(slot, parseItemNBT(item));
		}
		
		public static void setInvName(Player p, String name){
				if(p.getOpenInventory().getType() == InventoryType.PLAYER) return;
				ItemStack[] currentInvItems = p.getOpenInventory().getTopInventory().getContents();
				Inventory newInv = Bukkit.createInventory(p, currentInvItems.length, name);
				newInv.setContents(currentInvItems);
				p.openInventory(newInv);
		}
		
		public static void removeInvRow(Player p, Integer rows){
				if(!inCustomInv(p)) return;
				
				InventoryView inv = p.getOpenInventory();
				List<ItemStack> invItems = Arrays.asList(inv.getTopInventory().getContents());
				Integer invSize = invItems.size() - rows * 9;
				if(invSize < 9) return;
				Inventory newInv = Bukkit.createInventory(p, invSize, inv.getTitle());
				newInv.setContents(invItems.subList(0, invSize).toArray(new ItemStack[0]));
				p.openInventory(newInv);
		}
		
		public static void openContainerInv(Player p, Location loc){
			  if(loc == null) return;
				Block block = p.getWorld().getBlockAt(loc);
				switch(block.getType()){
					case CRAFTING_TABLE:
						p.openWorkbench(loc, true);
						break;
					case ENCHANTING_TABLE:
						p.openEnchanting(loc, true);
						break;
					case OAK_SIGN:
					case OAK_WALL_SIGN:
					case SPRUCE_SIGN:
					case SPRUCE_WALL_SIGN:
					case BIRCH_SIGN:
					case BIRCH_WALL_SIGN:
					case DARK_OAK_SIGN:
					case DARK_OAK_WALL_SIGN:
					case ACACIA_SIGN:
					case ACACIA_WALL_SIGN:
					case JUNGLE_SIGN:
					case JUNGLE_WALL_SIGN:
					case CRIMSON_SIGN:
					case CRIMSON_WALL_SIGN:
					case WARPED_SIGN:
					case WARPED_WALL_SIGN:
						p.openSign((Sign) block);
						break;
					case GRINDSTONE:
						p.openInventory(Bukkit.createInventory(p, InventoryType.GRINDSTONE));
						break;
					case ENDER_CHEST:
						p.openInventory(p.getEnderChest());
						break;
					case BEACON:
						p.openInventory(Bukkit.createInventory(p, InventoryType.BEACON));
						break;
					case CARTOGRAPHY_TABLE:
						p.openInventory(Bukkit.createInventory(p, InventoryType.CARTOGRAPHY));
						break;
					case SMITHING_TABLE:
						p.openInventory(Bukkit.createInventory(p, InventoryType.SMITHING));
						break;
					case ANVIL:
					case CHIPPED_ANVIL:
					case DAMAGED_ANVIL:
						p.openInventory(Bukkit.createInventory(p, InventoryType.ANVIL));
						//TODO: Using this menu will not damage the actual anvil at the location! Figure out how to use HumanEntity#openAnvil() instead!
						break;
					default:
						Inventory inv = ((InventoryHolder) block.getState()).getInventory();
						p.openInventory(inv);
						break;
				}
		}
		
		public static void removePotions(Player p, PotionEffect[] effects){
			 for(PotionEffect effect : effects) p.removePotionEffect(effect.getType());
		}
		
		public static Player playerFromName(String name){
			return Bukkit.getPlayer(name) == null ? Bukkit.getPlayer(UUID.fromString(name)) : Bukkit.getPlayer(name);
		}
		
		public static boolean inCustomInv(Player p){
				Inventory inv = p.getOpenInventory().getTopInventory();
				return inv.getType() != InventoryType.PLAYER
					&& inv.getType() != InventoryType.CRAFTING
					&& inv.getLocation() == null;
		}
		
		@EventHandler
		public void ClickMenuSlot(InventoryClickEvent event){
				if(inCustomInv((Player) event.getView().getPlayer())) event.setCancelled(true); // ClickSlot event triggered from inside custom GUI
		}
		
		@EventHandler
		public void PlayerDmgPlayer(EntityDamageByEntityEvent event){
			if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
				if(!PlayerData.getPlayerData(((Player) event.getEntity()).getUniqueId()).canPvP)
					event.setCancelled(true);
			}
		}
		
		@EventHandler
		public void PlaceBlock(BlockPlaceEvent event){
			 PlayerData playerData = PlayerData.getPlayerData(event.getPlayer().getUniqueId());
			 if(!playerData.allowedBlocks.contains(event.getBlockPlaced().getType())) event.setCancelled(true);
		}
		
		@EventHandler
		public void BreakBlock(BlockBreakEvent event){
			 PlayerData playerData = PlayerData.getPlayerData(event.getPlayer().getUniqueId());
			 if(!playerData.allowedBlocks.contains(event.getBlock().getType())) event.setCancelled(true);
		}
		
		@EventHandler
		public void Death(PlayerDeathEvent event){
			PlayerData playerData = PlayerData.getPlayerData(((Player) event.getEntity()).getUniqueId());
			if(!playerData.deathDrops) event.getDrops().clear();
			if(playerData.keepInv) event.setKeepInventory(true);
			if(playerData.instantRespawn) ((Player) event.getEntity()).spigot().respawn();
		}
		
		public static void getManager(JavaPlugin plugin){
				managerClass = new FileManager(plugin, "playerData.yml");
		}
}
