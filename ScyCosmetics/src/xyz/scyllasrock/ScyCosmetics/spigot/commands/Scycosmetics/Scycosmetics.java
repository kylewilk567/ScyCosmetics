package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticTier;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class Scycosmetics implements CommandExecutor, TabCompleter {
	
	private static Main plugin = Main.getInstance();
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();


	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
				
		if(args.length == 0) {
			new BaseCommand().onCommand(sender, cmd, label, args);
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
		
		if(args[0].equalsIgnoreCase("givew")) {
			new GivewCommand().onCommand(sender, cmd, label, args);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("take")) {
			new TakeCommand().onCommand(sender, cmd, label, args);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("giveall")) {
			new GiveAllCommand().onCommand(sender, cmd, label, args);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("takeall")) {
			new TakeAllCommand().onCommand(sender, cmd, label, args);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("afk")) {
			new AFKCommand().onCommand(sender, cmd, label, args);
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
			
			if ("givew".startsWith(searching) && player.hasPermission(configMang.getPermission("scycosmetics_givew"))) {
				help.add("givew");
			}
			
			if ("take".startsWith(searching) && player.hasPermission(configMang.getPermission("scycosmetics_take"))) {
				help.add("take");
			}
			if ("giveall".startsWith(searching) && player.hasPermission(configMang.getPermission("scycosmetics_giveall"))) {
				help.add("giveall");
			}
			if ("takeall".startsWith(searching) && player.hasPermission(configMang.getPermission("scycosmetics_takeall"))) {
				help.add("takeall");
			}
			if("afk".startsWith(searching) && player.hasPermission(configMang.getPermission("scycosmetics_afk"))) {
				help.add("afk");
			}
			return help;
			
		case 2:
			searching = "";
			if(args[1] != null) searching = args[1].toLowerCase();
			
			//Give command - player argument
			if(args[0].equalsIgnoreCase("give") && player.hasPermission(configMang.getPermission("scycosmetics_give"))
					|| args[0].equalsIgnoreCase("givew") && player.hasPermission(configMang.getPermission("scycosmetics_givew"))
					|| args[0].equalsIgnoreCase("take") && player.hasPermission(configMang.getPermission("scycosmetics_take"))
					|| args[0].equalsIgnoreCase("giveall") && player.hasPermission(configMang.getPermission("scycosmetics_giveall"))
					|| args[0].equalsIgnoreCase("takeall") && player.hasPermission(configMang.getPermission("scycosmetics_takeall"))) {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(onlinePlayer.getName().toLowerCase().startsWith(searching))
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
			
			///Givew command
			if(args[0].equalsIgnoreCase("givew") && player.hasPermission(configMang.getPermission("scycosmetics_givew"))) {
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
			
			//Giveall or takeall commands
			if(args[0].equalsIgnoreCase("giveall") && player.hasPermission(configMang.getPermission("scycosmetics_giveall"))
					|| args[0].equalsIgnoreCase("takeall") && player.hasPermission(configMang.getPermission("scycosmetics_takeall"))) {
				
				//cosmetic types
				for(CosmeticType type : CosmeticType.values()) {
					if(type.toString().toLowerCase().startsWith(searching)) help.add(type.toString());
				}
				
			}
			
			
			
			return help;
			
		case 4:
			searching = "";
			if(args[3] != null) searching = args[3].toLowerCase();
			
			//Give random and givew random commands
			if(args[0].equalsIgnoreCase("give")
					&& args[2].equalsIgnoreCase("random")
					&& player.hasPermission(configMang.getPermission("scycosmetics_give"))
					|| args[0].equalsIgnoreCase("givew")
					&& args[2].equalsIgnoreCase("random")
					&& player.hasPermission(configMang.getPermission("scycosmetics_givew"))
					) {
				
				//cosmetic types
				for(CosmeticType type : CosmeticType.values()) {
					if(type.toString().toLowerCase().startsWith(searching)) help.add(type.toString());
				}

			}
			
			//Giveall or takeall commands
			if(args[0].equalsIgnoreCase("giveall") && player.hasPermission(configMang.getPermission("scycosmetics_giveall"))
					|| args[0].equalsIgnoreCase("takeall") && player.hasPermission(configMang.getPermission("scycosmetics_takeall"))) {
				
				//cosmetic tiers
				for(CosmeticTier tier : CosmeticTier.values()) {
					if(tier.toString().toLowerCase().startsWith(searching)) help.add(tier.toString());
				}
				
			}
			return help;
			
		case 5:
			searching = "";
			if(args[4] != null) searching = args[4].toLowerCase();
			
			//Give random type [tier] command
			if(args[0].equalsIgnoreCase("give")
					&& args[2].equalsIgnoreCase("random")
					&& player.hasPermission(configMang.getPermission("scycosmetics_give"))
					|| args[0].equalsIgnoreCase("givew")
					&& args[2].equalsIgnoreCase("random")
					&& player.hasPermission(configMang.getPermission("scycosmetics_givew"))) {
				
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
