package de.impelon.disenchanter;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.proxies.CommonProxy;
import de.impelon.disenchanter.update.VersionChecker;

@Mod(modid="disenchanter", name="Disenchanter", version="1.5")
public class DisenchanterMain {
	
	public static final String MODID = "disenchanter";
	public static final String VERSION = "1.5";
	public static final String PREFIX = TextFormatting.GRAY + "[" + TextFormatting.GOLD + TextFormatting.BOLD + 
			"Disenchanter" + TextFormatting.GRAY + "] " + TextFormatting.RESET;
	public static final VersionChecker versionChecker = new VersionChecker();
	public static Configuration config;
	
	@Mod.Instance(value="disenchanter")
	public static DisenchanterMain instance;
	
	@SidedProxy(clientSide="de.impelon.disenchanter.proxies.CombinedClientProxy", serverSide="de.impelon.disenchanter.proxies.ServerProxy")
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
