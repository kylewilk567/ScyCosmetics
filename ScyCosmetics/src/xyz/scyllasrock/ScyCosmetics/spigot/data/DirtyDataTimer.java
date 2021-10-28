package xyz.scyllasrock.ScyCosmetics.spigot.data;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.DirtyDataType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;


public class DirtyDataTimer implements Runnable {
	
	Main plugin = Main.getPlugin(Main.class);
	ConfigManager configMang = ConfigManager.getConfigMang();
	PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	private BukkitTask assignedTask;
	private int secondsBetweenRuns = configMang.getConfig().getInt("datastorage.dirty_seconds");
	
	
	
/*
 * Dirty timer
 */
	@Override
	public void run() {
		Long start_time = System.currentTimeMillis();
		this.saveDirtyData();
		Bukkit.getConsoleSender().sendMessage("ScyCosmetics db save time: " + (System.currentTimeMillis() - start_time) / 1000.0 + " seconds");
		
	}
	
	
    /**
     * Schedules this instance to "run" every secondsBetweenRuns
     */
    public void scheduleTimer() {
        // Initialize our assigned task's id, for later use so we can cancel
        this.assignedTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, Long.valueOf(20 * secondsBetweenRuns));
    }
    
    /*
     * Stops the timer
     */
    public void stopTimer() {
    	this.assignedTask.cancel();
    	//Save data one more time
    	saveDirtyData();
    }
    
    /*
     * Saves all data to the correct file locations
     */
    private void saveDirtyData() {
    	
    	for(PlayerObject playerObject : PlayerDataHandler.getPlayerObjects().values()) {
    		for(DirtyDataType type : playerObject.getDirtyData()) {
    			switch(type) {
    			
    			case ACTIVE_COSMETICS:
    				playerHandler.updateActiveCosmetics(playerObject.getUUID());
    				
    			case UNLOCKED_COSMETICS:
    				playerHandler.updateUnlockedCosmetics(playerObject.getUUID());
    			case LAST_LOGIN:
    				
    			
    			
    			}
    		}
    	}
    	
    }
    

}
