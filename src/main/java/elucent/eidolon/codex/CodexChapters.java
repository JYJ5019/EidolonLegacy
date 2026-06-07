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
                chapter("monsters", new ItemStack(ModItems.TATTERED_CLOTH),
                        new TitlePage("eidolon.codex.page.monsters.zombie_brute", new ItemStack(ModItems.SPAWN_ZOMBIE_BRUTE)),
                        new EntityPage("eidolon.codex.page.monsters.wraith", new ItemStack(ModItems.SPAWN_WRAITH)),
                        new TextPage("eidolon.codex.page.monsters.chilled", new ItemStack(Items.SNOWBALL))),
                chapter("ores", new ItemStack(ModBlocks.LEAD_ORE),
                        new TitlePage("eidolon.codex.page.ores.lead_ore", new ItemStack(ModBlocks.LEAD_ORE), new ItemStack(ModItems.RAW_LEAD), new ItemStack(ModItems.LEAD_INGOT)),
                        new SmeltingPage("eidolon.codex.page.ores.silver_ore", new ItemStack(ModBlocks.SILVER_ORE), new ItemStack(ModItems.RAW_SILVER), new ItemStack(ModItems.SILVER_INGOT))),
                chapter("pewter", new ItemStack(ModItems.PEWTER_INGOT),
                        new TitlePage("eidolon.codex.page.pewter", new ItemStack(ModItems.LEAD_INGOT), new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.PEWTER_BLEND), new ItemStack(ModItems.PEWTER_INGOT)),
                        new CraftingPage("eidolon.codex.page.pewter.blocks", new ItemStack(ModItems.PEWTER_NUGGET), new ItemStack(ModItems.PEWTER_INGOT), new ItemStack(ModBlocks.PEWTER_BLOCK))),
                chapter("enchanted_ash", new ItemStack(ModBlocks.ENCHANTED_ASH),
                        new SmeltingPage("eidolon.codex.page.enchanted_ash", new ItemStack(Items.BONE), new ItemStack(ModBlocks.ENCHANTED_ASH)))));

        CATEGORIES.add(new Category("rituals", new ItemStack(ModItems.LESSER_SOUL_GEM), 0xdfb22b,
                chapter("brazier", new ItemStack(ModBlocks.BRAZIER),
                        new TitlePage("eidolon.codex.page.brazier.0", new ItemStack(ModBlocks.BRAZIER)),
                        new TextPage("eidolon.codex.page.brazier.1", new ItemStack(Blocks.COAL_BLOCK), new ItemStack(ModItems.PEWTER_INGOT))),
                chapter("item_providers", new ItemStack(ModBlocks.STONE_HAND),
                        new TitlePage("eidolon.codex.page.item_providers.0", new ItemStack(ModBlocks.STONE_HAND)),
                        new TextPage("eidolon.codex.page.item_providers.1", new ItemStack(ModBlocks.NECROTIC_FOCUS))),
                chapter("crystal_ritual", new ItemStack(ModItems.SOUL_SHARD),
                        new TitledRitualPage("eidolon.codex.page.crystal_ritual", new ItemStack(Items.DYE), new ItemStack(Items.REDSTONE), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("summon_ritual", new ItemStack(Items.ROTTEN_FLESH),
                        new TitledRitualPage("eidolon.codex.page.summon_ritual.0", new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.BONE), new ItemStack(ModItems.SOUL_SHARD)),
                        new TextPage("eidolon.codex.page.summon_ritual.1", new ItemStack(ModItems.TATTERED_CLOTH))),
                chapter("allure_ritual", new ItemStack(Items.GOLDEN_APPLE),
                        new TitledRitualPage("eidolon.codex.page.allure_ritual", new ItemStack(Items.GOLDEN_APPLE), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("repelling_ritual", new ItemStack(Items.SHIELD),
                        new TitledRitualPage("eidolon.codex.page.repelling_ritual", new ItemStack(Items.SHIELD), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("deceit_ritual", new ItemStack(Items.EMERALD),
                        new TitledRitualPage("eidolon.codex.page.deceit_ritual", new ItemStack(Items.EMERALD), new ItemStack(Items.FERMENTED_SPIDER_EYE))),
                chapter("time_rituals", new ItemStack(Items.CLOCK),
                        new TitledRitualPage("eidolon.codex.page.time_rituals.0", new ItemStack(Items.CLOCK), new ItemStack(Items.WHEAT_SEEDS)),
                        new TextPage("eidolon.codex.page.time_rituals.1", new ItemStack(Items.SNOWBALL))),
                chapter("purify_ritual", new ItemStack(Items.SPECKLED_MELON),
                        new TitledRitualPage("eidolon.codex.page.purify_ritual", new ItemStack(Items.SPECKLED_MELON), new ItemStack(ModBlocks.ENCHANTED_ASH))),
                chapter("sanguine_ritual", new ItemStack(ModItems.SANGUINE_AMULET),
                        new TitledRitualPage("eidolon.codex.page.sanguine_ritual.0", new ItemStack(ModItems.SANGUINE_AMULET), new ItemStack(ModItems.SHADOW_GEM)),
                        new TextPage("eidolon.codex.page.sanguine_ritual.1", new ItemStack(Items.IRON_SWORD)))));

        CATEGORIES.add(new Category("artifice", new ItemStack(ModItems.GOLD_INLAY), 0xcc3948,
                chapter("wooden_stand", new ItemStack(ModBlocks.STONE_HAND),
                        new TitlePage("eidolon.codex.page.wooden_stand.0", new ItemStack(ModBlocks.STONE_HAND)),
                        new CruciblePage("eidolon.codex.page.wooden_stand.1", new ItemStack(ModItems.FUNGUS_SPROUTS))),
                chapter("tallow", new ItemStack(ModItems.TALLOW),
                        new SmeltingPage("eidolon.codex.page.tallow.0", new ItemStack(Items.ROTTEN_FLESH), new ItemStack(ModItems.TALLOW)),
                        new CraftingPage("eidolon.codex.page.tallow.1", new ItemStack(ModBlocks.CANDLE), new ItemStack(ModBlocks.CANDLESTICK))),
                chapter("crucible", new ItemStack(ModBlocks.CRUCIBLE),
                        new TitlePage("eidolon.codex.page.crucible.0", new ItemStack(ModBlocks.CRUCIBLE)),
                        new TextPage("eidolon.codex.page.crucible.1", new ItemStack(ModItems.ALCHEMISTS_TONGS))),
                chapter("arcane_gold", new ItemStack(ModItems.ARCANE_GOLD_INGOT),
                        new CruciblePage("eidolon.codex.page.arcane_gold.0", new ItemStack(ModItems.GOLD_INLAY), new ItemStack(ModItems.ARCANE_GOLD_INGOT)),
                        new CraftingPage("eidolon.codex.page.arcane_gold.1", new ItemStack(ModItems.ARCANE_GOLD_NUGGET), new ItemStack(ModBlocks.ARCANE_GOLD_BLOCK))),
                chapter("reagents", new ItemStack(ModItems.DEATH_ESSENCE),
                        new ListPage("eidolon.codex.page.reagents.0", new ItemStack[] {new ItemStack(ModItems.DEATH_ESSENCE), new ItemStack(ModItems.CRIMSON_ESSENCE), new ItemStack(ModItems.ENDER_CALX)}, "death", "crimson", "ender")),
                chapter("soul_gems", new ItemStack(ModItems.LESSER_SOUL_GEM),
                        new TitlePage("eidolon.codex.page.soul_gems", new ItemStack(ModItems.SOUL_SHARD), new ItemStack(ModItems.LESSER_SOUL_GEM))),
                chapter("shadow_gem", new ItemStack(ModItems.SHADOW_GEM),
                        new CruciblePage("eidolon.codex.page.shadow_gem", new ItemStack(ModItems.SHADOW_GEM))),
                chapter("warped_sprouts", new ItemStack(ModItems.WARPED_SPROUTS),
                        new CruciblePage("eidolon.codex.page.warped_sprouts", new ItemStack(ModItems.FUNGUS_SPROUTS), new ItemStack(ModItems.WARPED_SPROUTS))),
                chapter("basic_alchemy", new ItemStack(Items.GUNPOWDER),
                        new ListPage("eidolon.codex.page.basic_alchemy", new ItemStack[] {new ItemStack(Items.GUNPOWDER), new ItemStack(Items.BLAZE_POWDER), new ItemStack(ModItems.ENDER_CALX)}, "gunpowder", "calx", "essence")),
                chapter("inlays", new ItemStack(ModItems.GOLD_INLAY),
                        new WorktablePage("eidolon.codex.page.inlays", new ItemStack(ModItems.PEWTER_INLAY), new ItemStack(ModItems.GOLD_INLAY))),
                chapter("basic_baubles", new ItemStack(ModItems.BASIC_RING),
                        new CraftingPage("eidolon.codex.page.basic_baubles", new ItemStack(ModItems.BASIC_RING), new ItemStack(ModItems.BASIC_AMULET), new ItemStack(ModItems.BASIC_BELT))),
                chapter("magic_workbench", new ItemStack(ModBlocks.WORKTABLE),
                        new TitlePage("eidolon.codex.page.magic_workbench", new ItemStack(ModBlocks.WORKTABLE))),
                chapter("void_amulet", new ItemStack(ModItems.VOID_AMULET),
                        new WorktablePage("eidolon.codex.page.void_amulet", new ItemStack(ModItems.VOID_AMULET))),
                chapter("warded_mail", new ItemStack(ModItems.WARDED_MAIL),
                        new WorktablePage("eidolon.codex.page.warded_mail", new ItemStack(ModItems.WARDED_MAIL))),
                chapter("soulfire_wand", new ItemStack(ModItems.SOULFIRE_WAND),
                        new WorktablePage("eidolon.codex.page.soulfire_wand", new ItemStack(ModItems.SOULFIRE_WAND))),
                chapter("bonechill_wand", new ItemStack(ModItems.BONECHILL_WAND),
                        new WorktablePage("eidolon.codex.page.bonechill_wand", new ItemStack(ModItems.BONECHILL_WAND))),
                chapter("reaper_scythe", new ItemStack(ModItems.REAPER_SCYTHE),
                        new WorktablePage("eidolon.codex.page.reaper_scythe", new ItemStack(ModItems.REAPER_SCYTHE))),
                chapter("cleaving_axe", new ItemStack(ModItems.CLEAVING_AXE),
                        new WorktablePage("eidolon.codex.page.cleaving_axe", new ItemStack(ModItems.CLEAVING_AXE))),
                chapter("soul_enchanter", new ItemStack(ModBlocks.SOUL_ENCHANTER),
                        new TitlePage("eidolon.codex.page.soul_enchanter", new ItemStack(ModBlocks.SOUL_ENCHANTER))),
                chapter("reversal_pick", new ItemStack(ModItems.REVERSAL_PICK),
                        new WorktablePage("eidolon.codex.page.reversal_pick", new ItemStack(ModItems.REVERSAL_PICK))),
                chapter("warlock_armor", new ItemStack(ModItems.WARLOCK_HAT),
                        new WorktablePage("eidolon.codex.page.warlock_armor", new ItemStack(ModItems.WARLOCK_HAT), new ItemStack(ModItems.WARLOCK_CLOAK), new ItemStack(ModItems.WARLOCK_BOOTS))),
                chapter("gravity_belt", new ItemStack(ModItems.GRAVITY_BELT),
                        new WorktablePage("eidolon.codex.page.gravity_belt", new ItemStack(ModItems.GRAVITY_BELT))),
                chapter("prestigious_palm", new ItemStack(ModItems.PRESTIGIOUS_PALM),
                        new WorktablePage("eidolon.codex.page.prestigious_palm", new ItemStack(ModItems.PRESTIGIOUS_PALM))),
                chapter("mind_shielding_plate", new ItemStack(ModItems.MIND_SHIELDING_PLATE),
                        new WorktablePage("eidolon.codex.page.mind_shielding_plate", new ItemStack(ModItems.MIND_SHIELDING_PLATE))),
                chapter("resolute_belt", new ItemStack(ModItems.RESOLUTE_BELT),
                        new WorktablePage("eidolon.codex.page.resolute_belt", new ItemStack(ModItems.RESOLUTE_BELT))),
                chapter("glass_hand", new ItemStack(ModItems.GLASS_HAND),
                        new WorktablePage("eidolon.codex.page.glass_hand", new ItemStack(ModItems.GLASS_HAND))),
                chapter("athame", new ItemStack(ModItems.ATHAME),
                        new WorktablePage("eidolon.codex.page.athame", new ItemStack(ModItems.ATHAME)))));

        CATEGORIES.add(new Category("theurgy", new ItemStack(ModBlocks.GOBLET), 0x5e5adb,
                chapter("intro_signs", new ItemStack(Items.PAPER),
                        new TitlePage("eidolon.codex.page.intro_signs.0", new ItemStack(ModItems.UNHOLY_SYMBOL)),
                        new TextPage("eidolon.codex.page.intro_signs.1", new ItemStack(ModItems.PARCHMENT))),
                chapter("research", new ItemStack(ModItems.NOTETAKING_TOOLS),
                        new TitlePage("eidolon.codex.page.research.0", new ItemStack(ModItems.NOTETAKING_TOOLS)),
                        new TextPage("eidolon.codex.page.research.1", new ItemStack(ModBlocks.RESEARCH_TABLE))),
                chapter("effigy", new ItemStack(ModBlocks.STRAW_EFFIGY),
                        new CraftingPage("eidolon.codex.page.effigy", new ItemStack(ModBlocks.STRAW_EFFIGY))),
                chapter("altars", new ItemStack(ModBlocks.WOODEN_ALTAR),
                        new TitlePage("eidolon.codex.page.altars.0", new ItemStack(ModBlocks.WOODEN_ALTAR), new ItemStack(ModBlocks.STONE_ALTAR)),
                        new TextPage("eidolon.codex.page.altars.1", new ItemStack(ModItems.OFFERTORY_PLATE))),
                chapter("altar_lights", new ItemStack(ModBlocks.CANDLE),
                        new ListPage("eidolon.codex.page.altar_lights.1", new ItemStack[] {new ItemStack(Blocks.TORCH), new ItemStack(ModBlocks.CANDLE), new ItemStack(ModBlocks.CANDLESTICK)}, "torch", "candle", "candlestick")),
                chapter("altar_skulls", new ItemStack(Items.SKULL),
                        new ListPage("eidolon.codex.page.altar_skulls.1", new ItemStack[] {new ItemStack(Items.SKULL), new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.BONE)}, "skeleton", "zombie", "wither")),
                chapter("altar_herbs", new ItemStack(ModBlocks.AVENNIAN_SPRIG),
                        new ListPage("eidolon.codex.page.altar_herbs.1", new ItemStack[] {new ItemStack(ModBlocks.AVENNIAN_SPRIG), new ItemStack(ModBlocks.MERAMMER_ROOT), new ItemStack(ModBlocks.OANNA_BLOOM)}, "avennian", "merammer", "oanna")),
                chapter("goblet", new ItemStack(ModBlocks.GOBLET),
                        new CraftingPage("eidolon.codex.page.goblet", new ItemStack(ModBlocks.GOBLET))),
                chapter("dark_prayer", new ItemStack(ModItems.SHADOW_GEM),
                        new ChantPage("eidolon.codex.page.dark_prayer.0", new ItemStack(ModItems.UNHOLY_SYMBOL)),
                        new TextPage("eidolon.codex.page.dark_prayer.1")),
                chapter("animal_sacrifice", new ItemStack(Items.PORKCHOP),
                        new ChantPage("eidolon.codex.page.animal_sacrifice", new ItemStack(Items.PORKCHOP), new ItemStack(ModItems.SOUL_SHARD))),
                chapter("dark_touch", new ItemStack(ModItems.UNHOLY_SYMBOL),
                        new ChantPage("eidolon.codex.page.dark_touch.0", new ItemStack(ModItems.UNHOLY_SYMBOL)),
                        new TextPage("eidolon.codex.page.dark_touch.1")),
                chapter("stone_altar", new ItemStack(ModBlocks.STONE_ALTAR),
                        new WorktablePage("eidolon.codex.page.stone_altar", new ItemStack(ModBlocks.STONE_ALTAR))),
                chapter("unholy_effigy", new ItemStack(ModBlocks.UNHOLY_EFFIGY),
                        new WorktablePage("eidolon.codex.page.unholy_effigy", new ItemStack(ModBlocks.UNHOLY_EFFIGY))),
                chapter("villager_sacrifice", new ItemStack(Items.IRON_SWORD),
                        new ChantPage("eidolon.codex.page.villager_sacrifice", new ItemStack(Items.EMERALD), new ItemStack(ModItems.SOUL_SHARD)))));

        CATEGORIES.add(new Category("signs", new ItemStack(ModItems.UNHOLY_SYMBOL), 0xa34acf,
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
