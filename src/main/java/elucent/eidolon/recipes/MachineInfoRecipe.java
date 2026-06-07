package elucent.eidolon.recipes;

import elucent.eidolon.Reference;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MachineInfoRecipe {
    public static final ResourceLocation SOUL_ENCHANTER_ID = new ResourceLocation(Reference.MOD_ID, "soul_enchanter");
    public static final ResourceLocation WOODEN_BREWING_STAND_ID = new ResourceLocation(Reference.MOD_ID, "wooden_brewing_stand");

    private static final List<MachineInfoRecipe> RECIPES = new ArrayList<>();

    static {
        RECIPES.add(new MachineInfoRecipe(SOUL_ENCHANTER_ID,
                "gui.eidolon.machine.soul_enchanter.title",
                "gui.eidolon.machine.soul_enchanter.desc",
                new ItemStack(ModBlocks.SOUL_ENCHANTER),
                new ItemStack(Items.BOOK),
                new ItemStack(ModItems.SOUL_SHARD),
                new ItemStack(Items.ENCHANTED_BOOK)));
        RECIPES.add(new MachineInfoRecipe(WOODEN_BREWING_STAND_ID,
                "gui.eidolon.machine.wooden_brewing_stand.title",
                "gui.eidolon.machine.wooden_brewing_stand.desc",
                new ItemStack(ModBlocks.WOODEN_BREWING_STAND),
                new ItemStack(Items.POTIONITEM),
                new ItemStack(Items.NETHER_WART),
                new ItemStack(Items.POTIONITEM)));
    }

    private final ResourceLocation id;
    private final String titleKey;
    private final String descriptionKey;
    private final ItemStack catalyst;
    private final ItemStack primaryInput;
    private final ItemStack secondaryInput;
    private final ItemStack output;

    private MachineInfoRecipe(ResourceLocation id, String titleKey, String descriptionKey, ItemStack catalyst,
                              ItemStack primaryInput, ItemStack secondaryInput, ItemStack output) {
        this.id = id;
        this.titleKey = titleKey;
        this.descriptionKey = descriptionKey;
        this.catalyst = catalyst;
        this.primaryInput = primaryInput;
        this.secondaryInput = secondaryInput;
        this.output = output;
    }

    public static List<MachineInfoRecipe> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public ResourceLocation getId() {
        return id;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public ItemStack getCatalyst() {
        return catalyst.copy();
    }

    public ItemStack getPrimaryInput() {
        return primaryInput.copy();
    }

    public ItemStack getSecondaryInput() {
        return secondaryInput.copy();
    }

    public ItemStack getOutput() {
        return output.copy();
    }
}
