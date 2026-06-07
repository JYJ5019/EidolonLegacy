package elucent.eidolon.gui;

import elucent.eidolon.Reference;
import elucent.eidolon.codex.Category;
import elucent.eidolon.codex.Chapter;
import elucent.eidolon.codex.CodexChapters;
import elucent.eidolon.codex.Page;
import elucent.eidolon.compat.jei.SoulShardHarvestRecipe;
import elucent.eidolon.item.AthameItem;
import elucent.eidolon.network.AttemptCastPacket;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModItems;
import elucent.eidolon.registries.ModSounds;
import elucent.eidolon.recipes.CrucibleRecipe;
import elucent.eidolon.recipes.CrucibleRecipes;
import elucent.eidolon.recipes.IncubatorRecipe;
import elucent.eidolon.recipes.IncubatorRecipes;
import elucent.eidolon.recipes.MachineInfoRecipe;
import elucent.eidolon.recipes.WorktableRecipe;
import elucent.eidolon.recipes.WorktableRecipes;
import elucent.eidolon.research.Research;
import elucent.eidolon.research.Researches;
import elucent.eidolon.spell.AltarEntries;
import elucent.eidolon.spell.AltarEntry;
import elucent.eidolon.spell.AltarRitual;
import elucent.eidolon.spell.AltarRituals;
import elucent.eidolon.spell.Rune;
import elucent.eidolon.spell.Runes;
import elucent.eidolon.spell.Sign;
import elucent.eidolon.spell.SignSequence;
import elucent.eidolon.spell.Signs;
import elucent.eidolon.spell.Spell;
import elucent.eidolon.spell.Spells;
import elucent.eidolon.spell.StaticSpell;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CodexGui extends GuiScreen {
    private static final int VIEW_INDEX = 0;
    private static final int VIEW_RESEARCH = 1;
    private static final int VIEW_WORKTABLE_RECIPES = 2;
    private static final int VIEW_CRUCIBLE_RECIPES = 3;
    private static final int VIEW_ALTAR_RITUALS = 4;
    private static final int VIEW_ALTAR_OFFERINGS = 5;
    private static final int VIEW_ATHAME_HARVEST = 6;
    private static final int VIEW_SOUL_SHARD_HARVEST = 7;
    private static final int VIEW_MACHINE_INFO = 8;
    private static final int VIEW_INCUBATOR_RECIPES = 9;
    private static final int VIEW_SIGNS = 10;
    private static final int VIEW_RUNES = 11;
    private static final int VIEW_CHANTS = 12;
    private static final int VIEW_GUIDE = 13;
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/codex_bg.png");
    private static final ResourceLocation INDEX_PAGE =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/codex_index_page.png");
    private static final ResourceLocation BLANK_PAGE =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/codex_blank_page.png");
    private static final ResourceLocation WORKTABLE_PAGE =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/codex_worktable_page.png");
    private static final ResourceLocation CRUCIBLE_PAGE =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/codex_crucible_page.png");
    private static final ResourceLocation RITUAL_PAGE =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/codex_ritual_page.png");
    private static final int STATE_KNOWN = 0;
    private static final int STATE_AVAILABLE = 1;
    private static final int STATE_LOCKED = 2;
    private static final int WIDTH = 312;
    private static final int HEIGHT = 208;
    private static final int LEFT_PAGE_X = 14;
    private static final int RIGHT_PAGE_X = 170;
    private static final int PAGE_Y = 24;
    private static final int PAGE_WIDTH = 128;
    private static final int PAGE_HEIGHT = 160;
    private static final int LIST_START_Y = 68;
    private static final int LIST_LINE_HEIGHT = 20;
    private static final int LIST_WIDTH = 122;
    private static final int PAGE_ARROW_Y = 169;
    private static final int LEFT_ARROW_X = 10;
    private static final int RIGHT_ARROW_X = 270;
    private static final int LEFT_PAGE_RIGHT_ARROW_X = 114;
    private static final int PAGE_ARROW_WIDTH = 32;
    private static final int PAGE_ARROW_HEIGHT = 18;
    private static final int DETAIL_PAGE_TEXT = 0;
    private static final int DETAIL_PAGE_INFO = 1;
    private static final int DETAIL_PAGE_COUNT = 2;
    private static final int DETAIL_ARROW_Y = 170;
    private static final int DETAIL_LEFT_ARROW_X = 42;
    private static final int DETAIL_RIGHT_ARROW_X = 78;
    private static final int DETAIL_ARROW_SIZE = 12;
    private static final int CRUCIBLE_STEP_ROW_HEIGHT = 20;
    private static final int CRUCIBLE_VISIBLE_STEPS = 5;
    private static final int CRUCIBLE_STEP_START_Y = 30;
    private static final int CRUCIBLE_FLUID_Y = 6;
    private static final int CRUCIBLE_RESULT_Y = 128;
    private static final int CRUCIBLE_SCROLL_X = 96;
    private static final int CRUCIBLE_SCROLL_UP_Y = 135;
    private static final int CRUCIBLE_SCROLL_DOWN_Y = 149;
    private static final int CRUCIBLE_SCROLL_BUTTON_SIZE = 12;
    private static final int CRUCIBLE_COMPACT_ITEM_SIZE = 12;
    private static final int CRUCIBLE_INPUT_START_X = 22;
    private static final int CRUCIBLE_INPUT_SPACING = 14;
    private static final int CRUCIBLE_MAX_VISIBLE_INPUTS = 6;
    private static final int CRUCIBLE_STIRRER_X = 106;
    private static final int ALTAR_RITUAL_RESULT_X = 56;
    private static final int ALTAR_RITUAL_RESULT_Y = 39;
    private static final int ALTAR_RITUAL_SACRIFICE_X = 56;
    private static final int ALTAR_RITUAL_SACRIFICE_Y = 80;
    private static final int ALTAR_RITUAL_FOCUS_X = 56;
    private static final int ALTAR_RITUAL_FOCUS_Y = 141;
    private static final int CATEGORY_TAB_X = 8;
    private static final int CATEGORY_TAB_Y = 28;
    private static final int CATEGORY_TAB_WIDTH = 48;
    private static final int CATEGORY_TAB_HEIGHT = 19;
    private static final int CATEGORY_TAB_SPACING = 20;
    private static final int CATEGORY_TABS_PER_SIDE = 8;
    private static final int INDEX_VISIBLE_ENTRIES = 6;
    private static final int INDEX_ENTRIES_PER_SPREAD = INDEX_VISIBLE_ENTRIES * 2;
    private static final int INDEX_LEFT_START_Y = 54;
    private static final int INDEX_RIGHT_START_Y = 38;
    private static final int GUIDE_CATEGORY_Y = 58;
    private static final int GUIDE_CATEGORY_SIZE = 16;
    private static final int GUIDE_CATEGORY_SPACING = 18;
    private static final int GUIDE_CHAPTER_START_Y = 92;
    private static final int GUIDE_VISIBLE_CHAPTERS = 4;
    private static final int GUIDE_DETAIL_ARROW_Y = 134;
    private static final int GUIDE_PAGE_COUNT_Y = 144;
    private static final int CHANT_MAX_RUNES = 18;
    private static final int CHANT_BASE_Y = 180;
    private static final int CHANT_CAP_WIDTH = 16;
    private static final int CHANT_SLOT_WIDTH = 12;
    private static final int CHANT_BUTTON_SIZE = 32;
    private static final int CHANT_BUTTON_SPACING = 36;
    private IndexEntry hoveredCategoryTab;

    private final EntityPlayer player;
    private final List<Rune> chant = new ArrayList<>();
    private Research selectedResearch;
    private int researchListPage;
    private int researchDetailPage;
    private int recipeListPage;
    private int selectedRecipeIndex;
    private int loreListPage;
    private int selectedLoreIndex;
    private int guideCategoryIndex;
    private int guideChapterListPage;
    private int selectedGuideChapterIndex;
    private int guidePageIndex;
    private int crucibleStepScroll;
    private int indexPage;
    private int view = VIEW_INDEX;

    public CodexGui(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int left = (width - WIDTH) / 2;
        int top = (height - HEIGHT) / 2;
        hoveredCategoryTab = null;
        drawCategoryTabs(left, top, mouseX, mouseY);
        drawCodexBackground(left, top);

        String title = I18n.format("gui.eidolon.codex.title");
        int titleX = left + 86 - fontRenderer.getStringWidth(title) / 2;
        fontRenderer.drawString(title, titleX, top + 28, 0x3a2418);

        List<Research> researches = getVisibleResearches();
        if (selectedResearch == null && !researches.isEmpty()) {
            selectedResearch = researches.get(0);
        }

        if (view == VIEW_INDEX) {
            drawIndexOverview(left, top, mouseX, mouseY);
            finishDraw(left, top, mouseX, mouseY, partialTicks);
            return;
        }

        drawResearchPageBackgrounds(left, top);
        fontRenderer.drawString("< " + I18n.format("gui.eidolon.codex.back"), left + 26, top + 34, 0x5b4732);

        if (isRecipeView()) {
            drawRecipeChapter(left, top, mouseX, mouseY);
            finishDraw(left, top, mouseX, mouseY, partialTicks);
            return;
        }

        if (view == VIEW_GUIDE) {
            drawGuideChapter(left, top, mouseX, mouseY);
            finishDraw(left, top, mouseX, mouseY, partialTicks);
            return;
        }

        if (isLoreView()) {
            drawLoreChapter(left, top, mouseX, mouseY);
            finishDraw(left, top, mouseX, mouseY, partialTicks);
            return;
        }

        String count = I18n.format("gui.eidolon.codex.research_count", getKnownCount(researches));
        fontRenderer.drawString(count, left + 26, top + 50, 0x5b4732);
        researchListPage = clampResearchListPage(researchListPage, researches.size());

        if (researches.isEmpty()) {
            fontRenderer.drawSplitString(I18n.format("gui.eidolon.codex.no_research"), left + 26, top + LIST_START_Y, 118, 0x5b4732);
        } else {
            int y = top + LIST_START_Y;
            int maxVisible = getMaxVisibleEntries();
            int start = researchListPage * maxVisible;
            for (int i = start; i < researches.size() && i < start + maxVisible; i++) {
                Research research = researches.get(i);
                drawResearchLine(research, left + 26, y, research == selectedResearch, getResearchState(research));
                y += LIST_LINE_HEIGHT;
            }
            drawResearchListPageArrows(left, top, mouseX, mouseY, researches.size());
            drawResearchDetails(selectedResearch, left + 174, top, mouseX, mouseY);
        }

        finishDraw(left, top, mouseX, mouseY, partialTicks);
    }

    private void finishDraw(int left, int top, int mouseX, int mouseY, float partialTicks) {
        if (!chant.isEmpty()) {
            drawChantBar(left, top, mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCategoryTabTooltip(mouseX, mouseY);
    }

    private void drawCategoryTabs(int left, int top, int mouseX, int mouseY) {
        List<IndexEntry> entries = getIndexEntries();
        for (int i = 0; i < entries.size(); i++) {
            IndexEntry entry = entries.get(i);
            int tabX = getCategoryTabX(left, i);
            int tabY = getCategoryTabY(top, i);
            boolean right = isRightCategoryTab(i);
            boolean active = view == entry.view || (view == VIEW_INDEX && i == 0);
            boolean hovered = isIn(mouseX, mouseY, tabX, tabY, tabX + CATEGORY_TAB_WIDTH, tabY + CATEGORY_TAB_HEIGHT);
            if (hovered) {
                hoveredCategoryTab = entry;
            }
            drawCategoryTab(entry, tabX, tabY, active, hovered, right);
        }
    }

    private void drawCategoryTabTooltip(int mouseX, int mouseY) {
        if (hoveredCategoryTab != null) {
            drawHoveringText(Collections.singletonList(I18n.format(hoveredCategoryTab.titleKey)), mouseX, mouseY);
        }
    }

    private boolean isRightCategoryTab(int index) {
        return index >= CATEGORY_TABS_PER_SIDE;
    }

    private int getCategoryTabX(int left, int index) {
        return isRightCategoryTab(index) ? left + WIDTH - 20 : left + CATEGORY_TAB_X - 36;
    }

    private int getCategoryTabY(int top, int index) {
        return top + CATEGORY_TAB_Y + index % CATEGORY_TABS_PER_SIDE * CATEGORY_TAB_SPACING;
    }

    private void drawCategoryTab(IndexEntry entry, int x, int y, boolean active, boolean hovered, boolean right) {
        int color = active ? brighten(entry.color) : hovered ? soften(entry.color) : entry.color;
        GlStateManager.color(((color >> 16) & 255) / 255.0F,
                ((color >> 8) & 255) / 255.0F,
                (color & 255) / 255.0F,
                1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawModalRectWithCustomSizedTexture(x, y, 208, right ? 208 : 227,
                CATEGORY_TAB_WIDTH, CATEGORY_TAB_HEIGHT, 512, 512);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawCategoryIcon(entry.icon, x + (right ? 23 : 9), y + 1);
    }

    private void drawCategoryIcon(ItemStack icon, int x, int y) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 8.0F, y + 8.0F, 0.0F);
        GlStateManager.scale(0.78F, 0.78F, 0.78F);
        itemRender.renderItemAndEffectIntoGUI(icon, -8, -8);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private int brighten(int color) {
        int r = Math.min(255, ((color >> 16) & 255) + 36);
        int g = Math.min(255, ((color >> 8) & 255) + 36);
        int b = Math.min(255, (color & 255) + 36);
        return (r << 16) | (g << 8) | b;
    }

    private int soften(int color) {
        int r = Math.min(255, ((color >> 16) & 255) + 18);
        int g = Math.min(255, ((color >> 8) & 255) + 18);
        int b = Math.min(255, (color & 255) + 18);
        return (r << 16) | (g << 8) | b;
    }

    private void drawIndexOverview(int left, int top, int mouseX, int mouseY) {
        drawIndexPageBackgrounds(left, top);
        drawCenteredPageTitle(I18n.format("gui.eidolon.codex.title"), left + LEFT_PAGE_X, top + 38, 0x2f2118);
        drawIndex(left, top, mouseX, mouseY);
    }

    private void drawIndexPageBackgrounds(int left, int top) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(INDEX_PAGE);
        drawModalRectWithCustomSizedTexture(left + LEFT_PAGE_X, top + PAGE_Y, 0, 0,
                PAGE_WIDTH, PAGE_HEIGHT, 256, 256);
        drawModalRectWithCustomSizedTexture(left + RIGHT_PAGE_X, top + PAGE_Y, 0, 0,
                PAGE_WIDTH, PAGE_HEIGHT, 256, 256);
    }

    private void drawIndex(int left, int top, int mouseX, int mouseY) {
        List<IndexEntry> entries = getIndexEntries();
        indexPage = Math.max(0, Math.min(indexPage, getMaxIndexPage(entries.size())));
        int start = indexPage * INDEX_ENTRIES_PER_SPREAD;
        int leftEnd = Math.min(entries.size(), start + INDEX_VISIBLE_ENTRIES);
        for (int i = start; i < leftEnd; i++) {
            IndexEntry entry = entries.get(i);
            drawIndexEntry(left + LEFT_PAGE_X, top + INDEX_LEFT_START_Y, i - start, I18n.format(entry.titleKey), entry.icon);
        }
        int rightStart = start + INDEX_VISIBLE_ENTRIES;
        int rightEnd = Math.min(entries.size(), rightStart + INDEX_VISIBLE_ENTRIES);
        for (int i = rightStart; i < rightEnd; i++) {
            IndexEntry entry = entries.get(i);
            drawIndexEntry(left + RIGHT_PAGE_X, top + INDEX_RIGHT_START_Y, i - rightStart, I18n.format(entry.titleKey), entry.icon);
        }
        drawIndexPageArrows(left, top, mouseX, mouseY);
    }

    private List<IndexEntry> getIndexEntries() {
        List<IndexEntry> entries = new ArrayList<>();
        entries.add(new IndexEntry(VIEW_RESEARCH, "gui.eidolon.codex.chapter_research", "gui.eidolon.codex.chapter_research_desc", new ItemStack(ModItems.RESEARCH_NOTES), 0x4f8f3a));
        entries.add(new IndexEntry(VIEW_GUIDE, "gui.eidolon.codex.chapter_guide", "gui.eidolon.codex.chapter_guide_desc", new ItemStack(ModItems.CODEX), 0x8f6d3a));
        entries.add(new IndexEntry(VIEW_WORKTABLE_RECIPES, "gui.eidolon.codex.chapter_worktable_recipes", "gui.eidolon.codex.chapter_worktable_recipes_desc", new ItemStack(ModBlocks.WORKTABLE), 0xcaa23d));
        entries.add(new IndexEntry(VIEW_CRUCIBLE_RECIPES, "gui.eidolon.codex.chapter_crucible_recipes", "gui.eidolon.codex.chapter_crucible_recipes_desc", new ItemStack(ModBlocks.CRUCIBLE), 0xa94747));
        entries.add(new IndexEntry(VIEW_ALTAR_RITUALS, "gui.eidolon.codex.chapter_altar_rituals", "gui.eidolon.codex.chapter_altar_rituals_desc", new ItemStack(ModBlocks.STONE_ALTAR), 0x5c5bb5));
        entries.add(new IndexEntry(VIEW_ALTAR_OFFERINGS, "gui.eidolon.codex.chapter_altar_offerings", "gui.eidolon.codex.chapter_altar_offerings_desc", new ItemStack(ModItems.OFFERTORY_PLATE), 0x8b52b5));
        entries.add(new IndexEntry(VIEW_ATHAME_HARVEST, "gui.eidolon.codex.chapter_athame_harvest", "gui.eidolon.codex.chapter_athame_harvest_desc", new ItemStack(ModItems.ATHAME), 0x6b58bd));
        entries.add(new IndexEntry(VIEW_SOUL_SHARD_HARVEST, "gui.eidolon.codex.chapter_soul_shard_harvest", "gui.eidolon.codex.chapter_soul_shard_harvest_desc", new ItemStack(ModItems.SOUL_SHARD), 0x4057a5));
        entries.add(new IndexEntry(VIEW_MACHINE_INFO, "gui.eidolon.codex.chapter_machine_info", "gui.eidolon.codex.chapter_machine_info_desc", new ItemStack(ModBlocks.SOUL_ENCHANTER), 0x4d7b8f));
        entries.add(new IndexEntry(VIEW_INCUBATOR_RECIPES, "gui.eidolon.codex.chapter_incubator_recipes", "gui.eidolon.codex.chapter_incubator_recipes_desc", new ItemStack(ModBlocks.INCUBATOR), 0x4f8f6a));
        entries.add(new IndexEntry(VIEW_SIGNS, "gui.eidolon.codex.chapter_signs", "gui.eidolon.codex.chapter_signs_desc", new ItemStack(ModItems.UNHOLY_SYMBOL), 0xa34acf));
        entries.add(new IndexEntry(VIEW_RUNES, "gui.eidolon.codex.chapter_runes", "gui.eidolon.codex.chapter_runes_desc", new ItemStack(ModItems.PARCHMENT), 0x4646c2));
        entries.add(new IndexEntry(VIEW_CHANTS, "gui.eidolon.codex.chapter_chants", "gui.eidolon.codex.chapter_chants_desc", new ItemStack(ModBlocks.STRAW_EFFIGY), 0x6e58c9));
        return entries;
    }

    private void drawIndexPageArrows(int left, int top, int mouseX, int mouseY) {
        List<IndexEntry> entries = getIndexEntries();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        if (indexPage > 0) {
            drawPageArrow(left + LEFT_ARROW_X, top + PAGE_ARROW_Y, 128,
                    isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                            left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT));
        }
        if (indexPage < getMaxIndexPage(entries.size())) {
            drawPageArrow(left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y, 160,
                    isIn(mouseX, mouseY, left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                            left + LEFT_PAGE_RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT));
        }
    }

    private void drawIndexEntry(int pageX, int startY, int index, String label, ItemStack icon) {
        int chapterY = startY + index * LIST_LINE_HEIGHT;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(INDEX_PAGE);
        drawModalRectWithCustomSizedTexture(pageX + 1, chapterY - 5, 128, 0, 122, 18, 256, 256);
        itemRender.renderItemAndEffectIntoGUI(icon, pageX + 2, chapterY - 4);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        fontRenderer.drawString(label, pageX + 24, chapterY, 0x2f2118);
    }

    private void drawResearchPageBackgrounds(int left, int top) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(INDEX_PAGE);
        drawModalRectWithCustomSizedTexture(left + LEFT_PAGE_X, top + PAGE_Y, 0, 0,
                PAGE_WIDTH, PAGE_HEIGHT, 256, 256);
        mc.getTextureManager().bindTexture(BLANK_PAGE);
        drawModalRectWithCustomSizedTexture(left + RIGHT_PAGE_X, top + PAGE_Y, 0, 0,
                PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_HEIGHT);
    }

    private void drawCodexBackground(int left, int top) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawModalRectWithCustomSizedTexture(left, top, 0, 256, WIDTH, HEIGHT, 512, 512);
        drawModalRectWithCustomSizedTexture(left, top, 0, 0, WIDTH, HEIGHT, 512, 512);
    }

    private void drawResearchLine(Research research, int x, int y, boolean selected, int state) {
        boolean known = state == STATE_KNOWN;
        String name = known ? getResearchName(research) : I18n.format("gui.eidolon.codex.unknown");
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(INDEX_PAGE);
        drawModalRectWithCustomSizedTexture(x + 1, y - 5, 128, 0, 122, 18, 256, 256);
        itemRender.renderItemAndEffectIntoGUI(new ItemStack(ModItems.RESEARCH_NOTES), x + 2, y - 4);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (selected) {
            drawRect(x + 23, y - 2, x + 120, y + 11, 0x227d5a2f);
        }
        int color = getListColor(state, selected);
        fontRenderer.drawString(name, x + 24, y, color);
    }

    private void drawResearchDetails(Research research, int left, int top, int mouseX, int mouseY) {
        int state = getResearchState(research);
        if (state != STATE_KNOWN) {
            researchDetailPage = DETAIL_PAGE_TEXT;
            String unknown = I18n.format("gui.eidolon.codex.unknown");
            String detail = state == STATE_AVAILABLE
                    ? I18n.format("gui.eidolon.codex.available_detail")
                    : I18n.format("gui.eidolon.codex.locked_detail");
            drawCenteredPageTitle(unknown, left, top + 38, getListColor(state, true));
            fontRenderer.drawSplitString(detail, left + 4, top + 62, 120, 0x5b4732);
            return;
        }
        String name = getResearchName(research);
        drawCenteredPageTitle(name, left, top + 36, 0x2f2118);
        int y = top + 58;
        researchDetailPage = clampResearchDetailPage(researchDetailPage);
        if (researchDetailPage == DETAIL_PAGE_TEXT) {
            fontRenderer.drawSplitString(getResearchDescription(research), left + 4, y, 120, 0x5b4732);
            drawResearchDetailPageArrows(left, top, mouseX, mouseY);
            return;
        }
        fontRenderer.drawString(I18n.format("gui.eidolon.codex.research_stars", research.getStars()), left + 4, y, 0x5b4732);

        y += 18;
        fontRenderer.drawString(TextFormatting.UNDERLINE + I18n.format("gui.eidolon.codex.source"), left + 4, y, 0x3a2418);
        y += 14;
        String source = getResearchSource(research);
        y += drawSplitStringWithHeight(source, left + 10, y, 112, 0x5b4732) + 12;

        fontRenderer.drawString(TextFormatting.UNDERLINE + I18n.format("gui.eidolon.codex.prerequisites"), left + 4, y, 0x3a2418);
        y += 14;
        List<ResourceLocation> prerequisites = research.getPrerequisites();
        if (prerequisites.isEmpty()) {
            fontRenderer.drawString(I18n.format("gui.eidolon.codex.none"), left + 10, y, 0x5b4732);
        } else {
            for (ResourceLocation prerequisite : prerequisites) {
                Research prerequisiteResearch = Researches.find(prerequisite);
                String prerequisiteName = prerequisiteResearch == null
                        ? prerequisite.toString()
                        : getResearchName(prerequisiteResearch);
                int color = KnowledgeUtil.knowsResearch(player, prerequisite) ? 0x5b4732 : 0x8f3f32;
                fontRenderer.drawString("- " + prerequisiteName, left + 8, y, color);
                y += 12;
            }
        }
        drawResearchDetailPageArrows(left, top, mouseX, mouseY);
    }

    private String getResearchName(Research research) {
        return research.hasDisplayNameOverride()
                ? research.getDisplayNameOverride()
                : I18n.format(research.getTranslationKey());
    }

    private String getResearchDescription(Research research) {
        return research.hasDescriptionOverride()
                ? research.getDescriptionOverride()
                : I18n.format(research.getDescriptionKey());
    }

    private String getResearchSource(Research research) {
        if (research.hasSourceText()) {
            return research.getSourceText();
        }
        return research.getSourceKey().isEmpty()
                ? I18n.format("gui.eidolon.codex.none")
                : I18n.format(research.getSourceKey());
    }

    private int drawSplitStringWithHeight(String text, int x, int y, int width, int color) {
        List<String> lines = fontRenderer.listFormattedStringToWidth(text, width);
        fontRenderer.drawSplitString(text, x, y, width, color);
        return Math.max(fontRenderer.FONT_HEIGHT, lines.size() * fontRenderer.FONT_HEIGHT);
    }

    private int drawSplitStringLimited(String text, int x, int y, int width, int color, int maxLines) {
        if (text == null || text.isEmpty() || maxLines <= 0) {
            return 0;
        }
        List<String> lines = fontRenderer.listFormattedStringToWidth(text, width);
        int lineCount = Math.min(maxLines, lines.size());
        for (int i = 0; i < lineCount; i++) {
            String line = lines.get(i);
            if (i == lineCount - 1 && lineCount < lines.size()) {
                line = ellipsizeLine(line, width);
            }
            fontRenderer.drawString(line, x, y + i * fontRenderer.FONT_HEIGHT, color);
        }
        return lineCount * fontRenderer.FONT_HEIGHT;
    }

    private String ellipsizeLine(String line, int width) {
        String suffix = "...";
        int availableWidth = width - fontRenderer.getStringWidth(suffix);
        if (availableWidth <= 0) {
            return suffix;
        }
        return fontRenderer.trimStringToWidth(line, availableWidth) + suffix;
    }

    private int getLineCapacity(int y, int bottomY) {
        return Math.max(0, (bottomY - y) / fontRenderer.FONT_HEIGHT);
    }

    private void drawResearchDetailPageArrows(int left, int top, int mouseX, int mouseY) {
        if (researchDetailPage > 0) {
            int x = left + DETAIL_LEFT_ARROW_X;
            int y = top + DETAIL_ARROW_Y;
            fontRenderer.drawString("<", x + 3, y + 2, getDetailArrowColor(mouseX, mouseY, x, y));
        }
        if (researchDetailPage < getResearchDetailPageCount(selectedResearch) - 1) {
            int x = left + DETAIL_RIGHT_ARROW_X;
            int y = top + DETAIL_ARROW_Y;
            fontRenderer.drawString(">", x + 3, y + 2, getDetailArrowColor(mouseX, mouseY, x, y));
        }
    }

    private int getDetailArrowColor(int mouseX, int mouseY, int x, int y) {
        return isIn(mouseX, mouseY, x, y, x + DETAIL_ARROW_SIZE, y + DETAIL_ARROW_SIZE) ? 0x2f2118 : 0x5b4732;
    }

    private void drawRecipeChapter(int left, int top, int mouseX, int mouseY) {
        List<ItemStack> outputs = getRecipeOutputs();
        recipeListPage = clampRecipeListPage(recipeListPage, outputs.size());
        selectedRecipeIndex = clampSelectedRecipeIndex(selectedRecipeIndex, outputs.size());

        String title = getRecipeChapterTitle();
        fontRenderer.drawString(title, left + 26, top + 50, 0x5b4732);

        int y = top + LIST_START_Y;
        int maxVisible = getMaxVisibleEntries();
        int start = recipeListPage * maxVisible;
        for (int i = start; i < outputs.size() && i < start + maxVisible; i++) {
            drawRecipeLine(outputs.get(i), left + 26, y, i == selectedRecipeIndex);
            y += LIST_LINE_HEIGHT;
        }
        drawRecipeListPageArrows(left, top, mouseX, mouseY, outputs.size());
        drawSelectedRecipePage(left + RIGHT_PAGE_X, top + PAGE_Y, mouseX, mouseY);
    }

    private void drawRecipeLine(ItemStack output, int x, int y, boolean selected) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(INDEX_PAGE);
        drawModalRectWithCustomSizedTexture(x + 1, y - 5, 128, 0, 122, 18, 256, 256);
        if (selected) {
            drawRect(x + 23, y - 2, x + 120, y + 11, 0x11A88A55);
        }
        drawStack(output, x + 2, y - 4);
        fontRenderer.drawString(output.getDisplayName(), x + 24, y, selected ? 0x1f140f : 0x2f2118);
    }

    private void drawLoreChapter(int left, int top, int mouseX, int mouseY) {
        List<LoreEntry> entries = getLoreEntries();
        loreListPage = clampLoreListPage(loreListPage, entries.size());
        selectedLoreIndex = clampSelectedLoreIndex(selectedLoreIndex, entries.size());

        fontRenderer.drawString(getLoreChapterTitle(), left + 26, top + 50, 0x5b4732);
        int y = top + LIST_START_Y;
        int maxVisible = getMaxVisibleEntries();
        int start = loreListPage * maxVisible;
        for (int i = start; i < entries.size() && i < start + maxVisible; i++) {
            drawLoreLine(entries.get(i), left + 26, y, i == selectedLoreIndex);
            y += LIST_LINE_HEIGHT;
        }
        drawLoreListPageArrows(left, top, mouseX, mouseY, entries.size());
        drawSelectedLorePage(left + RIGHT_PAGE_X, top + PAGE_Y, entries);
    }

    private void drawLoreLine(LoreEntry entry, int x, int y, boolean selected) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(INDEX_PAGE);
        drawModalRectWithCustomSizedTexture(x + 1, y - 5, 128, 0, 122, 18, 256, 256);
        if (selected) {
            drawRect(x + 23, y - 2, x + 120, y + 11, 0x11A88A55);
        }
        drawLoreIcon(entry, x + 4, y - 2);
        fontRenderer.drawString(entry.title, x + 24, y, selected ? 0x1f140f : 0x2f2118);
    }

    private void drawGuideChapter(int left, int top, int mouseX, int mouseY) {
        List<Category> categories = CodexChapters.getCategories();
        if (categories.isEmpty()) {
            fontRenderer.drawSplitString(I18n.format("gui.eidolon.codex.guide_empty"), left + 26, top + LIST_START_Y, 118, 0x5b4732);
            return;
        }
        guideCategoryIndex = clampGuideCategoryIndex(guideCategoryIndex, categories.size());
        Category category = categories.get(guideCategoryIndex);
        List<Chapter> chapters = category.getChapters();
        guideChapterListPage = clampGuideChapterListPage(guideChapterListPage, chapters.size());
        selectedGuideChapterIndex = clampSelectedGuideChapterIndex(selectedGuideChapterIndex, chapters.size());

        fontRenderer.drawString(I18n.format("gui.eidolon.codex.chapter_guide"), left + 26, top + 48, 0x5b4732);
        drawGuideCategoryIcons(categories, left, top, mouseX, mouseY);
        drawGuideChapterList(category, chapters, left, top);
        drawGuideListPageArrows(left, top, mouseX, mouseY, chapters.size());
        drawSelectedGuidePage(left + RIGHT_PAGE_X, top + PAGE_Y, category, chapters, mouseX, mouseY);
    }

    private void drawGuideCategoryIcons(List<Category> categories, int left, int top, int mouseX, int mouseY) {
        int x = left + 26;
        int y = top + GUIDE_CATEGORY_Y;
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            int iconX = x + i * GUIDE_CATEGORY_SPACING;
            boolean selected = i == guideCategoryIndex;
            boolean hovered = isIn(mouseX, mouseY, iconX, y, iconX + GUIDE_CATEGORY_SIZE, y + GUIDE_CATEGORY_SIZE);
            if (selected || hovered) {
                drawRect(iconX - 1, y - 1, iconX + GUIDE_CATEGORY_SIZE + 1, y + GUIDE_CATEGORY_SIZE + 1,
                        selected ? 0x337d5a2f : 0x22a88a55);
            }
            drawStack(category.getIcon(), iconX, y);
            if (hovered) {
                drawHoveringText(Collections.singletonList(I18n.format(category.getTitleKey())), mouseX, mouseY);
            }
        }
    }

    private void drawGuideChapterList(Category category, List<Chapter> chapters, int left, int top) {
        String categoryName = I18n.format(category.getTitleKey());
        fontRenderer.drawString(categoryName, left + 26, top + GUIDE_CHAPTER_START_Y - 13, category.getColor());
        int start = guideChapterListPage * GUIDE_VISIBLE_CHAPTERS;
        int y = top + GUIDE_CHAPTER_START_Y;
        for (int i = start; i < chapters.size() && i < start + GUIDE_VISIBLE_CHAPTERS; i++) {
            Chapter chapter = chapters.get(i);
            drawGuideChapterLine(chapter, left + 26, y, i == selectedGuideChapterIndex);
            y += LIST_LINE_HEIGHT;
        }
    }

    private void drawGuideChapterLine(Chapter chapter, int x, int y, boolean selected) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(INDEX_PAGE);
        drawModalRectWithCustomSizedTexture(x + 1, y - 5, 128, 0, 122, 18, 256, 256);
        if (selected) {
            drawRect(x + 23, y - 2, x + 120, y + 11, 0x11A88A55);
        }
        drawStack(chapter.getIcon(), x + 2, y - 4);
        String label = fontRenderer.trimStringToWidth(I18n.format(chapter.getTitleKey()), 94);
        fontRenderer.drawString(label, x + 24, y, selected ? 0x1f140f : 0x2f2118);
    }

    private void drawSelectedGuidePage(int x, int y, Category category, List<Chapter> chapters, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BLANK_PAGE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_HEIGHT);
        if (chapters.isEmpty()) {
            drawCenteredPageTitle(I18n.format(category.getTitleKey()), x, y + 28, 0x2f2118);
            fontRenderer.drawSplitString(I18n.format("gui.eidolon.codex.guide_empty"), x + 12, y + 58, 104, 0x5b4732);
            return;
        }
        Chapter chapter = chapters.get(selectedGuideChapterIndex);
        List<Page> pages = chapter.getPages();
        guidePageIndex = clampGuidePageIndex(guidePageIndex, pages.size());
        Page page = pages.isEmpty() ? null : pages.get(guidePageIndex);
        drawCenteredPageTitle(I18n.format(chapter.getTitleKey()), x, y + 22, 0x2f2118);
        if (page == null) {
            fontRenderer.drawSplitString(I18n.format("gui.eidolon.codex.guide_no_pages"), x + 12, y + 58, 104, 0x5b4732);
            return;
        }
        drawGuidePageContent(page, x, y, mouseX, mouseY);
        drawGuideDetailPageArrows(x, y, mouseX, mouseY, pages.size());
    }

    private void drawGuidePageContent(Page page, int x, int y, int mouseX, int mouseY) {
        List<ItemStack> stacks = page.getDisplayStacks();
        int iconY = y + 44;
        drawGuideStacks(stacks, x, iconY, mouseX, mouseY);
        String kind = I18n.format("gui.eidolon.codex.page_kind." + page.getKind().name().toLowerCase());
        fontRenderer.drawString(kind, x + 12, y + 67, 0x7d6b55);
        int textY = y + 81;
        String text = fallback(page.getTextKey(), I18n.format("gui.eidolon.codex.guide_fallback", titleCase(page.getTextKey())));
        int height = drawSplitStringLimited(text, x + 12, textY, 104, 0x5b4732, 5);
        textY += height + 4;
        for (String entry : page.getEntries()) {
            if (textY > y + 142) {
                break;
            }
            fontRenderer.drawString("- " + I18n.format("gui.eidolon.codex.list_entry." + entry), x + 16, textY, 0x7d6b55);
            textY += 10;
        }
        if (stacks.size() > 4) {
            fontRenderer.drawString(I18n.format("gui.eidolon.codex.guide_more_items", stacks.size()), x + 12, y + 145, 0x7d6b55);
        }
    }

    private void drawGuideStacks(List<ItemStack> stacks, int x, int y, int mouseX, int mouseY) {
        int shown = Math.min(4, stacks.size());
        int totalWidth = shown * 18 - 2;
        int startX = x + 64 - totalWidth / 2;
        for (int i = 0; i < shown; i++) {
            int stackX = startX + i * 18;
            drawStack(stacks.get(i), stackX, y);
            if (isIn(mouseX, mouseY, stackX, y, stackX + 16, y + 16)) {
                renderToolTip(stacks.get(i), mouseX, mouseY);
            }
        }
    }

    private void drawGuideDetailPageArrows(int x, int y, int mouseX, int mouseY, int pageCount) {
        if (guidePageIndex > 0) {
            int arrowX = x + DETAIL_LEFT_ARROW_X;
            int arrowY = y + GUIDE_DETAIL_ARROW_Y;
            fontRenderer.drawString("<", arrowX + 3, arrowY + 2, getDetailArrowColor(mouseX, mouseY, arrowX, arrowY));
        }
        if (guidePageIndex < pageCount - 1) {
            int arrowX = x + DETAIL_RIGHT_ARROW_X;
            int arrowY = y + GUIDE_DETAIL_ARROW_Y;
            fontRenderer.drawString(">", arrowX + 3, arrowY + 2, getDetailArrowColor(mouseX, mouseY, arrowX, arrowY));
        }
        if (pageCount > 1) {
            String count = I18n.format("gui.eidolon.codex.guide_pages", guidePageIndex + 1, pageCount);
            fontRenderer.drawString(count, x + 64 - fontRenderer.getStringWidth(count) / 2,
                    y + GUIDE_PAGE_COUNT_Y, 0x7d6b55);
        }
    }

    private void drawLoreIcon(LoreEntry entry, int x, int y) {
        if (!entry.unlocked) {
            fontRenderer.drawString("?", x + 4, y + 2, 0xa6957c);
        } else if (entry.kind == LoreEntry.KIND_SIGN) {
            drawSignIcon(entry.sign, x, y, 12);
        } else if (entry.kind == LoreEntry.KIND_RUNE) {
            drawRuneIcon(entry.rune, x, y, 12);
        } else if (!entry.signs.isEmpty()) {
            drawSignIcon(entry.signs.get(0), x, y, 12);
        }
    }

    private void drawSelectedLorePage(int x, int y, List<LoreEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        LoreEntry entry = entries.get(selectedLoreIndex);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BLANK_PAGE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_HEIGHT);
        drawCenteredPageTitle(entry.title, x, y + 24, 0x2f2118);
        if (!entry.unlocked) {
            fontRenderer.drawSplitString(entry.description, x + 12, y + 62, 104, 0x5b4732);
        } else if (entry.kind == LoreEntry.KIND_SIGN) {
            drawSignIcon(entry.sign, x + 56, y + 50, 16);
            fontRenderer.drawSplitString(entry.description, x + 12, y + 82, 104, 0x5b4732);
        } else if (entry.kind == LoreEntry.KIND_RUNE) {
            drawRuneIcon(entry.rune, x + 56, y + 50, 16);
            fontRenderer.drawSplitString(entry.description, x + 12, y + 82, 104, 0x5b4732);
        } else {
            drawChantLorePage(entry, x, y);
        }
    }

    private void drawChantLorePage(LoreEntry entry, int x, int y) {
        drawChantSequence(entry.signs, x, y + 42);
        int textX = x + 12;
        int textWidth = 104;
        int bottomY = y + PAGE_HEIGHT - 14;
        int textY = y + 64;
        int descriptionHeight = drawSplitStringLimited(entry.description, textX, textY, textWidth, 0x5b4732,
                Math.min(3, getLineCapacity(textY, bottomY)));
        textY += descriptionHeight > 0 ? descriptionHeight + 3 : 0;
        if (entry.spell != null) {
            String condition = I18n.format("gui.eidolon.codex.chant_condition",
                    localizeSpellCondition(entry.spell));
            int conditionHeight = drawSplitStringLimited(condition, textX, textY, textWidth, 0x7d6b55,
                    Math.min(2, getLineCapacity(textY, bottomY)));
            textY += conditionHeight > 0 ? conditionHeight + 2 : 0;
        }
        drawSplitStringLimited(I18n.format("gui.eidolon.codex.chant_flow"), textX, textY, textWidth, 0x7d6b55,
                Math.min(2, getLineCapacity(textY, bottomY)));
    }

    private void drawChantSequence(List<Sign> signs, int x, int y) {
        if (signs.isEmpty()) {
            return;
        }
        int spacing = 18;
        int width = Math.min(112, (signs.size() - 1) * spacing + 14);
        int startX = x + 64 - width / 2;
        for (int i = 0; i < signs.size(); i++) {
            drawSignIcon(signs.get(i), startX + i * spacing, y, 14);
        }
    }

    private boolean addToChant(Rune rune) {
        if (rune == null || chant.size() >= CHANT_MAX_RUNES) {
            return false;
        }
        chant.add(rune);
        return true;
    }

    private void drawChantBar(int left, int top, int mouseX, int mouseY) {
        int chantWidth = CHANT_CAP_WIDTH * 2 + CHANT_SLOT_WIDTH * chant.size();
        int baseX = left + WIDTH / 2 - chantWidth / 2;
        int baseY = top + CHANT_BASE_Y;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        int x = baseX;
        drawModalRectWithCustomSizedTexture(x, baseY, 256, 208, CHANT_CAP_WIDTH, CHANT_BUTTON_SIZE, 512, 512);
        x += CHANT_CAP_WIDTH;
        for (int i = 0; i < chant.size(); i++) {
            drawModalRectWithCustomSizedTexture(x, baseY, 272, 208, CHANT_SLOT_WIDTH, CHANT_BUTTON_SIZE, 512, 512);
            drawModalRectWithCustomSizedTexture(x, baseY + 6, 320, 240, CHANT_SLOT_WIDTH, CHANT_SLOT_WIDTH, 512, 512);
            x += CHANT_SLOT_WIDTH;
        }
        drawModalRectWithCustomSizedTexture(x, baseY, 296, 208, CHANT_CAP_WIDTH, CHANT_BUTTON_SIZE, 512, 512);

        int buttonX = baseX + chantWidth + 8;
        boolean chantHover = isIn(mouseX, mouseY, buttonX, baseY - 4,
                buttonX + CHANT_BUTTON_SIZE, baseY + CHANT_BUTTON_SIZE - 4);
        drawModalRectWithCustomSizedTexture(buttonX, baseY - 4, 336, chantHover ? 240 : 208,
                CHANT_BUTTON_SIZE, CHANT_BUTTON_SIZE, 512, 512);
        int clearX = buttonX + CHANT_BUTTON_SPACING;
        boolean clearHover = isIn(mouseX, mouseY, clearX, baseY - 4,
                clearX + CHANT_BUTTON_SIZE, baseY + CHANT_BUTTON_SIZE - 4);
        drawModalRectWithCustomSizedTexture(clearX, baseY - 4, 368, clearHover ? 240 : 208,
                CHANT_BUTTON_SIZE, CHANT_BUTTON_SIZE, 512, 512);

        int runeX = baseX + CHANT_CAP_WIDTH;
        for (Rune rune : chant) {
            drawRuneIcon(rune, runeX + 2, baseY + 8, 8);
            runeX += CHANT_SLOT_WIDTH;
        }

        if (chantHover) {
            drawHoveringText(Collections.singletonList(I18n.format("eidolon.codex.chant_hover")), mouseX, mouseY);
        } else if (clearHover) {
            drawHoveringText(Collections.singletonList(I18n.format("eidolon.codex.cancel_hover")), mouseX, mouseY);
        }
    }

    private boolean interactChantBar(int left, int top, int mouseX, int mouseY) {
        if (chant.isEmpty()) {
            return false;
        }
        int chantWidth = CHANT_CAP_WIDTH * 2 + CHANT_SLOT_WIDTH * chant.size();
        int baseX = left + WIDTH / 2 - chantWidth / 2;
        int baseY = top + CHANT_BASE_Y;
        int buttonX = baseX + chantWidth + 8;
        if (isIn(mouseX, mouseY, buttonX, baseY - 4,
                buttonX + CHANT_BUTTON_SIZE, baseY + CHANT_BUTTON_SIZE - 4)) {
            ModNetwork.CHANNEL.sendToServer(new AttemptCastPacket(chant));
            chant.clear();
            playPageSound();
            if (mc.player != null) {
                mc.player.closeScreen();
            }
            return true;
        }
        int clearX = buttonX + CHANT_BUTTON_SPACING;
        if (isIn(mouseX, mouseY, clearX, baseY - 4,
                clearX + CHANT_BUTTON_SIZE, baseY + CHANT_BUTTON_SIZE - 4)) {
            chant.clear();
            playPageSound();
            return true;
        }
        return false;
    }

    private void drawSignIcon(Sign sign, int x, int y, int size) {
        drawColoredTexture(sign.getSprite(), x, y, size, size, sign.getRed(), sign.getGreen(), sign.getBlue());
    }

    private void drawRuneIcon(Rune rune, int x, int y, int size) {
        drawColoredTexture(rune.getSprite(), x, y, size, size, 1.0F, 1.0F, 1.0F);
    }

    private void drawColoredTexture(ResourceLocation texture, int x, int y, int width, int height,
                                    float red, float green, float blue) {
        GlStateManager.color(red, green, blue, 1.0F);
        mc.getTextureManager().bindTexture(new ResourceLocation(texture.getNamespace(),
                "textures/" + texture.getPath() + ".png"));
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawSelectedRecipePage(int x, int y, int mouseX, int mouseY) {
        if (view == VIEW_WORKTABLE_RECIPES) {
            List<WorktableRecipe> recipes = WorktableRecipes.getRecipes();
            if (!recipes.isEmpty()) {
                drawWorktableRecipePage(recipes.get(selectedRecipeIndex), x, y);
            }
        } else if (view == VIEW_CRUCIBLE_RECIPES) {
            List<CrucibleRecipe> recipes = CrucibleRecipes.getRecipes();
            if (!recipes.isEmpty()) {
                drawCrucibleRecipePage(recipes.get(selectedRecipeIndex), x, y);
            }
        } else if (view == VIEW_ALTAR_RITUALS) {
            List<AltarRitual> rituals = AltarRituals.getRituals();
            if (!rituals.isEmpty()) {
                drawAltarRitualPage(rituals.get(selectedRecipeIndex), x, y, mouseX, mouseY);
            }
        } else if (view == VIEW_ALTAR_OFFERINGS) {
            List<ItemStack> offerings = getAltarOfferingStacks();
            if (!offerings.isEmpty()) {
                drawAltarOfferingPage(offerings.get(selectedRecipeIndex), x, y);
            }
        } else if (view == VIEW_ATHAME_HARVEST) {
            List<AthameItem.HarvestEntry> entries = AthameItem.getHarvestEntries();
            if (!entries.isEmpty()) {
                drawAthameHarvestPage(entries.get(selectedRecipeIndex), x, y);
            }
        } else if (view == VIEW_SOUL_SHARD_HARVEST) {
            drawSoulShardHarvestPage(SoulShardHarvestRecipe.INSTANCE, x, y);
        } else if (view == VIEW_MACHINE_INFO) {
            List<MachineInfoRecipe> recipes = MachineInfoRecipe.getRecipes();
            if (!recipes.isEmpty()) {
                drawMachineInfoPage(recipes.get(selectedRecipeIndex), x, y);
            }
        } else if (view == VIEW_INCUBATOR_RECIPES) {
            List<IncubatorRecipe> recipes = IncubatorRecipes.getRecipes();
            if (!recipes.isEmpty()) {
                drawIncubatorRecipePage(recipes.get(selectedRecipeIndex), x, y);
            }
        }
    }

    private void drawMachineInfoPage(MachineInfoRecipe recipe, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BLANK_PAGE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_HEIGHT);
        drawCenteredPageTitle(I18n.format(recipe.getTitleKey()), x, y + 26, 0x2f2118);
        drawStack(recipe.getCatalyst(), x + 12, y + 54);
        drawStack(recipe.getPrimaryInput(), x + 40, y + 72);
        drawStack(recipe.getSecondaryInput(), x + 66, y + 72);
        drawStack(recipe.getOutput(), x + 100, y + 72);
        fontRenderer.drawString("+", x + 58, y + 77, 0x5b4732);
        fontRenderer.drawString(">", x + 86, y + 77, 0x5b4732);
        fontRenderer.drawSplitString(I18n.format(recipe.getDescriptionKey()), x + 12, y + 100, 104, 0x5b4732);
    }

    private void drawIncubatorRecipePage(IncubatorRecipe recipe, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BLANK_PAGE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_HEIGHT);
        drawCenteredPageTitle(I18n.format("tile.eidolon.incubator.name"), x, y + 26, 0x2f2118);
        drawIngredient(recipe.getInput(), x + 28, y + 52);
        drawIngredient(recipe.getCatalyst(), x + 28, y + 76);
        drawStack(recipe.getResult(), x + 92, y + 64);
        drawRect(x + 56, y + 69, x + 82, y + 74, 0xff8d7f6d);
        drawRect(x + 56, y + 69, x + 74, y + 74, 0xff4f8f6a);
        fontRenderer.drawString(">", x + 67, y + 55, 0x5b4732);
        drawCenteredText(recipe.getResult().getDisplayName(), x + 64, y + 92, 116, 0x2f2118);
        fontRenderer.drawSplitString(I18n.format("gui.eidolon.incubator.ticks", recipe.getTicks()),
                x + 12, y + 116, 104, 0x5b4732);
    }

    private void drawSoulShardHarvestPage(SoulShardHarvestRecipe recipe, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BLANK_PAGE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_HEIGHT);
        drawCenteredPageTitle(I18n.format("gui.eidolon.soul_shard_harvest.title"), x, y + 28, 0x2f2118);
        drawStack(recipe.getSources().get(0), x + 20, y + 62);
        drawStack(recipe.getTool(), x + 56, y + 62);
        drawStack(recipe.getResult(), x + 92, y + 62);
        fontRenderer.drawString(">", x + 42, y + 67, 0x5b4732);
        fontRenderer.drawString(">", x + 78, y + 67, 0x5b4732);
        drawCenteredText(recipe.getResult().getDisplayName(), x + 64, y + 88, 116, 0x2f2118);
        fontRenderer.drawSplitString(I18n.format("gui.eidolon.soul_shard_harvest.codex_desc"),
                x + 12, y + 108, 104, 0x5b4732);
    }

    private void drawAthameHarvestPage(AthameItem.HarvestEntry entry, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BLANK_PAGE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_HEIGHT);
        drawCenteredPageTitle(I18n.format("gui.eidolon.athame_harvest.title"), x, y + 28, 0x2f2118);
        drawStack(entry.getSources().get(0), x + 20, y + 62);
        drawStack(new ItemStack(ModItems.ATHAME), x + 56, y + 62);
        drawStack(entry.getResult(), x + 92, y + 62);
        fontRenderer.drawString(">", x + 42, y + 67, 0x5b4732);
        fontRenderer.drawString(">", x + 78, y + 67, 0x5b4732);
        drawCenteredText(entry.getResult().getDisplayName(), x + 64, y + 88, 116, 0x2f2118);
        fontRenderer.drawSplitString(I18n.format("gui.eidolon.athame_harvest.codex_desc",
                I18n.format(entry.getSourceKey())), x + 12, y + 108, 104, 0x5b4732);
    }

    private void drawAltarOfferingPage(ItemStack stack, int x, int y) {
        AltarEntry entry = AltarEntries.find(stack);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BLANK_PAGE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_HEIGHT);
        drawCenteredPageTitle(I18n.format("gui.eidolon.codex.altar_offering"), x, y + 28, 0x2f2118);
        drawStack(stack, x + 56, y + 52);
        drawCenteredText(stack.getDisplayName(), x + 64, y + 74, 116, 0x2f2118);
        if (entry == null) {
            return;
        }
        int textY = y + 96;
        fontRenderer.drawString(I18n.format("gui.eidolon.codex.altar_offering_capacity", formatRecipeValue(entry.getCapacity())), x + 12, textY, 0x5b4732);
        textY += 14;
        fontRenderer.drawString(I18n.format("gui.eidolon.codex.altar_offering_power", formatRecipeValue(entry.getPower())), x + 12, textY, 0x5b4732);
        textY += 18;
        fontRenderer.drawSplitString(I18n.format("gui.eidolon.codex.altar_offering_hint"), x + 12, textY, 104, 0x7d6b55);
    }

    private void drawAltarRitualPage(AltarRitual ritual, int x, int y, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(RITUAL_PAGE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, 256, 256);
        String requirements = I18n.format("gui.eidolon.codex.altar_capacity", formatRecipeValue(ritual.getRequiredCapacity()))
                + "  " + I18n.format("gui.eidolon.codex.altar_power", formatRecipeValue(ritual.getRequiredPower()));
        fontRenderer.drawString(requirements, x + 64 - fontRenderer.getStringWidth(requirements) / 2, y + 8, 0x5b4732);
        if (ritual.hasHealthCost()) {
            String health = I18n.format("gui.eidolon.codex.altar_health", formatRecipeValue(ritual.getHealthCost()));
            fontRenderer.drawString(health, x + 64 - fontRenderer.getStringWidth(health) / 2, y + 18, 0x8b2f2f);
        }
        String behavior = I18n.format(ritual.getBehaviorTranslationKey());
        fontRenderer.drawString(behavior, x + 64 - fontRenderer.getStringWidth(behavior) / 2, y + 28, 0x7d5a39);
        drawStack(ritual.getDisplayStack(), x + ALTAR_RITUAL_RESULT_X, y + ALTAR_RITUAL_RESULT_Y);
        drawIngredient(ritual.getSacrifice(), x + ALTAR_RITUAL_SACRIFICE_X, y + ALTAR_RITUAL_SACRIFICE_Y);
        drawAltarRitualOfferings(ritual, x, y);
        if (ritual.hasFocus()) {
            int[] focusPos = getRitualFocusPosition(ritual);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(RITUAL_PAGE);
            drawModalRectWithCustomSizedTexture(x + focusPos[0] - 5, y + focusPos[1] - 5, 128, 0, 26, 24, 256, 256);
            drawIngredient(ritual.getFocus(), x + focusPos[0], y + focusPos[1]);
        }
        drawAltarRitualTooltip(ritual, x, y, mouseX, mouseY);
    }

    private void drawAltarRitualTooltip(AltarRitual ritual, int x, int y, int mouseX, int mouseY) {
        if (isIn(mouseX, mouseY, x + ALTAR_RITUAL_RESULT_X, y + ALTAR_RITUAL_RESULT_Y,
                x + ALTAR_RITUAL_RESULT_X + 16, y + ALTAR_RITUAL_RESULT_Y + 16)) {
            String key = ritual.hasResult()
                    ? "gui.eidolon.codex.altar_slot.result"
                    : ritual.getBehaviorTranslationKey();
            List<String> tooltip = new ArrayList<>();
            tooltip.add(I18n.format(key));
            tooltip.add(I18n.format(ritual.getResultDescriptionTranslationKey()));
            drawHoveringText(tooltip, mouseX, mouseY);
            return;
        }
        if (isIn(mouseX, mouseY, x + ALTAR_RITUAL_SACRIFICE_X, y + ALTAR_RITUAL_SACRIFICE_Y,
                x + ALTAR_RITUAL_SACRIFICE_X + 16, y + ALTAR_RITUAL_SACRIFICE_Y + 16)) {
            drawHoveringText(Collections.singletonList(I18n.format("gui.eidolon.codex.altar_slot.sacrifice")), mouseX, mouseY);
            return;
        }
        if (ritual.hasFocus()) {
            int[] focusPos = getRitualFocusPosition(ritual);
            if (!isIn(mouseX, mouseY, x + focusPos[0], y + focusPos[1], x + focusPos[0] + 16, y + focusPos[1] + 16)) {
                focusPos = null;
            }
            if (focusPos != null) {
                String key = ritual.getBehaviorType() == AltarRitual.BehaviorType.ITEM_CHARGE
                        ? "gui.eidolon.codex.altar_slot.charge_focus"
                        : ritual.getBehaviorType() == AltarRitual.BehaviorType.ENTITY_SUMMON
                        ? "gui.eidolon.codex.altar_slot.summon_focus"
                        : ritual.getBehaviorType() == AltarRitual.BehaviorType.ABSORPTION
                        ? "gui.eidolon.codex.altar_slot.absorption_focus"
                        : "gui.eidolon.codex.altar_slot.focus";
                drawHoveringText(Collections.singletonList(I18n.format(key)), mouseX, mouseY);
                return;
            }
        }
        for (int i = 0; i < ritual.getProviderOfferings().size(); i++) {
            int[] pos = getRitualOfferingPosition(ritual, i);
            if (isIn(mouseX, mouseY, x + pos[0], y + pos[1], x + pos[0] + 16, y + pos[1] + 16)) {
                drawHoveringText(Collections.singletonList(I18n.format("gui.eidolon.codex.altar_slot.offering")), mouseX, mouseY);
                return;
            }
        }
    }

    private void drawAltarRitualOfferings(AltarRitual ritual, int x, int y) {
        for (int i = 0; i < ritual.getProviderOfferings().size(); i++) {
            Ingredient ingredient = ritual.getProviderOfferings().get(i);
            int[] pos = getRitualOfferingPosition(ritual, i);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(RITUAL_PAGE);
            drawModalRectWithCustomSizedTexture(x + pos[0], y + pos[1], 154, 0, 16, 16, 256, 256);
            drawIngredient(ingredient, x + pos[0], y + pos[1]);
        }
    }

    private int[] getRitualOfferingPosition(AltarRitual ritual, int offeringIndex) {
        return getRitualArcPosition(offeringIndex, ritual.getProviderOfferings().size());
    }

    private int[] getRitualFocusPosition(AltarRitual ritual) {
        return new int[] {ALTAR_RITUAL_FOCUS_X, ALTAR_RITUAL_FOCUS_Y};
    }

    private int[] getRitualArcPosition(int index, int count) {
        if (count <= 0) {
            return new int[] {56, 120};
        }
        double angleStep = Math.min(36.0D, 180.0D / count);
        double rootAngle = 90.0D - (count - 1) * angleStep / 2.0D;
        double angle = Math.toRadians(rootAngle + angleStep * index);
        int centerX = (int)(64.0D + 56.0D * Math.cos(angle));
        int centerY = (int)(79.0D + 47.0D * Math.sin(angle));
        return new int[] {centerX - 8, centerY - 8};
    }

    private void drawWorktableRecipePage(WorktableRecipe recipe, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(WORKTABLE_PAGE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, 256, 256);
        for (int i = 0; i < WorktableRecipe.GRID_SIZE; i++) {
            drawIngredient(recipe.getGridIngredient(i), x + 39 + (i % 3) * 17, y + 33 + (i / 3) * 17);
        }

        drawIngredient(recipe.getReagent(0), x + 56, y + 11);
        drawIngredient(recipe.getReagent(1), x + 95, y + 50);
        drawIngredient(recipe.getReagent(2), x + 56, y + 89);
        drawIngredient(recipe.getReagent(3), x + 17, y + 50);
        drawStack(recipe.getResult(), x + 56, y + 129);
    }

    private void drawCrucibleRecipePage(CrucibleRecipe recipe, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(CRUCIBLE_PAGE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, 256, 256);
        List<CrucibleRecipe.Step> steps = recipe.getSteps();
        crucibleStepScroll = clampCrucibleStepScroll(crucibleStepScroll, steps.size());
        drawStack(getFluidBucket(recipe), x + 24, y + CRUCIBLE_FLUID_Y);
        int visibleEnd = Math.min(steps.size(), crucibleStepScroll + CRUCIBLE_VISIBLE_STEPS);
        for (int stepIndex = crucibleStepScroll; stepIndex < visibleEnd; stepIndex++) {
            CrucibleRecipe.Step step = steps.get(stepIndex);
            int rowX = x;
            int rowY = y + CRUCIBLE_STEP_START_Y + (stepIndex - crucibleStepScroll) * CRUCIBLE_STEP_ROW_HEIGHT;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(CRUCIBLE_PAGE);
            drawModalRectWithCustomSizedTexture(rowX, rowY, 128, 0, 128, 20, 256, 256);
            rowX += CRUCIBLE_INPUT_START_X;
            int inputs = 0;
            for (List<ItemStack> stacks : getCondensedStacks(step)) {
                if (inputs >= CRUCIBLE_MAX_VISIBLE_INPUTS) {
                    break;
                }
                drawCompactStack(firstStack(stacks), rowX, rowY + 4);
                rowX += CRUCIBLE_INPUT_SPACING;
                inputs++;
            }
            if (step.getStirs() > 0) {
                drawCompactStack(firstStack(stacksFor(recipe.getStirrer())), x + CRUCIBLE_STIRRER_X, rowY + 4);
                fontRenderer.drawString("x" + step.getStirs(), x + CRUCIBLE_STIRRER_X + 10, rowY + 7, 0x5b4732);
            }
            fontRenderer.drawString((stepIndex + 1) + ".", x + 7, rowY + 7, 0x5b4732);
        }
        int resultY = y + CRUCIBLE_RESULT_Y;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(CRUCIBLE_PAGE);
        drawModalRectWithCustomSizedTexture(x, resultY, 128, 64, 128, 32, 256, 256);
        drawStack(recipe.getResult(), x + 56, resultY + 11);
        drawCrucibleScrollControls(x, y, steps.size());
    }

    private void drawCrucibleScrollControls(int x, int y, int stepCount) {
        if (getMaxCrucibleStepScroll(stepCount) <= 0) {
            return;
        }
        drawCrucibleScrollButton(x + CRUCIBLE_SCROLL_X, y + CRUCIBLE_SCROLL_UP_Y, "^", crucibleStepScroll > 0);
        drawCrucibleScrollButton(x + CRUCIBLE_SCROLL_X, y + CRUCIBLE_SCROLL_DOWN_Y, "v",
                crucibleStepScroll < getMaxCrucibleStepScroll(stepCount));
    }

    private void drawCrucibleScrollButton(int x, int y, String label, boolean enabled) {
        int fill = enabled ? 0xffd8ceb8 : 0xffb7af9e;
        int border = enabled ? 0xff5b4732 : 0xff8f8472;
        drawRect(x, y, x + CRUCIBLE_SCROLL_BUTTON_SIZE, y + CRUCIBLE_SCROLL_BUTTON_SIZE, fill);
        drawHorizontalLine(x, x + CRUCIBLE_SCROLL_BUTTON_SIZE, y, border);
        drawHorizontalLine(x, x + CRUCIBLE_SCROLL_BUTTON_SIZE, y + CRUCIBLE_SCROLL_BUTTON_SIZE, border);
        drawVerticalLine(x, y, y + CRUCIBLE_SCROLL_BUTTON_SIZE, border);
        drawVerticalLine(x + CRUCIBLE_SCROLL_BUTTON_SIZE, y, y + CRUCIBLE_SCROLL_BUTTON_SIZE, border);
        fontRenderer.drawString(label, x + 4, y + 2, enabled ? 0x2f2118 : 0x7d7366);
    }

    private void drawIngredient(Ingredient ingredient, int x, int y) {
        drawStack(firstStack(stacksFor(ingredient)), x, y);
    }

    private ItemStack getFluidBucket(CrucibleRecipe recipe) {
        if (recipe.getFluid().getFluid() == FluidRegistry.WATER) {
            return new ItemStack(Items.WATER_BUCKET);
        }
        if (recipe.getFluid().getFluid() == FluidRegistry.LAVA) {
            return new ItemStack(Items.LAVA_BUCKET);
        }
        return new ItemStack(Items.BUCKET);
    }

    private void drawStack(ItemStack stack, int x, int y) {
        if (stack.isEmpty()) {
            return;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, null);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawCompactStack(ItemStack stack, int x, int y) {
        if (stack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0F);
        float scale = CRUCIBLE_COMPACT_ITEM_SIZE / 16.0F;
        GlStateManager.scale(scale, scale, 1.0F);
        drawStack(stack, 0, 0);
        GlStateManager.popMatrix();
    }

    private List<List<ItemStack>> getCondensedStacks(CrucibleRecipe.Step step) {
        List<List<ItemStack>> display = new ArrayList<>();
        for (Ingredient ingredient : step.getIngredients()) {
            List<ItemStack> stacks = copyStacks(stacksFor(ingredient));
            if (stacks.isEmpty()) {
                continue;
            }
            if (!display.isEmpty() && canMerge(display.get(display.size() - 1), stacks)) {
                for (ItemStack stack : display.get(display.size() - 1)) {
                    stack.grow(1);
                }
            } else {
                display.add(stacks);
            }
        }
        return display;
    }

    private List<ItemStack> stacksFor(Ingredient ingredient) {
        if (ingredient == Ingredient.EMPTY) {
            return Collections.emptyList();
        }
        return Arrays.asList(ingredient.getMatchingStacks());
    }

    private List<ItemStack> copyStacks(List<ItemStack> stacks) {
        List<ItemStack> copy = new ArrayList<>();
        for (ItemStack stack : stacks) {
            copy.add(stack.copy());
        }
        return copy;
    }

    private ItemStack firstStack(List<ItemStack> stacks) {
        return stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0).copy();
    }

    private boolean canMerge(List<ItemStack> left, List<ItemStack> right) {
        if (left.size() != right.size()) {
            return false;
        }
        for (int i = 0; i < left.size(); i++) {
            if (!ItemStack.areItemsEqual(left.get(i), right.get(i))
                    || !ItemStack.areItemStackTagsEqual(left.get(i), right.get(i))) {
                return false;
            }
        }
        return true;
    }

    private List<ItemStack> getRecipeOutputs() {
        List<ItemStack> outputs = new ArrayList<>();
        if (view == VIEW_WORKTABLE_RECIPES) {
            for (WorktableRecipe recipe : WorktableRecipes.getRecipes()) {
                outputs.add(recipe.getResult());
            }
        } else if (view == VIEW_CRUCIBLE_RECIPES) {
            for (CrucibleRecipe recipe : CrucibleRecipes.getRecipes()) {
                outputs.add(recipe.getResult());
            }
        } else if (view == VIEW_ALTAR_RITUALS) {
            for (AltarRitual ritual : AltarRituals.getRituals()) {
                ItemStack display = ritual.getDisplayStack();
                if (!display.isEmpty()) {
                    outputs.add(display);
                }
            }
        } else if (view == VIEW_ALTAR_OFFERINGS) {
            outputs.addAll(getAltarOfferingStacks());
        } else if (view == VIEW_ATHAME_HARVEST) {
            for (AthameItem.HarvestEntry entry : AthameItem.getHarvestEntries()) {
                outputs.add(entry.getResult());
            }
        } else if (view == VIEW_SOUL_SHARD_HARVEST) {
            outputs.add(SoulShardHarvestRecipe.INSTANCE.getResult());
        } else if (view == VIEW_MACHINE_INFO) {
            for (MachineInfoRecipe recipe : MachineInfoRecipe.getRecipes()) {
                outputs.add(recipe.getCatalyst());
            }
        } else if (view == VIEW_INCUBATOR_RECIPES) {
            for (IncubatorRecipe recipe : IncubatorRecipes.getRecipes()) {
                outputs.add(recipe.getResult());
            }
        }
        return outputs;
    }

    private List<LoreEntry> getLoreEntries() {
        List<LoreEntry> entries = new ArrayList<>();
        if (view == VIEW_SIGNS) {
            for (Sign sign : Signs.getSigns()) {
                boolean known = KnowledgeUtil.knowsSign(player, sign);
                entries.add(LoreEntry.sign(sign, known ? localizeSign(sign) : lockedLoreTitle(),
                        known ? localizeSignDescription(sign) : lockedLoreDescription(), known));
            }
        } else if (view == VIEW_RUNES) {
            for (Rune rune : Runes.getRunes()) {
                boolean known = KnowledgeUtil.knowsRune(player, rune);
                entries.add(LoreEntry.rune(rune, known ? localizeRune(rune) : lockedLoreTitle(),
                        known ? localizeRuneDescription(rune) : lockedLoreDescription(), known));
            }
        } else if (view == VIEW_CHANTS) {
            for (Spell spell : Spells.getSpells()) {
                if (spell instanceof StaticSpell) {
                    SignSequence sequence = ((StaticSpell) spell).getSigns();
                    List<Sign> signs = new ArrayList<>(sequence.getSigns());
                    boolean known = knowsAllSigns(signs);
                    entries.add(LoreEntry.chant(spell, known ? localizeSpell(spell) : lockedLoreTitle(),
                            known ? localizeSpellDescription(spell) : lockedLoreDescription(), signs, known));
                }
            }
        }
        return entries;
    }

    private boolean knowsAllSigns(List<Sign> signs) {
        for (Sign sign : signs) {
            if (!KnowledgeUtil.knowsSign(player, sign)) {
                return false;
            }
        }
        return true;
    }

    private String lockedLoreTitle() {
        return I18n.format("gui.eidolon.codex.unknown");
    }

    private String lockedLoreDescription() {
        return fallback("gui.eidolon.codex.lore_locked_detail", I18n.format("gui.eidolon.codex.locked_detail"));
    }

    private String localizeSign(Sign sign) {
        return fallback("eidolon.sign." + sign.getRegistryName().getPath(), titleCase(sign.getRegistryName().getPath()));
    }

    private String localizeRune(Rune rune) {
        return fallback("eidolon.rune." + rune.getRegistryName().getPath(), titleCase(rune.getRegistryName().getPath()));
    }

    private String localizeSpell(Spell spell) {
        return fallback("eidolon.spell." + spell.getRegistryName().getPath(),
                fallback("eidolon.codex.chapter." + spell.getRegistryName().getPath(),
                        titleCase(spell.getRegistryName().getPath())));
    }

    private String localizeSignDescription(Sign sign) {
        return fallback("eidolon.codex.page." + sign.getRegistryName().getPath() + "_sign",
                I18n.format("gui.eidolon.codex.sign_fallback", localizeSign(sign)));
    }

    private String localizeRuneDescription(Rune rune) {
        return fallback("eidolon.codex.rune." + rune.getRegistryName().getPath(),
                I18n.format("gui.eidolon.codex.rune_fallback", localizeRune(rune)));
    }

    private String localizeSpellDescription(Spell spell) {
        return fallback("eidolon.codex.page." + spell.getRegistryName().getPath(),
                I18n.format("gui.eidolon.codex.chant_fallback"));
    }

    private String localizeSpellCondition(Spell spell) {
        return fallback("eidolon.codex.chant.condition." + spell.getRegistryName().getPath(),
                I18n.format("gui.eidolon.codex.chant_condition_fallback"));
    }

    private String fallback(String key, String fallback) {
        String translated = I18n.format(key);
        return key.equals(translated) ? fallback : translated;
    }

    private String titleCase(String path) {
        String[] parts = path.split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.length() == 0 ? path : builder.toString();
    }

    private List<ItemStack> getAltarOfferingStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (Item item : AltarEntries.getEntries().keySet()) {
            stacks.add(new ItemStack(item));
        }
        return stacks;
    }

    private boolean isRecipeView() {
        return view == VIEW_WORKTABLE_RECIPES || view == VIEW_CRUCIBLE_RECIPES
                || view == VIEW_ALTAR_RITUALS || view == VIEW_ALTAR_OFFERINGS
                || view == VIEW_ATHAME_HARVEST || view == VIEW_SOUL_SHARD_HARVEST
                || view == VIEW_MACHINE_INFO || view == VIEW_INCUBATOR_RECIPES;
    }

    private boolean isLoreView() {
        return view == VIEW_SIGNS || view == VIEW_RUNES || view == VIEW_CHANTS;
    }

    private String getRecipeChapterTitle() {
        if (view == VIEW_WORKTABLE_RECIPES) {
            return I18n.format("gui.eidolon.codex.chapter_worktable_recipes");
        }
        if (view == VIEW_CRUCIBLE_RECIPES) {
            return I18n.format("gui.eidolon.codex.chapter_crucible_recipes");
        }
        if (view == VIEW_ALTAR_RITUALS) {
            return I18n.format("gui.eidolon.codex.chapter_altar_rituals");
        }
        if (view == VIEW_ALTAR_OFFERINGS) {
            return I18n.format("gui.eidolon.codex.chapter_altar_offerings");
        }
        if (view == VIEW_SOUL_SHARD_HARVEST) {
            return I18n.format("gui.eidolon.codex.chapter_soul_shard_harvest");
        }
        if (view == VIEW_MACHINE_INFO) {
            return I18n.format("gui.eidolon.codex.chapter_machine_info");
        }
        if (view == VIEW_INCUBATOR_RECIPES) {
            return I18n.format("gui.eidolon.codex.chapter_incubator_recipes");
        }
        return I18n.format("gui.eidolon.codex.chapter_athame_harvest");
    }

    private String getLoreChapterTitle() {
        if (view == VIEW_SIGNS) {
            return I18n.format("gui.eidolon.codex.chapter_signs");
        }
        if (view == VIEW_RUNES) {
            return I18n.format("gui.eidolon.codex.chapter_runes");
        }
        return I18n.format("gui.eidolon.codex.chapter_chants");
    }

    private String formatRecipeValue(double value) {
        return value == (int) value ? Integer.toString((int) value) : Double.toString(value);
    }

    private void drawCenteredPageTitle(String title, int left, int y, int color) {
        fontRenderer.drawString(TextFormatting.UNDERLINE + title, left + 64 - fontRenderer.getStringWidth(title) / 2, y, color);
    }

    private void drawCenteredText(String text, int centerX, int y, int width, int color) {
        List<String> lines = fontRenderer.listFormattedStringToWidth(text, width);
        if (lines.isEmpty()) {
            return;
        }
        String line = lines.get(0);
        fontRenderer.drawString(line, centerX - fontRenderer.getStringWidth(line) / 2, y, color);
    }

    private List<Research> getVisibleResearches() {
        List<Research> researches = new ArrayList<>(Researches.getResearches());
        researches.sort(Comparator
                .comparingInt(this::getResearchState)
                .thenComparing(this::getResearchName)
                .thenComparing(research -> research.getId().toString()));
        return researches;
    }

    private int getResearchState(Research research) {
        if (KnowledgeUtil.knowsResearch(player, research.getId())) {
            return STATE_KNOWN;
        }
        return research.isUnlockedFor(player) ? STATE_AVAILABLE : STATE_LOCKED;
    }

    private int getListColor(int state, boolean selected) {
        if (state == STATE_KNOWN) {
            return selected ? 0x1f140f : 0x2f2118;
        }
        if (state == STATE_AVAILABLE) {
            return 0x7d6b55;
        }
        return 0xa6957c;
    }

    private int getKnownCount(List<Research> researches) {
        int count = 0;
        for (Research research : researches) {
            if (KnowledgeUtil.knowsResearch(player, research.getId())) {
                count++;
            }
        }
        return count;
    }

    private void playPageSound() {
        if (mc == null || mc.player == null || mc.world == null) {
            return;
        }
        mc.world.playSound(mc.player, mc.player.posX, mc.player.posY, mc.player.posZ,
                ModSounds.SELECT_RUNE, SoundCategory.NEUTRAL, 0.45F,
                0.8F + mc.player.getRNG().nextFloat() * 0.25F);
    }

    private void drawResearchListPageArrows(int left, int top, int mouseX, int mouseY, int totalEntries) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        if (researchListPage > 0) {
            drawPageArrow(left + LEFT_ARROW_X, top + PAGE_ARROW_Y, 128,
                    isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                            left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT));
        }
        if (researchListPage < getMaxResearchListPage(totalEntries)) {
            drawPageArrow(left + RIGHT_ARROW_X, top + PAGE_ARROW_Y, 160,
                    isIn(mouseX, mouseY, left + RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                            left + RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT));
        }
    }

    private void drawRecipeListPageArrows(int left, int top, int mouseX, int mouseY, int totalEntries) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        if (recipeListPage > 0) {
            drawPageArrow(left + LEFT_ARROW_X, top + PAGE_ARROW_Y, 128,
                    isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                            left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT));
        }
        if (recipeListPage < getMaxRecipeListPage(totalEntries)) {
            drawPageArrow(left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y, 160,
                    isIn(mouseX, mouseY, left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                            left + LEFT_PAGE_RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT));
        }
    }

    private void drawLoreListPageArrows(int left, int top, int mouseX, int mouseY, int totalEntries) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        if (loreListPage > 0) {
            drawPageArrow(left + LEFT_ARROW_X, top + PAGE_ARROW_Y, 128,
                    isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                            left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT));
        }
        if (loreListPage < getMaxLoreListPage(totalEntries)) {
            drawPageArrow(left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y, 160,
                    isIn(mouseX, mouseY, left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                            left + LEFT_PAGE_RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT));
        }
    }

    private void drawGuideListPageArrows(int left, int top, int mouseX, int mouseY, int totalEntries) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        if (guideChapterListPage > 0) {
            drawPageArrow(left + LEFT_ARROW_X, top + PAGE_ARROW_Y, 128,
                    isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                            left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT));
        }
        if (guideChapterListPage < getMaxGuideChapterListPage(totalEntries)) {
            drawPageArrow(left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y, 160,
                    isIn(mouseX, mouseY, left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                            left + LEFT_PAGE_RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT));
        }
    }

    private void drawPageArrow(int x, int y, int u, boolean hovered) {
        drawModalRectWithCustomSizedTexture(x, y, u, hovered ? 226 : 208,
                PAGE_ARROW_WIDTH, PAGE_ARROW_HEIGHT, 512, 512);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int left = (width - WIDTH) / 2;
            int top = (height - HEIGHT) / 2;
            if (interactChantBar(left, top, mouseX, mouseY)) {
                return;
            }
            IndexEntry clickedTab = getClickedCategoryTab(mouseX, mouseY, left, top);
            if (clickedTab != null) {
                if (view != clickedTab.view) {
                    playPageSound();
                }
                view = clickedTab.view;
                recipeListPage = 0;
                loreListPage = 0;
                researchListPage = 0;
                selectedRecipeIndex = 0;
                selectedLoreIndex = 0;
                resetGuideSelection();
                crucibleStepScroll = 0;
                return;
            }
            if (view == VIEW_INDEX) {
                if (isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                        left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                    if (indexPage > 0) {
                        indexPage--;
                        playPageSound();
                    }
                    return;
                }
                if (isIn(mouseX, mouseY, left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                        left + LEFT_PAGE_RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                    if (indexPage < getMaxIndexPage(getIndexEntries().size())) {
                        indexPage++;
                        playPageSound();
                    }
                    return;
                }
                IndexEntry clickedEntry = getClickedIndexEntry(mouseX, mouseY, left, top);
                if (clickedEntry != null) {
                    view = clickedEntry.view;
                    recipeListPage = 0;
                    loreListPage = 0;
                    selectedRecipeIndex = 0;
                    selectedLoreIndex = 0;
                    resetGuideSelection();
                    crucibleStepScroll = 0;
                    playPageSound();
                    return;
                }
            } else if (isIn(mouseX, mouseY, left + 24, top + 32, left + 70, top + 44)) {
                view = VIEW_INDEX;
                playPageSound();
                return;
            } else if (view == VIEW_GUIDE) {
                if (handleGuideClick(mouseX, mouseY, left, top)) {
                    return;
                }
                return;
            } else if (isRecipeView()) {
                if (isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                        left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                    if (recipeListPage > 0) {
                        recipeListPage--;
                        crucibleStepScroll = 0;
                        playPageSound();
                        return;
                    }
                } else if (isIn(mouseX, mouseY, left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                        left + LEFT_PAGE_RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                    int totalEntries = getRecipeOutputs().size();
                    if (recipeListPage < getMaxRecipeListPage(totalEntries)) {
                        recipeListPage++;
                        crucibleStepScroll = 0;
                        playPageSound();
                        return;
                    }
                }
                if (handleCrucibleRecipeScrollClick(mouseX, mouseY, left, top)) {
                    return;
                }
                int clicked = getClickedRecipeIndex(mouseX, mouseY, left, top);
                if (clicked >= 0) {
                    if (selectedRecipeIndex != clicked) {
                        playPageSound();
                    }
                    selectedRecipeIndex = clicked;
                    crucibleStepScroll = 0;
                    return;
                }
                return;
            } else if (isLoreView()) {
                if (isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                        left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                    if (loreListPage > 0) {
                        loreListPage--;
                        playPageSound();
                        return;
                    }
                } else if (isIn(mouseX, mouseY, left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                        left + LEFT_PAGE_RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                    int totalEntries = getLoreEntries().size();
                    if (loreListPage < getMaxLoreListPage(totalEntries)) {
                        loreListPage++;
                        playPageSound();
                        return;
                    }
                }
                int clicked = getClickedLoreIndex(mouseX, mouseY, left, top);
                if (clicked >= 0) {
                    boolean played = false;
                    if (selectedLoreIndex != clicked) {
                        playPageSound();
                        played = true;
                    }
                    selectedLoreIndex = clicked;
                    List<LoreEntry> entries = getLoreEntries();
                    if (view == VIEW_RUNES && clicked < entries.size()) {
                        LoreEntry entry = entries.get(clicked);
                        if (entry.unlocked && addToChant(entry.rune) && !played) {
                            playPageSound();
                        }
                    }
                    return;
                }
                return;
            } else if (view == VIEW_RESEARCH && isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                    left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                if (researchListPage > 0) {
                    researchListPage--;
                    playPageSound();
                    return;
                }
            } else if (view == VIEW_RESEARCH && isIn(mouseX, mouseY, left + RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                    left + RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                List<Research> researches = getVisibleResearches();
                if (researchListPage < getMaxResearchListPage(researches.size())) {
                    researchListPage++;
                    playPageSound();
                    return;
                }
            } else if (view == VIEW_RESEARCH && selectedResearch != null && getResearchState(selectedResearch) == STATE_KNOWN
                    && isIn(mouseX, mouseY, left + 174 + DETAIL_LEFT_ARROW_X, top + DETAIL_ARROW_Y,
                    left + 174 + DETAIL_LEFT_ARROW_X + DETAIL_ARROW_SIZE, top + DETAIL_ARROW_Y + DETAIL_ARROW_SIZE)) {
                if (researchDetailPage > 0) {
                    researchDetailPage--;
                    playPageSound();
                    return;
                }
            } else if (view == VIEW_RESEARCH && selectedResearch != null && getResearchState(selectedResearch) == STATE_KNOWN
                    && isIn(mouseX, mouseY, left + 174 + DETAIL_RIGHT_ARROW_X, top + DETAIL_ARROW_Y,
                    left + 174 + DETAIL_RIGHT_ARROW_X + DETAIL_ARROW_SIZE, top + DETAIL_ARROW_Y + DETAIL_ARROW_SIZE)) {
                if (researchDetailPage < getResearchDetailPageCount(selectedResearch) - 1) {
                    researchDetailPage++;
                    playPageSound();
                    return;
                }
            }
            Research clicked = getClickedResearch(mouseX, mouseY, left, top);
            if (clicked != null) {
                if (selectedResearch != clicked) {
                    playPageSound();
                }
                selectedResearch = clicked;
                researchDetailPage = DETAIL_PAGE_TEXT;
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel == 0 || view != VIEW_CRUCIBLE_RECIPES) {
            return;
        }
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        int left = (width - WIDTH) / 2;
        int top = (height - HEIGHT) / 2;
        if (!isIn(mouseX, mouseY, left + RIGHT_PAGE_X, top + PAGE_Y,
                left + RIGHT_PAGE_X + PAGE_WIDTH, top + PAGE_Y + PAGE_HEIGHT)) {
            return;
        }
        if (scrollCrucibleSteps(wheel < 0 ? 1 : -1)) {
            playPageSound();
        }
    }

    private boolean handleCrucibleRecipeScrollClick(int mouseX, int mouseY, int left, int top) {
        if (view != VIEW_CRUCIBLE_RECIPES || getMaxCrucibleStepScroll(getSelectedCrucibleStepCount()) <= 0) {
            return false;
        }
        int pageX = left + RIGHT_PAGE_X;
        int pageY = top + PAGE_Y;
        if (isIn(mouseX, mouseY, pageX + CRUCIBLE_SCROLL_X, pageY + CRUCIBLE_SCROLL_UP_Y,
                pageX + CRUCIBLE_SCROLL_X + CRUCIBLE_SCROLL_BUTTON_SIZE,
                pageY + CRUCIBLE_SCROLL_UP_Y + CRUCIBLE_SCROLL_BUTTON_SIZE)) {
            if (scrollCrucibleSteps(-1)) {
                playPageSound();
            }
            return true;
        }
        if (isIn(mouseX, mouseY, pageX + CRUCIBLE_SCROLL_X, pageY + CRUCIBLE_SCROLL_DOWN_Y,
                pageX + CRUCIBLE_SCROLL_X + CRUCIBLE_SCROLL_BUTTON_SIZE,
                pageY + CRUCIBLE_SCROLL_DOWN_Y + CRUCIBLE_SCROLL_BUTTON_SIZE)) {
            if (scrollCrucibleSteps(1)) {
                playPageSound();
            }
            return true;
        }
        return false;
    }

    private boolean scrollCrucibleSteps(int amount) {
        int oldScroll = crucibleStepScroll;
        crucibleStepScroll = clampCrucibleStepScroll(crucibleStepScroll + amount, getSelectedCrucibleStepCount());
        return oldScroll != crucibleStepScroll;
    }

    private boolean handleGuideClick(int mouseX, int mouseY, int left, int top) {
        List<Category> categories = CodexChapters.getCategories();
        if (categories.isEmpty()) {
            return false;
        }
        int clickedCategory = getClickedGuideCategoryIndex(mouseX, mouseY, left, top, categories.size());
        if (clickedCategory >= 0) {
            if (guideCategoryIndex != clickedCategory) {
                guideCategoryIndex = clickedCategory;
                guideChapterListPage = 0;
                selectedGuideChapterIndex = 0;
                guidePageIndex = 0;
                playPageSound();
            }
            return true;
        }
        Category category = categories.get(clampGuideCategoryIndex(guideCategoryIndex, categories.size()));
        List<Chapter> chapters = category.getChapters();
        if (isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
            if (guideChapterListPage > 0) {
                guideChapterListPage--;
                playPageSound();
            }
            return true;
        }
        if (isIn(mouseX, mouseY, left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                left + LEFT_PAGE_RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
            if (guideChapterListPage < getMaxGuideChapterListPage(chapters.size())) {
                guideChapterListPage++;
                playPageSound();
            }
            return true;
        }
        int clickedChapter = getClickedGuideChapterIndex(mouseX, mouseY, left, top, chapters.size());
        if (clickedChapter >= 0) {
            if (selectedGuideChapterIndex != clickedChapter) {
                selectedGuideChapterIndex = clickedChapter;
                guidePageIndex = 0;
                playPageSound();
            }
            return true;
        }
        Chapter chapter = chapters.isEmpty() ? null : chapters.get(clampSelectedGuideChapterIndex(selectedGuideChapterIndex, chapters.size()));
        int pageCount = chapter == null ? 0 : chapter.getPages().size();
        int pageX = left + RIGHT_PAGE_X;
        int pageY = top + PAGE_Y;
        if (isIn(mouseX, mouseY, pageX + DETAIL_LEFT_ARROW_X, pageY + GUIDE_DETAIL_ARROW_Y,
                pageX + DETAIL_LEFT_ARROW_X + DETAIL_ARROW_SIZE, pageY + GUIDE_DETAIL_ARROW_Y + DETAIL_ARROW_SIZE)) {
            if (guidePageIndex > 0) {
                guidePageIndex--;
                playPageSound();
            }
            return true;
        }
        if (isIn(mouseX, mouseY, pageX + DETAIL_RIGHT_ARROW_X, pageY + GUIDE_DETAIL_ARROW_Y,
                pageX + DETAIL_RIGHT_ARROW_X + DETAIL_ARROW_SIZE, pageY + GUIDE_DETAIL_ARROW_Y + DETAIL_ARROW_SIZE)) {
            if (guidePageIndex < pageCount - 1) {
                guidePageIndex++;
                playPageSound();
            }
            return true;
        }
        return false;
    }

    private int getSelectedCrucibleStepCount() {
        List<CrucibleRecipe> recipes = CrucibleRecipes.getRecipes();
        if (selectedRecipeIndex < 0 || selectedRecipeIndex >= recipes.size()) {
            return 0;
        }
        return recipes.get(selectedRecipeIndex).getSteps().size();
    }

    private int clampCrucibleStepScroll(int value, int stepCount) {
        return Math.max(0, Math.min(value, getMaxCrucibleStepScroll(stepCount)));
    }

    private int getMaxCrucibleStepScroll(int stepCount) {
        return Math.max(0, stepCount - CRUCIBLE_VISIBLE_STEPS);
    }

    private IndexEntry getClickedCategoryTab(int mouseX, int mouseY, int left, int top) {
        List<IndexEntry> entries = getIndexEntries();
        for (int i = 0; i < entries.size(); i++) {
            IndexEntry entry = entries.get(i);
            int tabX = getCategoryTabX(left, i);
            int tabY = getCategoryTabY(top, i);
            if (isIn(mouseX, mouseY, tabX, tabY, tabX + CATEGORY_TAB_WIDTH, tabY + CATEGORY_TAB_HEIGHT)) {
                return entry;
            }
        }
        return null;
    }

    private Research getClickedResearch(int mouseX, int mouseY, int left, int top) {
        if (mouseX < left + 26 || mouseX > left + 26 + LIST_WIDTH) {
            return null;
        }
        int relativeY = mouseY - (top + LIST_START_Y);
        if (relativeY < 0) {
            return null;
        }
        int index = relativeY / LIST_LINE_HEIGHT;
        if (index < 0 || index >= getMaxVisibleEntries()) {
            return null;
        }
        List<Research> researches = getVisibleResearches();
        int absoluteIndex = researchListPage * getMaxVisibleEntries() + index;
        return absoluteIndex < researches.size() ? researches.get(absoluteIndex) : null;
    }

    private int getClickedRecipeIndex(int mouseX, int mouseY, int left, int top) {
        if (mouseX < left + 26 || mouseX > left + 26 + LIST_WIDTH) {
            return -1;
        }
        int relativeY = mouseY - (top + LIST_START_Y);
        if (relativeY < 0) {
            return -1;
        }
        int index = relativeY / LIST_LINE_HEIGHT;
        if (index < 0 || index >= getMaxVisibleEntries()) {
            return -1;
        }
        int absoluteIndex = recipeListPage * getMaxVisibleEntries() + index;
        return absoluteIndex < getRecipeOutputs().size() ? absoluteIndex : -1;
    }

    private int getClickedLoreIndex(int mouseX, int mouseY, int left, int top) {
        if (mouseX < left + 26 || mouseX > left + 26 + LIST_WIDTH) {
            return -1;
        }
        int relativeY = mouseY - (top + LIST_START_Y);
        if (relativeY < 0) {
            return -1;
        }
        int index = relativeY / LIST_LINE_HEIGHT;
        if (index < 0 || index >= getMaxVisibleEntries()) {
            return -1;
        }
        int absoluteIndex = loreListPage * getMaxVisibleEntries() + index;
        return absoluteIndex < getLoreEntries().size() ? absoluteIndex : -1;
    }

    private int getClickedGuideCategoryIndex(int mouseX, int mouseY, int left, int top, int categoryCount) {
        int x = left + 26;
        int y = top + GUIDE_CATEGORY_Y;
        for (int i = 0; i < categoryCount; i++) {
            int iconX = x + i * GUIDE_CATEGORY_SPACING;
            if (isIn(mouseX, mouseY, iconX, y, iconX + GUIDE_CATEGORY_SIZE, y + GUIDE_CATEGORY_SIZE)) {
                return i;
            }
        }
        return -1;
    }

    private int getClickedGuideChapterIndex(int mouseX, int mouseY, int left, int top, int chapterCount) {
        if (mouseX < left + 26 || mouseX > left + 26 + LIST_WIDTH) {
            return -1;
        }
        int relativeY = mouseY - (top + GUIDE_CHAPTER_START_Y);
        if (relativeY < 0) {
            return -1;
        }
        int index = relativeY / LIST_LINE_HEIGHT;
        if (index < 0 || index >= GUIDE_VISIBLE_CHAPTERS) {
            return -1;
        }
        int absoluteIndex = guideChapterListPage * GUIDE_VISIBLE_CHAPTERS + index;
        return absoluteIndex < chapterCount ? absoluteIndex : -1;
    }

    private IndexEntry getClickedIndexEntry(int mouseX, int mouseY, int left, int top) {
        List<IndexEntry> entries = getIndexEntries();
        int start = indexPage * INDEX_ENTRIES_PER_SPREAD;
        int leftIndex = getClickedIndexEntryOnPage(mouseX, mouseY, left + LEFT_PAGE_X, top + INDEX_LEFT_START_Y);
        if (leftIndex >= 0) {
            int absoluteIndex = start + leftIndex;
            return absoluteIndex < entries.size() ? entries.get(absoluteIndex) : null;
        }
        int rightIndex = getClickedIndexEntryOnPage(mouseX, mouseY, left + RIGHT_PAGE_X, top + INDEX_RIGHT_START_Y);
        if (rightIndex >= 0) {
            int absoluteIndex = start + INDEX_VISIBLE_ENTRIES + rightIndex;
            return absoluteIndex < entries.size() ? entries.get(absoluteIndex) : null;
        }
        return null;
    }

    private int getClickedIndexEntryOnPage(int mouseX, int mouseY, int pageX, int startY) {
        if (mouseX < pageX + 1 || mouseX > pageX + 1 + LIST_WIDTH) {
            return -1;
        }
        int relativeY = mouseY - (startY - 5);
        if (relativeY < 0) {
            return -1;
        }
        int index = relativeY / LIST_LINE_HEIGHT;
        if (index < 0 || index >= INDEX_VISIBLE_ENTRIES) {
            return -1;
        }
        int rowY = startY - 5 + index * LIST_LINE_HEIGHT;
        return mouseY <= rowY + 18 ? index : -1;
    }

    private boolean isIn(int mouseX, int mouseY, int left, int top, int right, int bottom) {
        return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
    }

    private int getMaxVisibleEntries() {
        return (HEIGHT - LIST_START_Y - 28) / LIST_LINE_HEIGHT;
    }

    private int getMaxResearchListPage(int totalEntries) {
        int entriesPerPage = getMaxVisibleEntries();
        return entriesPerPage <= 0 ? 0 : Math.max(0, (totalEntries - 1) / entriesPerPage);
    }

    private int getMaxRecipeListPage(int totalEntries) {
        int entriesPerPage = getMaxVisibleEntries();
        return entriesPerPage <= 0 ? 0 : Math.max(0, (totalEntries - 1) / entriesPerPage);
    }

    private int getMaxLoreListPage(int totalEntries) {
        int entriesPerPage = getMaxVisibleEntries();
        return entriesPerPage <= 0 ? 0 : Math.max(0, (totalEntries - 1) / entriesPerPage);
    }

    private int getMaxGuideChapterListPage(int totalEntries) {
        return GUIDE_VISIBLE_CHAPTERS <= 0 ? 0 : Math.max(0, (totalEntries - 1) / GUIDE_VISIBLE_CHAPTERS);
    }

    private int getMaxIndexPage(int totalEntries) {
        int entriesPerPage = INDEX_ENTRIES_PER_SPREAD;
        return entriesPerPage <= 0 ? 0 : Math.max(0, (totalEntries - 1) / entriesPerPage);
    }

    private int clampResearchListPage(int value, int totalEntries) {
        return Math.max(0, Math.min(value, getMaxResearchListPage(totalEntries)));
    }

    private int clampRecipeListPage(int value, int totalEntries) {
        return Math.max(0, Math.min(value, getMaxRecipeListPage(totalEntries)));
    }

    private int clampLoreListPage(int value, int totalEntries) {
        return Math.max(0, Math.min(value, getMaxLoreListPage(totalEntries)));
    }

    private int clampGuideCategoryIndex(int value, int totalCategories) {
        return totalCategories <= 0 ? 0 : Math.max(0, Math.min(value, totalCategories - 1));
    }

    private int clampGuideChapterListPage(int value, int totalEntries) {
        return Math.max(0, Math.min(value, getMaxGuideChapterListPage(totalEntries)));
    }

    private int clampSelectedRecipeIndex(int value, int totalEntries) {
        return totalEntries <= 0 ? 0 : Math.max(0, Math.min(value, totalEntries - 1));
    }

    private int clampSelectedLoreIndex(int value, int totalEntries) {
        return totalEntries <= 0 ? 0 : Math.max(0, Math.min(value, totalEntries - 1));
    }

    private int clampSelectedGuideChapterIndex(int value, int totalEntries) {
        return totalEntries <= 0 ? 0 : Math.max(0, Math.min(value, totalEntries - 1));
    }

    private int clampGuidePageIndex(int value, int totalPages) {
        return totalPages <= 0 ? 0 : Math.max(0, Math.min(value, totalPages - 1));
    }

    private void resetGuideSelection() {
        guideCategoryIndex = 0;
        guideChapterListPage = 0;
        selectedGuideChapterIndex = 0;
        guidePageIndex = 0;
    }

    private int clampResearchDetailPage(int value) {
        return Math.max(0, Math.min(value, getResearchDetailPageCount(selectedResearch) - 1));
    }

    private int getResearchDetailPageCount(Research research) {
        return DETAIL_PAGE_COUNT;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
            mc.player.closeScreen();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static final class IndexEntry {
        private final int view;
        private final String titleKey;
        private final String descriptionKey;
        private final ItemStack icon;
        private final int color;

        private IndexEntry(int view, String titleKey, String descriptionKey, ItemStack icon, int color) {
            this.view = view;
            this.titleKey = titleKey;
            this.descriptionKey = descriptionKey;
            this.icon = icon;
            this.color = color;
        }
    }

    private static final class LoreEntry {
        private static final int KIND_SIGN = 0;
        private static final int KIND_RUNE = 1;
        private static final int KIND_CHANT = 2;

        private final int kind;
        private final String title;
        private final String description;
        private final Spell spell;
        private final Sign sign;
        private final Rune rune;
        private final List<Sign> signs;
        private final boolean unlocked;

        private LoreEntry(int kind, String title, String description, Spell spell, Sign sign, Rune rune, List<Sign> signs,
                          boolean unlocked) {
            this.kind = kind;
            this.title = title;
            this.description = description;
            this.spell = spell;
            this.sign = sign;
            this.rune = rune;
            this.signs = signs == null ? Collections.emptyList() : signs;
            this.unlocked = unlocked;
        }

        private static LoreEntry sign(Sign sign, String title, String description, boolean unlocked) {
            return new LoreEntry(KIND_SIGN, title, description, null, sign, null, Collections.emptyList(), unlocked);
        }

        private static LoreEntry rune(Rune rune, String title, String description, boolean unlocked) {
            return new LoreEntry(KIND_RUNE, title, description, null, null, rune, Collections.emptyList(), unlocked);
        }

        private static LoreEntry chant(Spell spell, String title, String description, List<Sign> signs, boolean unlocked) {
            return new LoreEntry(KIND_CHANT, title, description, spell, null, null, signs, unlocked);
        }

    }
}
