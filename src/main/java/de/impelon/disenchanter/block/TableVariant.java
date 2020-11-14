package de.impelon.disenchanter.block;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public enum TableVariant {
	AUTOMATIC, BULKDISENCHANTING, CYCLING, VOIDING;

	public String toString() {
		return this.name().toLowerCase();
	}

	public TextComponentTranslation getDescription() {
		return new TextComponentTranslation("msg." + this.toString() + ".txt");
	}
	
	public boolean hasVariantAt(World world, BlockPos position) {
		return hasVariant(world.getBlockState(position));
	}
	
	public boolean hasVariant(IBlockState state) {
		return state.getValue(PropertyBool.create(this.toString()));
	}
	
}