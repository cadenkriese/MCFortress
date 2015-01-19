package com.chaseoes.tf2.commands;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResourceCommand implements CommandExecutor
{
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (label.equalsIgnoreCase("resource"))
		{
			if (sender instanceof Player)
			{
				Player p = (Player) sender;
				p.sendMessage(ChatColor.GREEN+"TF2"+ChatColor.WHITE.toString()+ChatColor.BOLD+" » "+ChatColor.DARK_AQUA+"Attempting to send you the resource pack...");
				p.setResourcePack("https://www.dropbox.com/s/4434r93l77u4q6s/TF2.zip?dl=1");
				return true;
			}
			sender.sendMessage("[TF2] You must be a player to execute this command!");
			return true;
		}
		return true;
	}
}
