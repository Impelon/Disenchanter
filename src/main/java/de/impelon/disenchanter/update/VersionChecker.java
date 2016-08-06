package de.impelon.disenchanter.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import de.impelon.disenchanter.DisenchanterMain;

public class VersionChecker implements Runnable {

	private static boolean isLatestVersion = false;
	private static String latestVersion = "";
	private static String url = "";

	@Override
	public void run() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/Impelon/Disenchanter/1.7.10/src/main/resources/mcmod.info").openStream(), "UTF-8"));
			StringBuilder modinfo = new StringBuilder();
			String ln;
			while ((ln = in.readLine()) != null)
				modinfo.append(ln);
			
			int verindex = modinfo.indexOf("\"version\"");
			if (verindex != -1) {
				String verstr = modinfo.substring(verindex + 9, modinfo.indexOf(",", verindex));
				latestVersion = verstr.substring(verstr.indexOf('\"') + 1, verstr.lastIndexOf('\"'));
			}
			int urlindex = modinfo.indexOf("\"url\"");
			if (urlindex != -1) {
				String urlstr = modinfo.substring(urlindex + 5, modinfo.indexOf(",", urlindex));
				url = urlstr.substring(urlstr.indexOf('\"') + 1, urlstr.lastIndexOf('\"'));
			}
		} catch (IOException ex) {
			latestVersion = DisenchanterMain.VERSION;
		}
		if (in != null) {
			try {
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		isLatestVersion = DisenchanterMain.VERSION.equals(this.getLatestVersion());
	}

	public boolean isLatestVersion() {
		return isLatestVersion;
	}

	public String getLatestVersion() {
		return latestVersion;
	}
	
	public String getUrl() {
		return url;
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onEvent(PlayerTickEvent ev) {
		if (ev.player.worldObj.isRemote) {
			if (isLatestVersion()) {
				FMLCommonHandler.instance().bus().unregister(this);
				return;
			}
			ChatStyle linkStyle = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, getUrl()));
			ChatComponentText warning = new ChatComponentText("§7[§6§lDisenchanter§7] §r" +
					new ChatComponentTranslation("msg.outdated.txt").getFormattedText() + " §o(" +
					new ChatComponentTranslation("msg.currentversion.txt").getUnformattedText() + DisenchanterMain.VERSION + " §o" +
					new ChatComponentTranslation("msg.latestversion.txt").getUnformattedText() + getLatestVersion() + ")");
			warning.setChatStyle(linkStyle);
			ev.player.addChatMessage(warning);
			FMLCommonHandler.instance().bus().unregister(this);
		}
	}

}
