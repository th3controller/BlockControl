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
	@EventHandler
	public void StopBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		List<Integer> disallowedblocks = plugin.getConfig().getIntegerList("disallowedblocks."+e.getPlayer().getWorld().getName());
		for(Integer disallowedblock : disallowedblocks) {
			if(e.getBlock().getTypeId() == disallowedblock && !hasPerms("blockcontrol.destroy", p)) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.message.get("dmsg")));
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
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.message.get("pmsg")));
				if(plugin.getConfig().getBoolean("deletewhenplaced", true)) {
					inventory.remove(Material.getMaterial(disallowedblock));
				}
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
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.message.get("pmsg")));
					event.setCancelled(true);
				}
			}
		}
		else if(event.getPlayer().getItemInHand().getTypeId() == 326) {
			Player p = event.getPlayer();
			List<String> world = plugin.getConfig().getStringList("bucket.waterworld");
			for(String bworld : world) {
				if(event.getBlockClicked().getWorld().getName().equals(bworld) && !hasPerms("blockcontrol.water", p)) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.message.get("pmsg")));
					event.setCancelled(true);
				}
			}
		}
	}
}
