package com.chaseoes.tf2.boosters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.events.BoosterEndEvent;
import com.chaseoes.tf2.events.BoosterStartEvent;

public class BoosterStatusFile 
{
	public BoosterStatusFile() {}
	public static BoosterStatusFile instance = new BoosterStatusFile();
	public File boosterStatus = null;
	public FileConfiguration status = null;
	public boolean loaded = false;
	static int booster = 0;
	static int time = 0;
	static int boost = 0;
	static Booster currentBooster = null;
	static String name = "";

	public static BoosterStatusFile getFile()
	{
		return instance;
	}

	public void loadConfiguration(String folder)
	{
		boosterStatus = new File(folder);
		status = YamlConfiguration.loadConfiguration(boosterStatus);
	}

	public void saveFiles()
	{
		if (!loaded)
		{
			loadConfiguration("plugins/TF2/boosters/status.yml");
			loaded = true;
		}

		try 
		{
			status.save(boosterStatus);
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
			loadConfiguration("plugins/TF2/boosters/status.yml");
			loaded = true;
		}

		if(boosterStatus.exists())
		{
			try 
			{
				status.load(boosterStatus);
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
				status.save(boosterStatus);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}	
	}

	public void setBooster(Booster b)
	{
		if (status.getKeys(true).size() == 0)
		{
			BoosterStartEvent event = new BoosterStartEvent(b);
			Bukkit.getServer().getPluginManager().callEvent(event);


			name = b.getPlayerName();
			time = b.getTime();
			boost = b.getBoost();
			currentBooster = b;

			status.set("current-booster", b.toString());
			status.set("time-remaining", b.getTime());
			try 
			{
				status.save(boosterStatus);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			startCounter("time-remaining");
		}
	}

	public void startCounter(final String path)
	{
		booster = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				int i = status.getInt(path);
				if (i == 0)
				{
					BoosterEndEvent event = new BoosterEndEvent(getCurrentBooster());
					Bukkit.getServer().getPluginManager().callEvent(event);

					booster = 0;
					time = 0;
					boost = 0;
					name = "";
					currentBooster = null;
					status.set(path, null);
					status.set("current-booster", null);
					try {
						status.save(boosterStatus);
					} catch (IOException e) {
						Bukkit.getServer().getLogger().severe("Error occored while saving the booster time to the flat file.");
						e.printStackTrace();
					}

					Bukkit.getServer().getScheduler().cancelTask(booster);

					Booster b = BoosterQueueFile.getFile().getNextbooster();
					if (b == null)
					{
						return;
					}
					setBooster(b);

					return;
				}
				time--;
				status.set(path, i-1);
				try {
					status.save(boosterStatus);
				} 
				catch (IOException e) 
				{
					Bukkit.getServer().getLogger().severe("Error occored while saving the booster time to the flat file.");
					e.printStackTrace();
				}
			}
		}, 20L, 20L);
	}

	public Booster getCurrentBooster()
	{
		if (status.getString("current-booster") != null)
		{
			String boosterString = status.getString("current-booster");
			Pattern p = Pattern.compile("(\\S+)_(\\d+)_(\\d+)");
			Matcher match = p.matcher(boosterString);
			if (match.find())
			{
				Booster booster = new Booster(match.group(1), Integer.valueOf(match.group(2)), Integer.valueOf(match.group(3)));
				currentBooster = booster;
				return booster;
			}
			return null;
		}
		return null;
	}

	public void pauseBooster()
	{
		if (getCurrentBooster() != null)
		{
			Bukkit.getServer().getScheduler().cancelTask(booster);
			booster = 0;
		}
		try 
		{
			status.save(boosterStatus);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void resumeBooster()
	{
		if (status.getKeys(false).size() == 2 && getCurrentBooster() != null)
		{
			time = status.getInt("time-remaining");
			startCounter("time-remaining");

			Booster b = getCurrentBooster();
			BoosterStartEvent event = new BoosterStartEvent(b);
			Bukkit.getServer().getPluginManager().callEvent(event);
		}
	}
	
	public String getBoosterMessage()
	{
		String boosterMessage = "";
		if (getCurrentBooster() != null)
		{
			Booster booster = getCurrentBooster();
			boost = booster.getBoost();
			boosterMessage = " §3x"+boost+" §e"+booster.getPlayerName()+"'s Credit Booster";
		}
		if (TF2.getInstance().getConfig().getBoolean("double-coins"))
		{
			boost++;
			if (getCurrentBooster() != null)
			{
				boosterMessage = " §3x"+boost+" §e"+getCurrentBooster().getPlayerName()+"'s Booster §7+ §eDouble Credits";
			}
			else
			{
				boosterMessage = boosterMessage+" §3x2 §eDouble Credits";
			}
		}
		return boosterMessage;
	}
	
	public int getBoost()
	{
		int boost = 1;
		if (getCurrentBooster() != null)
		{
			boost = getCurrentBooster().getBoost();
		}
		if (TF2.getInstance().getConfig().getBoolean("double-coins"))
		{
			boost++;
		}
		return boost;
	}
}
