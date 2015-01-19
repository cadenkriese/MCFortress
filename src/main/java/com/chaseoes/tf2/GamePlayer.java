package com.chaseoes.tf2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.bossbar.BossBarAPI;

import com.chaseoes.tf2.boosters.BoosterStatusFile;
import com.chaseoes.tf2.classes.TF2Class;
import com.chaseoes.tf2.extras.MediGunHandler;
import com.chaseoes.tf2.extras.UberActionBar;
import com.chaseoes.tf2.listeners.TF2DeathListener;
import com.chaseoes.tf2.utilities.MedicUtilities;
//import com.chaseoes.tf2.engineer.EngineerHandler;
import com.chaseoes.tf2.utilities.NametagUtilities;

import de.robingrether.idisguise.api.DisguiseAPI;
import fr.mrsheepsheep.tinthealth.THAPI;
import fr.mrsheepsheep.tinthealth.TintHealth;

public class GamePlayer {

    Player player;
    String map;
    Team team;
    int kills;
    int deaths;
    int totalKills;
    int totalDeaths;
    int currentKillstreak;
    int arrowsFired;
    int pointsCaptured;
    int respawnTimer;
    int backStabs;
	int headShots;
	int scopeCharge;
	int gameCredits;


	long timeEnteredGame;
    boolean inLobby;
    boolean usingChangeClassButton;
    boolean makingChangeClassButton;
    boolean makingClassButton;
    boolean justSpawned;
    boolean creatingContainer;
    boolean isDead;
    boolean isOnCP;
    boolean isInvis;
    boolean invertInvis;
    boolean isAtReSupply;
    boolean isScoping;
    boolean justFired;
    boolean isUbered;
    boolean isUbering;
    Player healing;
	String critMessage;

	Location spawnLoc;
    String mapCreatingItemFor;
    String classButtonType;
    String classButtonName;
    TF2Class currentClass;
    String playerLastDamagedBy;
    Player killer;
    StatCollector stats;
    HashSet<Integer> killstreaks;
    Player disguise;
    ArrayList<Entity> mines;
    DamageCause lastDamageCause;

    ItemStack[] savedInventoryItems;
    ItemStack[] savedArmorItems;
    GameMode savedGameMode;
    float savedXPCount;
    int savedLevelCount;
    int savedFoodLevel;
    int savedHealth;
    int invisTime;
    float uberPercent;
    
	//EngineerHandler engiHandler;

    public GamePlayer(Player p) 
    {
        player = p;
        team = null;
        map = null;
        kills = 0;
        deaths = 0;
        inLobby = false;
        savedInventoryItems = null;
        savedArmorItems = null;
        savedXPCount = 0;
        savedLevelCount = 0;
        savedFoodLevel = 0;
        savedHealth = 0;
        currentKillstreak = 0;
        arrowsFired = 0;
        pointsCaptured = 0;
        timeEnteredGame = System.currentTimeMillis();
        savedGameMode = null;
        playerLastDamagedBy = this.getName();
        killstreaks = new HashSet<Integer>();
        stats = new StatCollector(p);
        spawnLoc = null;
        killer = null;
        respawnTimer = 0;
        isOnCP = false;
        isInvis = false;
        invisTime = 10;
        invertInvis = false;
        isAtReSupply = false;
        backStabs = 0;
        headShots = 0;
        critMessage = "";
        gameCredits = 0;
        uberPercent = 0;
        isUbered = false;
        isUbering = false;
        healing = null;
    }
    
	public int getGameCredits() {
		return gameCredits;
	}

	public void addGameCredits(int gameCredits) 
	{
		player.sendMessage(ChatColor.GOLD+"+ $"+gameCredits+" Credits"+ChatColor.GRAY+BoosterStatusFile.getFile().getBoosterMessage());
		this.gameCredits = this.gameCredits+gameCredits;
	}

	public int getScopeCharge() {
		return scopeCharge;
	}
	
	public boolean JustFired() {
		return justFired;
	}

