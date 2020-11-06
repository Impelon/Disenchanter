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
	public void readFromNBT(NBTTagCompound nbtData) {
		super.readFromNBT(nbtData);
		this.tableContent.deserializeNBT(nbtData);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtData) {
		super.writeToNBT(nbtData);
		nbtData.merge(this.tableContent.serializeNBT());

		return nbtData;
	}

	@Override
	public void update() {
		super.update();
		if (!this.world.isRemote
				&& this.tickCount % DisenchanterConfig.disenchanting.ticksAutomaticDisenchantmentProcess == 0) {
			DisenchantingUtils.disenchantInInventory(this.tableContent, true, this.world, this.pos, this.world.rand);
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
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(tableContent);
        }
		return super.getCapability(capability, facing);
	}
	
//	TODO
//	Like this:
//	https://github.com/Draco18s/ReasonableRealism/blob/master/src/main/java/com/draco18s/hardlib/api/internal/inventory/OutputItemStackHandler.java
//	possibly with: CombinedInvWrapper
//	
//	@Override
//	public boolean canInsertItem(int slotID, ItemStack stack, EnumFacing side) {
//		return slotID == 2 ? false : isItemValidForSlot(slotID, stack);
//	}
//
//	@Override
//	public boolean canExtractItem(int slotID, ItemStack stack, EnumFacing side) {
//		if (slotID == 1)
//			return false;
//		return true;
//	}

}