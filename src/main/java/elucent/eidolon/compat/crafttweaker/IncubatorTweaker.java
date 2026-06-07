package elucent.eidolon.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import elucent.eidolon.recipes.IncubatorRecipe;
import elucent.eidolon.recipes.IncubatorRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.eidolon.Incubator")
public final class IncubatorTweaker {
    private IncubatorTweaker() {
    }

    @ZenMethod
    public static void addRecipe(String id, IIngredient input, IIngredient catalyst, IItemStack output,
                                 @Optional int ticks) {
        ResourceLocation recipeId = TweakerUtil.id(id);
        Ingredient convertedInput = TweakerUtil.ingredient(input);
        Ingredient convertedCatalyst = TweakerUtil.ingredient(catalyst);
        ItemStack result = TweakerUtil.stack(output);
        int recipeTicks = ticks <= 0 ? IncubatorRecipe.DEFAULT_TICKS : ticks;
        CraftTweakerAPI.apply(new NamedAction("Adding Eidolon Incubator recipe " + recipeId) {
            @Override
            public void apply() {
                IncubatorRecipes.addRecipe(recipeId, convertedInput, convertedCatalyst, result, recipeTicks);
            }
        });
    }

    @ZenMethod
    public static void removeById(String id) {
        ResourceLocation recipeId = TweakerUtil.id(id);
        CraftTweakerAPI.apply(new NamedAction("Removing Eidolon Incubator recipe " + recipeId) {
            @Override
            public void apply() {
                IncubatorRecipes.removeRecipe(recipeId);
            }
        });
    }

    @ZenMethod
    public static void removeByOutput(IIngredient output) {
        Ingredient ingredient = TweakerUtil.ingredient(output);
        CraftTweakerAPI.apply(new NamedAction("Removing Eidolon Incubator recipes with output " + output.toCommandString()) {
            @Override
            public void apply() {
                IncubatorRecipes.removeRecipesByOutput(ingredient);
            }
        });
    }

    @ZenMethod
    public static void removeAll() {
        CraftTweakerAPI.apply(new NamedAction("Removing all Eidolon Incubator recipes") {
            @Override
            public void apply() {
                IncubatorRecipes.removeAllRecipes();
            }
        });
    }
}
