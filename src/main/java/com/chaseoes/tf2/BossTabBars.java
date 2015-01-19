package com.chaseoes.tf2;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.server.v1_8_R3.EntityPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBarAPI;

import com.chaseoes.tf2.capturepoints.CaptureStatus;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.connorlinfoot.titleapi.TitleAPI;

public class BossTabBars {
	public HashMap<String, Integer> bossBarMap = new HashMap<String, Integer>();
	public HashMap<Player, Integer> tabMap =  new HashMap<Player, Integer>();

	static BossTabBars instance = new BossTabBars();
	public static BossTabBars getBars() {
		return instance;
	}

	public void startGameBossBar(final GamePlayer gp, final Map map)
	{
		bossBarMap.put(gp.getPlayer().getName()+"_"+map.getName(), TF2.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				if (gp.isIngame() && bossBarMap.containsKey(gp.getPlayer().getName()+"_"+map.getName()) && map.getName().equalsIgnoreCase(TF2.getInstance().getADmap()))
				{
					Player p = gp.getPlayer();
					Game game = GameUtilities.getUtilities().getGame(map);



					if (game.redHasBeenTeleported)
					{
						String message = "";

						if (map.getCapturePoint(1).getStatus() == CaptureStatus.CAPTURED && map.getCapturePoint(2).getStatus() == CaptureStatus.CAPTURED)
						{
							message = ChatColor.RED+"RED "+ChatColor.GRAY+"Team Seizes Area!";
						}
						else if (map.getCapturePoint(1).getStatus() == CaptureStatus.CAPTURED && map.getCapturePoint(2).getStatus() == CaptureStatus.CAPTURING)
						{
							message = ChatColor.DARK_GRAY.toString()+ChatColor.BOLD.toString()+"CP1: " +ChatColor.DARK_RED+"Captured   "+ChatColor.DARK_GRAY.toString()+ChatColor.BOLD.toString()+"CP2: " +ChatColor.RED+"Capturing ";
						}
						else if (map.getCapturePoint(1).getStatus() == CaptureStatus.CAPTURED && map.getCapturePoint(2).getStatus() == CaptureStatus.UNCAPTURED)
						{
							message = ChatColor.DARK_GRAY.toString()+ChatColor.BOLD.toString()+"CP1: " +ChatColor.DARK_RED+"Captured   "+ChatColor.DARK_GRAY.toString()+ChatColor.BOLD.toString()+"CP2: " +ChatColor.BLUE+"Uncaptured";
						}
						else if (map.getCapturePoint(1).getStatus() == CaptureStatus.CAPTURING && map.getCapturePoint(2).getStatus() == CaptureStatus.UNCAPTURED)
						{
							message = ChatColor.DARK_GRAY.toString()+ChatColor.BOLD.toString()+"CP1: " +ChatColor.RED+"Capturing   "+ChatColor.DARK_GRAY.toString()+ChatColor.BOLD.toString()+"CP2: " +ChatColor.BLUE+"Uncaptured";
						}
						else if (map.getCapturePoint(1).getStatus() == CaptureStatus.UNCAPTURED && map.getCapturePoint(2).getStatus() == CaptureStatus.UNCAPTURED)
						{
							message = ChatColor.DARK_GRAY.toString()+ChatColor.BOLD.toString()+"CP1: " +ChatColor.BLUE+"Uncaptured   "+ChatColor.DARK_GRAY.toString()+ChatColor.BOLD.toString()+"CP2: " +ChatColor.BLUE+"Uncaptured";
						}
						else
						{
							message = ChatColor.RED+"Error, contact an admin.";
						}
						BossBarAPI.setMessage(p, message);
					}
				}
				else
				{
					stopGameBossBar(map.getName(), gp.getPlayer());
				}
			}
		}, 0L, 20L));
	}
	public void stopGameBossBar(String map, Player p)
	{
		if (!(bossBarMap.get(p.getName()+"_"+map) == null)) 
		{
			TF2.getInstance().getServer().getScheduler().cancelTask(bossBarMap.get(p.getName()+"_"+map));
			bossBarMap.remove(p.getName()+"_"+map);
			ActionBarAPI.sendActionBar(p, ChatColor.RED+"");
		}
	}

	public void startTabAnimation(final Player p)
	{
		final ArrayList<String> frames = new ArrayList<String>();
		frames.add("&f&lM&6&lC-FORTRESS");
		frames.add("&e&lM&f&lC&6&l-FORTRESS");
		frames.add("&6&lM&e&lC&f&l-&6&lFORTRESS");
		frames.add("&6&lMC&e&l-&f&l&6&lFORTRESS");
		frames.add("&6&lMC-&e&lF&f&lO&6&lRTRESS");
		frames.add("&6&lMC-F&e&lO&f&lR&6&lTRESS");
		frames.add("&6&lMC-FO&e&lR&f&lT&6&lRESS");
		frames.add("&6&lMC-FOR&e&lT&f&lR&6&lESS");
		frames.add("&6&lMC-FORT&e&lR&f&lE&6&lSS");
		frames.add("&6&lMC-FORTR&e&lE&f&lS&6&lS");
		frames.add("&6&lMC-FORTRE&e&lS&f&lS");
		frames.add("&6&lMC-FORTRES&e&lS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");
		frames.add("&6&lMC-FORTRESS");

		if (!tabMap.containsKey(p))
		{
			
			tabMap.put(p, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
			{
				int frame = 0;
				@Override
				public void run()
				{
					GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
					String header = "";
					String footer = "";
					
					if (!gp.isIngame())
					{
					header = ChatColor.translateAlternateColorCodes('&', "&m&l-|-------------------------|-"
							+ "\n"+frames.get(frame)
							+ "\n&7&oTeam Fortress 2 in Minecraft"
							+ "\n&8play.mc-fort.net"
							+ "\n"
							+ "\n&6&lPing: &f&l"+getPing(p));
					
					footer = ChatColor.translateAlternateColorCodes('&', ""
							+ "\n&6&lWebsite: &fhttp://mc-fort.net"
							+ "\n&6&lWiki: &fhttp://wiki.mc-fort.net/"
							+ "\n&6&lStore: &fhttp://shop.mc-fort.net/"
							+ "\n&m&l-|-------------------------|-");
					}
					else
					{
						header = ChatColor.translateAlternateColorCodes('&', "&m&l-|-------------------------|-"
								+ "\n"+frames.get(frame)
								+ "\n&7&oTeam Fortress 2 in Minecraft"
								+ "\n&8play.mc-fort.net"
								+ "\n"
								+ "\n&6&lMap: &f"+gp.getGame().getMapName()
								+ "\n"
								+ "\n&6&lPlayers: &e"+gp.getGame().playersInGame.size()+"&f&l/&a24 &8&l| &6&lPing: &f&l"+getPing(p));
						
						footer = ChatColor.translateAlternateColorCodes('&', ""
								+ "\n&6&lGame Status: "+getGameStatus(gp)
								+ "\n&f&m&l-|-------------------------|-");
					}
					
					
					if (frame == frames.size()-1)
					{
						frame = 0;
					}
					else
					{
						frame++;
					}
					
					TitleAPI.sendTabTitle(p, header, footer);
				}
			}, 0L, 2L));
		}
	}

	public void stopTabAnimation(Player p)
	{
		if (tabMap.containsKey(p))
		{
			Bukkit.getServer().getScheduler().cancelTask(tabMap.get(p));
			tabMap.remove(p);
		}
	}
	
	
	public int getPing(Player p) { CraftPlayer cp = (CraftPlayer) p; EntityPlayer ep = cp.getHandle(); return ep.ping; }
	
	private String getGameStatus(GamePlayer gp)
	{
		if (gp.getGame().getStatus() == GameStatus.WAITING)
		{
			return ChatColor.YELLOW+"Waiting For Players...";
		}
		else if (gp.getGame().getStatus() == GameStatus.STARTING || !gp.getGame().redHasBeenTeleported)
		{
			return ChatColor.GREEN+"Starting";
		}
		else if (gp.getGame().getStatus() == GameStatus.INGAME)
		{
			return ChatColor.translateAlternateColorCodes('&', "&aIngame\n&6&lTime Left: &e"+gp.getGame().getTimeLeftPretty());
		}
		else if (gp.getGame().getStatus() == GameStatus.ENDING)
		{
			return ChatColor.RED+"Ending";
		}
		else
		{
			return ChatColor.DARK_RED+ChatColor.BOLD.toString()+"NULL";
		}
	}
}
