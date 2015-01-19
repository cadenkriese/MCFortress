package com.chaseoes.tf2.capturepoints;

import java.util.ArrayList;

import com.chaseoes.tf2.localization.Localizers;
import com.chaseoes.tf2.sound.TFSound;
import com.chaseoes.tf2.utilities.FireworkUtilities;
import com.chaseoes.tf2.utilities.GeneralUtilities;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import com.chaseoes.tf2.Game;
import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.Team;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.connorlinfoot.titleapi.TitleAPI;

public class CapturePoint implements Comparable<CapturePoint> {

	String map;
	Integer id;
	Location location;
	Integer task = 0;
	Integer ptask = 0;
	ArrayList<Player> cappers = new ArrayList<Player>();
	CaptureStatus status;
	double capSpeed = 1;
	public GamePlayer capturing;

	public CapturePoint(String map, Integer i, Location loc) {
		capturing = null;
		setStatus(CaptureStatus.UNCAPTURED);
		id = i;
		this.map = map;
		location = loc;
	}

	public Integer getId() { 
		return id;
	}

	public Location getLocation() {
		return location;
	}

	public CaptureStatus getStatus() {
		return status;
	}

	public void setStatus(CaptureStatus s) {
		status = s;
	}

	public ArrayList<Player> getCappers() {
		return cappers;
	}

	public void addCappers(Player p) {
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		if (cappers.contains(p) || capturing.getName().equals(p.getName()))
		{
			return;
		}
		if (gp.isIngame() && gp.getCurrentClass().getName().equalsIgnoreCase("scout"))
		{
			capSpeed = capSpeed+0.25;
		}
		capSpeed = capSpeed+0.50;
		cappers.add(p);
	}
	public void removeCappers(Player p)
	{
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		if (cappers.size() == 0 && capturing.getName().equals(p.getName()))
		{
			capSpeed = 0;
			stopCapturing();
			return;
		}
		else if (capturing.getName().equals(p.getName()))
		{
			capturing = GameUtilities.getUtilities().getGamePlayer(cappers.get(0));
			cappers.remove(0);
			if (gp.getClass().getName().equalsIgnoreCase("scout"))
			{
				capSpeed = capSpeed-0.75;
				return;
			}
			capSpeed = capSpeed-0.50;
			return;
		}
		if (gp.getClass().getName().equalsIgnoreCase("scout"))
		{
			cappers.remove(p);
			capSpeed = capSpeed-0.75;
			return;
		}
		cappers.remove(p);
		capSpeed = capSpeed-0.5;
	}

