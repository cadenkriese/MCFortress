package com.chaseoes.tf2;

import java.sql.ResultSet;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.chaseoes.tf2.utilities.SQLUtilities;

public class StatCollector {

	String player;
	boolean loaded;

	int kills = 0;
	int highest_killstreak = 0;
	int points_captured = 0;
	int games_played = 0;
	int red_team_count = 0;
	int blue_team_count = 0;
	int time_ingame = 0;
	int games_won = 0;
	int deaths = 0;

	public StatCollector(OfflinePlayer p) 
	{
		player = p.getName();
		if (!TF2.getInstance().isDisabling) {
			load();
		}
	}

	public void load() 
	{
		TF2.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(TF2.getInstance(), new Runnable() 
		{
			@Override
			public void run() 
			{
				ResultSet rs = SQLUtilities.getUtilities().getResultSet("SELECT * FROM player_stats WHERE username='" + player + "'");
				loaded = false;
				if (rs == null) 
				{
					SQLUtilities.getUtilities().execUpdate("INSERT INTO player_stats(username, kills, highest_killstreak, points_captured, games_played, red_team_count, blue_team_count, time_ingame, games_won, deaths) VALUES ('" + player + "', '0', '0', '0', '0', '0', '0', '0', '0', '0')");
					loaded = true;
					return;
				}
				try {
					while (rs.next()) {
						loaded = true;
						kills = Integer.parseInt(rs.getString("kills"));
						highest_killstreak = Integer.parseInt(rs.getString("highest_killstreak"));
						points_captured = Integer.parseInt(rs.getString("points_captured"));
						games_played = Integer.parseInt(rs.getString("games_played"));
						red_team_count = Integer.parseInt(rs.getString("red_team_count"));
						blue_team_count = Integer.parseInt(rs.getString("blue_team_count"));
						time_ingame = Integer.parseInt(rs.getString("time_ingame"));
						games_won = Integer.parseInt(rs.getString("games_won"));
						deaths = Integer.parseInt(rs.getString("deaths"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// this will not occur
				//if (!loaded) {
				//	SQLUtilities.getUtilities().execUpdate("INSERT INTO player_stats(username, kills, highest_killstreak, points_captured, games_played, red_team_count, blue_team_count, time_ingame, games_won, deaths) VALUES ('" + player + "', '0', '0', '0', '0', '0', '0', '0', '0', '0')");
				//}
			}
		}, 20L);

	}

	public void addStatsFromGame(int k, int h_k, int pc, Team team, int time, Team winningTeam, int death) {
		kills = kills + k;

		if (h_k > highest_killstreak) {
			highest_killstreak = h_k;
		}

		points_captured = points_captured + pc;
		games_played++;
		if (team == Team.RED) {
			red_team_count++;
		} else {
			blue_team_count++;
		}

		if (team == winningTeam) {
			games_won++;
		}

		time_ingame = time_ingame + time;
		deaths = deaths + death;
	}

	public void submit()
	{
		TF2.getInstance().getServer().getScheduler().runTaskAsynchronously(TF2.getInstance(), new Runnable() 
		{
			@Override
			public void run() 
			{
				SQLUtilities.getUtilities().execUpdate("UPDATE player_stats SET kills = "+kills+" WHERE username = '"+player+"'");
				SQLUtilities.getUtilities().execUpdate("UPDATE player_stats SET highest_killstreak = "+highest_killstreak+" WHERE username = '"+player+"'");
				SQLUtilities.getUtilities().execUpdate("UPDATE player_stats SET points_captured = "+points_captured+" WHERE username = '"+player+"'");
				SQLUtilities.getUtilities().execUpdate("UPDATE player_stats SET games_played = "+games_played+" WHERE username = '"+player+"'");
				SQLUtilities.getUtilities().execUpdate("UPDATE player_stats SET red_team_count = "+red_team_count+" WHERE username = '"+player+"'");
				SQLUtilities.getUtilities().execUpdate("UPDATE player_stats SET blue_team_count = "+blue_team_count+" WHERE username = '"+player+"'");
				SQLUtilities.getUtilities().execUpdate("UPDATE player_stats SET time_ingame = "+time_ingame+" WHERE username = '"+player+"'");
				SQLUtilities.getUtilities().execUpdate("UPDATE player_stats SET games_won = "+games_won+" WHERE username = '"+player+"'");
				SQLUtilities.getUtilities().execUpdate("UPDATE player_stats SET deaths = "+deaths+" WHERE username = '"+player+"'");
				Player p = Bukkit.getPlayerExact(player);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDatabase &f&l» &3Your stats were sucsesfully updated."));
			}
		});
	}

	public void syncStats(final int kills, final int points_captured, final int games_played, final int games_won, final int deaths, final Player sender)
	{
		TF2.getInstance().getServer().getScheduler().runTaskAsynchronously(TF2.getInstance(), new Runnable() 
		{
			@Override
			public void run() 
			{
				SQLUtilities db = SQLUtilities.getUtilities();
				

				db.execUpdate("INSERT INTO "
							+ "player_stats(username, kills, highest_killstreak, points_captured, games_played, red_team_count, blue_team_count, time_ingame, games_won, deaths) "
							+ "VALUES ('" + player + "', '"+kills+"', '0', '"+points_captured+"', '"+games_played+"', '0', '0', '0', '"+games_won+"', '"+deaths+"') "
							+ "ON DUPLICATE KEY UPDATE kills=" + kills + ", points_captured=" + points_captured + ", games_played=" + games_played
							+ ", games_won=" + games_won + ", deaths = " + deaths);

				
				StatsConfiguration.setTimesUpdated(StatsConfiguration.getTimesUpdated()+1);
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDatabase &f&l» &3Sucsesfully synced &e"+player+"'s &3stats."));
				if (StatsConfiguration.getTimesUpdated() == StatsConfiguration.getTotalEntrys())
				{
					sender.sendMessage("");
					sender.sendMessage("");
					sender.sendMessage("");
					sender.sendMessage("");
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDatabase &f&l» &3Sucsesfully sent &e"+StatsConfiguration.getTimesUpdated()+" &3entrys to the database."));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDatabase &f&l» &3The stats have been synced."));
				}
			}
		});
	}
}
