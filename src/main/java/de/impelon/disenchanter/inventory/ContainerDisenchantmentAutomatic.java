package de.impelon.disenchanter.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

public class ContainerDisenchantmentAutomatic extends ContainerDisenchantmentBase {
	
	protected IDisenchantmentItemHandler tableContent;

	public ContainerDisenchantmentAutomatic(InventoryPlayer playerInventory, World world, BlockPos position) {
		super(playerInventory, world, position);
	}

	@Override
	protected IDisenchantmentItemHandler getTableInventory() {
		if (this.tableContent == null) {
			TileEntity te = this.world.getTileEntity(this.position);
			this.tableContent = (IDisenchantmentItemHandler) te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		}
		return this.tableContent;
	}

}
