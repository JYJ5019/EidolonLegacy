package elucent.eidolon.compat.jei;

import elucent.eidolon.Reference;
import elucent.eidolon.recipes.CrucibleRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class CrucibleRecipeCategory implements IRecipeCategory<CrucibleRecipeWrapper> {
    public static final String UID = Reference.MOD_ID + ".crucible";
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/codex_crucible_page.png");
    static final int PAGE_WIDTH = 128;
    static final int PAGE_HEIGHT = 160;
    static final int STEP_ROW_HEIGHT = 20;
    static final int VISIBLE_STEPS = 5;
    static final int STEP_START_Y = 30;
    static final int FLUID_Y = 6;
    static final int RESULT_Y = 128;
    static final int SCROLL_X = 96;
    static final int SCROLL_UP_Y = 135;
    static final int SCROLL_DOWN_Y = 149;
    static final int SCROLL_BUTTON_SIZE = 12;
    static final int COMPACT_ITEM_SIZE = 12;
    static final int INPUT_START_X = 22;
    static final int INPUT_SPACING = 14;
    static final int MAX_VISIBLE_INPUTS = 6;
    static final int STIRRER_X = 106;

    private final IDrawable background;

    public CrucibleRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND, 0, 0, PAGE_WIDTH, PAGE_HEIGHT);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return I18n.format("tile.eidolon.crucible.name");
    }

    @Override
    public String getModName() {
        return Reference.MOD_NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CrucibleRecipeWrapper recipeWrapper, IIngredients ingredients) {
        int slot = 0;
        recipeLayout.getItemStacks().init(slot, true, 24, FLUID_Y);
        setIfPresent(recipeLayout, slot, recipeWrapper.getFluidStacks());
        slot++;
        recipeLayout.getItemStacks().init(slot, false, 56, RESULT_Y + 11);
        recipeLayout.getItemStacks().set(slot, recipeWrapper.getRecipe().getResult());
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return java.util.Collections.emptyList();
    }

    private void setIfPresent(IRecipeLayout recipeLayout, int slot, List<ItemStack> stacks) {
        if (!stacks.isEmpty()) {
            recipeLayout.getItemStacks().set(slot, stacks);
        }
    }

    static int getMaxStepScroll(int stepCount) {
        return Math.max(0, stepCount - VISIBLE_STEPS);
    }
}
