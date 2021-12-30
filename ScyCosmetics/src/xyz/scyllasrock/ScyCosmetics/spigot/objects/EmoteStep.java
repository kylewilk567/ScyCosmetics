package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class EmoteStep {
	
	private final float rotPose;
	private final EulerAngle headPose;
	private final EulerAngle bodyPose;
	private final EulerAngle leftArmPose;
	private final EulerAngle rightArmPose;
	private final EulerAngle leftLegPose;
	private final EulerAngle rightLegPose;
	private final ItemStack mainHand;
	private final ItemStack offHand;
	
	public EmoteStep(float rotPose, EulerAngle headPose, EulerAngle bodyPose,
			EulerAngle leftArmPose, EulerAngle rightArmPose, EulerAngle leftLegPose, EulerAngle rightLegPose,
			ItemStack mainHand, ItemStack offHand) {
		this.rotPose = rotPose;
		this.headPose = headPose;
		this.bodyPose = bodyPose;
		this.leftArmPose = leftArmPose;
		this.rightArmPose = rightArmPose;
		this.leftLegPose = leftLegPose;
		this.rightLegPose = rightLegPose;
		this.mainHand = mainHand;
		this.offHand = offHand;
	}
	
	public float getRotAmount() {
		return rotPose;
	}
	
	public EulerAngle getHeadPose() {
		return headPose;
	}
	
	public EulerAngle getBodyPose() {
		return bodyPose;
	}
	
	public EulerAngle getLeftArmPose() {
		return leftArmPose;
	}
	
	public EulerAngle getRightArmPose() {
		return rightArmPose;
	}
	
	public EulerAngle getLeftLegPose() {
		return leftLegPose;
	}
	
	public EulerAngle getRightLegPose() {
		return rightLegPose;
	}
	
	public ItemStack getMainHand() {
		return mainHand;
	}
	
	public ItemStack getOffHand() {
		return offHand;
	}
	
	

}
