package elucent.eidolon.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import elucent.eidolon.Eidolon;
import elucent.eidolon.Reference;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
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

public final class CrucibleRecipes {
    private static final String RESOURCE_DIR = "assets/eidolon/crucible_recipes";
    private static final JsonContext JSON_CONTEXT = new JsonContext(Reference.MOD_ID);
    private static final Map<ResourceLocation, CrucibleRecipe> RECIPES = new LinkedHashMap<>();
    private static final List<Runnable> CUSTOMIZATIONS = new ArrayList<>();
    private static boolean initialized;

    private CrucibleRecipes() {
    }

    public static void init() {
        initialized = false;
        RECIPES.clear();
        loadFromClasspath();
        loadFromModSource();
        if (RECIPES.isEmpty()) {
            registerBuiltIns();
        }
        initialized = true;
        applyCustomizations();
        Eidolon.LOGGER.info("Loaded {} Eidolon crucible recipes", RECIPES.size());
    }

    private static void loadFromClasspath() {
        try {
            Enumeration<java.net.URL> urls = CrucibleRecipes.class.getClassLoader().getResources(RESOURCE_DIR);
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
            Eidolon.LOGGER.error("Failed to scan Eidolon crucible recipes", e);
        } catch (Exception e) {
            Eidolon.LOGGER.error("Failed to locate Eidolon crucible recipe resources", e);
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
            Eidolon.LOGGER.error("Failed to scan Eidolon crucible recipes from mod source", e);
        }
    }

    public static List<CrucibleRecipe> getRecipes() {
        return Collections.unmodifiableList(new ArrayList<>(RECIPES.values()));
    }

    public static CrucibleRecipe find(ResourceLocation id) {
        return RECIPES.get(id);
    }

