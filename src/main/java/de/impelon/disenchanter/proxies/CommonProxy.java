package de.impelon.disenchanter.proxies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import de.impelon.disenchanter.blocks.BlockDisenchantmentTable;
import de.impelon.disenchanter.blocks.ItemBlockDisenchantment;
import de.impelon.disenchanter.blocks.TileEntityDisenchantmentTable;
import de.impelon.disenchanter.blocks.TileEntityDisenchantmentTableAutomatic;
import de.impelon.disenchanter.crafting.UpgradeTableRecipe;
import de.impelon.disenchanter.gui.GUIHandler;
import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;


public class CommonProxy {
	
	public static final BlockDisenchantmentTable disenchantmentTable = new BlockDisenchantmentTable();
	public static final ItemBlockDisenchantment itemDisenchantmentTable = new ItemBlockDisenchantment();
	
	private static final ResourceLocation disenchantmentTableUseLocation = new ResourceLocation(DisenchanterMain.MODID, "block.disenchantment_table.use");
	public static final SoundEvent disenchantmentTableUse = new SoundEvent(disenchantmentTableUseLocation).setRegistryName(disenchantmentTableUseLocation);
		
	public void preInit(FMLPreInitializationEvent ev) {
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		DisenchanterMain.config = config;
		config.load();
		
		config.get("general", "CheckVersion", true, "Should Disenchanter check for new versions on startup?");
		config.get("general", "EnableAutomaticRecipe", true, "Should the recipe for the automatic-upgrade be avalible?");
		config.get("general", "EnableVoidingRecipe", true, "Should the recipe for the voiding-upgrade be avalible?");
		config.get("general", "EnableBulkDisenchantingRecipe", true, "Should the recipe for the bulk-disenchanting-upgrade be avalible?");
		config.get("general", "EnableClearRecipe", true, "Should the recipe for clearing all upgrades be avalible?");
		config.get("disenchanting", "FlatDamage", 10, "How much flat damage should be dealt to items when disenchanting?");
		config.get("disenchanting", "MaxDurabilityDamage", 0.025, "How much of the item's maximal durability should be dealt as damage to items when disenchanting?");
		config.get("disenchanting", "MaxDurabilityDamageReduceable", 0.2, "How much of the item's maximal durability should be dealt as reduceable damage to items when disenchanting?");
		config.get("disenchanting", "MachineDamageMultiplier", 2.5, "By how much should the dammage on the item be multiplied when using an automaic Disenchantment Table?");
		config.get("disenchanting", "EnchantmentLossChance", 0.0, "What should the probability be of additional enchantments being lost from items when disenchanting?");
		config.get("disenchanting", "AutomaticDisenchantmentProcessTicks", 100, "How many ticks should a disenchantment process last when using an automaic Disenchantment Table?");
		config.get("disenchanting", "DisabeledItems", new String[]{"minecraft:dirt"}, "Which items should not be disenchantable? (modid:itemid)");
		config.get("disenchanting", "DisabeledEnchantments", new String[]{}, "Which enchantments should be ignored when disenchanting? (modid:enchantid)");

		config.save();
		
		GameRegistry.registerTileEntity(TileEntityDisenchantmentTable.class, "TileDisentchantmentTable");
		GameRegistry.registerTileEntity(TileEntityDisenchantmentTableAutomatic.class, "TileDisentchantmentTableAutomatic");
	}
	
	public void load(FMLInitializationEvent ev) {			
		NetworkRegistry.INSTANCE.registerGuiHandler(DisenchanterMain.instance, new GUIHandler());
	}
	
	public void postInit(FMLPostInitializationEvent ev) {}
	
	public void registerBlocks(RegistryEvent.Register<Block> ev) {
		ev.getRegistry().register(disenchantmentTable);
	}
	
	public void registerItems(RegistryEvent.Register<Item> ev) {
		ev.getRegistry().register(itemDisenchantmentTable.setRegistryName(disenchantmentTable.getRegistryName()));
	}
	
	public void registerSoundEvents(RegistryEvent.Register<SoundEvent> ev) {
		ev.getRegistry().register(disenchantmentTableUse);
	}
	
	public void registerRecipes(RegistryEvent.Register<IRecipe> ev) {
		
		ItemStack table = new ItemStack(Item.getItemFromBlock(disenchantmentTable), 1, OreDictionary.WILDCARD_VALUE);
		
		//TODO: Switch to JSON for ShapedOreRecipe first, UpgradeTableRecipe later
		
		ev.getRegistry().register(new ShapedOreRecipe(null, new ItemStack(itemDisenchantmentTable, 1, 0),
					"   ",
					"YEY",
					"ETE",
					Character.valueOf('T'), Blocks.ENCHANTING_TABLE,
					Character.valueOf('E'), "gemEmerald",
					Character.valueOf('Y'), "dyeYellow"
		).setRegistryName(itemDisenchantmentTable.getRegistryName()));
		
		if (DisenchanterMain.config.get("general", "EnableAutomaticRecipe", true).getBoolean())
			ev.getRegistry().register(new UpgradeTableRecipe(BlockDisenchantmentTable.AUTOMATIC, new ItemStack(itemDisenchantmentTable, 1, 1),
					"IMI",
					"BCB",
					"ITI",
					Character.valueOf('T'), table,
					Character.valueOf('I'), "ingotIron",
					Character.valueOf('C'), Blocks.CHEST,
					Character.valueOf('B'), "dyeBlack",
					Character.valueOf('M'), Items.COMPARATOR
			).setRegistryName(DisenchanterMain.MODID, "upgradetable_automatic"));
		
		if (DisenchanterMain.config.get("general", "EnableBulkDisenchantingRecipe", true).getBoolean())
			ev.getRegistry().register(new UpgradeTableRecipe(BlockDisenchantmentTable.BULKDISENCHANTING, new ItemStack(itemDisenchantmentTable, 1, 2),
					"QGQ",
					"GDG",
					"QTQ",
					Character.valueOf('T'), table,
					Character.valueOf('D'), "gemDiamond",
					Character.valueOf('Q'), "gemQuartz",
					Character.valueOf('G'), "ingotGold"
			).setRegistryName(DisenchanterMain.MODID, "upgradetable_bulkdisenchanting"));
		
		if (DisenchanterMain.config.get("general", "EnableVoidingRecipe", true).getBoolean())
			ev.getRegistry().register(new UpgradeTableRecipe(BlockDisenchantmentTable.VOIDING, new ItemStack(itemDisenchantmentTable, 1, 4),
					"POP",
					"EHE",
					"PTP",
					Character.valueOf('T'), table,
					Character.valueOf('E'), Items.ENDER_PEARL,
					Character.valueOf('H'), Blocks.HOPPER,
					Character.valueOf('P'), "dyePurple",
					Character.valueOf('O'), Blocks.OBSIDIAN
			).setRegistryName(DisenchanterMain.MODID, "upgradetable_voiding"));
		
		if (DisenchanterMain.config.get("general", "EnableClearRecipe", true).getBoolean())
			ev.getRegistry().register(new ShapedOreRecipe(null, new ItemStack(itemDisenchantmentTable, 1, 0),
					"PPP",
					"PTP",
					"PPP",
					Character.valueOf('T'), table,
					Character.valueOf('P'), Items.PAPER
			).setRegistryName(DisenchanterMain.MODID, "clearupgrades"));
	}
	
}
