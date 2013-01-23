package us.th3controller.blockcontrol;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockControl extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	PluginDescriptionFile pdfile;
	
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
	}
	public void onDisable() {
		log.info("[BlockControl] Successfully terminated the plugin!");
	}
}
