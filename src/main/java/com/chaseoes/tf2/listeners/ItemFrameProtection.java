package com.chaseoes.tf2.listeners;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;

public class ItemFrameProtection implements Listener {

	@EventHandler
	public void EntityDamageByEntity(HangingBreakByEntityEvent e)
	{
		if (e.getRemover() instanceof Player)
		{
			Player p = (Player) e.getRemover();
			GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
			if (gp.isIngame())
			{
				if (e.getEntity() instanceof ItemFrame)
				{
					e.setCancelled(true);
					return;
				}
			}
		}
		else
		{
			if (e.getEntity() instanceof ItemFrame)
			{
				e.setCancelled(true);
			}
		}
	}
}
