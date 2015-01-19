package com.chaseoes.tf2.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.chaseoes.tf2.StatsConfiguration;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.guis.profileGUI;
import com.chaseoes.tf2.utilities.GroupUtilities;
import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class StatsCommand implements CommandExecutor, Listener{
	Boolean guiOpen = false;
	public StatsCommand(TF2 tf2) {
	}
	public StatsCommand() {
	}
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (label.equalsIgnoreCase("stats"))
		{
			if (sender instanceof Player)
			{
				Player p = (Player) sender;
				if (args.length == 0)
				{
					openGUI(p, p.getName(), p.getUniqueId());
				}
				else
				{
					OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[0]);
					if (target == null)
					{
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aStats &f&l» &e"+args[0]+"&3 is not a valid player!"));
						return false;
					}
					UUID Tid = target.getUniqueId();
					if (Tid == null)
					{
						p.sendMessage(ChatColor.GREEN + "Stats" + ChatColor.WHITE + "" + ChatColor.BOLD + " » "+ChatColor.YELLOW + args[0] + ChatColor.DARK_AQUA + " is not a valid minecraft username!");
						return false;
					}
					if (!StatsConfiguration.containsUUID(Tid))
					{
						p.sendMessage(ChatColor.GREEN + "Stats" + ChatColor.WHITE + "" + ChatColor.BOLD + " » "+ChatColor.YELLOW + args[0] + ChatColor.DARK_AQUA + " has never joined before!");
						return false;
					}
					if (target != null && target.isOnline())
					{
						openGUI(p, target.getName(), Tid);
					}
					else
					{
						sender.sendMessage(ChatColor.GREEN+"Stats"+ChatColor.WHITE.toString()+ChatColor.BOLD+" » "+ChatColor.DARK_AQUA + "Warning, "+ChatColor.YELLOW+args[0]+ChatColor.DARK_AQUA+" is not online, you won't be able to see some of their information.");
						openGUI(p, args[0], Tid);
					}
				}
			}
		}
		return false;
	}
	@SuppressWarnings("unused")
	private void openGUI(Player sender, String target, UUID tid)
	{
		File playerStats = StatsConfiguration.getStatsConfig();
		FileConfiguration stats = StatsConfiguration.getStats();

		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GREEN + target + "'s stats");

		if (sender.getUniqueId() == tid)
		{
			inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&6Profile &7» &6Stats"));

			ItemStack backItem = new ItemStack(Material.BARRIER);
			ItemMeta backMeta = backItem.getItemMeta();
			backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9«« &cBack to Profile"));
			backItem.setItemMeta(backMeta);

			inv.setItem(49, backItem);
		}


		SkullMeta skull = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		skull.setOwner(target);
		skull.setDisplayName(ChatColor.GOLD + target);
		ItemStack head = new ItemStack (Material.SKULL_ITEM, 1, (byte)3);
		ArrayList<String> lore = new ArrayList<String>();
		OfflinePlayer targ = Bukkit.getServer().getOfflinePlayer(tid);
		lore.add("");
		if (targ != null && targ.isOnline())
		{
			if (GroupUtilities.getUtilities().getGroup(targ).equalsIgnoreCase("owner"))
			{
				lore.add(ChatColor.DARK_GRAY + "Rank: " + ChatColor.BLUE + "Owner");
			}
			else if (GroupUtilities.getUtilities().getGroup(targ).equalsIgnoreCase("admin"))
			{
				lore.add(ChatColor.DARK_GRAY + "Rank: " + ChatColor.DARK_RED + "Administrator");
			}
			else if (GroupUtilities.getUtilities().getGroup(targ).equalsIgnoreCase("moderator"))
			{
				lore.add(ChatColor.DARK_GRAY + "Rank: " + ChatColor.GOLD + "Moderator");
			}
			else if (GroupUtilities.getUtilities().getGroup(targ).equalsIgnoreCase("architect"))
			{
				lore.add(ChatColor.DARK_GRAY + "Rank: " + ChatColor.GREEN + "Builder");
			}
			else if (GroupUtilities.getUtilities().getGroup(targ).equalsIgnoreCase("vip"))
			{
				lore.add(ChatColor.DARK_GRAY + "Rank: " + ChatColor.AQUA + "VIP");
			}
			else
			{
				lore.add(ChatColor.DARK_GRAY + "Rank: " + ChatColor.GRAY + "Default");
			}
		}
		Economy econ = TF2.getInstance().getEcon();
		String credits = "";
		if (targ.isOnline())
		{
			credits = ""+econ.getBalance(targ);
		}
		if (!credits.equalsIgnoreCase(""))
		{
			lore.add(ChatColor.DARK_GRAY + "Credits: " + ChatColor.WHITE + credits);
			lore.add("");
		}
		int pKills = stats.getInt(tid.toString()+".kills");
		lore.add(ChatColor.DARK_GRAY + "Kills: " + ChatColor.WHITE + pKills);
		int pHeadshots = stats.getInt(tid.toString() + ".headshots");
		lore.add(ChatColor.DARK_GRAY + "Headshot Kills: " + ChatColor.WHITE + pHeadshots);
		int pBackstabs = stats.getInt(tid.toString() + ".backstabs");
		lore.add(ChatColor.DARK_GRAY + "Backstab Kills: " + ChatColor.WHITE + pBackstabs);
		int pDeaths = stats.getInt(tid.toString()+".deaths");
		lore.add(ChatColor.DARK_GRAY + "Deaths: " + ChatColor.WHITE + pDeaths);
		if (pDeaths == 0)
			pDeaths = 1;
		double KDR = pKills/pDeaths;
		String KDRS = String.valueOf(KDR);
		if (KDRS.length() > 4)
		{
			KDRS = KDRS.substring(0, 4);
		}
		lore.add(ChatColor.DARK_GRAY + "Kill/Death Ratio: " + ChatColor.WHITE + KDRS);
		int pPoints = stats.getInt(tid.toString()+".points");
		lore.add(ChatColor.DARK_GRAY + "Points Captured: " + ChatColor.WHITE + pPoints);
		int pWins = stats.getInt(tid.toString()+".wins");
		lore.add(ChatColor.DARK_GRAY + "Wins: " + ChatColor.WHITE + pWins);
		int pLosses = stats.getInt(tid.toString()+".losses");
		lore.add(ChatColor.DARK_GRAY + "Losses: " + ChatColor.WHITE + pLosses);

		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&6For more details visit"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&ehttp://mc-fort.net/stats"));

		skull.setLore(lore);
		head.setItemMeta(skull);
		inv.setItem(4, head);

		if (stats.getBoolean(tid.toString()+".scout"))
		{
			ItemStack pointAch = new ItemStack (Material.WOOL,1, (byte)5);
			ItemMeta pointsMeta = pointAch.getItemMeta();
			pointsMeta.setDisplayName(ChatColor.YELLOW + "Scout");
			ArrayList<String> lore1 = new ArrayList<String>();
			lore1.add(ChatColor.GRAY + "Capture 20 control points.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.GREEN + "Acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + "100%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"300 credits");
			pointsMeta.setLore(lore1);
			pointAch.setItemMeta(pointsMeta);
			inv.setItem(18, pointAch);
		}
		else
		{
			ItemStack pointAch = new ItemStack (Material.WOOL,1, (byte)14);
			ItemMeta pointsMeta = pointAch.getItemMeta();
			pointsMeta.setDisplayName(ChatColor.YELLOW + "Scout");
			ArrayList<String> lore1 = new ArrayList<String>();
			Integer percent = pPoints * 5;
			lore1.add(ChatColor.GRAY + "Capture 20 control points.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.RED + "Not acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + percent + "%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"300 credits");
			pointsMeta.setLore(lore1);
			pointAch.setItemMeta(pointsMeta);
			inv.setItem(18, pointAch);
		}

		if (stats.getBoolean(tid.toString()+".pauling"))
		{
			ItemStack killAch = new ItemStack (Material.WOOL,1, (byte)5);
			ItemMeta killsMeta = killAch.getItemMeta();
			killsMeta.setDisplayName(ChatColor.YELLOW + "Ms. Pauling");
			ArrayList<String> lore1 = new ArrayList<String>();
			lore1.add(ChatColor.GRAY + "Kill 100 people.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.GREEN + "Acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + "100%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"300 credits");
			killsMeta.setLore(lore1);
			killAch.setItemMeta(killsMeta);
			inv.setItem(20, killAch);
		}
		else
		{
			ItemStack killAch = new ItemStack (Material.WOOL,1, (byte)14);
			ItemMeta killsMeta = killAch.getItemMeta();
			killsMeta.setDisplayName(ChatColor.YELLOW + "Ms. Pauling");
			ArrayList<String> lore1 = new ArrayList<String>();
			lore1.add(ChatColor.GRAY + "Kill 100 people.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.RED + "Not acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + pKills + "%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"300 credits");
			killsMeta.setLore(lore1);
			killAch.setItemMeta(killsMeta);
			inv.setItem(20, killAch);
		}


		if (stats.getBoolean(tid.toString()+".spy"))
		{
			ItemStack spyAch = new ItemStack (Material.WOOL,1, (byte)5);
			ItemMeta spyMeta = spyAch.getItemMeta();
			spyMeta.setDisplayName(ChatColor.YELLOW + "Slap my Hand!");
			ArrayList<String> lore1 = new ArrayList<String>();
			lore1.add(ChatColor.GRAY + "Get 100 kills with backstabs.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.GREEN + "Acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + "100%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"300 credits");
			lore1.add("");
			lore1.add(ChatColor.GRAY+"Slap it now!");
			spyMeta.setLore(lore1);
			spyAch.setItemMeta(spyMeta);
			inv.setItem(22, spyAch);
		}
		else
		{
			ItemStack spyAch = new ItemStack (Material.WOOL,1, (byte)14);
			ItemMeta spyMeta = spyAch.getItemMeta();
			spyMeta.setDisplayName(ChatColor.YELLOW + "Slap my Hand!");
			ArrayList<String> lore1 = new ArrayList<String>();
			int percent = stats.getInt(tid.toString()+".backstabs");
			lore1.add(ChatColor.GRAY + "Get 100 kills with backstabs.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.RED + "Not acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + percent + "%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"300 credits");
			lore1.add("");
			lore1.add(ChatColor.GRAY+"Slap it now!");
			spyMeta.setLore(lore1);
			spyAch.setItemMeta(spyMeta);
			inv.setItem(22, spyAch);
		}


		if (stats.getBoolean(tid.toString()+".sniper"))
		{
			ItemStack hedAch = new ItemStack (Material.WOOL,1, (byte)5);
			ItemMeta hedMeta = hedAch.getItemMeta();
			hedMeta.setDisplayName(ChatColor.YELLOW + "Be Efficient.");
			ArrayList<String> lore1 = new ArrayList<String>();
			lore1.add(ChatColor.GRAY + "Get 100 kills with headshots.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.GREEN + "Acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + "100%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"300 credits");
			hedMeta.setLore(lore1);
			hedAch.setItemMeta(hedMeta);
			inv.setItem(24, hedAch);
		}
		else
		{
			ItemStack hedAch = new ItemStack (Material.WOOL,1, (byte)14);
			ItemMeta hedMeta = hedAch.getItemMeta();
			hedMeta.setDisplayName(ChatColor.YELLOW + "Be Efficient.");
			ArrayList<String> lore1 = new ArrayList<String>();
			int percent = stats.getInt(tid.toString()+".headshots");
			lore1.add(ChatColor.GRAY + "Get 100 kills with headshots.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.RED + "Not acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + percent + "%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"300 credits");
			hedMeta.setLore(lore1);
			hedAch.setItemMeta(hedMeta);
			inv.setItem(24, hedAch);
		}


		if (stats.getBoolean(tid.toString()+".champ"))
		{
			ItemStack winAch = new ItemStack (Material.WOOL,1, (byte)5);
			ItemMeta winsMeta = winAch.getItemMeta();
			winsMeta.setDisplayName(ChatColor.YELLOW + "Champion");
			ArrayList<String> lore1 = new ArrayList<String>();
			lore1.add(ChatColor.GRAY + "Win 50 games.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.GREEN + "Acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + "100%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"500 credits");
			winsMeta.setLore(lore1);
			winAch.setItemMeta(winsMeta);
			inv.setItem(26, winAch);
		}
		else
		{
			ItemStack winAch = new ItemStack (Material.WOOL,1, (byte)14);
			ItemMeta winsMeta = winAch.getItemMeta();
			winsMeta.setDisplayName(ChatColor.YELLOW + "Champion");
			ArrayList<String> lore1 = new ArrayList<String>();
			Integer percent = pWins * 2;
			lore1.add(ChatColor.GRAY + "Win 50 games.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.RED + "Not acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + percent + "%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"500 credits");
			winsMeta.setLore(lore1);
			winAch.setItemMeta(winsMeta);
			inv.setItem(26, winAch);
		}

		if (stats.getBoolean(tid.toString()+".master"))
		{
			ItemStack winAch = new ItemStack (Material.EMERALD_BLOCK);
			ItemMeta winsMeta = winAch.getItemMeta();
			winsMeta.setDisplayName(ChatColor.YELLOW + "Master Achievement");
			ArrayList<String> lore1 = new ArrayList<String>();
			lore1.add(ChatColor.GRAY + "Get all of the MC-Fortress Achievements.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.GREEN + "Acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + "100%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"1000 credits");
			winsMeta.setLore(lore1);
			winAch.setItemMeta(winsMeta);
			inv.setItem(40, winAch);
		}
		else
		{
			int totalAch = 0;
			if (stats.getBoolean(tid.toString()+".champ"))
			{
				totalAch++;
			}
			if (stats.getBoolean(tid.toString()+".scout"))
			{
				totalAch++;
			}
			if (stats.getBoolean(tid.toString()+".pauling"))
			{
				totalAch++;
			}
			if (stats.getBoolean(tid.toString()+".spy"))
			{
				totalAch++;
			}
			if (stats.getBoolean(tid.toString()+".sniper"))
			{
				totalAch++;
			}
			int percent = totalAch * 20;
			ItemStack winAch = new ItemStack (Material.REDSTONE_BLOCK);
			ItemMeta winsMeta = winAch.getItemMeta();
			winsMeta.setDisplayName(ChatColor.YELLOW + "Master Achievement");
			ArrayList<String> lore1 = new ArrayList<String>();
			lore1.add(ChatColor.GRAY + "Get all of the MC-Fortress Achievements.");
			lore1.add("");
			lore1.add(ChatColor.DARK_GRAY + "Status: " + ChatColor.RED + "Not acheived");
			lore1.add(ChatColor.DARK_GRAY + "Progress: " + ChatColor.WHITE + percent + "%");
			lore1.add(ChatColor.DARK_GRAY + "Reward: " + ChatColor.WHITE+"1000 credits");
			winsMeta.setLore(lore1);
			winAch.setItemMeta(winsMeta);
			inv.setItem(40, winAch);
		}

		sender.openInventory(inv);
		guiOpen = true;
	}
	@EventHandler
	public void cancelClick(InventoryClickEvent ev)
	{
		if (ChatColor.stripColor(ev.getInventory().getName()).contains("'s stats"))
		{
			ev.setCancelled(true);	
		}
		else if (ChatColor.stripColor(ev.getInventory().getName()).equals("Profile » Stats"))
		{
			if (ev.getWhoClicked() instanceof Player)
			{
				Player p = (Player) ev.getWhoClicked();
				
				ev.setCancelled(true);

				if (ev.getSlot() == 49)
				{
					profileGUI.getGUI().openGUI(p);
				}
			}
		}
	}

	@EventHandler
	public void dropEvent(PlayerDropItemEvent ev)
	{
		Player p = ev.getPlayer();
		if (!p.hasPermission("tf2.dropitems"))
		{
			ActionBarAPI.sendActionBar(p, ChatColor.RED+"You can't drop items!");

			ev.setCancelled(true);
		}
		return;
	}
}
