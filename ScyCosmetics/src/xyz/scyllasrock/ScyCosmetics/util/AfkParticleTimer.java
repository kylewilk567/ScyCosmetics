package xyz.scyllasrock.ScyCosmetics.util;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.AFKEffect;

public class AfkParticleTimer implements Runnable {
	
	
	Main plugin = Main.getInstance();
	ConfigManager configMang = ConfigManager.getConfigMang();
	
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
        // Initialize our assigned task's id, for later use so we can cancel
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, 1L);
    }
    
    /*
     * Stops the timer
     */
    public void stopTimer() {
    	task.cancel();
    }
    
    private void playEffect() {
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
                player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 1, 0, 0, 0, 0);
           
            }
    		
    		break;
    		
    	case SMALL_PULSE:
    		if(step % 20 != 0) return;
    		
    		Location centerLoc2 = player.getLocation().clone();
    		//Increase y slightly
    		centerLoc2.setY(centerLoc2.getY() + 0.05);
    		
    		//Spawn particles in circle at radius
    		for (double t = 0; t <= 2*Math.PI; t += 0.3) {
                player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, centerLoc2, 0, Math.cos(t), 0, Math.sin(t), 0.1);
           
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
    		player.getWorld().spawnParticle(Particle.END_ROD, twin1, 1, 0, 0, 0, 0);
    		player.getWorld().spawnParticle(Particle.END_ROD, twin2, 1, 0, 0, 0, 0);
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
                player.getWorld().spawnParticle(Particle.FLAME, haloLoc, 1, 0, 0, 0, 0);
           
            }
    		break;
    		
    	case RAIN:
    		if(step % 5 != 0) return;
    		
    		Location cloudHeight = player.getLocation().clone();
    		//Increase y slightly
    		cloudHeight.setY(cloudHeight.getY() + 2.5);
    		player.getWorld().spawnParticle(Particle.DRIP_WATER, cloudHeight, 10, 0.75, 0, 0.75, 1);

    		break;
    		
    	}
    }

}
