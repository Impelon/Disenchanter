package de.impelon.disenchanter.tileentity;

import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.inventory.ContainerDisenchantmentBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityEnchantmentTable;

public class TileEntityDisenchantmentTable extends TileEntityEnchantmentTable {

	@Override
	public String getName() {
		return this.hasCustomName() ? super.getName() : "container.disenchant";
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return ContainerDisenchantmentBase.create(playerInventory, this.world, this.pos);
	}

	@Override
	public String getGuiID() {
		return DisenchanterMain.MODID + ":disenchanting_table";
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound updateTag = super.getUpdateTag();
		return super.writeToNBT(updateTag);
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		// Default implementation is absolutely fine;
		// This is just a reminder, that this implementation aspect has not been
		// overlooked.
		super.handleUpdateTag(tag);
	}

}
