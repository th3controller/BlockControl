package us.th3controller.blockcontrol;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnderEggListener implements Listener {
	
	BlockControl plugin;
	
	public EnderEggListener(BlockControl plugin) {
		this.plugin = plugin;
	}
	private boolean hasPerms(String perm, Player p) {
		return p.hasPermission(perm);
	}
	@EventHandler
	public void PlayerInteractUseDragon(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action act = e.getAction();
		Block block = e.getClickedBlock();
		if((act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK || act == Action.LEFT_CLICK_AIR || act == Action.LEFT_CLICK_BLOCK) && (block != null)) {
			if(block.getTypeId() == 122 && !hasPerms("blockcontrol.dragon", p)) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED+"You have to use pistons to obtain the dragon egg!");
			}
		}
	}
}
