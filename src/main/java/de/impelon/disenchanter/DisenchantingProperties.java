package de.impelon.disenchanter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.impelon.disenchanter.block.BlockDisenchantmentTable;
import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class DisenchantingProperties {

	protected SortedSet<TableVariant> variants;

	public enum TableVariant {
		AUTOMATIC, BULKDISENCHANTING, VOIDING;

		public String toString() {
			return this.name().toLowerCase();
		}

		public TextComponentTranslation getDescription() {
			return new TextComponentTranslation("msg." + this.toString() + ".txt");
		}
	}

	public static DisenchantingProperties getPropertiesFromStateAt(World world, BlockPos position) {
		return getPropertiesFromState(world.getBlockState(position));
	}

	public static DisenchantingProperties getPropertiesFromState(IBlockState state) {
		List<TableVariant> variants = new ArrayList<TableVariant>();
		if (state.getBlock().equals(CommonProxy.disenchantmentTable)) {
			if (state.getValue(BlockDisenchantmentTable.AUTOMATIC))
				variants.add(TableVariant.AUTOMATIC);
			if (state.getValue(BlockDisenchantmentTable.BULKDISENCHANTING))
				variants.add(TableVariant.BULKDISENCHANTING);
			if (state.getValue(BlockDisenchantmentTable.VOIDING))
				variants.add(TableVariant.VOIDING);
			return new DisenchantingProperties(variants);
		}
		return null;
	}

	public DisenchantingProperties(TableVariant... variants) {
		this(Arrays.asList(variants));
	}

	public DisenchantingProperties(Collection<TableVariant> variants) {
		this.variants = new TreeSet<TableVariant>(variants);
	}

	public boolean hasPersistantInventory() {
		return this.is(TableVariant.AUTOMATIC);
	}

	public int getDisenchantmentIndex(World world, BlockPos position) {
		return 0;
	}

	public boolean is(TableVariant variant) {
		return this.variants.contains(variant);
	}

	public Set<TableVariant> getVariants() {
		return this.variants;
	}

	public List<String> getTableVariantDescriptions() {
		List<String> descriptions = new ArrayList<String>();
		for (TableVariant variant : this.getVariants())
			descriptions.add(variant.getDescription().getUnformattedText());
		return descriptions;
	}

}
