package com.chaseoes.tf2.listeners;

import com.chaseoes.tf2.localization.Localizers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.TF2;

public class InventoryClickListener implements Listener {

    private TF2 pl;

    public InventoryClickListener(TF2 tf2) {
        pl = tf2;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getName().equalsIgnoreCase(ChatColor.GOLD.toString()+"Disguise Menu"))
		{
			return;
		}
        if (event.getWhoClicked() != null && event.getWhoClicked() instanceof Player) {
            GamePlayer gp = GameUtilities.getUtilities().getGamePlayer((Player) event.getWhoClicked());
            if (gp.isIngame() && pl.getConfig().getBoolean("prevent-inventory-moving")) {
                event.setCancelled(true);
                Localizers.getDefaultLoc().PLAYER_INVENTORY_MOVING_BLOCKED.sendPrefixed(gp.getPlayer());
            }
            Player p = (Player) event.getWhoClicked();
            if (event.getInventory() == p.getInventory())
            {
            	if (!p.getName().equalsIgnoreCase("GamerKing195") && !p.hasPermission("tf2.moveinv"))
            	{
            		event.setCancelled(true);
            	}
            }
        }
    }
}
