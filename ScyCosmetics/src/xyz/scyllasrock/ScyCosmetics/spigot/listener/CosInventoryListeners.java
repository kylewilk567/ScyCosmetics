package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
import xyz.scyllasrock.ScyCosmetics.util.CosmeticUtils;
import xyz.scyllasrock.ScyCosmetics.util.InventoryUtils;
import xyz.scyllasrock.ScyCosmetics.util.ItemUtils;
import xyz.scyllasrock.ScyUtility.objects.Pair;

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
	final int emoteEquipmentSlot = configMang.getConfig().getInt(baseInvPath + ".items.emote_equipment.slot");
	final int prefixSlot = configMang.getConfig().getInt(baseInvPath + ".items.prefix.slot");
	final int killEffectSlot = configMang.getConfig().getInt(baseInvPath + ".items.kill_effect.slot");
	final int afkEffectSlot = configMang.getConfig().getInt(baseInvPath + ".items.afk_effect.slot");
	final int logMessageSlot = configMang.getConfig().getInt(baseInvPath + ".items.log_message.slot");
	final int titleSlot = configMang.getConfig().getInt(baseInvPath + ".items.title.slot");
	final int infoItemSlot = configMang.getConfig().getInt(baseInvPath + ".items.info.slot");
	
	private static Map<UUID, Pair<List<Cosmetic>, Integer>> savedSortedCosmetics = new HashMap<UUID, Pair<List<Cosmetic>, Integer>>();
	private static Map<UUID, Pair<Long, String>> purchaseClickCooldown = new HashMap<UUID, Pair<Long, String>>();
	
	@EventHandler
	public void onBaseInvClick(InventoryClickEvent event) {
		
		//Clicked an item?
		if(event.getCurrentItem() == null) return;
		
		//Check if inventory is base inventory
		String currTitle = ChatColor.stripColor(event.getView().getTitle());
		if(!currTitle.equals(configTitle)) return;
		
		//It is the base inventory. Cancel click 
		event.setCancelled(true);
		
		//Check slot of clicked item and open new inventory if it's not the info item or a glass pane
		Player player = (Player) event.getWhoClicked();
		if(event.getSlot() != infoItemSlot && !event.getCurrentItem().getType().toString().contains("PANE")) {
			
		//Remove cosmetic if one is active
		if(event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT)) {
			if(event.getCurrentItem().hasItemMeta()) {
				ItemMeta meta = event.getCurrentItem().getItemMeta();
				if(event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(
						new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING)) {
					
					String cosTypeStr = event.getCurrentItem().getItemMeta().getPersistentDataContainer()
							.get(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING);
					//Check if one is active
					if(!playerHandler.getPlayerObjectByUUID(player.getUniqueId()).hasActiveCosmeticType(CosmeticType.valueOf(cosTypeStr))) return;
					
					//Remove cosmetic
					playerHandler.getPlayerObjectByUUID(player.getUniqueId()).removeActiveCosmetic(CosmeticType.valueOf(cosTypeStr));
									
					//Remove last line of lore
					List<String> lore = event.getCurrentItem().getItemMeta().getLore();
					if(lore.size() > 1) {
						lore.remove(lore.size() - 1); //Remove last two lines
						lore.remove(lore.size() - 1);
					}
					meta.setLore(lore);
					event.getCurrentItem().setItemMeta(meta);
					
					//Remove enchants from item (remove glow)
					for(Enchantment e : event.getCurrentItem().getEnchantments().keySet()) {
						event.getCurrentItem().removeEnchantment(e);
					}
					
				}

				
			}
			
			return;
		}
		
		else {
			Pair<Inventory, List<Cosmetic>> invCosPair = getCosmeticInventory(event.getSlot(), player);
		player.openInventory(invCosPair.getFirst());
		savedSortedCosmetics.put(player.getUniqueId(), new Pair<List<Cosmetic>, Integer>(invCosPair.getSecond(), 1));
		}
		}
		
		
	}
	
	@EventHandler
	public void onSpecificCosInvClick(InventoryClickEvent event) {
		
		//Clicked an item?
		if(event.getCurrentItem() == null) return;
		
		//Check if inventory is a cosmetic inventory
		String currTitle = ChatColor.stripColor(event.getView().getTitle());
		
		Player player = (Player) event.getWhoClicked();
		if(!currTitle.contains(player.getName() + "'s ")) return;
		if(!currTitle.contains("Arrow trails") && !currTitle.contains("Last words")
				&& !currTitle.contains("Player trails") && !currTitle.contains("Prefixes")
				&& !currTitle.contains("Log Messages") && !currTitle.contains("Emote Equipment")
				&& !currTitle.contains("Titles") && !currTitle.contains("Kill Effects")
				&& !currTitle.contains("AFK Effects")) return;
		
		//It is a cosmetic inventory - cancel click
		event.setCancelled(true);
		
		//If clicked a glass pane, return
		if(event.getCurrentItem().getType().toString().contains("PANE")) return;
		
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		//If clicking an informational item
			//Return home
		if(event.getSlot() == event.getInventory().getSize() - 5) {
			player.closeInventory();
			player.openInventory(InventoryUtils.getBaseCosInventory(playerObject));
			return;
		}
			//Toggle viewing locked items - does not close inventory
		else if(event.getSlot() == event.getInventory().getSize() - 9) {
			playerObject.setShowLockedCosmetics(!playerObject.showLockedCosmetics());
			String cosTypeString = event.getInventory().getItem(event.getInventory().getSize() - 2)
					.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING);
			CosmeticType cosType = CosmeticType.valueOf(cosTypeString);
			//Resort cosmetics
			CosmeticUtils.sortCosmetics(playerObject, cosType);
			//Go back to page 1! (since size of sortedCosmetics has changed)
			this.updateCosmeticsForPage(playerObject, event.getClickedInventory(), cosType, 1);
			
			//Update show locked item
			ItemStack showLockedItem = event.getCurrentItem();
			ItemMeta lockedMeta = showLockedItem.getItemMeta();
			if(playerObject.showLockedCosmetics()) {
				lockedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aHide &cLOCKED &aitems."));
			}
			else {
				lockedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aShow &cLOCKED &aitems."));	
			}
			
			showLockedItem.setItemMeta(lockedMeta);	
			event.getClickedInventory().setItem(event.getClickedInventory().getSize() - 9, showLockedItem);
			return;
		}
		
			//Toggle filter method - does not close inventory
		else if(event.getSlot() == event.getInventory().getSize() - 1) {
			playerObject.toggleItemFilter();
			String cosTypeString = event.getInventory().getItem(event.getInventory().getSize() - 2)
					.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING);
			CosmeticType cosType = CosmeticType.valueOf(cosTypeString);
				//Resort cosmetics
			CosmeticUtils.sortCosmetics(playerObject, cosType);
			
			this.updateCosmeticsForPage(playerObject, event.getClickedInventory(), cosType, savedSortedCosmetics.get(playerObject.getUUID()).getSecond());
			
				//Update filter item 
			ItemStack filterItem = new ItemStack(Material.GOLD_INGOT);
			ItemMeta filterMeta = filterItem.getItemMeta();
			filterMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eCurrent filter method: &6" + playerObject.getItemFilter().toString()));
			List<String> filterLore = new ArrayList<String>();
			filterLore.add(ChatColor.translateAlternateColorCodes('&', "&5Click to toggle method"));
			filterMeta.setLore(filterLore);
			filterItem.setItemMeta(filterMeta);
			event.getClickedInventory().setItem(event.getClickedInventory().getSize() - 1, filterItem);
			return;
		}
		
		//If clicking an item to change inventories
		else if(event.getSlot() < 9) {
			String cosTypeString = event.getInventory().getItem(event.getSlot())
					.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING);
			CosmeticType cosType = CosmeticType.valueOf(cosTypeString);
			//reload inventory
			player.closeInventory();
			Pair<Inventory, List<Cosmetic>> invCosPair = getSpecificCosmeticInventory(player, cosType, cosType.label);
			player.openInventory(invCosPair.getFirst());
			savedSortedCosmetics.put(player.getUniqueId(), new Pair<List<Cosmetic>, Integer>(invCosPair.getSecond(), 1));
			return;
		}
		
		//If clicking a page item
		if(event.getSlot() == event.getInventory().getSize() - 4 || event.getSlot() == event.getInventory().getSize() - 6) {
			String cosTypeString = event.getInventory().getItem(event.getInventory().getSize() - 2)
					.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING);
			CosmeticType cosType = CosmeticType.valueOf(cosTypeString);
			//Next page
			if(event.getSlot() == event.getInventory().getSize() - 4) {
				this.updateCosmeticsForPage(playerObject, event.getClickedInventory(),
						cosType, savedSortedCosmetics.get(player.getUniqueId()).getSecond() + 1);

			}
			//Previous page
			else {
				this.updateCosmeticsForPage(playerObject, event.getClickedInventory(),
						cosType, savedSortedCosmetics.get(player.getUniqueId()).getSecond() - 1);
			}
			return;
		}
		
		//If clicking any other item in the bottom row
		else if(event.getSlot() >= event.getInventory().getSize() - 9) {
			return;
		}
		
		//If clicking a cosmetic
		else {
			//If clicking a redstone block - buying an item
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
					
					//If second click - purchase
					else {
						String id = purchaseClickCooldown.get(player.getUniqueId()).getSecond();
						Cosmetic cos = plugin.getCosmeticFromId(id);
						//Check money
						if(plugin.getVaultEco().getBalance(player) < cos.getBuyPrice()) {
							player.closeInventory();
							player.sendMessage(configMang.getMessage("error_insufficient_funds"));
						}
							
						else {
						//Take money
						plugin.getVaultEco().withdrawPlayer(player, cos.getBuyPrice());
						
						//Unlock cosmetic
						playerObject.addUnlockedCosmetic(id);
						
						//Update itemstack
						event.getClickedInventory().setItem(event.getSlot(), plugin.getCosmeticFromId(id).getDisplayItem().clone());
						
						//Remove from map
						purchaseClickCooldown.remove(player.getUniqueId());
						
						//Send message
						player.sendMessage(ChatColor.translateAlternateColorCodes('&',
								configMang.getMessageNoColor("purchase_successful").replace("{cosmetic}", cos.getDisplayItem().getItemMeta().getDisplayName())));
						}
					}
				}
				return;
			}
			
		//On click, toggle the cosmetic as being active.
		boolean setNewGlowing = false;
		String cosTypeString = event.getInventory().getItem(event.getInventory().getSize() - 2)
				.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING);
		CosmeticType cosType = CosmeticType.valueOf(cosTypeString);
		for(Cosmetic cos : plugin.getCosmetics().values()) {
			if(!cos.getType().equals(cosType)) continue;
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
	
	/*
	 * Remove sorted cosmetics from map to save memory
	 */
	@EventHandler
	public void onInvClose(InventoryCloseEvent event) {
		savedSortedCosmetics.remove(event.getPlayer().getUniqueId());
	}
	
	
	private Pair<Inventory, List<Cosmetic>> getCosmeticInventory(int slot, Player player) {
		if(slot == arrowTrailSlot) {
			return this.getSpecificCosmeticInventory(player, CosmeticType.ARROW_TRAIL, CosmeticType.ARROW_TRAIL.label);
		}
		else if(slot == lastWordsSlot) {
			return this.getSpecificCosmeticInventory(player, CosmeticType.LAST_WORDS, CosmeticType.LAST_WORDS.label);
		}
		else if(slot == playerTrailSlot) {
			return this.getSpecificCosmeticInventory(player, CosmeticType.PLAYER_TRAIL, CosmeticType.PLAYER_TRAIL.label);
		}
		else if(slot == prefixSlot) {
			return this.getSpecificCosmeticInventory(player, CosmeticType.PREFIX, CosmeticType.PREFIX.label);
		}
		else if(slot == logMessageSlot) {
			return this.getSpecificCosmeticInventory(player, CosmeticType.LOG_MESSAGE, CosmeticType.LOG_MESSAGE.label);
		}
		else if(slot == emoteEquipmentSlot) {
			return this.getSpecificCosmeticInventory(player, CosmeticType.EMOTE_EQUIPMENT, CosmeticType.EMOTE_EQUIPMENT.label);
		}
		else if(slot == titleSlot) {
			return this.getSpecificCosmeticInventory(player, CosmeticType.TITLE, CosmeticType.TITLE.label);
		}
		else if(slot == killEffectSlot) {
			return this.getSpecificCosmeticInventory(player, CosmeticType.KILL_EFFECT, CosmeticType.KILL_EFFECT.label);
		}
		else if(slot == afkEffectSlot) {
			return this.getSpecificCosmeticInventory(player, CosmeticType.AFK_EFFECT, CosmeticType.AFK_EFFECT.label);
		}
		

		return null;		
	}
	
	//Sort options are rarity_ascending, rarity_descending, name (alphabetic), most_recent, least_recent
	
	//Steps FOR FILTERING...
	//Step 1, get all itemStacks for the cosmetic type
	//Step 2, use itemstacks to find the size of inventory to be created
	//Step 3, take in a filtering argument and sort items (alphabetic by displayname, recently unlocked (front to back and back to front) - obtained from player file)
	//Step 4, create inventory
	private Pair<Inventory, List<Cosmetic>> getSpecificCosmeticInventory(Player player, CosmeticType type, String cosmeticTitle) {
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		//Values to initialize
		ItemStack infoItem, showLockedItem, filterItem, currentCosItem;
		ItemStack pageItem2 = null;
		List<Cosmetic> sortedCosmetics = new ArrayList<Cosmetic>();

		//Initialize lockedItemStack
		showLockedItem = new ItemStack(Material.REDSTONE);
		ItemMeta lockedMeta = showLockedItem.getItemMeta();
		if(playerObject.showLockedCosmetics()) {
			lockedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aHide &cLOCKED &aitems."));
		}
		else {
			lockedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aShow &cLOCKED &aitems."));	
		}
		
		showLockedItem.setItemMeta(lockedMeta);	
		
		//Initialize Info Item
		infoItem = new ItemStack(Material.BEACON);
		ItemMeta infoMeta = infoItem.getItemMeta();
		infoMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bClick to go back"));
		infoItem.setItemMeta(infoMeta);
		
		//Initialize filterItem
		filterItem = new ItemStack(Material.GOLD_INGOT);
		ItemMeta filterMeta = filterItem.getItemMeta();
		filterMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eCurrent filter method: &6" + playerObject.getItemFilter().toString()));
		List<String> filterLore = new ArrayList<String>();
		filterLore.add(ChatColor.translateAlternateColorCodes('&', "&5Click to toggle method"));
		filterMeta.setLore(filterLore);
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
		sortedCosmetics = CosmeticUtils.sortCosmetics(playerObject, type);
		
		//Find size of inventory
		int size = 54; //4 rows of 7 cosmetics max.
		if(sortedCosmetics.size() > 28) {
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
			if(!playerObject.getUnlockedCosmetics().contains(cos.getId())) {
				item.setType(Material.REDSTONE_BLOCK);
				//Append buyprice
				ItemMeta meta = item.getItemMeta();
				List<String> lore = new ArrayList<String>();
				if(meta.hasLore()) lore = meta.getLore();
				lore.add(ChatColor.translateAlternateColorCodes('&', "&ePrice: &c" + cos.getBuyPrice()));
				lore.add(ChatColor.translateAlternateColorCodes('&', "&5&oDouble shift-right-click to purchase"));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			inv.setItem(slot, item);
			if(slot % 9 == 7) slot += 3;
			else ++slot;
			
			++count;
		}
		
		//Add informational items
		inv.setItem(size - 9, showLockedItem);
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

		return new Pair<Inventory, List<Cosmetic>>(inv, sortedCosmetics);
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
		List<Cosmetic> pageCos = getCosmeticsForPage(savedSortedCosmetics.get(playerObject.getUUID()).getFirst(), newPage);
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
		int sortedSize = savedSortedCosmetics.get(playerObject.getUUID()).getFirst().size();
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
		savedSortedCosmetics.put(playerObject.getUUID(), new Pair<List<Cosmetic>, Integer>(savedSortedCosmetics.get(playerObject.getUUID()).getFirst(), newPage));
	}
	
	public static Map<UUID, Pair<List<Cosmetic>, Integer>> getSavedSortedCosmetics(){
		return savedSortedCosmetics;
	}

	
 



}
