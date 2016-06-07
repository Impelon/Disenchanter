package de.impelon.disenchanter.blocks;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDisenchantmentTable extends TileEntitySpecialRenderer {
	
	private static final ResourceLocation bookResource = new ResourceLocation("textures/entity/enchanting_table_book.png");
	private ModelBook bookModel = new ModelBook();

	public void renderTileEntityAt(TileEntityDisenchantmentTable tileentity,
			double x, double y, double z, float f) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.15F, (float) z + 0.5F);
		float f1 = (float) tileentity.tickCount + f;
		GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(f1 * 0.1F) * 0.01F, 0.0F);
		
		float f2;

		for (f2 = tileentity.bookRotation - tileentity.bookRotationPrev; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {}
		
		while (f2 < -(float) Math.PI)
			f2 += ((float) Math.PI * 2F);

		float f3 = tileentity.bookRotationPrev + f2 * f;
		GL11.glRotatef(-f3 * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-80.0F, 0.0F, 0.0F, 1.0F);
		this.bindTexture(bookResource);
		float f4 = tileentity.pageFlipPrev + (tileentity.pageFlip - tileentity.pageFlipPrev) * f + 0.25F;
		float f5 = f4 + 0.5F;
		f4 = (f4 - (float) MathHelper.truncateDoubleToInt(f4)) * 1.6F - 0.3F;
		f5 = (f5 - (float) MathHelper.truncateDoubleToInt(f5)) * 1.6F - 0.3F;

		if (f4 < 0.0F)
			f4 = 0.0F;
		else if (f4 > 1.0F)
			f4 = 1.0F;

		if (f5 < 0.0F)
			f5 = 0.0F;
		else if (f5 > 1.0F)
			f5 = 1.0F;

		float f6 = tileentity.bookSpreadPrev + (tileentity.bookSpread - tileentity.bookSpreadPrev) * f;
		GL11.glEnable(GL11.GL_CULL_FACE);
		this.bookModel.render((Entity) null, f1, f5, f4, f6, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}

	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		this.renderTileEntityAt((TileEntityDisenchantmentTable) tileentity, x, y, z, f);
	}
}