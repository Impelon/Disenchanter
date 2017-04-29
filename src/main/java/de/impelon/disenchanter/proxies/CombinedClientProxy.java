package de.impelon.disenchanter.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
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
		
		for (byte meta = 0; meta < 8; meta++)
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(disenchantmentTable), meta, 
					new ModelResourceLocation(disenchantmentTable.getUnlocalizedName().substring(5), 
							disenchantmentTable.getStateFromMeta(meta).toString().split("[\\[\\]]")[1]));
	}
	
	@Override
	public void load(FMLInitializationEvent ev) {
		super.load(ev);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDisenchantmentTable.class, new TileEntityDisenchantmentTableRenderer());
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent ev) {
		super.postInit(ev);

		if (DisenchanterMain.config.get("general", "CheckVersion", true).getBoolean())
			new Thread(DisenchanterMain.versionChecker, "Version Check").start();
	}
}
