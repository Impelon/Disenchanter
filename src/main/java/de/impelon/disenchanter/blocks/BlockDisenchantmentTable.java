package de.impelon.disenchanter.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDisenchantmentTable extends BlockContainer {
	
    @SideOnly(Side.CLIENT)
    private IIcon top;
    @SideOnly(Side.CLIENT)
    private IIcon bottom;

	public BlockDisenchantmentTable() {
		super(Material.rock);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		this.setLightOpacity(0);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setBlockName("blockDisenchantmentTable");
		this.setHardness(5.0F);
		this.setResistance(2000.0F);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public int damageDropped (int metadata) {
		return metadata;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World w, int x, int y, int z, Random random) {
		super.randomDisplayTick(w, x, y, z, random);

		for (int blockX = x - 2; blockX <= x + 2; ++blockX) {
			for (int blockZ = z - 2; blockZ <= z + 2; ++blockZ) {
				if (blockX > x - 2 && blockX < x + 2 && blockZ == z - 1) 
					blockZ = z + 2;

				if (random.nextInt(16) == 0) {
					for (int blockY = y; blockY <= y + 1; ++blockY) {
						if (w.getBlock(blockX, blockY, blockZ) == Blocks.bookshelf) {
							if (!w.isAirBlock((blockX - x) / 2 + x, blockY, (blockZ - z) / 2 + z))
								break;

							w.spawnParticle("enchantmenttable",
									(double) blockX + 0.25D,
									(double) blockY + 0.55D,
									(double) blockZ + 0.25D,
									(double) (x - blockX) + 0.5D,
									(double) (y - blockY) + (random.nextFloat() / 2) + 0.15D,
									(double) (z - blockZ) + 0.5D);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int unknown, CreativeTabs tab, List subItems) {
		subItems.add(new ItemStack(this, 1, 0));
		subItems.add(new ItemStack(this, 1, 1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return side == 0 ? this.bottom : (side == 1 ? this.top : this.blockIcon);
	}
	
	
	
	@Override
    @SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess access, int x, int y, int z) {
		if (access.getBlockMetadata(x, y, z) == 1)
			return 0x888888;
		return super.colorMultiplier(access, x, y, z);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public int getRenderColor(int meta) {
		if (meta == 1)
			return 0x888888;
		return super.getRenderColor(meta);
	}

	@Override
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer p,
			int metadata, float sideX, float sideY, float sideZ) {
		if (!w.isRemote)
			p.openGui(DisenchanterMain.instance, 0, w, x, y, z);
		return true;
	}

	@Override
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		super.onBlockPlacedBy(w, x, y, z, entity, stack);

		if (stack.hasDisplayName())
			((TileEntityDisenchantmentTable) w.getTileEntity(x, y, z)).setCustomName(stack.getDisplayName());
	}
		
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister i) {
		this.blockIcon = i.registerIcon("Disenchanter:disenchantment_side");
		this.top = i.registerIcon("Disenchanter:disenchantment_top");
		this.bottom = i.registerIcon("Disenchanter:disenchantment_bottom");
	}
	
	@Override
	public TileEntity createNewTileEntity(World w, int metadata) {
		if (metadata == 1)
			return new TileEntityDisenchantmentTableAutomatic();
		return new TileEntityDisenchantmentTable();
	}

}
