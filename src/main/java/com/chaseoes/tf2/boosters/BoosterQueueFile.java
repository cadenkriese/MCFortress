package com.chaseoes.tf2.boosters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class BoosterQueueFile 
{
	public BoosterQueueFile() {}
	public static BoosterQueueFile instance = new BoosterQueueFile();
	public static File boosterQueue = null;
	public static FileConfiguration queue = null;
	public static boolean loaded = false;
	int booster = 0;
	static ArrayList<Booster> queueList = new ArrayList<Booster>();
	static ArrayList<String> stringList = new ArrayList<String>();

	public static BoosterQueueFile getFile()
	{
		return instance;
	}

	public void loadConfiguration(String folder)
	{
		boosterQueue = new File(folder);
		queue = YamlConfiguration.loadConfiguration(boosterQueue);
	}

	public void saveFiles()
	{
		if (!loaded)
		{
			loadConfiguration("plugins/TF2/boosters/queue.yml");
			loaded = true;
		}

		try 
		{
			queue.save(boosterQueue);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public void loadFiles()
	{
		if ( !loaded)
		{
			loadConfiguration("plugins/TF2/boosters/queue.yml");
			loaded = true;
		}

		if(boosterQueue.exists())
		{
			try 
			{
				queue.load(boosterQueue);
			} 
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (InvalidConfigurationException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			try 
			{
				queue.save(boosterQueue);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}	
	}

	public void addBooster(Booster b)
	{
		Player p = Bukkit.getPlayer(b.getPlayerName());
		if (queueList.size() <= 0)
		{
			BoosterStatusFile.getFile();
			if (BoosterStatusFile.getFile().getCurrentBooster() == null)
			{
				BoosterStatusFile.getFile().setBooster(b);
				return;
			}
		}
		p.sendMessage(ChatColor.GREEN+"Booster "+ChatColor.WHITE+ChatColor.BOLD.toString()+"» "+ChatColor.DARK_AQUA+"Your booster has been put in the queue and it will be activated in approxomitely "+ChatColor.YELLOW+getETA(b)+ChatColor.DARK_AQUA+" hours.");
		queueList.add(b);
		stringList.add(b.toString());
		queue.set("queue", stringList);
		try 
		{
			queue.save(boosterQueue);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public int getETA(Booster b)
	{
		int time1 = BoosterStatusFile.getFile().getCurrentBooster().getTime();
		int time2 = 0;
		for (Booster b2 : queueList)
		{
			if (b2 == b)
			{
				break;
			}
			time2 = time2+b2.getTime();
		}
		int time3 = time1+time2;
		return time3/60/60;
	}

	public Booster getNextbooster()
	{
		if (!queueList.isEmpty())
		{
			Booster b = queueList.get(0);
			queueList.remove(0);
			
			queue.set("queue", queueList);
			try 
			{
				queue.save(boosterQueue);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return b;
		}
		return null;
	}

	public void updateArray()
	{
		if (queue.getKeys(false).size() == 0)
		{
			return;
		}
		ArrayList<Booster> newQueueList = new ArrayList<Booster>();
		ArrayList<String> newStringList = new ArrayList<String>();
		Pattern p = Pattern.compile("(\\S+)_(\\d+)_(\\d+)");
		for (Object o : queue.getList("queue"))
		{
			if (o instanceof String)
			{
				String s = (String) o; 
				Matcher match = p.matcher(s);
				if (match.find())
				{
					String name = match.group(1);
					int time = Integer.valueOf(match.group(2));
					int boost = Integer.valueOf(match.group(3));
					Booster b = new Booster(name, time, boost);
					newQueueList.add(b);
					newStringList.add(s);
				}
			}
		}
		queueList = newQueueList;
		stringList = newStringList;
	}
}
