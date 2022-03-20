package xyz.scyllasrock.ScyCosmetics.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.CosInventoryListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticTier;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.ItemFilter;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;
import xyz.scyllasrock.ScyUtility.objects.Pair;

public class CosmeticUtils {
	
	private static Main plugin = Main.getInstance();
	
	
	
	public static List<Cosmetic> sortCosmetics(PlayerObject playerObject, CosmeticType type){
		List<Cosmetic> sortedList = new ArrayList<Cosmetic>();
		switch(playerObject.getItemSort()) {
		case RARITY_ASCENDING:
			//Locked items
			if(playerObject.getItemFilter().equals(ItemFilter.SHOW_ALL)) {
				//Get all locked and unlocked cosmetics for cosmetic type
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type)) sortedList.add(cos);
				}				
			}
			
			//Obtainable items
			else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_OBTAINABLE)) {
				sortedList = plugin.getCosmetics().values().stream().filter(cos ->
				cos.getType().equals(type) && !cos.isUnobtainable()).collect(Collectors.toList());
			}
			
			//Unlocked items
			else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_UNLOCKED)){
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) sortedList.add(cos);
				}
			}
			
			//Get Items by rarity
			else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_RARITY)) {
				//Get all locked and unlocked cosmetics for cosmetic type and tier
				CosmeticTier rarity = playerObject.getRarityFilterTier();
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type) && cos.getTier().equals(rarity)) sortedList.add(cos);
				}	
			}
			
			//Sort using rarity, then using alphabetical
			Collections.sort(sortedList, new Comparator<Cosmetic>() {
				  @Override
				  public int compare(Cosmetic c1, Cosmetic c2) {
					  int order = c1.getTier().ordinal() - c2.getTier().ordinal();
				    if(order != 0) return order;
				    return ChatColor.stripColor(c1.getDisplayItem().getItemMeta().getDisplayName())
				    		.compareTo(ChatColor.stripColor(c2.getDisplayItem().getItemMeta().getDisplayName()));
				  }
				});
			break;
		case RARITY_DESCENDING:
			//Locked items
			if(playerObject.getItemFilter().equals(ItemFilter.SHOW_ALL)) {
				//Get all locked and unlocked cosmetics for cosmetic type
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type)) sortedList.add(cos);
				}				
			}
			
			//Obtainable items
			else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_OBTAINABLE)) {
				sortedList = plugin.getCosmetics().values().stream().filter(cos ->
				cos.getType().equals(type) && !cos.isUnobtainable()).collect(Collectors.toList());
			}
			
			//Unlocked items
			else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_UNLOCKED)){
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) sortedList.add(cos);
				}
			}
			
			//Get Items by rarity
			else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_RARITY)) {
				//Get all locked and unlocked cosmetics for cosmetic type and tier
				CosmeticTier rarity = playerObject.getRarityFilterTier();
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type) && cos.getTier().equals(rarity)) sortedList.add(cos);
				}	
			}
			
			//Sort using display names
			Collections.sort(sortedList, new Comparator<Cosmetic>() {
				  @Override
				  public int compare(Cosmetic c1, Cosmetic c2) {
					  int order = c2.getTier().ordinal() - c1.getTier().ordinal();
				    if(order != 0) return order;
				    return ChatColor.stripColor(c1.getDisplayItem().getItemMeta().getDisplayName())
				    		.compareTo(ChatColor.stripColor(c2.getDisplayItem().getItemMeta().getDisplayName()));
				  }
				});
			break;
		case LEAST_RECENT:
			//Add unlocked cosmetics
			
			//... for rarity
			if(playerObject.getItemFilter().equals(ItemFilter.SHOW_RARITY)) {
				//Get all locked and unlocked cosmetics for cosmetic type and tier
				CosmeticTier rarity = playerObject.getRarityFilterTier();
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type) && cos.getTier().equals(rarity)) sortedList.add(cos);
				}	
			}
			
			// for others
			else {
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) sortedList.add(cos);
				}
			}
			
			//If showing locked items...
			if(playerObject.getItemFilter().equals(ItemFilter.SHOW_ALL)
					|| playerObject.getItemFilter().equals(ItemFilter.SHOW_OBTAINABLE)
					|| playerObject.getItemFilter().equals(ItemFilter.SHOW_RARITY)) {
				List<Cosmetic> unsortedLocked = new ArrayList<Cosmetic>();
				if(playerObject.getItemFilter().equals(ItemFilter.SHOW_ALL)) {
				//Add locked cosmetics
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type) && !sortedList.contains(cos)) unsortedLocked.add(cos);
				}
				}
				else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_OBTAINABLE)) {
					for(Cosmetic cos : plugin.getCosmetics().values()) {
						if(cos.getType().equals(type) && !cos.isUnobtainable() && !sortedList.contains(cos)) unsortedLocked.add(cos);
					}
				}
				else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_RARITY)) {
					//Get all locked and unlocked cosmetics for cosmetic type and tier
					CosmeticTier rarity = playerObject.getRarityFilterTier();
					for(Cosmetic cos : plugin.getCosmetics().values()) {
						if(cos.getType().equals(type) && cos.getTier().equals(rarity) && !sortedList.contains(cos)) sortedList.add(cos);
					}	
				}
				//Sort locked cosmetics alphabetically
				Collections.sort(unsortedLocked, new Comparator<Cosmetic>() {
					  @Override
					  public int compare(Cosmetic c1, Cosmetic c2) {
					    return ChatColor.stripColor(c1.getDisplayItem().getItemMeta().getDisplayName())
					    		.compareTo(ChatColor.stripColor(c2.getDisplayItem().getItemMeta().getDisplayName()));
					  }
					});
				//Add locked cosmetics to sorted list
				sortedList.addAll(unsortedLocked);
			}
			
			break;
		case MOST_RECENT:
			
			//Add unlocked cosmetics in reverse order
			List<Cosmetic> unsortedList = new ArrayList<Cosmetic>();
			
			//... for rarity
			if(playerObject.getItemFilter().equals(ItemFilter.SHOW_RARITY)) {
				//Get all locked and unlocked cosmetics for cosmetic type and tier
				CosmeticTier rarity = playerObject.getRarityFilterTier();
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type) && cos.getTier().equals(rarity)) unsortedList.add(cos);
				}	
			}
			
			// for others
			else {
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) unsortedList.add(cos);
				}
			}
			
			sortedList = reverseArrayList(unsortedList);
			
			//If showing all locked items
			if(playerObject.getItemFilter().equals(ItemFilter.SHOW_ALL)
					|| playerObject.getItemFilter().equals(ItemFilter.SHOW_OBTAINABLE)
					|| playerObject.getItemFilter().equals(ItemFilter.SHOW_RARITY)) {
				List<Cosmetic> unsortedLocked = new ArrayList<Cosmetic>();
				if(playerObject.getItemFilter().equals(ItemFilter.SHOW_ALL)) {
				//Add locked cosmetics
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type) && !sortedList.contains(cos)) unsortedLocked.add(cos);
				}
				}
				else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_OBTAINABLE)) {
					for(Cosmetic cos : plugin.getCosmetics().values()) {
						if(cos.getType().equals(type) && !cos.isUnobtainable() && !sortedList.contains(cos)) unsortedLocked.add(cos);
					}
				}
				else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_RARITY)) {
					//Get all locked and unlocked cosmetics for cosmetic type and tier
					CosmeticTier rarity = playerObject.getRarityFilterTier();
					for(Cosmetic cos : plugin.getCosmetics().values()) {
						if(cos.getType().equals(type) && cos.getTier().equals(rarity) && !sortedList.contains(cos)) unsortedLocked.add(cos);
					}	
				}
				
				//Sort locked cosmetics alphabetically
				Collections.sort(unsortedLocked, new Comparator<Cosmetic>() {
					  @Override
					  public int compare(Cosmetic c1, Cosmetic c2) {
					    return ChatColor.stripColor(c1.getDisplayItem().getItemMeta().getDisplayName())
					    		.compareTo(ChatColor.stripColor(c2.getDisplayItem().getItemMeta().getDisplayName()));
					  }
					});
				//Add locked cosmetics to sorted list
				sortedList.addAll(unsortedLocked);
			}
			
			break;
			
		default: //Filter by display name

			//Locked items
			if(playerObject.getItemFilter().equals(ItemFilter.SHOW_ALL)) {
				//Get all locked and unlocked cosmetics for cosmetic type
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type)) sortedList.add(cos);
				}				
			}
			
			//Obtainable items
			else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_OBTAINABLE)) {
				sortedList = plugin.getCosmetics().values().stream().filter(cos ->
				cos.getType().equals(type) && !cos.isUnobtainable()).collect(Collectors.toList());
			}
			
			//Unlocked items
			else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_UNLOCKED)){
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) sortedList.add(cos);
				}
			}
			
			//Get items by rarity
			else if(playerObject.getItemFilter().equals(ItemFilter.SHOW_RARITY)) {
				//Get all locked and unlocked cosmetics for cosmetic type and tier
				CosmeticTier rarity = playerObject.getRarityFilterTier();
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type) && cos.getTier().equals(rarity)) sortedList.add(cos);
				}
			}
			
			//Sort using display names
			Collections.sort(sortedList, new Comparator<Cosmetic>() {
				  @Override
				  public int compare(Cosmetic c1, Cosmetic c2) {
				    return ChatColor.stripColor(c1.getDisplayItem().getItemMeta().getDisplayName())
				    		.compareTo(ChatColor.stripColor(c2.getDisplayItem().getItemMeta().getDisplayName()));
				  }
				});
			break;
		}
		//Save sorted list to uuid
		if(!CosInventoryListeners.getSavedSortedCosmetics().containsKey(playerObject.getUUID()))
			CosInventoryListeners.getSavedSortedCosmetics().put(playerObject.getUUID(), new Pair<List<Cosmetic>, Integer>(sortedList, 1));
		else {
			int currPage = CosInventoryListeners.getSavedSortedCosmetics().get(playerObject.getUUID()).getSecond();
			CosInventoryListeners.getSavedSortedCosmetics().put(playerObject.getUUID(), new Pair<List<Cosmetic>, Integer>(sortedList, currPage));
		}
		
		return sortedList;
	}
	
	
    // Takes an arraylist as a parameter and returns
    // a reversed arraylist
    private static List<Cosmetic> reverseArrayList(List<Cosmetic> alist) {
        // Arraylist for storing reversed elements
        // this.revArrayList = alist;
        for (int i = 0; i < alist.size() / 2; i++) {
            Cosmetic temp = alist.get(i);
            alist.set(i, alist.get(alist.size() - i - 1));
            alist.set(alist.size() - i - 1, temp);
        }
 
        // Return the reversed arraylist
        return alist;
    }

}
