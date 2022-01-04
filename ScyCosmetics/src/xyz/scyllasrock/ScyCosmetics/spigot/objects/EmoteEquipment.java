package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import org.bukkit.inventory.ItemStack;

public class EmoteEquipment extends Cosmetic {

	private final ItemStack helmet;
	private final ItemStack chestplate;
	private final ItemStack leggings;
	private final ItemStack boots;
	
	public EmoteEquipment(String id, CosmeticTier tier, ItemStack displayItem, double buyPrice,
			ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
		super(id, CosmeticType.EMOTE_EQUIPMENT, tier, displayItem, buyPrice);
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
	}
	
	public ItemStack getHelmet() {
		return helmet;
	}
	
	public ItemStack getChestplate() {
		return chestplate;
	}
	
	public ItemStack getLeggings() {
		return leggings;
	}
	
	public ItemStack getBoots() {
		return boots;
	}

	
	
	@Override
	public String[] getCosData() {
		return null;
	}
	
	
	

}
