package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;

public class PlayerObject {
	
	Main plugin = Main.getPlugin(Main.class);
	
	private UUID uuid;

	private List<String> unlockedCosmetics = new ArrayList<String>();
	private List<String> activeCosmetics = new ArrayList<String>();
	private List<DirtyDataType> dirtyData = new ArrayList<DirtyDataType>();
	
	
	public PlayerObject(UUID uuid, List<String> unlockedCosmetics) {
		this.uuid = uuid;
		this.unlockedCosmetics = unlockedCosmetics;
	}
	
	public PlayerObject(UUID uuid, List<String> unlockedCosmetics, List<String> activeCosmetics) {
		this.uuid = uuid;
		this.unlockedCosmetics = unlockedCosmetics;
		this.activeCosmetics = activeCosmetics;
	}
	
	
	public List<DirtyDataType> getDirtyData(){
		return dirtyData;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public List<String> getUnlockedCosmetics(){
		return unlockedCosmetics;
	}
	
	public List<String> getActiveCosmetics(){
		return activeCosmetics;
	}
	
	
	/**
	 * Returns active cosmetic of specified type
	 * @param type
	 * @return
	 */
	public String getActiveCosmetic(CosmeticType type) {
		for(String cos: activeCosmetics) {
			if(plugin.getCosmetics().get(cos).getType().equals(type)) return cos;
		}
		return null;
	}
	
	/**
	 * Sets specified cosmetic as active. Disables other active cosmetics of the same type.
	 * @param cos
	 */
	public void setActiveCosmetic(Cosmetic cos) {
		boolean dirty = false;
		if(hasActiveCosmeticType(cos.getType())) {
			if(activeCosmetics.remove(getActiveCosmetic(cos.getType())) || activeCosmetics.add(cos.getId())) {
				dirty = true;
			}
		}
		else dirty = activeCosmetics.add(cos.getId());
		
		if(dirty) dirtyData.add(DirtyDataType.ACTIVE_COSMETICS);
	}
	
	public boolean hasActiveCosmeticType(CosmeticType type) {
		for(String cos : activeCosmetics) {
			if(plugin.getCosmeticFromId(cos).getType().equals(type)) return true;
		}
		return false;
	}
	
	public boolean hasCosmeticUnlocked(Cosmetic cos) {
		if(unlockedCosmetics.contains(cos.getId())) return true;
		return false;
	}
	
}
