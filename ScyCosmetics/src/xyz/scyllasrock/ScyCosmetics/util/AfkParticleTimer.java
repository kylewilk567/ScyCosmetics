package xyz.scyllasrock.ScyCosmetics.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.AFKEffect;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.AFKEffectStyle;

public class AfkParticleTimer implements Runnable {
	
	
	private static Main plugin = Main.getInstance();
	
	private BukkitTask task;
	private Player player;	
	private AFKEffect effect;
	
	public AfkParticleTimer(Player player, AFKEffect effect) {
		this.player = player;
		try {
			this.effect = effect.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}		
	
	@Override
	public void run() {		
		
		playEffect();
		
	}
	
	
    /**
     * Schedules this instance to "run" every colorChangeTicks
     */
    public void scheduleTimer() {
    	if(effect.getStyle().equals(AFKEffectStyle.HAIL)) {
    		task = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 1L);
    	}
        // Initialize our assigned task's id, for later use so we can cancel
    	else {
    		task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, 1L);
    	}
    }
    
    /*
     * Stops the timer
     */
    public void stopTimer() {
    	task.cancel();
    }
    
    private void playEffect() {
    	//Check if player is vanished. If vanished, do not play effect
    	if(isVanished(player)) return;
    	//Check if in spectator mode. Also do not play effect
    	if(player.getGameMode().equals(GameMode.SPECTATOR)) return;
    	
		int step = effect.step();
    	switch(effect.getStyle()) {
    	
    	case PULSE:
    		if(step % 3 != 0) return;
    		if(step > 30) effect.setStep(0); //Reset step
    		double radius = step * 0.05 + 1; //Expands out to 3 block radius
    		Location centerLoc = player.getLocation().clone();
    		//Increase y slightly
    		centerLoc.setY(centerLoc.getY() + 0.05);
    		
    		//Spawn particles in circle at radius
    		for (double t = 0; t <= 2*Math.PI*radius; t += 0.3) {
                double x = (radius * Math.cos(t / radius)) + centerLoc.getX();
                double z = centerLoc.getZ() + (radius * Math.sin(t / radius));
                Location particleLoc = new Location(player.getWorld(), x, centerLoc.getY(), z);
                player.getWorld().spawnParticle(effect.getParticle(), particleLoc, 1, 0, 0, 0, 0);
           
            }
    		
    		break;
    		
    	case SMALL_PULSE:
    		if(step % 20 != 0) return;
    		
    		Location centerLoc2 = player.getLocation().clone();
    		//Increase y slightly
    		centerLoc2.setY(centerLoc2.getY() + 0.05);
    		
    		//Spawn particles in circle at radius
    		for (double t = 0; t <= 2*Math.PI; t += 0.3) {
                player.getWorld().spawnParticle(effect.getParticle(), centerLoc2, 0, Math.cos(t), 0, Math.sin(t), 0.1);
           
            }
    		
    		break;
    		
    	case TWINS:
    		if(step % 2 != 0) return;
    		Location twin1 = player.getLocation().clone();
    		twin1.setY(twin1.getY() + 1);
    		Location twin2 = twin1.clone();
    		twin1.setX(twin1.getX() + Math.sin(step * .05));
    		twin1.setZ(twin1.getZ() + Math.cos(step * .05));
    		twin2.setX(twin2.getX() - Math.sin(step * .05));
    		twin2.setZ(twin2.getZ() - Math.cos(step * .05));
    		player.getWorld().spawnParticle(effect.getParticle(), twin1, 1, 0, 0, 0, 0);
    		player.getWorld().spawnParticle(effect.getParticle(), twin2, 1, 0, 0, 0, 0);
    		break;
    		
    	case SOLAR_SYSTEM:
    		if(step % 2 != 0) return;
    		//Player is the Sun
    		Location init = player.getLocation().clone();
    		init.setY(init.getY() + 1);
    		//Venus
    		Location mercury = init.clone();
    		mercury.setX(mercury.getX() + Math.sin(step * .05));
    		mercury.setZ(mercury.getZ() + Math.cos(step * .05));
    		
    		//Red planet - elliptical (mercury is not a typo)
    		Location redPlanet = init.clone();
    		redPlanet.setX(mercury.getX() + 1.5*Math.sin(1.6 * step * .05 + 1.3));
    		redPlanet.setZ(mercury.getZ() + 1.5*Math.cos(1.6 * step * .05 + 1.3));
    		
    		//Yellow planet
    		Location yellowPlanet = init.clone();
    		yellowPlanet.setX(yellowPlanet.getX() + 3*Math.sin(1.5 * step * .05 + 2));
    		yellowPlanet.setZ(yellowPlanet.getZ() + 3*Math.cos(1.5 * step * .05 + 2));
    		
    		//Green planet
    		Location greenPlanet = init.clone();
    		greenPlanet.setX(greenPlanet.getX() + 2.5*Math.sin(1.2 * step * .05 + 3));
    		greenPlanet.setZ(greenPlanet.getZ() + 2.5*Math.cos(1.2 * step * .05 + 3));
    		
    		//Blue planet
    		Location bluePlanet = init.clone();
    		bluePlanet.setX(mercury.getX() + 2*Math.sin(1.1 * step * .05 + 1));
    		bluePlanet.setZ(mercury.getZ() + 2*Math.cos(1.1* step * .05 + 1));
    		
			DustOptions dustOptions = new DustOptions(Color.fromRGB(168, 115, 50), 1.0F);
			init.getWorld().spawnParticle(Particle.REDSTONE, mercury, 5, dustOptions);
			DustOptions dustOptions1 = new DustOptions(Color.fromRGB(130, 50, 4), 1.0F);
			init.getWorld().spawnParticle(Particle.REDSTONE, redPlanet, 5, dustOptions1);
			DustOptions dustOptions2 = new DustOptions(Color.fromRGB(230, 197, 16), 1.0F);
			init.getWorld().spawnParticle(Particle.REDSTONE, yellowPlanet, 5, dustOptions2);
			DustOptions dustOptions3 = new DustOptions(Color.fromRGB(4, 130, 25), 1.0F);
			init.getWorld().spawnParticle(Particle.REDSTONE, greenPlanet, 5, dustOptions3);
			DustOptions dustOptions4 = new DustOptions(Color.fromRGB(52, 155, 186), 1.0F);
			init.getWorld().spawnParticle(Particle.REDSTONE, bluePlanet, 5, dustOptions4);
    		
    		break;
    		
    	case HALO:
    		if(step % 10 != 0) return;
    		
    		Location haloCenter = player.getLocation().clone();
    		//Increase y slightly
    		haloCenter.setY(haloCenter.getY() + 2.3);
    		
    		//Spawn particles in circle at radius
    		for (double t = 0; t <= 2*Math.PI; t += 0.3) {
    			Location haloLoc = haloCenter.clone().add(0.5 * Math.cos(t), 0, 0.5 * Math.sin(t));
                player.getWorld().spawnParticle(effect.getParticle(), haloLoc, 1, 0, 0, 0, 0);
           
            }
    		break;
    		
    	case RAIN:
    		if(step % 5 != 0) return;
    		
    		Location cloudHeight = player.getLocation().clone();
    		//Increase y slightly
    		cloudHeight.setY(cloudHeight.getY() + 2.5);
    		player.getWorld().spawnParticle(effect.getParticle(), cloudHeight, 10, 0.75, 0, 0.75, 1);

    		break;
    		
    	case HAIL:
    		if(step % 7 != 0) return;
    		
    		//Spawn snowball at random location 2 blocks above player's head
    		Location snowballLoc = player.getLocation().clone();
    		double rangeMin = -1.5;
    		double rangeMax = 1.5;
    		Random r = new Random();
    		double xOffset = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    		double zOffset = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    		snowballLoc.add(new Vector(xOffset, 4, zOffset));
    		Snowball snowball = (Snowball) player.getWorld().spawnEntity(snowballLoc, EntityType.SNOWBALL);
    		snowball.setMetadata("scos_hail", new FixedMetadataValue(plugin, true)); //Used to disable damage
    		snowball.setVelocity(new Vector(-0.06 * xOffset, -0.05, -0.06 * zOffset)); //Snowballs move towards player
    		
    		break;
    		
    	case MOVING_COMMAND_FILE:
    	case STATIONARY_COMMAND_FILE:
    		//if(step % 2 != 0) return; //Spawn particles every 2 ticks. Code runs for both moving and stationary types
    		spawnPlayerParticleImage(effect, step);
    		
    		break;
    		
    	}
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
	
	private void spawnPlayerParticleImage(AFKEffect effect, int step) {
		
		Particle particle = Particle.REDSTONE;
		Location playerClone = player.getLocation().clone();
		for(int i = 0; i < effect.getFiles().size(); ++i) {
	
		try {
			File file = effect.getFiles().get(i);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while((line = br.readLine()) != null) {
				String[] commandArgs = line.split(" ");
				if(!commandArgs[0].equalsIgnoreCase("particle")) continue;
				if(commandArgs.length < 15) continue;
				double red = Double.parseDouble(commandArgs[2]) * 255;
				double green = Double.parseDouble(commandArgs[3]) * 255;
				double blue = Double.parseDouble(commandArgs[4]) * 255;
				float size = Float.parseFloat(commandArgs[5]);
				Vector offset = getParticleOffsetFromCommandArgs(effect, step, commandArgs, i);
				
				Location particleLoc = playerClone.clone();
				particleLoc.setX(particleLoc.getX() + offset.getX());
				particleLoc.setY(particleLoc.getY() + offset.getY());
				particleLoc.setZ(particleLoc.getZ() + offset.getZ());
				
				int count = Integer.parseInt(commandArgs[12]);
				
				DustOptions options = new DustOptions(
						org.bukkit.Color.fromRGB((int) red, (int) green, (int) blue), size / 1.7F);
				particleLoc.getWorld().spawnParticle(particle, particleLoc, count, 0, 0, 0, options);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		
	}
	
	private Vector getParticleOffsetFromCommandArgs(AFKEffect effect, int step, String[] commandArgs, int fileNumber) {
		
		Vector yHat = new Vector(0, 1, 0);
		
		//Unit vector in xz plane where player is facing
		Vector pHat = player.getLocation().getDirection();
		pHat.setY(0);
		pHat.normalize();
		
		//Left right offset length
		double leftRight = Double.parseDouble(commandArgs[6].substring(1)); //Left right
		
		//Front back offset length
		double frontBack = Double.parseDouble(commandArgs[8].substring(1)); //Forward back
		
		//Up down offset length
		double yOff = Double.parseDouble(commandArgs[7].substring(1)); //Up down
		
		//Add custom offsets
		leftRight += effect.getLeftRightOffset();
		yOff += effect.getUpDownOffset();
		frontBack += effect.getFrontBackOffset();
		
		//Get frontBack and leftRight in terms of x z coordinates
		Vector frontBackXZ = pHat.clone().multiply(frontBack);
		Vector leftRightXZ = pHat.clone().crossProduct(yHat).multiply(leftRight);
		
		//Calculate final offset in xz
		Vector offset = new Vector(0, yOff, 0);
		offset.add(frontBackXZ);
		offset.add(leftRightXZ);
		
		//Rotate offset if effect is of type moving_command_file
		if(effect.getStyle().equals(AFKEffectStyle.MOVING_COMMAND_FILE)) {
			offset.rotateAroundAxis(new Vector(0, 1, 0), step * effect.getRotations().get(fileNumber));
		}
		
		return offset;
	}

	

}
