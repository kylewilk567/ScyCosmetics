package xyz.scyllasrock.ScyCosmetics.spigot.objects;

public enum CosmeticType {
//Should be of the same name as key specified in config
	PREFIX("&cPrefixes"), LAST_WORDS("&2Last words"), EMOTE_EQUIPMENT("&3Emote Equipment"), EMOTE_DANCE("&3Emote Dances"), ARROW_TRAIL("&bArrow trails")
	, PLAYER_TRAIL("&aPlayer trails"), KILL_EFFECT("&4Kill Effects"), AFK_EFFECT("&9AFK Effects"), LOG_MESSAGE("&1Log Messages"), TITLE("&eTitles");
	
	
    public final String label;

    private CosmeticType(String label) {
        this.label = label;
    }
	
}
