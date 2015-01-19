package com.chaseoes.tf2.engineer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.chaseoes.tf2.Team;

public enum BuildType 
{
	SENTRY("Sentry Gun"),

	DISPENSER("Dispenser"),

	TELEPORT_ENTRANCE("Teleporter Entrance"),

	TELEPORT_EXIT("Teleporter Exit");

	String name;

	private BuildType(String name)
	{
		this.name = name;
	}

	public String toString()
	{
		return name;
	}

	public int getAmmo()
	{
		switch (this)
		{
		case SENTRY:
			return 150;
			//This represents the dispensers max metal&ammo cap.
		case DISPENSER:
			return 600;
			//Teleport delay in ticks.
		case TELEPORT_ENTRANCE:
		case TELEPORT_EXIT:
			return 200;
		default:
			return 0;
		}
	}

	public int getCost()
	{
		switch (this)
		{
		case SENTRY:
			return 130;
		case DISPENSER:
			return 100;
		case TELEPORT_ENTRANCE:
		case TELEPORT_EXIT:
			return 125;
		default:
			return 0;
		}
	}

	public Material getMaterial(Team t)
	{
		switch (this)
		{
			case SENTRY:
				return Material.DISPENSER;
			case DISPENSER:
				return Material.FURNACE;
			case TELEPORT_ENTRANCE:
			case TELEPORT_EXIT:
				return new ItemStack(Material.STONE_SLAB2, 1, (byte) 6).getType();
			default:
				return Material.AIR;
		}
	}
}