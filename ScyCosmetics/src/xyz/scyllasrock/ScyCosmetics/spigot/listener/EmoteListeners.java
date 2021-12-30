package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class EmoteListeners implements Listener {
	
	
	@EventHandler
	public void onStandManipulate(PlayerArmorStandManipulateEvent event) {
		if(event.getRightClicked().hasMetadata("Scycos_emote")){
			event.setCancelled(true);
		}
	}

}
