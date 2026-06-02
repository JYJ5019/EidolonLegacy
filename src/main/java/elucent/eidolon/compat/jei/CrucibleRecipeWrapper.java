package elucent.eidolon.compat.jei;

import elucent.eidolon.Reference;
import elucent.eidolon.recipes.CrucibleRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CrucibleRecipeWrapper implements IRecipeWrapper {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/codex_crucible_page.png");

    private final CrucibleRecipe recipe;
    private int stepScroll;

    public CrucibleRecipeWrapper(CrucibleRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputs = new ArrayList<>();
        List<ItemStack> stirrerStacks = getStirrerStacks();
        if (!stirrerStacks.isEmpty()) {
            inputs.add(stirrerStacks);
        }
        inputs.add(getFluidStacks());
        for (CrucibleRecipe.Step step : recipe.getSteps()) {
            for (Ingredient ingredient : step.getIngredients()) {
                List<ItemStack> stacks = stacksFor(ingredient);
                if (!stacks.isEmpty()) {
                    inputs.add(stacks);
                }
            }
        }
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResult());
    }

    public CrucibleRecipe getRecipe() {
        return recipe;
    }

    public List<List<ItemStack>> getDisplayStacks(CrucibleRecipe.Step step) {
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

    public List<ItemStack> getStirrerStacks() {
        return stacksFor(recipe.getStirrer());
    }

    public List<ItemStack> getFluidStacks() {
        if (recipe.getFluid().getFluid() == FluidRegistry.WATER) {
            return Collections.singletonList(new ItemStack(Items.WATER_BUCKET));
        }
        if (recipe.getFluid().getFluid() == FluidRegistry.LAVA) {
            return Collections.singletonList(new ItemStack(Items.LAVA_BUCKET));
        }
        return Collections.singletonList(new ItemStack(Items.BUCKET));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        List<CrucibleRecipe.Step> steps = recipe.getSteps();
        stepScroll = clampStepScroll(stepScroll);
        int visibleEnd = Math.min(steps.size(), stepScroll + CrucibleRecipeCategory.VISIBLE_STEPS);
        for (int i = stepScroll; i < visibleEnd; i++) {
            CrucibleRecipe.Step step = steps.get(i);
            int y = CrucibleRecipeCategory.STEP_START_Y + (i - stepScroll) * CrucibleRecipeCategory.STEP_ROW_HEIGHT;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(BACKGROUND);
            Gui.drawModalRectWithCustomSizedTexture(0, y, 128, 0, 128, 20, 256, 256);
            int x = CrucibleRecipeCategory.INPUT_START_X;
            int inputs = 0;
            for (List<ItemStack> stacks : getDisplayStacks(step)) {
                if (inputs >= CrucibleRecipeCategory.MAX_VISIBLE_INPUTS) {
                    break;
                }
                renderCompactStack(minecraft, firstStack(stacks), x, y + 4);
                x += CrucibleRecipeCategory.INPUT_SPACING;
                inputs++;
            }
            if (step.getStirs() > 0) {
                renderCompactStack(minecraft, firstStack(getStirrerStacks()), CrucibleRecipeCategory.STIRRER_X, y + 4);
                minecraft.fontRenderer.drawString("x" + step.getStirs(), CrucibleRecipeCategory.STIRRER_X + 10, y + 7, 0x5b4732);
            }
            minecraft.fontRenderer.drawString((i + 1) + ".", 7, y + 7, 0x5b4732);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(BACKGROUND);
        Gui.drawModalRectWithCustomSizedTexture(0, CrucibleRecipeCategory.RESULT_Y, 128, 64, 128, 32, 256, 256);
        drawScrollControls(minecraft, steps.size());
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        ItemStack hovered = getHoveredStepStack(mouseX, mouseY);
        if (!hovered.isEmpty()) {
            return Collections.singletonList(hovered.getDisplayName());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0 || CrucibleRecipeCategory.getMaxStepScroll(recipe.getSteps().size()) <= 0) {
            return false;
        }
        if (isIn(mouseX, mouseY, CrucibleRecipeCategory.SCROLL_X, CrucibleRecipeCategory.SCROLL_UP_Y,
                CrucibleRecipeCategory.SCROLL_X + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE,
                CrucibleRecipeCategory.SCROLL_UP_Y + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE)) {
            stepScroll = clampStepScroll(stepScroll - 1);
            return true;
        }
        if (isIn(mouseX, mouseY, CrucibleRecipeCategory.SCROLL_X, CrucibleRecipeCategory.SCROLL_DOWN_Y,
                CrucibleRecipeCategory.SCROLL_X + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE,
                CrucibleRecipeCategory.SCROLL_DOWN_Y + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE)) {
            stepScroll = clampStepScroll(stepScroll + 1);
            return true;
        }
        return false;
    }

    private void drawScrollControls(Minecraft minecraft, int stepCount) {
        if (CrucibleRecipeCategory.getMaxStepScroll(stepCount) <= 0) {
            return;
        }
        drawScrollButton(minecraft, CrucibleRecipeCategory.SCROLL_X, CrucibleRecipeCategory.SCROLL_UP_Y, "^", stepScroll > 0);
        drawScrollButton(minecraft, CrucibleRecipeCategory.SCROLL_X, CrucibleRecipeCategory.SCROLL_DOWN_Y, "v",
                stepScroll < CrucibleRecipeCategory.getMaxStepScroll(stepCount));
    }

    private void drawScrollButton(Minecraft minecraft, int x, int y, String label, boolean enabled) {
        int fill = enabled ? 0xffd8ceb8 : 0xffb7af9e;
        int border = enabled ? 0xff5b4732 : 0xff8f8472;
        Gui.drawRect(x, y, x + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE, y + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE, fill);
        Gui.drawRect(x, y, x + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE, y + 1, border);
        Gui.drawRect(x, y + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE - 1,
                x + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE, y + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE, border);
        Gui.drawRect(x, y, x + 1, y + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE, border);
        Gui.drawRect(x + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE - 1, y,
                x + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE, y + CrucibleRecipeCategory.SCROLL_BUTTON_SIZE, border);
        minecraft.fontRenderer.drawString(label, x + 4, y + 2, enabled ? 0x2f2118 : 0x7d7366);
    }

    private void renderStack(Minecraft minecraft, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) {
            return;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        minecraft.getRenderItem().renderItemOverlayIntoGUI(minecraft.fontRenderer, stack, x, y, null);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderCompactStack(Minecraft minecraft, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0F);
        float scale = CrucibleRecipeCategory.COMPACT_ITEM_SIZE / 16.0F;
        GlStateManager.scale(scale, scale, 1.0F);
        renderStack(minecraft, stack, 0, 0);
        GlStateManager.popMatrix();
    }

    private ItemStack getHoveredStepStack(int mouseX, int mouseY) {
        List<CrucibleRecipe.Step> steps = recipe.getSteps();
        int visibleEnd = Math.min(steps.size(), stepScroll + CrucibleRecipeCategory.VISIBLE_STEPS);
        for (int i = stepScroll; i < visibleEnd; i++) {
            CrucibleRecipe.Step step = steps.get(i);
            int x = CrucibleRecipeCategory.INPUT_START_X;
            int y = CrucibleRecipeCategory.STEP_START_Y + (i - stepScroll) * CrucibleRecipeCategory.STEP_ROW_HEIGHT;
            int inputs = 0;
            for (List<ItemStack> stacks : getDisplayStacks(step)) {
                if (inputs >= CrucibleRecipeCategory.MAX_VISIBLE_INPUTS) {
                    break;
                }
                if (isIn(mouseX, mouseY, x, y + 4,
                        x + CrucibleRecipeCategory.COMPACT_ITEM_SIZE,
                        y + 4 + CrucibleRecipeCategory.COMPACT_ITEM_SIZE)) {
                    return firstStack(stacks);
                }
                x += CrucibleRecipeCategory.INPUT_SPACING;
                inputs++;
            }
            if (step.getStirs() > 0) {
                if (isIn(mouseX, mouseY, CrucibleRecipeCategory.STIRRER_X, y + 4,
                        CrucibleRecipeCategory.STIRRER_X + CrucibleRecipeCategory.COMPACT_ITEM_SIZE,
                        y + 4 + CrucibleRecipeCategory.COMPACT_ITEM_SIZE)) {
                    return firstStack(getStirrerStacks());
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private int clampStepScroll(int value) {
        return Math.max(0, Math.min(value, CrucibleRecipeCategory.getMaxStepScroll(recipe.getSteps().size())));
    }

    private ItemStack firstStack(List<ItemStack> stacks) {
        return stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0).copy();
    }

    private boolean isIn(int mouseX, int mouseY, int left, int top, int right, int bottom) {
        return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
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
}
