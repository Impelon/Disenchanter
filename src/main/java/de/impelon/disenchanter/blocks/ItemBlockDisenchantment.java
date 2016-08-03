package de.impelon.disenchanter.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
		
		if (stack.getItemDamage() == 1)
			l.add(new ChatComponentTranslation("msg.automatic.txt")
				.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).getFormattedText());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if (stack.getItemDamage() == 1)
			return 0x888888;
		return super.getColorFromItemStack(stack, pass);
	}

}
