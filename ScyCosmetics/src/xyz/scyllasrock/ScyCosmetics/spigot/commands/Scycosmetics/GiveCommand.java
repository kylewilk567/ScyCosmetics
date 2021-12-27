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

public class GiveCommand implements CommandExecutor {
	
	Main plugin = Main.getPlugin(Main.class);
	ConfigManager configMang = ConfigManager.getConfigMang();
	PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();

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
			sender.sendMessage(ChatColor.RED + "Usage: /scycosmetics give <player> <cosmetic id>");
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
		Cosmetic cos = plugin.getCosmeticFromId(args[2]);
		if(cos == null) {
			sender.sendMessage(configMang.getMessage("error_invalid_cosmetic_id"));
			return true;
		}
		
		//Add cosmetic id to unlocked cosmetics
		playerHandler.getPlayerObjectByUUID(player.getUniqueId()).addUnlockedCosmetic(args[2]);
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
				configMang.getMessageNoColor("scycos_give_success").replace("{player}", args[1])
				.replace("{cosmetic}", cos.getDisplayItem().getItemMeta().getDisplayName())));
		
		return false;
	}

}
