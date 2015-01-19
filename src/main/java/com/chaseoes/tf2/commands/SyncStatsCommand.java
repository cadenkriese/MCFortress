package com.chaseoes.tf2.commands;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.chaseoes.tf2.StatsConfiguration;
import com.chaseoes.tf2.TF2;

public class SyncStatsCommand 
implements CommandExecutor 
{
	Permission perms = TF2.getInstance().getPerms();
	
	public SyncStatsCommand(TF2 tf2) {}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (label.equalsIgnoreCase("syncstats"))
		{
			if (sender instanceof Player)
			{
				Player p = (Player) sender;
				if (perms.getPrimaryGroup(p).equalsIgnoreCase("admin") || perms.getPrimaryGroup(p).equalsIgnoreCase("owner"))
				{
					StatsConfiguration.syncStats(p);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDatabase &f&l» &3Attempting to sync the stats in the config file with the stats in the database..."));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDatabase &f&l» &3This will only sync the Kills, Deaths, Points captured, Wins, and Games Played, for approximately &e"+StatsConfiguration.getTotalEntrys()+"&3 players."));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDatabase &f&l» &3The process will take up to of &e10 &3seconds."));
					return true;
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDatabase &f&l» &3You must have &eADMINISTRATOR &3permissions to run this command."));
				return false;
			}
			return false;
		}
		return false;
	}
}
