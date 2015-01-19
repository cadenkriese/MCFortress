package com.chaseoes.tf2.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.chaseoes.tf2.TF2;

public class SQLUtilities {

	static SQLUtilities instance = new SQLUtilities();
	private TF2 plugin;
	Connection conn;
	boolean connected = false;

	private SQLUtilities() {

	}

	public static SQLUtilities getUtilities() {
		return instance;
	}

	public void setup(TF2 p) {
		plugin = p;
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				final TF2 p = plugin;
				String username = p.getConfig().getString("stats-database.username");
				String password = p.getConfig().getString("stats-database.password");
				String url = "jdbc:mysql://" + p.getConfig().getString("stats-database.hostname") + ":" + p.getConfig().getInt("stats-database.port") + "/" + p.getConfig().getString("stats-database.database_name");

				try {
					conn = DriverManager.getConnection(url, username, password);
					Statement st = conn.createStatement();
					String table = "CREATE TABLE IF NOT EXISTS player_stats(id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), username TEXT, kills TEXT, highest_killstreak TEXT, points_captured TEXT, games_played TEXT, red_team_count TEXT, blue_team_count TEXT, time_ingame TEXT, games_won TEXT, deaths TEXT, UNIQUE KEY username(username(64)) )";
					String loadoutTable = "CREATE TABLE IF NOT EXISTS player_loadouts(id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), uuid TEXT, class TEXT, primary_wep TEXT, secondary_wep TEXT, melee_wep TEXT)";
					String infoTable = "CREATE TABLE IF NOT EXISTS player_info(id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (id), uuid TEXT, lastname TEXT, rank TEXT, credits TEXT, UNIQUE KEY UUID(UUID(37)))";
					st.executeUpdate(table);
					st.executeUpdate(loadoutTable);
					st.executeUpdate(infoTable);
					connected = true;
				} catch (Exception e) {
					connected = false;
					e.printStackTrace();
					plugin.getLogger().log(Level.WARNING, e.getMessage());
					plugin.getLogger().log(Level.SEVERE, "Could not connect to database! Verify your database details in the configuration are correct.");
					plugin.getServer().getPluginManager().disablePlugin(plugin);
				}
			}
		});
	}

	private void reopenConnection() {
		String username = plugin.getConfig().getString("stats-database.username");
		String password = plugin.getConfig().getString("stats-database.password");
		String url = "jdbc:mysql://" + plugin.getConfig().getString("stats-database.hostname") + ":" + plugin.getConfig().getInt("stats-database.port") + "/" + plugin.getConfig().getString("stats-database.database_name");

		try {
			conn = DriverManager.getConnection(url, username, password);
			connected = true;
		} catch(Exception e) {
			connected = false;
			e.printStackTrace();
			plugin.getLogger().log(Level.SEVERE,  "Could not re-connect to the database" );
		}
	}

	public ResultSet getResultSet(String statement) {
		if (!connected) 
		{
			Bukkit.getServer().getLogger().severe("[TF2] The server is not connected to the database, could not get the resultset1!");
			return null;
		}
		ResultSet result = null;
		try
		{
			Statement st;
			try
			{
				if ( conn.isClosed() )
				{
					reopenConnection();
				}
			}
			catch(SQLException sqe)
			{
				reopenConnection();
			}
			st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			if (!plugin.getConfig().getBoolean("settings.sandbox")) 
			{
				return st.executeQuery(statement);
			}
			return st.executeQuery(statement);
		} 
		catch (SQLException e) 
		{
			plugin.getLogger().log(Level.SEVERE,  "DB error " + e );
		}
		return result;
	}

	public void execUpdate(String statement) 
	{
		try
		{
			if ( conn.isClosed() )
			{
				reopenConnection();
			}
		}
		catch(SQLException sqe)
		{
			reopenConnection();
		}
		Statement st;
		try
		{
			st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			st.executeUpdate(statement);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			Bukkit.getServer().getLogger().severe("[TF2] Error occured while updating the database!");
		}
	}

	public void playerJoin(Player player, Boolean firstJoin)
	{
		Permission perms = TF2.getInstance().getPerms();
		Economy econ = TF2.getInstance().getEcon();
		ResultSet rs = getResultSet("SELECT * FROM player_info WHERE uuid='"+player.getUniqueId().toString()+"'");

		try {
			if (conn == null || conn.isClosed())
			{
				reopenConnection();
			}
			if (firstJoin || rs == null || !rs.next())
			{

				String statement = ""
						+ "INSERT INTO player_info(uuid, lastname, rank, credits) "
						+ "VALUES ('"+player.getUniqueId().toString()+"', '"+player.getName()+"', '"+perms.getPrimaryGroup(player)+"', '"+econ.getBalance(player)+"') "
						+ "ON DUPLICATE KEY "
						+ "UPDATE lastname='"+player.getName()+"'";
				execUpdate(statement);
			}
			else
			{
				try 
				{
					if (!rs.getString("lastname").equalsIgnoreCase(player.getName()))
					{
						execUpdate("UPDATE player_info SET lastname = "+player.getName()+" WHERE uuid = "+player.getUniqueId().toString());
					}

					if (!rs.getString("rank").equalsIgnoreCase(perms.getPrimaryGroup(player)))
					{
						GroupManager groupManager = (GroupManager)Bukkit.getPluginManager().getPlugin("GroupManager");
						final OverloadedWorldHolder handler = groupManager.getWorldsHolder().getWorldData(player);
						if (handler == null)
						{
							Bukkit.getServer().getLogger().severe("[TF2] Could not update "+player.getName()+"'s group!");
						}
						else
						{
							handler.getUser(player.getName()).setGroup(handler.getGroup(rs.getString("rank")));
						}
					}

					if (!rs.getString("credits").equalsIgnoreCase(String.valueOf(econ.getBalance(player))))
					{
						double newCredits = Double.valueOf(rs.getString("credits"));
						double oldCredits = econ.getBalance(player);

						econ.withdrawPlayer(player, oldCredits);
						econ.depositPlayer(player, newCredits);
					}
				}
				catch (SQLException e) 
				{
					e.printStackTrace();
					Bukkit.getLogger().severe("[TF2] Could not get a column from the resultset.");
				}
			}
		} 
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			
			e.printStackTrace();
		}
	}
}
