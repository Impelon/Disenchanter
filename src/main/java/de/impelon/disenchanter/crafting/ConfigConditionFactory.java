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
					return DisenchanterConfig.crafting.enableDisenchantmentTableRecipe;
				case "automatic_upgrade_enabled":
					return DisenchanterConfig.crafting.enableAutomaticTableUpgradeRecipe;
				case "bulkdisenchanting_upgrade_enabled":
					return DisenchanterConfig.crafting.enableBulkDisenchantingTableUpgradeRecipe;
				case "cycling_upgrade_enabled":
					return DisenchanterConfig.crafting.enableCyclingTableUpgradeRecipe;
				case "voiding_upgrade_enabled":
					return DisenchanterConfig.crafting.enableVoidingTableUpgradeRecipe;
				case "clear_table_upgrades_enabled":
					return DisenchanterConfig.crafting.enableClearTableRecipe;
				case "experience_jar_enabled":
					return DisenchanterConfig.crafting.enableJarRecipe;
				case "overload_upgrade_enabled":
					return DisenchanterConfig.crafting.enableOverloadJarUpgradeRecipe;
				case "capacity_upgrade_enabled":
					return DisenchanterConfig.crafting.enableCapacityJarUpgradeRecipe;
				case "clear_jar_upgrades_enabled":
					return DisenchanterConfig.crafting.enableClearJarRecipe;
				}
				return false;
			};
		};
	}

}
