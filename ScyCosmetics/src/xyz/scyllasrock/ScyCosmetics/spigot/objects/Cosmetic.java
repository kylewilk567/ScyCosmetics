package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import org.bukkit.inventory.ItemStack;

public abstract class Cosmetic {
	
	private final String id;
	private final CosmeticType type;
	private final CosmeticTier tier;
	private final ItemStack displayItem;
	private final double buyPrice;
	
	public Cosmetic(String id, CosmeticType type, CosmeticTier tier, ItemStack displayItem, double buyPrice) {
		this.id = id;
		this.type = type;
		this.tier = tier;
		this.displayItem = displayItem;
		this.buyPrice = buyPrice;
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
	
	public double getBuyPrice() {
		return buyPrice;
	}
	
	public abstract String[] getCosData();
	
	
	


}
