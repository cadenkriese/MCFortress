package com.chaseoes.tf2.listeners;

import com.chaseoes.tf2.localization.Localizers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.Map;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.Team;
import com.chaseoes.tf2.capturepoints.CapturePointUtilities;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class PlayerMoveListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent event) 
	{
		if (!(event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())) {
			return;
		}
		if (GameUtilities.getUtilities().getGamePlayer(event.getPlayer()).isInLobby() || !GameUtilities.getUtilities().getGamePlayer(event.getPlayer()).isIngame()) {
			return;
		}

		Player player = event.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(player);
		
		Location l = event.getTo();
		if (gp.isDead())
		{
			Map m = gp.getGame().getMap();
			Selection sel = new CuboidSelection(m.getP1().getWorld(), m.getP1(), m.getP2());
			
			if (!sel.contains(player.getLocation()))
			{
				Location loc = m.getRedSpawn();
				player.teleport(loc);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You need to stay inside of the map!"));
			}
		}

		// Capture Points
		if (gp.isIngame())
		{
			if (gp.isOnCP())
			{
				Location FloorBlock = new Location(gp.getPlayer().getWorld(), l.getX(), l.getY()-1, l.getZ());
				if (FloorBlock.getBlock().getType() == Material.STAINED_CLAY || (FloorBlock.getBlock().getType() != Material.STAINED_GLASS && FloorBlock.getBlock().getData() != (byte) 11 || FloorBlock.getBlock().getData() != (byte) 14))
				{
					gp.setOnCP(false);
				}
				else if (FloorBlock.getBlock().getType() == Material.STAINED_CLAY || (FloorBlock.getBlock().getType() == Material.STAINED_GLASS && FloorBlock.getBlock().getData() == (byte) 11 || FloorBlock.getBlock().getData() == (byte) 14))
				{
					return;
				}
			}
		}
		if (gp.isIngame() && !gp.isOnCP())
		{
			int radius = 5;
			for (int x = -(radius); x <= radius; x++)
			{
				for (int y = -(radius); y <= radius; y++)
				{
					for (int z = -(radius); z <= radius; z++)
					{
						Location loc = l.getBlock().getRelative(x, y, z).getLocation();
						Location FloorBlock = new Location(player.getWorld(), l.getX(), l.getY()-1, l.getZ());
						Map map = gp.getGame().getMap();
						//Doing this because adding them in one if statemen't dosen't seem to work.
						Boolean p1 = false;
						Boolean p2 = false;
						if (CapturePointUtilities.getUtilities().locationIsCapturePoint(loc))
						{
							if (!gp.isOnCP())
							{
								p1 = true;
							}
						}
						if (FloorBlock.getBlock().getType() == Material.STAINED_CLAY || (FloorBlock.getBlock().getType() == Material.STAINED_GLASS && FloorBlock.getBlock().getData() == (byte) 11 || FloorBlock.getBlock().getData() == (byte) 14))
						{
							p2 = true;
						}
						if (p1 && p2)
						{

							if (gp.isInvis())
							{
								ActionBarAPI.sendActionBar(player, ChatColor.DARK_RED+"You cannot capture points while cloaked!");
								return;
							}
							else if (gp.isUbered() || gp.isUbering())
							{
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You cannot captuer points while ÜberCharged!"));
								return;
							}
							gp.setOnCP(true);
							beginCap(gp, map, player, loc.getBlock());
						}
					}

				}

			}
		}
	}
	public void beginCap(GamePlayer gp, Map map, Player p, Block b)
	{
		Integer id = CapturePointUtilities.getUtilities().getIDFromLocation(b.getLocation());
		com.chaseoes.tf2.capturepoints.CapturePoint cp = map.getCapturePoint(id);
		if (gp.getTeam() == Team.RED) 
		{
			if (cp.getStatus().toString().equalsIgnoreCase("uncaptured") || cp.getStatus().toString().equalsIgnoreCase("capturing"))
			{
				if (TF2.getInstance().getConfig().getBoolean("capture-in-order")) 
				{
					if (CapturePointUtilities.getUtilities().capturePointBeforeHasBeenCaptured(map, id)) 
					{
						if (cp.capturing == null) 
						{
							cp.startCapturing(gp);
							return;
						}
						else
						{
							cp.addCappers(p);
							return;
						}
					} 
					else 
					{
						Localizers.getDefaultLoc().CP_MUST_CAPTURE_PREVIOUS.sendPrefixed(p, CapturePointUtilities.getUtilities().getFirstUncaptured(map).getId());
					}
				} 
				else 
				{
					if (cp.capturing == null) 
					{
						cp.startCapturing(gp);
						return;
					}
					else
					{
						cp.addCappers(p);
						return;
					}
				}
			} 
			else if (map.getCapturePoint(id).getStatus().string().equalsIgnoreCase("captured")) 
			{
				Localizers.getDefaultLoc().CP_ALREADY_CAPTURED_RED.sendPrefixed(p, id + 1);
			} else if (map.getCapturePoint(id).getStatus().string().equalsIgnoreCase("capturing")) {
				return;
			}
		} 
		else 
		{
			if (!map.getCapturePoint(id).getStatus().string().equalsIgnoreCase("captured")) 
			{
				Localizers.getDefaultLoc().CP_WRONG_TEAM.sendPrefixed(p);
			}
			else 
			{
				Localizers.getDefaultLoc().CP_ALREADY_CAPTURED_BLUE.sendPrefixed(p);
			}
		}
	}
}
