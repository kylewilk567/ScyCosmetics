package xyz.scyllasrock.ScyCosmetics.util;

import java.util.UUID;

import org.bukkit.Bukkit;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Prefix;

public class PrefixColorTimer implements Runnable {
	
	Main plugin = Main.getInstance();
	ConfigManager configMang = ConfigManager.getConfigMang();
	
	private int assignedTaskId;
	private UUID uuid;	
	private Prefix prefix;
	
	public PrefixColorTimer(UUID uuid, Prefix prefix) {
		this.uuid = uuid;
		try {
			this.prefix = prefix.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {		
		//each run, increase color index by 1 until end of list reached
		prefix.stepCurrentCodeIndex();	
		
	}
	
	
    /**
     * Schedules this instance to "run" every colorChangeTicks
     */
    public void scheduleTimer() {
        // Initialize our assigned task's id, for later use so we can cancel
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, prefix.getColorChangeTicks());
    }
    
    /*
     * Stops the timer
     */
    public void stopTimer() {
    	Bukkit.getScheduler().cancelTask(this.assignedTaskId);
    }
    
    
    public UUID getUUID() {
    	return uuid;
    }
    
    public Prefix getPrefix() {
    	return prefix;
    }

}
