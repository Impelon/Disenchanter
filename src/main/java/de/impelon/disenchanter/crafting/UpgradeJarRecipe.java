package de.impelon.disenchanter.crafting;

import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class UpgradeJarRecipe extends ShapedOreRecipe {
	
	protected final ExperienceJarUpgrade upgrade;

	public static ItemStack getResultingJar(ExperienceJarUpgrade upgrade) {
		ItemStack result = new ItemStack(CommonProxy.itemExperienceJar, 1);
		return upgrade.apply(result);
	}

	public UpgradeJarRecipe(ResourceLocation group, ExperienceJarUpgrade upgrade, Object... recipe) {
		this(group, upgrade, CraftingHelper.parseShaped(recipe));

	}

	public UpgradeJarRecipe(ResourceLocation group, ExperienceJarUpgrade upgrade, ShapedPrimer recipe) {
		super(group, getResultingJar(upgrade), recipe);
		this.upgrade = upgrade;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {
		ItemStack jar = null;
		for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
			ItemStack stack = grid.getStackInSlot(slot);
			if (stack != null && stack.getItem().equals(CommonProxy.itemExperienceJar)) {
				jar = stack.copy();
				break;
			}
		}
		if (jar == null)
			return ItemStack.EMPTY;
		return this.upgrade.apply(jar);
	}

}
