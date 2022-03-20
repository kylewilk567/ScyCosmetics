package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class AFKEffect extends Cosmetic implements Cloneable {
	
	AFKEffectStyle style;
	Particle particle;
	List<File> files;
	List<Double> rotations;
	int step = 0;
	double frontBackOffset;
	double upDownOffset;
	double leftRightOffset;

	public AFKEffect(String id, CosmeticTier tier, ItemStack displayItem, double buyPrice,
			List<String> purchaseableAfterTimes, List<String> purchaseableBeforeTimes, boolean isUnobtainable,
			AFKEffectStyle style, Particle particle, @Nullable List<File> files, @Nullable String offset,
			@Nullable List<Double> rotations) {
		super(id, CosmeticType.AFK_EFFECT, tier, displayItem, buyPrice, purchaseableAfterTimes, purchaseableBeforeTimes, isUnobtainable);
		this.style = style;
		this.particle = particle;
		this.files = files;
		if(offset != null) {
			String[] offsetArr = offset.split(":");
			frontBackOffset = Double.parseDouble(offsetArr[0]);
			upDownOffset = Double.parseDouble(offsetArr[1]);
			leftRightOffset = Double.parseDouble(offsetArr[2]);
		}
		this.rotations = rotations.stream().map(degree -> Math.toRadians(degree)).collect(Collectors.toList());
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
	
	public List<File> getFiles() {
		return files;
	}
	
	/**
	 * 
	 * @return The amount, in radians, to rotate each file in getFiles() in the same order.
	 */
	public List<Double> getRotations(){
		return rotations;
	}
	
	public double getFrontBackOffset() {
		return frontBackOffset;
	}
	
	public double getUpDownOffset() {
		return upDownOffset;
	}
	
	public double getLeftRightOffset() {
		return leftRightOffset;
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
