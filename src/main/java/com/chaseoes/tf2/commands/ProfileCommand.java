package com.chaseoes.tf2.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.chaseoes.tf2.guis.profileGUI;

public class ProfileCommand
implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (label.equalsIgnoreCase("profile"))
		{
			if (sender instanceof Player)
			{
				Player p = (Player) sender;
				profileGUI.getGUI().openGUI(p);
			}
			sender.sendMessage("You gotta be a player tho!");
		}
		return false;
	}

}
