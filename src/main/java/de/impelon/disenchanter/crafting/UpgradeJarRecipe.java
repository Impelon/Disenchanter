package de.impelon.disenchanter.crafting;

import de.impelon.disenchanter.inventory.InventoryUtils;
import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
	public boolean matches(InventoryCrafting grid, World world) {
		ItemStack jar = InventoryUtils.findFirstItemStackInInventory(grid, CommonProxy.itemExperienceJar);
		if (!jar.isEmpty()) {
			ItemStack upgradedJar = this.upgrade.apply(jar.copy());
			if (ItemStack.areItemStacksEqual(jar, upgradedJar))
				return false;
		}
		return super.matches(grid, world);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {
		ItemStack jar = InventoryUtils.findFirstItemStackInInventory(grid, CommonProxy.itemExperienceJar);
		if (jar.isEmpty())
			return jar;
		return this.upgrade.apply(jar.copy());
	}

}
