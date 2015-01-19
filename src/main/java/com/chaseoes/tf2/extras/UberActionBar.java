package com.chaseoes.tf2.extras;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.chaseoes.tf2.TF2;
import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class UberActionBar 
{
	//To get an instance of the class
	public static UberActionBar instance = new UberActionBar();
	public UberActionBar() {}
	public static UberActionBar getBar() {    return instance;    }
	
	//The hashmap of players with bars
	HashMap<Player, Integer> barMap = new HashMap<Player, Integer>();
	
	public void startBar(final Player p)
	{
		if (!barMap.containsKey(p))
		{
			barMap.put(p, 
					Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
					{
						@Override
						public void run()
						{
							ActionBarAPI.sendActionBar(p, MediGunHandler.getHandler().getUberBar(p));
						}
					}, 0L, 1L));
		}
	}
	
	public void endBar(Player p)
	{	
		if (barMap.containsKey(p))
		{
			Bukkit.getServer().getScheduler().cancelTask(barMap.get(p));
			barMap.remove(p);
		}
		ActionBarAPI.sendActionBar(p, "");
	}
}
