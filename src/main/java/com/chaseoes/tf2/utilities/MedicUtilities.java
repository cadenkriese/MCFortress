package com.chaseoes.tf2.utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.Team;
import com.chaseoes.tf2.extras.MediGunHandler;
import com.chaseoes.tf2.particles.ParticleEffect;
import com.chaseoes.tf2.sound.TFSound;

import fr.mrsheepsheep.tinthealth.THAPI;
import fr.mrsheepsheep.tinthealth.TintHealth;

public class MedicUtilities 
{	
	//Get an instance of the class
	public static MedicUtilities instance = new MedicUtilities();
	public MedicUtilities() {}
	public static MedicUtilities getUtilities() 
	{   return instance;    }

	//Storing the ubercharges
	HashMap<Player, Integer> activeUbers = new HashMap<Player, Integer>();


	//blocks that can be walked through
	public static HashSet<Material> blockAirFoliageSet = new HashSet<Material>();

	static 
	{
		blockAirFoliageSet.add(Material.AIR);    
		blockAirFoliageSet.add(Material.SAPLING);    
		blockAirFoliageSet.add(Material.LONG_GRASS);   
		blockAirFoliageSet.add(Material.DEAD_BUSH);   
		blockAirFoliageSet.add(Material.YELLOW_FLOWER);   
		blockAirFoliageSet.add(Material.RED_ROSE);   
		blockAirFoliageSet.add(Material.BROWN_MUSHROOM);   
		blockAirFoliageSet.add(Material.RED_MUSHROOM);   
		blockAirFoliageSet.add(Material.FIRE);   
		blockAirFoliageSet.add(Material.WHEAT);   
		blockAirFoliageSet.add(Material.PUMPKIN_STEM);  
		blockAirFoliageSet.add(Material.MELON_STEM);  
		blockAirFoliageSet.add(Material.NETHER_WARTS);  
		blockAirFoliageSet.add(Material.CARROT);  
		blockAirFoliageSet.add(Material.POTATO);
	}
	
	//The range it scans to find the player that shall be healed.
	int range = 8;
	
	public Player getTarget(GamePlayer gp)
	{
		Player p = gp.getPlayer();
		if (gp.getHealing() == null)
		{
			Entity e = getEntityInSight(p, range, false, true, true, 1.9f);
			if (e == null)
			{
				Player targ = getNearestPlayer(p);
				if (targ != null)
				{
					return targ;
				}
			}
			else if (e instanceof Player)
			{
				return (Player) e;
			}
		}
		else
		{
			Player target = gp.getHealing();
			GamePlayer gt = GameUtilities.getUtilities().getGamePlayer(target);
			if (p.getNearbyEntities(range, range, range).contains(target))
			{
				if (!gt.isDead() && !gp.isDead())
				{
					return target;
				}
			}
			else
			{
				Entity e = getEntityInSight(p, range, false, true, true, 1.9f);
				if (e != null)
				{
					if (e instanceof Player)
					{
						Player target2 = (Player) e;
						GamePlayer gt2 = GameUtilities.getUtilities().getGamePlayer(target);
						if (gt2.getTeam() == gp.getTeam() || gp.getDisguise() != null)
						{
							if (!gt2.isDead() && !gp.isDead())
							{
								return target2;
							}
						}
					}
					else
					{
						Player targ = getNearestPlayer(p);
						if (targ != null)
						{
							return targ;
						}
					}
				}
				else
				{
					Player targ = getNearestPlayer(p);
					if (targ != null)
					{
						return targ;
					}
				}
			}
		}
		return null;
	}

