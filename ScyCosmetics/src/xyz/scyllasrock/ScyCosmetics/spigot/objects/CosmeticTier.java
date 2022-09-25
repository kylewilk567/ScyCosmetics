package xyz.scyllasrock.ScyCosmetics.spigot.objects;

public enum CosmeticTier {
	
	COMMON("&f") {
		@Override
		public CosmeticTier prev() {
			return values()[values().length - 1];
		}
	},
	UNCOMMON("&7"),
	RARE("&6"),
	EPIC("&5"),
	SPECIAL("&b"),
	LEGENDARY("&c"),
	MYTHIC("&a"),
	ARTIFACT("&8"),
	SEASONAL("&9") {
		@Override
		public CosmeticTier prev() {
			return CosmeticTier.UNCOMMON; //Used for givew to prevent giving lots of rare items
		}
	},
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
    
    public CosmeticTier prev() {
    	
    	return values()[ordinal() - 1];
    }
	
    public final String colorCode;

    private CosmeticTier(String colorCode) {
        this.colorCode = colorCode;
    }
	
}
