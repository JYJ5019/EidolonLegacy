package elucent.eidolon.world;

import com.google.common.base.Optional;
import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public final class ModernTemplatePlacer {
    private static final String RESOURCE_ROOT = "assets/eidolon/structures/";
    private static final LootPool[] LAB_LOOT = new LootPool[] {
            pool(2, 5,
                    entry("eidolon:zombie_heart", 20),
                    entry("eidolon:basic_ring", 15),
                    entry("eidolon:basic_amulet", 15),
                    entry("eidolon:basic_belt", 15),
                    enchantedBook(10)),
            pool(6, 9,
                    entry("eidolon:pewter_ingot", 10, 1, 4),
                    entry("eidolon:fungus_sprouts", 10, 1, 2),
                    entry("eidolon:pewter_inlay", 10, 1, 2),
                    entry("eidolon:soul_shard", 20, 1, 3),
                    entry("minecraft:bone", 15, 1, 4),
                    entry("minecraft:rotten_flesh", 15, 1, 4),
                    entry("minecraft:gold_ingot", 10, 1, 2),
                    entry("minecraft:book", 10, 1, 3))
    };
    private static final LootPool[] CATACOMB_LOOT = new LootPool[] {
            pool(4, 7,
                    entry("eidolon:pewter_ingot", 10, 1, 4),
                    entry("minecraft:coal", 15, 1, 2),
                    entry("eidolon:soul_shard", 20),
                    entry("minecraft:bone", 20, 1, 4),
                    entry("minecraft:arrow", 20, 3, 7),
                    entry("minecraft:bone_block", 10),
                    entry("minecraft:soul_sand", 5, 3, 7),
                    entry("eidolon:death_essence", 5),
                    entry("minecraft:chainmail_helmet", 5),
                    entry("minecraft:chainmail_chestplate", 5),
                    entry("minecraft:chainmail_leggings", 5),
                    entry("minecraft:chainmail_boots", 5),
                    entry("minecraft:iron_ingot", 15, 2, 5),
                    entry("minecraft:book", 10, 1, 3))
    };
    private static final LootPool[] COFFIN_LOOT = new LootPool[] {
            pool(3, 5,
                    entry("eidolon:zombie_heart", 20),
                    entry("minecraft:diamond", 20),
                    entry("eidolon:shadow_gem", 10),
                    entry("eidolon:basic_ring", 15),
                    entry("eidolon:basic_amulet", 15),
                    enchantedBook(10)),
            pool(6, 9,
                    entry("eidolon:arcane_gold_ingot", 10, 1, 3),
                    entry("eidolon:gold_inlay", 10, 1, 3),
                    entry("eidolon:death_essence", 25),
                    entry("minecraft:golden_helmet", 5),
                    entry("minecraft:golden_chestplate", 5),
                    entry("minecraft:golden_sword", 5),
                    entry("minecraft:skull", 5),
                    entry("minecraft:bone", 20, 1, 4),
                    entry("minecraft:rotten_flesh", 20, 1, 4),
                    entry("minecraft:gold_ingot", 15, 1, 2),
                    entry("minecraft:gold_nugget", 15, 1, 3))
    };

    private final Map<String, NBTTagCompound> cache = new HashMap<>();

    public boolean place(World world, Random random, String template, BlockPos origin) {
        NBTTagCompound root = load(template);
        if (root == null) {
            return false;
        }

        NBTTagList palette = root.getTagList("palette", Constants.NBT.TAG_COMPOUND);
        IBlockState[] states = new IBlockState[palette.tagCount()];
        for (int i = 0; i < palette.tagCount(); i++) {
            states[i] = stateFromPalette(palette.getCompoundTagAt(i));
        }

        NBTTagList blocks = root.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound blockTag = blocks.getCompoundTagAt(i);
            int stateId = blockTag.getInteger("state");
            if (stateId < 0 || stateId >= states.length) {
                continue;
            }

            IBlockState state = states[stateId];
            if (state == null) {
                continue;
            }

            BlockPos target = origin.add(readPos(blockTag.getTagList("pos", Constants.NBT.TAG_INT)));
            if (!canPlace(world, target)) {
                continue;
            }

            world.setBlockState(target, state, 2);
            if (state.getBlock() != Blocks.AIR) {
                applyTileEntity(world, target, blockTag, random);
            }
        }

        placeEntities(world, random, root, origin);
        return true;
    }

    private NBTTagCompound load(String template) {
        if (cache.containsKey(template)) {
            return cache.get(template);
        }

        String path = RESOURCE_ROOT + template + ".nbt";
        try (InputStream stream = ModernTemplatePlacer.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                Eidolon.LOGGER.warn("Missing Eidolon structure template {}", path);
                cache.put(template, null);
                return null;
            }

            NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);
            cache.put(template, tag);
            return tag;
        } catch (IOException e) {
            Eidolon.LOGGER.error("Failed to load Eidolon structure template {}", path, e);
            cache.put(template, null);
            return null;
        }
    }

    private BlockPos readPos(NBTTagList list) {
        if (list.tagCount() < 3) {
            return BlockPos.ORIGIN;
        }
        return new BlockPos(list.getIntAt(0), list.getIntAt(1), list.getIntAt(2));
    }

    private boolean canPlace(World world, BlockPos pos) {
        if (pos.getY() <= 0 || pos.getY() >= world.getActualHeight()) {
            return false;
        }
        return world.getBlockState(pos).getBlock() != Blocks.BEDROCK;
    }

    private IBlockState stateFromPalette(NBTTagCompound paletteEntry) {
        String id = normalizeBlockId(paletteEntry.getString("Name"));
        Block block = Block.getBlockFromName(id);
        IBlockState state = mappedState(id, block);

        if (state == null) {
            Eidolon.LOGGER.debug("Skipping unsupported Eidolon structure block {}", id);
            return null;
        }

        if (paletteEntry.hasKey("Properties", Constants.NBT.TAG_COMPOUND)) {
            state = applyProperties(state, paletteEntry.getCompoundTag("Properties"));
        }
        return state;
    }

    private String normalizeBlockId(String id) {
        if ("eidolon:brazier_tile".equals(id)) {
            return "eidolon:brazier";
        }
        if ("eidolon:hand_tile".equals(id)) {
            return "eidolon:stone_hand";
        }
        if ("eidolon:effigy".equals(id)) {
            return "eidolon:straw_effigy";
        }
        return id;
    }

    private IBlockState mappedState(String id, Block directBlock) {
        if (directBlock != null) {
            return directBlock.getDefaultState();
        }

        switch (id) {
            case "minecraft:air":
            case "minecraft:chain":
            case "minecraft:potted_brown_mushroom":
            case "minecraft:potted_red_mushroom":
                return Blocks.AIR.getDefaultState();
            case "minecraft:stone_bricks":
                return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT);
            case "minecraft:mossy_stone_bricks":
                return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
            case "minecraft:cracked_stone_bricks":
                return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
            case "minecraft:chiseled_stone_bricks":
                return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
            case "minecraft:stone_brick_slab":
            case "minecraft:mossy_stone_brick_slab":
                return Blocks.STONE_SLAB.getStateFromMeta(5);
            case "minecraft:cobblestone_slab":
            case "minecraft:mossy_cobblestone_slab":
                return Blocks.STONE_SLAB.getStateFromMeta(3);
            case "minecraft:stone_brick_wall":
                return Blocks.COBBLESTONE_WALL.getDefaultState();
            case "minecraft:mossy_cobblestone_stairs":
                return Blocks.STONE_STAIRS.getDefaultState();
            case "minecraft:mossy_stone_brick_stairs":
                return Blocks.STONE_BRICK_STAIRS.getDefaultState();
            case "minecraft:dark_oak_planks":
                return Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK);
            case "minecraft:oak_planks":
                return Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK);
            case "minecraft:spruce_slab":
                return Blocks.WOODEN_SLAB.getStateFromMeta(1);
            case "minecraft:stripped_oak_log":
                return Blocks.LOG.getDefaultState();
            case "minecraft:polished_diorite":
                return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH);
            case "minecraft:carved_pumpkin":
                return Blocks.PUMPKIN.getDefaultState();
            case "minecraft:skeleton_skull":
            case "minecraft:skull":
                return Blocks.SKULL.getDefaultState();
            case "minecraft:lantern":
            case "minecraft:campfire":
                return Blocks.TORCH.getDefaultState();
            case "minecraft:blast_furnace":
                return Blocks.FURNACE.getDefaultState();
            case "minecraft:grindstone":
            case "minecraft:fletching_table":
            case "minecraft:smithing_table":
                return Blocks.CRAFTING_TABLE.getDefaultState();
            case "minecraft:mob_spawner":
            case "minecraft:spawner":
                return Blocks.MOB_SPAWNER.getDefaultState();
            case "eidolon:lead_block":
                return ModBlocks.LEAD_BLOCK.getDefaultState();
            case "eidolon:smooth_stone_tiles":
                return ModBlocks.SMOOTH_STONE_TILES.getDefaultState();
            case "eidolon:stone_hand":
                return ModBlocks.STONE_HAND.getDefaultState();
            case "eidolon:brazier":
                return ModBlocks.BRAZIER.getDefaultState();
            case "eidolon:candle":
                return ModBlocks.CANDLE.getDefaultState();
            case "eidolon:crucible":
                return ModBlocks.CRUCIBLE.getDefaultState();
            case "eidolon:plinth":
                return ModBlocks.PLINTH.getDefaultState();
            case "eidolon:straw_effigy":
                return ModBlocks.STRAW_EFFIGY.getDefaultState();
            case "eidolon:wooden_altar":
                return ModBlocks.WOODEN_ALTAR.getDefaultState();
            case "eidolon:wooden_brewing_stand":
                return ModBlocks.WOODEN_BREWING_STAND.getDefaultState();
            default:
                return null;
        }
    }

    private IBlockState applyProperties(IBlockState state, NBTTagCompound properties) {
        for (IProperty<?> property : state.getPropertyKeys()) {
            if (!properties.hasKey(property.getName())) {
                continue;
            }
            state = applyProperty(state, property, properties.getString(property.getName()));
        }
        return state;
    }

    private <T extends Comparable<T>> IBlockState applyProperty(IBlockState state, IProperty<T> property, String value) {
        Optional<T> parsed = property.parseValue(value);
        return parsed.isPresent() ? state.withProperty(property, parsed.get()) : state;
    }

    private void applyTileEntity(World world, BlockPos target, NBTTagCompound blockTag, Random random) {
        if (!blockTag.hasKey("nbt", Constants.NBT.TAG_COMPOUND)) {
            return;
        }

        TileEntity tile = world.getTileEntity(target);
        if (tile == null) {
            return;
        }

        NBTBase raw = blockTag.getTag("nbt");
        if (!(raw instanceof NBTTagCompound)) {
            return;
        }

        NBTTagCompound tileTag = ((NBTTagCompound) raw).copy();
        if (fillEidolonChest(tile, tileTag, random) || configureSpawner(tile, tileTag)) {
            tile.markDirty();
            return;
        }

        tileTag.setInteger("x", target.getX());
        tileTag.setInteger("y", target.getY());
        tileTag.setInteger("z", target.getZ());

        try {
            tile.readFromNBT(tileTag);
            tile.markDirty();
        } catch (RuntimeException e) {
            Eidolon.LOGGER.debug("Skipped incompatible structure tile NBT at {}", target, e);
        }
    }

    private boolean fillEidolonChest(TileEntity tile, NBTTagCompound tileTag, Random random) {
        if (!(tile instanceof TileEntityChest) || !tileTag.hasKey("LootTable")) {
            return false;
        }

        String table = tileTag.getString("LootTable");
        if (!table.startsWith("eidolon:chests/")) {
            return false;
        }

        TileEntityChest chest = (TileEntityChest) tile;
        LootPool[] pools = table.endsWith("catacomb_coffin") ? COFFIN_LOOT
                : table.endsWith("catacomb_basic") ? CATACOMB_LOOT
                : LAB_LOOT;

        for (LootPool pool : pools) {
            pool.fill(chest, random);
        }
        return true;
    }

    private static void putInRandomSlot(TileEntityChest chest, ItemStack stack, Random random) {
        for (int i = 0; i < 12; i++) {
            int slot = random.nextInt(chest.getSizeInventory());
            if (chest.getStackInSlot(slot).isEmpty()) {
                chest.setInventorySlotContents(slot, stack);
                return;
            }
        }
    }

    private boolean configureSpawner(TileEntity tile, NBTTagCompound tileTag) {
        if (!(tile instanceof TileEntityMobSpawner)) {
            return false;
        }

        String text = tileTag.toString().toLowerCase(Locale.ROOT);
        String entity = text.contains("eidolon:wraith") ? "eidolon:wraith"
                : text.contains("eidolon:zombie_brute") ? "eidolon:zombie_brute"
                : text.contains("stray") ? "minecraft:stray"
                : text.contains("skeleton") ? "minecraft:skeleton"
                : text.contains("witch") ? "minecraft:witch"
                : "minecraft:zombie";
        ((TileEntityMobSpawner) tile).getSpawnerBaseLogic().setEntityId(new ResourceLocation(entity));
        return true;
    }

    private void placeEntities(World world, Random random, NBTTagCompound root, BlockPos origin) {
        NBTTagList entities = root.getTagList("entities", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < entities.tagCount(); i++) {
            NBTTagCompound wrapper = entities.getCompoundTagAt(i);
            if (!wrapper.hasKey("nbt", Constants.NBT.TAG_COMPOUND)) {
                continue;
            }

            NBTTagCompound entityTag = wrapper.getCompoundTag("nbt");
            String id = normalizeEntityId(entityTag.getString("id"));
            if (id.isEmpty()) {
                continue;
            }

            Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(id), world);
            if (entity == null) {
                entity = createEidolonEntity(id, world);
            }
            if (entity == null) {
                continue;
            }

            NBTTagList relative = wrapper.getTagList("pos", Constants.NBT.TAG_DOUBLE);
            double x = origin.getX() + (relative.tagCount() > 0 ? relative.getDoubleAt(0) : 0.5D);
            double y = origin.getY() + (relative.tagCount() > 1 ? relative.getDoubleAt(1) : 0.0D);
            double z = origin.getZ() + (relative.tagCount() > 2 ? relative.getDoubleAt(2) : 0.5D);
            entity.setPositionAndRotation(x, y, z, random.nextFloat() * 360.0F, 0.0F);
            world.spawnEntity(entity);
        }
    }

    private Entity createEidolonEntity(String id, World world) {
        if (!id.startsWith("eidolon:")) {
            return null;
        }
        return ModEntities.create(id.substring("eidolon:".length()), world);
    }

    private String normalizeEntityId(String id) {
        return id;
    }

    private static LootPool pool(int minRolls, int maxRolls, LootEntry... entries) {
        return new LootPool(minRolls, maxRolls, entries);
    }

    private static LootEntry entry(String id, int weight) {
        return entry(id, weight, 1, 1);
    }

    private static LootEntry entry(String id, int weight, int minCount, int maxCount) {
        return new LootEntry(id, weight, minCount, maxCount, false);
    }

    private static LootEntry enchantedBook(int weight) {
        return new LootEntry("minecraft:book", weight, 1, 1, true);
    }

    private static final class LootPool {
        private final int minRolls;
        private final int maxRolls;
        private final LootEntry[] entries;
        private final int totalWeight;

        private LootPool(int minRolls, int maxRolls, LootEntry[] entries) {
            this.minRolls = minRolls;
            this.maxRolls = Math.max(minRolls, maxRolls);
            this.entries = entries;
            int weight = 0;
            for (LootEntry entry : entries) {
                weight += Math.max(0, entry.weight);
            }
            this.totalWeight = weight;
        }

        private void fill(TileEntityChest chest, Random random) {
            if (entries.length == 0 || totalWeight <= 0) {
                return;
            }
            int rolls = minRolls + random.nextInt(maxRolls - minRolls + 1);
            for (int i = 0; i < rolls; i++) {
                ItemStack stack = select(random).createStack(random);
                if (!stack.isEmpty()) {
                    putInRandomSlot(chest, stack, random);
                }
            }
        }

        private LootEntry select(Random random) {
            int cursor = random.nextInt(totalWeight);
            for (LootEntry entry : entries) {
                cursor -= Math.max(0, entry.weight);
                if (cursor < 0) {
                    return entry;
                }
            }
            return entries[entries.length - 1];
        }
    }

    private static final class LootEntry {
        private final String id;
        private final int weight;
        private final int minCount;
        private final int maxCount;
        private final boolean randomEnchantment;

        private LootEntry(String id, int weight, int minCount, int maxCount, boolean randomEnchantment) {
            this.id = id;
            this.weight = weight;
            this.minCount = Math.max(1, minCount);
            this.maxCount = Math.max(this.minCount, maxCount);
            this.randomEnchantment = randomEnchantment;
        }

        private ItemStack createStack(Random random) {
            if (randomEnchantment) {
                return EnchantmentHelper.addRandomEnchantment(random, new ItemStack(Items.BOOK), 30, true);
            }

            Item item = Item.getByNameOrId(id);
            if (item == null) {
                return ItemStack.EMPTY;
            }

            int count = minCount + random.nextInt(maxCount - minCount + 1);
            return new ItemStack(item, count);
        }
    }
}
