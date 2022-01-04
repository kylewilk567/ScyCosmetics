package xyz.scyllasrock.ScyCosmetics.spigot.objects;

public enum CosmeticTier {
	
	COMMON("&f"), UNCOMMON("&7"), RARE("&6"), EPIC("&5"), SPECIAL("&b"), LEGENDARY("&c"), MYTHIC("&a"), ARTIFACT("&8"), SEASONAL("&9"), LORE("&d");
	
    public final String colorCode;

    private CosmeticTier(String colorCode) {
        this.colorCode = colorCode;
    }
	
}
