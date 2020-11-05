package de.impelon.disenchanter.inventory;

import java.util.Random;

import de.impelon.disenchanter.DisenchantingUtils;
import de.impelon.disenchanter.proxy.CommonProxy;
import de.impleon.disenchanter.tileentity.TileEntityDisenchantmentTableAutomatic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerDisenchantment extends Container {

	protected static final int SOURCE_SLOT = DisenchantmentItemStackHandler.SOURCE_SLOT;
	protected static final int RECEIVER_SLOT = DisenchantmentItemStackHandler.RECEIVER_SLOT;
	protected static final int OUTPUT_SLOT = DisenchantmentItemStackHandler.OUTPUT_SLOT;
	protected static final int FIRST_NON_TABLE_SLOT = OUTPUT_SLOT + 1;

	private boolean isAutomatic;
	private World world;
	private BlockPos posBlock;
	private Random random = new Random();
	private DisenchantmentItemStackHandler tableContent;

	public ContainerDisenchantment(InventoryPlayer pInventory, World w, BlockPos pos) {
		this.world = w;
		this.posBlock = pos;
		this.isAutomatic = false;

		TileEntity te = this.world.getTileEntity(pos);
		if (te instanceof TileEntityDisenchantmentTableAutomatic) {
			this.isAutomatic = true;
			this.tableContent = (DisenchantmentItemStackHandler) te
					.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		} else {
			this.tableContent = new DisenchantmentItemStackHandler() {
				@Override
				protected void onContentsChanged(int slot) {
					super.onContentsChanged(slot);
					ContainerDisenchantment.this.onTableContentChanged();
				};
			};
		}

		this.addSlotToContainer(new SlotItemHandler(this.tableContent, SOURCE_SLOT, 26, 35));
		this.addSlotToContainer(new SlotItemHandler(this.tableContent, RECEIVER_SLOT, 75, 35));
		this.addSlotToContainer(new SlotItemHandler(this.tableContent, OUTPUT_SLOT, 133, 35) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}
			
//			@Override
//			public ItemStack onTake(EntityPlayer p, ItemStack stack) {
//				if (isAutomatic)
//					return stack;
//
//				DisenchantingUtils.disenchantInInventory(tableContent, isAutomatic, world, posBlock, random);
//				return stack;
//			}
//			
//			@Override
//			public ItemStack decrStackSize(int amount) {
//				if (!isAutomatic)
//					DisenchantingUtils.disenchantInInventory(tableContent, isAutomatic, world, posBlock, random);
//				ItemStack result = super.decrStackSize(amount);
//				System.out.println(result);
//				return result;
//			}

		});

		int tmp;

		for (tmp = 0; tmp < 3; tmp++)
			for (int col = 0; col < 9; col++)
				this.addSlotToContainer(new Slot(pInventory, col + tmp * 9 + 9, 8 + col * 18, 84 + tmp * 18));

		for (tmp = 0; tmp < 9; tmp++)
			this.addSlotToContainer(new Slot(pInventory, tmp, 8 + tmp * 18, 142));

	}

	protected void onTableContentChanged() {
		if (!this.isAutomatic)
			this.updateOutput();
		this.detectAndSendChanges();
	}

	public void updateOutput() {
		if (!this.world.isRemote && !this.isAutomatic) {
			ItemStack source = this.tableContent.getSourceStack();
			ItemStack receiver = this.tableContent.getReceiverStack();
			ItemStack target = DisenchantingUtils.getAppropriateResultTarget(receiver);
			if (!source.isEmpty() && !target.isEmpty() && DisenchantingUtils.disenchant(source.copy(), target,
					this.isAutomatic, true, this.world, this.posBlock, this.random)) {
				if (!(ItemStack.areItemStacksEqual(this.tableContent.getOutputStack(), target)))
					this.tableContent.setOutputStack(target);
			} else if (!this.tableContent.getOutputStack().isEmpty())
				this.tableContent.setOutputStack(ItemStack.EMPTY);
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer p) {
		super.onContainerClosed(p);

		if (!this.isAutomatic)
			if (!this.world.isRemote)
				InventoryUtils.returnInventoryToPlayer(p, p.world, this.tableContent);
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return !this.world.getBlockState(this.posBlock).getBlock().equals(CommonProxy.disenchantmentTable) ? false
				: p.getDistanceSq(this.posBlock.add(0.5, 0.5, 0.5)) <= 64.0D;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer p, int slotID) {
		ItemStack itemstackPrev = ItemStack.EMPTY;
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack()) {
			itemstack = slot.getStack();
			itemstackPrev = itemstack.copy();

			switch (slotID) {
			case OUTPUT_SLOT:
				if (!this.mergeItemStack(itemstack, FIRST_NON_TABLE_SLOT, this.inventorySlots.size(), true))
					return ItemStack.EMPTY;
				break;
			case SOURCE_SLOT:
			case RECEIVER_SLOT:
				if (!this.mergeItemStack(itemstack, FIRST_NON_TABLE_SLOT, this.inventorySlots.size(), true))
					return ItemStack.EMPTY;
				break;
			default: {
				ItemStack i = InventoryUtils.copyStackWithSize(itemstack, 1);
				int slockToCheck = this.tableContent.isItemValid(DisenchantmentItemStackHandler.RECEIVER_SLOT, i)
						? RECEIVER_SLOT
						: SOURCE_SLOT;
				if (this.inventorySlots.get(slockToCheck).getHasStack() || !this.mergeItemStack(i, slockToCheck, slockToCheck + 1, false)) 
					return ItemStack.EMPTY;
				itemstack.shrink(1);
			}
			}
			if (itemstack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (itemstack.getCount() == itemstackPrev.getCount())
				return ItemStack.EMPTY;

			slot.onTake(p, itemstack);
		}

		return itemstack;
	}
}
