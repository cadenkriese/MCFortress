package com.chaseoes.tf2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class StatsConfiguration implements Listener{
	@SuppressWarnings("unused")
	private static TF2 plugin;
	public static File playerStats = null;
	public static FileConfiguration stats = null;
	public static boolean loadedStats = false;
	public static int timesUpdated = 0;

	public static void loadConfiguration(String folder)
	{
		playerStats = new File(folder);
		stats = YamlConfiguration.loadConfiguration(playerStats);
	}

	public static void saveFiles()
	{
		//plugin.getLogger().info("[TF2] saveFiles");
		if ( !loadedStats)
		{
			//plugin.getLogger().info("[TF2] loadconfig");
			loadConfiguration("plugins/TF2/stats/stats.yml");
			loadedStats = true;
		}

		try {
			//plugin.getLogger().info("[TF2] saveStats");
			stats.save(playerStats);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void loadFiles()
	{
		//plugin.getLogger().info("[TF2] loadFiles");
		if ( !loadedStats)
		{
			//plugin.getLogger().info("[TF2] loading stats...");
			loadConfiguration("plugins/TF2/stats/stats.yml");
			loadedStats = true;
		}

		if(playerStats.exists())
		{
			//plugin.getLogger().info("[TF2] playerStats exists");
			try {
				stats.load(playerStats);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try {
				//plugin.getLogger().info("[TF2] trySave");
				stats.save(playerStats);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}

	public static void addStats(Player p, int Kills, int Deaths, int Points_Captured, int Wins, int Losses, int headShots, int backStabs)
	{		
		UUID pid = p.getUniqueId();
		int oldKills = (Integer) stats.get(pid.toString() + ".kills");
		int oldDeaths = (Integer) stats.get(pid.toString() + ".deaths");
		int oldPoints = (Integer) stats.get(pid.toString() + ".points");
		int oldWins = (Integer) stats.get(pid.toString() + ".wins");
		int oldLosses = (Integer) stats.get(pid.toString() + ".losses");
		int oldHeadshots = (Integer) stats.get(pid.toString() + ".headshots");
		int oldBackstabs = (Integer) stats.get(pid.toString() + ".backstabs");
		stats.set(pid.toString()+".kills", (oldKills+Kills));
		stats.set(pid.toString()+".deaths", (oldDeaths+Deaths));
		stats.set(pid.toString()+".points", (oldPoints+Points_Captured));
		stats.set(pid.toString()+".wins", oldWins+Wins);
		stats.set(pid.toString()+".losses", oldLosses+Losses);
		stats.set(pid.toString()+".headshots", oldHeadshots+headShots);
		stats.set(pid.toString()+".backstabs", oldBackstabs+backStabs);

		try {
			stats.save(playerStats);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static HashMap<String, Integer> getStats(String pid)
	{
		HashMap<String, Integer> statsMap = new HashMap<String, Integer>();
		statsMap.put("kills", stats.getInt(pid.toString() + ".kills"));
		statsMap.put("deaths", stats.getInt(pid.toString() + ".deaths"));
		statsMap.put("points", stats.getInt(pid.toString() + ".points"));
		statsMap.put("wins", stats.getInt(pid.toString() + ".wins"));
		statsMap.put("losses", stats.getInt(pid.toString() + ".losses"));
		return statsMap;
	}

	@EventHandler
	public void checkJoin(PlayerJoinEvent ev)
	{
		Player p = ev.getPlayer();
		UUID pid = p.getUniqueId();
		if(StatsConfiguration.stats.getString(pid.toString()) == null)
		{
			StatsConfiguration.stats.set(pid.toString() + ".kills", 0);
			StatsConfiguration.stats.set(pid.toString() + ".deaths", 0);
			StatsConfiguration.stats.set(pid.toString() + ".points", 0);
			StatsConfiguration.stats.set(pid.toString() + ".wins", 0);
			StatsConfiguration.stats.set(pid.toString() + ".losses", 0);
			StatsConfiguration.stats.set(pid.toString() + ".headshots", 0);
			StatsConfiguration.stats.set(pid.toString() + ".backstabs", 0);
			StatsConfiguration.stats.set(pid.toString() + ".pauling", false);
			StatsConfiguration.stats.set(pid.toString() + ".scout", false);
			StatsConfiguration.stats.set(pid.toString() + ".champ", false);
			StatsConfiguration.stats.set(pid.toString() + ".sniper", false);
			StatsConfiguration.stats.set(pid.toString() + ".spy", false);
			stats.set(pid.toString() + ".master", false);
			try {
				StatsConfiguration.stats.save(StatsConfiguration.playerStats);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static boolean containsUUID(UUID tid)
	{
		if(stats.contains(tid.toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public static void syncStats(final Player sender)
	{
		for (String UUID : stats.getKeys(false))
		{
			HashMap<String, Integer> pStats = getStats(UUID);
			UUID pid = getUUID(UUID);
			OfflinePlayer p = Bukkit.getOfflinePlayer(pid);
			if (p != null)
			{
				StatCollector sc = new StatCollector(p);
				syncStatsDelay(sc, pStats, sender);
			}
			else
			{
				Bukkit.getServer().getLogger().info("Could not get player");
				return;
			}
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				sender.sendMessage("");
				sender.sendMessage("");				
			}
		}, 40L);
	}

	private static UUID getUUID(String id)
	{
		return UUID.fromString(id);
	}

	public static boolean checkKills(Player p, int kills)
	{
		UUID pid = p.getUniqueId();
		int oldKills = (Integer) stats.get(pid.toString() + ".kills");
		if (!stats.getBoolean(pid.toString()+".pauling"))
		{
			if (kills+oldKills >= 100)
			{
				return true;
			}
			return false;
		}
		return false;
	}

	public static boolean checkPoints(Player p, int points)
	{
		UUID pid = p.getUniqueId();
		int oldPoints = (Integer) stats.get(pid.toString() + ".points");
		if (!stats.getBoolean(pid.toString()+".scout"))
		{
			if (points+oldPoints >= 20)
			{
				return true;
			}
			return false;
		}
		return false;
	}

	public static boolean checkWins(Player p)
	{
		UUID pid = p.getUniqueId();
		int oldWins = (Integer) stats.get(pid.toString() + ".wins");
		if (!stats.getBoolean(pid.toString()+".champ") && oldWins >= 50)
		{
			return true;
		}
		if (oldWins == 49)
		{
			return true;
		}
		return false;
	}

	public static boolean checkHeadshots(Player p, int headshots)
	{
		UUID pid = p.getUniqueId();
		if (stats.getBoolean(pid.toString()+".sniper"))
		{
			return false;
		}
		int oldHeadshots = (Integer) stats.get(pid.toString() + ".headshots");
		if (oldHeadshots + headshots >= 100)
		{
			return true;
		}
		return false;
	}

	public static boolean checkBackStabs(Player p, int stabs)
	{
		UUID pid = p.getUniqueId();
		if (stats.getBoolean(pid.toString()+".spy"))
		{
			return false;
		}
		int oldStabs = (Integer) stats.get(pid.toString() + ".backstabs");
		if (oldStabs + stabs >= 100)
		{
			return true;
		}
		return false;
	}

	public static FileConfiguration getStats()
	{
		return stats;
	}
	public static File getStatsConfig()
	{
		return playerStats;
	}

	public static void syncStatsDelay(final StatCollector sc, final HashMap<String, Integer> pStats, final Player sender)
	{
		TF2.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable()
		{
			public void run()
			{
				sc.syncStats(pStats.get("kills"), pStats.get("points"), pStats.get("wins")+pStats.get("losses"), pStats.get("wins"), pStats.get("deaths"), sender);
			}
		}, 60L);
	}

	public static int getTimesUpdated() 
	{
		return timesUpdated;
	}

	public static void setTimesUpdated(int timesUpdated) 
	{
		StatsConfiguration.timesUpdated = timesUpdated;
	}
	
	public static int getTotalEntrys()
	{
		return stats.getKeys(false).size();
	}
}
