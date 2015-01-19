package com.chaseoes.tf2;

/*
 * 					TF2
 * 	Created by: chaseoes & psycowithespn
 * 
 * 	Modified by: GamerKing195
 * 
 *  License: GPU General Public Lisence
 * 
 */

import com.chaseoes.tf2.boosters.BoosterQueueFile;
import com.chaseoes.tf2.boosters.BoosterStatusFile;
import com.chaseoes.tf2.boosters.PlayerBoostersFile;
import com.chaseoes.tf2.capturepoints.CapturePointUtilities;
import com.chaseoes.tf2.classes.ClassDataFile;
import com.chaseoes.tf2.classes.ClassGuis;
import com.chaseoes.tf2.classes.ClassUtilities;
import com.chaseoes.tf2.commands.*;
import com.chaseoes.tf2.guis.profileGUI;
import com.chaseoes.tf2.listeners.*;
import com.chaseoes.tf2.lobbywall.LobbyWall;
import com.chaseoes.tf2.lobbywall.LobbyWallUtilities;
import com.chaseoes.tf2.localization.Localizers;
import com.chaseoes.tf2.utilities.*;
import com.chaseos.tf2.placeholders.Stage;
import com.chaseos.tf2.placeholders.players;
import com.comphenix.protocol.ProtocolManager;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

import de.robingrether.idisguise.api.DisguiseAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

public class TF2 extends JavaPlugin implements PlaceholderReplacer {

	public HashMap<String, Map> maps = new HashMap<String, Map>();
	public HashMap<String, String> usingSetSpawnMenu = new HashMap<String, String>();
	public HashMap<String, StatCollector> stats = new HashMap<String, StatCollector>();
	public IconMenu setSpawnMenu;
	private static TF2 instance;
	public boolean enabled;
	public boolean isDisabling;
	public String admap;
	public DisguiseAPI disguiseAPI;
    //private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    public ProtocolManager protocolManager;

