package de.impelon.disenchanter.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class BlockDisenchantmentTable extends BlockContainer {
	
    @SideOnly(Side.CLIENT)
    private IIcon[] top = new IIcon[2];
    @SideOnly(Side.CLIENT)
    private IIcon[] bottom = new IIcon[2];
    @SideOnly(Side.CLIENT)
    private IIcon[] side = new IIcon[2];

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
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World w, int x, int y, int z, Random random) {
		super.randomDisplayTick(w, x, y, z, random);

		for (int blockX = x - 2; blockX <= x + 2; ++blockX)
			for (int blockZ = z - 2; blockZ <= z + 2; ++blockZ) {
				if (blockX > x - 2 && blockX < x + 2 && blockZ == z - 1) 
					blockZ = z + 2;

				if (random.nextInt(16) == 0)
					for (int blockY = y; blockY <= y + 1; ++blockY)
						if (ForgeHooks.getEnchantPower(w, blockX, blockY, blockZ) > 0) {
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
	
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int unknown, CreativeTabs tab, List subItems) {
		for (byte n = 0; n < 8; n++)
			subItems.add(new ItemStack(this, 1, n));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return side == 0 ? this.bottom[(meta / 4) % 2] : (side == 1 ? this.top[(meta / 2) % 2] : this.side[meta % 2]);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister i) {
		this.side[0] 	= i.registerIcon("Disenchanter:disenchantmenttable_side");
		this.side[1] 	= i.registerIcon("Disenchanter:disenchantmenttable_side_automatic");
		this.top[0] 	= i.registerIcon("Disenchanter:disenchantmenttable_top");
		this.top[1] 	= i.registerIcon("Disenchanter:disenchantmenttable_top_bulk");
		this.bottom[0] 	= i.registerIcon("Disenchanter:disenchantmenttable_bottom");
		this.bottom[1] 	= i.registerIcon("Disenchanter:disenchantmenttable_bottom_voiding");
		
		this.blockIcon 	= this.side[0];
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	public boolean isAutomatic(int meta) {
		return meta % 2 == 1;
	}
	
	public boolean isBulkDisenchanting(int meta) {
		return (meta / 2) % 2 == 1;
	}
	
	public boolean isVoiding(int meta) {
		return (meta / 4) % 2 == 1;
	}
	
	@Override
	public int damageDropped (int metadata) {
		return metadata;
	}
	
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(World w, int x, int y, int z, int side) {
		TileEntity te = w.getTileEntity(x, y, z);
		if (te instanceof TileEntityDisenchantmentTableAutomatic) 
			return Container.calcRedstoneFromInventory((IInventory)te);
		return 0;
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
	public void breakBlock(World w, int x, int y, int z, Block b, int meta) {
		TileEntity te = w.getTileEntity(x, y, z);
		if (te instanceof TileEntityDisenchantmentTableAutomatic) {
			TileEntityDisenchantmentTableAutomatic ta = (TileEntityDisenchantmentTableAutomatic) te;
			
			for (int i = 0; i < ta.getSizeInventory(); ++i) {
                ItemStack itemstack = ta.getStackInSlot(i);

                if (itemstack != null) {
                    float offsetX = (float) (Math.random() * 0.8F + 0.1F);
                    float offsetY = (float) (Math.random() * 0.8F + 0.1F);
                    float offsetZ = (float) (Math.random() * 0.8F + 0.1F);
                    EntityItem entityitem;

                    while (itemstack.stackSize > 0) {
                        int size = (int) (Math.random() * 21 + 10);

                        if (size > itemstack.stackSize)
                            size = itemstack.stackSize;

                        itemstack.stackSize -= size;
                        entityitem = new EntityItem(w, x + offsetX, y + offsetY, z + offsetZ, new ItemStack(itemstack.getItem(), size, itemstack.getItemDamage()));
                        entityitem.motionX = Math.random() * 0.05F;
                        entityitem.motionY = Math.random() * 0.05F + 0.2F;
                        entityitem.motionZ = Math.random() * 0.05F;

                        if (itemstack.hasTagCompound())
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                        w.spawnEntityInWorld(entityitem);
                    }
                }
            }
		}
		super.breakBlock(w, x, y, z, b, meta);
	}
	
	@Override
	public TileEntity createNewTileEntity(World w, int metadata) {
		if (isAutomatic(metadata))
			return new TileEntityDisenchantmentTableAutomatic();
		return new TileEntityDisenchantmentTable();
	}
	
	public float getEnchantingPower(World w, int x, int y, int z) {
		float power = 1;
		for (int blockZ = -1; blockZ <= 1; ++blockZ) {
			for (int blockX = -1; blockX <= 1; ++blockX) {
				if ((blockZ != 0 || blockX != 0) && w.isAirBlock(x + blockX, y, z + blockZ)
						&& w.isAirBlock(x + blockX, y + 1, z + blockZ)) {
					power += ForgeHooks.getEnchantPower(w, x + blockX * 2, y, z + blockZ * 2);
					power += ForgeHooks.getEnchantPower(w, x + blockX * 2, y + 1, z + blockZ * 2);

					if (blockX != 0 && blockZ != 0) {
						power += ForgeHooks.getEnchantPower(w, x + blockX * 2, y, z + blockZ);
						power += ForgeHooks.getEnchantPower(w, x + blockX * 2, y + 1, z + blockZ);
						power += ForgeHooks.getEnchantPower(w, x + blockX, y, z + blockZ * 2);
						power += ForgeHooks.getEnchantPower(w, x + blockX, y + 1, z + blockZ * 2);
					}
				}
			}
		}
		
		if (power > 15)
			power = 15;
		return power;
	}
	
	public void transferEnchantment(ItemStack input, ItemStack output, int index, Random random) {
		if (input != null && output != null && input.stackTagCompound != null) {
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
					Items.enchanted_book.addEnchantment(output, new EnchantmentData(id, lvl));
				
				enchants.removeTag(index);
			}
			if (enchants.tagCount() <= 0)
				if (this.isEnchantmentStorage(input))
					input.stackTagCompound.removeTag("StoredEnchantments");
				else
					input.stackTagCompound.removeTag("ench");
		}
	}
	
	public NBTTagList getEnchantmentList(ItemStack itemstack) {
		if (itemstack.stackTagCompound == null)
			return null;
		if (itemstack.stackTagCompound.getTag("ench") != null)
			return (NBTTagList) itemstack.stackTagCompound.getTag("ench");
		if (itemstack.stackTagCompound.getTag("StoredEnchantments") != null)
			return (NBTTagList) itemstack.stackTagCompound.getTag("StoredEnchantments");
		return null;
	}
	
	public boolean isEnchantmentStorage(ItemStack itemstack) {
		return itemstack.stackTagCompound.getTag("StoredEnchantments") != null;
	}

}
