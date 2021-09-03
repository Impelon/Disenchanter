package de.impelon.disenchanter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.impelon.disenchanter.block.TableVariant;
import de.impelon.disenchanter.inventory.ContainerDisenchantmentBase;
import de.impelon.disenchanter.inventory.IDisenchantmentItemHandler;
import de.impelon.disenchanter.item.ItemExperienceJar;
import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
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
	 * needed. The enchantment loss mechanic will be considered. This will
	 * disenchant starting from the first enchantment.
	 * </p>
	 * <p>
	 * <b>This method should be called when a disenchanting process is
	 * <i>persistently</i> performed.</b>
	 * </p>
	 * <p>
	 * The properties of the disenchantment table performing the
	 * disenchanting-process will be derived from its given position.
	 * </p>
	 * 
	 * @param inventory             an inventory like the slots of
	 *                              {@linkplain ContainerDisenchantmentBase}
	 * @param ignoreEnchantmentLoss true if enchantment loss should be considered,
	 *                              false otherwise
	 * @param startIndex            index of the enchantment to start the
	 *                              disenchanting-process from
	 * @param world                 the world the disenchanting is performed in
	 * @param position              the position the disenchanting is performed at
	 * @param random                a {@linkplain Random}-instance to use for random
	 *                              decisions
	 * @return the resulting output-itemstack, {@linkplain ItemStack#EMPTY} if the
	 *         disenchanting process should not be completed.
	 */
	public static ItemStack disenchantInInventory(IDisenchantmentItemHandler inventory, World world, BlockPos position,
			Random random) {
		return disenchantInInventory(inventory, DisenchantingProperties.getPropertiesFromStateAt(world, position), 0,
				world, position, random);
	}
	
	/**
	 * <p>
	 * Performs the disenchanting of an itemstack inside an inventory. This will
	 * transfer enchantments from the input-itemstack to the output-itemstack as
	 * needed. The enchantment loss mechanic will be considered.
	 * </p>
	 * <p>
	 * <b>This method should be called when a disenchanting process is
	 * <i>persistently</i> performed.</b>
	 * </p>
	 * <p>
	 * The properties of the disenchantment table performing the
	 * disenchanting-process will be derived from its given position.
	 * </p>
	 * 
	 * @param inventory             an inventory like the slots of
	 *                              {@linkplain ContainerDisenchantmentBase}
	 * @param ignoreEnchantmentLoss true if enchantment loss should be considered,
	 *                              false otherwise
	 * @param startIndex            index of the enchantment to start the
	 *                              disenchanting-process from
	 * @param world                 the world the disenchanting is performed in
	 * @param position              the position the disenchanting is performed at
	 * @param random                a {@linkplain Random}-instance to use for random
	 *                              decisions
	 * @return the resulting output-itemstack, {@linkplain ItemStack#EMPTY} if the
	 *         disenchanting process should not be completed.
	 */
	public static ItemStack disenchantInInventory(IDisenchantmentItemHandler inventory, int startIndex, World world,
			BlockPos position, Random random) {
		return disenchantInInventory(inventory, DisenchantingProperties.getPropertiesFromStateAt(world, position),
				startIndex, world, position, random);
	}

	/**
	 * <p>
	 * Performs the disenchanting of an itemstack inside an inventory. This will
	 * transfer enchantments from the input-itemstack to the output-itemstack as
	 * needed. The enchantment loss mechanic will be considered.
	 * </p>
	 * <p>
	 * <b>This method should be called when a disenchanting process is
	 * <i>persistently</i> performed.</b>
	 * </p>
	 * 
	 * @param inventory             an inventory like the slots of
	 *                              {@linkplain ContainerDisenchantmentBase}
	 * @param properties            the properties of the object performing the
	 *                              disenchanting-process
	 * @param ignoreEnchantmentLoss true if enchantment loss should be considered,
	 *                              false otherwise
	 * @param startIndex            index of the enchantment to start the
	 *                              disenchanting-process from
	 * @param world                 the world the disenchanting is performed in
	 * @param position              the position the disenchanting is performed at
	 * @param random                a {@linkplain Random}-instance to use for random
	 *                              decisions
	 * @return the resulting output-itemstack, {@linkplain ItemStack#EMPTY} if the
	 *         disenchanting process should not be completed.
	 */
	public static ItemStack disenchantInInventory(IDisenchantmentItemHandler inventory,
			DisenchantingProperties properties, int startIndex, World world, BlockPos position, Random random) {
		ItemStack result = simulateDisenchantingInInventory(inventory, properties, false, startIndex, world, position,
				random);
		if (!result.isEmpty()) {
			if (!world.isRemote)
				world.playSound(null, position, CommonProxy.disenchantmentTableUse, SoundCategory.BLOCKS,
						properties.is(TableVariant.AUTOMATIC) ? 0.5F : 1.0F, random.nextFloat() * 0.1F + 0.9F);
		}
		return result;
	}

	/**
	 * <p>
	 * Simulates the disenchanting of an itemstack inside an inventory. This will
	 * transfer enchantments from the input-itemstack to the output-itemstack as
	 * needed. This will disenchant starting from the first enchantment.
	 * </p>
	 * <b>Do not call this method to persistently perform a disenchanting
	 * process.</b><br>
	 * Use {@link DisenchantingUtils#disenchantInInventory} for that purpose.
	 * </p>
	 * <p>
	 * The properties of the disenchantment table performing the
	 * disenchanting-process will be derived from its given position.
	 * </p>
	 * 
	 * @param inventory             an inventory like the slots of
	 *                              {@linkplain ContainerDisenchantmentBase}
	 * @param properties            the properties of the object performing the
	 *                              disenchanting-process
	 * @param ignoreEnchantmentLoss true if enchantment loss should be considered,
	 *                              false otherwise
	 * @param world                 the world the disenchanting is performed in
	 * @param position              the position the disenchanting is performed at
	 * @param random                a {@linkplain Random}-instance to use for random
	 *                              decisions
	 * @return the resulting output-itemstack, {@linkplain ItemStack#EMPTY} if the
	 *         disenchanting process should not be completed.
	 */
	public static ItemStack simulateDisenchantingInInventory(IDisenchantmentItemHandler inventory,
			boolean ignoreEnchantmentLoss, World world, BlockPos position, Random random) {
		return simulateDisenchantingInInventory(inventory,
				DisenchantingProperties.getPropertiesFromStateAt(world, position), ignoreEnchantmentLoss, 0, world,
				position, random);
	}
	
	/**
	 * <p>
	 * Simulates the disenchanting of an itemstack inside an inventory. This will
	 * transfer enchantments from the input-itemstack to the output-itemstack as
	 * needed.
	 * </p>
	 * <b>Do not call this method to persistently perform a disenchanting
	 * process.</b><br>
	 * Use {@link DisenchantingUtils#disenchantInInventory} for that purpose.
	 * </p>
	 * <p>
	 * The properties of the disenchantment table performing the
	 * disenchanting-process will be derived from its given position.
	 * </p>
	 * 
	 * @param inventory             an inventory like the slots of
	 *                              {@linkplain ContainerDisenchantmentBase}
	 * @param properties            the properties of the object performing the
	 *                              disenchanting-process
	 * @param ignoreEnchantmentLoss true if enchantment loss should be considered,
	 *                              false otherwise
	 * @param startIndex            index of the enchantment to start the
	 *                              disenchanting-process from
	 * @param world                 the world the disenchanting is performed in
	 * @param position              the position the disenchanting is performed at
	 * @param random                a {@linkplain Random}-instance to use for random
	 *                              decisions
	 * @return the resulting output-itemstack, {@linkplain ItemStack#EMPTY} if the
	 *         disenchanting process should not be completed.
	 */
	public static ItemStack simulateDisenchantingInInventory(IDisenchantmentItemHandler inventory,
			boolean ignoreEnchantmentLoss, int startIndex, World world, BlockPos position, Random random) {
		return simulateDisenchantingInInventory(inventory,
				DisenchantingProperties.getPropertiesFromStateAt(world, position), ignoreEnchantmentLoss, startIndex,
				world, position, random);
	}

	/**
	 * <p>
	 * Simulates the disenchanting of an itemstack inside an inventory. This will
	 * transfer enchantments from the input-itemstack to the output-itemstack as
	 * needed.
	 * </p>
	 * <b>Do not call this method to persistently perform a disenchanting
	 * process.</b><br>
	 * Use {@link DisenchantingUtils#disenchantInInventory} for that purpose.
	 * </p>
	 * 
	 * @param inventory             an inventory like the slots of
	 *                              {@linkplain ContainerDisenchantmentBase}
	 * @param properties            the properties of the object performing the
	 *                              disenchanting-process
	 * @param ignoreEnchantmentLoss true if enchantment loss should be considered,
	 *                              false otherwise
	 * @param startIndex            index of the enchantment to start the
	 *                              disenchanting-process from
	 * @param world                 the world the disenchanting is performed in
	 * @param position              the position the disenchanting is performed at
	 * @param random                a {@linkplain Random}-instance to use for random
	 *                              decisions
	 * @return the resulting output-itemstack, {@linkplain ItemStack#EMPTY} if the
	 *         disenchanting process should not be completed.
	 */
	public static ItemStack simulateDisenchantingInInventory(IDisenchantmentItemHandler inventory,
			DisenchantingProperties properties, boolean ignoreEnchantmentLoss, int startIndex, World world,
			BlockPos position, Random random) {
		if (properties.hasPersistantInventory() && !inventory.getOutputStack().isEmpty())
			return ItemStack.EMPTY;

		ItemStack source = inventory.getSourceStack();
		ItemStack receiver = inventory.getReceiverStack();
		ItemStack target = getAppropriateResultTarget(receiver);
		if (target.isEmpty())
			return ItemStack.EMPTY;

		if (disenchant(source, target, properties, ignoreEnchantmentLoss, startIndex, world, position, random)) {
			if (receiver.getCount() > 1)
				receiver.setCount(receiver.getCount() - 1);
			else
				receiver = ItemStack.EMPTY;
			inventory.setReceiverStack(receiver);

			if (isItemStackBroken(source) || shouldNonDamageableBreak(source))
				source = ItemStack.EMPTY;

			if (!source.isEmpty()) {
				if (source.getItem().equals(Items.ENCHANTED_BOOK) && getEnchantmentList(source) == null)
					source = new ItemStack(Items.BOOK);
				if (properties.is(TableVariant.VOIDING) && getAvailableEnchantmentIndices(source).isEmpty())
					source = ItemStack.EMPTY;
			}
			inventory.setSourceStack(source);

			if (target.getItem().equals(Items.ENCHANTED_BOOK) && getEnchantmentList(target) == null)
				target = new ItemStack(Items.BOOK);
			if (properties.hasPersistantInventory())
				inventory.setOutputStack(target);
			return target;
		}
		return ItemStack.EMPTY;
	}
	
	/**
	 * <p>
	 * Performs the disenchanting of an itemstack. This will transfer enchantments
	 * from the input-itemstack to the output-itemstack as needed.
	 * </p>
	 * <p>
	 * The properties of the disenchantment table performing the
	 * disenchanting-process will be derived from its given position.
	 * </p>
	 * 
	 * @param source                the itemstack to transfer enchantments from
	 * @param target                the itemstack to transfer enchantments to; needs
	 *                              to be a stack of {@linkplain ItemEnchantedBook}
	 *                              or {@linkplain ItemExperienceJar}
	 * @param properties            the properties of the object performing the
	 *                              disenchanting-process
	 * @param ignoreEnchantmentLoss true if enchantment loss should be considered,
	 *                              false otherwise
	 * @param startIndex            index of the enchantment to start the
	 *                              disenchanting-process from
	 * @param world                 the world the disenchanting is performed in
	 * @param position              the position the disenchanting is performed at
	 * @param random                a {@linkplain Random}-instance to use for random
	 *                              decisions
	 * @return true if enchantments were transferred, false otherwise
	 */
	public static boolean disenchant(ItemStack source, ItemStack target, boolean ignoreEnchantmentLoss, int startIndex,
			World world, BlockPos position, Random random) {
		return disenchant(source, target, DisenchantingProperties.getPropertiesFromStateAt(world, position),
				ignoreEnchantmentLoss, startIndex, world, position, random);
	}

	/**
	 * <p>
	 * Performs the disenchanting of an itemstack. This will transfer enchantments
	 * from the input-itemstack to the output-itemstack as needed.
	 * </p>
	 * 
	 * @param source                the itemstack to transfer enchantments from
	 * @param target                the itemstack to transfer enchantments to; needs
	 *                              to be a stack of {@linkplain ItemEnchantedBook}
	 *                              or {@linkplain ItemExperienceJar}
	 * @param properties            the properties of the object performing the
	 *                              disenchanting-process
	 * @param ignoreEnchantmentLoss true if enchantment loss should be considered,
	 *                              false otherwise
	 * @param startIndex            index of the enchantment to start the
	 *                              disenchanting-process from
	 * @param world                 the world the disenchanting is performed in
	 * @param position              the position the disenchanting is performed at
	 * @param random                a {@linkplain Random}-instance to use for random
	 *                              decisions
	 * @return true if enchantments were transferred, false otherwise
	 */
	public static boolean disenchant(ItemStack source, ItemStack target, DisenchantingProperties properties,
			boolean ignoreEnchantmentLoss, int startIndex, World world, BlockPos position, Random random) {
		float power = getEnchantingPower(world, position);
		int flatDmg = DisenchanterConfig.disenchanting.flatDamage;
		double durabilityDmg = DisenchanterConfig.disenchanting.maxDurabilityDamage;
		double reducibleDmg = DisenchanterConfig.disenchanting.maxDurabilityDamageReducible;
		double dmgMultiplier = properties.is(TableVariant.AUTOMATIC) ? DisenchanterConfig.disenchanting.machineDamageMultiplier : 1.0;

		List<Integer> indices;
		boolean hasTransferredEnchantments = false;
		while (!(indices = getAvailableEnchantmentIndices(source)).isEmpty()) {
			int index = Math.abs(indices.get(startIndex % indices.size()));
			if (!transferEnchantment(source, target, index, ignoreEnchantmentLoss, random))
				break;
			hasTransferredEnchantments = true;

			source.attemptDamageItem((int) (dmgMultiplier * (flatDmg + source.getMaxDamage() * durabilityDmg
					+ source.getMaxDamage() * (reducibleDmg / power))), random, null);

			if (isItemStackBroken(source) || shouldNonDamageableBreak(source) || !properties.is(TableVariant.BULKDISENCHANTING))
				break;
		}
		return hasTransferredEnchantments;
	}

	/**
	 * <p>
	 * Transfers the enchantment at the given index from the enchantment-nbt-list of
	 * the input-itemstack to the output-itemstack.
	 * </p>
	 * <p>
	 * If the target is a stack of {@linkplain ItemExperienceJar}, this will instead
	 * convert to enchantment to experience points.
	 * </p>
	 * 
	 * @param source                the itemstack to transfer the enchantment from
	 * @param target                the itemstack to transfer the enchantment to;
	 *                              needs to be a stack of
	 *                              {@linkplain ItemEnchantedBook} or
	 *                              {@linkplain ItemExperienceJar}
	 * @param index                 the index of the enchantment-nbt-list of the
	 *                              input to transfer the enchantment from
	 * @param ignoreEnchantmentLoss true if enchantment loss should be considered,
	 *                              false otherwise
	 * @param random                a {@linkplain Random}-instance to use for random
	 *                              decisions
	 * @return true if an enchantment was transferred, false otherwise
	 */
	public static boolean transferEnchantment(ItemStack source, ItemStack target, int index,
			boolean ignoreEnchantmentLoss, Random random) {
		if (!source.isEmpty() && !target.isEmpty() && source.getTagCompound() != null) {
			NBTTagList enchants = getEnchantmentList(source);
			if (enchants == null)
				return false;

			if (enchants.tagCount() > 0) {
				index = Math.min(Math.abs(index), enchants.tagCount() - 1);

				NBTTagCompound enchant = enchants.getCompoundTagAt(index);
				int id = enchant.getInteger("id");
				int lvl = enchant.getInteger("lvl");
				EnchantmentData enchantment = new EnchantmentData(Enchantment.getEnchantmentByID(id), lvl);
				double enchantmentLoss = DisenchanterConfig.disenchanting.enchantmentLossChance;

				if (ignoreEnchantmentLoss || random.nextFloat() > enchantmentLoss) {
					if (target.getItem().equals(Items.ENCHANTED_BOOK))
						ItemEnchantedBook.addEnchantment(target, enchantment);
					else if (target.getItem().equals(CommonProxy.itemExperienceJar)) {
						ItemExperienceJar.ensureValidTag(target);
						ItemExperienceJar.setStoredExperienceClamped(target,
								ItemExperienceJar.getStoredExperience(target) + getExperienceForEnchantment(
										enchantment.enchantment, enchantment.enchantmentLevel));
					}
				}

				enchants.removeTag(index);
				double repairCostMultiplier = DisenchanterConfig.disenchanting.repairCostMultiplier;
				source.setRepairCost((int) (source.getRepairCost() * repairCostMultiplier));
			}
			if (enchants.tagCount() <= 0)
				if (isEnchantmentStorage(source))
					source.getTagCompound().removeTag("StoredEnchantments");
				else
					source.getTagCompound().removeTag("ench");
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
			boolean valid = DisenchanterConfig.disenchanting.disableCurses ? enchantment.isCurse() : true;
			for (String disabeledName : disabledEnchantments) {
				if (!valid)
					break;
				String[] splitted = disabeledName.split("\\[r\\]", 2);
				if (splitted.length > 1) {
					if (enchantment.getRegistryName().toString().matches(splitted[1]))
						valid = false;
				} else {
					if (enchantment.getRegistryName().toString().equalsIgnoreCase(splitted[0]))
						valid = false;
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
		if (itemstack.isEmpty() || itemstack.getTagCompound() == null)
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
	 * Returns the experience points to be gained when disenchanting this
	 * enchantment.
	 * </p>
	 * 
	 * @param enchantment the enchantment to get the experience from
	 * @param level       the level of the enchantment
	 * @return the experience points to be gained
	 */
	public static int getExperienceForEnchantment(Enchantment enchantment, int level) {
		int min = enchantment.getMinEnchantability(level);
		int max = enchantment.getMaxEnchantability(level);
		int flatXp = DisenchanterConfig.disenchanting.flatExperience;
		double minEnchantabilityXp = DisenchanterConfig.disenchanting.minEnchantabilityExperience;
		double maxEnchantabilityXp = DisenchanterConfig.disenchanting.maxEnchantabilityExperience;
		return flatXp + (int) (min * minEnchantabilityXp + max * maxEnchantabilityXp);
	}

	/**
	 * <p>
	 * Returns the itemstack that should be used as disenchanting-target when using
	 * the given itemstack as receiver.
	 * </p>
	 * 
	 * @param receiver the itemstack to get the target for
	 * @return an itemstack that should be used as target for the
	 *         disenchanting-process, {@linkplain ItemStack#EMPTY} if there is no
	 *         appropriate target for the given receiver
	 */
	public static ItemStack getAppropriateResultTarget(ItemStack receiver) {
		if (!receiver.isEmpty()) {
			if (receiver.getItem().equals(Items.BOOK))
				return new ItemStack(Items.ENCHANTED_BOOK);
			else if (receiver.getItem().equals(CommonProxy.itemExperienceJar)
					&& (ItemExperienceJar.hasAvailableExperienceCapacity(receiver)
							|| ItemExperienceJar.isOverloadActive(receiver)))
				return receiver.copy();
		}
		return ItemStack.EMPTY;
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
	 * Returns whether the given itemstack is broken (considering the durability).
	 * </p>
	 * 
	 * @param itemstack the itemstack to check
	 * @return true if the itemstack is broken, false otherwise
	 */
	public static boolean isItemStackBroken(ItemStack itemstack) {
		return itemstack.isItemStackDamageable() && itemstack.getItemDamage() > itemstack.getMaxDamage();
	}

	/**
	 * <p>
	 * Returns whether the given itemstack should break (for non-damageable items).
	 * </p>
	 * 
	 * @param itemstack the itemstack to check
	 * @return true if the itemstack is non-damageable and should be broken, false otherwise
	 */
	public static boolean shouldNonDamageableBreak(ItemStack itemstack) {
		return DisenchanterConfig.disenchanting.destroyNonDamageableItems && !itemstack.isItemStackDamageable();
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
