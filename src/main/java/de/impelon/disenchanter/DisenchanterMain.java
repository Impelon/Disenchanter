package de.impelon.disenchanter;

import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Mod(modid = DisenchanterMain.MODID, name = DisenchanterMain.NAME, version = DisenchanterMain.VERSION, updateJSON = DisenchanterMain.UPDATE_JSON)
public class DisenchanterMain {

	public static final String NAME = "Disenchanter";
	public static final String MODID = "disenchanter";
	public static final String VERSION = "1.8";
	public static final String UPDATE_JSON = "https://raw.githubusercontent.com/Impelon/Disenchanter/meta/versions.json";

	@Mod.Instance(value = MODID)
	public static DisenchanterMain instance;

	@SidedProxy(clientSide = "de.impelon.disenchanter.proxy.CombinedClientProxy", serverSide = "de.impelon.disenchanter.proxy.ServerProxy")
	public static CommonProxy proxy;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent ev) {
		proxy.preInit(ev);
	}

	@EventHandler
	public static void load(FMLInitializationEvent ev) {
		proxy.load(ev);
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent ev) {
		proxy.postInit(ev);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> ev) {
		proxy.registerBlocks(ev);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> ev) {
		proxy.registerItems(ev);
	}

	@SubscribeEvent
	public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> ev) {
		proxy.registerSoundEvents(ev);
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> ev) {
		proxy.registerRecipes(ev);
	}

}
