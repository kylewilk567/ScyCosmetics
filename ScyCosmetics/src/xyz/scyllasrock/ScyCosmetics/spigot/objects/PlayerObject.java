package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;

public class PlayerObject {
	
	Main plugin = Main.getPlugin(Main.class);
	
	private UUID uuid;
	
	//Inventory information
	private ItemFilter itemFilter;
	private boolean showLockedCosmetics;

	private List<String> unlockedCosmetics = new ArrayList<String>();
	private List<String> activeCosmetics = new ArrayList<String>();
	private List<DirtyDataType> dirtyData = new ArrayList<DirtyDataType>();
	

	
	
	public PlayerObject(UUID uuid, ItemFilter itemFilter, boolean showLockedCosmetics, List<String> unlockedCosmetics) {
		this.uuid = uuid;
		this.itemFilter = itemFilter;
		this.showLockedCosmetics = showLockedCosmetics;
		this.unlockedCosmetics = unlockedCosmetics;
	}
	
	public PlayerObject(UUID uuid, ItemFilter itemFilter, boolean showLockedCosmetics, List<String> unlockedCosmetics, List<String> activeCosmetics) {
		this.uuid = uuid;
		this.itemFilter = itemFilter;
		this.showLockedCosmetics = showLockedCosmetics;
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
	
	public ItemFilter getItemFilter() {
		return itemFilter;
	}
	
	public boolean showLockedCosmetics() {
		return showLockedCosmetics;
	}
	
	
	/**
	 * Returns active cosmetic id of specified type
	 * @param type
	 * @return
	 */
	public String getActiveCosmeticId(CosmeticType type) {
		for(String cos: activeCosmetics) {
			if(plugin.getCosmetics().get(cos).getType().equals(type)) return cos;
		}
		return null;
	}
	
	/**
	 * Returns active cosmetic object of specified type
	 * @param type
	 * @return
	 */
	public Cosmetic getActiveCosmetic(CosmeticType type) {
		for(String cos: activeCosmetics) {
			if(plugin.getCosmetics().get(cos).getType().equals(type)) return plugin.getCosmetics().get(cos);
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
			activeCosmetics.remove(getActiveCosmeticId(cos.getType()));
			activeCosmetics.add(cos.getId());
			dirty = true;
		}
		else dirty = activeCosmetics.add(cos.getId());
		
		if(dirty) dirtyData.add(DirtyDataType.ACTIVE_COSMETICS);
	}
	
	public void removeActiveCosmetic(CosmeticType type) {
		activeCosmetics.remove(getActiveCosmeticId(type));
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
	
	public boolean addUnlockedCosmetic(String id) {
		if(unlockedCosmetics.contains(id)) return false;
		unlockedCosmetics.add(id);
		dirtyData.add(DirtyDataType.UNLOCKED_COSMETICS);
		return true;
	}
	
	public void setItemFilter(ItemFilter filter) {
		this.itemFilter = filter;
		dirtyData.add(DirtyDataType.ITEM_FILTER);
	}
	
	public void toggleItemFilter() {
		switch(itemFilter) {
		case NAME:
			itemFilter = ItemFilter.RARITY_ASCENDING;
			break;
		case RARITY_ASCENDING:
			itemFilter = ItemFilter.RARITY_DESCENDING;
			break;
		case RARITY_DESCENDING:
			itemFilter = ItemFilter.LEAST_RECENT;
			break;
		case LEAST_RECENT:
			itemFilter = ItemFilter.MOST_RECENT;
			break;
		case MOST_RECENT:
			itemFilter = ItemFilter.NAME;
			break;
		}
		dirtyData.add(DirtyDataType.ITEM_FILTER);
	}
	
	public void setShowLockedCosmetics(boolean show) {
		this.showLockedCosmetics = show;
		dirtyData.add(DirtyDataType.SHOW_LOCKED_COSMETICS);
	}
	
}
