package de.impelon.disenchanter.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;


/**
 * Class to provide consistent access to helpful methods related to inventories/item-handlers.
 * Mostly reimplements utility-functions of the legacy {@link IInventory} or delegates to said functions.
 */
public class InventoryUtils {
	
	public static ItemStack copyStackWithSize(ItemStack itemstack, int size) {
		return ItemHandlerHelper.copyStackWithSize(itemstack, size);
	}

	public static NonNullList<ItemStack> extractItemsFromInventory(IItemHandler inventory, boolean simulate) {
		NonNullList<ItemStack> items = NonNullList.create();
		for (int i = 0; i < inventory.getSlots(); i++)
			items.add(inventory.extractItem(i, inventory.getSlotLimit(i), simulate));
		return items;
	}

	public static NonNullList<ItemStack> extractItemsFromInventory(IInventory inventory, boolean simulate) {
		NonNullList<ItemStack> items = NonNullList.create();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (simulate)
				items.add(inventory.getStackInSlot(i));
			else
				items.add(inventory.removeStackFromSlot(i));
		}
		return items;
	}

	public static int calcRedstoneFromInventory(TileEntity tileentity) {
		if (tileentity instanceof IInventory)
			return calcRedstoneFromInventory((IInventory) tileentity);
		IItemHandler inventory = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (inventory == null)
			return 0;
		return calcRedstoneFromInventory(inventory);
	}

	public static int calcRedstoneFromInventory(IItemHandler inventory) {
		return ItemHandlerHelper.calcRedstoneFromInventory(inventory);
	}

	public static int calcRedstoneFromInventory(IInventory inventory) {
		return Container.calcRedstoneFromInventory(inventory);
	}

	public static void dropInventory(World world, BlockPos position, TileEntity tileentity) {
		if (tileentity instanceof IInventory) {
			dropInventory(world, position, (IInventory) tileentity);
			return;
		}
		IItemHandler inventory = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (inventory == null)
			return;
		dropInventory(world, position, inventory);
	}

	public static void dropInventory(World world, BlockPos position, IItemHandler inventory) {
		dropItems(world, position, extractItemsFromInventory(inventory, true));
	}

	public static void dropInventory(World world, BlockPos position, IInventory inventory) {
		InventoryHelper.dropInventoryItems(world, position, inventory);
	}

	public static void dropItems(World world, BlockPos position, List<ItemStack> items) {
		for (ItemStack itemstack : items) {
			if (!itemstack.isEmpty())
				InventoryHelper.spawnItemStack(world, position.getX(), position.getY(), position.getZ(), itemstack);
		}
	}

	public static void returnInventoryToPlayer(EntityPlayer player, World world, IItemHandler inventory) {
		returnItemsToPlayer(player, world, extractItemsFromInventory(inventory, false));
	}

	/**
	 * Drop-in replacement for {@link Container#clearContainer}
	 */
	public static void returnInventoryToPlayer(EntityPlayer player, World world, IInventory inventory) {
		returnItemsToPlayer(player, world, extractItemsFromInventory(inventory, false));
	}

	public static void returnItemsToPlayer(EntityPlayer player, World world, List<ItemStack> items) {
		boolean dropItems = !player.isEntityAlive() || player instanceof EntityPlayerMP && ((EntityPlayerMP) player).hasDisconnected();
		for (ItemStack itemstack : items) {
			if (!itemstack.isEmpty()) {
				if (dropItems)
					player.dropItem(itemstack, false);
				else
					player.inventory.placeItemBackInInventory(world, itemstack);
			}
		}
	}

}
