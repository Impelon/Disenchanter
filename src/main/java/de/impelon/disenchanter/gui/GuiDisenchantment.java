package de.impelon.disenchanter.gui;

import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.inventory.ContainerDisenchantmentBase;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDisenchantment extends GuiContainer {

	private static final ResourceLocation guiResource = new ResourceLocation(DisenchanterMain.MODID,
			"textures/gui/container/disenchanting_table.png");
	private String displayName;

	public GuiDisenchantment(InventoryPlayer playerInventory, World world, BlockPos position, String displayName) {
		super(ContainerDisenchantmentBase.create(playerInventory, world, position));
		this.displayName = displayName;
	}

	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(x, y, partialTicks);
		this.renderHoveredToolTip(x, y);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		if (this.displayName != null)
			this.fontRenderer.drawString(this.displayName, 8, 5, 4210752);
		this.fontRenderer.drawString((new TextComponentTranslation("container.inventory")).getFormattedText(), 8,
				this.ySize - 94, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(guiResource);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

}
