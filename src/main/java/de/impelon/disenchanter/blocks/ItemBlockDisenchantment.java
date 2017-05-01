package de.impelon.disenchanter.blocks;

import java.util.List;

import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.proxies.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
	public void addInformation(ItemStack stack, EntityPlayer p, List l, boolean b) {
		super.addInformation(stack, p, l, b);
		
		super.addInformation(stack, p, l, b);
		
		BlockDisenchantmentTable table = DisenchanterMain.proxy.disenchantmentTable;
		
		if (table.getStateFromMeta(stack.getItemDamage()).getValue(table.AUTOMATIC))
			l.add(new TextComponentTranslation("msg.automatic.txt")
				.setStyle(new Style().setColor(TextFormatting.GREEN)).getFormattedText());
		if (table.getStateFromMeta(stack.getItemDamage()).getValue(table.BULKDISENCHANTING))
			l.add(new TextComponentTranslation("msg.bulk.txt")
					.setStyle(new Style().setColor(TextFormatting.GREEN)).getFormattedText());
		if (table.getStateFromMeta(stack.getItemDamage()).getValue(table.VOIDING))
			l.add(new TextComponentTranslation("msg.voiding.txt")
					.setStyle(new Style().setColor(TextFormatting.GREEN)).getFormattedText());
	}
	
}
