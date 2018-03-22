package de.impelon.disenchanter.proxies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.registry.GameRegistry;
import de.impelon.disenchanter.blocks.BlockDisenchantmentTable;
import de.impelon.disenchanter.blocks.ItemBlockDisenchantment;
import de.impelon.disenchanter.blocks.TileEntityDisenchantmentTable;
import de.impelon.disenchanter.blocks.TileEntityDisenchantmentTableAutomatic;
import de.impelon.disenchanter.blocks.UpgradeTableRecipe;
import de.impelon.disenchanter.gui.GUIHandler;
import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;


public class CommonProxy {
	
	public static final BlockDisenchantmentTable disenchantmentTable = new BlockDisenchantmentTable();
	public static final ItemBlockDisenchantment itemDisenchantmentTable = new ItemBlockDisenchantment();
	public static SoundEvent disenchantmentTableUse;
		
	public void preInit(FMLPreInitializationEvent ev) {
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		DisenchanterMain.config = config;
		config.load();
		
		config.get("general", "CheckVersion", true, "Should Disenchanter check for new versions on startup?");
		config.get("general", "EnableAutomaticRecipe", true, "Should the recipe for the automatic-upgrade be avalible?");
		config.get("general", "EnableVoidingRecipe", true, "Should the recipe for the voiding-upgrade be avalible?");
		config.get("general", "EnableBulkDisenchantingRecipe", true, "Should the recipe for the bulk-disenchanting-upgrade be avalible?");
		config.get("general", "EnableClearRecipe", true, "Should the recipe for clearing all upgrades be avalible?");
		config.get("disenchanting", "FlatDamage", 10, "How much flat damage should be dealt to Items when disenchanting?");
		config.get("disenchanting", "MaxDurabilityDamage", 0.025, "How much of the Item's maximal durability should be dealt as damage to Items when disenchanting?");
		config.get("disenchanting", "MaxDurabilityDamageReduceable", 0.2, "How much of the Item's maximal durability should be dealt as reduceable damage to Items when disenchanting?");
		config.get("disenchanting", "MachineDamageMultiplier", 2.5, "By how much should the dammage on the item be multiplied when using an automaic Disenchantment Table?");
		config.get("disenchanting", "EnchantmentLossChance", 0.0, "What should the probability be of additional enchantments being lost from Items when disenchanting?");
		config.get("disenchanting", "EnableTCBehaviour", true, "Should Items from Tinkers Construct be handeled differently? (banned / modifiers removed [in TC2])");
		config.get("disenchanting", "AutomaticDisenchantmentProcessTicks", 100, "How many ticks should a disenchantment process last when using an automaic Disenchantment Table?");

		config.save();
		
		GameRegistry.registerTileEntity(TileEntityDisenchantmentTable.class, "TileDisentchantmentTable");
		GameRegistry.registerTileEntity(TileEntityDisenchantmentTableAutomatic.class, "TileDisentchantmentTableAutomatic");
		
		GameRegistry.register(disenchantmentTable);
		GameRegistry.register(itemDisenchantmentTable, disenchantmentTable.getRegistryName());
		
		ResourceLocation soundLocation = new ResourceLocation(DisenchanterMain.MODID, "block.disenchantment_table.use");
		disenchantmentTableUse = GameRegistry.register(new SoundEvent(soundLocation).setRegistryName(soundLocation));
	}
	
	public void load(FMLInitializationEvent ev) {	
		if (DisenchanterMain.config.get("general", "CheckVersion", true).getBoolean())
			MinecraftForge.EVENT_BUS.register(DisenchanterMain.versionChecker);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(DisenchanterMain.instance, new GUIHandler());
			
		ItemStack table = new ItemStack(Item.getItemFromBlock(disenchantmentTable), 1, OreDictionary.WILDCARD_VALUE);
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(disenchantmentTable), 1, 0),
					"   ",
					"YEY",
					"ETE",
					Character.valueOf('T'), Blocks.ENCHANTING_TABLE,
					Character.valueOf('E'), "gemEmerald",
					Character.valueOf('Y'), "dyeYellow"
		));
		
		if (DisenchanterMain.config.get("general", "EnableAutomaticRecipe", true).getBoolean())
			GameRegistry.addRecipe(new UpgradeTableRecipe(disenchantmentTable.AUTOMATIC, new ItemStack(Item.getItemFromBlock(disenchantmentTable), 1, 1),
					"IMI",
					"BCB",
					"ITI",
					Character.valueOf('T'), table,
					Character.valueOf('I'), "ingotIron",
					Character.valueOf('C'), Blocks.CHEST,
					Character.valueOf('B'), "dyeBlack",
					Character.valueOf('M'), Items.COMPARATOR
		));
		
		if (DisenchanterMain.config.get("general", "EnableBulkDisenchantingRecipe", true).getBoolean())
			GameRegistry.addRecipe(new UpgradeTableRecipe(disenchantmentTable.BULKDISENCHANTING, new ItemStack(Item.getItemFromBlock(disenchantmentTable), 1, 2),
					"QGQ",
					"GDG",
					"QTQ",
					Character.valueOf('T'), table,
					Character.valueOf('D'), "gemDiamond",
					Character.valueOf('Q'), "gemQuartz",
					Character.valueOf('G'), "ingotGold"
		));
		
		if (DisenchanterMain.config.get("general", "EnableVoidingRecipe", true).getBoolean())
			GameRegistry.addRecipe(new UpgradeTableRecipe(disenchantmentTable.VOIDING, new ItemStack(Item.getItemFromBlock(disenchantmentTable), 1, 4),
					"POP",
					"EHE",
					"PTP",
					Character.valueOf('T'), table,
					Character.valueOf('E'), Items.ENDER_PEARL,
					Character.valueOf('H'), Blocks.HOPPER,
					Character.valueOf('P'), "dyePurple",
					Character.valueOf('O'), Blocks.OBSIDIAN
		));
		
		if (DisenchanterMain.config.get("general", "EnableClearRecipe", true).getBoolean())
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(disenchantmentTable), 1, 0),
					"PPP",
					"PTP",
					"PPP",
					Character.valueOf('T'), table,
					Character.valueOf('P'), Items.PAPER
		));
	}
	
	public void postInit(FMLPostInitializationEvent ev) {}
	
}