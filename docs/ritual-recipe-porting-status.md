# Ritual and Recipe Porting Status

Source reference: `../eidolon-1.20x` targets modern data-driven rituals and recipe types. This port targets Minecraft 1.12.2 / Cleanroom, so ritual behavior is represented by `AltarRitual` plus JSON resources under `assets/eidolon/altar_rituals`, and modern recipe types are approximated by the 1.12 recipe systems.

## Ritual ID Coverage

Audited against `../eidolon-1.20x/src/main/java/elucent/eidolon/ritual/RitualRegistry.java` on 2026-06-09.

| 1.20 ritual ID | 1.12 status | Notes |
| --- | --- | --- |
| `absorption` | Present | Uses `summoning_staff` focus. The JSON leaves `sacrifice` implicit, so `AltarRitual.defaultSacrifice` makes the first offering, `death_essence`, the trigger item. |
| `allure` | Present | `rose_bush` is represented as 1.12 `minecraft:double_plant#4`; red dye is `minecraft:dye#1`. |
| `bonechill_recharging` | Present | Focus is `eidolon:bonechill_wand`; offerings preserve lesser soul gem, snowballs, and redstone. |
| `crystal` | Present | Bone meal is represented as 1.12 `minecraft:dye#15`; redstone offerings preserved. |
| `daylight` | Present | Sunflower is represented as 1.12 `minecraft:double_plant#0`; charcoal and seed offerings preserved. |
| `deceit` | Present | Emerald sacrifice/requirement preserved; mushroom tag is narrowed to brown mushroom for 1.12. |
| `moonlight` | Present | Black dye is represented as 1.12 `minecraft:dye#0`; snowball, spider eye, and soul shard offerings preserved. |
| `purify` | Present | Sacrifice is `minecraft:speckled_melon`; enchanted ash x2, healing potion, and soul shard x2 are preserved. |
| `repelling` | Present | Nautilus shell has no 1.12 equivalent; `minecraft:prismarine_crystals` is the current substitute. |
| `sanguine_sanguine_amulet` | Present | Harming potion sacrifice, `basic_amulet` focus, and health cost `40` are explicit. |
| `sanguine_sapping_sword` | Present | Harming potion sacrifice, iron sword focus, and health cost `20` are explicit. |
| `soulfire_recharging` | Present | Focus is `eidolon:soulfire_wand`; offerings preserve lesser soul gem, blaze powder, and redstone. |
| `summon_drowned` | Present with 1.12 substitute | Drowned does not exist in 1.12; focus is `minecraft:prismarine_shard`, summon entity/result is `minecraft:guardian`. |
| `summon_husk` | Present | Uses 1.12 husk entity with charcoal sacrifice and sand focus. |
| `summon_phantom` | Present with 1.12 substitute | Phantom membrane/entity do not exist in 1.12; focus is `eidolon:raven_feather`, summon entity/result is `minecraft:vex`. |
| `summon_skeleton` | Present | Uses skeleton entity with charcoal sacrifice and bone focus. |
| `summon_stray` | Present | Uses stray entity with charcoal sacrifice and string focus. |
| `summon_wither_skeleton` | Present | Uses wither skeleton entity with charcoal sacrifice and soul sand focus. |
| `summon_wraith` | Present | Uses Eidolon wraith entity with charcoal sacrifice and tattered cloth focus. |
| `summon_zombie` | Present | Uses zombie entity with charcoal sacrifice and rotten flesh focus. |

The 1.12 port also has legacy altar-resource rituals not registered by the modern `RitualRegistry`: `death_essence`, `imbued_bones`, `lesser_soul_gem`, `soulbone_amulet`, `wicked_weave`, and `wraith_heart`. These are retained as port-era altar recipes and are not treated as missing or conflicting modern behavior.

## Focus, Sacrifice, Offering, and Health Notes

- `AltarRitual` supports explicit `focus`, explicit `sacrifice`, ordered `offerings`, `entity`, and `health`. JSON entries that omit `sacrifice` fall back to the first offering, then focus, then result, matching the current 1.12 implementation.
- Modern `MultiItemSacrifice(main, focus)` is represented by explicit `sacrifice` plus `focus` in summon and sanguine JSON files. Provider-mode altar setup consumes provider offerings separately and handles focus through the existing focus-provider path.
- Sanguine health costs are data-driven and explicit: `sanguine_sapping_sword` costs `20`, `sanguine_sanguine_amulet` costs `40`.
- `purify` and `absorption` keep their behavior-specific code paths. No gameplay change was made during this audit.

## Recipe Type Convergence

Audited against `../eidolon-1.20x/src/main/resources/data/eidolon/recipes` on 2026-06-09.

