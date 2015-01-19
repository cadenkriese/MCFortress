package com.chaseoes.tf2.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.chaseoes.tf2.TF2;

public class leaveAlias implements CommandExecutor{
	public leaveAlias(TF2 tf2) {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("leave"))
		{
			if (sender instanceof Player)
			{
				Player p = (Player) sender;
				p.performCommand("tf2 leave");
				return true;
			}
			return false;
		}
		return false;
	}
}
