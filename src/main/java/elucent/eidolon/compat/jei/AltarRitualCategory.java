package elucent.eidolon.compat.jei;

import elucent.eidolon.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class AltarRitualCategory implements IRecipeCategory<AltarRitualWrapper> {
    public static final String UID = Reference.MOD_ID + ".altar_ritual";
    static final int PAGE_WIDTH = 128;
    static final int PAGE_HEIGHT = 160;
    static final int RESULT_X = 56;
    static final int RESULT_Y = 39;
    static final int SACRIFICE_X = 56;
    static final int SACRIFICE_Y = 80;
    static final int FOCUS_X = 56;
    static final int FOCUS_Y = 141;
    private AltarRitualWrapper currentWrapper;

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/codex_ritual_page.png");

    private final IDrawable background;

    public AltarRitualCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND, 0, 0, PAGE_WIDTH, PAGE_HEIGHT);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return I18n.format("gui.eidolon.codex.chapter_altar_rituals");
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
    public void setRecipe(IRecipeLayout recipeLayout, AltarRitualWrapper wrapper, IIngredients ingredients) {
        this.currentWrapper = wrapper;
        List<List<ItemStack>> offerings = wrapper.getOfferingStacks();
        for (int i = 0; i < offerings.size(); i++) {
            int[] pos = getOfferingPosition(wrapper, i);
            recipeLayout.getItemStacks().init(i, true, pos[0], pos[1]);
            if (!offerings.get(i).isEmpty()) {
                recipeLayout.getItemStacks().set(i, offerings.get(i));
            }
        }
        int slot = offerings.size();
        if (wrapper.hasFocus()) {
            int[] focusPos = getFocusPosition(wrapper);
            recipeLayout.getItemStacks().init(slot, true, focusPos[0], focusPos[1]);
            recipeLayout.getItemStacks().set(slot, wrapper.getFocusStacks());
            slot++;
        }
        recipeLayout.getItemStacks().init(slot, true, SACRIFICE_X, SACRIFICE_Y);
        recipeLayout.getItemStacks().set(slot, wrapper.getSacrificeStacks());
        slot++;
        recipeLayout.getItemStacks().init(slot, false, RESULT_X, RESULT_Y);
        ItemStack output = wrapper.getRitual().hasResult()
                ? wrapper.getRitual().getResult()
                : wrapper.getRitual().getDisplayStack();
        if (!output.isEmpty()) {
            recipeLayout.getItemStacks().set(slot, output);
        }
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (isInSlot(mouseX, mouseY, RESULT_X, RESULT_Y)) {
            String key = currentWrapper != null && !currentWrapper.getRitual().hasResult()
                    ? currentWrapper.getRitual().getBehaviorTranslationKey()
                    : "gui.eidolon.codex.altar_slot.result";
            java.util.List<String> tooltip = new java.util.ArrayList<>();
            tooltip.add(I18n.format(key));
            if (currentWrapper != null) {
                tooltip.add(I18n.format(currentWrapper.getRitual().getResultDescriptionTranslationKey()));
            }
            return tooltip;
        }
        if (isInSlot(mouseX, mouseY, SACRIFICE_X, SACRIFICE_Y)) {
            return java.util.Collections.singletonList(I18n.format("gui.eidolon.codex.altar_slot.sacrifice"));
        }
        if (currentWrapper != null && currentWrapper.hasFocus()) {
            int[] focusPos = getFocusPosition(currentWrapper);
            if (!isInSlot(mouseX, mouseY, focusPos[0], focusPos[1])) {
                focusPos = null;
            }
            if (focusPos != null) {
            elucent.eidolon.spell.AltarRitual.BehaviorType behavior = currentWrapper.getRitual().getBehaviorType();
            String key = behavior == elucent.eidolon.spell.AltarRitual.BehaviorType.ITEM_CHARGE
                    ? "gui.eidolon.codex.altar_slot.charge_focus"
                    : behavior == elucent.eidolon.spell.AltarRitual.BehaviorType.ENTITY_SUMMON
                    ? "gui.eidolon.codex.altar_slot.summon_focus"
                    : behavior == elucent.eidolon.spell.AltarRitual.BehaviorType.ABSORPTION
                    ? "gui.eidolon.codex.altar_slot.absorption_focus"
                    : "gui.eidolon.codex.altar_slot.focus";
            return java.util.Collections.singletonList(I18n.format(key));
            }
        }
        if (currentWrapper != null) {
            int count = currentWrapper.getOfferingStacks().size();
            for (int i = 0; i < count; i++) {
                int[] pos = getOfferingPosition(currentWrapper, i);
                if (isInSlot(mouseX, mouseY, pos[0], pos[1])) {
                    return java.util.Collections.singletonList(I18n.format("gui.eidolon.codex.altar_slot.offering"));
                }
            }
        }
        return java.util.Collections.emptyList();
    }

    private boolean isInSlot(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }

    static int[] getOfferingPosition(AltarRitualWrapper wrapper, int offeringIndex) {
        return getArcPosition(offeringIndex, wrapper.getOfferingStacks().size());
    }

    static int[] getFocusPosition(AltarRitualWrapper wrapper) {
        return new int[] {FOCUS_X, FOCUS_Y};
    }

    private static int[] getArcPosition(int index, int count) {
        if (count <= 0) {
            return new int[] {56, 120};
        }
        double angleStep = Math.min(36.0D, 180.0D / count);
        double rootAngle = 90.0D - (count - 1) * angleStep / 2.0D;
        double angle = Math.toRadians(rootAngle + angleStep * index);
        int centerX = (int)(64.0D + 56.0D * Math.cos(angle));
        int centerY = (int)(79.0D + 47.0D * Math.sin(angle));
        return new int[] {centerX - 8, centerY - 8};
    }
}