    public static void addRecipe(ResourceLocation id, ItemStack result, Ingredient stirrer, FluidStack fluid,
                                 List<CrucibleRecipe.Step> steps) {
        ItemStack resultCopy = result.copy();
        FluidStack fluidCopy = fluid.copy();
        List<CrucibleRecipe.Step> stepCopy = new ArrayList<>(steps);
        addCustomization(() -> RECIPES.put(id, new CrucibleRecipe(id, stepCopy, resultCopy, stirrer, fluidCopy)));
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

    public static CrucibleRecipe.Step makeStep(int stirs, List<Ingredient> ingredients) {
        return new CrucibleRecipe.Step(stirs, ingredients);
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
        for (CrucibleRecipe recipe : RECIPES.values()) {
            if (output.apply(recipe.getResult())) {
                count++;
            }
        }
        return count;
    }

    public static CrucibleRecipe find(List<CrucibleRecipe.ProvidedStep> steps, FluidStack fluid) {
        for (CrucibleRecipe recipe : RECIPES.values()) {
            if (recipe.matches(steps, fluid)) {
                return recipe;
            }
        }
        return null;
    }

    public static boolean matchesAnyPrefix(List<CrucibleRecipe.ProvidedStep> steps, FluidStack fluid) {
        for (CrucibleRecipe recipe : RECIPES.values()) {
            if (recipe.matchesPrefix(steps, fluid)) {
                return true;
            }
        }
        return false;
    }

    public static boolean acceptsFluid(FluidStack fluid) {
        if (fluid == null) {
            return false;
        }
        for (CrucibleRecipe recipe : RECIPES.values()) {
            if (recipe.matchesFluid(fluid)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStirrer(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        for (CrucibleRecipe recipe : RECIPES.values()) {
            if (recipe.getStirrer().apply(stack)) {
                return true;
            }
        }
        return false;
    }

    private static void loadFromDirectory(Path recipeDir) throws IOException {
        if (!Files.isDirectory(recipeDir)) {
            return;
        }
        Files.walk(recipeDir)
                .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))
                .sorted()
                .forEach(CrucibleRecipes::loadRecipe);
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
                        Eidolon.LOGGER.error("Failed to load crucible recipe {}", id, e);
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
            Eidolon.LOGGER.error("Failed to load crucible recipe {}", id, e);
        }
    }

    private static void loadRecipe(ResourceLocation id, BufferedReader reader) {
        JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
        String type = JsonUtils.getString(json, "type", "");
        if (!"eidolon:crucible".equals(type)) {
            return;
        }
        RECIPES.put(id, parseRecipe(id, json));
    }

    private static CrucibleRecipe parseRecipe(ResourceLocation id, JsonObject json) {
        ItemStack result = CraftingHelper.getItemStack(mapItemObject(JsonUtils.getJsonObject(json, "result")), JSON_CONTEXT);
        Ingredient stirrer = json.has("stirrer") ? parseIngredient(json.get("stirrer")) : CrucibleRecipe.defaultStirrer();
        FluidStack fluid = json.has("fluid") ? parseFluid(json.get("fluid")) : CrucibleRecipe.defaultFluid();
        List<CrucibleRecipe.Step> steps = new ArrayList<>();
        JsonArray stepArray = JsonUtils.getJsonArray(json, "steps");
        for (JsonElement stepElement : stepArray) {
            JsonObject stepJson = JsonUtils.getJsonObject(stepElement, "step");
            int stirs = JsonUtils.getInt(stepJson, "stirs", 0);
            List<Ingredient> ingredients = new ArrayList<>();
            if (stepJson.has("items")) {
                JsonArray itemArray = JsonUtils.getJsonArray(stepJson, "items");
                for (JsonElement ingredientElement : itemArray) {
                    ingredients.add(parseIngredient(ingredientElement));
                }
            }
            steps.add(new CrucibleRecipe.Step(stirs, ingredients));
        }
        return new CrucibleRecipe(id, steps, result, stirrer, fluid);
    }

    private static Ingredient parseIngredient(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("tag")) {
                return new OreIngredient(mapTagToOre(JsonUtils.getString(object, "tag")));
            }
            if (object.has("type") && "forge:ore_dict".equals(JsonUtils.getString(object, "type"))) {
                return new OreIngredient(JsonUtils.getString(object, "ore"));
            }
            if (object.has("item")) {
                JsonObject mapped = mapItemObject(object);
                return Ingredient.fromStacks(CraftingHelper.getItemStack(mapped, JSON_CONTEXT));
            }
        }
        return CraftingHelper.getIngredient(element);
    }

    private static FluidStack parseFluid(JsonElement element) {
        String fluidName;
        int amount = Fluid.BUCKET_VOLUME;
        if (element.isJsonPrimitive()) {
            fluidName = element.getAsString();
        } else {
            JsonObject object = element.getAsJsonObject();
            fluidName = JsonUtils.getString(object, "fluid", JsonUtils.getString(object, "name", "water"));
            amount = JsonUtils.getInt(object, "amount", Fluid.BUCKET_VOLUME);
        }
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null && fluidName.indexOf(':') >= 0) {
            fluid = FluidRegistry.getFluid(fluidName.substring(fluidName.indexOf(':') + 1));
        }
        if (fluid == null) {
            throw new IllegalArgumentException("Unknown crucible fluid: " + fluidName);
        }
        return new FluidStack(fluid, amount);
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
            case "minecraft:crying_obsidian":
                mapped.addProperty("item", "minecraft:obsidian");
                break;
            case "minecraft:crimson_fungus":
                mapped.addProperty("item", "minecraft:red_mushroom");
                break;
            case "minecraft:crimson_roots":
                mapped.addProperty("item", "minecraft:nether_wart");
                break;
            case "minecraft:glow_berries":
                mapped.addProperty("item", "minecraft:speckled_melon");
                break;
            case "minecraft:glow_ink_sac":
                mapped.addProperty("item", "minecraft:glowstone_dust");
                break;
            case "minecraft:warped_fungus":
                mapped.addProperty("item", "minecraft:brown_mushroom");
                break;
            case "minecraft:weeping_vines":
                mapped.addProperty("item", "minecraft:vine");
                break;
            case "minecraft:dirt":
                mapped.addProperty("data", 0);
                break;
            case "minecraft:stone":
                mapped.addProperty("data", 0);
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
                throw new IllegalArgumentException("Unsupported crucible tag: " + tag);
        }
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(0, dot) : fileName;
    }

    private static void registerBuiltIns() {
        register("arcane_gold", stack(ModItems.ARCANE_GOLD_INGOT, 2),
                step(0, ore("dustRedstone"), ore("dustRedstone"), item(ModItems.SOUL_SHARD)),
                step(0, ore("ingotGold"), ore("ingotGold")));
        register("lesser_soul_gem", stack(ModItems.LESSER_SOUL_GEM, 1),
                step(0, ore("dustRedstone"), ore("dustRedstone"), ore("gemLapis"), ore("gemLapis")),
                step(2, item(ModItems.SOUL_SHARD), item(ModItems.SOUL_SHARD), item(ModItems.SOUL_SHARD), item(ModItems.SOUL_SHARD)),
                step(0, ore("gemQuartz")));
        register("shadow_gem", stack(ModItems.SHADOW_GEM, 1),
                step(0, item(Items.COAL)),
                step(1, item(Items.GHAST_TEAR), item(ModItems.DEATH_ESSENCE)),
                step(1, item(ModItems.SOUL_SHARD), item(ModItems.SOUL_SHARD), item(ModItems.DEATH_ESSENCE)),
                step(0, ore("gemDiamond")));
        register("sulfur", stack(ModItems.SULFUR, 2),
                step(0, item(Items.COAL), item(ModBlocks.ENCHANTED_ASH)));
        register("ender_calx", stack(ModItems.ENDER_CALX, 2),
                step(0, ore("enderpearl"), item(ModBlocks.ENCHANTED_ASH)));
        register("leather_from_flesh", new ItemStack(Items.LEATHER),
                step(0, item(ModBlocks.ENCHANTED_ASH), item(ModBlocks.ENCHANTED_ASH)),
                step(2, item(Items.ROTTEN_FLESH)));
        register("rotten_beef", new ItemStack(Items.ROTTEN_FLESH),
                step(0, item(Items.BEEF), ore("cropMushroom")));
        register("rotten_pork", new ItemStack(Items.ROTTEN_FLESH),
                step(0, item(Items.PORKCHOP), ore("cropMushroom")));
        register("rotten_mutton", new ItemStack(Items.ROTTEN_FLESH),
                step(0, item(Items.MUTTON), ore("cropMushroom")));
        register("rotten_chicken", new ItemStack(Items.ROTTEN_FLESH),
                step(0, item(Items.CHICKEN), ore("cropMushroom")));
        register("rotten_rabbit", new ItemStack(Items.ROTTEN_FLESH),
                step(0, item(Items.RABBIT), ore("cropMushroom")));
        register("gunpowder", new ItemStack(Items.GUNPOWDER, 4),
                step(0, item(ModItems.SULFUR), item(new ItemStack(Items.DYE, 1, 15))),
                step(1, item(new ItemStack(Items.COAL, 1, 1))));
        register("gilded_apple", new ItemStack(Items.GOLDEN_APPLE),
                step(0, ore("ingotGold"), ore("ingotGold")),
                step(2, item(ModBlocks.ENCHANTED_ASH)),
                step(0, item(Items.APPLE)));
        register("gilded_carrot", new ItemStack(Items.GOLDEN_CARROT),
                step(0, ore("nuggetGold"), ore("nuggetGold")),
                step(2, item(ModBlocks.ENCHANTED_ASH)),
                step(0, item(Items.CARROT)));
        register("gilded_melon", new ItemStack(Items.SPECKLED_MELON),
                step(0, ore("nuggetGold"), ore("nuggetGold")),
                step(2, item(ModBlocks.ENCHANTED_ASH)),
                step(0, item(Items.MELON)));
        register("death_essence", stack(ModItems.DEATH_ESSENCE, 4),
                step(0, item(ModItems.ZOMBIE_HEART), item(Items.ROTTEN_FLESH)),
                step(2, item(new ItemStack(Items.DYE, 1, 15)), item(new ItemStack(Items.DYE, 1, 15))),
                step(0, item(new ItemStack(Items.COAL, 1, 1))));
        register("fungus_sprouts", stack(ModItems.FUNGUS_SPROUTS, 2),
                step(0, ore("cropMushroom")),
                step(2, item(new ItemStack(Items.DYE, 1, 15))),
                step(0, item(Items.WHEAT_SEEDS)));
        register("polished_planks", new ItemStack(ModBlocks.POLISHED_PLANKS, 32),
                step(0, planks32()),
                step(1, item(ModItems.SOUL_SHARD), item(ModBlocks.ENCHANTED_ASH)));
    }

    private static void register(String name, ItemStack result, CrucibleRecipe.Step... steps) {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, name);
        List<CrucibleRecipe.Step> stepList = new ArrayList<>();
        Collections.addAll(stepList, steps);
        RECIPES.put(id, new CrucibleRecipe(id, stepList, result, CrucibleRecipe.defaultStirrer(), CrucibleRecipe.defaultFluid()));
    }

    private static void register(String name, ItemStack result, Ingredient stirrer, CrucibleRecipe.Step... steps) {
        register(name, result, stirrer, CrucibleRecipe.defaultFluid(), steps);
    }

    private static void register(String name, ItemStack result, Ingredient stirrer, FluidStack fluid, CrucibleRecipe.Step... steps) {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, name);
        List<CrucibleRecipe.Step> stepList = new ArrayList<>();
        Collections.addAll(stepList, steps);
        RECIPES.put(id, new CrucibleRecipe(id, stepList, result, stirrer, fluid));
    }

    private static CrucibleRecipe.Step step(int stirs, Ingredient... ingredients) {
        List<Ingredient> list = new ArrayList<>();
        Collections.addAll(list, ingredients);
        return new CrucibleRecipe.Step(stirs, list);
    }

    private static Ingredient[] planks32() {
        Ingredient[] ingredients = new Ingredient[32];
        for (int i = 0; i < ingredients.length; i++) {
            ingredients[i] = ore("plankWood");
        }
        return ingredients;
    }

    private static Ingredient item(net.minecraft.item.Item item) {
        return Ingredient.fromStacks(new ItemStack(item));
    }

    private static Ingredient item(net.minecraft.block.Block block) {
        return Ingredient.fromStacks(new ItemStack(block));
    }

    private static Ingredient item(ItemStack stack) {
        return Ingredient.fromStacks(stack);
    }

    private static Ingredient ore(String key) {
        return new OreIngredient(key);
    }

    private static ItemStack stack(net.minecraft.item.Item item, int count) {
        return new ItemStack(item, count);
    }
}
