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
	private static Field blockProfileField;
	private static Method metaSetProfileMethod;
	private static Field metaProfileField;
	
	
	@SuppressWarnings("deprecation")
	public static ItemStack getHead(String value) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if(!(skull.getItemMeta() instanceof SkullMeta)) return skull;
        
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		mutateItemMeta(meta, value);
		skull.setItemMeta(meta);
		return skull;
  //      UUID hashAsId = new UUID(value.hashCode(), value.hashCode());
  //      return Bukkit.getUnsafe().modifyItemStack(skull,
  //              "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
 //       );
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

}