	public void startCapturing(final GamePlayer player) {
		capturing = player;
		setStatus(CaptureStatus.CAPTURING);
		Game game = capturing.getGame();
		game.broadcast(Localizers.getDefaultLoc().CP_BEING_CAPTURED.getPrefixedString(id));

		if (player.isDead())
		{
			stopCapturing();
			return;
		}
		if (player.getCurrentClass().getName().equalsIgnoreCase("scout"))
		{
			capSpeed = capSpeed+0.25;
		}
		capSpeed++;
		player.setOnCP(true);


		task = CapturePointUtilities.getUtilities().plugin.getServer().getScheduler().scheduleSyncRepeatingTask(CapturePointUtilities.getUtilities().plugin, new Runnable() {
			double timeRemaining = CapturePointUtilities.getUtilities().plugin.getConfig().getInt("capture-timer");
			double timeTotal = CapturePointUtilities.getUtilities().plugin.getConfig().getInt("capture-timer");
			Game game = capturing.getGame();
			int currentTick = 0;

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				double diff = capSpeed / (CapturePointUtilities.getUtilities().plugin.getConfig().getInt("capture-timer") * 20);
				if (capSpeed-1 == 0.0d)
				{
					diff = 1.0d / (20 * 20);
				}
				game.setExpOfPlayers(diff * currentTick);
				if (timeRemaining > 0 && currentTick % 20 == 0) {
					if (TF2.getInstance().getConfig().getBoolean("lightning-while-capturing")) {
						player.getPlayer().getWorld().strikeLightningEffect(player.getPlayer().getLocation());
					}
				}
				if (timeRemaining <= 0.0d) {
					if (cappers.size() != 0)
					{
						for (int playernum = 0; playernum<=cappers.size(); playernum++)
						{
							Player p = cappers.get(playernum);
							GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
							gp.setPointsCaptured(gp.getPointsCaptured()+1);
							gp.setOnCP(false);
						}
					}
					for (final String gp : game.getPlayersIngame()) {
						final Player player = Bukkit.getPlayerExact(gp);
						if (player == null) {
							continue;
						}
						TF2.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable() {
							@Override
							public void run() {
								player.playSound(player.getLocation(), Sound.ANVIL_LAND, 10000, 1);
							}
						}, 1L);
					}
					stopCapturing();
					setStatus(CaptureStatus.CAPTURED);
					TFSound.SCORED.send(location, game, 1f, 1000000f);
					if (id == 1)
					{
						for(final String lp : game.getPlayersIngame()){
							final Player ps = Bukkit.getPlayerExact(lp);
							TitleAPI.sendTitle(ps,10,40,10,"&cCapture Point 1","&7Has been captured.");
							ActionBarAPI.sendActionBar(ps, ChatColor.GREEN+"+ 5:00");
							game.addTimeLeft();
							GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(ps);
							if (gp.getTeam() == Team.BLUE)
							{
								ps.playSound(ps.getLocation(), Sound.SHEEP_SHEAR, 10000, 1);
							}
						}
						Location newLoc = new Location(location.getWorld(), location.getX(), location.getY()-1, location.getZ()-1);
						int radius = 4;
						for (int x = -(radius); x <= radius; x++)
						{
							for (int y = -(radius); y <= radius; y++)
							{
								for (int z = -(radius); z <= radius; z++)
								{
									Location loc = location.getBlock().getRelative(x, y, z).getLocation();
									if (loc.getBlock().getType() == Material.STAINED_GLASS)
									{
										loc.getBlock().setData((byte) 14);
									}
								}
							}
						}
						Firework fw = (Firework) newLoc.getWorld().spawn(newLoc, Firework.class);
						FireworkMeta fm = fw.getFireworkMeta();
						FireworkEffect effect = FireworkEffect.builder()
								.flicker(true)
								.trail(false)
								.with(Type.BALL_LARGE)
								.withColor(Color.RED)
								.build();
						fm.clearEffects();
						fm.addEffect(effect);
						fm.setPower(3);
						fw.setFireworkMeta(fm);
						FireworkUtilities.detonateInstantly(fw);
					}
					else
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
										loc.getBlock().setData((byte) 14);
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
										fm.setPower(3);
										fw.setFireworkMeta(fm);
										FireworkUtilities.detonateInstantly(fw);
									}
								}
							}
						}
					}
					player.setPointsCaptured(-1);
					game.broadcast(Localizers.getDefaultLoc().CP_CAPTURED.getPrefixedString(id, player.getName()));
					game.setExpOfPlayers(0);

					GeneralUtilities.runCommands("on-point-capture", player.getPlayer(), player.getPlayer(), game.getMap());

					if (TF2.getInstance().getMap(map).allCaptured()) {
						game.winMatch(Team.RED);
						return;
					}
				}
				currentTick++;
				if (currentTick % 20 == 0) 
				{
					timeRemaining = timeRemaining-capSpeed;
					timeTotal = timeTotal+capSpeed;
				}
			}
		}, 0L, 1L);

		ptask = CapturePointUtilities.getUtilities().plugin.getServer().getScheduler().scheduleSyncRepeatingTask(CapturePointUtilities.getUtilities().plugin, new Runnable() {
			@Override
			public void run() {
				GamePlayer p = capturing;
				if (p == null) {
					stopCapturing();
					return;
				}

				if (!p.isOnCP() || p.isDead() || p.isInvis()) {
					removeCappers(p.getPlayer());
					return;
				}

				if (cappers.size() != 0)
				{
					for (int playernum = 0; playernum < cappers.size(); playernum++)
					{
						GamePlayer capGp = GameUtilities.getUtilities().getGamePlayer(cappers.get(playernum));
						if (!capGp.isOnCP() || capGp.isDead() || capGp.isInvis())
						{
							removeCappers(capGp.getPlayer());
						}
					}
				}
			}
		}, 0L, 1L);
	}

	public void stopCapturing() {
		if (ptask != 0) {
			Bukkit.getScheduler().cancelTask(ptask);
			ptask = 0;
		}
		if (task != 0) {
			Bukkit.getScheduler().cancelTask(task);
			task = 0;
		}

		if (capturing != null && capturing.getGame() != null) {
			capturing.getGame().setExpOfPlayers(0d);
			capturing = null;
		}
		capSpeed = 0;
		setStatus(CaptureStatus.UNCAPTURED);
	}

	@Override
	public int compareTo(CapturePoint o) {
		return this.getId() - o.getId();
	}
}
