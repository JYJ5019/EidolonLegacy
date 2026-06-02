package elucent.eidolon.gui;

import elucent.eidolon.Reference;
import elucent.eidolon.compat.jei.SoulShardHarvestRecipe;
import elucent.eidolon.item.AthameItem;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModItems;
import elucent.eidolon.recipes.CrucibleRecipe;
import elucent.eidolon.recipes.CrucibleRecipes;
import elucent.eidolon.recipes.WorktableRecipe;
import elucent.eidolon.recipes.WorktableRecipes;
import elucent.eidolon.research.Research;
import elucent.eidolon.research.Researches;
import elucent.eidolon.spell.AltarEntries;
import elucent.eidolon.spell.AltarEntry;
import elucent.eidolon.spell.AltarRitual;
import elucent.eidolon.spell.AltarRituals;
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
    private IndexEntry hoveredCategoryTab;

    private final EntityPlayer player;
    private Research selectedResearch;
    private int researchListPage;
    private int researchDetailPage;
    private int recipeListPage;
    private int selectedRecipeIndex;
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
            drawIndexOverview(left, top);
            super.drawScreen(mouseX, mouseY, partialTicks);
            drawCategoryTabTooltip(mouseX, mouseY);
            return;
        }

        drawResearchPageBackgrounds(left, top);
        fontRenderer.drawString("< " + I18n.format("gui.eidolon.codex.back"), left + 26, top + 34, 0x5b4732);

        if (isRecipeView()) {
            drawRecipeChapter(left, top, mouseX, mouseY);
            super.drawScreen(mouseX, mouseY, partialTicks);
            drawCategoryTabTooltip(mouseX, mouseY);
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

        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCategoryTabTooltip(mouseX, mouseY);
    }

    private void drawCategoryTabs(int left, int top, int mouseX, int mouseY) {
        List<IndexEntry> entries = getIndexEntries();
        for (int i = 0; i < entries.size(); i++) {
            IndexEntry entry = entries.get(i);
            int tabX = getCategoryTabX(left, entry);
            int tabY = getCategoryTabY(top, i);
            boolean active = view == entry.view || (view == VIEW_INDEX && i == 0);
            boolean hovered = isIn(mouseX, mouseY, tabX, tabY, tabX + CATEGORY_TAB_WIDTH, tabY + CATEGORY_TAB_HEIGHT);
            if (hovered) {
                hoveredCategoryTab = entry;
            }
            drawCategoryTab(entry, tabX, tabY, active, hovered);
        }
    }

    private void drawCategoryTabTooltip(int mouseX, int mouseY) {
        if (hoveredCategoryTab != null) {
            drawHoveringText(Collections.singletonList(I18n.format(hoveredCategoryTab.titleKey)), mouseX, mouseY);
        }
    }

    private int getCategoryTabX(int left, IndexEntry entry) {
        return left + CATEGORY_TAB_X - 36;
    }

    private int getCategoryTabY(int top, int index) {
        return top + CATEGORY_TAB_Y + index * CATEGORY_TAB_SPACING;
    }

    private void drawCategoryTab(IndexEntry entry, int x, int y, boolean active, boolean hovered) {
        int color = active ? brighten(entry.color) : hovered ? soften(entry.color) : entry.color;
        GlStateManager.color(((color >> 16) & 255) / 255.0F,
                ((color >> 8) & 255) / 255.0F,
                (color & 255) / 255.0F,
                1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawModalRectWithCustomSizedTexture(x, y, 208, 227, CATEGORY_TAB_WIDTH, CATEGORY_TAB_HEIGHT, 512, 512);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawCategoryIcon(entry.icon, x + 9, y + 1);
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

    private void drawIndexOverview(int left, int top) {
        drawResearchPageBackgrounds(left, top);
        drawCenteredPageTitle(I18n.format("gui.eidolon.codex.title"), left + LEFT_PAGE_X, top + 38, 0x2f2118);
        fontRenderer.drawSplitString(I18n.format("gui.eidolon.codex.chapter_research_desc"),
                left + LEFT_PAGE_X + 8, top + 64, 112, 0x5b4732);
        fontRenderer.drawSplitString(I18n.format("gui.eidolon.codex.chapter_research_desc"),
                left + RIGHT_PAGE_X + 4, top + 58, 120, 0x5b4732);
    }

    private void drawIndex(int left, int top, int mouseX, int mouseY) {
        List<IndexEntry> entries = getIndexEntries();
        indexPage = Math.max(0, Math.min(indexPage, getMaxIndexPage(entries.size())));
        int maxVisible = getMaxVisibleEntries();
        int start = indexPage * maxVisible;
        for (int i = start; i < entries.size() && i < start + maxVisible; i++) {
            IndexEntry entry = entries.get(i);
            drawIndexEntry(left, top, i - start, I18n.format(entry.titleKey), entry.icon);
        }
        drawIndexPageArrows(left, top, mouseX, mouseY);
        fontRenderer.drawSplitString(I18n.format("gui.eidolon.codex.chapter_research_desc"), left + 174, top + 58, 118, 0x5b4732);
    }

    private List<IndexEntry> getIndexEntries() {
        List<IndexEntry> entries = new ArrayList<>();
        entries.add(new IndexEntry(VIEW_RESEARCH, "gui.eidolon.codex.chapter_research", new ItemStack(ModItems.RESEARCH_NOTES), 0x4f8f3a));
        entries.add(new IndexEntry(VIEW_WORKTABLE_RECIPES, "gui.eidolon.codex.chapter_worktable_recipes", new ItemStack(ModBlocks.WORKTABLE), 0xcaa23d));
        entries.add(new IndexEntry(VIEW_CRUCIBLE_RECIPES, "gui.eidolon.codex.chapter_crucible_recipes", new ItemStack(ModBlocks.CRUCIBLE), 0xa94747));
        entries.add(new IndexEntry(VIEW_ALTAR_RITUALS, "gui.eidolon.codex.chapter_altar_rituals", new ItemStack(ModBlocks.STONE_ALTAR), 0x5c5bb5));
        entries.add(new IndexEntry(VIEW_ALTAR_OFFERINGS, "gui.eidolon.codex.chapter_altar_offerings", new ItemStack(ModItems.OFFERTORY_PLATE), 0x8b52b5));
        entries.add(new IndexEntry(VIEW_ATHAME_HARVEST, "gui.eidolon.codex.chapter_athame_harvest", new ItemStack(ModItems.ATHAME), 0x6b58bd));
        entries.add(new IndexEntry(VIEW_SOUL_SHARD_HARVEST, "gui.eidolon.codex.chapter_soul_shard_harvest", new ItemStack(ModItems.SOUL_SHARD), 0x4057a5));
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

    private void drawIndexEntry(int left, int top, int index, String label, ItemStack icon) {
        int chapterY = top + 68 + index * LIST_LINE_HEIGHT;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(INDEX_PAGE);
        drawModalRectWithCustomSizedTexture(left + 24, chapterY - 5, 128, 0, 122, 18, 256, 256);
        itemRender.renderItemAndEffectIntoGUI(icon, left + 26, chapterY - 4);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        fontRenderer.drawString(label, left + 48, chapterY, 0x2f2118);
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
        }
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
        drawStack(ritual.getResult(), x + ALTAR_RITUAL_RESULT_X, y + ALTAR_RITUAL_RESULT_Y);
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
            drawHoveringText(Collections.singletonList(I18n.format("gui.eidolon.codex.altar_slot.result")), mouseX, mouseY);
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
                outputs.add(ritual.getResult());
            }
        } else if (view == VIEW_ALTAR_OFFERINGS) {
            outputs.addAll(getAltarOfferingStacks());
        } else if (view == VIEW_ATHAME_HARVEST) {
            for (AthameItem.HarvestEntry entry : AthameItem.getHarvestEntries()) {
                outputs.add(entry.getResult());
            }
        } else if (view == VIEW_SOUL_SHARD_HARVEST) {
            outputs.add(SoulShardHarvestRecipe.INSTANCE.getResult());
        }
        return outputs;
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
                || view == VIEW_ATHAME_HARVEST || view == VIEW_SOUL_SHARD_HARVEST;
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
        return I18n.format("gui.eidolon.codex.chapter_athame_harvest");
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

    private void drawPageArrow(int x, int y, int u, boolean hovered) {
        drawModalRectWithCustomSizedTexture(x, y, u, hovered ? 226 : 208,
                PAGE_ARROW_WIDTH, PAGE_ARROW_HEIGHT, 512, 512);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int left = (width - WIDTH) / 2;
            int top = (height - HEIGHT) / 2;
            IndexEntry clickedTab = getClickedCategoryTab(mouseX, mouseY, left, top);
            if (clickedTab != null) {
                view = clickedTab.view;
                recipeListPage = 0;
                researchListPage = 0;
                selectedRecipeIndex = 0;
                crucibleStepScroll = 0;
                return;
            }
            if (view == VIEW_INDEX) {
                if (isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                        left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                    if (indexPage > 0) {
                        indexPage--;
                    }
                    return;
                }
                if (isIn(mouseX, mouseY, left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                        left + LEFT_PAGE_RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                    if (indexPage < getMaxIndexPage(getIndexEntries().size())) {
                        indexPage++;
                    }
                    return;
                }
                IndexEntry clickedEntry = getClickedIndexEntry(mouseX, mouseY, left, top);
                if (clickedEntry != null) {
                    view = clickedEntry.view;
                    recipeListPage = 0;
                    selectedRecipeIndex = 0;
                    crucibleStepScroll = 0;
                    return;
                }
            } else if (isIn(mouseX, mouseY, left + 24, top + 32, left + 70, top + 44)) {
                view = VIEW_INDEX;
                return;
            } else if (isRecipeView()) {
                if (isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                        left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                    if (recipeListPage > 0) {
                        recipeListPage--;
                        crucibleStepScroll = 0;
                        return;
                    }
                } else if (isIn(mouseX, mouseY, left + LEFT_PAGE_RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                        left + LEFT_PAGE_RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                    int totalEntries = getRecipeOutputs().size();
                    if (recipeListPage < getMaxRecipeListPage(totalEntries)) {
                        recipeListPage++;
                        crucibleStepScroll = 0;
                        return;
                    }
                }
                if (handleCrucibleRecipeScrollClick(mouseX, mouseY, left, top)) {
                    return;
                }
                int clicked = getClickedRecipeIndex(mouseX, mouseY, left, top);
                if (clicked >= 0) {
                    selectedRecipeIndex = clicked;
                    crucibleStepScroll = 0;
                    return;
                }
                return;
            } else if (view == VIEW_RESEARCH && isIn(mouseX, mouseY, left + LEFT_ARROW_X, top + PAGE_ARROW_Y,
                    left + LEFT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                if (researchListPage > 0) {
                    researchListPage--;
                    return;
                }
            } else if (view == VIEW_RESEARCH && isIn(mouseX, mouseY, left + RIGHT_ARROW_X, top + PAGE_ARROW_Y,
                    left + RIGHT_ARROW_X + PAGE_ARROW_WIDTH, top + PAGE_ARROW_Y + PAGE_ARROW_HEIGHT)) {
                List<Research> researches = getVisibleResearches();
                if (researchListPage < getMaxResearchListPage(researches.size())) {
                    researchListPage++;
                    return;
                }
            } else if (view == VIEW_RESEARCH && selectedResearch != null && getResearchState(selectedResearch) == STATE_KNOWN
                    && isIn(mouseX, mouseY, left + 174 + DETAIL_LEFT_ARROW_X, top + DETAIL_ARROW_Y,
                    left + 174 + DETAIL_LEFT_ARROW_X + DETAIL_ARROW_SIZE, top + DETAIL_ARROW_Y + DETAIL_ARROW_SIZE)) {
                if (researchDetailPage > 0) {
                    researchDetailPage--;
                    return;
                }
            } else if (view == VIEW_RESEARCH && selectedResearch != null && getResearchState(selectedResearch) == STATE_KNOWN
                    && isIn(mouseX, mouseY, left + 174 + DETAIL_RIGHT_ARROW_X, top + DETAIL_ARROW_Y,
                    left + 174 + DETAIL_RIGHT_ARROW_X + DETAIL_ARROW_SIZE, top + DETAIL_ARROW_Y + DETAIL_ARROW_SIZE)) {
                if (researchDetailPage < getResearchDetailPageCount(selectedResearch) - 1) {
                    researchDetailPage++;
                    return;
                }
            }
            Research clicked = getClickedResearch(mouseX, mouseY, left, top);
            if (clicked != null) {
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
        scrollCrucibleSteps(wheel < 0 ? 1 : -1);
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
            scrollCrucibleSteps(-1);
            return true;
        }
        if (isIn(mouseX, mouseY, pageX + CRUCIBLE_SCROLL_X, pageY + CRUCIBLE_SCROLL_DOWN_Y,
                pageX + CRUCIBLE_SCROLL_X + CRUCIBLE_SCROLL_BUTTON_SIZE,
                pageY + CRUCIBLE_SCROLL_DOWN_Y + CRUCIBLE_SCROLL_BUTTON_SIZE)) {
            scrollCrucibleSteps(1);
            return true;
        }
        return false;
    }

    private void scrollCrucibleSteps(int amount) {
        crucibleStepScroll = clampCrucibleStepScroll(crucibleStepScroll + amount, getSelectedCrucibleStepCount());
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
            int tabX = getCategoryTabX(left, entry);
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

    private IndexEntry getClickedIndexEntry(int mouseX, int mouseY, int left, int top) {
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
        List<IndexEntry> entries = getIndexEntries();
        int absoluteIndex = indexPage * getMaxVisibleEntries() + index;
        return absoluteIndex < entries.size() ? entries.get(absoluteIndex) : null;
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

    private int getMaxIndexPage(int totalEntries) {
        int entriesPerPage = getMaxVisibleEntries();
        return entriesPerPage <= 0 ? 0 : Math.max(0, (totalEntries - 1) / entriesPerPage);
    }

    private int clampResearchListPage(int value, int totalEntries) {
        return Math.max(0, Math.min(value, getMaxResearchListPage(totalEntries)));
    }

    private int clampRecipeListPage(int value, int totalEntries) {
        return Math.max(0, Math.min(value, getMaxRecipeListPage(totalEntries)));
    }

    private int clampSelectedRecipeIndex(int value, int totalEntries) {
        return totalEntries <= 0 ? 0 : Math.max(0, Math.min(value, totalEntries - 1));
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
        private final ItemStack icon;
        private final int color;

        private IndexEntry(int view, String titleKey, ItemStack icon, int color) {
            this.view = view;
            this.titleKey = titleKey;
            this.icon = icon;
            this.color = color;
        }
    }
}
