package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;

public class ArrowTrail extends Cosmetic {

	private Main plugin = Main.getInstance();
	
	private final Particle particle;
	private final boolean hasCorrectCustomData;


	public ArrowTrail(String id, CosmeticTier tier, ItemStack displayItem, double buyPrice,
			List<String> purchaseableAfterTimes, List<String> purchaseableBeforeTimes, boolean isUnobtainble,
			Particle particle) {		
		super(id, CosmeticType.ARROW_TRAIL, tier, displayItem, buyPrice, purchaseableAfterTimes, purchaseableBeforeTimes, isUnobtainble);
		this.particle = particle;
		
		String[] customData = this.getCosData();
		if(customData == null) {
			hasCorrectCustomData = true;
			return;
		}
		String customDataValue = customData[0];
		//*** BETTER WAY TO MATCH UP PARTICLES WITH KEYS
		if(customDataValue.equalsIgnoreCase("rainbow") || customDataValue.equalsIgnoreCase("christmas") && this.getParticle().equals(Particle.NOTE)) hasCorrectCustomData = true;
		else if(customDataValue.startsWith("RGB") && this.getParticle().equals(Particle.REDSTONE)) hasCorrectCustomData = true;
		else hasCorrectCustomData = false;	
		
	}
	
	public Particle getParticle() {
		return particle;
	}
	
	
	/**
	 * 
	 * @param trail
	 * @return - true if particle and custom data are a correct match
	 */
	public boolean hasCorrectCustomData() {
		return hasCorrectCustomData;
	}

	/**
	 * @return String array of size 1
	 */
	@Override
	public String[] getCosData() {
		if(this.getDisplayItem().hasItemMeta()) {
			if(this.getDisplayItem().getItemMeta().getPersistentDataContainer()
					.has(new NamespacedKey(plugin, "ScyCos_data"), PersistentDataType.STRING)) {
				return new String[] {this.getDisplayItem().getItemMeta().getPersistentDataContainer()
						.get(new NamespacedKey(plugin, "ScyCos_data"), PersistentDataType.STRING)};
			}
		}
		return null;
	}

}
