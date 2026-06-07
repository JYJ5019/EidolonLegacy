package elucent.eidolon.registries;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public final class ModRecipes {
    private ModRecipes() {
    }

    public static void init() {
        // 1.12 has no blasting or stonecutting recipe types; the blast/smelt pairs
        // from the modern source are represented as furnace recipes here.
        GameRegistry.addSmelting(ModBlocks.LEAD_ORE, new ItemStack(ModItems.LEAD_INGOT), 0.5F);
        GameRegistry.addSmelting(ModBlocks.DEEP_LEAD_ORE, new ItemStack(ModItems.LEAD_INGOT), 0.5F);
        GameRegistry.addSmelting(ModItems.RAW_LEAD, new ItemStack(ModItems.LEAD_INGOT), 0.5F);
        GameRegistry.addSmelting(ModBlocks.SILVER_ORE, new ItemStack(ModItems.SILVER_INGOT), 0.5F);
        GameRegistry.addSmelting(ModBlocks.DEEP_SILVER_ORE, new ItemStack(ModItems.SILVER_INGOT), 0.5F);
        GameRegistry.addSmelting(ModItems.RAW_SILVER, new ItemStack(ModItems.SILVER_INGOT), 0.5F);
        GameRegistry.addSmelting(ModItems.PEWTER_BLEND, new ItemStack(ModItems.PEWTER_INGOT), 0.5F);
        addOreSmelting("dustLead", new ItemStack(ModItems.LEAD_INGOT), 0.5F);
        addOreSmelting("dustSilver", new ItemStack(ModItems.SILVER_INGOT), 0.5F);
        GameRegistry.addSmelting(net.minecraft.init.Items.BONE, new ItemStack(ModBlocks.ENCHANTED_ASH, 2), 0.1F);
        GameRegistry.addSmelting(net.minecraft.init.Blocks.BONE_BLOCK, new ItemStack(ModBlocks.ENCHANTED_ASH, 6), 0.1F);
        GameRegistry.addSmelting(net.minecraft.init.Items.ROTTEN_FLESH, new ItemStack(ModItems.TALLOW), 0.5F);
    }

    private static void addOreSmelting(String oreName, ItemStack result, float experience) {
        for (ItemStack input : OreDictionary.getOres(oreName)) {
            if (!input.isEmpty()) {
                GameRegistry.addSmelting(input.copy(), result.copy(), experience);
            }
        }
    }
}
