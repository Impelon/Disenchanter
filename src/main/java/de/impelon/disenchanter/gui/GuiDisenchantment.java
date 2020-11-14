package de.impelon.disenchanter.gui;

import java.util.List;

import de.impelon.disenchanter.DisenchanterConfig;
import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.DisenchantingProperties;
import de.impelon.disenchanter.inventory.ContainerDisenchantmentBase;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
	private List<String> descriptions;

	public GuiDisenchantment(InventoryPlayer playerInventory, World world, BlockPos position, String tableName) {
		super(ContainerDisenchantmentBase.create(playerInventory, world, position));
		this.tableName = tableName;
		this.inventoryName = playerInventory.getDisplayName().getUnformattedText();
		this.descriptions = DisenchantingProperties.getPropertiesFromStateAt(world, position).getTableVariantDescriptions();
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
		if (DisenchanterConfig.visual.showUpgradesInGUI) {
			float offset = 0;
			for (String description : descriptions) {
				this.drawString(description, this.xSize - 8, 5 + offset, DisenchanterConfig.visual.descriptionInGUIColor, false, TextAlignment.RIGHT_ALIGNED);
				offset += this.fontRenderer.FONT_HEIGHT + 1;
			}
		}
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
