package xyz.scyllasrock.ScyCosmetics.spigot.objects;


import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;

public class KillEffect extends Cosmetic {

	private static Main plugin = Main.getInstance();
	
	private KillEffectStyle style;
	
	public KillEffect(String id, CosmeticTier tier, ItemStack displayItem, double buyPrice,
			List<String> purchaseableAfterTimes, List<String> purchaseableBeforeTimes, boolean isUnobtainble,
			KillEffectStyle style) {
		super(id, CosmeticType.KILL_EFFECT, tier, displayItem, buyPrice, purchaseableAfterTimes, purchaseableBeforeTimes, isUnobtainble);
		this.style = style;
	}
	
	public KillEffectStyle getStyle() {
		return style;
	}

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
