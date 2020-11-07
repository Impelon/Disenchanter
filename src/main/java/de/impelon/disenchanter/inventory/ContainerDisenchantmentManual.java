package de.impelon.disenchanter.inventory;

import de.impelon.disenchanter.DisenchantingUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerDisenchantmentManual extends ContainerDisenchantmentBase {
	
	/**
	 * Must <b>NOT</b> be initialized here, as that would override whatever was
	 * initialized during the calling of the super-constructor.
	 */
	protected DisenchantmentItemStackHandler tableContent;

	public ContainerDisenchantmentManual(InventoryPlayer playerInventory, World world, BlockPos position) {
		super(playerInventory, world, position);
	}

	@Override
	protected AbstractDisenchantmentItemStackHandler getTableInventory() {
		if (this.tableContent == null) {
			this.tableContent = new DisenchantmentItemStackHandler() {
				@Override
				protected void onContentsChanged(int slot) {
					super.onContentsChanged(slot);
					ContainerDisenchantmentManual.this.onTableContentChanged(slot);
				};
			};
		}
		return this.tableContent;
	}

	@Override
	public void onContainerClosed(EntityPlayer p) {
		super.onContainerClosed(p);

		if (!this.world.isRemote)
			InventoryUtils.returnInventoryToPlayer(p, p.world, this.getTableInventory());
	}

	protected void onTableContentChanged(int slot) {
		if (slot != OUTPUT_SLOT)
			this.updateOutput(true);
		this.detectAndSendChanges();
	}

	public void updateOutput(boolean ignoreEnchantmentLoss) {
		if (!this.world.isRemote) {
			DisenchantmentItemStackHandler virtualContent = new DisenchantmentItemStackHandler();
			virtualContent.setSourceStack(this.getTableInventory().getSourceStack().copy());
			virtualContent.setReceiverStack(this.getTableInventory().getReceiverStack().copy());
			ItemStack output = DisenchantingUtils.simulateDisenchantingInInventory(virtualContent, false, ignoreEnchantmentLoss, this.world, this.position, this.random);
			if (!output.isEmpty()) {
				if (!(ItemStack.areItemStacksEqual(this.getTableInventory().getOutputStack(), output)))
					this.getTableInventory().setOutputStack(output);
			} else if (!this.getTableInventory().getOutputStack().isEmpty())
				this.getTableInventory().setOutputStack(ItemStack.EMPTY);
		}
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer player) {
		if (slot == OUTPUT_SLOT && clickType != ClickType.CLONE && !this.world.isRemote) {
			this.updateOutput(false);
			boolean wasEmpty = this.getTableInventory().getOutputStack().isEmpty();
			ItemStack result = super.slotClick(slot, dragType, clickType, player);
			if (this.getTableInventory().getOutputStack().isEmpty() && !wasEmpty)
				DisenchantingUtils.disenchantInInventory(this.getTableInventory(), false, this.world, this.position, this.random);
			this.updateOutput(true);
			if (player instanceof EntityPlayerMP)
				((EntityPlayerMP) player).sendContainerToPlayer(this);
			return result;
		}
		return super.slotClick(slot, dragType, clickType, player);
	}

}
