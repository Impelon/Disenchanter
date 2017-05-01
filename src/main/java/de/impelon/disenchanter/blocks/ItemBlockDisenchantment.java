package de.impelon.disenchanter.blocks;

import java.util.List;

import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class ItemBlockDisenchantment extends ItemBlockWithMetadata {

	public ItemBlockDisenchantment(Block block) {
		super(block, block);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer p, List l, boolean b) {
		super.addInformation(stack, p, l, b);
		
		BlockDisenchantmentTable table = DisenchanterMain.proxy.disenchantmentTable;
		
		if (table.isAutomatic(stack.getItemDamage()))
			l.add(new ChatComponentTranslation("msg.automatic.txt")
				.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).getFormattedText());
		if (table.isBulkDisenchanting(stack.getItemDamage()))
			l.add(new ChatComponentTranslation("msg.bulk.txt")
					.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).getFormattedText());
		if (table.isVoiding(stack.getItemDamage()))
			l.add(new ChatComponentTranslation("msg.voiding.txt")
					.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).getFormattedText());
	}

}
