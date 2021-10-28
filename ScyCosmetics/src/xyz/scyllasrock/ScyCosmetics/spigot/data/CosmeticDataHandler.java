package xyz.scyllasrock.ScyCosmetics.spigot.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;
import org.bukkit.inventory.ItemStack;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.ArrowTrail;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;

public class CosmeticDataHandler {
	
	Main plugin = Main.getPlugin(Main.class);
	
	private static CosmeticDataHandler cosmeticHandler;
	
	public CosmeticDataHandler() {
		checkOrCreateDirectories("Cosmetics", "Cosmetics" + File.separator + "Emotes");
		checkOrCreateFiles("last_words.yml", "arrow_trails.yml");
	}




	/*
	 * gets the instance of CosmeticDataHandler used in this plugin
	 */
	public static CosmeticDataHandler getCosmeticHandler() {
		if(cosmeticHandler == null) {
			cosmeticHandler = new CosmeticDataHandler();
		}
		return cosmeticHandler;
	}
	
	/**
	 * Creates all directories in plugin folder
	 * @param files
	 */
	private void checkOrCreateDirectories(String... paths) {
		for(String s : paths) {
			File newFile = new File(plugin.getDataFolder() + File.separator + s);
			if(!newFile.exists()) {
				newFile.mkdirs();
			}
		}
	}
	
	/**
	 * Creates all files in Cosmetics folder - MODIFY TO ALLOW A CHANGE OF DIRECTORY!
	 * @param files
	 */
	private void checkOrCreateFiles(String... files) {
		for(String s : files) {
			
			InputStream istream = plugin.getResource(s);
			File newFile = new File(plugin.getDataFolder() + File.separator + "Cosmetics" +  File.separator + s);
			
			try {
				OutputStream ostream = new FileOutputStream(newFile);
				IOUtils.copy(istream, ostream);
				ostream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Initializes all cosmetics in this plugin by reading from config files
	 */
	public HashMap<String, Cosmetic> initializeCosmetics() {
		HashMap<String, Cosmetic> cosmetics = new HashMap<String, Cosmetic>();
		//Initialize arrow trails
		YamlConfiguration trailConfig = getConfigFile("Cosmetics" + File.separator + "arrow_trails.yml");
		for(String id : trailConfig.getConfigurationSection("particles").getKeys(false)) {
			cosmetics.put(id, (Cosmetic) new ArrowTrail(id, Particle.valueOf(trailConfig.getString("particles." + id + ".particle")),
					trailConfig.getString("particles." + id + ".display_name"),
					new ItemStack(Material.valueOf(trailConfig.getString("particles." + id + ".display_item"))),
					trailConfig.getStringList("particles." + id + ".display_lore")));
		}
		
		return cosmetics;
	}
	
	/**
	 * Returns yamlconfiguration of specified file
	 * @param path
	 * @return
	 */
	public YamlConfiguration getConfigFile(String path) {
		File configFile = new File(plugin.getDataFolder() + File.separator + path);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		return config;
	}
	
	

}
