package de.impelon.disenchanter.item;

import java.util.List;

import de.impelon.disenchanter.DisenchanterConfig;
import de.impelon.disenchanter.DisenchanterMain;
import net.minecraft.block.state.IBlockState;
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
				return getStoredExperience(stack) / getExperienceCapacity(stack);
			}
		});
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos,
			EntityLivingBase entityLiving) {
		this.ensureValidTag(stack);
		this.setStoredExperience(stack, this.getStoredExperience(stack) + 256);
		return super.onBlockDestroyed(stack, world, state, pos, entityLiving);
	}

	/**
	 * Ensures that the given stack has a valid NBT-tag with all needed components.
	 * 
	 * @param stack the itemstack
	 * @return true if the stack has been modified, false otherwise
	 */
	public boolean ensureValidTag(ItemStack stack) {
		boolean modified = false;
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			modified = true;
		}
		if (getExperienceCapacity(stack) <= 0) {
			this.setExperienceCapacity(stack, DisenchanterConfig.general.jarDefaultExperienceCapacity);
			modified = true;
		}
		if (!this.hasStoredExperience(stack)) {
			this.setStoredExperience(stack, 0);
			modified = true;
		}
		return modified;
	}
	
	/**
	 * Returns the capacity for experience points to be stored in the stack.
	 * 
	 * @param stack the itemstack
	 * @return the amount of xp or 0 if the stack did not contain any xp
	 */
	public int getExperienceCapacity(ItemStack stack) {
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
	public boolean setExperienceCapacity(ItemStack stack, int capacity) {
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
	public boolean hasStoredExperience(ItemStack stack) {
		return getStoredExperience(stack) > 0;
	}

	/**
	 * Returns the amount of experience points stored in the stack.
	 * 
	 * @param stack the itemstack
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
	 * @param stack the itemstack
	 * @param amount a positive amount of xp to set
	 * @return true if this was successful, false otherwise
	 */
	public boolean setStoredExperience(ItemStack stack, int amount) {
		if (stack.hasTagCompound()) {
			if (amount >= 0 && amount <= this.getExperienceCapacity(stack)) {
				stack.getTagCompound().setInteger(STORED_EXPERIENCE_KEY, amount);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			int amountToAdd = 0;
			this.ensureValidTag(stack);
			int xpStored = this.getStoredExperience(stack);
			if (!player.isSneaking()) {
				int xpToLevelup = MathHelper.ceil(player.xpBarCap() * (1.0F - player.experience));
				amountToAdd = xpToLevelup;
			} else {
				amountToAdd = xpStored;
			}
			amountToAdd = Math.min(amountToAdd, xpStored);
			player.addExperience(amountToAdd);
			this.setStoredExperience(stack, xpStored - amountToAdd);
		}
		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (this.hasStoredExperience(stack)) {
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

		l.add(new TextComponentTranslation("msg.stored_xp.txt", this.getStoredExperience(stack), this.getExperienceCapacity(stack))
				.setStyle(new Style().setColor(TextFormatting.GREEN)).getFormattedText());
	}

}
