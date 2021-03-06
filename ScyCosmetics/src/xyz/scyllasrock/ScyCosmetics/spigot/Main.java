package xyz.scyllasrock.ScyCosmetics.spigot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import me.jet315.antiafkpro.AntiAFKProAPI;
import me.jet315.antiafkpro.JetsAntiAFKPro;
import net.milkbowl.vault.economy.Economy;
import xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics.Scycosmetics;
import xyz.scyllasrock.ScyCosmetics.spigot.commands.Semote.Semote;
import xyz.scyllasrock.ScyCosmetics.spigot.data.ConfigManager;
import xyz.scyllasrock.ScyCosmetics.spigot.data.CosmeticDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.data.DirtyDataTimer;
import xyz.scyllasrock.ScyCosmetics.spigot.data.PlayerDataHandler;
import xyz.scyllasrock.ScyCosmetics.spigot.hooks.ScyCosmeticsExpansion;
import xyz.scyllasrock.ScyCosmetics.spigot.hooks.VaultHook;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.AFKParticleListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.ArrowTrailListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.CosInventoryListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.EmoteDanceInvListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.EmoteListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.KillEffectListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.LastWordsListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.LogMessageListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.ManualAFKListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.MiscAFKEffectListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.PlayerDataListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.listener.PlayerTrailListeners;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.util.AfkDetectionTimer;

public class Main extends JavaPlugin {
	
	private HashMap<String, Cosmetic> cosmetics;
	DirtyDataTimer dirtyTimer;
	AfkDetectionTimer afkDetTimer;
	private AntiAFKProAPI afkAPI;
	private Economy eco;
	private static Main instance;
	private boolean premiumVanishSupportEnabled = false;
	private List<ArmorStand> activeEmoteStands;
	
	private final String[] name = {
	"   _____            ______                          __  _          ",
	"   / ___/_______  __/ ____/___  _________ ___  ___  / /_(_)_________",
	"   \\__ \\/ ___/ / / / /   / __ \\/ ___/ __ `__ \\/ _ \\/ __/ / ___/ ___/",
	"  ___/ / /__/ /_/ / /___/ /_/ (__  ) / / / / /  __/ /_/ / /__(__  ) ",
	" /____/\\___/\\__, /\\____/\\____/____/_/ /_/ /_/\\___/\\__/_/\\___/____/  ",
	"           /____/",
	" ",
	"by kwilk"
	};

	@Override
	public void onEnable() {
		
		instance = this;
		
		for(String s : name) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + s);
		}
		
		activeEmoteStands = new ArrayList<ArmorStand>();
		
		//Load dependencies
		
		//ScyUtility
		if(Bukkit.getPluginManager().getPlugin("ScyUtility") != null) {
			//do nothing
		}
		else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ScyCosmetics >> Error! ScyUtility is a dependency of this plugin and was not found. Disabling ScyCosmetics.");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		//Vault
		VaultHook econ = new VaultHook();
		if(!(econ.setupEconomy())) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Scycosmetics >> You must have Vault installed and an economy plugin. Disabling ScyCosmetics.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		eco = econ.getEconomy();
		
		//JetsAntiAfkPro - soft
		if(Bukkit.getPluginManager().getPlugin("JetsAntiAFKPro") != null) {
			afkAPI = ((JetsAntiAFKPro) Bukkit.getPluginManager().getPlugin("JetsAntiAFKPro")).getAntiAFKProAPI();
		}
		else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Scycosmetics >> JetsAntiAFKPro not found. Using internal afk system.");
		}
		
		//Placeholder API - soft
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ScyCosmeticsExpansion(this).register();
		} 
		else {
    	  Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "PlaceholderAPI not found. Disabled ScyCosmetics expansion."
    	  		+ " Plugin will function without prefixes.");
      }
		
		//PremiumVanish - soft
		if(Bukkit.getPluginManager().getPlugin("PremiumVanish") != null) {
			premiumVanishSupportEnabled = true;
		}
		
		
		//Set up files
		ConfigManager configMang = ConfigManager.getConfigMang();
		configMang.setupConfig();
		
		// initialize cosmetics
		CosmeticDataHandler cosmeticHandler = CosmeticDataHandler.getCosmeticHandler();
		cosmetics = cosmeticHandler.initializeCosmetics();
		
		// initialize playerobjects of currently online players
		PlayerDataHandler playerHandler = PlayerDataHandler.getPlayerHandler();
		playerHandler.initializePlayerObjects();
		
		//Set command executors
		Bukkit.getPluginCommand("scos").setExecutor(new Scycosmetics());
		Bukkit.getPluginCommand("semote").setExecutor(new Semote());
		
		//Set listeners
		Bukkit.getPluginManager().registerEvents(new ArrowTrailListeners(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDataListeners(), this);
//		Bukkit.getPluginManager().registerEvents(new ArrowTrailListenersOLD(), this);
		Bukkit.getPluginManager().registerEvents(new CosInventoryListeners(), this);
		Bukkit.getPluginManager().registerEvents(new LastWordsListeners(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerTrailListeners(), this);
		Bukkit.getPluginManager().registerEvents(new LogMessageListeners(), this);
		Bukkit.getPluginManager().registerEvents(new EmoteListeners(), this);
		Bukkit.getPluginManager().registerEvents(new EmoteDanceInvListeners(), this);
		Bukkit.getPluginManager().registerEvents(new KillEffectListeners(), this);
		Bukkit.getPluginManager().registerEvents(new ManualAFKListeners(), this);
		Bukkit.getPluginManager().registerEvents(new MiscAFKEffectListeners(), this);
		
		if(afkAPI != null) Bukkit.getPluginManager().registerEvents(new AFKParticleListeners(), this);
		
		//Start dirty data timer
		dirtyTimer = new DirtyDataTimer();
		dirtyTimer.scheduleTimer();
		
		//Start afk detection timer
		afkDetTimer = new AfkDetectionTimer();
		afkDetTimer.scheduleTimer();		
	}
	public static BukkitTask tasko;
	@Override
	public void onDisable() {

		//Remove all active emote armor stands
		for(ArmorStand stand : this.activeEmoteStands) {
			stand.remove();
		}
		
		//Stop dirty data timer
		if(dirtyTimer != null) dirtyTimer.stopTimer();
		
		//Stop afkDet Timer
		if(afkDetTimer != null)	afkDetTimer.stopTimer();
	}

	public static Main getInstance() {
		return instance;
	}
	
	public boolean premVanishEnabled() {
		return premiumVanishSupportEnabled;
	}
	
	public Cosmetic getCosmeticFromId(String id) {
		return cosmetics.get(id);
	}
	
	public HashMap<String, Cosmetic> getCosmetics(){
		return cosmetics;
	}
	
	public List<ArmorStand> getActiveEmoteStands(){
		return activeEmoteStands;
	}
	
	public AntiAFKProAPI getAFKApi() {
		return afkAPI;
	}
	
	public Economy getVaultEco() {
		return eco;
	}
	
}
