package com.chaseoes.tf2.listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.chaseoes.tf2.*;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(event.getPlayer());
        if (!gp.isIngame())
        {
    		Player p = event.getPlayer();
    		Inventory pInv = p.getInventory();
    		ItemStack statsItem = new ItemStack (Material.SKULL_ITEM,1, (byte)3);
    		SkullMeta SIM = (SkullMeta) statsItem.getItemMeta();
    		SIM.setDisplayName(ChatColor.GREEN + "Stats");
    		ArrayList<String> skullLore = new ArrayList<String>();
    		skullLore.add(ChatColor.GRAY + "Displays your stats");
    		skullLore.add(ChatColor.GRAY + "From the TF2 game.");
    		SIM.setLore(skullLore);
    		SIM.setOwner(p.getName());
    		statsItem.setItemMeta(SIM);
    		if (!pInv.contains(statsItem));
    		{
    			pInv.setItem(0, statsItem);
    		}
        }
        Game game = gp.getGame();
		Player p = gp.getPlayer();
        if (game == null) {
            return;
        }
        if (gp.getGame() == null || gp.getGame().getMap() == null || gp == null || gp.getTeam() == null)
        {
        	p.sendMessage(ChatColor.YELLOW+"[TF2] An internal error has occured please contact an owner or staff member.");
        }
        event.setRespawnLocation(gp.getSpawnLoc());
        p.teleport(gp.getSpawnLoc());
		p.setFireTicks(0);
		PlayerInventory inv = p.getInventory();
		inv.clear();
		inv.setArmorContents(null);
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 1, true));
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, true));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 3, true));
		p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 10000, 1000000));
		p.setAllowFlight(true);
		p.setFlying(true);
		p.setFlySpeed(0.3f);
		Location teamspawn = MapUtilities.getUtilities().loadTeamSpawn(game.getMap().toString(), gp.getTeam());
		TF2DeathListener.getListener().respawnDelay(p, gp, gp.getKiller(), gp.getGame().getMap(), teamspawn, game);
		gp.setSpawnLoc(null);
    }
}
