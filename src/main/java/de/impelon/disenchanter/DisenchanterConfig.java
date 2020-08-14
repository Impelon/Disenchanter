package de.impelon.disenchanter;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = DisenchanterMain.MODID, category = "")
public class DisenchanterConfig {

	@Config.RequiresMcRestart
	@Config.Name("general")
	public static final GeneralSection general = new GeneralSection();
	
	public static class GeneralSection {
		
		@Config.Name("CheckVersion")
		@Config.Comment("Should Disenchanter check for new versions on startup?")
		public boolean shouldCheckVersion = true;		
		
		@Config.Name("EnableTabletRecipe")
		@Config.Comment("Should the recipe for the experience-tablet be available?")
		public boolean enableTabletRecipe = true;
		
		@Config.Name("EnableAutomaticRecipe")
		@Config.Comment("Should the recipe for the automatic-upgrade be available?")
		public boolean enableAutomaticRecipe = true;
		
		@Config.Name("EnableVoidingRecipe")
		@Config.Comment("Should the recipe for the voiding-upgrade be available?")
		public boolean enableVoidingRecipe = true;
		
		@Config.Name("EnableBulkDisenchantingRecipe")
		@Config.Comment("Should the recipe for the bulk-disenchanting-upgrade be available?")
		public boolean enableBulkDisenchantingRecipe = true;
		
		@Config.Name("EnableClearRecipe")
		@Config.Comment("Should the recipe for clearing all upgrades be available?")
		public boolean enableClearRecipe = true;
		
	};
	
	@Config.Name("disenchanting")
	public static final DisenchantingSection disenchanting = new DisenchantingSection();
	
	public static class DisenchantingSection {
		
		@Config.Name("FlatDamage")
		@Config.Comment("How much flat damage should be dealt to items when disenchanting?")
		public int flatDamage = 10;
		
		@Config.Name("MaxDurabilityDamage")
		@Config.Comment("How much of the item's maximum durability should be dealt as damage to items when disenchanting?")
		public double maxDurabilityDamage = 0.025;
		
		@Config.Name("MaxDurabilityDamageReduceable")
		@Config.Comment({"How much of the item's maximum durability should be dealt as reduceable damage to items when disenchanting?",
						 "This can be reduced by surrounding the disenchantment table with blocks that increase the enchanting level for the enchanting table (e.g. bookshelves)."})
		public double maxDurabilityDamageReduceable = 0.2;
		
		@Config.Name("MachineDamageMultiplier")
		@Config.Comment("By how much should the damage be multiplied when using an automatic disenchantment table?")
		public double machineDamageMultiplier = 2.5;
		
		@Config.Name("EnchantmentLossChance")
		@Config.Comment("What should the probability be of additional enchantments being lost when disenchanting?")
		@Config.RangeDouble(min = 0.0, max = 1.0)
		public double enchantmentLossChance = 0.0;
		
		@Config.Name("AutomaticDisenchantmentProcessTicks")
		@Config.Comment("How many ticks should the disenchanting process last when using an automatic disenchantment table?")
		@Config.RangeInt(min = 0)
		public int ticksAutomaticDisenchantmentProcess = 100;
		
		@Config.Name("DisabledItems")
		@Config.Comment({"Which items should not be disenchantable?",
						 "Entries are of the format `modid:itemid`; for example minecraft:dirt",
						 "Java Regex can be used with a [r]-prefix; for example [r]minecraft:.* to ban all vanilla items."})
		public String[] disabledItems = {};
		
		@Config.Name("DisabledEnchantments")
		@Config.Comment({"Which enchantments should be ignored when disenchanting?",
						 "Entries are of the format `modid:enchantid`; for example minecraft:bane_of_arthropods",
						 "Java Regex can be used with a [r]-prefix; for example [r]minecraft:.* to ban all vanilla enchantments."})
		public String[] disabledEnchantments = {};

		@Config.Name("EnableTCBehaviour")
		@Config.Comment({"Should items from Tinkers Construct be handeled differently?",
						 "Enchantments will not be able to be removed from these items."})
		public boolean enableTCBehaviour = true;
		
	};
	
	@Config.Name("visual")
	public static final VisualSection visual = new VisualSection();
	
	public static class VisualSection {
		
		@Config.Name("BookRendererYOffset")
		@Config.Comment({"How should the book be positioned above the disenchantment table compared to the regular enchanting table?",
						 "0.0 is the same as the enchanting table."})
		public double bookRendererYOffset = 0.4;
		
		@Config.Name("BookRendererFlipped")
		@Config.Comment("Should the book above the disenchantment table be flipped upside-down?")
		public boolean bookRendererFlipped = true;
		
	};
	
	@Mod.EventBusSubscriber
	private static class EventHandler {

		/**
		 * Inject the new values and save to the config file when the config has been changed from the GUI.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(DisenchanterMain.MODID)) {
				ConfigManager.sync(DisenchanterMain.MODID, Config.Type.INSTANCE);
			}
		}
	}

}
