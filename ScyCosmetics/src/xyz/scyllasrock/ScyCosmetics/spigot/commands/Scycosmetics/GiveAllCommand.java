package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import java.util.List;
import java.util.stream.Collectors;

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

public class GiveAllCommand implements CommandExecutor {
	
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
		//If type specified
		if(args.length > 2) {
			type = args[2].toUpperCase();
			if(!type.equalsIgnoreCase("any")) {
				//check for valid type
				try {
					CosmeticType.valueOf(type);
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
					CosmeticTier.valueOf(tier);
				} catch(IllegalArgumentException e) {
					sender.sendMessage(configMang.getMessage("error_invalid_cosmetic_tier"));
					return true;
				}
			}
		}
		
		//Get cosmetic
		List<Cosmetic> cosmetics;
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		if(tier.equalsIgnoreCase("any") && type.equalsIgnoreCase("any")) {
			cosmetics = plugin.getCosmetics().values().stream()
					.filter(cos -> !playerObject.hasCosmeticUnlocked(cos)).collect(Collectors.toList());
		}
		else if(tier.equalsIgnoreCase("any")) {
			CosmeticType cosType = CosmeticType.valueOf(type);
			cosmetics = plugin.getCosmetics().values().stream()
					.filter(cos -> !playerObject.hasCosmeticUnlocked(cos)
							&& cos.getType().equals(cosType)).collect(Collectors.toList());
		}
		else if(type.equalsIgnoreCase("any")) {
			CosmeticTier cosTier = CosmeticTier.valueOf(tier);
			cosmetics = plugin.getCosmetics().values().stream()
					.filter(cos -> !playerObject.hasCosmeticUnlocked(cos)
							&& cos.getTier().equals(cosTier)).collect(Collectors.toList());
		}
		else {
			CosmeticType cosType = CosmeticType.valueOf(type);
			CosmeticTier cosTier = CosmeticTier.valueOf(tier);
			cosmetics = plugin.getCosmetics().values().stream()
					.filter(cos -> !playerObject.hasCosmeticUnlocked(cos)
							&& cos.getType().equals(cosType) && cos.getTier().equals(cosTier)).collect(Collectors.toList());
		}
		
		for(Cosmetic cos : cosmetics) {
			playerObject.addUnlockedCosmetic(cos.getId(), false); //Add all cosmetics to player
		}
		
		//Send confirmation message to sender
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
				configMang.getMessageNoColor("scycos_giveall_success").replace("{player}", args[1])));
		
		//Send message to receiver
		if(sender instanceof Player) {
			Player senderPlayer = (Player) sender;
			if(senderPlayer.getUniqueId().equals(player.getUniqueId())) return true;
		}
		
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
				configMang.getMessageNoColor("scycos_receiveall_success").replace("{type}", type).replace("{tier}", tier)));
		
	return true;	
	}

}
