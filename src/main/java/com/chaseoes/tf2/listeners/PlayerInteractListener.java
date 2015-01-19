package com.chaseoes.tf2.listeners;

import java.util.ArrayList;
import java.util.HashMap;

import com.chaseoes.tf2.localization.Localizers;
import com.chaseoes.tf2.packetwrappers.WrapperPlayServerEntityEquipment;
import com.chaseoes.tf2.particles.ParticleEffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.chaseoes.tf2.DataConfiguration;
import com.chaseoes.tf2.Game;
import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.Map;
import com.chaseoes.tf2.MapUtilities;
import com.chaseoes.tf2.Slot;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.Team;
import com.chaseoes.tf2.classes.ClassChest;
import com.chaseoes.tf2.classes.ClassUtilities;
import com.chaseoes.tf2.classes.TF2Class;
import com.chaseoes.tf2.commands.SpectateCommand;
import com.chaseoes.tf2.extras.FlameThrowerHandler;
import com.chaseoes.tf2.sound.TFSound;
import com.chaseoes.tf2.utilities.DataChecker;
import com.chaseoes.tf2.utilities.GeneralUtilities;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.connorlinfoot.titleapi.TitleAPI;
import com.shampaggon.crackshot.CSUtility;
import com.shampaggon.crackshot.events.WeaponPreShootEvent;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.disguise.PlayerDisguise;

