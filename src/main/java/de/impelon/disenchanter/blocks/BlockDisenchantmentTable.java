package de.impelon.disenchanter.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
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
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.impelon.disenchanter.DisenchanterMain;

public class BlockDisenchantmentTable extends BlockContainer {
    
    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
    public static final PropertyBool AUTOMATIC = PropertyBool.create("automatic");
	public static final PropertyBool BULKDISENCHANTING = PropertyBool.create("bulkdisenchanting");
	public static final PropertyBool VOIDING = PropertyBool.create("voiding");
	
	public BlockDisenchantmentTable() {
		super(Material.ROCK);
		this.setLightOpacity(0);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setRegistryName(DisenchanterMain.MODID, "disenchantmentTable");
		this.setUnlocalizedName(this.getRegistryName().toString().toLowerCase());
		this.setHardness(5.0F);
		this.setResistance(2000.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AUTOMATIC, false).withProperty(BULKDISENCHANTING, false).withProperty(VOIDING, false));
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
	protected BlockStateContainer createBlockState() {
	    return new BlockStateContainer(this, new IProperty[] { AUTOMATIC, BULKDISENCHANTING, VOIDING });
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
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState state, World w, BlockPos pos) {
		TileEntity te = w.getTileEntity(pos);
		if (te instanceof TileEntityDisenchantmentTableAutomatic) 
			return Container.calcRedstone(te);
		return 0;
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
	
	protected void disenchant(IInventory inventory, boolean isAutomatic, World world, BlockPos position, Random random) {
		if (inventory.getSizeInventory() < 3 || (isAutomatic && inventory.getStackInSlot(2) != null))
			return;

		ItemStack itemstack = inventory.getStackInSlot(0);
		ItemStack bookstack = inventory.getStackInSlot(1);
		ItemStack outputBookstack = new ItemStack(Items.ENCHANTED_BOOK);

		if (itemstack != null && bookstack != null && this.getEnchantmentList(itemstack) != null) {
			if (bookstack.stackSize > 1)
				bookstack.stackSize--;
			else
				bookstack = (ItemStack) null;
			inventory.setInventorySlotContents(1, bookstack);
				
			this.disenchant(itemstack, outputBookstack, isAutomatic, world, position, random);
			
			if (itemstack.getItemDamage() > itemstack.getMaxDamage())
				itemstack = null;
			
			if (itemstack != null && this.getEnchantmentList(itemstack) == null) {
				if (itemstack.getItem() == Items.ENCHANTED_BOOK)
					itemstack = new ItemStack(Items.BOOK);
				if (world.getBlockState(position).getValue(this.VOIDING))
					itemstack = null;
			}
			inventory.setInventorySlotContents(0, itemstack);
			
			if (isAutomatic && outputBookstack.getTagCompound() != null && outputBookstack.getTagCompound().getTag("StoredEnchantments") != null)
				inventory.setInventorySlotContents(2, outputBookstack);
			
			if (!world.isRemote)
				world.playSound(null, position, DisenchanterMain.proxy.disenchantmentTableUse, SoundCategory.BLOCKS, isAutomatic ? 0.5F : 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
		}
	}
	
	protected void disenchant(ItemStack itemstack, ItemStack outputBookstack, boolean isAutomatic, World world, BlockPos position, Random random) {
		float power = this.getEnchantingPower(world, position);
		int flatDmg = DisenchanterMain.config.get("disenchanting", "FlatDamage", 10).getInt();
		double durabiltyDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamage", 0.025).getDouble();
		double reduceableDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamageReduceable", 0.2).getDouble();
		double machineDmgMultiplier = isAutomatic ? DisenchanterMain.config.get("disenchanting", "MachineDamageMultiplier", 2.5).getDouble() : 1.0;

		while (this.getEnchantmentList(itemstack) != null) {
			this.transferEnchantment(itemstack, outputBookstack, 0, random);
			
			itemstack.attemptDamageItem((int) (machineDmgMultiplier * (flatDmg + itemstack.getMaxDamage() * durabiltyDmg + 
					itemstack.getMaxDamage() * (reduceableDmg / power))), random);
			
			if (itemstack.getItemDamage() > itemstack.getMaxDamage() || 
					!(world.getBlockState(position).getValue(this.BULKDISENCHANTING)))
				break;
		}
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
					Items.ENCHANTED_BOOK.addEnchantment(output, new EnchantmentData(Enchantment.getEnchantmentByID(id), lvl));
				
				enchants.removeTag(index);
				input.setRepairCost(input.getRepairCost() / 2);
			}
			if (enchants.tagCount() <= 0)
				if (this.isEnchantmentStorage(input))
					input.getTagCompound().removeTag("StoredEnchantments");
				else
					input.getTagCompound().removeTag("ench");
		}
	}
	
	public NBTTagList getEnchantmentList(ItemStack itemstack) {
		if (itemstack == null || itemstack.getTagCompound() == null)
			return null;
		
		if (itemstack.getTagCompound().getTag("InfiTool") != null)
			if (DisenchanterMain.config.get("disenchanting", "EnableTCBehaviour", true).getBoolean())
				return null;
		if (itemstack.getTagCompound().getTag("TinkerData") != null)
			if (DisenchanterMain.config.get("disenchanting", "EnableTCBehaviour", true).getBoolean())
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