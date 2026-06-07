package elucent.eidolon.spell;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import elucent.eidolon.Eidolon;
import elucent.eidolon.Reference;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraft.world.World;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class AltarRituals {
    private static final String RESOURCE_DIR = "assets/eidolon/altar_rituals";
    private static final JsonContext JSON_CONTEXT = new JsonContext(Reference.MOD_ID);
    private static final Map<ResourceLocation, AltarRitual> RITUALS = new LinkedHashMap<>();
    private static final List<Runnable> CUSTOMIZATIONS = new ArrayList<>();
    private static boolean initialized;

    private AltarRituals() {
    }

    public static void init() {
        initialized = false;
        RITUALS.clear();
        loadFromClasspath();
        loadFromModSource();
        loadFromDevelopmentSource();
        registerMissingBuiltIns();
        initialized = true;
        applyCustomizations();
        Eidolon.LOGGER.info("Loaded {} Eidolon altar rituals", RITUALS.size());
    }

    public static List<AltarRitual> getRituals() {
        return Collections.unmodifiableList(new ArrayList<>(RITUALS.values()));
    }

    public static AltarRitual find(AltarInfo info) {
        return find(info, null);
    }

    public static AltarRitual find(AltarInfo info, EntityPlayer player) {
        return find(info, null, null, player);
    }

    public static AltarRitual find(AltarInfo info, World world, BlockPos origin, EntityPlayer player) {
        for (AltarRitual ritual : RITUALS.values()) {
            if (ritual.matches(info, world, origin, player)) {
                return ritual;
            }
        }
        return null;
    }

    public static AltarRitual find(ResourceLocation id) {
        return RITUALS.get(id);
    }

    public static void addRitual(ResourceLocation id, double capacity, double power, ItemStack result,
                                 AltarRitual.BehaviorType behavior, Ingredient focus, Ingredient sacrifice,
                                 ResourceLocation entity, float healthCost, List<Ingredient> offerings) {
        ItemStack resultCopy = result.copy();
        Ingredient[] offeringCopy = offerings.toArray(new Ingredient[0]);
        Ingredient focusCopy = focus == null ? Ingredient.EMPTY : focus;
        addCustomization(() -> RITUALS.put(id, new AltarRitual(id, capacity, power, resultCopy, behavior,
                focusCopy, sacrifice, entity, healthCost, offeringCopy)));
    }

    public static boolean removeRitual(ResourceLocation id) {
        addCustomization(() -> RITUALS.remove(id));
        return initialized && !RITUALS.containsKey(id);
    }

    public static int removeRitualsByOutput(Ingredient output) {
        int count = initialized ? countRitualsByOutput(output) : 0;
        addCustomization(() -> RITUALS.entrySet().removeIf(entry -> output.apply(entry.getValue().getResult())));
        return count;
    }

    public static int removeAllRituals() {
        int count = initialized ? RITUALS.size() : 0;
        addCustomization(RITUALS::clear);
        return count;
    }

    private static void addCustomization(Runnable customization) {
        CUSTOMIZATIONS.add(customization);
        if (initialized) {
            customization.run();
        }
    }

    private static void applyCustomizations() {
        for (Runnable customization : CUSTOMIZATIONS) {
            customization.run();
        }
    }

    private static int countRitualsByOutput(Ingredient output) {
        int count = 0;
        for (AltarRitual ritual : RITUALS.values()) {
            if (output.apply(ritual.getResult())) {
                count++;
            }
        }
        return count;
    }

    public static AltarRitual findBySacrifice(ItemStack sacrifice) {
        if (sacrifice.isEmpty()) {
            return null;
        }
        for (AltarRitual ritual : RITUALS.values()) {
            if (ritual.matchesSacrifice(sacrifice)) {
                return ritual;
            }
        }
        return null;
    }

    private static void loadFromClasspath() {
        try {
            Enumeration<java.net.URL> urls = AltarRituals.class.getClassLoader().getResources(RESOURCE_DIR);
            while (urls.hasMoreElements()) {
                java.net.URL url = urls.nextElement();
                if ("file".equals(url.getProtocol())) {
                    loadFromDirectory(Paths.get(url.toURI()));
                } else if ("jar".equals(url.getProtocol())) {
                    String path = url.getPath();
                    int separator = path.indexOf("!/");
                    if (separator >= 0) {
                        loadFromJar(Paths.get(new java.net.URI(path.substring(0, separator))).toFile());
                    }
                }
            }
        } catch (IOException e) {
            Eidolon.LOGGER.error("Failed to scan Eidolon altar rituals", e);
        } catch (Exception e) {
            Eidolon.LOGGER.error("Failed to locate Eidolon altar ritual resources", e);
        }
    }

    private static void loadFromModSource() {
        try {
            Path root = Loader.instance().getIndexedModList().get(Reference.MOD_ID).getSource().toPath();
            if (Files.isDirectory(root)) {
                loadFromDirectory(root.resolve(RESOURCE_DIR));
            } else if (Files.isRegularFile(root)) {
                loadFromJar(root.toFile());
            }
        } catch (Exception e) {
            Eidolon.LOGGER.error("Failed to scan Eidolon altar rituals from mod source", e);
        }
    }

    private static void loadFromDevelopmentSource() {
        try {
            Path current = Paths.get("").toAbsolutePath();
            for (int i = 0; i < 4 && current != null; i++) {
                loadFromDirectory(current.resolve("src/main/resources").resolve(RESOURCE_DIR));
                current = current.getParent();
            }
        } catch (Exception e) {
            Eidolon.LOGGER.error("Failed to scan Eidolon altar rituals from development source", e);
        }
    }

    private static void loadFromDirectory(Path ritualDir) throws IOException {
        if (!Files.isDirectory(ritualDir)) {
            return;
        }
        Files.walk(ritualDir)
                .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))
                .sorted()
                .forEach(AltarRituals::loadRitual);
    }

    private static void loadFromJar(java.io.File file) throws IOException {
        try (JarFile jar = new JarFile(file)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().startsWith(RESOURCE_DIR + "/") && entry.getName().endsWith(".json")) {
                    ResourceLocation id = new ResourceLocation(Reference.MOD_ID, stripExtension(Paths.get(entry.getName()).getFileName().toString()));
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entry), StandardCharsets.UTF_8))) {
                        loadRitual(id, reader);
                    } catch (Exception e) {
                        Eidolon.LOGGER.error("Failed to load altar ritual {}", id, e);
                    }
                }
            }
        }
    }

    private static void loadRitual(Path path) {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, stripExtension(path.getFileName().toString()));
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            loadRitual(id, reader);
        } catch (Exception e) {
            Eidolon.LOGGER.error("Failed to load altar ritual {}", id, e);
        }
    }

    private static void loadRitual(ResourceLocation id, BufferedReader reader) {
        JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
        String type = JsonUtils.getString(json, "type", "");
        if (!"eidolon:altar_ritual".equals(type)) {
            return;
        }
        RITUALS.put(id, parseRitual(id, json));
    }

    private static AltarRitual parseRitual(ResourceLocation id, JsonObject json) {
        AltarRitual.BehaviorType behavior = parseBehavior(JsonUtils.getString(json, "behavior", "item_result"));
        double capacity = JsonUtils.getFloat(json, "capacity", 0.0F);
        double power = JsonUtils.getFloat(json, "power", 0.0F);
        float health = JsonUtils.getFloat(json, "health", 0.0F);
        ItemStack result = json.has("result")
                ? CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), JSON_CONTEXT)
                : ItemStack.EMPTY;
        Ingredient focus = json.has("focus") ? parseIngredient(json.get("focus")) : Ingredient.EMPTY;
        Ingredient sacrifice = json.has("sacrifice") ? parseIngredient(json.get("sacrifice")) : null;
        ResourceLocation entity = json.has("entity") ? new ResourceLocation(JsonUtils.getString(json, "entity")) : null;
        List<Ingredient> offerings = parseOfferings(JsonUtils.getJsonArray(json, "offerings", new JsonArray()));
        return new AltarRitual(id, capacity, power, result, behavior, focus, sacrifice, entity, health, offerings.toArray(new Ingredient[0]));
    }

    private static AltarRitual.BehaviorType parseBehavior(String behavior) {
        if ("item_transform".equals(behavior)) {
            return AltarRitual.BehaviorType.ITEM_TRANSFORM;
        }
        if ("sanguine".equals(behavior)) {
            return AltarRitual.BehaviorType.SANGUINE;
        }
        if ("item_charge".equals(behavior)) {
            return AltarRitual.BehaviorType.ITEM_CHARGE;
        }
        if ("entity_summon".equals(behavior)) {
            return AltarRitual.BehaviorType.ENTITY_SUMMON;
        }
        if ("absorption".equals(behavior)) {
            return AltarRitual.BehaviorType.ABSORPTION;
        }
        if ("purify".equals(behavior)) {
            return AltarRitual.BehaviorType.PURIFY;
        }
        if ("crystal".equals(behavior)) {
            return AltarRitual.BehaviorType.CRYSTAL;
        }
        if ("allure".equals(behavior)) {
            return AltarRitual.BehaviorType.ALLURE;
        }
        if ("repelling".equals(behavior)) {
            return AltarRitual.BehaviorType.REPELLING;
        }
        if ("deceit".equals(behavior)) {
            return AltarRitual.BehaviorType.DECEIT;
        }
        if ("daylight".equals(behavior)) {
            return AltarRitual.BehaviorType.DAYLIGHT;
        }
        if ("moonlight".equals(behavior)) {
            return AltarRitual.BehaviorType.MOONLIGHT;
        }
        return AltarRitual.BehaviorType.ITEM_RESULT;
    }

    private static List<Ingredient> parseOfferings(JsonArray array) {
        List<Ingredient> offerings = new ArrayList<>();
        for (JsonElement element : array) {
            offerings.add(parseIngredient(element));
        }
        return offerings;
    }

    private static Ingredient parseIngredient(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return Ingredient.EMPTY;
        }
        if (element.isJsonObject()) {
            return CraftingHelper.getIngredient(element.getAsJsonObject(), JSON_CONTEXT);
        }
        return CraftingHelper.getIngredient(element);
    }

    private static void registerMissingBuiltIns() {
        putMissing("lesser_soul_gem", 2.0D, 2.0D, new ItemStack(ModItems.LESSER_SOUL_GEM),
                AltarRitual.BehaviorType.ITEM_RESULT, Ingredient.EMPTY,
                ingredient(ModItems.SOUL_SHARD),
                ingredient(ModItems.SOUL_SHARD));
        putMissing("death_essence", 3.0D, 3.0D, new ItemStack(ModItems.DEATH_ESSENCE),
                AltarRitual.BehaviorType.ITEM_RESULT, Ingredient.EMPTY,
                ingredient(ModItems.SOUL_SHARD),
                ingredient(ModItems.TATTERED_CLOTH),
                ingredient(Items.BONE));
        putMissingWithSacrifice("purify", 3.0D, 3.0D, ItemStack.EMPTY,
                AltarRitual.BehaviorType.PURIFY, Ingredient.EMPTY, ingredient(Items.SPECKLED_MELON),
                ingredient(Item.getItemFromBlock(ModBlocks.ENCHANTED_ASH)),
                ingredient(Item.getItemFromBlock(ModBlocks.ENCHANTED_ASH)),
                ingredient(potion(PotionTypes.HEALING)),
                ingredient(ModItems.SOUL_SHARD),
                ingredient(ModItems.SOUL_SHARD));
        putMissing("wraith_heart", 3.0D, 4.0D, new ItemStack(ModItems.WRAITH_HEART),
                AltarRitual.BehaviorType.ITEM_RESULT, Ingredient.EMPTY,
                ingredient(ModItems.ZOMBIE_HEART),
                ingredient(ModItems.TATTERED_CLOTH),
                ingredient(ModItems.SOUL_SHARD),
                ingredient(ModItems.DEATH_ESSENCE));
        putMissing("wicked_weave", 3.0D, 3.0D, new ItemStack(ModItems.WICKED_WEAVE),
                AltarRitual.BehaviorType.ITEM_RESULT, Ingredient.EMPTY,
                ingredient(ModItems.TATTERED_CLOTH),
                ingredient(ModItems.TATTERED_CLOTH),
                ingredient(ModItems.DEATH_ESSENCE),
                ingredient(Items.STRING));
        putMissing("imbued_bones", 3.0D, 3.0D, new ItemStack(ModItems.IMBUED_BONES),
                AltarRitual.BehaviorType.ITEM_RESULT, Ingredient.EMPTY,
                ingredient(Items.BONE),
                ingredient(Items.BONE),
                ingredient(ModItems.SOUL_SHARD),
                ingredient(ModItems.DEATH_ESSENCE));
        putMissing("soulbone_amulet", 4.0D, 4.0D, new ItemStack(ModItems.SOULBONE_AMULET),
                AltarRitual.BehaviorType.ITEM_TRANSFORM, ingredient(ModItems.BASIC_AMULET),
                ingredient(ModItems.IMBUED_BONES),
                ingredient(ModItems.LESSER_SOUL_GEM),
                ingredient(ModItems.SOUL_SHARD),
                ingredient(Items.BONE));
        putMissing("soulfire_recharging", 3.0D, 3.0D, new ItemStack(ModItems.SOULFIRE_WAND),
                AltarRitual.BehaviorType.ITEM_CHARGE, ingredient(ModItems.SOULFIRE_WAND),
                ingredient(ModItems.LESSER_SOUL_GEM),
                ingredient(Items.BLAZE_POWDER),
                ingredient(Items.BLAZE_POWDER),
                ingredient(Items.REDSTONE),
                ingredient(Items.REDSTONE));
        putMissing("bonechill_recharging", 3.0D, 3.0D, new ItemStack(ModItems.BONECHILL_WAND),
                AltarRitual.BehaviorType.ITEM_CHARGE, ingredient(ModItems.BONECHILL_WAND),
                ingredient(ModItems.LESSER_SOUL_GEM),
                ingredient(Items.SNOWBALL),
                ingredient(Items.SNOWBALL),
                ingredient(Items.REDSTONE),
                ingredient(Items.REDSTONE));
        putMissingSummon("summon_zombie", 3.0D, 3.0D, spawnEgg("minecraft:zombie"),
                new ResourceLocation("minecraft:zombie"), ingredient(Items.ROTTEN_FLESH),
                ingredient(ModItems.SOUL_SHARD),
                ingredient(Items.ROTTEN_FLESH));
        putMissingSummon("summon_skeleton", 3.0D, 3.0D, spawnEgg("minecraft:skeleton"),
                new ResourceLocation("minecraft:skeleton"), ingredient(Items.BONE),
                ingredient(ModItems.SOUL_SHARD),
                ingredient(Items.BONE));
        putMissingSummon("summon_husk", 3.0D, 3.0D, spawnEgg("minecraft:husk"),
                new ResourceLocation("minecraft:husk"), ingredient(Item.getItemFromBlock(Blocks.SAND)),
                ingredient(ModItems.SOUL_SHARD),
                ingredient(Items.ROTTEN_FLESH));
        putMissingSummon("summon_stray", 3.0D, 3.0D, spawnEgg("minecraft:stray"),
                new ResourceLocation("minecraft:stray"), ingredient(Items.STRING),
                ingredient(ModItems.SOUL_SHARD),
                ingredient(Items.BONE));
        putMissingSummon("summon_wither_skeleton", 4.0D, 4.0D, spawnEgg("minecraft:wither_skeleton"),
                new ResourceLocation("minecraft:wither_skeleton"), ingredient(Item.getItemFromBlock(Blocks.SOUL_SAND)),
                ingredient(ModItems.SOUL_SHARD),
                ingredient(Items.BONE));
        putMissing("absorption", 4.0D, 4.0D, new ItemStack(ModItems.SUMMONING_STAFF),
                AltarRitual.BehaviorType.ABSORPTION, ingredient(ModItems.SUMMONING_STAFF),
                ingredient(ModItems.DEATH_ESSENCE),
                ingredient(ModItems.TATTERED_CLOTH),
                ingredient(ModItems.TATTERED_CLOTH),
                ingredient(Items.BONE),
                ingredient(ModItems.SOUL_SHARD),
                ingredient(ModItems.SOUL_SHARD));
    }

    private static void putMissing(String name, double capacity, double power, ItemStack result,
                                   AltarRitual.BehaviorType behavior, Ingredient focus, Ingredient... offerings) {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, name);
        if (!RITUALS.containsKey(id)) {
            RITUALS.put(id, new AltarRitual(id, capacity, power, result, behavior, focus, null, missingHealthCost(name), offerings));
        }
    }

    private static void putMissingWithSacrifice(String name, double capacity, double power, ItemStack result,
                                                AltarRitual.BehaviorType behavior, Ingredient focus,
                                                Ingredient sacrifice, Ingredient... offerings) {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, name);
        if (!RITUALS.containsKey(id)) {
            RITUALS.put(id, new AltarRitual(id, capacity, power, result, behavior, focus,
                    sacrifice, null, missingHealthCost(name), offerings));
        }
    }

    private static void putMissingSummon(String name, double capacity, double power, ItemStack result,
                                         ResourceLocation entity, Ingredient focus, Ingredient... offerings) {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, name);
        if (!RITUALS.containsKey(id)) {
            RITUALS.put(id, new AltarRitual(id, capacity, power, result,
                    AltarRitual.BehaviorType.ENTITY_SUMMON, focus, ingredient(Items.COAL, 1), entity, 0.0F, offerings));
        }
    }

    private static float missingHealthCost(String name) {
        return 0.0F;
    }

    private static Ingredient ingredient(Item item) {
        return Ingredient.fromStacks(new ItemStack(item));
    }

    private static Ingredient ingredient(Item item, int metadata) {
        return Ingredient.fromStacks(new ItemStack(item, 1, metadata));
    }

    private static Ingredient ingredient(ItemStack stack) {
        return Ingredient.fromStacks(stack);
    }

    private static ItemStack potion(PotionType type) {
        return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), type);
    }

    private static ItemStack spawnEgg(String entityId) {
        ItemStack stack = new ItemStack(Items.SPAWN_EGG);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound entityTag = new NBTTagCompound();
        entityTag.setString("id", entityId);
        tag.setTag("EntityTag", entityTag);
        stack.setTagCompound(tag);
        return stack;
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(0, dot) : fileName;
    }
}
