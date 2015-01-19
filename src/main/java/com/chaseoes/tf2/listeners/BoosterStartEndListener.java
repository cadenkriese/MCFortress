package com.chaseoes.tf2.listeners;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBarAPI;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.boosters.Booster;
import com.chaseoes.tf2.boosters.BoosterStatusFile;
import com.chaseoes.tf2.events.BoosterEndEvent;
import com.chaseoes.tf2.events.BoosterStartEvent;
import com.chaseoes.tf2.particles.ParticleEffect;
import com.chaseoes.tf2.sound.TFSound;
import com.chaseoes.tf2.utilities.FireworkUtilities;
import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class BoosterStartEndListener implements Listener 
{
	static int bossBarTime = 0;

	@EventHandler
	public void BoosterStartListener(final BoosterStartEvent e)
	{
		Booster b = e.getBooster();

		final Player p = Bukkit.getPlayerExact(b.getPlayerName());
		if (p != null)
		{
			GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
			if (!gp.isIngame())
			{
				Firework fw = (Firework) p.getWorld().spawn(p.getLocation(), Firework.class);
				FireworkMeta fm = fw.getFireworkMeta();
				FireworkEffect effect = FireworkEffect.builder()
						.flicker(true)
						.trail(false)
						.with(Type.STAR)
						.withColor(Color.ORANGE, Color.RED)
						.build();
				fm.clearEffects();
				fm.addEffect(effect);
				fm.setPower(1);
				fw.setFireworkMeta(fm);
				FireworkUtilities.detonateInstantly(fw);
			}
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable()
			{
				@Override
				public void run()
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
							Player updatedPlayer = Bukkit.getServer().getPlayerExact(e.getBooster().getPlayerName());
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
			}, 10L);
		}

		int time = b.getTime()/60/60;
		String fancytime = time+" hours";
		if (time == 1)
		{
			fancytime = time+" hour";
		}

		Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aBooster &f&l» &e"+e.getBooster().getPlayerName()+" &3has activated a &ex"+b.getBoost()+" &3booster for &e"+fancytime+"&3."));
		for (Player p2 : Bukkit.getOnlinePlayers())
		{
			TFSound.EXCITING.send(p2, 1, 1);
			ActionBarAPI.sendActionBar(p2, ChatColor.translateAlternateColorCodes('&', "&b"+e.getBooster().getPlayerName()+" &8has activated a &b"+b.getBoost()+" &8booster for &v"+fancytime+"&8."));
			time = e.getBooster().getTime()/60;
		}
		startBossBar(b);
	}

	public static void startBossBar(final Booster b)
	{
		bossBarTime = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				double time1 = BoosterStatusFile.getFile().status.getInt("time-remaining")/60;
				int time = (int) time1;
				if (time1 != time)
				{
					time++;
				}

				for (Player p : Bukkit.getOnlinePlayers())
				{
					GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
					if (!gp.isIngame())
					{
						BossBarAPI.setMessage(p, ChatColor.translateAlternateColorCodes('&', "&bx"+b.getBoost()+" &8credits from &b"+b.getPlayerName()+" &8expires in &b"+time+" &8minutes."));
					}
				}
			}
		}, 0L, 1200L);
	}

	@EventHandler
	public void BoosterEndListener(BoosterEndEvent e)
	{
		Booster b = e.getBooster();
		if (b == null)
		{
			for (Player p : Bukkit.getOnlinePlayers())
			{
				if (BossBarAPI.hasBar(p))
				{
					BossBarAPI.removeBar(p);
				}
			}
			return;
		}
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN+"Booster"+ChatColor.WHITE.toString()+ChatColor.BOLD+" » "+ChatColor.YELLOW+e.getBooster().getPlayerName()+ChatColor.DARK_AQUA+"'s "+ChatColor.YELLOW+"x"+e.getBooster().getBoost()+ChatColor.DARK_AQUA+" booster has expired.");
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN+"Booster"+ChatColor.WHITE.toString()+ChatColor.BOLD+" » "+ChatColor.DARK_AQUA+"To buy a booster visit the server store at "+ChatColor.YELLOW+"http://shop.mc-fort.net/category/620613"+ChatColor.DARK_AQUA+".");
		Bukkit.getServer().getScheduler().cancelTask(bossBarTime);
		bossBarTime = 0;
		for (Player p : Bukkit.getOnlinePlayers())
		{
			ActionBarAPI.sendActionBar(p, ChatColor.AQUA+b.getPlayerName()+ChatColor.DARK_GRAY+"'s "+ChatColor.AQUA+" x"+b.getBoost()+ChatColor.DARK_GRAY+" booster has expired.");
			GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
			if (!gp.isIngame())
			{
				if (BossBarAPI.hasBar(p))
				{
					BossBarAPI.removeBar(p);
				}
			}
		}
	}

	public void resumeBossBar(String name, int bTime, int boost)
	{
		Booster b = new Booster(name, bTime, boost);
		startBossBar(b);
	}
}
