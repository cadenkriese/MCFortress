package com.chaseoes.tf2;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import com.chaseoes.tf2.Game;
import com.chaseoes.tf2.GamePlayer;

public class Sounds {
	public static void VictoryRed(Game game){
		for (GamePlayer gp : game.playersInGame.values()){
			Player p = gp.getPlayer();
    		if (gp.getTeam() == Team.RED) {
    			p.playSound(p.getLocation(), Sound.WOLF_PANT, 10000, 0);
			}
		}
	}
	public static void VictoryBlue(Game game){
		for (GamePlayer gp : game.playersInGame.values()){
			Player p = gp.getPlayer();
			if (gp.getTeam() == Team.BLUE){
				p.playSound(p.getLocation(), Sound.WOLF_PANT, 1000, 0);
			}
		}
	}
	public static void LoseRed(Game game){
		for(GamePlayer gp : game.playersInGame.values()){
			Player p = gp.getPlayer();
			if (gp.getTeam() == Team.RED){
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 10000, 1);
			}
		}
	}
	public static void LoseBlue(Game game){
		for(GamePlayer gp : game.playersInGame.values()){
			Player p = gp.getPlayer();
			if (gp.getTeam() == Team.BLUE){
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 10000, 1);
			}
		}
	}
}
