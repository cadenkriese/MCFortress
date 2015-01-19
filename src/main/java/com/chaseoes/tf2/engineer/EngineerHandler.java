package com.chaseoes.tf2.engineer;

import java.util.ArrayList;
import java.util.Arrays;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.chaseoes.tf2.GamePlayer;

public class EngineerHandler 
{
	GamePlayer gp;
	Player player;
	ArrayList<Building> buildings;
	int metal = 200;
	
	public EngineerHandler(GamePlayer gp)
	{
		this.gp = gp;
		player = gp.getPlayer();
	}
	
	public void addBuilding (Building b)
	{
		buildings.add(b);
	}
	
	public ArrayList<Building> getBuildings()
	{
		return buildings;
	}

	public int getMetal() 
	{
		return metal;
	}

	public void setMetal(int metal) 
	{
		this.metal = metal;
		if (gp.isIngame())
		{
			ItemStack metalItem = new ItemStack(Material.IRON_INGOT);
			ItemMeta metalMeta = metalItem.getItemMeta();
			metalMeta.setDisplayName(ChatColor.GOLD+"Metal");
			metalItem.setAmount(metal);
			metalItem.setItemMeta(metalMeta);
			player.getInventory().setItem(8, metalItem);
		}
	}
	
	public void buildGUI()
	{
		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD+"BUILD");
		boolean hasSentry = false;
		boolean hasDisp = false;
		boolean hasEnt = false;
		boolean hasExt = false;
		for (Building b : buildings)
		{
			if (b.getType() == BuildType.SENTRY)
			{
				hasSentry = true;
			}
			else if (b.getType() == BuildType.DISPENSER)
			{
				hasDisp = true;
			}
			else if (b.getType() == BuildType.TELEPORT_ENTRANCE)
			{
				hasEnt = true;
			}
			else if (b.getType() == BuildType.TELEPORT_EXIT)
			{
				hasExt = true;
			}
		}
		
		ItemStack sentry = new ItemStack(BuildType.SENTRY.getMaterial(gp.getTeam()));
		if (hasSentry) sentry.setType(Material.BARRIER);
		ItemMeta sentryMeta = sentry.getItemMeta();
		sentryMeta.setDisplayName(ChatColor.GOLD+ChatColor.BOLD.toString() + BuildType.SENTRY.toString());
		sentryMeta.setLore(Arrays.asList("", 
				ChatColor.translateAlternateColorCodes('&', "&8Cost: &f" + BuildType.SENTRY.getCost()),
				ChatColor.translateAlternateColorCodes('&', "&8Health: &f150"), 
				ChatColor.translateAlternateColorCodes('&', "&8Buildable: &f" + !hasSentry)));
		sentry.setItemMeta(sentryMeta);
		
		ItemStack dispenser = new ItemStack(BuildType.SENTRY.getMaterial(gp.getTeam()));
		if (hasDisp) dispenser.setType(Material.BARRIER);
		ItemMeta dispenserMeta = dispenser.getItemMeta();
		dispenserMeta.setDisplayName(ChatColor.GOLD+ChatColor.BOLD.toString()+BuildType.DISPENSER.toString());
		dispenserMeta.setLore(Arrays.asList("", 
				ChatColor.translateAlternateColorCodes('&', "&8Cost: &f"+BuildType.DISPENSER.getCost()),
				ChatColor.translateAlternateColorCodes('&', "&8Health: &f150"), 
				ChatColor.translateAlternateColorCodes('&', "&8Buildable: &f" + !hasDisp)));
		dispenser.setItemMeta(dispenserMeta);
		
		ItemStack teleEntrance = new ItemStack(BuildType.TELEPORT_ENTRANCE.getMaterial(gp.getTeam()));
		if (hasEnt) teleEntrance.setType(Material.BARRIER);
		ItemMeta entMeta = teleEntrance.getItemMeta();
		entMeta.setDisplayName(ChatColor.GOLD+ChatColor.BOLD.toString()+BuildType.TELEPORT_ENTRANCE.toString());
		entMeta.setLore(Arrays.asList("",
				ChatColor.translateAlternateColorCodes('&', "&8Cost: &f"+BuildType.TELEPORT_ENTRANCE.getCost()),
				ChatColor.translateAlternateColorCodes('&', "&8Health: &f150"), 
				ChatColor.translateAlternateColorCodes('&', "&8Buildable: &f" + !hasEnt)));
		teleEntrance.setItemMeta(entMeta);
		
		ItemStack teleExit = new ItemStack(BuildType.TELEPORT_EXIT.getMaterial(gp.getTeam()));
		if (hasExt) teleExit.setType(Material.BARRIER);
		ItemMeta extMeta = teleExit.getItemMeta();
		extMeta.setDisplayName(ChatColor.GOLD+ChatColor.BOLD.toString()+BuildType.TELEPORT_EXIT.toString());
		extMeta.setLore(Arrays.asList("",
				ChatColor.translateAlternateColorCodes('&', "&8Cost: &f"+BuildType.TELEPORT_EXIT.getCost()),
				ChatColor.translateAlternateColorCodes('&', "&8Health: &f150"), 
				ChatColor.translateAlternateColorCodes('&', "&8Buildable: &f" + !hasExt)));
		teleExit.setItemMeta(extMeta);
		
		inv.setItem(1, sentry);
		inv.setItem(3, dispenser);
		inv.setItem(5, teleEntrance);
		inv.setItem(7, teleExit);
		
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		
	}
}