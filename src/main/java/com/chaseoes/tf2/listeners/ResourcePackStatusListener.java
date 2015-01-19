package com.chaseoes.tf2.listeners;

import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.MapUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.boosters.BoosterStatusFile;
import com.chaseoes.tf2.particles.ParticleEffect;

import de.inventivegames.rpapi.ResourcePackStatusEvent;
import de.inventivegames.rpapi.Status;

public class ResourcePackStatusListener implements Listener
{
	HashMap<Player, Integer> sendPackMap = new HashMap<Player, Integer>();
	@EventHandler
	public void onResourcePackStatus(final ResourcePackStatusEvent e)
	{
		Player p = e.getPlayer();
		if (e.getStatus() == Status.SUCCESSFULLY_LOADED)
		{
			if (sendPackMap.containsKey(p))
			{
				Bukkit.getServer().getScheduler().cancelTask(sendPackMap.get(p));
				sendPackMap.remove(p);
			}
			p.sendMessage(ChatColor.GREEN+"TF2"+ChatColor.WHITE.toString()+ChatColor.BOLD+" » "+ChatColor.DARK_AQUA+"Thank you for downloading the MC-Fortress Resource Pack, enjoy the game!");
			p.teleport(MapUtilities.getUtilities().loadLobby());
			PlayerJoinListener.giveInventory(p);
			
			if (BoosterStatusFile.getFile().getCurrentBooster() != null && BoosterStatusFile.getFile().getCurrentBooster().getPlayerName().equalsIgnoreCase(p.getName()))
			{
				new BukkitRunnable() 
				{
					int maxRadius = 5;
					double radius = .5;
					int lines = 6;
					double anglePerLine = Math.PI * 2 / lines;
					double maxHeight = 2;

					@Override
					public void run()
					{
						Player updatedPlayer = e.getPlayer();
						GamePlayer gp2 = GameUtilities.getUtilities().getGamePlayer(updatedPlayer);
						if (updatedPlayer == null || (BoosterStatusFile.getFile().getCurrentBooster() != null && !BoosterStatusFile.getFile().getCurrentBooster().getPlayerName().equals(updatedPlayer.getName())))
						{
							cancel();
							return;
						}

						if (!gp2.isIngame() && !updatedPlayer.isSneaking() && !updatedPlayer.isSprinting())
						{
							for (int i = 0; i < lines; i++) 
							{
								double startAngle = anglePerLine * i;
								double x = Math.cos(startAngle + Math.toRadians(radius * 12)) * Math.sin(Math.toRadians(radius * 20));
								double z = Math.sin(startAngle + Math.toRadians(radius * 12)) * Math.sin(Math.toRadians(radius * 20));
								double y = maxHeight / maxRadius * radius;
								ParticleEffect.FLAME.display(0F, 0F, 0F, 0F, 1, updatedPlayer.getLocation().add(x, y, z), 255D);
							}
							radius += .5;
							if (radius > 9)
								radius = .5;
						}
					}
				}.runTaskTimer(TF2.getInstance(), 0L, 1L);
			}
			
			return;
		}
		if (e.getStatus() == Status.FAILED_DOWNLOAD)
		{
			p.sendMessage(ChatColor.RED+"TF2"+ChatColor.GRAY.toString()+ChatColor.BOLD+" » "+ChatColor.LIGHT_PURPLE+"There was an error while downloading the resource pack.");
			if (sendPackMap.containsKey(p))
			{
				return;
			}
			if (!p.hasPermission("tf2.rank.moderator"))
			{
				failedResourcepack(p);
			}
			return;
		}
		if (e.getStatus() == Status.DECLINED)
		{
			if (sendPackMap.containsKey(p))
			{
				return;
			}
			failedResourcepack(p);
			return;
		}
	}

	public void failedResourcepack(final Player p)
	{
		sendPackMap.put(p, TF2.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				if (p == null)
				{
					sendPackMap.remove(p);
					TF2.getInstance().getServer().getScheduler().cancelTask(sendPackMap.get(p));
				}
				p.sendMessage(ChatColor.RED+"Warning you do not have the MC-Fortress Resource Pack. You will not be able to access the server until you have it installed.");
				p.sendMessage("");
				p.sendMessage(ChatColor.GOLD+"To fix this do /resource or try re logging.");
				p.sendMessage("");
				p.sendMessage(ChatColor.DARK_RED+"Still not sure what to do? Visit this page for more information"+ChatColor.YELLOW+"http://mcfort.wikia.com/wiki/Getting_the_Resource_Pack");
				p.sendMessage("");
				p.sendMessage("");
			}
		}, 0L, 300L));
	}
}
