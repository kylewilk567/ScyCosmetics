package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.LogMessage;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class LogMessageListeners implements Listener {
	
	private static Main plugin = Main.getPlugin(Main.class);
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(isVanished(player)) return;
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		if(playerObject != null) {
			LogMessage cos = (LogMessage) playerObject.getActiveCosmetic(CosmeticType.LOG_MESSAGE);
			if(cos != null) {
				if(cos.getLogInSound() != null)	player.playSound(player.getLocation(), cos.getLogInSound(), 0.8F, 1.0F); //0.8 means 80% volume. 1.0 means play at 1x speed
				
				event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', 
						cos.getLogInMessage().replace("{player}", player.getDisplayName())));
			}
			//Use a default join message
			else {
				event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&7" + player.getDisplayName() + " &6has joined the game."));
			}
		}
		
	}
	
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if(isVanished(player)) return;
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
	
		if(playerObject != null) {
			LogMessage cos = (LogMessage) playerObject.getActiveCosmetic(CosmeticType.LOG_MESSAGE);
			if(cos != null) {
				event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', 
						cos.getLogOffMessage().replace("{player}", player.getDisplayName())));
			}
			//Use a default leave message
			else {
				event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&7" + player.getDisplayName() + " &6couldn't handle it anymore"));
			}
		}
	}
		
	
	/**
	 * PremiumVanish support
	 * @param player
	 * @return
	 */
	private static boolean isVanished(Player player) {
		if(!plugin.premVanishEnabled()) return false;
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
}

}
