package elucent.eidolon.codex;

import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CodexChapters {
    private static final List<Category> CATEGORIES = new ArrayList<>();

    private CodexChapters() {
    }

    public static List<Category> getCategories() {
        if (CATEGORIES.isEmpty()) {
            init();
        }
        return Collections.unmodifiableList(CATEGORIES);
    }

    public static void init() {
        if (!CATEGORIES.isEmpty()) {
            return;
        }
        CATEGORIES.add(new Category("nature", new ItemStack(ModItems.ZOMBIE_HEART), 0x598f4c,
                chapter("nature_index", new ItemStack(ModItems.CODEX),
                        new TitledIndexPage("eidolon.codex.page.nature_index.0", new ItemStack(ModItems.TATTERED_CLOTH), new ItemStack(ModBlocks.LEAD_ORE), new ItemStack(ModItems.PEWTER_INGOT), new ItemStack(ModBlocks.ENCHANTED_ASH))),
                chapter("monsters", new ItemStack(ModItems.TATTERED_CLOTH),
                        new TitlePage("eidolon.codex.page.monsters.zombie_brute", new ItemStack(ModItems.SPAWN_ZOMBIE_BRUTE)),
                        new EntityPage("eidolon.codex.page.monsters.wraith", new ItemStack(ModItems.SPAWN_WRAITH)),
                        new TextPage("eidolon.codex.page.monsters.chilled", new ItemStack(Items.SNOWBALL))),
                chapter("ores", new ItemStack(ModBlocks.LEAD_ORE),
                        new TitlePage("eidolon.codex.page.ores.lead_ore", new ItemStack(ModBlocks.LEAD_ORE), new ItemStack(ModBlocks.DEEP_LEAD_ORE), new ItemStack(ModItems.RAW_LEAD), new ItemStack(ModItems.LEAD_INGOT)),
                        new SmeltingPage("eidolon.codex.page.ores.lead_smelting", new ItemStack(ModBlocks.LEAD_ORE), new ItemStack(ModItems.RAW_LEAD), new ItemStack(ModItems.LEAD_INGOT)),
                        new CraftingPage("eidolon.codex.page.ores.lead_storage", new ItemStack(ModBlocks.LEAD_BLOCK), new ItemStack(ModItems.LEAD_INGOT), new ItemStack(ModItems.LEAD_NUGGET)),
                        new TitlePage("eidolon.codex.page.ores.silver_ore", new ItemStack(ModBlocks.SILVER_ORE), new ItemStack(ModBlocks.DEEP_SILVER_ORE), new ItemStack(ModItems.RAW_SILVER), new ItemStack(ModItems.SILVER_INGOT)),
                        new SmeltingPage("eidolon.codex.page.ores.silver_smelting", new ItemStack(ModBlocks.SILVER_ORE), new ItemStack(ModItems.RAW_SILVER), new ItemStack(ModItems.SILVER_INGOT)),
                        new CraftingPage("eidolon.codex.page.ores.silver_storage", new ItemStack(ModBlocks.SILVER_BLOCK), new ItemStack(ModItems.SILVER_INGOT), new ItemStack(ModItems.SILVER_NUGGET))),
                chapter("pewter", new ItemStack(ModItems.PEWTER_INGOT),
                        new TitlePage("eidolon.codex.page.pewter", new ItemStack(ModItems.LEAD_INGOT), new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.PEWTER_BLEND), new ItemStack(ModItems.PEWTER_INGOT)),
                        new CraftingPage("eidolon.codex.page.pewter.blend", stack(ModItems.PEWTER_BLEND, 2), new ItemStack(ModItems.LEAD_INGOT), new ItemStack(Items.IRON_INGOT)),
                        new SmeltingPage("eidolon.codex.page.pewter.smelting", new ItemStack(ModItems.PEWTER_BLEND), new ItemStack(ModItems.PEWTER_INGOT)),
                        new CraftingPage("eidolon.codex.page.pewter.blocks", new ItemStack(ModItems.PEWTER_NUGGET), new ItemStack(ModItems.PEWTER_INGOT), new ItemStack(ModBlocks.PEWTER_BLOCK))),
                chapter("enchanted_ash", new ItemStack(ModBlocks.ENCHANTED_ASH),
                        new TitlePage("eidolon.codex.page.enchanted_ash", new ItemStack(Items.BONE), new ItemStack(ModBlocks.ENCHANTED_ASH)),
                        new SmeltingPage("eidolon.codex.page.enchanted_ash", new ItemStack(Items.BONE), new ItemStack(ModBlocks.ENCHANTED_ASH)))));

        CATEGORIES.add(new Category("rituals", new ItemStack(ModItems.LESSER_SOUL_GEM), 0xdfb22b,
                chapter("rituals_index", new ItemStack(ModBlocks.BRAZIER),
                        new TitledIndexPage("eidolon.codex.page.rituals.0", new ItemStack(ModBlocks.BRAZIER), new ItemStack(ModBlocks.STONE_HAND), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(Items.ROTTEN_FLESH)),
                        new IndexPage("eidolon.codex.page.rituals.1", new ItemStack(Items.GOLDEN_APPLE), new ItemStack(Items.SHIELD), new ItemStack(Items.EMERALD), new ItemStack(Items.CLOCK)),
                        new IndexPage("eidolon.codex.page.rituals.2", new ItemStack(Items.SPECKLED_MELON), new ItemStack(ModItems.SANGUINE_AMULET))),
                chapter("brazier", new ItemStack(ModBlocks.BRAZIER),
                        new TitlePage("eidolon.codex.page.brazier.0", new ItemStack(ModBlocks.BRAZIER)),
                        new TextPage("eidolon.codex.page.brazier.1", new ItemStack(Blocks.COAL_BLOCK), new ItemStack(ModItems.PEWTER_INGOT)),
                        new CraftingPage("eidolon.codex.page.brazier.recipe", new ItemStack(ModBlocks.BRAZIER), new ItemStack(ModItems.PEWTER_INGOT), new ItemStack(Blocks.COAL_BLOCK), new ItemStack(Items.STICK))),
                chapter("item_providers", new ItemStack(ModBlocks.STONE_HAND),
                        new TitlePage("eidolon.codex.page.item_providers.0", new ItemStack(ModBlocks.STONE_HAND)),
                        new CraftingPage("eidolon.codex.page.item_providers.stone_hand", new ItemStack(ModBlocks.STONE_HAND), new ItemStack(Blocks.STONE_SLAB), new ItemStack(Blocks.STONE)),
                        new TextPage("eidolon.codex.page.item_providers.1", new ItemStack(ModBlocks.NECROTIC_FOCUS)),
                        new CraftingPage("eidolon.codex.page.item_providers.necrotic_focus", new ItemStack(ModBlocks.NECROTIC_FOCUS), new ItemStack(Blocks.STONE), new ItemStack(Items.BONE), new ItemStack(ModItems.PEWTER_INLAY))),
                chapter("crystal_ritual", new ItemStack(ModItems.SOUL_SHARD),
                        new TitledRitualPage("eidolon.codex.page.crystal_ritual", new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("summon_ritual", new ItemStack(Items.ROTTEN_FLESH),
                        new TitledRitualPage("eidolon.codex.page.summon_ritual.0", new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.ROTTEN_FLESH), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(Items.COAL, 1, 1)),
                        new TitledRitualPage("eidolon.codex.page.summon_ritual.1", new ItemStack(Items.BONE), new ItemStack(Items.BONE), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(Items.COAL, 1, 1)),
                        new TitledRitualPage("eidolon.codex.page.summon_ritual.2", new ItemStack(ModItems.RAVEN_FEATHER), new ItemStack(ModItems.RAVEN_FEATHER), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(Items.COAL, 1, 1)),
                        new TitledRitualPage("eidolon.codex.page.summon_ritual.3", new ItemStack(Blocks.SOUL_SAND), new ItemStack(Items.BONE), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(Items.COAL, 1, 1)),
                        new TitledRitualPage("eidolon.codex.page.summon_ritual.4", new ItemStack(Blocks.SAND), new ItemStack(Items.ROTTEN_FLESH), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(Items.COAL, 1, 1)),
                        new TitledRitualPage("eidolon.codex.page.summon_ritual.5", new ItemStack(Items.PRISMARINE_SHARD), new ItemStack(Items.ROTTEN_FLESH), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(Items.COAL, 1, 1)),
                        new TitledRitualPage("eidolon.codex.page.summon_ritual.6", new ItemStack(Items.STRING), new ItemStack(Items.BONE), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(Items.COAL, 1, 1)),
                        new TitledRitualPage("eidolon.codex.page.summon_ritual.7", new ItemStack(ModItems.TATTERED_CLOTH), new ItemStack(ModItems.TATTERED_CLOTH), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(Items.COAL, 1, 1))),
                chapter("allure_ritual", new ItemStack(Items.GOLDEN_APPLE),
                        new TitledRitualPage("eidolon.codex.page.allure_ritual", new ItemStack(Blocks.DOUBLE_PLANT, 1, 4), new ItemStack(Items.GOLDEN_APPLE), new ItemStack(Items.DYE, 1, 1), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("repelling_ritual", new ItemStack(Items.SHIELD),
                        new TitledRitualPage("eidolon.codex.page.repelling_ritual", new ItemStack(Items.PRISMARINE_CRYSTALS), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.LEATHER), new ItemStack(Items.QUARTZ))),
                chapter("deceit_ritual", new ItemStack(Items.EMERALD),
                        new TitledRitualPage("eidolon.codex.page.deceit_ritual", new ItemStack(Items.EMERALD), new ItemStack(Items.FERMENTED_SPIDER_EYE), new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("time_rituals", new ItemStack(Items.CLOCK),
                        new TitledRitualPage("eidolon.codex.page.time_rituals.0", new ItemStack(Blocks.DOUBLE_PLANT, 1, 0), new ItemStack(Items.COAL, 1, 1), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ModItems.SOUL_SHARD)),
                        new TitledRitualPage("eidolon.codex.page.time_rituals.1", new ItemStack(Items.DYE, 1, 0), new ItemStack(Items.SNOWBALL), new ItemStack(Items.SPIDER_EYE), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("purify_ritual", new ItemStack(Items.SPECKLED_MELON),
                        new TitledRitualPage("eidolon.codex.page.purify_ritual", new ItemStack(Items.SPECKLED_MELON), new ItemStack(ModBlocks.ENCHANTED_ASH), new ItemStack(Items.POTIONITEM), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("sanguine_ritual", new ItemStack(ModItems.SANGUINE_AMULET),
                        new TitledRitualPage("eidolon.codex.page.sanguine_ritual.0", new ItemStack(ModItems.SAPPING_SWORD), new ItemStack(Items.IRON_SWORD), new ItemStack(Items.POTIONITEM), new ItemStack(ModItems.SHADOW_GEM)),
                        new TitledRitualPage("eidolon.codex.page.sanguine_ritual.1", new ItemStack(ModItems.SANGUINE_AMULET), new ItemStack(ModItems.BASIC_AMULET), new ItemStack(Items.POTIONITEM), new ItemStack(Items.DIAMOND)))));

        CATEGORIES.add(new Category("artifice", new ItemStack(ModItems.GOLD_INLAY), 0xcc3948,
                chapter("artifice_index", new ItemStack(ModItems.GOLD_INLAY),
                        new TitledIndexPage("eidolon.codex.page.artifice", new ItemStack(ModItems.TALLOW), new ItemStack(ModBlocks.CRUCIBLE), new ItemStack(ModItems.ARCANE_GOLD_INGOT), new ItemStack(ModItems.DEATH_ESSENCE)),
                        new IndexPage("eidolon.codex.page.artifice.1", new ItemStack(ModItems.LESSER_SOUL_GEM), new ItemStack(ModItems.SHADOW_GEM), new ItemStack(ModItems.WARPED_SPROUTS), new ItemStack(ModItems.GOLD_INLAY)),
                        new IndexPage("eidolon.codex.page.artifice.2", new ItemStack(ModItems.BASIC_RING), new ItemStack(ModBlocks.WORKTABLE), new ItemStack(ModItems.SOULFIRE_WAND), new ItemStack(ModItems.WARLOCK_HAT))),
                chapter("wooden_stand", new ItemStack(ModBlocks.STONE_HAND),
                        new TitlePage("eidolon.codex.page.wooden_stand.0", new ItemStack(ModBlocks.STONE_HAND)),
                        new CraftingPage("eidolon.codex.page.wooden_stand.stone_hand", new ItemStack(ModBlocks.STONE_HAND), new ItemStack(Blocks.STONE_SLAB), new ItemStack(Blocks.STONE)),
                        new CruciblePage("eidolon.codex.page.wooden_stand.1", new ItemStack(ModItems.FUNGUS_SPROUTS), new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.WHEAT_SEEDS))),
                chapter("tallow", new ItemStack(ModItems.TALLOW),
                        new TitlePage("eidolon.codex.page.tallow.0", new ItemStack(Items.ROTTEN_FLESH), new ItemStack(ModItems.TALLOW)),
                        new SmeltingPage("eidolon.codex.page.tallow.0", new ItemStack(Items.ROTTEN_FLESH), new ItemStack(ModItems.TALLOW)),
                        new CraftingPage("eidolon.codex.page.tallow.1", stack(ModBlocks.CANDLE, 4), new ItemStack(Items.STRING), new ItemStack(ModItems.TALLOW)),
                        new CraftingPage("eidolon.codex.page.tallow.candlestick", new ItemStack(ModBlocks.CANDLESTICK), new ItemStack(ModBlocks.CANDLE), new ItemStack(ModItems.ARCANE_GOLD_NUGGET)),
                        new CraftingPage("eidolon.codex.page.tallow.magic_candle", stack(ModBlocks.MAGIC_CANDLE, 4), new ItemStack(Items.STRING), new ItemStack(ModItems.MAGICIANS_WAX)),
                        new CraftingPage("eidolon.codex.page.tallow.magic_candlestick", new ItemStack(ModBlocks.MAGIC_CANDLESTICK), new ItemStack(ModBlocks.MAGIC_CANDLE), new ItemStack(ModItems.ARCANE_GOLD_NUGGET))),
                chapter("crucible", new ItemStack(ModBlocks.CRUCIBLE),
                        new TitlePage("eidolon.codex.page.crucible.0", new ItemStack(ModBlocks.CRUCIBLE)),
                        new TextPage("eidolon.codex.page.crucible.1", new ItemStack(ModItems.ALCHEMISTS_TONGS)),
                        new CraftingPage("eidolon.codex.page.crucible.recipe", new ItemStack(ModBlocks.CRUCIBLE), new ItemStack(ModItems.PEWTER_INGOT))),
                chapter("arcane_gold", new ItemStack(ModItems.ARCANE_GOLD_INGOT),
                        new TitlePage("eidolon.codex.page.arcane_gold.0", new ItemStack(ModItems.ARCANE_GOLD_INGOT)),
                        new CruciblePage("eidolon.codex.page.arcane_gold.0", stack(ModItems.ARCANE_GOLD_INGOT, 2), new ItemStack(Items.REDSTONE), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(Items.GOLD_INGOT)),
                        new CraftingPage("eidolon.codex.page.arcane_gold.1", new ItemStack(ModItems.ARCANE_GOLD_NUGGET), new ItemStack(ModItems.ARCANE_GOLD_INGOT), new ItemStack(ModBlocks.ARCANE_GOLD_BLOCK))),
                chapter("reagents", new ItemStack(ModItems.DEATH_ESSENCE),
                        new ListPage("eidolon.codex.page.reagents.0", new ItemStack[] {new ItemStack(ModItems.SULFUR), new ItemStack(ModItems.DEATH_ESSENCE), new ItemStack(ModItems.CRIMSON_ESSENCE), new ItemStack(ModItems.ENDER_CALX)}, "sulfur", "death", "crimson", "ender"),
                        new CruciblePage("eidolon.codex.page.reagents.sulfur", stack(ModItems.SULFUR, 2), new ItemStack(Items.COAL), new ItemStack(ModBlocks.ENCHANTED_ASH)),
                        new CruciblePage("eidolon.codex.page.reagents.1", stack(ModItems.DEATH_ESSENCE, 4), new ItemStack(ModItems.ZOMBIE_HEART), new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.DYE, 1, 15)),
                        new CruciblePage("eidolon.codex.page.reagents.1_withered", stack(ModItems.DEATH_ESSENCE, 8), new ItemStack(ModItems.WITHERED_HEART), new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.COAL, 1, 1)),
                        new CruciblePage("eidolon.codex.page.reagents.2", stack(ModItems.CRIMSON_ESSENCE, 4), new ItemStack(Blocks.RED_MUSHROOM), new ItemStack(Items.NETHER_WART), new ItemStack(ModItems.SULFUR)),
                        new CruciblePage("eidolon.codex.page.reagents.2_vines", stack(ModItems.CRIMSON_ESSENCE, 2), new ItemStack(Blocks.VINE), new ItemStack(Items.NETHER_WART), new ItemStack(ModItems.SULFUR)),
                        new CruciblePage("eidolon.codex.page.reagents.3", stack(ModItems.ENDER_CALX, 2), new ItemStack(Items.ENDER_PEARL), new ItemStack(ModBlocks.ENCHANTED_ASH)),
                        new CruciblePage("eidolon.codex.page.reagents.magic_ink", stack(ModItems.MAGIC_INK, 2), new ItemStack(Items.DYE, 1, 0), new ItemStack(Items.DYE, 1, 4), new ItemStack(ModItems.SILVER_NUGGET)),
                        new CruciblePage("eidolon.codex.page.reagents.magicians_wax", stack(ModItems.MAGICIANS_WAX, 4), new ItemStack(ModItems.ENDER_CALX), new ItemStack(Items.REDSTONE), new ItemStack(ModItems.TALLOW)),
                        new CruciblePage("eidolon.codex.page.reagents.parchment", stack(ModItems.PARCHMENT, 4), new ItemStack(Items.LEATHER), new ItemStack(Items.PAPER), new ItemStack(ModBlocks.ENCHANTED_ASH))),
                chapter("soul_gems", new ItemStack(ModItems.LESSER_SOUL_GEM),
                        new TitlePage("eidolon.codex.page.soul_gems", new ItemStack(ModItems.SOUL_SHARD), new ItemStack(ModItems.LESSER_SOUL_GEM)),
                        new CruciblePage("eidolon.codex.page.soul_gems", new ItemStack(ModItems.LESSER_SOUL_GEM), new ItemStack(Items.REDSTONE), new ItemStack(Items.DYE, 1, 4), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("shadow_gem", new ItemStack(ModItems.SHADOW_GEM),
                        new TitlePage("eidolon.codex.page.shadow_gem", new ItemStack(ModItems.SHADOW_GEM)),
                        new CruciblePage("eidolon.codex.page.shadow_gem", new ItemStack(ModItems.SHADOW_GEM), new ItemStack(Items.COAL), new ItemStack(Items.GHAST_TEAR), new ItemStack(ModItems.DEATH_ESSENCE)),
                        new CruciblePage("eidolon.codex.page.shadow_gem.final", new ItemStack(ModItems.SHADOW_GEM), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(ModItems.DEATH_ESSENCE), new ItemStack(Items.DIAMOND))),
                chapter("warped_sprouts", new ItemStack(ModItems.WARPED_SPROUTS),
                        new TitlePage("eidolon.codex.page.warped_sprouts.0", new ItemStack(ModItems.WARPED_SPROUTS)),
                        new CruciblePage("eidolon.codex.page.warped_sprouts.0", stack(ModItems.WARPED_SPROUTS, 2), new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(ModItems.ENDER_CALX), new ItemStack(Items.NETHER_WART)),
                        new TextPage("eidolon.codex.page.warped_sprouts.1", new ItemStack(ModItems.ENDER_CALX), new ItemStack(Items.NETHER_WART))),
                chapter("basic_alchemy", new ItemStack(Items.GUNPOWDER),
                        new ListPage("eidolon.codex.page.basic_alchemy", new ItemStack[] {new ItemStack(Items.LEATHER), new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.GUNPOWDER), new ItemStack(Items.GOLDEN_APPLE)}, "leather", "flesh", "gunpowder", "gilding"),
                        new CruciblePage("eidolon.codex.page.basic_alchemy.0", new ItemStack(Items.LEATHER), new ItemStack(ModBlocks.ENCHANTED_ASH), new ItemStack(Items.ROTTEN_FLESH)),
                        new CruciblePage("eidolon.codex.page.basic_alchemy.1", new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(Items.PORKCHOP)),
                        new CruciblePage("eidolon.codex.page.basic_alchemy.2", stack(Items.GUNPOWDER, 4), new ItemStack(ModItems.SULFUR), new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.COAL, 1, 1)),
                        new CruciblePage("eidolon.codex.page.basic_alchemy.3", new ItemStack(Items.GOLDEN_APPLE), new ItemStack(Items.GOLDEN_CARROT), new ItemStack(Items.SPECKLED_MELON), new ItemStack(ModBlocks.ENCHANTED_ASH)),
                        new CruciblePage("eidolon.codex.page.basic_alchemy.4", stack(Items.GLOWSTONE_DUST, 2), new ItemStack(Items.SPECKLED_MELON), new ItemStack(ModItems.SULFUR))),
                chapter("inlays", new ItemStack(ModItems.GOLD_INLAY),
                        new TitlePage("eidolon.codex.page.inlays", new ItemStack(ModItems.PEWTER_INLAY), new ItemStack(ModItems.GOLD_INLAY)),
                        new CraftingPage("eidolon.codex.page.inlays.pewter", stack(ModItems.PEWTER_INLAY, 2), new ItemStack(ModItems.PEWTER_INGOT)),
                        new CraftingPage("eidolon.codex.page.inlays.gold", stack(ModItems.GOLD_INLAY, 2), new ItemStack(ModItems.ARCANE_GOLD_INGOT))),
                chapter("basic_baubles", new ItemStack(ModItems.BASIC_RING),
                        new TitlePage("eidolon.codex.page.basic_baubles", new ItemStack(ModItems.BASIC_RING), new ItemStack(ModItems.BASIC_AMULET), new ItemStack(ModItems.BASIC_BELT)),
                        new CraftingPage("eidolon.codex.page.basic_baubles.amulet", new ItemStack(ModItems.BASIC_AMULET), new ItemStack(Items.STRING), new ItemStack(ModItems.ARCANE_GOLD_INGOT)),
                        new CraftingPage("eidolon.codex.page.basic_baubles.ring", new ItemStack(ModItems.BASIC_RING), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(ModItems.ARCANE_GOLD_INGOT)),
                        new CraftingPage("eidolon.codex.page.basic_baubles.belt", new ItemStack(ModItems.BASIC_BELT), new ItemStack(Items.LEATHER))),
                chapter("magic_workbench", new ItemStack(ModBlocks.WORKTABLE),
                        new TitlePage("eidolon.codex.page.magic_workbench", new ItemStack(ModBlocks.WORKTABLE)),
                        new CraftingPage("eidolon.codex.page.magic_workbench.recipe", new ItemStack(ModBlocks.WORKTABLE), new ItemStack(Blocks.CARPET, 1, 10), new ItemStack(ModItems.PEWTER_INLAY), new ItemStack(Blocks.PLANKS))),
                chapter("void_amulet", new ItemStack(ModItems.VOID_AMULET),
                        new WorktablePage("eidolon.codex.page.void_amulet", new ItemStack(ModItems.VOID_AMULET), new ItemStack(ModItems.BASIC_AMULET), new ItemStack(ModItems.PEWTER_INLAY), new ItemStack(ModItems.SOUL_SHARD)),
                        new WorktablePage("eidolon.codex.page.void_amulet.materials", new ItemStack(ModItems.VOID_AMULET), new ItemStack(Blocks.OBSIDIAN), new ItemStack(ModItems.PEWTER_INGOT))),
                chapter("warded_mail", new ItemStack(ModItems.WARDED_MAIL),
                        new WorktablePage("eidolon.codex.page.warded_mail", new ItemStack(ModItems.WARDED_MAIL), new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(ModBlocks.ENCHANTED_ASH), new ItemStack(ModItems.SOUL_SHARD)),
                        new WorktablePage("eidolon.codex.page.warded_mail.materials", new ItemStack(ModItems.WARDED_MAIL), new ItemStack(ModItems.PEWTER_INLAY))),
                chapter("soulfire_wand", new ItemStack(ModItems.SOULFIRE_WAND),
                        new WorktablePage("eidolon.codex.page.soulfire_wand", new ItemStack(ModItems.SOULFIRE_WAND), new ItemStack(ModItems.SHADOW_GEM), new ItemStack(ModItems.GOLD_INLAY), new ItemStack(ModItems.LESSER_SOUL_GEM)),
                        new WorktablePage("eidolon.codex.page.soulfire_wand.materials", new ItemStack(ModItems.SOULFIRE_WAND), new ItemStack(ModItems.ARCANE_GOLD_INGOT), new ItemStack(Items.BLAZE_POWDER), new ItemStack(Items.STICK))),
                chapter("bonechill_wand", new ItemStack(ModItems.BONECHILL_WAND),
                        new WorktablePage("eidolon.codex.page.bonechill_wand", new ItemStack(ModItems.BONECHILL_WAND), new ItemStack(ModItems.WRAITH_HEART), new ItemStack(ModItems.PEWTER_INLAY), new ItemStack(ModItems.LESSER_SOUL_GEM)),
                        new WorktablePage("eidolon.codex.page.bonechill_wand.materials", new ItemStack(ModItems.BONECHILL_WAND), new ItemStack(ModItems.PEWTER_INGOT), new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.STICK))),
                chapter("reaper_scythe", new ItemStack(ModItems.REAPER_SCYTHE),
                        new WorktablePage("eidolon.codex.page.reaper_scythe", new ItemStack(ModItems.REAPER_SCYTHE), new ItemStack(ModItems.PEWTER_INGOT), new ItemStack(Items.STICK), new ItemStack(ModItems.UNHOLY_SYMBOL)),
                        new WorktablePage("eidolon.codex.page.reaper_scythe.materials", new ItemStack(ModItems.REAPER_SCYTHE), new ItemStack(ModItems.TATTERED_CLOTH), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("cleaving_axe", new ItemStack(ModItems.CLEAVING_AXE),
                        new WorktablePage("eidolon.codex.page.cleaving_axe", new ItemStack(ModItems.CLEAVING_AXE), new ItemStack(ModItems.PEWTER_INGOT), new ItemStack(Items.STICK), new ItemStack(ModItems.UNHOLY_SYMBOL)),
                        new WorktablePage("eidolon.codex.page.cleaving_axe.materials", new ItemStack(ModItems.CLEAVING_AXE), new ItemStack(ModItems.PEWTER_INLAY))),
                chapter("soul_enchanter", new ItemStack(ModBlocks.SOUL_ENCHANTER),
                        new TitlePage("eidolon.codex.page.soul_enchanter.0", new ItemStack(ModBlocks.SOUL_ENCHANTER)),
                        new TextPage("eidolon.codex.page.soul_enchanter.1", new ItemStack(ModItems.LESSER_SOUL_GEM)),
                        new WorktablePage("eidolon.codex.page.soul_enchanter.recipe", new ItemStack(ModBlocks.SOUL_ENCHANTER), new ItemStack(Items.BOOK), new ItemStack(ModItems.ARCANE_GOLD_INGOT), new ItemStack(Blocks.OBSIDIAN)),
                        new WorktablePage("eidolon.codex.page.soul_enchanter.materials", new ItemStack(ModBlocks.SOUL_ENCHANTER), new ItemStack(Items.DIAMOND), new ItemStack(ModItems.GOLD_INLAY))),
                chapter("reversal_pick", new ItemStack(ModItems.REVERSAL_PICK),
                        new WorktablePage("eidolon.codex.page.reversal_pick", new ItemStack(ModItems.REVERSAL_PICK), new ItemStack(Blocks.OBSIDIAN), new ItemStack(ModItems.PEWTER_INGOT), new ItemStack(ModItems.PEWTER_INLAY)),
                        new WorktablePage("eidolon.codex.page.reversal_pick.materials", new ItemStack(ModItems.REVERSAL_PICK), new ItemStack(Items.ENDER_PEARL), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(ModItems.LESSER_SOUL_GEM))),
                chapter("warlock_armor", new ItemStack(ModItems.WARLOCK_HAT),
                        new TitlePage("eidolon.codex.page.warlock_armor.0", new ItemStack(ModItems.WICKED_WEAVE)),
                        new WorktablePage("eidolon.codex.page.warlock_armor.0", stack(ModItems.WICKED_WEAVE, 8), new ItemStack(Blocks.WOOL), new ItemStack(ModItems.SHADOW_GEM), new ItemStack(ModItems.UNHOLY_SYMBOL)),
                        new WorktablePage("eidolon.codex.page.warlock_armor.1", new ItemStack(ModItems.WARLOCK_HAT), new ItemStack(ModItems.WICKED_WEAVE), new ItemStack(ModItems.SOUL_SHARD)),
                        new WorktablePage("eidolon.codex.page.warlock_armor.2", new ItemStack(ModItems.WARLOCK_CLOAK), new ItemStack(ModItems.WICKED_WEAVE), new ItemStack(ModItems.SOUL_SHARD)),
                        new WorktablePage("eidolon.codex.page.warlock_armor.3", new ItemStack(ModItems.WARLOCK_BOOTS), new ItemStack(ModItems.WICKED_WEAVE), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("gravity_belt", new ItemStack(ModItems.GRAVITY_BELT),
                        new WorktablePage("eidolon.codex.page.gravity_belt", new ItemStack(ModItems.GRAVITY_BELT), new ItemStack(ModItems.BASIC_BELT), new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.FEATHER)),
                        new WorktablePage("eidolon.codex.page.gravity_belt.materials", new ItemStack(ModItems.GRAVITY_BELT), new ItemStack(ModItems.LESSER_SOUL_GEM), new ItemStack(ModItems.ENDER_CALX), new ItemStack(ModItems.PEWTER_INLAY))),
                chapter("prestigious_palm", new ItemStack(ModItems.PRESTIGIOUS_PALM),
                        new WorktablePage("eidolon.codex.page.prestigious_palm", new ItemStack(ModItems.PRESTIGIOUS_PALM), new ItemStack(ModItems.WICKED_WEAVE), new ItemStack(ModItems.LESSER_SOUL_GEM), new ItemStack(ModItems.WARPED_SPROUTS)),
                        new WorktablePage("eidolon.codex.page.prestigious_palm.materials", new ItemStack(ModItems.PRESTIGIOUS_PALM), new ItemStack(ModItems.ENDER_CALX), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("mind_shielding_plate", new ItemStack(ModItems.MIND_SHIELDING_PLATE),
                        new WorktablePage("eidolon.codex.page.mind_shielding_plate", new ItemStack(ModItems.MIND_SHIELDING_PLATE), new ItemStack(ModItems.LEAD_INGOT), new ItemStack(Items.LEATHER), new ItemStack(Blocks.LAPIS_BLOCK)),
                        new WorktablePage("eidolon.codex.page.mind_shielding_plate.materials", new ItemStack(ModItems.MIND_SHIELDING_PLATE), new ItemStack(Items.QUARTZ))),
                chapter("resolute_belt", new ItemStack(ModItems.RESOLUTE_BELT),
                        new WorktablePage("eidolon.codex.page.resolute_belt", new ItemStack(ModItems.RESOLUTE_BELT), new ItemStack(ModItems.BASIC_BELT), new ItemStack(ModItems.GOLD_INLAY), new ItemStack(ModItems.ARCANE_GOLD_INGOT)),
                        new WorktablePage("eidolon.codex.page.resolute_belt.materials", new ItemStack(ModItems.RESOLUTE_BELT), new ItemStack(Items.DIAMOND), new ItemStack(ModItems.SOUL_SHARD), new ItemStack(ModBlocks.ENCHANTED_ASH))),
                chapter("glass_hand", new ItemStack(ModItems.GLASS_HAND),
                        new WorktablePage("eidolon.codex.page.glass_hand", new ItemStack(ModItems.GLASS_HAND), new ItemStack(ModItems.BASIC_AMULET), new ItemStack(Blocks.DIAMOND_BLOCK), new ItemStack(Blocks.GLASS)),
                        new WorktablePage("eidolon.codex.page.glass_hand.materials", new ItemStack(ModItems.GLASS_HAND), new ItemStack(ModItems.ZOMBIE_HEART), new ItemStack(ModItems.WRAITH_HEART), new ItemStack(ModItems.LESSER_SOUL_GEM))),
                chapter("athame", new ItemStack(ModItems.ATHAME),
                        new WorktablePage("eidolon.codex.page.athame", new ItemStack(ModItems.ATHAME), new ItemStack(ModItems.PEWTER_INGOT), new ItemStack(ModItems.PEWTER_INLAY), new ItemStack(Items.ENDER_PEARL)),
                        new WorktablePage("eidolon.codex.page.athame.materials", new ItemStack(ModItems.ATHAME), new ItemStack(Items.GOLD_NUGGET), new ItemStack(ModItems.SILVER_NUGGET)))));

        CATEGORIES.add(new Category("theurgy", new ItemStack(ModBlocks.GOBLET), 0x5e5adb,
                chapter("theurgy_index", new ItemStack(ModItems.PARCHMENT),
                        new TitledIndexPage("eidolon.codex.page.theurgy", new ItemStack(ModItems.NOTETAKING_TOOLS), new ItemStack(ModBlocks.STRAW_EFFIGY), new ItemStack(ModBlocks.WOODEN_ALTAR), new ItemStack(ModBlocks.CANDLE)),
                        new IndexPage("eidolon.codex.page.theurgy.locked", new ItemStack(ModItems.SHADOW_GEM), new ItemStack(Items.PORKCHOP), new ItemStack(ModItems.UNHOLY_SYMBOL), new ItemStack(ModBlocks.STONE_ALTAR)),
                        new IndexPage("eidolon.codex.page.theurgy.fact_locked", new ItemStack(Items.IRON_SWORD))),
                chapter("intro_signs", new ItemStack(Items.PAPER),
                        new TitlePage("eidolon.codex.page.intro_signs.0", new ItemStack(ModItems.UNHOLY_SYMBOL)),
                        new TextPage("eidolon.codex.page.intro_signs.1", new ItemStack(ModItems.PARCHMENT))),
                chapter("research", new ItemStack(ModItems.NOTETAKING_TOOLS),
                        new TitlePage("eidolon.codex.page.research.0", new ItemStack(ModItems.NOTETAKING_TOOLS)),
                        new CraftingPage("eidolon.codex.page.research.notetaking_tools", new ItemStack(ModItems.NOTETAKING_TOOLS), new ItemStack(ModItems.PARCHMENT), new ItemStack(ModItems.MAGIC_INK), new ItemStack(Items.FEATHER)),
                        new TextPage("eidolon.codex.page.research.1", new ItemStack(ModBlocks.RESEARCH_TABLE)),
                        new CraftingPage("eidolon.codex.page.research.table", new ItemStack(ModBlocks.RESEARCH_TABLE), new ItemStack(ModBlocks.MAGIC_CANDLE), new ItemStack(Blocks.CARPET, 1, 14), new ItemStack(Blocks.PLANKS))),
                chapter("effigy", new ItemStack(ModBlocks.STRAW_EFFIGY),
                        new CraftingPage("eidolon.codex.page.effigy", new ItemStack(ModBlocks.STRAW_EFFIGY))),
                chapter("altars", new ItemStack(ModBlocks.WOODEN_ALTAR),
                        new TitlePage("eidolon.codex.page.altars.0", new ItemStack(ModBlocks.WOODEN_ALTAR), new ItemStack(ModBlocks.STONE_ALTAR)),
                        new TextPage("eidolon.codex.page.altars.1", new ItemStack(ModItems.OFFERTORY_PLATE)),
                        new CraftingPage("eidolon.codex.page.altars.wooden_altar", stack(ModBlocks.WOODEN_ALTAR, 3), new ItemStack(Blocks.WOODEN_SLAB), new ItemStack(Blocks.PLANKS))),
                chapter("altar_lights", new ItemStack(ModBlocks.CANDLE),
                        new TitlePage("eidolon.codex.page.altar_lights.0", new ItemStack(ModBlocks.CANDLE)),
                        new ListPage("eidolon.codex.page.altar_lights.1", new ItemStack[] {new ItemStack(Blocks.TORCH), new ItemStack(ModBlocks.CANDLE), new ItemStack(ModBlocks.CANDLESTICK), new ItemStack(ModBlocks.MAGIC_CANDLE)}, "torch", "candle", "candlestick", "magic_candle")),
                chapter("altar_skulls", new ItemStack(Items.SKULL),
                        new TitlePage("eidolon.codex.page.altar_skulls.0", new ItemStack(Items.SKULL)),
                        new ListPage("eidolon.codex.page.altar_skulls.1", new ItemStack[] {new ItemStack(Items.SKULL, 1, 0), new ItemStack(Items.SKULL, 1, 2), new ItemStack(Items.SKULL, 1, 1)}, "skeleton", "zombie", "wither")),
                chapter("altar_herbs", new ItemStack(ModBlocks.AVENNIAN_SPRIG),
                        new TitlePage("eidolon.codex.page.altar_herbs.0", new ItemStack(ModBlocks.AVENNIAN_SPRIG)),
                        new ListPage("eidolon.codex.page.altar_herbs.1", new ItemStack[] {new ItemStack(ModBlocks.AVENNIAN_SPRIG), new ItemStack(ModBlocks.MERAMMER_ROOT), new ItemStack(ModBlocks.OANNA_BLOOM)}, "avennian", "merammer", "oanna")),
                chapter("goblet", new ItemStack(ModBlocks.GOBLET),
                        new TitlePage("eidolon.codex.page.goblet", new ItemStack(ModBlocks.GOBLET)),
                        new CraftingPage("eidolon.codex.page.goblet", new ItemStack(ModBlocks.GOBLET), new ItemStack(ModItems.ARCANE_GOLD_INGOT))),
                chapter("dark_prayer", new ItemStack(ModItems.SHADOW_GEM),
                        new ChantPage("eidolon.codex.page.dark_prayer.0", new ItemStack(ModItems.UNHOLY_SYMBOL)),
                        new TextPage("eidolon.codex.page.dark_prayer.1")),
                chapter("animal_sacrifice", new ItemStack(Items.PORKCHOP),
                        new ChantPage("eidolon.codex.page.animal_sacrifice", new ItemStack(Items.PORKCHOP), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("dark_touch", new ItemStack(ModItems.UNHOLY_SYMBOL),
                        new ChantPage("eidolon.codex.page.dark_touch.0", new ItemStack(ModItems.UNHOLY_SYMBOL)),
                        new TextPage("eidolon.codex.page.dark_touch.1")),
                chapter("stone_altar", new ItemStack(ModBlocks.STONE_ALTAR),
                        new WorktablePage("eidolon.codex.page.stone_altar", stack(ModBlocks.STONE_ALTAR, 3), new ItemStack(Blocks.STONE_SLAB), new ItemStack(Blocks.STONE), new ItemStack(ModItems.PEWTER_INLAY)),
                        new WorktablePage("eidolon.codex.page.stone_altar.materials", stack(ModBlocks.STONE_ALTAR, 3), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("unholy_effigy", new ItemStack(ModBlocks.UNHOLY_EFFIGY),
                        new WorktablePage("eidolon.codex.page.unholy_effigy", new ItemStack(ModBlocks.UNHOLY_EFFIGY), new ItemStack(Blocks.STONE), new ItemStack(ModItems.UNHOLY_SYMBOL), new ItemStack(ModItems.GOLD_INLAY))),
                chapter("villager_sacrifice", new ItemStack(Items.IRON_SWORD),
                        new ChantPage("eidolon.codex.page.villager_sacrifice", new ItemStack(Items.EMERALD), new ItemStack(ModItems.SOUL_SHARD)))));

        CATEGORIES.add(new Category("signs", new ItemStack(ModItems.UNHOLY_SYMBOL), 0xa34acf,
                chapter("signs_index", new ItemStack(ModItems.UNHOLY_SYMBOL),
                        new SignIndexPage("eidolon.codex.page.signs_index.0", new ItemStack(ModItems.UNHOLY_SYMBOL), new ItemStack(Items.GOLDEN_APPLE), new ItemStack(Items.ROTTEN_FLESH), new ItemStack(ModItems.SOUL_SHARD)),
                        new SignIndexPage("eidolon.codex.page.signs_index.1", new ItemStack(Items.BOOK), new ItemStack(Items.BLAZE_POWDER), new ItemStack(Items.SNOWBALL), new ItemStack(Items.WHEAT)),
                        new SignIndexPage("eidolon.codex.page.signs_index.2", new ItemStack(ModItems.DEATH_ESSENCE), new ItemStack(Items.SHIELD), new ItemStack(ModItems.ARCANE_SEAL))),
                sign("wicked_sign", ModItems.UNHOLY_SYMBOL),
                sign("sacred_sign", Items.GOLDEN_APPLE),
                sign("blood_sign", Items.ROTTEN_FLESH),
                sign("soul_sign", ModItems.SOUL_SHARD),
                sign("mind_sign", Items.BOOK),
                sign("flame_sign", Items.BLAZE_POWDER),
                sign("winter_sign", Items.SNOWBALL),
                sign("harmony_sign", Items.WHEAT),
                sign("death_sign", ModItems.DEATH_ESSENCE),
                sign("warding_sign", Items.SHIELD),
                sign("magic_sign", ModItems.ARCANE_SEAL)));

        CATEGORIES.add(new Category("runes", new ItemStack(ModItems.PARCHMENT), 0x4646c2,
                chapter("runes_index", new ItemStack(ModItems.PARCHMENT),
                        new RuneDescPage("eidolon.codex.page.runes_index.0", new ItemStack(ModItems.PARCHMENT)),
                        new RuneIndexPage("eidolon.codex.page.runes_index.1", new ItemStack(ModItems.PEWTER_INLAY), new ItemStack(ModItems.GOLD_INLAY), new ItemStack(ModItems.ARCANE_SEAL)))));
    }

    private static Chapter sign(String id, Object icon) {
        return chapter(id, stack(icon), new TitlePage("eidolon.codex.page." + id, stack(icon)), new SignPage("eidolon.codex.page." + id + "_sign", stack(icon)));
    }

    private static Chapter chapter(String id, ItemStack icon, Page... pages) {
        return new Chapter(id, "eidolon.codex.chapter." + id, icon, pages);
    }

    private static ItemStack stack(Object object, int count) {
        ItemStack stack = stack(object);
        if (!stack.isEmpty()) {
            stack.setCount(count);
        }
        return stack;
    }

    private static ItemStack stack(Object object) {
        if (object instanceof ItemStack) {
            return ((ItemStack) object).copy();
        }
        if (object instanceof net.minecraft.item.Item) {
            return new ItemStack((net.minecraft.item.Item) object);
        }
        if (object instanceof net.minecraft.block.Block) {
            return new ItemStack((net.minecraft.block.Block) object);
        }
        return ItemStack.EMPTY;
    }
}
