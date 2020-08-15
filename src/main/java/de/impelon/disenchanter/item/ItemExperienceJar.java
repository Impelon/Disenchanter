package de.impelon.disenchanter.item;

import java.util.List;

import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemExperienceJar extends Item {
	
	public static final String STORED_EXPERIENCE_KEY = "StoredExperience";

	public ItemExperienceJar() {
		super();
		this.setCreativeTab(CreativeTabs.MISC);
		this.setRegistryName(DisenchanterMain.MODID, "experience_jar");
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

	public boolean ensure(ItemStack stack) {
		boolean modified = false;
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			modified = true;
		}
		if (!this.hasStoredExperience(stack)) {
			this.setStoredExperience(stack, 0);
			modified = true;
		}
		return modified;
	}

	/**
	 * Returns whether there are any experience points stored in the stack.
	 * 
	 * @param stack the itemstack of the stack
	 * @return true if there are stored xp, false otherwise
	 */
	public boolean hasStoredExperience(ItemStack stack) {
		return getStoredExperience(stack) > 0;
	}

	/**
	 * Returns the amount of experience points stored in the stack.
	 * 
	 * @param stack the itemstack of the stack
	 * @return the amount of xp or 0 if the stack did not contain any xp
	 */
	public int getStoredExperience(ItemStack stack) {
		if (stack.hasTagCompound())
			return stack.getTagCompound().getInteger(STORED_EXPERIENCE_KEY);
		return 0;
	}
	
	/**
	 * Sets the amount of experience points stored in the stack.
	 * This will fail if the item has no NBT-tag or if the specified amount is invalid.
	 * 
	 * @param stack the itemstack of the stack
	 * @param amount the amount of xp to set
	 * @return true if this was successful, false otherwise
	 */
	public boolean setStoredExperience(ItemStack stack, int amount) {
		if (stack.hasTagCompound()) {
			if (amount >= 0) {
				stack.getTagCompound().setInteger(STORED_EXPERIENCE_KEY, amount);
				return true;
			}
		}
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		int amountToAdd = 0;
		ItemStack stack = playerIn.getHeldItem(handIn);
		this.ensure(stack);
		int xpStored = this.getStoredExperience(stack);
		if (!playerIn.isSneaking()) {
			int xpToLevelup = MathHelper.ceil(playerIn.xpBarCap() * (1.0F - playerIn.experience));
			amountToAdd = xpToLevelup;
		} else {
			amountToAdd = xpStored;
		}
		amountToAdd = Math.min(amountToAdd, xpStored);
		playerIn.addExperience(amountToAdd);
		this.setStoredExperience(stack, xpStored - amountToAdd);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World w, List<String> l, ITooltipFlag advanced) {
		super.addInformation(stack, w, l, advanced);

		l.add(new TextComponentTranslation("msg.stored_xp.txt", this.getStoredExperience(stack), 1)
				.setStyle(new Style().setColor(TextFormatting.GREEN)).getFormattedText());
	}

}
