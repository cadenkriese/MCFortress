package com.chaseoes.tf2.commands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
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

import com.chaseoes.tf2.boosters.Booster;
import com.chaseoes.tf2.boosters.PlayerBoostersFile;
import com.chaseoes.tf2.guis.profileGUI;

public class BoostersCommand implements Listener, CommandExecutor 
{
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (label.equalsIgnoreCase("boosters"))
		{
			if (args.length != 0)
			{
				if (args.length == 4)
				{
					if (args[0].equalsIgnoreCase("give"))
					{
						int time = Integer.valueOf(args[2]);
						int boost = Integer.valueOf(args[3]);
						Booster b = new Booster(args[1], time, boost);
						PlayerBoostersFile.getFile().addBooster(args[1], b);
						sender.sendMessage(ChatColor.GREEN+"Booster "+ChatColor.WHITE+ChatColor.BOLD.toString()+"» "+ChatColor.DARK_AQUA+"Sucsesfully gave booster.");
						return true;
					}
				}
				sender.sendMessage(ChatColor.GREEN+"Booster "+ChatColor.WHITE+ChatColor.BOLD.toString()+"» "+ChatColor.DARK_AQUA+"Please provide a player a time and a multiplier.");
			}
			else
			{
				if (sender instanceof Player)
				{
					sender.sendMessage(ChatColor.GREEN+"Booster "+ChatColor.WHITE+ChatColor.BOLD.toString()+"» "+ChatColor.DARK_AQUA+"Opening your boosters...");
					Player p = (Player) sender;
					openBoosterGui(p);
					return true;
				}
			}
		}
		return false;
	}

	public static void openBoosterGui(Player p)
	{
		int size = 0;
		boolean noBoosters = false;
		if (PlayerBoostersFile.getFile().getBoosters(p) == null || PlayerBoostersFile.getFile().getBoosters(p).size() == 0)
		{
			size = 27;
			noBoosters = true;
		}

		if (!noBoosters)
		{
			size = 18;
		}
		
		Inventory inv = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', "&6Profile &7» &6Boosters"));
		
		ItemStack backItem = new ItemStack(Material.BARRIER);
		ItemMeta backMeta = backItem.getItemMeta();
		backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9«« &cBack to Profile"));
		backItem.setItemMeta(backMeta);
		
		if (!noBoosters)
			inv.setItem(13, backItem);
		
		if (noBoosters)
		{
			ItemStack is = new ItemStack(Material.BARRIER);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.DARK_RED+"No boosters!");
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.RED+"You do not have any boosters!");
			lore.add("");
			lore.add(ChatColor.RED+"To get boosters you can");
			lore.add(ChatColor.RED+"purchase them at our shop!");
			lore.add("");
			lore.add(ChatColor.GOLD+"shop.mc-fort.net");
			im.setLore(lore);
			is.setItemMeta(im);
			inv.setItem(13, is);
			p.openInventory(inv);
			return;
		}

		PlayerBoostersFile.getFile();
		for (Booster b : PlayerBoostersFile.getFile().getBoosters(p))
		{
			ItemStack is = new ItemStack(Material.INK_SACK, 1, (byte) 3);
			ItemMeta im = is.getItemMeta();
			int time = b.getTime()/60/60;
			im.setDisplayName(ChatColor.GOLD.toString()+"x"+b.getBoost()+ChatColor.WHITE+" - "+ChatColor.AQUA+time+" Hour Booster");
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GREEN+"LEFT-CLICK "+ChatColor.DARK_GRAY+"to activate this booster!");
			im.setLore(lore);
			is.setItemMeta(im);
			inv.addItem(is);
		}
		p.openInventory(inv);
	}

	@EventHandler
	public void playerInventoryClickListener(InventoryClickEvent e)
	{
		if (ChatColor.stripColor(e.getInventory().getName()).equalsIgnoreCase("Profile » Boosters"))
		{
			Player p = (Player) e.getWhoClicked();
			e.setCancelled(true);
			ItemStack is = e.getInventory().getItem(e.getSlot());
			String itemName = ChatColor.stripColor(is.getItemMeta().getDisplayName());
			if (is.getType() == Material.INK_SACK)
			{
				Pattern pattern = Pattern.compile("x(\\d+) - (\\d+) Hour Booster");
				Matcher match = pattern.matcher(itemName);
				if (match.find())
				{
					Booster b = new Booster(p.getName(), Integer.valueOf(match.group(2))*60*60, Integer.valueOf(match.group(1)));
					PlayerBoostersFile.getFile().activateBooster(b);
					
				}
				p.closeInventory();
			}
			else if (is.getType() == Material.BARRIER)
			{
				profileGUI.getGUI().openGUI(p);
			}
		}
	}
}
