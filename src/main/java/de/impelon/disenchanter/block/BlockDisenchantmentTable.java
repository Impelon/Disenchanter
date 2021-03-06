package de.impelon.disenchanter.block;

import java.util.Random;

import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.inventory.InventoryUtils;
import de.impelon.disenchanter.tileentity.TileEntityDisenchantmentTable;
import de.impelon.disenchanter.tileentity.TileEntityDisenchantmentTableAutomatic;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDisenchantmentTable extends BlockContainer {

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
	public static final PropertyBool AUTOMATIC = PropertyBool.create("automatic");
	public static final PropertyBool BULKDISENCHANTING = PropertyBool.create("bulkdisenchanting");
	public static final PropertyBool CYCLING = PropertyBool.create("cycling");
	public static final PropertyBool VOIDING = PropertyBool.create("voiding");
	
	protected static final String SHOULD_DROP_KEY = "ShouldDropOnHarvest";

	public BlockDisenchantmentTable() {
		super(Material.ROCK, MapColor.YELLOW);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setRegistryName(DisenchanterMain.MODID, "disenchantmentTable");
		this.setTranslationKey(this.getRegistryName().toString().toLowerCase());
		this.setLightOpacity(0);
		this.setHardness(5.0F);
		this.setResistance(2000.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AUTOMATIC, false)
				.withProperty(BULKDISENCHANTING, false).withProperty(VOIDING, false).withProperty(CYCLING, false));
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
				if (blockX >= x - 1 && blockX <= x + 1 && blockZ == z - 1)
					blockZ = z + 2;

				if (random.nextInt(16) == 0) {
					for (int blockY = y; blockY <= y + 1; ++blockY) {
						if (ForgeHooks.getEnchantPower(w, new BlockPos(blockX, blockY, blockZ)) > 0) {
							if (!w.isAirBlock(new BlockPos((blockX - x) / 2 + x, blockY, (blockZ - z) / 2 + z)))
								break;

							w.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, blockX + 0.25D, blockY + 0.55D,
									blockZ + 0.25D, x - blockX + 0.5D, (y - blockY) + (random.nextFloat() / 2) + 0.15D,
									z - blockZ + 0.5D);
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { AUTOMATIC, BULKDISENCHANTING, VOIDING, CYCLING });
	}

	@Override
	public IBlockState getStateFromMeta(int metadata) {
		IBlockState state = getDefaultState();
		if (metadata < 8) {
			state = state.withProperty(AUTOMATIC, metadata % 2 == 1 ? true : false);
			state = state.withProperty(BULKDISENCHANTING, (metadata / 2) % 2 == 1 ? true : false);
			state = state.withProperty(VOIDING, (metadata / 4) % 2 == 1 ? true : false);
		} else {
			state = state.withProperty(CYCLING, true);
			state = state.withProperty(VOIDING, metadata % 2 == 1 ? true : false);
		}
		
		return state;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int metadata = 0;
		if (!state.getValue(CYCLING)) {
			metadata += state.getValue(AUTOMATIC) ? 1 : 0;
			metadata += state.getValue(BULKDISENCHANTING) ? 2 : 0;
			metadata += state.getValue(VOIDING) ? 4 : 0;
		} else {
			metadata += 8;
			metadata += state.getValue(VOIDING) ? 1 : 0;
		}
		return metadata;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (byte meta = 0; meta < 10; meta++)
			subItems.add(new ItemStack(this, 1, meta));
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return state.getValue(AUTOMATIC);
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World w, BlockPos pos) {
		TileEntity te = w.getTileEntity(pos);
		if (te instanceof TileEntityDisenchantmentTableAutomatic)
			return InventoryUtils.calcRedstoneFromInventory(te);
		return 0;
	}

	@Override
	public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!w.isRemote)
			p.openGui(DisenchanterMain.instance, 0, w, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void onBlockPlacedBy(World w, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		super.onBlockPlacedBy(w, pos, state, entity, stack);

		if (stack.hasDisplayName()) {
			TileEntity te = w.getTileEntity(pos);
			if (te instanceof TileEntityDisenchantmentTable)
				((TileEntityDisenchantmentTable) te).setCustomName(stack.getDisplayName());
		}
	}
	
	@Override
	public void onBlockHarvested(World w, BlockPos pos, IBlockState state, EntityPlayer player) {
		TileEntity te = w.getTileEntity(pos);
		if (te instanceof TileEntityDisenchantmentTable)
			te.getTileData().setBoolean(SHOULD_DROP_KEY, !player.isCreative() && this.canHarvestBlock(w, pos, player));
		super.onBlockHarvested(w, pos, state, player);
	}

	@Override
	public void breakBlock(World w, BlockPos pos, IBlockState state) {
		TileEntity te = w.getTileEntity(pos);
		if (te instanceof TileEntityDisenchantmentTableAutomatic)
			InventoryUtils.dropInventory(w, pos, te);
		if (te instanceof TileEntityDisenchantmentTable) {
			TileEntityDisenchantmentTable table = (TileEntityDisenchantmentTable) te;
			if (!table.getTileData().hasKey(SHOULD_DROP_KEY) || table.getTileData().getBoolean(SHOULD_DROP_KEY)) {
				ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(state));
				if (table.hasCustomName()) {
					stack.setStackDisplayName(table.getName());
					table.setCustomName(""); // so BlockContainer legacy code for breaking named tiles will not trigger
				}
				spawnAsEntity(w, pos, stack);
			}
			w.updateComparatorOutputLevel(pos, state.getBlock());
		}
		super.breakBlock(w, pos, state);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ItemStack.EMPTY.getItem(); // block is dropped with custom logic in breakBlock
	}
	
	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return ItemStack.EMPTY; // block is dropped with custom logic in breakBlock
	}

	@Override
	public TileEntity createNewTileEntity(World w, int metadata) {
		if (this.getStateFromMeta(metadata).getValue(AUTOMATIC))
			return new TileEntityDisenchantmentTableAutomatic();
		return new TileEntityDisenchantmentTable();
	}

}
