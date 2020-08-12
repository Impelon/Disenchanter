package de.impelon.disenchanter.item;

import java.util.List;

import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemXPTablet extends Item {
	
	public ItemXPTablet() {
		super();
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setRegistryName(DisenchanterMain.MODID, "xptablet");
		this.setUnlocalizedName(this.getRegistryName().toString().toLowerCase());
		this.setMaxStackSize(1);
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World w, List<String> l, ITooltipFlag advanced) {		
		super.addInformation(stack, w, l, advanced);
		
		l.add(new TextComponentTranslation("msg.stored_xp.txt", 0.0, 1.0).setStyle(new Style().setColor(TextFormatting.GREEN)).getFormattedText());
	}

}
