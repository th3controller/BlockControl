package us.th3controller.blockcontrol;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;

public class BlockControlListener implements Listener {
	
	BlockControl plugin;
	
	public BlockControlListener(BlockControl plugin) {
		this.plugin = plugin;
	}
	/**
	 * Checks for permissions
	 * @param perm The permission node to be used
	 * @param p The player being checked
	 * @return If has permission returns true, else false
	 */
	private boolean hasPerms(String perm, Player p) {
		return p.hasPermission(perm);
	}
	/**
	 * Translated colored chat
	 * @param p Player to be involved in the chat
	 * @param msg Message to be translated to display colors
	 */
	private void chatmessage(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	@EventHandler
	public void StopBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		List<Integer> disallowedblocks = plugin.getConfig().getIntegerList("disallowedblocks."+p.getWorld().getName());
		for(Integer disallowedblock : disallowedblocks) {
			if(e.getBlock().getTypeId() == disallowedblock && !hasPerms("blockcontrol.destroy", p)) {
				e.setCancelled(true);
				chatmessage(p, this.plugin.message.get("dmsg"));
			}
			else if(hasPerms("blockcontrol.denydestroy."+e.getBlock().getTypeId(), p) && !p.isOp()) {
				e.setCancelled(true);
				chatmessage(p, this.plugin.message.get("dmsg"));
			}
		}
		List<String> disallowedworlds = plugin.getConfig().getStringList("disallowedworlds");
		for(String disallowedworld : disallowedworlds) {
			if(e.getBlock().getWorld().getName().contains(disallowedworld) && !hasPerms("blockcontrol.world."+p.getWorld().getName(), p)) {
				e.setCancelled(true);
				chatmessage(p, this.plugin.message.get("dmsg"));
			}
		}
	}
	@EventHandler
	public void StopBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		PlayerInventory inventory = p.getInventory();
		List<Integer> disallowedblocks = plugin.getConfig().getIntegerList("disallowedblocks."+p.getWorld().getName());
		for(Integer disallowedblock : disallowedblocks) {
			if(e.getBlock().getTypeId() == disallowedblock && !hasPerms("blockcontrol.place", p)) {
				e.setCancelled(true);
				chatmessage(p, this.plugin.message.get("pmsg"));
				if(plugin.getConfig().getBoolean("deletewhenplaced", true)) {
					inventory.remove(Material.getMaterial(disallowedblock));
				}
			}
			else if(hasPerms("blockcontrol.denyplace."+e.getBlock().getTypeId(), p) && !p.isOp()) {
				e.setCancelled(true);
				chatmessage(p, this.plugin.message.get("pmsg"));
			}
		}
		List<String> disallowedworlds = plugin.getConfig().getStringList("disallowedworlds");
		for(String disallowedworld : disallowedworlds) {
			if(e.getBlock().getWorld().getName().contains(disallowedworld) && !hasPerms("blockcontrol.world."+p.getWorld().getName(), p)) {
				e.setCancelled(true);
				chatmessage(p, this.plugin.message.get("pmsg"));
			}
		}
	}
	@EventHandler
	public void PlayerPickUp(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		List<Integer> disallowedpickups = plugin.getConfig().getIntegerList("disallowedpickups."+p.getWorld().getName());
		for(Integer disallowedpickup : disallowedpickups) {
			if(e.getItem().getItemStack().getTypeId() == disallowedpickup && !hasPerms("blockcontrol.pickup", p)) {
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void PlayerDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		List<Integer> disalloweddrops = plugin.getConfig().getIntegerList("disalloweddrops."+p.getWorld().getName());
		for(Integer disalloweddrop : disalloweddrops) {
			if(event.getItemDrop().equals(disalloweddrop)) {
				if(plugin.bool.get("dropdelete").contains("true")) {
					event.getItemDrop().remove();
				} else {
					event.setCancelled(true);
				}
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
