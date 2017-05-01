package de.impelon.disenchanter.blocks;

import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class UpgradeTableRecipe extends ShapedOreRecipe {
	
	private final PropertyBool addedProperty;
	
	static {
	    RecipeSorter.register(DisenchanterMain.MODID + ":upgradeTable", UpgradeTableRecipe.class, RecipeSorter.Category.SHAPED, "after:forge:shapedore");
	}

	public UpgradeTableRecipe(PropertyBool addedProperty, ItemStack result, Object... recipe) {
		super(result, recipe);
		this.addedProperty = addedProperty;
	}
	
	@Override
	public boolean matches(InventoryCrafting grid, World world) {
		for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
			ItemStack st = grid.getStackInSlot(slot);
			if (st != null && st.getItem() == Item.getItemFromBlock(DisenchanterMain.proxy.disenchantmentTable))
				if (DisenchanterMain.proxy.disenchantmentTable.getStateFromMeta(st.getItemDamage()).getValue(this.addedProperty))
					return false;
		}
		return super.matches(grid, world);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {
		ItemStack table = null;
		for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
				ItemStack st = grid.getStackInSlot(slot);
				if (st != null && st.getItem() == Item.getItemFromBlock(DisenchanterMain.proxy.disenchantmentTable))
					table = st.copy();
		}

		ItemStack res = super.getCraftingResult(grid);
		res.setItemDamage(table.getItemDamage() + DisenchanterMain.proxy.disenchantmentTable.
				getMetaFromState(DisenchanterMain.proxy.disenchantmentTable.getDefaultState().withProperty(this.addedProperty, true)));
		return res;
	}

}
