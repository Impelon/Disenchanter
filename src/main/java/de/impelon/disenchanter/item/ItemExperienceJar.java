package de.impelon.disenchanter.item;

import java.util.List;

import de.impelon.disenchanter.DisenchanterConfig;
import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemExperienceJar extends Item {
	
	public static final String STORED_EXPERIENCE_KEY = "StoredExperience";
	public static final String EXPERIENCE_CAPACITY_KEY = "ExperienceCapacity";

	public ItemExperienceJar() {
		super();
		this.setCreativeTab(CreativeTabs.MISC);
		this.setRegistryName(DisenchanterMain.MODID, "experience_jar");
		this.setUnlocalizedName(this.getRegistryName().toString().toLowerCase());
		this.setMaxStackSize(1);
		this.addPropertyOverride(new ResourceLocation(DisenchanterMain.MODID, "fill_level"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, World world, EntityLivingBase entityIn) {
				ensureValidTag(stack);
				return getExperienceFillLevel(stack);
			}
		});
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) { 
		super.getSubItems(tab, subItems);
		if (tab == CreativeTabs.SEARCH) {
			ItemStack fullJar = new ItemStack(this);
			ensureValidTag(fullJar);
			setStoredExperience(fullJar, getExperienceCapacity(fullJar));
			subItems.add(fullJar);
		}
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			int amountToAdd = 0;
			ensureValidTag(stack);
			int xpStored = getStoredExperience(stack);
			if (!player.isSneaking()) {
				int xpToLevelup = MathHelper.ceil(player.xpBarCap() * (1.0F - player.experience));
				amountToAdd = xpToLevelup;
			} else {
				amountToAdd = xpStored;
			}
			amountToAdd = Math.min(amountToAdd, xpStored);
			player.addExperience(amountToAdd);
			setStoredExperience(stack, xpStored - amountToAdd);
		}
		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (hasStoredExperience(stack)) {
			player.setActiveHand(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 16;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> l, ITooltipFlag advanced) {
		super.addInformation(stack, world, l, advanced);

		l.add(new TextComponentTranslation("msg.stored_xp.txt", getStoredExperience(stack), getExperienceCapacity(stack))
				.setStyle(new Style().setColor(TextFormatting.GREEN)).getFormattedText());
	}
	
	/**
	 * Ensures that the given stack has a valid NBT-tag with all needed components.
	 * 
	 * @param stack the itemstack
	 * @return true if the stack has been modified, false otherwise
	 */
	public static boolean ensureValidTag(ItemStack stack) {
		boolean modified = false;
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			modified = true;
		}
		if (getExperienceCapacity(stack) <= 0) {
			setExperienceCapacity(stack, DisenchanterConfig.general.jarDefaultExperienceCapacity);
			modified = true;
		}
		if (!hasStoredExperience(stack)) {
			setStoredExperience(stack, 0);
			modified = true;
		} else if (getExperienceFillLevel(stack) > 1) {
			setStoredExperience(stack, getExperienceCapacity(stack));
			modified = true;
		}
		return modified;
	}
	
	/**
	 * Returns whether the stack can store any more experience.
	 * 
	 * @param stack the itemstack
	 * @return true if the stored experience is not at full capacity, false otherwise
	 */
	public static boolean hasAvailableExperienceCapacity(ItemStack stack) {
		return getExperienceFillLevel(stack) < 1;
	}
	
	/**
	 * Returns the percentage of how much of the stack's experience capacity is filled by its stored experience.
	 * 
	 * @param stack the itemstack
	 * @return the percentage or 0 if the stack has no capacity
	 */
	public static float getExperienceFillLevel(ItemStack stack) {
		int capacity = getExperienceCapacity(stack);
		if (capacity == 0)
			return 0;
		return getStoredExperience(stack) / capacity;
	}
	
	/**
	 * Returns the capacity for experience points to be stored in the stack.
	 * 
	 * @param stack the itemstack
	 * @return the amount of xp or 0 if the stack did not contain any xp
	 */
	public static int getExperienceCapacity(ItemStack stack) {
		if (stack.hasTagCompound())
			return stack.getTagCompound().getInteger(EXPERIENCE_CAPACITY_KEY);
		return 0;
	}
	
	/**
	 * Sets the capacity for experience points to be stored in the stack.
	 * This will fail if the item has no NBT-tag or if the specified capacity is invalid.
	 * 
	 * @param stack the itemstack
	 * @param capacity a positive capacity to set
	 * @return true if this was successful, false otherwise
	 */
	public static boolean setExperienceCapacity(ItemStack stack, int capacity) {
		if (stack.hasTagCompound()) {
			if (capacity >= 0) {
				stack.getTagCompound().setInteger(EXPERIENCE_CAPACITY_KEY, capacity);
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether there are any experience points stored in the stack.
	 * 
	 * @param stack the itemstack
	 * @return true if there are stored xp, false otherwise
	 */
	public static boolean hasStoredExperience(ItemStack stack) {
		return getStoredExperience(stack) > 0;
	}

	/**
	 * Returns the amount of experience points stored in the stack.
	 * 
	 * @param stack the itemstack
	 * @return the amount of xp or 0 if the stack did not contain any xp
	 */
	public static int getStoredExperience(ItemStack stack) {
		if (stack.hasTagCompound())
			return stack.getTagCompound().getInteger(STORED_EXPERIENCE_KEY);
		return 0;
	}
	
	/**
	 * Sets the amount of experience points stored in the stack.
	 * This will fail if the item has no NBT-tag or if the specified amount is invalid.
	 * 
	 * @param stack the itemstack
	 * @param amount a positive amount of xp to set
	 * @return true if this was successful, false otherwise
	 */
	public static boolean setStoredExperience(ItemStack stack, int amount) {
		if (stack.hasTagCompound()) {
			if (amount >= 0 && amount <= getExperienceCapacity(stack)) {
				stack.getTagCompound().setInteger(STORED_EXPERIENCE_KEY, amount);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Sets the amount of experience points stored in the stack; the amount will be clamped to a valid range.
	 * This will fail if the item has no NBT-tag.
	 * 
	 * @param stack the itemstack
	 * @param amount a positive amount of xp to set
	 * @return true if this was successful, false otherwise
	 */
	public static boolean setStoredExperienceClamped(ItemStack stack, int amount) {
		return setStoredExperience(stack, MathHelper.clamp(amount, 0, getExperienceCapacity(stack)));
	}

}
