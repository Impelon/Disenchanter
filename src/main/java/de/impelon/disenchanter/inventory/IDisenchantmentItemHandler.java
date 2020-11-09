package de.impelon.disenchanter.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface IDisenchantmentItemHandler extends IItemHandler {
	
	/**
	 * Set the itemstack of the source slot.
	 * @param stack the itemstack to set
	 */
	public abstract void setSourceStack(ItemStack stack);
	
	/**
	 * Set the itemstack of the receiver slot.
	 * @param stack the itemstack to set
	 */
	public abstract void setReceiverStack(ItemStack stack);
	
	/**
	 * Set the itemstack of the output slot.
	 * @param stack the itemstack to set
	 */
	public abstract void setOutputStack(ItemStack stack);
	
	/**
	 * Return the itemstack in the source slot.
	 */
	public abstract ItemStack getSourceStack();
	
	/**
	 * Return the itemstack in the receiver slot.
	 */
	public abstract ItemStack getReceiverStack();
	
	/**
	 * Return the itemstack in the output slot.
	 */
	public abstract ItemStack getOutputStack();

}
