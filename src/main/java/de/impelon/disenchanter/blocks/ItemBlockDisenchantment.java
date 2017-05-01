package de.impelon.disenchanter.blocks;

import java.util.List;

import de.impelon.disenchanter.DisenchanterMain;
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
		
		BlockDisenchantmentTable table = DisenchanterMain.proxy.disenchantmentTable;
		
		if (table.getStateFromMeta(stack.getItemDamage()).getValue(table.AUTOMATIC))
			l.add(new ChatComponentTranslation("msg.automatic.txt")
				.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).getFormattedText());
		if (table.getStateFromMeta(stack.getItemDamage()).getValue(table.BULKDISENCHANTING))
			l.add(new ChatComponentTranslation("msg.bulk.txt")
					.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).getFormattedText());
		if (table.getStateFromMeta(stack.getItemDamage()).getValue(table.VOIDING))
			l.add(new ChatComponentTranslation("msg.voiding.txt")
					.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).getFormattedText());
	}

}