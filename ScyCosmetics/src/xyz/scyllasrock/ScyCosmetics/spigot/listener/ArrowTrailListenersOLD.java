package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.ArrowTrail;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class ArrowTrailListenersOLD implements Listener {
	
	private ConfigManager configMang = ConfigManager.getConfigMang();
	private PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	@EventHandler
	public void onShoot(EntityShootBowEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(!(event.getProjectile() instanceof Arrow)) return;
		
		Player player = (Player) event.getEntity();
		
		//Check if player has an arrow trail active
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		ArrowTrail trail = (ArrowTrail) playerObject.getActiveCosmetic(CosmeticType.ARROW_TRAIL);
		if(trail == null) return;
		Particle particle = trail.getParticle();
		
		String customData;
		if(trail.getCosData() != null) customData = trail.getCosData()[0];
		else customData = null;
		
		boolean correctData = checkParticleData(trail);
		//Send error message if data does not match with particle type
		if(!correctData) {
			player.sendMessage(configMang.getMessage("error_particle_data"));
		}
		Arrow arrow = (Arrow) event.getProjectile();
		arrow.setCritical(false); //Remove crit particles if cosmetic enabled

		new BukkitRunnable() {
			double offsetX = 0, offsetY = 0, offsetZ = 0;
			int data = 0;
			Object dataOb = null;
			int count = 0;
			
			@Override
			public void run() {
		
				if(correctData && customData != null) {
					
					//NOTE
					if(particle.equals(Particle.NOTE)) {
						if(customData.equalsIgnoreCase("rainbow")) {
							count += 1;
							count = count % 24;
							offsetX = count / 24D;
							data = 1;
						}
						else if(customData.equalsIgnoreCase("christmas")) {
							if(count % 2 == 0) {
								data = 1;
								offsetX = 0 / 24D; //green
							}
							else {
								data = 1;
								offsetX = 6 / 24D; //red
							}
						}
					}
					
					//REDSTONE
					else if(particle.equals(Particle.REDSTONE)) {
						if(customData.startsWith("RGB")) {
							//Extract RGB values
							String[] rgbStringArr = customData.split(";");
							
							//check length
							if(rgbStringArr.length < 4) {
								Bukkit.getConsoleSender().sendMessage(ChatColor.RED +
										"Error! ArrowTrail with particle " + particle.toString() + " "
												+ "rgb misconfigured! Custom_data should be of the form 'RGB;75;75;75'");
							}
							try {
								offsetX = Integer.parseInt(rgbStringArr[1]);
								offsetY = Integer.parseInt(rgbStringArr[2]);
								offsetZ = Integer.parseInt(rgbStringArr[3]);
								dataOb = new DustOptions(Color.fromRGB((int) offsetX, (int) offsetY, (int) offsetZ), 1.0F);
							} catch(NumberFormatException e) {
								e.printStackTrace();
							}

						}
					}
				}
		        if(arrow.isDead() || arrow.isOnGround()) {
		            this.cancel();
		        } else {
		            for(int offset=0; offset<4; ++offset) {
		                Location location = arrow.getLocation().clone();
		                location.setX(location.getX() + arrow.getVelocity().getX() * (double) offset / 4.0D);
		                location.setY(location.getY() + arrow.getVelocity().getY() * (double) offset / 4.0D);
		                location.setZ(location.getZ() + arrow.getVelocity().getZ() * (double) offset / 4.0D);
		                
		                //Spawn particle
		                
		                //If redstone
		                if(particle.equals(Particle.REDSTONE)) {
		    				arrow.getWorld().spawnParticle(particle, location, 0,
		                            offsetX, offsetY, offsetZ,
		                            1, dataOb, true);
		                }
		                
		                //Else note or other particles
		                else {
			                arrow.getWorld().spawnParticle(particle, location, 0,
			                        offsetX, offsetY, offsetZ,
			                        1, null, true);
		                }

		            }
		        }
				count += 1;
			}
			
		}.runTaskTimer(Main.getInstance(), 0L, 1L);
		
		
		

	}
	
	
	/**
	 * 
	 * @param trail
	 * @return - true if particle and custom data are a correct match
	 */
	private boolean checkParticleData(ArrowTrail trail) {
		String[] customData = trail.getCosData();
		if(customData == null) return true;
		String customDataValue = customData[0];
		//*** BETTER WAY TO MATCH UP PARTICLES WITH KEYS
		if(customDataValue.equalsIgnoreCase("rainbow") || customDataValue.equalsIgnoreCase("christmas") && trail.getParticle().equals(Particle.NOTE)) return true;
		if(customDataValue.startsWith("RGB") && trail.getParticle().equals(Particle.REDSTONE)) return true;
			return false;	
	}
	

}
