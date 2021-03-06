package de.impelon.disenchanter.crafting;

import com.google.gson.JsonObject;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class UpgradeTableRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
		ShapedPrimer primer = new ShapedPrimer();
		String group = recipe.getGroup();

		primer.width = recipe.getRecipeWidth();
		primer.height = recipe.getRecipeHeight();
		primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
		primer.input = recipe.getIngredients();

		return new UpgradeTableRecipe(group.isEmpty() ? null : new ResourceLocation(group), PropertyBool.create(JsonUtils.getString(json, "added_property")), primer);
	}

}
