package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Title extends Cosmetic {

	private String title;
	
	public Title(String id, CosmeticTier tier, ItemStack displayItem, double buyPrice,
			List<String> purchaseableAfterTimes, List<String> purchaseableBeforeTimes, boolean isUnobtainble,
			String title) {
		super(id, CosmeticType.TITLE, tier, displayItem, buyPrice, purchaseableAfterTimes, purchaseableBeforeTimes, isUnobtainble);
		this.title = title;		
	}
	
	public String getTitle() {
		return title;
	}

	@Override
	public String[] getCosData() {
		return null;
	}
	
	

}
