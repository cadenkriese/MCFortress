package com.chaseoes.tf2.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mkremins.fanciful.FancyMessage;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.chaseoes.tf2.Slot;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.commands.LoadoutCommand;
import com.shampaggon.crackshot.CSUtility;

public class ClassGuis implements Listener{
	static ClassGuis instance = new ClassGuis();
	
	//Scout
	int SodaPopper = 4000;
	int FlyingGuillotine = 4000;
	int CritaCola = 3500;
	
	//Soldier
	int RocketJumper = 4500;
	
	//Pyro
	int Phlogistinator = 5000;
	int FlareGun = 3000;
	int LolliChop = 3000;
	
	//Demoman
	int lochnload = 3500;
	int StickyJumper = 2500;
	
	//Heavy
	int BrassBeast = 3000;
	int Sandvich = 4000;
	int GlovesOfRunning = 3500;
	
	//Engineer
	
	//Medic
	
	//Sniper
	int Jarate = 3000;
	int TribalmansShiv = 3000;
	
	//Spy
	int DiamondBack = 5000;
	int BigEarner = 5000;
	
	public static ClassGuis getGuis()
	{
		return instance;
	}

	public void SniperP(Player p)
	{
		CSUtility cUtil = new CSUtility();
		
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD+"Sniper ("+ChatColor.GRAY+"Primary"+ChatColor.GOLD+")");
		
		ItemStack sniper = cUtil.generateWeapon("SKS");
		ItemMeta sniperMeta = sniper
				.getItemMeta();
		List<String> lore = sniperMeta.getLore();;
		lore.add("");
		lore.add(ChatColor.DARK_GRAY+"Can Use: "+ChatColor.GREEN+"True");
		sniperMeta.setLore(lore);
		sniper.setItemMeta(sniperMeta);
		inv.setItem(11, sniper);

