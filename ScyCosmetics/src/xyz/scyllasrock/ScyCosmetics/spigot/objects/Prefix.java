package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Prefix extends Cosmetic implements Cloneable {
	
	private final String prefix;
	private final List<String> colorCodes;
	private int currentCodeIndex;
	private final int colorChangeTicks;

	public Prefix(String id, CosmeticTier tier, ItemStack displayItem, String prefix, List<String> colorCodes, int colorChangeTicks) {
		super(id, CosmeticType.PREFIX, tier, displayItem);
		this.prefix = prefix;
		this.colorCodes = colorCodes;
		currentCodeIndex = 0;
		if(colorChangeTicks != 0) this.colorChangeTicks = colorChangeTicks;
		else this.colorChangeTicks = 10;
	}
	

	public String getPrefix() {
		return prefix;
	}
	
	public String getColorCode() {
		return colorCodes.get(currentCodeIndex);
	}
	
	public int getColorChangeTicks() {
		return colorChangeTicks;
	}
	
	public int getCurrentCodeIndex() {
		return currentCodeIndex;
	}
	
	public void stepCurrentCodeIndex() {
		if(currentCodeIndex == colorCodes.size() - 1) currentCodeIndex = 0;
		else ++currentCodeIndex;
	}
	
    public Prefix clone() throws CloneNotSupportedException {
        return (Prefix) super.clone();
    }

	/**
	 * There is no cosmetic data for this object!
	 */
	@Override
	public String[] getCosData() {
		return null;
	}

}
