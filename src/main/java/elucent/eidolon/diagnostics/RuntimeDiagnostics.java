package elucent.eidolon.diagnostics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import elucent.eidolon.Eidolon;
import elucent.eidolon.Reference;
import elucent.eidolon.gui.ModGuiHandler;
import elucent.eidolon.recipes.CrucibleRecipes;
import elucent.eidolon.recipes.IncubatorRecipes;
import elucent.eidolon.recipes.WorktableRecipes;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.spell.AltarEntries;
import elucent.eidolon.spell.AltarRituals;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public final class RuntimeDiagnostics {
    private static final String ENABLE_PROPERTY = "eidolon.runtimeDump";
    private static final String OUTPUT_PROPERTY = "eidolon.runtimeDumpDir";
    private static final String AUTO_STOP_PROPERTY = "eidolon.runtimeDumpStopServer";
    private static final Path ENABLE_FILE = Paths.get("runtime-dump-request.txt");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static boolean ran;

    private RuntimeDiagnostics() {
    }

    public static void runIfRequested(MinecraftServer server) {
        if (ran || !isRequested()) {
            return;
        }
        ran = true;

        Path outputDir = resolveOutputDir();
        try {
            Files.createDirectories(outputDir);
            Dump dump = collect();
            writeJson(outputDir.resolve("runtime-dump.json"), dump);
            writeSummary(outputDir.resolve("runtime-summary.md"), dump);
            Eidolon.LOGGER.info("Eidolon runtime diagnostics written to {}", outputDir);
        } catch (Exception e) {
            Eidolon.LOGGER.error("Failed to write Eidolon runtime diagnostics", e);
        } finally {
            if (server != null && Boolean.parseBoolean(System.getProperty(AUTO_STOP_PROPERTY, "true"))) {
                stopServer(server);
            }
        }
    }

    private static boolean isRequested() {
        return Boolean.getBoolean(ENABLE_PROPERTY)
                || Files.isRegularFile(ENABLE_FILE)
                || Files.isRegularFile(projectRoot().resolve(ENABLE_FILE));
    }

    private static Dump collect() {
        Dump dump = new Dump();
        dump.items = collectItems();
        dump.blocks = collectBlocks();
        dump.entities = collectEntities();
        dump.tileEntities = collectTileEntities();
        dump.drops = collectDrops();
        dump.gui = collectGui();
        dump.recipes = collectRecipes();
        dump.resources = collectResources();
        dump.failures.addAll(validateRegistryCoverage(dump));
        dump.failures.addAll(validateDrops(dump));
        dump.failures.addAll(validateGui(dump));
        dump.failures.addAll(validateAssets(dump));
        dump.failures.addAll(validateResourceBuckets(dump));
        return dump;
    }

    private static RegistryDump collectItems() {
        RegistryDump result = new RegistryDump();
        for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            ResourceLocation name = item.getRegistryName();
            if (isEidolon(name)) {
                result.ids.add(name.toString());
            }
        }
        result.expected = expectedItemIds();
        result.missingExpected = difference(result.expected, result.ids);
        result.extraRuntime = difference(result.ids, result.expected);
        sortRegistry(result);
        return result;
    }

    private static RegistryDump collectBlocks() {
        RegistryDump result = new RegistryDump();
        for (Block block : ForgeRegistries.BLOCKS.getValuesCollection()) {
            ResourceLocation name = block.getRegistryName();
            if (isEidolon(name)) {
                result.ids.add(name.toString());
            }
        }
        result.expected = expectedBlockIds();
        result.missingExpected = difference(result.expected, result.ids);
        result.extraRuntime = difference(result.ids, result.expected);
        sortRegistry(result);
        return result;
    }

    private static RegistryDump collectEntities() {
        RegistryDump result = new RegistryDump();
        for (EntityEntry entry : ForgeRegistries.ENTITIES.getValuesCollection()) {
            ResourceLocation name = entry.getRegistryName();
            if (isEidolon(name)) {
                result.ids.add(name.toString());
            }
        }
        result.expected.addAll(Arrays.asList(
                "eidolon:soulfire_projectile",
                "eidolon:bonechill_projectile",
                "eidolon:necromancer_spell",
                "eidolon:angel_arrow",
                "eidolon:chant_caster",
                "eidolon:wraith",
                "eidolon:zombie_brute",
                "eidolon:necromancer",
                "eidolon:slimy_slug",
                "eidolon:raven"
        ));
        result.missingExpected = difference(result.expected, result.ids);
        result.extraRuntime = difference(result.ids, result.expected);
        sortRegistry(result);
        return result;
    }

    private static TileDump collectTileEntities() {
        TileDump result = new TileDump();
        result.expected.addAll(Arrays.asList(
                "eidolon:worktable",
                "eidolon:crucible",
                "eidolon:research_table",
                "eidolon:altar",
                "eidolon:glass_tube",
                "eidolon:cistern",
                "eidolon:brazier",
                "eidolon:stone_hand",
                "eidolon:necrotic_focus",
                "eidolon:offertory_plate",
                "eidolon:soul_enchanter",
                "eidolon:wooden_brewing_stand",
                "eidolon:incubator"
        ));
        for (String id : result.expected) {
            Class<? extends TileEntity> clazz = getTileEntityClass(new ResourceLocation(id));
            if (clazz == null) {
                result.missingExpected.add(id);
            } else {
                result.ids.add(id);
                result.classes.put(id, clazz.getName());
            }
        }
        sortRegistry(result);
        return result;
    }

    private static GuiDump collectGui() {
        GuiDump result = new GuiDump();
        result.ids.put("WORKTABLE", ModGuiHandler.WORKTABLE);
        result.ids.put("RESEARCH_TABLE", ModGuiHandler.RESEARCH_TABLE);
        result.ids.put("CODEX", ModGuiHandler.CODEX);
        result.ids.put("SOUL_ENCHANTER", ModGuiHandler.SOUL_ENCHANTER);
        result.ids.put("WOODEN_BREWING_STAND", ModGuiHandler.WOODEN_BREWING_STAND);
        result.ids.put("INCUBATOR", ModGuiHandler.INCUBATOR);
        result.serverBacked.add("WORKTABLE");
        result.serverBacked.add("RESEARCH_TABLE");
        result.serverBacked.add("SOUL_ENCHANTER");
        result.serverBacked.add("WOODEN_BREWING_STAND");
        result.serverBacked.add("INCUBATOR");
        result.clientOnly.add("CODEX");
        result.bindings.put("WORKTABLE", new GuiBinding(
                "elucent.eidolon.gui.WorktableContainer",
                "elucent.eidolon.gui.WorktableGui",
                "elucent.eidolon.tile.WorktableTileEntity"));
        result.bindings.put("RESEARCH_TABLE", new GuiBinding(
                "elucent.eidolon.gui.ResearchTableContainer",
                "elucent.eidolon.gui.ResearchTableGui",
                "elucent.eidolon.tile.ResearchTableTileEntity"));
        result.bindings.put("CODEX", new GuiBinding(
                "client-only",
                "elucent.eidolon.gui.CodexGui",
                "none"));
        result.bindings.put("SOUL_ENCHANTER", new GuiBinding(
                "elucent.eidolon.gui.SoulEnchanterContainer",
                "elucent.eidolon.gui.SoulEnchanterGui",
                "elucent.eidolon.tile.SoulEnchanterTileEntity"));
        result.bindings.put("WOODEN_BREWING_STAND", new GuiBinding(
                "elucent.eidolon.gui.WoodenBrewingStandContainer",
                "elucent.eidolon.gui.WoodenBrewingStandGui",
                "elucent.eidolon.tile.WoodenBrewingStandTileEntity"));
        result.bindings.put("INCUBATOR", new GuiBinding(
                "elucent.eidolon.gui.IncubatorContainer",
                "elucent.eidolon.gui.IncubatorGui",
                "elucent.eidolon.tile.IncubatorTileEntity"));
        return result;
    }

    private static RecipeDump collectRecipes() {
        RecipeDump result = new RecipeDump();
        for (IRecipe recipe : ForgeRegistries.RECIPES.getValuesCollection()) {
            ResourceLocation name = recipe.getRegistryName();
            if (isEidolon(name)) {
                result.forgeCrafting++;
            }
        }
        for (Map.Entry<net.minecraft.item.ItemStack, net.minecraft.item.ItemStack> entry
                : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
            if (isEidolon(entry.getKey()) || isEidolon(entry.getValue())) {
                result.smelting++;
            }
        }
        result.worktable = WorktableRecipes.getRecipes().size();
        result.crucible = CrucibleRecipes.getRecipes().size();
        result.incubator = IncubatorRecipes.getRecipes().size();
        result.altarRituals = AltarRituals.getRituals().size();
        result.altarEntries = AltarEntries.getEntries().size();
        return result;
    }

    private static DropDump collectDrops() {
        DropDump result = new DropDump();
        Random random = new Random(0L);
        for (Block block : ForgeRegistries.BLOCKS.getValuesCollection()) {
            ResourceLocation name = block.getRegistryName();
            if (!isEidolon(name)) {
                continue;
            }
            IBlockState state = block.getDefaultState();
            Item drop = block.getItemDropped(state, random, 0);
            String blockId = name.toString();
            result.ids.add(blockId);
            if (drop == null || drop == Items.AIR || drop.getRegistryName() == null) {
                result.missingRuntimeDrops.add(blockId);
            } else {
                result.runtimeDrops.put(blockId, drop.getRegistryName().toString());
            }
        }
        result.ids.sort(Comparator.naturalOrder());
        result.missingRuntimeDrops.sort(Comparator.naturalOrder());
        return result;
    }

    private static ResourceDump collectResources() {
        ResourceDump result = new ResourceDump();
        Path root = projectRoot().resolve("src/main/resources/assets/" + Reference.MOD_ID);
        result.root = root.toAbsolutePath().toString();
        countFiles(root.resolve("blockstates"), result.counts, "blockstates");
        countFiles(root.resolve("models/block"), result.counts, "blockModels");
        countFiles(root.resolve("models/item"), result.counts, "itemModels");
        countFiles(root.resolve("loot_tables/blocks"), result.counts, "blockLootTables");
        countFiles(root.resolve("loot_tables/chests"), result.counts, "chestLootTables");
        countFiles(root.resolve("loot_tables/entities"), result.counts, "entityLootTables");
        countFiles(root.resolve("recipes"), result.counts, "vanillaRecipes");
        countFiles(root.resolve("worktable_recipes"), result.counts, "worktableRecipeFiles");
        countFiles(root.resolve("crucible_recipes"), result.counts, "crucibleRecipeFiles");
        countFiles(root.resolve("incubator_recipes"), result.counts, "incubatorRecipeFiles");
        countFiles(root.resolve("altar_rituals"), result.counts, "altarRitualFiles");
        countFiles(root.resolve("structures"), result.counts, "structureTemplates");
        countFiles(root.resolve("sounds"), result.counts, "sounds");
        countFiles(root.resolve("textures"), result.counts, "textures");
        return result;
    }

    private static List<String> validateRegistryCoverage(Dump dump) {
        List<String> failures = new ArrayList<>();
        appendFailures(failures, "missing item", dump.items.missingExpected);
        appendFailures(failures, "missing block", dump.blocks.missingExpected);
        appendFailures(failures, "missing entity", dump.entities.missingExpected);
        appendFailures(failures, "missing tile entity", dump.tileEntities.missingExpected);
        if (dump.recipes.forgeCrafting <= 0) {
            failures.add("no Forge crafting recipes loaded");
        }
        if (dump.recipes.smelting <= 0) {
            failures.add("no smelting recipes loaded");
        }
        if (dump.recipes.worktable <= 0) {
            failures.add("no worktable recipes loaded");
        }
        if (dump.recipes.crucible <= 0) {
            failures.add("no crucible recipes loaded");
        }
        if (dump.recipes.incubator <= 0) {
            failures.add("no incubator recipes loaded");
        }
        if (dump.recipes.altarRituals <= 0) {
            failures.add("no altar rituals loaded");
        }
        if (dump.recipes.altarEntries <= 0) {
            failures.add("no altar entries loaded");
        }
        return failures;
    }

    private static List<String> validateDrops(Dump dump) {
        List<String> failures = new ArrayList<>();
        appendFailures(failures, "missing runtime drop", dump.drops.missingRuntimeDrops);
        return failures;
    }

    private static List<String> validateGui(Dump dump) {
        List<String> failures = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dump.gui.ids.entrySet()) {
            if (entry.getValue() == null || entry.getValue() < 0) {
                failures.add("invalid GUI ID: " + entry.getKey());
            } else if (ids.contains(entry.getValue())) {
                failures.add("duplicate GUI ID: " + entry.getKey() + " -> " + entry.getValue());
            }
            ids.add(entry.getValue());
            if (!dump.gui.bindings.containsKey(entry.getKey())) {
                failures.add("missing GUI binding: " + entry.getKey());
            }
        }
        for (Map.Entry<String, GuiBinding> entry : dump.gui.bindings.entrySet()) {
            GuiBinding binding = entry.getValue();
            if (!"client-only".equals(binding.serverElement)
                    && !classFileExists(binding.serverElement)) {
                failures.add("missing GUI server element class: " + entry.getKey() + " -> " + binding.serverElement);
            }
            if (!classFileExists(binding.clientElement)) {
                failures.add("missing GUI client element class: " + entry.getKey() + " -> " + binding.clientElement);
            }
            if (!"none".equals(binding.tileEntity)) {
                if (!classFileExists(binding.tileEntity)) {
                    failures.add("missing GUI tile entity class: " + entry.getKey() + " -> " + binding.tileEntity);
                } else if (!dump.tileEntities.classes.containsValue(binding.tileEntity)) {
                    failures.add("GUI tile entity is not registered: " + entry.getKey() + " -> " + binding.tileEntity);
                }
            }
        }
        return failures;
    }

    private static List<String> validateAssets(Dump dump) {
        List<String> failures = new ArrayList<>();
        Path assets = projectRoot().resolve("src/main/resources/assets/" + Reference.MOD_ID);
        for (String blockId : dump.blocks.ids) {
            String name = path(blockId);
            requireFile(failures, dump.resources.missingBlockstates,
                    assets.resolve("blockstates").resolve(name + ".json"), "missing blockstate for " + blockId, blockId);
            requireFile(failures, dump.resources.missingBlockLootTables,
                    assets.resolve("loot_tables/blocks").resolve(name + ".json"), "missing block loot table for " + blockId, blockId);
        }
        for (String itemId : dump.items.ids) {
            String name = path(itemId);
            requireFile(failures, dump.resources.missingItemModels,
                    assets.resolve("models/item").resolve(name + ".json"), "missing item model for " + itemId, itemId);
        }
        for (String template : expectedStructureTemplates()) {
            requireFile(failures, dump.resources.missingStructureTemplates,
                    assets.resolve("structures").resolve(template + ".nbt"), "missing structure template: " + template, template);
        }
        for (String table : expectedChestLootTables()) {
            requireFile(failures, dump.resources.missingChestLootTables,
                    assets.resolve("loot_tables/chests").resolve(table + ".json"), "missing chest loot table: " + table, table);
        }
        for (String table : expectedEntityLootTables()) {
            requireFile(failures, dump.resources.missingEntityLootTables,
                    assets.resolve("loot_tables/entities").resolve(table + ".json"), "missing entity loot table: " + table, table);
        }
        dump.resources.missingBlockstates.sort(Comparator.naturalOrder());
        dump.resources.missingBlockLootTables.sort(Comparator.naturalOrder());
        dump.resources.missingItemModels.sort(Comparator.naturalOrder());
        dump.resources.missingStructureTemplates.sort(Comparator.naturalOrder());
        dump.resources.missingChestLootTables.sort(Comparator.naturalOrder());
        dump.resources.missingEntityLootTables.sort(Comparator.naturalOrder());
        return failures;
    }

    private static List<String> validateResourceBuckets(Dump dump) {
        List<String> failures = new ArrayList<>();
        requireResourceCount(failures, dump, "vanillaRecipes", "no vanilla recipe files");
        requireResourceCount(failures, dump, "worktableRecipeFiles", "no worktable recipe files");
        requireResourceCount(failures, dump, "crucibleRecipeFiles", "no crucible recipe files");
        requireResourceCount(failures, dump, "incubatorRecipeFiles", "no incubator recipe files");
        requireResourceCount(failures, dump, "altarRitualFiles", "no altar ritual files");
        requireResourceCount(failures, dump, "blockLootTables", "no block loot table files");
        requireResourceCount(failures, dump, "chestLootTables", "no chest loot table files");
        requireResourceCount(failures, dump, "entityLootTables", "no entity loot table files");
        requireResourceCount(failures, dump, "structureTemplates", "no structure template files");
        return failures;
    }

    private static void requireResourceCount(List<String> failures, Dump dump, String key, String message) {
        Integer count = dump.resources.counts.get(key);
        if (count == null || count <= 0) {
            failures.add(message);
        }
    }

    private static List<String> expectedItemIds() {
        List<String> result = new ArrayList<>();
        for (Item item : getStaticArray(ModItemsMirror.ITEMS_FIELD, Item.class)) {
            ResourceLocation name = item.getRegistryName();
            if (isEidolon(name)) {
                result.add(name.toString());
            }
        }
        for (net.minecraft.item.ItemBlock itemBlock : ModBlocks.ITEM_BLOCKS) {
            ResourceLocation name = itemBlock.getRegistryName();
            if (isEidolon(name)) {
                result.add(name.toString());
            }
        }
        return result;
    }

    private static List<String> expectedBlockIds() {
        List<String> result = new ArrayList<>();
        for (Block block : ModBlocks.BLOCKS) {
            ResourceLocation name = block.getRegistryName();
            if (isEidolon(name)) {
                result.add(name.toString());
            }
        }
        return result;
    }

    private static List<String> expectedStructureTemplates() {
        return Arrays.asList(
                "lab",
                "stray_tower",
                "catacomb/catacomb_coffin",
                "catacomb/catacomb_corridor_center",
                "catacomb/catacomb_corridor_door",
                "catacomb/catacomb_graveyard",
                "catacomb/catacomb_lab",
                "catacomb/catacomb_room_medium",
                "catacomb/catacomb_room_small",
                "catacomb/catacomb_shrine",
                "catacomb/catacomb_skull",
                "catacomb/catacomb_spawner",
                "catacomb/catacomb_trap",
                "catacomb/catacomb_turnaround"
        );
    }

    private static List<String> expectedChestLootTables() {
        return Arrays.asList(
                "catacomb_basic",
                "catacomb_coffin",
                "lab"
        );
    }

    private static List<String> expectedEntityLootTables() {
        return Arrays.asList(
                "raven",
                "slimy_slug",
                "wraith",
                "zombie_brute"
        );
    }

    private static <T> List<T> getStaticArray(String fieldName, Class<T> type) {
        List<T> result = new ArrayList<>();
        try {
            Field field = Class.forName("elucent.eidolon.registries.ModItems").getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(null);
            if (value instanceof Object[]) {
                for (Object entry : (Object[]) value) {
                    if (type.isInstance(entry)) {
                        result.add(type.cast(entry));
                    }
                }
            }
        } catch (Exception e) {
            Eidolon.LOGGER.error("Failed to read ModItems.{}", fieldName, e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends TileEntity> getTileEntityClass(ResourceLocation id) {
        try {
            Method method = GameRegistry.class.getDeclaredMethod("findRegistry", Class.class);
            Object registry = method.invoke(null, TileEntity.class);
            Method getValue = registry.getClass().getMethod("getValue", ResourceLocation.class);
            Object value = getValue.invoke(registry, id);
            if (value instanceof Class && TileEntity.class.isAssignableFrom((Class<?>) value)) {
                return (Class<? extends TileEntity>) value;
            }
        } catch (Exception ignored) {
            try {
                Method method = TileEntity.class.getDeclaredMethod("getKey", Class.class);
                Object value = method.invoke(null, Class.forName(tileClassName(id)));
                if (id.equals(value)) {
                    return (Class<? extends TileEntity>) Class.forName(tileClassName(id));
                }
            } catch (Exception ignoredAgain) {
                return null;
            }
        }
        return null;
    }

    private static String tileClassName(ResourceLocation id) {
        String path = id.getPath();
        if ("worktable".equals(path)) return "elucent.eidolon.tile.WorktableTileEntity";
        if ("crucible".equals(path)) return "elucent.eidolon.tile.CrucibleTileEntity";
        if ("research_table".equals(path)) return "elucent.eidolon.tile.ResearchTableTileEntity";
        if ("altar".equals(path)) return "elucent.eidolon.tile.AltarTileEntity";
        if ("glass_tube".equals(path)) return "elucent.eidolon.tile.GlassTubeTileEntity";
        if ("cistern".equals(path)) return "elucent.eidolon.tile.CisternTileEntity";
        if ("brazier".equals(path)) return "elucent.eidolon.tile.BrazierTileEntity";
        if ("stone_hand".equals(path)) return "elucent.eidolon.tile.StoneHandTileEntity";
        if ("necrotic_focus".equals(path)) return "elucent.eidolon.tile.NecroticFocusTileEntity";
        if ("offertory_plate".equals(path)) return "elucent.eidolon.tile.OffertoryPlateTileEntity";
        if ("soul_enchanter".equals(path)) return "elucent.eidolon.tile.SoulEnchanterTileEntity";
        if ("wooden_brewing_stand".equals(path)) return "elucent.eidolon.tile.WoodenBrewingStandTileEntity";
        if ("incubator".equals(path)) return "elucent.eidolon.tile.IncubatorTileEntity";
        return "";
    }

    private static void writeJson(Path path, Dump dump) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("modId", Reference.MOD_ID);
        json.addProperty("modName", Reference.MOD_NAME);
        json.add("items", registryJson(dump.items));
        json.add("blocks", registryJson(dump.blocks));
        json.add("entities", registryJson(dump.entities));
        json.add("tileEntities", tileJson(dump.tileEntities));
        json.add("drops", dropJson(dump.drops));
        json.add("gui", guiJson(dump.gui));
        json.add("recipes", recipeJson(dump.recipes));
        json.add("resources", resourceJson(dump.resources));
        json.add("failures", strings(dump.failures));
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(json, writer);
        }
    }

    private static void writeSummary(Path path, Dump dump) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("# Eidolon Runtime Diagnostics\n\n");
            writer.write("Status: " + (dump.failures.isEmpty() ? "PASS" : "FAIL") + "\n\n");
            writer.write("| Check | Runtime | Expected | Missing | Extra |\n");
            writer.write("| --- | ---: | ---: | ---: | ---: |\n");
            writeRegistryRow(writer, "Items", dump.items);
            writeRegistryRow(writer, "Blocks", dump.blocks);
            writeRegistryRow(writer, "Entities", dump.entities);
            writeRegistryRow(writer, "Tile entities", dump.tileEntities);
            writer.write("| Runtime drops | " + dump.drops.ids.size() + " | " + dump.blocks.ids.size()
                    + " | " + dump.drops.missingRuntimeDrops.size() + " | 0 |\n");
            writer.write("\n");
            writer.write("| Recipe/GUI check | Count |\n");
            writer.write("| --- | ---: |\n");
            writer.write("| Forge crafting recipes | " + dump.recipes.forgeCrafting + " |\n");
            writer.write("| Smelting recipes | " + dump.recipes.smelting + " |\n");
            writer.write("| Worktable recipes | " + dump.recipes.worktable + " |\n");
            writer.write("| Crucible recipes | " + dump.recipes.crucible + " |\n");
            writer.write("| Incubator recipes | " + dump.recipes.incubator + " |\n");
            writer.write("| Altar rituals | " + dump.recipes.altarRituals + " |\n");
            writer.write("| Altar entries | " + dump.recipes.altarEntries + " |\n");
            writer.write("| GUI IDs | " + dump.gui.ids.size() + " |\n\n");
            writer.write("| Resource check | Count |\n");
            writer.write("| --- | ---: |\n");
            for (Map.Entry<String, Integer> entry : dump.resources.counts.entrySet()) {
                writer.write("| " + entry.getKey() + " | " + entry.getValue() + " |\n");
            }
            writer.write("\n");
            writer.write("| Missing asset bucket | Count |\n");
            writer.write("| --- | ---: |\n");
            writer.write("| Item models | " + dump.resources.missingItemModels.size() + " |\n");
            writer.write("| Blockstates | " + dump.resources.missingBlockstates.size() + " |\n");
            writer.write("| Block loot tables | " + dump.resources.missingBlockLootTables.size() + " |\n");
            writer.write("| Chest loot tables | " + dump.resources.missingChestLootTables.size() + " |\n");
            writer.write("| Entity loot tables | " + dump.resources.missingEntityLootTables.size() + " |\n");
            writer.write("| Structure templates | " + dump.resources.missingStructureTemplates.size() + " |\n\n");
            writer.write("| GUI ID | Server element | Client element |\n");
            writer.write("| --- | --- | --- |\n");
            for (Map.Entry<String, GuiBinding> entry : dump.gui.bindings.entrySet()) {
                writer.write("| " + entry.getKey() + " (" + dump.gui.ids.get(entry.getKey()) + ") | "
                        + entry.getValue().serverElement + " | " + entry.getValue().clientElement + " |\n");
            }
            writer.write("\n");
            if (dump.failures.isEmpty()) {
                writer.write("No failures detected.\n");
            } else {
                writer.write("## Failures\n\n");
                for (String failure : dump.failures) {
                    writer.write("- " + failure + "\n");
                }
            }
        }
    }

    private static void writeRegistryRow(BufferedWriter writer, String name, RegistryDump dump) throws IOException {
        writer.write("| " + name + " | " + dump.ids.size() + " | " + dump.expected.size()
                + " | " + dump.missingExpected.size() + " | " + dump.extraRuntime.size() + " |\n");
    }

    private static JsonObject registryJson(RegistryDump dump) {
        JsonObject json = new JsonObject();
        json.addProperty("runtimeCount", dump.ids.size());
        json.addProperty("expectedCount", dump.expected.size());
        json.add("runtime", strings(dump.ids));
        json.add("expected", strings(dump.expected));
        json.add("missingExpected", strings(dump.missingExpected));
        json.add("extraRuntime", strings(dump.extraRuntime));
        return json;
    }

    private static JsonObject tileJson(TileDump dump) {
        JsonObject json = registryJson(dump);
        JsonObject classes = new JsonObject();
        for (Map.Entry<String, String> entry : dump.classes.entrySet()) {
            classes.addProperty(entry.getKey(), entry.getValue());
        }
        json.add("classes", classes);
        return json;
    }

    private static JsonObject dropJson(DropDump dump) {
        JsonObject json = new JsonObject();
        json.addProperty("runtimeCount", dump.ids.size());
        json.addProperty("missingRuntimeDropCount", dump.missingRuntimeDrops.size());
        JsonObject drops = new JsonObject();
        for (Map.Entry<String, String> entry : dump.runtimeDrops.entrySet()) {
            drops.addProperty(entry.getKey(), entry.getValue());
        }
        json.add("runtimeDrops", drops);
        json.add("missingRuntimeDrops", strings(dump.missingRuntimeDrops));
        return json;
    }

    private static JsonObject guiJson(GuiDump dump) {
        JsonObject json = new JsonObject();
        JsonObject ids = new JsonObject();
        for (Map.Entry<String, Integer> entry : dump.ids.entrySet()) {
            ids.addProperty(entry.getKey(), entry.getValue());
        }
        json.add("ids", ids);
        json.add("serverBacked", strings(dump.serverBacked));
        json.add("clientOnly", strings(dump.clientOnly));
        JsonObject bindings = new JsonObject();
        for (Map.Entry<String, GuiBinding> entry : dump.bindings.entrySet()) {
            JsonObject binding = new JsonObject();
            binding.addProperty("serverElement", entry.getValue().serverElement);
            binding.addProperty("clientElement", entry.getValue().clientElement);
            binding.addProperty("tileEntity", entry.getValue().tileEntity);
            bindings.add(entry.getKey(), binding);
        }
        json.add("bindings", bindings);
        return json;
    }

    private static JsonObject recipeJson(RecipeDump dump) {
        JsonObject json = new JsonObject();
        json.addProperty("forgeCrafting", dump.forgeCrafting);
        json.addProperty("smelting", dump.smelting);
        json.addProperty("worktable", dump.worktable);
        json.addProperty("crucible", dump.crucible);
        json.addProperty("incubator", dump.incubator);
        json.addProperty("altarRituals", dump.altarRituals);
        json.addProperty("altarEntries", dump.altarEntries);
        return json;
    }

    private static JsonObject resourceJson(ResourceDump dump) {
        JsonObject json = new JsonObject();
        json.addProperty("root", dump.root);
        JsonObject counts = new JsonObject();
        for (Map.Entry<String, Integer> entry : dump.counts.entrySet()) {
            counts.addProperty(entry.getKey(), entry.getValue());
        }
        json.add("counts", counts);
        json.add("missingItemModels", strings(dump.missingItemModels));
        json.add("missingBlockstates", strings(dump.missingBlockstates));
        json.add("missingBlockLootTables", strings(dump.missingBlockLootTables));
        json.add("missingStructureTemplates", strings(dump.missingStructureTemplates));
        json.add("missingChestLootTables", strings(dump.missingChestLootTables));
        json.add("missingEntityLootTables", strings(dump.missingEntityLootTables));
        return json;
    }

    private static JsonArray strings(List<String> values) {
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(value);
        }
        return array;
    }

    private static void stopServer(MinecraftServer server) {
        try {
            Method method = MinecraftServer.class.getMethod("initiateShutdown");
            method.invoke(server);
        } catch (Exception e) {
            Eidolon.LOGGER.warn("Unable to auto-stop server after diagnostics; stop it manually", e);
        }
    }

    private static void countFiles(Path path, Map<String, Integer> counts, String key) {
        counts.put(key, countFiles(path));
    }

    private static int countFiles(Path path) {
        if (!Files.isDirectory(path)) {
            return 0;
        }
        try {
            return (int) Files.walk(path).filter(Files::isRegularFile).count();
        } catch (IOException e) {
            return 0;
        }
    }

    private static void requireFile(List<String> failures, List<String> bucket, Path path, String message, String id) {
        if (!Files.isRegularFile(path)) {
            bucket.add(id);
            failures.add(message);
        }
    }

    private static void appendFailures(List<String> failures, String label, List<String> values) {
        for (String value : values) {
            failures.add(label + ": " + value);
        }
    }

    private static boolean classFileExists(String className) {
        if (className == null || className.isEmpty()) {
            return false;
        }
        String resource = className.replace('.', '/') + ".class";
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        return (contextLoader != null && contextLoader.getResource(resource) != null)
                || RuntimeDiagnostics.class.getClassLoader().getResource(resource) != null;
    }

    private static boolean isEidolon(ResourceLocation name) {
        return name != null && Reference.MOD_ID.equals(name.getNamespace());
    }

    private static boolean isEidolon(net.minecraft.item.ItemStack stack) {
        return stack != null && !stack.isEmpty() && isEidolon(stack.getItem().getRegistryName());
    }

    private static Path resolveOutputDir() {
        Path output = Paths.get(System.getProperty(OUTPUT_PROPERTY, "runtime-dump"));
        if (output.isAbsolute()) {
            return output;
        }
        return projectRoot().resolve(output).toAbsolutePath();
    }

    private static Path projectRoot() {
        Path current = Paths.get("").toAbsolutePath();
        while (current != null) {
            if (Files.isRegularFile(current.resolve("build.gradle"))
                    && Files.isDirectory(current.resolve("src/main/resources/assets/" + Reference.MOD_ID))) {
                return current;
            }
            current = current.getParent();
        }
        return Paths.get("").toAbsolutePath();
    }

    private static String path(String id) {
        int index = id.indexOf(':');
        return index >= 0 ? id.substring(index + 1) : id;
    }

    private static List<String> difference(List<String> left, List<String> right) {
        List<String> result = new ArrayList<>();
        for (String value : left) {
            if (!right.contains(value)) {
                result.add(value);
            }
        }
        result.sort(Comparator.naturalOrder());
        return result;
    }

    private static void sortRegistry(RegistryDump dump) {
        dump.ids.sort(Comparator.naturalOrder());
        dump.expected.sort(Comparator.naturalOrder());
        dump.missingExpected.sort(Comparator.naturalOrder());
        dump.extraRuntime.sort(Comparator.naturalOrder());
    }

    private static final class Dump {
        RegistryDump items;
        RegistryDump blocks;
        RegistryDump entities;
        TileDump tileEntities;
        DropDump drops;
        GuiDump gui;
        RecipeDump recipes;
        ResourceDump resources;
        List<String> failures = new ArrayList<>();
    }

    private static class RegistryDump {
        List<String> ids = new ArrayList<>();
        List<String> expected = new ArrayList<>();
        List<String> missingExpected = new ArrayList<>();
        List<String> extraRuntime = new ArrayList<>();
    }

    private static final class TileDump extends RegistryDump {
        Map<String, String> classes = new TreeMap<>();
    }

    private static final class DropDump {
        List<String> ids = new ArrayList<>();
        Map<String, String> runtimeDrops = new TreeMap<>();
        List<String> missingRuntimeDrops = new ArrayList<>();
    }

    private static final class GuiDump {
        Map<String, Integer> ids = new LinkedHashMap<>();
        List<String> serverBacked = new ArrayList<>();
        List<String> clientOnly = new ArrayList<>();
        Map<String, GuiBinding> bindings = new LinkedHashMap<>();
    }

    private static final class GuiBinding {
        final String serverElement;
        final String clientElement;
        final String tileEntity;

        GuiBinding(String serverElement, String clientElement, String tileEntity) {
            this.serverElement = serverElement;
            this.clientElement = clientElement;
            this.tileEntity = tileEntity;
        }
    }

    private static final class RecipeDump {
        int forgeCrafting;
        int smelting;
        int worktable;
        int crucible;
        int incubator;
        int altarRituals;
        int altarEntries;
    }

    private static final class ResourceDump {
        String root;
        Map<String, Integer> counts = new LinkedHashMap<>();
        List<String> missingItemModels = new ArrayList<>();
        List<String> missingBlockstates = new ArrayList<>();
        List<String> missingBlockLootTables = new ArrayList<>();
        List<String> missingChestLootTables = new ArrayList<>();
        List<String> missingEntityLootTables = new ArrayList<>();
        List<String> missingStructureTemplates = new ArrayList<>();
    }

    private static final class ModItemsMirror {
        static final String ITEMS_FIELD = "ITEMS";
    }
}
