package de.impelon.disenchanter.item;

import java.util.List;

import de.impelon.disenchanter.DisenchantingProperties;
import de.impelon.disenchanter.block.BlockDisenchantmentTable;
import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockDisenchantment extends ItemBlock {

	public ItemBlockDisenchantment() {
		super(CommonProxy.disenchantmentTable);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int metadata) {
		return metadata;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World w, List<String> l, ITooltipFlag advanced) {
		super.addInformation(stack, w, l, advanced);

		BlockDisenchantmentTable table = CommonProxy.disenchantmentTable;

		List<String> descriptions = DisenchantingProperties
				.getPropertiesFromState(table.getStateFromMeta(stack.getItemDamage())).getTableVariantDescriptions();
		for (String description : descriptions) {
			l.add(new TextComponentString(description).setStyle(new Style().setColor(TextFormatting.GREEN))
					.getFormattedText());
		}
	}

}
