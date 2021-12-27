package xyz.scyllasrock.ScyCosmetics.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class ItemUtils {
	
	
	
	// some reflection stuff to be used when setting a skull's profile
	private static Method metaSetProfileMethod;
	private static Field metaProfileField;
	
	
	public static ItemStack getHead(String value) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if(!(skull.getItemMeta() instanceof SkullMeta)) return skull;
        
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		mutateItemMeta(meta, value);
		skull.setItemMeta(meta);
		return skull;
    }
	
	
	private static void mutateItemMeta(SkullMeta meta, String b64) {
		try {
			if (metaSetProfileMethod == null) {
				metaSetProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
				metaSetProfileMethod.setAccessible(true);
			}
			metaSetProfileMethod.invoke(meta, makeProfile(b64));
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
			// if in an older API where there is no setProfile method,
			// we set the profile field directly.
			try {
				if (metaProfileField == null) {
					metaProfileField = meta.getClass().getDeclaredField("profile");
					metaProfileField.setAccessible(true);
				}
				metaProfileField.set(meta, makeProfile(b64));

			} catch (NoSuchFieldException | IllegalAccessException ex2) {
				ex2.printStackTrace();
			}
		}
	}
	
	private static GameProfile makeProfile(String b64) {
		// random uuid based on the b64 string
		UUID id = new UUID(
				b64.substring(b64.length() - 20).hashCode(),
				b64.substring(b64.length() - 10).hashCode()
		);
		GameProfile profile = new GameProfile(id, "Player");
		profile.getProperties().put("textures", new Property("textures", b64));
		return profile;
	}
	
	/**
	 * 
	 * @param item1
	 * @param item2
	 * @return whether the two items are equal. If one or both items are null, returns false.
	 */
	public static boolean itemEquals(ItemStack item1, ItemStack item2) {
		if(item1 == null || item2 == null) return false;
		
		//If one does and the other does not have something, return false
		boolean bool1 = item1.hasItemMeta();
		boolean bool2 = item2.hasItemMeta();
		if(bool1 != bool2) return false;
		bool1 = item1.getItemMeta().hasDisplayName();
		bool2 = item2.getItemMeta().hasDisplayName();
		if(bool1 != bool2) return false;
		bool1 = item1.getItemMeta().hasLore();
		bool2 = item2.getItemMeta().hasLore();
		if(bool1 != bool2) return false;
		if(!item1.getItemMeta().getDisplayName().equals(item2.getItemMeta().getDisplayName())) return false;
		if(!item1.getItemMeta().getLore().equals(item2.getItemMeta().getLore())) return false;
		
		return true;
	}
	
	

}
