package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class LogMessage extends Cosmetic {
	
	private final String logInMessage;
	private final String logOffMessage;
	private Sound logInSound;

	public LogMessage(String id, CosmeticTier tier, ItemStack displayItem, double buyPrice,
			List<String> purchaseableAfterTimes, List<String> purchaseableBeforeTimes, boolean isUnobtainble,
			String logInMessage, String logOffMessage, Sound logInSound) {
		super(id, CosmeticType.LOG_MESSAGE, tier, displayItem, buyPrice, purchaseableAfterTimes, purchaseableBeforeTimes, isUnobtainble);
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
