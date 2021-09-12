package com.chaseoes.tf2;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.Map;

public class GameScoreboard {

	Game game;
	ScoreboardManager manager;
	Scoreboard board;
	Team red;
	Team blue;
	Objective objective;
	Objective tabObjective;
	Set<String> currentlyOnScoreboard = new HashSet<String>(10);

	public GameScoreboard(Game g) {
		game = g;
		manager = TF2.getInstance().getServer().getScoreboardManager();
		board = manager.getNewScoreboard();
		red = board.registerNewTeam(com.chaseoes.tf2.Team.RED.getName());
		blue = board.registerNewTeam(com.chaseoes.tf2.Team.BLUE.getName());
		
		red.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + com.chaseoes.tf2.Team.RED.getName());
		blue.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + com.chaseoes.tf2.Team.BLUE.getName());
		red.setCanSeeFriendlyInvisibles(true);
		red.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		blue.setCanSeeFriendlyInvisibles(true);
		blue.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		objective = board.registerNewObjective("TF2;", "dummy");
		tabObjective = board.registerNewObjective("TF2tab", "dummy");
		red.setPrefix(ChatColor.RED + "");
		blue.setPrefix(ChatColor.BLUE + "");
		tabObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		//qobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		tabObjective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "TF2 Kills");
		objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "TF2 Kills");
	}

	
	public void addPlayer(GamePlayer gp) 
	{
		if (gp.getTeam() == com.chaseoes.tf2.Team.RED) 
		{
			red.addEntry(gp.getName());
		} 
		else 
		{
			blue.addEntry(gp.getName());
		}
		updateBoard();
	}

	public void removePlayer(GamePlayer gp) {
		if (gp.getTeam() == com.chaseoes.tf2.Team.RED) {
			red.removeEntry(gp.getName());
		} else {
			blue.removeEntry(gp.getName());
		}
		board.resetScores(gp.getName());
		if (TF2.getInstance().getConfig().getBoolean("scoreboard")) {
			gp.getPlayer().setScoreboard(manager.getMainScoreboard());
		}
		updateBoard();
	}


	public void updateBoard() {
		Collection<GamePlayer> players = game.playersInGame.values();
		if (TF2.getInstance().getConfig().getBoolean("scoreboard")) {
			for (GamePlayer gp : players) {
				if (!gp.getPlayer().getScoreboard().equals(board)) {
					gp.getPlayer().setScoreboard(board);
				}
			}
		}
		ArrayList<GamePlayer> arrayPlayers = new ArrayList<GamePlayer>(players);
		Collections.sort(arrayPlayers, new GameplayerKillComparator());
		Set<Map.Entry<String, Integer>> entries = new HashSet<Map.Entry<String, Integer>>();
		for (int i = 0; i < 10 && i < arrayPlayers.size(); i++) {
			GamePlayer gp = arrayPlayers.get(i);
			entries.add(new AbstractMap.SimpleEntry<String, Integer>(gp.getName(), gp.getTotalKills()));
			currentlyOnScoreboard.remove(gp.getName());
		}
		for (String player : currentlyOnScoreboard) {
			board.resetScores(getPlayer(player).getName());
		}
		currentlyOnScoreboard.clear();
		for (Map.Entry<String, Integer> entry : entries) {
			Score score = objective.getScore(getPlayer(entry.getKey()).getName());
			Score scoreTab = tabObjective.getScore(getPlayer(entry.getKey()).getName());
			if (entry.getValue() == 0) {
				score.setScore(1);
				scoreTab.setScore(1);
			}
			score.setScore(entry.getValue());
			scoreTab.setScore(entry.getValue());
			currentlyOnScoreboard.add(entry.getKey());
		}
	}

	public void resetScores() {
		for (GamePlayer gp : game.playersInGame.values()) {
			board.resetScores(gp.getName());
		}
	}

	public void remove() {
		resetScores();
		for (String name : red.getEntries()) 
		{
			red.removeEntry(name);
		}
		
		for (String name : blue.getEntries()) 
		{
			blue.removeEntry(name);
		}
		
		for (GamePlayer gp : game.playersInGame.values())
		{
			if (TF2.getInstance().getConfig().getBoolean("scoreboard")) 
			{
				gp.getPlayer().setScoreboard(manager.getMainScoreboard());
			}
		}
	}

	@SuppressWarnings("deprecation")
	private OfflinePlayer getPlayer(String player) {
		return TF2.getInstance().getServer().getOfflinePlayer(player);
	}

	class GameplayerKillComparator implements Comparator<GamePlayer> {

		@Override
		public int compare(GamePlayer o1, GamePlayer o2) {
			return o2.getTotalKills() - o1.getTotalKills();
		}
	}

}
