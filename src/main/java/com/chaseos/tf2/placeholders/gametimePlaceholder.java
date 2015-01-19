package com.chaseos.tf2.placeholders;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameStatus;
import com.chaseoes.tf2.GameUtilities;

import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class gametimePlaceholder implements PlaceholderReplacer{

	@Override
	public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
		Player p = e.getPlayer();
		if (p == null)
		{
			return ChatColor.RED+"NULL";
		}
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		if (!gp.isIngame())
		{
			return ChatColor.RED+"Not Ingame!";
		}
		if (gp.getGame().getStatus() != GameStatus.INGAME)
		{
			return ChatColor.RED+"Game not started!";
		}
		return gp.getGame().getTimeLeftPretty();
	}

}
