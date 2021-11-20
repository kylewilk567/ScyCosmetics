package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.util.InventoryUtils;

public class BaseCommand implements CommandExecutor {
	
	Main plugin = Main.getPlugin(Main.class);
	ConfigManager configMang = ConfigManager.getConfigMang();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			//**Send help command message instead for Console
			
			
			return true;
		}
		
			Player player = (Player) sender;
			player.openInventory(InventoryUtils.getInventoryFromConfigPath("inventory_settings.base_inventory"));
		
		
		return false;
	}
	


}
