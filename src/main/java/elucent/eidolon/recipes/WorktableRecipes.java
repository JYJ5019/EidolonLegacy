package elucent.eidolon.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import elucent.eidolon.Eidolon;
import elucent.eidolon.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.fml.common.Loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class WorktableRecipes {
    private static final String RESOURCE_DIR = "assets/eidolon/worktable_recipes";
    private static final JsonContext JSON_CONTEXT = new JsonContext(Reference.MOD_ID);
    private static final Map<ResourceLocation, WorktableRecipe> RECIPES = new LinkedHashMap<>();
    private static final List<Runnable> CUSTOMIZATIONS = new ArrayList<>();
    private static boolean initialized;

    private WorktableRecipes() {
    }

    public static void init() {
        initialized = false;
        RECIPES.clear();
        loadFromClasspath();
        loadFromModSource();
        initialized = true;
        applyCustomizations();
        Eidolon.LOGGER.info("Loaded {} Eidolon worktable recipes", RECIPES.size());
    }

    private static void loadFromClasspath() {
        try {
            Enumeration<java.net.URL> urls = WorktableRecipes.class.getClassLoader().getResources(RESOURCE_DIR);
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
            Eidolon.LOGGER.error("Failed to scan Eidolon worktable recipes", e);
        } catch (Exception e) {
            Eidolon.LOGGER.error("Failed to locate Eidolon worktable recipe resources", e);
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
            Eidolon.LOGGER.error("Failed to scan Eidolon worktable recipes from mod source", e);
        }
    }

    public static List<WorktableRecipe> getRecipes() {
        return Collections.unmodifiableList(new ArrayList<>(RECIPES.values()));
    }

    public static WorktableRecipe getRecipe(ResourceLocation id) {
        return RECIPES.get(id);
    }

    public static void addRecipe(ResourceLocation id, Ingredient[] grid, Ingredient[] reagents, ItemStack result) {
        Ingredient[] gridCopy = Arrays.copyOf(grid, grid.length);
        Ingredient[] reagentCopy = Arrays.copyOf(reagents, reagents.length);
        ItemStack resultCopy = result.copy();
        addCustomization(() -> RECIPES.put(id, new WorktableRecipe(id, gridCopy, reagentCopy, resultCopy)));
    }

    public static boolean removeRecipe(ResourceLocation id) {
        addCustomization(() -> RECIPES.remove(id));
        return initialized && !RECIPES.containsKey(id);
    }

    public static int removeRecipesByOutput(Ingredient output) {
        int count = initialized ? countRecipesByOutput(output) : 0;
        addCustomization(() -> RECIPES.entrySet().removeIf(entry -> output.apply(entry.getValue().getResult())));
        return count;
    }

    public static int removeAllRecipes() {
        int count = initialized ? RECIPES.size() : 0;
        addCustomization(RECIPES::clear);
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

    private static int countRecipesByOutput(Ingredient output) {
        int count = 0;
        for (WorktableRecipe recipe : RECIPES.values()) {
            if (output.apply(recipe.getResult())) {
                count++;
            }
        }
        return count;
    }

    public static WorktableRecipe findMatch(ItemStack[] inputGrid, ItemStack[] inputReagents) {
        for (WorktableRecipe recipe : RECIPES.values()) {
            if (recipe.matches(inputGrid, inputReagents)) {
                return recipe;
            }
        }
        return null;
    }

    private static void loadFromDirectory(Path recipeDir) throws IOException {
        if (!Files.isDirectory(recipeDir)) {
            return;
        }
        Files.walk(recipeDir)
                .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))
                .sorted()
                .forEach(WorktableRecipes::loadRecipe);
    }

    private static void loadFromJar(java.io.File file) throws IOException {
        try (JarFile jar = new JarFile(file)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().startsWith(RESOURCE_DIR + "/") && entry.getName().endsWith(".json")) {
                    ResourceLocation id = new ResourceLocation(Reference.MOD_ID, stripExtension(Paths.get(entry.getName()).getFileName().toString()));
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entry), StandardCharsets.UTF_8))) {
                        loadRecipe(id, reader);
                    } catch (Exception e) {
                        Eidolon.LOGGER.error("Failed to load worktable recipe {}", id, e);
                    }
                }
            }
        }
    }

    private static void loadRecipe(Path path) {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, stripExtension(path.getFileName().toString()));
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            loadRecipe(id, reader);
        } catch (Exception e) {
            Eidolon.LOGGER.error("Failed to load worktable recipe {}", id, e);
        }
    }

    private static void loadRecipe(ResourceLocation id, BufferedReader reader) {
        JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
        String type = JsonUtils.getString(json, "type", "");
        if (!"eidolon:worktable".equals(type)) {
            return;
        }
        WorktableRecipe recipe = parseRecipe(id, json);
        RECIPES.put(id, recipe);
    }

    private static WorktableRecipe parseRecipe(ResourceLocation id, JsonObject json) {
        Map<Character, Ingredient> key = parseKey(JsonUtils.getJsonObject(json, "key"));
        Ingredient[] grid = parsePattern(JsonUtils.getJsonArray(json, "pattern"), key);
        Ingredient[] reagents = parseReagents(JsonUtils.getString(json, "reagents", ""), key);
        ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), JSON_CONTEXT);
        return new WorktableRecipe(id, grid, reagents, result);
    }

    private static Map<Character, Ingredient> parseKey(JsonObject keyJson) {
        Map<Character, Ingredient> key = new LinkedHashMap<>();
        key.put(' ', Ingredient.EMPTY);
        for (Map.Entry<String, JsonElement> entry : keyJson.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new IllegalArgumentException("Worktable key entries must be single characters");
            }
            key.put(entry.getKey().charAt(0), parseIngredient(entry.getValue()));
        }
        return key;
    }

    private static Ingredient parseIngredient(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("tag")) {
                return new OreIngredient(mapTagToOre(JsonUtils.getString(object, "tag")));
            }
            if (object.has("item")) {
                JsonObject mapped = mapItemObject(object);
                return Ingredient.fromStacks(CraftingHelper.getItemStack(mapped, JSON_CONTEXT));
            }
        }
        return CraftingHelper.getIngredient(element);
    }

    private static JsonObject mapItemObject(JsonObject object) {
        String item = JsonUtils.getString(object, "item");
        JsonObject mapped = object.deepCopy();
        switch (item) {
            case "minecraft:bone_meal":
                mapped.addProperty("item", "minecraft:dye");
                mapped.addProperty("data", 15);
                break;
            case "minecraft:charcoal":
                mapped.addProperty("item", "minecraft:coal");
                mapped.addProperty("data", 1);
                break;
            case "minecraft:chiseled_stone_bricks":
                mapped.addProperty("item", "minecraft:stonebrick");
                mapped.addProperty("data", 3);
                break;
            case "minecraft:glistering_melon_slice":
                mapped.addProperty("item", "minecraft:speckled_melon");
                break;
            case "minecraft:lapis_lazuli":
                mapped.addProperty("item", "minecraft:dye");
                mapped.addProperty("data", 4);
                break;
            case "minecraft:melon_slice":
                mapped.addProperty("item", "minecraft:melon");
                break;
            case "minecraft:skeleton_skull":
                mapped.addProperty("item", "minecraft:skull");
                mapped.addProperty("data", 0);
                break;
            case "minecraft:wither_skeleton_skull":
                mapped.addProperty("item", "minecraft:skull");
                mapped.addProperty("data", 1);
                break;
            case "minecraft:crying_obsidian":
                mapped.addProperty("item", "minecraft:obsidian");
                break;
            case "minecraft:dirt":
                mapped.addProperty("data", 0);
                break;
            case "minecraft:stone":
                mapped.addProperty("data", 0);
                break;
            case "minecraft:smooth_stone":
                mapped.addProperty("item", "minecraft:stone");
                mapped.addProperty("data", 0);
                break;
            case "minecraft:smooth_stone_slab":
                mapped.addProperty("item", "minecraft:stone_slab");
                mapped.addProperty("data", 0);
                break;
            case "minecraft:white_wool":
                mapped.addProperty("item", "minecraft:wool");
                mapped.addProperty("data", 0);
                break;
            case "minecraft:gray_wool":
                mapped.addProperty("item", "minecraft:wool");
                mapped.addProperty("data", 7);
                break;
            case "minecraft:red_carpet":
                mapped.addProperty("item", "minecraft:carpet");
                mapped.addProperty("data", 14);
                break;
            case "minecraft:purple_carpet":
                mapped.addProperty("item", "minecraft:carpet");
                mapped.addProperty("data", 10);
                break;
            default:
                break;
        }
        if (!mapped.has("data")) {
            mapped.addProperty("data", 0);
        }
        return mapped;
    }

    private static String mapTagToOre(String tag) {
        switch (tag) {
            case "forge:bones":
                return "bone";
            case "forge:dusts/lead":
                return "dustLead";
            case "forge:dusts/redstone":
                return "dustRedstone";
            case "forge:dusts/silver":
                return "dustSilver";
            case "forge:dusts/sulfur":
                return "dustSulfur";
            case "forge:dyes/black":
                return "dyeBlack";
            case "forge:dyes/blue":
                return "dyeBlue";
            case "forge:dyes/red":
                return "dyeRed";
            case "forge:ender_pearls":
                return "enderpearl";
            case "forge:feathers":
                return "feather";
            case "forge:gems/diamond":
                return "gemDiamond";
            case "forge:gems/quartz":
                return "gemQuartz";
            case "forge:gems/shadow_gem":
                return "gemShadow";
            case "forge:ingots/arcane_gold":
                return "ingotArcaneGold";
            case "forge:ingots/gold":
                return "ingotGold";
            case "forge:ingots/iron":
                return "ingotIron";
            case "forge:ingots/lead":
                return "ingotLead";
            case "forge:ingots/pewter":
                return "ingotPewter";
            case "forge:ingots/silver":
                return "ingotSilver";
            case "forge:mushrooms":
                return "cropMushroom";
            case "forge:nuggets/arcane_gold":
                return "nuggetArcaneGold";
            case "forge:nuggets/gold":
                return "nuggetGold";
            case "forge:nuggets/lead":
                return "nuggetLead";
            case "forge:nuggets/pewter":
                return "nuggetPewter";
            case "forge:nuggets/silver":
                return "nuggetSilver";
            case "forge:ores/lead":
                return "oreLead";
            case "forge:ores/silver":
                return "oreSilver";
            case "forge:rods/wooden":
                return "stickWood";
            case "forge:storage_blocks/arcane_gold":
                return "blockArcaneGold";
            case "forge:storage_blocks/coal":
                return "blockCoal";
            case "forge:storage_blocks/diamond":
                return "blockDiamond";
            case "forge:storage_blocks/lapis":
                return "blockLapis";
            case "forge:storage_blocks/lead":
                return "blockLead";
            case "forge:storage_blocks/pewter":
                return "blockPewter";
            case "forge:storage_blocks/shadow_gem":
                return "blockShadowGem";
            case "forge:storage_blocks/silver":
                return "blockSilver";
            case "forge:string":
                return "string";
            case "forge:tallow":
                return "tallow";
            case "minecraft:planks":
                return "plankWood";
            case "minecraft:wooden_slabs":
                return "slabWood";
            default:
                throw new IllegalArgumentException("Unsupported worktable tag: " + tag);
        }
    }

    private static Ingredient[] parsePattern(JsonArray patternJson, Map<Character, Ingredient> key) {
        if (patternJson.size() > 3) {
            throw new IllegalArgumentException("Worktable pattern must have at most 3 rows");
        }
        Ingredient[] grid = emptyIngredients(WorktableRecipe.GRID_SIZE);
        for (int row = 0; row < patternJson.size(); row++) {
            String line = patternJson.get(row).getAsString();
            if (line.length() > 3) {
                throw new IllegalArgumentException("Worktable pattern rows must be at most 3 columns");
            }
            for (int col = 0; col < line.length(); col++) {
                grid[row * 3 + col] = ingredientFor(key, line.charAt(col));
            }
        }
        return grid;
    }

    private static Ingredient[] parseReagents(String reagentText, Map<Character, Ingredient> key) {
        Ingredient[] reagents = emptyIngredients(WorktableRecipe.REAGENT_SIZE);
        for (int i = 0; i < Math.min(reagentText.length(), WorktableRecipe.REAGENT_SIZE); i++) {
            reagents[i] = ingredientFor(key, reagentText.charAt(i));
        }
        return reagents;
    }

    private static Ingredient[] emptyIngredients(int size) {
        Ingredient[] ingredients = new Ingredient[size];
        for (int i = 0; i < ingredients.length; i++) {
            ingredients[i] = Ingredient.EMPTY;
        }
        return ingredients;
    }

    private static Ingredient ingredientFor(Map<Character, Ingredient> key, char symbol) {
        Ingredient ingredient = key.get(symbol);
        if (ingredient == null) {
            throw new IllegalArgumentException("Undefined worktable key '" + symbol + "'");
        }
        return ingredient;
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(0, dot) : fileName;
    }
}
