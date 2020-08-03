package de.impelon.disenchanter.proxies;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
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
	}
	
	@Override
	public void registerBlocks(RegistryEvent.Register<Block> ev) {
		super.registerBlocks(ev);
		
		ModelLoader.setCustomStateMapper(disenchantmentTable, new DefaultStateMapper() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				StringBuilder variant = new StringBuilder();
				variant.append(BlockDisenchantmentTable.AUTOMATIC.getName() + "=" + state.getValue(BlockDisenchantmentTable.AUTOMATIC).toString());
				variant.append(',');
				variant.append(BlockDisenchantmentTable.BULKDISENCHANTING.getName() + "=" + state.getValue(BlockDisenchantmentTable.BULKDISENCHANTING).toString());
				variant.append(',');
				variant.append(BlockDisenchantmentTable.VOIDING.getName() + "=" + state.getValue(BlockDisenchantmentTable.VOIDING).toString());
				return new ModelResourceLocation(disenchantmentTable.getRegistryName(), variant.toString());
		    }
		});
	}
	
	@Override
	public void registerItems(RegistryEvent.Register<Item> ev) {
		super.registerItems(ev);
		
		for (byte meta = 0; meta < 8; meta++) {
			IBlockState state = disenchantmentTable.getStateFromMeta(meta);
			StringBuilder variant = new StringBuilder();
			variant.append(BlockDisenchantmentTable.AUTOMATIC.getName() + "=" + state.getValue(BlockDisenchantmentTable.AUTOMATIC).toString());
			variant.append(',');
			variant.append(BlockDisenchantmentTable.BULKDISENCHANTING.getName() + "=" + state.getValue(BlockDisenchantmentTable.BULKDISENCHANTING).toString());
			variant.append(',');
			variant.append(BlockDisenchantmentTable.VOIDING.getName() + "=" + state.getValue(BlockDisenchantmentTable.VOIDING).toString());
			ModelLoader.setCustomModelResourceLocation(itemDisenchantmentTable, meta, 
					new ModelResourceLocation(disenchantmentTable.getRegistryName(), variant.toString()));
		}
	}
	
	@Override
	public void load(FMLInitializationEvent ev) {
		super.load(ev);
		if (DisenchanterMain.config.get("general", "CheckVersion", true).getBoolean())
			MinecraftForge.EVENT_BUS.register(DisenchanterMain.versionChecker);
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDisenchantmentTable.class, new TileEntityDisenchantmentTableRenderer());
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent ev) {
		super.postInit(ev);
		
		if (DisenchanterMain.config.get("general", "CheckVersion", true).getBoolean())
			new Thread(DisenchanterMain.versionChecker, "Version Check").start();
	}
}
