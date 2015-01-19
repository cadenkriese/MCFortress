package com.chaseoes.tf2;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Door;
import org.bukkit.util.Vector;

import com.chaseoes.tf2.classes.TF2Class;
import com.chaseoes.tf2.particles.ParticleEffect;
import com.connorlinfoot.titleapi.TitleAPI;

import fr.mrsheepsheep.tinthealth.THAPI;
import fr.mrsheepsheep.tinthealth.TintHealth;

public class AmmoKit implements Listener{
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent ev)
	{
		Player p = ev.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		Location l = ev.getTo();
		Block block = l.getBlock().getRelative(0, -1, 0);

		if (l.getBlock() != null && l.getBlock().getType() == Material.GOLD_PLATE)
		{
			if (gp.isIngame())
			{
				return;
			}
			p.setVelocity(l.getDirection().multiply(5));
			p.setVelocity(new Vector(p.getVelocity().getX(),
					1.0D, p.getVelocity().getZ()));

			for (int times = 0; times <= 40; times++)
			{
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1f, 1f);	
			}
			ParticleEffect.SMOKE_NORMAL.display(0.3f, 0.3f, 0.3f, 1, 40, l, 20);
		}
		else if (l.getBlock().getType() == Material.IRON_PLATE)
		{
			if (!p.isSneaking())
			{
				p.playSound(p.getLocation(), Sound.SLIME_WALK2, 1f, 1f);
				ParticleEffect.EXPLOSION_NORMAL.display(0.3f, 0.3f, 0.3f, 1, 10, l, 20);
				p.setVelocity(new Vector(0, 20, 0));
			}
		}

		if (!gp.isIngame() || gp.isInLobby() || (!gp.getGame().redHasBeenTeleported && gp.getTeam() == Team.RED))
		{
			return;
		}

		if(block.getType() == Material.STAINED_CLAY)
		{
			if(block.getData() == 1)
			{
				if (gp.isIngame())
				{
					if (!gp.isDead)
					{
						TF2Class pClass = gp.getCurrentClass();
						if (pClass.ammoKit(p))
						{
							block.setData((byte) 7);
							TitleAPI.sendTitle(p, 5, 10, 5, "", "&6Ammo Restocked.");
							reset(block, true);
						}
					}
				}
			}
			else if (block.getData() == 14)
			{
				if (p.getHealth() < p.getMaxHealth())
				{
					if (gp.isIngame())
					{
						if (!gp.isDead())
						{
							if (!gp.isUbered())
							{
								TintHealth th = (TintHealth) TF2.getInstance().getServer().getPluginManager().getPlugin("TintHealth");
								THAPI api = th.getAPI();

								float per1 = (float) (p.getHealth() / p.getMaxHealth());
								float per2 = 1-per1;
								int per3 = Math.round(per2*100);

								api.fadeTint(p, per3, 1);
							}
							p.playSound(p.getLocation(), Sound.PIG_DEATH, 1f, 1f);

							double gain1 = (float) (p.getMaxHealth()-p.getHealth());
							double gain2 = gain1/2;
							double gain3 = Math.round(gain2 * 2) / 2.0;
							TitleAPI.sendTitle(p, 5, 10, 5, "", "&a+ "+gain3+"&c♥");

							p.setHealth(p.getMaxHealth());
							p.setFireTicks(0);
							p.setFoodLevel(20);

							block.setData((byte) 0);
							reset(block, false);
						}
					}
				}
			}
		}
		/*else
		{
			if (gp.isIngame())
			{
				HashMap<Player, List<Block>> blockMap = new HashMap<Player, List<Block>>();
				List<Block> blockList1 = new ArrayList<Block>();
				int radius = 3;
				for (int x = -(radius); x <= radius; x++)
				{
					for (int y = -(radius); y <= radius; y++)
					{
						for (int z = -(radius); z <= radius; z++)
						{	
							Location loc = l.getBlock().getRelative(x, y, z).getLocation();
							if (loc.getBlock().getType() == Material.IRON_DOOR_BLOCK)
							{
								blockList1.add(loc.getBlock());
							}
							else if (loc.getBlock().getType() == Material.WOOL)
							{
								blockList1.add(loc.getBlock());
							}
						}
						blockMap.put(p, blockList1);
					}
				}
				if (blockMap.get(p) != null && blockMap.get(p).size() != 0)
				{
					List<Block> blockList2 = blockMap.get(p);
					blockMap.remove(p);
					if (blockList2.size() == 8)
					{
						//p.sendMessage("1");
						for (int index = 0; blockList2.size() > index; index++)
						{
							Block b = blockList2.get(index);
							if (b.getType() == Material.IRON_DOOR_BLOCK)
							{
								//p.sendMessage("2");
								Door door = (Door) b.getState();
								//p.sendMessage("3");
								ArrayList<Block> doorList = new ArrayList<Block>();
								doorList.add(b);
								door.setOpen(true);
								//p.sendMessage("4");
								if (!gp.isAtReSupply)
								{
									//p.sendMessage("5");
									if (doorList.size() > 1)
									{
										//p.sendMessage("6");
										gp.setAtReSupply(true);
										float math = Math.round(p.getMaxHealth()-p.getHealth());
										p.setHealth(p.getMaxHealth());
										if (p.getFireTicks() > 0)
										{
											p.setFireTicks(1);
										}
										TF2Class pClass = gp.getCurrentClass();
										pClass.apply(gp);
										p.performCommand("shot reload");
										ActionBarAPI.sendActionBar(p, ChatColor.GREEN+"+ "+math);
										p.playSound(p.getLocation(), Sound.PIG_DEATH, 1, 1);
										resetReSupply(doorList.get(0), doorList.get(1), gp);
										//p.sendMessage("7");
									}
								}
							}
						}
					}
					else
					{
						blockMap.remove(p);
						if (gp.isAtReSupply)
						{
							gp.setAtReSupply(false);
						}
					}
				}
			}
		}*/
	}


	public void reset(final Block block, final boolean ammo) 
	{
		TF2.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() 
			{
				if (ammo)
					block.setData((byte) 1);
				else
					block.setData((byte) 14);
			}
		}, 1200L);
	}

	public void resetReSupply(final Block b1, final Block b2, final GamePlayer gp)
	{
		TF2.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(TF2.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() 
			{
				Door d1 = new Door(b1.getType());
				Door d2 = new Door(b2.getType());
				d1.setOpen(false);
				d2.setOpen(false);
				b1.setData(d1.getData());
				b2.setData(d2.getData());
				gp.setAtReSupply(false);
				//gp.getPlayer().sendMessage("7");
			}
		}, 120L);
	}
}
