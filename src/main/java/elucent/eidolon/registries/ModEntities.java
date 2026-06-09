package elucent.eidolon.registries;

import elucent.eidolon.CommonConfig;
import elucent.eidolon.Eidolon;
import elucent.eidolon.Reference;
import elucent.eidolon.entity.AngelArrowEntity;
import elucent.eidolon.entity.BonechillProjectileEntity;
import elucent.eidolon.entity.ChantCasterEntity;
import elucent.eidolon.entity.NecromancerEntity;
import elucent.eidolon.entity.NecromancerSpellEntity;
import elucent.eidolon.entity.RavenEntity;
import elucent.eidolon.entity.SlimySlugEntity;
import elucent.eidolon.entity.SoulfireProjectileEntity;
import elucent.eidolon.entity.WraithEntity;
import elucent.eidolon.entity.ZombieBruteEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class ModEntities {
    private static int nextId = 0;
    private static final Map<String, Function<World, Entity>> FACTORIES = new LinkedHashMap<>();

    private ModEntities() {
    }

    public static void init() {
        register("soulfire_projectile", SoulfireProjectileEntity.class, 64, 10, true);
        register("bonechill_projectile", BonechillProjectileEntity.class, 64, 10, true);
        register("necromancer_spell", NecromancerSpellEntity.class, 64, 10, true);
        register("angel_arrow", AngelArrowEntity.class, 64, 20, true);
        register("chant_caster", ChantCasterEntity.class, 64, 5, true);
        registerLiving("wraith", WraithEntity.class, 64, 3, true, WraithEntity::new);
        registerLiving("zombie_brute", ZombieBruteEntity.class, 64, 3, true, ZombieBruteEntity::new);
        registerLiving("necromancer", NecromancerEntity.class, 64, 3, true, NecromancerEntity::new);
        registerLiving("slimy_slug", SlimySlugEntity.class, 48, 3, true, SlimySlugEntity::new);
        registerLiving("raven", RavenEntity.class, 48, 3, true, RavenEntity::new);
        registerNaturalSpawns();
    }

    private static void register(String name, Class entityClass, int trackingRange, int updateFrequency, boolean sendVelocityUpdates) {
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, name), entityClass, name,
                nextId++, Eidolon.instance, trackingRange, updateFrequency, sendVelocityUpdates);
    }

    private static void registerLiving(String name, Class entityClass, int trackingRange, int updateFrequency,
                                       boolean sendVelocityUpdates, Function<World, Entity> factory) {
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, name), entityClass, name,
                nextId++, Eidolon.instance, trackingRange, updateFrequency, sendVelocityUpdates);
        FACTORIES.put(name, factory);
    }

    public static Entity create(String name, World world) {
        Function<World, Entity> factory = FACTORIES.get(name);
        return factory == null ? null : factory.apply(world);
    }

    private static void registerNaturalSpawns() {
        Biome[] overworld = collectBiomes(ModEntities::isOverworldBiome);
        Biome[] forests = collectBiomes(ModEntities::isForestBiome);
        Biome[] slugBiomes = collectBiomes(ModEntities::isSlugBiome);

        if (overworld.length > 0) {
            addSpawn(WraithEntity.class, CommonConfig.wraithSpawnWeight(), 1, 2, EnumCreatureType.MONSTER, overworld);
            addSpawn(ZombieBruteEntity.class, CommonConfig.zombieBruteSpawnWeight(), 1, 2, EnumCreatureType.MONSTER, overworld);
        }
        if (forests.length > 0) {
            addSpawn(RavenEntity.class, CommonConfig.ravenSpawnWeight(), 2, 5, EnumCreatureType.CREATURE, forests);
        }
        if (slugBiomes.length > 0) {
            addSpawn(SlimySlugEntity.class, CommonConfig.slimySlugSpawnWeight(), 2, 5, EnumCreatureType.CREATURE, slugBiomes);
        }
    }

    private static void addSpawn(Class entityClass, int weight, int min, int max, EnumCreatureType type, Biome[] biomes) {
        if (weight > 0) {
            EntityRegistry.addSpawn(entityClass, weight, min, max, type, biomes);
        }
    }

    private interface BiomePredicate {
        boolean test(Biome biome);
    }

    private static Biome[] collectBiomes(BiomePredicate predicate) {
        List<Biome> biomes = new ArrayList<>();
        for (Biome biome : ForgeRegistries.BIOMES.getValuesCollection()) {
            if (biome != null && predicate.test(biome)) {
                biomes.add(biome);
            }
        }
        return biomes.toArray(new Biome[0]);
    }

    private static boolean isOverworldBiome(Biome biome) {
        ResourceLocation id = ForgeRegistries.BIOMES.getKey(biome);
        String path = id == null ? "" : id.toString().toLowerCase(Locale.ROOT);
        return !path.contains("hell")
                && !path.contains("nether")
                && !path.contains("sky")
                && !path.contains("the_end")
                && !path.endsWith(":end");
    }

    private static boolean isForestBiome(Biome biome) {
        String name = biome.getBiomeName().toLowerCase(Locale.ROOT);
        return isOverworldBiome(biome)
                && (BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST)
                || name.contains("forest")
                || name.contains("woods")
                || name.contains("woodland"));
    }

    private static boolean isSlugBiome(Biome biome) {
        ResourceLocation id = ForgeRegistries.BIOMES.getKey(biome);
        String path = id == null ? "" : id.toString().toLowerCase(Locale.ROOT);
        String name = biome.getBiomeName().toLowerCase(Locale.ROOT);
        return isOverworldBiome(biome)
                && (path.endsWith(":redwood_taiga")
                || path.endsWith(":redwood_taiga_hills")
                || path.endsWith(":mutated_redwood_taiga")
                || path.endsWith(":mutated_redwood_taiga_hills")
                || path.endsWith(":giant_tree_taiga")
                || path.endsWith(":giant_spruce_taiga")
                || path.endsWith(":old_growth_pine_taiga")
                || path.endsWith(":old_growth_spruce_taiga")
                || path.endsWith(":lush_caves")
                || name.equals("mega taiga")
                || name.equals("mega taiga hills")
                || name.equals("mega spruce taiga")
                || name.equals("mega spruce taiga hills")
                || name.equals("lush caves"));
    }
}
