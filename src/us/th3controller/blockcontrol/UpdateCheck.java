package us.th3controller.blockcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateCheck implements Runnable {
	
	private final BlockControl plugin;
	
	public UpdateCheck(BlockControl plugin) {
		this.plugin = plugin;
	}
	
	public void run(){
		try {
			URL url = new URL("http://dl.dropbox.com/u/34716611/Public%20Files/blockcontrol.properties");
			URLConnection connection = url.openConnection();
			
			connection.setDoInput(true);
			InputStream inStream = connection.getInputStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(inStream));
			
			String line = input.readLine();
			if (line != null) {
				double currentVer = plugin.parseVersion(plugin.pdfile.getVersion());
				double newVer = plugin.parseVersion(line);
				if (newVer > currentVer) {
					plugin.log.info("[BlockControl] Latest version is available now on BukkitDev!");
					plugin.log.info("[BlockControl] Latest Version: "+line);
					plugin.log.info("[BlockControl] http://goo.gl/pc6D9");
				}
				else if(newVer == currentVer) {
					plugin.log.info("[BlockControl] Up-to-date!");
				}
			}
			
		} catch (IOException e) {
			// Failed to check for updates :-(
			plugin.log.warning("[BlockControl] Could not check for latest updates!");
		}
	}
}
