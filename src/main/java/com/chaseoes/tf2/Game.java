package com.chaseoes.tf2;

import com.chaseoes.tf2.capturepoints.CapturePoint;
import com.chaseoes.tf2.capturepoints.CapturePointUtilities;
import com.chaseoes.tf2.classes.TF2Class;
import com.chaseoes.tf2.commands.SpectateCommand;
import com.chaseoes.tf2.engineer.Building;
import com.chaseoes.tf2.listeners.TF2DeathListener;
import com.chaseoes.tf2.lobbywall.LobbyWall;
import com.chaseoes.tf2.localization.Localizer;
import com.chaseoes.tf2.localization.Localizers;
import com.chaseoes.tf2.sound.TFSound;
import com.chaseoes.tf2.utilities.Container;
import com.chaseoes.tf2.utilities.FireworkUtilities;
import com.chaseoes.tf2.utilities.NametagUtilities;
import com.chaseoes.tf2.utilities.WorldEditUtilities;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.connorlinfoot.titleapi.TitleAPI;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBarAPI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game 
{
	TF2 plugin;
	Map map;
	GameStatus status = GameStatus.WAITING;
	public boolean redHasBeenTeleported = false;
	public int time = 0;
	GameScoreboard scoreboard;
	boolean addTime = false;
	boolean isCrit = false;
	Team critTeam = null;
	ArrayList<Building> buildings = new ArrayList<Building>();

	public HashMap<String, GamePlayer> playersInGame = new HashMap<String, GamePlayer>();

	class DelayedRunner implements Runnable
	{
		private List<GamePlayer> winningTeamList;
		private List<GamePlayer> losingTeamList;

		public DelayedRunner( List<GamePlayer> winners, List<GamePlayer> losers)
		{
			winningTeamList = winners;
			losingTeamList = losers;
		}

		public void run()
		{
			for(GamePlayer gp : winningTeamList)
			{
				Player p = gp.getPlayer();
				p.playSound(p.getLocation(), Sound.WOLF_PANT, 100000, 1);
			}
			for(GamePlayer gp : losingTeamList)
			{
				Player p = gp.getPlayer();
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 100000, 1);
			}
		}
	}

	public String isCrit(Player p)
	{
		if (p == null || !isCrit || critTeam == null)
		{
			return "false";
		}


		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		if (isCrit)
		{
			if (gp.getTeam() == critTeam)
			{
				return "true";
			}
			else
			{
				return "no_fire";
			}
		}
		return "false";
	}
	public Game(Map m, TF2 plugin) {
		map = m;
		this.plugin = plugin;
		scoreboard = new GameScoreboard(this);
	}

	public GamePlayer getPlayer(Player player) {
		return playersInGame.get(player.getName());
	}

	public void setStatus(GameStatus s) {
		status = s;
	}

	public boolean hasAddedTime() {
		return addTime;
	}

	public void addTimeLeft() {
		addTime = true;
	}

	public GameStatus getStatus() {
		return status;
	}

	public GameScoreboard getScoreboard() {
		return scoreboard;
	}

	public String getMapName() {
		return map.getName();
	}

	public void updateTime(int time) {
		this.time = time;
	}

	public Integer getTimeLeftSeconds() {
		return map.getTimelimit() - time;
	}

	public void setExpOfPlayers(double expOfPlayers) {
		for (GamePlayer gp : playersInGame.values()) {
			gp.getPlayer().setExp((float) expOfPlayers);
		}
	}

	public List<String> getPlayersIngame() {
		List<String> l = new ArrayList<String>();
		for (GamePlayer gp : playersInGame.values()) {
			l.add(gp.getName());
		}
		return l;
	}

	public void startMatch() {
		isCrit = false;
		critTeam = null;
		for (GamePlayer gp : playersInGame.values())
		{
			NametagUtilities.clearTag(gp.getPlayer());
		}
		setStatus(GameStatus.INGAME);
		CapturePointUtilities.getUtilities().uncaptureAll(map);
		for (Container container : map.getContainers()) {
			container.applyItems();
		}
		Schedulers.getSchedulers().startTimeLimitCounter(map);
		Schedulers.getSchedulers().startRedTeamCountdown(map);

		for (GamePlayer gp : playersInGame.values()) {
			Player player = gp.getPlayer();
			if (gp.getTeam() == Team.BLUE) {
				if (gp.getCurrentClass() != null) {
					gp.setInLobby(false);
					player.teleport(map.getBlueSpawn());
					gp.getCurrentClass().apply(gp);
					gp.setUsingChangeClassButton(false);
				} else {
					gp.setUsingChangeClassButton(true);
					Localizers.getDefaultLoc().TELEPORT_AFTER_CHOOSE_CLASS.sendPrefixed(player);
				}
			}
		}

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (GamePlayer gp : playersInGame.values()) {
					Player player = gp.getPlayer();
					player.playSound(player.getLocation(), Sound.WOLF_SHAKE, 1000000, 1);
					if (gp.getTeam() == Team.RED) {
						if (gp.getCurrentClass() != null) {
							gp.setInLobby(false);
							player.teleport(map.getRedSpawn());
							gp.getCurrentClass().apply(gp);
							gp.setUsingChangeClassButton(false);
						} else {
							gp.setUsingChangeClassButton(true);
							Localizers.getDefaultLoc().TELEPORT_AFTER_CHOOSE_CLASS.sendPrefixed(player);
						}
					}
				}

				redHasBeenTeleported = true;
				Schedulers.getSchedulers().stopRedTeamCountdown(map.getName());
			}
		}, map.getRedTeamTeleportTime() * 20L);

		for (GamePlayer gp : playersInGame.values()) 
		{
			scoreboard.addPlayer(gp);
		}
		scoreboard.updateBoard();
	}

	@SuppressWarnings("deprecation")
	public void stopMatch(boolean queueCheck, boolean stopTimeLimit) { // TODO: This may make players
		// in queue join on a disabled
		// match
		setStatus(GameStatus.WAITING);
		scoreboard.remove();
		Schedulers.getSchedulers().stopRedTeamCountdown(map.getName());
		if (stopTimeLimit)
		{
			Schedulers.getSchedulers().stopTimeLimitCounter(map.getName());
		}
		Schedulers.getSchedulers().stopCountdown(map.getName());
		String map2 = map.getName();
		for (Container container : map.getContainers()) {
			container.applyItems();
		}

		CapturePointUtilities.getUtilities().uncaptureAll(map);

		for (Entity e : map.getP1().getWorld().getEntities()) {
			if (e instanceof Arrow || e instanceof Item) {
				if (WorldEditUtilities.getWEUtilities().isInMap(e, map)) {
					e.remove();
				}
			}
		}

		for (Location location : map.getCapturePointsLocations())
		{
			int radius = 3;
			for (int x = -(radius); x <= radius; x++)
			{
				for (int y = -(radius); y <= radius; y++)
				{
					for (int z = -(radius); z <= radius; z++)
					{
						Location loc = location.getBlock().getRelative(x, y, z).getLocation();
						if (loc.getBlock().getType() == Material.STAINED_GLASS)
						{
							loc.getBlock().setData((byte) 11);
						}
					}
				}
			}
		}

		Location loc = map.getCapturePoint(1).getLocation();
		Location p1 = map.getP1();
		Location p2 = map.getP2();
		CuboidSelection sel = new CuboidSelection(loc.getWorld(), p1, p2);
		Location min = sel.getMinimumPoint();
		Location max = sel.getMaximumPoint();

		for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
			for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					Block blk = min.getWorld().getBlockAt(new Location(min.getWorld(), x, y, z));
					if (blk.getType() == Material.STAINED_CLAY) {
						if (blk.getData() == (byte) 0)
						{
							blk.setData((byte) 14);
						}
						else if (blk.getData() == (byte) 7)
						{
							Block blk2 = blk.getRelative(0, -1, 0);
							if (!(blk2.getType() == Material.STAINED_CLAY) && !(blk2.getData() == (byte) 13))
							{
								blk.setData((byte) 1);
							}
						}
					}
				}
			}
		}

		for (CapturePoint cp : map.getCapturePoints()) {
			cp.stopCapturing();
		}
		SpectateCommand.getCommand().stopSpectating(this);
		redHasBeenTeleported = false;
		HashMap<String, GamePlayer> hmap = new HashMap<String, GamePlayer>(playersInGame);
		playersInGame.clear();

		for (GamePlayer gp : hmap.values()) {
			Player p = gp.getPlayer();
			if (BossBarAPI.hasBar(p))
			{
				BossBarAPI.removeBar(p);
			}
			Team team = gp.team;
			gp.leaveCurrentGame();
			Localizers.getDefaultLoc().GAME_END.sendPrefixed(gp.getPlayer());
			if (plugin.getConfig().getBoolean("map-rotation")) 
			{
				if (critTeam == Team.RED)
				{
					if (map2.equals("Dustbowl"))
					{
						TF2.getInstance().setADmap("Dustbowl-2");
						gp.getPlayer().performCommand("tf2 join Dustbowl-2 " + team);
					}
					else if (map2.equals("Dustbowl-2"))
					{
						TF2.getInstance().setADmap("Dustbowl-3");
						gp.getPlayer().performCommand("tf2 join Dustbowl-3 " + team);
					}
					else if (map2.equals("Dustbowl-3"))
					{
						TF2.getInstance().setADmap("Dustbowl");
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void winMatch(final Team team)
	{
		setStatus(GameStatus.ENDING);
		Schedulers.getSchedulers().stopTimeLimitCounter(map.getName());
		List<String> inGameOld = new ArrayList<String>();
		final List<GamePlayer> winningTeamList = new ArrayList<GamePlayer>();
		List<GamePlayer> losingTeamList = new ArrayList<GamePlayer>();
		for (GamePlayer gp : playersInGame.values()) 
		{

			if (gp.getTeam() == team)
			{
				inGameOld.add(gp.getName());	
				winningTeamList.add(gp);
			}
			else
			{
				losingTeamList.add(gp);
			}

		}

		// scheduleSyncDelayedTask
		Bukkit.getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new DelayedRunner(winningTeamList, losingTeamList), 40L);
		isCrit = true;
		critTeam = team;

		if (TF2.getInstance().getConfig().getBoolean("stats-database.enabled")) 
		{
			for (GamePlayer gp2 : playersInGame.values()) 
			{
				StatCollector sc = gp2.getStatCollector();
				int highest_killstreak = gp2.getHighestKillstreak();
				int points_captured = gp2.getPointsCaptured();
				int time_ingame = gp2.getTotalTimeIngame();
				sc.addStatsFromGame(gp2.getTotalKills(), highest_killstreak, points_captured, gp2.getTeam(), time_ingame, team, gp2.getDeaths());
				sc.submit();
			}
		}
		final FileConfiguration stats = StatsConfiguration.getStats();
		final File playerStats = StatsConfiguration.getStatsConfig();


		String[] winlines = new String[4];
		winlines[0] = " ";
		winlines[1] = "" + ChatColor.DARK_RED + ChatColor.BOLD + Localizers.getDefaultLoc().RED_TEAM.getString();

		if (team == Team.BLUE) {
			winlines[1] = ChatColor.BLUE + "" + ChatColor.BOLD + Localizers.getDefaultLoc().BLUE_TEAM.getString();
		}

		winlines[2] = ChatColor.GREEN + "" + ChatColor.BOLD + Localizers.getDefaultLoc().WINS.getString();
		winlines[3] = " ";
		String te = ChatColor.DARK_RED + "" + ChatColor.BOLD + Localizers.getDefaultLoc().RED_TEAM.getString() + "" + ChatColor.RESET + ChatColor.YELLOW;

		if (team == Team.BLUE) {
			te = ChatColor.BLUE + "" + ChatColor.BOLD + ChatColor.BOLD + Localizers.getDefaultLoc().BLUE.getString() + "" + ChatColor.RESET + ChatColor.YELLOW;
		}

		LobbyWall.getWall().setAllLines(map.getName(), null, winlines, false, true);

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				String[] creditlines = new String[4];
				creditlines[0] = ChatColor.BOLD + "TF2 Plugin By:";
				creditlines[1] = ChatColor.BLUE + "Chaseoes";
				creditlines[2] = ChatColor.GREEN + "And";
				creditlines[3] = ChatColor.BLUE + "GamerKing195";
				LobbyWall.getWall().setAllLines(map.getName(), 4, creditlines, false, true);
			}
		}, 120L);


		if (TF2.getInstance().getConfig().getBoolean("broadcast-winning-team")) {
			for (Player p : Bukkit.getServer().getOnlinePlayers())
			{
				ActionBarAPI.sendActionBar(p, team.getColor()+" "+team.getName()+"§ehas won on map §9"+TF2.getInstance().getADmap());
			}
			Localizers.getDefaultLoc().GAME_WIN.broadcast(te, ChatColor.BOLD + TF2.getInstance().getADmap() + ChatColor.RESET + "" + ChatColor.YELLOW);
		}

		for (GamePlayer gp : playersInGame.values())
		{
			Player p = gp.getPlayer();

			if (team.equals(Team.BLUE))
			{
				TF2.getInstance().setADmap("Dustbowl");
				TitleAPI.sendTitle(p,10,80,10,"&9Blu","&7Has won The Game");
				Location loc = new Location(MapUtilities.getUtilities().loadLobby().getWorld(), -1, 4, -209);
				Block spawnBeacon = loc.getBlock();
				spawnBeacon.setData((byte) 11);
				resetBeacon(spawnBeacon);
			}
			else 
			{
				TitleAPI.sendTitle(p,10,80,10,"&cRed","&7Has won the game.");
				if (map.getName().equalsIgnoreCase("Dustbowl-3"))
				{
					Location loc = new Location(MapUtilities.getUtilities().loadLobby().getWorld(), -1, 4, -209);
					Block spawnBeacon = loc.getBlock();
					spawnBeacon.setData((byte) 14);
					resetBeacon(spawnBeacon);
				}
			}
		}

		Bukkit.getServer().getScheduler().runTaskLater(TF2.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				CapturePointUtilities.getUtilities().uncaptureAll(map);
				
				if (team == Team.BLUE || map.getName().equals("Dustbowl-3"))
					FireworkUtilities.winFireworks(team);
				
				for (GamePlayer gp : playersInGame.values())
				{
					Player p = gp.getPlayer();
					int highest_killstreak = gp.getHighestKillstreak();
					int points_captured = gp.getPointsCaptured();
					String teamColor = "";
					if (team == Team.RED)
					{
						teamColor = "§c";
					}
					else
					{
						teamColor = "§9";
					}

					Localizer.sendGameEndStats(gp.getPlayer(), gp.getTeam(), gp.getTotalKills(), gp.getTotalDeaths(), points_captured, highest_killstreak, teamColor+team.getName(), gp.getTeam()==team);

					if (StatsConfiguration.checkKills(gp.getPlayer(), gp.getTotalKills()))
					{
						stats.set(gp.getPlayer().getUniqueId().toString()+".pauling", true);
						gp.getPlayer().sendMessage("§9»»§km§c You've earned the acheivement §6Ms. Pauling§c! §9§km§9««");
						TF2.getInstance().addCreditsPretty(p, 300, true);
						if (stats.getBoolean(p.getUniqueId()+".spy") && stats.getBoolean(p.getUniqueId()+".scout") && stats.getBoolean(p.getUniqueId()+".sniper") && stats.getBoolean(p.getUniqueId()+".champ") && !stats.getBoolean(p.getUniqueId()+".master"))
						{
							stats.set(p.getUniqueId()+".master", true);
							gp.getPlayer().sendMessage("§9»»§km§c You've earned the §e§kIII§6MASTER§e§kIII§c acheivement! §9§km§9««");
							TF2.getInstance().addCreditsPretty(p, 1000, true);
							TFSound.ACHEIVEMENT.sendDelayed(p, 1f, 1f, 40L);
						}
						else
						{
							TFSound.ACHEIVEMENT.sendDelayed(p, 1f, 1f, 40L);
						}
					}
					if (StatsConfiguration.checkPoints(gp.getPlayer(), gp.getPointsCaptured()))
					{
						stats.set(p.getUniqueId()+".scout", true);
						gp.getPlayer().sendMessage("§9»»§km§c You've earned the acheivement §6Scout§c! §9§km§9««");
						TF2.getInstance().addCreditsPretty(p, 300, true);
						if (stats.getBoolean(p.getUniqueId()+".spy") && stats.getBoolean(p.getUniqueId()+".pauling") && stats.getBoolean(p.getUniqueId()+".sniper") && stats.getBoolean(p.getUniqueId()+".champ") && !stats.getBoolean(p.getUniqueId()+".master"))
						{
							stats.set(p.getUniqueId()+".master", true);
							gp.getPlayer().sendMessage("§9»»§km§c You've earned the §e§kIII§6MASTER§e§kIII§c acheivement! §9§km§9««");
							TF2.getInstance().addCreditsPretty(p, 1000, true);
							TFSound.ACHEIVEMENT.sendDelayed(p, 1f, 1f, 40L);
						}
						else
						{
							TFSound.ACHEIVEMENT.sendDelayed(p, 1f, 1f, 40L);
						}
					}
					if (StatsConfiguration.checkBackStabs(p, gp.getBackStabs()))
					{
						stats.set(p.getUniqueId()+".spy", true);
						gp.getPlayer().sendMessage("§9»»§km§c You've earned the acheivement §6Slap my Hand§c! §9§km§9««");
						TF2.getInstance().addCreditsPretty(p, 300, true);
						if (stats.getBoolean(p.getUniqueId()+".sniper") && stats.getBoolean(p.getUniqueId()+".pauling") && stats.getBoolean(p.getUniqueId()+".scout") && stats.getBoolean(p.getUniqueId()+".champ") && !stats.getBoolean(p.getUniqueId()+".master"))
						{
							stats.set(p.getUniqueId()+".master", true);
							gp.getPlayer().sendMessage("§9»»§km§c You've earned the §e§kIII§6MASTER§e§kIII§c acheivement! §9§km§9««");
							TF2.getInstance().addCreditsPretty(p, 1000, true);
							TFSound.ACHEIVEMENT.sendDelayed(p, 1f, 1f, 40L);
						}
						else
						{
							TFSound.ACHEIVEMENT.sendDelayed(p, 1f, 1f, 40L);
						}
					}
					if (StatsConfiguration.checkHeadshots(p, gp.getHeadShots()))
					{
						stats.set(p.getUniqueId()+".sniper", true);
						gp.getPlayer().sendMessage("§9»»§km§c You've earned the acheivement §6Be Efficient§c! §9§km§9««");
						TF2.getInstance().addCreditsPretty(p, 300, true);
						if (stats.getBoolean(p.getUniqueId()+".spy") && stats.getBoolean(p.getUniqueId()+".pauling") && stats.getBoolean(p.getUniqueId()+".scout") && stats.getBoolean(p.getUniqueId()+".champ") && !stats.getBoolean(p.getUniqueId()+".master"))
						{
							stats.set(p.getUniqueId()+".master", true);
							gp.getPlayer().sendMessage("§9»»§km§c You've earned the §e§kIII§6MASTER§e§kIII§c acheivement! §9§km§9««");
							TF2.getInstance().addCreditsPretty(p, 1000, true);
							TFSound.ACHEIVEMENT.sendDelayed(p, 1f, 1f, 40L);
						}
						else
						{
							TFSound.ACHEIVEMENT.sendDelayed(p, 1f, 1f, 40L);
						}
					}
					if (winningTeamList.contains(gp))
					{
						if (StatsConfiguration.checkWins(gp.getPlayer()))
						{
							stats.set(p.getUniqueId()+".champ", true);
							gp.getPlayer().sendMessage("§9»»§km§c You've earned the acheivement §6Champion§c! §9§km§9««");
							TF2.getInstance().addCreditsPretty(p, 500, true);
							if (stats.getBoolean(p.getUniqueId()+".sniper") && stats.getBoolean(p.getUniqueId()+".pauling") && stats.getBoolean(p.getUniqueId()+".spy") && stats.getBoolean(p.getUniqueId()+".scout") && !stats.getBoolean(p.getUniqueId()+".master"))
							{
								stats.set(p.getUniqueId()+".master", true);
								gp.getPlayer().sendMessage("§9»»§km§c You've earned the §e§kIII§6MASTER§e§kIII§c acheivement! §9§km§9««");
								TF2.getInstance().addCreditsPretty(p, 1000, true);
								TFSound.ACHEIVEMENT.sendDelayed(p, 1f, 1f, 40L);
							}
							else
							{
								TFSound.ACHEIVEMENT.sendDelayed(p, 1f, 1f, 40L);
							}
						}
						StatsConfiguration.addStats(gp.getPlayer(), gp.getTotalKills(), gp.getTotalDeaths(), gp.getPointsCaptured(), 1, 0, gp.getHeadShots(), gp.getBackStabs());
					}
					else
					{
						StatsConfiguration.addStats(gp.getPlayer(), gp.getTotalKills(), gp.getTotalDeaths(), gp.getPointsCaptured(), 0, 1, gp.getHeadShots(), gp.getBackStabs());
					}
					try {
						stats.save(playerStats);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				stopMatch(true, false);
			}
		}, 300L);
	}

	@SuppressWarnings({ "unused" })
	public void joinGame(GamePlayer player, Team team) {
		//Featherboard not working with scoreboards in plugin.
		//FeatherBoardAPI.showScoreboard(player.getPlayer(), "Ingame");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "featherboard toggle "+player.getName());
		player.getPlayer().setMaxHealth(20d);
		String admap = TF2.getInstance().getADmap();
		BossTabBars.getBars().startGameBossBar(player, map);
		Player p = player.getPlayer();
		GameQueue q = map.getQueue();
		boolean full = q.gameHasRoom();
		if (!q.gameHasRoom()) {
			if (!player.getPlayer().hasPermission("tf2.create")) {
				q.add(player.getPlayer());
				Localizers.getDefaultLoc().IN_LINE.sendPrefixed(player.getPlayer(), q.getPosition(player.getPlayer()) + 1);
				return;
			}
		}

		if (!full && player.getPlayer().hasPermission("tf2.create")) {
			Localizers.getDefaultLoc().PLAYER_JOIN_FULL_MAP.sendPrefixed(player.getPlayer(), ChatColor.BOLD + map.getName() + ChatColor.RESET + "" + ChatColor.YELLOW);
		} else {
			Localizers.getDefaultLoc().PLAYER_JOIN_MAP.sendPrefixed(player.getPlayer(), ChatColor.BOLD + map.getName() + ChatColor.RESET + "" + ChatColor.YELLOW);
		}

		for (Game g : GameUtilities.getUtilities().games.values()) {
			Map gm = TF2.getInstance().getMap(g.getMapName());
			gm.getQueue().remove(player.getPlayer());
		}

		if (SpectateCommand.getCommand().isSpectating(player.getPlayer())) {
			SpectateCommand.getCommand().stopSpectating(player.getPlayer());
		}

		TF2Class c = new TF2Class("NONE");
		playersInGame.put(player.getName(), player);
		player.setTimeEnteredGame();
		player.setMap(getMapName());
		player.setInLobby(true);
		player.setTeam(team);
		player.saveInventory();
		c.clearInventory(player.getPlayer());
		player.getPlayer().setHealth(20);
		player.getPlayer().setFoodLevel(20);
		player.getPlayer().setGameMode(GameMode.valueOf(plugin.getConfig().getString("gamemode").toUpperCase()));
		player.getPlayer().setLevel(0);
		player.getPlayer().setExp(0);

		switch (team) {
		case BLUE:
			player.getPlayer().teleport(map.getBlueLobby());
			break;
		case RED:
			player.getPlayer().teleport(map.getRedLobby());
			break;
		}

		double currentpercent = (double) playersInGame.size() / map.getPlayerlimit() * 100;
		if (getStatus().equals(GameStatus.WAITING)) {
			if (currentpercent >= plugin.getConfig().getInt("autostart-percent")) {
				Schedulers.getSchedulers().startCountdown(map);
				setStatus(GameStatus.STARTING);
			}
		}

		if (getStatus().equals(GameStatus.INGAME)) 

		{
			NametagUtilities.clearTag(p);
			scoreboard.addPlayer(player);
			scoreboard.updateBoard();
		}

		if (getStatus() == GameStatus.WAITING) {
			Localizers.getDefaultLoc().PERCENT_JOIN.sendPrefixed(player.getPlayer(), plugin.getConfig().getInt("autostart-percent"));
		} else if (getStatus() == GameStatus.INGAME) {
			switch (player.getTeam()) {
			case RED:
				if (redHasBeenTeleported) {
					player.setUsingChangeClassButton(true);
				}
				break;
			case BLUE:
				player.setUsingChangeClassButton(true);
				break;
			}
		}

		player.getPlayer().updateInventory();
	}

	public void leaveGame(Player player) {
		player.setMaxHealth(20d);
		GamePlayer gp = getPlayer(player);
		TF2DeathListener.getListener().stopRespawn(gp);
		if (gp.isInvis || gp.InvertInvis())
		{
			gp.setInvertInvis(false);
			gp.setInvis(false);
			gp.setInvisTime(10);
		}
		else
		{
			BossTabBars.getBars().stopGameBossBar(map.getName(), gp.getPlayer());
		}
		playersInGame.remove(gp.getName());
		boolean redEmpty = getSizeOfTeam(Team.RED) == 0;
		boolean blueEmpty = getSizeOfTeam(Team.BLUE) == 0;
		if (status == GameStatus.INGAME) {
			if (redEmpty && !blueEmpty) {
				stopMatch(true, true);
			} else if (blueEmpty && !redEmpty) {
				stopMatch(true, true);
			}
			scoreboard.removePlayer(gp);
		}
		gp.leaveCurrentGame();
		NametagUtilities.applyTag(player);
		if (redEmpty && blueEmpty)
		{
			TF2.getInstance().setADmap("Dustbowl");
		}
	}

	public Team decideTeam() {
		int red = getSizeOfTeam(Team.RED);
		int blue = getSizeOfTeam(Team.BLUE);

		if (red > blue) {
			return Team.BLUE;
		}

		return Team.RED;
	}

	public Integer getSizeOfTeam(Team team) {
		int red = 0;
		int blue = 0;
		for (GamePlayer player : playersInGame.values()) {
			if (player.getTeam() == Team.RED) {
				red++;
			}

			if (player.getTeam() == Team.BLUE) {
				blue++;
			}
		}

		if (team == Team.BLUE) {
			return blue;
		}

		return red;
	}

	public String getTimeLeft() {
		if (getStatus().equals(GameStatus.WAITING) || getStatus().equals(GameStatus.STARTING)) {
			return Localizers.getDefaultLoc().GAMESTATUS_NOT_STARTED.getString();
		}

		int time = getTimeLeftSeconds();
		int hours = time / (60 * 60);
		time = time % (60 * 60);
		int minutes = time / 60;
		time = time % 60;

		if (hours == 0) {
			return minutes + "m " + time + "s";
		}

		return Math.abs(hours) + "h " + Math.abs(minutes) + "m " + Math.abs(time) + "s";
	}

	public String getTimeLeftPretty() {
		if (getStatus().equals(GameStatus.WAITING) || getStatus().equals(GameStatus.STARTING)) {
			return Localizers.getDefaultLoc().GAMESTATUS_NOT_STARTED.getString();
		}
		Integer time = getTimeLeftSeconds();
		int hours = time / (60 * 60);
		time = time % (60 * 60);
		int minutes = time / 60;
		time = time % 60;

		String minute = "minutes";
		if (minutes == 1) {
			minute = "minute";
		}

		if (hours == 0) {
			if (time == 0) {
				return minutes + " " + ChatColor.DARK_AQUA + minute;
			}
			if (minutes == 0) {
				return time + " " + ChatColor.DARK_AQUA + "seconds";
			}
			return minutes + " " + ChatColor.DARK_AQUA + minute + " " + ChatColor.YELLOW + time + " " + ChatColor.DARK_AQUA + "seconds";
		}
		return Math.abs(hours) + "h " + Math.abs(minutes) + "m " + Math.abs(time) + "s";
	}

	public String getPrettyStatus() {
		GameStatus status = getStatus();
		if (status == GameStatus.INGAME) {
			return Localizers.getDefaultLoc().GAMESTATUS_INGAME.getString();
		} else if (status == GameStatus.STARTING) {
			return Localizers.getDefaultLoc().GAMESTATUS_STARTING.getString();
		} else if (status == GameStatus.WAITING) {
			return Localizers.getDefaultLoc().GAMESTATUS_WAITING.getString();
		} else if (status == GameStatus.DISABLED) {
			return Localizers.getDefaultLoc().GAMESTATUS_DISABLED.getString();
		}
		return "ERROR";
	}

	public void broadcast(String message) {
		for (GamePlayer player : playersInGame.values()) {
			player.getPlayer().sendMessage(message);
		}
		for (SpectatePlayer sp : SpectateCommand.getCommand().getSpectators(this)) {
			sp.player.sendMessage(message);
		}
	}

	public void broadcast(String message, Team team) {
		for (GamePlayer player : playersInGame.values()) {
			if (player.getTeam() == team) {
				player.getPlayer().sendMessage(message);
			}
		}
	}

	/*
	 * public Queue getQueue() { return queue; }
	 */

	public boolean isFull() {
		return playersInGame.size() >= map.getPlayerlimit();
	}

	public Map getMap() {
		return map;
	}
	public void resetBeacon(final Block b) {
		TF2.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				b.setData((byte) 15);
			}
		}, 1000L);
	}
	public ArrayList<Building> getBuildings() 
	{
		return buildings;
	}
	public void addBuilding(Building building) 
	{
		buildings.add(building);
	}
}
