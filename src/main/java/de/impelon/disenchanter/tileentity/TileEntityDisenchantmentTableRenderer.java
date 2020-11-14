package de.impelon.disenchanter.tileentity;

import de.impelon.disenchanter.DisenchanterConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityDisenchantmentTableRenderer extends TileEntityEnchantmentTableRenderer {

	@Override
	public void render(TileEntityEnchantmentTable tileentity, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		if (DisenchanterConfig.visual.bookRendererHidden)
			return;
		
		GlStateManager.pushMatrix();
		float bookYOffset = (float) DisenchanterConfig.visual.bookRendererYOffset;
		GlStateManager.translate((float) x, (float) y + bookYOffset, (float) z);

		if (DisenchanterConfig.visual.bookRendererFlipped) {
			float f = tileentity.tickCount + partialTicks;
			float bookFloatOffset = 0.85F + MathHelper.sin(f * 0.1F) * 0.01F;
			GlStateManager.translate(0.5F, bookFloatOffset, 0.5F);
			GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(180, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-0.5F, -bookFloatOffset, -0.5F);
			tileentity.bookRotationPrev *= -1;
			tileentity.bookRotation *= -1;
			super.render(tileentity, 0, 0, 0, partialTicks, destroyStage, alpha);
			tileentity.bookRotationPrev *= -1;
			tileentity.bookRotation *= -1;
		} else {
			super.render(tileentity, 0, 0, 0, partialTicks, destroyStage, alpha);
		}
		GlStateManager.popMatrix();
	}

}