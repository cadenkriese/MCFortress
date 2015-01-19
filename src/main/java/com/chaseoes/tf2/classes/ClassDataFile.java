package com.chaseoes.tf2.classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.chaseoes.tf2.Slot;
import com.shampaggon.crackshot.CSUtility;

public class ClassDataFile {
	public static File kitConfig = null;
	public static FileConfiguration kits = null;
	public static boolean loadedFile = false;

	public static void loadConfiguration(String folder)
	{
		Bukkit.getServer().getLogger().info("[TF2] Loading class files.");
		kitConfig = new File(folder);
		kits = YamlConfiguration.loadConfiguration(kitConfig);
	}

	public static void saveFiles()
	{
		Bukkit.getServer().getLogger().info("[TF2] Kit files saved.");
		if ( !loadedFile)
		{
			loadConfiguration("plugins/TF2/classes.yml");
			loadedFile = true;
		}

		try {
			kits.save(kitConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void loadFiles()
	{
		if ( !loadedFile || !kitConfig.exists())
		{
			Bukkit.getServer().getLogger().info("[TF2] Creating kit file...");
			loadConfiguration("plugins/TF2/classes.yml");
			loadedFile = true;
			Bukkit.getServer().getLogger().info("[TF2] Kit files created");
		}

		if(kitConfig.exists())
		{
			Bukkit.getServer().getLogger().info("[TF2] Loading kit files");
			try {
				kits.load(kitConfig);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			Bukkit.getServer().getLogger().info("[Tf2] Kit files loaded.");
		}
		else
		{
			Bukkit.getServer().getLogger().info("[TF2] Saving config");
			try {
				kits.save(kitConfig);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}

	public static boolean contains(String str)
	{
		if (kits.contains(str))
		{
			return true;
		}
		return false;
	}
	public static ItemStack getItem(String path, Slot slot, String className)
	{
		//Format = ItemName Slot Amount Name Lore

		Pattern p1 = Pattern.compile("(\\S+)-(\\d+)-([^-]+)-\\[([^-\\]]+)\\]");
		Pattern p2 = Pattern.compile("(\\S+)-(\\d+)-([^-]+)");
		Pattern p3 = Pattern.compile("(\\S+)-(\\d+)");

		String itemString = kits.getString(path+"."+slot.getName());
		if (kits.contains(path+"."+slot.getName()))
		{
			if (itemString != null)
			{

				Matcher match1 = p1.matcher(itemString);
				Matcher match2 = p2.matcher(itemString);
				Matcher match3 = p3.matcher(itemString);


				if (match1.find())
				{
					ItemStack iStack = new ItemStack(Material.getMaterial(match1.group(1)), Integer.valueOf(match1.group(2)));
					ItemMeta iStackMeta = iStack.getItemMeta();
					iStackMeta.setDisplayName(match1.group(3));
					String[] loreArray = match1.group(4).split(",");
					List<String> loreList = new ArrayList<String>();
					for( String loreItem : loreArray)
					{
						loreList.add(loreItem);
					}
					iStackMeta.setLore(loreList);
					iStack.setItemMeta(iStackMeta);
					return iStack;
				}
				else if (match2.find())
				{
					ItemStack iStack = new ItemStack(Material.getMaterial(match2.group(1)), Integer.valueOf(match2.group(2)));
					ItemMeta iStackMeta = iStack.getItemMeta();
					iStackMeta.setDisplayName(match2.group(3));
					iStack.setItemMeta(iStackMeta);
					return iStack;
				}
				else if (match3.find())
				{
					return new ItemStack(Material.getMaterial(match3.group(1)), Integer.valueOf(match3.group(2)));	
				}
				else
				{
					return null;
				}
			}
			return null;
		}
		else
		{
			ClassChest cChest = new ClassChest(className);
			ItemStack is = cChest.getItem(slot);
			if (is != null)
			{
				return is;
			}
			return new ItemStack(Material.AIR);
		}
	}
	public static void setItem(String path, Slot slot, ItemStack item)
	{
		/*
		 *           *FORMAT*
		 * Material-Amount-DisplayName-Lore
		 */
		String itemString = "";
		CSUtility cUtil = new CSUtility();
		if (cUtil.generateWeapon(cUtil.getWeaponTitle(item)) != null)
		{
			ItemStack i = cUtil.generateWeapon(cUtil.getWeaponTitle(item));
			if (i.hasItemMeta() && i.getItemMeta().hasLore() && i.getItemMeta().hasDisplayName())
			{
				itemString = i.getType().toString()+"-"+i.getAmount()+"-"+i.getItemMeta().getDisplayName()+"-"+i.getItemMeta().getLore().toString();
			}
			else if (i.hasItemMeta() && i.getItemMeta().hasDisplayName())
			{
				itemString = i.getType().toString()+"-"+i.getAmount()+"-"+i.getItemMeta().getDisplayName();
			}
			else
			{
				itemString = i.getType().toString()+"-"+i.getAmount();
			}
		}
		else
		{
			if (item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().hasDisplayName())
			{
				itemString = item.getType().toString()+"-"+item.getAmount()+"-"+item.getItemMeta().getDisplayName()+"-"+item.getItemMeta().getLore().toString();
			}
			else if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
			{
				itemString = item.getType().toString()+"-"+item.getAmount()+"-"+item.getItemMeta().getDisplayName();
			}
			else
			{
				itemString = item.getType().toString()+"-"+item.getAmount();
			}
		}
		kits.set(path+"."+slot.getName(), itemString);
		try {
			kits.save(kitConfig);
		} catch (IOException e) {
			e.printStackTrace();
			Bukkit.getServer().getLogger().severe("[TF2] Error occured whilst in saving the kit file!");
		}
	}
}
