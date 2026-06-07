package elucent.eidolon.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import elucent.eidolon.Eidolon;
import elucent.eidolon.Reference;
import elucent.eidolon.registries.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;

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

public final class IncubatorRecipes {
    private static final String RESOURCE_DIR = "assets/eidolon/incubator_recipes";
    private static final JsonContext JSON_CONTEXT = new JsonContext(Reference.MOD_ID);
    private static final Map<ResourceLocation, IncubatorRecipe> RECIPES = new LinkedHashMap<>();
    private static final List<Runnable> CUSTOMIZATIONS = new ArrayList<>();
    private static boolean initialized;

    private IncubatorRecipes() {
    }

    public static void init() {
        initialized = false;
        RECIPES.clear();
        loadFromClasspath();
        loadFromModSource();
        registerMissingBuiltIns();
        initialized = true;
        applyCustomizations();
        Eidolon.LOGGER.info("Loaded {} Eidolon incubator recipes", RECIPES.size());
    }

    public static List<IncubatorRecipe> getRecipes() {
        return Collections.unmodifiableList(new ArrayList<>(RECIPES.values()));
    }

    public static IncubatorRecipe find(ItemStack input, ItemStack catalyst) {
        for (IncubatorRecipe recipe : RECIPES.values()) {
            if (recipe.matches(input, catalyst)) {
                return recipe;
            }
        }
        return null;
    }

    public static void addRecipe(ResourceLocation id, Ingredient input, Ingredient catalyst, ItemStack result, int ticks) {
        ItemStack resultCopy = result.copy();
        addCustomization(() -> RECIPES.put(id, new IncubatorRecipe(id, input, catalyst, resultCopy, ticks)));
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
        for (IncubatorRecipe recipe : RECIPES.values()) {
            if (output.apply(recipe.getResult())) {
                count++;
            }
        }
        return count;
    }

    private static void loadFromClasspath() {
        try {
            Enumeration<java.net.URL> urls = IncubatorRecipes.class.getClassLoader().getResources(RESOURCE_DIR);
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
            Eidolon.LOGGER.error("Failed to scan Eidolon incubator recipes", e);
        } catch (Exception e) {
            Eidolon.LOGGER.error("Failed to locate Eidolon incubator recipe resources", e);
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
            Eidolon.LOGGER.error("Failed to scan Eidolon incubator recipes from mod source", e);
        }
    }

    private static void loadFromDirectory(Path recipeDir) throws IOException {
        if (!Files.isDirectory(recipeDir)) {
            return;
        }
        Files.walk(recipeDir)
                .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))
                .sorted()
                .forEach(IncubatorRecipes::loadRecipe);
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
                        Eidolon.LOGGER.error("Failed to load incubator recipe {}", id, e);
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
            Eidolon.LOGGER.error("Failed to load incubator recipe {}", id, e);
        }
    }

    private static void loadRecipe(ResourceLocation id, BufferedReader reader) {
        JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
        String type = JsonUtils.getString(json, "type", "");
        if (!"eidolon:incubator".equals(type)) {
            return;
        }
        Ingredient input = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "input"));
        Ingredient catalyst = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "catalyst"));
        ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), JSON_CONTEXT);
        int ticks = JsonUtils.getInt(json, "ticks", IncubatorRecipe.DEFAULT_TICKS);
        RECIPES.put(id, new IncubatorRecipe(id, input, catalyst, result, ticks));
    }

    private static void registerMissingBuiltIns() {
        putMissing("zombie_heart", Ingredient.fromStacks(new ItemStack(Items.ROTTEN_FLESH)),
                Ingredient.fromStacks(new ItemStack(ModItems.SOUL_SHARD)), new ItemStack(ModItems.ZOMBIE_HEART), 1200);
        putMissing("wraith_heart", Ingredient.fromStacks(new ItemStack(ModItems.ZOMBIE_HEART)),
                Ingredient.fromStacks(new ItemStack(ModItems.DEATH_ESSENCE)), new ItemStack(ModItems.WRAITH_HEART), 1600);
    }

    private static void putMissing(String name, Ingredient input, Ingredient catalyst, ItemStack result, int ticks) {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, name);
        if (!RECIPES.containsKey(id)) {
            RECIPES.put(id, new IncubatorRecipe(id, input, catalyst, result, ticks));
        }
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(0, dot) : fileName;
    }
}
