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
	
		/*
		 * TO BE REMOVED
		 */
		if(args[0].equalsIgnoreCase("test")) {
			Player player = (Player) sender;
			if(!player.isOp()) return true;
			Location particleLoc = player.getLocation().clone();
			particleLoc.setY(particleLoc.getY() + 0.1);
			
//			runImageTest(player, particleLoc);
			
			//Read from file and do command spawning
//			runCommandTest(player);
	    	

	    	

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
	
	/*
	 * TO BE REMOVED
	 */
	private void runCommandTest(Player player) {
	
	   	Main.tasko = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
			File file = new File(plugin.getDataFolder() + File.separator + "test1.txt");
			int count = 0;
			Vector yHat = new Vector(0, 1, 0);
			@Override
			public void run() {
				try {
					++count;
					if(count > 80) Main.tasko.cancel();
					Location playerClone = player.getLocation().clone();

					
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line;
					Particle particle = Particle.REDSTONE;
					while((line = br.readLine()) != null) {
						String[] commandArgs = line.split(" ");
						if(!commandArgs[0].equalsIgnoreCase("particle")) continue;
						if(commandArgs.length < 15) continue;
						double red = Double.parseDouble(commandArgs[2]) * 255;
						double green = Double.parseDouble(commandArgs[3]) * 255;
						double blue = Double.parseDouble(commandArgs[4]) * 255;
						float size = Float.parseFloat(commandArgs[5]);
						
						//Unit vector in xz plane where player is facing
						Vector pHat = player.getLocation().getDirection();
						pHat.setY(0);
						pHat.normalize();
						
						//Left right offset length
						double leftRight = Double.parseDouble(commandArgs[6].substring(1)); //Left right
						
						//Front back offset length
						double frontBack = Double.parseDouble(commandArgs[8].substring(1)); //Forward back
						
						//Get frontBack and leftRight in terms of x z coordinates
						Vector frontBackXZ = pHat.clone().multiply(frontBack);
						Vector leftRightXZ = pHat.clone().crossProduct(yHat).multiply(leftRight);
						
					
						double yOff = Double.parseDouble(commandArgs[7].substring(1)); //Up down
						
						//Calculate final offset in xz
						Vector offset = new Vector(0, yOff, 0);
						offset.add(frontBackXZ);
						offset.add(leftRightXZ);
						
						Location particleLoc = playerClone.clone();
						particleLoc.setX(particleLoc.getX() + offset.getX());
						particleLoc.setY(particleLoc.getY() + offset.getY());
						particleLoc.setZ(particleLoc.getZ() + offset.getZ());
						
						int count = Integer.parseInt(commandArgs[12]);
						
						DustOptions options = new DustOptions(
								org.bukkit.Color.fromRGB((int) red, (int) green, (int) blue), 0.6F);
						particleLoc.getWorld().spawnParticle(particle, particleLoc, count, 0, 0, 0, options);
					}
					br.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
	   		
	   	}, 0L, 1L);
		
		


	}
	
	/*
	 * TO BE REMOVED
	 */
	private void runImageTest(Player player, Location particleLoc) {
    	Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

	    	int width = 128;
	    	int height = width;
	    	
	    	double distanceMultiplier = 5D / 64D;
	    	
	    	BufferedImage imageOld = null;
	    	
	    	BufferedImage image = readFromFile(width, height, imageOld);
	    	
			@Override
			public void run() {
		    	for(int i = 0; i < image.getWidth(); ++i) {
		    		for(int j = 0; j < image.getHeight(); ++j) {
		    			Color pixelColor = new Color(image.getRGB(i, j));
		    			if(pixelColor.getRed() + pixelColor.getBlue() + pixelColor.getGreen() > 745) {
		    				Location newLoc = particleLoc.clone();
		    				newLoc.setX(newLoc.getX() + ((i - 64) * distanceMultiplier));
		    				newLoc.setZ(newLoc.getZ() + ((j - 64) * distanceMultiplier));
		    				
		    				//Spawn particle at this location
		    				DustOptions dustOptions = new DustOptions(org.bukkit.Color.fromRGB(255, 255, 255), 0.5F);
		    				particleLoc.getWorld().spawnParticle(Particle.REDSTONE, newLoc,
		    						0, 50/255D,50/255D,50/255D, 1, dustOptions);
		    			}
		    		}
		    	}
				
			}
    		
    	}, 0L, 5L);
	}
	
	/*
	 * TO BE REMOVED
	 * 
	 */
    private static BufferedImage readFromFile(int width, int height, BufferedImage image) {
    	File imageFile = new File(plugin.getDataFolder() + File.separator + "out.png");
    	image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    	try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return image;
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
					|| args[0].equalsIgnoreCase("take") && player.hasPermission(configMang.getPermission("scycosmetics_take"))
					|| args[0].equalsIgnoreCase("giveall") && player.hasPermission(configMang.getPermission("scycosmetics_giveall"))
					|| args[0].equalsIgnoreCase("takeall") && player.hasPermission(configMang.getPermission("scycosmetics_takeall"))) {
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
			
			//Give random command
			if(args[0].equalsIgnoreCase("give") && args[2].equalsIgnoreCase("random") && player.hasPermission(configMang.getPermission("scycosmetics_give"))) {
				
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
