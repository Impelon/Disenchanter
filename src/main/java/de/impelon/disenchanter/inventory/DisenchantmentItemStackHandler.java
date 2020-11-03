package de.impelon.disenchanter.inventory;

import de.impelon.disenchanter.item.ItemExperienceJar;
import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class DisenchantmentItemStackHandler extends AbstractDisenchantmentItemStackHandler {

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
	protected static final int NUMBER_OF_SLOTS = 3;

	public DisenchantmentItemStackHandler() {
		super(NUMBER_OF_SLOTS);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		switch (slot) {
		case SOURCE_SLOT:
			return stack.getEnchantmentTagList() != null;
		case RECEIVER_SLOT:
			return stack.getItem().equals(Items.BOOK) || (stack.getItem().equals(CommonProxy.itemExperienceJar)
					&& ItemExperienceJar.hasAvailableExperienceCapacity(stack));
		}
		return super.isItemValid(slot, stack);
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