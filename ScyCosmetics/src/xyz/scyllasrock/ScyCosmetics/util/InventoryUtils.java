package xyz.scyllasrock.ScyCosmetics.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;

public class InventoryUtils {
	
	static ConfigManager configMang = ConfigManager.getConfigMang();
	
	
	public static Inventory getInventoryFromConfigPath(String path) {
		YamlConfiguration config = configMang.getConfig();
		Inventory inv = Bukkit.createInventory(null, config.getInt(path + ".size"), ChatColor.translateAlternateColorCodes('&', 
				config.getString(path + ".title")));
		for(String item : config.getConfigurationSection(path + ".items").getKeys(false)) {
			//Check slot first. If no slot existent, skip item and output error to console
			if(config.getString(path + ".items." + item + ".slot") == null || config.getInt(path + ".items." + item + ".slot") >= config.getInt(path + ".size")) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Config Error! Problem with slot specified for the item " + item + " in ScyCosmetics config.yml!");
				continue;
			}
			
			//Set itemstack
			ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK);
			if(config.getString(path + ".items." + item + ".material") != null) {
				String mat = config.getString(path + ".items." + item + ".material").strip();
				if(mat.startsWith("head;")) {
					itemStack = ItemUtils.getHead(mat.split("head;")[1]);
				}
				else itemStack.setType(Material.valueOf(config.getString(path + ".items." + item + ".material")));
			}
			
			//Get item meta after setting itemstack has head or other material
			ItemMeta meta = itemStack.getItemMeta();

			if(config.getInt(path + ".items." + item + ".amount") != 0) {
				itemStack.setAmount(config.getInt(path + ".items." + item + ".amount"));
			}
			if(config.getString(path + ".items." + item + ".display_name") != null) {
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(path + ".items." + item + ".display_name")));
			}
			if(config.getStringList(path + ".items." + item + ".lore") != null) {
				List<String> lore = new ArrayList<String>();
				for(String line : config.getStringList(path + ".items." + item + ".lore")) {
					lore.add(ChatColor.translateAlternateColorCodes('&', line));
				}
				meta.setLore(lore);
			}
			
			itemStack.setItemMeta(meta);
			inv.setItem(config.getInt(path + ".items." + item + ".slot"), itemStack);
		}
		
		
		return inv;
	}

}
