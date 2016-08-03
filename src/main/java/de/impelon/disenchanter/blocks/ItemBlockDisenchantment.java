package de.impelon.disenchanter.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockDisenchantment extends ItemBlock {

	public ItemBlockDisenchantment(Block block) {
		super(block);
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