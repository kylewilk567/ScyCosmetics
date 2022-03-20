package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.util.PrefixColorTimer;

public class PlayerObject {
	
	private static Main plugin = Main.getInstance();
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	
	private UUID uuid;
	
	private boolean isAFK = false; //Set to true when /scos afk run. Set back to false on player move.
	
	//Inventory information
	private ItemFilter itemFilter;
	private CosmeticTier rarityFilterTier;
	private ItemSort itemSort;

	private List<String> unlockedCosmetics = new ArrayList<String>();
	private List<String> activeCosmetics = new ArrayList<String>();
	private Set<DirtyDataType> dirtyData = new HashSet<DirtyDataType>();
	
	//Prefix timer
	PrefixColorTimer prefixTimer;
	
	
	public PlayerObject(UUID uuid, ItemFilter itemFilter, ItemSort itemSort, CosmeticTier rarityFilterTier, List<String> unlockedCosmetics) {
		this.uuid = uuid;
		this.itemFilter = itemFilter;
		this.itemSort = itemSort;
		this.rarityFilterTier = rarityFilterTier;
		this.unlockedCosmetics = unlockedCosmetics;
	}
	
	public PlayerObject(UUID uuid, ItemFilter itemFilter, ItemSort itemSort, CosmeticTier rarityFilterTier, List<String> unlockedCosmetics, List<String> activeCosmetics) {
		this.uuid = uuid;
		this.itemFilter = itemFilter;
		this.itemSort = itemSort;
		this.rarityFilterTier = rarityFilterTier;
		this.unlockedCosmetics = unlockedCosmetics;
		this.activeCosmetics = activeCosmetics;
		
		//Start prefix timer if one is active
		if(this.hasActiveCosmeticType(CosmeticType.PREFIX)) {
			prefixTimer = new PrefixColorTimer(uuid, (Prefix) getActiveCosmetic(CosmeticType.PREFIX));
			prefixTimer.scheduleTimer();

		}
	}
	
	
	public Set<DirtyDataType> getDirtyData(){
		return dirtyData;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public boolean isManuallyAFK() {
		return isAFK;
	}
	
	public void setAFK(boolean afk) {
		isAFK = afk;
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
	
	public CosmeticTier getRarityFilterTier() {
		return rarityFilterTier;
	}
	
	public ItemSort getItemSort() {
		return itemSort;
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
			if(plugin.getCosmetics().get(cos).getType().equals(type)) {
				if(type.equals(CosmeticType.PREFIX) && prefixTimer != null) return prefixTimer.getPrefix();
				else return plugin.getCosmetics().get(cos);
			}
		}
		return null;
	}
	
	/**
	 * Sets specified cosmetic as active. Disables other active cosmetics of the same type.
	 * @param cos
	 */
	public void setActiveCosmetic(Cosmetic cos) {
		if(hasActiveCosmeticType(cos.getType())) {
			removeActiveCosmetic(cos.getType());
			addActiveCosmetic(cos);
		}
		else addActiveCosmetic(cos);
	}
	
	public void removeActiveCosmetic(CosmeticType type) {
		//If removing a prefix - cancel color change runnable
		if(type.equals(CosmeticType.PREFIX) && prefixTimer != null) {
			prefixTimer.stopTimer();
			prefixTimer = null;
		}

		if(activeCosmetics.remove(getActiveCosmeticId(type))) {
			dirtyData.add(DirtyDataType.ACTIVE_COSMETICS);
		}
	}
	
	public void addActiveCosmetic(Cosmetic cos) {
		//If adding a prefix - set prefix timer
		if(cos.getType().equals(CosmeticType.PREFIX)) {
			if(prefixTimer != null) prefixTimer.stopTimer(); //Extra line to make sure timer is stopped
			prefixTimer = new PrefixColorTimer(getUUID(), (Prefix) cos);
			prefixTimer.scheduleTimer();
		}
		if(activeCosmetics.add(cos.getId())) {
			dirtyData.add(DirtyDataType.ACTIVE_COSMETICS);
		}
	}
	
	public boolean hasActiveCosmeticType(CosmeticType type) {
		for(String cos : activeCosmetics) {
			Cosmetic cosmetic = plugin.getCosmeticFromId(cos);
			if(cosmetic != null && cosmetic.getType().equals(type)) return true;
		}
		return false;
	}
	
	public boolean hasCosmeticUnlocked(Cosmetic cos) {
		if(unlockedCosmetics.contains(cos.getId())) return true;
		return false;
	}
	
	public boolean addUnlockedCosmetic(String id, boolean sendMessage) {
		if(unlockedCosmetics.contains(id)) return false;
		unlockedCosmetics.add(id);
		//Message player
		if(sendMessage) {
		Player player = Bukkit.getPlayer(uuid);
		Cosmetic cos = plugin.getCosmeticFromId(id);
		if(player != null) player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
				configMang.getMessageNoColor("scycos_receive_success").replace("{cosmetic}",
						cos.getType() + " " + cos.getDisplayItem().getItemMeta().getDisplayName())));
		}
		dirtyData.add(DirtyDataType.UNLOCKED_COSMETICS);
		return true;
	}
	
	public boolean removeUnlockedCosmetic(String id, boolean sendMessage) {
		if(!unlockedCosmetics.contains(id)) return false;
		unlockedCosmetics.remove(id);
		//If player has this cosmetic active, disable it.
		if(activeCosmetics.contains(id)) {
			Cosmetic cos = plugin.getCosmeticFromId(id);
			removeActiveCosmetic(cos.getType());
			if(sendMessage) {
				Player player = Bukkit.getPlayer(uuid);
				if(player != null) player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
						configMang.getMessageNoColor("scycos_active_taken").replace("{cosmetic}",
								cos.getType() + " " + cos.getDisplayItem().getItemMeta().getDisplayName())));
			}

		}
		
		dirtyData.add(DirtyDataType.UNLOCKED_COSMETICS);
		return true;
	}
	
	public void setItemFilter(ItemFilter filter) {
		this.itemFilter = filter;
		dirtyData.add(DirtyDataType.ITEM_FILTER);
	}
	
	public void toggleItemFilter() {
		itemFilter = itemFilter.next();
		dirtyData.add(DirtyDataType.ITEM_FILTER);
	}
	
	public void setItemSort(ItemSort itemSort) {
		this.itemSort = itemSort;
		dirtyData.add(DirtyDataType.ITEM_SORT);
	}
	
	public void toggleItemSort() {
		itemSort = itemSort.next();
		dirtyData.add(DirtyDataType.ITEM_SORT);
	}
	
	public void toggleRarityFilterTier() {
		rarityFilterTier = rarityFilterTier.next();
		dirtyData.add(DirtyDataType.RARITY_FILTER_TIER);
	}
	
}
