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
import xyz.scyllasrock.ScyCosmetics.spigot.listener.LastWordsListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.PlayerDataListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.PlayerTrailListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;

public class Main extends JavaPlugin {
	
	private HashMap<String, Cosmetic> cosmetics;
	DirtyDataTimer dirtyTimer;
	private static Main instance;
	private boolean premiumVanishSupportEnabled = false;

	@Override
	public void onEnable() {
		
		instance = this;
		
		//Load dependencies
		
		//PremiumVanish
		if(Bukkit.getPluginManager().getPlugin("PremiumVanish") != null) {
			premiumVanishSupportEnabled = true;
		}
		
		
		//Set up files
		ConfigManager configMang = ConfigManager.getConfigMang();
		configMang.setupConfig();
		
		// initialize cosmetics
		CosmeticDataHandler cosmeticHandler = CosmeticDataHandler.getCosmeticHandler();
		cosmetics = cosmeticHandler.initializeCosmetics();
		
		// initialize playerobjects of currently online players
		PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
		playerHandler.initializePlayerObjects();
		
		//Set command executors
		Bukkit.getPluginCommand("scycosmetics").setExecutor(new Scycosmetics());
		
		//Set listeners
		Bukkit.getPluginManager().registerEvents(new PlayerDataListeners(), this);
		Bukkit.getPluginManager().registerEvents(new ArrowTrailListeners(), this);
		Bukkit.getPluginManager().registerEvents(new CosInventoryListeners(), this);
		Bukkit.getPluginManager().registerEvents(new LastWordsListeners(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerTrailListeners(), this);
		
		//Start dirty data timer
		dirtyTimer = new DirtyDataTimer();
		dirtyTimer.scheduleTimer();
		
	}

	@Override
	public void onDisable() {
		
		//Stop dirty data timer
		dirtyTimer.stopTimer();
	}

	public static Main getInstance() {
		return instance;
	}
	
	public boolean premVanishEnabled() {
		return premiumVanishSupportEnabled;
	}
	
	public Cosmetic getCosmeticFromId(String id) {
		return cosmetics.get(id);
	}
	
	public HashMap<String, Cosmetic> getCosmetics(){
		return cosmetics;
	}
	
}
