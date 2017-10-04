package de.impelon.disenchanter;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import de.impelon.disenchanter.DisenchanterMain;
import de.impelon.disenchanter.crafting.UpgradeTableRecipe;
import de.impelon.disenchanter.proxies.CommonProxy;
import de.impelon.disenchanter.update.VersionChecker;

@Mod.EventBusSubscriber
@Mod(modid = DisenchanterMain.MODID, name = DisenchanterMain.NAME, version = DisenchanterMain.VERSION)
public class DisenchanterMain {
	
	public static final String NAME = "Disenchanter";
	public static final String MODID = "disenchanter";
	public static final String VERSION = "1.5";
	public static final String PREFIX = TextFormatting.GRAY + "[" + TextFormatting.GOLD + TextFormatting.BOLD + 
			NAME + TextFormatting.GRAY + "] " + TextFormatting.RESET;
	public static final VersionChecker versionChecker = new VersionChecker();
	public static Configuration config;
	
	@Mod.Instance(value = MODID)
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
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> ev) {
		
		proxy.registerBlocks(ev);
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> ev) {
		proxy.registerItems(ev);
	}
	
	@SubscribeEvent
	public static void registerPotions(RegistryEvent.Register<Potion> ev) {
		proxy.registerPotions(ev);
	}
	
	@SubscribeEvent
	public static void registerBiomes(RegistryEvent.Register<Biome> ev) {
		proxy.registerBiomes(ev);
	}
	
	@SubscribeEvent
	public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> ev) {
		proxy.registerSoundEvents(ev);
	}
	
	@SubscribeEvent
	public static void registerPotionTypes(RegistryEvent.Register<PotionType> ev) {
		proxy.registerPotionTypes(ev);
	}
	
	@SubscribeEvent
	public static void registerEnchantments(RegistryEvent.Register<Enchantment> ev) {
		proxy.registerEnchantments(ev);
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> ev) {
		proxy.registerRecipes(ev);
	}
	
	@SubscribeEvent
	public static void registerVillagerProfessions(RegistryEvent.Register<VillagerProfession> ev) {
		proxy.registerVillagerProfessions(ev);
	}
	
	@SubscribeEvent
	public static void registerEntityEntries(RegistryEvent.Register<EntityEntry> ev) {
		proxy.registerEntityEntries(ev);
	}

}
