package elucent.eidolon.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public final class EidolonStructureGenerator {
    private static final String LAB = "lab";
    private static final String STRAY_TOWER = "stray_tower";

    private static final String[] CATACOMB_POOL = new String[] {
            "catacomb/catacomb_coffin",
            "catacomb/catacomb_corridor_center",
            "catacomb/catacomb_corridor_door",
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

        if (candidate.getType() == EidolonWorldGenerator.StructureType.STRAY_TOWER) {
            placer.place(world, random, STRAY_TOWER, candidate.getPos().add(-8, -9, -8));
        } else {
            placer.place(world, random, LAB, candidate.getPos().add(-8, 0, -8));
        }
    }

    public void generateCatacomb(World world, Random random, int chunkX, int chunkZ) {
        EidolonWorldGenerator.StructureCandidate candidate = EidolonWorldGenerator.getCatacombCandidate(world, chunkX, chunkZ);
        BlockPos origin = candidate.getPos().add(-16, 0, -16);

        placer.place(world, random, "catacomb/catacomb_corridor_center", origin);

        int pieces = 4 + random.nextInt(4);
        for (int i = 0; i < pieces; i++) {
            String template = CATACOMB_POOL[random.nextInt(CATACOMB_POOL.length)];
            int dx = (random.nextInt(5) - 2) * 12;
            int dz = (random.nextInt(5) - 2) * 12;
            int dy = random.nextInt(5) - 2;
            placer.place(world, random, template, origin.add(dx, dy, dz));
        }
    }
}
