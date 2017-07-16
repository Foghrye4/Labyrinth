package labyrinth.config;

import java.io.File;

import labyrinth.LabyrinthMod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LabyrinthConfig {
    public static enum IntOptions {
        DUNGEON_START_HEIGHT(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 
        		"A height from whom (downward) dungeons will be generated.");

        private final int minValue;
        private final int maxValue;
        private final int defaultValue;
        private final String description;
        private int value;

        private IntOptions(int minValue1, int maxValue1, int defaultValue1, String description1) {
            minValue = minValue1;
            maxValue = maxValue1;
            defaultValue = defaultValue1;
            description = description1;
            value = defaultValue;
        }

        public float getNormalValue() {
            return (float) (value - minValue) / (maxValue - minValue);
        }

        public void setValueFromNormal(float sliderValue) {
            value = minValue + (int) ((maxValue - minValue) * sliderValue);
        }

        public int getValue() {
            return value;
        }
    }

    public static enum FloatOptions {
        DUNGEON_BIOME_HEIGHT_UPPER_BOUND(-2.0f, 2.0f, 2.0f,
        		"Biome height base upper bound under which dungeons will be generated. "
        		+ "A few examples: Ocean biome: -1.0, Deep ocean biome: -1.8,"
        		+" Plains: 0.125, Forest hills: 0.45, Savanna Plateau: 1.5"),
        DUNGEON_BIOME_HEIGHT_LOWER_BOUND(-2.0f, 2.0f, -1.1f,
        		"Biome height base lower bound above which dungeons will be generated. "
        		+ "A few examples: Ocean biome: -1.0, Deep ocean biome: -1.8,"
        		+" Plains: 0.125, Forest hills: 0.45, Savanna Plateau: 1.5");

        private final float minValue;
        private final float maxValue;
        private final float defaultValue;
        private final String description;
        private float value;

        private FloatOptions(float minValue1, float maxValue1, float defaultValue1, String description1) {
            minValue = minValue1;
            maxValue = maxValue1;
            defaultValue = defaultValue1;
            description = description1;
            value = defaultValue;
        }

        public float getNormalValue() {
            return (float) (value - minValue) / (maxValue - minValue);
        }

        public void setValueFromNormal(float sliderValue) {
            value = minValue + (int) ((maxValue - minValue) * sliderValue);
        }

        public float getValue() {
            return value;
        }
    }

    public static enum BoolOptions {
        REMOVE_MOBS(true,
                "Enabling this option will remove all hostile mobs form all "
                        + " biomes spawnable lists. It is necessary to restart server for this option to work.");

        private final boolean defaultValue;
        private final String description;
        private boolean value;

        private BoolOptions(boolean defaultValue1, String description1) {
            defaultValue = defaultValue1;
            description = description1;
            value = defaultValue;
        }

        public boolean getValue() {
            return value;
        }
    }
    
    public static String getNicelyFormattedName(String name) {
        StringBuffer out = new StringBuffer();
        char char_ = '_';
        char prevchar = 0;
        for (char c : name.toCharArray()) {
            if (c != char_ && prevchar != char_) {
                out.append(String.valueOf(c).toLowerCase());
            } else if (c != char_) {
                out.append(String.valueOf(c));
            }
            prevchar = c;
        }
        return out.toString();
    }

    public Configuration configuration;

    public LabyrinthConfig(Configuration configuration) {
        loadConfig(configuration);
        syncConfig();
    }

    void loadConfig(Configuration configuration) {
        this.configuration = configuration;
    }
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.getModID().equals(LabyrinthMod.MODID)) {
        	LabyrinthMod.config.syncConfig();
        }
    }

    void syncConfig() {
        for (IntOptions configOption : IntOptions.values()) {
            configOption.value = configuration.getInt(getNicelyFormattedName(configOption.name()), Configuration.CATEGORY_GENERAL,
                    configOption.defaultValue, configOption.minValue, configOption.maxValue, configOption.description);
        }
        for (FloatOptions configOption : FloatOptions.values()) {
            configOption.value = configuration.getFloat(getNicelyFormattedName(configOption.name()), Configuration.CATEGORY_GENERAL,
                    configOption.defaultValue, configOption.minValue, configOption.maxValue, configOption.description);
        }
        for (BoolOptions configOption : BoolOptions.values()) {
            configOption.value = configuration.getBoolean(getNicelyFormattedName(configOption.name()), Configuration.CATEGORY_GENERAL,
                    configOption.defaultValue, configOption.description);
        }
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public int getStartHeight() {
        return IntOptions.DUNGEON_START_HEIGHT.getValue();
    }
    
	public static File getConfigFile() {
		File folder = new File(LabyrinthMod.proxy.getMinecraftDir(), "config");
		folder.mkdirs();
		return new File(folder, LabyrinthMod.MODID+".cfg");
	}

	public float getBiomeHeightUpperBound() {
		return FloatOptions.DUNGEON_BIOME_HEIGHT_UPPER_BOUND.getValue();
	}
	
	public float getBiomeHeightLowerBound() {
		return FloatOptions.DUNGEON_BIOME_HEIGHT_LOWER_BOUND.getValue();
	}

	public boolean shouldRemoveMobSpawn() {
		return BoolOptions.REMOVE_MOBS.getValue();
	}
}
