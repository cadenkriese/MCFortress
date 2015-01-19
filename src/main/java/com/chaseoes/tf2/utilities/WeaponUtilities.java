package com.chaseoes.tf2.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.inventory.ItemStack;

public class WeaponUtilities 
{
	public WeaponUtilities() {}
	public static WeaponUtilities instance =  new WeaponUtilities();
	public static WeaponUtilities getUtilities() {	return instance;	}

	public String getWeaponTitleExact(ItemStack weapon)
	{
		if (weapon.hasItemMeta() && weapon.getItemMeta().hasDisplayName())
		{
			String weaponName = ChatColor.stripColor(weapon.getItemMeta().getDisplayName());

			Pattern p = Pattern.compile("(\\w+\\s*\\w*\\s*\\w*)");
			Matcher match = p.matcher(weaponName);

			if (match.find())
			{
				return match.group(1);
			}
			else
			{
				return weapon.getItemMeta().getDisplayName();
			}
		}
		return "";
	}
}
