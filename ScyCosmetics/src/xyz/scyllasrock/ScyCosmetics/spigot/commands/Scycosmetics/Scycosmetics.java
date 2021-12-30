package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.util.InventoryUtils;

public class Scycosmetics implements CommandExecutor, TabCompleter {
	
	Main plugin = Main.getPlugin(Main.class);
	private static ConfigManager configMang = ConfigManager.getConfigMang();


	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) { //**** TO BE REMOVED
			Player player = (Player) sender;
			if(!player.isOp()) return false;
		}
		
		
		if(args.length == 0) {
			new BaseCommand().onCommand(sender, cmd, label, args);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("test")) {
			Player player = (Player) sender;
			ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
			stand.setRotation(player.getLocation().getPitch() + 90, player.getLocation().getYaw());
			player.openInventory(InventoryUtils.getColorPickerInv());
			return true;
		}
		
		if(args[0].equalsIgnoreCase("give")) {
			new GiveCommand().onCommand(sender, cmd, label, args);
			return true;
		}
		

		
		return false;
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		Player player = (Player) sender;
		 //**** TO BE REMOVED
		if(!player.isOp()) return null;
		
		List<String> help = new ArrayList<>();
		String searching = "";
		switch(args.length) {

		case 0:
			return help;
		//First argument
		case 1:

			if (args[0] != null) {
				searching = args[0].toLowerCase();
			}

			if ("give".startsWith(searching) && player.hasPermission("scycosmetics")) {
				help.add("give");
			}
			return help;
			
		case 2:
			searching = "";
			if(args[1] != null) searching = args[1].toLowerCase();
			
			//Give command - player argument
			if(args[0].equalsIgnoreCase("give") && player.hasPermission(configMang.getPermission("scycosmetics_give"))) {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					help.add(onlinePlayer.getName());
				}
			}
			return help;

		
		case 3:
			searching = "";
			if(args[2] != null) searching = args[2].toLowerCase();
			
			//Give command
			if(args[0].equalsIgnoreCase("give") && player.hasPermission(configMang.getPermission("scycosmetics_give"))) {
				
				//Cosmetic ids
				for(String id : plugin.getCosmetics().keySet()) {
					if(id.toLowerCase().startsWith(searching)) help.add(id);
				}

			}
			return help;

		default:
			return help;
		}
			
	}

}
