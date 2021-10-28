package xyz.scyllasrock.ScyCosmetics.spigot.commands.Scycosmetics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import xyz.scyllasrock.ScyCosmetics.spigot.Main;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.ArrowTrail;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.Cosmetic;
import xyz.scyllasrock.ScyCosmetics.spigot.objects.CosmeticType;

public class Scycosmetics implements CommandExecutor, TabCompleter {
	
	Main plugin = Main.getPlugin(Main.class);


	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) { //**** TO BE REMOVED
			Player player = (Player) sender;
			if(!player.isOp()) return false;
		}
		
		
		if(args.length == 0) {
			Inventory inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&cTest Title"));
			int slot = 0;
			for(Cosmetic cos : plugin.getCosmetics().values()) {
				if(slot < 54) {
				ItemStack item = new ItemStack(Material.BLUE_WOOL);
				if(cos.getType().equals(CosmeticType.ARROW_TRAIL)) {
					ItemMeta meta = item.getItemMeta();
					ArrowTrail trail = (ArrowTrail) cos;
					meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', trail.getParticle().toString()));
					item.setItemMeta(meta);
				}

				inv.setItem(slot, item);
				++slot;
				}
			}
			Player player = (Player) sender;
			player.openInventory(inv);
		}
		
		return false;
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
		//First argument
		switch(args.length) {
		case 0:
			return help;
		
		case 1:

			String searching = "";

			if (args[0] != null) {
				searching = args[0].toLowerCase();
			}

			if ("create".startsWith(searching) && player.hasPermission("scycosmetics")) {
				help.add("create");
			}
			
		}
			
			return help;
	}

}