	public void setJustFired(boolean justFired) {
		this.justFired = justFired;
	}

	public void setScopeCharge(int scopeCharge) {
		this.scopeCharge = scopeCharge;
	}
    
	public boolean isScoping() {
		return isScoping;
	}

	public void setScoping(boolean isScoping) {
		this.isScoping = isScoping;
	}
    
	public String critMessage() {
		return critMessage;
	}

	public void setCritMessage(String critMessage) {
		this.critMessage = critMessage;
	}

	public int getHeadShots() {
		return headShots;
	}

	public void setHeadShots(int headShots) {
		this.headShots = headShots;
	}
	
    public int getBackStabs() {
		return backStabs;
	}

	public void setBackStabs(int backStabs) {
		this.backStabs = backStabs;
	}
	
	public boolean isAtReSupply() {
		return isAtReSupply;
	}

	public void setAtReSupply(boolean isAtReSupply) {
		this.isAtReSupply = isAtReSupply;
	}
    
	public boolean InvertInvis() {
		return invertInvis;
	}

	public void setInvertInvis(boolean invertInvis) {
		this.invertInvis = invertInvis;
	}

	public int getInvisTime() {
		return invisTime;
	}

	public void setInvisTime(int invisTime) {
		this.invisTime = invisTime;
	}

	public boolean isInvis() {
		return isInvis;
	}

	public void setInvis(boolean isInvis) {
		this.isInvis = isInvis;
	}

	public int getRespawnTimer() {
		return respawnTimer;
	}

	public void setRespawnTimer(int respawnTimer) {
		this.respawnTimer = respawnTimer;
	}
    public Game getGame() {
        if (getCurrentMap() == null) {
            return null;
        }
        return GameUtilities.getUtilities().getGame(TF2.getInstance().getMap(getCurrentMap()));
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return player.getName();
    }

    public boolean isIngame() {
        return team != null;
    }

    public boolean isInLobby() {
        return inLobby;
    }

    public void setInLobby(boolean b) {
        inLobby = b;
    }

    public String getCurrentMap() {
        return map;
    }

    public void setMap(String m) {
        map = m;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team t) {
        team = t;
    }

    public int getKills() {
        return kills;
    }
    public boolean isOnCP() {
		return isOnCP;
	}
	public void setOnCP(boolean isOnCP) {
		this.isOnCP = isOnCP;
	}

    public void setKills(int i) {
        if (i == -1) {
            kills++;
        } else {
            kills = i;
        }
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int i) {
        if (i == -1) {
            deaths++;
        } else {
            deaths = i;
        }
    }

    public String getTeamColor() {
        if (!isIngame()) 
        {
            return null;
        }

        String color = "" + ChatColor.BLUE;
        if (getTeam() == Team.RED) 
        {
            color = "" + ChatColor.RED;
        }

        return color;
    }

    public void leaveCurrentGame() 
    {
    	if (healing != null)
    	{
    		MediGunHandler.getHandler().stopHealingProcess(player, healing);
    		UberActionBar.getBar().endBar(player);
    	}
    	
    	if (isUbered || isUbering)
    	{
    		MedicUtilities.getUtilities().setUbered(player, false);
    	}
    	
    	DisguiseAPI api = TF2.getInstance().getDisguiseAPI();
    	NametagUtilities.applyTag(player);
    	
    	if (isDead)
    	{
    		TF2DeathListener.getListener().cancelRespawn(this);
    	}
    	
    	if (BossBarAPI.hasBar(player))
    	{
    		BossBarAPI.removeBar(player);
    	}
		TintHealth th = (TintHealth) TF2.getInstance().getServer().getPluginManager().getPlugin("TintHealth");
		THAPI THapi = th.getAPI();
		THapi.setTint(player, 0);
    	if (api.isDisguised(player))
    	{
    		api.undisguiseToAll(player);
    	}
        Game game = getGame();
        game.map.getQueue().remove(player);
        TF2Class c = new TF2Class("NONE");
        c.clearInventory(player);
        loadInventory();
        if (game.getStatus() == GameStatus.STARTING && game.playersInGame.size() == 1) {
            game.stopMatch(true, true);
        }

        if (game.playersInGame.size() == 0) 
        {
            game.stopMatch(true, true);
        }

        player.teleport(MapUtilities.getUtilities().loadLobby());

        if (!TF2.getInstance().isDisabling)
        {
            game.map.getQueue().check();
        }
        
        UberActionBar.getBar().endBar(player);
        
        player.setMaxHealth(20d);
        clear();
    }

