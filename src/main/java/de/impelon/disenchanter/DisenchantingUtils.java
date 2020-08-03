package de.impelon.disenchanter;

import java.util.Random;

import de.impelon.disenchanter.blocks.BlockDisenchantmentTable;
import de.impelon.disenchanter.proxies.CommonProxy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class DisenchantingUtils {

	public static void disenchantInTable(IInventory inventory, boolean isAutomatic, World world, BlockPos position,
			Random random) {
		if (inventory.getSizeInventory() < 3 || (isAutomatic && inventory.getStackInSlot(2) != ItemStack.EMPTY))
			return;

		ItemStack itemstack = inventory.getStackInSlot(0);
		ItemStack bookstack = inventory.getStackInSlot(1);
		ItemStack outputBookstack = new ItemStack(Items.ENCHANTED_BOOK);

		if (itemstack != ItemStack.EMPTY && bookstack != ItemStack.EMPTY
				&& getEnchantmentList(itemstack) != null) {
			if (bookstack.getCount() > 1)
				bookstack.setCount(bookstack.getCount() - 1);
			else
				bookstack = ItemStack.EMPTY;
			inventory.setInventorySlotContents(1, bookstack);

			disenchant(itemstack, outputBookstack, isAutomatic, world, position, random);

			if (itemstack.getItemDamage() > itemstack.getMaxDamage())
				itemstack = ItemStack.EMPTY;

			if (itemstack != ItemStack.EMPTY && getEnchantmentList(itemstack) == null) {
				if (itemstack.getItem() == Items.ENCHANTED_BOOK)
					itemstack = new ItemStack(Items.BOOK);
				if (world.getBlockState(position).getValue(BlockDisenchantmentTable.VOIDING))
					itemstack = ItemStack.EMPTY;
			}
			inventory.setInventorySlotContents(0, itemstack);

			if (isAutomatic && outputBookstack.getTagCompound() != null
					&& outputBookstack.getTagCompound().getTag("StoredEnchantments") != null)
				inventory.setInventorySlotContents(2, outputBookstack);

			if (!world.isRemote)
				world.playSound(null, position, CommonProxy.disenchantmentTableUse, SoundCategory.BLOCKS,
						isAutomatic ? 0.5F : 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
		}
	}

	public static void disenchant(ItemStack itemstack, ItemStack outputBookstack, boolean isAutomatic, World world,
			BlockPos position, Random random) {
		float power = getEnchantingPower(world, position);
		int flatDmg = DisenchanterMain.config.get("disenchanting", "FlatDamage", 10).getInt();
		double durabiltyDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamage", 0.025).getDouble();
		double reduceableDmg = DisenchanterMain.config.get("disenchanting", "MaxDurabilityDamageReduceable", 0.2)
				.getDouble();
		double machineDmgMultiplier = isAutomatic
				? DisenchanterMain.config.get("disenchanting", "MachineDamageMultiplier", 2.5).getDouble()
				: 1.0;

		while (getEnchantmentList(itemstack) != null) {
			transferEnchantment(itemstack, outputBookstack, 0, random);

			itemstack.attemptDamageItem((int) (machineDmgMultiplier * (flatDmg + itemstack.getMaxDamage() * durabiltyDmg
					+ itemstack.getMaxDamage() * (reduceableDmg / power))), random, null);

			if (itemstack.getItemDamage() > itemstack.getMaxDamage()
					|| !(world.getBlockState(position).getValue(BlockDisenchantmentTable.BULKDISENCHANTING)))
				break;
		}
	}

	public static void transferEnchantment(ItemStack input, ItemStack output, int index, Random random) {
		if (input != ItemStack.EMPTY && output != ItemStack.EMPTY && input.getTagCompound() != null) {
			double enchantmentLoss = DisenchanterMain.config.get("disenchanting", "EnchantmentLossChance", 0.0)
					.getDouble();

			NBTTagList enchants = getEnchantmentList(input);
			if (enchants == null)
				return;

			if (enchants.tagCount() > 0) {
				index = Math.min(Math.abs(index), enchants.tagCount() - 1);

				NBTTagCompound enchant = enchants.getCompoundTagAt(index);
				int id = enchant.getInteger("id");
				int lvl = enchant.getInteger("lvl");

				if (random.nextFloat() > enchantmentLoss)
					ItemEnchantedBook.addEnchantment(output,
							new EnchantmentData(Enchantment.getEnchantmentByID(id), lvl));

				enchants.removeTag(index);
				input.setRepairCost(input.getRepairCost() / 2);
			}
			if (enchants.tagCount() <= 0)
				if (isEnchantmentStorage(input))
					input.getTagCompound().removeTag("StoredEnchantments");
				else
					input.getTagCompound().removeTag("ench");
		}
	}

	public static NBTTagList getEnchantmentList(ItemStack itemstack) {
		if (itemstack == ItemStack.EMPTY || itemstack.getTagCompound() == null)
			return null;

		if (itemstack.getTagCompound().getTag("InfiTool") != null)
			if (DisenchanterMain.config.get("disenchanting", "EnableTCBehaviour", true).getBoolean())
				return null;
		if (itemstack.getTagCompound().getTag("TinkerData") != null)
			if (DisenchanterMain.config.get("disenchanting", "EnableTCBehaviour", true).getBoolean())
				return null;

		NBTTagList origTags = null;

		if (itemstack.getTagCompound().getTag("ench") != null)
			origTags = (NBTTagList) itemstack.getTagCompound().getTag("ench");
		if (itemstack.getTagCompound().getTag("StoredEnchantments") != null)
			origTags = (NBTTagList) itemstack.getTagCompound().getTag("StoredEnchantments");

		if (origTags == null)
			return null;
		NBTTagList tags = origTags.copy();

		lbl: for (int i = tags.tagCount() - 1; i > -1; i--) {
			NBTTagCompound enchantTag = tags.getCompoundTagAt(i);
			Enchantment enchant = Enchantment.getEnchantmentByID(enchantTag.getInteger("id"));
			String[] enchantBlacklist = DisenchanterMain.config
					.get("disenchanting", "EnchantmentBlacklist", new String[] {}).getStringList();

			for (String eb : enchantBlacklist) {
				if (eb == null || eb.equals(""))
					continue;

				if (Enchantment.REGISTRY.containsKey(new ResourceLocation(eb))) {
					Enchantment e = Enchantment.REGISTRY.getObject(new ResourceLocation(eb));
					if (e == null)
						continue;
					if (e.equals(enchant)) {
						tags.removeTag(i);
						continue lbl;
					}
				}
			}
		}
		if (tags.tagCount() == 0)
			return null;
		return tags;
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

}
