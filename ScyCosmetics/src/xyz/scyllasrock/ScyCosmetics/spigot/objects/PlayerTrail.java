package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;

public class PlayerTrail extends Cosmetic{
	
	private Main plugin = Main.getInstance();
	
	private final List<Particle> particles;
	private final int count;
	private final double offsetX;
	private final double offsetY;
	private final double offsetZ;
	
	public PlayerTrail(String id, CosmeticTier tier, ItemStack displayItem, double buyPrice, List<Particle> particles, int count,
			double offsetX, double offsetY, double offsetZ) {		
		super(id, CosmeticType.PLAYER_TRAIL, tier, displayItem, buyPrice);
		this.particles = particles;
		this.count = count;
		this.offsetX = offsetX;
		this.offsetY = offsetY + 0.1;
		this.offsetZ = offsetZ;
	}
	
	public List<Particle> getParticles(){
		return particles;
	}
	
	public int getCount() {
		return count;
	}
	
	public double getOffsetX() {
		return offsetX;
	}
	
	public double getOffsetY() {
		return offsetY;
	}
	
	public double getOffsetZ() {
		return offsetZ;
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
