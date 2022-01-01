package xyz.scyllasrock.ScyCosmetics.spigot.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.KillEffect;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.PlayerObject;
import xyz.scyllasrock.ScyCosmetics.util.ItemUtils;

public class KillEffectListeners implements Listener {
	
	private static Main plugin = Main.getInstance();
	private static PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
	private static Map<Integer, BukkitTask> effectMap = new HashMap<Integer, BukkitTask>();
	private static Integer effectCounter = 0;
	
	//**TO BE REMOVED
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if(!player.isOp()) return;
		PlayerObject killerObject = playerHandler.getPlayerObjectByUUID(player.getUniqueId());
		KillEffect effect = (KillEffect) killerObject.getActiveCosmetic(CosmeticType.KILL_EFFECT);
		if(effect == null) return;
		spawnKillEffect(effect, event.getBlock().getLocation(), player);
	}
	
	@EventHandler
	public void onPlayerKill(PlayerDeathEvent event) {
		if(event.getEntity() == null) return;
		if(event.getEntity().getKiller() == null) return;
		if(!(event.getEntity().getKiller() instanceof Player)) return;
		
		Player player = event.getEntity();
		Player killer = event.getEntity().getKiller();
		
		PlayerObject killerObject = playerHandler.getPlayerObjectByUUID(killer.getUniqueId());
		if(killerObject == null) return;
		
		//spawn death effect of given by killer at player's location
		KillEffect effect = (KillEffect) killerObject.getActiveCosmetic(CosmeticType.KILL_EFFECT);
		if(effect == null) return;
		spawnKillEffect(effect, player.getLocation(), killer);
		
	}
	
	
	
	private void spawnKillEffect(KillEffect effect, Location loc, Player killer) {
		
		switch(effect.getStyle()) {
		
		case LIGHTNING:
			loc.getWorld().strikeLightningEffect(loc);
			break;
			
		case FIREWORK:
			Firework fw = (Firework) loc.getWorld().spawnEntity(loc,EntityType.FIREWORK);
			FireworkMeta meta = fw.getFireworkMeta();
			// Modify firework based on meta
			
			//No data - pick random from 4 options
			if(effect.getCosData() == null) {
				double rand = Math.random();
				if(rand < 0.25) {
					meta.addEffect(FireworkEffect.builder().withColor(Color.AQUA).trail(true).build());
				}
				else if(rand < 0.5) {
					meta.addEffect(FireworkEffect.builder().withColor(Color.MAROON).trail(true).build());
				}
				else if(rand < 0.75) {
					meta.addEffect(FireworkEffect.builder().withColor(Color.PURPLE).trail(true).withFade(Color.GRAY).build());
				}
				else {
					meta.addEffect(FireworkEffect.builder().withColor(Color.LIME).trail(true).withFade(Color.GREEN).build());
				}
			}
			
			else if(effect.getCosData()[0].equalsIgnoreCase("winter")) {
				meta.addEffect(FireworkEffect.builder().withColor(Color.WHITE, Color.TEAL).trail(true).withFade(Color.AQUA).build());
			}
			
			meta.setPower(1); //how long it travels up
			fw.setFireworkMeta(meta);
		
			break;
			
		case BLOOD:
			loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 40, 1, 1, 1, Material.REDSTONE_BLOCK.createBlockData());
			break;
			
		case COOKIE:
			++effectCounter;
			Location cookieLoc = loc.clone();
			cookieLoc.setY(cookieLoc.getY() + 1);
			BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
				int step = 0;
				final int count = 30;
				final int key = effectCounter;

				@Override
				public void run() {
					if(step > count) {
						effectMap.get(key).cancel();
						effectMap.remove(key);
						return;
					}
					ItemStack cookie = new ItemStack(Material.COOKIE);
					
					Item item = loc.getWorld().dropItem(cookieLoc, cookie);
					//Play sound
					cookieLoc.getWorld().playSound(cookieLoc, Sound.ENTITY_ITEM_PICKUP, 1F, 1F);
					item.setPickupDelay(32767); //Cannot be picked up
					item.setTicksLived(5960);
					item.setVelocity(new Vector(0.1 * Math.sin(2 * Math.PI * Math.random()), 0.1, 0.1 * Math.cos(2 * Math.PI * Math.random()))); //set velocity
					++step;
				}
				
			}, 1L, 2L);
			effectMap.put(effectCounter, task);
			break;
			
		case GHOST: //Testing for now
			++effectCounter;
			
			Location ghostLoc = loc.clone();
			//Play sound
			ghostLoc.getWorld().playSound(ghostLoc, Sound.ENTITY_GHAST_DEATH, 1F, 1F);
			
			//Slight horizontal offset
			ghostLoc.setX(ghostLoc.getX() + 0.5);
			
			//Create ghost head (armor stand) and spawn
			ArmorStand stand = (ArmorStand) ghostLoc.getWorld().spawnEntity(ghostLoc, EntityType.ARMOR_STAND);
			
			//Subtract x again (center of circle)
			ghostLoc.setX(ghostLoc.getX() - 0.5);
			
			//Add stand to plugin list (to prevent bad things)
			plugin.getActiveEmoteStands().add(stand);
			
			//Set equipment and settings
			stand.setGravity(true);
			stand.setSmall(true);
			stand.setInvisible(true);
			stand.setInvulnerable(true);
			String headStr = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0=";
			
			stand.getEquipment().setHelmet(ItemUtils.getHead(headStr));
			
			BukkitTask ghostTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
				int step = 0;
				final int count = 90;
				final int key = effectCounter;

				@Override
				public void run() {
					Location oneAbove = stand.getLocation().clone();
					oneAbove.setY(oneAbove.getY() + 1.5);
					if(step > count || !stand.getLocation().getWorld().getBlockAt(oneAbove).getType().equals(Material.AIR)) {
						stand.remove();
						plugin.getActiveEmoteStands().remove(stand);
						effectMap.get(key).cancel();
						effectMap.remove(key);
						return;
					}
					stand.setRotation(killer.getLocation().getYaw() + 180, 0); //Make stand look at killer
					//Calculate velocity
						//Create vector in x-z plane between each location
						Vector v1 = new Vector(stand.getLocation().getX() - ghostLoc.getX(), 0 , stand.getLocation().getZ() - ghostLoc.getZ());
						Vector v2 = new Vector(0, -1, 0); // -y axis
						
						//Find cross product vector and normalize. Then multiply and set y velocity
						Vector velocity = v1.crossProduct(v2).normalize().multiply(0.12).setY(0.07);
						
					stand.setVelocity(velocity); //Set velocity tangent to circle center (ghostItemLoc with x - 1). Counter-clockwise
					
					//Spawn particle below it
					Location particleLoc = stand.getLocation().clone();
					particleLoc.add(0, 0.5, 0);
					ghostLoc.getWorld().spawnParticle(Particle.DRAGON_BREATH, particleLoc, 4, 0.1, 0.1, 0.1, 0);
					++step;
				}
				
			}, 1L, 2L);
			effectMap.put(effectCounter, ghostTask);
			break;
			
			
		case EXPLOSION:
			++effectCounter;
			
			Location explosionLoc = loc.clone();
			
			//Play sound
			explosionLoc.getWorld().playSound(explosionLoc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1F, 1F);
			
			//Slight vertical offset
			explosionLoc.setX(explosionLoc.getX() + 1);
			
			BukkitTask explosionTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
				int step = 0;
				final int count = 20;
				final int key = effectCounter;

				@Override
				public void run() {

					if(step > count) {
						effectMap.get(key).cancel();
						effectMap.remove(key);
						return;
					}

					//Create a sphere of randomly located particles (inside sphere flame, outside sphere crit)
					if(step < 6) {
						explosionLoc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, explosionLoc, 4, 0.4, 0.4, 0.4, 0);
					}
					if(step < 10) {
						explosionLoc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, explosionLoc, 5, 1.7, 1.7, 1.7, 0);
						explosionLoc.getWorld().spawnParticle(Particle.FLAME, explosionLoc, 7, 0.75, 0.75, 0.75, 0);
					}
					++step;
				}
				
			}, 1L, 2L);
			effectMap.put(effectCounter, explosionTask);
			
			
			break;
			
		case ANGEL:
			//Spawns particles that travel to the sky like an angel ascending
			++effectCounter;
			Location particleLoc = loc.clone();
			BukkitTask angelTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
				final int count = 20; //Blocks up to travel
				int step = 0;
				final int key = effectCounter;
				Particle angelParticle = Particle.END_ROD;
				
				@Override
				public void run() {
					if(step > count) {
						effectMap.get(key).cancel();
						effectMap.remove(key);
						return;
					}
					
					particleLoc.getWorld().spawnParticle(angelParticle, particleLoc, 6, 0.5, 0.5, 0.5, 0); //6 particles, 0.5 offset
					//Increase y by 1 block
					particleLoc.setY(particleLoc.getY() + 1);
					++step;
				}
				
			}, 1L, 2L);
			effectMap.put(effectCounter, angelTask);
			break;
			
		//Normal
		default:
			//Pick random particle
			Particle particle;
			double rand = Math.random();
			if(rand < 0.2) {
				particle = Particle.FLAME;
			}
			else if(rand < 0.4) {
				particle = Particle.SOUL;
			}
			else if(rand < 0.6) {
				particle = Particle.CRIT;
			}
			else if(rand < 0.8) {
				particle = Particle.DRAGON_BREATH;
			}
			else {
				particle = Particle.COMPOSTER;
			}
			
			loc.getWorld().spawnParticle(particle, loc, 50, 2, 2, 2, 0);
			break;
		}
		
	}
	

}
