package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.List;

import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class ArrowTrail extends Cosmetic {
	
	private Particle particle;

	public ArrowTrail(String id, Particle particle, String displayName, ItemStack displayItem, List<String> displayLore) {
		//GIVEN ID, FETCH ALL RELEVANT STUFF FROM CONFIG
		
		super(id, CosmeticType.ARROW_TRAIL, displayName, displayItem, displayLore);
		this.particle = particle;
	}
	
	public Particle getParticle() {
		return particle;
	}

}
