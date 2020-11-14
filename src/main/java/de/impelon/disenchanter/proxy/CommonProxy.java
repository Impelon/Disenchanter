package de.impelon.disenchanter.proxy;

import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.block.BlockDisenchantmentTable;
import de.impelon.disenchanter.gui.GUIHandler;
import de.impelon.disenchanter.item.ItemBlockDisenchantment;
import de.impelon.disenchanter.item.ItemExperienceJar;
import de.impelon.disenchanter.tileentity.TileEntityDisenchantmentTable;
import de.impelon.disenchanter.tileentity.TileEntityDisenchantmentTableAutomatic;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

	public static final BlockDisenchantmentTable disenchantmentTable = new BlockDisenchantmentTable();
	public static final ItemBlockDisenchantment itemDisenchantmentTable = new ItemBlockDisenchantment();
	public static final ItemExperienceJar itemExperienceJar = new ItemExperienceJar();

	private static final ResourceLocation disenchantmentTableUseLocation = new ResourceLocation(DisenchanterMain.MODID,
			"block.disenchantment_table.use");
	public static final SoundEvent disenchantmentTableUse = new SoundEvent(disenchantmentTableUseLocation)
			.setRegistryName(disenchantmentTableUseLocation);

	public void preInit(FMLPreInitializationEvent ev) {
		GameRegistry.registerTileEntity(TileEntityDisenchantmentTable.class,
				new ResourceLocation(DisenchanterMain.MODID, "tiledisentchantmenttable"));
		GameRegistry.registerTileEntity(TileEntityDisenchantmentTableAutomatic.class,
				new ResourceLocation(DisenchanterMain.MODID, "tiledisentchantmenttableautomatic"));
	}

	public void load(FMLInitializationEvent ev) {
		NetworkRegistry.INSTANCE.registerGuiHandler(DisenchanterMain.instance, new GUIHandler());
	}

	public void postInit(FMLPostInitializationEvent ev) {
	}

	public void registerBlocks(RegistryEvent.Register<Block> ev) {
		ev.getRegistry().register(disenchantmentTable);
	}

	public void registerItems(RegistryEvent.Register<Item> ev) {
		ev.getRegistry().register(itemExperienceJar);
		ev.getRegistry().register(itemDisenchantmentTable.setRegistryName(disenchantmentTable.getRegistryName()));
	}

	public void registerSoundEvents(RegistryEvent.Register<SoundEvent> ev) {
		ev.getRegistry().register(disenchantmentTableUse);
	}

	public void registerRecipes(RegistryEvent.Register<IRecipe> ev) {
	};

}
