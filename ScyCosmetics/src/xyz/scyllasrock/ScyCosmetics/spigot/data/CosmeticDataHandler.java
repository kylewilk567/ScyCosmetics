package xyz.scyllasrock.ScyCosmetics.spigot.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.AFKEffect;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.AFKEffectStyle;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.ArrowTrail;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticTier;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Emote;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.EmoteEquipment;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.EmoteStep;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.KillEffect;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.KillEffectStyle;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.LastWords;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.LogMessage;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerTrail;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Prefix;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Title;
import xyz.scyllasrock.ScyCosmetics.util.ItemUtils;
import xyz.scyllasrock.ScyCosmetics.util.MiscUtils;
import xyz.scyllasrock.ScyUtility.objects.Pair;

public class CosmeticDataHandler {
	
	Main plugin = Main.getPlugin(Main.class);
	
	private static CosmeticDataHandler cosmeticHandler;
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	
	public CosmeticDataHandler() {
		checkOrCreateDirectories("Cosmetics", "Cosmetics" + File.separator + "Emotes", "Cosmetics" + File.separator + "AFK_Files");
		checkOrCreateFiles("last_words.yml", "arrow_trails.yml", "player_trails.yml", "prefixes.yml", "log_messages.yml",
				"emote_equipment.yml", "titles.yml", "kill_effects.yml", "afk_effects.yml");
		checkOrCreateEmotes("disco.yml", "sway.yml", "stabstab.yml", "starlord.yml", "t_pose.yml");
		
		//Temporarily removed - may be permanently removed later as too many particles cause massive lag. Directory also removed above
		checkOrCreateAfkFiles("ytlogo.txt");
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
			File newFile = new File(plugin.getDataFolder() + File.separator + "Cosmetics" +  File.separator + s);
			if(newFile.exists()) continue;
			try {
				InputStream istream = plugin.getResource(s);
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
	 * Creates all files in Emotes folder
	 * @param files
	 */
	private void checkOrCreateEmotes(String... files) {
		for(String s : files) {
			File newFile = new File(plugin.getDataFolder() + File.separator + "Cosmetics" +  File.separator + "Emotes" + File.separator + s);
			if(newFile.exists()) continue;
			try {
				InputStream istream = plugin.getResource("emoteFiles" + File.separator + s);
				OutputStream ostream = new FileOutputStream(newFile);
				istream.transferTo(ostream);
				ostream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Creates all files in Afk files folder
	 * @param files
	 */
	private void checkOrCreateAfkFiles(String... files) {
		for(String s : files) {
			File newFile = new File(plugin.getDataFolder() + File.separator + "Cosmetics" +  File.separator + "AFK_Files" + File.separator + s);
			if(newFile.exists()) continue;
			try {
				InputStream istream = plugin.getResource("AFK_Files" + File.separator + s);
				OutputStream ostream = new FileOutputStream(newFile);
				istream.transferTo(ostream);
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
		//get default buy prices for each tier
		Map<String, Double> priceMap = new HashMap<String, Double>();
		for(CosmeticTier tier : CosmeticTier.values()) {
			priceMap.put(tier.toString(), configMang.getConfig().getDouble("cosmetics.default_buy_prices." + tier.toString().toLowerCase()));
		}
		
		HashMap<String, Cosmetic> cosmetics = new HashMap<String, Cosmetic>();
		//Initialize arrow trails
		YamlConfiguration trailConfig = getConfigFile("Cosmetics" + File.separator + "arrow_trails.yml");
		for(String id : trailConfig.getConfigurationSection("particles").getKeys(false)) {
			CosmeticTier tier = CosmeticTier.valueOf(trailConfig.getString("particles." + id + ".tier"));
			double buyPrice = trailConfig.getDouble("particles." + id + ".buy_price");
			if(buyPrice == 0) buyPrice = priceMap.get(tier.toString());
			ItemStack itemStack = this.getItemStackFromConfigSectionAndKey(trailConfig, "particles", id, tier);
			String afterString = trailConfig.getString("particles." + id + ".is_purchaseable_after");
			String beforeString = trailConfig.getString("particles." + id + ".is_purchaseable_before");
			cosmetics.put(id, (Cosmetic) new ArrowTrail(id, tier, itemStack, buyPrice,
					getPurchaseableStringList(afterString), getPurchaseableStringList(beforeString),
					trailConfig.getBoolean("particles." + id + ".is_unobtainable"),
					Particle.valueOf(trailConfig.getString("particles." + id + ".particle"))));
		}
			
		//Initialize last words
		YamlConfiguration lastWordsConfig = getConfigFile("Cosmetics" + File.separator + "last_words.yml");
		for(String id : lastWordsConfig.getConfigurationSection("last_words").getKeys(false)) {
			CosmeticTier tier = CosmeticTier.valueOf(lastWordsConfig.getString("last_words." + id + ".tier"));
			double buyPrice = lastWordsConfig.getDouble("last_words." + id + ".buy_price");
			if(buyPrice == 0) buyPrice = priceMap.get(tier.toString());
			ItemStack itemStack = getItemStackFromConfigSectionAndKey(lastWordsConfig, "last_words", id, tier);
			String message = lastWordsConfig.getString("last_words." + id + ".text");
			if(message == null) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERROR: Message for last words with id " + id + " is null!");
			}
			else {
				String afterString = lastWordsConfig.getString("last_words." + id + ".is_purchaseable_after");
				String beforeString = lastWordsConfig.getString("last_words." + id + ".is_purchaseable_before");
				cosmetics.put(id, (Cosmetic) new LastWords(id, tier, itemStack, buyPrice,
					getPurchaseableStringList(afterString), getPurchaseableStringList(beforeString),
					lastWordsConfig.getBoolean("last_words." + id + ".is_unobtainable"),
					ChatColor.translateAlternateColorCodes('&', message)));
			}
		}
		
		//Initialize player trails
		YamlConfiguration playerTrailConfig = getConfigFile("Cosmetics" + File.separator + "player_trails.yml");
		for(String id : playerTrailConfig.getConfigurationSection("trails").getKeys(false)) {
			CosmeticTier tier = CosmeticTier.valueOf(playerTrailConfig.getString("trails." + id + ".tier"));
			double buyPrice = playerTrailConfig.getDouble("trails." + id + ".buy_price");
			if(buyPrice == 0) buyPrice = priceMap.get(tier.toString());
			ItemStack itemStack = getItemStackFromConfigSectionAndKey(playerTrailConfig, "trails", id, tier);
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
				String afterString = playerTrailConfig.getString("trails." + id + ".is_purchaseable_after");
				String beforeString = playerTrailConfig.getString("trails." + id + ".is_purchaseable_before");
				cosmetics.put(id, (Cosmetic) new PlayerTrail(id, tier, 
					 itemStack, buyPrice,
					 getPurchaseableStringList(afterString), getPurchaseableStringList(beforeString),
					 playerTrailConfig.getBoolean("trails." + id + ".is_unobtainable"),
					 particles, count, offsetX, offsetY, offsetZ));
			}
		}
		
		//Initialize prefixes
		YamlConfiguration prefixConfig = getConfigFile("Cosmetics" + File.separator + "prefixes.yml");
		for(String id : prefixConfig.getConfigurationSection("prefixes").getKeys(false)) {
			CosmeticTier tier = CosmeticTier.valueOf(prefixConfig.getString("prefixes." + id + ".tier"));
			double buyPrice = prefixConfig.getDouble("prefixes." + id + ".buy_price");
			if(buyPrice == 0) buyPrice = priceMap.get(tier.toString());
			ItemStack itemStack = getItemStackFromConfigSectionAndKey(prefixConfig, "prefixes", id, tier);
			String translatedPrefix = MiscUtils.unescape_perl_string(prefixConfig.getString("prefixes." + id + ".prefix"));
			if(translatedPrefix == null) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERROR: No prefix for prefix with id " + id + "!");
			}
			else {
				String afterString = prefixConfig.getString("prefixes." + id + ".is_purchaseable_after");
				String beforeString = prefixConfig.getString("prefixes." + id + ".is_purchaseable_before");
				cosmetics.put(id, (Cosmetic) new Prefix(id, tier, 
					 itemStack, buyPrice,
					 getPurchaseableStringList(afterString), getPurchaseableStringList(beforeString),
					 prefixConfig.getBoolean("prefixes." + id + ".is_unobtainable"),
					 translatedPrefix, prefixConfig.getStringList("prefixes." + id + ".color_codes"),
					 prefixConfig.getInt("prefixes." + id + ".color_change_ticks")));
			}
		}
		
		//Initialize log messages
		YamlConfiguration logConfig = getConfigFile("Cosmetics" + File.separator + "log_messages.yml");
		for(String id : logConfig.getConfigurationSection("messages").getKeys(false)) {
			CosmeticTier tier = CosmeticTier.valueOf(logConfig.getString("messages." + id + ".tier"));
			double buyPrice = logConfig.getDouble("messages." + id + ".buy_price");
			if(buyPrice == 0) buyPrice = priceMap.get(tier.toString());
			ItemStack itemStack = getItemStackFromConfigSectionAndKey(logConfig, "messages", id, tier);
			Sound sound;
			String logInMessage = logConfig.getString("messages." + id + ".join_message");
			String logOffMessage = logConfig.getString("messages." + id + ".leave_message");
			try {
				sound = Sound.valueOf(logConfig.getString("messages." + id + ".sound").toUpperCase());
			} catch(Exception e) {
				sound = null;
			}
			if(logInMessage == null || logOffMessage == null) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERROR: Could not find log in/off message for log message with id " + id + "!");
			}
			else {
				String afterString = logConfig.getString("messages." + id + ".is_purchaseable_after");
				String beforeString = logConfig.getString("messages." + id + ".is_purchaseable_before");
				cosmetics.put(id, (Cosmetic) new LogMessage(id, tier, 
					 itemStack, buyPrice,
					 getPurchaseableStringList(afterString), getPurchaseableStringList(beforeString),
					 logConfig.getBoolean("messages." + id + ".is_unobtainable"),
					 logInMessage, logOffMessage, sound));
			}
		}
		
		//Initialize emote equipment
		YamlConfiguration equipmentConfig = getConfigFile("Cosmetics" + File.separator + "emote_equipment.yml");
		for(String id : equipmentConfig.getConfigurationSection("equipment").getKeys(false)) {
			try {
				CosmeticTier tier = CosmeticTier.valueOf(equipmentConfig.getString("equipment." + id + ".tier"));
				double buyPrice = equipmentConfig.getDouble("equipment." + id + ".buy_price");
				if(buyPrice == 0) buyPrice = priceMap.get(tier.toString());
				ItemStack itemStack = getItemStackFromConfigSectionAndKey(equipmentConfig, "equipment", id, tier);
				String helmetMat = equipmentConfig.getString("equipment." + id + ".helmet.material");
				ItemStack helmet;
				if(helmetMat.startsWith("head;")) helmet = ItemUtils.getHead(helmetMat.split(";")[1]);
				else helmet = new ItemStack(Material.valueOf(helmetMat.toUpperCase()));
			
				if(helmetMat.toUpperCase().contains("LEATHER") && equipmentConfig.getString("equipment." + id + ".helmet.color") != null) {
					LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
					String[] colorArr = equipmentConfig.getString("equipment." + id + ".helmet.color").split(":");
					helmetMeta.setColor(Color.fromRGB(Integer.parseInt(colorArr[0]),
							Integer.parseInt(colorArr[1]), Integer.parseInt(colorArr[2])));
					helmet.setItemMeta(helmetMeta);
				}
			
				String chestMat = equipmentConfig.getString("equipment." + id + ".chestplate.material");
				String leggingsMat = equipmentConfig.getString("equipment." + id + ".leggings.material");
				String bootsMat = equipmentConfig.getString("equipment." + id + ".boots.material");
			
				ItemStack chestplate = new ItemStack(Material.valueOf(chestMat.toUpperCase()));
				ItemStack leggings = new ItemStack(Material.valueOf(leggingsMat.toUpperCase()));
				ItemStack boots = new ItemStack(Material.valueOf(bootsMat.toUpperCase()));
			
				if(chestMat.toUpperCase().contains("LEATHER") && equipmentConfig.getString("equipment." + id + ".chestplate.color") != null) {
					LeatherArmorMeta leatherMeta = (LeatherArmorMeta) chestplate.getItemMeta();
					String[] colorArr = equipmentConfig.getString("equipment." + id + ".chestplate.color").split(":");
					leatherMeta.setColor(Color.fromRGB(Integer.parseInt(colorArr[0]),
							Integer.parseInt(colorArr[1]), Integer.parseInt(colorArr[2])));
					chestplate.setItemMeta(leatherMeta);
				}
			
				if(leggingsMat.toUpperCase().toUpperCase().contains("LEATHER") && equipmentConfig.getString("equipment." + id + ".leggings.color") != null) {
					LeatherArmorMeta leatherMeta = (LeatherArmorMeta) leggings.getItemMeta();
					String[] colorArr = equipmentConfig.getString("equipment." + id + ".leggings.color").split(":");
					leatherMeta.setColor(Color.fromRGB(Integer.parseInt(colorArr[0]),
							Integer.parseInt(colorArr[1]), Integer.parseInt(colorArr[2])));
					leggings.setItemMeta(leatherMeta);
				}
			
				if(bootsMat.contains("LEATHER") && equipmentConfig.getString("equipment." + id + ".boots.color") != null) {
					LeatherArmorMeta leatherMeta = (LeatherArmorMeta) boots.getItemMeta();
					String[] colorArr = equipmentConfig.getString("equipment." + id + ".boots.color").split(":");
					leatherMeta.setColor(Color.fromRGB(Integer.parseInt(colorArr[0]),
						Integer.parseInt(colorArr[1]), Integer.parseInt(colorArr[2])));
					boots.setItemMeta(leatherMeta);
				}
				String afterString = equipmentConfig.getString("equipment." + id + ".is_purchaseable_after");
				String beforeString = equipmentConfig.getString("equipment." + id + ".is_purchaseable_before");
				cosmetics.put(id, (Cosmetic) new EmoteEquipment(id, tier, 
						itemStack, buyPrice,
						getPurchaseableStringList(afterString), getPurchaseableStringList(beforeString),
						equipmentConfig.getBoolean("equipment." + id + ".is_unobtainable"),
						helmet, chestplate, leggings, boots));
			} catch(Exception e) {
				e.printStackTrace();
			}

		}
		
		//Initialize emote dances
		File emoteDir = new File(plugin.getDataFolder() + File.separator + "Cosmetics" + File.separator + "Emotes");
		for(File emoteFile : emoteDir.listFiles()) {
			YamlConfiguration emoteConfig = YamlConfiguration.loadConfiguration(emoteFile);
			String id = emoteConfig.getString("id");
			CosmeticTier tier = CosmeticTier.valueOf(emoteConfig.getString("tier"));
			double buyPrice = emoteConfig.getDouble("buy_price");
			if(buyPrice == 0) buyPrice = priceMap.get(tier.toString());
			ItemStack itemStack = getEmoteItemStack(emoteConfig, tier);
			List<Pair<Integer, EmoteStep>> positions = readEmoteSteps(emoteConfig);
			if(positions == null) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERROR: Could not read emote with id " + id + "!");
			}
			else {
				String afterString = emoteConfig.getString("is_purchaseable_after");
				String beforeString = emoteConfig.getString("is_purchaseable_before");
				cosmetics.put(id, (Cosmetic) new Emote(id, tier, itemStack, buyPrice,
						getPurchaseableStringList(afterString), getPurchaseableStringList(beforeString),
						emoteConfig.getBoolean("is_unobtainable"), positions,
						emoteConfig.getBoolean("disable_base_plate"), emoteConfig.getBoolean("set_small")));
			}
			
		}
		
		//Initialize titles
		YamlConfiguration titleConfig = getConfigFile("Cosmetics" + File.separator + "titles.yml");
		for(String id : titleConfig.getConfigurationSection("titles").getKeys(false)) {
			CosmeticTier tier = CosmeticTier.valueOf(titleConfig.getString("titles." + id + ".tier"));
			double buyPrice = titleConfig.getDouble("titles." + id + ".buy_price");
			if(buyPrice == 0) buyPrice = priceMap.get(tier.toString());
			ItemStack item = getItemStackFromConfigSectionAndKey(titleConfig, "titles", id, tier);
			String title = titleConfig.getString("titles." + id + ".title");
			if(title == null) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERROR: Could not find title for title with id " + id + "!");
			}
			else {
				String afterString = titleConfig.getString("titles." + id + ".is_purchaseable_after");
				String beforeString = titleConfig.getString("titles." + id + ".is_purchaseable_before");
				cosmetics.put(id, (Cosmetic) new Title(id, tier, item, buyPrice,
						getPurchaseableStringList(afterString), getPurchaseableStringList(beforeString),
						titleConfig.getBoolean("titles." + id + ".is_unobtainable"), title));
			}
		}
		
		//Initialize kill effects
		YamlConfiguration killConfig = getConfigFile("Cosmetics" + File.separator + "kill_effects.yml");
		for(String id : killConfig.getConfigurationSection("effects").getKeys(false)) {
			CosmeticTier tier = CosmeticTier.valueOf(killConfig.getString("effects." + id + ".tier"));
			double buyPrice = killConfig.getDouble("effects." + id + ".buy_price");
			if(buyPrice == 0) buyPrice = priceMap.get(tier.toString());
			ItemStack item = getItemStackFromConfigSectionAndKey(killConfig, "effects", id, tier);
			String styleStr = killConfig.getString("effects." + id + ".style");
			if(styleStr == null) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERROR: Could not find style for kill effect with id " + id + "!");
			}
			else {
				String afterString = killConfig.getString("effects." + id + ".is_purchaseable_after");
				String beforeString = killConfig.getString("effects." + id + ".is_purchaseable_before");
				cosmetics.put(id, (Cosmetic) new KillEffect(id, 
						tier, item, buyPrice,
						getPurchaseableStringList(afterString), getPurchaseableStringList(beforeString),
						killConfig.getBoolean("effects." + id + ".is_unobtainable"),
						KillEffectStyle.valueOf(styleStr.toUpperCase())));
			}
		}
		
		//Initialize afk effects
		YamlConfiguration afkConfig = getConfigFile("Cosmetics" + File.separator + "afk_effects.yml");
		for(String id : afkConfig.getConfigurationSection("effects").getKeys(false)) {
			CosmeticTier tier = CosmeticTier.valueOf(afkConfig.getString("effects." + id + ".tier"));
			double buyPrice = afkConfig.getDouble("effects." + id + ".buy_price");
			if(buyPrice == 0) buyPrice = priceMap.get(tier.toString());
			ItemStack item = getItemStackFromConfigSectionAndKey(afkConfig, "effects", id, tier);
			String styleStr = afkConfig.getString("effects." + id + ".style");
			Particle particle;
			try {
			particle = Particle.valueOf(afkConfig.getString("effects." + id + ".particle").toUpperCase());
			} catch(Exception e) {
				particle = null;
			}
			if(styleStr == null) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERROR: Could not find style for afk effect with id " + id + "!");
			}
			else {
				String afterString = afkConfig.getString("effects." + id + ".is_purchaseable_after");
				String beforeString = afkConfig.getString("effects." + id + ".is_purchaseable_before");
				
				//Get file if specified
				List<String> fileNames = afkConfig.getStringList("effects." + id + ".files");
				List<File> files = new ArrayList<File>();
				for(String fileName : fileNames) {
					files.add(new File(plugin.getDataFolder() + File.separator + "Cosmetics" + File.separator
							+ "AFK_Files" + File.separator + fileName));
				}
				
				//Get offset if specified
				String offsetStr = afkConfig.getString("effects." + id + ".offset");
				
				//Get rotations if specified
				List<Double> rotations = afkConfig.getDoubleList("effects." + id + ".rotations");
				

				cosmetics.put(id, (Cosmetic) new AFKEffect(id, tier, item, buyPrice,
						getPurchaseableStringList(afterString), getPurchaseableStringList(beforeString),
						afkConfig.getBoolean("effects." + id + ".is_unobtainable"),
						AFKEffectStyle.valueOf(styleStr.toUpperCase()), particle, files, offsetStr, rotations));
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
	
	
	
	private ItemStack getItemStackFromConfigSectionAndKey(YamlConfiguration config, String configSection, String key, CosmeticTier tier) {
		ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK);
		if(config.getString(configSection + "." + key + ".display_item") != null) {
			String mat = config.getString(configSection + "." + key + ".display_item").strip();
			if(mat.startsWith("head;")) {
				itemStack = ItemUtils.getHead(mat.split("head;")[1]);
			}
			else itemStack.setType(Material.valueOf(config.getString(configSection + "." + key + ".display_item")));
		}
		ItemMeta meta = itemStack.getItemMeta();
		List<String> lore = config.getStringList(configSection + "." + key + ".display_lore")
				.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
		//Append tier if necessary
		if(configMang.getConfig().getBoolean("cosmetics.display_items.lore.append_tier")) {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&eTier: " + tier.colorCode + tier.toString()));
		}
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(configSection + "." + key + ".display_name")));
		meta.setLore(lore);
		//Add custom persistent data if exists
		PersistentDataContainer data = meta.getPersistentDataContainer();
		data.set(new NamespacedKey(plugin, "ScyCos_id"), PersistentDataType.STRING, key);
		if(config.getString(configSection + "." + key + ".custom_data") != null) {
		data.set(new NamespacedKey(plugin, "ScyCos_data"), PersistentDataType.STRING, config.getString(configSection + "." + key + ".custom_data"));
		}
		//Remove attributes from showing
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		itemStack.setItemMeta(meta);
		return itemStack;
	}
	
	private ItemStack getEmoteItemStack(YamlConfiguration config, CosmeticTier tier) {
		ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK);
		if(config.getString("display_item") != null) {
			String mat = config.getString("display_item").strip();
			if(mat.startsWith("head;")) {
				itemStack = ItemUtils.getHead(mat.split("head;")[1]);
			}
			else itemStack.setType(Material.valueOf(config.getString("display_item")));
		}
		ItemMeta meta = itemStack.getItemMeta();
		List<String> lore = config.getStringList("display_lore")
				.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
		//Append tier if necessary
		if(configMang.getConfig().getBoolean("cosmetics.display_items.lore.append_tier")) {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&eTier: " + tier.colorCode + tier.toString()));
		}
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("display_name")));
		meta.setLore(lore);
		//Add custom persistent data if exists
		PersistentDataContainer data = meta.getPersistentDataContainer();
		data.set(new NamespacedKey(plugin, "ScyCos_id"), PersistentDataType.STRING, config.getString("id"));
		if(config.getString("custom_data") != null) {
		data.set(new NamespacedKey(plugin, "ScyCos_data"), PersistentDataType.STRING, config.getString("custom_data"));
		}
		//Remove attributes from showing
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		itemStack.setItemMeta(meta);
		return itemStack;
	}
	
	private List<String> getPurchaseableStringList(String s) {
		if(s == null) return new ArrayList<String>();
		String[] dateArr = s.split(";");
		return Arrays.asList(dateArr).stream().map(dateString -> dateString.strip()).collect(Collectors.toList());
	}
	
	private List<Pair<Integer, EmoteStep>> readEmoteSteps(YamlConfiguration emoteConfig) {
		List<Pair<Integer, EmoteStep>> positions = new ArrayList<Pair<Integer, EmoteStep>>();

		//Read in sequence string and find step numbers
		String sequence = emoteConfig.getString("sequence"); //Of the form #, #:#:#, #:#:#...
		if(sequence == null) return null;
		String[] sequenceStepsArr = sequence.split(",");
		String[][] sequenceDataArr = new String[sequenceStepsArr.length][]; // first index is step, second index which # to look at.
		for(int i = 0; i < sequenceStepsArr.length; ++i) {
			sequenceDataArr[i] = sequenceStepsArr[i].split(":");
		}
		//Remove spaces from all entries
		for(int i = 0; i < sequenceDataArr.length; ++i) {
			for(int j = 0; j < sequenceDataArr[i].length; ++j) {
				sequenceDataArr[i][j] = sequenceDataArr[i][j].strip();
			}
		}
		
		//Read in configSteps
		Map<Integer, EmoteStep> configSteps = new HashMap<Integer, EmoteStep>(); //Stores steps in
		try {
		for(String[] num : sequenceDataArr) {
			int step = Integer.parseInt(num[0]);
			float rotation = (float) emoteConfig.getInt("steps." + step + ".rotation");
			String[] head = emoteConfig.getString("steps." + step + ".head").split(":");
			EulerAngle headPose = new EulerAngle(Double.valueOf(head[0]), Double.valueOf(head[1]), Double.valueOf(head[2]));
			String[] body = emoteConfig.getString("steps." + step + ".body").split(":");
			EulerAngle bodyPose = new EulerAngle(Double.valueOf(body[0]), Double.valueOf(body[1]), Double.valueOf(body[2]));
			String[] leftArm = emoteConfig.getString("steps." + step + ".left_arm").split(":");
			EulerAngle leftArmPose = new EulerAngle(Double.valueOf(leftArm[0]), Double.valueOf(leftArm[1]), Double.valueOf(leftArm[2]));
			String[] rightArm = emoteConfig.getString("steps." + step + ".right_arm").split(":");
			EulerAngle rightArmPose = new EulerAngle(Double.valueOf(rightArm[0]), Double.valueOf(rightArm[1]), Double.valueOf(rightArm[2]));
			String[] leftLeg = emoteConfig.getString("steps." + step + ".left_leg").split(":");
			EulerAngle leftLegPose = new EulerAngle(Double.valueOf(leftLeg[0]), Double.valueOf(leftLeg[1]), Double.valueOf(leftLeg[2]));
			String[] rightLeg = emoteConfig.getString("steps." + step + ".right_leg").split(":");
			EulerAngle rightLegPose = new EulerAngle(Double.valueOf(rightLeg[0]), Double.valueOf(rightLeg[1]), Double.valueOf(rightLeg[2]));
			String mainMat = emoteConfig.getString("steps." + step + ".main_hand");
			ItemStack mainHand = null;
			if(mainMat != null) mainHand = new ItemStack(Material.valueOf(mainMat.toUpperCase()));
			String offMat = emoteConfig.getString("steps." + step + ".off_hand");
			ItemStack offHand = null;
			if(offMat != null) offHand = new ItemStack(Material.valueOf(offMat.toUpperCase()));
			EmoteStep emoteStep = new EmoteStep(rotation, headPose, bodyPose, leftArmPose, rightArmPose, leftLegPose, rightLegPose, mainHand, offHand);
			configSteps.put(step, emoteStep);
		}
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		//Read in positions - must account for negative numbers in config steps
		EmoteStep currentStep;
		EmoteStep nextStep;
		int ticksBetweenInter;
		int numInter;
		for(int i = 0; i < sequenceDataArr.length - 1; ++i) {
			currentStep = configSteps.get(Integer.parseInt(sequenceDataArr[i][0]));
			nextStep = configSteps.get(Integer.parseInt(sequenceDataArr[i+1][0]));
			ticksBetweenInter = Integer.parseInt(sequenceDataArr[i+1][1]);
			numInter = Integer.parseInt(sequenceDataArr[i+1][2]);
			List<EmoteStep> interSteps = findInterpolatedEmoteSteps(currentStep, nextStep, numInter);
			for(EmoteStep step : interSteps) {
				positions.add(new Pair<Integer, EmoteStep>(ticksBetweenInter, step));
			}

		}

		
		return positions;
	}
	
	
	private List<EmoteStep> findInterpolatedEmoteSteps(EmoteStep currentStep, EmoteStep nextStep, int numInterpolations) {
		List<EmoteStep> interSteps = new ArrayList<EmoteStep>();
		//Calculate interpolation amounts per step
		double rotAmount = calculateInterpolationAmount(Math.abs(currentStep.getRotAmount()), Math.abs(nextStep.getRotAmount()), numInterpolations, !(nextStep.getRotAmount() < 0));
		double[] headAmounts = calculateInterpolationAmounts(currentStep.getHeadPose(), nextStep.getHeadPose(), numInterpolations);
		double[] bodyAmounts = calculateInterpolationAmounts(currentStep.getBodyPose(), nextStep.getBodyPose(), numInterpolations);
		double[] leftArmAmounts = calculateInterpolationAmounts(currentStep.getLeftArmPose(), nextStep.getLeftArmPose(), numInterpolations);
		double[] rightArmAmounts = calculateInterpolationAmounts(currentStep.getRightArmPose(), nextStep.getRightArmPose(), numInterpolations);		
		double[] leftLegAmounts = calculateInterpolationAmounts(currentStep.getLeftLegPose(), nextStep.getLeftLegPose(), numInterpolations);
		double[] rightLegAmounts = calculateInterpolationAmounts(currentStep.getRightLegPose(), nextStep.getRightLegPose(), numInterpolations);
		for(int i = 0; i < numInterpolations; ++i) {
			//Calculate next step
			double newRot = Math.abs(currentStep.getRotAmount()) + rotAmount * i;
			EulerAngle newHeadPose = calculateNextEmoteStep(currentStep.getHeadPose(), headAmounts, i);
			EulerAngle newBodyPose = calculateNextEmoteStep(currentStep.getBodyPose(), bodyAmounts, i);
			EulerAngle newLeftArmPose = calculateNextEmoteStep(currentStep.getLeftArmPose(), leftArmAmounts, i);
			EulerAngle newRightArmPose = calculateNextEmoteStep(currentStep.getRightArmPose(), rightArmAmounts, i);
			EulerAngle newLeftLegPose = calculateNextEmoteStep(currentStep.getLeftLegPose(), leftLegAmounts, i);
			EulerAngle newRightLegPose = calculateNextEmoteStep(currentStep.getRightLegPose(), rightLegAmounts, i);

			interSteps.add(new EmoteStep((float) newRot, newHeadPose, newBodyPose, newLeftArmPose, newRightArmPose, newLeftLegPose, newRightLegPose,
					currentStep.getMainHand(), currentStep.getOffHand()));
		}

		
		return interSteps;
	}
	
	private EulerAngle calculateNextEmoteStep(EulerAngle currentPose, double[] amounts, int multiplier) {
		return new EulerAngle(Math.toRadians(Math.abs(currentPose.getX()) + amounts[0] * multiplier),
				Math.toRadians(Math.abs(currentPose.getY()) + amounts[1] * multiplier), Math.toRadians(
						Math.abs(currentPose.getZ()) + amounts[2] * multiplier));
	}
	
	
	private double[] calculateInterpolationAmounts(EulerAngle start, EulerAngle end, int numInterpolations) {
		double[] amounts = new double[3];
		amounts[0] = calculateInterpolationAmount(Math.abs(start.getX()), Math.abs(end.getX()), numInterpolations, !(end.getX() < 0)); //rotate negative direction if configStep < 0
		amounts[1] = calculateInterpolationAmount(Math.abs(start.getY()), Math.abs(end.getY()), numInterpolations, !(end.getY() < 0));
		amounts[2] = calculateInterpolationAmount(Math.abs(start.getZ()), Math.abs(end.getZ()), numInterpolations, !(end.getZ() < 0));
		
		return amounts;
	}
	
	/**
	 * 
	 * @param start - between 0 and 360
	 * @param end - between 0 and 360
	 * @param numInterpolations
	 * @param direction - true means rotate in positive direction. false means rotate in negative
	 * @return number between -360 / numInterpolations and +360 / numInterpolations
	 */
	private double calculateInterpolationAmount(double start, double end, double numInterpolations, boolean direction) {
		//If passing 0 or 360 mark
		if(start < end && !direction) {
			return -1 * ((start + (360 - end)) / numInterpolations);
		}
		else if(start > end && direction) {
			return (end + (360 - start)) / numInterpolations;
		}
		else {
			return (end - start) / numInterpolations;
		}
		
	}
	

}
