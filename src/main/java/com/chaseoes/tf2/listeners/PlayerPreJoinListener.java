package com.chaseoes.tf2.listeners;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerPreJoinListener implements Listener 
{
	@EventHandler
	public void playerPreJoinListener(org.bukkit.event.player.PlayerLoginEvent e)
	{
		Player p = e.getPlayer();
		if (e.getResult() == Result.KICK_WHITELIST)
		{
			e.setKickMessage("KICK_WHITELIST");
			Bukkit.getServer().broadcastMessage(ChatColor.GREEN+"Login "+ChatColor.WHITE.toString()+ChatColor.BOLD+"» "+ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" has attempted to connect to the server!");
		}
		if (e.getResult() == Result.KICK_FULL)
		{
			Boolean hasKicked = false;
			for (Player p2 : Bukkit.getServer().getOnlinePlayers())
			{
				if (p.hasPermission("tf2.rank.owner"))
				{
					if (!p2.hasPermission("tf2.rank.owner"))
					{
						p2.kickPlayer(ChatColor.GOLD.toString()+ChatColor.BOLD+"\nMC-Fortress\n"+ChatColor.GRAY+"You have been kicked to make space for\na higher ranked member!");
						hasKicked = true;
					}
				}
				if (p.hasPermission("tf2.rank.gm"))
				{
					if (!p2.hasPermission("tf2.rank.gm"))
					{
						p2.kickPlayer(ChatColor.GOLD.toString()+ChatColor.BOLD+"\nMC-Fortress\n"+ChatColor.GRAY+"You have been kicked to make space for\na higher ranked member!");
						hasKicked = true;
					}
				}
				if (p.hasPermission("tf2.rank.admin"))
				{
					if (!p2.hasPermission("tf2.rank.admin"))
					{
						p2.kickPlayer(ChatColor.GOLD.toString()+ChatColor.BOLD+"\nMC-Fortress\n"+ChatColor.GRAY+"You have been kicked to make space for\na higher ranked member!");
						hasKicked = true;
					}
				}
				if (p.hasPermission("tf2.rank.moderator"))
				{
					if (!p2.hasPermission("tf2.rank.moderator"))
					{
						p2.kickPlayer(ChatColor.GOLD.toString()+ChatColor.BOLD+"\nMC-Fortress\n"+ChatColor.GRAY+"You have been kicked to make space for\na higher ranked member!");
						hasKicked = true;
					}
				}
				if (p.hasPermission("tf2.rank.builder"))
				{
					if (!p2.hasPermission("tf2.rank.builder"))
					{
						p2.kickPlayer(ChatColor.GOLD.toString()+ChatColor.BOLD+"\nMC-Fortress\n"+ChatColor.GRAY+"You have been kicked to make space for\na higher ranked member!");
						hasKicked = true;
					}
				}
				if (!p2.hasPermission("tf2.rank.vip"))
				{
					if (!hasKicked)
					{
						p2.kickPlayer(ChatColor.GOLD.toString()+ChatColor.BOLD+"\nMC-Fortress\n"+ChatColor.GRAY+"You have been kicked to make space for\na higher ranked member!");
						hasKicked = true;
					}
				}
			}
			if (hasKicked)
			{
				e.allow();
				e.setResult(Result.ALLOWED);
				return;
			}
			else
			{
				e.setKickMessage("KICK_NOROOM");
			}
		}
		else
		{
			if (e.getResult() == Result.KICK_FULL)
			{
				e.setKickMessage("KICK_FULL");
			}
		}
	}
}
