# Tags, OreDictionary, Harvest, and Baubles Porting Status

Scope: compared `eidolon-1.20x/src/main/resources/data/minecraft/tags`,
`data/forge/tags`, `data/eidolon/tags`, and `data/curios/tags` against the
1.12 Legacy `ModOreDictionary`, `ModBlocks`, common config lists, and bauble
item classes.

## Forge Item/Block Tags

| Source tag | 1.12 mapping | Status |
| --- | --- | --- |
| `forge:ores/lead`, `forge:ores` | `oreLead` | Covered for `lead_ore` and `deep_lead_ore`. |
| `forge:ores/silver`, `forge:ores` | `oreSilver` | Covered for `silver_ore` and `deep_silver_ore`. |
| `forge:ingots/lead` | `ingotLead` | Covered. |
| `forge:ingots/silver` | `ingotSilver` | Covered. |
| `forge:ingots/pewter` | `ingotPewter` | Covered. |
| `forge:ingots/arcane_gold` | `ingotArcaneGold` | Covered. |
| `forge:nuggets/lead` | `nuggetLead` | Covered. |
| `forge:nuggets/silver` | `nuggetSilver` | Covered. |
| `forge:nuggets/pewter` | `nuggetPewter` | Covered. |
| `forge:nuggets/arcane_gold` | `nuggetArcaneGold` | Covered. |
| `forge:storage_blocks/lead` | `blockLead` | Covered. |
| `forge:storage_blocks/silver` | `blockSilver` | Covered. |
| `forge:storage_blocks/pewter` | `blockPewter` | Covered. |
| `forge:storage_blocks/arcane_gold` | `blockArcaneGold` | Covered. |
| `forge:storage_blocks/shadow_gem` | `blockShadowGem` | Covered. |
| `forge:storage_blocks/raw_lead` | `blockRawLead` | Covered. |
| `forge:storage_blocks/raw_silver` | `blockRawSilver` | Covered. |
| `forge:gems/shadow_gem`, `forge:gems` | `gemShadow` | Covered. |
| `forge:dusts/sulfur`, `forge:dusts` | `dustSulfur` | Covered. |
| `forge:raw_materials/lead` | `rawLead` | Covered. |
| `forge:raw_materials/silver` | `rawSilver` | Covered. |
| `forge:feathers` | `feather` plus `featherRaven` | Covered for raven feather. |
| `forge:tallow` | `tallow` | Covered. |

Legacy also registers internal recipe aliases such as `dustPewter`, `platePewter`,
`ingotElderBrick`, `gemSoul`, and `shardSoul`; these do not have direct source
Forge tags in the compared tag folders.

## Eidolon Item Tags

| Source tag | Source values | 1.12 mapping | Status |
| --- | --- | --- | --- |
| `eidolon:zombie_food` | `minecraft:rotten_flesh`, `eidolon:zombie_heart` | `CommonConfig.DEFAULT_ZOMBIE_FOOD` and `CommonConfig.zombieFood()` | Covered without a runtime item tag system. `GameplayEvents.isZombieFood` checks the configured item-id list when Undeath eating rules run. |

The 1.20 tag is intentionally represented as config data in Legacy because
Forge 1.12 does not provide the same datapack tag reload model. Pack authors can
extend or narrow the list in `eidolon-common.cfg` instead of adding a JSON tag.

## Minecraft Tags

| Source tag | 1.12 mapping | Status |
| --- | --- | --- |
| `minecraft:items/planks` | `plankWood` | Added `polished_planks`. Source only adds `polished_planks` to the item tag. |
| `minecraft:blocks/planks` | material/recipes, no block OreDictionary equivalent | Block tag includes `polished_planks` and `illwood_planks`; Legacy keeps block behavior through `Material.WOOD`. |
| `minecraft:blocks/illwood_logs`, `logs_that_burn` | block material / `BlockLog` behavior | No OreDictionary registration added. Source has block tags only, not item tags. |
| `minecraft:blocks/wooden_fences`, `fence_gates`, `wooden_stairs`, `wooden_slabs` | block material / vanilla block classes | No OreDictionary registration added. Source compared tags are block tags; Legacy recipes use explicit blocks or vanilla `slabWood`. |
| `minecraft:blocks/walls` | vanilla wall behavior | Covered by `BlockWall` subclasses; no OreDictionary equivalent. |
| `minecraft:blocks/mineable/axe` | material hardness/effective tool behavior | Legacy uses wood materials/classes for listed wood blocks. No harvest-level patch needed. |
| `minecraft:blocks/mineable/pickaxe` | material hardness/effective tool behavior | Legacy uses rock/iron materials. `needs_iron_tool` is handled separately below. |
| `minecraft:blocks/needs_iron_tool` | `setHarvestLevel("pickaxe", 1)` | Ores were already covered; storage/raw/gem blocks now explicitly covered. |

## Curios to Baubles

| Source Curios tag | Source items | Legacy slot/status |
| --- | --- | --- |
| `curios:belt` | `basic_belt`, `gravity_belt`, `resolute_belt` | `BaubleType.BELT`; covered. |
| `curios:charm` | `prestigious_palm` | `BaubleType.CHARM`; covered. |
| `curios:ring` | `basic_ring`, `angels_sight` | `basic_ring` is `RING`; `angels_sight` is `HEAD` in Legacy. Not changed to avoid changing equip behavior/save expectations. |
| `curios:head` | `mind_shielding_plate` | Legacy is `CHARM`. Not changed to avoid changing equip behavior/save expectations. |
| `curios:necklace` | `basic_amulet`, `sanguine_amulet`, `void_amulet`, `glass_hand`, `terminus_mirror`, `soulbone_amulet` | Basic/sanguine/void/soulbone are `AMULET`; glass hand and terminus mirror are `CHARM`. Not changed for compatibility. |
| `curios:body` | `warded_mail`, `raven_cloak` | Legacy implements these as chest-slot curio armor items outside `item/curio`. Not changed in this task. |

Slot differences are deliberate compatibility substitutions, not missing tag
ports:

- Curios allows named slots such as `ring`, `head`, `necklace`, and `body`;
  Legacy relies on Baubles slot types plus normal armor slots.
- `angels_sight` is kept in `BaubleType.HEAD` even though the source lists it
  under `curios:ring`.
- `mind_shielding_plate`, `glass_hand`, and `terminus_mirror` are kept as
  `BaubleType.CHARM` to preserve existing 1.12 equip behavior.
- `warded_mail` and `raven_cloak` are chest equipment items based on
  `EidolonCurioArmorItem`; their effects are checked from the chest slot rather
  than a Curios `body` inventory.

## Notes

- No mixins or build configuration changes are required for these mappings.
- Block-only tags were not blindly converted to OreDictionary item names where
  the source did not define an item tag. This avoids expanding recipe inputs
  beyond the observed source item-tag surface.
- The zombie food tag is not mapped to OreDictionary because it is gameplay
  policy for Undeath eating rules, not a recipe ingredient family.
