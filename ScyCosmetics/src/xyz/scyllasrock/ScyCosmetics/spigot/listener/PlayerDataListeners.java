package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;

public class PlayerDataListeners implements Listener {
	
	Main plugin = Main.getPlugin(Main.class);
	PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		playerHandler.addPlayerObject(event.getPlayer().getUniqueId()); //Add playerObject to map
		
	}

}
