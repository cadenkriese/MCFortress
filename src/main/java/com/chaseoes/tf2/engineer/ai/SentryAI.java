package com.chaseoes.tf2.engineer.ai;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.engineer.BuildType;
import com.chaseoes.tf2.engineer.Building;
import com.chaseoes.tf2.particles.ParticleEffect;
import com.chaseoes.tf2.sound.TFSound;

public class SentryAI 
implements Listener
{
	ArrayList<Building> activeBuildings = new ArrayList<Building>();

	public void addBuilding(Building b)
	{
		if (b.getType() == BuildType.SENTRY)
		{
			activeBuildings.add(b);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		final Player target = e.getPlayer();
		GamePlayer tp = GameUtilities.getUtilities().getGamePlayer(target);

		if (tp.isIngame() && tp.getGame().redHasBeenTeleported)
		{
			for (final Building b : activeBuildings)
			{
				Player attacking = b.getTarget();
				final GamePlayer gp = b.getOwner();
				if (target.getLocation().distance(b.getSource()) <= 8)
				{
					if (gp.getEnemyMebers().contains(tp))
					{
						if (tp.getDisguise() == null)
						{
							if (b.isActive())
							{
								if (attacking != null)
								{
									if (attacking.getLocation().distance(b.getSource()) > target.getLocation().distance(b.getSource()))
									{
										TFSound.SENTRY_MOVE_SHORT.send(b.getSource(), gp.getGame(), 1, 1);
										new BukkitRunnable()
										{
											@Override
											public void run() 
											{
												b.setTarget(target);
											}
										}.runTaskLater(TF2.getInstance(), 10L);
									}
								}
								else
								{
									TFSound.SENTRY_SPOT.send(b.getSource(), gp.getGame(), 1, 1);
									TFSound.SENTRY_MOVE_SHORT.sendDelayed(b.getSource(), gp.getGame(), 1, 1, 5L);
									attackPlayer(b);
								}
							}
						}
					}
				}
			}
		}
	}

	private void attackPlayer(final Building b)
	{
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				Player p = b.getTarget();
				if (p.getLocation().distance(b.getSource()) <= 8)
				{
					if (p.getLocation().getBlockX() == b.getSource().getBlockX() && p.getLocation().getBlockZ() == b.getSource().getBlockZ())
					{
						Random rand = new Random();
						if (rand.nextInt(100) == 1)
						{
							TFSound.SENTRY_MOVE_SHORT.send(b.getSource(), b.getOwner().getGame(), 1f, 1f);
						}
					}
					else
					{
						TFSound.SENTRY_SHOOT.send(b.getSource(), b.getOwner().getGame(), 1f, 1f);
						p.damage(2.56d);
						ParticleEffect.FLAME.display(0f, 0.3f, 0.3f, 1, 5, b.getSource().add(0, 0, 0), 20);
					}
				}
				else
				{
					this.cancel();
					return;
				}
			}

		}.runTaskTimer(TF2.getInstance(), 10L, 5L);
	}
}
