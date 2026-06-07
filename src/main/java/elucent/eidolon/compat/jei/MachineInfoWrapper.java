package elucent.eidolon.compat.jei;

import elucent.eidolon.recipes.MachineInfoRecipe;
import elucent.eidolon.Reference;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class MachineInfoWrapper implements IRecipeWrapper {
    private static final ResourceLocation SOUL_ENCHANTER =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/soul_enchanter.png");

    private final MachineInfoRecipe recipe;

    public MachineInfoWrapper(MachineInfoRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getCatalyst(), recipe.getPrimaryInput(), recipe.getSecondaryInput()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    public ItemStack getCatalyst() {
        return recipe.getCatalyst();
    }

    public ItemStack getPrimaryInput() {
        return recipe.getPrimaryInput();
    }

    public ItemStack getSecondaryInput() {
        return recipe.getSecondaryInput();
    }

    public ItemStack getOutput() {
        return recipe.getOutput();
    }

    public boolean isSoulEnchanter() {
        return MachineInfoRecipe.SOUL_ENCHANTER_ID.equals(recipe.getId());
    }

    public boolean isWoodenBrewingStand() {
        return MachineInfoRecipe.WOODEN_BREWING_STAND_ID.equals(recipe.getId());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (isSoulEnchanter()) {
            drawSoulEnchanterInfo(minecraft);
        } else if (isWoodenBrewingStand()) {
            drawWoodenBrewingStandInfo(minecraft);
        }
        minecraft.fontRenderer.drawSplitString(I18n.format(recipe.getDescriptionKey()), 8, 88, 160, 0x5b4732);
    }

    private void drawSoulEnchanterInfo(Minecraft minecraft) {
        drawPanel();
        minecraft.fontRenderer.drawString(I18n.format(recipe.getTitleKey()), 8, 6, 0x3f2a1d);
        drawSlot(82, 16);
        drawSlot(22, 44);
        drawSlot(44, 44);
        drawSlot(136, 44);
        drawArrow(67, 51, 125);
        drawTierButtons(minecraft, 74, 63);
    }

    private void drawWoodenBrewingStandInfo(Minecraft minecraft) {
        drawPanel();
        minecraft.fontRenderer.drawString(I18n.format(recipe.getTitleKey()), 8, 6, 0x3f2a1d);
        drawSlot(18, 38);
        drawSlot(58, 56);
        drawSlot(86, 24);
        drawSlot(126, 56);
        drawArrow(78, 63, 116);
        drawBrewingPath();
    }

    private void drawPanel() {
        Gui.drawRect(0, 0, 176, 84, 0xffd4c2a7);
        Gui.drawRect(1, 1, 175, 2, 0xfff1e5cf);
        Gui.drawRect(1, 1, 2, 83, 0xfff1e5cf);
        Gui.drawRect(1, 82, 175, 83, 0xff6b5139);
        Gui.drawRect(174, 1, 175, 83, 0xff6b5139);
        Gui.drawRect(4, 18, 172, 80, 0x40ffffff);
    }

    private void drawSlot(int x, int y) {
        Gui.drawRect(x - 1, y - 1, x + 17, y + 17, 0xff5a4634);
        Gui.drawRect(x, y, x + 18, y + 18, 0xfff3e7d1);
        Gui.drawRect(x, y, x + 17, y + 17, 0xff9c8060);
        Gui.drawRect(x + 1, y + 1, x + 17, y + 17, 0xffead8b9);
    }

    private void drawArrow(int x, int y, int endX) {
        Gui.drawRect(x, y, endX, y + 2, 0xff6b5139);
        Gui.drawRect(endX - 4, y - 3, endX - 2, y + 5, 0xff6b5139);
        Gui.drawRect(endX - 2, y - 2, endX, y + 4, 0xff6b5139);
        Gui.drawRect(endX, y - 1, endX + 2, y + 3, 0xff6b5139);
    }

    private void drawTierButtons(Minecraft minecraft, int x, int y) {
        minecraft.getTextureManager().bindTexture(SOUL_ENCHANTER);
        for (int i = 0; i < 3; i++) {
            int buttonX = x + i * 22;
            Gui.drawRect(buttonX, y, buttonX + 20, y + 20, 0xff5a4634);
            Gui.drawRect(buttonX + 1, y + 1, buttonX + 19, y + 19, 0xffc8a675);
            Gui.drawModalRectWithCustomSizedTexture(buttonX + 2, y + 2, 16 * i, 223, 16, 16, 256, 256);
        }
    }

    private void drawBrewingPath() {
        drawHeatMarker(42, 61);
        Gui.drawRect(94, 43, 99, 45, 0xff6b5139);
        Gui.drawRect(96, 45, 98, 58, 0xff6b5139);
        Gui.drawRect(96, 57, 112, 59, 0xff6b5139);
    }

    private void drawHeatMarker(int x, int y) {
        Gui.drawRect(x + 1, y + 12, x + 15, y + 14, 0xff5a4634);
        Gui.drawRect(x + 4, y + 8, x + 6, y + 12, 0xff9f3f2e);
        Gui.drawRect(x + 6, y + 5, x + 9, y + 12, 0xffd36b2c);
        Gui.drawRect(x + 9, y + 8, x + 12, y + 12, 0xffe9a344);
        Gui.drawRect(x + 7, y + 9, x + 10, y + 13, 0xffffd37a);
    }
}
