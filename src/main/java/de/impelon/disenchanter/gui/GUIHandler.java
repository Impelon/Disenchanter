package de.impelon.disenchanter.gui;

import de.impelon.disenchanter.inventory.ContainerDisenchantmentBase;
import de.impelon.disenchanter.tileentity.TileEntityDisenchantmentTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos position = new BlockPos(x, y, z);
		if (world.getTileEntity(position) instanceof TileEntityDisenchantmentTable)
			return ContainerDisenchantmentBase.create(player.inventory, world, position);
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos position = new BlockPos(x, y, z);
		TileEntity tileentity = world.getTileEntity(position);
		if (tileentity instanceof TileEntityDisenchantmentTable)
			return new GuiDisenchantment(player.inventory, world, position, ((TileEntityDisenchantmentTable) tileentity).getDisplayName().getUnformattedText());
		return null;
	}

}
