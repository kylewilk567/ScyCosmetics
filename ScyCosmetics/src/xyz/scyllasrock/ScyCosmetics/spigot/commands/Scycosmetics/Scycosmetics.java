package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.ArrowTrail;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.util.ItemUtils;

public class Scycosmetics implements CommandExecutor, TabCompleter {
	
	Main plugin = Main.getPlugin(Main.class);


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
			ItemStack item = ItemUtils.getHead(args[1]);
			Player player = (Player) sender;
			player.getInventory().addItem(item);
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
		//First argument
		switch(args.length) {
		case 0:
			return help;
		
		case 1:

			String searching = "";

			if (args[0] != null) {
				searching = args[0].toLowerCase();
			}

			if ("give".startsWith(searching) && player.hasPermission("scycosmetics")) {
				help.add("give");
			}
			return help;
		default:
			return help;
		}
			
	}

}
