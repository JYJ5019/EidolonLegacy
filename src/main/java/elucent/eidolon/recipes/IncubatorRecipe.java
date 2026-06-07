package elucent.eidolon.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class IncubatorRecipe {
    public static final int DEFAULT_TICKS = 1200;

    private final ResourceLocation id;
    private final Ingredient input;
    private final Ingredient catalyst;
    private final ItemStack result;
    private final int ticks;

    public IncubatorRecipe(ResourceLocation id, Ingredient input, Ingredient catalyst, ItemStack result, int ticks) {
        this.id = id;
        this.input = input;
        this.catalyst = catalyst;
        this.result = result;
        this.ticks = Math.max(1, ticks);
    }

    public ResourceLocation getId() {
        return id;
    }

    public Ingredient getInput() {
        return input;
    }

    public Ingredient getCatalyst() {
        return catalyst;
    }

    public ItemStack getResult() {
        return result.copy();
    }

    public int getTicks() {
        return ticks;
    }

    public boolean matches(ItemStack inputStack, ItemStack catalystStack) {
        return input.apply(inputStack) && catalyst.apply(catalystStack);
    }
}
