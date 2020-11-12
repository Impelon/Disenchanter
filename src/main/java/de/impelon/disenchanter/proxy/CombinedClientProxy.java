package de.impelon.disenchanter.proxy;

import de.impelon.disenchanter.DisenchanterConfig;
import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.DisenchantingProperties;
import de.impelon.disenchanter.DisenchantingProperties.TableVariant;
import de.impelon.disenchanter.tileentity.TileEntityDisenchantmentTable;
import de.impelon.disenchanter.tileentity.TileEntityDisenchantmentTableRenderer;
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

public class CombinedClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent ev) {
		super.preInit(ev);
	}

	public String buildVariantString(IBlockState state) {
		StringBuilder variantBuilder = new StringBuilder();
		DisenchantingProperties properties = DisenchantingProperties.getPropertiesFromState(state);
		for (TableVariant variant : TableVariant.values()) {
			variantBuilder.append(variant);
			variantBuilder.append("=");
			variantBuilder.append(properties.is(variant));
			variantBuilder.append(',');
		}
		return variantBuilder.substring(0, variantBuilder.length() - 1);
	}

	@Override
	public void registerBlocks(RegistryEvent.Register<Block> ev) {
		super.registerBlocks(ev);

		ModelLoader.setCustomStateMapper(disenchantmentTable, new DefaultStateMapper() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(disenchantmentTable.getRegistryName(), buildVariantString(state));
			}
		});
	}

	@Override
	public void registerItems(RegistryEvent.Register<Item> ev) {
		super.registerItems(ev);

		ModelLoader.setCustomModelResourceLocation(itemExperienceJar, 0,
				new ModelResourceLocation(itemExperienceJar.getRegistryName().toString()));

		for (byte meta = 0; meta < 8; meta++) {
			IBlockState state = disenchantmentTable.getStateFromMeta(meta);
			ModelLoader.setCustomModelResourceLocation(itemDisenchantmentTable, meta,
					new ModelResourceLocation(disenchantmentTable.getRegistryName(), buildVariantString(state)));
		}

	}

	@Override
	public void load(FMLInitializationEvent ev) {
		super.load(ev);
		if (DisenchanterConfig.general.shouldCheckVersion)
			MinecraftForge.EVENT_BUS.register(DisenchanterMain.versionChecker);

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDisenchantmentTable.class,
				new TileEntityDisenchantmentTableRenderer());
	}

	@Override
	public void postInit(FMLPostInitializationEvent ev) {
		super.postInit(ev);

		if (DisenchanterConfig.general.shouldCheckVersion)
			new Thread(DisenchanterMain.versionChecker, "Version Check").start();
	}
}
