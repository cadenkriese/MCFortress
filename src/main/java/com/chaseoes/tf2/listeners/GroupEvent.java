package com.chaseoes.tf2.listeners;

import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.events.GMUserEvent;
import org.anjocaido.groupmanager.events.GMUserEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.utilities.SQLUtilities;

public class GroupEvent 
implements Listener
{
	@EventHandler
	public void GroupChangeEvent(final GMUserEvent e)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				if (e.getAction() == Action.USER_GROUP_CHANGED)
				{
					Player p = e.getUser().getBukkitPlayer();
					Group g = e.getUser().getGroup();
					
					SQLUtilities.getUtilities().execUpdate("INSERT INTO "
							+ "player_info(uuid, rank) "
							+ "VALUES ('"+p.getUniqueId()+"', '"+g.getName()+"') "
							+ "ON DUPLICATE KEY UPDATE rank='"+g.getName()+"'");
				}
			}
		}, 20L);
	}
}
