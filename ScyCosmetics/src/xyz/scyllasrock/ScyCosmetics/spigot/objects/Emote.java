package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import xyz.scyllasrock.ScyUtility.objects.Pair;

public class Emote extends Cosmetic implements Cloneable {
	
	
	private final List<Pair<Integer, EmoteStep>> positions;
	//private final List<EmoteStep> positions;
	private int currentStep;
	private int waitCount;
	private final boolean disableBasePlate;
	private final boolean setSmall;

	public Emote(String id, CosmeticTier tier, ItemStack displayItem, List<Pair<Integer, EmoteStep>> positions, boolean disableBasePlate, boolean setSmall) {
		super(id, CosmeticType.EMOTE_DANCE, tier, displayItem);
		this.positions = positions;
		currentStep = 0;
		this.disableBasePlate = disableBasePlate;
		this.setSmall = setSmall;
	}
	
	//How to tell when emote is done vs. when it should not move vs. when it should move
	//Also don't forget to alter data to not input repeat steps
	/**
	 * 
	 * @return Integer is a code. If pair is 0,null - wait. If pair is 0, not null, set next step. If pair is 1, null - emote is done
	 */
	public Pair<Integer, EmoteStep> stepPosition() {
		if(currentStep < positions.size()) {
			if(waitCount == positions.get(currentStep).getFirst()) { //Return next step
				waitCount = 0;
				return new Pair<Integer, EmoteStep>(0, positions.get(currentStep++).getSecond());
			}
			else { //Wait
				++waitCount;
				return new Pair<Integer, EmoteStep>(0, null);
			}

		}
		//Emote is done
		return new Pair<Integer, EmoteStep>(1, null);
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
