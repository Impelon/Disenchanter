package de.impelon.disenchanter.blocks;

import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class UpgradeTableRecipe extends ShapedOreRecipe {
	
	private final int addedmeta;

	public UpgradeTableRecipe(int addedmeta, ItemStack result, Object... recipe) {
		super(result, recipe);
		this.addedmeta = addedmeta;
	}
	
	@Override
	public boolean matches(InventoryCrafting grid, World world) {
		for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
			ItemStack st = grid.getStackInSlot(slot);
			if (st != null && st.getItem() == Item.getItemFromBlock(DisenchanterMain.proxy.disenchantmentTable))
				if (((st.getItemDamage() / this.addedmeta)) % 2 == 1)
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
		res.setItemDamage(table.getItemDamage() + this.addedmeta);
		return res;
	}

}
