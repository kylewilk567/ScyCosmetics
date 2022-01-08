package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class TakeCommand implements CommandExecutor {
	
	private static Main plugin = Main.getInstance();
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//If sender is player without perms
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!player.hasPermission(configMang.getPermission("scycosmetics_take"))) {
				player.sendMessage(configMang.getMessage("no_permission"));
				return true;
			}
		}
		
		//Check for at least 3 arguments
		if(args.length < 3) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configMang.getConfig().getString("messages.prefix")
					+ " &cUsage: /scos take <player> <cosmetic id>"));
			return true;
		}
		
		//Check player is a valid player
		Player player = Bukkit.getPlayer(args[1]);
		if(player == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					configMang.getMessageNoColor("error_player_not_found").replace("{player}", args[1])));
			return true;
		}
		
		//Check extra argument is a valid cosmetic id
		Cosmetic cos = plugin.getCosmeticFromId(args[2].toUpperCase());
		if(cos == null) {
			sender.sendMessage(configMang.getMessage("error_invalid_cosmetic_id"));
			return true;
		}
		
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		
		//Check if player has cosmetic unlocked
		if(!playerObject.hasCosmeticUnlocked(cos)) {
			sender.sendMessage(configMang.getMessage("error_take_cosmetic_already_locked"));
			return true;
		}
		
		//Checks complete! Remove cosmetic
		playerObject.removeUnlockedCosmetic(args[2].toUpperCase(), true);
		
		//Send confirmation message to sender
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
				configMang.getMessageNoColor("scycos_take_success")
				.replace("{player}", args[1]).replace("{cosmetic}", cos.getDisplayItem().getItemMeta().getDisplayName())));
		
	return false;	
	}

}
