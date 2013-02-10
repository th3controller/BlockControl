package us.th3controller.blockcontrol;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockControl extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	PluginDescriptionFile pdfile;
	
	public HashMap<String, String> message = new HashMap<String, String>();
	public HashMap<String, String> bool = new HashMap<String, String>();
	
	/**
	 * Shows a message in the console with a prefix tag
	 * @param msg The message to be displayed on the console
	 */
	public void lm(String msg){
		log.info("[BlockControl] " + msg);
	}
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BlockControlListener(this), this);
		pdfile = getDescription();
		getConfig().options().copyDefaults(true);
		saveConfig();
		boolConfig();
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
			log.warning("[BlockControl] Could not connect to metrics!");
		}
		lm("Successfully initiated the plugin!");
		lm("Running version "+pdfile.getVersion());
		lm("GNU General Public License version 3 (GPLv3)");
		if(getConfig().getBoolean("checkforupdates", true)) {
			getServer().getScheduler().runTaskAsynchronously(this, new UpdateCheck(this));
		}
	}
	public void onDisable() {
		lm("Successfully terminated the plugin!");
	}
	public void boolConfig() {
		//Cached messages
		message.put("pmsg", getConfig().getString("placemessage"));
		message.put("dmsg", getConfig().getString("destroymessage"));
		//Separate listeners, implemented to reduce event checks
		if(getConfig().getBoolean("disableendereggteleport", true)) {
			getServer().getPluginManager().registerEvents(new EnderEggListener(this), this);
		}
		if(getConfig().getBoolean("pickupdelete", true)) {
			getServer().getPluginManager().registerEvents(new PickupListener(this), this);
		}
		//Cached booleans
		if(getConfig().getBoolean("dropdelete", true)) {
			bool.put("dropdelete", "true");
		} else {
			bool.put("dropdelete", "false");
		}
	}
	public double parseVersion(String toParse) {
		String[] parts = toParse.split("\\.");
		double version = 0.0D;
		for (int i = 0; i < parts.length; i++) {
			version += Integer.parseInt(parts[i]) * Math.pow(10.0D, -2 * i);
		}
		return version;
	}
}
