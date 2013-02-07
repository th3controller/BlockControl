package us.th3controller.blockcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockControl extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	PluginDescriptionFile pdfile;
	
	public HashMap<String, String> message = new HashMap<String, String>();
	public HashMap<String, String> bucket = new HashMap<String, String>();
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BlockControlListener(this), this);
		pdfile = getDescription();
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
			log.warning("[BlockControl] Could not connect to metrics!");
		}
		try {
			URL url = new URL("http://dl.dropbox.com/u/34716611/Public%20Files/blockcontrol.properties");
			URLConnection connection = url.openConnection();
			
			connection.setDoInput(true);
			InputStream inStream = connection.getInputStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(inStream));
			
			String line = input.readLine();
			if (line != null) {
				double currentVer = parseVersion(pdfile.getVersion());
				double newVer = parseVersion(line);
				if (newVer > currentVer) {
					log.warning("[BlockControl] Latest version is available now on BukkitDev!");
					log.warning("[BlockControl] http://goo.gl/pc6D9");
				}
			}
			
		} catch (IOException e) {
			// Failed to check for updates :-(
			log.warning("[BlockControl] Could not check for latest updates!");
		}
		log.info("[BlockControl] Successfully initiated the plugin!");
		log.info("[BlockControl] Running version "+pdfile.getVersion());
		log.info("[BlockControl] GNU General Public License version 3 (GPLv3)");
		getConfig().options().copyDefaults(true);
		saveConfig();
		message.put("pmsg", this.getConfig().getString("placemessage"));
		message.put("dmsg", this.getConfig().getString("destroymessage"));
		if(this.getConfig().getBoolean("disableendereggteleport", true)) {
			getServer().getPluginManager().registerEvents(new EnderEggListener(this), this);
		}
		if(this.getConfig().getBoolean("pickupdelete", true)) {
			getServer().getPluginManager().registerEvents(new PickupListener(this), this);
		}
		if(this.getConfig().getBoolean("bucket.disablelava", true)) {
			bucket.put("lava", "true");
		}
		if(this.getConfig().getBoolean("bucket.disablewater", true)) {
			bucket.put("water", "true");
		}
	}
	public void onDisable() {
		log.info("[BlockControl] Successfully terminated the plugin!");
	}
	private double parseVersion(String toParse) {
		String[] parts = toParse.split("\\.");
		double version = 0.0D;
		for (int i = 0; i < parts.length; i++) {
			version += Integer.parseInt(parts[i]) * Math.pow(10.0D, -2 * i);
		}
		return version;
	}
}
