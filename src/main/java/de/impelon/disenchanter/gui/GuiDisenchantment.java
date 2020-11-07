package de.impelon.disenchanter.gui;

import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.block.BlockDisenchantmentTable;
import de.impelon.disenchanter.inventory.ContainerDisenchantmentBase;
import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.block.state.IBlockState;
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
	
	public enum TextAlignment {
		CENTERED,
		LEFT_ALIGNED,
		RIGHT_ALIGNED;
	}

	private static final ResourceLocation guiResource = new ResourceLocation(DisenchanterMain.MODID,
			"textures/gui/container/disenchanting_table.png");
	private String tableName;
	private String inventoryName;
	private boolean isAutomatic = false;
	private boolean isBulkDisenchanting = false;
	private boolean isVoiding = false;

	public GuiDisenchantment(InventoryPlayer playerInventory, World world, BlockPos position, String tableName) {
		super(ContainerDisenchantmentBase.create(playerInventory, world, position));
		this.tableName = tableName;
		this.inventoryName = playerInventory.getDisplayName().getUnformattedText();
		IBlockState state = world.getBlockState(position);
		if (state.getBlock().equals(CommonProxy.disenchantmentTable)) {
			this.isAutomatic = state.getValue(BlockDisenchantmentTable.AUTOMATIC);
			this.isBulkDisenchanting = state.getValue(BlockDisenchantmentTable.BULKDISENCHANTING);
			this.isVoiding = state.getValue(BlockDisenchantmentTable.VOIDING);
		}
	}

	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(x, y, partialTicks);
		this.renderHoveredToolTip(x, y);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		if (this.tableName != null)
			this.fontRenderer.drawString(this.tableName, 8, 5, 4210752);
		if (this.isAutomatic) 
			this.drawString(new TextComponentTranslation("msg.automatic.txt").getUnformattedText(), this.xSize - 8, 5, 11184810, false, TextAlignment.RIGHT_ALIGNED);
		if (this.isBulkDisenchanting)
			this.drawString(new TextComponentTranslation("msg.bulk.txt").getUnformattedText(), this.xSize - 8, 5 + (this.fontRenderer.FONT_HEIGHT + 1), 11184810, false, TextAlignment.RIGHT_ALIGNED);
		if (this.isVoiding)
			this.drawString(new TextComponentTranslation("msg.voiding.txt").getUnformattedText(), this.xSize - 8, 5 + (this.fontRenderer.FONT_HEIGHT + 1) * 2, 11184810, false, TextAlignment.RIGHT_ALIGNED);
		this.fontRenderer.drawString(this.inventoryName, 8, this.ySize - 94, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(guiResource);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	protected int drawString(String text, float x, float y, int color, boolean dropShadow, TextAlignment alignment) {
		float offset = 0;
		switch (alignment) {
		case CENTERED:
			offset = this.fontRenderer.getStringWidth(text) / 2;
		case RIGHT_ALIGNED:
			offset = this.fontRenderer.getStringWidth(text);
		default:
			break;
		}
		return this.fontRenderer.drawString(text, x - offset, y, color, dropShadow);
	}

}
