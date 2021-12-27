package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;
import xyz.scyllasrock.ScyCosmetics.util.InventoryUtils;
import xyz.scyllasrock.ScyCosmetics.util.ItemUtils;

public class CosInventoryListeners implements Listener {
	
	Main plugin = Main.getInstance();
	ConfigManager configMang = ConfigManager.getConfigMang();
	PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	final String baseInvPath = "inventory_settings.base_inventory";
	final String configTitle = ChatColor.stripColor(
			ChatColor.translateAlternateColorCodes(
					'&', configMang.getConfig().getString(baseInvPath + ".title")));
	final int arrowTrailSlot = configMang.getConfig().getInt(baseInvPath + ".items.arrow_trail.slot");
	final int lastWordsSlot = configMang.getConfig().getInt(baseInvPath + ".items.last_words.slot");
	final int playerTrailSlot = configMang.getConfig().getInt(baseInvPath + ".items.player_trail.slot");
	
	@EventHandler
	public void onBaseInvClick(InventoryClickEvent event) {
		
		//Clicked an item?
		if(event.getCurrentItem() == null) return;
		
		//Check if inventory is base inventory
		String currTitle = ChatColor.stripColor(event.getView().getTitle());
		if(!currTitle.equals(configTitle)) return;
		
		//It is the base inventory. Cancel click 
		event.setCancelled(true);
		
		//Check slot of clicked item and open new inventory
		Player player = (Player) event.getWhoClicked();
		player.openInventory(getCosmeticInventory(event.getSlot(), player));
		
	}
	
	@EventHandler
	public void onSpecificCosInvClick(InventoryClickEvent event) {
		
		//Clicked an item?
		if(event.getCurrentItem() == null) return;
		
		//Check if inventory is a cosmetic inventory
		String currTitle = ChatColor.stripColor(event.getView().getTitle());
		
		Player player = (Player) event.getWhoClicked();
		if(!currTitle.contains(player.getName() + "'s ")) return;
		if(!currTitle.contains("Arrow trails") && !currTitle.contains("Last words") && !currTitle.contains("Player trails")) return;
		
		//It is a cosmetic inventory - cancel click
		event.setCancelled(true);
		
		//If clicked a glass pane, return
		if(event.getCurrentItem().getType().toString().contains("PANE")) return;
		
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		//If clicking an informational item
			//Return home
		if(event.getSlot() == event.getInventory().getSize() - 5) {
			player.closeInventory();
			player.openInventory(InventoryUtils.getInventoryFromConfigPath(baseInvPath));
			return;
		}
			//Toggle viewing locked items - must reload inventory (changes size)
		else if(event.getSlot() == event.getInventory().getSize() - 9) {
			playerObject.setShowLockedCosmetics(!playerObject.showLockedCosmetics());
			String cosTypeString = event.getInventory().getItem(event.getInventory().getSize() - 2)
					.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING);
			CosmeticType cosType = CosmeticType.valueOf(cosTypeString);
			//reload inventory
			player.closeInventory();
			player.openInventory(this.getSpecificCosmeticInventory(player, cosType, cosType.label));
			return;
		}
		
