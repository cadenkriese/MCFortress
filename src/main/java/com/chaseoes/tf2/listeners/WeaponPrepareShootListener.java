package com.chaseoes.tf2.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.classes.ClassChest;
import com.chaseoes.tf2.extras.FlameThrowerHandler;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.shampaggon.crackshot.events.WeaponPlaceMineEvent;
import com.shampaggon.crackshot.events.WeaponPreShootEvent;
import com.shampaggon.crackshot.events.WeaponPrepareShootEvent;

public class WeaponPrepareShootListener implements Listener
{
	@EventHandler
	public void prepareShoot(WeaponPrepareShootEvent e)
	{
		Player p = e.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		if (!gp.isIngame() || gp.isInLobby())
		{
			e.setCancelled(true);
			ActionBarAPI.sendActionBar(gp.getPlayer(), ChatColor.RED+"You can't shoot right now!");
			return;
		}

		if (gp.isIngame())
		{
			if (gp.getGame().isCrit(p).equalsIgnoreCase("no_fire"))
			{
				e.setCancelled(true);
				ActionBarAPI.sendActionBar(gp.getPlayer(), ChatColor.RED+"You can't shoot right now!");
				return;
			}
		}

		if (gp.isInvis())
		{
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You cannot attack while cloaked!"));
			e.setCancelled(true);
			return;
		}

		if (gp.isIngame() && gp.getCurrentClass() != null && gp.getCurrentClass().getName().equalsIgnoreCase("Sniper"))
		{	
			PlayerInventory inventory = p.getInventory();
			ClassChest pClass = new ClassChest(gp.getCurrentClass().getName());
			inventory.setHelmet(pClass.getHelmet());
			p.updateInventory();
			p.removePotionEffect(PotionEffectType.SPEED);
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);
			gp.setScoping(false);
			gp.setJustFired(true);
			Scoping.stopCharge(gp);
			Scoping.resetCharge(gp);
		}
	}

	@EventHandler
	public void preShoot(WeaponPreShootEvent e)
	{
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(e.getPlayer());
		if (gp.getPlayer().getFireTicks() > 0)
		{
			e.setBulletSpread(2.5d);
		}
		if (e.getWeaponTitle().equalsIgnoreCase("flame"))
		{
			e.setCancelled(true);
			FlameThrowerHandler.getHandler().fire(gp, Action.RIGHT_CLICK_AIR);
		}
		if (gp.isInLobby() || !gp.isIngame() || gp.isUsingChangeClassButton())
		{
			e.setCancelled(true);
		}
		/*
		 * TODO
		 * improve this stuff
		if (e.getPlayer().isSprinting())
		{
			e.setBulletSpread(2);
		}
		if (e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) 
		{
			e.setBulletSpread(2);
		}
		 */
	}

	@EventHandler
	public void hitEvent(WeaponDamageEntityEvent e)
	{
		final Player p = e.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);

		/*if (gp.isInLobby())
		{
			e.setCancelled(true);
		}*/
		
		if (e.getVictim() instanceof Player)
		{
			Player v = (Player) e.getVictim();
			GamePlayer gv = GameUtilities.getUtilities().getGamePlayer(v);

			if (v == p)
			{
				return;
			}

			if (gp.getTeam() == gv.getTeam())
			{
				ActionBarAPI.sendActionBar(p, ChatColor.RED+"You can't shoot your teammates!");
				return;
			}

			if (gp.isDead() || gv.isDead())
			{
				e.setCancelled(true);
				return;
			}

			if (p == v)
			{
				if (gp.getCurrentClass().getName().equalsIgnoreCase("Soldier"))
				{
					if (e.getWeaponTitle().equalsIgnoreCase("jumper"))
					{
						final double health = p.getHealth();
						e.setDamage(1);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable()
						{
							@Override
							public void run()
							{
								p.setHealth(health);
							}
						}, 5L);
					}
				}
				e.setDamage(e.getDamage()/2);
			}
			if (gp.isIngame() && gv.isIngame())
			{
				Random rand = new Random();
				int critChance = 100;
				int crits = gp.getKills()*5;
				int are = gp.getDeaths()*5;
				int fair = critChance-crits;
				int and =  fair+are;
				int balanced = and-(gp.getCurrentKillstreak()*10);
				if (rand.nextInt(balanced) == 0)
				{
					if (!e.isCritical())
					{
						e.setDamage(e.getDamage()*3);
						ActionBarAPI.sendActionBar(p, ChatColor.DARK_GREEN+"CRITICAL HIT!");
						gp.setCritMessage( ChatColor.DARK_GREEN+" CRITICAL");
					}
				}
				if (gp.getGame().isCrit(gp.getPlayer()).equalsIgnoreCase("true"))
				{
					e.setDamage(e.getDamage()*3);
					ActionBarAPI.sendActionBar(p, ChatColor.DARK_GREEN+"CRITICAL HIT!");
					gp.setCritMessage(" §2CRITICAL");
					return;
				}
				if (e.getWeaponTitle().equalsIgnoreCase("flare1") || e.getWeaponTitle().equalsIgnoreCase("flare2"))
				{
					if (v.getFireTicks() > 0)
					{
						e.setDamage(14.4d);
						ActionBarAPI.sendActionBar(p, ChatColor.DARK_GREEN+"CRITICAL HIT!");
						gp.setCritMessage(" §2CRITICAL");
						return;
					}
					else
					{
						e.setDamage(4.8d);
					}
				}
				else if (e.isHeadshot())
				{
					if (gp.getCurrentClass().getName().equalsIgnoreCase("sniper"))
					{

						ActionBarAPI.sendActionBar(p, ChatColor.AQUA+"HEADSHOT!");
						if (v.getHealth() <= e.getDamage())
						{
							gp.setHeadShots(gp.getHeadShots()+1);
						}
						gp.setCritMessage(" §bHEADSHOT");
					}
				}
				else if (e.isBackstab())
				{
					if (gp.getCurrentClass().getName().equalsIgnoreCase("spy"))
					{
						ActionBarAPI.sendActionBar(p, ChatColor.AQUA+"BACKSTAB!");
						gp.setBackStabs(gp.getBackStabs()+1);
					}
					gp.setCritMessage(" §bBACKSTAB");
					return;
				}
				else if (e.isCritical())
				{
					ActionBarAPI.sendActionBar(p, ChatColor.DARK_GREEN+"CRITICAL HIT!");
					gp.setCritMessage(" §2CRITICAL");	
					return;
				}
				else
				{
					gp.setCritMessage("");
				}

				//Scope Damage
				if (gp.isIngame() && gp.getCurrentClass() != null && gp.getCurrentClass().getName().equalsIgnoreCase("Sniper"))
				{
					if (p.getInventory().getHeldItemSlot() == 0)
					{
						if (e.isHeadshot())
						{
							if (gp.getScopeCharge() == 0)
							{
								e.setDamage(24d);
								return;
							}
							else
							{
								double damage1 = gp.getScopeCharge()*23;
								double damage2 = damage1+150;
								double damage3 = damage2/12.5;
								double damageFinal = Math.round(damage3*2);
								e.setDamage(damageFinal);
								gp.setScopeCharge(0);	
							}
						}
						else
						{
							if (gp.getScopeCharge() == 0)
							{
								e.setDamage(8d);
							}
							else
							{
								double damage1 = gp.getScopeCharge()*8;
								double damage2 = damage1+50;
								double damage3 = damage2/12.5;
								double damageFinal = Math.round(damage3*2);
								e.setDamage(damageFinal);
								gp.setScopeCharge(0);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void PlaceMine(WeaponPlaceMineEvent e)
	{
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(e.getPlayer());

		gp.addMine(e.getMine());
	}
}
