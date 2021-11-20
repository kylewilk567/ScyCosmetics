package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class CosInventoryListeners implements Listener {
	
	Main plugin = Main.getPlugin(Main.class);
	ConfigManager configMang = ConfigManager.getConfigMang();
	PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	final String baseInvPath = "inventory_settings.base_inventory";
	final String configTitle = ChatColor.stripColor(
			ChatColor.translateAlternateColorCodes(
					'&', configMang.getConfig().getString(baseInvPath + ".title")));
	final int arrowTrailSlot = configMang.getConfig().getInt(baseInvPath + ".items.arrow_trail.slot");
	
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
	public void onArrowTrailInvClick(InventoryClickEvent event) {
		
		//Clicked an item?
		if(event.getCurrentItem() == null) return;
		
		//Check if inventory is base inventory
		String currTitle = ChatColor.stripColor(event.getView().getTitle());
		Player player = (Player) event.getWhoClicked();
		if(!currTitle.equals(player.getName() + "'s Arrow trails")) return;
		
		//It is arrow trail inventory
		event.setCancelled(true);
		
	}
	
	
	private Inventory getCosmeticInventory(int slot, Player player) {
		String title = "default";
		Inventory inv = Bukkit.createInventory(null, 27, title);
		if(slot == arrowTrailSlot) {
			inv = getArrowTrailInventory(player);
			
		}

		return inv;		
	}
	
	private Inventory getArrowTrailInventory(Player player) {
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		int size = (int) Math.ceil(playerObject.getUnlockedCosmetics().size()) * 9;
		Inventory inv = Bukkit.createInventory(null, size,
				ChatColor.translateAlternateColorCodes('&', player.getName() + "'s &bArrow trails"));
		int slot = 0;
		for(String id : playerObject.getUnlockedCosmetics()) {
			Cosmetic cos = plugin.getCosmeticFromId(id);
			ItemStack item = new ItemStack(cos.getDisplayItem());
			ItemMeta meta = item.getItemMeta();
			List<String> lore = new ArrayList<String>();
			lore = cos.getDisplayLore();
			meta.setDisplayName(cos.getDisplayName());
			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.setItem(slot, item);
			++slot;
		}
		return inv;
	}
	

}
