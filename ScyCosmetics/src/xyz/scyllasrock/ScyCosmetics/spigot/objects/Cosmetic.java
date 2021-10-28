package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public abstract class Cosmetic {
	
	private String id;
	private CosmeticType type;
	private String displayName;
	private ItemStack displayItem;
	private List<String> displayLore;
	
	public Cosmetic(String id, CosmeticType type, String displayName, ItemStack displayItem, List<String> displayLore) {
		this.id = id;
		this.type = type;
		this.displayName = displayName;
		this.displayItem = displayItem;
		this.displayLore = displayLore;
	}
	
	public String getId() {
		return id;
	}
	
	public CosmeticType getType() {
		return type;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public ItemStack getDisplayItem() {
		return displayItem;
	}
	
	public List<String> getDisplayLore() {
		return displayLore;
	}
	
	


}
