package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import java.util.ArrayList;
import java.util.List;

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
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticTier;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class TakeAllCommand implements CommandExecutor {
	
	private static Main plugin = Main.getInstance();
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//If sender is player without perms
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!player.hasPermission(configMang.getPermission("scycosmetics_giveall"))) {
				player.sendMessage(configMang.getMessage("no_permission"));
				return true;
			}
		}
		
		//Check for at least 2 arguments
		if(args.length < 2) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configMang.getConfig().getString("messages.prefix")
					+ " &cUsage: /scos giveall <player> [type] [tier]"));
			return true;
		}
		
		//Check player is a valid player
		Player player = Bukkit.getPlayer(args[1]);
		if(player == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					configMang.getMessageNoColor("error_player_not_found").replace("{player}", args[1])));
			return true;
		}
		
		String type = "any";
		String tier = "any";
		CosmeticType cosType = null;
		CosmeticTier cosTier = null;
		//If type specified
		if(args.length > 2) {
			type = args[2].toUpperCase();
			if(!type.equalsIgnoreCase("any")) {
				//check for valid type
				try {
					cosType = CosmeticType.valueOf(type);
				} catch(IllegalArgumentException e) {
					sender.sendMessage(configMang.getMessage("error_invalid_cosmetic_type"));
					return true;
				}
			}
		}
		
		//If tier specified
		if(args.length == 4) {
			tier = args[3].toUpperCase();
			if(!tier.equalsIgnoreCase("any")) {
				//Check for valid tier
				try {
					cosTier = CosmeticTier.valueOf(tier);
				} catch(IllegalArgumentException e) {
					sender.sendMessage(configMang.getMessage("error_invalid_cosmetic_tier"));
					return true;
				}
			}
		}
		
		List<String> cosmeticsToRemove = new ArrayList<String>();
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());

		//Get cosmetics to remove
		for(String id : playerObject.getUnlockedCosmetics()) {
			Cosmetic cos = plugin.getCosmeticFromId(id);
			if(cosType == null && cosTier == null) {
				cosmeticsToRemove.add(id);
			}
			else if(cosType == null) {
				if(cos.getTier().equals(cosTier)) cosmeticsToRemove.add(id);
			}
			else if(cosTier == null) {
				if(cos.getType().equals(cosType)) cosmeticsToRemove.add(id);
			}
			else {
				if(cos.getType().equals(cosType) && cos.getTier().equals(cosTier)) cosmeticsToRemove.add(id);
			}
		}
		//Remove cosmetics
		cosmeticsToRemove.stream().forEach(cos -> playerObject.removeUnlockedCosmetic(cos, false));
		
		//Send confirmation message to sender
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
				configMang.getMessageNoColor("scycos_takeall_success").replace("{player}", args[1])));
		
		
		return true;
	}

}
