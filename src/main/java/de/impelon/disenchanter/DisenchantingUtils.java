package de.impelon.disenchanter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.impelon.disenchanter.block.BlockDisenchantmentTable;
import de.impelon.disenchanter.inventory.ContainerDisenchantment;
import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class DisenchantingUtils {

	/**
	 * <p>
	 * Performs the disenchanting of an itemstack inside an inventory. This will
	 * transfer enchantments from the input-itemstack to the output-itemstack as
	 * needed.
	 * </p>
	 * 
	 * @param inventory   an inventory like the slots of
	 *                    {@linkplain ContainerDisenchantment}
	 * @param isAutomatic true when the disenchanting process is performed by an
	 *                    automatic disenchantment table, false otherwise
	 * @param world       the world the disenchanting is performed in
	 * @param position    the position the disenchanting is performed at
	 * @param random      a {@linkplain Random}-instance to use for random decisions
	 */
	public static void disenchantInInventory(IInventory inventory, boolean isAutomatic, World world, BlockPos position,
			Random random) {
		if (inventory.getSizeInventory() < 3 || (isAutomatic && inventory.getStackInSlot(2) != ItemStack.EMPTY))
			return;

		ItemStack itemstack = inventory.getStackInSlot(0);
		ItemStack bookstack = inventory.getStackInSlot(1);
		ItemStack outputBookstack = new ItemStack(Items.ENCHANTED_BOOK);

		if (disenchant(itemstack, outputBookstack, isAutomatic, false, world, position, random)) {
			if (bookstack.getCount() > 1)
				bookstack.setCount(bookstack.getCount() - 1);
			else
				bookstack = ItemStack.EMPTY;
			inventory.setInventorySlotContents(1, bookstack);

			if (isItemStackBroken(itemstack))
				itemstack = ItemStack.EMPTY;

			if (itemstack != ItemStack.EMPTY) {
				if (itemstack.getItem() == Items.ENCHANTED_BOOK && getEnchantmentList(itemstack) == null)
					itemstack = new ItemStack(Items.BOOK);
				if (world.getBlockState(position).getValue(BlockDisenchantmentTable.VOIDING)
						&& getAvailableEnchantmentIndices(itemstack).isEmpty())
					itemstack = ItemStack.EMPTY;
			}
			inventory.setInventorySlotContents(0, itemstack);
			
			if (getEnchantmentList(outputBookstack) == null)
				outputBookstack = new ItemStack(Items.BOOK);
			if (isAutomatic)
				inventory.setInventorySlotContents(2, outputBookstack);
					

			if (!world.isRemote)
				world.playSound(null, position, CommonProxy.disenchantmentTableUse, SoundCategory.BLOCKS,
						isAutomatic ? 0.5F : 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
		}
	}

	/**
	 * <p>
	 * Performs the disenchanting of an itemstack. This will transfer enchantments
	 * from the input-itemstack to the output-itemstack as needed.
	 * </p>
	 * 
	 * @param input           the itemstack to transfer enchantments from
	 * @param outputBookstack the itemstack to transfer enchantments to; needs to be
	 *                        a stack of {@linkplain ItemEnchantedBook}
	 * @param isAutomatic     true when the disenchanting process is performed by an
	 *                        automatic disenchantment table, false otherwise
	 * @param world           the world the disenchanting is performed in
	 * @param position        the position the disenchanting is performed at
	 * @param random          a {@linkplain Random}-instance to use for random
	 *                        decisions
	 * @return true if enchantments were transferred, false otherwise
	 */
	public static boolean disenchant(ItemStack input, ItemStack outputBookstack, boolean isAutomatic, boolean ignoreEnchantmentLoss, World world,
			BlockPos position, Random random) {
		float power = getEnchantingPower(world, position);
		int flatDmg = DisenchanterConfig.disenchanting.flatDamage;
		double durabilityDmg = DisenchanterConfig.disenchanting.maxDurabilityDamage;
		double reduceableDmg = DisenchanterConfig.disenchanting.maxDurabilityDamageReduceable;
		double dmgMultiplier = isAutomatic ? DisenchanterConfig.disenchanting.machineDamageMultiplier : 1.0;

		List<Integer> indices;
		boolean hasTransferredEnchantments = false;
		while (!(indices = getAvailableEnchantmentIndices(input)).isEmpty()) {
			if (!transferEnchantment(input, outputBookstack, indices.get(0), ignoreEnchantmentLoss, random))
				break;
			hasTransferredEnchantments = true;

			input.attemptDamageItem((int) (dmgMultiplier * (flatDmg + input.getMaxDamage() * durabilityDmg
					+ input.getMaxDamage() * (reduceableDmg / power))), random, null);

			if (isItemStackBroken(input)
					|| !(world.getBlockState(position).getValue(BlockDisenchantmentTable.BULKDISENCHANTING)))
				break;
		}
		return hasTransferredEnchantments;
	}

	/**
	 * <p>
	 * Transfers the enchantment at the given index from the enchantment-nbt-list of
	 * the input-itemstack to the output-itemstack.
	 * </p>
	 * 
	 * @param input           the itemstack to transfer the enchantment from
	 * @param outputBookstack the itemstack to transfer the enchantment to; needs to
	 *                        be a stack of {@linkplain ItemEnchantedBook}
	 * @param index           the index of the enchantment-nbt-list of the input to
	 *                        transfer the enchantment from
	 * @param random          a {@linkplain Random}-instance to use for random
	 *                        decisions
	 * @return true if an enchantment was transferred, false otherwise
	 */
	public static boolean transferEnchantment(ItemStack input, ItemStack outputBookstack, int index, boolean ignoreEnchantmentLoss, Random random) {
		if (input != ItemStack.EMPTY && outputBookstack != ItemStack.EMPTY && input.getTagCompound() != null) {
			NBTTagList enchants = getEnchantmentList(input);
			if (enchants == null)
				return false;

			if (enchants.tagCount() > 0) {
				index = Math.min(Math.abs(index), enchants.tagCount() - 1);

				NBTTagCompound enchant = enchants.getCompoundTagAt(index);
				int id = enchant.getInteger("id");
				int lvl = enchant.getInteger("lvl");
				double enchantmentLoss = DisenchanterConfig.disenchanting.enchantmentLossChance;

				if (!ignoreEnchantmentLoss && random.nextFloat() > enchantmentLoss)
					ItemEnchantedBook.addEnchantment(outputBookstack,
							new EnchantmentData(Enchantment.getEnchantmentByID(id), lvl));

				enchants.removeTag(index);
				input.setRepairCost(input.getRepairCost() / 2);
			}
			if (enchants.tagCount() <= 0)
				if (isEnchantmentStorage(input))
					input.getTagCompound().removeTag("StoredEnchantments");
				else
					input.getTagCompound().removeTag("ench");
			return true;
		}
		return false;
	}

	/**
	 * <p>
	 * Returns the list of the indices of the enchantments that are available for
	 * disenchanting on the given itemstack. This takes disabled items and
	 * enchantments into account.
	 * </p>
	 * 
	 * @param itemstack the itemstack to get the enchantments from
	 * @return the list of indices in the itemstack's enchantment nbt-list
	 */
	public static List<Integer> getAvailableEnchantmentIndices(ItemStack itemstack) {
		List<Integer> available = new ArrayList<Integer>();

		String[] disabledItems = DisenchanterConfig.disenchanting.disabledItems;
		for (String disabeledName : disabledItems) {
			String[] splitted = disabeledName.split("\\[r\\]", 2);
			if (splitted.length > 1) {
				if (itemstack.getItem().getRegistryName().toString().matches(splitted[1])) {
					return available;
				}
			} else {
				if (itemstack.getItem().getRegistryName().toString().equalsIgnoreCase(splitted[0])) {
					return available;
				}
			}
		}

		NBTTagList enchants = getEnchantmentList(itemstack);
		if (enchants == null)
			return available;

		String[] disabledEnchantments = DisenchanterConfig.disenchanting.disabledEnchantments;
		for (int index = 0; index < enchants.tagCount(); index++) {
			NBTTagCompound enchant = enchants.getCompoundTagAt(index);
			Enchantment enchantment = Enchantment.getEnchantmentByID(enchant.getInteger("id"));
			boolean valid = true;
			for (String disabeledName : disabledEnchantments) {
				String[] splitted = disabeledName.split("\\[r\\]", 2);
				if (splitted.length > 1) {
					if (enchantment.getRegistryName().toString().matches(splitted[1])) {
						valid = false;
						break;
					}
				} else {
					if (enchantment.getRegistryName().toString().equalsIgnoreCase(splitted[0])) {
						valid = false;
						break;
					}
				}
			}
			if (valid)
				available.add(index);
		}

		return available;
	}

	/**
	 * <p>
	 * Returns the nbt-list of enchantments that are stored on the given itemstack.
	 * </p>
	 * 
	 * @param itemstack the itemstack to get the enchantments from
	 * @return the nbt-list of enchantments
	 */
	public static NBTTagList getEnchantmentList(ItemStack itemstack) {
		if (itemstack == ItemStack.EMPTY || itemstack.getTagCompound() == null)
			return null;

		if (itemstack.getTagCompound().getTag("InfiTool") != null)
			if (DisenchanterConfig.disenchanting.enableTCBehaviour)
				return null;
		if (itemstack.getTagCompound().getTag("TinkerData") != null)
			if (DisenchanterConfig.disenchanting.enableTCBehaviour)
				return null;

		if (itemstack.getTagCompound().getTag("ench") != null)
			return (NBTTagList) itemstack.getTagCompound().getTag("ench");
		if (itemstack.getTagCompound().getTag("StoredEnchantments") != null)
			return (NBTTagList) itemstack.getTagCompound().getTag("StoredEnchantments");

		return null;
	}

	/**
	 * <p>
	 * Returns whether the given itemstack can store enchantments.
	 * </p>
	 * 
	 * @param itemstack the itemstack to check
	 * @return true if the itemstack can store enchantments, false otherwise
	 */
	public static boolean isEnchantmentStorage(ItemStack itemstack) {
		return itemstack.getTagCompound().getTag("StoredEnchantments") != null;
	}

	/**
	 * <p>
	 * Calculates the enchanting power level of the valid positions surrounding the
	 * given position. The enchanting power level can be increased by certain
	 * blocks.
	 * </p>
	 * 
	 * This will return a value ranging from 1 to 15 inclusive.
	 * 
	 * @param w   the world to act in
	 * @param pos the position to check around
	 * @see ForgeHooks#getEnchantPower(World, BlockPos)
	 * @return the power level
	 */
	public static float getEnchantingPower(World w, BlockPos pos) {
		int power = 1;
		for (int blockZ = -1; blockZ <= 1; blockZ++) {
			for (int blockX = -1; blockX <= 1; blockX++) {
				if ((blockZ != 0 || blockX != 0) && w.isAirBlock(pos.add(blockX, 0, blockZ))
						&& w.isAirBlock(pos.add(blockX, 1, blockZ))) {
					power += ForgeHooks.getEnchantPower(w, pos.add(blockX * 2, 0, blockZ * 2));
					power += ForgeHooks.getEnchantPower(w, pos.add(blockX * 2, 1, blockZ * 2));

					if (blockX != 0 && blockZ != 0) {
						power += ForgeHooks.getEnchantPower(w, pos.add(blockX * 2, 0, blockZ));
						power += ForgeHooks.getEnchantPower(w, pos.add(blockX * 2, 1, blockZ));
						power += ForgeHooks.getEnchantPower(w, pos.add(blockX, 0, blockZ * 2));
						power += ForgeHooks.getEnchantPower(w, pos.add(blockX, 1, blockZ * 2));
					}
				}
			}
			if (power >= 15) {
				power = 15;
				break;
			}
		}

		return power;
	}

	/**
	 * <p>
	 * Returns whether the given itemstack is broken (considering the durability).
	 * </p>
	 * 
	 * @param itemstack the itemstack to check
	 * @return true if the itemstack is broker, false otherwise
	 */
	public static boolean isItemStackBroken(ItemStack itemstack) {
		return itemstack.isItemStackDamageable() && itemstack.getItemDamage() > itemstack.getMaxDamage();
	}

}
