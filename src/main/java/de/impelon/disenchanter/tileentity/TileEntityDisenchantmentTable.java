package de.impelon.disenchanter.tileentity;

import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.inventory.ContainerDisenchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntityEnchantmentTable;

public class TileEntityDisenchantmentTable extends TileEntityEnchantmentTable {

	@Override
	public String getName() {
		return this.hasCustomName() ? super.getName() : "container.disenchant";
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerDisenchantment(playerInventory, this.world, this.pos);
	}

	@Override
	public String getGuiID() {
		return DisenchanterMain.MODID + ":disenchanting_table";
	}

}
