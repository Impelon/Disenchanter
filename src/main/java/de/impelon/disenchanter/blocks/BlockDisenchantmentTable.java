package de.impelon.disenchanter.blocks;

import java.util.List;
import java.util.Random;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDisenchantmentTable extends BlockContainer {
	
	public static final PropertyBool AUTOMATIC = PropertyBool.create("automatic");
	public static final PropertyBool BULKDISENCHANTING = PropertyBool.create("bulkdisenchanting");
	public static final PropertyBool VOIDING = PropertyBool.create("voiding");
    
	public BlockDisenchantmentTable() {
		super(Material.rock);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		this.setLightOpacity(0);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setRegistryName(DisenchanterMain.MODID, "disenchantmentTable");
		this.setUnlocalizedName(this.getRegistryName().toLowerCase());
		this.setHardness(5.0F);
		this.setResistance(2000.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AUTOMATIC, false).withProperty(BULKDISENCHANTING, false).withProperty(VOIDING, false));
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
	protected BlockState createBlockState() {
	    return new BlockState(this, new IProperty[] { AUTOMATIC, BULKDISENCHANTING, VOIDING });
	}
	
	@Override
	public IBlockState getStateFromMeta(int metadata) {
		IBlockState state = getDefaultState();
		state = state.withProperty(AUTOMATIC, metadata % 2 == 1 ? true : false);
		state = state.withProperty(BULKDISENCHANTING, (metadata / 2) % 2 == 1 ? true : false);
		state = state.withProperty(VOIDING, (metadata / 4) % 2 == 1 ? true : false);
	    return state;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int metadata = 0;
		metadata += state.getValue(AUTOMATIC) ? 1 : 0;
		metadata += state.getValue(BULKDISENCHANTING) ? 2 : 0;
		metadata += state.getValue(VOIDING) ? 4 : 0;
	    return metadata;
	}
	
	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}
	
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (byte meta = 0; meta < 8; meta++)
			subItems.add(new ItemStack(itemIn, 1, meta));
	}
	
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(World w, BlockPos pos) {
		TileEntity te = w.getTileEntity(pos);
		if (te instanceof TileEntityDisenchantmentTableAutomatic) 
			return Container.calcRedstone(te);
		return 0;
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
	public void breakBlock(World w, BlockPos pos, IBlockState state) {
		TileEntity te = w.getTileEntity(pos);
		if (te instanceof TileEntityDisenchantmentTableAutomatic) {
			InventoryHelper.dropInventoryItems(w, pos, (IInventory) te);
		}
		super.breakBlock(w, pos, state);
	}
		
	@Override
	public TileEntity createNewTileEntity(World w, int metadata) {
		if (this.getStateFromMeta(metadata).getValue(AUTOMATIC))
			return new TileEntityDisenchantmentTableAutomatic();
		return new TileEntityDisenchantmentTable();
	}
	
	public float getEnchantingPower(World w, BlockPos pos) {
		int power = 1;
		for (int blockZ = -1; blockZ <= 1; ++blockZ) {
			for (int blockX = -1; blockX <= 1; ++blockX) {
				if ((blockZ != 0 || blockX != 0) && w.isAirBlock(new BlockPos(pos.getX() + blockX, pos.getY(), pos.getZ() + blockZ))
						&& w.isAirBlock(new BlockPos(pos.getX() + blockX, pos.getY() + 1, pos.getZ() + blockZ))) {
					power += ForgeHooks.getEnchantPower(w, new BlockPos(pos.getX() + blockX * 2, pos.getY(), pos.getZ() + blockZ * 2));
					power += ForgeHooks.getEnchantPower(w, new BlockPos(pos.getX() + blockX * 2, pos.getY() + 1, pos.getZ() + blockZ * 2));

					if (blockX != 0 && blockZ != 0) {
						power += ForgeHooks.getEnchantPower(w, new BlockPos(pos.getX() + blockX * 2, pos.getY(), pos.getZ() + blockZ));
						power += ForgeHooks.getEnchantPower(w, new BlockPos(pos.getX() + blockX * 2, pos.getY() + 1, pos.getZ() + blockZ));
						power += ForgeHooks.getEnchantPower(w, new BlockPos(pos.getX() + blockX, pos.getY(), pos.getZ() + blockZ * 2));
						power += ForgeHooks.getEnchantPower(w, new BlockPos(pos.getX() + blockX, pos.getY() + 1, pos.getZ() + blockZ * 2));
					}
				}
			}
		}
		
		if (power > 15)
			power = 15;
		return power;
	}
	
	public void transferEnchantment(ItemStack input, ItemStack output, int index, Random random) {
		if (input != null && output != null && input.getTagCompound() != null) {
			double enchantmentLoss = DisenchanterMain.config.get("disenchanting", "EnchantmentLossChance", 0.0).getDouble();
			
			NBTTagList enchants = this.getEnchantmentList(input);
			if (enchants == null)
				return;
			
			if (enchants.tagCount() > 0) {
				index = Math.min(Math.abs(index), enchants.tagCount() - 1);
				
				NBTTagCompound enchant = enchants.getCompoundTagAt(index);
				int id = enchant.getInteger("id");
				int lvl = enchant.getInteger("lvl");
				
				if (random.nextFloat() > enchantmentLoss)
					Items.enchanted_book.addEnchantment(output, new EnchantmentData(Enchantment.getEnchantmentById(id), lvl));
				
				enchants.removeTag(index);
			}
			if (enchants.tagCount() <= 0)
				if (this.isEnchantmentStorage(input))
					input.getTagCompound().removeTag("StoredEnchantments");
				else
					input.getTagCompound().removeTag("ench");
		}
	}
	
	public NBTTagList getEnchantmentList(ItemStack itemstack) {
		if (itemstack.getTagCompound() == null)
			return null;
		if (itemstack.getTagCompound().getTag("ench") != null)
			return (NBTTagList) itemstack.getTagCompound().getTag("ench");
		if (itemstack.getTagCompound().getTag("StoredEnchantments") != null)
			return (NBTTagList) itemstack.getTagCompound().getTag("StoredEnchantments");
		return null;
	}
	
	public boolean isEnchantmentStorage(ItemStack itemstack) {
		return itemstack.getTagCompound().getTag("StoredEnchantments") != null;
	}

}
