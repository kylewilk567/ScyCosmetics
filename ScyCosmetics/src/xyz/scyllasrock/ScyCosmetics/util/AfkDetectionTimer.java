package xyz.scyllasrock.ScyCosmetics.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.jet315.antiafkpro.AntiAFKProAPI;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.AFKParticleListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.AFKEffect;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class AfkDetectionTimer implements Runnable {
	
	private Main plugin = Main.getInstance();
	private PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	private AntiAFKProAPI afkAPI = plugin.getAFKApi();
	
	private int assignedTaskId;
	private static Map<UUID, AfkParticleTimer> afkParticleTracker = new HashMap<UUID, AfkParticleTimer>();
	
	@Override
	public void run() {		
		//For every player online, check if they are afk
		for(Player player : Bukkit.getOnlinePlayers()) {
			//Check if player is afk
			int afkSeconds;
			if(afkAPI == null) afkSeconds = AFKParticleListeners.getAfkSeconds(player.getUniqueId());
			else afkSeconds = afkAPI.getAFKPlayer(player).getSecondsAFK();
			
			//If player is afk (seconds > 60) and does not have afk particles, set afk particles
			PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
			if(afkSeconds > 60 || playerObject.isManuallyAFK()) {
				if(!afkParticleTracker.containsKey(player.getUniqueId())) {
					//Check if player has afkParticles enabled
					AFKEffect effect = (AFKEffect) playerObject.getActiveCosmetic(CosmeticType.AFK_EFFECT);
					if(effect != null) {
						AfkParticleTimer timer = new AfkParticleTimer(player, effect);
						Bukkit.getScheduler().runTaskLater(plugin, () -> {
							timer.scheduleTimer();
							afkParticleTracker.put(player.getUniqueId(), timer);
						}, 5L);

					}
				}
			}
			
			//If player is not afk and has afk particles, stop particles
			else {
				if(afkParticleTracker.containsKey(player.getUniqueId())) {
					afkParticleTracker.get(player.getUniqueId()).stopTimer();
					afkParticleTracker.remove(player.getUniqueId());
				}
			}

			
		}
		
	}
	
	
    /**
     * Schedules this instance to "run" every half second
     */
    public void scheduleTimer() {
        // Initialize our assigned task's id, for later use so we can cancel
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 10L);
    }
    
    /*
     * Stops the timer
     */
    public void stopTimer() {
    	Bukkit.getScheduler().cancelTask(this.assignedTaskId);
    }
    
    public static AfkParticleTimer getTracker(UUID uuid){
    	return afkParticleTracker.get(uuid);
    }

}
