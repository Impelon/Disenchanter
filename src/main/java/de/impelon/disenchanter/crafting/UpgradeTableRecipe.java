package de.impelon.disenchanter.crafting;

import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class UpgradeTableRecipe extends ShapedOreRecipe {

	private final PropertyBool addedProperty;

	public static ItemStack getResultingTable(PropertyBool property) {
		ItemStack result = new ItemStack(CommonProxy.itemDisenchantmentTable, 1);
		result.setItemDamage(CommonProxy.disenchantmentTable
				.getMetaFromState(CommonProxy.disenchantmentTable.getDefaultState().withProperty(property, true)));
		return result;
	}

	public UpgradeTableRecipe(ResourceLocation group, PropertyBool addedProperty, Object... recipe) {
		super(group, getResultingTable(addedProperty), recipe);
		this.addedProperty = addedProperty;

	}

	public UpgradeTableRecipe(ResourceLocation group, PropertyBool addedProperty, ShapedPrimer recipe) {
		super(group, getResultingTable(addedProperty), recipe);
		this.addedProperty = addedProperty;
	}

	@Override
	public boolean matches(InventoryCrafting grid, World world) {
		for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
			ItemStack stack = grid.getStackInSlot(slot);
			if (stack != null && stack.getItem().equals(CommonProxy.itemDisenchantmentTable))
				if (CommonProxy.disenchantmentTable.getStateFromMeta(stack.getItemDamage()).getValue(this.addedProperty))
					return false;
		}
		return super.matches(grid, world);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {
		ItemStack table = null;
		for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
			ItemStack stack = grid.getStackInSlot(slot);
			if (stack != null && stack.getItem().equals(CommonProxy.itemDisenchantmentTable))
				table = stack.copy();
		}

		ItemStack res = super.getCraftingResult(grid);
		res.setItemDamage(CommonProxy.disenchantmentTable.getMetaFromState(CommonProxy.disenchantmentTable
				.getStateFromMeta(table.getItemDamage()).withProperty(this.addedProperty, true)));
		return res;
	}

}
