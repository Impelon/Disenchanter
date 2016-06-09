package de.impelon.disenchanter.blocks;

import java.util.Random;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockDisenchantmentTable extends BlockContainer {
    
	public BlockDisenchantmentTable() {
		super(Material.rock);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		this.setLightOpacity(0);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setUnlocalizedName("disenchantmentTable");
		this.setHardness(5.0F);
		this.setResistance(2000.0F);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World w, BlockPos pos, IBlockState state, Random random) {
		super.randomDisplayTick(w, pos, state, random);
		
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		for (int blockX = x - 2; blockX <= x + 2; ++blockX) {
			for (int blockZ = z - 2; blockZ <= z + 2; ++blockZ) {
				if (blockX > x - 2 && blockX < x + 2 && blockZ == z - 1) 
					blockZ = z + 2;

				if (random.nextInt(16) == 0) {
					for (int blockY = y; blockY <= y + 1; ++blockY) {
						if (w.getBlockState(new BlockPos(blockX, blockY, blockZ)).getBlock().equals(Blocks.bookshelf)) {
							if (!w.isAirBlock(new BlockPos((blockX - x) / 2 + x, blockY, (blockZ - z) / 2 + z)))
								break;
							
							w.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,
									(double) blockX + 0.25D,
									(double) blockY + 0.55D,
									(double) blockZ + 0.25D,
									(double) (x - blockX) + 0.5D,
									(double) (y - blockY) + (random.nextFloat() / 2) + 0.15D,
									(double) (z - blockZ) + 0.5D,
									new int[0]);
						}
					}
				}
			}
		}
	}
	
	@Override
	public boolean isFullCube() {
        return false;
    }

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer p,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!w.isRemote)
			p.openGui(DisenchanterMain.instance, 0, w, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void onBlockPlacedBy(World w, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		super.onBlockPlacedBy(w, pos, state, entity, stack);

		if (stack.hasDisplayName())
			((TileEntityDisenchantmentTable) w.getTileEntity(pos)).setCustomName(stack.getDisplayName());
	}
	
	@Override
	public TileEntity createNewTileEntity(World w, int metadata) {
		return new TileEntityDisenchantmentTable();
	}

}
