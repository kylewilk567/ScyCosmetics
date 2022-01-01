package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import org.bukkit.inventory.ItemStack;

public class AFKEffect extends Cosmetic implements Cloneable {
	
	AFKEffectStyle style;
	int step = 0;

	public AFKEffect(String id, CosmeticTier tier, ItemStack displayItem, AFKEffectStyle style) {
		super(id, CosmeticType.AFK_EFFECT, tier, displayItem);
		this.style = style;
	}
	
	public AFKEffectStyle getStyle() {
		return style;
	}
	
	public void setStep(int step) {
		this.step = step;
	}
	
	public int getStep() {
		return step;
	}
	
	public int step() {
		return step++;
	}

	@Override
	public String[] getCosData() {
		return null;
	}
	
	
    public AFKEffect clone() throws CloneNotSupportedException  {
        return (AFKEffect) super.clone();
    }

}
