package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;
import xyz.scyllasrock.ScyCosmetics.util.CosmeticUtils;
import xyz.scyllasrock.ScyCosmetics.util.InventoryUtils;
import xyz.scyllasrock.ScyCosmetics.util.ItemUtils;

public class EmoteDanceInvListeners implements Listener {
	
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
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
		//If clicking an informational item
			//Toggle viewing locked items - must reload inventory (changes size)
		if(event.getSlot() == event.getInventory().getSize() - 9) {
			playerObject.setShowLockedCosmetics(!playerObject.showLockedCosmetics());
			//reload inventory
			player.closeInventory();
			player.openInventory(InventoryUtils.getEmoteDanceInv(player, playerObject));
			return;
		}
		
			//Toggle filter method - will just reload for now
		else if(event.getSlot() == event.getInventory().getSize() - 1) {
			playerObject.toggleItemFilter();
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
				if(!playerObject.getUnlockedCosmetics().contains(cos.getId())) item.setType(Material.REDSTONE_BLOCK);
				event.getClickedInventory().setItem(slot, item);
				if(slot % 9 == 7) slot += 3;
				else ++slot;
				
				++count;
			}
			
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
	}

}
