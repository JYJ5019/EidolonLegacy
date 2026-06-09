package elucent.eidolon.research;

import elucent.eidolon.Reference;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModItems;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public final class Researches {
    public static final ResourceLocation DEFAULT_RESEARCH = new ResourceLocation(Reference.MOD_ID, "gluttony");
    public static final String USES_WORLD_SEED_TAG = "usesWorldSeed";
    private static final Map<ResourceLocation, Research> RESEARCHES = new LinkedHashMap<>();
    private static final Map<ResourceLocation, List<Research>> BLOCK_RESEARCHES = new LinkedHashMap<>();
    private static final Map<ResourceLocation, List<Research>> ENTITY_RESEARCHES = new LinkedHashMap<>();
    private static final Map<Integer, List<Research>> DIMENSION_RESEARCHES = new LinkedHashMap<>();
    private static final Map<ResourceLocation, List<Research>> FLUID_RESEARCHES = new LinkedHashMap<>();
    private static final List<Function<Random, ResearchTask>> TASK_POOL = new ArrayList<>();
    private static final List<Runnable> CUSTOMIZATIONS = new ArrayList<>();
    static final int GENERATED_TASKS_PER_STEP = 3;
    private static final int DIAGNOSTIC_TASK_FACTORY_SAMPLES = 64;
    private static boolean initialized;

    private Researches() {
    }

    public static void init() {
        initialized = false;
        RESEARCHES.clear();
        BLOCK_RESEARCHES.clear();
        ENTITY_RESEARCHES.clear();
        DIMENSION_RESEARCHES.clear();
        FLUID_RESEARCHES.clear();
        TASK_POOL.clear();

        addTask(ResearchTask.ScrivenerItems::new);
        addTask(ResearchTask.ScrivenerItems::new);
        addTask(ResearchTask.ScrivenerItems::new);
        addTask(ResearchTask.ScrivenerItems::new);
        addTask(ResearchTask.Xp::new);
        addTask(ResearchTask.Xp::new);

        ResourceLocation gluttonyId = new ResourceLocation(Reference.MOD_ID, "gluttony");
        ResourceLocation testBlockId = new ResourceLocation(Reference.MOD_ID, "test_block_research");
        ResourceLocation overworldId = new ResourceLocation(Reference.MOD_ID, "overworld");
        ResourceLocation netherId = new ResourceLocation(Reference.MOD_ID, "nether");
        ResourceLocation endId = new ResourceLocation(Reference.MOD_ID, "end");
        ResourceLocation waterId = new ResourceLocation(Reference.MOD_ID, "water");
        ResourceLocation codexId = new ResourceLocation(Reference.MOD_ID, "codex");
        ResourceLocation worktableId = new ResourceLocation(Reference.MOD_ID, "worktable");
        ResourceLocation crucibleId = new ResourceLocation(Reference.MOD_ID, "crucible");
        ResourceLocation researchTableId = new ResourceLocation(Reference.MOD_ID, "research_table");
        ResourceLocation altarId = new ResourceLocation(Reference.MOD_ID, "altar");
        ResourceLocation enchantedAshId = new ResourceLocation(Reference.MOD_ID, "enchanted_ash");

        Research gluttony = register(new Research(gluttonyId, 5).source("research_source.eidolon.gluttony"));
        Research testBlock = register(new Research(testBlockId, 1).requires(overworldId).source("research_source.eidolon.test_block_research"));
        Research overworld = register(new Research(overworldId, 1).source("research_source.eidolon.overworld"));
        Research nether = register(new Research(netherId, 1).requires(overworldId).source("research_source.eidolon.nether"));
        Research end = register(new Research(endId, 1).requires(netherId).source("research_source.eidolon.end"));
        Research water = register(new Research(waterId, 1).source("research_source.eidolon.water"));
        Research lava = register(new Research(new ResourceLocation(Reference.MOD_ID, "lava"), 1).requires(netherId).source("research_source.eidolon.lava"));
        Research codex = register(new Research(codexId, 1).source("research_source.eidolon.codex"));
        Research worktable = register(new Research(worktableId, 2).requires(codexId).source("research_source.eidolon.worktable"));
        Research crucible = register(new Research(crucibleId, 2).requires(worktableId, waterId).source("research_source.eidolon.crucible"));
        Research researchTable = register(new Research(researchTableId, 2).requires(codexId).source("research_source.eidolon.research_table"));
        Research altar = register(new Research(altarId, 2).requires(worktableId).source("research_source.eidolon.altar"));
        Research enchantedAsh = register(new Research(enchantedAshId, 2).requires(altarId).source("research_source.eidolon.enchanted_ash"));

        addEntityResearch(new ResourceLocation("minecraft", "pig"), gluttony);
        addBlockResearch(ModBlocks.TEST_STONE, testBlock);
        addBlockResearch(ModBlocks.WORKTABLE, worktable);
        addBlockResearch(ModBlocks.CRUCIBLE, crucible);
        addBlockResearch(ModBlocks.RESEARCH_TABLE, researchTable);
        addBlockResearch(ModBlocks.STONE_ALTAR, altar);
        addBlockResearch(ModBlocks.WOODEN_ALTAR, altar);
        addBlockResearch(ModBlocks.ENCHANTED_ASH, enchantedAsh);
        addDimensionResearch(0, overworld);
        addDimensionResearch(0, codex);
        addDimensionResearch(-1, nether);
        addDimensionResearch(1, end);
        addFluidResearch(Blocks.WATER, water);
        addFluidResearch(Blocks.FLOWING_WATER, water);
        addFluidResearch(Blocks.LAVA, lava);
        addFluidResearch(Blocks.FLOWING_LAVA, lava);

        initialized = true;
        applyCustomizations();
    }

    public static Research register(Research research) {
        RESEARCHES.put(research.getId(), research);
        replaceResearchInTriggers(research);
        return research;
    }

    public static void addResearch(Research research) {
        addCustomization(() -> register(research));
    }

    public static boolean removeResearch(ResourceLocation id) {
        addCustomization(() -> {
            RESEARCHES.remove(id);
            removeResearchFromTriggers(id);
        });
        return initialized && !RESEARCHES.containsKey(id);
    }

    public static int removeAllResearches() {
        int count = initialized ? RESEARCHES.size() : 0;
        addCustomization(() -> {
            RESEARCHES.clear();
            BLOCK_RESEARCHES.clear();
            ENTITY_RESEARCHES.clear();
            DIMENSION_RESEARCHES.clear();
            FLUID_RESEARCHES.clear();
        });
        return count;
    }

    public static void addTask(Function<Random, ResearchTask> task) {
        TASK_POOL.add(task);
    }

    public static Collection<Research> getResearches() {
        ensureInitialized();
        return RESEARCHES.values();
    }

    public static void addBlockResearch(Block block, Research research) {
        ResourceLocation id = block.getRegistryName();
        if (id != null) {
            addBlockResearch(id, research);
        }
    }

    public static void addBlockResearch(ResourceLocation id, Research research) {
        BLOCK_RESEARCHES.computeIfAbsent(id, key -> new ArrayList<>()).add(research);
    }

    public static void addBlockResearch(ResourceLocation id, ResourceLocation researchId) {
        addCustomization(() -> addBlockResearch(id, requireResearch(researchId)));
    }

    public static void addEntityResearch(ResourceLocation id, Research research) {
        ENTITY_RESEARCHES.computeIfAbsent(id, key -> new ArrayList<>()).add(research);
    }

    public static void addEntityResearch(ResourceLocation id, ResourceLocation researchId) {
        addCustomization(() -> addEntityResearch(id, requireResearch(researchId)));
    }

    public static void addDimensionResearch(int dimension, Research research) {
        DIMENSION_RESEARCHES.computeIfAbsent(dimension, key -> new ArrayList<>()).add(research);
    }

    public static void addDimensionResearch(int dimension, ResourceLocation researchId) {
        addCustomization(() -> addDimensionResearch(dimension, requireResearch(researchId)));
    }

    public static void addFluidResearch(Block block, Research research) {
        ResourceLocation id = block.getRegistryName();
        if (id != null) {
            FLUID_RESEARCHES.computeIfAbsent(id, key -> new ArrayList<>()).add(research);
        }
    }

    public static void addFluidResearch(ResourceLocation id, ResourceLocation researchId) {
        addCustomization(() -> FLUID_RESEARCHES.computeIfAbsent(id, key -> new ArrayList<>()).add(requireResearch(researchId)));
    }

    public static void removeBlockResearches(ResourceLocation id) {
        addCustomization(() -> BLOCK_RESEARCHES.remove(id));
    }

    public static void removeEntityResearches(ResourceLocation id) {
        addCustomization(() -> ENTITY_RESEARCHES.remove(id));
    }

    public static void removeDimensionResearches(int dimension) {
        addCustomization(() -> DIMENSION_RESEARCHES.remove(dimension));
    }

    public static void removeFluidResearches(ResourceLocation id) {
        addCustomization(() -> FLUID_RESEARCHES.remove(id));
    }

    public static void removeAllTriggers() {
        addCustomization(() -> {
            BLOCK_RESEARCHES.clear();
            ENTITY_RESEARCHES.clear();
            DIMENSION_RESEARCHES.clear();
            FLUID_RESEARCHES.clear();
        });
    }

    public static Collection<Research> getBlockResearches(Block block) {
        ensureInitialized();
        ResourceLocation id = block.getRegistryName();
        return id == null ? new ArrayList<>() : new ArrayList<>(BLOCK_RESEARCHES.getOrDefault(id, new ArrayList<>()));
    }

    public static Collection<Research> getEntityResearches(EntityLivingBase entity) {
        ensureInitialized();
        ResourceLocation id = EntityList.getKey(entity);
        return id == null ? new ArrayList<>() : new ArrayList<>(ENTITY_RESEARCHES.getOrDefault(id, new ArrayList<>()));
    }

    public static Collection<Research> getDimensionResearches(int dimension) {
        ensureInitialized();
        return new ArrayList<>(DIMENSION_RESEARCHES.getOrDefault(dimension, new ArrayList<>()));
    }

    public static Collection<Research> getFluidResearches(Block block) {
        ensureInitialized();
        ResourceLocation id = block.getRegistryName();
        return id == null ? new ArrayList<>() : new ArrayList<>(FLUID_RESEARCHES.getOrDefault(id, new ArrayList<>()));
    }

    @Nullable
    public static Research find(ResourceLocation id) {
        ensureInitialized();
        return RESEARCHES.get(id);
    }

    public static ResearchTask getRandomTask(Random random) {
        ensureInitialized();
        if (TASK_POOL.isEmpty()) {
            throw new IllegalStateException("No research tasks registered");
        }
        return TASK_POOL.get(random.nextInt(TASK_POOL.size())).apply(random);
    }

    public static ResearchTaskSlotDiagnostics getTaskSlotDiagnostics(int slotLimit) {
        ensureInitialized();
        ResearchTaskSlotDiagnostics result = new ResearchTaskSlotDiagnostics(slotLimit);
        result.researchCount = RESEARCHES.size();
        result.taskPoolSize = TASK_POOL.size();
        result.generatedTasksPerStep = GENERATED_TASKS_PER_STEP;
        result.taskFactorySamples = DIAGNOSTIC_TASK_FACTORY_SAMPLES;
        result.maxTaskPoolItemSlots = getMaxTaskPoolItemSlotsForDiagnostics(result);
        result.maxDefaultStepItemSlots = result.generatedTasksPerStep * result.maxTaskPoolItemSlots;

        for (Research research : RESEARCHES.values()) {
            for (int step = 0; step < research.getStars(); step++) {
                int itemSlots = Math.max(result.maxDefaultStepItemSlots,
                        getTaskItemSlotCount(research.getTasks(0, step)));
                String stepKey = research.getId() + "#step_" + step;
                result.stepItemSlots.put(stepKey, itemSlots);
                if (itemSlots > result.maxStepItemSlots) {
                    result.maxStepItemSlots = itemSlots;
                    result.maxStep = stepKey;
                }
                if (itemSlots > slotLimit) {
                    result.overflowingSteps.add(stepKey + " requires " + itemSlots + " item slots");
                }
            }
        }
        return result;
    }

    public static void ensureDefaultResearch(ItemStack stack) {
        ensureInitialized();
        if (stack.isEmpty() || stack.getItem() != ModItems.RESEARCH_NOTES) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        boolean hadProgressData = tag.hasKey("research") || tag.hasKey("stepsDone");
        if (!tag.hasKey("research")) {
            tag.setString("research", DEFAULT_RESEARCH.toString());
        }
        if (!tag.hasKey("stepsDone")) {
            tag.setInteger("stepsDone", 0);
        }
        if (!hadProgressData && !tag.hasKey(USES_WORLD_SEED_TAG)) {
            tag.setBoolean(USES_WORLD_SEED_TAG, true);
        }
    }

    public static ItemStack createNotes(Research research) {
        ensureInitialized();
        ItemStack notes = new ItemStack(ModItems.RESEARCH_NOTES);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("research", research.getId().toString());
        tag.setInteger("stepsDone", 0);
        tag.setBoolean(USES_WORLD_SEED_TAG, true);
        notes.setTagCompound(tag);
        return notes;
    }

    public static boolean usesWorldSeed(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() == ModItems.RESEARCH_NOTES
                && stack.hasTagCompound()
                && stack.getTagCompound().getBoolean(USES_WORLD_SEED_TAG);
    }

    public static boolean tryCreateNotes(EntityPlayer player, ItemStack tools, EnumHand hand, Collection<Research> researches) {
        ensureInitialized();
        if (researches.isEmpty()) {
            return false;
        }
        Research research = null;
        for (Research candidate : researches) {
            if (candidate.isUnlockedFor(player)) {
                research = candidate;
                break;
            }
        }
        if (research == null) {
            return false;
        }
        if (!player.world.isRemote) {
            ItemStack notes = createNotes(research);
            tools.shrink(1);
            if (tools.isEmpty()) {
                player.setHeldItem(hand, notes);
            } else if (!player.inventory.addItemStackToInventory(notes)) {
                player.dropItem(notes, false);
            }
        }
        return true;
    }

    private static void ensureInitialized() {
        if (!initialized) {
            init();
        }
    }

    private static int getTaskItemSlotCount(List<ResearchTask> tasks) {
        int slots = 0;
        for (ResearchTask task : tasks) {
            slots += Math.max(0, task.getSlotCount());
        }
        return slots;
    }

    private static int getMaxTaskPoolItemSlotsForDiagnostics(ResearchTaskSlotDiagnostics result) {
        int max = 0;
        for (int factoryIndex = 0; factoryIndex < TASK_POOL.size(); factoryIndex++) {
            Function<Random, ResearchTask> factory = TASK_POOL.get(factoryIndex);
            for (int sample = 0; sample < DIAGNOSTIC_TASK_FACTORY_SAMPLES; sample++) {
                try {
                    ResearchTask task = factory.apply(new Random(factoryIndex * 341873128712L + sample));
                    if (task == null) {
                        result.factoryFailures.add("task factory " + factoryIndex + " sample " + sample + " returned null");
                    } else {
                        max = Math.max(max, task.getSlotCount());
                    }
                } catch (RuntimeException e) {
                    result.factoryFailures.add("task factory " + factoryIndex + " sample " + sample + " failed: "
                            + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        }
        return max;
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

    private static Research requireResearch(ResourceLocation id) {
        Research research = RESEARCHES.get(id);
        if (research == null) {
            throw new IllegalArgumentException("Unknown Eidolon research id: " + id);
        }
        return research;
    }

    private static void replaceResearchInTriggers(Research research) {
        replaceResearchInTriggers(BLOCK_RESEARCHES, research);
        replaceResearchInTriggers(ENTITY_RESEARCHES, research);
        replaceResearchInTriggers(DIMENSION_RESEARCHES, research);
        replaceResearchInTriggers(FLUID_RESEARCHES, research);
    }

    private static <K> void replaceResearchInTriggers(Map<K, List<Research>> map, Research research) {
        for (List<Research> list : map.values()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(research.getId())) {
                    list.set(i, research);
                }
            }
        }
    }

    private static void removeResearchFromTriggers(ResourceLocation id) {
        removeResearchFromTriggers(BLOCK_RESEARCHES, id);
        removeResearchFromTriggers(ENTITY_RESEARCHES, id);
        removeResearchFromTriggers(DIMENSION_RESEARCHES, id);
        removeResearchFromTriggers(FLUID_RESEARCHES, id);
    }

    private static <K> void removeResearchFromTriggers(Map<K, List<Research>> map, ResourceLocation id) {
        List<K> emptyKeys = new ArrayList<>();
        for (Map.Entry<K, List<Research>> entry : map.entrySet()) {
            entry.getValue().removeIf(research -> research.getId().equals(id));
            if (entry.getValue().isEmpty()) {
                emptyKeys.add(entry.getKey());
            }
        }
        for (K key : emptyKeys) {
            map.remove(key);
        }
    }

    public static final class ResearchTaskSlotDiagnostics {
        private final int slotLimit;
        private int researchCount;
        private int taskPoolSize;
        private int generatedTasksPerStep;
        private int taskFactorySamples;
        private int maxTaskPoolItemSlots;
        private int maxDefaultStepItemSlots;
        private int maxStepItemSlots;
        private String maxStep = "";
        private final Map<String, Integer> stepItemSlots = new LinkedHashMap<>();
        private final List<String> overflowingSteps = new ArrayList<>();
        private final List<String> factoryFailures = new ArrayList<>();

        private ResearchTaskSlotDiagnostics(int slotLimit) {
            this.slotLimit = slotLimit;
        }

        public int getSlotLimit() {
            return slotLimit;
        }

        public int getResearchCount() {
            return researchCount;
        }

        public int getTaskPoolSize() {
            return taskPoolSize;
        }

        public int getGeneratedTasksPerStep() {
            return generatedTasksPerStep;
        }

        public int getTaskFactorySamples() {
            return taskFactorySamples;
        }

        public int getMaxTaskPoolItemSlots() {
            return maxTaskPoolItemSlots;
        }

        public int getMaxDefaultStepItemSlots() {
            return maxDefaultStepItemSlots;
        }

        public int getMaxStepItemSlots() {
            return maxStepItemSlots;
        }

        public String getMaxStep() {
            return maxStep;
        }

        public Map<String, Integer> getStepItemSlots() {
            return new LinkedHashMap<>(stepItemSlots);
        }

        public List<String> getOverflowingSteps() {
            return new ArrayList<>(overflowingSteps);
        }

        public List<String> getFactoryFailures() {
            return new ArrayList<>(factoryFailures);
        }
    }
}
