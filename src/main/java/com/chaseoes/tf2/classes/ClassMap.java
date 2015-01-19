package com.chaseoes.tf2.classes;

import org.bukkit.inventory.ItemStack;

public class ClassMap {
	private ItemStack myItem;
	private int slot;
	
	public ItemStack getMyItem() {
		return myItem;
	}
	public void setItemStack(ItemStack myItem) {
		this.myItem = myItem;
	}
	public int getSlot() {
		return slot;
	}
	public void setSlot(int count) {
		this.slot = count;
	}
}
