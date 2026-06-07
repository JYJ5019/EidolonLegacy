package elucent.eidolon.world;

import elucent.eidolon.registries.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public final class IllwoodTreeGenerator extends WorldGenerator {
    private static final IBlockState LOG_Y = ModBlocks.ILLWOOD_LOG.getDefaultState()
            .withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y);
    private static final IBlockState LOG_X = ModBlocks.ILLWOOD_LOG.getDefaultState()
            .withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X);
    private static final IBlockState LOG_Z = ModBlocks.ILLWOOD_LOG.getDefaultState()
            .withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z);
    private static final IBlockState LEAVES = ModBlocks.ILLWOOD_LEAVES.getDefaultState();

    public IllwoodTreeGenerator(boolean notify) {
        super(notify);
    }

    public static boolean generateAt(World world, Random random, BlockPos pos) {
        return new IllwoodTreeGenerator(true).generate(world, random, pos);
    }

    @Override
    public boolean generate(World world, Random random, BlockPos pos) {
        int height = 6 + random.nextInt(3) + random.nextInt(3);
        if (!canGenerate(world, pos, height)) {
            return false;
        }

        world.setBlockState(pos.down(), Blocks.DIRT.getDefaultState(), 2);

        for (int y = 0; y < height; y++) {
            placeLog(world, pos.up(y), LOG_Y);
        }

        placeRoots(world, random, pos);
        placeLeaves(world, random, pos, height);
        return true;
    }

    private boolean canGenerate(World world, BlockPos pos, int height) {
        if (pos.getY() < 1 || pos.getY() + height + 3 >= world.getActualHeight()) {
            return false;
        }

        if (!canSustain(world.getBlockState(pos.down()))) {
            return false;
        }

        for (int y = 0; y <= height + 2; y++) {
            int radius = y < 2 ? 1 : 4;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos check = pos.add(dx, y, dz);
                    if (!canReplace(world, check)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void placeRoots(World world, Random random, BlockPos pos) {
        EnumFacing[] directions = new EnumFacing[] {
                EnumFacing.NORTH,
                EnumFacing.EAST,
                EnumFacing.SOUTH,
                EnumFacing.WEST
        };
        int roots = 1 + random.nextInt(4);

        for (int i = 0; i < roots; i++) {
            EnumFacing facing = directions[i];
            BlockPos root = pos.offset(facing);
            if (canReplace(world, root) && canSustain(world.getBlockState(root.down()))) {
                placeLog(world, root, axisFor(facing));
                if (random.nextBoolean() && canReplace(world, root.up())) {
                    placeLog(world, root.up(), LOG_Y);
                }
            }
        }
    }

    private void placeLeaves(World world, Random random, BlockPos pos, int height) {
        for (int y = height - 3; y <= height + 2; y++) {
            int radius = y > height ? 2 : 3;
            if (y <= height - 2) {
                radius = 2;
            }

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int distance = Math.abs(dx) + Math.abs(dz);
                    boolean raggedEdge = distance > radius + 1 || (distance == radius + 1 && random.nextBoolean());
                    if (!raggedEdge) {
                        placeLeaves(world, pos.add(dx, y, dz));
                    }
                }
            }
        }

        for (int i = 0; i < 6; i++) {
            BlockPos extra = pos.add(random.nextInt(7) - 3, height - 1 + random.nextInt(3), random.nextInt(7) - 3);
            placeLeaves(world, extra);
        }
    }

    private void placeLog(World world, BlockPos pos, IBlockState state) {
        if (canReplace(world, pos)) {
            setBlockAndNotifyAdequately(world, pos, state);
        }
    }

    private void placeLeaves(World world, BlockPos pos) {
        if (canReplace(world, pos)) {
            setBlockAndNotifyAdequately(world, pos, LEAVES);
        }
    }

    private IBlockState axisFor(EnumFacing facing) {
        return facing == EnumFacing.EAST || facing == EnumFacing.WEST ? LOG_X : LOG_Z;
    }

    private boolean canSustain(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.FARMLAND;
    }

    private boolean canReplace(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Material material = state.getMaterial();
        return state.getBlock().isAir(state, world, pos)
                || material == Material.LEAVES
                || material == Material.PLANTS
                || state.getBlock() == ModBlocks.ILLWOOD_SAPLING;
    }
}
