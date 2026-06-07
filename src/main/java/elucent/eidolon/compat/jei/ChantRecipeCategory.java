package elucent.eidolon.compat.jei;

import elucent.eidolon.Reference;
import elucent.eidolon.spell.Sign;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ChantRecipeCategory implements IRecipeCategory<ChantRecipeWrapper> {
    public static final String UID = Reference.MOD_ID + ".chant";

    private final IDrawable background;
    private ChantRecipeWrapper currentWrapper;

    public ChantRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(176, 150);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return I18n.format("gui.eidolon.codex.chapter_chants");
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
    public void setRecipe(IRecipeLayout recipeLayout, ChantRecipeWrapper recipeWrapper, IIngredients ingredients) {
        this.currentWrapper = recipeWrapper;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        drawPanel();
        if (currentWrapper != null) {
            drawSignSequence(minecraft, currentWrapper.getSigns(), 10, 28);
        }
    }

    private void drawPanel() {
        Gui.drawRect(0, 0, 176, 150, 0xffd4c2a7);
        Gui.drawRect(1, 1, 175, 2, 0xfff1e5cf);
        Gui.drawRect(1, 1, 2, 149, 0xfff1e5cf);
        Gui.drawRect(1, 148, 175, 149, 0xff6b5139);
        Gui.drawRect(174, 1, 175, 149, 0xff6b5139);
        Gui.drawRect(4, 20, 172, 54, 0x40ffffff);
    }

    private void drawSignSequence(Minecraft minecraft, List<Sign> signs, int x, int y) {
        int spacing = 20;
        for (int i = 0; i < signs.size(); i++) {
            Sign sign = signs.get(i);
            int iconX = x + i * spacing;
            if (iconX > 152) {
                break;
            }
            drawSignIcon(minecraft, sign, iconX, y, 16);
            if (i < signs.size() - 1 && iconX + 18 <= 166) {
                minecraft.fontRenderer.drawString(">", iconX + 17, y + 4, 0x6b5139);
            }
        }
    }

    private void drawSignIcon(Minecraft minecraft, Sign sign, int x, int y, int size) {
        GlStateManager.color(sign.getRed(), sign.getGreen(), sign.getBlue(), 1.0F);
        ResourceLocation sprite = sign.getSprite();
        minecraft.getTextureManager().bindTexture(new ResourceLocation(sprite.getNamespace(),
                "textures/" + sprite.getPath() + ".png"));
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, size, size, size, size);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
