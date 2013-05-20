package us.th3controller.blockcontrol;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockControl extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	PluginDescriptionFile pdfile;
	
	/**
	 * Shows a message in the console with a prefix tag
	 * @param msg The message to be displayed on the console
	 */
	public void lm(String msg){
		log.info("[BlockControl] "+msg);
	}
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BlockControlListener(this), this);
		pdfile = getDescription();
		File file = new File("plugins/BlockControl", "config.yml");
		if(!file.exists()) {
			this.saveResource("config.yml", true);
		}
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
		getCommand("blockcontrol").setExecutor(this);
		if(getConfig().getBoolean("checkforupdates", true)) {
			getServer().getScheduler().runTaskAsynchronously(this, new UpdateCheck(this));
		}
	}
	@Override
	public void onDisable() {
		lm("Successfully terminated the plugin!");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player) || (sender.hasPermission("blockcontrol.command"))) {
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("create")) {
					configCreate("worlds."+args[1]+".no-build", false);
					configCreate("worlds."+args[1]+".block-place", Arrays.asList("7", "19"));
					configCreate("worlds."+args[1]+".block-destroy", Arrays.asList("7", "19"));
					configCreate("worlds."+args[1]+".pickup", Arrays.asList("7", "19"));
					configCreate("worlds."+args[1]+".item-drop", Arrays.asList("7", "19"));
					configCreate("worlds."+args[1]+".lava-bucket-place", false);
					configCreate("worlds."+args[1]+".water-bucket-place", false);
					configCreate("worlds."+args[1]+".lava-bucket-fill", false);
					configCreate("worlds."+args[1]+".water-bucket-fill", false);
					configCreate("worlds."+args[1]+".delete-disabled-pickup", false);
					configCreate("worlds."+args[1]+".disabled-pickup-message", "&cYou cannot pickup this item.");
					configCreate("worlds."+args[1]+".delete-disabled-drop", false);
					configCreate("worlds."+args[1]+".disabled-drop-message", "&cYou cannot drop this item.");
					configCreate("worlds."+args[1]+".delete-disabled-place", false);
					configCreate("worlds."+args[1]+".enderegg-teleport-disable", false);
					configCreate("worlds."+args[1]+".place-message", "&cYou have insufficient permission to place that block.");
					configCreate("worlds."+args[1]+".destroy-message", "&cYou have insufficient permission to destroy that block.");
					configCreate("worlds."+args[1]+".disable-water-physics", false);
					configCreate("worlds."+args[1]+".disable-lava-physics", false);
					configCreate("worlds."+args[1]+".disable-all-drops", false);
					configCreate("worlds."+args[1]+".disable-all-pickup", false);
					saveConfig();
					reloadConfig();
					sender.sendMessage(ChatColor.GREEN+"Configuration section successfully created!");
					sender.sendMessage(ChatColor.RED+"World name is case sensitive! Make sure you typed your world name correctly!");
				}
				return true;
			}
			else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("reload")) {
					reloadConfig();
					sender.sendMessage(ChatColor.GREEN+"BlockControl successfully reloaded!");
				}
				return true;
			}
			else if(args.length == 0) {
				sender.sendMessage(ChatColor.GREEN+"BlockControl Help:");
				sender.sendMessage(" - /blockcontrol create <world name> "+ChatColor.GREEN+": Creates a world section in the config file. " +
						"Case sensitive, no spaces.");
				sender.sendMessage(" - /blockcontrol reload "+ChatColor.GREEN+": Reloads the configuration file if you made changes.");
				return true;
			}
		}
		return false;
	}
	private void configCreate(String path, Object value) {
		if(!getConfig().contains(path)) {
			getConfig().set(path, value);
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
