package xyz.scyllasrock.ScyCosmetics.spigot.objects;

public enum CosmeticTier {
	
	COMMON("&f"),
	UNCOMMON("&7"),
	RARE("&6"),
	EPIC("&5"),
	SPECIAL("&b"),
	LEGENDARY("&c"),
	MYTHIC("&a"),
	ARTIFACT("&8"),
	SEASONAL("&9"),
	LORE("&d") {
        @Override
        public CosmeticTier next() {
            return values()[0]; //Return first one
        };
    };;
	

    public CosmeticTier next() {
    	// No bounds checking required here, because the last instance overrides
    	return values()[ordinal() + 1];
	}
	
    public final String colorCode;

    private CosmeticTier(String colorCode) {
        this.colorCode = colorCode;
    }
	
}
