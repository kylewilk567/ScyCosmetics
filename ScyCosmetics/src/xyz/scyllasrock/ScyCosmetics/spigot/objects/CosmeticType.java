package xyz.scyllasrock.ScyCosmetics.spigot.objects;

public enum CosmeticType {
//Should be of the same name as key specified in config
	PREFIX("&cPrefixes"), LAST_WORDS("&2Last words"), STAND_EMOTE("&3Emotes"), ARROW_TRAIL("&bArrow trails"), PLAYER_TRAIL("&aPlayer trails");
	
	
    public final String label;

    private CosmeticType(String label) {
        this.label = label;
    }
	
}
