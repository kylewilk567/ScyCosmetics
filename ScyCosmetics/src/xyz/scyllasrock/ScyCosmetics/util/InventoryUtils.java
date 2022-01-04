package xyz.scyllasrock.ScyCosmetics.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;

public class InventoryUtils {
	
	static Main plugin = Main.getInstance();
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
			
			//Hide attributes and potion effects
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			itemStack.setItemMeta(meta);
			inv.setItem(config.getInt(path + ".items." + item + ".slot"), itemStack);
		}
		
		
		return inv;
	}
	
	
	public static Inventory getBaseCosInventory(PlayerObject playerObject) {
		
		String path = "inventory_settings.base_inventory";
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
			List<String> lore = new ArrayList<String>();
			if(config.getStringList(path + ".items." + item + ".lore") != null) {
				for(String line : config.getStringList(path + ".items." + item + ".lore")) {
					lore.add(ChatColor.translateAlternateColorCodes('&', line));
				}
			}
			
			//Add meta data to identify cosmetic type from item - not for info item though
			if(!item.equalsIgnoreCase("info")) {
			PersistentDataContainer itemData = meta.getPersistentDataContainer();
			itemData.set(new NamespacedKey(plugin, "ScyCos_Type"), PersistentDataType.STRING, item.toUpperCase());
			
			//Modify items for active cosmetics if necessary - add active display name to lore
			//the string item is the same as the name of the cosmetic type
				if(playerObject.hasActiveCosmeticType(CosmeticType.valueOf(item.toUpperCase()))) {
						Cosmetic cos = playerObject.getActiveCosmetic(CosmeticType.valueOf(item.toUpperCase()));
							lore.add(ChatColor.translateAlternateColorCodes('&',
									"&eCurrently active: " + cos.getDisplayItem().getItemMeta().getDisplayName()));
							lore.add(ChatColor.translateAlternateColorCodes('&',
									"&cShift click to disable"));
					}
			}
			//Info item lore
			else {
				//Add unlocked out of total info here
				int numUnlocked = playerObject.getUnlockedCosmetics().size();
				int totalCos = plugin.getCosmetics().size();
				lore.add(ChatColor.translateAlternateColorCodes('&', "&eCosmetics unlocked: &a" + numUnlocked + "&e/&a" + totalCos));
			}

			meta.setLore(lore);

			//Hide attributes and potion effects
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			itemStack.setItemMeta(meta);
			
			//Add enchantments to item if necessary - must be done after meta is set
			if(!item.equalsIgnoreCase("info") && playerObject.hasActiveCosmeticType(CosmeticType.valueOf(item.toUpperCase()))) {
				itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			}

			inv.setItem(config.getInt(path + ".items." + item + ".slot"), itemStack);

		}
		//Add glass panes
		int[] purple_panes = new int[] {0, 1, 2, 3, 5, 6, 7, 8, 10, 16, 28, 34, 36, 37, 38, 39, 41, 42, 43, 44};
		int[] black_panes = new int[] {45, 46, 47, 48, 50, 51, 52, 53};
		int[] orange_panes = new int[] {4, 12, 14, 18, 19, 20, 24, 25, 26, 30, 32, 40};
		int[] yellow_panes = new int[] {13, 21, 23, 31};
		ItemStack pane = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
		ItemMeta meta = pane.getItemMeta();
		meta.setDisplayName(" ");
		pane.setItemMeta(meta);
		
		setPanes(inv, pane, purple_panes);
		
		pane.setType(Material.BLACK_STAINED_GLASS_PANE);
		setPanes(inv, pane, black_panes);
		
		pane.setType(Material.ORANGE_STAINED_GLASS_PANE);
		setPanes(inv, pane, orange_panes);
		
		pane.setType(Material.YELLOW_STAINED_GLASS_PANE);
		setPanes(inv, pane, yellow_panes);
		
		return inv;
	}
	
	public static Inventory getEmoteDanceInv(Player player, PlayerObject playerObject) {
		//Values to initialize
		ItemStack infoItem, showLockedItem, filterItem;
		List<Cosmetic> sortedCosmetics = new ArrayList<Cosmetic>();
		//Filter items based on player's filter specifications
		sortedCosmetics = CosmeticUtils.sortCosmetics(playerObject, CosmeticType.EMOTE_DANCE);		

		//Initialize lockedItemStack - locked
		if(playerObject.showLockedCosmetics()) {
			showLockedItem = new ItemStack(Material.REDSTONE);
			ItemMeta lockedMeta = showLockedItem.getItemMeta();
			lockedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aHide &cLOCKED &aitems."));
			showLockedItem.setItemMeta(lockedMeta);
		}
	
		//Initialize lockedItemStack - unlocked
		else {
			showLockedItem = new ItemStack(Material.REDSTONE);
			ItemMeta lockedMeta = showLockedItem.getItemMeta();
			lockedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aShow &cLOCKED &aitems."));
			showLockedItem.setItemMeta(lockedMeta);			
		}
		
		//Initialize Info Item
		infoItem = new ItemStack(Material.BEACON);
		ItemMeta infoMeta = infoItem.getItemMeta();
		infoMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bEmote Info:"));
		//Find emotes unlocked
		int emotesUnlocked = 0;
		int totalEmotes = 0;
		for(Cosmetic cos : plugin.getCosmetics().values()) {
			if(cos.getType().equals(CosmeticType.EMOTE_DANCE)) {
				++totalEmotes;
				if(playerObject.getUnlockedCosmetics().contains(cos.getId())) ++emotesUnlocked;
			}
		}
		//lore
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eEmotes unlocked: &a" + emotesUnlocked + "&e/&a" + totalEmotes));
		infoMeta.setLore(lore);
		infoItem.setItemMeta(infoMeta);
		
		//Initialize filterItem
		filterItem = new ItemStack(Material.GOLD_INGOT);
		ItemMeta filterMeta = filterItem.getItemMeta();
		filterMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eCurrent filter method: &6" + playerObject.getItemFilter().toString()));
		List<String> filterLore = new ArrayList<String>();
		filterLore.add(ChatColor.translateAlternateColorCodes('&', "&5Click to toggle method"));
		filterMeta.setLore(filterLore);
		filterItem.setItemMeta(filterMeta);
		
		//Find size of inventory
		int size = 54;
		
		//Create inventory
		Inventory inv = Bukkit.createInventory(null, size,
				ChatColor.translateAlternateColorCodes('&', player.getName() + "'s " + CosmeticType.EMOTE_DANCE.label));
		
		
		//Add the first cosmetics, no more than 28. Locked cosmetics replaced with redstone block
		int count = 0;
		int slot = 10;
		for(Cosmetic cos : sortedCosmetics) {
			if(count > 28) break;

			ItemStack item = cos.getDisplayItem().clone();

			if(!playerObject.getUnlockedCosmetics().contains(cos.getId())) item.setType(Material.REDSTONE_BLOCK);
			inv.setItem(slot, item);
			if(slot % 9 == 7) slot += 3;
			else ++slot;
			
			++count;
		}
		
		//Add informational items
		inv.setItem(size - 9, showLockedItem);
		inv.setItem(size - 5, infoItem);
		inv.setItem(size - 1, filterItem);
		
		//Add glass panes on sides, top, and bottom
		int row = 1;
		ItemStack pane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta paneMeta = pane.getItemMeta();
		paneMeta.setDisplayName(" ");
		pane.setItemMeta(paneMeta);
		//Sides
		while(row + 1 < size / 9) {
			inv.setItem(9 * row, pane);
			inv.setItem(9 * row + 8, pane);
			++row;
		}
		//Top
		for(int i = 0; i < 9; ++i) {
			inv.setItem(i, pane);
		}
		pane.setType(Material.BLACK_STAINED_GLASS_PANE);
		//Bottom
		inv.setItem(size - 2, pane);
		inv.setItem(size - 3, pane);
		inv.setItem(size - 4, pane);
		inv.setItem(size - 6, pane);
		inv.setItem(size - 7, pane);
		inv.setItem(size - 8, pane);
		
		return inv;
	}
	
	
	public static Inventory getColorPickerInv() {
		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&aColor &2Picker"));
		ItemStack item = new ItemStack(Material.POTION);
		int slot = 0;
		//Fill by column
		for(float sat = 1F; sat > 0.1F ; sat -= 0.2F) {
			//Fill rows
			for(int hue = 0; hue < 360; hue += 40) {
				
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				meta.setColor(getColorFromHSV(hue, sat, 1F));
				item.setItemMeta(meta);
				inv.setItem(slot, item);
				++slot;
			}
		}
		//Fill bottom row white to black
		for(int i = 0; i < 9; ++i) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			meta.setColor(Color.fromRGB(28 * i, 28 * i, 28 * i));
			item.setItemMeta(meta);
			inv.setItem(slot, item);
			++slot;
		}

		
		
		return inv;
	}
	
	/**
	 * 
	 * @param h - value between 0 and 360 corresponding to degrees on color wheel
	 * @param s - saturation between 0 and 1
	 * @param v - value between 0 and 1
	 * @return
	 */
	private static Color getColorFromHSV(float h, float s, float v) {
		int red, green, blue;
		float r_prime, g_prime, b_prime;
		float c = v * s;
		float x = (float) (c * (1 - Math.abs(((h / 60.0) % 2) - 1)));
		float m = v - c;
		
		if(h < 60) {
			r_prime = c;
			g_prime = x;
			b_prime = 0F;
		}
		else if(h < 120) {
			r_prime = x;
			g_prime = c;
			b_prime = 0F;
		}
		else if(h < 180) {
			r_prime = 0;
			g_prime = c;
			b_prime = x;
		}
		else if(h < 240) {
			r_prime = 0F;
			g_prime = x;
			b_prime = c;
		}
		else if(h < 300) {
			r_prime = x;
			g_prime = 0F;
			b_prime = c;
		}
		else {
			r_prime = c;
			g_prime = 0F;
			b_prime = x;
		}
		
		red = (int) ((r_prime + m) * 255);
		green = (int) ((g_prime + m) * 255);
		blue = (int) ((b_prime + m) * 255);
		
		return Color.fromRGB(red, green, blue);
	}
	
	private static Inventory setPanes(Inventory inv, ItemStack pane, int[] slots) {
		for(int slot : slots) {
			inv.setItem(slot, pane);
		}
		return inv;
	}

}
