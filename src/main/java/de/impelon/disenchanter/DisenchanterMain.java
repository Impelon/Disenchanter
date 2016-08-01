package de.impelon.disenchanter;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.impelon.disenchanter.proxies.CommonProxy;
import de.impelon.disenchanter.update.VersionChecker;

@Mod(modid = "DisenchanterMod", name = "Disenchanter", version = "1.3")
public class DisenchanterMain {

	public static final String MODID = "DisenchanterMod";
	public static final String VERSION = "1.3";
	public static final VersionChecker versionChecker = new VersionChecker();
	public static Configuration config;

	@Mod.Instance(value = "DisenchanterMod")
	public static DisenchanterMain instance;

	@SidedProxy(clientSide = "de.impelon.disenchanter.proxies.CombinedClientProxy", serverSide = "de.impelon.disenchanter.proxies.ServerProxy")
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

}
