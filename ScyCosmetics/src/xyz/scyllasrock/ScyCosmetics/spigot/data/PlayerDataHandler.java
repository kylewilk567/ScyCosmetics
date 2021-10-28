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
	 * gets the instance of CosmeticDataHandler used in this plugin
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
		File f = new File(plugin.getDataFolder() + File.separator + "Player_data" + File.separator + uuid.toString());
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}
	
	private boolean playerFileExists(UUID uuid) {
		File f = new File(plugin.getDataFolder() + File.separator + "Player_data" + File.separator + uuid.toString());
		if(f.exists()) return true;
		return false;
	}
	
	public void initializePlayerObjects() {
		//Initialize using only players who are already online. Rest init on PlayerJoinEvent
		for(Player player : Bukkit.getOnlinePlayers()) {
			File playerFile = new File(plugin.getDataFolder() + File.separator + "Player_data" + File.separator + player.getUniqueId().toString());
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			playerObjects.put(UUID.fromString(config.getString("uuid")),
					new PlayerObject(player.getUniqueId(), config.getStringList("unlocked_cosmetics"), config.getStringList("active_cosmetics")));
		}
	}
	
	
	/**
	 * Returns PlayerObject corresponding to uuid. Returns null if no PlayerObject found.
	 * @param uuid
	 * @return
	 */
	public PlayerObject getPlayerObjectByUUID(UUID uuid) {
		if(playerObjects.containsKey(uuid))	return playerObjects.get(uuid);
		return null;
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
			File playerFile = new File(plugin.getDataFolder() + File.separator + "Player_data" + File.separator + uuid.toString());
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			playerObjects.put(uuid,
					new PlayerObject(uuid, config.getStringList("unlocked_cosmetics"), config.getStringList("active_cosmetics")));
		}
		else playerObjects.put(uuid, new PlayerObject(uuid, new ArrayList<String>(), new ArrayList<String>()));
	}
	
	public void updateUnlockedCosmetics(UUID uuid) {
		File f = createPlayerDataFile(uuid); //Create file if not exists
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		config.set("unlocked_cosmetics", playerObjects.get(uuid).getUnlockedCosmetics());
		try {
			config.save(f);
		} catch (IOException e) {}
	}
	
	public void updateActiveCosmetics(UUID uuid) {
		File f = createPlayerDataFile(uuid); //Create file if not exists
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		config.set("active_cosmetics", playerObjects.get(uuid).getActiveCosmetics());
		try {
			config.save(f);
		} catch (IOException e) {}
	}
	
	
	public PlayerObject getPlayerObjectFromUUID(UUID uuid) {
		return playerObjects.get(uuid);
	}

}