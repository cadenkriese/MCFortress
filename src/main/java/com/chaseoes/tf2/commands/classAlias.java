package com.chaseoes.tf2.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.MapUtilities;
import com.chaseoes.tf2.TF2;

public class classAlias implements CommandExecutor {
	public classAlias(TF2 tf2) {
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("class"))
		{
			if (sender instanceof Player)
			{
				Player p = (Player) sender;
				GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
				if (gp.isIngame() && !gp.isInLobby())
				{
                    gp.setInLobby(true);
                    gp.setUsingChangeClassButton(true);
					p.teleport(MapUtilities.getUtilities().loadTeamLobby(GameUtilities.getUtilities().getGamePlayer(p).getCurrentMap(), gp.getTeam()));
					return true;
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» Your not playing a game!"));
				return false;
			}
			return false;
		}
		return false;
	}
}
