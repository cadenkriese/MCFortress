package com.chaseoes.tf2.guis;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.chaseoes.tf2.commands.LoadoutCommand;

public class profileGUI 
implements Listener 
{
	public profileGUI() {}
	public static profileGUI instance = new profileGUI();
	public static profileGUI getGUI() {		return instance;	}

	public void openGUI(Player p)
	{
		Inventory inv = Bukkit.createInventory(null, 36, ChatColor.translateAlternateColorCodes('&', "&6Profile"));

		ItemStack statsItem = new ItemStack (Material.SKULL_ITEM, 1, (byte)3);
		SkullMeta SIM = (SkullMeta) statsItem.getItemMeta();
		SIM.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lStats"));
		ArrayList<String> skullLore = new ArrayList<String>();
		skullLore.add(ChatColor.GRAY + "Click to view your player stats.");
		SIM.setLore(skullLore);
		SIM.setOwner(p.getName());
		statsItem.setItemMeta(SIM);


		ItemStack loadoutItem = new ItemStack(Material.CHEST);
		ItemMeta loadMeta = loadoutItem.getItemMeta();
		loadMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lLoadouts"));
		ArrayList<String> loadLore = new ArrayList<String>();
		loadLore.add(ChatColor.GRAY+"Click to view your class loadouts.");
		loadMeta.setLore(loadLore);
		loadoutItem.setItemMeta(loadMeta);


		ItemStack boosterItem = new ItemStack(Material.INK_SACK, 1, (byte) 3);
		ItemMeta boosterMeta = boosterItem.getItemMeta();
		boosterMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lBoosters"));
		ArrayList<String> boostLore = new ArrayList<String>();
		boostLore.add(ChatColor.GRAY+"Click to view your network boosters.");
		boosterMeta.setLore(boostLore);
		boosterItem.setItemMeta(boosterMeta);


		ItemStack backItem = new ItemStack(Material.BARRIER);
		ItemMeta backMeta = backItem.getItemMeta();
		backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9«« &cReturn to reality"));
		backItem.setItemMeta(backMeta);

		inv.setItem(11, statsItem);
		inv.setItem(13, loadoutItem);
		inv.setItem(15, boosterItem);
		inv.setItem(31, backItem);

		p.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{	
		if (e.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', "&6Profile")))
		{
			e.setCancelled(true);
			if (e.getWhoClicked() instanceof Player)
			{
				Player p = (Player) e.getWhoClicked();

				switch(e.getSlot())
				{
				case 11:
					p.performCommand("stats");
					break;
				case 13:
					LoadoutCommand.openLoadoutGUI(p);
					break;
				case 15:
					p.performCommand("boosters");
					break;
				case 31:
					p.closeInventory();
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e)
	{
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Player p = e.getPlayer();
			ItemStack is = e.getItem();
			
			if (is != null && is.getType() == Material.SKULL_ITEM)
			{
				if (is.hasItemMeta() && is.getItemMeta().hasDisplayName() && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equals("Profile - Right Click"))
				{
					openGUI(p);
				}
			}
		}
	}
}
