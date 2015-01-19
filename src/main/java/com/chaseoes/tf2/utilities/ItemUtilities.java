package com.chaseoes.tf2.utilities;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

public class ItemUtilities 
{
	//To get an instance of the class
	public ItemUtilities() {}
	public static ItemUtilities instance = new ItemUtilities();
	public static ItemUtilities getUtilities() {    return instance;    }


	public ItemStack setColor(ItemStack item, org.bukkit.Color color) 
	{
		if (item != null && (item.getType() == Material.LEATHER_HELMET || item.getType() == Material.LEATHER_CHESTPLATE || item.getType() == Material.LEATHER_LEGGINGS || item.getType() == Material.LEATHER_BOOTS)) {
			LeatherArmorMeta i = (LeatherArmorMeta) item.getItemMeta();
			i.setColor(color);
			item.setItemMeta(i);
		}
		return item;
	}

	public void setGlowing(ItemStack stack, boolean glow)
	{
		if (stack != null && stack.getType() != Material.AIR) 
		{
			if (glow)
			{
				stack.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32);
				NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(stack);
				compound.put(NbtFactory.ofList("ench"));
			}
			else
			{
				NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(stack);
				compound.remove("ench");
			}
		}
	}
}
