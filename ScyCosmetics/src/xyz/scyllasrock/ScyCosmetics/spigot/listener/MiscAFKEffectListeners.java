package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.scyllasrock.ScyCosmetics.util.AfkDetectionTimer;
import xyz.scyllasrock.ScyCosmetics.util.AfkParticleTimer;

public class MiscAFKEffectListeners implements Listener {
		
	@EventHandler
	public void onSnowballHit(EntityDamageByEntityEvent event) {
		if(event.getCause().equals(DamageCause.PROJECTILE)) {
			if(event.getDamager() instanceof Snowball) {
				Snowball damager = (Snowball) event.getDamager();
				if(damager.hasMetadata("scos_hail")) {
					event.setCancelled(true); //Cancel snowball damage if from hail afk effect
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		AfkParticleTimer timer = AfkDetectionTimer.getTracker(event.getPlayer().getUniqueId());
		if(timer != null) timer.stopTimer();
	}

}
