package elucent.eidolon.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import elucent.eidolon.recipes.CrucibleRecipe;
import elucent.eidolon.recipes.CrucibleRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.eidolon.Crucible")
public final class CrucibleTweaker {
    private static final int MAX_INPUTS_PER_STEP = 6;

    private CrucibleTweaker() {
    }

    @ZenMethod
    public static void addRecipe(String id, IItemStack output, int[] stirs, IIngredient[][] steps,
                                 @Optional IIngredient stirrer, @Optional ILiquidStack fluid) {
        ResourceLocation recipeId = TweakerUtil.id(id);
        ItemStack result = TweakerUtil.stack(output);
        Ingredient convertedStirrer = stirrer == null ? CrucibleRecipe.defaultStirrer() : TweakerUtil.ingredient(stirrer);
        FluidStack convertedFluid = TweakerUtil.fluid(fluid, CrucibleRecipe.defaultFluid());
        List<CrucibleRecipe.Step> convertedSteps = convertSteps(stirs, steps);
        CraftTweakerAPI.apply(new NamedAction("Adding Eidolon Crucible recipe " + recipeId) {
            @Override
            public void apply() {
                CrucibleRecipes.addRecipe(recipeId, result, convertedStirrer, convertedFluid, convertedSteps);
            }
        });
    }

    @ZenMethod
    public static void removeById(String id) {
        ResourceLocation recipeId = TweakerUtil.id(id);
        CraftTweakerAPI.apply(new NamedAction("Removing Eidolon Crucible recipe " + recipeId) {
            @Override
            public void apply() {
                CrucibleRecipes.removeRecipe(recipeId);
            }
        });
    }

    @ZenMethod
    public static void removeByOutput(IIngredient output) {
        Ingredient ingredient = TweakerUtil.ingredient(output);
        CraftTweakerAPI.apply(new NamedAction("Removing Eidolon Crucible recipes with output " + output.toCommandString()) {
            @Override
            public void apply() {
                CrucibleRecipes.removeRecipesByOutput(ingredient);
            }
        });
    }

    @ZenMethod
    public static void removeAll() {
        CraftTweakerAPI.apply(new NamedAction("Removing all Eidolon Crucible recipes") {
            @Override
            public void apply() {
                CrucibleRecipes.removeAllRecipes();
            }
        });
    }

    private static List<CrucibleRecipe.Step> convertSteps(int[] stirs, IIngredient[][] steps) {
        if (stirs == null || steps == null || stirs.length != steps.length) {
            throw new IllegalArgumentException("Crucible stirs and steps must have the same length");
        }
        List<CrucibleRecipe.Step> converted = new ArrayList<>();
        for (int i = 0; i < steps.length; i++) {
            if (steps[i] == null) {
                throw new IllegalArgumentException("Crucible step " + (i + 1) + " must not be null");
            }
            if (steps[i].length > MAX_INPUTS_PER_STEP) {
                throw new IllegalArgumentException("Crucible step " + (i + 1) + " has " + steps[i].length
                        + " inputs, but CraftTweaker crucible recipes support at most "
                        + MAX_INPUTS_PER_STEP + " inputs per step");
            }
            List<Ingredient> ingredients = new ArrayList<>();
            for (IIngredient ingredient : steps[i]) {
                ingredients.add(TweakerUtil.ingredient(ingredient));
            }
            converted.add(CrucibleRecipes.makeStep(stirs[i], ingredients));
        }
        return converted;
    }
}
