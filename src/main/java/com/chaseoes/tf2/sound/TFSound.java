package com.chaseoes.tf2.sound;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.chaseoes.tf2.Game;
import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.TF2;

public enum TFSound 
{	
	MEDIGUN_HEAL("medic", "medigun_heal"),

	MEDIGUN_NO_TARGET("medic", "medigun_no_target"),

	MEDIC_INVULN_ON("medic", "invuln_on"),

	MEDIC_INVULN_OFF("medic", "invuln_off"),

	HEAL_STATION("misc", "heal_station"),

	RECHARGED("misc", "recharged"),

	SCORED("misc", "scored"),

	EXCITING("misc", "exciting"),

	ACHEIVEMENT("misc", "acheivement"),

	SENTRY_SPOT("sentry", "spot"),

	SENTRY_SHOOT("sentry", "shoot"),

	SENTRY_SHOOT_UPGRADED("sentry", "shoot_upgraded"),

	SENTRY_ROCKET("sentry", "rocket"),

	SENTRY_MOVE_SHORT("sentry", "move_short"),

	SENTRY_MOVE_MEDIUM("sentry", "move_medium"),

	SENTRY_EXPLODE("sentry", "explode"),

	SENTRY_DAMAGE("sentry", "damage"),

	SENTRY_AMBIENT("sentry", "ambient");

	String category;
	String name;

	private TFSound(String category, String name)
	{
		this.category = category;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public String getCategory()
	{
		return category;
	}


	/**
	 * Sends a sound to a specific player.
	 * 
	 * @author Caden
	 * @param player - The player which the sound is sent to.
	 * @param pitch - The pitch of the sound.
	 * @param volume - The volume of the sound (This is the distance from the origin that the sound can be heard, not the loudness).
	 */
	public void send(Player player, float pitch, float volume)
	{
		player.playSound(player.getLocation(), category+"."+name, volume, pitch);
	}

	/**
	 * Sends a sound to everyone in a game.
	 * 
	 * @author Caden
	 * @param loc - The origin of the sound.
	 * @param game - The player which the sound is sent to.
	 * @param pitch - The pitch of the sound.
	 * @param volume - The volume of the sound (This is the distance from the origin that the sound can be heard, not the loudness).
	 */
	public void send(Location loc, Game game, float pitch, float volume)
	{
		for (GamePlayer gp : game.playersInGame.values())
		{
			Player p = gp.getPlayer();
			p.playSound(loc, category+"."+name, volume, pitch);
		}
	}

	/**
	 * Sends a sound to a specific player.
	 * 
	 * @author Caden
	 * @param player - The player which the sound is sent to.
	 * @param pitch - The pitch of the sound.
	 * @param volume - The volume of the sound (This is the distance from the origin that the sound can be heard, not the loudness).
	 * @param delay - The delay in ticks before the sound is played.
	 */
	public void sendDelayed(final Player player, final float pitch, final float volume, long delay)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				player.playSound(player.getLocation(), category+"."+name, volume, pitch);
			}
		}, delay);
	}

	/**
	 * Sends a sound to everyone in a game.
	 * 
	 * @author Caden
	 * @param loc - The origin of the sound.
	 * @param game - The player which the sound is sent to.
	 * @param pitch - The pitch of the sound.
	 * @param volume - The volume of the sound (This is the distance from the origin that the sound can be heard, not the loudness).
	 * @param delay - The delay before the sound is played.
	 */
	public void sendDelayed(final Location loc, final Game game, final float pitch, final float volume, long delay)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{		
				for (GamePlayer gp : game.playersInGame.values())
				{
					Player p = gp.getPlayer();
					p.playSound(loc, category+"."+name, volume, pitch);
				}
			}
		}, delay);
	}
}
