package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticTier;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class GiveCommand implements CommandExecutor {
	
	private static Main plugin = Main.getInstance();
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	private static Economy eco = plugin.getVaultEco();
	private static double randomVaultChance = configMang.getConfig().getDouble("misc.give_random_vault_chance");
	private static double randomVaultAmount = configMang.getConfig().getDouble("misc.give_random_vault_amount");
	private static double vaultAllUnlockedAmount = configMang.getConfig().getDouble("misc.give_vault_all_unlocked");

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//If sender is player without perms
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!player.hasPermission(configMang.getPermission("scycosmetics_give"))) {
				player.sendMessage(configMang.getMessage("no_permission"));
				return true;
			}
		}
		
		//Check for at least 3 arguments
		if(args.length < 3) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configMang.getConfig().getString("messages.prefix")
					+ " &cUsage: /scos give <player> <cosmetic id> &eOR\n &c/scos give <player> random [type] [tier]"));
			return true;
		}
		
		//Check player is a valid player
		Player player = Bukkit.getPlayer(args[1]);
		if(player == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					configMang.getMessageNoColor("error_player_not_found").replace("{player}", args[1])));
			return true;
		}
		
		//**Random version of command
		if(args[2].equalsIgnoreCase("random")) {
			//Check args length again
			if(args.length != 3 && args.length != 4 && args.length != 5) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configMang.getConfig().getString("messages.prefix")
						+ " &cUsage: /scos give <player> random [type] [tier]"));
				return true;
			}
			
			String type = "any";
			String tier = "any";
			//If type specified
			if(args.length > 3) {
				type = args[3].toUpperCase();
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
			if(args.length == 5) {
				tier = args[4].toUpperCase();
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
			
			//Calculate probability to give vault money or a cosmetic
			double randomPercent = Math.random() * 100.0;
			
				//Give random
			if(randomPercent < randomVaultChance) {
				if(randomVaultAmount != 0.0) {
				eco.depositPlayer(player, randomVaultAmount);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
						configMang.getMessageNoColor("random_vault_money_given").replace("{amount}", String.valueOf(randomVaultAmount))));
				}
				else {
					player.sendMessage(configMang.getMessage("random_vault_no_money_given"));
				}
			}
				//Give cosmetic
			else {
				//Get cosmetic options
				List<Cosmetic> cosmeticOptions;
				PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
				if(tier.equalsIgnoreCase("any") && type.equalsIgnoreCase("any")) {
					cosmeticOptions = plugin.getCosmetics().values().stream()
							.filter(cos -> !playerObject.hasCosmeticUnlocked(cos) && !cos.isUnobtainable()).collect(Collectors.toList());
				}
				else if(tier.equalsIgnoreCase("any")) {
					CosmeticType cosType = CosmeticType.valueOf(type);
					cosmeticOptions = plugin.getCosmetics().values().stream()
							.filter(cos -> !playerObject.hasCosmeticUnlocked(cos) && !cos.isUnobtainable()
									&& cos.getType().equals(cosType)).collect(Collectors.toList());
				}
				else if(type.equalsIgnoreCase("any")) {
					CosmeticTier cosTier = CosmeticTier.valueOf(tier);
					cosmeticOptions = plugin.getCosmetics().values().stream()
							.filter(cos -> !playerObject.hasCosmeticUnlocked(cos) && !cos.isUnobtainable()
									&& cos.getTier().equals(cosTier)).collect(Collectors.toList());
				}
				else {
					CosmeticType cosType = CosmeticType.valueOf(type);
					CosmeticTier cosTier = CosmeticTier.valueOf(tier);
					cosmeticOptions = plugin.getCosmetics().values().stream()
							.filter(cos -> !playerObject.hasCosmeticUnlocked(cos) && !cos.isUnobtainable()
									&& cos.getType().equals(cosType) && cos.getTier().equals(cosTier)).collect(Collectors.toList());
				}
				//If no available cosmetic exists, check if money should be given. Else do not give anything and send message
				if(cosmeticOptions.isEmpty()) {
					if(vaultAllUnlockedAmount == 0.0) {
						player.sendMessage(configMang.getMessage("all_cos_unlocked_no_reward"));
					}
					else {
						eco.depositPlayer(player, vaultAllUnlockedAmount);
						player.sendMessage(ChatColor.translateAlternateColorCodes('&',
								configMang.getMessageNoColor("all_cos_unlocked_given_money").replace("{amount}", String.valueOf(vaultAllUnlockedAmount))));
					}
				}
				//Give random cosmetic
				else {
					Random random = new Random();
					Cosmetic unlockedCosmetic = cosmeticOptions.get(random.nextInt(cosmeticOptions.size()));
					playerObject.addUnlockedCosmetic(unlockedCosmetic.getId(), true);
				}


			}
					
			
			
			return true;
		}
		
		//**Cosmetic id version of command
		else {
		
		
		//Check extra argument is a valid cosmetic id
		Cosmetic cos = plugin.getCosmeticFromId(args[2]);
		if(cos == null) {
			sender.sendMessage(configMang.getMessage("error_invalid_cosmetic_id"));
			return true;
		}
		
		//Add cosmetic id to unlocked cosmetics
		if(sender instanceof Player) {
			Player senderPlayer = (Player) sender;
			if(senderPlayer.getUniqueId().equals(player.getUniqueId())) {
				playerHandler.getPlayerObjectByUUID(player.getUniqueId()).addUnlockedCosmetic(args[2], false);
			}
			else playerHandler.getPlayerObjectByUUID(player.getUniqueId()).addUnlockedCosmetic(args[2], true);
		}
		else playerHandler.getPlayerObjectByUUID(player.getUniqueId()).addUnlockedCosmetic(args[2], false);

		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
				configMang.getMessageNoColor("scycos_give_success").replace("{player}", args[1])
				.replace("{cosmetic}", cos.getDisplayItem().getItemMeta().getDisplayName())));

			return true;
		}
		

	}

}
