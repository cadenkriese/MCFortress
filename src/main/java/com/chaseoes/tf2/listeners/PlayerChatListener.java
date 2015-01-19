package com.chaseoes.tf2.listeners;

import java.util.ArrayList;
import java.util.HashMap;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.StatsConfiguration;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.Team;

@SuppressWarnings("deprecation")
public class PlayerChatListener implements Listener {
	String dead;

	@EventHandler
	public void PlayerChat(PlayerChatEvent e)
	{
		Player p = e.getPlayer();
		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
		if (e.getMessage().equals("@"))
		{
			if (gp.isIngame())
			{
				p.sendMessage(new String("&aChat &f&l» &3Please enter a message to say in team chat!").replace("&", "\u00a7"));
				e.setCancelled(true);
				return;
			}
		}
		if (e.getMessage().equalsIgnoreCase("ez") || e.getMessage().contains(" ez ") || e.getMessage().contains(" ez") || e.getMessage().contains("ez "))
		{
			e.setCancelled(true);
			p.sendMessage(new String("&aChat &f&l» &3Please be respectful to other players.").replace("&", "\u00a7"));
			return;
		}

		Permission perms = TF2.getInstance().getPerms();
		Economy econ = TF2.getInstance().getEcon();

		ArrayList<String> pTip =  new ArrayList<String>();
		if (perms.getPrimaryGroup(p).equalsIgnoreCase("owner"))
		{
			pTip.add(ChatColor.DARK_PURPLE+ChatColor.BOLD.toString()+p.getName()+ChatColor.DARK_GRAY+ChatColor.BOLD.toString()+":");
		}
		else if (p.hasPermission("tf2.rank.architect"))
		{
			pTip.add(ChatColor.RED+ChatColor.BOLD.toString()+p.getName()+ChatColor.DARK_GRAY+ChatColor.BOLD.toString()+":");
		}
		else if (p.hasPermission("tf2.rank.vip"))
		{
			pTip.add(ChatColor.GREEN+ChatColor.BOLD.toString()+p.getName()+ChatColor.DARK_GRAY+ChatColor.BOLD.toString()+":");
		}
		else
		{
			pTip.add(ChatColor.GRAY+ChatColor.BOLD.toString()+p.getName()+ChatColor.DARK_GRAY+ChatColor.BOLD.toString()+":");
		}
		HashMap<String, Integer> pStats = StatsConfiguration.getStats(p.getUniqueId().toString());

		pTip.add("");
		pTip.add(ChatColor.translateAlternateColorCodes('&', "&8Credits: &f"+econ.getBalance(p)));
		pTip.add(ChatColor.DARK_GRAY+"Kills: "+ChatColor.WHITE+pStats.get("kills"));
		pTip.add(ChatColor.DARK_GRAY+"Deaths: "+ChatColor.WHITE+pStats.get("deaths"));
		if (pStats.get("deaths") == 0 && pStats.get("kills") > 0)
		{
			pTip.add(ChatColor.DARK_GRAY+"KDR: "+ChatColor.WHITE+"0");
		}
		else if (pStats.get("deaths") == 0 && pStats.get("kills") == 0)
		{
			pTip.add(ChatColor.DARK_GRAY+"KDR: "+ChatColor.WHITE+"0");

		}
		else
		{
			pTip.add(ChatColor.DARK_GRAY+"KDR: "+ChatColor.WHITE+pStats.get("kills")/pStats.get("deaths"));
		}
		pTip.add(ChatColor.DARK_GRAY+"Points Captured: "+ChatColor.WHITE+pStats.get("points"));
		pTip.add(ChatColor.DARK_GRAY+"Wins: "+ChatColor.WHITE+pStats.get("wins"));
		pTip.add(ChatColor.DARK_GRAY+"Losses: "+ChatColor.WHITE+pStats.get("losses"));
		if (pStats.get("losses") == 0 && pStats.get("wins") > 0)
		{
			pTip.add(ChatColor.DARK_GRAY+"WLR: "+ChatColor.WHITE+"0");
		}
		else if (pStats.get("wins") == 0 && pStats.get("losses") == 0)
		{
			pTip.add(ChatColor.DARK_GRAY+"WLR: "+ChatColor.WHITE+"0");

		}
		else
		{
			pTip.add(ChatColor.DARK_GRAY+"WLR: "+ChatColor.WHITE+pStats.get("wins")/pStats.get("losses"));
		}
		pTip.add("");
		pTip.add(ChatColor.AQUA+"Click to view their stats.");

		if (gp.isIngame())
		{

			/*
			 * TODO
			 * GAME CHAT
			 */
			if (!e.getMessage().startsWith("@"))
			{
				mkremins.fanciful.FancyMessage message1 = null;
				if (gp.getTeam() == Team.RED)
				{
					ArrayList<String> teamTip = new ArrayList<String>();
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&4&lRED TEAM&8&l:"));
					teamTip.add("");
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&cRED or Reliable Excavation Demolition"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3is a subsidiary of TF Industries,"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3allegedly specializes in \"demolition operations\"."));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3In reality, from behind a myriad of sub-fronts,"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3it secretly controls half of the world's governments,"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3led by founder and CEO &cRedmond Mann."));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3Beneath the convincing exterior of their bases of operations"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3can be found computer racks, satellites, rockets, and missile installations."));
					teamTip.add("");
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3Join &e"+p.getName()+" &3on &e"+gp.getGame().getMapName()));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&aClick Here!"));
					if (gp.isDead())
					{
						mkremins.fanciful.FancyMessage message = new mkremins.fanciful.FancyMessage("*DEAD* ")
						.color(ChatColor.GRAY)
						.then("[")
						.color(ChatColor.DARK_GRAY)
						.then("RED")
						.tooltip(teamTip)
						.command("/tf2 join "+gp.getGame().getMapName())
						.color(ChatColor.RED)
						.then("] ")
						.color(ChatColor.DARK_GRAY);
						message1 = message;
					}
					else
					{
						mkremins.fanciful.FancyMessage message = new mkremins.fanciful.FancyMessage("[")
						.color(ChatColor.DARK_GRAY)
						.then("RED")
						.tooltip(teamTip)
						.command("/tf2 join "+gp.getGame().getMapName())
						.color(ChatColor.RED)
						.then("] ")
						.color(ChatColor.DARK_GRAY);
						message1 = message;
					}
				}	
				else
				{
					ArrayList<String> teamTip = new ArrayList<String>();
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&b&lBLU TEAM&8&l:"));
					teamTip.add("");
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&9BLU or Builders League United"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3is a subsidiary of TF Industries,"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3allegedly specializes in \"construction operations\"."));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3In reality, from behind a myriad of sub-fronts,"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3it secretly controls half of the world's governments,"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3led by founder and CEO &9Blutarch Mann."));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3Beneath the convincing exterior of their bases of operations"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3can be found computer racks, satellites, rockets, and missile installations."));
					teamTip.add("");
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3Join &e"+p.getName()+" &3on &e"+gp.getGame().getMapName()));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&aClick Here!"));

					if (gp.isDead())
					{
						mkremins.fanciful.FancyMessage message = new mkremins.fanciful.FancyMessage("*DEAD* ")
						.color(ChatColor.GRAY)
						.then("[")
						.color(ChatColor.DARK_GRAY)
						.then("BLU")
						.tooltip(teamTip)
						.command("/tf2 join "+gp.getGame().getMapName())
						.color(ChatColor.AQUA)
						.then("] ")
						.color(ChatColor.DARK_GRAY);
						message1 = message;
					}
					else
					{
						mkremins.fanciful.FancyMessage message = new mkremins.fanciful.FancyMessage("[")
						.color(ChatColor.DARK_GRAY)
						.then("BLU")
						.color(ChatColor.AQUA)
						.tooltip(teamTip)
						.command("/tf2 join "+gp.getGame().getMapName())
						.then("] ")
						.color(ChatColor.DARK_GRAY);
						message1 = message;
					}
				}

				if (perms.getPrimaryGroup(p).equalsIgnoreCase("owner"))
				{
					ArrayList<String> tip =  new ArrayList<String>();
					tip.add(ChatColor.BLUE.toString()+ChatColor.BOLD+"OWNER"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
					tip.add("");
					tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is an owner on MC-Fortress.");
					tip.add(ChatColor.DARK_AQUA+"This means they are one of the main founders.");
					tip.add(ChatColor.DARK_AQUA+"And they deserve your applause!");

					message1
					.then("[")
					.color(ChatColor.DARK_GRAY)
					.then("Owner")
					.tooltip(tip)
					.color(ChatColor.BLUE)
					.then("] ")
					.color(ChatColor.DARK_GRAY)
					.then(p.getName())
					.color(ChatColor.DARK_PURPLE)
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.AQUA);
				}
				else if (perms.getPrimaryGroup(p).equalsIgnoreCase("Admin"))
				{
					ArrayList<String> tip =  new ArrayList<String>();
					tip.add(ChatColor.DARK_RED.toString()+ChatColor.BOLD+"ADMIN"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
					tip.add("");
					tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is an Administrator on MC-Fortress.");
					tip.add(ChatColor.DARK_AQUA+"This means they are a very respected");
					tip.add(ChatColor.DARK_AQUA+"staff member, and they can answer any");
					tip.add(ChatColor.DARK_AQUA+"of your questions.");

					message1
					.then("[")
					.color(ChatColor.DARK_GRAY)
					.then("Admin")
					.tooltip(tip)
					.color(ChatColor.DARK_RED)
					.then("] ")
					.color(ChatColor.DARK_GRAY)
					.then(p.getName())
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.color(ChatColor.RED)
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.WHITE);
				}
				else if (perms.getPrimaryGroup(p).equalsIgnoreCase("Moderator"))
				{
					ArrayList<String> tip =  new ArrayList<String>();
					tip.add(ChatColor.YELLOW.toString()+ChatColor.BOLD+"MOD"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
					tip.add("");
					tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is a Moderator on MC-Fortress.");
					tip.add(ChatColor.DARK_AQUA+"This means they are a staff member.");
					tip.add(ChatColor.DARK_AQUA+"Ask them any questions you have!");

					message1
					.then("[")
					.color(ChatColor.DARK_GRAY)
					.then("Mod")
					.tooltip(tip)
					.color(ChatColor.YELLOW)
					.then("] ")
					.color(ChatColor.DARK_GRAY)
					.then(p.getName())
					.color(ChatColor.RED)
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.WHITE);
				}
				else if (perms.getPrimaryGroup(p).equalsIgnoreCase("Architect"))
				{
					ArrayList<String> tip =  new ArrayList<String>();
					tip.add(ChatColor.GREEN.toString()+ChatColor.BOLD+"BUILDER"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
					tip.add("");
					tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is a Builder on MC-Fortress.");
					tip.add(ChatColor.DARK_AQUA+"This means they have built most of");
					tip.add(ChatColor.DARK_AQUA+"the builds on the server!");

					message1
					.then("[")
					.color(ChatColor.DARK_GRAY)
					.then("Builder")
					.tooltip(tip)
					.color(ChatColor.GREEN)
					.then("] ")
					.color(ChatColor.DARK_GRAY)
					.then(p.getName())
					.color(ChatColor.RED)
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.WHITE);
				}
				else if (perms.getPrimaryGroup(p).equalsIgnoreCase("VIP"))
				{
					ArrayList<String> tip =  new ArrayList<String>();
					tip.add(ChatColor.AQUA.toString()+ChatColor.BOLD+"VIP"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
					tip.add("");
					tip.add(ChatColor.AQUA+p.getName()+ChatColor.DARK_AQUA+" Is a VIP on MC-Fortress.");
					tip.add(ChatColor.DARK_AQUA+"This means they have donated to");
					tip.add(ChatColor.DARK_AQUA+"the server and are very supportive.");
					tip.add(ChatColor.DARK_AQUA+"They're the reason the server is running!");
					tip.add("");
					tip.add(ChatColor.AQUA+"Be like "+ChatColor.YELLOW+p.getName()+ChatColor.AQUA+" and donate!");
					tip.add(ChatColor.YELLOW+"Click Here!");

					message1
					.then("[")
					.color(ChatColor.DARK_GRAY)
					.then("VIP")
					.color(ChatColor.AQUA)
					.tooltip(tip)
					.link("http://shop.mc-fort.net/")
					.then("] ")
					.color(ChatColor.DARK_GRAY)
					.then(p.getName())
					.color(ChatColor.GREEN)
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.WHITE);
				}
				else
				{
					message1
					.then(p.getName())
					.color(ChatColor.GRAY)
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.GRAY);
				}

				for (Player p2 : Bukkit.getOnlinePlayers())
				{
					if (!e.isCancelled())
					{
						message1.send(p2);
					}
				}
				e.setCancelled(true);
			}
			/*
			 * TODO
			 * TEAM CHAT
			 */

			else
			{
				e.setMessage(e.getMessage().substring(1));



				mkremins.fanciful.FancyMessage message1 = null;

				if (gp.getTeam() == Team.RED)
				{
					ArrayList<String> teamTip = new ArrayList<String>();
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&4&lTEAM-CHAT&8&l:"));
					teamTip.add("");
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3Team chat can be used to quickly"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3Comminucate with your team members!"));
					teamTip.add("");
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&cTo use it start your messages with an @"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&aExample:"));
					teamTip.add("");
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&2\"&a@Guys come to CP1 quick!&2\""));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&bClick here to talk in team chat!"));
					if (gp.isDead())
					{
						mkremins.fanciful.FancyMessage message = new mkremins.fanciful.FancyMessage("*DEAD* ")
						.color(ChatColor.GRAY)
						.then("[")
						.color(ChatColor.DARK_GRAY)
						.then("TEAM")
						.suggest("@")
						.tooltip(teamTip)
						.color(ChatColor.RED)
						.then("] ")
						.color(ChatColor.DARK_GRAY);
						message1 = message;
					}
					else
					{
						mkremins.fanciful.FancyMessage message = new mkremins.fanciful.FancyMessage("[")
						.color(ChatColor.DARK_GRAY)
						.then("TEAM")
						.suggest("@")
						.tooltip(teamTip)
						.color(ChatColor.RED)
						.then("] ")
						.color(ChatColor.DARK_GRAY);
						message1 = message;
					}
				}	
				else
				{
					ArrayList<String> teamTip = new ArrayList<String>();
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&b&lTEAM-CHAT&8&l:"));
					teamTip.add("");
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3Team chat can be used to quickly"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&3Comminucate with your team members!"));
					teamTip.add("");
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&bTo use it start your messages with an @"));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&aExample:"));
					teamTip.add("");
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&2\"&a@Guys come to CP1 quick!&2\""));
					teamTip.add(ChatColor.translateAlternateColorCodes('&', "&bClick here to talk in team chat!"));
					if (gp.isDead())
					{
						mkremins.fanciful.FancyMessage message = new mkremins.fanciful.FancyMessage("*DEAD* ")
						.color(ChatColor.GRAY)
						.then("[")
						.color(ChatColor.DARK_GRAY)
						.then("TEAM")
						.suggest("@")
						.tooltip(teamTip)
						.color(ChatColor.AQUA)
						.then("] ")
						.color(ChatColor.DARK_GRAY);
						message1 = message;
					}
					else
					{
						mkremins.fanciful.FancyMessage message = new mkremins.fanciful.FancyMessage("[")
						.color(ChatColor.DARK_GRAY)
						.then("TEAM")
						.suggest("@")
						.tooltip(teamTip)
						.color(ChatColor.AQUA)
						.then("] ")
						.color(ChatColor.DARK_GRAY);
						message1 = message;
					}
				}


				if (perms.getPrimaryGroup(p).equalsIgnoreCase("owner"))
				{
					ArrayList<String> tip =  new ArrayList<String>();
					tip.add(ChatColor.BLUE.toString()+ChatColor.BOLD+"OWNER"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
					tip.add("");
					tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is an owner on MC-Fortress.");
					tip.add(ChatColor.DARK_AQUA+"This means they are one of the main founders.");
					tip.add(ChatColor.DARK_AQUA+"And they deserve your applause!");

					message1
					.then("[")
					.color(ChatColor.DARK_GRAY)
					.then("Owner")
					.tooltip(tip)
					.color(ChatColor.BLUE)
					.then("] ")
					.color(ChatColor.DARK_GRAY)
					.then(p.getName())
					.color(ChatColor.DARK_PURPLE)
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.AQUA);
				}
				else if (perms.getPrimaryGroup(p).equalsIgnoreCase("Admin"))
				{
					ArrayList<String> tip =  new ArrayList<String>();
					tip.add(ChatColor.DARK_RED.toString()+ChatColor.BOLD+"ADMIN"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
					tip.add("");
					tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is an Administrator on MC-Fortress.");
					tip.add(ChatColor.DARK_AQUA+"This means they are a very respected");
					tip.add(ChatColor.DARK_AQUA+"staff member, and they can answer any");
					tip.add(ChatColor.DARK_AQUA+"of your questions.");

					message1
					.then("[")
					.color(ChatColor.DARK_GRAY)
					.then("Admin")
					.tooltip(tip)
					.color(ChatColor.DARK_RED)
					.then("] ")
					.color(ChatColor.DARK_GRAY)
					.then(p.getName())
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.color(ChatColor.RED)
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.WHITE);
				}
				else if (perms.getPrimaryGroup(p).equalsIgnoreCase("Moderator"))
				{
					ArrayList<String> tip =  new ArrayList<String>();
					tip.add(ChatColor.YELLOW.toString()+ChatColor.BOLD+"MOD"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
					tip.add("");
					tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is a Moderator on MC-Fortress.");
					tip.add(ChatColor.DARK_AQUA+"This means they are a staff member.");
					tip.add(ChatColor.DARK_AQUA+"Ask them any questions you have!");

					message1
					.then("[")
					.color(ChatColor.DARK_GRAY)
					.then("Mod")
					.tooltip(tip)
					.color(ChatColor.YELLOW)
					.then("] ")
					.color(ChatColor.DARK_GRAY)
					.then(p.getName())
					.color(ChatColor.RED)
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.WHITE);
				}
				else if (perms.getPrimaryGroup(p).equalsIgnoreCase("Architect"))
				{
					ArrayList<String> tip =  new ArrayList<String>();
					tip.add(ChatColor.GREEN.toString()+ChatColor.BOLD+"BUILDER"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
					tip.add("");
					tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is a Builder on MC-Fortress.");
					tip.add(ChatColor.DARK_AQUA+"This means they have built most of");
					tip.add(ChatColor.DARK_AQUA+"the builds on the server!");

					message1
					.then("[")
					.color(ChatColor.DARK_GRAY)
					.then("Builder")
					.tooltip(tip)
					.color(ChatColor.GREEN)
					.then("] ")
					.color(ChatColor.DARK_GRAY)
					.then(p.getName())
					.color(ChatColor.RED)
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.WHITE);
				}
				else if (perms.getPrimaryGroup(p).equalsIgnoreCase("VIP"))
				{
					ArrayList<String> tip =  new ArrayList<String>();
					tip.add(ChatColor.AQUA.toString()+ChatColor.BOLD+"VIP"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
					tip.add("");
					tip.add(ChatColor.AQUA+p.getName()+ChatColor.DARK_AQUA+" Is a VIP on MC-Fortress.");
					tip.add(ChatColor.DARK_AQUA+"This means they have donated to");
					tip.add(ChatColor.DARK_AQUA+"the server and are very supportive.");
					tip.add(ChatColor.DARK_AQUA+"They're the reason the server is running!");
					tip.add("");
					tip.add(ChatColor.AQUA+"Be like "+ChatColor.YELLOW+p.getName()+ChatColor.AQUA+" and donate!");
					tip.add(ChatColor.YELLOW+"Click Here!");

					message1
					.then("[")
					.color(ChatColor.DARK_GRAY)
					.then("VIP")
					.color(ChatColor.AQUA)
					.tooltip(tip)
					.link("http://shop.mc-fort.net/")
					.then("] ")
					.color(ChatColor.DARK_GRAY)
					.then(p.getName())
					.color(ChatColor.GREEN)
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.WHITE);
				}
				else
				{
					message1
					.then(p.getName())
					.color(ChatColor.GRAY)
					.tooltip(pTip)
					.command("/stats "+p.getName())
					.then(" » ")
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
					.then(e.getMessage())
					.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
					.suggest("\""+e.getMessage()+"\"")
					.color(ChatColor.GRAY);
				}

				for (Player p2 : Bukkit.getServer().getOnlinePlayers())
				{
					GamePlayer gp2 = GameUtilities.getUtilities().getGamePlayer(p2);
					if (gp2.getTeam() == gp.getTeam())
					{
						if (!e.isCancelled())
						{
							message1.send(p2);
						}
					}
				}
				e.setCancelled(true);
			}
		}

		/*
		 * TODO
		 * LOBBY CHAT
		 */

		else
		{
			mkremins.fanciful.FancyMessage message1 = new mkremins.fanciful.FancyMessage("[")
			.color(ChatColor.DARK_GRAY)
			.then("LOBBY")
			.color(ChatColor.GRAY)
			.then("] ")
			.color(ChatColor.DARK_GRAY);


			if (perms.getPrimaryGroup(p).equalsIgnoreCase("owner"))
			{
				ArrayList<String> tip =  new ArrayList<String>();
				tip.add(ChatColor.BLUE.toString()+ChatColor.BOLD+"OWNER"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
				tip.add("");
				tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is an owner on MC-Fortress.");
				tip.add(ChatColor.DARK_AQUA+"This means they are one of the main founders.");
				tip.add(ChatColor.DARK_AQUA+"And they deserve your applause!");

				message1
				.then("[")
				.color(ChatColor.DARK_GRAY)
				.then("Owner")
				.tooltip(tip)
				.color(ChatColor.BLUE)
				.then("] ")
				.color(ChatColor.DARK_GRAY)
				.then(p.getName())
				.color(ChatColor.DARK_PURPLE)
				.tooltip(pTip)
				.command("/stats "+p.getName())
				.then(" » ")
				.color(ChatColor.GOLD)
				.style(ChatColor.BOLD)
				.then(e.getMessage())
				.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message..")
				.suggest("\""+e.getMessage()+"\"")
				.color(ChatColor.AQUA);
			}
			else if (perms.getPrimaryGroup(p).equalsIgnoreCase("Admin"))
			{
				ArrayList<String> tip =  new ArrayList<String>();
				tip.add(ChatColor.DARK_RED.toString()+ChatColor.BOLD+"ADMIN"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
				tip.add("");
				tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is an Administrator on MC-Fortress.");
				tip.add(ChatColor.DARK_AQUA+"This means they are a very respected");
				tip.add(ChatColor.DARK_AQUA+"staff member, and they can answer any");
				tip.add(ChatColor.DARK_AQUA+"of your questions.");

				message1
				.then("[")
				.color(ChatColor.DARK_GRAY)
				.then("Admin")
				.tooltip(tip)
				.color(ChatColor.DARK_RED)
				.then("] ")
				.color(ChatColor.DARK_GRAY)
				.then(p.getName())
				.tooltip(pTip)
				.command("/stats "+p.getName())
				.color(ChatColor.RED)
				.then(" » ")
				.color(ChatColor.GOLD)
				.style(ChatColor.BOLD)
				.then(e.getMessage())
				.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message..")
				.suggest("\""+e.getMessage()+"\"")
				.color(ChatColor.WHITE);
			}
			else if (perms.getPrimaryGroup(p).equalsIgnoreCase("Moderator"))
			{
				ArrayList<String> tip =  new ArrayList<String>();
				tip.add(ChatColor.YELLOW.toString()+ChatColor.BOLD+"MOD"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
				tip.add("");
				tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is a Moderator on MC-Fortress.");
				tip.add(ChatColor.DARK_AQUA+"This means they are a staff member.");
				tip.add(ChatColor.DARK_AQUA+"Ask them any questions you have!");

				message1
				.then("[")
				.color(ChatColor.DARK_GRAY)
				.then("Mod")
				.tooltip(tip)
				.color(ChatColor.YELLOW)
				.then("] ")
				.color(ChatColor.DARK_GRAY)
				.then(p.getName())
				.color(ChatColor.RED)
				.tooltip(pTip)
				.command("/stats "+p.getName())
				.then(" » ")
				.color(ChatColor.GOLD)
				.style(ChatColor.BOLD)
				.then(e.getMessage())
				.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message..")
				.suggest("\""+e.getMessage()+"\"")
				.color(ChatColor.WHITE);
			}
			else if (perms.getPrimaryGroup(p).equalsIgnoreCase("Architect"))
			{
				ArrayList<String> tip =  new ArrayList<String>();
				tip.add(ChatColor.GREEN.toString()+ChatColor.BOLD+"BUILDER"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
				tip.add("");
				tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is a Builder on MC-Fortress.");
				tip.add(ChatColor.DARK_AQUA+"This means they have built most of");
				tip.add(ChatColor.DARK_AQUA+"the builds on the server!");

				message1
				.then("[")
				.color(ChatColor.DARK_GRAY)
				.then("Builder")
				.tooltip(tip)
				.color(ChatColor.GREEN)
				.then("] ")
				.color(ChatColor.DARK_GRAY)
				.then(p.getName())
				.color(ChatColor.RED)
				.tooltip(pTip)
				.command("/stats "+p.getName())
				.then(" » ")
				.color(ChatColor.GOLD)
				.style(ChatColor.BOLD)
				.then(e.getMessage())
				.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
				.suggest("\""+e.getMessage()+"\"")
				.color(ChatColor.WHITE);
			}
			else if (perms.getPrimaryGroup(p).equalsIgnoreCase("VIP"))
			{
				ArrayList<String> tip =  new ArrayList<String>();
				tip.add(ChatColor.AQUA.toString()+ChatColor.BOLD+"VIP"+ChatColor.DARK_GRAY+ChatColor.BOLD+":");
				tip.add("");
				tip.add(ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" Is a VIP on MC-Fortress.");
				tip.add(ChatColor.DARK_AQUA+"This means they have donated to");
				tip.add(ChatColor.DARK_AQUA+"the server and are very supportive.");
				tip.add(ChatColor.DARK_AQUA+"They're the reason the server is running!");
				tip.add("");
				tip.add(ChatColor.DARK_AQUA+"Be like "+ChatColor.YELLOW+p.getName()+ChatColor.DARK_AQUA+" and donate!");
				tip.add(ChatColor.BLUE+ChatColor.UNDERLINE.toString()+"Click Here!");

				message1
				.then("[")
				.color(ChatColor.DARK_GRAY)
				.then("VIP")
				.color(ChatColor.AQUA)
				.tooltip(tip)
				.link("http://shop.mc-fort.net/")
				.then("] ")
				.color(ChatColor.DARK_GRAY)
				.then(p.getName())
				.color(ChatColor.GREEN)
				.tooltip(pTip)
				.command("/stats "+p.getName())
				.then(" » ")
				.color(ChatColor.GOLD)
				.style(ChatColor.BOLD)
				.then(e.getMessage())
				.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
				.suggest("\""+e.getMessage()+"\"")
				.color(ChatColor.WHITE);
			}
			else
			{
				message1
				.then(p.getName())
				.color(ChatColor.GRAY)
				.tooltip(pTip)
				.command("/stats "+p.getName())
				.then(" » ")
				.color(ChatColor.GOLD)
				.style(ChatColor.BOLD)
				.then(e.getMessage())
				.tooltip(ChatColor.DARK_AQUA+"Click to qoute this message.")
				.suggest("\""+e.getMessage()+"\"")
				.color(ChatColor.GRAY);
			}
			for (Player p2 : Bukkit.getOnlinePlayers())
			{
				if (!e.isCancelled())
				{
					message1.send(p2);
				}
			}
		} 
		e.setCancelled(true);
	}
}