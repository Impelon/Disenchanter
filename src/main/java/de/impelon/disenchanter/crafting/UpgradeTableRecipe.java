package de.impelon.disenchanter.crafting;

import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class UpgradeTableRecipe extends ShapedOreRecipe {

	private final PropertyBool addedProperty;

	public UpgradeTableRecipe(PropertyBool addedProperty, ItemStack result, Object... recipe) {
		super(null, result, recipe);
		this.addedProperty = addedProperty;
	}

	@Override
	public boolean matches(InventoryCrafting grid, World world) {
		for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
			ItemStack st = grid.getStackInSlot(slot);
			if (st != null && st.getItem().equals(CommonProxy.itemDisenchantmentTable))
				if (CommonProxy.disenchantmentTable.getStateFromMeta(st.getItemDamage()).getValue(this.addedProperty))
					return false;
		}
		return super.matches(grid, world);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {
		ItemStack table = null;
		for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
			ItemStack st = grid.getStackInSlot(slot);
			if (st != null && st.getItem().equals(CommonProxy.itemDisenchantmentTable))
				table = st.copy();
		}

		ItemStack res = super.getCraftingResult(grid);
		res.setItemDamage(CommonProxy.disenchantmentTable.getMetaFromState(CommonProxy.disenchantmentTable
				.getStateFromMeta(table.getItemDamage()).withProperty(this.addedProperty, true)));
		return res;
	}

}
