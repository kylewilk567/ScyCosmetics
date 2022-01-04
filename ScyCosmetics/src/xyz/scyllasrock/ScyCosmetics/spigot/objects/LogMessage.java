package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class LogMessage extends Cosmetic {
	
	private final String logInMessage;
	private final String logOffMessage;
	private Sound logInSound;

	public LogMessage(String id, CosmeticTier tier, ItemStack displayItem, double buyPrice,
			String logInMessage, String logOffMessage, Sound logInSound) {
		super(id, CosmeticType.LOG_MESSAGE, tier, displayItem, buyPrice);
		this.logInMessage = logInMessage;
		this.logOffMessage = logOffMessage;
		this.logInSound = logInSound;
	}
	
	/**
	 * 
	 * @return logInMessage before color translation
	 */
	public String getLogInMessage() {
		return logInMessage;
	}

	/**
	 * 
	 * @return logOffMessage before color translation
	 */
	public String getLogOffMessage() {
		return logOffMessage;
	}
	
	public Sound getLogInSound() {
		return logInSound;
	}
	
	@Override
	public String[] getCosData() {
		return null;
	}

}
