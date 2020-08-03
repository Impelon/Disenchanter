package de.impelon.disenchanter.crafting;

import de.impelon.disenchanter.proxies.CommonProxy;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class UpgradeTableRecipe extends ShapedOreRecipe {
	
	private final PropertyBool addedProperty;
	
//	static {
//	    RecipeSorter.register(DisenchanterMain.MODID + ":upgradeTable", UpgradeTableRecipe.class, RecipeSorter.Category.SHAPED, "after:forge:shapedore");
//	}
	
	/* 
	 * TODO: remove constructor if converted to JSON recipe
	 * see: 
	 * https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/common/crafting/recipe/ArmorUpgradeRecipe.java
	 * https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/common/crafting/recipe/AncientWillRecipe.java
	 * https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/common/crafting/ModCraftingRecipes.java
	 * https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/common/crafting/FluxfieldConditionFactory.java
	 */
	
	public UpgradeTableRecipe(PropertyBool addedProperty, ItemStack result, Object... recipe) {
		super(null, result, recipe);
		this.addedProperty = addedProperty;
	}
	
	@Override
	public boolean matches(InventoryCrafting grid, World world) {
		for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
			ItemStack st = grid.getStackInSlot(slot);
			if (st != null && st.getItem() == Item.getItemFromBlock(CommonProxy.disenchantmentTable))
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
				if (st != null && st.getItem() == Item.getItemFromBlock(CommonProxy.disenchantmentTable))
					table = st.copy();
		}

		ItemStack res = super.getCraftingResult(grid);
		res.setItemDamage(table.getItemDamage() + CommonProxy.disenchantmentTable.
				getMetaFromState(CommonProxy.disenchantmentTable.getDefaultState().withProperty(this.addedProperty, true)));
		return res;
	}

}
