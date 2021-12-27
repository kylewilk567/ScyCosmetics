package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import org.bukkit.inventory.ItemStack;

public class LastWords extends Cosmetic {
	
	private final String finalMessage;

	public LastWords(String id, CosmeticTier tier, ItemStack displayItem, String message) {
		super(id, CosmeticType.LAST_WORDS, tier, displayItem);
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
