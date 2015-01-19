package com.chaseoes.tf2.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChangeListener implements Listener
{
	@EventHandler
	public void WeatherChange(WeatherChangeEvent e)
	{
		e.setCancelled(true);
	}
}
