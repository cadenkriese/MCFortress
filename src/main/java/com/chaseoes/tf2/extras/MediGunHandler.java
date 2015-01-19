package com.chaseoes.tf2.extras;

import java.util.HashMap;
import java.util.Random;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameStatus;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.Team;
import com.chaseoes.tf2.particles.ParticleEffect;
import com.chaseoes.tf2.sound.TFSound;
import com.chaseoes.tf2.utilities.MedicUtilities;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.connorlinfoot.titleapi.TitleAPI;

import fr.mrsheepsheep.tinthealth.THAPI;
import fr.mrsheepsheep.tinthealth.TintHealth;

public class MediGunHandler 
{
	//To get an instance of the class.
	public static MediGunHandler instance = new MediGunHandler();
	public MediGunHandler() {}
	public static MediGunHandler getHandler(){	return instance;   }

	//Task Maps
	HashMap<Player, Integer> particleMap = new HashMap<Player, Integer>();
	HashMap<Player, Integer> uberMap = new HashMap<Player, Integer>();
	HashMap<Player, Integer> healingMap = new HashMap<Player, Integer>();

	//The range it scans to find the player that shall be healed.
	int range = 8;

	public void heal(Action a, final Player player)
	{
		final GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(player);
		if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)
		{
			if (!gp.isIngame() || gp.getGame().isCrit(player).equalsIgnoreCase("no_fire"))
			{
				ActionBarAPI.sendActionBar(player, ChatColor.translateAlternateColorCodes('&', "&4You cannot heal people right now!"));
				return;
			}

			final Player targ = MedicUtilities.getUtilities().getTarget(gp);
			GamePlayer gt = GameUtilities.getUtilities().getGamePlayer(targ);

			if (gt == null)
			{
				
			}
			
			if (gt.getTeam() != gp.getTeam())
			{
				return;
			}
			
			if (gt.isInvis())
			{
				return;
			}
			
			if (gp.isUbering())
			{
				if (targ != null)
					MedicUtilities.getUtilities().setUbered(targ, true);
			}

			if (gp.isUbering() && particleMap.containsKey(player))
			{
				return;
			}
			else if (particleMap.containsKey(player))
			{
				stopHealingProcess(player, gp.getHealing());
				return;
			}
			if (targ != null)
			{
				startHealingProcess(player, targ);
				TFSound.MEDIGUN_HEAL.send(player.getLocation(), gp.getGame(), 1f, 1f);
			}
			particleMap.put(player,
					Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
					{
						double t = 0;
						double a = 0;
						Random dice = new Random();
						@Override
						public void run()
						{
							Player newTarg = MedicUtilities.getUtilities().getTarget(gp);
							Location loc = player.getLocation().add(0, 0.8, 0);
							Location target = player.getLocation();
							if (gp.isDead() && gp.getHealing() != null)
							{
								stopHealingProcess(gp.getPlayer(), gp.getHealing());
								return;
							}
							if (gp.getHealing() == null && newTarg == null)
							{
								TFSound.MEDIGUN_NO_TARGET.send(player, 1f, 1f);
								stopHealingProcess(player, null);
								return;
							}
							if (newTarg == null)
							{
								if (gp.getHealing() != null)
								{
									if (GameUtilities.getUtilities().getGamePlayer(gp.getHealing()).isUbered())
									{
										MedicUtilities.getUtilities().setUbered(gp.getHealing(), false);
									}	
								}
								stopHealingProcess(player, gp.getHealing());
								return;
							}
							else
							{
								if (player.getNearbyEntities(range, range, range).contains(newTarg))
								{
									target = newTarg.getLocation().add(0, 0.8, 0);
								}
								else
								{
									if (gp.getHealing() != null)
									{
										if (GameUtilities.getUtilities().getGamePlayer(gp.getHealing()).isUbered())
										{
											MedicUtilities.getUtilities().setUbered(gp.getHealing(), false);
										}	
									}
									stopHealingProcess(player, newTarg);
								}
							}

							GamePlayer gt = GameUtilities.getUtilities().getGamePlayer(newTarg);

							if (!gp.isIngame() || gp.getGame().isCrit(player).equalsIgnoreCase("no_fire"))
							{
								stopHealingProcess(player, newTarg);
							}
							if (gp.isDead() || gt.isDead() || gt.isInvis())
							{
								stopHealingProcess(player, newTarg);
							}
							if (!gt.isIngame() || gp.getGame().isCrit(newTarg).equalsIgnoreCase("no_fire"))
							{
								stopHealingProcess(player, newTarg);
							}
							if (gt.getTeam() != gp.getTeam() && gp.getDisguise() == null)
							{
								stopHealingProcess(player, newTarg);
							}
							
							Vector vec = target.toVector().subtract(loc.toVector());
							for (int r = 1; r < range /* range default 8 */; r++) 
							{
								double radius = Math.sin(t + (Math.PI * 2 / 15 * r)) / 3;
								Location tempLoc = loc.clone().add(vec.normalize().multiply(r));
								for (double angle = a; angle < a + Math.PI * 2; angle += Math.PI / 8) 
								{
									double x = Math.sin(angle) * radius;
									double z = Math.cos(angle) * radius;
									Vector v = new Vector(x, 0, z);
									rotateAroundAxisX(v, player.getLocation().getPitch() + 90);
									rotateAroundAxisY(v, -player.getLocation().getYaw());
									if (gp.getTeam() == Team.RED)
									{
										ParticleEffect.REDSTONE.display(Color.RED.getRed() + 1, Color.RED.getGreen(),
												Color.RED.getBlue(), 0.004F, 0, tempLoc.clone().add(v), 257D);
									}
									else if (gp.getTeam() == Team.BLUE)
									{
										ParticleEffect.REDSTONE.display(Color.BLUE.getRed() + 1, Color.BLUE.getGreen(),
												Color.BLUE.getBlue(), 0.004F, 0, tempLoc.clone().add(v), 257D);
									}
									if (dice.nextInt(150) == 0)
									{
										if (gp.getTeam() == Team.RED)
										{
											ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0.004F, 1, tempLoc.clone().add(v), 257D);
										}
										else if (gp.getTeam() == Team.BLUE)
										{
											ParticleEffect.HEART.display(0, 0, 0, 0.004F, 1, tempLoc.clone().add(v), 257D);
										}
									}
								}
							}
							if (a >= Math.PI * 2)
								a = 0;
							t -= Math.PI / 40;
							if (t < 0)
								t = Math.PI * 2;
						}
					}, 0L, 1L));
		}

		else if (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK)
		{
			Player targ = MedicUtilities.getUtilities().getTarget(gp);

			if (!gp.getGame().redHasBeenTeleported)
			{
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3Please wait for the game to start to uber."));
				return;
			}

			if (targ != null)
			{
				MedicUtilities.getUtilities().attemptUber(player, gp.getHealing());
			}
		}
	}

	private Vector rotateAroundAxisX(Vector v, double angle) {
		angle = Math.toRadians(angle);
		double y, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		y = v.getY() * cos - v.getZ() * sin;
		z = v.getY() * sin + v.getZ() * cos;
		return v.setY(y).setZ(z);
	}



	private Vector rotateAroundAxisY(Vector v, double angle) {
		angle = Math.toRadians(angle);
		double x, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		x = v.getX() * cos + v.getZ() * sin;
		z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}


	public void startHealingProcess(final Player p, final Player targ)
	{
		final GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);

		gp.setHealing(targ);

		final GamePlayer gt = GameUtilities.getUtilities().getGamePlayer(targ);

		//Ubercharge
		if (!uberMap.containsKey(p))
		{
			uberMap.put(p, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
			{
				boolean soundSent = false;
				@Override
				public void run()
				{
					if (gp.getGame().getStatus() == GameStatus.STARTING || !gp.getGame().redHasBeenTeleported)
					{
						gp.setUberPercent(gp.getUberPercent()+7.5f);
					}
					else if (targ.getHealth() == targ.getMaxHealth())
					{
						gp.setUberPercent(gp.getUberPercent()+1.25f);
					}
					else
					{
						gp.setUberPercent(gp.getUberPercent()+2.5f);
					}
					if (gp.getUberPercent() >= 100.0f)
					{
						if (!soundSent)
						{
							TFSound.RECHARGED.send(p, 1f, 1f);
							soundSent = true;
						}
						gp.setUberPercent(100.0f);
					}
				}
			}, 0L, 20L));
		}

		//Healing
		if (!healingMap.containsKey(p))
		{
			healingMap.put(p, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					String healBarToMedic = "&8YOU ARE HEALING "+gp.getTeamColor()+targ.getName()+" %health%";
					String healBarToPlayer = "&8YOU ARE BEING HEALED BY "+gp.getTeamColor()+gp.getName() +" &7("+gp.getTeamColor()+Math.round(gp.getUberPercent())+"%&7)";

					//Normal Heal Rate
					if (p.getHealth() == p.getMaxHealth() || p.getHealth() >= p.getMaxHealth()-4)
					{
						if (targ.getHealth()+1 <= targ.getMaxHealth())
						{
							targ.setHealth(targ.getHealth()+1);
						}
					}
					//Increased Heal Rate
					else
					{
						if (targ.getHealth()+2 >= targ.getMaxHealth())
						{
							targ.setHealth(targ.getMaxHealth());
						}
						else
						{
							targ.setHealth(targ.getHealth()+2);
						}
					}


					if (!gt.isUbered())
					{
						TintHealth th = (TintHealth) TF2.getInstance().getServer().getPluginManager().getPlugin("TintHealth");
						THAPI api = th.getAPI();
						float per1 = (float) (targ.getHealth() / targ.getMaxHealth());
						float per2 = 1-per1;
						int per3 = Math.round(per2*100);
						api.setTint(targ, per3);
					}

					TitleAPI.sendTitle(p, 0, 10, 5, "", healBarToMedic.replace("%health%", Math.round(targ.getHealth()/2)+"♥ / "+Math.round(targ.getMaxHealth()/2)+"♥"));
					TitleAPI.sendTitle(targ, 0, 10, 5, "", healBarToPlayer);
				}
			}, 0L, 5L));
		}
	}

	public void stopHealingProcess(Player p, Player target)
	{
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);

		if (particleMap.containsKey(p))
		{
			Bukkit.getServer().getScheduler().cancelTask(particleMap.get(p));
			particleMap.remove(p);
		}

		if (healingMap.containsKey(p))
		{
			Bukkit.getServer().getScheduler().cancelTask(healingMap.get(p));
			healingMap.remove(p);
		}

		if (uberMap.containsKey(p))
		{
			Bukkit.getServer().getScheduler().cancelTask(uberMap.get(p));
			uberMap.remove(p);
		}

		if (target != null)
		{
			if (GameUtilities.getUtilities().getGamePlayer(target).isUbered())
			{
				MedicUtilities.getUtilities().setUbered(target, false);
			}
		}

		gp.setHealing(null);
	}

	public String getUberBar(Player p)
	{
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		
		int percent = Math.round(gp.getUberPercent());
		int bar = (int) gp.getUberPercent()/5;
		
		
		if (gp.isUbering())
		{
			String uberBar = gp.getTeamColor()+"&l&oÜber &6"+percent+"% &8| &a:::::::::::::::::::: &8|";
			int start = uberBar.length()-24;
			return ChatColor.translateAlternateColorCodes('&', uberBar.substring(0, start+bar)+"&4"+uberBar.substring(start+bar));
		}
		else
		{
			String uberBar = gp.getTeamColor()+"Über &6"+percent+"% &8| &a:::::::::::::::::::: &8|";
			int start = uberBar.length()-24;
			return ChatColor.translateAlternateColorCodes('&', uberBar.substring(0, start+bar)+"&4"+uberBar.substring(start+bar));
		}
	}

	public void cancelUberIncrease(Player p)
	{
		if (uberMap.containsKey(p))
		{
			Bukkit.getServer().getScheduler().cancelTask(uberMap.get(p));
			uberMap.remove(p);
		}
	}
}