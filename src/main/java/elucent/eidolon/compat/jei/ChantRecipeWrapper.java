package elucent.eidolon.compat.jei;

import elucent.eidolon.spell.Sign;
import elucent.eidolon.spell.Spell;
import elucent.eidolon.spell.StaticSpell;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;

public class ChantRecipeWrapper implements IRecipeWrapper {
    private final StaticSpell spell;
    private final List<Sign> signs;

    public ChantRecipeWrapper(StaticSpell spell) {
        this.spell = spell;
        this.signs = new ArrayList<>(spell.getSigns().getSigns());
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
    }

    public StaticSpell getSpell() {
        return spell;
    }

    public List<Sign> getSigns() {
        return signs;
    }

    public String getTitle() {
        return I18n.format("eidolon.spell." + spell.getRegistryName().getPath());
    }

    public String getDescription() {
        String key = "eidolon.codex.page." + spell.getRegistryName().getPath();
        String translated = I18n.format(key);
        return key.equals(translated) ? I18n.format("gui.eidolon.codex.chant_fallback") : translated;
    }

    public String getCondition() {
        String key = "eidolon.codex.chant.condition." + spell.getRegistryName().getPath();
        String translated = I18n.format(key);
        return key.equals(translated) ? I18n.format("gui.eidolon.codex.chant_condition_fallback") : translated;
    }

    public static List<ChantRecipeWrapper> getRecipes() {
        List<ChantRecipeWrapper> recipes = new ArrayList<>();
        for (Spell spell : elucent.eidolon.spell.Spells.getSpells()) {
            if (spell instanceof StaticSpell) {
                recipes.add(new ChantRecipeWrapper((StaticSpell) spell));
            }
        }
        return recipes;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString(getTitle(), 8, 6, 0x3f2a1d);
        minecraft.fontRenderer.drawSplitString(getDescription(), 8, 58, 160, 0x5b4732);
        minecraft.fontRenderer.drawSplitString(I18n.format("gui.eidolon.codex.chant_condition", getCondition()),
                8, 98, 160, 0x7d6b55);
        minecraft.fontRenderer.drawSplitString(I18n.format("gui.eidolon.jei.chant_flow"), 8, 122, 160, 0x7d6b55);
    }
}
