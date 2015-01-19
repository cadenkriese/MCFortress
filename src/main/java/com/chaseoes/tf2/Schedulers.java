package com.chaseoes.tf2;

import com.chaseoes.tf2.capturepoints.CapturePointUtilities;
import com.chaseoes.tf2.localization.Localizers;
import com.chaseoes.tf2.utilities.LocationStore;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.connorlinfoot.titleapi.TitleAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.inventivetalent.bossbar.BossBarAPI;

import java.util.HashMap;

public class Schedulers {

	private TF2 plugin;
	static Schedulers instance = new Schedulers();
	Integer afkchecker;
	public HashMap<String, Integer> redcounter = new HashMap<String, Integer>();
	public HashMap<String, Integer> countdowns = new HashMap<String, Integer>();
	public HashMap<String, Integer> timelimitcounter = new HashMap<String, Integer>();
	BukkitTask reminderTimer = null;

	private Schedulers() {

	}

	public static Schedulers getSchedulers() {
		return instance;
	}

	public void setup(TF2 p) {
		plugin = p;
	}

	public void startAFKChecker() {
		Bukkit.getServer().broadcastMessage("1");
		final Integer afklimit = plugin.getConfig().getInt("afk-timer");
		afkchecker = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().broadcastMessage("2");
				try {
					for (Map map : MapUtilities.getUtilities().getMaps()) {
						Bukkit.getServer().broadcastMessage("3");
						for (String p : GameUtilities.getUtilities().getGame(map).getPlayersIngame()) {
							Bukkit.getServer().broadcastMessage("4");
							Player player = plugin.getServer().getPlayerExact(p);
							Bukkit.getServer().broadcastMessage("5");
							if (player == null) 
							{
								continue;
							}
							Bukkit.getServer().broadcastMessage("6");
							Integer afktime = LocationStore.getAFKTime(player);
							Location lastloc = LocationStore.getLastLocation(player);
							Location currentloc = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
							if (lastloc != null) {
								if (lastloc.getWorld().getName().equals(currentloc.getWorld().getName()) && lastloc.getBlockX() == currentloc.getBlockX() && lastloc.getBlockY() == currentloc.getBlockY() && lastloc.getBlockZ() == currentloc.getBlockZ()) {
									Bukkit.getServer().broadcastMessage("7");
									if (afktime == null) {
										LocationStore.setAFKTime(player, 1);
										Bukkit.getServer().broadcastMessage("8");
									} 
									else {
										LocationStore.setAFKTime(player, afktime + 1);
										Integer afkTimeLeft = afklimit - afktime;
										if (afkTimeLeft < 4)
										{
											TitleAPI.sendTitle(player, 0, 21, 0, "&cHey there, wake up!", "&3You'll be kicked in &c"+afkTimeLeft+"&3 seconds");
											player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 100000, 1);
										}
										else if (afkTimeLeft < 11)
										{
											TitleAPI.sendTitle(player, 0, 21, 0, "&cHey there, wake up!", "&3You'll be kicked in &e"+afkTimeLeft+"&3 seconds");
											player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 100000, 1);
										}
										else if (afkTimeLeft < 16)
										{
											TitleAPI.sendTitle(player, 0, 21, 0, "&cHey there, wake up!", "&3You'll be kicked in &a"+afkTimeLeft+"&3 seconds");
											player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 100000, 1);
										}
									}

									if (afklimit.equals(afktime)) {
										GameUtilities.getUtilities().getGamePlayer(player).getGame().leaveGame(player);
										Localizers.getDefaultLoc().PLAYER_KICKED_FOR_AFK.sendPrefixed(player);
										TitleAPI.sendTitle(player, 10, 40, 10, "&cYou were kicked", "&eFor being AFK");
										LocationStore.setAFKTime(player, null);
										LocationStore.unsetLastLocation(player);
									}
								} else {
									LocationStore.setAFKTime(player, null);
									LocationStore.unsetLastLocation(player);
								}
								LocationStore.setLastLocation(player);
							} else {
								LocationStore.setLastLocation(player);
							}

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0L, 20L);

