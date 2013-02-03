package us.th3controller.blockcontrol;

import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;

public class BlockControlListener implements Listener {
	
	BlockControl plugin;
	
	public BlockControlListener(BlockControl plugin) {
		this.plugin = plugin;
	}
	private boolean hasPerms(String perm, Player p) {
		return p.hasPermission(perm);
	}
	private void chatmessage(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	/**
	 * metricCounter sends data to MCStats whenever it is used.
	 */
	public void metricCounter() {
		try {
			Metrics metrics = new Metrics(plugin);
			metrics.addCustomData(new Metrics.Plotter("Attempts to destroy or place a block") {
				@Override
				public int getValue() {
					return 1;
				}
			});
			metrics.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@EventHandler
	public void StopBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		List<Integer> disallowedblocks = plugin.getConfig().getIntegerList("disallowedblocks."+e.getPlayer().getWorld().getName());
		for(Integer disallowedblock : disallowedblocks) {
			if(e.getBlock().getTypeId() == disallowedblock && !hasPerms("blockcontrol.destroy", p)) {
				e.setCancelled(true);
				chatmessage(p, this.plugin.message.get("dmsg"));
				metricCounter();
			}
			else if(hasPerms("blockcontrol.denydestroy."+e.getBlock().getTypeId(), p)) {
				e.setCancelled(true);
				chatmessage(p, this.plugin.message.get("dmsg"));
				metricCounter();
			}
		}
	}
	@EventHandler
	public void StopBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		PlayerInventory inventory = p.getInventory();
		List<Integer> disallowedblocks = plugin.getConfig().getIntegerList("disallowedblocks."+e.getPlayer().getWorld().getName());
		for(Integer disallowedblock : disallowedblocks) {
			if(e.getBlock().getTypeId() == disallowedblock && !hasPerms("blockcontrol.place", p)) {
				e.setCancelled(true);
				chatmessage(p, this.plugin.message.get("pmsg"));
				metricCounter();
				if(plugin.getConfig().getBoolean("deletewhenplaced", true)) {
					inventory.remove(Material.getMaterial(disallowedblock));
				}
			}
			else if(hasPerms("blockcontrol.denyplace."+e.getBlock().getTypeId(), p)) {
				e.setCancelled(true);
				chatmessage(p, this.plugin.message.get("pmsg"));
				metricCounter();
			}
		}
	}
	@EventHandler
	public void PlayerPickUp(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		List<Integer> disallowedpickups = plugin.getConfig().getIntegerList("disallowedpickups."+e.getPlayer().getWorld().getName());
		for(Integer disallowedpickup : disallowedpickups) {
			if(e.getItem().getItemStack().getTypeId() == disallowedpickup && !hasPerms("blockcontrol.pickup", p)) {
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if(event.getPlayer().getItemInHand().getTypeId() == 327) {
			Player p = event.getPlayer();
			List<String> world = plugin.getConfig().getStringList("bucket.lavaworld");
			for(String bworld : world) {
				if(event.getBlockClicked().getWorld().getName().equals(bworld) && !hasPerms("blockcontrol.lava", p)) {
					chatmessage(p, this.plugin.message.get("pmsg"));
					event.setCancelled(true);
				}
			}
		}
		else if(event.getPlayer().getItemInHand().getTypeId() == 326) {
			Player p = event.getPlayer();
			List<String> world = plugin.getConfig().getStringList("bucket.waterworld");
			for(String bworld : world) {
				if(event.getBlockClicked().getWorld().getName().equals(bworld) && !hasPerms("blockcontrol.water", p)) {
					chatmessage(p, this.plugin.message.get("pmsg"));
					event.setCancelled(true);
				}
			}
		}
	}
}
