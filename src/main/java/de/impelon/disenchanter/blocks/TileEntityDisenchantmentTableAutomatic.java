package de.impelon.disenchanter.blocks;

import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.proxies.CommonProxy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeHooks;

public class TileEntityDisenchantmentTableAutomatic extends TileEntityDisenchantmentTable implements ISidedInventory {

	private NonNullList<ItemStack> disenchantmentTableContent = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
	private int[] accessible = new int[this.getSizeInventory()];
	
	public TileEntityDisenchantmentTableAutomatic() {
		for (byte n = 0; n < this.getSizeInventory(); n++)
			this.accessible[n] = n;
	}
	
	public void disenchant() {
		if (this.getStackInSlot(2) != ItemStack.EMPTY)
			return;

		ItemStack itemstack = this.getStackInSlot(0);
		ItemStack bookstack = this.getStackInSlot(1);
		ItemStack outputBookstack = new ItemStack(Items.ENCHANTED_BOOK);
		BlockDisenchantmentTable table = DisenchanterMain.proxy.disenchantmentTable;

		if (itemstack != ItemStack.EMPTY && bookstack != ItemStack.EMPTY && itemstack.getTagCompound() != null) {
			if (itemstack.getTagCompound().getTag("InfiTool") != null)
				if (DisenchanterMain.config.get("disenchanting", "EnableTCBehaviour", true).getBoolean())
					return;
			if (itemstack.getTagCompound().getTag("TinkerData") != null)
				if (DisenchanterMain.config.get("disenchanting", "EnableTCBehaviour", true).getBoolean())
					return;
				
			float power = table.getEnchantingPower(this.world, this.pos);
			int flatDmg = DisenchanterMain.config.get("disenchanting", "FlatDamage", 10).getInt();
			double durabiltyDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamage", 0.025).getDouble();
			double reduceableDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamageReduceable", 0.2).getDouble();
			double machineDmgMultiplier = DisenchanterMain.config.get("disenchanting", "MachineDamageMultiplier", 2.5).getDouble();

			while (table.getEnchantmentList(itemstack) != null) {
				table.transferEnchantment(itemstack, outputBookstack, 0, this.random);
				
				itemstack.attemptDamageItem((int) (machineDmgMultiplier * (flatDmg + itemstack.getMaxDamage() * durabiltyDmg + 
						itemstack.getMaxDamage() * (reduceableDmg / power))), this.random);
				if (itemstack.getItemDamage() > itemstack.getMaxDamage()) {
					this.setInventorySlotContents(0, ItemStack.EMPTY);
					break;
				}
				
				if (!(world.getBlockState(this.pos).getValue(table.BULKDISENCHANTING)))
					break;
			}
			
			if (table.getEnchantmentList(itemstack) == null) {
				if (itemstack.getItem() == Items.ENCHANTED_BOOK)
					this.setInventorySlotContents(0, new ItemStack(Items.BOOK));
				if (world.getBlockState(this.pos).getValue(table.VOIDING))
					this.setInventorySlotContents(0, ItemStack.EMPTY);
			}
			
			if (bookstack.getCount() > 1)
				bookstack.setCount(bookstack.getCount() - 1);
			else
				bookstack = ItemStack.EMPTY;
			this.setInventorySlotContents(1, bookstack);
			if (outputBookstack.getTagCompound() != null && outputBookstack.getTagCompound().getTag("StoredEnchantments") != null)
				this.setInventorySlotContents(2, outputBookstack);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtData) {
		super.readFromNBT(nbtData);
		NBTTagList nbttaglist = nbtData.getTagList("Items", 10);
        this.disenchantmentTableContent = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);

        for (int n = 0; n < nbttaglist.tagCount(); n++) {
            NBTTagCompound nbtTagCompound = nbttaglist.getCompoundTagAt(n);
            byte b = nbtTagCompound.getByte("Slot");

            if (b >= 0 && b < this.disenchantmentTableContent.size())
            	this.disenchantmentTableContent.set(b, new ItemStack(nbtTagCompound));
        }
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtData) {
		super.writeToNBT(nbtData);
		
		NBTTagList nbttaglist = new NBTTagList();

		for (byte n = 0; n < this.disenchantmentTableContent.size(); n++) {
			if (this.disenchantmentTableContent.get(n) != ItemStack.EMPTY) {
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                nbtTagCompound.setByte("Slot", n);
                this.disenchantmentTableContent.get(n).writeToNBT(nbtTagCompound);
                nbttaglist.appendTag(nbtTagCompound);
            }
        }

        nbtData.setTag("Items", nbttaglist);
        
        return nbtData;
	}
	
	@Override
	public void update() {
		super.update();
		if (!this.world.isRemote && this.tickCount % DisenchanterMain.config.get("disenchanting", "AutomaticDisenchantmentCycleTicks", 100).getInt() == 0)
			this.disenchant();
	}
	
	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public ItemStack getStackInSlot(int slotID) {
		return this.disenchantmentTableContent.get(slotID);
	}

	@Override
	public ItemStack decrStackSize(int slotID, int count) {
		if (this.disenchantmentTableContent.get(slotID) != ItemStack.EMPTY) {
			ItemStack stack = this.disenchantmentTableContent.get(slotID);
			if (this.disenchantmentTableContent.get(slotID).getCount() < count)
				return removeStackFromSlot(slotID);
			else if (this.disenchantmentTableContent.get(slotID).getCount() == count)
				this.disenchantmentTableContent.set(slotID, ItemStack.EMPTY);
			return stack.splitStack(count);
		}
		return null;
	}
	
	@Override
	public ItemStack removeStackFromSlot(int slotID) {
		ItemStack stack = this.disenchantmentTableContent.get(slotID);
		this.disenchantmentTableContent.set(slotID, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slotID, ItemStack stack) {
		this.disenchantmentTableContent.set(slotID, stack);

		if (stack != ItemStack.EMPTY && stack.getCount() > getInventoryStackLimit())
			stack.setCount(this.getInventoryStackLimit());
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer p) {
		return !this.world.getBlockState(this.pos).getBlock().equals(CommonProxy.disenchantmentTable) ? false
				: p.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack stack) {
		if (slotID == 1)
			return stack.getItem().equals(Items.BOOK);
		else if (slotID == 0)
			return stack.getEnchantmentTagList() != null;
		return true;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return this.accessible;
	}

	@Override
	public boolean canInsertItem(int slotID, ItemStack stack, EnumFacing side) {
		return slotID == 2 ? false : isItemValidForSlot(slotID, stack);
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack stack, EnumFacing side) {
		if (slotID == 1)
			return false;
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int n = 0; n < this.disenchantmentTableContent.size(); n++)
	        this.setInventorySlotContents(n, ItemStack.EMPTY);
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.disenchantmentTableContent)
            if (!itemstack.isEmpty())
                return false;
        return true;
	}

}