package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Emote extends Cosmetic implements Cloneable {
	
	
	private final List<EmoteStep> positions;
	private int currentStep;
	private final boolean disableBasePlate;
	private final boolean setSmall;

	public Emote(String id, CosmeticTier tier, ItemStack displayItem, List<EmoteStep> positions, boolean disableBasePlate, boolean setSmall) {
		super(id, CosmeticType.EMOTE_DANCE, tier, displayItem);
		this.positions = positions;
		currentStep = 0;
		this.disableBasePlate = disableBasePlate;
		this.setSmall = setSmall;
	}
	
	public EmoteStep stepPosition() {
		if(currentStep < positions.size()) {
			return positions.get(currentStep++);
		}
		return null;
	}
	
	public boolean disableBasePlate() {
		return disableBasePlate;
	}
	
	public boolean setSmall() {
		return setSmall;
	}
	
	
    public Emote clone() throws CloneNotSupportedException  {
        return (Emote) super.clone();
    }

	@Override
	public String[] getCosData() {
		return null;
	}
	
	

}
