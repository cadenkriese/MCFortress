package com.chaseoes.tf2.utilities;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import com.chaseoes.tf2.MapUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.Team;

public class FireworkUtilities {
	static Integer red = 0;
	static Integer blu = 0;
	static Integer timesDone = 0;
	public static void detonateInstantly(final Firework fw)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable()
		{
			@Override
			public void run() {
				fw.detonate();
			}
		}, 2L);	
	}
	public static void winFireworks(Team t)
	{
		timesDone = 0;
		if (t == Team.RED)
		{
			final ArrayList<Location> locList = new ArrayList<Location>();
			locList.add(new Location(MapUtilities.getUtilities().loadLobby().getWorld(), -26, 18, -216));
			locList.add(new Location(MapUtilities.getUtilities().loadLobby().getWorld(), -32, 21, -210));
			locList.add(new Location(MapUtilities.getUtilities().loadLobby().getWorld(), -31, 21, -202));
			locList.add(new Location(MapUtilities.getUtilities().loadLobby().getWorld(), -28, 17, -199));
			if (red != 0)
			{
				return;
			}
			red = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable() 
			{
				public void run()
				{
					Random random = new Random();
					Location loc = locList.get(random.nextInt(3));
					Firework fw = (Firework) loc.getWorld().spawn(loc, Firework.class);
					FireworkMeta fm = fw.getFireworkMeta();
					FireworkEffect effect = FireworkEffect.builder()
							.flicker(true)
							.trail(false)
							.with(Type.BALL_LARGE)
							.withColor(Color.RED)
							.build();
					fm.clearEffects();
					fm.addEffect(effect);
					fm.setPower(2);
					fw.setFireworkMeta(fm);
					timesDone++;
					if (timesDone >= 15)
					{
						Bukkit.getServer().getScheduler().cancelTask(red);
						red = 0;
						timesDone = 0;
					}
				}
			}, 60L, 20L);
		}
		else
		{
			final ArrayList<Location> locList = new ArrayList<Location>();
			locList.add(new Location(MapUtilities.getUtilities().loadLobby().getWorld(), 34, 12, -201));
			locList.add(new Location(MapUtilities.getUtilities().loadLobby().getWorld(), 37, 18, -206));
			locList.add(new Location(MapUtilities.getUtilities().loadLobby().getWorld(), 37, 14, -212));
			locList.add(new Location(MapUtilities.getUtilities().loadLobby().getWorld(), 29, 16, -220));
			if (blu != 0)
			{
				return;
			}
			blu = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable() 
			{
				public void run()
				{
					Random random = new Random();
					Location loc = locList.get(random.nextInt(3));
					Firework fw = (Firework) loc.getWorld().spawn(loc, Firework.class);
					FireworkMeta fm = fw.getFireworkMeta();
					FireworkEffect effect = FireworkEffect.builder()
							.flicker(true)
							.trail(false)
							.with(Type.BALL_LARGE)
							.withColor(Color.BLUE)
							.build();
					fm.clearEffects();
					fm.addEffect(effect);
					fm.setPower(2);
					fw.setFireworkMeta(fm);
					timesDone++;
					if (timesDone >= 15)
					{
						Bukkit.getServer().getScheduler().cancelTask(blu);
						blu = 0;
						timesDone = 0;
					}
				}
			}, 60L, 20L);
		}
	}
}
