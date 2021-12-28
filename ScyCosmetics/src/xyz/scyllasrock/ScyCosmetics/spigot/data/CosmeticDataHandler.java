package xyz.scyllasrock.ScyCosmetics.spigot.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.ArrowTrail;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticTier;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.LastWords;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerTrail;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Prefix;
import xyz.scyllasrock.ScyCosmetics.util.ItemUtils;

public class CosmeticDataHandler {
	
	Main plugin = Main.getPlugin(Main.class);
	
	private static CosmeticDataHandler cosmeticHandler;
	
	public CosmeticDataHandler() {
		checkOrCreateDirectories("Cosmetics", "Cosmetics" + File.separator + "Emotes");
		checkOrCreateFiles("last_words.yml", "arrow_trails.yml", "player_trails.yml", "prefixes.yml");
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
			if(newFile.exists()) continue;
			try {
				OutputStream ostream = new FileOutputStream(newFile);
				istream.transferTo(ostream);
//				IOUtils.copy(istream, ostream); //Removed in spigot 1.18
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
			ItemStack itemStack = this.getItemStackFromConfigSectionAndKey(trailConfig, "particles", id);
			cosmetics.put(id, (Cosmetic) new ArrowTrail(id, CosmeticTier.valueOf(trailConfig.getString("particles." + id + ".tier")), 
					Particle.valueOf(trailConfig.getString("particles." + id + ".particle")), itemStack));
		}
			
		//Initialize last words
		YamlConfiguration lastWordsConfig = getConfigFile("Cosmetics" + File.separator + "last_words.yml");
		for(String id : lastWordsConfig.getConfigurationSection("last_words").getKeys(false)) {
			ItemStack itemStack = getItemStackFromConfigSectionAndKey(lastWordsConfig, "last_words", id);
			String message = lastWordsConfig.getString("last_words." + id + ".text");
			if(message == null) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERROR: Message for last words with id " + id + " is null!");
			}
			else {
			cosmetics.put(id, (Cosmetic) new LastWords(id, CosmeticTier.valueOf(lastWordsConfig.getString("last_words." + id + ".tier")), 
					 itemStack, ChatColor.translateAlternateColorCodes('&', message)));
			}
		}
		
		//Initialize player trails
		YamlConfiguration playerTrailConfig = getConfigFile("Cosmetics" + File.separator + "player_trails.yml");
		for(String id : playerTrailConfig.getConfigurationSection("trails").getKeys(false)) {
			ItemStack itemStack = getItemStackFromConfigSectionAndKey(playerTrailConfig, "trails", id);
			int count = playerTrailConfig.getInt("trails." + id + ".count");
			double offsetX = playerTrailConfig.getDouble("trails." + id + ".offsets.x");
			double offsetY = playerTrailConfig.getDouble("trails." + id + ".offsets.y");
			double offsetZ = playerTrailConfig.getDouble("trails." + id + ".offsets.z");
			List<Particle> particles = new ArrayList<Particle>();
			for(String particle : playerTrailConfig.getStringList("trails." + id + ".particles")) {
				particles.add(Particle.valueOf(particle));
			}
			if(particles.isEmpty()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERROR: No particles for player trail with id " + id + "!");
			}
			else {
			cosmetics.put(id, (Cosmetic) new PlayerTrail(id, CosmeticTier.valueOf(playerTrailConfig.getString("trails." + id + ".tier")), 
					 itemStack, particles, count, offsetX, offsetY, offsetZ));
			}
		}
		
		//Initialize prefixes
		YamlConfiguration prefixConfig = getConfigFile("Cosmetics" + File.separator + "prefixes.yml");
		for(String id : prefixConfig.getConfigurationSection("prefixes").getKeys(false)) {
			ItemStack itemStack = getItemStackFromConfigSectionAndKey(prefixConfig, "prefixes", id);
			String translatedPrefix = StringEscapeUtils.unescapeJava(prefixConfig.getString("prefixes." + id + ".prefix"));
			if(translatedPrefix == null) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERROR: No prefix for prefix with id " + id + "!");
			}
			else {
			cosmetics.put(id, (Cosmetic) new Prefix(id, CosmeticTier.valueOf(prefixConfig.getString("prefixes." + id + ".tier")), 
					 itemStack, translatedPrefix, prefixConfig.getStringList("prefixes." + id + ".color_codes"),
					 prefixConfig.getInt("prefixes." + id + ".color_change_ticks")));
			}
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
	
	
	
	private ItemStack getItemStackFromConfigSectionAndKey(YamlConfiguration config, String configSection, String key) {
		ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK);
		if(config.getString(configSection + "." + key + ".display_item") != null) {
			String mat = config.getString(configSection + "." + key + ".display_item").strip();
			if(mat.startsWith("head;")) {
				itemStack = ItemUtils.getHead(mat.split("head;")[1]);
			}
			else itemStack.setType(Material.valueOf(config.getString(configSection + "." + key + ".display_item")));
		}
		ItemMeta meta = itemStack.getItemMeta();
		List<String> unTranslatedLore = config.getStringList(configSection + "." + key + ".display_lore");
		List<String> lore = new ArrayList<String>();
		for(String s : unTranslatedLore) {
			lore.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(configSection + "." + key + ".display_name")));
		meta.setLore(lore);
		//Add custom persistent data if exists
		if(config.getString(configSection + "." + key + ".custom_data") != null) {
		PersistentDataContainer data = meta.getPersistentDataContainer();
		data.set(new NamespacedKey(plugin, "ScyCos_data"), PersistentDataType.STRING, config.getString(configSection + "." + key + ".custom_data"));
		}
		//Remove attributes from showing
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		itemStack.setItemMeta(meta);
		return itemStack;
	}
	

}
