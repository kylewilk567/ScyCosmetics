package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import org.bukkit.Bukkit;
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
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;
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
		
		//On click, toggle the cosmetic as being active.
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
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
	
	
	private Inventory getCosmeticInventory(int slot, Player player) {
		String title = "default";
		Inventory inv = Bukkit.createInventory(null, 27, title);
		if(slot == arrowTrailSlot) {
			inv = this.getSpecificCosmeticInventory(player, CosmeticType.ARROW_TRAIL, "&bArrow trails");
		}
		else if(slot == lastWordsSlot) {
			inv = this.getSpecificCosmeticInventory(player, CosmeticType.LAST_WORDS, "&2Last words");
		}
		else if(slot == playerTrailSlot) {
			inv = this.getSpecificCosmeticInventory(player, CosmeticType.PLAYER_TRAIL, "&aPlayer trails");
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
		
		//Get all unlocked itemStacks for cosmetic type
		
		//Get active cosmetic of type specified
		ItemStack activeItem = null;
		Cosmetic activeCos = playerObject.getActiveCosmetic(type);
		if(activeCos != null) activeItem = activeCos.getDisplayItem();
		int size = ((int) Math.ceil(playerObject.getUnlockedCosmetics().size() / 9.0)) * 9;
		Inventory inv = Bukkit.createInventory(null, size,
				ChatColor.translateAlternateColorCodes('&', player.getName() + "'s " + cosmeticTitle));
		int slot = 0;
		for(String id : playerObject.getUnlockedCosmetics()) {
			Cosmetic cos = plugin.getCosmeticFromId(id);
			if(cos == null) continue;
			if(!cos.getType().equals(type)) continue;
			ItemStack item = cos.getDisplayItem().clone();
			if(ItemUtils.itemEquals(item, activeItem)){
							item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
							ItemMeta meta = item.getItemMeta();
							meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
							item.setItemMeta(meta);
						}
					
				

			inv.setItem(slot, item);
			++slot;
		}
		return inv;
	}
	

	

}
