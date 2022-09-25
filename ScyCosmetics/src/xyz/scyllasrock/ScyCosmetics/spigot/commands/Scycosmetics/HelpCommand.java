package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;

public class HelpCommand implements CommandExecutor {
	
	Main plugin = Main.getPlugin(Main.class);
	ConfigManager configMang = ConfigManager.getConfigMang();
	PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	
	private String[] helpMessages = {" \n&d&lScyCosmetics by &bkwilk &dHelp Menu (1/2)"
			+ "\n&aCurrently running v&c" + plugin.getDescription().getVersion() + 
			"\n " +
			"\n&e/scos - &6Opens cosmetics inventory." + 
			"\n&e/scos afk - &6Manually toggles visibility of afk particles." +
			"\n&e/scos help [page] - &6displays the help menu." +
			"\n&e/semote [emote] - &6Opens inventory (no args) or plays emote.",
	
			" \n&d&nAdmin Commands Only:&e" +
			"\n " +
			"\n&e/scos give <player> <cosmetic id> - &6Gives player the cosmetic." +
			"\n&e/scos give <player> random [type] [tier] - &6Gives player a random cosmetic of specified type and/or tier. Use 'any' to specify any type or tier." +
			"\n&e/scos givew <play> random [type] - &6Gives player random cosmetic of specified type using modified chances for tiers." +
			"\n&e/scos take <player> <cosmetic id> - &6Takes cosmetic from player." +
			"\n&e/scos giveall <player> [type] [tier] - &6Gives player all cosmetics for type and tier. Can use 'any' for type or tier." +
			"\n&e/scos takeall <player> [type] [tier] - &6Takes all cosmetics of type and tier from player. Can use 'any' for type or tier."
	};

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!player.hasPermission(configMang.getPermission("scycosmetics_help"))) {
				player.sendMessage(configMang.getMessage("no_permission"));
				return true;
			}
		}
		
		
		//Send the message
		if(args.length == 1) { // /scos help (page 1)
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getHelpMessage(1)));
			return true;
		}
		//Check integer argument
		int page;
		try{
			page = Integer.parseInt(args[1]);
		} catch(Exception e) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configMang.getConfig().getString("messages.prefix") + 
					" &cArgument is not an integer!\n&cUsage: /scos help <page>"));
			return true;
		}
		
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getHelpMessage(page)));
				
		
		return false;
	}
	
	private String getHelpMessage(int page) {
		if(page > helpMessages.length) page = helpMessages.length;
		if(page <= 0) page = 1;
		return helpMessages[page - 1];
	}

}