public class PlayerInteractListener implements Listener {
	HashMap<String, Integer> invisMap = new HashMap<String, Integer>();
	static ArrayList<Player> noAttack = new ArrayList<Player>();
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) 
	{	
		Player p = event.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);


		CSUtility cUtil = new CSUtility();
		if (cUtil.getWeaponTitle(event.getItem())!= null && cUtil.getWeaponTitle(event.getItem()).equalsIgnoreCase("flame"))
		{
			FlameThrowerHandler.getHandler().fire(gp, event.getAction());
		}
		
		try {
			if (gp.isIngame()) {
				if (p.getItemInHand().getType() == Material.getMaterial(373) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
					if (gp.justSpawned()) {
						event.setCancelled(true);
						p.updateInventory();
					}
					if (gp.isInLobby()) {
						event.setCancelled(true);
						p.updateInventory();
					}
				}
			}


			if (event.hasBlock() && (event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST)) {
				Sign s = (Sign) event.getClickedBlock().getState();
				if (s.getLine(0).equalsIgnoreCase(Localizers.getDefaultLoc().LOBBYWALL_JOIN_1.getString()) && s.getLine(2).equalsIgnoreCase(Localizers.getDefaultLoc().LOBBYWALL_JOIN_3.getString())) {
					String map = TF2.getInstance().getADmap();
					Game game = GameUtilities.getUtilities().getGame(TF2.getInstance().getMap(map));
					Team team = game.decideTeam();

					DataChecker dc = new DataChecker(map);
					if (!dc.allGood()) {
						Localizers.getDefaultLoc().MAP_NOT_SETUP.sendPrefixed(p);
						if (p.hasPermission("tf2.create")) {
							Localizers.getDefaultLoc().MAP_NOT_SETUP_COMMAND_HELP.sendPrefixed(p, map);
						}
						return;
					}

					if (!p.hasPermission("tf2.play") || !p.hasPermission("tf2.join.sign")) {
						Localizers.getDefaultLoc().NO_PERMISSION.sendPrefixed(event.getPlayer());
						return;
					}

					if (gp.isIngame()) {
						Localizers.getDefaultLoc().PLAYER_ALREADY_PLAYING.sendPrefixed(event.getPlayer());
						return;
					}

					if (SpectateCommand.getCommand().isSpectating(gp.getPlayer())) {
						Localizers.getDefaultLoc().PLAYER_ALREADY_SPECTATING.sendPrefixed(event.getPlayer());
						return;
					}

					if (DataConfiguration.getData().getDataFile().getStringList("disabled-maps").contains(map)) {
						Localizers.getDefaultLoc().MAP_INFO_DISABLED.sendPrefixed(p);
						return;
					}

					game.joinGame(GameUtilities.getUtilities().getGamePlayer(p), team);
					event.setCancelled(true);
				}
			}

			if (event.hasBlock() && (event.getClickedBlock().getType() == Material.STONE_BUTTON || event.getClickedBlock().getType() == Material.WOOD_BUTTON)) 
			{
				if (gp.isIngame()) 
				{
					for (String s : DataConfiguration.getData().getDataFile().getStringList("classbuttons")) 
					{
						if (ClassUtilities.getUtilities().loadClassButtonLocation(s).toString().equalsIgnoreCase(event.getClickedBlock().getLocation().toString())) 
						{
							TF2Class c = new TF2Class(ClassUtilities.getUtilities().loadClassFromLocation(s));
							if (c.canUse(p))
							{
								if (c.apply(gp)) 
								{
									if (gp.isUsingChangeClassButton()) 
									{
										gp.setInLobby(false);
										gp.setUsingChangeClassButton(false);
										
										TF2Class classChosen = gp.getCurrentClass();
										classChosen.apply(gp);
										
										if (!gp.isDead() && gp.getGame().redHasBeenTeleported)
											TF2DeathListener.getListener().respawn(gp, gp.isDead());
										
										p.teleport(MapUtilities.getUtilities().loadTeamSpawn(gp.getGame().getMapName(), gp.getTeam()));

									}
								}
								return;
							}
							event.getPlayer().sendMessage(ChatColor.YELLOW + "[TF2] " + GeneralUtilities.colorize(TF2.getInstance().getConfig().getString("class-button-noperm")));
						}
					}

					for (String s : DataConfiguration.getData().getDataFile().getStringList("changeclassbuttons")) 
					{
						if (ClassUtilities.getUtilities().loadClassButtonLocation(s).toString().equalsIgnoreCase(event.getClickedBlock().getLocation().toString())) 
						{
							gp.setInLobby(true);
							gp.setUsingChangeClassButton(true);
							event.getPlayer().teleport(MapUtilities.getUtilities().loadTeamLobby(GameUtilities.getUtilities().getGamePlayer(p).getCurrentMap(), gp.getTeam()));
						}
					}
				}
			}

			if (event.hasBlock() && event.getClickedBlock().getState() instanceof InventoryHolder && gp.isCreatingContainer()) {
				if (TF2.getInstance().getMap(gp.getMapCreatingItemFor()).isContainerRegistered(event.getClickedBlock().getLocation())) {
					Localizers.getDefaultLoc().CONTAINER_ALREADY_REGSITERED.sendPrefixed(p);
				} else {
					Map map = TF2.getInstance().getMap(gp.getMapCreatingItemFor());
					map.addContainer(event.getClickedBlock().getLocation(), ((InventoryHolder) event.getClickedBlock().getState()).getInventory());
					Localizers.getDefaultLoc().CONTAINER_CREATED.sendPrefixed(p);
				}
				gp.setCreatingContainer(false);
				gp.setMapCreatingItemFor(null);
				event.setCancelled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}



		//spy invis
		if (gp.isIngame() && !gp.isInLobby())
		{
			if (((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getPlayer().getItemInHand().getType() == Material.DIAMOND_SWORD))
			{
				if (noAttack.contains(p))
				{
					ActionBarAPI.sendActionBar(p, ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3Please wait &e2 &3seconds before cloaking."));
					return;
				}
				else if (gp.getTeam() == Team.RED && gp.isOnCP())
				{
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You cannot cloak while capturing a point"));
					return;
				}
				else if (gp.isInvis())
				{
					//To start the invert
					stopInvis(p);
					gp.setInvis(false);
					gp.setInvertInvis(true);
					return;
				}
				else if (gp.InvertInvis())
				{
					//To go invisible
					ParticleEffect.SMOKE_NORMAL.display(0.2f, 0, 0.2f, 0, 1000, p.getLocation(), 255);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You're now cloaked, woosh!"));
					for (GamePlayer gp2 : gp.getGame().playersInGame.values())
					{
						Player p2 = gp2.getPlayer();
						p2.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 1);
						p2.hidePlayer(p);
					}
					int invisTime = gp.getInvisTime();
					cancelTask(p);
					gp.setInvisTime(invisTime);
					gp.setInvis(true);
					gp.setInvertInvis(false);
					spyInvis(p);
					return;
				}
				else
				{
					ParticleEffect.SMOKE_NORMAL.display(0.5f, 0.3f, 0.5f, 0, 20, p.getLocation(), 255);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You're now cloaked, woosh!"));
					for (GamePlayer gp2 : gp.getGame().playersInGame.values())
					{
						Player p2 = gp2.getPlayer();
						p2.playSound(p.getLocation(), Sound.ITEM_BREAK, 3, 1);
						p2.hidePlayer(p);
					}
					gp.setInvis(true);
					gp.setInvertInvis(false);
					spyInvis(p);
				}
			}
		}
	}
	public void spyInvis(final Player p)
	{
		final GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		invisMap.put(p.getName(), Bukkit.getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
		{
			int invisTime = 0;
			//double timeLeft = 10;
			String actionBar = "§8| §a:::::::::: §8| §7";
			@Override
			public void run()
			{
				if ((!gp.isInvis() && !gp.InvertInvis() && gp.getInvisTime() == 10) || (!gp.getCurrentClass().getName().equalsIgnoreCase("spy") && !gp.getCurrentClass().getName().equalsIgnoreCase("bspy")))
				{
					cancelTask(p);
				}
				if (!gp.isIngame())
				{
					gp.setInvis(false);
					gp.setInvertInvis(false);
					gp.setInvisTime(10);
					stopInvis(p);
					cancelTask(p);
				}
				if (gp.isDead())
				{
					gp.setInvis(false);
					gp.setInvertInvis(false);
					gp.setInvisTime(10);
					deadStopInvis(p);
				}
				invisTime++;
				if (!gp.InvertInvis() && invisTime % 20 == 0)
				{
					gp.setInvisTime(gp.getInvisTime()-1);
					actionBar = actionBar.substring(0, gp.getInvisTime()+6)+ChatColor.RED+actionBar.substring(gp.getInvisTime()+6);
					ActionBarAPI.sendActionBar(p, actionBar+gp.getInvisTime());
				}

				if (gp.getInvisTime() <= 0 || gp.InvertInvis())
				{
					if (invisTime == 200)
					{
						stopInvis(p);
						gp.setInvertInvis(true);
					}
					if (invisTime % 60 == 0)
					{
						gp.setInvisTime(gp.getInvisTime()+1);
						actionBar = "§8| §a:::::::::: §8| §7";
						actionBar = actionBar.substring(0, gp.getInvisTime()+6)+ChatColor.RED+actionBar.substring(gp.getInvisTime()+6);
					}
					ActionBarAPI.sendActionBar(p, actionBar+gp.getInvisTime());
					/*if (invisTime % 6 == 0)
					{
						timeLeft = timeLeft+0.1d;
					}*/
					if (gp.getInvisTime() >= 10)
					{
						TFSound.RECHARGED.send(p, 1f, 1f);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3Your cloak has recharged."));
						cancelTask(p);
					}
				}
			}
		}, 0L, 1L));
	}
	public static void stopInvis(Player p)
	{
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		gp.setInvis(false);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You're no longer cloaked."));
		for (GamePlayer gp2 : gp.getGame().playersInGame.values())
		{
			if (gp2 == null)
			{
				continue;
			}
			Player p2 = gp2.getPlayer();
			p2.showPlayer(p);
			p2.playSound(p.getLocation(), Sound.FUSE, 3, 1);
			ParticleEffect.SMOKE_NORMAL.display(0.5f, 0.3f, 0.5f, 0, 20, p.getLocation(), 255);
		}
		noAttack.add(p);
		resetNoAttack(p);
	}

	public void deadStopInvis(Player p)
	{
		if (!(invisMap.get(p.getName()) == null))
		{
			TF2.getInstance().getServer().getScheduler().cancelTask(invisMap.get(p.getName()));
			invisMap.remove(p.getName());
			GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
			gp.setInvertInvis(false);
			gp.setInvis(false);
			gp.setInvisTime(10);
		}
	}

	public void cancelTask(Player p)
	{
		if (invisMap.get(p.getName()) != null)
		{
			TF2.getInstance().getServer().getScheduler().cancelTask(invisMap.get(p.getName()));
			invisMap.remove(p.getName());
			GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
			gp.setInvertInvis(false);
			gp.setInvis(false);
			gp.setInvisTime(10);
			for (GamePlayer gp2 : gp.getGame().playersInGame.values())
			{
				Player p2 = gp2.getPlayer();
				p2.showPlayer(p);
			}
		}
	}
	public static void resetNoAttack(final Player p)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable() 
		{
			@Override
			public void run()
			{
				if (noAttack.contains(p))
				{
					noAttack.remove(p);
				}
			}
		}, 40L);
	}
	@EventHandler
	public void AttackEvent(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player)
		{
			Player p = (Player) e.getDamager();
			GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
			if (noAttack.contains(p) && !gp.isInvis())
			{
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3Please wait &e2 &3seconds after un-cloaking to attack."));
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void shootEvent(WeaponPreShootEvent e)
	{
		Player p = e.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		if (gp.isIngame())
		{
			if (gp.getCurrentClass().getName().equalsIgnoreCase("spy") || gp.getCurrentClass().getName().equalsIgnoreCase("bspy"))
			{
				if (noAttack.contains(p))
				{
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3Please wait &e2 &3seconds after un-cloaking to attack."));
					e.setCancelled(true);
				}
			}
		}
	}





	// Spy Disguise Kit
	@EventHandler
	public void SpyDisguise(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);

		//For Undisguising
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			//Setting the crit message to nothing to avoid weird messages like player1 killed by player2 with kukri HEADSHOT
			gp.setCritMessage("");

			DisguiseAPI api = TF2.getInstance().getDisguiseAPI();
			if (gp.getCurrentClass() != null && (gp.getCurrentClass().getName().equalsIgnoreCase("spy") || gp.getCurrentClass().getName().equalsIgnoreCase("bspy")))
			{
				if (api.isDisguised(p))
				{
					e.setCancelled(true);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You have undisguised."));
					TitleAPI.sendTitle(p, 10, 20, 10, "", "&bYou have undisguised.");
					ClassChest pClass = new ClassChest(gp.getCurrentClass().getName());
					p.getInventory().setHelmet(pClass.getHelmet());

					if (gp.getTeam() == Team.BLUE)
					{
						ClassChest blueSpy = new ClassChest("bspy");
						p.getInventory().setHelmet(blueSpy.getHelmet());
					}

					p.getInventory().setChestplate(pClass.getChestplate());

					p.getInventory().setItem(8, new ItemStack(Material.AIR));
					p.getInventory().setItem(7, new ItemStack(Material.AIR));

					api.undisguiseToAll(p);
					ParticleEffect.EXPLOSION_NORMAL.display(0.2f, 1, 0.2f, 0.01f, 10000, p.getLocation(), 255);

					gp.setDisguise(null);
				}
			}
		}
	}
	
	@EventHandler
	public void onItemHeld(PlayerItemHeldEvent e)
	{
		Player p = e.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		ItemStack item = p.getInventory().getItem(e.getNewSlot());
		if (item != null && item.getType() == Material.BOOK)
		{
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW.toString()+"Disguise Kit"))
			{
				if (gp.isIngame())
				{
					if (gp.getCurrentClass().getName().equalsIgnoreCase("spy") || gp.getCurrentClass().getName().equalsIgnoreCase("bspy"))
					{
						Game game = gp.getGame();
						if (gp.getGame().getPlayersIngame().size() == 1)
						{
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3There is no-one on the enemy team, you can't disguise ;-;"));
							return;
						}
						int opposingPlayers = 0;
						for (GamePlayer gp2 : game.playersInGame.values())
						{
							if (gp2.getTeam() != gp.getTeam())
							{
								opposingPlayers++;
							}
						}
						int slot1 = (int) Math.floor(opposingPlayers/9.0);
						int slot2 = slot1 + 1;
						int slot3 = slot2*9;
						Inventory inv = Bukkit.createInventory(null, slot3, ChatColor.GOLD+"Disguise Menu");
						for (GamePlayer gp2 : game.playersInGame.values())
						{
							if (gp2.getTeam() != gp.getTeam())
							{
								Player p2 = gp2.getPlayer();
								SkullMeta skull = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
								skull.setOwner(p2.getName());
								skull.setDisplayName(ChatColor.GOLD + p2.getName());
								ItemStack head = new ItemStack (Material.SKULL_ITEM, 1, (byte)3);
								ArrayList<String> lore = new ArrayList<String>();
								lore.add(ChatColor.GRAY+"Click to disguise as "+p2.getName());
								String className = gp2.getCurrentClass().getName();
								if (gp2.getCurrentClass().getName().equalsIgnoreCase("bspy"))
								{
									className = "Spy";
								}
								if (gp2.getCurrentClass().getName().equalsIgnoreCase("PyroR") || gp2.getCurrentClass().getName().equalsIgnoreCase("PyroB"))
								{
									className = "Pyro";
								}
								lore.add(ChatColor.DARK_GRAY+"Class: "+ChatColor.WHITE+className);
								skull.setLore(lore);
								head.setItemMeta(skull);
								inv.addItem(head);


							}
						}
						if (opposingPlayers == 0)
						{
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3There is no-one active on the enemy team, you can't disguise ;-;"));
							return;
						}
						p.openInventory(inv);

					}
				}
				else
				{
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You must be ingame to disguise!"));
					return;
				}
			}
		}
	}

	//Disguise Click Event
	@EventHandler
	public void disguiseClickEvent(InventoryClickEvent e)
	{
		if (e.getWhoClicked() instanceof Player)
		{
			Player p = (Player) e.getWhoClicked();
			GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
			if (e.getInventory().getName().equalsIgnoreCase(ChatColor.GOLD.toString()+"Disguise Menu"))
			{
				e.setCancelled(true);
				ItemStack i = e.getInventory().getItem(e.getSlot());
				ItemMeta im = i.getItemMeta();
				String target = ChatColor.stripColor(i.getItemMeta().getDisplayName());
				im.setDisplayName(ChatColor.GRAY+"Disguised as "+ChatColor.GOLD+target);
				im.setLore(null);
				i.setItemMeta(im);
				p.getInventory().setItem(8, i);
				DisguiseAPI api = TF2.getInstance().getDisguiseAPI();
				Player p2 = Bukkit.getPlayerExact(target);
				GamePlayer gp2 = GameUtilities.getUtilities().getGamePlayer(p2);
				if (p2 == null)
				{
					return;
				}
				ClassChest p2Class = new ClassChest(gp2.getCurrentClass().getName());
				if (p2Class.exists())
				{
					ItemStack helmet = p2Class.getHelmet();
					ItemStack chest = p2Class.getChestplate();

					p.getInventory().setHelmet(helmet);
					p.getInventory().setChestplate(chest);

					WrapperPlayServerEntityEquipment equipmentPacket = new WrapperPlayServerEntityEquipment();
					equipmentPacket.setEntityId(p.getEntityId());
					equipmentPacket.setItem(p2Class.getItem(Slot.PRIMARY));
					for (GamePlayer ingame : gp.getGame().playersInGame.values())
					{
						if (gp != ingame)
						{
							equipmentPacket.sendPacket(ingame.getPlayer());
						}
					}

					gp.setDisguise(p2);

					p.closeInventory();
					api.disguiseToAll(p, new PlayerDisguise(target, false));
				}
				else
				{
					Bukkit.getServer().getLogger().severe("[TF2] An error occured while getting a classchest.");
					return;
				}


				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You have disguised as &e"+target+"&3. (&e&o"+p2Class.getClassName()+"&3)"));

				ParticleEffect.EXPLOSION_NORMAL.display(0.2f, 1, 0.2f, 0.01f, 10000, p.getLocation(), 255);

				String color = "&f";
				switch (gp2.getTeam())
				{
				case RED: color = "&c";
				case BLUE: color = "&9";
				}

				TitleAPI.sendTitle(p, 10, 20, 10, "&6Disguised as,", target+" &8("+color+p2Class.getClassName()+"&8)");
			}
		}
	}

	@EventHandler
	public void undisShoot(WeaponPreShootEvent e)
	{
		Player p = e.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		DisguiseAPI api = TF2.getInstance().getDisguiseAPI();
		if (gp.isIngame())
		{
			if (gp.getCurrentClass().getName().equalsIgnoreCase("spy") || gp.getCurrentClass().getName().equalsIgnoreCase("bspy"))
			{

				if (api.isDisguised(p))
				{
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You have undisguised."));
					TitleAPI.sendTitle(p, 10, 20, 10, "", "&bYou have undisguised.");
					ClassChest pClass = new ClassChest(gp.getCurrentClass().getName());
					p.getInventory().setHelmet(pClass.getHelmet());

					if (gp.getTeam() == Team.BLUE)
					{
						ClassChest blueSpy = new ClassChest("bspy");
						p.getInventory().setHelmet(blueSpy.getHelmet());
					}

					p.getInventory().setChestplate(pClass.getChestplate());

					p.getInventory().setItem(8, new ItemStack(Material.AIR));
					p.getInventory().setItem(7, new ItemStack(Material.AIR));

					api.undisguiseToAll(p);
					ParticleEffect.EXPLOSION_NORMAL.display(0.2f, 1, 0.2f, 0.01f, 10000, p.getLocation(), 255);

					gp.setDisguise(null);
				}
			}
		}
	}
}
