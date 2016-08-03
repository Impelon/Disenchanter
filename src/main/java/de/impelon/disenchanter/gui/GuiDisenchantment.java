package de.impelon.disenchanter.gui;

import org.lwjgl.opengl.GL11;

import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.blocks.ContainerDisenchantment;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiDisenchantment extends GuiContainer {
	
	private static final ResourceLocation guiResource = new ResourceLocation(DisenchanterMain.MODID, "textures/gui/container/disenchanting_table.png");
	private String customName;

	public GuiDisenchantment(InventoryPlayer p, World w,
			int x, int y, int z, String customName) {
		super(new ContainerDisenchantment(p, w, new BlockPos(x, y, z)));
		this.customName = customName;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		this.fontRendererObj.drawString(this.customName == null ? 
				I18n.format("container.disenchant", new Object[0]) : this.customName, 8, 5, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f,
			int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(guiResource);
        this.drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
	}
	
}