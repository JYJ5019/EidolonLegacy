package elucent.eidolon.world;

import elucent.eidolon.CommonConfig;
import elucent.eidolon.registries.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Locale;
import java.util.Random;

public final class EidolonWorldGenerator implements IWorldGenerator {
    public static final int OVERWORLD = 0;

    public static final int SURFACE_SPACING = 48;
    public static final int SURFACE_SEPARATION = 32;
    public static final int SURFACE_SALT = 13215577;

    public static final int CATACOMB_SPACING = 80;
    public static final int CATACOMB_SEPARATION = 40;
    public static final int CATACOMB_SALT = 12512867;

    private final IllwoodTreeGenerator illwoodTree = new IllwoodTreeGenerator(true);
    private final EidolonStructureGenerator structures = new EidolonStructureGenerator();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != OVERWORLD) {
            return;
        }

        generateOres(random, chunkX, chunkZ, world);
        generateIllwood(random, chunkX, chunkZ, world);
        generateStructures(random, chunkX, chunkZ, world);
    }

    private void generateOres(Random random, int chunkX, int chunkZ, World world) {
        if (CommonConfig.leadEnabled()) {
            generateOre(world, random, chunkX, chunkZ, ModBlocks.LEAD_ORE.getDefaultState(),
                    CommonConfig.leadOreVeinSize(), CommonConfig.leadOreVeinCount(),
                    CommonConfig.leadOreMinY(), CommonConfig.leadOreMaxY());
            generateOre(world, random, chunkX, chunkZ, ModBlocks.DEEP_LEAD_ORE.getDefaultState(),
                    CommonConfig.deepLeadOreVeinSize(), CommonConfig.deepLeadOreVeinCount(),
                    CommonConfig.deepLeadOreMinY(), CommonConfig.deepLeadOreMaxY());
        }
        if (CommonConfig.silverEnabled()) {
            generateOre(world, random, chunkX, chunkZ, ModBlocks.SILVER_ORE.getDefaultState(),
                    CommonConfig.silverOreVeinSize(), CommonConfig.silverOreVeinCount(),
                    CommonConfig.silverOreMinY(), CommonConfig.silverOreMaxY());
            generateOre(world, random, chunkX, chunkZ, ModBlocks.DEEP_SILVER_ORE.getDefaultState(),
                    CommonConfig.deepSilverOreVeinSize(), CommonConfig.deepSilverOreVeinCount(),
                    CommonConfig.deepSilverOreMinY(), CommonConfig.deepSilverOreMaxY());
        }
    }

    private void generateOre(World world, Random random, int chunkX, int chunkZ, IBlockState state,
                             int veinSize, int attempts, int minY, int maxY) {
        WorldGenMinable generator = new WorldGenMinable(state, veinSize);
        int heightRange = Math.max(1, maxY - minY + 1);

        for (int i = 0; i < attempts; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int y = minY + random.nextInt(heightRange);
            int z = chunkZ * 16 + random.nextInt(16);
            generator.generate(world, random, new BlockPos(x, y, z));
        }
    }

    private void generateIllwood(Random random, int chunkX, int chunkZ, World world) {
        if (random.nextInt(32) != 0) {
            return;
        }

        int x = chunkX * 16 + 8 + random.nextInt(8);
        int z = chunkZ * 16 + 8 + random.nextInt(8);
        BlockPos sample = new BlockPos(x, 0, z);
        if (!isIllwoodBiome(world.getBiome(sample))) {
            return;
        }

        BlockPos pos = world.getHeight(sample);
        if (pos.getY() > 0 && pos.getY() < world.getActualHeight() - 16) {
            illwoodTree.generate(world, random, pos);
        }
    }

    private void generateStructures(Random random, int chunkX, int chunkZ, World world) {
        if (isSurfaceStructureChunk(world.getSeed(), chunkX, chunkZ)) {
            structures.generateSurface(world, createStructureRandom(world.getSeed(), chunkX, chunkZ, SURFACE_SALT), chunkX, chunkZ);
        }

        if (isCatacombStructureChunk(world.getSeed(), chunkX, chunkZ)) {
            structures.generateCatacomb(world, createStructureRandom(world.getSeed(), chunkX, chunkZ, CATACOMB_SALT), chunkX, chunkZ);
        }
    }

    private boolean isIllwoodBiome(Biome biome) {
        String name = biome.getBiomeName().toLowerCase(Locale.ROOT);
        return name.contains("forest")
                || name.contains("swamp")
                || name.contains("swampland")
                || name.contains("roofed");
    }

    public static StructureCandidate getSurfaceCandidate(World world, int chunkX, int chunkZ) {
        if (!CommonConfig.labEnabled() && !CommonConfig.strayTowerEnabled()) {
            return null;
        }

        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;
        BlockPos surface = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        Random random = createStructureRandom(world.getSeed(), chunkX, chunkZ, SURFACE_SALT);
        boolean towerPick = random.nextInt(6) < 3;
        double rarity = surfaceRarity();

        if (CommonConfig.strayTowerEnabled() && towerPick && isStrayTowerBiome(world, surface)
                && passesRarity(random, CommonConfig.strayTowerRarity(), rarity)) {
            return new StructureCandidate(StructureType.STRAY_TOWER, surface, chunkX, chunkZ);
        }
        if (!CommonConfig.labEnabled()) {
            return null;
        }
        if (!passesRarity(random, CommonConfig.labRarity(), rarity)) {
            return null;
        }

        int y = clamp(18 + random.nextInt(14), 10, Math.max(10, surface.getY() - 12));
        return new StructureCandidate(StructureType.LAB, new BlockPos(x, y, z), chunkX, chunkZ);
    }

    public static StructureCandidate getCatacombCandidate(World world, int chunkX, int chunkZ) {
        if (!CommonConfig.catacombEnabled()) {
            return null;
        }

        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;
        BlockPos surface = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        Random random = createStructureRandom(world.getSeed(), chunkX, chunkZ, CATACOMB_SALT);
        int y = clamp(14 + random.nextInt(14), 8, Math.max(8, surface.getY() - 18));
        return new StructureCandidate(StructureType.CATACOMB, new BlockPos(x, y, z), chunkX, chunkZ);
    }

    public static boolean isStrayTowerBiome(World world, BlockPos pos) {
        String name = world.getBiome(pos).getBiomeName().toLowerCase(Locale.ROOT);
        for (String keyword : CommonConfig.strayTowerBiomeKeywords()) {
            if (keyword != null && !keyword.isEmpty() && name.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSurfaceStructureChunk(long worldSeed, int chunkX, int chunkZ) {
        if (!CommonConfig.labEnabled() && !CommonConfig.strayTowerEnabled()) {
            return false;
        }
        int spacing = surfaceSpacing();
        int separation = scaleSeparation(SURFACE_SEPARATION, SURFACE_SPACING, spacing);
        return isRandomSpreadChunk(worldSeed, chunkX, chunkZ, spacing, separation, SURFACE_SALT);
    }

    public static boolean isCatacombStructureChunk(long worldSeed, int chunkX, int chunkZ) {
        if (!CommonConfig.catacombEnabled()) {
            return false;
        }
        int spacing = catacombSpacing();
        int separation = scaleSeparation(CATACOMB_SEPARATION, CATACOMB_SPACING, spacing);
        return isRandomSpreadChunk(worldSeed, chunkX, chunkZ, spacing, separation, CATACOMB_SALT);
    }

    public static Random createStructureRandom(long worldSeed, int chunkX, int chunkZ, int salt) {
        return new Random(worldSeed + chunkX * 341873128712L + chunkZ * 132897987541L + salt);
    }

    private static int clamp(int value, int min, int max) {
        if (max < min) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    private static int surfaceSpacing() {
        return scaleSpacing(SURFACE_SPACING, surfaceRarity(), CommonConfig.DEFAULT_LAB_RARITY);
    }

    private static double surfaceRarity() {
        double rarity = Double.MAX_VALUE;
        if (CommonConfig.labEnabled()) {
            rarity = Math.min(rarity, CommonConfig.labRarity());
        }
        if (CommonConfig.strayTowerEnabled()) {
            rarity = Math.min(rarity, CommonConfig.strayTowerRarity());
        }
        if (rarity == Double.MAX_VALUE) {
            rarity = CommonConfig.DEFAULT_LAB_RARITY;
        }
        return rarity;
    }

    private static int catacombSpacing() {
        return scaleSpacing(CATACOMB_SPACING, CommonConfig.catacombRarity(), CommonConfig.DEFAULT_CATACOMB_RARITY);
    }

    private static int scaleSpacing(int baseSpacing, double rarity, double defaultRarity) {
        double scale = Math.sqrt(Math.max(1.0D, rarity) / defaultRarity);
        return Math.max(2, (int) Math.round(baseSpacing * scale));
    }

    private static int scaleSeparation(int baseSeparation, int baseSpacing, int spacing) {
        int separation = (int) Math.round((double) baseSeparation * spacing / baseSpacing);
        return Math.max(1, Math.min(spacing - 1, separation));
    }

    private static boolean passesRarity(Random random, double rarity, double defaultRarity) {
        if (rarity <= defaultRarity) {
            return true;
        }
        return random.nextDouble() < defaultRarity / rarity;
    }

    private static boolean isRandomSpreadChunk(long worldSeed, int chunkX, int chunkZ,
                                               int spacing, int separation, int salt) {
        int regionX = Math.floorDiv(chunkX, spacing);
        int regionZ = Math.floorDiv(chunkZ, spacing);
        Random spreadRandom = new Random(worldSeed
                + regionX * 341873128712L
                + regionZ * 132897987541L
                + salt);
        int range = Math.max(1, spacing - separation);
        int targetX = regionX * spacing + spreadRandom.nextInt(range);
        int targetZ = regionZ * spacing + spreadRandom.nextInt(range);
        return chunkX == targetX && chunkZ == targetZ;
    }

    public enum StructureType {
        LAB("lab"),
        STRAY_TOWER("stray_tower"),
        CATACOMB("catacomb");

        private final String id;

        StructureType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static final class StructureCandidate {
        private final StructureType type;
        private final BlockPos pos;
        private final int chunkX;
        private final int chunkZ;

        private StructureCandidate(StructureType type, BlockPos pos, int chunkX, int chunkZ) {
            this.type = type;
            this.pos = pos;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        public StructureType getType() {
            return type;
        }

        public BlockPos getPos() {
            return pos;
        }

        public int getChunkX() {
            return chunkX;
        }

        public int getChunkZ() {
            return chunkZ;
        }
    }
}