    public ArrayList<GamePlayer> getEnemyMebers()
    {
    	ArrayList<GamePlayer> list = new ArrayList<GamePlayer>();
    	for (GamePlayer gp : getGame().playersInGame.values())
    	{
    		if (gp.getTeam() != team)
    		{
    			list.add(gp);
    		}
    	}
    	return list;
    }
    
    public void saveInventory() {
        savedInventoryItems = player.getInventory().getContents();
        savedArmorItems = player.getInventory().getArmorContents();
        savedXPCount = player.getExp();
        savedLevelCount = player.getLevel();
        savedFoodLevel = player.getFoodLevel();
        savedHealth = (int) player.getHealth();
        savedGameMode = player.getGameMode();
    }

    public void loadInventory() {
        player.getInventory().setContents(savedInventoryItems);
        player.getInventory().setArmorContents(savedArmorItems);
        player.setExp(savedXPCount);
        player.setLevel(savedLevelCount);
        player.setFoodLevel(savedFoodLevel);
        player.setHealth(savedHealth);
        player.setGameMode(savedGameMode);
        player.updateInventory();
    }

    public void setUsingChangeClassButton(Boolean b) {
        usingChangeClassButton = b;
    }

    public void setMakingChangeClassButton(Boolean b) {
        makingChangeClassButton = b;
    }

    public boolean isUsingChangeClassButton() {
        return usingChangeClassButton;
    }

    public boolean isMakingChangeClassButton() {
        return makingChangeClassButton;
    }

    public void setMakingClassButton(boolean b) {
        makingClassButton = b;
    }

    public boolean isMakingClassButton() {
        return makingClassButton;
    }

    public void setClassButtonType(String s) {
        classButtonType = s;
    }

    public String getClassButtonType() {
        return classButtonType;
    }

    public void setClassButtonName(String name) {
        classButtonName = name;
    }

    public String getClassButtonName() {
        return classButtonName;
    }

