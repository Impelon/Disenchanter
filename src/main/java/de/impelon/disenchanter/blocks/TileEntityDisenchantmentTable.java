package de.impelon.disenchanter.blocks;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityDisenchantmentTable extends TileEntity {

	public int tickCount;
	public float pageFlip;
	public float pageFlipPrev;
	public float pagesFlipping;
	public float pageFlipChange;
	public float bookSpread;
	public float bookSpreadPrev;
	public float bookRotation;
	public float bookRotationPrev;
	public float bookRotationChange;
	private static Random random = new Random();
	private String customName;

	public void writeToNBT(NBTTagCompound nbtData) {
		super.writeToNBT(nbtData);

		if (this.hasCustomName())
			nbtData.setString("CustomName", this.customName);
	}

	public void readFromNBT(NBTTagCompound nbtData) {
		super.readFromNBT(nbtData);

		if (nbtData.hasKey("CustomName", 8))
			this.customName = nbtData.getString("CustomName");
	}

	public void updateEntity() {
		super.updateEntity();
		this.bookSpreadPrev = this.bookSpread;
		this.bookRotationPrev = this.bookRotation;
		EntityPlayer entityplayer = this.worldObj.getClosestPlayer(
				(double) this.xCoord + 0.5D,
				(double) this.yCoord + 0.5D,
				(double) this.zCoord + 0.5D, 3.0D);

		if (entityplayer != null) {
			double distanceX = entityplayer.posX - (this.xCoord + 0.5F);
			double distanceZ = entityplayer.posZ - (this.zCoord + 0.5F);
			this.bookRotationChange = (float) Math.atan2(distanceZ, distanceX);
			this.bookSpread += 0.1F;

			if (this.bookSpread < 0.5F || random.nextInt(40) == 0) {
				float previous = this.pagesFlipping;
				do {
					this.pagesFlipping += (float) (random.nextInt(4) - random.nextInt(4));
				} while (previous == this.pagesFlipping);
			}
		} else {
			this.bookRotationChange += 0.02F;
			this.bookSpread -= 0.1F;
		}

		while (this.bookRotation >= (float) Math.PI)
			this.bookRotation -= ((float) Math.PI * 2F);

		while (this.bookRotation < -(float) Math.PI)
			this.bookRotation += ((float) Math.PI * 2F);

		while (this.bookRotationChange >= (float) Math.PI)
			this.bookRotationChange -= ((float) Math.PI * 2F);

		while (this.bookRotationChange < -(float) Math.PI)
			this.bookRotationChange += ((float) Math.PI * 2F);

		float f2;

		for (f2 = this.bookRotationChange - this.bookRotation; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {}
		
		while (f2 < -(float) Math.PI)
			f2 += ((float) Math.PI * 2F);

		this.bookRotation += f2 * 0.4F;

		if (this.bookSpread < 0.0F)
			this.bookSpread = 0.0F;
		else if (this.bookSpread > 1.0F)
			this.bookSpread = 1.0F;

		++this.tickCount;
		this.pageFlipPrev = this.pageFlip;
		float f = (this.pagesFlipping - this.pageFlip) * 0.4F;
		float f3 = 0.2F;

		if (f < -f3)
			f = -f3;
		else if (f > f3)
			f = f3;

		this.pageFlipChange += (f - this.pageFlipChange) * 0.9F;
		this.pageFlip += this.pageFlipChange;
	}
	
	public String getName() {
		return this.hasCustomName() ? this.customName : "container.disenchant";
	}

	public boolean hasCustomName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

}
