package com.chaseoes.tf2.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.chaseoes.tf2.boosters.Booster;

public class BoosterEndEvent extends Event
{
	private static final HandlerList handlers =  new HandlerList();
	private Booster b;
	
	public BoosterEndEvent(Booster b)
	{
		this.b = b;
	}
	
	public Booster getBooster()
	{
		return b;
	}
	
    public HandlerList getHandlers() 
    {
        return handlers;
    }
 
    public static HandlerList getHandlerList() 
    {
        return handlers;
    }
}
