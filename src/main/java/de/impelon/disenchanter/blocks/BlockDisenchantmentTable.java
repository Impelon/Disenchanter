package de.impelon.disenchanter.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.impelon.disenchanter.DisenchanterMain;

public class BlockDisenchantmentTable extends BlockContainer {
    
    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
	
	public BlockDisenchantmentTable() {
		super(Material.ROCK);
		this.setLightOpacity(0);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setUnlocalizedName("disenchantmentTable");
		this.setHardness(5.0F);
		this.setResistance(2000.0F);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World w, BlockPos pos, Random random) {
		super.randomDisplayTick(state, w, pos, random);
		
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		for (int blockX = x - 2; blockX <= x + 2; ++blockX) {
			for (int blockZ = z - 2; blockZ <= z + 2; ++blockZ) {
				if (blockX > x - 2 && blockX < x + 2 && blockZ == z - 1) 
					blockZ = z + 2;

				if (random.nextInt(16) == 0) {
					for (int blockY = y; blockY <= y + 1; ++blockY) {
						if (w.getBlockState(new BlockPos(blockX, blockY, blockZ)).getBlock().equals(Blocks.BOOKSHELF)) {
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }
	
	@Override
	public boolean isFullCube(IBlockState state) {
        return false;
    }

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, 
			@Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
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
