package com.chaseoes.tf2;

public enum Slot 
{
    PRIMARY("primary"), SECONDARY("secondary"), MELEE("melee");

    private final String slot;

    private Slot(String slot) 
    {
        this.slot = slot;
    }

    public String getName() 
    {
        return slot;
    }
    
    public int getInt()
    {
    	int slotNum;
    	slotNum = 0;
    	if (slot.equalsIgnoreCase("primary"))
    	{
    		slotNum = 1;
    	}
    	else if (slot.equalsIgnoreCase("secondary"))
    	{
    		slotNum = 2;
    	}
    	else if (slot.equalsIgnoreCase("melee"))
    	{
    		slotNum = 3;
    	}
    	return slotNum;
    }
}
