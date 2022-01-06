package xyz.scyllasrock.ScyCosmetics.spigot.objects;

public enum ItemFilter {
	
	SHOW_ALL, SHOW_OBTAINABLE, SHOW_UNLOCKED;
	
	
    private static ItemFilter[] vals = values();
    
    public ItemFilter next()
    {
        return vals[(this.ordinal() + 1) % vals.length];
    }

}
