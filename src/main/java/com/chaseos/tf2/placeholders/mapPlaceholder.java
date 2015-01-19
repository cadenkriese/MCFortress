package com.chaseos.tf2.placeholders;

import org.bukkit.entity.Player;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;

import net.md_5.bungee.api.ChatColor;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class mapPlaceholder implements PlaceholderReplacer{

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
		return ChatColor.GOLD+gp.getGame().getMapName();
	}
}
