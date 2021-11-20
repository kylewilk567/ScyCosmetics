package xyz.scyllasrock.ScyCosmetics.spigot;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics.Scycosmetics;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.CosmeticDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.data.DirtyDataTimer;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.ArrowTrailListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.CosInventoryListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.PlayerDataListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;

public class Main extends JavaPlugin {
	
	private HashMap<String, Cosmetic> cosmetics;
	DirtyDataTimer dirtyTimer;

	@Override
	public void onEnable() {
		
		//Set command executors
		Bukkit.getPluginCommand("scycosmetics").setExecutor(new Scycosmetics());
		
		//Set listeners
		Bukkit.getPluginManager().registerEvents(new PlayerDataListeners(), this);
		Bukkit.getPluginManager().registerEvents(new ArrowTrailListeners(), this);
		Bukkit.getPluginManager().registerEvents(new CosInventoryListeners(), this);
		
		//Set up files
		ConfigManager configMang = ConfigManager.getConfigMang();
		configMang.setupConfig();
		
		CosmeticDataHandler cosmeticHandler = CosmeticDataHandler.getCosmeticHandler();

		// initialize cosmetics
		cosmetics = cosmeticHandler.initializeCosmetics();
		
		// initialize playerobjects of currently online players
		PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
		playerHandler.initializePlayerObjects();
		
		
		//Start dirty data timer
		dirtyTimer = new DirtyDataTimer();
		dirtyTimer.scheduleTimer();
		
	}

	@Override
	public void onDisable() {
		
		//Stop dirty data timer
		dirtyTimer.stopTimer();
	}

	
	public Cosmetic getCosmeticFromId(String id) {
		if(cosmetics.containsKey(id)) return cosmetics.get(id);
		return null;
	}
	
	public HashMap<String, Cosmetic> getCosmetics(){
		return cosmetics;
	}
	
}