	public static Entity getEntityInSight(Player player, int rangeToScan, boolean avoidAllies, boolean avoidNonLiving,
			boolean lineOfSight, float expandBoxesPercentage)
	{
		Location observerPos = player.getEyeLocation();
		Vector3D observerDir = new Vector3D(observerPos.getDirection());
		Vector3D observerStart = new Vector3D(observerPos);
		Vector3D observerEnd = observerStart.add(observerDir.multiply(rangeToScan));

		Entity hit = null;

		for (Entity entity : player.getNearbyEntities(rangeToScan, rangeToScan, rangeToScan))
		{
			if (entity instanceof Player)
			{
				Player targ = (Player) entity;
				GamePlayer gt =  GameUtilities.getUtilities().getGamePlayer(targ);
				if (gt.isDead() || gt.isInvis() || !gt.isIngame())
					continue;
			}

			if (avoidNonLiving && !(entity instanceof LivingEntity))
				continue;

			double theirDist = player.getEyeLocation().distance(entity.getLocation());
			if (lineOfSight
					&& player.getLastTwoTargetBlocks(MedicUtilities.blockAirFoliageSet, (int) Math.ceil(theirDist)).get(0)
					.getLocation().distance(player.getEyeLocation()) + 1 < theirDist)
				continue;

			Vector3D targetPos = new Vector3D(entity.getLocation());

			float width = (((CraftEntity) entity).getHandle().width / 1.8F) * expandBoxesPercentage;

			Vector3D minimum = targetPos.add(-width, -0.1 / expandBoxesPercentage, -width);
			Vector3D maximum = targetPos.add(width, ((CraftEntity) entity).getHandle().length * expandBoxesPercentage, width);

			if (hasIntersection(observerStart, observerEnd, minimum, maximum))
			{
				if (hit == null
						|| hit.getLocation().distanceSquared(observerPos) > entity.getLocation().distanceSquared(observerPos))
				{
					hit = entity;
				}
			}
		}
		return hit;
	}