    public TF2Class getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(TF2Class c) {
        currentClass = c;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(int i) {
        if (i == -1) {
            totalKills++;
        } else {
            totalKills = i;
        }
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void settotalDeaths(int i) {
        if (i == -1) {
            totalDeaths++;
        } else {
            totalDeaths = i;
        }
    }

    public boolean justSpawned() {
        return justSpawned;
    }

    public void setJustSpawned(Boolean b) {
        justSpawned = b;
    }

    public GamePlayer getPlayerLastDamagedBy() {
        return GameUtilities.getUtilities().getGamePlayer(Bukkit.getPlayerExact(playerLastDamagedBy));
    }

    public void setPlayerLastDamagedBy(GamePlayer player) {
        if (player != null) {
            playerLastDamagedBy = player.getName();
        } else {
            playerLastDamagedBy = this.getName();
        }
    }

    public boolean isCreatingContainer() {
        return creatingContainer;
    }

    public void setCreatingContainer(boolean bool) {
        creatingContainer = bool;
    }

    public String getMapCreatingItemFor() {
        return mapCreatingItemFor;
    }

    public void setMapCreatingItemFor(String map) {
        mapCreatingItemFor = map;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setIsDead(boolean bool) {
        isDead = bool;
    }

    public StatCollector getStatCollector() {
        return stats;
    }

    public void setCurrentKillstreak(int i) {
        currentKillstreak = i;
    }

    public int getCurrentKillstreak() {
        return currentKillstreak;
    }

    public void setArrowsFired(int i) {
        if (i != -1) {
            arrowsFired = i;
            return;
        }
        arrowsFired++;
    }

    public int getArrowsFired() {
        return arrowsFired;
    }

    public void setPointsCaptured(int i) {
        if (i != -1) {
            pointsCaptured = i;
            return;
        }
        pointsCaptured++;
    }

    public int getPointsCaptured() {
        return pointsCaptured;
    }

    public void setTimeEnteredGame() {
        timeEnteredGame = System.currentTimeMillis();
    }

    public int getTotalTimeIngame() {
        return (int) ((System.currentTimeMillis() - timeEnteredGame) / 1000);
    }

    public int getHighestKillstreak() {
        if (!killstreaks.isEmpty()) {
            return Collections.max(killstreaks);
        }
        return 0;
    }

    public void addKillstreak(Integer i) {
        killstreaks.add(i);
    }
    
    public Location getSpawnLoc() {
    	return spawnLoc;
    }
    
    public void setSpawnLoc(Location loc)
    {
    	spawnLoc = loc;
    }
    public Player getKiller()
    {
    	return killer;
    }
    public void setKiller(Player k)
    {
    	killer = k;
    }

	public float getUberPercent() 
	{
		return uberPercent;
	}

	public void setUberPercent(float uberPercent) 
	{
		if (uberPercent == -1)
		{
			this.uberPercent++;
			return;
		}
		this.uberPercent = uberPercent;
	}

	public boolean isUbered() {
		return isUbered;
	}

	public void setUbered(boolean isUbered) {
		this.isUbered = isUbered;
	}

	public boolean isUbering() {
		return isUbering;
	}

	public void setUbering(boolean isUbering) {
		this.isUbering = isUbering;
	}

	public Player getHealing() 
	{
		return healing;
	}

	public void setHealing(Player healing) 
	{
		this.healing = healing;
	}

	public Player getDisguise() 
	{
		return disguise;
	}

	public void setDisguise(Player disguise) 
	{
		this.disguise = disguise;
	}

	public ArrayList<Entity> getMines() 
	{
		return mines;
	}

	public void addMine(Entity mine) 
	{
		mines.add(mine);
	}

	public DamageCause getLastDamageCause() 
	{
		return lastDamageCause;
	}

	public void setLastDamageCause(DamageCause lastDamageCause) 
	{
		this.lastDamageCause = lastDamageCause;
	}

	public void clear() 
	{
		player.setFireTicks(0);
		for (Player p2 : Bukkit.getOnlinePlayers())
		{
			player.showPlayer(p2);
			p2.showPlayer(player);
		}
		
        map = null;
        team = null;
        kills = 0;
        deaths = 0;
        totalKills = 0;
        totalDeaths = 0;
        inLobby = false;
        usingChangeClassButton = false;
        makingChangeClassButton = false;
        makingClassButton = false;
        creatingContainer = false;
        isDead = false;
        justSpawned = false;
        classButtonType = null;
        classButtonName = null;
        currentClass = null;
        savedInventoryItems = null;
        savedArmorItems = null;
        savedGameMode = null;
        savedXPCount = 0f;
        savedLevelCount = 0;
        savedFoodLevel = 0;
        savedHealth = 0;
        currentKillstreak = 0;
        arrowsFired = 0;
        pointsCaptured = 0;
        timeEnteredGame = System.currentTimeMillis();
        playerLastDamagedBy = this.getName();
        mapCreatingItemFor = null;
        killstreaks.clear();
        stats = new StatCollector(player);
        killer = null;
        isOnCP = false;
        isInvis = false;
        invisTime = 10;
        invertInvis = false;
        isAtReSupply = false;
        critMessage = "";
        gameCredits = 0;
        isDead = false;
        uberPercent = 0.0f;
        disguise = null;
        healing = null;
        isUbering = false;
        isUbered = false;
    }
}
