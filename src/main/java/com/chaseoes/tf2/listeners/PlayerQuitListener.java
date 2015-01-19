package com.chaseoes.tf2.listeners;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.chaseoes.tf2.Game;
import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.commands.SpectateCommand;

public class PlayerQuitListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
    	Player p = event.getPlayer();
        GamePlayer gPlayer = GameUtilities.getUtilities().getGamePlayer(event.getPlayer());
        if (gPlayer.isIngame()) {
            Game game = gPlayer.getGame();
            if (game != null) {
                game.leaveGame(gPlayer.getPlayer());
            }
        }
        SpectateCommand.getCommand().stopSpectating(event.getPlayer());
        SpectateCommand.getCommand().playerLogout(event.getPlayer());
        GameUtilities.getUtilities().playerLeaveServer(event.getPlayer());
        if (p.hasPermission("tf2.rank.vip"))
        {
        	event.setQuitMessage(ChatColor.GREEN+"Login"+ChatColor.WHITE.toString()+ChatColor.BOLD+" » "+ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" has quit.");
        }
        else
        {
        	event.setQuitMessage(null);
        	for (Player other : Bukkit.getServer().getOnlinePlayers())
        	{
        		if (other.hasPermission("tf2.rank.moderator"))
        		{
        			other.sendMessage(ChatColor.GREEN+"Login"+ChatColor.WHITE.toString()+ChatColor.BOLD+" » "+ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" has quit.");
        		}
        	}
        }
    }

}
