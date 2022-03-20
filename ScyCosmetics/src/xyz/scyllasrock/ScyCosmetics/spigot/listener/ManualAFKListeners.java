package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class ManualAFKListeners implements Listener {
	
	private PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(event.getPlayer().getUniqueId());
		if(playerObject != null) playerObject.setAFK(false);
	}

}
