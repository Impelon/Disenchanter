package de.impelon.disenchanter.gui;

import de.impelon.disenchanter.inventory.ContainerDisenchantment;
import de.impelon.disenchanter.tileentity.TileEntityDisenchantmentTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (world.getTileEntity(new BlockPos(x, y, z)) instanceof TileEntityDisenchantmentTable)
			return new ContainerDisenchantment(player.inventory, world, new BlockPos(x, y, z));
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileentity = world.getTileEntity(new BlockPos(x, y, z));
		if (tileentity instanceof TileEntityDisenchantmentTable) {
			TileEntityDisenchantmentTable t = (TileEntityDisenchantmentTable) tileentity;
			return new GuiDisenchantment(player.inventory, world, x, y, z, t.hasCustomName() ? t.getName() : null);
		}
		return null;
	}

}
