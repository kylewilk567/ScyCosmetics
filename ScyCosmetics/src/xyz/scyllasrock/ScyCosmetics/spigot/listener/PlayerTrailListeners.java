package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Particle.DustTransition;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.MetadataValue;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerTrail;

public class PlayerTrailListeners implements Listener {
	
	
	private static Main plugin = Main.getInstance();
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	private static int cooldownSeconds = configMang.getConfig().getInt("misc.special_feature_error_cooldown");
	private static Map<UUID, Long> errorCooldown = new HashMap<UUID, Long>();
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()))
            return;
        
        Player player = event.getPlayer();
        
        //Do not spawn particles if player is vanished (Premium Vanish support optional)
        if(player.getGameMode().equals(GameMode.SPECTATOR) || isVanished(player)) {
        	return;
        }
        
        PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
        PlayerTrail trail = (PlayerTrail) playerObject.getActiveCosmetic(CosmeticType.PLAYER_TRAIL);
		if(trail == null) return;
		
		String customData;
		if(trail.getCosData() != null) customData = trail.getCosData()[0];
		else customData = null;
		
		boolean correctData = checkParticleData(trail);
		//Send error message if data does not match with particle type
		if(!correctData) {
			if(!errorCooldown.containsKey(player.getUniqueId()) || errorCooldown.get(player.getUniqueId()) < System.currentTimeMillis()) {
			player.sendMessage(configMang.getMessage("error_particle_data"));
			errorCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1000 * cooldownSeconds);
			}
		}
		
		int count = trail.getCount();
		double offsetX = trail.getOffsetX(), offsetY = trail.getOffsetY(), offsetZ = trail.getOffsetZ();
		
		//Spawn particles at player's feet
		for(Particle particle : trail.getParticles()) {
			Location loc = player.getLocation().clone();
			loc.setY(loc.getY() + 0.1);
			
			//Manually do offset if necessary
			if(particle.equals(Particle.REDSTONE) || particle.equals(Particle.SPELL_MOB) || particle.equals(Particle.SPELL_MOB_AMBIENT) ||
					particle.equals(Particle.DUST_COLOR_TRANSITION) || particle.equals(Particle.NOTE)) {

				loc.setX(loc.getX() + getRandomDouble(-1 * offsetX, offsetX));
				loc.setY(loc.getY() + getRandomDouble(-1 * offsetY, offsetY));
				loc.setZ(loc.getZ() + getRandomDouble(-1 * offsetZ, offsetZ));
				
			}
			
			spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, customData);
		}
    }
	
	
	/**
	 * 
	 * @param trail
	 * @return - true if particle and custom data are a correct match
	 */
	private boolean checkParticleData(PlayerTrail trail) {
		String[] customData = trail.getCosData();
		if(customData == null) return true;
		return true;	
	}
	
	private void spawnParticle(Particle particle, Location loc, int count, double offsetX, double offsetY, double offsetZ, String customData) {
		
		//Redstone particle - offsets are ignored
		if(particle.equals(Particle.REDSTONE)) {
			//No color specified, default to black
			if(customData == null) {
				DustOptions dustOptions = new DustOptions(Color.fromRGB(0, 0, 0), 1.0F);
				loc.getWorld().spawnParticle(particle, loc, count, dustOptions);
			}
			//Spawns using random colors
			else if(customData.equalsIgnoreCase("random")) {
				offsetX = getRandomInt(0, 255);
				offsetY = getRandomInt(0, 255);
				offsetZ = getRandomInt(0, 255);
				DustOptions dustOptions = new DustOptions(Color.fromRGB((int) offsetX, (int) offsetY, (int) offsetZ), 1.0F);
				loc.getWorld().spawnParticle(particle, loc, count, dustOptions);
			}

			//Spawns green OR red particle chosen at random - offsets are ignored
			else if(customData.equalsIgnoreCase("christmas")) {
			double random = Math.random();
			DustOptions dustOptions;
			if(random > 0.5) {
				dustOptions = new DustOptions(Color.fromRGB(255, 0, 0), 1.0F);
			}
			else {
				dustOptions = new DustOptions(Color.fromRGB(0, 255, 0), 1.0F);
			}
			loc.getWorld().spawnParticle(particle, loc, count, dustOptions);			
			}
			
			//Spawns using RGB values specified in custom data
			else if(customData.startsWith("RGB")) {
				//Extract RGB values
				String[] rgbStringArr = customData.split(";");
				
				//check length
				if(rgbStringArr.length < 4) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED +
							"Error! PlayerTrail with particle " + particle.toString() + " "
									+ "rgb misconfigured! Custom_data should be of the form 'RGB;75;75;75'");
				}
				try {
					int red = Integer.parseInt(rgbStringArr[1]);
					int green = Integer.parseInt(rgbStringArr[2]);
					int blue = Integer.parseInt(rgbStringArr[3]);
					DustOptions dustOptions = new DustOptions(Color.fromRGB(red, green, blue), 1.0F);
					loc.getWorld().spawnParticle(particle, loc, count, dustOptions);
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}

			}
			
		}
		
		//Dust Color Transition particle - does not use offsets
		else if(particle.equals(Particle.DUST_COLOR_TRANSITION)) {
			//No data specified - just spawn a white particle
			if(customData == null) {
				DustTransition dustTrans = new DustTransition(Color.fromRGB(0, 0, 0), Color.fromRGB(0, 0, 0), 1.0F);
				loc.getWorld().spawnParticle(particle, loc, count, dustTrans);
			}
			
			//Spawns using random colors
			else if(customData.equalsIgnoreCase("random")) {
				DustTransition dustTrans = new DustTransition(Color.fromRGB(getRandomInt(0, 255), getRandomInt(0, 255), getRandomInt(0, 255)),
						Color.fromRGB(getRandomInt(0, 255), getRandomInt(0, 255), getRandomInt(0, 255)), 1.0F);
				loc.getWorld().spawnParticle(particle, loc, count, dustTrans);
			}
			
			//Spawns green particles that transition to red
			else if(customData.equalsIgnoreCase("christmas")) {
				DustTransition dustTrans = new DustTransition(Color.fromRGB(0, 255, 0), Color.fromRGB(255, 0, 0), 1.0F);
				loc.getWorld().spawnParticle(particle, loc, count, dustTrans);
			}
			
		}
		//Spell Mob or Spell Mob Ambient particles - offsets are ignored
		else if(particle.equals(Particle.SPELL_MOB_AMBIENT) || particle.equals(Particle.SPELL_MOB)) {
			
			//No data specified - just spawn a white particle
			if(customData == null) {
				double red = 255 / 255D;
				double green = 255 / 255D;
				double blue = 255 / 255D;
				loc.getWorld().spawnParticle(particle, loc, 0, red, green, blue, 1);
			}
			
			//Spawns using random colors
			else if(customData.equalsIgnoreCase("random")) {
				double red = getRandomInt(0, 255) / 255D;
				double green = getRandomInt(0, 255) / 255D;
				double blue = getRandomInt(0, 255) / 255D;

				loc.getWorld().spawnParticle(particle, loc, 0, red, green, blue, 1);
				
			}
			
			//Spawns green OR red particles chosen at random
			else if(customData.equalsIgnoreCase("christmas")) {
				double random = Math.random();
				double red, green;
				double blue = 0 / 255D;
				if(random > 0.5) {
					red = 1.0;
					green = 0;
				}
				else {
					red = 0;
					green = 1.0;
				}
				loc.getWorld().spawnParticle(particle, loc, 0, red, green, blue, 1);
				
			}
			
			//Spawns from RGB values
			else if(customData.startsWith("RGB")) {
				//Extract RGB values
				String[] rgbStringArr = customData.split(";");
				
				//check length
				if(rgbStringArr.length < 4) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED +
							"Error! PlayerTrail with particle " + particle.toString() + " "
									+ "rgb misconfigured! Custom_data should be of the form 'RGB;75;75;75'");
				}
				try {
					double red = Integer.parseInt(rgbStringArr[1]) / 255D;
					double green = Integer.parseInt(rgbStringArr[2]) / 255D;
					double blue = Integer.parseInt(rgbStringArr[3]) / 255D;
					loc.getWorld().spawnParticle(particle, loc, 0, red, green, blue, 1);
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		//Note particle - Offsets not used
		else if(particle.equals(Particle.NOTE)) {
			//No color specified. Default to red
			if(customData == null) {
                loc.getWorld().spawnParticle(particle, loc, 0, 6.0 / 24.0, 0, 0, 1);	
			}
			
			//Spawn random color
			else if(customData.equalsIgnoreCase("random")) {
				double color = getRandomInt(0, 25) / 24D;
                loc.getWorld().spawnParticle(particle, loc, 0, color, 0, 0, 1);
			}
			
			//Spawn specified color
			else if(customData.startsWith("color")) {
				String[] colorArr = customData.split(";");
				try {
				//check length
				if(colorArr.length < 2 || Integer.parseInt(colorArr[1]) > 24) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED +
							"Error! PlayerTrail with particle " + particle.toString() + " "
									+ "color misconfigured! Custom_data should be of the form 'color;{0-24}'");
				}
				double color = Integer.parseInt(colorArr[1]) / 24D;
				loc.getWorld().spawnParticle(particle, loc, 0, color, 0, 0, 1);
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		//Other particles
		else {
			loc.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, 0);
			
		}
	}
	
	/**
	 * Generates random integer between lowerbound (inclusive) and upperbound (exclusive)
	 * @param lowerbound
	 * @param upperbound
	 * @return
	 */
	private int getRandomInt(int lowerbound, int upperbound) {
		return ((int) (Math.random() * (upperbound - lowerbound))) + lowerbound;
	}
	
	/**
	 * Generates random double between lowerbound (inclusive) and upperbound (exclusive)
	 * @param lowerbound
	 * @param upperbound
	 * @return
	 */
	private double getRandomDouble(double lowerbound, double upperbound) {
		return (Math.random() * (upperbound - lowerbound)) + lowerbound;
	}
	
	/**
	 * PremiumVanish support
	 * @param player
	 * @return
	 */
	private static boolean isVanished(Player player) {
		if(!plugin.premVanishEnabled()) return false;
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
}

}
