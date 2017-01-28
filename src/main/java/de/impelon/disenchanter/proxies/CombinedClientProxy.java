package de.impelon.disenchanter.proxies;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.impelon.disenchanter.blocks.BlockDisenchantmentTable;
import de.impelon.disenchanter.blocks.TileEntityDisenchantmentTable;
import de.impelon.disenchanter.gui.TileEntityDisenchantmentTableRenderer;
import de.impelon.disenchanter.DisenchanterMain;

public class CombinedClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent ev) {
		super.preInit(ev);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(disenchantmentTable), 0, new ModelResourceLocation(disenchantmentTable.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(disenchantmentTable), 1, new ModelResourceLocation(disenchantmentTable.getRegistryName(), "inventory"));
	}
	
	@Override
	public void load(FMLInitializationEvent ev) {
		super.load(ev);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDisenchantmentTable.class, new TileEntityDisenchantmentTableRenderer());	
		
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor() {
			
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) {
				if (state.getValue(BlockDisenchantmentTable.AUTOMATIC) == true)
					return 0x888888;
				return 0xffffff;
			}
		}, disenchantmentTable);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
			
			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				if (stack.getItemDamage() == 1)
					return 0x888888;
				return 0xffffff;
			}
		}, itemDisenchantment);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent ev) {
		super.postInit(ev);
		
		if (DisenchanterMain.config.get("general", "CheckVersion", true).getBoolean())
			new Thread(DisenchanterMain.versionChecker, "Version Check").start();
	}
}