	private static boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max)
	{
		final double epsilon = 0.0001f;

		Vector3D d = p2.subtract(p1).multiply(0.5);
		Vector3D e = max.subtract(min).multiply(0.5);
		Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
		Vector3D ad = d.abs();

		if (Math.abs(c.x) > e.x + ad.x)
			return false;
		if (Math.abs(c.y) > e.y + ad.y)
			return false;
		if (Math.abs(c.z) > e.z + ad.z)
			return false;

		if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
			return false;
		if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
			return false;
		if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
			return false;

		return true;
	}

	private static class Vector3D
	{

		// Use protected members, like Bukkit
		private final double x;
		private final double y;
		private final double z;

		private Vector3D(double x, double y, double z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		private Vector3D(Location location)
		{
			this(location.toVector());
		}

		private Vector3D(Vector vector)
		{
			if (vector == null)
				throw new IllegalArgumentException("Vector cannot be NULL.");
			this.x = vector.getX();
			this.y = vector.getY();
			this.z = vector.getZ();
		}

		private Vector3D abs()
		{
			return new Vector3D(Math.abs(x), Math.abs(y), Math.abs(z));
		}

		private Vector3D add(double x, double y, double z)
		{
			return new Vector3D(this.x + x, this.y + y, this.z + z);
		}

		private Vector3D add(Vector3D other)
		{
			if (other == null)
				throw new IllegalArgumentException("other cannot be NULL");

			return new Vector3D(x + other.x, y + other.y, z + other.z);
		}

		private Vector3D multiply(double factor)
		{
			return new Vector3D(x * factor, y * factor, z * factor);
		}

		private Vector3D multiply(int factor)
		{
			return new Vector3D(x * factor, y * factor, z * factor);
		}

		private Vector3D subtract(Vector3D other)
		{
			if (other == null)
				throw new IllegalArgumentException("other cannot be NULL");
			return new Vector3D(x - other.x, y - other.y, z - other.z);
		}
	}

	private Player getNearestPlayer(Player p)
	{
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		HashMap<Location, Player> playerMap = new HashMap<Location, Player>();

		for (Entity newEntity : p.getNearbyEntities(range, range, range))
		{
			if (newEntity instanceof Player)
			{
				Player target = (Player) newEntity;
				GamePlayer gt = GameUtilities.getUtilities().getGamePlayer(target);
				if (target != p)
				{
					if (gt.isIngame() && !gt.isDead())
					{
						if (gt.getTeam() == gp.getTeam() || gp.getDisguise() != null)
						{
							playerMap.put(target.getLocation(), target);
						}
					}
				}
			}
		}

		if (playerMap.size() == 0)
		{
			return null;
		}

		Location playerLoc = p.getLocation();
		Location closest = p.getLocation().add(100, 100, 100);
		double closestDist = playerLoc.distance(closest);

		for (Location loc : playerMap.keySet())
		{
			if (loc.distance(playerLoc) < closestDist)
			{
				closestDist = loc.distance(playerLoc);
				closest = loc;
			}
		}

		return playerMap.get(closest);
	}

	
	/**
	 * 
	 * @author GamerKing195
	 * @param The player that is the medic
	 * @param The player that is being healed by the medic.
	 * 
	 * Attemps to start an ubercharge with the player
	 * and the target.
	 * 
	 */
	public void attemptUber(final Player player, final Player target)
	{
		final GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(player);
		GamePlayer gt = GameUtilities.getUtilities().getGamePlayer(target);
		boolean solo1 = false;
		
		if (gt == null || target == null)
		{
			solo1 = true;
		}
		
		final boolean solo = solo1;
		
		if (gp.isDead() || (!solo && gt.isDead()))
		{
			return;
		}
		
		if (gp.getUberPercent() == 100.0f)
		{
			gp.setUbering(true);
			setUbered(player, true);
			setUbered(target, true);

			activeUbers.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TF2.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					Random dice = new Random();

					if (gp.getUberPercent() > 0)
					{
						gp.setUberPercent(gp.getUberPercent()-0.625f);

						if (dice.nextInt(10) == 0)
						{
							if (gp.getTeam() == Team.RED)
							{
								ParticleEffect.VILLAGER_HAPPY.display(0.4f, 0.8f, 0.4f, 1, 4, player.getLocation().add(0, 1, 0), 50);
								if (!solo) ParticleEffect.VILLAGER_HAPPY.display(0.4f, 0.8f, 0.4f, 1, 4, target.getLocation().add(0, 1, 0), 50);
							}
							else if (gp.getTeam() == Team.BLUE)
							{
								ParticleEffect.HEART.display(0.4f, 0.8f, 0.4f, 1, 4, player.getLocation().add(0, 1, 0), 50);
								if (!solo) ParticleEffect.HEART.display(0.4f, 0.8f, 0.4f, 1, 4, target.getLocation().add(0, 1, 0), 50);
							}
						}
					}
					else
					{
						setUbered(player, false);
						if (!solo) setUbered(target, false);
						gp.setUbering(false);
						MediGunHandler.getHandler().startHealingProcess(player, target);
						stopUberTask(player);
					}
				}
			}, 0L, 1L));
		}
		else
		{
			float percent = 100.0f-gp.getUberPercent();
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTF2 &f&l» &3You need &e"+Math.round(percent)+"%&3 more Über before you can activate Übercharge!"));
		}
	}

	/**
	 * 
	 * @author GamerKing195
	 * @param The player that is having the uber state modified
	 * @param If the ubercharge is to be enabled or disabled.
	 * 
	 * Changes the uber status of a single player.
	 */
	public void setUbered(Player player, boolean enable)
	{
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(player);
		
		if (enable)
		{
			TintHealth th = (TintHealth) TF2.getInstance().getServer().getPluginManager().getPlugin("TintHealth");
			THAPI api = th.getAPI();
			api.setTint(player, 100);

			TFSound.MEDIC_INVULN_ON.send(player.getLocation(), gp.getGame(), 1f, 1f);
			
			Color teamColor = Color.RED;
			
			if (gp.getTeam() == Team.RED || (gp.getTeam() == Team.BLUE && gp.getDisguise() != null))
					teamColor = Color.RED;
			else if (gp.getTeam() == Team.BLUE ||(gp.getTeam() == Team.RED && gp.getDisguise() != null))
					teamColor = Color.BLUE;
			
			ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
			ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
			
			ItemUtilities.getUtilities().setColor(pants, teamColor);
			ItemUtilities.getUtilities().setColor(boots, teamColor);
			
			player.getInventory().setLeggings(pants);
			player.getInventory().setBoots(boots);
			
			ItemUtilities.getUtilities().setGlowing(player.getItemInHand(), true);

			for (ItemStack stack : player.getInventory().getArmorContents())
			{
				ItemUtilities.getUtilities().setGlowing(stack, true);
			}


			gp.setUbered(true);
		}
		else
		{
			TintHealth th = (TintHealth) TF2.getInstance().getServer().getPluginManager().getPlugin("TintHealth");
			THAPI api = th.getAPI();
			api.fadeTint(player, 100, 2);

			TFSound.MEDIC_INVULN_OFF.send(player.getLocation(), gp.getGame(), 1f, 2f);
			
			for (ItemStack stack : player.getInventory().getContents())
			{
				ItemUtilities.getUtilities().setGlowing(stack, false);
			}

			player.getInventory().setLeggings(new ItemStack(Material.AIR));
			player.getInventory().setBoots(new ItemStack(Material.AIR));

			for (ItemStack stack : player.getInventory().getArmorContents())
			{
				if (stack != null)
				{
					ItemUtilities.getUtilities().setGlowing(stack, false);
				}
			}	
			gp.setUbered(false);
		}
	}

	public void stopUberTask(Player p)
	{
		if (activeUbers.containsKey(p))
		{
			Bukkit.getServer().getScheduler().cancelTask(activeUbers.get(p));
			activeUbers.remove(p);
		}
	}
}