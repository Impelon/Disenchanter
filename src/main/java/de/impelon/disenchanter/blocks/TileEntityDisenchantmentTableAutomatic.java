package de.impelon.disenchanter.blocks;

import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.proxies.CommonProxy;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeHooks;

public class TileEntityDisenchantmentTableAutomatic extends TileEntityDisenchantmentTable implements ISidedInventory {

	private ItemStack[] disenchantmentTableContent = new ItemStack[this.getSizeInventory()];
	private int[] accessible = new int[this.getSizeInventory()];
	
	public TileEntityDisenchantmentTableAutomatic() {
		for (byte n = 0; n < this.getSizeInventory(); n++)
			this.accessible[n] = n;
	}
	
	public void disenchant() {
		if (this.getStackInSlot(2) != null)
			return;
		ItemStack itemstack = this.getStackInSlot(0);
		ItemStack bookstack = this.getStackInSlot(1);

		if (itemstack != null && bookstack != null) {
			if (itemstack.stackTagCompound != null) {
				NBTTagList enchants = null;
				if (itemstack.stackTagCompound.getTag("ench") != null)
					enchants = (NBTTagList) itemstack.stackTagCompound.getTag("ench");
				else if (itemstack.stackTagCompound.getTag("StoredEnchantments") != null)
					enchants = (NBTTagList) itemstack.stackTagCompound.getTag("StoredEnchantments");
				else {
					this.setInventorySlotContents(2, (ItemStack) null);
					return;
				}
				
				if (enchants.tagCount() > 0) {
					NBTTagCompound enchant = enchants.getCompoundTagAt(0);
					int id = enchant.getInteger("id");
					int lvl = enchant.getInteger("lvl");

					ItemStack outputBookstack = new ItemStack(Items.enchanted_book);
					Items.enchanted_book.addEnchantment(outputBookstack, new EnchantmentData(id, lvl));

					this.setInventorySlotContents(2, (ItemStack) outputBookstack);
				} else {
					this.setInventorySlotContents(2, (ItemStack) null);
					return;
				}

				if (bookstack.stackSize > 1)
					bookstack.stackSize -= 1;
				else
					bookstack = (ItemStack) null;
				this.setInventorySlotContents(1, bookstack);

				int power = 1;
				for (int blockZ = -1; blockZ <= 1; ++blockZ) {
					for (int blockX = -1; blockX <= 1; ++blockX) {
						if ((blockZ != 0 || blockX != 0) && worldObj.isAirBlock(xCoord + blockX, yCoord, zCoord + blockZ)
								&& worldObj.isAirBlock(xCoord + blockX, yCoord + 1, zCoord + blockZ)) {
							power += ForgeHooks.getEnchantPower(worldObj, xCoord + blockX * 2, yCoord, zCoord + blockZ * 2);
							power += ForgeHooks.getEnchantPower(worldObj, xCoord + blockX * 2, yCoord + 1, zCoord + blockZ * 2);

							if (blockX != 0 && blockZ != 0) {
								power += ForgeHooks.getEnchantPower(worldObj, xCoord + blockX * 2, yCoord, zCoord + blockZ);
								power += ForgeHooks.getEnchantPower(worldObj, xCoord + blockX * 2, yCoord + 1, zCoord + blockZ);
								power += ForgeHooks.getEnchantPower(worldObj, xCoord + blockX, yCoord, zCoord + blockZ * 2);
								power += ForgeHooks.getEnchantPower(worldObj, xCoord + blockX, yCoord + 1, zCoord + blockZ * 2);
							}
						}
					}
				}
				
				if (power > 15)
					power = 15;
				int flatDmg = DisenchanterMain.config.get("disenchanting", "FlatDamage", 10).getInt();
				double durabiltyDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamage", 0.025).getDouble();
				double reduceableDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamageReduceable", 0.2).getDouble();
				double machineDmgMultiplier = DisenchanterMain.config.get("disenchanting", "MachineDamageMultiplier", 2.5).getDouble();
				double enchantmentLoss = DisenchanterMain.config.get("disenchanting", "EnchantmentLossChance", 0.0).getDouble();
				itemstack.attemptDamageItem((int) (machineDmgMultiplier * (flatDmg + itemstack.getMaxDamage() * durabiltyDmg + itemstack.getMaxDamage() * (reduceableDmg / power))), random);
				if (itemstack.getItemDamage() > itemstack.getMaxDamage()) {
					this.setInventorySlotContents(0, (ItemStack) null);
					return;
				}
				if (itemstack != null && itemstack.stackTagCompound != null) {
					enchants = null;
					if (itemstack.stackTagCompound.getTag("ench") != null) {
						enchants = (NBTTagList) itemstack.stackTagCompound.getTag("ench");
						byte loops = 1;
						if (random.nextFloat() <= enchantmentLoss)
							loops = (byte) (1 + random.nextInt(5));
						for (byte n = 0; n < loops; n++) {
							if (enchants.tagCount() > 1)
								enchants.removeTag(0);
							else if (itemstack.stackTagCompound.getTag("ench") != null)
								itemstack.stackTagCompound.removeTag("ench");
						}
					} else if (itemstack.stackTagCompound.getTag("StoredEnchantments") != null) {
						enchants = (NBTTagList) itemstack.stackTagCompound.getTag("StoredEnchantments");
						if (enchants.tagCount() > 1)
							enchants.removeTag(0);
						else
							this.setInventorySlotContents(0, new ItemStack(Items.book));
					}
				}
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtData) {
		super.readFromNBT(nbtData);
		NBTTagList nbttaglist = nbtData.getTagList("Items", 10);
        this.disenchantmentTableContent = new ItemStack[this.getSizeInventory()];

        for (int n = 0; n < nbttaglist.tagCount(); n++) {
            NBTTagCompound nbtTagCompound = nbttaglist.getCompoundTagAt(n);
            byte b = nbtTagCompound.getByte("Slot");

            if (b >= 0 && b < this.disenchantmentTableContent.length)
            	this.disenchantmentTableContent[b] = ItemStack.loadItemStackFromNBT(nbtTagCompound);
        }
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbtData) {
		super.writeToNBT(nbtData);
		
		NBTTagList nbttaglist = new NBTTagList();

        for (int n = 0; n < this.disenchantmentTableContent.length; n++) {
            if (this.disenchantmentTableContent[n] != null) {
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                nbtTagCompound.setByte("Slot", (byte) n);
                this.disenchantmentTableContent[n].writeToNBT(nbtTagCompound);
                nbttaglist.appendTag(nbtTagCompound);
            }
        }

        nbtData.setTag("Items", nbttaglist);
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (this.tickCount % 100 == 0)
			this.disenchant();
	}

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public ItemStack getStackInSlot(int slotID) {
		return this.disenchantmentTableContent[slotID];
	}

	@Override
	public ItemStack decrStackSize(int slotID, int count) {
		if (this.disenchantmentTableContent[slotID] != null) {
			ItemStack stack = this.disenchantmentTableContent[slotID];
			if (this.disenchantmentTableContent[slotID].stackSize < count)
				return getStackInSlotOnClosing(slotID);
			else if (this.disenchantmentTableContent[slotID].stackSize == count)
				this.disenchantmentTableContent[slotID] = null;
			return stack.splitStack(count);
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotID) {
		ItemStack stack = this.disenchantmentTableContent[slotID];
		this.disenchantmentTableContent[slotID] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slotID, ItemStack stack) {
		this.disenchantmentTableContent[slotID] = stack;

	     if (stack != null && stack.stackSize > getInventoryStackLimit())
	    	 stack.stackSize = this.getInventoryStackLimit();
	}

	@Override
	public String getInventoryName() {
		return this.getName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.hasCustomName();
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p) {
		return this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) != CommonProxy.disenchantmentTable ? false
				: p.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack stack) {
		if (slotID == 1)
			return stack.getItem().equals(Items.book);
		else if (slotID == 0)
			return stack.getEnchantmentTagList() != null;
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return this.accessible;
	}

	@Override
	public boolean canInsertItem(int slotID, ItemStack stack, int side) {
		return slotID == 2 ? false : isItemValidForSlot(slotID, stack);
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack stack, int side) {
		if (slotID == 1)
			return false;
		return true;
	}

}
