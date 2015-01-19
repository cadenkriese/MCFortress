package com.chaseoes.tf2.extras;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.boosters.BoosterStatusFile;
import com.chaseoes.tf2.classes.ClassChest;
import com.chaseoes.tf2.listeners.PlayerInteractListener;
import com.chaseoes.tf2.particles.ParticleEffect;
import com.chaseoes.tf2.utilities.AmmoUtilities;
import com.chaseoes.tf2.utilities.LocationIterator;
import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class FlameThrowerHandler 
{
	public static FlameThrowerHandler instance = new FlameThrowerHandler();
	int damage = 1;

	private FlameThrowerHandler(){}

	public static FlameThrowerHandler getHandler()
	{
		return instance;
	}

	@SuppressWarnings({ "unused" })
	public void fire(GamePlayer gp, Action a)
	{
		gp.setCritMessage("");
		if (!gp.isIngame() || gp.isInLobby())
		{
			ActionBarAPI.sendActionBar(gp.getPlayer(), ChatColor.RED+"You can't shoot right now!");
			return;
		}
		Player player = gp.getPlayer();
		player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1, 1);
		player.playSound(player.getLocation(), Sound.FIRE, 1, 1);
		Location origin = player.getLocation().add(0, 1, 0);

		if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)
		{
			for (int i = 1; i <= 4 /* range */; i++) 
			{
			   Location currentLoc = origin.clone().add(player.getLocation().getDirection().normalize().multiply(i));
			   ParticleEffect.FLAME.display(0.5F, 0.5F, 0.5F, 0.05F, 4, currentLoc, 400D);
			}
			
			LocationIterator iterator = new LocationIterator(player.getWorld(), player.getEyeLocation().toVector(), player.getEyeLocation().getDirection(), 0.0, 4);
			block0 : while (iterator.hasNext()) 
			{
				Location location = iterator.next();
				for (final GamePlayer enemy : gp.getEnemyMebers()) 
				{
					if (enemy.isDead() || enemy.getPlayer().getLocation().distance(location) > 1.5) continue;
					if (enemy.getCurrentClass().getName().equalsIgnoreCase("spy"))
					{
						if (enemy.isInvis())
						{
							PlayerInteractListener.stopInvis(enemy.getPlayer());
							ActionBarAPI.sendActionBar(player, ChatColor.AQUA+"You uncloaked "+ChatColor.YELLOW+enemy.getName());
							ActionBarAPI.sendActionBar(player, ChatColor.AQUA+"You were uncloaked by "+ChatColor.YELLOW+player.getName());
							gp.addGameCredits(25);
						}
					}
					enemy.setPlayerLastDamagedBy(gp);
					enemy.getPlayer().damage(0.5);
					if (!enemy.getCurrentClass().getName().equalsIgnoreCase("pyro"))
					{
						enemy.getPlayer().setFireTicks(200);	
					}
				}
			}
		}
		else if (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK)
		{
			ClassChest chest = new ClassChest(gp.getCurrentClass().getName());
			ItemStack is = chest.getItemFromSlot(18);

			if (!AmmoUtilities.getUtilities().subractAmmo(gp, 20, is, gp.getPlayer().getItemInHand()))
			{
				ActionBarAPI.sendActionBar(player, ChatColor.RED+"No ammo!");
				return;
			}

			player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1, 1);
			LocationIterator iterator = new LocationIterator(player.getWorld(), player.getEyeLocation().toVector(), player.getEyeLocation().getDirection(), 0.0, 6);
			while (iterator.hasNext())
			{
				Location location = iterator.next();
				ParticleEffect.SMOKE_NORMAL.display(1, 1, 1, 0.03f, 10, location, 20);
				for (GamePlayer other : gp.getGame().playersInGame.values()) 
				{
					if (other.getPlayer().getLocation().distance(location) > 2.2 || other == gp) continue;
					if (other.isDead()) continue;
					if (other.getTeam() == gp.getTeam()) 
					{
						
						if (other.getPlayer().getFireTicks() > 0) 
						{
							other.getPlayer().setFireTicks(0);
							ActionBarAPI.sendActionBar(other.getPlayer(), ChatColor.AQUA+"Extenguished by "+ChatColor.YELLOW+player.getName());
							ActionBarAPI.sendActionBar(player, ChatColor.AQUA+"Extenguished "+ChatColor.YELLOW+other.getName());
							player.sendMessage(ChatColor.GRAY+"(+"+25*BoosterStatusFile.getFile().getBoost()+" Credits)"+BoosterStatusFile.getFile().getBoosterMessage());
							gp.addGameCredits(25);
						}
						return;
					}
					Player target = other.getPlayer();
					Location explodeCenter = player.getLocation();
					Float power = 0.3f;
					Entity targetEntity = target;
					if (!targetEntity.isOnGround())
					{
						power = 0.2f;
					}
					else
					{
						power = 0.5f;
					}
					other.getPlayer().setVelocity(new Vector(target.getLocation().getX() - explodeCenter.getX(), target.getLocation().getZ() - explodeCenter.getZ(), target.getLocation().getZ() - explodeCenter.getZ()).multiply(power));
					target.playSound(target.getLocation(), Sound.HURT_FLESH, 1, 1);
				}
				for (Entity e : gp.getGame().getMap().getP1().getWorld().getEntities()) 
				{
					if (!(e instanceof ItemFrame) && !(e instanceof Painting) && !(e instanceof Snowball) && e.getLocation().distance(player.getLocation()) > 5.2) 
					{
						if (e instanceof Projectile)
						{
							Projectile proj = (Projectile) e;
							proj.setShooter(player);
						}
						Location explodeCenter = player.getLocation();
						Float power = 0.5f;
						e.setVelocity(new Vector().normalize());
						e.setVelocity(new Vector(e.getLocation().getX() - explodeCenter.getX(), e.getLocation().getZ() - explodeCenter.getZ(), e.getLocation().getZ() - explodeCenter.getZ()).multiply(power));
					}
				}
			}
		}
	}
}
