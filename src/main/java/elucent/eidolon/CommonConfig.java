package elucent.eidolon;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CommonConfig {
    public static final int DEFAULT_CRUCIBLE_STEP_DURATION = 100;
    public static final int DEFAULT_MAX_ETHEREAL_HEALTH = 80;

    public static final int DEFAULT_WRAITH_SPAWN_WEIGHT = 60;
    public static final int DEFAULT_ZOMBIE_BRUTE_SPAWN_WEIGHT = 60;
    public static final int DEFAULT_RAVEN_SPAWN_WEIGHT = 6;
    public static final int DEFAULT_SLIMY_SLUG_SPAWN_WEIGHT = 7;
    public static final String[] DEFAULT_ZOMBIE_FOOD = {
            "minecraft:rotten_flesh",
            "eidolon:zombie_heart"
    };

    public static final int DEFAULT_LEAD_ORE_MIN_Y = 32;
    public static final int DEFAULT_LEAD_ORE_MAX_Y = 72;
    public static final int DEFAULT_LEAD_ORE_VEIN_SIZE = 6;
    public static final int DEFAULT_LEAD_ORE_VEIN_COUNT = 4;
    public static final int DEFAULT_DEEP_LEAD_ORE_MIN_Y = 12;
    public static final int DEFAULT_DEEP_LEAD_ORE_MAX_Y = 36;
    public static final int DEFAULT_DEEP_LEAD_ORE_VEIN_SIZE = 6;
    public static final int DEFAULT_DEEP_LEAD_ORE_VEIN_COUNT = 2;

    public static final int DEFAULT_SILVER_ORE_MIN_Y = 20;
    public static final int DEFAULT_SILVER_ORE_MAX_Y = 52;
    public static final int DEFAULT_SILVER_ORE_VEIN_SIZE = 6;
    public static final int DEFAULT_SILVER_ORE_VEIN_COUNT = 3;
    public static final int DEFAULT_DEEP_SILVER_ORE_MIN_Y = 8;
    public static final int DEFAULT_DEEP_SILVER_ORE_MAX_Y = 32;
    public static final int DEFAULT_DEEP_SILVER_ORE_VEIN_SIZE = 6;
    public static final int DEFAULT_DEEP_SILVER_ORE_VEIN_COUNT = 2;

    public static final double DEFAULT_LAB_RARITY = 4.0D;
    public static final double DEFAULT_STRAY_TOWER_RARITY = 4.0D;
    public static final double DEFAULT_CATACOMB_RARITY = 3.0D;
    public static final String[] DEFAULT_STRAY_TOWER_BIOME_KEYWORDS = {
            "taiga",
            "snow",
            "ice",
            "cold",
            "frozen",
            "spruce",
            "pine"
    };

    private static Configuration configuration;

    private static int crucibleStepDuration = DEFAULT_CRUCIBLE_STEP_DURATION;
    private static int maxEtherealHealth = DEFAULT_MAX_ETHEREAL_HEALTH;

    private static int wraithSpawnWeight = DEFAULT_WRAITH_SPAWN_WEIGHT;
    private static int zombieBruteSpawnWeight = DEFAULT_ZOMBIE_BRUTE_SPAWN_WEIGHT;
    private static int ravenSpawnWeight = DEFAULT_RAVEN_SPAWN_WEIGHT;
    private static int slimySlugSpawnWeight = DEFAULT_SLIMY_SLUG_SPAWN_WEIGHT;
    private static List<String> zombieFood = Collections.unmodifiableList(Arrays.asList(DEFAULT_ZOMBIE_FOOD));

    private static boolean leadEnabled = true;
    private static int leadOreMinY = DEFAULT_LEAD_ORE_MIN_Y;
    private static int leadOreMaxY = DEFAULT_LEAD_ORE_MAX_Y;
    private static int leadOreVeinSize = DEFAULT_LEAD_ORE_VEIN_SIZE;
    private static int leadOreVeinCount = DEFAULT_LEAD_ORE_VEIN_COUNT;
    private static int deepLeadOreMinY = DEFAULT_DEEP_LEAD_ORE_MIN_Y;
    private static int deepLeadOreMaxY = DEFAULT_DEEP_LEAD_ORE_MAX_Y;
    private static int deepLeadOreVeinSize = DEFAULT_DEEP_LEAD_ORE_VEIN_SIZE;
    private static int deepLeadOreVeinCount = DEFAULT_DEEP_LEAD_ORE_VEIN_COUNT;

    private static boolean silverEnabled = true;
    private static int silverOreMinY = DEFAULT_SILVER_ORE_MIN_Y;
    private static int silverOreMaxY = DEFAULT_SILVER_ORE_MAX_Y;
    private static int silverOreVeinSize = DEFAULT_SILVER_ORE_VEIN_SIZE;
    private static int silverOreVeinCount = DEFAULT_SILVER_ORE_VEIN_COUNT;
    private static int deepSilverOreMinY = DEFAULT_DEEP_SILVER_ORE_MIN_Y;
    private static int deepSilverOreMaxY = DEFAULT_DEEP_SILVER_ORE_MAX_Y;
    private static int deepSilverOreVeinSize = DEFAULT_DEEP_SILVER_ORE_VEIN_SIZE;
    private static int deepSilverOreVeinCount = DEFAULT_DEEP_SILVER_ORE_VEIN_COUNT;

    private static boolean labEnabled = true;
    private static double labRarity = DEFAULT_LAB_RARITY;
    private static boolean strayTowerEnabled = true;
    private static double strayTowerRarity = DEFAULT_STRAY_TOWER_RARITY;
    private static List<String> strayTowerBiomeKeywords = Collections.unmodifiableList(Arrays.asList(DEFAULT_STRAY_TOWER_BIOME_KEYWORDS));
    private static boolean catacombEnabled = true;
    private static double catacombRarity = DEFAULT_CATACOMB_RARITY;

    private CommonConfig() {
    }

    public static void init(File suggestedConfigFile) {
        configuration = new Configuration(new File(suggestedConfigFile.getParentFile(), Reference.MOD_ID + "-common.cfg"));
        sync();
    }

    public static int crucibleStepDuration() {
        return crucibleStepDuration;
    }

    public static int maxEtherealHealth() {
        return maxEtherealHealth;
    }

    public static int wraithSpawnWeight() {
        return wraithSpawnWeight;
    }

    public static int zombieBruteSpawnWeight() {
        return zombieBruteSpawnWeight;
    }

    public static int ravenSpawnWeight() {
        return ravenSpawnWeight;
    }

    public static int slimySlugSpawnWeight() {
        return slimySlugSpawnWeight;
    }

    public static List<String> zombieFood() {
        return zombieFood;
    }

    public static boolean leadEnabled() {
        return leadEnabled;
    }

    public static int leadOreMinY() {
        return leadOreMinY;
    }

    public static int leadOreMaxY() {
        return leadOreMaxY;
    }

    public static int leadOreVeinSize() {
        return leadOreVeinSize;
    }

    public static int leadOreVeinCount() {
        return leadOreVeinCount;
    }

    public static int deepLeadOreMinY() {
        return deepLeadOreMinY;
    }

    public static int deepLeadOreMaxY() {
        return deepLeadOreMaxY;
    }

    public static int deepLeadOreVeinSize() {
        return deepLeadOreVeinSize;
    }

    public static int deepLeadOreVeinCount() {
        return deepLeadOreVeinCount;
    }

    public static boolean silverEnabled() {
        return silverEnabled;
    }

    public static int silverOreMinY() {
        return silverOreMinY;
    }

    public static int silverOreMaxY() {
        return silverOreMaxY;
    }

    public static int silverOreVeinSize() {
        return silverOreVeinSize;
    }

    public static int silverOreVeinCount() {
        return silverOreVeinCount;
    }

    public static int deepSilverOreMinY() {
        return deepSilverOreMinY;
    }

    public static int deepSilverOreMaxY() {
        return deepSilverOreMaxY;
    }

    public static int deepSilverOreVeinSize() {
        return deepSilverOreVeinSize;
    }

    public static int deepSilverOreVeinCount() {
        return deepSilverOreVeinCount;
    }

    public static boolean labEnabled() {
        return labEnabled;
    }

    public static double labRarity() {
        return labRarity;
    }

    public static boolean strayTowerEnabled() {
        return strayTowerEnabled;
    }

    public static double strayTowerRarity() {
        return strayTowerRarity;
    }

    public static List<String> strayTowerBiomeKeywords() {
        return strayTowerBiomeKeywords;
    }

    public static boolean catacombEnabled() {
        return catacombEnabled;
    }

    public static double catacombRarity() {
        return catacombRarity;
    }

    private static void sync() {
        if (configuration == null) {
            return;
        }

        String generic = "generic";
        configuration.addCustomCategoryComment(generic,
                "Common Eidolon gameplay settings. Defaults preserve the current Legacy 1.12 behavior.");
        crucibleStepDuration = configuration.getInt("crucibleStepDuration", generic, DEFAULT_CRUCIBLE_STEP_DURATION, 20, 1200,
                "Reference duration in ticks for source-style crucible steps. Legacy crucible interactions remain immediate.");
        maxEtherealHealth = configuration.getInt("maxEtherealHealth", generic, DEFAULT_MAX_ETHEREAL_HEALTH, 0, 1000,
                "Maximum amount of ethereal health a player can have at once.");

        String mobs = "mobs";
        configuration.addCustomCategoryComment(mobs,
                "Common Eidolon mob spawning settings. Set a spawn weight to zero to disable that natural spawn.");
        wraithSpawnWeight = configuration.getInt("wraithSpawnWeight", mobs, DEFAULT_WRAITH_SPAWN_WEIGHT, 0, 1000,
                "Spawn weight for wraith entities.");
        zombieBruteSpawnWeight = configuration.getInt("zombieBruteSpawnWeight", mobs, DEFAULT_ZOMBIE_BRUTE_SPAWN_WEIGHT, 0, 1000,
                "Spawn weight for zombie brute entities.");
        ravenSpawnWeight = configuration.getInt("ravenSpawnWeight", mobs, DEFAULT_RAVEN_SPAWN_WEIGHT, 0, 1000,
                "Spawn weight for raven entities.");
        slimySlugSpawnWeight = configuration.getInt("slimySlugSpawnWeight", mobs, DEFAULT_SLIMY_SLUG_SPAWN_WEIGHT, 0, 1000,
                "Spawn weight for slimy slug entities.");

        String gameplay = "gameplay";
        configuration.addCustomCategoryComment(gameplay,
                "Common Eidolon gameplay compatibility lists. Defaults mirror source data tags where 1.12 has no direct tag system.");
        zombieFood = Collections.unmodifiableList(Arrays.asList(configuration.getStringList("zombieFood", gameplay,
                DEFAULT_ZOMBIE_FOOD, "Item ids that entities under the Undeath effect are allowed to eat.")));

        String world = "world";
        configuration.addCustomCategoryComment(world,
                "Common Eidolon world generation settings. Defaults preserve the current Legacy 1.12 behavior.");

        leadEnabled = configuration.getBoolean("leadEnabled", world, true,
                "Whether lead ore is enabled. Set to false to disable lead and deep lead ore spawning.");
        leadOreMinY = configuration.getInt("leadOreMinY", world, DEFAULT_LEAD_ORE_MIN_Y, 0, 255,
                "Minimum Y value for Legacy lead ore veins.");
        leadOreMaxY = configuration.getInt("leadOreMaxY", world, DEFAULT_LEAD_ORE_MAX_Y, 0, 255,
                "Maximum Y value for Legacy lead ore veins.");
        leadOreVeinSize = configuration.getInt("leadOreVeinSize", world, DEFAULT_LEAD_ORE_VEIN_SIZE, 1, 255,
                "Maximum number of blocks per Legacy lead ore vein.");
        leadOreVeinCount = configuration.getInt("leadOreVeinCount", world, DEFAULT_LEAD_ORE_VEIN_COUNT, 0, 255,
                "Number of Legacy lead ore veins per chunk.");
        deepLeadOreMinY = configuration.getInt("deepLeadOreMinY", world, DEFAULT_DEEP_LEAD_ORE_MIN_Y, 0, 255,
                "Minimum Y value for Legacy deep lead ore veins.");
        deepLeadOreMaxY = configuration.getInt("deepLeadOreMaxY", world, DEFAULT_DEEP_LEAD_ORE_MAX_Y, 0, 255,
                "Maximum Y value for Legacy deep lead ore veins.");
        deepLeadOreVeinSize = configuration.getInt("deepLeadOreVeinSize", world, DEFAULT_DEEP_LEAD_ORE_VEIN_SIZE, 1, 255,
                "Maximum number of blocks per Legacy deep lead ore vein.");
        deepLeadOreVeinCount = configuration.getInt("deepLeadOreVeinCount", world, DEFAULT_DEEP_LEAD_ORE_VEIN_COUNT, 0, 255,
                "Number of Legacy deep lead ore veins per chunk.");

        silverEnabled = configuration.getBoolean("silverEnabled", world, true,
                "Whether silver ore is enabled. Set to false to disable silver and deep silver ore spawning.");
        silverOreMinY = configuration.getInt("silverOreMinY", world, DEFAULT_SILVER_ORE_MIN_Y, 0, 255,
                "Minimum Y value for Legacy silver ore veins.");
        silverOreMaxY = configuration.getInt("silverOreMaxY", world, DEFAULT_SILVER_ORE_MAX_Y, 0, 255,
                "Maximum Y value for Legacy silver ore veins.");
        silverOreVeinSize = configuration.getInt("silverOreVeinSize", world, DEFAULT_SILVER_ORE_VEIN_SIZE, 1, 255,
                "Maximum number of blocks per Legacy silver ore vein.");
        silverOreVeinCount = configuration.getInt("silverOreVeinCount", world, DEFAULT_SILVER_ORE_VEIN_COUNT, 0, 255,
                "Number of Legacy silver ore veins per chunk.");
        deepSilverOreMinY = configuration.getInt("deepSilverOreMinY", world, DEFAULT_DEEP_SILVER_ORE_MIN_Y, 0, 255,
                "Minimum Y value for Legacy deep silver ore veins.");
        deepSilverOreMaxY = configuration.getInt("deepSilverOreMaxY", world, DEFAULT_DEEP_SILVER_ORE_MAX_Y, 0, 255,
                "Maximum Y value for Legacy deep silver ore veins.");
        deepSilverOreVeinSize = configuration.getInt("deepSilverOreVeinSize", world, DEFAULT_DEEP_SILVER_ORE_VEIN_SIZE, 1, 255,
                "Maximum number of blocks per Legacy deep silver ore vein.");
        deepSilverOreVeinCount = configuration.getInt("deepSilverOreVeinCount", world, DEFAULT_DEEP_SILVER_ORE_VEIN_COUNT, 0, 255,
                "Number of Legacy deep silver ore veins per chunk.");

        labEnabled = configuration.getBoolean("labEnabled", world, true,
                "Whether the lab structure is enabled. Set to false to disable spawning.");
        labRarity = configuration.getFloat("labRarity", world, (float) DEFAULT_LAB_RARITY, 1.0F, 1000.0F,
                "Rarity of the lab structure. Higher numbers mean rarer structures.");
        strayTowerEnabled = configuration.getBoolean("strayTowerEnabled", world, true,
                "Whether the stray tower structure is enabled. Set to false to disable spawning.");
        strayTowerRarity = configuration.getFloat("strayTowerRarity", world, (float) DEFAULT_STRAY_TOWER_RARITY, 1.0F, 1000.0F,
                "Rarity of the stray tower structure. Higher numbers mean rarer structures.");
        strayTowerBiomeKeywords = Collections.unmodifiableList(Arrays.asList(configuration.getStringList("strayTowerBiomeKeywords", world,
                DEFAULT_STRAY_TOWER_BIOME_KEYWORDS,
                "Lowercase biome-name keywords used to approximate the 1.20 stray tower biome tag in 1.12. Defaults preserve current behavior.")));
        catacombEnabled = configuration.getBoolean("catacombEnabled", world, true,
                "Whether the catacomb structure is enabled. Set to false to disable spawning.");
        catacombRarity = configuration.getFloat("catacombRarity", world, (float) DEFAULT_CATACOMB_RARITY, 1.0F, 1000.0F,
                "Rarity of the catacomb structure. Higher numbers mean rarer structures.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

}
