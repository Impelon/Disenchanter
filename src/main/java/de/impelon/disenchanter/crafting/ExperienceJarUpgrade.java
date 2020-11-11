package de.impelon.disenchanter.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import de.impelon.disenchanter.DisenchanterConfig;
import de.impelon.disenchanter.item.ItemExperienceJar;
import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;

public class ExperienceJarUpgrade {

	public enum CapacityChange {
		INCREASE, DECREASE, UNCHANGED, RESET;
	}

	public enum ModeChange {
		ENABLE, DISABLE, UNCHANGED, RESET;
	}

	public final ModeChange overloadChange;
	public final CapacityChange capacityChange;
	public static final ExperienceJarUpgrade RESET_UPGRADE = new ExperienceJarUpgrade(ModeChange.RESET,
			CapacityChange.RESET);

	public static ExperienceJarUpgrade parseRecipe(JsonObject json) {
		ExperienceJarUpgrade upgrade;
		if (json.get("upgrade").isJsonObject())
			upgrade = ExperienceJarUpgrade.parse(JsonUtils.getJsonObject(json, "upgrade"));
		else if (JsonUtils.getString(json, "upgrade").equalsIgnoreCase("reset_all"))
			upgrade = ExperienceJarUpgrade.RESET_UPGRADE;
		else
			throw new JsonSyntaxException("Invalid upgrade specification.");
		return upgrade;
	}

	public static ExperienceJarUpgrade parse(JsonObject json) {
		ModeChange overloadChange = ModeChange.UNCHANGED;
		CapacityChange capacityChange = CapacityChange.UNCHANGED;

		String overload = JsonUtils.getString(json, "overload", null);
		if (overload != null)
			overloadChange = ModeChange.valueOf(overload.toUpperCase());
		String capacity = JsonUtils.getString(json, "capacity", null);
		if (capacity != null)
			capacityChange = CapacityChange.valueOf(capacity.toUpperCase());

		return new ExperienceJarUpgrade(overloadChange, capacityChange);
	}

	public ExperienceJarUpgrade(ModeChange overloadChange, ExperienceJarUpgrade.CapacityChange capacityChange) {
		this.overloadChange = overloadChange;
		this.capacityChange = capacityChange;
	}

	/**
	 * Apply this upgrade to a given stack. This will fail if the given stack does
	 * not contain a jar of experience.
	 * 
	 * @param stack the itemstack
	 * @return the resulting stack or an empty itemstack if the operation was not
	 *         succesful
	 */
	public ItemStack apply(ItemStack stack) {
		if (!stack.getItem().equals(CommonProxy.itemExperienceJar))
			return ItemStack.EMPTY;

		ItemExperienceJar.ensureValidTag(stack);
		switch (this.overloadChange) {
		case RESET:
		case DISABLE:
			ItemExperienceJar.setOverload(stack, false);
			break;
		case ENABLE:
			ItemExperienceJar.setOverload(stack, true);
			break;
		case UNCHANGED:
		default:
		}
		int changeMultiplier = 1;
		switch (this.capacityChange) {
		case RESET:
			ItemExperienceJar.resetExperienceCapacity(stack);
			break;
		case DECREASE:
			changeMultiplier = -1;
		case INCREASE: {
			int currentCapacity = ItemExperienceJar.getExperienceCapacity(stack);
			int capacityChange = DisenchanterConfig.experienceJar.jarUpgradeCapacityChange * changeMultiplier;
			ItemExperienceJar.setExperienceCapacity(stack, MathHelper.clamp(currentCapacity + capacityChange, 0,
					DisenchanterConfig.experienceJar.jarUpgradeMaxCapacity));
			break;
		}
		case UNCHANGED:
		default:
		}
		ItemExperienceJar.ensureValidTag(stack);
		return stack;
	}
}