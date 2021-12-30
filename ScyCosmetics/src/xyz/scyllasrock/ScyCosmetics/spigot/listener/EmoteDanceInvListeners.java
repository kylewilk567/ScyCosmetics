package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;
import xyz.scyllasrock.ScyCosmetics.util.InventoryUtils;

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
			//reload inventory
			player.closeInventory();
			player.openInventory(InventoryUtils.getEmoteDanceInv(player, playerObject));
			return;
		}
	}

}
