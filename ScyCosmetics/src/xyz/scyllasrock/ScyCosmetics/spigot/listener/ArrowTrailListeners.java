package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.ArrowTrail;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class ArrowTrailListeners implements Listener {
	
	Main plugin = Main.getPlugin(Main.class);
	PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	@EventHandler
	public void onShoot(EntityShootBowEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(!(event.getProjectile() instanceof Arrow)) return;
		
		//TO BE REMOVED
		Player player = (Player) event.getEntity();
		if(!player.getName().contains("Super")) return;
		
		//Check if player has an arrow trail active
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		ArrowTrail trail = (ArrowTrail) playerObject.getActiveCosmetic(CosmeticType.ARROW_TRAIL);
		if(playerObject.getActiveCosmetic(CosmeticType.ARROW_TRAIL) == null) return;
		final Particle particle = trail.getParticle();
		
		Arrow arrow = (Arrow) event.getProjectile();
		arrow.setCritical(false); //Remove crit particles if cosmetic enabled

		new BukkitRunnable() {

			@Override
			public void run() {
		        if(arrow.isDead() || arrow.isOnGround()) {
		            this.cancel();
		        } else {
		            for(int offset=0; offset<4; ++offset) {
		                Location location = arrow.getLocation().clone();
		                location.setX(location.getX() + arrow.getVelocity().getX() * (double) offset / 4.0D);
		                location.setY(location.getY() + arrow.getVelocity().getY() * (double) offset / 4.0D);
		                location.setZ(location.getZ() + arrow.getVelocity().getZ() * (double) offset / 4.0D);
		                // Range was removed in 1.13 for particles?
		                arrow.getWorld().spawnParticle(particle, location, 1,
		                        0, 0, 0,
		                        0);
		            }
		        }
				
			}
			
		}.runTaskTimer(plugin, 0L, 2L);
		
		
		

	}

}
