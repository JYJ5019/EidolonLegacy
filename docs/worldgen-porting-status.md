# Worldgen Porting Status

Source reference: `../eidolon-1.20x` targets Forge 1.19.2 data-driven worldgen. This port targets Minecraft 1.12.2 / Cleanroom, so structure sets, biome tags, jigsaw expansion, terrain adaptation, and negative-Y ore placement are approximated in the handwritten `IWorldGenerator`.

## Current Match

| Area | 1.20 reference | 1.12 port status |
| --- | --- | --- |
| Surface structure set | spacing `48`, separation `32`, salt `13215577`, lab weight 3, stray tower weight 3 | Constants preserved in `EidolonWorldGenerator`; candidate selection remains deterministic for `/eidolon_locate_structure` |
| Catacomb structure set | spacing `80`, separation `40`, salt `12512867` | Constants preserved; catacomb candidates are locatable and generated from copied NBT templates |
| Stray tower biome gate | `#minecraft:is_taiga` plus optional `#forge:is_snowy` | Approximated by 1.12 biome-name checks for taiga/snow/ice/cold/frozen/spruce/pine |
| Ores | lead size 6 count 6 from Y -27..41; silver size 6 count 5 from Y -64..33 | Kept on 1.12 positive-Y ranges instead of copying negative-Y values, to avoid collapsing generation near bedrock in a 0..255 world |
| Structure templates | `data/eidolon/structures` | Copied under `assets/eidolon/structures`; loader maps modern block ids to 1.12 equivalents where possible |

## Biome Modifier Replacement Map

Audited against
`../eidolon-1.20x/src/main/resources/data/eidolon/forge/biome_modifier` on
2026-06-09.

| Source biome modifier | 1.20 data intent | 1.12 Legacy replacement |
| --- | --- | --- |
| `lead_ore.json` | `forge:add_features` in `underground_ores` for `eidolon:lead_ore` in any biome | `EidolonWorldGenerator.generateOres` calls `WorldGenMinable` for `lead_ore` and `deep_lead_ore` in the overworld when `CommonConfig.leadEnabled()` is true. |
| `silver_ore.json` | `forge:add_features` in `underground_ores` for `eidolon:silver_ore` in any biome | `EidolonWorldGenerator.generateOres` calls `WorldGenMinable` for `silver_ore` and `deep_silver_ore` in the overworld when `CommonConfig.silverEnabled()` is true. |
| `raven.json` | `forge:add_spawns` in `#minecraft:is_forest`, weight 6, group 2-5 | `ModEntities.registerNaturalSpawns` collects overworld forest/woods/woodland biomes and calls `EntityRegistry.addSpawn(RavenEntity, CommonConfig.ravenSpawnWeight(), 2, 5, CREATURE, ...)`. Default weight is 6. |
| `slimy_slug.json` | `forge:add_spawns` in old-growth pine taiga, old-growth spruce taiga, and lush caves; weight 7, group 2-5 | `ModEntities.registerNaturalSpawns` approximates those IDs with 1.12 giant/redwood taiga names plus optional old-growth/lush-caves ids for modded backports, then calls `EntityRegistry.addSpawn(SlimySlugEntity, CommonConfig.slimySlugSpawnWeight(), 2, 5, CREATURE, ...)`. Default weight is 7. |
| `wraith.json` | `forge:add_spawns` in `#minecraft:is_overworld`, weight 60, group 1-2 | `ModEntities.registerNaturalSpawns` filters non-nether/non-end biomes and calls `EntityRegistry.addSpawn(WraithEntity, CommonConfig.wraithSpawnWeight(), 1, 2, MONSTER, ...)`. Default weight is 60. |
| `zombie_brute.json` | `forge:add_spawns` in `#minecraft:is_overworld`, weight 60, group 1-2 | `ModEntities.registerNaturalSpawns` uses the same overworld filter and calls `EntityRegistry.addSpawn(ZombieBruteEntity, CommonConfig.zombieBruteSpawnWeight(), 1, 2, MONSTER, ...)`. Default weight is 60. |

The spawn mapping is intentionally implemented in `ModEntities`, not in
worldgen JSON. Forge 1.12 has no biome modifier loader, so the closest stable
replacement is entity registration plus `EntityRegistry.addSpawn`. Spawn
weights are configurable and can be set to zero to disable individual natural
spawns.

