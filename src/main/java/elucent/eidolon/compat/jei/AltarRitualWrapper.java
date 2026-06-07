package elucent.eidolon.compat.jei;

import elucent.eidolon.Reference;
import elucent.eidolon.spell.AltarRitual;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AltarRitualWrapper implements IRecipeWrapper {
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/codex_ritual_page.png");

    private final AltarRitual ritual;

    public AltarRitualWrapper(AltarRitual ritual) {
        this.ritual = ritual;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputs = new ArrayList<>(getOfferingStacks());
        if (hasFocus()) {
            inputs.add(getFocusStacks());
        }
        inputs.add(getSacrificeStacks());
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ItemStack output = ritual.hasResult() ? ritual.getResult() : ritual.getDisplayStack();
        if (!output.isEmpty()) {
            ingredients.setOutput(VanillaTypes.ITEM, output);
        }
    }

    public AltarRitual getRitual() {
        return ritual;
    }

    public List<List<ItemStack>> getOfferingStacks() {
        List<List<ItemStack>> inputs = new ArrayList<>();
        for (Ingredient ingredient : ritual.getProviderOfferings()) {
            List<ItemStack> stacks = stacksFor(ingredient);
            if (!stacks.isEmpty()) {
                inputs.add(stacks);
            }
        }
        return inputs;
    }

    public boolean hasFocus() {
        return ritual.hasFocus();
    }

    public List<ItemStack> getFocusStacks() {
        return stacksFor(ritual.getFocus());
    }

    public List<ItemStack> getSacrificeStacks() {
        return stacksFor(ritual.getSacrifice());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        List<List<ItemStack>> offerings = getOfferingStacks();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(BACKGROUND);
        for (int i = 0; i < offerings.size(); i++) {
            int[] pos = AltarRitualCategory.getOfferingPosition(this, i);
            Gui.drawModalRectWithCustomSizedTexture(pos[0], pos[1], 154, 0, 16, 16, 256, 256);
        }
        if (ritual.hasFocus()) {
            int[] pos = AltarRitualCategory.getFocusPosition(this);
            Gui.drawModalRectWithCustomSizedTexture(pos[0] - 5, pos[1] - 5, 128, 0, 26, 24, 256, 256);
        }
        String requirements = I18n.format("gui.eidolon.codex.altar_capacity", formatValue(ritual.getRequiredCapacity()))
                + "  " + I18n.format("gui.eidolon.codex.altar_power", formatValue(ritual.getRequiredPower()));
        minecraft.fontRenderer.drawString(requirements,
                64 - minecraft.fontRenderer.getStringWidth(requirements) / 2, 8, 0x5b4732);
        if (ritual.hasHealthCost()) {
            String health = I18n.format("gui.eidolon.codex.altar_health", formatValue(ritual.getHealthCost()));
            minecraft.fontRenderer.drawString(health,
                    64 - minecraft.fontRenderer.getStringWidth(health) / 2, 18, 0x8b2f2f);
        }
        String behavior = I18n.format(ritual.getBehaviorTranslationKey());
        minecraft.fontRenderer.drawString(behavior,
                64 - minecraft.fontRenderer.getStringWidth(behavior) / 2, 28, 0x7d5a39);
    }

    private List<ItemStack> stacksFor(Ingredient ingredient) {
        if (ingredient == Ingredient.EMPTY) {
            return Collections.emptyList();
        }
        return Arrays.asList(ingredient.getMatchingStacks());
    }

    private String formatValue(double value) {
        return value == (int) value ? Integer.toString((int) value) : Double.toString(value);
    }
}
