package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;
import xyz.scyllasrock.ScyCosmetics.util.InventoryUtils;

public class BaseCommand implements CommandExecutor {
	
	Main plugin = Main.getPlugin(Main.class);
	ConfigManager configMang = ConfigManager.getConfigMang();
	PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			//**Send help command message instead for Console
			
			
			return true;
		}
		
			Player player = (Player) sender;
			
			//If sender is player without perms
			if(!player.hasPermission(configMang.getPermission("scycosmetics"))) {
				player.sendMessage(configMang.getMessage("no_permission"));
				return true;
			}
			
			PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
			Inventory inv = InventoryUtils.getBaseCosInventory(playerObject);			
			
			player.openInventory(inv);
		
		
		return false;
	}
	
	


}
