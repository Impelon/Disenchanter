package de.impelon.disenchanter.inventory;

import de.impelon.disenchanter.DisenchanterConfig;
import de.impelon.disenchanter.DisenchantingProperties;
import de.impelon.disenchanter.DisenchantingUtils;
import de.impelon.disenchanter.block.TableVariant;
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
	protected long lastTimeChanged;
	protected int disenchantingIndex = 0;
	protected boolean outputLocked = false;

	public ContainerDisenchantmentManual(InventoryPlayer playerInventory, World world, BlockPos position) {
		super(playerInventory, world, position);
		this.lastTimeChanged = this.world.getTotalWorldTime();
	}

	@Override
	protected IDisenchantmentItemHandler getTableInventory() {
		if (this.tableContent == null) {
			this.tableContent = new DisenchantmentItemStackHandler() {
				@Override
				protected void onContentsChanged(int slot) {
					super.onContentsChanged(slot);
					onTableContentChanged(slot);
				};
			};
		}
		return this.tableContent;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (Math.abs(this.world.getTotalWorldTime() - this.lastTimeChanged) >= DisenchanterConfig.disenchanting.ticksCyclingDisenchanting) {
			DisenchantingProperties properties = DisenchantingProperties.getPropertiesFromStateAt(this.world,
					this.position);
			if (properties != null && properties.is(TableVariant.CYCLING)) {
				this.disenchantingIndex++;
				this.lastTimeChanged = this.world.getTotalWorldTime();
				this.updateOutput(true);
			}
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer p) {
		super.onContainerClosed(p);

		if (!this.world.isRemote)
			InventoryUtils.returnInventoryToPlayer(p, p.world, this.getTableInventory());
	}

	protected void onTableContentChanged(int slot) {
		if (slot != OUTPUT_SLOT) {
			this.updateOutput(true);
			this.disenchantingIndex = 0;
			this.lastTimeChanged = this.world.getTotalWorldTime();
		}
		this.detectAndSendChanges();
	}

	public void updateOutput(boolean ignoreEnchantmentLoss) {
		if (!this.world.isRemote && !this.outputLocked) {
			DisenchantmentItemStackHandler virtualContent = new DisenchantmentItemStackHandler();
			virtualContent.setSourceStack(this.getTableInventory().getSourceStack().copy());
			virtualContent.setReceiverStack(this.getTableInventory().getReceiverStack().copy());
			ItemStack output = DisenchantingUtils.simulateDisenchantingInInventory(virtualContent,
					ignoreEnchantmentLoss, this.disenchantingIndex, this.world, this.position, this.world.rand);
			if (!output.isEmpty()) {
				if (!(ItemStack.areItemStacksEqual(this.getTableInventory().getOutputStack(), output)))
					this.getTableInventory().setOutputStack(output);
			} else if (!this.getTableInventory().getOutputStack().isEmpty())
				this.getTableInventory().setOutputStack(ItemStack.EMPTY);
		}
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer player) {
		if (slot == OUTPUT_SLOT && clickType != ClickType.CLONE) {
			if (!this.world.isRemote) {
				this.updateOutput(false);
				this.outputLocked = true; // enter "critical" section
				boolean wasEmpty = this.getTableInventory().getOutputStack().isEmpty();
				ItemStack result = super.slotClick(slot, dragType, clickType, player);
				if (this.getTableInventory().getOutputStack().isEmpty() && !wasEmpty)
					DisenchantingUtils.disenchantInInventory(this.getTableInventory(), this.disenchantingIndex,
							this.world, this.position, this.world.rand);
				this.outputLocked = false; // leave "critical" section
				this.updateOutput(true);
				if (player instanceof EntityPlayerMP)
					((EntityPlayerMP) player).sendContainerToPlayer(this);
				return result;
			} else if (clickType == ClickType.QUICK_MOVE)
				return ItemStack.EMPTY;
		}
		return super.slotClick(slot, dragType, clickType, player);
	}

}
