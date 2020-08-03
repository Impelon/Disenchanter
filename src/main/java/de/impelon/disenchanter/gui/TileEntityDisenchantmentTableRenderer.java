package de.impelon.disenchanter.gui;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;

import de.impelon.disenchanter.blocks.TileEntityDisenchantmentTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityDisenchantmentTableRenderer extends TileEntitySpecialRenderer<TileEntityDisenchantmentTable> {
	
	private static final ResourceLocation bookResource = new ResourceLocation("textures/entity/enchanting_table_book.png");
	private ModelBook bookModel = new ModelBook();
	
	@Override
	public void render(TileEntityDisenchantmentTable tileentity,
			double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.15F, (float) z + 0.5F);
		float f1 = (float) tileentity.tickCount + partialTicks;
		GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(f1 * 0.1F) * 0.01F, 0.0F);
		
		float f2;

		for (f2 = tileentity.bookRotation - tileentity.bookRotationPrev; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F));
		
		while (f2 < -(float) Math.PI)
			f2 += ((float) Math.PI * 2F);

		float f3 = tileentity.bookRotationPrev + f2 * partialTicks;
		GlStateManager.rotate(-f3 * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-80.0F, 0.0F, 0.0F, 1.0F);
		this.bindTexture(bookResource);
		float f4 = tileentity.pageFlipPrev + (tileentity.pageFlip - tileentity.pageFlipPrev) * partialTicks + 0.25F;
		float f5 = f4 + 0.5F;
		f4 = (f4 - (float) MathHelper.fastFloor(f4)) * 1.6F - 0.3F;
		f5 = (f5 - (float) MathHelper.fastFloor(f5)) * 1.6F - 0.3F;

		if (f4 < 0.0F)
			f4 = 0.0F;
		else if (f4 > 1.0F)
			f4 = 1.0F;

		if (f5 < 0.0F)
			f5 = 0.0F;
		else if (f5 > 1.0F)
			f5 = 1.0F;

		float f6 = tileentity.bookSpreadPrev + (tileentity.bookSpread - tileentity.bookSpreadPrev) * partialTicks;
		GlStateManager.enableCull();
		this.bookModel.render((Entity) null, f1, f5, f4, f6, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}

}