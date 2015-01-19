package com.chaseoes.tf2.classes;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.Slot;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.Team;
import com.chaseoes.tf2.extras.MediGunHandler;
import com.chaseoes.tf2.extras.UberActionBar;
import com.chaseoes.tf2.localization.Localizers;
import com.chaseoes.tf2.utilities.ItemUtilities;
import com.shampaggon.crackshot.CSUtility;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.logging.Level;

public class TF2Class {

	private String name;
	private ClassChest classChest;

	public TF2Class(String name) {
		this.name = name;
		classChest = new ClassChest(getName());
	}

	public String getName() {
		return name;
	}

	// Apply the class to a player (returns true if it was successful).
	public boolean apply(GamePlayer player)
	{
		// Check that the class exists.
		Player p = player.getPlayer();
		if (!classChest.exists()) {
			
			Localizers.getDefaultLoc().DOES_NOT_EXIST_CLASS.sendPrefixed(player.getPlayer(), name);
			clearInventory(player.getPlayer());
			return false;
		}

		if (player.isIngame())
		{
			try 
			{

				// Clear their inventory.
				clearInventory(p);

				// Loop through potion effects.
				@SuppressWarnings("unused")
				boolean apply = true;
				if (player.isInLobby() && TF2.getInstance().getConfig().getBoolean("potion-effects-after-start")) 
				{
					apply = false;
				}

				// Loop through chest items.
				for (ItemStack i : classChest.getClassItems()) 
				{
					// Check the name of water bottle for custom potion effects.
					// Should be in this format: POTION_NAME AMPLIFIER TIME_IN_SECONDS
					boolean give = true;
					if (i.getType() == Material.POTION) 
					{
						if (i.hasItemMeta())
						{
							if (i.getItemMeta().hasDisplayName()) 
							{
								if (i.getItemMeta().getDisplayName().toLowerCase().startsWith("classlimit")) 
								{
									int limit = Integer.parseInt(i.getItemMeta().getDisplayName().toLowerCase().replace("classlimit ", ""));
									int amount = 0;

									for (GamePlayer gp : player.getGame().playersInGame.values()) 
									{
										if (gp.getCurrentClass() != null) 
										{
											if (gp.getCurrentClass().getName().equals(getName()))
											{
												amount++;
											}
										}
									}

									if (amount >= limit) 
									{
										Localizers.getDefaultLoc().LIMIT_REACHED_CLASS.sendPrefixed(player.getPlayer());
										clearInventory(player.getPlayer());
										return false;
									}
								}

								for (PotionEffectType type : PotionEffectType.values()) 
								{
									if (type != null)
									{
										if (i.getItemMeta().getDisplayName().toLowerCase().startsWith(type.getName().toLowerCase())) 
										{
											if (!(player.isInLobby() && TF2.getInstance().getConfig().getBoolean("potion-effects-after-start"))) 
											{
												String[] parts = i.getItemMeta().getDisplayName().split(" ");
												PotionEffectType potionType = PotionEffectType.getByName(parts[0].toUpperCase());
												int amplifier = Integer.parseInt(parts[1]) - 1;
												int duration = 0;

												if (parts[2].equalsIgnoreCase("forever")) 
												{
													duration = Integer.MAX_VALUE;
												}
												else 
												{
													duration = Integer.parseInt(parts[2]) * 20;
												}

												PotionEffect e = new PotionEffect(potionType, duration, amplifier);
												player.getPlayer().addPotionEffect(e);
											}
											give = false;
										}
									}
								}
								if (give)
								{
									CSUtility cUtil = new CSUtility();
									if (cUtil.getWeaponTitle(i) != null)
									{
										i = cUtil.generateWeapon(cUtil.getWeaponTitle(i));
									}
								}
							}
						}
					}

					if (give) 
					{
						player.getPlayer().getInventory().addItem(i);
					}
				}

				// Add armor items.
				player.getPlayer().getInventory().setHelmet(ItemUtilities.getUtilities().setColor(classChest.getHelmet(), player.getTeam().getColor()));
				player.getPlayer().getInventory().setChestplate(ItemUtilities.getUtilities().setColor(classChest.getChestplate(), player.getTeam().getColor()));
				player.getPlayer().getInventory().setLeggings(ItemUtilities.getUtilities().setColor(classChest.getLeggings(), player.getTeam().getColor()));
				player.getPlayer().getInventory().setBoots(ItemUtilities.getUtilities().setColor(classChest.getBoots(), player.getTeam().getColor()));

				//Loadout logic
				player.getPlayer().getInventory().setItem(0, ClassDataFile.getItem(player.getPlayer().getUniqueId().toString()+"."+name, Slot.PRIMARY, name));
				player.getPlayer().getInventory().setItem(1, ClassDataFile.getItem(player.getPlayer().getUniqueId().toString()+"."+name, Slot.SECONDARY, name));
				player.getPlayer().getInventory().setItem(2, ClassDataFile.getItem(player.getPlayer().getUniqueId().toString()+"."+name, Slot.MELEE, name));

				CSUtility cUtil = new CSUtility();


				if (name.equalsIgnoreCase("pyro") || name.equalsIgnoreCase("demoman"))
				{
					p.setMaxHealth(28d);
					p.setHealth(p.getMaxHealth());
					if (ClassDataFile.getItem(player.getPlayer().getUniqueId().toString()+"."+name, Slot.SECONDARY, name).getType() == Material.STONE_SPADE)
					{
						ItemStack is = new ItemStack(Material.INK_SACK, 32, (byte) 1);
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(ChatColor.YELLOW+"12 Guage");
						is.setItemMeta(im);
						p.getInventory().setItem(7, is);
					}
				}
				else if (name.equalsIgnoreCase("soldier"))
				{
					p.setMaxHealth(32d);
					p.setHealth(p.getMaxHealth());
				}
				else if (name.equalsIgnoreCase("heavy"))
				{
					p.setMaxHealth(48d);
					p.setHealth(p.getMaxHealth());
				}
				else
				{
					p.setMaxHealth(20d);
					p.setHealth(p.getMaxHealth());
				}

				CSUtility cUtil1 = new CSUtility();

				if (name.equalsIgnoreCase("soldier"))
				{
					if (cUtil.getWeaponTitle(p.getInventory().getItem(0)).equalsIgnoreCase("jumper"))
					{
						p.getInventory().getItem(3).setAmount(60);
					}
				}

				if (name.equalsIgnoreCase("pyro") && (player.getPlayer().getInventory().getItem(0).getType() == Material.WOOD_AXE || player.getPlayer().getInventory().getItem(0).getType() == Material.DIAMOND_HOE))
				{
					switch (player.getTeam())
					{
					case RED: player.getPlayer().getInventory().setItem(1, cUtil1.generateWeapon(cUtil1.getWeaponTitle(new ItemStack(Material.DIAMOND_HOE))));

					case BLUE: player.getPlayer().getInventory().setItem(1, cUtil1.generateWeapon(cUtil1.getWeaponTitle(new ItemStack(Material.WOOD_AXE))));
					}
				}

				if (name.equalsIgnoreCase("spy") && player.getTeam() == Team.BLUE)
				{
					ClassChest blueSpy = new ClassChest("bspy");
					if (blueSpy != null)
					{
						player.getPlayer().getInventory().setHelmet(blueSpy.getHelmet());
					}
				}

				if (name.equalsIgnoreCase("medic") && (player.getPlayer().getInventory().getItem(1).getType() == Material.DIAMOND_PICKAXE || player.getPlayer().getInventory().getItem(1).getType() == Material.IRON_PICKAXE))
				{	
					p.setMaxHealth(24d);
					p.setHealth(p.getMaxHealth());
					UberActionBar.getBar().startBar(p);


					ItemStack i = new ItemStack(Material.DIAMOND_PICKAXE);
					ItemMeta im = i.getItemMeta();
					im.setDisplayName(ChatColor.YELLOW+"The Medi Gun");
					ArrayList<String> lore = new ArrayList<String>();
					lore.add(ChatColor.GRAY+"Level 1 Medi Gun");
					im.setLore(lore);
					i.setItemMeta(im);

					if (player.getTeam() == Team.RED)
					{
						i.setType(Material.DIAMOND_PICKAXE);
					}
					else
					{
						i.setType(Material.IRON_PICKAXE);
					}
					
					ItemStack syringe = new ItemStack(Material.WOOD_SPADE);
					ItemMeta sm = syringe.getItemMeta();
					sm.setDisplayName(ChatColor.YELLOW+"Syringe Gun");
					ArrayList<String> sLore = new ArrayList<String>();
					sLore.add(ChatColor.GRAY+"Level 1 Syringe Gun");
					sm.setLore(sLore);
					syringe.setItemMeta(im);

					if (player.getTeam() == Team.RED)
					{
						syringe.setType(Material.WOOD_SPADE);
					}
					else
					{
						syringe.setType(Material.WOOD_PICKAXE);
					}

					p.getInventory().setItem(0, syringe);
					p.getInventory().setItem(1, i);
				}
				else
				{
					if (player.getHealing() != null)
					{
						MediGunHandler.getHandler().stopHealingProcess(p, player.getHealing());
					}
					UberActionBar.getBar().endBar(p);

					player.setUberPercent(0.0f);
				}

				player.setCurrentClass(this);

				player.getPlayer().updateInventory();
				return true;
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				TF2.getInstance().getLogger().log(Level.SEVERE, "The error encountered while changing a player's class is above! Note that TF2 v2.0 has a new format for defining items - click here to view the new default configuration: http://goo.gl/LdKKR");
				Localizers.getDefaultLoc().ERROR_CHANGE_CLASS.sendPrefixed(player.getPlayer());
				clearInventory(player.getPlayer());
				return false;
			}
		}
		return false;
	}

