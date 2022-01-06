package xyz.scyllasrock.ScyCosmetics.spigot.objects;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import xyz.scyllasrock.ScyCosmetics.util.TimeUtils;

public abstract class Cosmetic {
	
	private final String id;
	private final CosmeticType type;
	private final CosmeticTier tier;
	private final ItemStack displayItem;
	private final double buyPrice;
	private final List<String> purchaseableAfterTimes;
	private final List<String> purchaseableBeforeTimes;
	private final boolean isUnobtainable;
	
	public Cosmetic(String id, CosmeticType type, CosmeticTier tier, ItemStack displayItem, double buyPrice, List<String> purchaseableAfterTimes,
			List<String> purchaseableBeforeTimes, boolean isUnobtainable) {
		this.id = id;
		this.type = type;
		this.tier = tier;
		this.displayItem = displayItem;
		this.buyPrice = buyPrice;
		this.purchaseableAfterTimes = purchaseableAfterTimes;
		this.purchaseableBeforeTimes = purchaseableBeforeTimes;
		this.isUnobtainable = isUnobtainable;
	}
	
	public String getId() {
		return id;
	}
	
	public CosmeticType getType() {
		return type;
	}
	
	public CosmeticTier getTier() {
		return tier;
	}
	
	public ItemStack getDisplayItem() {
		return displayItem;
	}
	
	public double getBuyPrice() {
		return buyPrice;
	}
	
	public boolean isPurchaseable() {
		if(isUnobtainable) return false; //Check player here. If they set this item to unobtainable, then prevent purchase
		if(purchaseableAfterTimes.isEmpty() && purchaseableBeforeTimes.isEmpty()) return true;
		int maxSize = Math.max(purchaseableAfterTimes.size(), purchaseableBeforeTimes.size());
		for(int i = 0; i < maxSize; ++i) {
			String afterDate = null;
			if(i < purchaseableAfterTimes.size()) afterDate = purchaseableAfterTimes.get(i);
			String beforeDate = null;
			if(i < purchaseableBeforeTimes.size()) beforeDate = purchaseableBeforeTimes.get(i); 
			if(isValidDate(afterDate, beforeDate)) return true;
		}
		return false;
	}
	
	final public boolean isUnobtainable(){
		return isUnobtainable;
	}
	
	public abstract String[] getCosData();
	
	
	
	private boolean isValidDate(@Nullable String afterDate, @Nullable String beforeDate) {		
		Calendar beforeDay = Calendar.getInstance();
		long beforeLong = TimeUtils.getLongFromString(beforeDate);
		if(beforeLong > 0)
		beforeDay.setTimeInMillis(beforeLong);
		else beforeDay = null;
		
		Calendar afterDay = Calendar.getInstance();
		long afterLong = TimeUtils.getLongFromString(afterDate);
		if(afterLong > 0)
		afterDay.setTimeInMillis(afterLong);
		else afterDay = null;
		
		//Adjust before or after date if neither is null and before < after day
		if(beforeLong > 0 && afterLong > 0 && beforeLong < afterLong) {
			if(Calendar.getInstance().before(beforeDay)){
				afterDay.add(Calendar.YEAR, -1);
			}
			else beforeDay.add(Calendar.YEAR, 1);
		}
		
		if(afterDay != null) {
			if(Calendar.getInstance().before(afterDay)) return false;
		}
		if(beforeDay != null) {
			if(Calendar.getInstance().after(beforeDay)) return false;
		}
		
		return true;
	}
	
	


}
