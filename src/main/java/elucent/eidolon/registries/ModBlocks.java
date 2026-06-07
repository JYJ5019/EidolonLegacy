package elucent.eidolon.registries;

import elucent.eidolon.Reference;
import elucent.eidolon.Eidolon;
import elucent.eidolon.gui.ModGuiHandler;
import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.recipes.CrucibleRecipes;
import elucent.eidolon.spell.AltarInfo;
import elucent.eidolon.reagent.IReagentTankProvider;
import elucent.eidolon.reagent.ReagentRegistry;
import elucent.eidolon.reagent.ReagentStack;
import elucent.eidolon.tile.AltarTileEntity;
import elucent.eidolon.tile.BrazierTileEntity;
import elucent.eidolon.tile.CisternTileEntity;
import elucent.eidolon.tile.CrucibleTileEntity;
import elucent.eidolon.tile.EffigyTileEntity;
import elucent.eidolon.tile.GlassTubeTileEntity;
import elucent.eidolon.tile.GobletTileEntity;
import elucent.eidolon.tile.IncubatorTileEntity;
import elucent.eidolon.tile.ItemHolderTileEntity;
import elucent.eidolon.tile.NecroticFocusTileEntity;
import elucent.eidolon.tile.OffertoryPlateTileEntity;
import elucent.eidolon.tile.ResearchTableTileEntity;
import elucent.eidolon.tile.SoulEnchanterTileEntity;
import elucent.eidolon.tile.StoneHandTileEntity;
import elucent.eidolon.tile.WoodenBrewingStandTileEntity;
import elucent.eidolon.tile.WorktableTileEntity;
import elucent.eidolon.world.IllwoodTreeGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ModBlocks {
    public static final Block TEST_STONE = block("test_stone", Material.ROCK);
    public static final Block LEAD_ORE = ore("lead_ore", ModItems.RAW_LEAD);
    public static final Block DEEP_LEAD_ORE = ore("deep_lead_ore", ModItems.RAW_LEAD);
    public static final Block LEAD_BLOCK = block("lead_block", Material.IRON);
    public static final Block RAW_LEAD_BLOCK = block("raw_lead_block", Material.ROCK);
    public static final Block SILVER_ORE = ore("silver_ore", ModItems.RAW_SILVER);
    public static final Block DEEP_SILVER_ORE = ore("deep_silver_ore", ModItems.RAW_SILVER);
    public static final Block SILVER_BLOCK = block("silver_block", Material.IRON);
    public static final Block RAW_SILVER_BLOCK = block("raw_silver_block", Material.ROCK);
    public static final Block PEWTER_BLOCK = block("pewter_block", Material.IRON);
    public static final Block ARCANE_GOLD_BLOCK = block("arcane_gold_block", Material.IRON);
    public static final Block SHADOW_GEM_BLOCK = block("shadow_gem_block", Material.ROCK);
    public static final Block SMOOTH_STONE_BRICKS = block("smooth_stone_bricks", Material.ROCK);
    public static final Block MOSSY_SMOOTH_STONE_BRICKS = block("mossy_smooth_stone_bricks", Material.ROCK);
    public static final Block SMOOTH_STONE_MASONRY = block("smooth_stone_masonry", Material.ROCK);
    public static final Block SMOOTH_STONE_TILES = block("smooth_stone_tiles", Material.ROCK);
    public static final Block ELDER_BRICKS = block("elder_bricks", Material.ROCK);
    public static final Block ELDER_BRICKS_EYE = block("elder_bricks_eye", Material.ROCK);
    public static final Block ELDER_MASONRY = block("elder_masonry", Material.ROCK);
    public static final Block ELDER_PILLAR = block("elder_pillar", Material.ROCK);
    public static final Block SMOOTH_STONE_BRICKS_STAIRS = stairs("smooth_stone_bricks_stairs", SMOOTH_STONE_BRICKS);
    public static final Block SMOOTH_STONE_BRICKS_WALL = wall("smooth_stone_bricks_wall", SMOOTH_STONE_BRICKS);
    public static final Block SMOOTH_STONE_MASONRY_STAIRS = stairs("smooth_stone_masonry_stairs", SMOOTH_STONE_MASONRY);
    public static final Block SMOOTH_STONE_TILES_STAIRS = stairs("smooth_stone_tiles_stairs", SMOOTH_STONE_TILES);
    public static final Block ELDER_BRICKS_STAIRS = stairs("elder_bricks_stairs", ELDER_BRICKS);
    public static final Block ELDER_BRICKS_WALL = wall("elder_bricks_wall", ELDER_BRICKS);
    public static final Block ELDER_MASONRY_STAIRS = stairs("elder_masonry_stairs", ELDER_MASONRY);
    public static final Block SMOOTH_STONE_BRICKS_SLAB = slab("smooth_stone_bricks_slab", false, SMOOTH_STONE_BRICKS);
    public static final Block DOUBLE_SMOOTH_STONE_BRICKS_SLAB = slab("double_smooth_stone_bricks_slab", true, SMOOTH_STONE_BRICKS);
    public static final Block SMOOTH_STONE_MASONRY_SLAB = slab("smooth_stone_masonry_slab", false, SMOOTH_STONE_MASONRY);
    public static final Block DOUBLE_SMOOTH_STONE_MASONRY_SLAB = slab("double_smooth_stone_masonry_slab", true, SMOOTH_STONE_MASONRY);
    public static final Block SMOOTH_STONE_TILES_SLAB = slab("smooth_stone_tiles_slab", false, SMOOTH_STONE_TILES);
    public static final Block DOUBLE_SMOOTH_STONE_TILES_SLAB = slab("double_smooth_stone_tiles_slab", true, SMOOTH_STONE_TILES);
    public static final Block ELDER_BRICKS_SLAB = slab("elder_bricks_slab", false, ELDER_BRICKS);
    public static final Block DOUBLE_ELDER_BRICKS_SLAB = slab("double_elder_bricks_slab", true, ELDER_BRICKS);
    public static final Block ELDER_MASONRY_SLAB = slab("elder_masonry_slab", false, ELDER_MASONRY);
    public static final Block DOUBLE_ELDER_MASONRY_SLAB = slab("double_elder_masonry_slab", true, ELDER_MASONRY);
    public static final Block ILLWOOD_PLANKS = woodBlock("illwood_planks");
    public static final Block ILLWOOD_PLANKS_STAIRS = woodStairs("illwood_planks_stairs", ILLWOOD_PLANKS);
    public static final Block ILLWOOD_PLANKS_SLAB = woodSlab("illwood_planks_slab", false, ILLWOOD_PLANKS);
    public static final Block DOUBLE_ILLWOOD_PLANKS_SLAB = woodSlab("double_illwood_planks_slab", true, ILLWOOD_PLANKS);
    public static final Block ILLWOOD_PLANKS_FENCE = fence("illwood_planks_fence");
    public static final Block ILLWOOD_PLANKS_FENCE_GATE = fenceGate("illwood_planks_fence_gate");
    public static final Block POLISHED_PLANKS = woodBlock("polished_planks");
    public static final Block POLISHED_PLANKS_STAIRS = woodStairs("polished_planks_stairs", POLISHED_PLANKS);
    public static final Block POLISHED_PLANKS_SLAB = woodSlab("polished_planks_slab", false, POLISHED_PLANKS);
    public static final Block DOUBLE_POLISHED_PLANKS_SLAB = woodSlab("double_polished_planks_slab", true, POLISHED_PLANKS);
    public static final Block POLISHED_PLANKS_FENCE = fence("polished_planks_fence");
    public static final Block POLISHED_PLANKS_FENCE_GATE = fenceGate("polished_planks_fence_gate");
    public static final Block ILLWOOD_LOG = log("illwood_log");
    public static final Block STRIPPED_ILLWOOD_LOG = log("stripped_illwood_log");
    public static final Block ILLWOOD_BARK = log("illwood_bark");
    public static final Block STRIPPED_ILLWOOD_BARK = log("stripped_illwood_bark");
    public static final Block POLISHED_WOOD_PILLAR = log("polished_wood_pillar");
    public static final Block BONE_PILE = block("bone_pile", Material.ROCK);
    public static final Block BONE_PILE_STAIRS = stairs("bone_pile_stairs", BONE_PILE);
    public static final Block BONE_PILE_SLAB = slab("bone_pile_slab", false, BONE_PILE);
    public static final Block DOUBLE_BONE_PILE_SLAB = slab("double_bone_pile_slab", true, BONE_PILE);
    public static final Block SMOOTH_STONE_ARCH = arch("smooth_stone_arch");
    public static final Block STRAW_EFFIGY = effigy("straw_effigy", Material.PLANTS);
    public static final Block UNHOLY_EFFIGY = effigy("unholy_effigy", Material.WOOD);
    public static final Block GOBLET = goblet("goblet");
    public static final Block STONE_HAND = ritualHolder("stone_hand", Material.ROCK, false);
    public static final Block CANDLE = smallDecorBlock("candle", Material.CIRCUITS);
    public static final Block CANDLESTICK = attachableDecorBlock("candlestick", Material.CIRCUITS);
    public static final Block MAGIC_CANDLE = smallDecorBlock("magic_candle", Material.CIRCUITS);
    public static final Block MAGIC_CANDLESTICK = attachableDecorBlock("magic_candlestick", Material.CIRCUITS);
    public static final Block AVENNIAN_SPRIG = herb("avennian_sprig");
    public static final Block MERAMMER_ROOT = herb("merammer_root");
    public static final Block OANNA_BLOOM = herb("oanna_bloom");
    public static final Block SILDRIAN_SEED = herb("sildrian_seed");
    public static final Block ILLWOOD_LEAVES = leaves("illwood_leaves");
    public static final Block ILLWOOD_SAPLING = sapling("illwood_sapling");
    public static final Block ENCHANTED_ASH = enchantedAsh("enchanted_ash");
    public static final Block PLANTER = smallDecorBlock("planter", Material.WOOD);
    public static final Block GLASS_TUBE = glassTube("glass_tube");
    public static final Block OBELISK = obelisk("obelisk");
    public static final Block PLINTH = obelisk("plinth");
    public static final Block BRAZIER = brazier("brazier");
    public static final Block STONE_ALTAR = altar("stone_altar");
    public static final Block WOODEN_ALTAR = altar("wooden_altar");
    public static final Block WORKTABLE = worktable("worktable");
    public static final Block WOODEN_BREWING_STAND = woodenBrewingStand("wooden_brewing_stand");
    public static final Block CRUCIBLE = crucible("crucible");
    public static final Block INCUBATOR = incubator("incubator");
    public static final Block CISTERN = cistern("cistern");
    public static final Block NECROTIC_FOCUS = ritualHolder("necrotic_focus", Material.ROCK, true);
    public static final Block OFFERTORY_PLATE_BLOCK = offertoryPlateBlock("offertory_plate_block", ModItems.OFFERTORY_PLATE);
    public static final Block PEWTER_OFFERTORY_PLATE_BLOCK = offertoryPlateBlock("pewter_offertory_plate_block", ModItems.PEWTER_OFFERTORY_PLATE);
    public static final Block GOLD_OFFERTORY_PLATE_BLOCK = offertoryPlateBlock("gold_offertory_plate_block", ModItems.GOLD_OFFERTORY_PLATE);
    public static final Block SOUL_ENCHANTER = soulEnchanter("soul_enchanter");
    public static final Block RESEARCH_TABLE = researchTable("research_table");

    public static final ItemBlock TEST_STONE_ITEM = itemBlock(TEST_STONE);
    public static final ItemBlock LEAD_ORE_ITEM = itemBlock(LEAD_ORE);
    public static final ItemBlock DEEP_LEAD_ORE_ITEM = itemBlock(DEEP_LEAD_ORE);
    public static final ItemBlock LEAD_BLOCK_ITEM = itemBlock(LEAD_BLOCK);
    public static final ItemBlock RAW_LEAD_BLOCK_ITEM = itemBlock(RAW_LEAD_BLOCK);
    public static final ItemBlock SILVER_ORE_ITEM = itemBlock(SILVER_ORE);
    public static final ItemBlock DEEP_SILVER_ORE_ITEM = itemBlock(DEEP_SILVER_ORE);
    public static final ItemBlock SILVER_BLOCK_ITEM = itemBlock(SILVER_BLOCK);
    public static final ItemBlock RAW_SILVER_BLOCK_ITEM = itemBlock(RAW_SILVER_BLOCK);
    public static final ItemBlock PEWTER_BLOCK_ITEM = itemBlock(PEWTER_BLOCK);
    public static final ItemBlock ARCANE_GOLD_BLOCK_ITEM = itemBlock(ARCANE_GOLD_BLOCK);
    public static final ItemBlock SHADOW_GEM_BLOCK_ITEM = itemBlock(SHADOW_GEM_BLOCK);
    public static final ItemBlock SMOOTH_STONE_BRICKS_ITEM = itemBlock(SMOOTH_STONE_BRICKS);
    public static final ItemBlock MOSSY_SMOOTH_STONE_BRICKS_ITEM = itemBlock(MOSSY_SMOOTH_STONE_BRICKS);
    public static final ItemBlock SMOOTH_STONE_MASONRY_ITEM = itemBlock(SMOOTH_STONE_MASONRY);
    public static final ItemBlock SMOOTH_STONE_TILES_ITEM = itemBlock(SMOOTH_STONE_TILES);
    public static final ItemBlock ELDER_BRICKS_ITEM = itemBlock(ELDER_BRICKS);
    public static final ItemBlock ELDER_BRICKS_EYE_ITEM = itemBlock(ELDER_BRICKS_EYE);
    public static final ItemBlock ELDER_MASONRY_ITEM = itemBlock(ELDER_MASONRY);
    public static final ItemBlock ELDER_PILLAR_ITEM = itemBlock(ELDER_PILLAR);
    public static final ItemBlock SMOOTH_STONE_BRICKS_STAIRS_ITEM = itemBlock(SMOOTH_STONE_BRICKS_STAIRS);
    public static final ItemBlock SMOOTH_STONE_BRICKS_WALL_ITEM = wallItemBlock(SMOOTH_STONE_BRICKS_WALL);
    public static final ItemBlock SMOOTH_STONE_MASONRY_STAIRS_ITEM = itemBlock(SMOOTH_STONE_MASONRY_STAIRS);
    public static final ItemBlock SMOOTH_STONE_TILES_STAIRS_ITEM = itemBlock(SMOOTH_STONE_TILES_STAIRS);
    public static final ItemBlock ELDER_BRICKS_STAIRS_ITEM = itemBlock(ELDER_BRICKS_STAIRS);
    public static final ItemBlock ELDER_BRICKS_WALL_ITEM = wallItemBlock(ELDER_BRICKS_WALL);
    public static final ItemBlock ELDER_MASONRY_STAIRS_ITEM = itemBlock(ELDER_MASONRY_STAIRS);
    public static final ItemBlock SMOOTH_STONE_BRICKS_SLAB_ITEM = slabItemBlock(SMOOTH_STONE_BRICKS_SLAB, DOUBLE_SMOOTH_STONE_BRICKS_SLAB);
    public static final ItemBlock SMOOTH_STONE_MASONRY_SLAB_ITEM = slabItemBlock(SMOOTH_STONE_MASONRY_SLAB, DOUBLE_SMOOTH_STONE_MASONRY_SLAB);
    public static final ItemBlock SMOOTH_STONE_TILES_SLAB_ITEM = slabItemBlock(SMOOTH_STONE_TILES_SLAB, DOUBLE_SMOOTH_STONE_TILES_SLAB);
    public static final ItemBlock ELDER_BRICKS_SLAB_ITEM = slabItemBlock(ELDER_BRICKS_SLAB, DOUBLE_ELDER_BRICKS_SLAB);
    public static final ItemBlock ELDER_MASONRY_SLAB_ITEM = slabItemBlock(ELDER_MASONRY_SLAB, DOUBLE_ELDER_MASONRY_SLAB);
    public static final ItemBlock ILLWOOD_PLANKS_ITEM = itemBlock(ILLWOOD_PLANKS);
    public static final ItemBlock ILLWOOD_PLANKS_STAIRS_ITEM = itemBlock(ILLWOOD_PLANKS_STAIRS);
    public static final ItemBlock ILLWOOD_PLANKS_SLAB_ITEM = slabItemBlock(ILLWOOD_PLANKS_SLAB, DOUBLE_ILLWOOD_PLANKS_SLAB);
    public static final ItemBlock ILLWOOD_PLANKS_FENCE_ITEM = itemBlock(ILLWOOD_PLANKS_FENCE);
    public static final ItemBlock ILLWOOD_PLANKS_FENCE_GATE_ITEM = itemBlock(ILLWOOD_PLANKS_FENCE_GATE);
    public static final ItemBlock POLISHED_PLANKS_ITEM = itemBlock(POLISHED_PLANKS);
    public static final ItemBlock POLISHED_PLANKS_STAIRS_ITEM = itemBlock(POLISHED_PLANKS_STAIRS);
    public static final ItemBlock POLISHED_PLANKS_SLAB_ITEM = slabItemBlock(POLISHED_PLANKS_SLAB, DOUBLE_POLISHED_PLANKS_SLAB);
    public static final ItemBlock POLISHED_PLANKS_FENCE_ITEM = itemBlock(POLISHED_PLANKS_FENCE);
    public static final ItemBlock POLISHED_PLANKS_FENCE_GATE_ITEM = itemBlock(POLISHED_PLANKS_FENCE_GATE);
    public static final ItemBlock ILLWOOD_LOG_ITEM = itemBlock(ILLWOOD_LOG);
    public static final ItemBlock STRIPPED_ILLWOOD_LOG_ITEM = itemBlock(STRIPPED_ILLWOOD_LOG);
    public static final ItemBlock ILLWOOD_BARK_ITEM = itemBlock(ILLWOOD_BARK);
    public static final ItemBlock STRIPPED_ILLWOOD_BARK_ITEM = itemBlock(STRIPPED_ILLWOOD_BARK);
    public static final ItemBlock POLISHED_WOOD_PILLAR_ITEM = itemBlock(POLISHED_WOOD_PILLAR);
    public static final ItemBlock BONE_PILE_ITEM = itemBlock(BONE_PILE);
    public static final ItemBlock BONE_PILE_STAIRS_ITEM = itemBlock(BONE_PILE_STAIRS);
    public static final ItemBlock BONE_PILE_SLAB_ITEM = slabItemBlock(BONE_PILE_SLAB, DOUBLE_BONE_PILE_SLAB);
    public static final ItemBlock SMOOTH_STONE_ARCH_ITEM = itemBlock(SMOOTH_STONE_ARCH);
    public static final ItemBlock STRAW_EFFIGY_ITEM = itemBlock(STRAW_EFFIGY);
    public static final ItemBlock UNHOLY_EFFIGY_ITEM = itemBlock(UNHOLY_EFFIGY);
    public static final ItemBlock GOBLET_ITEM = itemBlock(GOBLET);
    public static final ItemBlock STONE_HAND_ITEM = itemBlock(STONE_HAND);
    public static final ItemBlock CANDLE_ITEM = itemBlock(CANDLE);
    public static final ItemBlock CANDLESTICK_ITEM = itemBlock(CANDLESTICK);
    public static final ItemBlock MAGIC_CANDLE_ITEM = itemBlock(MAGIC_CANDLE);
    public static final ItemBlock MAGIC_CANDLESTICK_ITEM = itemBlock(MAGIC_CANDLESTICK);
    public static final ItemBlock AVENNIAN_SPRIG_ITEM = itemBlock(AVENNIAN_SPRIG);
    public static final ItemBlock MERAMMER_ROOT_ITEM = itemBlock(MERAMMER_ROOT);
    public static final ItemBlock OANNA_BLOOM_ITEM = itemBlock(OANNA_BLOOM);
    public static final ItemBlock SILDRIAN_SEED_ITEM = itemBlock(SILDRIAN_SEED);
    public static final ItemBlock ILLWOOD_LEAVES_ITEM = itemBlock(ILLWOOD_LEAVES);
    public static final ItemBlock ILLWOOD_SAPLING_ITEM = itemBlock(ILLWOOD_SAPLING);
    public static final ItemBlock ENCHANTED_ASH_ITEM = itemBlock(ENCHANTED_ASH);
    public static final ItemBlock PLANTER_ITEM = itemBlock(PLANTER);
    public static final ItemBlock GLASS_TUBE_ITEM = itemBlock(GLASS_TUBE);
    public static final ItemBlock OBELISK_ITEM = itemBlock(OBELISK);
    public static final ItemBlock PLINTH_ITEM = itemBlock(PLINTH);
    public static final ItemBlock BRAZIER_ITEM = itemBlock(BRAZIER);
    public static final ItemBlock STONE_ALTAR_ITEM = itemBlock(STONE_ALTAR);
    public static final ItemBlock WOODEN_ALTAR_ITEM = itemBlock(WOODEN_ALTAR);
    public static final ItemBlock WORKTABLE_ITEM = itemBlock(WORKTABLE);
    public static final ItemBlock WOODEN_BREWING_STAND_ITEM = itemBlock(WOODEN_BREWING_STAND);
    public static final ItemBlock CRUCIBLE_ITEM = itemBlock(CRUCIBLE);
    public static final ItemBlock INCUBATOR_ITEM = itemBlock(INCUBATOR);
    public static final ItemBlock CISTERN_ITEM = itemBlock(CISTERN);
    public static final ItemBlock NECROTIC_FOCUS_ITEM = itemBlock(NECROTIC_FOCUS);
    public static final ItemBlock SOUL_ENCHANTER_ITEM = itemBlock(SOUL_ENCHANTER);
    public static final ItemBlock RESEARCH_TABLE_ITEM = itemBlock(RESEARCH_TABLE);

    public static final Block[] BLOCKS = {
            TEST_STONE,
            LEAD_ORE,
            DEEP_LEAD_ORE,
            LEAD_BLOCK,
            RAW_LEAD_BLOCK,
            SILVER_ORE,
            DEEP_SILVER_ORE,
            SILVER_BLOCK,
            RAW_SILVER_BLOCK,
            PEWTER_BLOCK,
            ARCANE_GOLD_BLOCK,
            SHADOW_GEM_BLOCK,
            SMOOTH_STONE_BRICKS,
            MOSSY_SMOOTH_STONE_BRICKS,
            SMOOTH_STONE_MASONRY,
            SMOOTH_STONE_TILES,
            ELDER_BRICKS,
            ELDER_BRICKS_EYE,
            ELDER_MASONRY,
            ELDER_PILLAR,
            SMOOTH_STONE_BRICKS_STAIRS,
            SMOOTH_STONE_BRICKS_WALL,
            SMOOTH_STONE_MASONRY_STAIRS,
            SMOOTH_STONE_TILES_STAIRS,
            ELDER_BRICKS_STAIRS,
            ELDER_BRICKS_WALL,
            ELDER_MASONRY_STAIRS,
            SMOOTH_STONE_BRICKS_SLAB,
            DOUBLE_SMOOTH_STONE_BRICKS_SLAB,
            SMOOTH_STONE_MASONRY_SLAB,
            DOUBLE_SMOOTH_STONE_MASONRY_SLAB,
            SMOOTH_STONE_TILES_SLAB,
            DOUBLE_SMOOTH_STONE_TILES_SLAB,
            ELDER_BRICKS_SLAB,
            DOUBLE_ELDER_BRICKS_SLAB,
            ELDER_MASONRY_SLAB,
            DOUBLE_ELDER_MASONRY_SLAB,
            ILLWOOD_PLANKS,
            ILLWOOD_PLANKS_STAIRS,
            ILLWOOD_PLANKS_SLAB,
            DOUBLE_ILLWOOD_PLANKS_SLAB,
            ILLWOOD_PLANKS_FENCE,
            ILLWOOD_PLANKS_FENCE_GATE,
            POLISHED_PLANKS,
            POLISHED_PLANKS_STAIRS,
            POLISHED_PLANKS_SLAB,
            DOUBLE_POLISHED_PLANKS_SLAB,
            POLISHED_PLANKS_FENCE,
            POLISHED_PLANKS_FENCE_GATE,
            ILLWOOD_LOG,
            STRIPPED_ILLWOOD_LOG,
            ILLWOOD_BARK,
            STRIPPED_ILLWOOD_BARK,
            POLISHED_WOOD_PILLAR,
            BONE_PILE,
            BONE_PILE_STAIRS,
            BONE_PILE_SLAB,
            DOUBLE_BONE_PILE_SLAB,
            SMOOTH_STONE_ARCH,
            STRAW_EFFIGY,
            UNHOLY_EFFIGY,
            GOBLET,
            STONE_HAND,
            CANDLE,
            CANDLESTICK,
            MAGIC_CANDLE,
            MAGIC_CANDLESTICK,
            AVENNIAN_SPRIG,
            MERAMMER_ROOT,
            OANNA_BLOOM,
            SILDRIAN_SEED,
            ILLWOOD_LEAVES,
            ILLWOOD_SAPLING,
            ENCHANTED_ASH,
            PLANTER,
            GLASS_TUBE,
            OBELISK,
            PLINTH,
            BRAZIER,
            STONE_ALTAR,
            WOODEN_ALTAR,
            WORKTABLE,
            WOODEN_BREWING_STAND,
            CRUCIBLE,
            INCUBATOR,
            CISTERN,
            NECROTIC_FOCUS,
            OFFERTORY_PLATE_BLOCK,
            PEWTER_OFFERTORY_PLATE_BLOCK,
            GOLD_OFFERTORY_PLATE_BLOCK,
            SOUL_ENCHANTER,
            RESEARCH_TABLE
    };

    public static final ItemBlock[] ITEM_BLOCKS = {
            TEST_STONE_ITEM,
            LEAD_ORE_ITEM,
            DEEP_LEAD_ORE_ITEM,
            LEAD_BLOCK_ITEM,
            RAW_LEAD_BLOCK_ITEM,
            SILVER_ORE_ITEM,
            DEEP_SILVER_ORE_ITEM,
            SILVER_BLOCK_ITEM,
            RAW_SILVER_BLOCK_ITEM,
            PEWTER_BLOCK_ITEM,
            ARCANE_GOLD_BLOCK_ITEM,
            SHADOW_GEM_BLOCK_ITEM,
            SMOOTH_STONE_BRICKS_ITEM,
            MOSSY_SMOOTH_STONE_BRICKS_ITEM,
            SMOOTH_STONE_MASONRY_ITEM,
            SMOOTH_STONE_TILES_ITEM,
            ELDER_BRICKS_ITEM,
            ELDER_BRICKS_EYE_ITEM,
            ELDER_MASONRY_ITEM,
            ELDER_PILLAR_ITEM,
            SMOOTH_STONE_BRICKS_STAIRS_ITEM,
            SMOOTH_STONE_BRICKS_WALL_ITEM,
            SMOOTH_STONE_MASONRY_STAIRS_ITEM,
            SMOOTH_STONE_TILES_STAIRS_ITEM,
            ELDER_BRICKS_STAIRS_ITEM,
            ELDER_BRICKS_WALL_ITEM,
            ELDER_MASONRY_STAIRS_ITEM,
            SMOOTH_STONE_BRICKS_SLAB_ITEM,
            SMOOTH_STONE_MASONRY_SLAB_ITEM,
            SMOOTH_STONE_TILES_SLAB_ITEM,
            ELDER_BRICKS_SLAB_ITEM,
            ELDER_MASONRY_SLAB_ITEM,
            ILLWOOD_PLANKS_ITEM,
            ILLWOOD_PLANKS_STAIRS_ITEM,
            ILLWOOD_PLANKS_SLAB_ITEM,
            ILLWOOD_PLANKS_FENCE_ITEM,
            ILLWOOD_PLANKS_FENCE_GATE_ITEM,
            POLISHED_PLANKS_ITEM,
            POLISHED_PLANKS_STAIRS_ITEM,
            POLISHED_PLANKS_SLAB_ITEM,
            POLISHED_PLANKS_FENCE_ITEM,
            POLISHED_PLANKS_FENCE_GATE_ITEM,
            ILLWOOD_LOG_ITEM,
            STRIPPED_ILLWOOD_LOG_ITEM,
            ILLWOOD_BARK_ITEM,
            STRIPPED_ILLWOOD_BARK_ITEM,
            POLISHED_WOOD_PILLAR_ITEM,
            BONE_PILE_ITEM,
            BONE_PILE_STAIRS_ITEM,
            BONE_PILE_SLAB_ITEM,
            SMOOTH_STONE_ARCH_ITEM,
            STRAW_EFFIGY_ITEM,
            UNHOLY_EFFIGY_ITEM,
            GOBLET_ITEM,
            STONE_HAND_ITEM,
            CANDLE_ITEM,
            CANDLESTICK_ITEM,
            MAGIC_CANDLE_ITEM,
            MAGIC_CANDLESTICK_ITEM,
            AVENNIAN_SPRIG_ITEM,
            MERAMMER_ROOT_ITEM,
            OANNA_BLOOM_ITEM,
            SILDRIAN_SEED_ITEM,
            ILLWOOD_LEAVES_ITEM,
            ILLWOOD_SAPLING_ITEM,
            ENCHANTED_ASH_ITEM,
            PLANTER_ITEM,
            GLASS_TUBE_ITEM,
            OBELISK_ITEM,
            PLINTH_ITEM,
            BRAZIER_ITEM,
            STONE_ALTAR_ITEM,
            WOODEN_ALTAR_ITEM,
            WORKTABLE_ITEM,
            WOODEN_BREWING_STAND_ITEM,
            CRUCIBLE_ITEM,
            INCUBATOR_ITEM,
            CISTERN_ITEM,
            NECROTIC_FOCUS_ITEM,
            SOUL_ENCHANTER_ITEM,
            RESEARCH_TABLE_ITEM
    };

    private ModBlocks() {
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(BLOCKS);
    }

    private static Block block(String name, Material material) {
        Block block = new Block(material);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.5F);
        block.setResistance(10.0F);
        return block;
    }

    private static Block woodBlock(String name) {
        Block block = block(name, Material.WOOD);
        block.setHardness(2.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block ore(String name, Item drop) {
        Block block = new DroppingOreBlock(drop);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(3.0F);
        block.setResistance(10.0F);
        block.setHarvestLevel("pickaxe", 1);
        return block;
    }

    private static Block stairs(String name, Block base) {
        Block block = new SimpleStairsBlock(base.getDefaultState());
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.5F);
        block.setResistance(10.0F);
        return block;
    }

    private static Block woodStairs(String name, Block base) {
        Block block = stairs(name, base);
        block.setHardness(2.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block wall(String name, Block base) {
        Block block = new SimpleWallBlock(base);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.5F);
        block.setResistance(10.0F);
        return block;
    }

    private static Block slab(String name, boolean isDouble, Block base) {
        Block block = isDouble ? new DoubleSimpleSlabBlock(base) : new HalfSimpleSlabBlock(base);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        if (!isDouble) {
            block.setCreativeTab(ModCreativeTabs.EIDOLON);
        }
        block.setHardness(1.5F);
        block.setResistance(10.0F);
        return block;
    }

    private static Block woodSlab(String name, boolean isDouble, Block base) {
        Block block = slab(name, isDouble, base);
        block.setHardness(2.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block fence(String name) {
        Block block = new BlockFence(Material.WOOD, MapColor.WOOD);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(2.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block fenceGate(String name) {
        Block block = new BlockFenceGate(BlockPlanks.EnumType.OAK);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(2.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block log(String name) {
        Block block = new SimpleLogBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(2.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block arch(String name) {
        Block block = new SmoothStoneArchBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.5F);
        block.setResistance(10.0F);
        return block;
    }

    private static Block horizontalFacingBlock(String name, Material material) {
        Block block = new HorizontalFacingBlock(material);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block smallDecorBlock(String name, Material material) {
        Block block = new SmallDecorBlock(material);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block effigy(String name, Material material) {
        Block block = new EffigyBlock(material);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block goblet(String name) {
        Block block = new GobletBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block ritualHolder(String name, Material material, boolean focus) {
        Block block = new RitualHolderBlock(material, focus);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block brazier(String name) {
        Block block = new BrazierBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block offertoryPlateBlock(String name, Item plateItem) {
        Block block = new OffertoryPlateBlock(plateItem);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setHardness(1.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block worktable(String name) {
        Block block = new WorktableBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(2.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block crucible(String name) {
        Block block = new CrucibleBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block researchTable(String name) {
        Block block = new ResearchTableBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block soulEnchanter(String name) {
        Block block = new SoulEnchanterBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block attachableDecorBlock(String name, Material material) {
        Block block = new AttachableDecorBlock(material);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.0F);
        block.setResistance(5.0F);
        return block;
    }

    private static Block herb(String name) {
        Block block = new HerbBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(0.0F);
        return block;
    }

    private static Block leaves(String name) {
        Block block = new SimpleLeavesBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(0.2F);
        block.setLightOpacity(1);
        return block;
    }

    private static Block sapling(String name) {
        Block block = new SimpleSaplingBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(0.0F);
        return block;
    }

    private static Block enchantedAsh(String name) {
        Block block = new EnchantedAshBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(0.0F);
        return block;
    }

    private static Block glassTube(String name) {
        Block block = new GlassTubeBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(0.3F);
        return block;
    }

    public static boolean setGlassTubeOutput(World world, BlockPos pos, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof GlassTubeBlock)) {
            return false;
        }
        return ((GlassTubeBlock) state.getBlock()).setOutput(world, pos, state, facing);
    }

    private static Block obelisk(String name) {
        Block block = new ObeliskBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.5F);
        block.setResistance(10.0F);
        return block;
    }

    private static Block altar(String name) {
        Block block = new AltarBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.5F);
        block.setResistance(10.0F);
        return block;
    }

    private static Block woodenBrewingStand(String name) {
        Block block = new WoodenBrewingStandBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(0.5F);
        block.setResistance(3.0F);
        return block;
    }

    private static Block incubator(String name) {
        Block block = new IncubatorBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(2.0F);
        block.setResistance(3.0F);
        return block;
    }

    private static Block cistern(String name) {
        Block block = new CisternBlock();
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        block.setCreativeTab(ModCreativeTabs.EIDOLON);
        block.setHardness(1.0F);
        block.setResistance(1.5F);
        return block;
    }

    private static ItemBlock itemBlock(Block block) {
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        return itemBlock;
    }

    private static ItemBlock wallItemBlock(Block block) {
        ItemBlock itemBlock = new SingleVariantItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        return itemBlock;
    }

    private static ItemBlock slabItemBlock(Block halfSlab, Block doubleSlab) {
        ItemBlock itemBlock = new ItemSlab(halfSlab, (BlockSlab) halfSlab, (BlockSlab) doubleSlab);
        itemBlock.setRegistryName(halfSlab.getRegistryName());
        return itemBlock;
    }

    private static final class DroppingOreBlock extends Block {
        private final Item drop;

        private DroppingOreBlock(Item drop) {
            super(Material.ROCK);
            this.drop = drop;
        }

        @Override
        public Item getItemDropped(IBlockState state, Random rand, int fortune) {
            return drop;
        }

        @Override
        protected ItemStack getSilkTouchDrop(IBlockState state) {
            return new ItemStack(Item.getItemFromBlock(this));
        }

        @Override
        public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
            return true;
        }

        @Override
        public int quantityDroppedWithBonus(int fortune, Random random) {
            if (fortune <= 0) {
                return quantityDropped(random);
            }
            int multiplier = random.nextInt(fortune + 2) - 1;
            if (multiplier < 0) {
                multiplier = 0;
            }
            return quantityDropped(random) * (multiplier + 1);
        }
    }

    private static final class SimpleStairsBlock extends BlockStairs {
        private SimpleStairsBlock(IBlockState modelState) {
            super(modelState);
        }
    }

    private static final class SimpleWallBlock extends BlockWall {
        private SimpleWallBlock(Block modelBlock) {
            super(modelBlock);
        }

        @Override
        public int damageDropped(IBlockState state) {
            return 0;
        }

        @Override
        public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
            items.add(new ItemStack(this, 1, 0));
        }
    }

    private static final class SingleVariantItemBlock extends ItemBlock {
        private SingleVariantItemBlock(Block block) {
            super(block);
            setHasSubtypes(false);
        }

        @Override
        public int getMetadata(int damage) {
            return 0;
        }

        @Override
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
            if (isInCreativeTab(tab)) {
                items.add(new ItemStack(this, 1, 0));
            }
        }
    }

    private static final class SimpleLogBlock extends BlockLog {
        private SimpleLogBlock() {
            setDefaultState(blockState.getBaseState().withProperty(LOG_AXIS, EnumAxis.Y));
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            IBlockState state = getDefaultState();

            switch (meta & 12) {
                case 4:
                    return state.withProperty(LOG_AXIS, EnumAxis.X);
                case 8:
                    return state.withProperty(LOG_AXIS, EnumAxis.Z);
                case 12:
                    return state.withProperty(LOG_AXIS, EnumAxis.NONE);
                default:
                    return state.withProperty(LOG_AXIS, EnumAxis.Y);
            }
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            switch (state.getValue(LOG_AXIS)) {
                case X:
                    return 4;
                case Z:
                    return 8;
                case NONE:
                    return 12;
                case Y:
                default:
                    return 0;
            }
        }

        @Override
        public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
            items.add(new ItemStack(this, 1, 0));
        }

        @Override
        public int damageDropped(IBlockState state) {
            return 0;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, LOG_AXIS);
        }
    }

    private static final class SmoothStoneArchBlock extends Block {
        private static final net.minecraft.block.properties.PropertyBool TOP =
                net.minecraft.block.properties.PropertyBool.create("top");
        private static final net.minecraft.block.properties.PropertyBool BOTTOM =
                net.minecraft.block.properties.PropertyBool.create("bottom");

        private SmoothStoneArchBlock() {
            super(Material.ROCK);
            setDefaultState(blockState.getBaseState()
                    .withProperty(TOP, false)
                    .withProperty(BOTTOM, false));
        }

        @Override
        public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
            return state
                    .withProperty(TOP, world.getBlockState(pos.up()).getBlock() == this)
                    .withProperty(BOTTOM, world.getBlockState(pos.down()).getBlock() == this);
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState();
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return 0;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, TOP, BOTTOM);
        }
    }

    private static final class ObeliskBlock extends Block {
        private static final PropertyBool TOP = PropertyBool.create("top");
        private static final PropertyBool BOTTOM = PropertyBool.create("bottom");

        private ObeliskBlock() {
            super(Material.ROCK);
            setDefaultState(blockState.getBaseState()
                    .withProperty(TOP, false)
                    .withProperty(BOTTOM, false));
        }

        @Override
        public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
            return state
                    .withProperty(TOP, world.getBlockState(pos.up()).getBlock() == this)
                    .withProperty(BOTTOM, world.getBlockState(pos.down()).getBlock() == this);
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState();
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return 0;
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, TOP, BOTTOM);
        }
    }

    private static final class AltarBlock extends Block implements ITileEntityProvider {
        private static final PropertyBool PX = PropertyBool.create("px");
        private static final PropertyBool NX = PropertyBool.create("nx");
        private static final PropertyBool PZ = PropertyBool.create("pz");
        private static final PropertyBool NZ = PropertyBool.create("nz");

        private AltarBlock() {
            super(Material.ROCK);
            setDefaultState(blockState.getBaseState()
                    .withProperty(PX, false)
                    .withProperty(NX, false)
                    .withProperty(PZ, false)
                    .withProperty(NZ, false));
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new AltarTileEntity();
        }

        @Override
        public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
            return state
                    .withProperty(PX, isAltar(world, pos.east()))
                    .withProperty(NX, isAltar(world, pos.west()))
                    .withProperty(PZ, isAltar(world, pos.south()))
                    .withProperty(NZ, isAltar(world, pos.north()));
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState();
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return 0;
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND) {
                return false;
            }
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!(tile instanceof AltarTileEntity)) {
                return false;
            }
            AltarTileEntity altar = (AltarTileEntity) tile;
            ItemStack held = playerIn.getHeldItem(hand);
            if (playerIn.isSneaking() && held.isEmpty()) {
                if (worldIn.isRemote) {
                    return true;
                }
                ItemStack removed = altar.removeOffering();
                if (!removed.isEmpty() && !playerIn.addItemStackToInventory(removed)) {
                    EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, removed);
                    worldIn.spawnEntity(item);
                }
                return true;
            }
            if (held.isEmpty()) {
                if (worldIn.isRemote) {
                    return true;
                }
                AltarInfo info = AltarInfo.scan(worldIn, pos);
                playerIn.sendStatusMessage(new TextComponentString(
                        "祭坛：" + info.getAltarCount()
                                + "，容量：" + formatAltarValue(info.getCapacity())
                                + "，力量：" + formatAltarValue(info.getPower())
                                + "，试剂：" + info.getReagentAmount() + "/" + info.getReagentCapacity()
                                + "；材料用石手/暗蚀焦点，祭品盘只计数值"), true);
                return true;
            }
            return false;
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof AltarTileEntity) {
                ItemStack stack = ((AltarTileEntity) tile).getOffering();
                if (!stack.isEmpty()) {
                    EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
                    worldIn.spawnEntity(item);
                }
            }
            super.breakBlock(worldIn, pos, state);
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, PX, NX, PZ, NZ);
        }

        private boolean isAltar(IBlockAccess world, BlockPos pos) {
            return world.getBlockState(pos).getBlock() == this;
        }

        private String formatAltarValue(double value) {
            return value == (int) value ? Integer.toString((int) value) : Double.toString(value);
        }
    }

    private static final class WoodenBrewingStandBlock extends Block implements ITileEntityProvider {
        private static final PropertyBool HAS_BOTTLE_0 = PropertyBool.create("has_bottle_0");
        private static final PropertyBool HAS_BOTTLE_1 = PropertyBool.create("has_bottle_1");
        private static final PropertyBool HAS_BOTTLE_2 = PropertyBool.create("has_bottle_2");

        private WoodenBrewingStandBlock() {
            super(Material.WOOD);
            setDefaultState(blockState.getBaseState()
                    .withProperty(HAS_BOTTLE_0, false)
                    .withProperty(HAS_BOTTLE_1, false)
                    .withProperty(HAS_BOTTLE_2, false));
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new WoodenBrewingStandTileEntity();
        }

        @Override
        public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
                                    EntityLivingBase placer, ItemStack stack) {
            if (stack.hasDisplayName()) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile instanceof WoodenBrewingStandTileEntity) {
                    ((WoodenBrewingStandTileEntity) tile).setCustomName(stack.getDisplayName());
                }
            }
        }

        @Override
        public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof WoodenBrewingStandTileEntity) {
                WoodenBrewingStandTileEntity stand = (WoodenBrewingStandTileEntity) tile;
                return state
                        .withProperty(HAS_BOTTLE_0, stand.hasBottle(0))
                        .withProperty(HAS_BOTTLE_1, stand.hasBottle(1))
                        .withProperty(HAS_BOTTLE_2, stand.hasBottle(2));
            }
            return state;
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND) {
                return false;
            }
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!(tile instanceof WoodenBrewingStandTileEntity)) {
                return false;
            }
            if (worldIn.isRemote) {
                return true;
            }
            playerIn.openGui(Eidolon.instance, ModGuiHandler.WOODEN_BREWING_STAND, worldIn, pos.getX(), pos.getY(), pos.getZ());
            playerIn.addStat(StatList.BREWINGSTAND_INTERACTION);
            return true;
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof WoodenBrewingStandTileEntity) {
                ((WoodenBrewingStandTileEntity) tile).dropContents();
            }
            super.breakBlock(worldIn, pos, state);
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState();
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return 0;
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, HAS_BOTTLE_0, HAS_BOTTLE_1, HAS_BOTTLE_2);
        }
    }

    private static final class IncubatorBlock extends Block implements ITileEntityProvider {
        private static final PropertyEnum<IncubatorHalf> HALF =
                PropertyEnum.create("half", IncubatorHalf.class);
        private static final AxisAlignedBB INCUBATOR_AABB =
                new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);

        private IncubatorBlock() {
            super(Material.IRON);
            setDefaultState(blockState.getBaseState().withProperty(HALF, IncubatorHalf.BOTTOM));
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return (meta & 1) == 0 ? new IncubatorTileEntity() : null;
        }

        @Override
        public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
            return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
        }

        @Override
        public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
                                    EntityLivingBase placer, ItemStack stack) {
            worldIn.setBlockState(pos.up(), getDefaultState().withProperty(HALF, IncubatorHalf.TOP), 3);
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            if (state.getValue(HALF) == IncubatorHalf.BOTTOM) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile instanceof IncubatorTileEntity) {
                    ((IncubatorTileEntity) tile).dropContents();
                }
            }
            BlockPos otherPos = state.getValue(HALF) == IncubatorHalf.TOP ? pos.down() : pos.up();
            IBlockState otherState = worldIn.getBlockState(otherPos);
            if (otherState.getBlock() == this) {
                worldIn.setBlockToAir(otherPos);
            }
            super.breakBlock(worldIn, pos, state);
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND) {
                return false;
            }
            BlockPos tilePos = state.getValue(HALF) == IncubatorHalf.TOP ? pos.down() : pos;
            TileEntity tile = worldIn.getTileEntity(tilePos);
            if (!(tile instanceof IncubatorTileEntity)) {
                return false;
            }
            if (worldIn.isRemote) {
                return true;
            }
            playerIn.openGui(Eidolon.instance, ModGuiHandler.INCUBATOR, worldIn, tilePos.getX(), tilePos.getY(), tilePos.getZ());
            return true;
        }

        @Override
        public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
            BlockPos otherPos = state.getValue(HALF) == IncubatorHalf.TOP ? pos.down() : pos.up();
            if (worldIn.getBlockState(otherPos).getBlock() != this) {
                worldIn.setBlockToAir(pos);
            }
        }

        @Override
        public int quantityDropped(IBlockState state, int fortune, Random random) {
            return 1;
        }

        @Override
        public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
            return INCUBATOR_AABB;
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState().withProperty(HALF, (meta & 1) == 0 ? IncubatorHalf.BOTTOM : IncubatorHalf.TOP);
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return state.getValue(HALF) == IncubatorHalf.TOP ? 1 : 0;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, HALF);
        }
    }

    private static final class CisternBlock extends Block implements ITileEntityProvider {
        private static final PropertyBool TOP = PropertyBool.create("top");
        private static final PropertyBool BOTTOM = PropertyBool.create("bottom");
        private static final AxisAlignedBB CISTERN_AABB =
                new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);

        private CisternBlock() {
            super(Material.GLASS);
            setDefaultState(blockState.getBaseState()
                    .withProperty(TOP, false)
                    .withProperty(BOTTOM, false));
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new CisternTileEntity();
        }

        @Override
        public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
            return state
                    .withProperty(TOP, world.getBlockState(pos.up()).getBlock() == this)
                    .withProperty(BOTTOM, world.getBlockState(pos.down()).getBlock() == this);
        }

        @Override
        public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
            return CISTERN_AABB;
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.TRANSLUCENT;
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND) {
                return false;
            }
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!(tile instanceof CisternTileEntity)) {
                return false;
            }
            if (worldIn.isRemote) {
                return true;
            }

            CisternTileEntity cistern = (CisternTileEntity) tile;
            ItemStack held = playerIn.getHeldItem(hand);
            if (held.getItem() == Items.BUCKET
                    && !cistern.getTank().isEmpty()
                    && cistern.getTank().getContents().reagent == ReagentRegistry.STEAM
                    && cistern.getTank().getContents().amount >= Fluid.BUCKET_VOLUME) {
                cistern.getTank().drain(Fluid.BUCKET_VOLUME);
                cistern.onContentsChanged();
                if (!playerIn.capabilities.isCreativeMode) {
                    ItemStack waterBucket = new ItemStack(Items.WATER_BUCKET);
                    if (held.getCount() == 1) {
                        playerIn.setHeldItem(hand, waterBucket);
                    } else {
                        held.shrink(1);
                        if (!playerIn.addItemStackToInventory(waterBucket)) {
                            EntityItem item = new EntityItem(worldIn,
                                    pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, waterBucket);
                            worldIn.spawnEntity(item);
                        }
                    }
                }
                worldIn.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.75F, 1.0F);
                return true;
            }

            IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(held.copy());
            FluidStack containedFluid = fluidHandler == null ? null : fluidHandler.drain(Integer.MAX_VALUE, false);
            if (containedFluid != null && containedFluid.amount > 0) {
                ReagentStack reagent = new ReagentStack(ReagentRegistry.STEAM, containedFluid.amount);
                int accepted = cistern.getTank().fill(worldIn, pos, reagent);
                if (accepted > 0) {
                    if (!playerIn.capabilities.isCreativeMode) {
                        fluidHandler.drain(accepted, true);
                        playerIn.setHeldItem(hand, fluidHandler.getContainer());
                    }
                    cistern.onContentsChanged();
                    worldIn.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.75F, 1.1F);
                } else {
                    worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_BASS, SoundCategory.BLOCKS, 0.45F, 0.65F);
                }
                return true;
            }

            if (held.isEmpty()) {
                if (playerIn.isSneaking()) {
                    cistern.getTank().clear();
                    cistern.onContentsChanged();
                    worldIn.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.35F, 1.4F);
                    return true;
                }
                String reagentName = cistern.getTank().isEmpty()
                        ? "空"
                        : formatReagentName(cistern.getTank().getContents().reagent.getRegistryName().getPath());
                playerIn.sendStatusMessage(new TextComponentString(
                        "蓄水罐：" + reagentName + " "
                                + cistern.getTank().getContents().amount + "/" + cistern.getTank().getCapacity()
                                + "，压力 " + String.format("%.2f", cistern.getTank().getPressure())), true);
                return true;
            }

            return false;
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState();
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return 0;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, TOP, BOTTOM);
        }

        private String formatReagentName(String name) {
            if ("steam".equals(name)) {
                return "蒸汽";
            }
            if ("esprit".equals(name)) {
                return "灵质";
            }
            if ("crimsol".equals(name)) {
                return "绯液";
            }
            return name;
        }
    }

    private static class HorizontalFacingBlock extends Block {
        private static final PropertyDirection FACING =
                PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

        private HorizontalFacingBlock(Material material) {
            super(material);
            setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        }

        @Override
        public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
                                                float hitX, float hitY, float hitZ, int meta,
                                                EntityLivingBase placer) {
            return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return state.getValue(FACING).getHorizontalIndex();
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, FACING);
        }
    }

    private static final class SoulEnchanterBlock extends HorizontalFacingBlock implements ITileEntityProvider {
        private SoulEnchanterBlock() {
            super(Material.ROCK);
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new SoulEnchanterTileEntity();
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND) {
                return false;
            }
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!(tile instanceof SoulEnchanterTileEntity)) {
                return false;
            }
            if (worldIn.isRemote) {
                return true;
            }
            playerIn.openGui(Eidolon.instance, ModGuiHandler.SOUL_ENCHANTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof SoulEnchanterTileEntity) {
                ((SoulEnchanterTileEntity) tile).dropContents();
            }
            super.breakBlock(worldIn, pos, state);
        }
    }

    private static class SmallDecorBlock extends Block {
        private SmallDecorBlock(Material material) {
            super(material);
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }
    }

    private static final class EffigyBlock extends HorizontalFacingBlock implements ITileEntityProvider {
        private EffigyBlock(Material material) {
            super(material);
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new EffigyTileEntity();
        }
    }

    private static final class GobletBlock extends SmallDecorBlock implements ITileEntityProvider {
        private GobletBlock() {
            super(Material.IRON);
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new GobletTileEntity();
        }
    }

    private static final class RitualHolderBlock extends HorizontalFacingBlock implements ITileEntityProvider {
        private final boolean focus;

        private RitualHolderBlock(Material material, boolean focus) {
            super(material);
            this.focus = focus;
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return focus ? new NecroticFocusTileEntity() : new StoneHandTileEntity();
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND) {
                return false;
            }
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!(tile instanceof ItemHolderTileEntity)) {
                return false;
            }
            if (worldIn.isRemote) {
                return true;
            }
            return ((ItemHolderTileEntity) tile).activate(playerIn, hand);
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof ItemHolderTileEntity) {
                ((ItemHolderTileEntity) tile).dropContents();
            }
            super.breakBlock(worldIn, pos, state);
        }
    }

    private static final class BrazierBlock extends SmallDecorBlock implements ITileEntityProvider {
        private BrazierBlock() {
            super(Material.IRON);
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new BrazierTileEntity();
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND) {
                return false;
            }
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!(tile instanceof BrazierTileEntity)) {
                return false;
            }
            if (worldIn.isRemote) {
                return true;
            }
            return ((BrazierTileEntity) tile).activate(playerIn, hand);
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof BrazierTileEntity) {
                ((BrazierTileEntity) tile).dropContents();
            }
            super.breakBlock(worldIn, pos, state);
        }
    }

    private static final class OffertoryPlateBlock extends SmallDecorBlock implements ITileEntityProvider {
        private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.125D, 0.875D);
        private final Item plateItem;

        private OffertoryPlateBlock(Item plateItem) {
            super(Material.IRON);
            this.plateItem = plateItem;
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new OffertoryPlateTileEntity();
        }

        @Override
        public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
            return BOUNDS;
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND) {
                return false;
            }
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!(tile instanceof OffertoryPlateTileEntity)) {
                return false;
            }
            if (worldIn.isRemote) {
                return true;
            }
            return ((OffertoryPlateTileEntity) tile).activate(playerIn, hand);
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof OffertoryPlateTileEntity) {
                ((OffertoryPlateTileEntity) tile).dropContents();
            }
            super.breakBlock(worldIn, pos, state);
        }

        @Override
        public Item getItemDropped(IBlockState state, Random rand, int fortune) {
            return plateItem;
        }
    }

    private static final class WorktableBlock extends SmallDecorBlock implements ITileEntityProvider {
        private WorktableBlock() {
            super(Material.WOOD);
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new WorktableTileEntity();
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND) {
                return false;
            }
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!(tile instanceof WorktableTileEntity)) {
                return false;
            }
            if (worldIn.isRemote) {
                return true;
            }
            playerIn.openGui(Eidolon.instance, ModGuiHandler.WORKTABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof WorktableTileEntity) {
                WorktableTileEntity worktable = (WorktableTileEntity) tile;
                for (ItemStack stack : worktable.getInventory()) {
                    if (!stack.isEmpty()) {
                        EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack.copy());
                        worldIn.spawnEntity(item);
                    }
                }
            }
            super.breakBlock(worldIn, pos, state);
        }
    }

    private static final class CrucibleBlock extends SmallDecorBlock implements ITileEntityProvider {
        private static final PropertyBool HAS_CONTENTS = PropertyBool.create("has_contents");

        private CrucibleBlock() {
            super(Material.IRON);
            setDefaultState(blockState.getBaseState().withProperty(HAS_CONTENTS, false));
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new CrucibleTileEntity();
        }

        @Override
        public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
            TileEntity tile = worldIn.getTileEntity(pos);
            return state.withProperty(HAS_CONTENTS,
                    tile instanceof CrucibleTileEntity && ((CrucibleTileEntity) tile).hasContents());
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND) {
                return false;
            }
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!(tile instanceof CrucibleTileEntity)) {
                return false;
            }
            if (worldIn.isRemote) {
                return true;
            }

            CrucibleTileEntity crucible = (CrucibleTileEntity) tile;
            ItemStack held = playerIn.getHeldItem(hand);
            IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(held.copy());
            FluidStack containedFluid = fluidHandler == null ? null : fluidHandler.drain(Integer.MAX_VALUE, false);
            if (containedFluid != null) {
                if (crucible.fill(containedFluid)) {
                    if (!playerIn.capabilities.isCreativeMode) {
                        fluidHandler.drain(containedFluid.amount, true);
                        playerIn.setHeldItem(hand, fluidHandler.getContainer());
                    }
                    playCrucibleSound(worldIn, pos, SoundEvents.ITEM_BUCKET_EMPTY, 0.8F, 1.0F);
                    spawnCrucibleParticles(worldIn, pos, EnumParticleTypes.WATER_SPLASH, 8);
                    sendCrucibleVisual(worldIn, pos, VisualEffectPacket.MAGIC_BURST, 0.45F, 0.75F, 1.0F);
                } else {
                    playCrucibleSound(worldIn, pos, SoundEvents.BLOCK_NOTE_BASS, 0.45F, 0.6F);
                }
                return true;
            }
            if (held.isEmpty()) {
                if (playerIn.isSneaking()) {
                    crucible.reset();
                    sendCrucibleVisual(worldIn, pos, VisualEffectPacket.EXTINGUISH, 0.55F, 0.55F, 0.6F);
                    return true;
                }
                if (!crucible.hasFluid()) {
                    if (crucible.getSteamProgress() > 0 || !crucible.getTank().isEmpty()) {
                        ReagentStack steamStack = crucible.getTank().getContents();
                        int steam = crucible.getSteamProgress() + (steamStack == null ? 0 : steamStack.amount);
                        playerIn.sendStatusMessage(new TextComponentString(
                                "坩埚：蒸汽 " + steam + "/" + Fluid.BUCKET_VOLUME), true);
                        return true;
                    }
                    playCrucibleSound(worldIn, pos, SoundEvents.BLOCK_NOTE_BASS, 0.45F, 0.6F);
                    return true;
                }
                boolean hadAction = crucible.hasPendingStepAction();
                ItemStack result = crucible.commitStep();
                if (!result.isEmpty()) {
                    EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, result);
                    item.motionX = (worldIn.rand.nextDouble() - 0.5D) * 0.04D;
                    item.motionY = 0.22D;
                    item.motionZ = (worldIn.rand.nextDouble() - 0.5D) * 0.04D;
                    item.setDefaultPickupDelay();
                    worldIn.spawnEntity(item);
                    sendCrucibleVisual(worldIn, pos, VisualEffectPacket.CRUCIBLE_SUCCESS, 0.65F, 0.35F, 1.0F);
                    playCrucibleSound(worldIn, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, 0.45F, 1.35F);
                    spawnCrucibleParticles(worldIn, pos, EnumParticleTypes.SPELL_WITCH, 10);
                } else if (hadAction && crucible.hasFluid()) {
                    playCrucibleSound(worldIn, pos, SoundEvents.BLOCK_WATER_AMBIENT, 0.45F, 1.6F);
                    spawnCrucibleParticles(worldIn, pos, EnumParticleTypes.SPELL, 8);
                    sendCrucibleVisual(worldIn, pos, VisualEffectPacket.MAGIC_BURST, 0.55F, 0.8F, 1.0F);
                } else if (!hadAction || !crucible.hasFluid()) {
                    sendCrucibleVisual(worldIn, pos, VisualEffectPacket.CRUCIBLE_FAIL, 0.45F, 0.45F, 0.45F);
                }
                return true;
            }

            if (!crucible.hasFluid()) {
                playCrucibleSound(worldIn, pos, SoundEvents.BLOCK_NOTE_BASS, 0.45F, 0.6F);
                return true;
            }
            if (CrucibleRecipes.isStirrer(held)) {
                crucible.stir(held);
                playCrucibleSound(worldIn, pos, SoundEvents.BLOCK_WATER_AMBIENT, 0.45F, 1.6F);
                spawnCrucibleParticles(worldIn, pos, EnumParticleTypes.WATER_SPLASH, 4);
                sendCrucibleVisual(worldIn, pos, VisualEffectPacket.MAGIC_BURST, 0.55F, 0.8F, 1.0F);
                return true;
            }
            if (crucible.addOne(held)) {
                playCrucibleSound(worldIn, pos, SoundEvents.ENTITY_ITEM_PICKUP, 0.35F, 0.6F);
                spawnCrucibleParticles(worldIn, pos, EnumParticleTypes.SPELL, 3);
                sendCrucibleVisual(worldIn, pos, VisualEffectPacket.MAGIC_BURST, 0.7F, 0.35F, 1.0F);
                return true;
            }
            return false;
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!worldIn.isRemote && tile instanceof CrucibleTileEntity) {
                for (ItemStack stack : ((CrucibleTileEntity) tile).getDroppedStacks()) {
                    if (!stack.isEmpty()) {
                        EntityItem item = new EntityItem(worldIn,
                                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack.copy());
                        worldIn.spawnEntity(item);
                    }
                }
            }
            super.breakBlock(worldIn, pos, state);
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState();
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return 0;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, HAS_CONTENTS);
        }

        private void playCrucibleSound(World world, BlockPos pos, net.minecraft.util.SoundEvent sound, float volume, float pitch) {
            world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D,
                    sound, SoundCategory.BLOCKS, volume, pitch);
        }

        private void spawnCrucibleParticles(World world, BlockPos pos, EnumParticleTypes particle, int count) {
            if (world instanceof WorldServer) {
                ((WorldServer) world).spawnParticle(particle,
                        pos.getX() + 0.5D, pos.getY() + 0.9D, pos.getZ() + 0.5D,
                        count, 0.18D, 0.12D, 0.18D, 0.02D);
            }
        }

        private void sendCrucibleVisual(World world, BlockPos pos, int effect, float r, float g, float b) {
            VisualEffectPacket.sendAround(world, pos.getX() + 0.5D, pos.getY() + 0.95D, pos.getZ() + 0.5D,
                    VisualEffectPacket.at(effect, pos.getX() + 0.5D, pos.getY() + 0.95D, pos.getZ() + 0.5D, r, g, b));
        }
    }

    private static final class ResearchTableBlock extends Block implements ITileEntityProvider {
        private static final PropertyDirection FACING =
                PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

        private ResearchTableBlock() {
            super(Material.WOOD);
            setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new ResearchTableTileEntity();
        }

        @Override
        public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
                                                float hitX, float hitY, float hitZ, int meta,
                                                EntityLivingBase placer) {
            return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND) {
                return false;
            }
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!(tile instanceof ResearchTableTileEntity)) {
                return false;
            }
            if (worldIn.isRemote) {
                return true;
            }
            playerIn.openGui(Eidolon.instance, ModGuiHandler.RESEARCH_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof ResearchTableTileEntity) {
                ResearchTableTileEntity researchTable = (ResearchTableTileEntity) tile;
                for (int i = 0; i < researchTable.getSizeInventory(); i++) {
                    ItemStack stack = researchTable.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack.copy());
                        worldIn.spawnEntity(item);
                    }
                }
            }
            super.breakBlock(worldIn, pos, state);
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return state.getValue(FACING).getHorizontalIndex();
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, FACING);
        }
    }

    private static final class AttachableDecorBlock extends Block {
        private static final PropertyDirection FACING =
                PropertyDirection.create("facing", facing -> facing != EnumFacing.DOWN);

        private AttachableDecorBlock(Material material) {
            super(material);
            setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        }

        @Override
        public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
                                                float hitX, float hitY, float hitZ, int meta,
                                                EntityLivingBase placer) {
            if (facing == EnumFacing.UP || facing.getAxis().isHorizontal()) {
                return getDefaultState().withProperty(FACING, facing);
            }
            return getDefaultState();
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            EnumFacing facing = EnumFacing.byIndex(meta);
            return getDefaultState().withProperty(FACING, facing == EnumFacing.DOWN ? EnumFacing.UP : facing);
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return state.getValue(FACING).getIndex();
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, FACING);
        }
    }

    private static final class HerbBlock extends Block implements IGrowable {
        private static final PropertyInteger AGE = PropertyInteger.create("age", 0, 1);
        private static final AxisAlignedBB[] BOUNDS = {
                new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.25D, 0.6875D),
                new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D)
        };

        private HerbBlock() {
            super(Material.PLANTS);
            setTickRandomly(true);
            setDefaultState(blockState.getBaseState().withProperty(AGE, 0));
        }

        @Override
        public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
            return BOUNDS[state.getValue(AGE)];
        }

        @Override
        public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
            return NULL_AABB;
        }

        @Override
        public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
            return super.canPlaceBlockAt(worldIn, pos) && canSustain(worldIn.getBlockState(pos.down()));
        }

        @Override
        public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
            if (!canSustain(worldIn.getBlockState(pos.down()))) {
                dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }

        @Override
        public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
            if (!worldIn.isRemote && state.getValue(AGE) == 0 && rand.nextInt(20) == 0) {
                worldIn.setBlockState(pos, state.withProperty(AGE, 1), 2);
            }
        }

        @Override
        public int quantityDropped(IBlockState state, int fortune, Random random) {
            return state.getValue(AGE) == 1 && random.nextFloat() < 0.5F ? 2 : 1;
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState().withProperty(AGE, meta & 1);
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return state.getValue(AGE);
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, AGE);
        }

        @Override
        public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
            return state.getValue(AGE) == 0;
        }

        @Override
        public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
            return true;
        }

        @Override
        public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
            worldIn.setBlockState(pos, state.withProperty(AGE, 1), 2);
        }

        private boolean canSustain(IBlockState state) {
            Block block = state.getBlock();
            return block == net.minecraft.init.Blocks.GRASS
                    || block == net.minecraft.init.Blocks.DIRT
                    || block == net.minecraft.init.Blocks.FARMLAND;
        }
    }

    private static final class SimpleLeavesBlock extends Block {
        private SimpleLeavesBlock() {
            super(Material.LEAVES);
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT_MIPPED;
        }
    }

    private static final class SimpleSaplingBlock extends Block implements IGrowable {
        private static final AxisAlignedBB SAPLING_AABB =
                new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);

        private SimpleSaplingBlock() {
            super(Material.PLANTS);
            setTickRandomly(true);
        }

        @Override
        public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
            if (!worldIn.isRemote
                    && worldIn.getLightFromNeighbors(pos.up()) >= 9
                    && rand.nextInt(7) == 0) {
                grow(worldIn, rand, pos, state);
            }
        }

        @Override
        public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
            return SAPLING_AABB;
        }

        @Override
        public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
            return NULL_AABB;
        }

        @Override
        public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
            return super.canPlaceBlockAt(worldIn, pos) && canSustain(worldIn.getBlockState(pos.down()));
        }

        @Override
        public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
            if (!canSustain(worldIn.getBlockState(pos.down()))) {
                dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }

        @Override
        public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
            return true;
        }

        @Override
        public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
            return true;
        }

        @Override
        public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
            if (!worldIn.isRemote) {
                IllwoodTreeGenerator.generateAt(worldIn, rand, pos);
            }
        }

        private boolean canSustain(IBlockState state) {
            Block block = state.getBlock();
            return block == net.minecraft.init.Blocks.GRASS
                    || block == net.minecraft.init.Blocks.DIRT
                    || block == net.minecraft.init.Blocks.FARMLAND;
        }
    }

    private static final class EnchantedAshBlock extends Block {
        private static final PropertyEnum<AshSide> NORTH = PropertyEnum.create("north", AshSide.class);
        private static final PropertyEnum<AshSide> EAST = PropertyEnum.create("east", AshSide.class);
        private static final PropertyEnum<AshSide> SOUTH = PropertyEnum.create("south", AshSide.class);
        private static final PropertyEnum<AshSide> WEST = PropertyEnum.create("west", AshSide.class);
        private static final AxisAlignedBB ASH_AABB =
                new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);
        private static final AxisAlignedBB UNDEAD_BARRIER_AABB =
                new AxisAlignedBB(0.0D, -4.0D, 0.0D, 1.0D, 5.0D, 1.0D);

        private EnchantedAshBlock() {
            super(Material.CIRCUITS);
            setDefaultState(blockState.getBaseState()
                    .withProperty(NORTH, AshSide.NONE)
                    .withProperty(EAST, AshSide.NONE)
                    .withProperty(SOUTH, AshSide.NONE)
                    .withProperty(WEST, AshSide.NONE));
        }

        @Override
        public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
            return state
                    .withProperty(NORTH, getSide(worldIn, pos, EnumFacing.NORTH))
                    .withProperty(EAST, getSide(worldIn, pos, EnumFacing.EAST))
                    .withProperty(SOUTH, getSide(worldIn, pos, EnumFacing.SOUTH))
                    .withProperty(WEST, getSide(worldIn, pos, EnumFacing.WEST));
        }

        @Override
        public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
            return ASH_AABB;
        }

        @Override
        public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
            return NULL_AABB;
        }

        @Override
        public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                          List<AxisAlignedBB> collidingBoxes, Entity entityIn,
                                          boolean isActualState) {
            if (isBlocked(entityIn)) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, UNDEAD_BARRIER_AABB);
            }
        }

        private boolean isBlocked(Entity entity) {
            if (entity == null) {
                return false;
            }
            if (isEffectiveUndead(entity)) {
                return true;
            }
            for (Entity passenger : entity.getPassengers()) {
                if (isEffectiveUndead(passenger)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isEffectiveUndead(Entity entity) {
            return entity instanceof EntityLivingBase
                    && elucent.eidolon.Eidolon.getCreatureAttribute((EntityLivingBase) entity)
                    == net.minecraft.entity.EnumCreatureAttribute.UNDEAD;
        }

        @Override
        public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
            return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
        }

        @Override
        public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
            if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP)) {
                dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.CUTOUT;
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState();
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return 0;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST);
        }

        private AshSide getSide(IBlockAccess world, BlockPos pos, EnumFacing facing) {
            BlockPos sidePos = pos.offset(facing);
            IBlockState sideState = world.getBlockState(sidePos);
            if (!isSolidAbove(world, pos)
                    && canPlaceOnTopOf(world, sidePos, sideState)
                    && canConnectTo(world.getBlockState(sidePos.up()))) {
                return sideState.isSideSolid(world, sidePos, facing.getOpposite()) ? AshSide.UP : AshSide.SIDE;
            }
            if (canConnectTo(sideState)) {
                return AshSide.SIDE;
            }
            return sideState.isSideSolid(world, sidePos, facing.getOpposite())
                    || !canConnectTo(world.getBlockState(sidePos.down())) ? AshSide.NONE : AshSide.SIDE;
        }

        private boolean isSolidAbove(IBlockAccess world, BlockPos pos) {
            BlockPos above = pos.up();
            return world.getBlockState(above).isSideSolid(world, above, EnumFacing.DOWN);
        }

        private boolean canPlaceOnTopOf(IBlockAccess world, BlockPos pos, IBlockState state) {
            return state.isSideSolid(world, pos, EnumFacing.UP);
        }

        private boolean canConnectTo(IBlockState state) {
            return state.getBlock() == this;
        }
    }

    private static final class GlassTubeBlock extends Block implements ITileEntityProvider {
        private static final PropertyDirection IN = PropertyDirection.create("in");
        private static final PropertyDirection OUT = PropertyDirection.create("out");
        private static final PropertyBool NORTH = PropertyBool.create("north");
        private static final PropertyBool EAST = PropertyBool.create("east");
        private static final PropertyBool SOUTH = PropertyBool.create("south");
        private static final PropertyBool WEST = PropertyBool.create("west");
        private static final PropertyBool UP = PropertyBool.create("up");
        private static final PropertyBool DOWN = PropertyBool.create("down");
        private static final AxisAlignedBB TUBE_AABB =
                new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D);

        private GlassTubeBlock() {
            super(Material.GLASS);
            setDefaultState(blockState.getBaseState()
                    .withProperty(IN, EnumFacing.DOWN)
                    .withProperty(OUT, EnumFacing.UP)
                    .withProperty(NORTH, false)
                    .withProperty(EAST, false)
                    .withProperty(SOUTH, false)
                    .withProperty(WEST, false)
                    .withProperty(UP, false)
                    .withProperty(DOWN, false));
        }

        private boolean setOutput(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
            TileEntity tile = world.getTileEntity(pos);
            if (!(tile instanceof GlassTubeTileEntity)) {
                return false;
            }
            GlassTubeTileEntity tube = (GlassTubeTileEntity) tile;
            if (facing == tube.getInput()) {
                return false;
            }
            boolean changed = world.isRemote || tube.setOutput(facing);
            if (changed && !world.isRemote) {
                world.playSound(null, pos, SoundEvents.BLOCK_GLASS_HIT, SoundCategory.BLOCKS, 0.45F, 1.4F);
            }
            return changed;
        }

        @Override
        public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                                float hitZ, int meta, EntityLivingBase placer) {
            return getDefaultState()
                    .withProperty(IN, facing.getOpposite())
                    .withProperty(OUT, facing);
        }

        @Override
        public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
            super.onBlockAdded(worldIn, pos, state);
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof GlassTubeTileEntity) {
                ((GlassTubeTileEntity) tile).setDirections(state.getValue(IN), state.getValue(OUT));
            }
        }

        @Override
        public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                    ItemStack stack) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof GlassTubeTileEntity) {
                ((GlassTubeTileEntity) tile).setDirections(state.getValue(IN), state.getValue(OUT));
            }
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new GlassTubeTileEntity();
        }

        @Override
        public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof GlassTubeTileEntity) {
                GlassTubeTileEntity tube = (GlassTubeTileEntity) tile;
                state = state.withProperty(IN, tube.getInput()).withProperty(OUT, tube.getOutput());
            }
            return state
                    .withProperty(NORTH, isGlassTube(worldIn, pos.north()))
                    .withProperty(EAST, isGlassTube(worldIn, pos.east()))
                    .withProperty(SOUTH, isGlassTube(worldIn, pos.south()))
                    .withProperty(WEST, isGlassTube(worldIn, pos.west()))
                    .withProperty(UP, isGlassTube(worldIn, pos.up()))
                    .withProperty(DOWN, isGlassTube(worldIn, pos.down()));
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                        EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (hand != EnumHand.MAIN_HAND || !playerIn.getHeldItem(hand).isEmpty()) {
                return false;
            }
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!(tile instanceof GlassTubeTileEntity)) {
                return false;
            }
            if (worldIn.isRemote) {
                return true;
            }
            GlassTubeTileEntity tube = (GlassTubeTileEntity) tile;
            setOutput(worldIn, pos, state, facing);
            String reagentName = tube.getTank().isEmpty()
                    ? "空"
                    : formatReagentName(tube.getTank().getContents().reagent.getRegistryName().getPath());
            playerIn.sendStatusMessage(new TextComponentString(
                    "玻璃管：" + reagentName + " "
                            + tube.getTank().getContents().amount + "/" + tube.getTank().getCapacity()
                            + "，输入 " + formatFacingName(tube.getInput())
                            + "，输出 " + formatFacingName(tube.getOutput())
                            + "，压力 " + String.format("%.2f", tube.getTank().getPressure())), true);
            return true;
        }

        @Override
        public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
            return TUBE_AABB;
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.TRANSLUCENT;
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState();
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return 0;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, IN, OUT, NORTH, EAST, SOUTH, WEST, UP, DOWN);
        }

        private boolean isGlassTube(IBlockAccess world, BlockPos pos) {
            if (world.getBlockState(pos).getBlock() == this) {
                return true;
            }
            return world.getTileEntity(pos) instanceof IReagentTankProvider;
        }

        private String formatReagentName(String name) {
            if ("steam".equals(name)) {
                return "蒸汽";
            }
            if ("esprit".equals(name)) {
                return "灵质";
            }
            if ("crimsol".equals(name)) {
                return "绯液";
            }
            return name;
        }

        private String formatFacingName(EnumFacing facing) {
            if (facing == EnumFacing.DOWN) {
                return "下";
            }
            if (facing == EnumFacing.UP) {
                return "上";
            }
            if (facing == EnumFacing.NORTH) {
                return "北";
            }
            if (facing == EnumFacing.SOUTH) {
                return "南";
            }
            if (facing == EnumFacing.WEST) {
                return "西";
            }
            if (facing == EnumFacing.EAST) {
                return "东";
            }
            return facing.getName();
        }
    }

    private abstract static class SimpleSlabBlock extends BlockSlab {
        public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

        private SimpleSlabBlock(Block modelBlock) {
            super(modelBlock.getDefaultState().getMaterial());
            useNeighborBrightness = true;
        }

        @Override
        public String getTranslationKey(int meta) {
            return getTranslationKey();
        }

        @Override
        public IProperty<?> getVariantProperty() {
            return VARIANT;
        }

        @Override
        public Comparable<?> getTypeForItem(ItemStack stack) {
            return Variant.DEFAULT;
        }

        @Override
        public int damageDropped(IBlockState state) {
            return 0;
        }
    }

    private static final class HalfSimpleSlabBlock extends SimpleSlabBlock {
        private HalfSimpleSlabBlock(Block modelBlock) {
            super(modelBlock);
            setDefaultState(blockState.getBaseState()
                    .withProperty(VARIANT, Variant.DEFAULT)
                    .withProperty(HALF, EnumBlockHalf.BOTTOM));
        }

        @Override
        public boolean isDouble() {
            return false;
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState()
                    .withProperty(VARIANT, Variant.DEFAULT)
                    .withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return state.getValue(HALF) == EnumBlockHalf.TOP ? 8 : 0;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, VARIANT, HALF);
        }
    }

    private static final class DoubleSimpleSlabBlock extends SimpleSlabBlock {
        private DoubleSimpleSlabBlock(Block modelBlock) {
            super(modelBlock);
            setDefaultState(blockState.getBaseState().withProperty(VARIANT, Variant.DEFAULT));
        }

        @Override
        public boolean isDouble() {
            return true;
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState().withProperty(VARIANT, Variant.DEFAULT);
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return 0;
        }

        @Override
        public Item getItemDropped(IBlockState state, Random rand, int fortune) {
            ResourceLocation name = getRegistryName();
            if (name == null || !name.getPath().startsWith("double_")) {
                return Items.AIR;
            }
            Item item = Item.getByNameOrId(Reference.MOD_ID + ":" + name.getPath().substring("double_".length()));
            return item == null ? Items.AIR : item;
        }

        @Override
        public int quantityDropped(Random random) {
            return 2;
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, VARIANT);
        }
    }

    private enum Variant implements IStringSerializable {
        DEFAULT;

        @Override
        public String getName() {
            return "default";
        }
    }

    private enum AshSide implements IStringSerializable {
        NONE("none"),
        SIDE("side"),
        UP("up");

        private final String name;

        AshSide(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private enum IncubatorHalf implements IStringSerializable {
        BOTTOM("bottom"),
        TOP("top");

        private final String name;

        IncubatorHalf(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
