package de.impelon.disenchanter.item;

import java.util.List;

import org.lwjgl.Sys;

import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemXPTablet extends Item {
	
	public static final String STORED_EXPERIENCE_KEY = "StoredExperience";

	public ItemXPTablet() {
		super();
		this.setCreativeTab(CreativeTabs.MISC);
		this.setRegistryName(DisenchanterMain.MODID, "xptablet");
		this.setUnlocalizedName(this.getRegistryName().toString().toLowerCase());
		this.setMaxStackSize(1);
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
			EntityLivingBase entityLiving) {
		this.ensure(stack);
		this.setStoredExperience(stack, 1000);
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}

	public boolean ensure(ItemStack tablet) {
		boolean modified = false;
		if (!tablet.hasTagCompound()) {
			tablet.setTagCompound(new NBTTagCompound());
			modified = true;
		}
		if (!this.hasStoredExperience(tablet)) {
			this.setStoredExperience(tablet, 0);
			modified = true;
		}
		return modified;
	}

	/**
	 * Returns whether there are any experience points stored in the tablet.
	 * 
	 * @param tablet the itemstack of the tablet
	 * @return true if there are stored xp, false otherwise
	 */
	public boolean hasStoredExperience(ItemStack tablet) {
		return getStoredExperience(tablet) > 0;
	}

	/**
	 * Returns the amount of experience points stored in the tablet.
	 * 
	 * @param tablet the itemstack of the tablet
	 * @return the amount of xp or 0 if the stack did not contain any xp
	 */
	public int getStoredExperience(ItemStack tablet) {
		if (tablet.hasTagCompound())
			return tablet.getTagCompound().getInteger(STORED_EXPERIENCE_KEY);
		return 0;
	}
	
	/**
	 * Sets the amount of experience points stored in the tablet.
	 * 
	 * @param tablet the itemstack of the tablet
	 * @param amount the amount of xp to set
	 */
	public boolean setStoredExperience(ItemStack tablet, int amount) {
		if (tablet.hasTagCompound()) {
			if (amount >= 0) {
				tablet.getTagCompound().setInteger(STORED_EXPERIENCE_KEY, amount);
				return true;
			}
		}
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		int amountToAdd = 0;
		ItemStack tablet = playerIn.getHeldItem(handIn);
		this.ensure(tablet);
		int xpStored = this.getStoredExperience(tablet);
		if (!playerIn.isSneaking()) {
			int xpToLevelup = MathHelper.ceil(playerIn.xpBarCap() * (1.0F - playerIn.experience));
			amountToAdd = xpToLevelup;
		} else {
			amountToAdd = xpStored;
		}
		amountToAdd = Math.min(amountToAdd, xpStored);
		playerIn.addExperience(amountToAdd);
		this.setStoredExperience(tablet, xpStored - amountToAdd);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World w, List<String> l, ITooltipFlag advanced) {
		super.addInformation(stack, w, l, advanced);

		l.add(new TextComponentTranslation("msg.stored_xp.txt", this.getStoredExperience(stack), 1)
				.setStyle(new Style().setColor(TextFormatting.GREEN)).getFormattedText());
	}

}