	public static TF2 getInstance() {
		return instance;
	}
	@Override
	public void onEnable()
	{
		disguiseAPI = getServer().getServicesManager().getRegistration(DisguiseAPI.class).getProvider();
		instance = this;
		isDisabling = false;
		admap = "Dustbowl";
		//protocolManager = ProtocolLibrary.getProtocolManager();
		
        new FakeEquipment(this) 
        {
            @Override
            protected boolean onEquipmentSending(EquipmentSendingEvent equipmentEvent) 
            {
                if (equipmentEvent.getSlot() == EquipmentSlot.HELD)
                {
                	LivingEntity e = equipmentEvent.getVisibleEntity();
                	if (e instanceof Player)
                	{
                		Player p = (Player) e;
                		GamePlayer gp = GameUtilities.getUtilities().getGamePlayer(p);
                		if (gp.getDisguise() != null)
                		{
                			int slot = p.getInventory().getHeldItemSlot();
                			ItemStack fakeItem = gp.getDisguise().getInventory().getItem(slot);
                			if (fakeItem == null)
                			{
                				fakeItem = gp.getDisguise().getInventory().getItem(0);
                				if (fakeItem == null)
                				{
                					return false;
                				}
                			}
                			equipmentEvent.setEquipment(fakeItem);
                			return true;
                		}
                	}
                }
                return false;
            }
            
            @Override
            protected void onEntitySpawn(Player client, LivingEntity visibleEntity) 
            {
                if (EquipmentSlot.HELD.isEmpty(visibleEntity)) 
                {
                    updateSlot(client, visibleEntity, EquipmentSlot.HELD);
                }
            }
        };
		
		ClassDataFile.loadFiles();
		Stage adstage = new Stage();
		players adplayers = new players();
		if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"))
		{
			HologramsAPI.registerPlaceholder(this, "%adstatus", 1, this);
			HologramsAPI.registerPlaceholder(this, "%adstage", 1, adstage);
			HologramsAPI.registerPlaceholder(this, "%adplayers", 1, adplayers);
		}
		else
		{
			getServer().getLogger().severe("[TF2] HolographicDisplays not found! Please make sure you have it installed!");
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
		{
	        setupEconomy();
	        setupPermissions();
	    }
		else
		{
			getServer().getLogger().severe("[TF2] Vault not found! Please make sure you have it installed!");
		}
		
		if (!Bukkit.getPluginManager().isPluginEnabled("GroupManager"))
		{
			getServer().getLogger().severe("[TF2] GroupManager not found! Please make sure you have it installed!");

		}
		
		if (!Bukkit.getPluginManager().isPluginEnabled("CrackShot"))
		{
			getServer().getLogger().severe("[TF2] CrackShot not found! Please make sure you have it installed!");
		}
		
		if (!Bukkit.getServer().getPluginManager().isPluginEnabled("BossBarAPI"))
		{
			getServer().getLogger().severe("[TF2] BossBarAPI not found! Please make sure you have it installed!");
		}
		
		if (!Bukkit.getServer().getPluginManager().isPluginEnabled("FeatherBoard"))
		{
			getServer().getLogger().severe("[TF2] FeatherBoard not found! Please make sure you have it installed!");
		}
		
		if (!Bukkit.getServer().getPluginManager().isPluginEnabled("ActionBarAPI"))
		{
			getServer().getLogger().severe("[TF2] ActionBarAPI not found! Please make sure you have it installed!");
		}
		
		if (!Bukkit.getServer().getPluginManager().isPluginEnabled("TitleAPI"))
		{
			getServer().getLogger().severe("[TF2] TitleAPI not found! Please make sure you have it installed!");
		}
		
		if (!Bukkit.getServer().getPluginManager().isPluginEnabled("ResourcePackApi"))
		{
			getServer().getLogger().severe("[TF2] ResourcePackAPI not found! Please make sure you have it installed!");

		}
		/*if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI"))
		{
			PlaceholderAPI.registerPlaceholder(this, "gametime", new gametimePlaceholder());
			PlaceholderAPI.registerPlaceholder(this, "team", new teamPlaceholder());
			PlaceholderAPI.registerPlaceholder(this, "kills", new killsPlaceholder());
			PlaceholderAPI.registerPlaceholder(this, "map", new mapPlaceholder());
			PlaceholderAPI.registerPlaceholder(this, "tc", new teamcolorPlaceholder());
			PlaceholderAPI.registerPlaceholder(this, "btc", new brightteamcolorPlaceholder());
		}
		else
		{
			getServer().getLogger().severe("[TF2] Place holder API not found! Please make sure you have it installed!");
		}*/
		//getServer().getScheduler().cancelTasks(this);

		setupClasses();
		loadMethods();

		if (getServer().getPluginManager().getPlugin("WorldEdit") == null) {
			getLogger().log(Level.SEVERE, pluginRequiredMessage("WorldEdit"));
			getServer().getPluginManager().disablePlugin(this);
			enabled = false;
			return;
		}

		getCommand("tf2").setExecutor(new CommandManager());
		getCommand("loadout").setExecutor(new LoadoutCommand());
		getConfig().options().copyDefaults(true);
		saveConfig();
		DataConfiguration.getData().reloadData();
		Localizers.getInstance().reload();

		for (String map : MapUtilities.getUtilities().getEnabledMaps()) {
			addMap(map, GameStatus.WAITING);
		}

		for (String map : MapUtilities.getUtilities().getDisabledMaps()) {
			addMap(map, GameStatus.DISABLED);
		}

		//Schedulers.getSchedulers().startAFKChecker();

		LobbyWall.getWall().startTask();
		

		setSpawnMenu = new IconMenu(Localizers.getDefaultLoc().SETSPAWN_TITLE.getString(), 9, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(IconMenu.OptionClickEvent event) {
				String map = usingSetSpawnMenu.get(event.getPlayer().getName());
				String name = ChatColor.stripColor(event.getName());
				if (name.equalsIgnoreCase(Localizers.getDefaultLoc().SETSPAWN_BLUE_LOBBY.getString())) {
					MapUtilities.getUtilities().setTeamLobby(map, Team.BLUE, event.getPlayer().getLocation());
					Localizers.getDefaultLoc().SETSPAWN_BLUE_LOBBY_DESC.sendPrefixed(event.getPlayer());
					usingSetSpawnMenu.remove(event.getPlayer().getName());
				} else if (name.equalsIgnoreCase(Localizers.getDefaultLoc().SETSPAWN_RED_LOBBY.getString())) {
					MapUtilities.getUtilities().setTeamLobby(map, Team.RED, event.getPlayer().getLocation());
					Localizers.getDefaultLoc().SETSPAWN_RED_LOBBY_DESC.sendPrefixed(event.getPlayer());
					usingSetSpawnMenu.remove(event.getPlayer().getName());
				} else if (name.equalsIgnoreCase(Localizers.getDefaultLoc().SETSPAWN_BLUE_SPAWN.getString())) {
					MapUtilities.getUtilities().setTeamSpawn(map, Team.BLUE, event.getPlayer().getLocation());
					Localizers.getDefaultLoc().SETSPAWN_BLUE_SPAWN_DESC.sendPrefixed(event.getPlayer());
					usingSetSpawnMenu.remove(event.getPlayer().getName());
				} else if (name.equalsIgnoreCase(Localizers.getDefaultLoc().SETSPAWN_RED_SPAWN.getString())) {
					MapUtilities.getUtilities().setTeamSpawn(map, Team.RED, event.getPlayer().getLocation());
					Localizers.getDefaultLoc().SETSPAWN_RED_SPAWN_DESC.sendPrefixed(event.getPlayer());
					usingSetSpawnMenu.remove(event.getPlayer().getName());
				}
				event.setWillClose(true);
			}
		}, this).setOption(2, new ItemStack(Material.REDSTONE, 1), ChatColor.DARK_RED + "" + ChatColor.BOLD + Localizers.getDefaultLoc().SETSPAWN_RED_LOBBY.getString() + ChatColor.RESET, ChatColor.WHITE + Localizers.getDefaultLoc().SETSPAWN_RED_LOBBY_DESC.getString()).setOption(3, new ItemStack(Material.INK_SACK, 1, (short) 4), ChatColor.AQUA + "" + ChatColor.BOLD + Localizers.getDefaultLoc().SETSPAWN_BLUE_LOBBY.getString() + ChatColor.RESET, ChatColor.WHITE + Localizers.getDefaultLoc().SETSPAWN_BLUE_LOBBY_DESC.getString()).setOption(4, new ItemStack(Material.WOOL, 1, (short) 14), ChatColor.DARK_RED + "" + ChatColor.BOLD + Localizers.getDefaultLoc().SETSPAWN_RED_SPAWN.getString() + ChatColor.RESET, ChatColor.WHITE + Localizers.getDefaultLoc().SETSPAWN_RED_SPAWN_DESC.getString()).setOption(5, new ItemStack(Material.WOOL, 1, (short) 11), ChatColor.AQUA + "" + ChatColor.BOLD + Localizers.getDefaultLoc().SETSPAWN_BLUE_SPAWN.getString() + ChatColor.RESET, ChatColor.WHITE + Localizers.getDefaultLoc().SETSPAWN_BLUE_SPAWN_DESC.getString()).setOption(6, new ItemStack(Material.BEDROCK, 1), ChatColor.RED + "" + ChatColor.BOLD + Localizers.getDefaultLoc().SETSPAWN_EXIT.getString() + ChatColor.RESET, ChatColor.RED + Localizers.getDefaultLoc().SETSPAWN_EXIT_DESC.getString());

		/*if (getConfig().getBoolean("auto-update")) {
			if (!getDescription().getVersion().contains("SNAPSHOT")) {
				new Updater(this, 46264, this.getFile(), Updater.UpdateType.DEFAULT, false);
			}
		}*/

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit Metrics!
		}

