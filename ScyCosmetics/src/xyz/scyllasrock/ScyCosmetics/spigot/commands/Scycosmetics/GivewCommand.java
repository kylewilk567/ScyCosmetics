package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

/*
 * Same as give command but each rarity has a weighted chance to be given instead of equal chance
 */
public class GivewCommand implements CommandExecutor {
	
	private static Main plugin = Main.getInstance();
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	private static Economy eco = plugin.getVaultEco();
	private static double randomVaultChance = configMang.getConfig().getDouble("misc.give_random_vault_chance");
	private static double randomVaultAmount = configMang.getConfig().getDouble("misc.give_random_vault_amount");
	private static double vaultAllUnlockedAmount = configMang.getConfig().getDouble("misc.give_vault_all_unlocked");
	
	private static Map<CosmeticTier, Double> tierWeights = new HashMap<CosmeticTier, Double>();
	private static double totalWeightSum = 0;
	
	static {
		tierWeights.put(CosmeticTier.COMMON, configMang.getConfig().getDouble("give_weighted.weights.common"));
		tierWeights.put(CosmeticTier.UNCOMMON, configMang.getConfig().getDouble("give_weighted.weights.uncommon"));
		tierWeights.put(CosmeticTier.RARE, configMang.getConfig().getDouble("give_weighted.weights.rare"));
		tierWeights.put(CosmeticTier.EPIC, configMang.getConfig().getDouble("give_weighted.weights.epic"));
		tierWeights.put(CosmeticTier.SPECIAL, configMang.getConfig().getDouble("give_weighted.weights.special"));
		tierWeights.put(CosmeticTier.LEGENDARY, configMang.getConfig().getDouble("give_weighted.weights.legendary"));
		tierWeights.put(CosmeticTier.MYTHIC, configMang.getConfig().getDouble("give_weighted.weights.mythic"));
		tierWeights.put(CosmeticTier.ARTIFACT, configMang.getConfig().getDouble("give_weighted.weights.artifact"));
		tierWeights.put(CosmeticTier.SEASONAL, configMang.getConfig().getDouble("give_weighted.weights.seasonal"));
		tierWeights.put(CosmeticTier.LORE, configMang.getConfig().getDouble("give_weighted.weights.lore"));
		for(double weight : tierWeights.values()) {
			totalWeightSum += weight;
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		//If sender is player without perms
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!player.hasPermission(configMang.getPermission("scycosmetics_givew"))) {
				player.sendMessage(configMang.getMessage("no_permission"));
				return true;
			}
		}
		
		//Check for at least 3 arguments
		if(args.length < 3 || !args[2].equalsIgnoreCase("random")) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configMang.getConfig().getString("messages.prefix")
					+ " &cUsage: &c/scos givew <player> random [type] [tier]"));
			return true;
		}
		
		//Check player is a valid player
		Player player = Bukkit.getPlayer(args[1]);
		if(player == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					configMang.getMessageNoColor("error_player_not_found").replace("{player}", args[1])));
			return true;
		}
		
			//Check args length again
			if(args.length != 3 && args.length != 4 && args.length != 5) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configMang.getConfig().getString("messages.prefix")
						+ " &cUsage: /scos give <player> random [type]"));
				return true;
			}
			
		String type = "any";

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
//		if(args.length == 5) {
//			tier = args[4].toUpperCase();
//			if(!tier.equalsIgnoreCase("any")) {
//				//Check for valid tier
//				try {
//					CosmeticTier.valueOf(tier);
//				} catch(IllegalArgumentException e) {
//					sender.sendMessage(configMang.getMessage("error_invalid_cosmetic_tier"));
//					return true;
//				}
//			}
//		}
		
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
			if(type.equalsIgnoreCase("any")) {
				cosmeticOptions = plugin.getCosmetics().values().stream()
						.filter(cos -> !playerObject.hasCosmeticUnlocked(cos) && !cos.isUnobtainable())
						.filter(cos -> !(cos.getTier().equals(CosmeticTier.SEASONAL) && !cos.isPurchaseable())) //Special case - remove unpurchaseable seasonal cosmetics
						.collect(Collectors.toList());
			}
			else {
				CosmeticType cosType = CosmeticType.valueOf(type);
				cosmeticOptions = plugin.getCosmetics().values().stream()
						.filter(cos -> !playerObject.hasCosmeticUnlocked(cos) && !cos.isUnobtainable()
								&& cos.getType().equals(cosType))
						.filter(cos -> !(cos.getTier().equals(CosmeticTier.SEASONAL) && !cos.isPurchaseable())) //Special case
						.collect(Collectors.toList());
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
			//Give random cosmetic - WEIGHTED BY COSMETIC TIER
			else {
				double chosenDouble = Math.random() * totalWeightSum;
				CosmeticTier chosenTier = CosmeticTier.COMMON;
				double runningSum = 0;
				while(runningSum <= chosenDouble) {
					runningSum += tierWeights.get(chosenTier);
					chosenTier = chosenTier.next();
				}
				
				chosenTier = chosenTier.prev();
				
				//Find highest tier cosmetic that is at or below the chosenTier
				while(true) {
					List<Cosmetic> tieredOptions = new ArrayList<Cosmetic>();
					for(Cosmetic cos : cosmeticOptions) {
						if(cos.getTier().equals(chosenTier)) tieredOptions.add(cos);
					}
					
					if(tieredOptions.size() == 0 && chosenTier.equals(CosmeticTier.COMMON)) { //Give vault money
						if(randomVaultAmount != 0.0) {
							eco.depositPlayer(player, randomVaultAmount);
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
									configMang.getMessageNoColor("random_vault_money_given").replace("{amount}", String.valueOf(randomVaultAmount))));
						}
						else {
							player.sendMessage(configMang.getMessage("random_vault_no_money_given"));
						}
						return true;
					}
					
					else if(tieredOptions.size() == 0) { //Try lower tier cosmetic
						chosenTier = chosenTier.prev();
						continue;
					}
					
					else { //Give random cosmetic of chosen Tier
						Random random = new Random();
						Cosmetic unlockedCosmetic = tieredOptions.get(random.nextInt(tieredOptions.size()));
						playerObject.addUnlockedCosmetic(unlockedCosmetic.getId(), true);
						return true;
					}
				
				}

				


			}


		}
				
		
		
		return true;
		
		
		
	}

}
