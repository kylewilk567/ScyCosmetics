package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import org.bukkit.inventory.ItemStack;

public class Title extends Cosmetic {

	private String title;
	
	public Title(String id, CosmeticTier tier, ItemStack displayItem) {
		super(id, CosmeticType.TITLE, tier, displayItem);
		
	}

	@Override
	public String[] getCosData() {
		return null;
	}
	
	

}
