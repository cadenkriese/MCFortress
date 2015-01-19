package com.chaseoes.tf2.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.Team;
import com.chaseoes.tf2.particles.ParticleEffect;

public class PlayerToggleFlightListener implements Listener
{

	double gameVelocity = 0.8;
	double lobbyVelocity = 1.3;

	int volume = 10;
	int pitch = 1;

	@EventHandler
	public void onPlayerFlightAttempt(PlayerToggleFlightEvent event) 
	{

		Player player = event.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(player);

		if (player.getGameMode() == GameMode.CREATIVE || gp.isDead()) {
			return;
		}

		event.setCancelled(true);
		player.setFlying(false);
		player.setAllowFlight(false);
		if (gp.isIngame())
		{
			if (gp.getCurrentClass() == null || !gp.getCurrentClass().getName().equalsIgnoreCase("scout"))
			{
				return;
			}
			if (gp.getTeam() == Team.RED)
			{
				if (!gp.getGame().redHasBeenTeleported)
				{
					return;
				}
			}
			if (gp.getCurrentClass().getName().equalsIgnoreCase("Scout"))
			{
				player.setVelocity(player.getLocation().getDirection().multiply(gameVelocity));
				ParticleEffect.CLOUD.display(0, 0, 0, 0.1f, 3, player.getLocation(), 40);
				return;
			}
		}
		if (player.hasPermission("tf2.rank.vip"))
		{
			player.setVelocity(player.getLocation().getDirection().multiply(lobbyVelocity));
			player.playSound(player.getLocation(), Sound.BLAZE_HIT, 100, 1);
			ParticleEffect.CLOUD.display(0, 0.4f, 0, 0.1f, 3, player.getLocation(), 40);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) 
	{

		Player player = event.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(player);

		if (player.getGameMode() == GameMode.CREATIVE) 
		{
			return;
		}
		if (gp.isIngame())
		{
			if (gp.getCurrentClass() != null)
			{
				if (!gp.getCurrentClass().getName().equalsIgnoreCase("scout") && !gp.isDead())
				{
					if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) 
					{
						player.setAllowFlight(false);
						return;
					}
				}
				if (gp.isDead() || (gp.getTeam() == Team.RED && !gp.getGame().redHasBeenTeleported)) 
				{
					return;
				}
			}
		}

		if (player.getGameMode() != GameMode.CREATIVE) 
		{
			if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) 
			{
				player.setAllowFlight(true);
			}
		}
	}
}
