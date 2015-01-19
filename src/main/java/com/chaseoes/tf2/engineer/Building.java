package com.chaseoes.tf2.engineer;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.chaseoes.tf2.GamePlayer;

public class Building 
{
	EngineerHandler handler;
	GamePlayer owner;
	Player target;
	Location sourceBlock;
	ArrayList<Location> locations = new ArrayList<Location>();
	
	int health;
	int ammo;
	boolean isActive;

	BuildType type;
	
	/**
	 * Defines a building.
	 * 
	 * @param handler The engineer handler class that created the building.
	 * @param owner The player that created the building.
	 * @param type The type of the building.
	 */
	public Building(EngineerHandler handler, GamePlayer owner, BuildType type)
	{
		this.handler = handler;
		this.owner = owner;
		this.type = type;
		
		health = 150;
		ammo = type.getAmmo();
	}
	
	public void setSource(Location loc)
	{
		sourceBlock = loc;
	}
	
	public void addLocation(Location loc)
	{
		locations.add(loc);
	}
	
	
	public Location getSource()
	{
		return sourceBlock;
	}
	
	public ArrayList<Location> getLocations()
	{
		return locations;
	}
	
	public BuildType getType()
	{
		return type;
	}
	
	public GamePlayer getOwner()
	{
		return owner;
	}

	public boolean isActive() 
	{
		return isActive;
	}

	public void setActive(boolean isActive) 
	{
		this.isActive = isActive;
	}

	public int getHealth() 
	{
		return health;
	}

	public void setHealth(int health) 
	{
		this.health = health;
	}

	public int getAmmo() 
	{
		return ammo;
	}

	public void setAmmo(int ammo) 
	{
		this.ammo = ammo;
	}

	public Player getTarget()
	{
		return target;
	}

	public void setTarget(Player target) 
	{
		this.target = target;
	}
}
