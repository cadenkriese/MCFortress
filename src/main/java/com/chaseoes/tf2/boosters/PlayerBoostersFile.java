package com.chaseoes.tf2.boosters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerBoostersFile 
{
	public PlayerBoostersFile() {}
	public static PlayerBoostersFile instance = new PlayerBoostersFile();
	public static File playerBoosters = null;
	public static FileConfiguration boosters = null;
	public static boolean loaded = false;

	public static PlayerBoostersFile getFile()
	{
		return instance;
	}

	public void loadConfiguration(String folder)
	{
		playerBoosters = new File(folder);
		boosters = YamlConfiguration.loadConfiguration(playerBoosters);
	}

	public void saveFiles()
	{
		if (!loaded)
		{
			loadConfiguration("plugins/TF2/boosters/players.yml");
			loaded = true;
		}

		try 
		{
			boosters.save(playerBoosters);
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
			loadConfiguration("plugins/TF2/boosters/players.yml");
			loaded = true;
		}

		if(playerBoosters.exists())
		{
			try 
			{
				boosters.load(playerBoosters);
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
				boosters.save(playerBoosters);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}	
	}

	public void addBooster(String name, Booster b)
	{
		if (boosters.get(name) != null)
		{
			ArrayList<String> boosterList = new ArrayList<String>();
			for (Object obj : boosters.getList(name))
			{
				if (obj instanceof String)
				{
					String string = (String) obj;
					boosterList.add(string);
				}
			}
			boosterList.add(b.toString());
			boosters.set(name, boosterList);
			
			try 
			{
				boosters.save(playerBoosters);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			ArrayList<String> boosterList = new ArrayList<String>();
			boosterList.add(b.toString());
			boosters.set(name, boosterList);

			try 
			{
				boosters.save(playerBoosters);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void activateBooster(Booster b)
	{
		if (boosters.contains(b.getPlayerName()))
		{
			if (boosters.getList(b.getPlayerName()).contains(b.toString()))
			{
				List<?> bList = boosters.getList(b.getPlayerName());
				ArrayList<String> newBList = new ArrayList<String>();
				for (Object o : bList)
				{
					if (o instanceof String)
					{
						String s = (String) o;
						if (s.equalsIgnoreCase(b.toString()))
						{
							continue;
						}
						newBList.add(s);
					}
				}
				bList.remove(b.toString());
				BoosterQueueFile.getFile().addBooster(b);
				
				try
				{
					boosters.save(playerBoosters);
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	public ArrayList<Booster> getBoosters(Player p)
	{
		if (boosters.contains(p.getName()))
		{
			Pattern pattern = Pattern.compile("(\\S+)_(\\d+)_(\\d+)");
			ArrayList<Booster> boosterList = new ArrayList<Booster>();
			for (Object o : boosters.getList(p.getName()))
			{
				if (o instanceof String)
				{
					String s = (String) o;
					Matcher match = pattern.matcher(s);
					if (match.find())
					{
						Booster b = new Booster(match.group(1), Integer.valueOf(match.group(2)), Integer.valueOf(match.group(3)));
						boosterList.add(b);
					}
				}
			}
			return boosterList;
		}
		return null;
	}
}