	public boolean canUse(Player player) {
		return player.hasPermission("tf2.class." + getName());
	}

	public void clearInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
		player.getInventory().setChestplate(new ItemStack(Material.AIR));
		player.getInventory().setLeggings(new ItemStack(Material.AIR));
		player.getInventory().setBoots(new ItemStack(Material.AIR));
		player.setItemOnCursor(new ItemStack(Material.AIR));

		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

		player.updateInventory();
	}

	public boolean ammoKit(Player p)
	{
		GamePlayer player = GameUtilities.getUtilities().getGamePlayer(p);

		boolean returnType = false;

		for (int slot = 18; slot < 23; slot++)
		{
			int slotReduced = slot-15;

			ItemStack oldItem = p.getInventory().getItem(slotReduced);
			ItemStack newItem = classChest.getItemFromSlot(slot);

			if (oldItem != null && newItem != null)
			{
				if (oldItem.getType() == newItem.getType() && oldItem.getAmount() < newItem.getAmount())
				{
					returnType = true;
				}
			}
		}

		if (!returnType)
		{
			return false;
		}

		for (int slot = 18; slot < 23; slot++)
		{
			p.getInventory().setItem(slot-15, classChest.getItemFromSlot(slot));
		}

		CSUtility cUtil = new CSUtility();

		if (name.equalsIgnoreCase("soldier"))
		{
			if (cUtil.getWeaponTitle(p.getInventory().getItem(0)).equalsIgnoreCase("jumper"))
			{
				p.getInventory().getItem(3).setAmount(60);
			}
		}

		if (name.equalsIgnoreCase("pyro") && (player.getPlayer().getInventory().getItem(0).getType() == Material.WOOD_AXE || player.getPlayer().getInventory().getItem(0).getType() == Material.DIAMOND_HOE))
		{
			switch (player.getTeam())
			{
			case RED: player.getPlayer().getInventory().setItem(1, cUtil.generateWeapon(cUtil.getWeaponTitle(new ItemStack(Material.DIAMOND_HOE))));

			case BLUE: player.getPlayer().getInventory().setItem(1, cUtil.generateWeapon(cUtil.getWeaponTitle(new ItemStack(Material.WOOD_AXE))));
			}
		}

		p.updateInventory();
		return true;
	}
}
