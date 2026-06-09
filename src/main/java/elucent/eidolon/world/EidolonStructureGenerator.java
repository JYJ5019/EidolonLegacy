package elucent.eidolon.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class EidolonStructureGenerator {
    private static final String LAB = "lab";
    private static final String STRAY_TOWER = "stray_tower";
    private static final int SURFACE_XZ_OFFSET = 8;
    private static final int STRAY_TOWER_Y_OFFSET = -9;
    private static final int CATACOMB_CENTER_OFFSET = 3;
    private static final int CATACOMB_PIECE_SPACING = 12;

    private static final String[] CATACOMB_POOL = new String[] {
            "catacomb/catacomb_coffin",
            "catacomb/catacomb_corridor_center",
            "catacomb/catacomb_graveyard",
            "catacomb/catacomb_lab",
            "catacomb/catacomb_room_medium",
            "catacomb/catacomb_room_small",
            "catacomb/catacomb_shrine",
            "catacomb/catacomb_skull",
            "catacomb/catacomb_spawner",
            "catacomb/catacomb_trap",
            "catacomb/catacomb_turnaround"
    };

    private final ModernTemplatePlacer placer = new ModernTemplatePlacer();

    public void generateSurface(World world, Random random, int chunkX, int chunkZ) {
        EidolonWorldGenerator.StructureCandidate candidate = EidolonWorldGenerator.getSurfaceCandidate(world, chunkX, chunkZ);
        if (candidate == null) {
            return;
        }

        if (candidate.getType() == EidolonWorldGenerator.StructureType.STRAY_TOWER) {
            placer.place(world, random, STRAY_TOWER,
                    candidate.getPos().add(-SURFACE_XZ_OFFSET, STRAY_TOWER_Y_OFFSET, -SURFACE_XZ_OFFSET));
        } else {
            placer.place(world, random, LAB, candidate.getPos().add(-SURFACE_XZ_OFFSET, 0, -SURFACE_XZ_OFFSET));
        }
    }

    public void generateCatacomb(World world, Random random, int chunkX, int chunkZ) {
        EidolonWorldGenerator.StructureCandidate candidate = EidolonWorldGenerator.getCatacombCandidate(world, chunkX, chunkZ);
        if (candidate == null) {
            return;
        }
        BlockPos origin = candidate.getPos().add(-CATACOMB_CENTER_OFFSET, 0, -CATACOMB_CENTER_OFFSET);

        placer.place(world, random, "catacomb/catacomb_corridor_center", origin);

        int pieces = 4 + random.nextInt(4);
        int placed = 0;
        int attempts = 0;
        Set<Long> usedOffsets = new HashSet<>();
        usedOffsets.add(offsetKey(0, 0));

        while (placed < pieces && attempts < pieces * 6) {
            attempts++;
            String template = CATACOMB_POOL[random.nextInt(CATACOMB_POOL.length)];
            int dx = (random.nextInt(5) - 2) * CATACOMB_PIECE_SPACING;
            int dz = (random.nextInt(5) - 2) * CATACOMB_PIECE_SPACING;
            if (!usedOffsets.add(offsetKey(dx, dz))) {
                continue;
            }
            placer.place(world, random, template, origin.add(dx, 0, dz));
            placed++;
        }
    }

    private static long offsetKey(int dx, int dz) {
        return ((long) dx << 32) ^ (dz & 0xffffffffL);
    }
}
