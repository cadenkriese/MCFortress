package com.chaseoes.tf2.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import com.chaseoes.tf2.GamePlayer;

public class AmmoUtilities 
{
	public static AmmoUtilities instance = new AmmoUtilities();

	public AmmoUtilities(){}

	public static AmmoUtilities getUtilities()
	{
		return instance;
	}


	public boolean subractAmmo(GamePlayer gp, int ammo, ItemStack is, ItemStack weapon)
	{
		Player player = gp.getPlayer();
		if (getAmount(player, is) < ammo)
		{
			return false;
		}
		int subtractAmmo = ammo;
		ItemStack i1 = player.getItemInHand();
		ItemMeta i1Meta = i1.getItemMeta();
		int itemAmmo = getAmmo(weapon)-subtractAmmo;
		i1Meta.setDisplayName(ChatColor.YELLOW+"Flame Thrower «"+itemAmmo+"»");
		i1.setItemMeta(i1Meta);
		for (ItemStack item : player.getInventory().getContents())
		{
			if (item != null && item.getType() == is.getType())
			{
				if (item.getAmount() == ammo)
				{
					player.getInventory().remove(item);
					return true;
				}
				else if (item.getAmount() > ammo)
				{
					int newAmmo = item.getAmount()-ammo;
					item.setAmount(newAmmo);

					return true;
				}
				else if (item.getAmount() < ammo)
				{
					ammo = ammo-item.getAmount();
					player.getInventory().remove(item);
					continue;
				}
			}
			else
			{
				continue;
			}
		}
		Bukkit.getServer().broadcastMessage("bad-other");
		return false;
	}

	private int getAmmo(ItemStack weapon) 
	{
		Pattern p = Pattern.compile("Flame Thrower «(\\d+)»");
		Matcher match = p.matcher(ChatColor.stripColor(weapon.getItemMeta().getDisplayName()));
		if (match.find())
		{
			return Integer.valueOf(match.group(1));
		}
		return 0;
	}

	private int getAmount(Player p, ItemStack is)
	{
		int amount = 0;

		for (int slot = 0; slot < 36; slot++)
		{
			ItemStack item = p.getInventory().getItem(slot);
			
			if (item == null || item.getType() == Material.AIR)
			{
				//Continue
			}
			else if (is.getType() == item.getType() && is.getData().toString().equals(item.getData().toString()))
			{
				amount = amount+item.getAmount();
			}
		}
		return amount;
	}
}