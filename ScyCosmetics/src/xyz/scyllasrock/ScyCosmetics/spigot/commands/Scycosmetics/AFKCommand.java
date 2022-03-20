package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class AFKCommand implements CommandExecutor {
	
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(configMang.getMessage("error_player_only_cmd"));
			return true;
		}
		
		Player player = (Player) sender;
		
		//Check perms
		if(!player.hasPermission(configMang.getPermission("scycosmetics_afk"))) {
			player.sendMessage(configMang.getMessage("no_permission"));
			return true;
		}
		
		//Set to afk
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		if(playerObject == null) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "Unexpected error in afk command (player object null)."
					+ " Please let staff know."));
			return true;
		}
		
		playerObject.setAFK(true);
		player.sendMessage(configMang.getMessage("player_set_afk"));
		
	return true;	
	}

}
