package com.chaseos.tf2.placeholders;

import org.bukkit.ChatColor;

import com.chaseoes.tf2.Game;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.Map;
import com.chaseoes.tf2.TF2;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

public class players implements PlaceholderReplacer{
	
	public players() {
	
	}

	@Override
	public String update() 
	{
		Map map = new Map(TF2.getInstance(), TF2.getInstance().getADmap());
		Game game = GameUtilities.getUtilities().getGame(map);
		int players = game.playersInGame.size();
		if (players == 0)
		{
			String prettyPlayers = new String(ChatColor.RED.toString()+"None");
			return prettyPlayers;
		}
		else if (players < 5)
		{
			String prettyPlayers = new String(ChatColor.RED.toString()+players+ChatColor.YELLOW+" / "+ChatColor.GREEN+"24");
			return prettyPlayers;
		}
		else if (players < 11)
		{
			String prettyPlayers = new String(ChatColor.GOLD.toString()+players+ChatColor.YELLOW+" / "+ChatColor.GREEN+"24");
			return prettyPlayers;
		}
		else if (players < 25)
		{
			String prettyPlayers = new String(ChatColor.GREEN.toString()+players+ChatColor.YELLOW+" / "+ChatColor.GREEN+"24");
			return prettyPlayers;
		}
		return null;
	}
}