		// Connect to database after everything else has loaded.
		if (getConfig().getBoolean("stats-database.enabled")) {
			SQLUtilities.getUtilities().setup(this);
		}
		
		StatsConfiguration.loadFiles();
		
		PlayerBoostersFile.getFile().loadFiles();
		BoosterStatusFile.getFile().loadFiles();
		BoosterQueueFile.getFile().loadFiles();
		BoosterQueueFile.getFile().updateArray();
		BoosterStatusFile.getFile().resumeBooster();
		
		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
			getLogger().severe("*** This plugin will be disabled. ***");
			this.setEnabled(false);
			return;
		}
		enabled = true;
	}

	private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
	}

	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
	}

	@Override
	public void onDisable() 
	{
		isDisabling = true;
		if (enabled) {
			reloadConfig();
			saveConfig();
			for (Map map : MapUtilities.getUtilities().getMaps()) {
				if (GameUtilities.getUtilities().getGame(map).getStatus() != GameStatus.DISABLED) 
				{
					GameUtilities.getUtilities().getGame(map).stopMatch(false, true);
				}
			}
			instance = null;
		}
		getServer().getScheduler().cancelTasks(this);
		
		StatsConfiguration.saveFiles();
		ClassDataFile.saveFiles();
		BoosterStatusFile.getFile();
		BoosterStatusFile.getFile().pauseBooster();
		BoosterQueueFile.getFile().saveFiles();
		BoosterStatusFile.getFile().saveFiles();
		
		enabled = false;
	}

	public void setupClasses() {
		MapUtilities.getUtilities().setup(this);
		WorldEditUtilities.getWEUtilities().setup(this);
		CreateCommand.getCommand().setup(this);
		RedefineCommand.getCommand().setup(this);
		LobbyWall.getWall().setup(this);
		DataConfiguration.getData().setup(this);
		LobbyWallUtilities.getUtilities().setup(this);
		WorldEditUtilities.getWEUtilities().setupWorldEdit(getServer().getPluginManager());
		ClassUtilities.getUtilities().setup(this);
		GameUtilities.getUtilities().setup(this);
		CapturePointUtilities.getUtilities().setup(this);
		Schedulers.getSchedulers().setup(this);
		CreateCommand.getCommand().setup(this);
		DeleteCommand.getCommand().setup(this);
		DisableCommand.getCommand().setup(this);
		EnableCommand.getCommand().setup(this);
		JoinCommand.getCommand().setup(this);
		LeaveCommand.getCommand().setup(this);
		ListCommand.getCommand().setup(this);
		ReloadCommand.getCommand().setup(this);
		SetCommand.getCommand().setup(this);
		DebugCommand.getCommand().setup(this);
		StartCommand.getCommand().setup(this);
		StopCommand.getCommand().setup(this);
		
		this.getCommand("profile").setExecutor(new ProfileCommand());
		this.getCommand("resource").setExecutor(new ResourceCommand());
		this.getCommand("stats").setExecutor(new StatsCommand(this));
		this.getCommand("class").setExecutor(new classAlias(this));
		this.getCommand("leave").setExecutor(new leaveAlias(this));
		this.getCommand("syncstats").setExecutor(new SyncStatsCommand(this));
		this.getCommand("boosters").setExecutor(new BoostersCommand());

		// Register Events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new BlockPlaceListener(), this);
		pm.registerEvents(new FoodLevelChangeListener(), this);
		pm.registerEvents(new PlayerInteractListener(), this);
		pm.registerEvents(new PlayerCommandPreprocessListener(), this);
		pm.registerEvents(new PlayerDamageByEntityListener(), this);
		pm.registerEvents(new PlayerDeathListener(), this);
		pm.registerEvents(new PlayerDropItemListener(), this);
		pm.registerEvents(new PlayerJoinListener(), this);
		pm.registerEvents(new PlayerMoveListener(), this);
		pm.registerEvents(new PlayerQuitListener(), this);
		pm.registerEvents(new PotionSplashListener(), this);
		pm.registerEvents(new ProjectileLaunchListener(), this);
		pm.registerEvents(new SignChangeListener(), this);
		pm.registerEvents(new TF2DeathListener(), this);
		pm.registerEvents(new BlockBreakListener(), this);
		pm.registerEvents(new EntityDamageListener(), this);
		pm.registerEvents(new EntityShootBowListener(), this);
		pm.registerEvents(new InventoryClickListener(this), this);
		pm.registerEvents(new PlayerRespawnListener(), this);
		pm.registerEvents(new PlayerChatListener(), this);
		pm.registerEvents(new LoadoutCommand(), this);
		pm.registerEvents(new ClassGuis(), this);
		pm.registerEvents(new PlayerToggleFlightListener(), this);
		pm.registerEvents(new WeaponPrepareShootListener(), this);
		pm.registerEvents(new Scoping(), this);
		pm.registerEvents(new ItemFrameProtection(), this);
		pm.registerEvents(new PlayerPreJoinListener(), this);
		pm.registerEvents(new WeatherChangeListener(), this);
		pm.registerEvents(new ResourcePackStatusListener(), this);
		pm.registerEvents(new BoostersCommand(), this);
		pm.registerEvents(new BoosterStartEndListener(), this);
		pm.registerEvents(new GroupEvent(), this);
		pm.registerEvents(new UberListeners(), this);
		pm.registerEvents(new profileGUI(), this);
	}

	public Map getMap(String map) {
		return maps.get(map);
	}

	public void addMap(String map, GameStatus status) {
		Map m = new Map(this, map);
		Game g = new Game(m, this);
		maps.put(map, m);
		GameUtilities.getUtilities().addGame(m, g);
		m.load();
		GameUtilities.getUtilities().getGame(m).redHasBeenTeleported = false;
		GameUtilities.getUtilities().getGame(m).setStatus(status);
		if (status == GameStatus.DISABLED) {
			String[] creditlines = new String[4];
			creditlines[0] = " ";
			creditlines[1] = "--------------------------";
			creditlines[2] = "--------------------------";
			creditlines[3] = " ";
			LobbyWall.getWall().setAllLines(map, null, creditlines, false, false);
		}
	}

	public Collection<Map> getMaps() {
		return maps.values();
	}

	public void removeMap(String map) {
		Map m = maps.remove(map);
		Game game = GameUtilities.getUtilities().removeGame(m);
		game.stopMatch(false, true);
		LobbyWall.getWall().unloadCacheInfo(map);
		MapUtilities.getUtilities().destroyMap(m);
	}

	public boolean mapExists(String map) {
		return maps.containsKey(map);
	}

	public String pluginRequiredMessage(String plugin) {
		return "\n------------------------------ [ ERROR ] ------------------------------\n-----------------------------------------------------------------------\n\n" + plugin + " is REQUIRED to run TF2!\nPlease download " + plugin + ", or TF2 will NOT work!\nDownload at: " + getPluginURL(plugin) + "\nTF2 is now being disabled...\n\n-----------------------------------------------------------------------\n-----------------------------------------------------------------------";
	}

	public String getPluginURL(String plugin) {
		if (plugin.equalsIgnoreCase("WorldEdit")) {
			return "http://dev.bukkit.org/server-mods/worldedit/";
		}
		return "";
	}
	public void loadMethods()
	{
		Bukkit.getServer().getPluginManager().registerEvents(new StatsConfiguration(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new StatsCommand(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new AmmoKit(), this);
	}
	public String getADmap()
	{
		return admap;
	}
	public void setADmap(String map)
	{
		admap = map;
	}

	@Override
	public String update() {
		Map map = new Map(this, admap);
		Game game = GameUtilities.getUtilities().getGame(map);
		if (game.getStatus() == GameStatus.DISABLED)
		{
			String status = new String(ChatColor.DARK_RED.toString()+ChatColor.UNDERLINE.toString()+"Disabled");
			return status;
		}
		else if (game.getStatus() == GameStatus.WAITING)
		{
			String status = new String(ChatColor.GREEN+"Waiting");
			return status;
		}
		else if (game.getStatus() == GameStatus.STARTING)
		{
			String status = new String(ChatColor.GOLD+"Starting");
			return status;
		}
		else if (game.getStatus() == GameStatus.INGAME && !game.redHasBeenTeleported)
		{
			String status = new String(ChatColor.GOLD+"Starting");
			return status;
		}
		else if (game.getStatus() == GameStatus.INGAME && game.redHasBeenTeleported)
		{
			String status = new String(ChatColor.LIGHT_PURPLE+"In Game");
			return status;
		}
		else if (game.getStatus() == GameStatus.ENDING)
		{
			String status = new String(ChatColor.RED+"Ending...");
			return status;
		}
		else
		{
			String status = new String(ChatColor.DARK_RED+"null");
			return status;
		}
	}
	
	public void addCredits(Player p, int metal)
	{
		econ.depositPlayer(p, metal);
		
		SQLUtilities.getUtilities().execUpdate("INSERT INTO "
				+ "player_info(uuid, credits) "
				+ "VALUES ('"+p.getUniqueId()+"', '"+econ.getBalance(p)+"') "
				+ "ON DUPLICATE KEY UPDATE credits="+econ.getBalance(p));
	}
	
	public void addCreditsPretty(Player p, int credits, boolean booster)
	{
		if (booster)
		{
		credits = credits*BoosterStatusFile.getFile().getBoost();
		}
		
		econ.depositPlayer(p, credits);
	
		SQLUtilities.getUtilities().execUpdate("INSERT INTO "
				+ "player_info(uuid, credits) "
				+ "VALUES ('"+p.getUniqueId()+"', '"+econ.getBalance(p)+"') "
				+ "ON DUPLICATE KEY UPDATE credits="+econ.getBalance(p));
		
		if (booster)
		{
			p.sendMessage(ChatColor.GRAY+"(+"+credits+" Credits)"+BoosterStatusFile.getFile().getBoosterMessage());	
		}
		else
		{
			p.sendMessage(ChatColor.GRAY+"(+"+credits+" Credits)");
		}
	}
	
	public void removeCredits(Player p, int metal)
	{
		econ.withdrawPlayer(p, metal);
		
		SQLUtilities.getUtilities().execUpdate("INSERT INTO "
				+ "player_info(uuid, credits) "
				+ "VALUES ('"+p.getUniqueId()+"', '"+econ.getBalance(p)+"') "
				+ "ON DUPLICATE KEY UPDATE credits="+econ.getBalance(p));
	}
	public DisguiseAPI getDisguiseAPI()
	{
		return disguiseAPI;
	}
    public Permission getPerms()
    {
    	if (perms == null)
    	{
    		setupPermissions();
    	}
    	return perms;
    }
    public Economy getEcon()
    {
    	if (econ == null)
    	{
    		setupEconomy();
    	}
    	return econ;
    }
	public ProtocolManager getProtocolManager() 
	{
		return protocolManager;
	}
}
