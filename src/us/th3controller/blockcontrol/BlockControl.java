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
	public HashMap<String, String> bucket = new HashMap<String, String>();
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BlockControlListener(this), this);
		pdfile = getDescription();
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
			e.printStackTrace();
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
}
