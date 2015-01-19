package com.chaseoes.tf2.boosters;

public class Booster 
{
	String name;
	int time;
	int boost;
	
	public Booster(String name, int time, int boost)
	{
		this.name = name;
		this.time = time;
		this.boost = boost;
	}
	
	public String toString()
	{
		return name+"_"+time+"_"+boost;
	}
	
	public String getPlayerName()
	{
		return name;
	}
	
	public int getTime()
	{
		return time;
	}
	
	public int getBoost()
	{
		return boost;
	}
}
