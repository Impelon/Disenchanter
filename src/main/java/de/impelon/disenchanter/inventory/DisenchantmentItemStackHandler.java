package de.impelon.disenchanter.inventory;

import de.impelon.disenchanter.DisenchantingUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class DisenchantmentItemStackHandler extends ItemStackHandler implements IDisenchantmentItemHandler {

	/**
	 * This is the slot for the item that gets disenchanted.
	 */
	public static final int SOURCE_SLOT = 0;
	/**
	 * This is the slot for the item that the enchantment is transferred to.
	 */
	public static final int RECEIVER_SLOT = 1;
	/**
	 * This is the slot for the output of the disenchantment-process
	 */
	public static final int OUTPUT_SLOT = 2;
	
	protected static final int NUMBER_OF_SLOTS = OUTPUT_SLOT + 1;

	protected IItemHandlerModifiable inputInventory;
	protected IItemHandlerModifiable outputInventory;

	public DisenchantmentItemStackHandler() {
		super(NUMBER_OF_SLOTS);
		this.inputInventory = new RangedWrapper(this, SOURCE_SLOT, OUTPUT_SLOT + 1) {
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if (slot != SOURCE_SLOT && slot != RECEIVER_SLOT)
					return stack;
				return super.insertItem(slot, stack, simulate);
			}
			
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				if (slot != OUTPUT_SLOT)
					return ItemStack.EMPTY;
				return super.extractItem(slot, amount, simulate);
			}
		};
		this.outputInventory = new RangedWrapper(this, SOURCE_SLOT, OUTPUT_SLOT + 1) {
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				if (slot != SOURCE_SLOT && slot != OUTPUT_SLOT)
					return ItemStack.EMPTY;
				return super.extractItem(slot, amount, simulate);
			}
		};
	}

	public IItemHandlerModifiable getInputInventory() {
		return this.inputInventory;
	}

	public IItemHandlerModifiable getOutputInventory() {
		return this.outputInventory;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		switch (slot) {
		case SOURCE_SLOT:
			return !DisenchantingUtils.getAvailableEnchantmentIndices(stack).isEmpty();
		case RECEIVER_SLOT:
			return !DisenchantingUtils.getAppropriateResultTarget(stack).isEmpty();
		}
		return super.isItemValid(slot, stack);
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (!this.isItemValid(slot, stack))
			return stack;
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public void setSourceStack(ItemStack stack) {
		this.setStackInSlot(SOURCE_SLOT, stack);
	}

	@Override
	public void setReceiverStack(ItemStack stack) {
		this.setStackInSlot(RECEIVER_SLOT, stack);
	}

	@Override
	public void setOutputStack(ItemStack stack) {
		this.setStackInSlot(OUTPUT_SLOT, stack);
	}

	@Override
	public ItemStack getSourceStack() {
		return this.getStackInSlot(SOURCE_SLOT);
	}

	@Override
	public ItemStack getReceiverStack() {
		return this.getStackInSlot(RECEIVER_SLOT);
	}

	@Override
	public ItemStack getOutputStack() {
		return this.getStackInSlot(OUTPUT_SLOT);
	}

}
