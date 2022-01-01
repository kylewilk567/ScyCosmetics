package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.scyllasrock.ScyUtility.objects.Pair;

public class AFKParticleListeners implements Listener {
		
	private static Map<UUID, Pair<Float, Long>> afkTracker = new HashMap<UUID, Pair<Float, Long>>();	
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if(!afkTracker.containsKey(player.getUniqueId())) return;
		
		//Check if player's pitch has changed and update time
		if(Math.abs(player.getLocation().getPitch() - afkTracker.get(player.getUniqueId()).getFirst()) > 1) {
			afkTracker.put(player.getUniqueId(), new Pair<Float,Long>(player.getLocation().getPitch(), System.currentTimeMillis()));
		}
		
	}
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		//Add player the afkTracker map with current time and pitch
		Player player = event.getPlayer();
		afkTracker.put(player.getUniqueId(), new Pair<Float, Long>(player.getLocation().getPitch(), System.currentTimeMillis()));
	}
	
	
	public static int getAfkSeconds(UUID uuid){
		if(!afkTracker.containsKey(uuid)) return 0;
		
		else return (int) ((System.currentTimeMillis() - afkTracker.get(uuid).getSecond()) / 1000);
	}

}
