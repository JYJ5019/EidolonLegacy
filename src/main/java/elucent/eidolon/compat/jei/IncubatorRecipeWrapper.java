package elucent.eidolon.compat.jei;

import elucent.eidolon.recipes.IncubatorRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IncubatorRecipeWrapper implements IRecipeWrapper {
    private final IncubatorRecipe recipe;

    public IncubatorRecipeWrapper(IncubatorRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputs = new ArrayList<>();
        inputs.add(stacksFor(recipe.getInput()));
        inputs.add(stacksFor(recipe.getCatalyst()));
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResult());
    }

    public List<ItemStack> getInputStacks() {
        return stacksFor(recipe.getInput());
    }

    public List<ItemStack> getCatalystStacks() {
        return stacksFor(recipe.getCatalyst());
    }

    public ItemStack getResult() {
        return recipe.getResult();
    }

    public int getTicks() {
        return recipe.getTicks();
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString(I18n.format("gui.eidolon.incubator.ticks", recipe.getTicks()), 8, 92, 0x5b4732);
    }

    private List<ItemStack> stacksFor(Ingredient ingredient) {
        if (ingredient == Ingredient.EMPTY) {
            return Collections.emptyList();
        }
        return Arrays.asList(ingredient.getMatchingStacks());
    }
}
