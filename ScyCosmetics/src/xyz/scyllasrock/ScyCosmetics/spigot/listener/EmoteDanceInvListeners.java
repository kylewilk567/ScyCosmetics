package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;
import xyz.scyllasrock.ScyCosmetics.util.CosmeticUtils;
import xyz.scyllasrock.ScyCosmetics.util.ItemUtils;
import xyz.scyllasrock.ScyUtility.objects.Pair;

public class EmoteDanceInvListeners implements Listener {
	
	private static Main plugin = Main.getInstance();
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	
	private static Map<UUID, Pair<Long, String>> purchaseClickCooldown = new HashMap<UUID, Pair<Long, String>>();
	
	@EventHandler
	public void onInvClick(InventoryClickEvent event) {
		
		//Clicked an item?
		if(event.getCurrentItem() == null) return;
		
		//Check if inventory is the dance inventory
		String currTitle = ChatColor.stripColor(event.getView().getTitle());
		
		Player player = (Player) event.getWhoClicked();
		if(!currTitle.contains("Emote Dances")) return;
		
		//It is the dance inventory - cancel click
		event.setCancelled(true);
		
		//If clicked a glass pane, return
		if(event.getCurrentItem().getType().toString().contains("PANE")) return;
		
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		

		//Toggle FILTER method - does not close inventory
	    if(event.getSlot() == event.getInventory().getSize() - 9) {
	    	playerObject.toggleItemFilter();
		
	    	//Resort cosmetics
	    	CosmeticUtils.sortCosmetics(playerObject, CosmeticType.EMOTE_DANCE);
	    	//Go back to page 1! (since size of sortedCosmetics has changed)
	    	this.updateCosmeticsForPage(playerObject, event.getClickedInventory(), CosmeticType.EMOTE_DANCE, 1);
		
	    	//Update filter item
	    	ItemStack lockedItem = event.getCurrentItem();
	    	ItemMeta lockedMeta = lockedItem.getItemMeta();
	    	lockedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eCurrent filter method: &6" + playerObject.getItemFilter().toString()));
	    	List<String> lockedLore = new ArrayList<String>();
	    	lockedLore.add(ChatColor.translateAlternateColorCodes('&', "&5Click to toggle method"));
	    	lockedMeta.setLore(lockedLore);
	    	lockedItem.setItemMeta(lockedMeta);
	    	event.getClickedInventory().setItem(event.getClickedInventory().getSize() - 9, lockedItem);
	    	return;
	    }
		
			//Toggle SORT method - will just reload for now
		else if(event.getSlot() == event.getInventory().getSize() - 1) {
			playerObject.toggleItemSort();
			//Resort cosmetics
			List<Cosmetic> sortedCosmetics = CosmeticUtils.sortCosmetics(playerObject, CosmeticType.EMOTE_DANCE);
			
			ItemStack activeItem = null;
			Cosmetic activeCos = playerObject.getActiveCosmetic(CosmeticType.EMOTE_DANCE);
			if(activeCos != null) activeItem = activeCos.getDisplayItem();
			
				//Replace the cosmetic items
			int count = 0;
			int slot = 10;
			for(Cosmetic cos : sortedCosmetics) {
				if(count > 28) break;
				//Glow if active item
				ItemStack item = cos.getDisplayItem().clone();
				if(ItemUtils.itemEquals(item, activeItem)){
					item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
					ItemMeta meta = item.getItemMeta();
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
				}
				if(!playerObject.getUnlockedCosmetics().contains(cos.getId())) {
					item.setType(Material.REDSTONE_BLOCK);
					//Append buyprice - if purchaseable
					if(cos.isPurchaseable()) {
					ItemMeta meta = item.getItemMeta();
					List<String> lore = new ArrayList<String>();
					if(meta.hasLore()) lore = meta.getLore();
					lore.add(ChatColor.translateAlternateColorCodes('&', "&ePrice: &c" + cos.getBuyPrice()));
					lore.add(ChatColor.translateAlternateColorCodes('&', "&5&0Double shift-right-click to purchase"));
					meta.setLore(lore);
					item.setItemMeta(meta);
					}
				}
				event.getClickedInventory().setItem(slot, item);
				if(slot % 9 == 7) slot += 3;
				else ++slot;
				
				++count;
			}
			
				//Update sort item 
			ItemStack filterItem = new ItemStack(Material.GOLD_INGOT);
			ItemMeta filterMeta = filterItem.getItemMeta();
			filterMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eCurrent sort method: &6" + playerObject.getItemSort().toString()));
			List<String> filterLore = new ArrayList<String>();
			filterLore.add(ChatColor.translateAlternateColorCodes('&', "&5Click to toggle method"));
			filterMeta.setLore(filterLore);
			filterItem.setItemMeta(filterMeta);
			event.getClickedInventory().setItem(event.getClickedInventory().getSize() - 1, filterItem);
			return;
		}
	    
	    //If clicking cosmetic
		if(event.getCurrentItem().getType().equals(Material.REDSTONE_BLOCK)) {
		if(event.getClick().equals(ClickType.SHIFT_RIGHT)) {
			//If first click - add to map
			if(!purchaseClickCooldown.containsKey(player.getUniqueId())) {
				purchaseClickCooldown.put(player.getUniqueId(),
						new Pair<Long, String>(System.currentTimeMillis() + 500, event.getCurrentItem().getItemMeta().getPersistentDataContainer()
								.get(new NamespacedKey(plugin, "ScyCos_id"), PersistentDataType.STRING)));
			}
			else if(purchaseClickCooldown.get(player.getUniqueId()).getFirst() < System.currentTimeMillis()
					|| !purchaseClickCooldown.get(player.getUniqueId()).getSecond()
					.equals(event.getCurrentItem().getItemMeta()
							.getPersistentDataContainer().get(new NamespacedKey(plugin, "ScyCos_id"), PersistentDataType.STRING))) {
				purchaseClickCooldown.put(player.getUniqueId(),
						new Pair<Long, String>(System.currentTimeMillis() + 500, event.getCurrentItem().getItemMeta().getPersistentDataContainer()
								.get(new NamespacedKey(plugin, "ScyCos_id"), PersistentDataType.STRING)));
			}
			
			//If second click - purchase if purchaseable
			else {
				String id = purchaseClickCooldown.get(player.getUniqueId()).getSecond();
				Cosmetic cos = plugin.getCosmeticFromId(id);
				if(!cos.isPurchaseable()) return;
				//Check money
				if(plugin.getVaultEco().getBalance(player) < cos.getBuyPrice()) {
					player.closeInventory();
					player.sendMessage(configMang.getMessage("error_insufficient_funds"));
				}
					
				else {
				//Take money
				plugin.getVaultEco().withdrawPlayer(player, cos.getBuyPrice());
				
				//Unlock cosmetic (and send message)
				playerObject.addUnlockedCosmetic(id, true);
				
				//Update itemstack
				event.getClickedInventory().setItem(event.getSlot(), plugin.getCosmeticFromId(id).getDisplayItem().clone());
				
				//Remove from map
				purchaseClickCooldown.remove(player.getUniqueId());
				
				}
			}
		}
		return;
	}
		
	}
	
	
	
