package com.chaseoes.tf2.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.MapUtilities;
import com.chaseoes.tf2.listeners.TF2DeathListener;

public class TF2DeathEvent extends Event 
{

	private static final HandlerList handlers = new HandlerList();
	public Player player;
	public Player killer;
	public DamageCause cause;
	
	public TF2DeathEvent(Player p, Player k, DamageCause c) 
	{
		player = p;
		killer = k;
		cause = c;
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		Location teamspawn = MapUtilities.getUtilities().loadTeamSpawn(gp.getGame().getMapName(), gp.getTeam());
		TF2DeathListener.getListener().respawnDelay(p, gp, k, gp.getGame().getMap(), teamspawn, gp.getGame());
	}

	@Override
	public HandlerList getHandlers() 
	{
		return handlers;
	}

	public static HandlerList getHandlerList() 
	{
		return handlers;
	}

	public Player getPlayer() 
	{
		return player;
	}

	public Player getKiller() 
	{
		return killer;
	}
	
	public DamageCause getCause()
	{
		return cause;
	}

}
