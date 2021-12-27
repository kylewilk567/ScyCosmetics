package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public abstract class Cosmetic {
	
	private final String id;
	private final CosmeticType type;
	private final CosmeticTier tier;
	private final ItemStack displayItem;
	
	public Cosmetic(String id, CosmeticType type, CosmeticTier tier, ItemStack displayItem) {
		this.id = id;
		this.type = type;
		this.tier = tier;
		this.displayItem = displayItem;

	}
	
	public String getId() {
		return id;
	}
	
	public CosmeticType getType() {
		return type;
	}
	
	public CosmeticTier getTier() {
		return tier;
	}
	
	public ItemStack getDisplayItem() {
		return displayItem;
	}
	
	public abstract String[] getCosData();
	
	
	


}