		int remindEvery = plugin.getConfig().getInt("capture-reminder");
		if (remindEvery != 0) {
			reminderTimer = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
				@Override
				public void run() {
					try {
						for (Map map : MapUtilities.getUtilities().getMaps()) {
							for (String p : GameUtilities.getUtilities().getGame(map).getPlayersIngame()) {
								Player player = Bukkit.getPlayerExact(p);
								if (player == null) {
									continue;
								}
								if (CapturePointUtilities.getUtilities().getFirstUncaptured(map) != null && CapturePointUtilities.getUtilities().getFirstUncaptured(map).getLocation() != null) {
									player.getLocation().getWorld().strikeLightningEffect(CapturePointUtilities.getUtilities().getFirstUncaptured(map).getLocation());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 0L, remindEvery * 20L);
		}
	}

	public void stopAFKChecker() {
		if (afkchecker != null) {
			plugin.getServer().getScheduler().cancelTask(afkchecker);
		}
		afkchecker = null;
	}

	public void startRedTeamCountdown(final Map map) {
		final Game game = GameUtilities.getUtilities().getGame(map);
		if (redcounter.containsKey(map.getName())) {
			return;
		}
		redcounter.put(map.getName(), plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			int secondsleft = map.getRedTeamTeleportTime();

			@Override
			public void run() {
				if (secondsleft > 0) 
				{
					if (secondsleft == 60 || secondsleft == 30 || secondsleft == 10 || secondsleft < 6) 
					{
						game.broadcast(Localizers.getDefaultLoc().RED_TEAM_TELEPORTED_IN.getPrefixedString(secondsleft), Team.RED);
						for(final String lp : game.getPlayersIngame())
						{
							final Player ps = Bukkit.getPlayerExact(lp);
							if (secondsleft == 1)
							{
								TitleAPI.sendTitle(ps, 0, 20, 1, "&3Mission begins in", "&c"+secondsleft+" &3second");
								ps.playSound(ps.getLocation(), Sound.VILLAGER_IDLE, 10000, 1);
							}
							else if (secondsleft == 2)
							{
								TitleAPI.sendTitle(ps, 0, 20, 1, "&3Mission begins in", "&c"+secondsleft+" &3seconds");
                				ps.playSound(ps.getLocation(), Sound.VILLAGER_YES, 10000, 1);
							}
							else if (secondsleft == 3)
							{
								TitleAPI.sendTitle(ps, 0, 20, 1, "&3Mission begins in", "&c"+secondsleft+" &3seconds");
                				ps.playSound(ps.getLocation(), Sound.VILLAGER_NO, 10000, 1);
							}
							else if (secondsleft == 4)
							{
								TitleAPI.sendTitle(ps, 0, 21, 0, "&3Mission begins in", "&e"+secondsleft+" &3seconds");
								ps.playSound(ps.getLocation(), Sound.VILLAGER_HAGGLE, 10000, 1);
							}
							else if (secondsleft == 5)
							{
								TitleAPI.sendTitle(ps, 5, 21, 0, "&3Mission begins in", "&e"+secondsleft+" &3seconds");
								ps.playSound(ps.getLocation(), Sound.CREEPER_DEATH, 10000, 1);
							}
							else if (secondsleft == 10)
							{
								TitleAPI.sendTitle(ps, 5, 21, 5, "&3Mission begins in", "&a"+secondsleft+" &3seconds");
								ps.playSound(ps.getLocation(),Sound.ENDERMAN_STARE, 10000, 1);
							}
							else if (secondsleft == 30)
							{
								TitleAPI.sendTitle(ps, 10, 40, 10, "&3Mission begins in", "&a"+secondsleft+" &3seconds");
								ps.playSound(ps.getLocation(), Sound.ENDERMAN_DEATH, 10000, 1);
							}
							else if (secondsleft == 60 || secondsleft == 30)
							{
								TitleAPI.sendTitle(ps, 10, 40, 10, "&3Mission begins in", "&a"+secondsleft+" &3seconds");
								ps.playSound(ps.getLocation(), Sound.VILLAGER_DEATH, 10000, 1);
							}
						}
					}
					for(final String lp : game.getPlayersIngame())
					{
						final Player p = Bukkit.getPlayerExact(lp);
						String seconds = "seconds";
						if (secondsleft == 1)
						{
							seconds = "second";
						}
						ActionBarAPI.sendActionBar(p, ChatColor.DARK_AQUA+"Mission begins in "+ChatColor.YELLOW+secondsleft+ChatColor.DARK_AQUA+" "+seconds);
					}
				} 
				else 
				{
					stopRedTeamCountdown(map.getName());
				}
				secondsleft--;
			}
		}, 0L, 20L));
	}

	public void startCountdown(final Map map) {
		final Game game = GameUtilities.getUtilities().getGame(map);
		game.setStatus(GameStatus.STARTING);
		if (countdowns.containsKey(map.getName())) {
			return;
		}
		countdowns.put(map.getName(), plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			int secondsLeft = plugin.getConfig().getInt("countdown");

			@Override
			public void run() {
				if (secondsLeft > 0) {
					if (secondsLeft == 1)
					{
						game.broadcast(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3Game starting in &e1 &3second!"));
					}
					else if ((secondsLeft % 10 == 0 || secondsLeft < 6) && secondsLeft != 1)
					{
						game.broadcast(Localizers.getDefaultLoc().GAME_STARTING_IN.getPrefixedString(secondsLeft));
					}
					secondsLeft--;
				} else {
					game.startMatch();
					stopCountdown(map.getName());
				}
			}
		}, 0L, 20L));
	}