			//Toggle filter method - will just reload for now
		else if(event.getSlot() == event.getInventory().getSize() - 1) {
			playerObject.toggleItemFilter();
			String cosTypeString = event.getInventory().getItem(event.getInventory().getSize() - 2)
					.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING);
			CosmeticType cosType = CosmeticType.valueOf(cosTypeString);
			//reload inventory
			player.closeInventory();
			player.openInventory(this.getSpecificCosmeticInventory(player, cosType, cosType.label));
			return;
		}
		
		//If clicking an item to change inventories
		else if(event.getSlot() < 9) {
			String cosTypeString = event.getInventory().getItem(event.getSlot())
					.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING);
			CosmeticType cosType = CosmeticType.valueOf(cosTypeString);
			//reload inventory
			player.closeInventory();
			player.openInventory(this.getSpecificCosmeticInventory(player, cosType, cosType.label));
			return;
		}
		
		//TODO: If clicking a page item
		
		//If clicking any other item in the bottom row
		else if(event.getSlot() >= event.getInventory().getSize() - 9) {
			return;
		}
		
		//If clicking a cosmetic
		else {
			//If clicking a redstone block
			if(event.getCurrentItem().getType().equals(Material.REDSTONE_BLOCK)) return;
			
		//On click, toggle the cosmetic as being active.
		boolean setNewGlowing = false;
		for(Cosmetic cos : plugin.getCosmetics().values()) {
			if(cos.getDisplayItem().getItemMeta().getDisplayName().equals(event.getCurrentItem().getItemMeta().getDisplayName())) {
				//If cos is same as current - disable
				if(playerObject.getActiveCosmeticId(cos.getType()) != null && 
						playerObject.getActiveCosmeticId(cos.getType()).equals(cos.getId()))
				playerObject.removeActiveCosmetic(cos.getType());
				
				//If cosmetic is different than current
				else {
					playerObject.setActiveCosmetic(cos);
					setNewGlowing = true;
				}
				
				break;
			}
		}

		Bukkit.getConsoleSender().sendMessage("Active Cos: " + playerObject.getActiveCosmetics()); //** TO BE REMOVED
		//Update glowing item
			//Remove all glowing
		for(ItemStack item : event.getClickedInventory().getContents()) {
			if(item == null) continue;
			for(Enchantment e : item.getEnchantments().keySet()) {
			    item.removeEnchantment(e);
			}
		}
			//Set current item glowing if new active was set
		if(setNewGlowing) {
		event.getCurrentItem().addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		ItemMeta meta = event.getCurrentItem().getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		event.getCurrentItem().setItemMeta(meta);
		}
		}
		
	}
	
	
	private Inventory getCosmeticInventory(int slot, Player player) {
		String title = "default";
		Inventory inv = Bukkit.createInventory(null, 27, title);
		if(slot == arrowTrailSlot) {
			inv = this.getSpecificCosmeticInventory(player, CosmeticType.ARROW_TRAIL, CosmeticType.ARROW_TRAIL.label);
		}
		else if(slot == lastWordsSlot) {
			inv = this.getSpecificCosmeticInventory(player, CosmeticType.LAST_WORDS, CosmeticType.LAST_WORDS.label);
		}
		else if(slot == playerTrailSlot) {
			inv = this.getSpecificCosmeticInventory(player, CosmeticType.PLAYER_TRAIL, CosmeticType.PLAYER_TRAIL.label);
		}
		

		return inv;		
	}
	
	//Sort options are rarity_ascending, rarity_descending, name (alphabetic), most_recent, least_recent
	
	//PLANS FOR FILTERING...
	//Step 1, get all itemStacks for the cosmetic type
	//Step 2, use itemstacks to find the size of inventory to be created
	//Step 3, take in a filtering argument and sort items (alphabetic by displayname, recently unlocked (front to back and back to front) - obtained from player file)
	//Step 4, create inventory
	private Inventory getSpecificCosmeticInventory(Player player, CosmeticType type, String cosmeticTitle) {
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		//Values to initialize
		ItemStack infoItem, showLockedItem, filterItem, currentCosItem;
		ItemStack pageItem1 = null;
		ItemStack pageItem2 = null;
		List<Cosmetic> sortedCosmetics = new ArrayList<Cosmetic>();

		//Initialize lockedItemStack - locked
		if(playerObject.showLockedCosmetics()) {
			showLockedItem = new ItemStack(Material.REDSTONE);
			ItemMeta lockedMeta = showLockedItem.getItemMeta();
			lockedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aHide &cLOCKED &aitems."));
			showLockedItem.setItemMeta(lockedMeta);
		}
	
		//Initialize lockedItemStack - unlocked
		else {
			showLockedItem = new ItemStack(Material.REDSTONE);
			ItemMeta lockedMeta = showLockedItem.getItemMeta();
			lockedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aShow &cLOCKED &aitems."));
			showLockedItem.setItemMeta(lockedMeta);			
		}
		
		//Initialize Info Item
		infoItem = new ItemStack(Material.BEACON);
		ItemMeta infoMeta = infoItem.getItemMeta();
		infoMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "Info-y info here"));
		infoItem.setItemMeta(infoMeta);
		
		//Initialize filterItem
		filterItem = new ItemStack(Material.GOLD_INGOT);
		ItemMeta filterMeta = filterItem.getItemMeta();
		filterMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eCurrent filter method: &6" + playerObject.getItemFilter().toString()));
		filterItem.setItemMeta(filterMeta);
		
		//Initialize currentCosItem
		String curCosMat = configMang.getConfig().getString(baseInvPath + ".items." + type.toString().toLowerCase() + ".material");
		
		if(curCosMat.startsWith("head;")) {
			currentCosItem = ItemUtils.getHead(curCosMat.split("head;")[1]);
		}
		else currentCosItem = new ItemStack(Material.valueOf(curCosMat.toUpperCase()));
		ItemMeta currCosMeta = currentCosItem.getItemMeta();
		currCosMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eCurrently viewing &6" + type.toString()));
		PersistentDataContainer data = currCosMeta.getPersistentDataContainer();
		data.set(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING, type.toString());
		currentCosItem.setItemMeta(currCosMeta);
		
		//Filter items based on player's filter specifications
		sortedCosmetics = this.sortCosmetics(playerObject, type);
		
		//Find size of inventory
		int size = ((int) Math.ceil(sortedCosmetics.size() / 7.0)) * 9 + 18; //4 rows of 7 cosmetics max.
		if(size > 54) {
			size = 54;
			//Initialize pageItem2
			pageItem2 = new ItemStack(Material.FEATHER);
			ItemMeta page2Meta = pageItem2.getItemMeta();
			page2Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fNext page"));
			pageItem2.setItemMeta(page2Meta);
		}
		
		//Create inventory
		Inventory inv = Bukkit.createInventory(null, size,
				ChatColor.translateAlternateColorCodes('&', player.getName() + "'s " + cosmeticTitle));
		
		//Get active cosmetic of type specified
		ItemStack activeItem = null;
		Cosmetic activeCos = playerObject.getActiveCosmetic(type);
		if(activeCos != null) activeItem = activeCos.getDisplayItem();
		
		//Add the first cosmetics, no more than 28. Locked cosmetics replaced with redstone block
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
			if(!playerObject.getUnlockedCosmetics().contains(cos.getId())) item.setType(Material.REDSTONE_BLOCK);
			inv.setItem(slot, item);
			if(slot % 9 == 7) slot += 3;
			else ++slot;
			
			++count;
		}
		
		//Add informational items
		inv.setItem(size - 9, showLockedItem);
		inv.setItem(size - 6, pageItem1);
		inv.setItem(size - 4, pageItem2);
		inv.setItem(size - 5, infoItem);
		inv.setItem(size - 1, filterItem);
		inv.setItem(size - 2, currentCosItem);
		inv.setItem(size - 8, currentCosItem);
		
		//Add glass panes on sides and bottom
		int row = 1;
		ItemStack pane = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
		ItemMeta paneMeta = pane.getItemMeta();
		paneMeta.setDisplayName(" ");
		pane.setItemMeta(paneMeta);
		
		while(row + 1 < size / 9) {
			inv.setItem(9 * row, pane);
			inv.setItem(9 * row + 8, pane);
			++row;
		}
		inv.setItem(size - 3, pane);
		inv.setItem(size - 7, pane);
		
		//Add cosmetic buttons on top row
		List<ItemStack> invSelItems = new ArrayList<ItemStack>();
		for(String key : configMang.getConfig().getConfigurationSection(baseInvPath + ".items").getKeys(false)) {
			if(key.equalsIgnoreCase("info")) continue;
			String materialString = configMang.getConfig().getString(baseInvPath + ".items." + key + ".material");
			
			ItemStack cosItem;
			if(materialString.startsWith("head;")) {
				cosItem = ItemUtils.getHead(materialString.split("head;")[1]);
			}
			else cosItem = new ItemStack(Material.valueOf(materialString.toUpperCase()));
			
			ItemMeta cosMeta = cosItem.getItemMeta();
			cosMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', 
					configMang.getConfig().getString(baseInvPath + ".items." + key + ".display_name")));
			PersistentDataContainer itemData = cosMeta.getPersistentDataContainer();
			itemData.set(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING, key.toUpperCase());
			currentCosItem.setItemMeta(currCosMeta);
			cosItem.setItemMeta(cosMeta);
			invSelItems.add(cosItem);
		}
		if(invSelItems.size() % 2 == 0) {
			//Set middle to black item pane
			inv.setItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
			//Place items on left side
			for(int i = 0; i < invSelItems.size() / 2; ++i) {
				inv.setItem(4 - invSelItems.size() / 2 + i, invSelItems.get(i));
			}
			//Place items on right side
			for(int i = 0; i < invSelItems.size() / 2; ++i) {
				inv.setItem(5 + i, invSelItems.get(i + invSelItems.size() / 2));
			}
		}
		else {
			//Place items centered
			for(int i = 0; i < invSelItems.size(); ++i) {
				inv.setItem(4 - invSelItems.size() / 2 + i, invSelItems.get(i));
			}
			
		//Add remaining panes to top row
		if(invSelItems.size() < 8) {
			for(int paneSlot = 0; paneSlot < 9; ++paneSlot) {
				inv.setItem(paneSlot, pane);
				if(paneSlot == 3 && invSelItems.size() == 1) {
					paneSlot += 1;
				}
				else if(paneSlot == 2 && invSelItems.size() > 1) {
					paneSlot += 3;
				}
				else if(paneSlot == 1 && invSelItems.size() > 3) {
					paneSlot += 5;
				}
				else if(paneSlot == 0 && invSelItems.size() > 5) {
					paneSlot += 7;
				}
			}
		}
		}

		return inv;
	}
	
	
	private List<Cosmetic> sortCosmetics(PlayerObject playerObject, CosmeticType type){
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
				sortedList = this.reverseArrayList(unsortedList);
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
				sortedList = this.reverseArrayList(unsortedList);
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
    private List<Cosmetic> reverseArrayList(List<Cosmetic> alist) {
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
