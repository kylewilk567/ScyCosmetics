package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class ArrowTrail extends Cosmetic {
	
	private Particle particle;

	public ArrowTrail(String id, CosmeticTier tier, Particle particle, String displayName, Material displayItem, List<String> displayLore) {		
		super(id, CosmeticType.ARROW_TRAIL, tier, displayName, displayItem, displayLore);
		this.particle = particle;
	}
	
	public Particle getParticle() {
		return particle;
	}

}
