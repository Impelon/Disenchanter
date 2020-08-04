package de.impleon.disenchanter.tileentity;

import de.impelon.disenchanter.DisenchanterMain;
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
		GlStateManager.pushMatrix();
		float bookYOffset = (float) DisenchanterMain.config.get("visual", "BookRenderYOffset", 0.4).getDouble();
		GlStateManager.translate((float) x, (float) y + bookYOffset, (float) z);

		if (DisenchanterMain.config.get("visual", "BookRenderFlipped", true).getBoolean()) {
			float f = (float) tileentity.tickCount + partialTicks;
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