	public void startTimeLimitCounter(final Map map) { 
		final Game game = GameUtilities.getUtilities().getGame(map);
		final int limit = map.getTimelimit();
		if (timelimitcounter.containsKey(map.getName())) {
			return;
		}
		timelimitcounter.put(map.getName(), plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			int current = 0;
			int secondsleft = map.getTimelimit();
			boolean hasAddedBonusTime = false;

			@Override
			public void run() {
				try {
					if (game.addTime && !hasAddedBonusTime)
					{
						current = current-300;
						secondsleft = secondsleft+300;
						hasAddedBonusTime = true;
					}
					game.time = current;
					if (secondsleft > 0) 
					{
						if (secondsleft == 1)
						{
							game.broadcast(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3Game ending in &e1 &3second!"));
						}
						else if ((secondsleft % 60 == 0 || secondsleft < 10)) 
						{
							game.broadcast(Localizers.getDefaultLoc().GAME_ENDING_IN.getPrefixedString(game.getTimeLeftPretty()));
						}
					}
					secondsleft--;
					if (current >= limit) 
					{
						for (GamePlayer gp : game.playersInGame.values())
						{
							BossTabBars.getBars().stopGameBossBar(game.getMapName(), gp.getPlayer());
							BossBarAPI.setMessage(gp.getPlayer(), ChatColor.translateAlternateColorCodes('&', "&bBLU &7Team Seizes Area!"));
						}
						game.winMatch(Team.BLUE);
						stopTimeLimitCounter(map.getName());
					}
					current++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0L, 20L));
	}

	public void stopRedTeamCountdown(String map) {
		if (redcounter.get(map) != null) {
			plugin.getServer().getScheduler().cancelTask(redcounter.get(map));
			redcounter.remove(map);
		}
	}

	public void stopTimeLimitCounter(String map) {
		if (timelimitcounter.get(map) != null) 
		{
			plugin.getServer().getScheduler().cancelTask(timelimitcounter.get(map));
			timelimitcounter.remove(map);
			
			Map map2 = new Map(TF2.getInstance(), map);
			final Game game = GameUtilities.getUtilities().getGame(map2);
			game.addTime = false;
		}
	}

	public void stopCountdown(String map) {
		if (countdowns.get(map) != null) {
			plugin.getServer().getScheduler().cancelTask(countdowns.get(map));
			countdowns.remove(map);
		}
	}

}
