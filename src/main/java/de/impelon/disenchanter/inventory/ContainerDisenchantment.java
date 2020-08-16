package de.impelon.disenchanter.inventory;

import java.util.Random;

import de.impelon.disenchanter.DisenchantingUtils;
import de.impelon.disenchanter.item.ItemExperienceJar;
import de.impelon.disenchanter.proxy.CommonProxy;
import de.impleon.disenchanter.tileentity.TileEntityDisenchantmentTableAutomatic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerDisenchantment extends Container {

	private World world;
	private BlockPos posBlock;
	private TileEntityDisenchantmentTableAutomatic tileentityAutomatic;
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
		this.world = w;
		this.posBlock = pos;
		this.tileentityAutomatic = null;

		TileEntity te = this.world.getTileEntity(pos);
		if (te instanceof TileEntityDisenchantmentTableAutomatic) {
			this.tileentityAutomatic = (TileEntityDisenchantmentTableAutomatic) te;
			this.slots = this.tileentityAutomatic;
		}

		this.addSlotToContainer(new Slot(this.slots, 0, 26, 35) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				if (stack.getItem().equals(Items.BOOK))
					return false;
				return true;
			}
		});

		this.addSlotToContainer(new Slot(this.slots, 1, 75, 35) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem().equals(Items.BOOK) || (stack.getItem().equals(CommonProxy.itemExperienceJar) && ItemExperienceJar.hasAvailableExperienceCapacity(stack));
			}

		});

		this.addSlotToContainer(new Slot(this.slots, 2, 133, 35) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}

			@Override
			public ItemStack onTake(EntityPlayer p, ItemStack stack) {
				if (tileentityAutomatic != null)
					return stack;

				DisenchantingUtils.disenchantInInventory(slots, false, world, posBlock, random);
				return stack;
			}

		});

		int l;

		for (l = 0; l < 3; ++l)
			for (int i1 = 0; i1 < 9; ++i1)
				this.addSlotToContainer(new Slot(pInventory, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));

		for (l = 0; l < 9; ++l)
			this.addSlotToContainer(new Slot(pInventory, l, 8 + l * 18, 142));

	}

	@Override
	public void onCraftMatrixChanged(IInventory inventory) {
		super.onCraftMatrixChanged(inventory);

		if (inventory == this.slots && this.tileentityAutomatic == null)
			this.updateOutput();

	}

	public void updateOutput() {
		if (!this.world.isRemote && this.tileentityAutomatic == null) {
			ItemStack itemstack = this.slots.getStackInSlot(0);
			ItemStack receiver = this.slots.getStackInSlot(1);
			ItemStack target = DisenchantingUtils.getAppropriateResultTarget(receiver);

			if (!itemstack.isEmpty() && !target.isEmpty() && DisenchantingUtils.disenchant(itemstack.copy(), target,
					this.tileentityAutomatic != null, false, this.world, this.posBlock, this.random)) {
				if (!(ItemStack.areItemStacksEqual(this.slots.getStackInSlot(2), target)))
					this.slots.setInventorySlotContents(2, target);
			} else if (!this.slots.getStackInSlot(2).isEmpty())
				this.slots.setInventorySlotContents(2, ItemStack.EMPTY);
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer p) {
		super.onContainerClosed(p);

		if (this.tileentityAutomatic == null)
			if (!this.world.isRemote)
				this.clearContainer(p, p.world, this.slots);
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return !this.world.getBlockState(this.posBlock).getBlock().equals(CommonProxy.disenchantmentTable) ? false
				: p.getDistanceSq((double) this.posBlock.getX() + 0.5D, (double) this.posBlock.getY() + 0.5D,
						(double) this.posBlock.getZ() + 0.5D) <= 64.0D;
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