| Recipe type in 1.20 data | Source count | 1.12 representation |
| --- | ---: | --- |
| `eidolon:crucible` | 27 | Loaded by the 1.12 crucible recipe registry, not vanilla JSON. |
| `eidolon:worktable` | 21 + 2 case-sensitive-key files | Loaded by the 1.12 worktable recipe registry, not vanilla JSON. |
| `forge:conditional` | 4 | Only dust lead/silver smelt/blast conditions use this in source; 1.12 approximates them through ore dictionary smelting for `dustLead` and `dustSilver`. |
| `minecraft:blasting` | 7 | No 1.12 blasting type exists. Modern blast/smelt pairs are represented as furnace recipes in `ModRecipes`. |
| `minecraft:crafting_shaped` | 55 | 1.12 uses vanilla shaped crafting JSON where the item/block exists or has a legacy equivalent. |
| `minecraft:crafting_shapeless` | 14 | 1.12 uses vanilla shapeless crafting JSON where the item/block exists or has a legacy equivalent. |
| `minecraft:smelting` | 7 | Represented by `GameRegistry.addSmelting` in `ModRecipes`. |
| `minecraft:stonecutting` | 12 | No 1.12 stonecutter exists. The 12 smooth-stone `*_stonecutting*.json` files are retained by name under `worktable_recipes` and use Eidolon worktable outputs. |

Current 1.12 vanilla recipe resources under `assets/eidolon/recipes` contain `minecraft:crafting_shaped` and `minecraft:crafting_shapeless` only. Furnace conversions are registered in code because 1.12 smelting is not data-driven in the same way. Smooth-stone stonecutting replacements are intentionally outside the vanilla recipe directory so the Eidolon worktable does not depend on vanilla crafting fallback behavior.

## Modern Recipe Replacement Map

The table below is the document-facing map for pack authors and QA. It is not a
new gameplay registry.

| 1.20 source data | Modern behavior | 1.12 Legacy replacement |
| --- | --- | --- |
| `blast_lead_ore.json`, `blast_silver_ore.json` | `minecraft:blasting` from `forge:ores/*` | Same ore inputs are covered by `oreLead` and `oreSilver`; outputs are registered through `GameRegistry.addSmelting` on lead/silver ore and deep variants. |
| `blast_raw_lead.json`, `blast_raw_silver.json` | `minecraft:blasting` from raw material items | `ModRecipes` registers ordinary furnace smelting for `raw_lead` and `raw_silver`. |
| `blast_pewter_blend.json` | `minecraft:blasting` from `pewter_blend` | `ModRecipes` registers ordinary furnace smelting to `pewter_ingot`. |
| `blast_enchanted_ash.json` | `minecraft:blasting` from bone | `ModRecipes` registers ordinary furnace smelting from `minecraft:bone` to `enchanted_ash` x2. |
| `tallow.json` | `minecraft:blasting` from rotten flesh | `ModRecipes` registers ordinary furnace smelting from `minecraft:rotten_flesh` to `tallow`. |
| `smelt_lead_ore.json`, `smelt_silver_ore.json` | `minecraft:smelting` from `forge:ores/*` | `GameRegistry.addSmelting` handles lead/silver ore and deep variants directly. |
| `smelt_raw_lead.json`, `smelt_raw_silver.json` | `minecraft:smelting` from raw material items | `GameRegistry.addSmelting` handles raw lead/silver directly. |
| `smelt_pewter_blend.json` | `minecraft:smelting` from `pewter_blend` | `GameRegistry.addSmelting` handles `pewter_blend` directly. |
| `enchanted_ash.json`, `enchanted_ash_from_block.json` | `minecraft:smelting` from bone or bone block | `GameRegistry.addSmelting` handles `minecraft:bone` and `minecraft:bone_block`; bone block yields six ash. |
| `smelt_lead_dust.json`, `blast_lead_dust.json` | `forge:conditional` around lead dust smelting/blasting, enabled only when `forge:dusts/lead` is non-empty | 1.12 has no conditional recipe wrapper; `addOreSmelting("dustLead", ...)` registers furnace recipes for currently registered OreDictionary entries. |
| `smelt_silver_dust.json`, `blast_silver_dust.json` | `forge:conditional` around silver dust smelting/blasting, enabled only when `forge:dusts/silver` is non-empty | 1.12 uses `addOreSmelting("dustSilver", ...)` for the same compatibility intent. |
| `smooth_stone_*_stonecutting*.json` | `minecraft:stonecutting` for smooth-stone brick/tile slabs, stairs, walls, and base blocks | The same filenames exist under `assets/eidolon/worktable_recipes` as `eidolon:worktable` recipes with empty reagent slots; 1.20 `minecraft:smooth_stone` is represented as 1.12 `minecraft:stone` metadata `0`. |

Notes:

- The source count still reports only 7 top-level `minecraft:blasting` files;
  the lead/silver dust blast recipes are nested inside `forge:conditional` and
  are counted in that row instead.
- Legacy does not attempt to recreate blast-furnace speed, stonecutter UI, or
  conditional JSON loading. The compatibility target is that the same materials
  remain obtainable through 1.12 furnace, OreDictionary, and Eidolon worktable
  systems.

`stone_altar` and `unholy_effigy` are modern worktable recipes, but their
source JSON keys intentionally include both lowercase and uppercase forms such
as `s` and `S`. Some case-insensitive tooling reports them as parse errors
instead of counting them with the other 21 worktable files. Legacy ports both
explicitly under `worktable_recipes`, preserving the intended case-sensitive
ingredient layout.

## Diagnostics

`RuntimeDiagnostics` now records:

- runtime altar ritual IDs,
- expected modern altar ritual IDs,
- missing modern ritual IDs,
- legacy extra ritual IDs,
- per-ritual behavior, focus, sacrifice, offering count, health cost, entity, and result,
- modern recipe type baseline counts and the 1.12 approximation notes.

The diagnostics are observational only. They do not register rituals, mutate recipe data, or change default gameplay.
