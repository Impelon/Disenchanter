package de.impelon.disenchanter.proxies;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.blocks.BlockDisenchantmentTable;
import de.impelon.disenchanter.blocks.GUIHandler;
import de.impelon.disenchanter.blocks.TileEntityDisenchantmentTable;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;


public class CommonProxy {
	
	public static final Block disenchantmentTable = new BlockDisenchantmentTable();
	
	public void preInit(FMLPreInitializationEvent ev) {
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		DisenchanterMain.config = config;
		config.load();
		
		config.get("general", "CheckVersion", true, "Should Disenchanter check for new versions on startup?");
		config.get("disenchanting", "FlatDamage", 10, "How much flat damage should be dealt to Items when disenchanting?");
		config.get("disenchanting", "MaxDurabilityDamage", 0.025, "How much of the Item's maximal durability should be dealt as damage to Items when disenchanting?");
		config.get("disenchanting", "MaxDurabilityDamageReduceable", 0.2, "How much of the Item's maximal durability should be dealt as reduceable damage to Items when disenchanting?");
		
		config.save();
		
		GameRegistry.registerTileEntity(TileEntityDisenchantmentTable.class, "TileDisentchantmentTable");
		
		GameRegistry.registerBlock(disenchantmentTable, "BlockDisenchantmentTable");
	}
	
	public void load(FMLInitializationEvent ev) {
		if (DisenchanterMain.config.get("general", "CheckVersion", true).getBoolean())
			FMLCommonHandler.instance().bus().register(DisenchanterMain.versionChecker);
		
		cpw.mods.fml.common.network.NetworkRegistry.INSTANCE.registerGuiHandler(DisenchanterMain.instance, new GUIHandler());
			
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(disenchantmentTable), 1),
					"   ",
					"YEY",
					"ETE",
					Character.valueOf('T'), Blocks.enchanting_table,
					Character.valueOf('E'), Items.emerald,
					Character.valueOf('Y'), "dyeYellow"
		));
	}
	
	public void postInit(FMLPostInitializationEvent ev) {}
	
}
