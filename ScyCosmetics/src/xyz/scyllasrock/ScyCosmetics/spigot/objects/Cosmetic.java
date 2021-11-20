package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import net.md_5.bungee.api.ChatColor;

public abstract class Cosmetic {
	
	private final String id;
	private final CosmeticType type;
	private final CosmeticTier tier;
	private final String displayName;
	private final Material displayItem;
	private List<String> displayLore;
	
	public Cosmetic(String id, CosmeticType type, CosmeticTier tier, String displayName, Material displayItem, List<String> displayLore) {
		this.id = id;
		this.type = type;
		this.tier = tier;
		this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		this.displayItem = displayItem;
		this.displayLore = new ArrayList<String>();
		for(String s : displayLore) {
			this.displayLore.add(ChatColor.translateAlternateColorCodes('&', s));
		}
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
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Material getDisplayItem() {
		return displayItem;
	}
	
	public List<String> getDisplayLore() {
		return displayLore;
	}
	
	


}
