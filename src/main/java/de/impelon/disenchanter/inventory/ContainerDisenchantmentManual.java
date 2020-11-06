package de.impelon.disenchanter.inventory;

import de.impelon.disenchanter.DisenchantingUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerDisenchantmentManual extends ContainerDisenchantmentBase {
	
	protected boolean outputUpdatesDisabeled = false;
	protected AbstractDisenchantmentItemStackHandler tableContent = null;

	public ContainerDisenchantmentManual(InventoryPlayer playerInventory, World world, BlockPos position) {
		super(playerInventory, world, position);
	}
	
	@Override
	protected AbstractDisenchantmentItemStackHandler getTableInventory() {
		if (this.tableContent == null) {
			System.out.println("manual was null before " + this.hashCode());
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
		this.updateOutput();
		this.detectAndSendChanges();
	}

	public void updateOutput() {
		if (!this.world.isRemote && !this.outputUpdatesDisabeled) {
			ItemStack source = this.getTableInventory().getSourceStack();
			ItemStack receiver = this.getTableInventory().getReceiverStack();
			ItemStack target = DisenchantingUtils.getAppropriateResultTarget(receiver);
			System.out.println("Output: " + source + " " + receiver + " " + target);
			if (!source.isEmpty() && !target.isEmpty() && DisenchantingUtils.disenchant(source.copy(), target,
					false, true, this.world, this.position, this.random)) {
				if (!(ItemStack.areItemStacksEqual(this.getTableInventory().getOutputStack(), target))) 
					this.getTableInventory().setOutputStack(target);
			} else if (!this.getTableInventory().getOutputStack().isEmpty())
				this.getTableInventory().setOutputStack(ItemStack.EMPTY);
		}
	}
	
	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer player) {
		if (slot == OUTPUT_SLOT) {
			this.outputUpdatesDisabeled = true;
			ItemStack output = DisenchantingUtils.disenchantInInventory(this.getTableInventory(), false, this.world, this.position, this.random);
			this.getTableInventory().setOutputStack(output);
		}
		ItemStack result = super.slotClick(slot, dragType, clickType, player);
		this.outputUpdatesDisabeled = false;
		return result;
	}

}
