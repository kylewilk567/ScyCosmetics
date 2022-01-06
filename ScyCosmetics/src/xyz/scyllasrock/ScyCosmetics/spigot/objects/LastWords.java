package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class LastWords extends Cosmetic {
	
	private final String finalMessage;

	public LastWords(String id, CosmeticTier tier, ItemStack displayItem, double buyPrice,
			List<String> purchaseableAfterTimes, List<String> purchaseableBeforeTimes, boolean isUnobtainble,
			String message) {
		super(id, CosmeticType.LAST_WORDS, tier, displayItem, buyPrice, purchaseableAfterTimes, purchaseableBeforeTimes, isUnobtainble);
		finalMessage = message;
	}
	
	/**
	 * 
	 * @return message to send to killer after player death. Includes color codes to be translated.
	 */
	public String getMessage() {
		return finalMessage;
	}

	/**
	 * Does nothing for now
	 */
	@Override
	public String[] getCosData() {
		return null;
	}
	
	

}