	private List<Cosmetic> getCosmeticsForPage(List<Cosmetic> sortedCosmetics, int page){
		List<Cosmetic> pageCosmetics = new ArrayList<Cosmetic>();
		//Check if there are cosmetics on that page
		if(sortedCosmetics.size() >= (page - 1) * 28) {
			//Add correct indices to list
			for(int index = (page - 1) * 28; index < sortedCosmetics.size(); ++index) {
				pageCosmetics.add(sortedCosmetics.get(index));
			}
		}
		
		return pageCosmetics;
	}
	
	private void updateCosmeticsForPage(PlayerObject playerObject, Inventory inv, CosmeticType cosType, int newPage) {
		//Get cosmetics on this page (can be empty)
		List<Cosmetic> pageCos = getCosmeticsForPage(CosInventoryListeners.getSavedSortedCosmetics().get(playerObject.getUUID()).getFirst(), newPage);
		ItemStack activeItem = null;
		Cosmetic activeCos = playerObject.getActiveCosmetic(cosType);
		if(activeCos != null) activeItem = activeCos.getDisplayItem();
		int slot = 10;
		for(int count = 0; count < 28; ++count) {
			Cosmetic cos = null;
			if(count < pageCos.size()) cos = pageCos.get(count);
			ItemStack item = null;
			if(cos != null) item = cos.getDisplayItem().clone();
			//Glow if active item
			if(ItemUtils.itemEquals(item, activeItem)){
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				ItemMeta meta = item.getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			}
			if(cos != null && item != null) {
			if(!playerObject.getUnlockedCosmetics().contains(cos.getId())) item.setType(Material.REDSTONE_BLOCK);
			}
			inv.setItem(slot, item);
			if(slot % 9 == 7) slot += 3;
			else ++slot;
		}

		
		//Update page items
		//Set previous page item
		int sortedSize = CosInventoryListeners.getSavedSortedCosmetics().get(playerObject.getUUID()).getFirst().size();
		if(newPage > 1) {
			ItemStack pageItem1 = new ItemStack(Material.FEATHER);
			ItemMeta page1Meta = pageItem1.getItemMeta();
			page1Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fPrevious page"));
			pageItem1.setItemMeta(page1Meta);
			inv.setItem(inv.getSize() - 6, pageItem1);
		}
		else {
			inv.setItem(inv.getSize() - 6, null);
		}

		//Update next page item
		if(sortedSize > newPage * 24) {
			ItemStack pageItem2 = new ItemStack(Material.FEATHER);
			ItemMeta page2Meta = pageItem2.getItemMeta();
			page2Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fNext page"));
			pageItem2.setItemMeta(page2Meta);
			inv.setItem(inv.getSize() - 4, pageItem2);
		}
		else {
			inv.setItem(inv.getSize() - 4, null);
		}

	
		//update page number
		CosInventoryListeners.getSavedSortedCosmetics().put(playerObject.getUUID(),
				new Pair<List<Cosmetic>, Integer>(CosInventoryListeners.getSavedSortedCosmetics().get(playerObject.getUUID()).getFirst(), newPage));
	}

}
