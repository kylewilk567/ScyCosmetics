package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class AFKEffect extends Cosmetic implements Cloneable {
	
	AFKEffectStyle style;
	Particle particle;
	int step = 0;

	public AFKEffect(String id, CosmeticTier tier, ItemStack displayItem, double buyPrice, AFKEffectStyle style, Particle particle) {
		super(id, CosmeticType.AFK_EFFECT, tier, displayItem, buyPrice);
		this.style = style;
		this.particle = particle;
	}
	
	public AFKEffectStyle getStyle() {
		return style;
	}
	
	/**
	 * 
	 * @return effect's particle. Can be null in which case particle is not configurable
	 */
	public Particle getParticle() {
		return particle;
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
