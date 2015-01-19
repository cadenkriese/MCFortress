package com.chaseoes.tf2.listeners;

import java.util.ArrayList;
import java.util.HashMap;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ca.wacos.nametagedit.NametagAPI;

import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.Map;
import com.chaseoes.tf2.MapUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.utilities.SQLUtilities;
import com.chaseoes.tf2.utilities.SerializableInventory;
import com.chaseoes.tf2.utilities.WorldEditUtilities;

public class PlayerJoinListener implements Listener 
{
	static HashMap<Player, String> invMap = new HashMap<Player, String>();


	@EventHandler
	public void onLogin(final PlayerJoinEvent event) 
	{
		final Player player = event.getPlayer();
		SQLUtilities.getUtilities().playerJoin(player, !player.hasPlayedBefore());

		if (player.hasPermission("tf2.rank.vip"))
		{
			event.setJoinMessage(ChatColor.GREEN+"Login"+ChatColor.WHITE.toString()+ChatColor.BOLD+" » "+ChatColor.YELLOW+player.getName()+ChatColor.DARK_AQUA+" has joined!");
		}
		else
		{
			event.setJoinMessage(null);
			for (Player other : Bukkit.getServer().getOnlinePlayers())
			{
				if (other.hasPermission("tf2.rank.moderator"))
				{
					other.sendMessage(ChatColor.GREEN+"Login"+ChatColor.WHITE.toString()+ChatColor.BOLD+" » "+ChatColor.YELLOW+player.getName()+ChatColor.DARK_AQUA+" has joined!");
				}
			}
		}
		invMap.put(player, SerializableInventory.inventoryToString(player.getInventory()));
		Location loc = new Location(Bukkit.getWorld("lobby"), -8, 86.5, -418, 26, 34);
		player.teleport(loc);

		player.setMaxHealth(20d);
		player.setHealth(player.getMaxHealth());
		for (Map m : TF2.getInstance().getMaps()) 
		{
			if (m.getP1() != null && m.getP2() != null && WorldEditUtilities.getWEUtilities().isInMap(player.getLocation(), m)) 
			{
				if (!player.hasPermission("tf2.create")) 
				{
					player.teleport(MapUtilities.getUtilities().loadLobby());
				}
			}
		}

		GameUtilities.getUtilities().playerJoinServer(player);
		if (TF2.getInstance().getConfig().getBoolean("dedicated-join")) {
			player.performCommand("tf2 join " + MapUtilities.getUtilities().getRandomMap().getName());
		}
		delayedLogic(player);
	}

	public void delayedLogic(final Player p)
	{
		TF2.getInstance().getServer().getScheduler().runTaskLater(TF2.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				p.getInventory().clear();
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "nte prefix "+p.getName()+" "+ChatColor.DARK_GRAY.toString());
			}
		}, 10L);
	}

	public static void giveInventory(Player p)
	{
		Permission perms = TF2.getInstance().getPerms();
		String prefix = "";
		if (perms.playerInGroup(p, "Owner"))
		{
			prefix = ChatColor.DARK_GRAY+"["+ChatColor.BLUE+"Owner"+ChatColor.DARK_GRAY+"] "+ChatColor.DARK_PURPLE;
		}
		else if (perms.playerInGroup(p, "Admin"))
		{
			prefix = ChatColor.DARK_GRAY+"["+ChatColor.DARK_RED+"Admin"+ChatColor.DARK_GRAY+"] "+ChatColor.RED;
		}
		else if (perms.playerInGroup(p, "Architect"))
		{
			prefix = ChatColor.DARK_GRAY+"["+ChatColor.GREEN+"Build"+ChatColor.DARK_GRAY+"] "+ChatColor.RED;
		}
		else if (perms.playerInGroup(p, "Moderator"))
		{
			prefix = ChatColor.DARK_GRAY+"["+ChatColor.YELLOW+"Mod"+ChatColor.DARK_GRAY+"] "+ChatColor.RED;
		}
		else if (perms.playerInGroup(p, "VIP"))
		{
			prefix = ChatColor.DARK_GRAY+"["+ChatColor.AQUA+"VIP"+ChatColor.DARK_GRAY+"] "+ChatColor.GREEN;
		}
		else
		{
			prefix = ChatColor.GRAY.toString();
		}
		NametagAPI.setPrefix(p.getName(), prefix);

		if (invMap.containsKey(p))
		{
			Inventory inv = SerializableInventory.stringToInventory(invMap.get(p));
			if (inv == null || inv.getContents() == null)
			{
				return;
			}
			for (int slot = 0; slot<inv.getSize(); slot++)
			{
				if (inv.getItem(slot) == null || inv.getItem(slot).getType() == Material.AIR)
				{
					continue;
				}
				p.getInventory().setItem(slot, inv.getItem(slot));
			}

			ItemStack profileItem = new ItemStack (Material.SKULL_ITEM,1, (byte)3);
			SkullMeta profileMeta = (SkullMeta) profileItem.getItemMeta();
			profileMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aProfile &F- &6Right Click"));
			ArrayList<String> profileLore = new ArrayList<String>();
			profileLore.add(ChatColor.GRAY + "Right click to see your player Profile.");
			profileMeta.setLore(profileLore);
			profileMeta.setOwner(p.getName());
			profileItem.setItemMeta(profileMeta);
			p.getInventory().setItem(0, profileItem);

			invMap.remove(p);
			p.updateInventory();
		}
	}
}
