package elucent.eidolon.spell;

import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModSounds;
import elucent.eidolon.tile.EffigyTileEntity;
import elucent.eidolon.tile.GobletTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

final class SpellHelper {
    private SpellHelper() {
    }

    static AxisAlignedBB area(BlockPos pos, double radius) {
        return new AxisAlignedBB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                pos.getX() + 1.0D + radius, pos.getY() + 1.0D + radius, pos.getZ() + 1.0D + radius);
    }

    static BlockPos findNearestBlock(World world, BlockPos center, int radius, Predicate<Block> predicate) {
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (!predicate.test(world.getBlockState(pos).getBlock())) {
                        continue;
                    }
                    double distance = pos.distanceSq(center);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = pos;
                    }
                }
            }
        }
        return nearest;
    }

    static BlockPos findEffigy(World world, BlockPos center) {
        return findNearestBlock(world, center, 4,
                block -> block == ModBlocks.STRAW_EFFIGY || block == ModBlocks.UNHOLY_EFFIGY);
    }

    static EffigyTileEntity findEffigyTile(World world, BlockPos center) {
        return findNearestTile(world, center, 4, EffigyTileEntity.class,
                tile -> world.getBlockState(tile.getPos()).getBlock() == ModBlocks.STRAW_EFFIGY
                        || world.getBlockState(tile.getPos()).getBlock() == ModBlocks.UNHOLY_EFFIGY);
    }

    static BlockPos findUnholyEffigy(World world, BlockPos center) {
        return findNearestBlock(world, center, 4, block -> block == ModBlocks.UNHOLY_EFFIGY);
    }

    static EffigyTileEntity findUnholyEffigyTile(World world, BlockPos center) {
        return findNearestTile(world, center, 4, EffigyTileEntity.class,
                tile -> world.getBlockState(tile.getPos()).getBlock() == ModBlocks.UNHOLY_EFFIGY);
    }

    static GobletTileEntity findGoblet(World world, BlockPos center) {
        return findNearestTile(world, center, 4, GobletTileEntity.class, tile -> true);
    }

    static BlockPos findStoneAltar(World world, BlockPos center) {
        return findNearestBlock(world, center, 4, block -> block == ModBlocks.STONE_ALTAR);
    }

    static boolean isOnStoneAltar(World world, BlockPos pos) {
        BlockPos below = pos.down();
        return world.getBlockState(below).getBlock() == ModBlocks.STONE_ALTAR;
    }

    static double getNearbyAltarPower(World world, BlockPos center) {
        BlockPos altar = findNearestBlock(world, center, 5,
                block -> block == ModBlocks.STONE_ALTAR || block == ModBlocks.WOODEN_ALTAR);
        if (altar == null) {
            return 0.0D;
        }
        return AltarInfo.scan(world, altar).getPower();
    }

    static <T extends EntityLivingBase> T nearestLiving(World world, BlockPos pos, Class<T> type, double radius) {
        List<T> entities = world.getEntitiesWithinAABB(type, area(pos, radius),
                entity -> entity != null && entity.isEntityAlive());
        if (entities.isEmpty()) {
            return null;
        }
        return entities.stream().min(Comparator.comparingDouble(entity -> entity.getDistanceSq(pos))).orElse(null);
    }

    private static <T extends TileEntity> T findNearestTile(World world, BlockPos center, int radius,
                                                           Class<T> type, Predicate<T> predicate) {
        T nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    TileEntity tile = world.getTileEntity(center.add(dx, dy, dz));
                    if (!type.isInstance(tile)) {
                        continue;
                    }
                    T typed = type.cast(tile);
                    if (!predicate.test(typed)) {
                        continue;
                    }
                    double distance = typed.getPos().distanceSq(center);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = typed;
                    }
                }
            }
        }
        return nearest;
    }

    static EntityItem singleNearbyItem(World world, double x, double y, double z, double radius) {
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius),
                entity -> entity != null && !entity.isDead && !entity.getItem().isEmpty());
        return items.size() == 1 ? items.get(0) : null;
    }

    static void playChantSuccess(World world, BlockPos pos, Sign primary, Sign secondary) {
        if (world.isRemote) {
            return;
        }
        world.playSound(null, pos, ModSounds.CHANT_WORD, SoundCategory.PLAYERS, 0.75F,
                0.75F + world.rand.nextFloat() * 0.25F);
        VisualEffectPacket.sendAround(world, pos, VisualEffectPacket.at(VisualEffectPacket.MAGIC_BURST, pos,
                primary.getRed(), primary.getGreen(), primary.getBlue()));
        VisualEffectPacket.sendAround(world, pos, VisualEffectPacket.at(VisualEffectPacket.RITUAL_COMPLETE,
                pos.getX() + 0.5D, pos.getY() + 0.65D, pos.getZ() + 0.5D,
                secondary.getRed(), secondary.getGreen(), secondary.getBlue()));
    }

    static void playChantFail(World world, BlockPos pos) {
        if (!world.isRemote) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.85F,
                    0.85F + world.rand.nextFloat() * 0.25F);
        }
    }
}
