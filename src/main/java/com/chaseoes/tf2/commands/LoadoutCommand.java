package com.chaseoes.tf2.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.chaseoes.tf2.Slot;
import com.chaseoes.tf2.classes.ClassChest;
import com.chaseoes.tf2.classes.ClassDataFile;
import com.chaseoes.tf2.classes.ClassGuis;
import com.chaseoes.tf2.guis.profileGUI;
import com.shampaggon.crackshot.CSUtility;

public class LoadoutCommand implements CommandExecutor, Listener 
{

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			if (label.equalsIgnoreCase("loadout"))
			{
				Player p = (Player) sender;
				if (args.length != 0)
				{
					String kitname = args[0];
					if (kitname.equalsIgnoreCase("scout") || kitname.equalsIgnoreCase("sniper") || kitname.equalsIgnoreCase("heavy") || kitname.equalsIgnoreCase("spy") || kitname.equalsIgnoreCase("soldier") || kitname.equalsIgnoreCase("pyro") || kitname.equalsIgnoreCase("demoman") || kitname.equalsIgnoreCase("medic") || kitname.equalsIgnoreCase("engineer"))
					{
						openGUI(kitname, p);
						return true;
					}
					else
					{
						sender.sendMessage("§aLoadouts §f§l» §e"+kitname+"§3 is not a valid class name!");
						return false;
					}
				}
				else
				{
					openLoadoutGUI(p);

					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			sender.sendMessage("You must be a player to execute this command.");
			return false;
		}
	}
	@EventHandler
	public void inventoryClick(InventoryClickEvent e)
	{
		if (e.getInventory().getName().contains(ChatColor.stripColor("Sniper")))
		{
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			switch (e.getSlot())
			{
			case 10:
				ClassGuis.getGuis().SniperP(p);
				break;
			case 22:
				openLoadoutGUI(p);
				break;
			}
		}
		if (e.getInventory().getName().contains(ChatColor.stripColor("Pyro")))
		{
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			switch (e.getSlot())
			{
			case 13:
				ClassGuis.getGuis().PyroS(p);
				break;
			case 22:
				openLoadoutGUI(p);
				break;
			}
		}
		if (e.getInventory().getName().contains(ChatColor.stripColor("Soldier")))
		{
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			switch (e.getSlot())
			{
			case 10:
				ClassGuis.getGuis().SoldierP(p);
				break;
			case 22:
				openLoadoutGUI(p);
				break;
			}
		}
		if (e.getInventory().getName().contains(ChatColor.stripColor("Heavy")))
		{		
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			switch (e.getSlot())
			{
			case 22:
				openLoadoutGUI(p);
				break;
			}
		}
		if (e.getInventory().getName().contains(ChatColor.stripColor("Spy")))
		{		
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			switch (e.getSlot())
			{
			case 22:
				openLoadoutGUI(p);
				break;
			}
		}
		if (e.getInventory().getName().contains(ChatColor.stripColor("Scout")))
		{		
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			switch (e.getSlot())
			{
			case 22:
				openLoadoutGUI(p);
				break;
			}
		}
		if (e.getInventory().getName().contains(ChatColor.stripColor("Demoman")))
		{
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			switch (e.getSlot())
			{
			case 22:
				openLoadoutGUI(p);
				break;
			}
		}
		if (e.getInventory().getName().contains(ChatColor.stripColor("Medic")))
		{
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			switch (e.getSlot())
			{
			case 22:
				openLoadoutGUI(p);
				break;
			}
		}
		if (e.getInventory().getName().contains(ChatColor.stripColor("Engineer")))
		{
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			switch (e.getSlot())
			{
			case 22:
				openLoadoutGUI(p);
				break;
			}
		}
		
		if (e.getInventory().getName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&6Profile &7» &6Loadouts")))
		{
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			if (e.getSlot() == 22)
			{
				profileGUI.getGUI().openGUI(p);
			}
			else
			{
				ItemStack is = e.getInventory().getItem(e.getSlot());
				if (is != null && is.getType() != Material.AIR)
				{
					p.performCommand("loadout "+ChatColor.stripColor(is.getItemMeta().getDisplayName()));
				}
			}
		}
	}
	
	public static void openGUI(String kitname, Player p)
	{
		if (kitname.equalsIgnoreCase("scout") || kitname.equalsIgnoreCase("sniper") || kitname.equalsIgnoreCase("heavy") || kitname.equalsIgnoreCase("spy") || kitname.equalsIgnoreCase("soldier") || kitname.equalsIgnoreCase("pyro") || kitname.equalsIgnoreCase("demoman") || kitname.equalsIgnoreCase("medic") || kitname.equalsIgnoreCase("engineer"))
		{
			UUID pid = p.getUniqueId();
			String className = kitname.substring(0,1).toUpperCase()+kitname.substring(1).toLowerCase();
			p.sendMessage("§aLoadouts §f§l» §3Opening your loadout for the §e"+className+"§3 class.");

			Inventory inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&6Loadouts &7» &6"+className));
			CSUtility cUtil = new CSUtility();

			ItemStack slot1 = cUtil.generateWeapon(cUtil.getWeaponTitle(ClassDataFile.getItem(pid.toString()+"."+className, Slot.PRIMARY, className)));
			ItemStack slot2 = cUtil.generateWeapon(cUtil.getWeaponTitle(ClassDataFile.getItem(pid.toString()+"."+className, Slot.SECONDARY, className)));
			ItemStack slot3 = cUtil.generateWeapon(cUtil.getWeaponTitle(ClassDataFile.getItem(pid.toString()+"."+className, Slot.MELEE, className)));

			if (slot1 == null || slot1.getType() == Material.AIR)
				slot1 = ClassDataFile.getItem(pid.toString()+"."+className, Slot.PRIMARY, className);
			if (slot2 == null || slot2.getType() == Material.AIR)
				slot2 = ClassDataFile.getItem(pid.toString()+"."+className, Slot.SECONDARY, className);
			if (slot3 == null || slot2.getType() == Material.AIR)
				slot3 = ClassDataFile.getItem(pid.toString()+"."+className, Slot.MELEE, className);

			inv.setItem(10, slot1);
			inv.setItem(13, slot2);
			inv.setItem(16, slot3);

			ItemStack backItem = new ItemStack(Material.BARRIER);
			ItemMeta backMeta = backItem.getItemMeta();
			backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9«« &cBack to Loadouts"));
			backItem.setItemMeta(backMeta);

			inv.setItem(22, backItem);

			p.openInventory(inv);
		}
	}

	public static void openLoadoutGUI(Player p)
	{
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&6Profile &7» &6Loadouts"));

		ItemStack scout = new ClassChest("Scout").getHelmet();
		ItemMeta scoutMeta = scout.getItemMeta();
		scoutMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lScout"));
		ArrayList<String> scoutLore = new ArrayList<String>();
		scoutLore.add(ChatColor.GRAY+"Click to open your loadout for "+ChatColor.ITALIC+"Scout"+ChatColor.GRAY+".");
		scoutMeta.setLore(scoutLore);
		scout.setItemMeta(scoutMeta);

		ItemStack soldier = new ClassChest("Soldier").getHelmet();
		ItemMeta soldierMeta = soldier.getItemMeta();
		soldierMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lSoldier"));
		ArrayList<String> soldierLore = new ArrayList<String>();
		soldierLore.add(ChatColor.GRAY+"Click to open your loadout for "+ChatColor.ITALIC+"Soldier"+ChatColor.GRAY+".");
		soldierMeta.setLore(soldierLore);
		soldier.setItemMeta(soldierMeta);

		ItemStack pyro = new ClassChest("Pyro").getHelmet();
		ItemMeta pyroMeta = pyro.getItemMeta();
		pyroMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lPyro"));
		ArrayList<String> pyroLore = new ArrayList<String>();
		pyroLore.add(ChatColor.GRAY+"Click to open your loadout for "+ChatColor.ITALIC+"Pyro"+ChatColor.GRAY+".");
		pyroMeta.setLore(pyroLore);
		pyro.setItemMeta(pyroMeta);

		ItemStack demoman = new ClassChest("Demoman").getHelmet();
		ItemMeta demoMeta = demoman.getItemMeta();
		demoMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lDemoman"));
		ArrayList<String> demoLore = new ArrayList<String>();
		demoLore.add(ChatColor.GRAY+"Click to open your loadout for "+ChatColor.ITALIC+"Demoman"+ChatColor.GRAY+".");
		demoMeta.setLore(demoLore);
		demoman.setItemMeta(demoMeta);

		ItemStack heavy = new ClassChest("Heavy").getHelmet();
		ItemMeta heavyMeta = heavy.getItemMeta();
		heavyMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lHeavy"));
		ArrayList<String> heavyLore = new ArrayList<String>();
		heavyLore.add(ChatColor.GRAY+"Click to open your loadout for "+ChatColor.ITALIC+"Heavy"+ChatColor.GRAY+".");
		heavyMeta.setLore(heavyLore);
		heavy.setItemMeta(heavyMeta);

		ItemStack engineer = new ClassChest("Engineer").getHelmet();
		ItemMeta engieMeta = engineer.getItemMeta();
		engieMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lEngineer"));
		ArrayList<String> engieLore = new ArrayList<String>();
		engieLore.add(ChatColor.GRAY+"Click to open your loadout for "+ChatColor.ITALIC+"Engineer"+ChatColor.GRAY+".");
		engieMeta.setLore(engieLore);
		engineer.setItemMeta(engieMeta);

		ItemStack medic = new ClassChest("Medic").getHelmet();
		ItemMeta medicMeta = medic.getItemMeta();
		medicMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lMedic"));
		ArrayList<String> medicLore = new ArrayList<String>();
		medicLore.add(ChatColor.GRAY+"Click to open your loadout for "+ChatColor.ITALIC+"Medic"+ChatColor.GRAY+".");
		medicMeta.setLore(medicLore);
		medic.setItemMeta(medicMeta);

		ItemStack sniper = new ClassChest("Sniper").getHelmet();
		ItemMeta sniperMeta = sniper.getItemMeta();
		sniperMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lSniper"));
		ArrayList<String> sniperLore = new ArrayList<String>();
		sniperLore.add(ChatColor.GRAY+"Click to open your loadout for "+ChatColor.ITALIC+"Sniper"+ChatColor.GRAY+".");
		sniperMeta.setLore(sniperLore);
		sniper.setItemMeta(sniperMeta);

		ItemStack spy = new ClassChest("Spy").getHelmet();
		ItemMeta spyMeta = spy.getItemMeta();
		spyMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lSpy"));
		ArrayList<String> spyLore = new ArrayList<String>();
		spyLore.add(ChatColor.GRAY+"Click to open your loadout for "+ChatColor.ITALIC+"Spy"+ChatColor.GRAY+".");
		spyMeta.setLore(spyLore);
		spy.setItemMeta(spyMeta);

		ItemStack backItem = new ItemStack(Material.BARRIER);
		ItemMeta backMeta = backItem.getItemMeta();
		backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9«« &cBack to Profile"));
		backItem.setItemMeta(backMeta);

		inv.setItem(0, scout);
		inv.setItem(1, soldier);
		inv.setItem(2, pyro);
		inv.setItem(3, demoman);
		inv.setItem(4, heavy);
		inv.setItem(5, engineer);
		inv.setItem(6, medic);
		inv.setItem(7, sniper);
		inv.setItem(8, spy);

		inv.setItem(22, backItem);
		
		p.openInventory(inv);
	}
}
