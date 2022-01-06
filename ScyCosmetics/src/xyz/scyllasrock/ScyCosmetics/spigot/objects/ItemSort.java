package xyz.scyllasrock.ScyCosmetics.spigot.objects;

public enum ItemSort {

	NAME, RARITY_ASCENDING, RARITY_DESCENDING, LEAST_RECENT, MOST_RECENT;
	
    private static ItemSort[] vals = values();
    
    public ItemSort next()
    {
        return vals[(this.ordinal() + 1) % vals.length];
    }
    
}
