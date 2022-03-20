package xyz.scyllasrock.ScyCosmetics.spigot.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticTier;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.ItemFilter;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.ItemSort;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class PlayerDataHandler {
	
	Main plugin = Main.getPlugin(Main.class);
	private static HashMap<UUID, PlayerObject> playerObjects = new HashMap<UUID, PlayerObject>();
	
	
	private static PlayerDataHandler playerHandler;
	
	public PlayerDataHandler() {
		//Set up player_data directory if not exists
		checkOrCreatePlayerDir();
	}
	
	
	/*
	 * gets the instance of PlayerDataHandler used in this plugin
	 */
	public static PlayerDataHandler getPlayerHandler() {
		if(playerHandler == null) {
			playerHandler = new PlayerDataHandler();
		}
		return playerHandler;
	}
	
	/**
	 * Creates all directories in plugin folder
	 * @param files
	 */
	private void checkOrCreatePlayerDir() {
			File newFile = new File(plugin.getDataFolder() + File.separator + "Player_data");
			if(!newFile.exists()) {
				newFile.mkdir();
			}
	}
	
	private File createPlayerDataFile(UUID uuid) {
		File f = new File(plugin.getDataFolder() + File.separator + "Player_data" + File.separator + uuid.toString() + ".yml");
		try {
			if(f.createNewFile()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
			config.set("cosmetic_filter", "SHOW_ALL");
			config.set("cosmetic_sort", "NAME");
			config.save(f);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}
	
	private boolean playerFileExists(UUID uuid) {
		File f = new File(plugin.getDataFolder() + File.separator + "Player_data" + File.separator + uuid.toString() + ".yml");
		if(f.exists()) return true;
		return false;
	}
	
	public void initializePlayerObjects() {
		//Initialize using only players who are already online. Rest init on PlayerJoinEvent
		for(Player player : Bukkit.getOnlinePlayers()) {
			addPlayerObject(player.getUniqueId());
		}
	}
	
	
	/**
	 * Returns PlayerObject corresponding to uuid. If none exists, creates a new object
	 * @param uuid
	 * @return
	 */
	public PlayerObject getPlayerObjectByUUID(UUID uuid) {
		if(!playerObjects.containsKey(uuid)) addPlayerObject(uuid);
		return playerObjects.get(uuid);
	}
	
	public static HashMap<UUID, PlayerObject> getPlayerObjects(){
		return playerObjects;
	}
	
	/**
	 * Adds playerObject from file info if file exists. Else creates new playerObject
	 * @param uuid
	 */
	public void addPlayerObject(UUID uuid) {
		if(playerFileExists(uuid)) {
			File playerFile = createPlayerDataFile(uuid);
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			playerObjects.put(uuid,
					new PlayerObject(uuid, ItemFilter.valueOf(config.getString("cosmetic_filter")), ItemSort.valueOf(config.getString("cosmetic_sort")),
							CosmeticTier.valueOf(config.getString("rarity_filter_tier")),
							config.getStringList("unlocked_cosmetics"), config.getStringList("active_cosmetics")));
		}
		else playerObjects.put(uuid, new PlayerObject(uuid, ItemFilter.SHOW_ALL, ItemSort.NAME,
				CosmeticTier.COMMON,
				new ArrayList<String>(), new ArrayList<String>()));
	}
	
	
	public void updateUnlockedCosmetics(UUID uuid) {
		File f = createPlayerDataFile(uuid); //Create file if not exists
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		config.set("unlocked_cosmetics", playerObjects.get(uuid).getUnlockedCosmetics());
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void updateActiveCosmetics(UUID uuid) {
		File f = createPlayerDataFile(uuid); //Create file if not exists
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		config.set("active_cosmetics", playerObjects.get(uuid).getActiveCosmetics());
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void updateRarityFilterTier(UUID uuid) {
		File f = createPlayerDataFile(uuid); //Create file if not exists
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		config.set("rarity_filter_tier", playerObjects.get(uuid).getRarityFilterTier().toString());
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateItemFilter(UUID uuid) {
		File f = createPlayerDataFile(uuid); //Create file if not exists
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		config.set("cosmetic_filter", playerObjects.get(uuid).getItemFilter().toString());
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateItemSort(UUID uuid) {
		File f = createPlayerDataFile(uuid); //Create file if not exists
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		config.set("cosmetic_sort", playerObjects.get(uuid).getItemSort().toString());
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
