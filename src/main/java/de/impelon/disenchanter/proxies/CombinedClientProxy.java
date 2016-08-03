package de.impelon.disenchanter.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import de.impelon.disenchanter.blocks.BlockDisenchantmentTable;
import de.impelon.disenchanter.blocks.TileEntityDisenchantmentTable;
import de.impelon.disenchanter.gui.TileEntityDisenchantmentTableRenderer;
import de.impelon.disenchanter.DisenchanterMain;

public class CombinedClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent ev) {
		super.preInit(ev);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(disenchantmentTable), 0, new ModelResourceLocation(DisenchanterMain.MODID + ":" + disenchantmentTable.getUnlocalizedName().substring(5), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(disenchantmentTable), 1, new ModelResourceLocation(DisenchanterMain.MODID + ":" + disenchantmentTable.getUnlocalizedName().substring(5), "inventory"));
	}
	
	@Override
	public void load(FMLInitializationEvent ev) {
		super.load(ev);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDisenchantmentTable.class, new TileEntityDisenchantmentTableRenderer());	
		
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor) disenchantmentTable, disenchantmentTable);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((IItemColor) itemDisenchantment, itemDisenchantment);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent ev) {
		super.postInit(ev);
		
		if (DisenchanterMain.config.get("general", "CheckVersion", true).getBoolean())
			new Thread(DisenchanterMain.versionChecker, "Version Check").start();
	}
}
