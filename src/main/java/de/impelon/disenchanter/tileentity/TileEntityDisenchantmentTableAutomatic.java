package de.impelon.disenchanter.tileentity;

import de.impelon.disenchanter.DisenchanterConfig;
import de.impelon.disenchanter.DisenchantingUtils;
import de.impelon.disenchanter.inventory.DisenchantmentItemStackHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityDisenchantmentTableAutomatic extends TileEntityDisenchantmentTable {

	protected DisenchantmentItemStackHandler tableContent = new DisenchantmentItemStackHandler() {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			TileEntityDisenchantmentTableAutomatic.this.markDirty();
		};
	};

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.tableContent.deserializeNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.merge(this.tableContent.serializeNBT());
		return nbt;
	}

	@Override
	public void update() {
		super.update();
		if (!this.world.isRemote
				&& this.tickCount % DisenchanterConfig.disenchanting.ticksAutomaticDisenchantmentProcess == 0) {
			DisenchantingUtils.disenchantInInventory(this.tableContent, this.world, this.pos, this.world.rand);
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == null)
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.tableContent);

			switch (facing) {
			case NORTH:
			case EAST:
			case SOUTH:
			case WEST:
			case UP:
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.tableContent.getInputInventory());
			case DOWN:
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.tableContent.getOutputInventory());
			}
		}
		return super.getCapability(capability, facing);
	}

}