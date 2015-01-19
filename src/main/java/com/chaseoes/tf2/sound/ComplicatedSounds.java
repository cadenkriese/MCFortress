package com.chaseoes.tf2.sound;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class ComplicatedSounds 
{
	//To get an instance of the class
	public static ComplicatedSounds instance = new ComplicatedSounds();
	public ComplicatedSounds() {}
	public static ComplicatedSounds getSounds() {    return instance;     }
	
	//Players using the medigun sound
	HashMap<Player, Integer> medigunMap = new HashMap<Player, Integer>();
	
	
	//TODO create flamethrower sounds.
}
