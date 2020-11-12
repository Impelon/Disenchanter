package de.impelon.disenchanter;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = DisenchanterMain.MODID, category = "")
public class DisenchanterConfig {

	@Config.Name("general")
	public static final GeneralSection general = new GeneralSection();
	
	public static class GeneralSection {
		
		@Config.RequiresMcRestart
		@Config.Name("CheckVersion")
		@Config.Comment("Should Disenchanter check for new versions on startup?")
		public boolean shouldCheckVersion = true;
		
	};
	
	@Config.Name("experience_jar")
	public static final ExperienceJarSection experienceJar = new ExperienceJarSection();
	
	public static class ExperienceJarSection {
		
		@Config.Name("JarDefaultExperienceCapacity")
		@Config.Comment("How many experience points should the jar of experience store at most by default?")
		@Config.RangeInt(min = 0)
		public int jarDefaultExperienceCapacity = 1024;
		
		@Config.Name("JarUpgradeCapacityChange")
		@Config.Comment("By how many experience points should the capacity be changed when upgrading a jar of experience?")
		public int jarUpgradeCapacityChange = 512;
		
		@Config.Name("JarUpgradeMaxCapacity")
		@Config.Comment("What should be the maximal capacity of experience points optainable via upgrades be for the jar of experience?")
		public int jarUpgradeMaxCapacity = 2048;
		
	};
	
	@Config.RequiresMcRestart
	@Config.Name("crafting")
	public static final CraftingSection crafting = new CraftingSection();
	
	public static class CraftingSection {
		
		@Config.Name("EnableDisenchantmentTableRecipe")
		@Config.Comment("Should the recipe for the normal disenchantment table be available?")
		public boolean enableDisenchantmentTableRecipe = true;

		@Config.Name("EnableAutomaticTableUpgradeRecipe")
		@Config.Comment("Should the recipe for the automatic-upgrade (disenchantment table) be available?")
		public boolean enableAutomaticTableUpgradeRecipe = true;
		
		@Config.Name("EnableVoidingTableUpgradeRecipe")
		@Config.Comment("Should the recipe for the voiding-upgrade (disenchantment table) be available?")
		public boolean enableVoidingTableUpgradeRecipe = true;
		
		@Config.Name("EnableBulkDisenchantingTableUpgradeRecipe")
		@Config.Comment("Should the recipe for the bulk-disenchanting-upgrade (disenchantment table) be available?")
		public boolean enableBulkDisenchantingTableUpgradeRecipe = true;
		
		@Config.Name("EnableClearTableRecipe")
		@Config.Comment("Should the recipe for clearing all upgrades from a disenchantment table be available?")
		public boolean enableClearTableRecipe = true;
		
		@Config.Name("EnableJarRecipe")
		@Config.Comment("Should the recipe for the jar of experience be available?")
		public boolean enableJarRecipe = true;
		
		@Config.Name("EnableOverloadJarUpgradeRecipe")
		@Config.Comment("Should the recipe for the overload-upgrade (jar of experience) be available?")
		public boolean enableOverloadJarUpgradeRecipe = true;
		
		@Config.Name("EnableCapacityJarUpgradeRecipe")
		@Config.Comment("Should the recipe for upgrading the capacity (jar of experience) be available?")
		public boolean enableCapacityJarUpgradeRecipe = true;
		
		@Config.Name("EnableClearJarRecipe")
		@Config.Comment("Should the recipe for clearing all upgrades from a jar of experience be available?")
		public boolean enableClearJarRecipe = true;
		
	}
	
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
		@Config.Comment("What should the probability be that an enchantment is lost when disenchanting?")
		@Config.RangeDouble(min = 0.0, max = 1.0)
		public double enchantmentLossChance = 0.0;
		
		@Config.Name("RepairCostMultiplier")
		@Config.Comment({"By how much should the repair cost of the item (f.e. used by anvils) be multiplied when disenchanting?",
						 "0.5 is halving the repair cost (using an anvil doubles the repair cost); 1.0 leaves the cost unchanged."})
		@Config.RangeDouble(min = 0.0)
		public double repairCostMultiplier = 0.5;
		
		@Config.Name("FlatExperience")
		@Config.Comment({"When converting an enchantment to XP, a flat amount of experience points will be added.",
						"What should that amount be?"})
		public int flatExperience = 0;

		@Config.Name("MinEnchantabilityExperience")
		@Config.Comment({"When converting an enchantment to XP, experience points relative to the enchantment's minimum enchantability will be added.",
						 "What percentage of the enchantability should be used?"})
		public double minEnchantabilityExperience = 0.33;
		
		@Config.Name("MaxEnchantabilityExperience")
		@Config.Comment({"When converting an enchantment to XP, experience points relative to the enchantment's maximum enchantability will be added.",
						 "What percentage of the enchantability should be used?"})
		public double maxEnchantabilityExperience = 0.15;
		
		@Config.Name("AutomaticDisenchantingProcessTicks")
		@Config.Comment("How many ticks should the disenchanting process last when using an automatic disenchantment table?")
		@Config.RangeInt(min = 0)
		public int ticksAutomaticDisenchantingProcess = 100;
		
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
		
		@Config.Name("DisableCurses")
		@Config.Comment("Should curse-enchantments -like curse of vanishing- be ignored when disenchanting?")
		public boolean disableCurses = false;

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
		
		@Config.Name("BookRendererHidden")
		@Config.Comment("Should the book above the disenchantment table be completely hidden?")
		public boolean bookRendererHidden = false;
		
		@Config.Name("ShowUpgradesInGUI")
		@Config.Comment("Should the different upgrades of a disenchantment table be listed when viewing the GUI of said table?")
		public boolean showUpgradesInGUI = true;
		
		@Config.Name("DescriptionInGUIColor")
		@Config.Comment("What color should be used for additional descriptions (f.e. list of table upgrades) shown in the GUI of a disenchantment table?")
		public int descriptionInGUIColor = 11184810;
		
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
