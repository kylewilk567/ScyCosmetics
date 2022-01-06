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
import org.bukkit.util.EulerAngle;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticTier;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;
import xyz.scyllasrock.ScyCosmetics.util.InventoryUtils;

public class Scycosmetics implements CommandExecutor, TabCompleter {
	
	Main plugin = Main.getPlugin(Main.class);
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();


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
			player.openInventory(InventoryUtils.getColorPickerInv());
			return true;
		}
		
		if(args[0].equalsIgnoreCase("help")) {
			new HelpCommand().onCommand(sender, cmd, label, args);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("give")) {
			new GiveCommand().onCommand(sender, cmd, label, args);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("take")) {
			new TakeCommand().onCommand(sender, cmd, label, args);
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
			
			if("help".startsWith(searching) && player.hasPermission(configMang.getPermission("scycosmetics_help"))) {
				help.add("help");
			}

			if ("give".startsWith(searching) && player.hasPermission(configMang.getPermission("scycosmetics_give"))) {
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
				Player argument = Bukkit.getPlayer(args[1]);
				PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(argument.getUniqueId());
				//Cosmetic ids player does not have, else add nothing
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getId().toLowerCase().startsWith(searching) && !playerObject.hasCosmeticUnlocked(cos)) help.add(cos.getId());
				}
				
				//Random argument
				if("random".startsWith(searching)) help.add("random");

			}
			
			//Take command
			if(args[0].equalsIgnoreCase("take") && player.hasPermission(configMang.getPermission("scycosmetics_take"))) {
				Player argument = Bukkit.getPlayer(args[1]);
				PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(argument.getUniqueId());
				//Cosmetic ids player does not have, else add nothing
				for(Cosmetic cos : plugin.getCosmetics().values()) {
					if(cos.getId().toLowerCase().startsWith(searching) && playerObject.hasCosmeticUnlocked(cos)) help.add(cos.getId());
				}
			}
			return help;
			
		case 4:
			searching = "";
			if(args[3] != null) searching = args[3].toLowerCase();
			
			//Give random command
			if(args[0].equalsIgnoreCase("give") && args[2].equalsIgnoreCase("random") && player.hasPermission(configMang.getPermission("scycosmetics_give"))) {
				
				//cosmetic types
				for(CosmeticType type : CosmeticType.values()) {
					if(type.toString().toLowerCase().startsWith(searching)) help.add(type.toString());
				}

			}
			return help;
			
		case 5:
			searching = "";
			if(args[4] != null) searching = args[4].toLowerCase();
			
			//Give random type [tier] command
			if(args[0].equalsIgnoreCase("give") && args[2].equalsIgnoreCase("random") && player.hasPermission(configMang.getPermission("scycosmetics_give"))) {
				
				//cosmetic tiers
				for(CosmeticTier tier : CosmeticTier.values()) {
					if(tier.toString().toLowerCase().startsWith(searching)) help.add(tier.toString());
				}

			}
			return help;

		default:
			return help;
		}
			
	}

}
