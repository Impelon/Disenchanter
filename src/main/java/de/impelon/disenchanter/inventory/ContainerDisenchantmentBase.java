package de.impelon.disenchanter.inventory;

import java.util.Random;

import de.impelon.disenchanter.proxy.CommonProxy;
import de.impelon.disenchanter.tileentity.TileEntityDisenchantmentTableAutomatic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

public abstract class ContainerDisenchantmentBase extends Container {

	protected static final int SOURCE_SLOT = DisenchantmentItemStackHandler.SOURCE_SLOT;
	protected static final int RECEIVER_SLOT = DisenchantmentItemStackHandler.RECEIVER_SLOT;
	protected static final int OUTPUT_SLOT = DisenchantmentItemStackHandler.OUTPUT_SLOT;
	protected static final int FIRST_NON_TABLE_SLOT = OUTPUT_SLOT + 1;

	protected World world;
	protected BlockPos position;
	protected Random random = new Random();

	public static ContainerDisenchantmentBase create(InventoryPlayer playerInventory, World world, BlockPos position) {
		TileEntity te = world.getTileEntity(position);
		if (te instanceof TileEntityDisenchantmentTableAutomatic)
			return new ContainerDisenchantmentAutomatic(playerInventory, world, position);
		return new ContainerDisenchantmentManual(playerInventory, world, position);
	}

	public ContainerDisenchantmentBase(InventoryPlayer playerInventory, World world, BlockPos position) {
		this.world = world;
		this.position = position;

		this.addTableSlots();
		this.addPlayerSlots(playerInventory);
	}
	
	/**
	 * Access to this should be fast, as it is needed often. Allows implementations
	 * to act on different inventory-systems (for example tile entity vs. virtual
	 * inventory).
	 * @return the disenchantment-inventory this container is linked to 
	 */
	protected abstract AbstractDisenchantmentItemStackHandler getTableInventory();
	
	protected void addTableSlots() {
		this.addSlotToContainer(new SlotItemHandler(this.getTableInventory(), SOURCE_SLOT, 26, 35));
		this.addSlotToContainer(new SlotItemHandler(this.getTableInventory(), RECEIVER_SLOT, 75, 35));
		this.addSlotToContainer(new SlotItemHandler(this.getTableInventory(), OUTPUT_SLOT, 133, 35) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}

		});
	}

	protected void addPlayerSlots(InventoryPlayer playerInventory) {
		int tmp;

		for (tmp = 0; tmp < 3; tmp++)
			for (int col = 0; col < 9; col++)
				this.addSlotToContainer(new Slot(playerInventory, col + tmp * 9 + 9, 8 + col * 18, 84 + tmp * 18));

		for (tmp = 0; tmp < 9; tmp++)
			this.addSlotToContainer(new Slot(playerInventory, tmp, 8 + tmp * 18, 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return !this.world.getBlockState(this.position).getBlock().equals(CommonProxy.disenchantmentTable) ? false
				: p.getDistanceSq(this.position.add(0.5, 0.5, 0.5)) <= 64.0D;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer p, int slotID) {
		ItemStack itemstackPrev = ItemStack.EMPTY;
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack()) {
			itemstack = slot.getStack();
			itemstackPrev = itemstack.copy();

			switch (slotID) {
			case OUTPUT_SLOT:
				if (!this.mergeItemStack(itemstack, FIRST_NON_TABLE_SLOT, this.inventorySlots.size(), true))
					return ItemStack.EMPTY;
				break;
			case SOURCE_SLOT:
			case RECEIVER_SLOT:
				if (!this.mergeItemStack(itemstack, FIRST_NON_TABLE_SLOT, this.inventorySlots.size(), true))
					return ItemStack.EMPTY;
				break;
			default: {
				ItemStack i = InventoryUtils.copyStackWithSize(itemstack, 1);
				int slockToCheck = this.getTableInventory().isItemValid(DisenchantmentItemStackHandler.RECEIVER_SLOT, i)
						? RECEIVER_SLOT
						: SOURCE_SLOT;
				if (this.inventorySlots.get(slockToCheck).getHasStack()
						|| !this.mergeItemStack(i, slockToCheck, slockToCheck + 1, false))
					return ItemStack.EMPTY;
				itemstack.shrink(1);
			}
			}
			if (itemstack.isEmpty())
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
