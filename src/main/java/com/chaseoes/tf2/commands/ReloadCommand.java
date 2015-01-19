package com.chaseoes.tf2.commands;

import ca.wacos.nametagedit.NametagAPI;

import com.chaseoes.tf2.DataConfiguration;
import com.chaseoes.tf2.GamePlayer;
import com.chaseoes.tf2.GameUtilities;
import com.chaseoes.tf2.MapUtilities;
import com.chaseoes.tf2.TF2;
import com.chaseoes.tf2.localization.Localizers;
import com.chaseoes.tf2.utilities.SQLUtilities;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand {

    private TF2 plugin;
    static ReloadCommand instance = new ReloadCommand();

    private ReloadCommand() {

    }

    public static ReloadCommand getCommand() {
        return instance;
    }

    public void setup(TF2 p) {
        plugin = p;
    }

    public void execReloadCommand(CommandSender cs, String[] strings, Command cmnd) {
        plugin.reloadConfig();
        plugin.saveConfig();
        TF2.getInstance().saveConfig();
        DataConfiguration.getData().reloadData();
        DataConfiguration.getData().reloadData();
        Localizers.getInstance().reload();
        for (String map : MapUtilities.getUtilities().getEnabledMaps()) 
        {
            TF2.getInstance().getMap(map).load();
        }
        for (Player p : Bukkit.getOnlinePlayers())
        {
        	Permission perms = TF2.getInstance().getPerms();
        	GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
        	
        	SQLUtilities.getUtilities().playerJoin(p, false);

        	if (!gp.isIngame())
        	{
        		String prefix = "";
        		if (perms.playerInGroup(p, "Owner"))
        		{
        			prefix = ChatColor.DARK_GRAY+"["+ChatColor.BLUE+"Owner"+ChatColor.DARK_GRAY+"] "+ChatColor.DARK_PURPLE;
        		}
        		else if (perms.playerInGroup(p, "Admin"))
        		{
        			prefix = ChatColor.DARK_GRAY+"["+ChatColor.DARK_RED+"Admin"+ChatColor.DARK_GRAY+"] "+ChatColor.RED;
        		}
        		else if (perms.playerInGroup(p, "Architect"))
        		{
        			prefix = ChatColor.DARK_GRAY+"["+ChatColor.GREEN+"Build"+ChatColor.DARK_GRAY+"] "+ChatColor.RED;
        		}
        		else if (perms.playerInGroup(p, "Moderator"))
        		{
        			prefix = ChatColor.DARK_GRAY+"["+ChatColor.YELLOW+"Mod"+ChatColor.DARK_GRAY+"] "+ChatColor.RED;
        		}
        		else if (perms.playerInGroup(p, "VIP"))
        		{
        			prefix = ChatColor.DARK_GRAY+"["+ChatColor.AQUA+"VIP"+ChatColor.DARK_GRAY+"] "+ChatColor.GREEN;
        		}
        		else
        		{
        			prefix = ChatColor.GRAY.toString();
        		}
        		NametagAPI.setPrefix(p.getName(), prefix);
        	}
        }
        
        Localizers.getDefaultLoc().CONFIG_RELOADED.sendPrefixed(cs);
    }

}
