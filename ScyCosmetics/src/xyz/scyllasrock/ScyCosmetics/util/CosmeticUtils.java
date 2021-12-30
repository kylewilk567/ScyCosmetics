package xyz.scyllasrock.ScyCosmetics.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class CosmeticUtils {
	
	private static Main plugin = Main.getInstance();
	
	
	
	public static List<Cosmetic> sortCosmetics(PlayerObject playerObject, CosmeticType type){
		List<Cosmetic> sortedList = new ArrayList<Cosmetic>();
		switch(playerObject.getItemFilter()) {
		case RARITY_ASCENDING:
			//Locked items
			if(playerObject.showLockedCosmetics()) {
				//Get all locked and unlocked cosmetics for cosmetic type
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type)) sortedList.add(cos);
				}				
			}
			
			//Unlocked items
			else {
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) sortedList.add(cos);
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
			if(playerObject.showLockedCosmetics()) {
				//Get all locked and unlocked cosmetics for cosmetic type
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type)) sortedList.add(cos);
				}				
			}
			
			//Unlocked items
			else {
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) sortedList.add(cos);
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
			//If showing locked items...
			if(playerObject.showLockedCosmetics()) {
				//Add unlocked cosmetics
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) sortedList.add(cos);
				}
				List<Cosmetic> unsortedLocked = new ArrayList<Cosmetic>();
				//Add locked cosmetics
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type) && !sortedList.contains(cos)) unsortedLocked.add(cos);
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
			
			//If not showing locked items
			else {
				//Add unlocked cosmetics
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) sortedList.add(cos);
				}
			}
			break;
		case MOST_RECENT:
			//If showing locked items
			if(playerObject.showLockedCosmetics()) {
				List<Cosmetic> unsortedList = new ArrayList<Cosmetic>();
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) unsortedList.add(cos);
				}
				sortedList = reverseArrayList(unsortedList);
				//Add locked cosmetics
				List<Cosmetic> unsortedLocked = new ArrayList<Cosmetic>();
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type) && !sortedList.contains(cos)) unsortedLocked.add(cos);
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
			//If not showing locked items, sorted is unsorted in reverse
			else {
				//Add unlocked cosmetics in reverse order
				List<Cosmetic> unsortedList = new ArrayList<Cosmetic>();
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) unsortedList.add(cos);
				}
				sortedList = reverseArrayList(unsortedList);
			}
			break;
			
		default: //Filter by display name

			//Locked items
			if(playerObject.showLockedCosmetics()) {
				//Get all locked and unlocked cosmetics for cosmetic type
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getType().equals(type)) sortedList.add(cos);
				}				
			}
			
			//Unlocked items
			else {
				for(String id : playerObject.getUnlockedCosmetics()) {
					Cosmetic cos = plugin.getCosmeticFromId(id);
					if(cos != null && cos.getType().equals(type)) sortedList.add(cos);
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
