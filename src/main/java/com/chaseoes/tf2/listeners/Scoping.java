package com.chaseoes.tf2.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.classes.ClassChest;
import com.chaseoes.tf2.sound.TFSound;
import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class Scoping implements Listener{

	static HashMap<Player, Integer> scopeMap = new HashMap<Player, Integer>();

	@EventHandler
	public void setPumpkinHead(PlayerToggleSneakEvent sneakEvent)
	{

		Player p = sneakEvent.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		if (gp.JustFired())
		{
			return;
		}
		if (sneakEvent.isSneaking())
		{
			if (gp.isIngame() && !gp.isInLobby())
			{
				if (gp.getCurrentClass() != null && gp.getCurrentClass().getName().equalsIgnoreCase("Sniper"))
				{
					if (p.getInventory().getHeldItemSlot() == 0)
					{
						gp.setScoping(true);
						ItemStack holdingStack = p.getItemInHand();
						if (holdingStack.getType() == Material.GOLD_SPADE ||
								holdingStack.getType() == Material.GOLD_PICKAXE ||
								holdingStack.getType() == Material.GOLD_AXE)
						{
							PlayerInventory inventory = p.getInventory();
							ItemStack itemStack = new ItemStack(Material.PUMPKIN,1);
							inventory.setHelmet(itemStack);
							p.updateInventory();
							p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20000, -20));
							p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20000, 10));
							startCharge(gp);
						}
					}
				}
			}
			else
			{
				return;
			}
		}
		else
		{
			// remove pumpkin
			ItemStack holdingStack = p.getItemInHand();
			if (holdingStack.getType() == Material.GOLD_SPADE || holdingStack.getType() == Material.GOLD_PICKAXE || holdingStack.getType() == Material.GOLD_AXE)
			{
				if (gp.isScoping())
				{
					if (gp.getCurrentClass() != null)
					{
						PlayerInventory inventory = p.getInventory();
						ClassChest pClass = new ClassChest(gp.getCurrentClass().getName());
						inventory.setHelmet(pClass.getHelmet());
						p.updateInventory();
						p.removePotionEffect(PotionEffectType.SPEED);
						p.removePotionEffect(PotionEffectType.NIGHT_VISION);
						gp.setScopeCharge(0);
						stopCharge(gp);
						gp.setScoping(false);
					}
				}
			}
		}
	}

	@EventHandler
	public void changeItemEvent(PlayerItemHeldEvent e)
	{
		Player p = e.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		if (!gp.JustFired())
		{
			if (gp.isIngame())
			{
				if (gp.getCurrentClass() != null && gp.getCurrentClass().getName().equalsIgnoreCase("sniper"))
				{
					PlayerInventory inventory = p.getInventory();
					ClassChest pClass = new ClassChest(gp.getCurrentClass().getName());
					inventory.setHelmet(pClass.getHelmet());
					p.updateInventory();
					p.removePotionEffect(PotionEffectType.SPEED);
					p.removePotionEffect(PotionEffectType.NIGHT_VISION);
					gp.setScopeCharge(0);
					stopCharge(gp);
					gp.setScoping(false);
				}
			}
		}
	}

	public static void startCharge(final GamePlayer gp)
	{
		final Player p = gp.getPlayer();
		scopeMap.put(p, TF2.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
		{
			boolean soundSent = false;
			String actionBar = "§e||||||||||||| §6";
			public void run()
			{
				if (gp.isScoping())
				{
					if (gp.isDead())
					{
						stopCharge(gp);
						return;
					}
					if (gp.JustFired())
					{
						stopCharge(gp);
						return;
					}
					if (gp.getScopeCharge() == 13)
					{
						if (!soundSent)
						{
							TFSound.RECHARGED.send(p, 1f, 1f);
							soundSent = true;
						}
						actionBar = "§e||||||||||||| §6100%";
						ActionBarAPI.sendActionBar(p, actionBar);
					}
					else
					{
						gp.setScopeCharge(gp.getScopeCharge()+1);
						if (gp.getScopeCharge() == 1)
						{
							actionBar = "§8||||||||||||| §67%";
							ActionBarAPI.sendActionBar(gp.getPlayer(), actionBar);
						}
						else
						{
							long percent = Math.round(gp.getScopeCharge()*6.6);
							actionBar = "§e||||||||||||| §6";
							actionBar = actionBar.substring(0, gp.getScopeCharge())+ChatColor.DARK_GRAY+actionBar.substring(gp.getScopeCharge())+percent+"%";
							ActionBarAPI.sendActionBar(gp.getPlayer(), actionBar);
						}
					}
				}
				else
				{
					gp.setScopeCharge(0);
				}
			}
		}, 7L, 7L));
	}

	public static void stopCharge(final GamePlayer gp)
	{
		if (scopeMap.containsKey(gp.getPlayer()))
		{
			Bukkit.getServer().getScheduler().cancelTask(scopeMap.get(gp.getPlayer()));
			scopeMap.remove(gp.getPlayer());
		}
	}

	public static void resetCharge(final GamePlayer gp)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable()
		{
			public void run()
			{
				Player p = gp.getPlayer();
				gp.setJustFired(false);
				gp.setScopeCharge(0);
				if (!gp.isIngame() || gp.isInLobby())
				{
					return;
				}
				if (p.isSneaking())
				{
					gp.setScoping(true);
					ItemStack holdingStack = p.getItemInHand();
					if (holdingStack.getType() == Material.GOLD_SPADE ||
							holdingStack.getType() == Material.GOLD_PICKAXE ||
							holdingStack.getType() == Material.GOLD_AXE)
					{
						PlayerInventory inventory = p.getInventory();
						ItemStack itemStack = new ItemStack(Material.PUMPKIN,1);
						inventory.setHelmet(itemStack);
						p.updateInventory();
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20000, -20));
						p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20000, 10));
					}
					
					startCharge(gp);
				}
			}
		}, 30L);
	}
}