## Chest Loot Parity

Audited against `../eidolon-1.20x/src/main/resources/data/eidolon/loot_tables/chests/*.json` on 2026-06-09.

| Table | 1.20 source | 1.12 JSON | `ModernTemplatePlacer` handwritten pool |
| --- | --- | --- | --- |
| `chests/lab` / `LAB_LOOT` | Rolls, weights, counts, and random enchanted book match | Match | Match |
| `chests/catacomb_basic` / `CATACOMB_LOOT` | Rolls, weights, and counts match | Match | Match |
| `chests/catacomb_coffin` / `COFFIN_LOOT` | Rolls, weights, counts, and random enchanted book match | Match, with `minecraft:skeleton_skull` represented as 1.12 `minecraft:skull` | Match, with `minecraft:skeleton_skull` represented as 1.12 `minecraft:skull` |

No loot parity patch is currently needed. The `minecraft:skull` entry is an intentional 1.12 item-id equivalent for the 1.20 `minecraft:skeleton_skull` entry; the handwritten pool creates it with default metadata.

## Known 1.12 Limits

- No vanilla 1.20 structure-set or jigsaw runtime exists in 1.12. Catacombs are therefore a deterministic handwritten approximation, not exact jigsaw expansion.
- 1.20 terrain adaptation (`beard_box`, `beard_thin`, `bury`) has no equivalent here. Placement avoids bedrock and uses explicit offsets, but surrounding terrain blending is not equivalent.
- 1.20 biome tags are approximated from biome names. Modded biomes with unusual names may not match the source tags perfectly.
- Negative-Y ore placement was intentionally not copied. Existing 1.12 ranges keep ore distribution playable in the older vertical build limit.

## Data-Driven vs Legacy Generator Notes

- In 1.20, `worldgen/structure_set/surface_structures.json` picks `lab` and `stray_tower` from the same random-spread placement with spacing `48`, separation `32`, salt `13215577`, and equal weight. In 1.12 this is mirrored by `EidolonWorldGenerator` candidate chunks plus a deterministic lab/tower roll.
- In 1.20, `worldgen/template_pool/*.json` and jigsaw settings choose pieces at runtime. In 1.12, `ModernTemplatePlacer` loads copied NBT templates directly, and `EidolonStructureGenerator` approximates catacomb expansion with a small fixed pool and offsets.
- In 1.20, stray tower biomes come from `data/eidolon/tags/worldgen/biome/has_structure/stray_tower.json`, which includes `#minecraft:is_taiga` and optional `#forge:is_snowy`. In 1.12, this is approximated by configurable lowercase biome-name keywords; defaults are `taiga`, `snow`, `ice`, `cold`, `frozen`, `spruce`, and `pine`, preserving the current behavior.
- In 1.20, biome modifiers add ores and natural mob spawns through data. In
  1.12, ore features live in `EidolonWorldGenerator` and natural spawns live in
  `ModEntities.registerNaturalSpawns`; both are config-gated rather than
  datapack-reloadable.
- Runtime diagnostics now report the effective stray tower biome keywords and include a note that structure sets, template pools, jigsaw expansion, and terrain adaptation are approximated by the legacy `IWorldGenerator`.

## Suggested Manual Tests

- In a fresh overworld, run `/eidolon_locate_structure lab 512 load`, teleport to the result, and verify the lab is underground, accessible, and has populated Eidolon chests.
- Run `/eidolon_locate_structure stray_tower 512 load` from or near a taiga/snow biome and verify the tower sits on the surface rather than floating or deeply buried.
- Run `/eidolon_locate_structure catacomb 512 load`, teleport to the result, and verify the center corridor is near the reported position with connected rooms on the same level.
- Check a newly generated mining area for lead/silver/deep variants and confirm ore frequency feels consistent with the existing 1.12 balance.
- In new chunks, verify ravens appear in forest-like overworld biomes, slimy
  slugs in mega/redwood taiga-style biomes, and wraiths/zombie brutes in
  overworld monster spawning contexts. If a spawn is absent, check the
  corresponding weight in `eidolon-common.cfg` first.
