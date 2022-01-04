package xyz.scyllasrock.ScyCosmetics.spigot.hooks;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;

public class VaultHook {
	
	private static Main plugin = Main.getInstance();
	private static Economy eco;
	private static VaultHook vaultHook;

	/*
	 * Initial Economy setup
	 */
	public boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economy = 
				plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if(economy != null) {
			eco = economy.getProvider();
		}
		return (eco != null);
	}
	
	/*
	 * Get the economy
	 */
	public Economy getEconomy() {
		RegisteredServiceProvider<Economy> economy = 
				plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		eco = economy.getProvider();
		return eco;
	}
	
	/*
	 * Returns instance of VaultIntegration
	 */
	public static VaultHook getVaultHook() {
		if(vaultHook == null) {
			vaultHook = new VaultHook();
		}
		return vaultHook;
	}

}
