package elucent.eidolon.registries;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class ModOreDictionary {
    private ModOreDictionary() {
    }

    public static void init() {
        OreDictionary.registerOre("oreLead", ModBlocks.LEAD_ORE);
        OreDictionary.registerOre("oreLead", ModBlocks.DEEP_LEAD_ORE);
        OreDictionary.registerOre("oreSilver", ModBlocks.SILVER_ORE);
        OreDictionary.registerOre("oreSilver", ModBlocks.DEEP_SILVER_ORE);

        OreDictionary.registerOre("ingotLead", ModItems.LEAD_INGOT);
        OreDictionary.registerOre("ingotSilver", ModItems.SILVER_INGOT);
        OreDictionary.registerOre("ingotPewter", ModItems.PEWTER_INGOT);
        OreDictionary.registerOre("ingotArcaneGold", ModItems.ARCANE_GOLD_INGOT);

        OreDictionary.registerOre("nuggetLead", ModItems.LEAD_NUGGET);
        OreDictionary.registerOre("nuggetSilver", ModItems.SILVER_NUGGET);
        OreDictionary.registerOre("nuggetPewter", ModItems.PEWTER_NUGGET);
        OreDictionary.registerOre("nuggetArcaneGold", ModItems.ARCANE_GOLD_NUGGET);

        OreDictionary.registerOre("blockLead", ModBlocks.LEAD_BLOCK);
        OreDictionary.registerOre("blockSilver", ModBlocks.SILVER_BLOCK);
        OreDictionary.registerOre("blockPewter", ModBlocks.PEWTER_BLOCK);
        OreDictionary.registerOre("blockArcaneGold", ModBlocks.ARCANE_GOLD_BLOCK);
        OreDictionary.registerOre("blockShadowGem", ModBlocks.SHADOW_GEM_BLOCK);

        OreDictionary.registerOre("gemShadow", ModItems.SHADOW_GEM);
        OreDictionary.registerOre("dustPewter", ModItems.PEWTER_BLEND);
        OreDictionary.registerOre("platePewter", ModItems.PEWTER_INLAY);
        OreDictionary.registerOre("rawLead", ModItems.RAW_LEAD);
        OreDictionary.registerOre("rawSilver", ModItems.RAW_SILVER);
        OreDictionary.registerOre("blockRawLead", ModBlocks.RAW_LEAD_BLOCK);
        OreDictionary.registerOre("blockRawSilver", ModBlocks.RAW_SILVER_BLOCK);

        OreDictionary.registerOre("dustSulfur", ModItems.SULFUR);
        OreDictionary.registerOre("ingotElderBrick", ModItems.ELDER_BRICK);
        OreDictionary.registerOre("plateGold", ModItems.GOLD_INLAY);
        OreDictionary.registerOre("gemSoul", ModItems.LESSER_SOUL_GEM);
        OreDictionary.registerOre("shardSoul", ModItems.SOUL_SHARD);
        OreDictionary.registerOre("featherRaven", ModItems.RAVEN_FEATHER);
        OreDictionary.registerOre("leather", ModItems.TATTERED_CLOTH);
        OreDictionary.registerOre("dyeBlack", ModItems.MAGIC_INK);
        OreDictionary.registerOre("tallow", ModItems.TALLOW);

        OreDictionary.registerOre("nuggetGold", Items.GOLD_NUGGET);
        OreDictionary.registerOre("enderpearl", Items.ENDER_PEARL);
        OreDictionary.registerOre("feather", Items.FEATHER);
        OreDictionary.registerOre("gemDiamond", Items.DIAMOND);
        OreDictionary.registerOre("gemQuartz", Items.QUARTZ);
        OreDictionary.registerOre("gemLapis", new ItemStack(Items.DYE, 1, 4));
        OreDictionary.registerOre("dustRedstone", Items.REDSTONE);
        OreDictionary.registerOre("ingotGold", Items.GOLD_INGOT);
        OreDictionary.registerOre("ingotIron", Items.IRON_INGOT);
        OreDictionary.registerOre("bone", Items.BONE);
        OreDictionary.registerOre("stickWood", Items.STICK);
        OreDictionary.registerOre("string", Items.STRING);
        OreDictionary.registerOre("plankWood", new ItemStack(Blocks.PLANKS, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("slabWood", new ItemStack(Blocks.WOODEN_SLAB, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("cropMushroom", Blocks.BROWN_MUSHROOM);
        OreDictionary.registerOre("cropMushroom", Blocks.RED_MUSHROOM);
        OreDictionary.registerOre("blockCoal", Blocks.COAL_BLOCK);
        OreDictionary.registerOre("blockDiamond", Blocks.DIAMOND_BLOCK);
        OreDictionary.registerOre("blockLapis", Blocks.LAPIS_BLOCK);
        OreDictionary.registerOre("dyeBlue", new ItemStack(Items.DYE, 1, 4));
        OreDictionary.registerOre("dyeRed", new ItemStack(Items.DYE, 1, 1));
        OreDictionary.registerOre("dyeBlack", new ItemStack(Items.DYE, 1, 0));
    }
}
