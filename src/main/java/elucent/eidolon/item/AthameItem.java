package elucent.eidolon.item;

import elucent.eidolon.Reference;
import elucent.eidolon.registries.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockRedFlower;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class AthameItem extends EidolonSwordItem {
    private static final int PLANT_DESTROY_CHANCE = 3;
    private static final int PLANT_ITEM_DROP_CHANCE = 5;
    private static final List<HarvestEntry> HARVEST_ENTRIES = new ArrayList<>();

    static {
        registerBuiltIn("avennian_sprig_from_fern",
                new ItemStack(Blocks.TALLGRASS, 1, BlockTallGrass.EnumType.FERN.getMeta()),
                () -> new ItemStack(ModBlocks.AVENNIAN_SPRIG_ITEM),
                "gui.eidolon.athame_harvest.source.fern");
        registerBuiltIn("merammer_root_from_oxeye_daisy",
                new ItemStack(Blocks.RED_FLOWER, 1, BlockRedFlower.EnumFlowerType.OXEYE_DAISY.getMeta()),
                () -> new ItemStack(ModBlocks.MERAMMER_ROOT_ITEM),
                "gui.eidolon.athame_harvest.source.oxeye_daisy");
        registerBuiltIn("oanna_bloom_from_lily_pad",
                new ItemStack(Blocks.WATERLILY),
                () -> new ItemStack(ModBlocks.OANNA_BLOOM_ITEM),
                "gui.eidolon.athame_harvest.source.lily_pad");
        registerBuiltIn("sildrian_seed_from_jungle_leaves",
                new ItemStack(Blocks.LEAVES, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()),
                () -> new ItemStack(ModBlocks.SILDRIAN_SEED_ITEM),
                "gui.eidolon.athame_harvest.source.jungle_leaves");
    }

    public AthameItem(ToolMaterial material) {
        super(material, 3.0D, -1.6D);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLooting(LootingLevelEvent event) {
        DamageSource source = event.getDamageSource();
        if (source != null && source.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
            if (attacker.getHeldItemMainhand().getItem() instanceof AthameItem) {
                event.setLootingLevel(event.getLootingLevel() * 2 + 1);
            }
        }
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source == null || !(source.getTrueSource() instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
        if (!(attacker.getHeldItemMainhand().getItem() instanceof AthameItem)) {
            return;
        }
        EntityLivingBase target = event.getEntityLiving();
        if (target instanceof EntityEnderman
                || target instanceof EntityEndermite
                || target instanceof EntityDragon
                || target instanceof EntityShulker) {
            event.setAmount(event.getAmount() * 4.0F);
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        float hardness = state.getBlockHardness(worldIn, pos);
        ItemStack harvest = getHarvestable(state);
        if (!isSoftPlant(state) || hardness < 0.0F || hardness >= 5.0F) {
            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        }
        if (!worldIn.isRemote) {
            spawnPlantBreakParticles(worldIn, pos, state, hitX, hitY, hitZ);
            worldIn.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS,
                    0.5F, 0.9F + itemRand.nextFloat() * 0.2F);
            if (itemRand.nextInt(PLANT_DESTROY_CHANCE) == 0) {
                BlockPos destroyPos = getDestroyPos(state, pos);
                worldIn.destroyBlock(destroyPos, false);
                if (itemRand.nextInt(PLANT_ITEM_DROP_CHANCE) == 0) {
                    if (!harvest.isEmpty()) {
                        worldIn.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS,
                                0.5F, 0.9F + itemRand.nextFloat() * 0.2F);
                        worldIn.spawnEntity(new EntityItem(worldIn,
                                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, harvest.copy()));
                    }
                    if (!player.capabilities.isCreativeMode) {
                        player.getHeldItem(hand).damageItem(1, player);
                    }
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

    private void spawnPlantBreakParticles(World world, BlockPos pos, IBlockState state,
                                          float hitX, float hitY, float hitZ) {
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                    pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ,
                    3, 0.08D, 0.08D, 0.08D, 0.05D, Block.getStateId(state));
        }
    }

    private BlockPos getDestroyPos(IBlockState state, BlockPos pos) {
        if (state.getBlock() == Blocks.DOUBLE_PLANT
                && state.getValue(BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
            return pos.down();
        }
        return pos;
    }

    private boolean isSoftPlant(IBlockState state) {
        Material material = state.getMaterial();
        return material == Material.PLANTS
                || material == Material.VINE
                || material == Material.LEAVES;
    }

    private ItemStack getHarvestable(IBlockState state) {
        ItemStack source = getSourceStack(state);
        if (source.isEmpty()) {
            return ItemStack.EMPTY;
        }
        for (HarvestEntry entry : HARVEST_ENTRIES) {
            if (entry.matches(source)) {
                return entry.getResult();
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack getSourceStack(IBlockState state) {
        Block block = state.getBlock();
        Item item = Item.getItemFromBlock(block);
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item, 1, block.damageDropped(state));
    }

    public static List<HarvestEntry> getHarvestEntries() {
        return Collections.unmodifiableList(new ArrayList<>(HARVEST_ENTRIES));
    }

    public static void addHarvestEntry(ResourceLocation id, Ingredient source, List<ItemStack> sourceStacks,
                                       ItemStack result, String sourceKey) {
        ItemStack resultCopy = result.copy();
        addHarvestEntry(id, source, sourceStacks, () -> resultCopy.copy(), sourceKey);
    }

    private static void addHarvestEntry(ResourceLocation id, Ingredient source, List<ItemStack> sourceStacks,
                                        Supplier<ItemStack> result, String sourceKey) {
        removeHarvestEntry(id);
        HARVEST_ENTRIES.add(new HarvestEntry(id, source, sourceStacks, result, sourceKey));
    }

    public static boolean removeHarvestEntry(ResourceLocation id) {
        return HARVEST_ENTRIES.removeIf(entry -> entry.getId().equals(id));
    }

    public static int removeHarvestEntriesByOutput(Ingredient output) {
        return removeHarvestEntries(entry -> output.apply(entry.getResult()));
    }

    public static int removeHarvestEntriesBySource(Ingredient source) {
        return removeHarvestEntries(entry -> entry.hasSourceMatching(source));
    }

    public static int removeAllHarvestEntries() {
        int count = HARVEST_ENTRIES.size();
        HARVEST_ENTRIES.clear();
        return count;
    }

    private static int removeHarvestEntries(java.util.function.Predicate<HarvestEntry> predicate) {
        int before = HARVEST_ENTRIES.size();
        HARVEST_ENTRIES.removeIf(predicate);
        return before - HARVEST_ENTRIES.size();
    }

    private static void registerBuiltIn(String name, ItemStack source, Supplier<ItemStack> result, String sourceKey) {
        addHarvestEntry(new ResourceLocation(Reference.MOD_ID, name), Ingredient.fromStacks(source),
                Collections.singletonList(source), result, sourceKey);
    }

    public static final class HarvestEntry {
        private final ResourceLocation id;
        private final Ingredient source;
        private final List<ItemStack> sources;
        private final Supplier<ItemStack> result;
        private final String sourceKey;

        private HarvestEntry(ResourceLocation id, Ingredient source, List<ItemStack> sources,
                             Supplier<ItemStack> result, String sourceKey) {
            this.id = id;
            this.source = source;
            this.sources = copySources(sources);
            this.result = result;
            this.sourceKey = sourceKey;
        }

        public ResourceLocation getId() {
            return id;
        }

        public List<ItemStack> getSources() {
            return copySources(sources);
        }

        public ItemStack getResult() {
            return result.get();
        }

        public String getSourceKey() {
            if (sourceKey != null && !sourceKey.isEmpty()) {
                return sourceKey;
            }
            return sources.isEmpty() ? "" : sources.get(0).getDisplayName();
        }

        private boolean matches(ItemStack stack) {
            return source.apply(stack);
        }

        private boolean hasSourceMatching(Ingredient ingredient) {
            for (ItemStack stack : sources) {
                if (ingredient.apply(stack)) {
                    return true;
                }
            }
            return false;
        }

        private static List<ItemStack> copySources(List<ItemStack> sources) {
            List<ItemStack> copy = new ArrayList<>();
            if (sources != null) {
                for (ItemStack source : sources) {
                    copy.add(source == null ? ItemStack.EMPTY : source.copy());
                }
            }
            if (copy.isEmpty()) {
                copy.add(ItemStack.EMPTY);
            }
            return copy;
        }
    }
}
