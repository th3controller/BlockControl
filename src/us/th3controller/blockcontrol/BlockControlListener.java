package us.th3controller.blockcontrol;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

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
		return p.hasPermission("blockcontrol."+perm);
	}
	/**
	 * Translated colored chat
	 * @param p Player to be involved in the chat
	 * @param msg Message to be translated to display colors
	 */
	private void chatmessage(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void BlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block getblock = event.getBlock();
		FileConfiguration config = plugin.getConfig();
		List<Integer> blocklist = config.getIntegerList("worlds."+player.getWorld().getName()+".block-destroy");
		for(Integer block : blocklist) {
			String world = player.getWorld().getName();
			if(getblock.getTypeId() == block && !hasPerms("destroy", player)) {
				event.setCancelled(true);
				chatmessage(player, config.getString("worlds."+world+".destroy-message"));
			}
			else if(hasPerms("denydestroy."+getblock.getTypeId(), player) && !player.isOp()) {
				event.setCancelled(true);
				chatmessage(player, config.getString("worlds."+world+".destroy-message"));
			}
		}
	}
	@EventHandler
	public void BlockBreakWorld(BlockBreakEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = plugin.getConfig();
		if(config.getBoolean("worlds."+player.getWorld().getName()+".no-build") && !hasPerms("world."+player.getWorld().getName(), player)) {
			event.setCancelled(true);
			chatmessage(player, config.getString("worlds."+player.getWorld().getName()+".destroy-message"));
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void BlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block getblock = event.getBlock();
		FileConfiguration config = plugin.getConfig();
		List<Integer> blocklist = config.getIntegerList("worlds."+player.getWorld().getName()+".block-place");
		for(Integer block : blocklist) {
			String world = event.getPlayer().getWorld().getName();
			if(getblock.getTypeId() == block && !hasPerms("place", player)) {
				event.setCancelled(true);
				chatmessage(player, config.getString("worlds."+world+".place-message"));
				if(config.getBoolean("worlds."+world+".delete-disabled-place")) {
					player.getInventory().remove(Material.getMaterial(block));
				}
			}
			else if(hasPerms("denyplace."+getblock.getTypeId(), player) && !player.isOp()) {
				event.setCancelled(true);
				chatmessage(player, config.getString("worlds."+world+".place-message"));
				if(config.getBoolean("worlds."+world+".delete-disabled-place")) {
					player.getInventory().remove(player.getItemInHand());
				}
			}
		}
	}
	@EventHandler
	public void BlockPlaceWorld(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = plugin.getConfig();
		if(config.getBoolean("worlds."+player.getWorld().getName()+".no-build") && !hasPerms("world."+player.getWorld().getName(), player)) {
			event.setCancelled(true);
			chatmessage(player, config.getString("worlds."+player.getWorld().getName()+".place-message"));
			if(config.getBoolean("worlds."+player.getWorld().getName()+".delete-disabled-place")) {
				player.getInventory().remove(player.getItemInHand());
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void LiquidPhysics(BlockFromToEvent event) {
		if(event.getBlock().isLiquid()) {
			String world = event.getBlock().getWorld().getName();
			Material block = event.getBlock().getType();
			if(block.equals(Material.STATIONARY_WATER) || block.equals(Material.WATER)) {
				if(plugin.getConfig().getBoolean("worlds."+world+".disable-water-physics")) {
					event.setCancelled(true);
				}
			}
			else if(block.equals(Material.STATIONARY_LAVA) || block.equals(Material.LAVA)) {
				if(plugin.getConfig().getBoolean("worlds."+world+".disable-lava-physics")) {
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void EnderEgg(BlockFromToEvent e) {
		Block block = e.getBlock();
		FileConfiguration config = plugin.getConfig();
		if(block.getTypeId() == 122) {
			if(config.getBoolean("worlds."+e.getBlock().getWorld().getName()+".enderegg-teleport-disable")) {
				e.setCancelled(true);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void BucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = plugin.getConfig();
		if(player.getItemInHand().getTypeId() == 327) {
			if(config.getBoolean("worlds."+player.getWorld().getName()+".lava-bucket-place")) {
				if(!hasPerms("lava.place", event.getPlayer())) {
					event.setCancelled(true);
					chatmessage(player, config.getString("worlds."+player.getWorld().getName()+".place-message"));
				}
			}
		}
		else if(player.getItemInHand().getTypeId() == 326) {
			if(config.getBoolean("worlds."+player.getWorld().getName()+".water-bucket-place")) {
				if(!hasPerms("water.place", event.getPlayer())) {
					event.setCancelled(true);
					chatmessage(player, config.getString("worlds."+player.getWorld().getName()+".place-message"));
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void BucketFill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = plugin.getConfig();
		if(player.getItemInHand().getTypeId() == 327) {
			if(config.getBoolean("worlds."+player.getWorld().getName()+".lava-bucket-fill")) {
				if(!hasPerms("lava.fill", event.getPlayer())) {
					event.setCancelled(true);
					chatmessage(player, config.getString("worlds."+player.getWorld().getName()+".destroy-message"));
				}
			}
		}
		else if(player.getItemInHand().getTypeId() == 326) {
			if(config.getBoolean("worlds."+player.getWorld().getName()+".water-bucket-fill")) {
				if(!hasPerms("water.fill", event.getPlayer())) {
					event.setCancelled(true);
					chatmessage(player, config.getString("worlds."+player.getWorld().getName()+".destroy-message"));
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void PlayerPickUp(PlayerPickupItemEvent e) {
		Player player = e.getPlayer();
		String world = player.getWorld().getName();
		FileConfiguration config = plugin.getConfig();
		List<Integer> pickups = config.getIntegerList("worlds."+world+".pickup");
		for(Integer pickup : pickups) {
			if(e.getItem().getItemStack().getTypeId() == pickup && !hasPerms("pickup", player)) {
				e.setCancelled(true);
				e.getItem().setPickupDelay(20);
				if(config.getBoolean("worlds."+world+".delete-disabled-pickup")) {
					e.getItem().remove();
				}
				if(config.getString("worlds."+world+".disabled-pickup-message") == null) {
					config.set("worlds."+world+".disabled-pickup-message", "&cYou cannot pickup this item.");
					plugin.saveConfig();
					plugin.reloadConfig();
					player.sendMessage(ChatColor.RED+"You cannot pickup this item.");
				} else {
					chatmessage(player, config.getString("worlds."+world+".disabled-pickup-message"));
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void PlayerDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		String world = player.getWorld().getName();
		FileConfiguration config = plugin.getConfig();
		List<Integer> drops = config.getIntegerList("worlds."+world+".item-drop");
		for(Integer drop : drops) {
			if(event.getItemDrop().getItemStack().getTypeId() == drop && !hasPerms("drop", player)) {
				if(config.getBoolean("worlds."+world+".delete-disabled-drop")) {
					event.getItemDrop().remove();
				} else {
					event.setCancelled(true);
				}
				if(config.getString("worlds."+world+".disabled-drop-message") == null) {
					config.set("worlds."+world+".disabled-drop-message", "&cYou cannot drop this item.");
					plugin.saveConfig();
					plugin.reloadConfig();
					player.sendMessage(ChatColor.RED+"You cannot drop this item.");
				} else {
					chatmessage(player, config.getString("worlds."+world+".disabled-drop-message"));
				}
			}
		}
	}
}
