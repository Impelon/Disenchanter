package de.impelon.disenchanter.proxies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
import de.impelon.disenchanter.gui.GUIHandler;
import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;


public class CommonProxy {
	
	public static final Block disenchantmentTable = new BlockDisenchantmentTable();
	public static final Item itemDisenchantment = new ItemBlockDisenchantment();
		
	public void preInit(FMLPreInitializationEvent ev) {
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		DisenchanterMain.config = config;
		config.load();
		
		config.get("general", "CheckVersion", true, "Should Disenchanter check for new versions on startup?");
		config.get("general", "EnableAutomaticRecipe", true, "Should the recipe for the automatic Disenchantment Table be avalible?");
		config.get("disenchanting", "FlatDamage", 10, "How much flat damage should be dealt to Items when disenchanting?");
		config.get("disenchanting", "MaxDurabilityDamage", 0.025, "How much of the Item's maximal durability should be dealt as damage to Items when disenchanting?");
		config.get("disenchanting", "MaxDurabilityDamageReduceable", 0.2, "How much of the Item's maximal durability should be dealt as reduceable damage to Items when disenchanting?");
		config.get("disenchanting", "MachineDamageMultiplier", 2.5, "By how much should the dammage on the item be multiplied when using an automaic Disenchantment Table?");
		config.get("disenchanting", "EnchantmentLossChance", 0.0, "What should the probability be of additional enchantments being lost from Items when disenchanting?");
		
		config.save();
		
		GameRegistry.registerTileEntity(TileEntityDisenchantmentTable.class, "TileDisentchantmentTable");
		GameRegistry.registerTileEntity(TileEntityDisenchantmentTableAutomatic.class, "TileDisentchantmentTableAutomatic");
		
		GameRegistry.register(disenchantmentTable, new ResourceLocation(DisenchanterMain.MODID, disenchantmentTable.getUnlocalizedName().substring(5)));
		GameRegistry.register(itemDisenchantment, new ResourceLocation(DisenchanterMain.MODID, disenchantmentTable.getUnlocalizedName().substring(5)));
	}
	
	public void load(FMLInitializationEvent ev) {	
		if (DisenchanterMain.config.get("general", "CheckVersion", true).getBoolean())
			MinecraftForge.EVENT_BUS.register(DisenchanterMain.versionChecker);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(DisenchanterMain.instance, new GUIHandler());
			
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(disenchantmentTable), 1),
					"   ",
					"YEY",
					"ETE",
					Character.valueOf('T'), Blocks.ENCHANTING_TABLE,
					Character.valueOf('E'), Items.EMERALD,
					Character.valueOf('Y'), "dyeYellow"
			));
		
		if (DisenchanterMain.config.get("general", "EnableAutomaticRecipe", true).getBoolean())
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(disenchantmentTable), 1, 1),
					"IMI",
					"BCB",
					"ITI",
					Character.valueOf('T'), new ItemStack(Item.getItemFromBlock(disenchantmentTable), 1, 0),
					Character.valueOf('I'), "ingotIron",
					Character.valueOf('C'), Blocks.CHEST,
					Character.valueOf('B'), "dyeBlack",
					Character.valueOf('M'), Items.COMPARATOR
			));
		
	}
	
	public void postInit(FMLPostInitializationEvent ev) {}
	
}
