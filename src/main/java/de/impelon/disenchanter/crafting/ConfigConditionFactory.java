package de.impelon.disenchanter.crafting;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import de.impelon.disenchanter.DisenchanterConfig;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class ConfigConditionFactory implements IConditionFactory {

	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		String type = new ResourceLocation(JsonUtils.getString(json, "type")).getPath();
		return new BooleanSupplier() {
			@Override
			public boolean getAsBoolean() {
				switch (type) {
				case "disenchantment_table_enabled":
					return DisenchanterConfig.general.enableDisenchantmentTableRecipe;
				case "experience_jar_enabled":
					return DisenchanterConfig.general.enableJarRecipe;
				case "automatic_upgrade_enabled":
					return DisenchanterConfig.general.enableAutomaticRecipe;
				case "bulkdisenchanting_upgrade_enabled":
					return DisenchanterConfig.general.enableBulkDisenchantingRecipe;
				case "voiding_upgrade_enabled":
					return DisenchanterConfig.general.enableVoidingRecipe;
				case "clear_table_upgrades_enabled":
					return DisenchanterConfig.general.enableClearRecipe;
				}
				return false;
			};
		};
	}

}
