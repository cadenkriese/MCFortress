package com.chaseoes.tf2.listeners;

import java.util.Random;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.TF2;
import com.connorlinfoot.actionbarapi.ActionBarAPI;

import fr.mrsheepsheep.tinthealth.THAPI;
import fr.mrsheepsheep.tinthealth.TintHealth;

public class EntityDamageListener implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) 
	{
		Entity dmgr = event.getDamager();
		Entity dmged = event.getEntity();
		if (dmgr instanceof Player && dmged instanceof Player)
		{
			Player p = (Player) dmgr;
			Player p2 = (Player) dmged;
			GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
			GamePlayer gp2 = GameUtilities.getUtilities().getGamePlayer(p2);

			if (!gp.isIngame() || gp.isInLobby() || gp.getGame().isCrit(p).equalsIgnoreCase("no_fire"))
			{
				ActionBarAPI.sendActionBar(p, ChatColor.RED+"You can't hit people right now!");
				event.setCancelled(true);
				return;
			}

			if (gp.isDead())
			{
				ActionBarAPI.sendActionBar(p, ChatColor.RED+"You can't hit people while your dead!");
				event.setCancelled(true);
				return;
			}

			if (gp2.isDead())
			{
				event.setCancelled(true);
				return;
			}
			if (gp != gp2)
			{
				if (gp2.isUbered() && gp2.getTeam() != gp.getTeam())
				{
					ActionBarAPI.sendActionBar(p, ChatColor.translateAlternateColorCodes('&', gp2.getTeamColor()+gp2.getName()+" &3Is Übercharged and can't be damaged, run!"));
					event.setCancelled(true);
					return;
				}
				if (gp.getTeam() == gp2.getTeam())
				{
					event.setCancelled(true);
					ActionBarAPI.sendActionBar(p, ChatColor.RED+"You can't hit your teammates!");
					return;
				}
			}

			if (gp.isInvis())
			{
				event.setCancelled(true);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You can't hit people while cloaked!"));
				return;
			}

			if (!event.isCancelled())
			{
				if (event.getCause() != DamageCause.BLOCK_EXPLOSION && event.getCause() == DamageCause.ENTITY_ATTACK && gp2.getLastDamageCause() == DamageCause.ENTITY_ATTACK)
				{
					if (p.getItemInHand().getType() == Material.STONE_SWORD) event.setDamage(4);
					else if (p.getItemInHand().getType() == Material.DIAMOND_SWORD) event.setDamage(5);
					else if (p.getItemInHand().getType() == Material.IRON_SWORD) event.setDamage(5);
					else if (p.getItemInHand().getType() == Material.DIAMOND_AXE) event.setDamage(5);
					else if (p.getItemInHand().getType() == Material.WOOD_SWORD) event.setDamage(5);
					else if (p.getItemInHand().getType() == Material.STONE_AXE)
					{
						if (dmged.getFireTicks() > 0)
						{
							event.setDamage(7d);
							ActionBarAPI.sendActionBar(p, ChatColor.YELLOW+"MINI-CRIT!");
							gp.setCritMessage( ChatColor.YELLOW+" MINI-CRIT!");
						}
						else
						{
							event.setDamage(5d);
						}
					}
					else if (p.getItemInHand().getType() == Material.BUCKET) event.setDamage(5);
					else if (p.getItemInHand().getType() == Material.GOLD_SWORD) event.setDamage(5);
					else if (p.getItemInHand().getType() == Material.IRON_AXE) event.setDamage(5);
					else event.setCancelled(true);
				}

				Random rand = new Random();
				int critChance = 100;
				int crits = gp.getKills()*7;
				int are = gp.getDeaths()+7;
				int fair = critChance-crits;
				int and =  fair+are;
				int balanced = and-(gp.getCurrentKillstreak()*10);
				if (rand.nextInt(balanced) == 0)
				{
					event.setDamage(event.getDamage()*3);
					ActionBarAPI.sendActionBar(p, ChatColor.DARK_GREEN+"CRITICAL HIT!");
					gp.setCritMessage( ChatColor.DARK_GREEN+" CRITICAL");
				}


				if (gp.getGame().isCrit(p).equalsIgnoreCase("true"))
				{
					event.setDamage(event.getDamage()*3);
					ActionBarAPI.sendActionBar(p, ChatColor.DARK_GREEN+"CRITICAL HIT!");
					gp.setCritMessage( ChatColor.DARK_GREEN+" CRITICAL");
				}
			}
		}
	}

	@EventHandler
	public void EntityDamaged(EntityDamageEvent e)
	{
		//TODO add prefrences logic for vignette effect
		if (e.getEntityType() == EntityType.PLAYER)
		{
			Player p = (Player) e.getEntity();
			GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);

			gp.setLastDamageCause(e.getCause());

			TintHealth th = (TintHealth) TF2.getInstance().getServer().getPluginManager().getPlugin("TintHealth");
			THAPI api = th.getAPI();
			float per1 = (float) (p.getHealth() / p.getMaxHealth());
			float per2 = 1-per1;
			int per3 = Math.round(per2*100);
			api.setTint(p, per3);
		}
	}
}
