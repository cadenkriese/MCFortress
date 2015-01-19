package com.chaseoes.tf2.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.chaseoes.tf2.sound.TFSound;
import com.chaseoes.tf2.utilities.GeneralUtilities;
import com.chaseoes.tf2.utilities.WeaponUtilities;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.chaseoes.tf2.Game;
import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameStatus;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.Map;
import com.chaseoes.tf2.MapUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.Team;
import com.chaseoes.tf2.boosters.BoosterStatusFile;
import com.chaseoes.tf2.classes.TF2Class;
import com.chaseoes.tf2.events.TF2DeathEvent;
import com.chaseoes.tf2.localization.Localizer;
import com.connorlinfoot.titleapi.TitleAPI;

import de.robingrether.idisguise.api.DisguiseAPI;
import fr.mrsheepsheep.tinthealth.THAPI;
import fr.mrsheepsheep.tinthealth.TintHealth;

public class TF2DeathListener implements Listener {
	public HashMap<String, Integer> respawncounter = new HashMap<String, Integer>();
	public int respawnTime;
	public int resetBar;
	static TF2DeathListener instance = new TF2DeathListener();
	public static TF2DeathListener getListener() {
		return instance;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(final TF2DeathEvent event) 
	{
		@SuppressWarnings("unused")
		final Location spawnPoint = event.getPlayer().getLocation();
		TF2.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable() {
			@Override
			public void run() 
			{
				final Player player = event.getPlayer();
				final GamePlayer playerg = GameUtilities.getUtilities().getGamePlayer(player);
				final Player killer = event.getKiller();
				GamePlayer killerg = GameUtilities.getUtilities().getGamePlayer(killer);

				Game game = playerg.getGame();
				if (game == null) {
					return;
				}
				Map map = game.getMap();


				ChatColor playerTeam = ChatColor.YELLOW;
				ChatColor killerTeam = ChatColor.YELLOW;
				switch(playerg.getTeam())
				{
				case BLUE:
					playerTeam = ChatColor.BLUE;
					killerTeam = ChatColor.RED;
					break;
				case RED:
					killerTeam = ChatColor.BLUE;
					playerTeam = ChatColor.RED;
					break;
				}
				
				playerg.setDisguise(null);

				// Reset the kills of the player who died.
				playerg.addKillstreak(playerg.getKills());
				playerg.setKills(0);
				playerg.setDeaths(-1);
				playerg.settotalDeaths(-1);

				// Add one kill to the kills the killer has made.
				if (!playerg.getName().equalsIgnoreCase(killerg.getName())) 
				{
					killerg.setTotalKills(-1);
					killer.setLevel(killerg.getTotalKills());
					killerg.setKills(-1);
				}

				if (killer != player)
				{	
					int kills = killerg.getKills();
					
					double health1 = killer.getHealth()/2;
					double health = Math.round(health1 * 2) / 2.0;
					
					ArrayList<String> tTip = new ArrayList<String>();
					tTip.add(ChatColor.translateAlternateColorCodes('&', killerTeam+"&l"+killer.getName()+"&7&l VS. "+playerTeam+"&l"+player.getName()+"&8&l:"));
					tTip.add("");
					tTip.add(ChatColor.DARK_GRAY+"Killed With: "+ChatColor.WHITE+WeaponUtilities.getUtilities().getWeaponTitleExact(killer.getItemInHand()));
					tTip.add(ChatColor.DARK_GRAY+"Remaining Health: "+ChatColor.RED+health+"♥"+ChatColor.DARK_RED+"/"+ChatColor.RED+killer.getMaxHealth()/2+"♥");
					if (killerg.critMessage() != null && !killerg.critMessage().equalsIgnoreCase(""))
					{
						tTip.add(ChatColor.DARK_GRAY+"Extra: "+killerg.critMessage());
					}
					
					for (GamePlayer ingame : game.playersInGame.values())
					{
						FancyMessage killMsg = new FancyMessage("");
						if (ingame.getTeam() == killerg.getTeam())
						{
							killMsg
							.then("» ")
							.color(ChatColor.GREEN)
							.tooltip(tTip);
						}
						else
						{
							killMsg
							.then("« ")
							.color(ChatColor.DARK_RED)
							.tooltip(tTip);
						}
						
						killMsg.then(killer.getName()+" ")
						.color(killerTeam)
						.tooltip(tTip)
						//TODO add support for custom kill messages
						.then(" killed ")
						.color(ChatColor.GRAY)
						.tooltip(tTip)
						.then(player.getName())
						.color(playerTeam)
						.tooltip(tTip)
						.then(" ❯ ")
						.color(ChatColor.GRAY)
						.then(kills+"")
						.color(ChatColor.DARK_GRAY)
						.send(ingame.getPlayer());
					}
					
					killer.sendMessage(ChatColor.GRAY+"(+ "+10*BoosterStatusFile.getFile().getBoost()+" Credits)"+BoosterStatusFile.getFile().getBoosterMessage());
					
					//KILLSTREAKS
					
					if (kills % 5 == 0) 
					{
						
						TFSound.EXCITING.send(killer, 1f, 1f);
						TFSound.EXCITING.send(player, 1f, 1f);
						
						int tier = kills / 5;
						String streakMessage = "";
						if (tier == 1)
						{
							streakMessage = "ON A §2KILLING SPREE ";
						}
						else if (tier == 2)
						{
							streakMessage = "§6UNSTOPPABLE ";
						}
						else if (tier == 3)
						{
							streakMessage  = "ON A §5RAMPAGE ";
						}
						else if (tier >= 4)
						{
							streakMessage = "§eGOD-LIKE ";
						}
						for (GamePlayer gp : game.playersInGame.values())
						{
							Player p = gp.getPlayer();
							TitleAPI.sendTitle(p, 10, 40, 10, "", killerTeam+killer.getName().toUpperCase()+"§8 IS "+streakMessage+" ❯ "+kills);
						}
					}
				}
				else
				{
					game.broadcast(playerTeam+player.getName()+" "+ChatColor.GRAY+Localizer.getRandomKillMessage(event.getCause()));
				}

				killer.playSound(killer.getLocation(), Sound.valueOf(TF2.getInstance().getConfig().getString("killsound.sound")), TF2.getInstance().getConfig().getInt("killsound.volume"), TF2.getInstance().getConfig().getInt("killsound.pitch"));

				if (playerg.getKills() >= 5)
				{
					for (GamePlayer gp2 : game.playersInGame.values())
					{
						TitleAPI.sendTitle(gp2.getPlayer(), 5, 10, 5, "", killerTeam+killerg.getName()+"§8 ENDED "+playerTeam+playerg.getName()+"'s §8KILLSTREAK "+playerTeam+playerg.getKills());
					}
					game.broadcast(ChatColor.translateAlternateColorCodes('&', killerTeam+killerg.getName()+"&8 ENDED "+playerTeam+playerg.getName()+"'s &8KILLSTREAK "+playerTeam+playerg.getKills()));
				}

				TF2.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable() {
					@Override
					public void run() {
						playerg.setJustSpawned(false);
					}
				}, 160L);

				// Run Commands
				GeneralUtilities.runCommands("on-kill", player.getPlayer(), killer.getPlayer(), game.getMap());

				
				Location teamspawn = MapUtilities.getUtilities().loadTeamSpawn(map.toString(), playerg.getTeam());
				if (teamspawn == null)
				{
				}
				game.getScoreboard().updateBoard();
				if (!(playerg.getKiller() == null))
				{
					playerg.setKiller(killerg.getPlayer());
				}
				else
				{
					playerg.setKiller(player);
				}
				playerg.setIsDead(true);
			}
		}, 1L);
	}

	public void respawnDelay(final Player p, final GamePlayer gp, final Player k, final Map map, final Location teamspawn, final Game game)
	{
		if (gp.isIngame())
		{
			gp.setSpawnLoc(p.getLocation());
			DisguiseAPI api =  TF2.getInstance().getDisguiseAPI();
			if (api.isDisguised(p))
			{
				api.undisguiseToAll(p);
			}
			for(final String lp : game.getPlayersIngame())
			{
				Player p2 = Bukkit.getPlayerExact(lp);
				p2.showPlayer(p);
				p2.hidePlayer(p);
			}
			p.setFireTicks(0);
			PlayerInventory inv = p.getInventory();
			inv.clear();
			inv.setArmorContents(null);
			p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 1));
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1));
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 3));
			p.setHealth(p.getMaxHealth());
			p.setAllowFlight(true);
			p.setFlying(true);
			p.setFlySpeed(0.3f);

			TintHealth th = (TintHealth) TF2.getInstance().getServer().getPluginManager().getPlugin("TintHealth");
			THAPI THapi = th.getAPI();
			THapi.fadeTint(p, 50, 4);

			gp.setSpawnLoc(p.getLocation());
			gp.setIsDead(true);

			if (respawncounter.containsKey(p.getName()))
			{
				return;
			}

			if (gp.getTeam() == Team.RED)
			{
				Random rand = new Random();
				int respawnTime = rand.nextInt((12 - 8) + 1) + 8;
				gp.setRespawnTimer(respawnTime);
			}

			else if (gp.getTeam() == Team.BLUE)
			{
				Random rand = new Random();
				int respawnTime = rand.nextInt((24 - 16) + 1) + 16;
				gp.setRespawnTimer(respawnTime);
			}
		}


		respawncounter.put(p.getName(), TF2.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable() 
		{
			int timesDone = 0;
			public void run()
			{
				if (respawncounter.containsKey(p.getName()))
				{
					if (gp.getGame().getStatus() != GameStatus.ENDING)
					{
						if (gp.getRespawnTimer() != 0)
						{
							if (timesDone == 0 || timesDone == 1)
							{
								p.setFireTicks(0);
								PlayerInventory inv = p.getInventory();
								inv.clear();
								inv.setArmorContents(null);
								p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 1));
								p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1));
								p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 3));
								p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 2000));
								p.setLevel(gp.getTotalKills());
								p.setAllowFlight(true);
								p.setFlying(true);
								p.setFlySpeed(0.2f);

								GamePlayer kgp = GameUtilities.getUtilities().getGamePlayer(k); 

								if (timesDone == 0)
								{
									if (k == null || k == p)
									{
										TitleAPI.sendTitle(p, 0, 40, 5, "&4YOU ARE DED", "&3~ Sincerely, You");
									}
									else
									{
										TitleAPI.sendTitle(p, 0, 40, 5, "&4DEAD", "&3Killed by "+kgp.getTeamColor()+k.getName()+" &e("+kgp.getTeamColor()+kgp.getCurrentClass().getName()+"&e)");
									}
								}
							}

							else if (gp.getRespawnTimer() < 4)
							{
								TitleAPI.sendTitle(p, 0, 40, 5, "&4DEAD", "&3Respawning in &c"+gp.getRespawnTimer());
							}
							else if (gp.getRespawnTimer() < 9)
							{
								TitleAPI.sendTitle(p, 0, 40, 5, "&4DEAD", "&3Respawning in &e"+gp.getRespawnTimer());
							}
							else if (gp.getRespawnTimer() < 100)
							{
								TitleAPI.sendTitle(p, 0, 40, 5, "&4DEAD", "&3Respawning in &a"+gp.getRespawnTimer());
							}

							gp.setRespawnTimer(gp.getRespawnTimer()-1);
							timesDone++;
						}
						else
						{
							// When they are going to respawn
							p.removePotionEffect(PotionEffectType.NIGHT_VISION);
							p.removePotionEffect(PotionEffectType.INVISIBILITY);
							p.removePotionEffect(PotionEffectType.SPEED);
							p.setFlySpeed(0.1f);
							p.setFlying(false);
							p.setAllowFlight(false);
							TF2.getInstance().getServer().getScheduler().cancelTask(respawncounter.get(p.getName()));
							respawncounter.remove(p.getName());
							resetClass(p, gp);
							gp.setSpawnLoc(p.getLocation());
							for(final String lp : game.getPlayersIngame())
							{
								Player p2 = Bukkit.getPlayerExact(lp);
								p2.showPlayer(p);
							}
							p.teleport(teamspawn);
							gp.setJustSpawned(true);
							gp.setIsDead(false);
							gp.setPlayerLastDamagedBy(null);
							p.setLevel(gp.getTotalKills());
							p.setFallDistance(0f);
						}
					}
					else
					{
						// if the game just ended
						TitleAPI.sendTitle(p, 0, 600, 5, "&4DEAD", "&3Respawning in wait for next round.");
						TF2.getInstance().getServer().getScheduler().cancelTask(respawncounter.get(p.getName()));
						respawncounter.remove(p.getName());
						return;
					}
				}
				else
				{
					// if their not in respawncounter for some reason
					gp.setIsDead(false);
					p.removePotionEffect(PotionEffectType.NIGHT_VISION);
					p.removePotionEffect(PotionEffectType.INVISIBILITY);
					p.removePotionEffect(PotionEffectType.SPEED);
					p.setFlySpeed(0.1f);
					p.setFlying(false);
					p.setAllowFlight(false);
					for(final String lp : game.getPlayersIngame())
					{
						Player p2 = Bukkit.getPlayerExact(lp);
						p2.showPlayer(p);
					}
					p.setFallDistance(0f);
					return;
				}
			}
		}, 20L, 20L));
	}

	public void resetClass(Player p, GamePlayer gp)
	{
		TF2Class c = gp.getCurrentClass();
		if (c == null || gp == null || respawncounter.containsKey(p.getName()) || c.toString().equalsIgnoreCase("NONE"))
		{
			Game game = gp.getGame();
			p.sendMessage(ChatColor.YELLOW+"[TF2] An internal error occured, please re select your class.");
			gp.setIsDead(false);
			p.setFlySpeed(0.1f);
			p.setFlying(false);
			p.setAllowFlight(false);
			for(final String lp : game.getPlayersIngame())
			{
				Player p2 = Bukkit.getPlayerExact(lp);
				p2.showPlayer(p);
			}

			p.performCommand("class");
		}
		c.apply(gp);
		if (gp.getCurrentClass().toString().equalsIgnoreCase("Scout"))
		{
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9000, 2), true);
		}
		else if (gp.getCurrentClass().toString().equalsIgnoreCase("Heavy"))
		{
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9000, 2), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 2, 100), true);
		}
		else if (gp.getCurrentClass().toString().equalsIgnoreCase("PyroR") ||
				gp.getCurrentClass().toString().equalsIgnoreCase("PyroB"))
		{
			p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9000, 1), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 2, 100), true);
		}
		else if (gp.getCurrentClass().getName().equalsIgnoreCase("demoman"))
		{
			p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 2, 100), true);
		}
		else if (gp.getCurrentClass().getName().equalsIgnoreCase("soldier"))
		{
			p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 2, 100), true);
		}
	}


	public void stopRespawn(GamePlayer gp) {
		if (gp.isDead())
		{
			Game game = gp.getGame();
			Player p = gp.getPlayer();
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);
			p.removePotionEffect(PotionEffectType.INVISIBILITY);
			p.removePotionEffect(PotionEffectType.SPEED);
			p.setFlySpeed(0.1f);
			p.setFlying(false);
			p.setAllowFlight(false);
			TF2.getInstance().getServer().getScheduler().cancelTask(respawncounter.get(p.getName()));
			respawncounter.remove(p.getName());
			gp.setSpawnLoc(p.getLocation());
			for(final String lp : game.getPlayersIngame())
			{
				Player p2 = Bukkit.getPlayerExact(lp);
				p2.showPlayer(p);
			}
			gp.setJustSpawned(false);
			gp.setIsDead(false);
			gp.setPlayerLastDamagedBy(null);
			p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 100, 2));
			p.setFallDistance(0f);
		}
	}

	public void cancelRespawn (GamePlayer gp)
	{
		Player p = gp.getPlayer();
		p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
		p.removePotionEffect(PotionEffectType.SPEED);
		p.setFlySpeed(0.1f);
		p.setFlying(false);
		p.setAllowFlight(false);
		p.setFallDistance(0f);
		
		for(GamePlayer gp2 : gp.getGame().playersInGame.values())
		{
			Player p2 = gp2.getPlayer();
			p2.showPlayer(gp.getPlayer());
		}

		if (respawncounter.containsKey(gp.getName()))
		{
			TF2.getInstance().getServer().getScheduler().cancelTask(respawncounter.get(p.getName()));
			respawncounter.remove(p.getName());
		}
	}

	public void respawn (final GamePlayer gp, boolean keepTime)
	{
		final Player p = gp.getPlayer();

		final Game game = gp.getGame();

		for (GamePlayer gp2 : game.playersInGame.values())
		{
			Player p2 = gp2.getPlayer();
			p2.hidePlayer(p);
		}

		PlayerInventory inv = p.getInventory();
		inv.clear();
		inv.setArmorContents(null);
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 1));
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 3));
		p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 2000));
		p.setLevel(gp.getTotalKills());
		p.setAllowFlight(true);
		p.setFlying(true);
		p.setFlySpeed(0.2f);
		p.setFallDistance(0f);
		
		Location teamspawn1 = null;

		if (gp.getTeam() == Team.RED)
		{
			teamspawn1 = gp.getGame().getMap().getRedSpawn();
		}
		else
		{
			teamspawn1 = gp.getGame().getMap().getBlueSpawn();
		}

		gp.setIsDead(true);

		final Location teamspawn = teamspawn1;

		if (!keepTime)
		{
			if (gp.getTeam() == Team.RED)
			{
				Random rand = new Random();
				int respawnTime = rand.nextInt((12 - 8) + 1) + 8;
				gp.setRespawnTimer(respawnTime);
			}
			else if (gp.getTeam() == Team.BLUE)
			{
				Random rand = new Random();
				int respawnTime = rand.nextInt((24 - 16) + 1) + 16;
				gp.setRespawnTimer(respawnTime);
			}
		}


		respawncounter.put(p.getName(), TF2.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable() 
		{
			public void run()
			{
				if (respawncounter.containsKey(p.getName()))
				{
					if (gp.getRespawnTimer() != 50)
					{
						if (gp.getRespawnTimer() != 0)
						{

							if (gp.getRespawnTimer() < 4)
							{
								TitleAPI.sendTitle(p, 0, 40, 5, "&4DEAD", "&3Respawning in &c"+gp.getRespawnTimer());
							}
							else if (gp.getRespawnTimer() < 9)
							{
								TitleAPI.sendTitle(p, 0, 40, 5, "&4DEAD", "&3Respawning in &e"+gp.getRespawnTimer());
							}
							else if (gp.getRespawnTimer() < 100)
							{
								TitleAPI.sendTitle(p, 0, 40, 5, "&4DEAD", "&3Respawning in &a"+gp.getRespawnTimer());
							}

							gp.setRespawnTimer(gp.getRespawnTimer()-1);
						}
						else
						{
							TitleAPI.sendTitle(p, 0, 40, 0, "&4DEAD", "&3Prepare to respawn.");

							// When they are going to respawn
							p.removePotionEffect(PotionEffectType.NIGHT_VISION);
							p.removePotionEffect(PotionEffectType.INVISIBILITY);
							p.removePotionEffect(PotionEffectType.SPEED);
							p.setFlySpeed(0.1f);
							p.setFlying(false);
							p.setAllowFlight(false);
							TF2.getInstance().getServer().getScheduler().cancelTask(respawncounter.get(p.getName()));
							respawncounter.remove(p.getName());
							resetClass(p, gp);
							gp.setSpawnLoc(p.getLocation());
							for(final String lp : game.getPlayersIngame())
							{
								Player p2 = Bukkit.getPlayerExact(lp);
								p2.showPlayer(p);
							}
							p.teleport(teamspawn);
							gp.setJustSpawned(true);
							gp.setIsDead(false);
							gp.setPlayerLastDamagedBy(null);
							p.setLevel(gp.getTotalKills());
							p.setFallDistance(0f);

							TitleAPI.sendTitle(p, 0, 1, 0, "&4DEAD", "&3Prepare to respawn.");
						}
					}
					else
					{
						// if the game just ended
						TitleAPI.sendTitle(p, 0, 600, 5, "&4DEAD", "&3Respawning in wait for next round.");
						gp.setIsDead(false);
						gp.setPlayerLastDamagedBy(null);
						p.removePotionEffect(PotionEffectType.NIGHT_VISION);
						p.removePotionEffect(PotionEffectType.INVISIBILITY);
						p.removePotionEffect(PotionEffectType.SPEED);
						p.setFlySpeed(0.1f);
						p.setFlying(false);
						p.setAllowFlight(false);
						TF2.getInstance().getServer().getScheduler().cancelTask(respawncounter.get(p.getName()));
						respawncounter.remove(p.getName());
						p.setFallDistance(0f);
						return;
					}
				}
				else
				{
					// if their not in respawncounter for some reason
					gp.setIsDead(false);
					p.removePotionEffect(PotionEffectType.NIGHT_VISION);
					p.removePotionEffect(PotionEffectType.INVISIBILITY);
					p.removePotionEffect(PotionEffectType.SPEED);
					p.setFlySpeed(0.1f);
					p.setFlying(false);
					p.setAllowFlight(false);
					for(final String lp : game.getPlayersIngame())
					{
						Player p2 = Bukkit.getPlayerExact(lp);
						p2.showPlayer(p);
					}
					return;
				}
			}
		}, 20L, 20L));
	}
}
