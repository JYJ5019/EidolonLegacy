package elucent.eidolon.compat.jei;

import elucent.eidolon.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.util.List;

public class IncubatorRecipeCategory implements IRecipeCategory<IncubatorRecipeWrapper> {
    public static final String UID = Reference.MOD_ID + ".incubator";

    private final IDrawable background;

    public IncubatorRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(176, 112);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return I18n.format("tile.eidolon.incubator.name");
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
    public void setRecipe(IRecipeLayout recipeLayout, IncubatorRecipeWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 44, 35);
        setIfPresent(recipeLayout, 0, recipeWrapper.getInputStacks());
        recipeLayout.getItemStacks().init(1, true, 44, 59);
        setIfPresent(recipeLayout, 1, recipeWrapper.getCatalystStacks());
        recipeLayout.getItemStacks().init(2, false, 116, 47);
        recipeLayout.getItemStacks().set(2, recipeWrapper.getResult());
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        drawPanel();
        drawSlot(43, 34);
        drawSlot(43, 58);
        drawSlot(115, 46);
        drawProgress(70, 50);
        minecraft.fontRenderer.drawString(I18n.format("tile.eidolon.incubator.name"), 8, 6, 0x3f2a1d);
    }

    private void drawPanel() {
        Gui.drawRect(0, 0, 176, 88, 0xffc6c6c6);
        Gui.drawRect(1, 1, 175, 2, 0xffffffff);
        Gui.drawRect(1, 1, 2, 87, 0xffffffff);
        Gui.drawRect(1, 86, 175, 87, 0xff555555);
        Gui.drawRect(174, 1, 175, 87, 0xff555555);
    }

    private void drawProgress(int x, int y) {
        Gui.drawRect(x, y, x + 34, y + 8, 0xff555555);
        Gui.drawRect(x + 1, y + 1, x + 33, y + 7, 0xff8d8d8d);
        Gui.drawRect(x + 2, y + 2, x + 22, y + 6, 0xff4f8f6a);
    }

    private void drawSlot(int x, int y) {
        Gui.drawRect(x - 1, y - 1, x + 17, y + 17, 0xff555555);
        Gui.drawRect(x, y, x + 18, y + 18, 0xffffffff);
        Gui.drawRect(x, y, x + 17, y + 17, 0xff8b8b8b);
        Gui.drawRect(x + 1, y + 1, x + 17, y + 17, 0xffeeeeee);
    }

    private void setIfPresent(IRecipeLayout recipeLayout, int slot, List<ItemStack> stacks) {
        if (!stacks.isEmpty()) {
            recipeLayout.getItemStacks().set(slot, stacks);
        }
    }
}
