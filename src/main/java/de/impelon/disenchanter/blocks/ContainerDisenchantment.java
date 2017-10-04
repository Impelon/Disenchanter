package de.impelon.disenchanter.blocks;

import java.util.Random;

import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.proxies.CommonProxy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ContainerDisenchantment extends Container {
	
	private EntityPlayerMP player = null;
	private World world;
	private BlockPos posBlock;
	private TileEntityDisenchantmentTableAutomatic tileentity;
	private Random random = new Random();
	private IInventory slots = new InventoryBasic("Disenchant", true, 3) {

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}
				
		@Override
		public void markDirty() {
			super.markDirty();
			ContainerDisenchantment.this.onCraftMatrixChanged(this);
		}
		
		@Override
		public ItemStack removeStackFromSlot(int index) {
			if (index == this.getSizeInventory() - 1)
				return ItemStack.EMPTY;
			return super.removeStackFromSlot(index);
		}
	};

	public ContainerDisenchantment(final InventoryPlayer pInventory, World w, BlockPos pos) {
		if (pInventory.player instanceof EntityPlayerMP)
			this.player = (EntityPlayerMP) pInventory.player;
		this.world = w;
		this.posBlock = pos;
		this.tileentity = null;
		
		TileEntity te = this.world.getTileEntity(pos);
		if (te instanceof TileEntityDisenchantmentTableAutomatic) {
			this.tileentity = (TileEntityDisenchantmentTableAutomatic) te;
			this.slots = this.tileentity;
		}
		
		this.addSlotToContainer(new Slot(this.slots, 0, 26, 35));

		this.addSlotToContainer(new Slot(this.slots, 1, 75, 35) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem().equals(Items.BOOK);
			}

		});

		this.addSlotToContainer(new Slot(this.slots, 2, 133, 35) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}
			
			@Override
			public ItemStack onTake(EntityPlayer p, ItemStack stack) {
				if (tileentity != null)
					return stack;
				if (player == null)
					if (pInventory.player instanceof EntityPlayerMP)
						player = (EntityPlayerMP) pInventory.player;
				
				ItemStack itemstack = slots.getStackInSlot(0);
				ItemStack bookstack = slots.getStackInSlot(1);
				ItemStack outputBookstack = new ItemStack(Items.ENCHANTED_BOOK);
				BlockDisenchantmentTable table = DisenchanterMain.proxy.disenchantmentTable;
				
				if (itemstack != ItemStack.EMPTY && bookstack != ItemStack.EMPTY) {
					if (bookstack.getCount() > 1)
						bookstack.setCount(bookstack.getCount() - 1);
					else
						bookstack = ItemStack.EMPTY;
					slots.setInventorySlotContents(1, bookstack);

					float power = table.getEnchantingPower(world, posBlock);
					int flatDmg = DisenchanterMain.config.get("disenchanting", "FlatDamage", 10).getInt();
					double durabiltyDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamage", 0.025).getDouble();
					double reduceableDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamageReduceable", 0.2).getDouble();
					double machineDmgMultiplier = DisenchanterMain.config.get("disenchanting", "MachineDamageMultiplier", 2.5).getDouble();
					
					while (table.getEnchantmentList(itemstack) != null) {
						table.transferEnchantment(itemstack, outputBookstack, 0, random);
					
						itemstack.attemptDamageItem((int) (machineDmgMultiplier * (flatDmg + itemstack.getMaxDamage() * durabiltyDmg + 
								itemstack.getMaxDamage() * (reduceableDmg / power))), random, player);
						if (itemstack.getItemDamage() > itemstack.getMaxDamage()) {
							slots.setInventorySlotContents(0, ItemStack.EMPTY);
							break;
						}
					
						if (!(world.getBlockState(posBlock).getValue(table.BULKDISENCHANTING)))
							break;
					}
					
					if (table.getEnchantmentList(itemstack) == null) {
						if (itemstack.getItem() == Items.ENCHANTED_BOOK)
							slots.setInventorySlotContents(0, new ItemStack(Items.BOOK));
						if (world.getBlockState(posBlock).getValue(table.VOIDING))
							slots.setInventorySlotContents(0, ItemStack.EMPTY);
					}
				}
				return itemstack;
			}

		});

		int l;

		for (l = 0; l < 3; ++l)
			for (int i1 = 0; i1 < 9; ++i1)
				this.addSlotToContainer(new Slot(pInventory, i1 + l * 9 + 9,
						8 + i1 * 18, 84 + l * 18));

		for (l = 0; l < 9; ++l)
			this.addSlotToContainer(new Slot(pInventory, l, 8 + l * 18, 142));

	}

	@Override
	public void onCraftMatrixChanged(IInventory inventory) {
		super.onCraftMatrixChanged(inventory);

		if (inventory == this.slots && this.tileentity == null)
			this.updateOutput();

	}

	public void updateOutput() {
		if (!this.world.isRemote) {
			ItemStack itemstack = this.slots.getStackInSlot(0);
			ItemStack bookstack = this.slots.getStackInSlot(1);
			ItemStack outputBookstack = new ItemStack(Items.ENCHANTED_BOOK);
			BlockDisenchantmentTable table = DisenchanterMain.proxy.disenchantmentTable;

			if (itemstack != ItemStack.EMPTY && bookstack != ItemStack.EMPTY && table.getEnchantmentList(itemstack) != null) {
				itemstack = itemstack.copy();
				
				float power = table.getEnchantingPower(this.world, this.posBlock);
				int flatDmg = DisenchanterMain.config.get("disenchanting", "FlatDamage", 10).getInt();
				double durabiltyDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamage", 0.025).getDouble();
				double reduceableDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamageReduceable", 0.2).getDouble();
				double machineDmgMultiplier = DisenchanterMain.config.get("disenchanting", "MachineDamageMultiplier", 2.5).getDouble();

				while (table.getEnchantmentList(itemstack) != null) {
					table.transferEnchantment(itemstack, outputBookstack, 0, this.random);
					
					itemstack.attemptDamageItem((int) (machineDmgMultiplier * (flatDmg + itemstack.getMaxDamage() * durabiltyDmg + 
							itemstack.getMaxDamage() * (reduceableDmg / power))), this.random, this.player);
					
					if (itemstack.getItemDamage() > itemstack.getMaxDamage() || 
							!(world.getBlockState(posBlock).getValue(table.BULKDISENCHANTING)))
						break;
				}
				if (!(ItemStack.areItemStacksEqual(this.slots.getStackInSlot(2), outputBookstack)))
					this.slots.setInventorySlotContents(2, (ItemStack) outputBookstack);
			} else
				if (this.slots.getStackInSlot(2) != ItemStack.EMPTY)
					this.slots.setInventorySlotContents(2, ItemStack.EMPTY);
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer p) {
		super.onContainerClosed(p);
		
		if (this.tileentity == null)
			if (!this.world.isRemote)
				this.clearContainer(p, p.world, this.slots);
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return !this.world.getBlockState(this.posBlock).getBlock().equals(CommonProxy.disenchantmentTable) ? false
				: p.getDistanceSq((double) this.posBlock.getX() + 0.5D, (double) this.posBlock.getY() + 0.5D, (double) this.posBlock.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer p, int slotID) {
		ItemStack itemstackPrev = ItemStack.EMPTY;
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack()) {
			itemstack = slot.getStack();
			itemstackPrev = itemstack.copy();

			if (slotID == 2) {
				if (!this.mergeItemStack(itemstack, 3, 39, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(itemstack, itemstackPrev);
			} else if (slotID != 0 && slotID != 1) {
				ItemStack i = itemstack.splitStack(1);
				if (i.getItem().equals(Items.BOOK)) {
					if (((Slot) this.inventorySlots.get(1)).getHasStack() || !this.mergeItemStack(i, 1, 2, false)) {
						itemstack.setCount(itemstack.getCount() + 1);
						return ItemStack.EMPTY;
					}
				} else {
					if (((Slot) this.inventorySlots.get(0)).getHasStack() || !this.mergeItemStack(i, 0, 1, false)) {
						itemstack.setCount(itemstack.getCount() + 1);
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.mergeItemStack(itemstack, 3, 39, true)) {
				return ItemStack.EMPTY;
			}
			
			if (itemstack.getCount() <= 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (itemstack.getCount() == itemstackPrev.getCount())
				return ItemStack.EMPTY;

			slot.onTake(p, itemstack);
		}

		return itemstack;
	}
}