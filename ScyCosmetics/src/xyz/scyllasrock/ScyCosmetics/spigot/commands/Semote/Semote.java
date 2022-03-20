package xyz.scyllasrock.ScyCosmetics.spigot.commands.Semote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Emote;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.EmoteEquipment;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.EmoteStep;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;
import xyz.scyllasrock.ScyCosmetics.util.InventoryUtils;
import xyz.scyllasrock.ScyUtility.objects.Pair;

public class Semote implements CommandExecutor, TabCompleter {
	
	private static Main plugin = Main.getPlugin(Main.class);
	private static ConfigManager configMang = ConfigManager.getConfigMang();
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	
	private static Map<UUID, Long> emoteCooldowns = new HashMap<UUID, Long>();
	private static int cooldownSeconds = configMang.getConfig().getInt("misc.emote_cooldown");
	private static int delayTicks = configMang.getConfig().getInt("misc.emote_delay_ticks");
	
	private static Map<UUID, BukkitTask> emoteMap = new HashMap<UUID, BukkitTask>();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//If sender is player without perms
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!player.hasPermission(configMang.getPermission("semote"))) {
				player.sendMessage(configMang.getMessage("no_permission"));
				return true;
			}
		}
		//Console
		else {
			sender.sendMessage(configMang.getMessage("error_player_only_cmd"));
			return true;
		}
		
		Player player = (Player) sender;
		PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		
		//Check arg length
		if(args.length > 1) {
			player.sendMessage(ChatColor.RED + "Usage: /semote <emote>");
			return true;
		}
		
		//Check gamemode - cannot emote if in spectator
		if(player.getGameMode().equals(GameMode.SPECTATOR)) {
			player.sendMessage(configMang.getMessage("error_emote_spectator"));
			return true;
		}
		
		//If no args, open GUI
		if(args.length == 0) {
			player.openInventory(InventoryUtils.getEmoteDanceInv(player, playerObject));			
			return true;
		}
		
		//Check id
		String id = "EM_" + args[0].toUpperCase();
		Cosmetic cos = plugin.getCosmeticFromId(id);
		if(cos == null) {
			player.sendMessage(configMang.getMessage("error_invalid_cosmetic_id"));
			return true;
		}
		
		//Check access to cosmetic
		if(!playerObject.getUnlockedCosmetics().contains(id)) {
			player.sendMessage(configMang.getMessage("error_emote_locked"));
			return true;
		}
		
		//Check cooldown
		if(emoteCooldowns.containsKey(player.getUniqueId()) && emoteCooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
			player.sendMessage(configMang.getMessage("error_emote_cooldown"));
			return true;
		}
		
		//Check only 1 emote active
		if(emoteMap.containsKey(player.getUniqueId())) {
			player.sendMessage(configMang.getMessage("error_emote_cooldown"));
			return true;
		}
		
		EmoteEquipment equipment;
		try {
			float yaw = player.getLocation().getYaw();
			Emote emote = ((Emote) cos).clone();
			equipment = (EmoteEquipment) playerObject.getActiveCosmetic(CosmeticType.EMOTE_EQUIPMENT);
			
			//add to cooldown
			emoteCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 1000 * cooldownSeconds);
			player.sendMessage(configMang.getMessage("emote_spawning"));
			
			BukkitTask task = Bukkit.getScheduler().runTaskTimer((Plugin) plugin, new Runnable() {				
				
				ArmorStand stand;
				int count = 0;
				
				@Override
				public void run() {
					Pair<Integer, EmoteStep> step = emote.stepPosition();
					
					//Initialize armorstand
					if(count == 0) {
						//Spawn armor stand and run through steps
						++count;
						stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
						stand.setInvisible(false);
						stand.setInvulnerable(true);
						stand.setGravity(false);
						stand.setMetadata("Scycos_emote", new FixedMetadataValue(plugin, true));
						stand.setArms(true);
						stand.setSmall(emote.setSmall());
						stand.setBasePlate(!emote.disableBasePlate());
						
						//Add armor
						if(equipment != null) {
							stand.getEquipment().setHelmet(equipment.getHelmet());
							stand.getEquipment().setChestplate(equipment.getChestplate());
							stand.getEquipment().setLeggings(equipment.getLeggings());
							stand.getEquipment().setBoots(equipment.getBoots());
						}
						//Else apply default leather
						else {
							stand.getEquipment().setHelmet(new ItemStack(Material.PLAYER_HEAD));
							stand.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
							stand.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
							stand.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS)); 
						}						
						plugin.getActiveEmoteStands().add(stand);
					}
					
					//First value is 1, emote complete
					if(step.getFirst() == 1) {
						plugin.getActiveEmoteStands().remove(stand);
						stand.remove();
						emoteMap.get(player.getUniqueId()).cancel();
						emoteMap.remove(player.getUniqueId());
						return;
					}
					//Second value is null - wait
					if(step.getSecond() == null) {
						return;
					}
					//Second value is not null - set position
					setPosition(stand, step.getSecond(), yaw);
					
				}
				
			}, delayTicks, 1L);
			
			emoteMap.put(player.getUniqueId(), task);
			
			
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	return true;	
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		Player player = (Player) sender;
		 //**** TO BE REMOVED
		if(!player.isOp()) return null;
		
		List<String> help = new ArrayList<>();
		String searching = "";
		switch(args.length) {

		case 0:
			return help;
		//First argument
		case 1:
			if (args[0] != null) {
				searching = args[0].toLowerCase();
			}

			//Add all unlocked
			PlayerObject playerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
			for(String id : playerObject.getUnlockedCosmetics()) {
				Cosmetic cos = plugin.getCosmeticFromId(id);
				if(cos == null) continue;
				if(cos.getType().equals(CosmeticType.EMOTE_DANCE)) {
					if(id.substring(3).toLowerCase().startsWith(searching)) help.add(id.substring(3));
				}
			}

			return help;
			
		default:
			return help;
			
		}
			
	}
	
/*	private void startEmote(Emote emote, ArmorStand stand) {
		task = Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				EmoteStep step = emote.stepPosition();
				if(step == null) {
					task.cancel();
				}
				setPosition(stand, step);
				
			}
		}, 20L,20L);
	}
	
	private void cancelEmote() {
		task.cancel();
	}*/
	
	private void setPosition(ArmorStand stand, EmoteStep step, float yaw) {
		stand.setRotation(yaw + step.getRotAmount(), 0F);
		stand.setHeadPose(step.getHeadPose());
		stand.setBodyPose(step.getBodyPose());
		stand.setRightArmPose(step.getRightArmPose());
		stand.setLeftArmPose(step.getLeftArmPose());
		stand.setRightLegPose(step.getRightLegPose());
		stand.setLeftLegPose(step.getLeftLegPose());
		stand.getEquipment().setItemInMainHand(step.getMainHand());
		stand.getEquipment().setItemInOffHand(step.getOffHand());
	}


}
