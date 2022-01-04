package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.LastWords;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class LastWordsListeners implements Listener {
	
	
	Main plugin = Main.getInstance();
	PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	ConfigManager configMang = ConfigManager.getConfigMang();
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		//Check if killer was a player
		if(player.getKiller() != null) {
		
		//Check if player has a lastwords active
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		LastWords lastWords = (LastWords) playerObject.getActiveCosmetic(CosmeticType.LAST_WORDS);
		event.setDeathMessage(lastWords.getMessage());
		}
	}
	

}
