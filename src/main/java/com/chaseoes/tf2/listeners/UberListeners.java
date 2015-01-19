package com.chaseoes.tf2.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.extras.MediGunHandler;
import com.chaseoes.tf2.utilities.ItemUtilities;
import com.chaseoes.tf2.utilities.MedicUtilities;

public class UberListeners
implements Listener
{
	@EventHandler
	public void playerSwitchItemEvent(PlayerItemHeldEvent event)
	{
		Player p = event.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);

		//Stop the uber if the medic changes items
		if (gp.isUbering())
		{
			ItemStack current = p.getInventory().getItem(event.getNewSlot());
			if (current != null && current.getType() != Material.AIR)
			{
				if (current.getType() == Material.DIAMOND_PICKAXE || current.getType() == Material.IRON_PICKAXE)
				{
					MedicUtilities.getUtilities().setUbered(p, true);
					ItemUtilities.getUtilities().setGlowing(current, true);
					ItemStack previous = p.getInventory().getItem(event.getPreviousSlot());
					if (previous != null)
					{
						ItemUtilities.getUtilities().setGlowing(previous, false);
					}
				}
				else
				{
					MedicUtilities.getUtilities().setUbered(p, false);
					if (gp.getHealing() != null)
					{
						MedicUtilities.getUtilities().setUbered(gp.getHealing(), false);
					}
				}
			}
			else
			{
				MedicUtilities.getUtilities().setUbered(p, false);
				if (gp.getHealing() != null)
				{
					MedicUtilities.getUtilities().setUbered(gp.getHealing(), false);
				}	
			}
		}

		if (gp.isUbered() && !gp.isUbering())
		{
			ItemStack previous = p.getInventory().getItem(event.getPreviousSlot());
			ItemStack current = p.getInventory().getItem(event.getNewSlot());
			if (previous != null && previous.getType() != Material.AIR)
			{
				ItemUtilities.getUtilities().setGlowing(previous, false);
			}
			if (current != null && current.getType() != Material.AIR)
			{
				ItemUtilities.getUtilities().setGlowing(current, true);
			}
		}

		//Cancel the healing process if the medic changes items
		if (gp.getHealing() != null)
		{
			ItemStack current = p.getInventory().getItem(event.getNewSlot());

			if (current == null)
			{
				MediGunHandler.getHandler().stopHealingProcess(p, gp.getHealing());
			}
			else if (current.getType() != Material.DIAMOND_PICKAXE && current.getType() != Material.IRON_PICKAXE)
			{
				MediGunHandler.getHandler().stopHealingProcess(p, gp.getHealing());
			}
		}
	}

	@EventHandler
	public void interactEvent(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);

		if (e.getItem() != null && (e.getItem().getType() == Material.DIAMOND_PICKAXE || e.getItem().getType() == Material.IRON_PICKAXE))
		{
			if (gp.getCurrentClass().getName().equalsIgnoreCase("medic"))
			{
				MediGunHandler.getHandler().heal(e.getAction(), p);
			}
		}
	}
}