		ItemStack awp = cUtil.generateWeapon("awp");
		ItemMeta awpMeta = awp.getItemMeta();
		List<String> awpLore = awpMeta.getLore();
		awpLore.add("");
		if (p.hasPermission("tf2.weapon.awp"))
		{
			awpLore.add(ChatColor.DARK_GRAY+"Can Use: "+ChatColor.GREEN+"True");
		}
		else
		{
			awpLore.add(ChatColor.DARK_GRAY+"Can Use: "+ChatColor.RED+"False");
			awpLore.add(ChatColor.translateAlternateColorCodes('&', "&cGet this weapon with &bVIP &cvisit &bshop.mc-fort.net&c!"));
		}
		awpMeta.setLore(awpLore);
		awp.setItemMeta(awpMeta);
		inv.setItem(15, awp);
		p.openInventory(inv);
	}
	
	@EventHandler
	public void SniperBuy(InventoryClickEvent e)
	{
		if (e.getWhoClicked() instanceof Player)
		{
			Player p = (Player) e.getWhoClicked();
			if (e.getInventory().getName().contains("§6Sniper (§7Primary§6)"))
			{
				e.setCancelled(true);
				String className = "Sniper";
				if(e.getCurrentItem().getType() == Material.GOLD_AXE || (e.getCurrentItem().getType() == Material.GOLD_PICKAXE))
				{
					if (e.getInventory().getItem(e.getSlot()).getType() == Material.GOLD_PICKAXE)
					{
						if (p.hasPermission("tf2.weapon.awp"))
						{
							UUID pid = p.getUniqueId();
							ClassDataFile.setItem(pid.toString()+"."+className, Slot.PRIMARY, e.getInventory().getItem(e.getSlot()));
							p.sendMessage("§aLoadouts §f§l» §3Succesfully set your §ePrimary §3slot to §eThe AWPer Hand§3.");
							LoadoutCommand.openGUI(className, p);
						}
						else
						{
							p.closeInventory();
							new FancyMessage("Shop ")
							.color(ChatColor.GREEN)
							.then("» ")
							.color(ChatColor.WHITE)
							.style(ChatColor.BOLD)
							.then("This weapon is")
							.color(ChatColor.DARK_AQUA)
							.tooltip(ChatColor.AQUA+"Click to visit shop.mc-fort.net")
							.link("http://shop.mc-fort.net/")
							.then(" VIP ")
							.color(ChatColor.AQUA)
							.then("exclusive. To purchase")
							.color(ChatColor.DARK_AQUA)
							.tooltip(ChatColor.AQUA+"Click to visit shop.mc-fort.net")
							.link("http://shop.mc-fort.net/")
							.then(" VIP ")
							.color(ChatColor.AQUA)
							.tooltip(ChatColor.AQUA+"Click to visit shop.mc-fort.net")
							.link("http://shop.mc-fort.net/")
							.then("please visit the server ")
							.color(ChatColor.DARK_AQUA)
							.tooltip(ChatColor.AQUA+"Click to visit shop.mc-fort.net")
							.link("http://shop.mc-fort.net/")
							.then("store")
							.color(ChatColor.YELLOW)
							.tooltip(ChatColor.AQUA+"Click to visit shop.mc-fort.net")
							.link("http://shop.mc-fort.net/")
							.then(".")
							.color(ChatColor.DARK_AQUA)
							.tooltip(ChatColor.AQUA+"Click to visit shop.mc-fort.net")
							.link("http://shop.mc-fort.net/")
							.send(p);
						}
					}
					else if (e.getInventory().getItem(e.getSlot()).getType() == Material.GOLD_AXE)
					{
						UUID pid = p.getUniqueId();
						ClassDataFile.setItem(pid.toString()+"."+className, Slot.PRIMARY, e.getInventory().getItem(e.getSlot()));
						p.sendMessage("§aLoadouts §f§l» §3Succesfully set your §ePrimary §3slot to the §eSniper Rifle§3.");
						LoadoutCommand.openGUI(className, p);
					}
				}
			}
		}
	}
	
	
	
	
	
	
	public void PyroS(Player p)
	{	
		CSUtility cUtil = new CSUtility();
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD+"Pyro ("+ChatColor.GRAY+"Secondary"+ChatColor.GOLD+")");
		
		ItemStack shotgun = cUtil.generateWeapon("shotgun");
		ItemMeta shotgunMeta = shotgun.getItemMeta();
		List<String> lore = shotgunMeta.getLore();
		lore.add(ChatColor.GRAY+"Level 1 Shotgun");
		lore.add("");
		lore.add(ChatColor.DARK_GRAY+"Can Use: "+ChatColor.GREEN+"True");
		shotgunMeta.setLore(lore);
		shotgun.setItemMeta(shotgunMeta);
		inv.setItem(11, shotgun);

		ItemStack flare = cUtil.generateWeapon("Flare2");
		ItemMeta flareMeta = flare.getItemMeta();
		List<String> flareLore = flareMeta.getLore();
		flareLore.add("");
		if (p.hasPermission("tf2.weapon.flare"))
		{
			flareLore.add(ChatColor.DARK_GRAY+"Can Use: "+ChatColor.GREEN+"True");
		}
		else
		{
			flareLore.add(ChatColor.DARK_GRAY+"Can Use: "+ChatColor.RED+"False");
			flareLore.add(ChatColor.DARK_GRAY+"Price: "+ChatColor.YELLOW+"$"+FlareGun);
		}
		flareMeta.setLore(flareLore);
		flare.setItemMeta(flareMeta);
		inv.setItem(15, flare);
		p.openInventory(inv);
	}

	@EventHandler
	public void PyroSbuy(InventoryClickEvent e)
	{
		if (e.getWhoClicked() instanceof Player)
		{
			Player p = (Player) e.getWhoClicked();
			if (e.getInventory().getName().contains("§6Pyro (§7Secondary§6)"))
			{
				e.setCancelled(true);
				String className = "Pyro";
				if(e.getCurrentItem().getType() == Material.DIAMOND_HOE || (e.getCurrentItem().getType() == Material.STONE_SPADE))
				{
					if (e.getInventory().getItem(e.getSlot()).getType() == Material.DIAMOND_HOE)
					{
						if (p.hasPermission("tf2.weapon.flare"))
						{
							UUID pid = p.getUniqueId();
							ClassDataFile.setItem(pid.toString()+"."+className, Slot.SECONDARY, e.getCurrentItem());
							p.sendMessage("§aLoadouts §f§l» §3Succesfully set your §eSecondary §3slot to §eThe Flare Gun§3.");
							LoadoutCommand.openGUI(className, p);
						}
						else
						{
							Economy econ = TF2.econ;
							Permission perm = TF2.perms;
							Integer bal = (int) econ.getBalance(p);
							if (bal >= FlareGun)
							{
								UUID pid = p.getUniqueId();
								TF2.getInstance().removeCredits(p, FlareGun);
								perm.playerAdd(p, "tf2.weapon.flare");
								ClassDataFile.setItem(pid.toString()+"."+className, Slot.SECONDARY, e.getInventory().getItem(e.getSlot()));
								p.sendMessage("§aShop §f§l» §3Succesfully bought §eThe Flare Gun§3, for §e"+FlareGun+"§3 credits.");
								p.sendMessage("§aLoadouts §f§l» §3Succesfully set your §eSecondary §3slot to §eThe Flare Gun§3.");
								p.sendMessage("§7(-"+FlareGun+" Credits)");
								LoadoutCommand.openGUI(className, p);
							}
							else
							{
								p.closeInventory();
								int difference = FlareGun-bal;
								p.sendMessage("§aShop §f§l» §3Error, you need §e"+difference+" §3more credits before you can make that purchase");
							}
						}
					}
					else if (e.getInventory().getItem(e.getSlot()).getType() == Material.STONE_SPADE)
					{
						CSUtility cUtil = new CSUtility();
						
						UUID pid = p.getUniqueId();
						ItemStack item = cUtil.generateWeapon("shotgun");
						ClassDataFile.setItem(pid.toString()+"."+className, Slot.SECONDARY, item);
						p.sendMessage("§aLoadouts §f§l» §3Succesfully set your §eSecondary §3slot to the §eShotgun§3.");
						LoadoutCommand.openGUI(className, p);
					}
				}
			}
		}
	}
	
	
	public void SoldierP(Player p)
	{	
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD+"Soldier ("+ChatColor.GRAY+"Primary"+ChatColor.GOLD+")");
		ItemStack rocketLauncher = new ItemStack(Material.DIAMOND_SPADE);
		ItemMeta rocketMeta = rocketLauncher.getItemMeta();
		rocketMeta.setDisplayName(ChatColor.YELLOW+"Rocket Launcher");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY+"Level 1 RocketLauncher");
		lore.add("");
		lore.add(ChatColor.DARK_GRAY+"Can Use: "+ChatColor.GREEN+"True");
		rocketMeta.setLore(lore);
		rocketLauncher.setItemMeta(rocketMeta);
		inv.setItem(11, rocketLauncher);

		ItemStack jumper = new ItemStack(Material.IRON_SPADE);
		ItemMeta jumpMeta = jumper.getItemMeta();
		jumpMeta.setDisplayName(ChatColor.YELLOW+"The Rocket Jumper");
		
		CSUtility cUtil = new CSUtility();
		ItemStack realItem = cUtil.generateWeapon("jumper");
		
		
		List<String> jumpLore = new ArrayList<String>();
		
		jumpLore = realItem.getItemMeta().getLore();
		
		jumpLore.add("");
		if (p.hasPermission("tf2.weapon.rocketjumper"))
		{
			jumpLore.add(ChatColor.DARK_GRAY+"Can Use: "+ChatColor.GREEN+"True");
		}
		else
		{
			jumpLore.add(ChatColor.DARK_GRAY+"Can Use: "+ChatColor.RED+"False");
			jumpLore.add(ChatColor.DARK_GRAY+"Price: "+ChatColor.YELLOW+"$"+RocketJumper);
		}
		jumpMeta.setLore(jumpLore);
		jumper.setItemMeta(jumpMeta);
		inv.setItem(15, jumper);
		p.openInventory(inv);
	}
	
	@EventHandler
	public void SoldierPBuy(InventoryClickEvent e)
	{
		if (e.getWhoClicked() instanceof Player)
		{
			Player p = (Player) e.getWhoClicked();
			if (e.getInventory().getName().contains("§6Soldier (§7Primary§6)"))
			{
				e.setCancelled(true);
				String className = "Soldier";
				if(e.getCurrentItem().getType() == Material.DIAMOND_SPADE || (e.getCurrentItem().getType() == Material.IRON_SPADE))
				{
					if (e.getInventory().getItem(e.getSlot()).getType() == Material.IRON_SPADE)
					{
						if (p.hasPermission("tf2.weapon.rocketjumper"))
						{
							UUID pid = p.getUniqueId();
							ClassDataFile.setItem(pid.toString()+"."+className, Slot.PRIMARY, e.getCurrentItem());
							p.sendMessage("§aLoadouts §f§l» §3Succesfully set your §ePrimary §3slot to §eThe Rocket Jumper§3.");
							LoadoutCommand.openGUI(className, p);
						}
						else
						{
							Economy econ = TF2.econ;
							Permission perm = TF2.perms;
							Integer bal = (int) econ.getBalance(p);
							if (bal >= RocketJumper)
							{
								UUID pid = p.getUniqueId();
								TF2.getInstance().removeCredits(p, RocketJumper);
								perm.playerAdd(p, "tf2.weapon.rocketjumper");
								ClassDataFile.setItem(pid.toString()+"."+className, Slot.PRIMARY, e.getInventory().getItem(e.getSlot()));
								p.sendMessage("§aShop §f§l» §3Succesfully bought §eThe Rocket Jumper§3, for §e"+RocketJumper+"§3 credits.");
								p.sendMessage("§aLoadouts §f§l» §3Succesfully set your §ePrimary §3slot to §eThe Rocket Jumper§3.");
								p.sendMessage("§7(-"+RocketJumper+" Credits)");
								LoadoutCommand.openGUI(className, p);
							}
							else
							{
								p.closeInventory();
								int difference = RocketJumper-bal;
								p.sendMessage("§aShop §f§l» §3Error, you need §e"+difference+" §3more credits before you can make that purchase");
							}
						}
					}
					else if (e.getInventory().getItem(e.getSlot()).getType() == Material.DIAMOND_SPADE)
					{
						CSUtility cUtil = new CSUtility();
						UUID pid = p.getUniqueId();
						ItemStack item = cUtil.generateWeapon("rpg");
						ClassDataFile.setItem(pid.toString()+"."+className, Slot.PRIMARY, item);
						p.sendMessage("§aLoadouts §f§l» §3Succesfully set your §ePrimary §3slot to the §eThe Rocket Launcher§3.");
						LoadoutCommand.openGUI(className, p);
					}
				}
			}
		}
	}
}
