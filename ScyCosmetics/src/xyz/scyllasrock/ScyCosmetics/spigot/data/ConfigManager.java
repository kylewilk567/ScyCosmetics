package xyz.scyllasrock.ScyCosmetics.spigot.data;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;


public class ConfigManager {
	
	private Main plugin = Main.getPlugin(Main.class);
	File configFile;
	FileConfiguration config;
	
	private static ConfigManager configMang;
	
	
	/*
	 * gets the instance of ConfigManager used in this plugin
	 */
	public static ConfigManager getConfigMang() {
		if(configMang == null) {
			configMang = new ConfigManager();
		}
		return configMang;
	}
	
	
	/*
	 * create file if it does not exist and load file
	 */
	public void setupConfig() {
	 configFile = new File(plugin.getDataFolder(), "config.yml");
	
	if(!configFile.exists()) {
			plugin.saveResource("config.yml", false);
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bScyCosmetics >> &3config.yml has been created."));
	}
	
	config = YamlConfiguration.loadConfiguration(configFile);
	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bScyCosmetics >> &3config.yml has been loaded."));
	
}
	
	/*
	 * get config file
	 */
	public YamlConfiguration getConfig() {
		configFile = new File(plugin.getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
		return (YamlConfiguration) config;
	}
	
	/*
	 * Reload config file
	 */
	public void reloadConfig() {
		configFile = new File(plugin.getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bScyCosmetics >> &3config.yml has been reloaded successfully."));
	}
	
	public String getPermission(String permID) {
		return this.getConfig().getString("permissions." + permID);
	}
	
	public String getMessage(String messageID) {
		return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.prefix") + " " + this.getConfig().getString("messages." + messageID));
	}
	
	public String getMessageNoColor(String messageID) {
		return this.getConfig().getString("messages.prefix") + " " + this.getConfig().getString("messages." + messageID);
	}
	


}
