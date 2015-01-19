package com.chaseoes.tf2.utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ca.wacos.nametagedit.NametagAPI;

public class NametagUtilities implements Listener
{
	public static void applyTag (Player p)
	{
		String prefix = "";
		if (p.hasPermission("tf2.rank.owner"))
        {
        	prefix = ChatColor.DARK_GRAY+"["+ChatColor.BLUE+"Owner"+ChatColor.DARK_GRAY+"] "+ChatColor.DARK_PURPLE;
        }
        else if (p.hasPermission("tf2.rank.gm"))
        {
        	prefix = ChatColor.DARK_GRAY+"["+ChatColor.DARK_AQUA+"GM"+ChatColor.DARK_GRAY+"] "+ChatColor.DARK_PURPLE;
        }
        else if (p.hasPermission("tf2.rank.admin"))
        {
        	prefix = ChatColor.DARK_GRAY+"["+ChatColor.DARK_RED+"Admin"+ChatColor.DARK_GRAY+"] "+ChatColor.RED;
        }
        else if (p.hasPermission("tf2.rank.builder"))
        {
        	prefix = ChatColor.DARK_GRAY+"["+ChatColor.GREEN+"Build"+ChatColor.DARK_GRAY+"] "+ChatColor.RED;
        }
        else if (p.hasPermission("tf2.rank.moderator"))
        {
        	prefix = ChatColor.DARK_GRAY+"["+ChatColor.YELLOW+"Mod"+ChatColor.DARK_GRAY+"] "+ChatColor.RED;
        }
        else if (p.hasPermission("tf2.rank.vip"))
        {
        	prefix = ChatColor.DARK_GRAY+"["+ChatColor.AQUA+"VIP"+ChatColor.DARK_GRAY+"] "+ChatColor.GREEN;
        }
        else
        {
        	prefix = ChatColor.GRAY.toString();
        }
		NametagAPI.setPrefix(p.getName(), prefix);
	}
	
	public static void clearTag(Player p)
	{
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "nte clear "+p.getName());
		NametagAPI.clear(p.getName());
	}
}
