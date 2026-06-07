package elucent.eidolon.compat.jei;

import elucent.eidolon.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class MachineInfoCategory implements IRecipeCategory<MachineInfoWrapper> {
    public static final String UID = Reference.MOD_ID + ".machine_info";

    private final IDrawable background;

    public MachineInfoCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(176, 128);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return I18n.format("gui.eidolon.machine_info.title");
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
    public void setRecipe(IRecipeLayout recipeLayout, MachineInfoWrapper recipeWrapper, IIngredients ingredients) {
        if (recipeWrapper.isSoulEnchanter()) {
            recipeLayout.getItemStacks().init(0, true, 82, 16);
            recipeLayout.getItemStacks().set(0, recipeWrapper.getCatalyst());
            recipeLayout.getItemStacks().init(1, true, 22, 44);
            recipeLayout.getItemStacks().set(1, recipeWrapper.getPrimaryInput());
            recipeLayout.getItemStacks().init(2, true, 44, 44);
            recipeLayout.getItemStacks().set(2, recipeWrapper.getSecondaryInput());
            recipeLayout.getItemStacks().init(3, false, 136, 44);
            recipeLayout.getItemStacks().set(3, recipeWrapper.getOutput());
            return;
        }
        if (recipeWrapper.isWoodenBrewingStand()) {
            recipeLayout.getItemStacks().init(0, true, 18, 38);
            recipeLayout.getItemStacks().set(0, recipeWrapper.getCatalyst());
            recipeLayout.getItemStacks().init(1, true, 58, 56);
            recipeLayout.getItemStacks().set(1, recipeWrapper.getPrimaryInput());
            recipeLayout.getItemStacks().init(2, true, 86, 24);
            recipeLayout.getItemStacks().set(2, recipeWrapper.getSecondaryInput());
            recipeLayout.getItemStacks().init(3, false, 126, 56);
            recipeLayout.getItemStacks().set(3, recipeWrapper.getOutput());
            return;
        }
        recipeLayout.getItemStacks().init(0, true, 12, 34);
        recipeLayout.getItemStacks().set(0, recipeWrapper.getCatalyst());
        recipeLayout.getItemStacks().init(1, true, 39, 52);
        recipeLayout.getItemStacks().set(1, recipeWrapper.getPrimaryInput());
        recipeLayout.getItemStacks().init(2, true, 63, 52);
        recipeLayout.getItemStacks().set(2, recipeWrapper.getSecondaryInput());
        recipeLayout.getItemStacks().init(3, false, 100, 52);
        recipeLayout.getItemStacks().set(3, recipeWrapper.getOutput());
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
    }
}
