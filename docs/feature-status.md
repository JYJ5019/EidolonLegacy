# Feature Status

## Milestone 1: Empty Mod Shell

- [x] Create `eidolon-1.12-port` from the Cleanroom mixin template.
- [x] Change mod metadata to Eidolon.
- [x] Move main package to `elucent.eidolon`.
- [x] Replace template main class with `Eidolon`.
- [x] Keep mixin configs present but empty.
- [x] Disable template coremod.
- [x] Verify `gradlew build` after Gradle wrapper downloads successfully.
- [x] Verify `gradlew runClient` or IDEA `runClient` manually.
- [x] Enter a single-player world and exit normally.

## Verification Notes

- 2026-05-30: `gradlew build` could not complete because the Gradle wrapper download was blocked/hung. The project-local temporary cache `.gradle-home/` is ignored.
- 2026-05-31: Fixed the missing Mixin resource launch path by setting `crl.dev.extrapath`; `runClient` should be re-tested manually by the user after refreshing Gradle in IDEA.
- 2026-05-31: User confirmed `gradlew build` completed successfully.
- 2026-05-31: Latest client log shows Eidolon loaded, ran `preInit`/`init`/`postInit`, entered an integrated server world, and exited normally.
- 2026-06-06: `gradlew.bat compileJava processResources` passed for the frozen baseline after 1.12 recipe/altar JSON compatibility fixes.
- 2026-06-06: `gradlew.bat runClient` reached main-menu startup and exited cleanly. Latest log loaded 24 Worktable recipes, 20 Crucible recipes, 22 Altar rituals, registered the matching HEI categories, registered Eidolon world generation, and showed no recipe/altar parse errors in the targeted scan.
- Git initialization/commit workflow is intentionally skipped for now per user direction.

## Current Baseline Freeze - 2026-06-06

This baseline records the current Legacy port state before the next implementation pass. It is based on the detailed milestones below and the current source tree.

### Baseline Completed

- [x] Cleanroom/Forge 1.12.2 project shell, metadata, proxies, registration lifecycle, access transformer, HEI compatibility access points, and debug command wiring are in place.
- [x] Static item/block closure is reached for the current source-visible set: the 1.12 port has matching item model names and blockstate names for the 1.20 reference resources.
- [x] Base materials, ores, storage blocks, decorative blocks, stairs, walls, slabs, wood sets, logs, herbs, candles, ashes, altars, Worktable, Crucible, Research Table, Codex, static utility items, armor, weapons, trinkets, wands, music disc, and spawn egg items are registered with 1.12 resources.
- [x] Worktable has recipe data loading, GUI/container behavior, output execution, Codex display, HEI display, and recipe transfer support.
- [x] Crucible has recipe data loading, HEI/Codex display, required fluid state, item insertion, stirring, step submission, success/failure feedback, and first-pass TESR fluid/input rendering.
- [x] Research has Research Notes generation, Research Table task preview/submission/progress, Completed Research conversion/use, persisted known-research storage, sync packet support, Codex research pages, and research debug commands.
- [x] Codex has source-style page backgrounds, index entries, research directory/detail paging, recipe directories for Worktable/Crucible, altar ritual/offering pages, Athame/Soul Shard acquisition pages, and source-style side tabs.
- [x] Altar has TileEntity offering storage/rendering, offering power/capacity scan, JSON ritual loading, item-result/item-transform/item-charge/entity-summon/absorption/health-cost behavior types, Codex/HEI display, and success feedback.
- [x] Gameplay behavior exists for current dedicated items: Athame, Sapping Sword, Reaper Scythe, Deathbringer Scythe, Cleaving Axe, Reversal Pick, Alchemist's Tongs, Soulfire Wand, Bonechill Wand, and Summoning Staff.
- [x] Entity registration exists for wand projectiles and the current Eidolon living entities; client renderers are registered for the current entities/projectiles.
- [x] World generation is registered for ores, Illwood trees, lab/stray tower structures, and a lightweight catacomb template-pool approximation.

### Baseline Differences From Source

- [x] The port is a 1.12-friendly rewrite rather than a direct class-for-class downgrade. Deferred registers, tags, datapack recipes, Curios, modern particles, shaders, and modern worldgen are mapped to 1.12 registry events, OreDictionary/custom loaders, Baubles-style handling, vanilla particles/TESR, and `IWorldGenerator`.
- [x] Codex and research are intentionally rebuilt around the current 1.12 systems instead of copying the full source `Category`/`Chapter`/`Page` hierarchy.
- [x] Rituals are currently lightweight altar recipes with direct execution; full source ritual timing, symbols, particle systems, deity/reputation coupling, and advanced requirements are still deferred.
- [x] Reagent classes exist, but a complete reagent pipe/tank network and machine integration are not part of this frozen baseline.
- [x] Several machines remain static or partial: Soul Enchanter, Wooden Brewing Stand, Incubator, and deeper reagent-backed behavior need later passes.
- [x] Runtime verification for the newest milestones remains outstanding because the latest detailed entries still mark compile/runtime validation as user-handled.

### Verification Target For This Freeze

- [x] Run `gradlew.bat compileJava processResources` from `EidolonLegacy`.
- [x] Launch a client build and verify startup-level loading for Worktable, Crucible, Altar rituals, HEI integration, and world generation registration.
- [ ] Manually verify in-game interaction for Worktable, Crucible, Research Table/Codex, Altar rituals, Soulfire/Bonechill Wand, Summoning Staff, Alchemist's Tongs, and new-world generation in newly generated chunks.
- [x] Record startup/runtime loading regressions below the affected milestone before starting the next implementation pass.

## Next Milestones

- [x] Verify the frozen 2026-06-06 baseline with `compileJava processResources` and startup-level client runtime checks.
- [ ] Complete hands-on in-game verification for the frozen baseline checklist.
- [x] Fill the reagent network gap first pass: tank storage, glass tube transfer, cistern behavior, Crucible/Altar integration, renderer feedback, and failure cases.
- [ ] Complete remaining partial machines: Soul Enchanter, Wooden Brewing Stand, Incubator, and any reagent-backed machine recipes.
- [ ] Expand entity behavior and runtime acquisition: natural spawns, AI parity, drops, advanced renderer polish, and source-inspired structure loot.
- [ ] Replace remaining visual placeholders with 1.12-safe particles/TESR/audio assets without trying to directly port modern shader-only behavior.
- [ ] Add release-facing coverage: English language fallback, README status table, known differences, HEI/CraftTweaker notes, and server/client verification notes.

## Milestone 2: Registry Smoke Test

- [x] Add `ModCreativeTabs` with an Eidolon creative tab.
- [x] Add `ModItems` and register `test_sigil`.
- [x] Add `ModBlocks` and register `test_stone` plus its `ItemBlock`.
- [x] Add client model registration for the test item and block item.
- [x] Add temporary vanilla-backed models for `test_sigil` and `test_stone`.
- [x] Add `zh_cn.lang` entries for the creative tab, item, and block.
- [x] Build verified by user.
- [x] Runtime verified by user: Eidolon tab, `Test Sigil`, `Test Stone`, and `Test Stone` placement/breaking are normal.

## Milestone 3: First Real Items And Blocks

- [x] Register basic material items: lead, silver, pewter, arcane gold, and shadow gem entries.
- [x] Register basic blocks: lead/silver ores, metal/raw storage blocks, pewter block, arcane gold block, and shadow gem block.
- [x] Add 1.12 model JSON for the first material item batch.
- [x] Add 1.12 model and blockstate JSON for the first block batch.
- [x] Copy corresponding textures from the source project.
- [x] Add `zh_cn.lang` names for the first real batch.
- [x] User verified the first real batch textures render normally.
- [x] User verified the first real batch Chinese display names are normal.
- [ ] Build verification is pending user-side `gradlew build`.
- [ ] Runtime verification for crafting, smelting, and block drops is pending user-side `runClient`.
- [ ] OreDictionary entries and world generation are intentionally deferred.

## Milestone 4: First Crafting, Smelting, And Drops

- [x] Add basic storage-block recipes for lead, silver, pewter, arcane gold, and shadow gem.
- [x] Add ingot/nugget conversion recipes for lead, silver, pewter, and arcane gold.
- [x] Add raw lead/raw silver block packing and unpacking recipes.
- [x] Add smelting for lead ore, raw lead, silver ore, raw silver, and pewter blend.
- [x] Add block loot tables for the first real block batch.
- [x] Static JSON syntax check passed.
- [x] Fix client-side recipe registration by making `ClientProxy` extend `CommonProxy`.
- [x] Add JSON smelting recipes as a resource-level path for furnace recipes.
- [x] Change lead ore and silver ore loot tables to drop raw lead/raw silver.
- [x] User verified furnace recipes now work.
- [x] Add a 1.12 `DroppingOreBlock` path so lead ore and silver ore return raw lead/raw silver from `getItemDropped`.
- [x] User verified ore drops now work.
- [x] Add OreDictionary registrations for lead, silver, pewter, arcane gold, shadow gem, raw lead, and raw silver.
- [x] Update compatible crafting recipe inputs to use `forge:ore_dict`.
- [x] Static JSON syntax check passed after OreDictionary recipe updates.
- [x] User verified OreDictionary-backed recipes are normal.
- [x] Ore world generation is intentionally skipped for now.
- [ ] Build verification is pending user-side `gradlew build`.
- [x] Runtime verification passed for OreDictionary-backed recipes.

## Milestone 5: Second Material Item Batch

- [x] Register second low-risk material batch: elder brick, sulfur, gold inlay, hearts, cloth, soul shard, essences, ender calx, tallow, lesser soul gem, wicked weave, imbued bones, raven feather, resin, ink, wax, arcane seal, and parchment.
- [x] Add client model registrations for the second material batch.
- [x] Copy item textures from the source project and adapt paths for 1.12.
- [x] Add 1.12 item model JSON for the second material batch.
- [x] Add `zh_cn.lang` names for the second material batch.
- [x] Add a few OreDictionary entries for the second material batch.
- [x] Static JSON syntax check passed.
- [x] Build and runtime verified by user.

## Milestone 6: First Simple Decorative Blocks

- [x] Register simple full-block decorative batch: smooth stone bricks, mossy smooth stone bricks, smooth stone masonry, smooth stone tiles, elder bricks, elder bricks eye, elder masonry, and elder pillar.
- [x] Add item block registrations through the existing item-block registry path.
- [x] Add blockstate, block model, item model, loot table, and texture resources.
- [x] Add `zh_cn.lang` names.
- [x] Add basic crafting recipes for smooth stone and elder block variants.
- [x] Static JSON syntax check passed.
- [x] Build and runtime verified by user.

## Milestone 7: First Stairs And Walls

- [x] Register stair variants for smooth stone bricks, smooth stone masonry, smooth stone tiles, elder bricks, and elder masonry.
- [x] Register wall variants for smooth stone bricks and elder bricks.
- [x] Add item block registrations through the existing item-block registry path.
- [x] Add blockstate, block model, item model, loot table, and recipe resources.
- [x] Add `zh_cn.lang` names.
- [x] Static JSON syntax check passed.
- [x] User verified stair variants are normal; wall item icons were broken.
- [x] Change wall item models to conservative `block/cube_all` previews.
- [x] Add both `wall` and `texture` texture keys to wall block models for 1.12/Cleanroom compatibility.
- [x] User verified placed walls render normally, but wall item icons remained broken.
- [x] Change wall item models to inherit from the verified wall post block models.
- [x] User found each wall appears twice in the creative tab: one valid icon and one broken metadata variant.
- [x] Add a single-variant wall `ItemBlock` to expose only metadata 0 in the creative tab.
- [x] Add a custom `SimpleWallBlock` to expose only metadata 0 from the block side as well.
- [x] Register fallback item models for wall metadata 1 to avoid purple/black icons if a stale variant appears.
- [x] Static JSON syntax check passed after wall model fix.
- [x] Runtime verified by user after duplicate wall variant fix.

## Milestone 8: First Slabs

- [x] Register slab variants for smooth stone bricks, smooth stone masonry, smooth stone tiles, elder bricks, and elder masonry.
- [x] Register matching double-slab blocks for placement merging.
- [x] Add item block registrations; double slabs are hidden from creative/model registration.
- [x] Runtime verified by user.

## Milestone 9: First Wood Decorative Blocks

- [x] Register illwood planks and polished planks.
- [x] Register matching stairs, slabs, double slabs, fences, and fence gates.
- [x] Add blockstates, models, item models, loot tables, recipes, textures, and zh_cn names.
- [x] User found fence gate item/block models were broken and slabs could only place bottom halves without merging.
- [x] Switch slab items to vanilla `ItemSlab` and stop registering double-slab item forms.
- [x] Switch fence gate models/blockstates to Minecraft 1.12 `fence_gate_closed/open` and `wall_gate_closed/open` parents.
- [x] User verified slab top placement/merging and fence gate item models are fixed, but placed fence gates still render missing textures.
- [x] Add `powered=false/true` variants to fence gate blockstates because the runtime state includes the powered property.
- [x] User verified placed fence gate rendering and open/close states are normal.

## Milestone 10: Wood Logs And Pillars

- [x] Register illwood log, stripped illwood log, illwood bark, stripped illwood bark, and polished wood pillar.
- [x] Use a simple `BlockLog` implementation for axis placement.
- [x] Add blockstates, block models, item models, loot tables, textures, one pillar recipe, and zh_cn names.
- [x] Static JSON syntax check passed.
- [x] `gradlew compileJava` passed.
- [x] Fix `SimpleLogBlock` startup crash by explicitly creating the `LOG_AXIS` blockstate container and meta mapping.
- [x] `gradlew compileJava` passed after the `SimpleLogBlock` fix.
- [x] Fix missing placed log models by removing the nonexistent 1.12 `block/cube_column_horizontal` parent and rotating the base `cube_column` model instead.
- [x] Keep elder pillar axis locked; this matches the current simple pillar implementation.
- [x] User verified wood log and pillar batch is normal after the horizontal model fix.

## Milestone 11: Deep Ores

- [x] Register deep lead ore and deep silver ore.
- [x] Reuse existing ore drop behavior so deep lead ore drops raw lead and deep silver ore drops raw silver.
- [x] Add blockstates, block models, item models, loot tables, textures, smelting recipes, OreDictionary entries, and zh_cn names.
- [x] Keep ore world generation skipped.
- [x] Static JSON syntax check passed.
- [x] `gradlew compileJava` passed.
- [x] User verified deep ore creative entries, models, drops, and smelting are normal.

## Milestone 12: Bone Pile Blocks

- [x] Register bone pile, bone pile stairs, bone pile slab, and matching double slab.
- [x] Reuse the stable stair and vanilla `ItemSlab` slab paths.
- [x] Add blockstates, block models, item models, loot tables, recipes, texture, and zh_cn names.
- [x] Static JSON syntax check passed.
- [x] `gradlew compileJava` passed.
- [x] Temporarily disable HEI/JEI runtime jar by moving it from `run/client/mods` to `run/client/mods-disabled`; third-party JEI compatibility remains skipped for the current ordinary block phase.
- [x] User requested an early HEI compatibility attempt.
- [x] Enable the project access transformer and expose `TextureMap#initMissingImage()` for HEI 4.28.0.
- [x] Move the HEI jar back to `run/client/mods`.
- [x] `gradlew compileJava processResources` passed and confirmed the user access transformer is applied.
- [x] User verified HEI passed the `TextureMap.initMissingImage()` access point, then failed on private field `TextureMap#mapUploadedSprites`.
- [x] Expose `TextureMap#mapUploadedSprites` through the project access transformer.
- [x] `gradlew compileJava processResources` passed after the `mapUploadedSprites` AT entry.
- [x] User verified HEI passed `mapUploadedSprites`, then failed on private field `TextureMap#listAnimatedSprites`.
- [x] Expose `TextureMap#listAnimatedSprites` through the project access transformer.
- [x] `gradlew compileJava processResources` passed after the `listAnimatedSprites` AT entry.
- [x] User verified HEI passed `listAnimatedSprites`, then failed on private field `TextureMap#mapRegisteredSprites`.
- [x] Expose `TextureMap#mapRegisteredSprites` through the project access transformer.
- [x] `gradlew compileJava processResources` passed after the `mapRegisteredSprites` AT entry.
- [x] User verified HEI passed `mapRegisteredSprites`, then failed on private field `TextureMap#basePath`.
- [x] Expose `TextureMap#basePath` through the project access transformer.
- [x] `gradlew compileJava processResources` passed after the `basePath` AT entry.
- [x] User verified HEI passed `basePath`, then failed on private field `TextureMap#missingImage`.
- [x] Expose the remaining likely TextureMap texture-registration internals used by HEI: `missingImage`, `mipmapLevels`, `iconCreator`, `loadingSprites`, and `loadedSprites`.
- [x] `gradlew compileJava processResources` passed after the additional TextureMap AT entries.
- [x] User verified HEI reached vanilla plugin registration, then failed because `Ingredient#matchingStacks` was private; the later empty recipe category error was a consequence of vanilla categories not registering.
- [x] Expose `Ingredient#matchingStacks` through the project access transformer.
- [x] `gradlew compileJava processResources` passed after the `Ingredient#matchingStacks` AT entry.
- [x] User verified HEI reached the fast item renderer, then failed on private method `RenderItem#renderModel(IBakedModel, ItemStack)`.
- [x] Expose `RenderItem#renderModel(IBakedModel, ItemStack)` through the project access transformer.
- [x] `gradlew compileJava processResources` passed after the `RenderItem#renderModel` AT entry.
- [x] User verified HEI then failed on the `RenderItem#renderModel(IBakedModel, int)` overload.
- [x] Expose the remaining private `RenderItem#renderModel` overloads used by fast rendering.
- [x] `gradlew compileJava processResources` passed after the additional `RenderItem#renderModel` overload AT entries.
- [x] User verified HEI starts normally after the additional `RenderItem#renderModel` overload AT entries.
- [x] User verified HEI later failed on private field `GuiRecipeBook#width`.
- [x] Expose `GuiRecipeBook#width` through the project access transformer.
- [x] User verified HEI then failed on private field `GuiRecipeBook#xOffset`.
- [x] Expose `GuiRecipeBook#xOffset` through the project access transformer.
- [x] User verified HEI then failed on private field `GuiRecipeBook#height`.
- [x] Expose `GuiRecipeBook#height` through the project access transformer.
- [x] User verified HEI then failed on private field `GuiRecipeBook#recipeTabs`.
- [x] Expose `GuiRecipeBook#recipeTabs` through the project access transformer.
- [x] User verified HEI is normal after the `GuiRecipeBook` AT entries.
- [x] User verified bone pile blocks are normal.

## Milestone 13: Smooth Stone Arch

- [x] Register smooth stone arch.
- [x] Add a simple actual-state block that switches model by whether adjacent arch blocks exist above or below.
- [x] Add blockstate, block models, item model, loot table, recipe, textures, and zh_cn name.
- [x] Static JSON syntax check passed.
- [x] `gradlew compileJava processResources` passed.
- [x] User verified smooth stone arch is normal.

## Milestone 14: Effigies

- [x] Register straw effigy and unholy effigy.
- [x] Add a simple horizontal-facing block implementation for static decorative placement.
- [x] Add blockstates, block models, item models, loot tables, textures, and zh_cn names.
- [x] Add the vanilla crafting recipe for straw effigy.
- [x] Defer unholy effigy worktable recipe because the worktable/reagent systems are not ported yet.
- [x] Static JSON syntax check passed.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified effigies are normal.

## Milestone 15: Small Decor Blocks

- [x] Register goblet and stone hand.
- [x] Reuse a small non-full-cube decor block for goblet.
- [x] Reuse the horizontal-facing decorative block path for stone hand.
- [x] Add blockstates, block models, item models, loot tables, recipes, textures, and zh_cn names.
- [x] Convert goblet's arcane gold recipe input to the existing `ingotArcaneGold` OreDictionary key.
- [x] Static JSON syntax check passed.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified goblet and stone hand are normal.

## Milestone 16: Candles

- [x] Register candle, candlestick, magic candle, and magic candlestick.
- [x] Reuse the small non-full-cube decor block for floor candles.
- [x] Add an attachable decorative block path for floor or wall candlesticks.
- [x] Add blockstates, block models, item models, loot tables, recipes, textures, and zh_cn names.
- [x] Add `tallow` OreDictionary registration for candle recipe compatibility.
- [x] Static JSON syntax check passed.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified candles and candlesticks are normal.

## Milestone 17: Herb Blocks

- [x] Register avennian sprig, merammer root, oanna bloom, and sildrian seed.
- [x] Add a simple two-stage herb block with `age=0/1`, random growth, bonemeal growth, and cutout rendering.
- [x] Temporarily allow placement on grass, dirt, and farmland because the source planter block is not ported yet.
- [x] Add blockstates, block models, item models, loot tables, textures, and zh_cn names.
- [x] Defer planter-only planting behavior until planter is ported.
- [x] Static JSON syntax check passed.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified herb blocks are normal.

## Milestone 18: Illwood Leaves And Sapling

- [x] Register illwood leaves and illwood sapling.
- [x] Add simple transparent leaves rendering without decay/worldgen behavior.
- [x] Add a simple sapling block that can be placed on grass, dirt, and farmland, without tree growth yet.
- [x] Add blockstates, block models, item models, loot tables, textures, and zh_cn names.
- [x] Defer sapling tree generation until illwood tree generation is ported.
- [x] Static JSON syntax check passed.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entries, item/block models, sapling placement, and drops.

## Milestone 19: Enchanted Ash

- [x] Register enchanted ash as a placeable connecting floor block.
- [x] Add simple four-direction connection rendering with `none/side/up` state values, currently producing `none/side` only.
- [x] Add blockstate, block models, item model, loot table, textures, and zh_cn name.
- [x] Add furnace smelting from bone and bone block.
- [x] Defer source special collision/barrier behavior for specific living entities.
- [x] Static JSON syntax check passed.
- [x] Fix connected ash missing models by restoring model parents from `eidolon:blocks/...` to `eidolon:block/...`.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified enchanted ash is normal after connected model fix.

## Milestone 20: Planter

- [x] Register planter as a simple decorative block.
- [x] Add blockstate, block model, item model, loot table, textures, and zh_cn name.
- [x] Defer source worktable recipe because worktable/reagent systems are not ported yet.
- [x] Defer planter-specific herb planting behavior until planter block is runtime-verified.
- [x] Static JSON syntax check passed.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified planter is normal.

## Milestone 21: Glass Tube

- [x] Register glass tube as a non-TileEntity translucent block.
- [x] Use a single stable straight model for the temporary non-functional glass tube.
- [x] Add blockstate, block models, item model, loot table, texture, and zh_cn name.
- [x] Defer reagent pipe TileEntity and transfer behavior.
- [x] Static JSON syntax check passed.
- [x] Simplify glass tube to a single stable straight model after the complex multipart placement rendered incorrectly.
- [x] Replace the single straight model with a stable six-direction visual connector using simple center and arm models.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified glass tube is normal.

## Milestone 22: Obelisk

- [x] Register obelisk as a non-TileEntity decorative block.
- [x] Add simple vertical actual-state rendering so stacked obelisks switch between bottom, middle, and top models.
- [x] Add blockstate, block models, item model, loot table, recipe, textures, and zh_cn name.
- [x] Defer ritual/reagent behavior.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entry, item/block models, stacked vertical rendering, placement, breaking, drop, and recipe.

## Milestone 23: Plinth

- [x] Register plinth as a non-TileEntity decorative block.
- [x] Reuse the stable vertical actual-state rendering path for stacked bottom, middle, and top models.
- [x] Add blockstate, block models, item model, loot table, recipe, textures, and zh_cn name.
- [x] Defer displayed-item TileEntity behavior and ritual interactions.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entry, item/block models, stacked vertical rendering, placement, breaking, drop, and recipe.

## Milestone 24: Brazier

- [x] Register brazier as a static non-TileEntity decorative block.
- [x] Add blockstate, block model, item model, loot table, recipe, textures, and zh_cn name.
- [x] Convert the source recipe tags to existing 1.12 OreDictionary inputs.
- [x] Defer brazier TileEntity, item handling, ritual catalyst behavior, particles, and renderer.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entry, item/block models, placement, breaking, drop, and recipe.

## Milestone 25: Stone Altar

- [x] Register stone altar as a static non-TileEntity decorative block.
- [x] Add simple four-direction actual-state rendering for adjacent altar connections.
- [x] Add blockstate, block models, item model, loot table, textures, and zh_cn name.
- [x] Defer worktable recipe, altar ritual behavior, and any TileEntity interaction.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entry, item/block models, connected altar rendering, placement, breaking, and drop.

## Milestone 26: Wooden Altar

- [x] Register wooden altar as a static non-TileEntity decorative block.
- [x] Reuse the four-direction actual-state rendering path for adjacent altar connections.
- [x] Add blockstate, block models, item model, loot table, recipe, textures, and zh_cn name.
- [x] Convert the source recipe tags to 1.12 OreDictionary inputs.
- [x] Defer altar ritual behavior and any TileEntity interaction.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entry, item/block models, connected altar rendering, placement, breaking, drop, and recipe.

## Milestone 27: Worktable

- [x] Register worktable as a static non-GUI decorative block.
- [x] Add blockstate, block model, item model, loot table, recipe, textures, and zh_cn name.
- [x] Convert the source recipe tag to a 1.12 OreDictionary input and map purple carpet to vanilla metadata.
- [x] Defer container, GUI, worktable recipe type, recipe execution, and HEI worktable category.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entry, item/block models, placement, breaking, drop, and vanilla crafting recipe.

## Milestone 28: Wooden Brewing Stand

- [x] Register wooden brewing stand as a static non-TileEntity decorative block.
- [x] Add placeholder bottle-state properties, currently always empty.
- [x] Add blockstate, block models, item model, loot table, recipe, textures, and zh_cn name.
- [x] Convert the source recipe tags to 1.12 OreDictionary inputs.
- [x] Defer TileEntity inventory, brewing logic, GUI/container, and HEI category.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entry, item/block models, empty bottle-position rendering, placement, breaking, drop, and recipe.

## Milestone 29: Crucible

- [x] Register crucible as a static non-TileEntity decorative block.
- [x] Add blockstate, block model, item model, loot table, recipe, textures, and zh_cn name.
- [x] Convert the source recipe input to the existing 1.12 OreDictionary pewter ingot key.
- [x] Defer TileEntity inventory, crucible recipe type, boiling/stirring logic, particles, network packets, renderer, and HEI category.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entry, item/block models, placement, breaking, drop, and recipe.

## Milestone 30: Incubator

- [x] Register incubator as a static two-block-high decorative block.
- [x] Add bottom/top half placement, neighbor cleanup, and bottom-only item drop behavior.
- [x] Add blockstate, block models, item model, loot table, textures, and zh_cn name.
- [x] Defer any future incubation behavior until a concrete source behavior target is selected.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entry, item/block models, two-high placement, top/bottom cleanup, breaking, and drop.

## Milestone 31: Cistern

- [x] Register cistern as a static non-TileEntity translucent decorative block.
- [x] Add simple vertical actual-state rendering so stacked cisterns switch between lower, middle, and upper models.
- [x] Add blockstate, block models, item model, loot table, textures, and zh_cn name.
- [x] Defer reagent tank storage, pressure logic, liquid rendering, TileEntity, and renderer.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entry, item/block models, stacked vertical rendering, placement, breaking, and drop.

## Milestone 32: Necrotic Focus

- [x] Register necrotic focus as a static horizontally-facing decorative block.
- [x] Add blockstate, block model, item model, loot table, recipe, textures, and zh_cn name.
- [x] Convert the source recipe tag to a 1.12 OreDictionary input and map stone to vanilla metadata.
- [x] Defer TileEntity, ritual item focus behavior, waterlogging, and renderer.
- [ ] Compile/runtime verification is handled by the user.
- [ ] Runtime verify creative entry, item/block models, facing placement, breaking, drop, and recipe.

## Milestone 33: Soul Enchanter

- [x] Register soul enchanter as a static horizontally-facing decorative block.
- [x] Add blockstate, block model, item model, loot table, textures, and zh_cn name.
- [x] Defer worktable recipe, TileEntity, GUI/container, enchanting logic, book renderer, and HEI category.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified soul enchanter is normal.

## Milestone 34: Research Table

- [x] Register research table as a static horizontally-facing decorative block.
- [x] Add blockstate, block model, item model, loot table, recipe, textures, and zh_cn name.
- [x] Convert the source recipe to a 1.12 shaped recipe using red carpet metadata and the existing `plankWood` OreDictionary key.
- [x] Defer TileEntity, GUI/container, research logic, gui texture wiring, and HEI category.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified research table creative entry, item/block models, facing placement, breaking, drop, and recipe are normal.

## Milestone 35: Research Paper Items

- [x] Register note-taking tools, research notes, and completed research as ordinary items.
- [x] Add item models, textures, zh_cn names, and the note-taking tools shapeless recipe.
- [x] Convert the source recipe tags to existing 1.12 OreDictionary keys.
- [x] Add the three research paper items to the client model registration list after user found purple/black icons.
- [x] Defer research NBT/data behavior and research table integration until the research system is ported.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified research paper item models, Chinese names, and the note-taking tools recipe are normal after model registration fix.

## Milestone 36: Offertory Plates And Soul Candy

- [x] Register offertory plate, gold offertory plate, pewter offertory plate, red soul candy, and grape soul candy as ordinary items.
- [x] Add item models, textures, client model registrations, and zh_cn names.
- [x] Keep the offertory plates as item-only 3D models; no placed block behavior is added.
- [x] Defer candy food/effect behavior, lore text, and any ritual/offering integration.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified offertory plate and soul candy creative entries, item models, and Chinese names are normal.

## Milestone 37: Codex And Static Utility Item Models

- [x] Register codex, archive, scriptorium, cabinet, and wooden podium as ordinary items.
- [x] Add item models, textures, client model registrations, zh_cn names, and the codex shapeless recipe.
- [x] Keep archive, scriptorium, cabinet, and wooden podium as item-only static models for now; no placed block behavior is added.
- [x] Defer codex GUI/content, storage behavior, and any interaction logic.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified codex/static utility creative entries, item models, Chinese names, and the codex recipe are normal.

## Milestone 38: Static Miscellaneous Items

- [x] Register fungus sprouts, warped sprouts, mirecap, glass hand, and unholy symbol as ordinary items.
- [x] Add item models, textures, client model registrations, and zh_cn names.
- [x] Defer fungus/mirecap placement or food behavior, crucible recipes, worktable recipe, and any amulet/ritual integration.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified static miscellaneous item creative entries, item models, and Chinese names are normal.

## Milestone 39: Basic Silver Equipment

- [x] Register silver sword, pickaxe, axe, shovel, hoe, helmet, chestplate, leggings, and boots using vanilla 1.12 item classes.
- [x] Add simple silver tool and armor materials for a baseline usable implementation.
- [x] Add item models, textures, armor texture placeholders, client model registrations, zh_cn names, and vanilla shaped recipes.
- [x] Convert source recipe tags to existing 1.12 OreDictionary keys.
- [x] Replace the copied 1.20 single silver armor entity texture with simplified 1.12 `silver_layer_1` and `silver_layer_2` armor-layer textures after user found worn armor UVs were wrong.
- [x] Defer undead bonus damage, any source-specific combat hooks, and exact armor-layer texture tuning until runtime feedback.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified silver equipment creative entries, item models, recipes, tool/weapon use, armor equipping, and worn armor texture are normal after armor-layer refinement.

## Milestone 40: Basic Special Weapons And Tools

- [x] Register athame, cleaving axe, reversal pick, reaper scythe, deathbringer scythe, and sapping sword using vanilla 1.12 sword/axe/pickaxe-style item classes.
- [x] Add item models, textures, client model registrations, and zh_cn names.
- [x] Defer source worktable recipes because the worktable/reagent system is not ported yet.
- [x] Defer special behavior such as cleaving, inversion mining, sapping, soul reaping, and deathbringer effects.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified special weapon/tool creative entries, item models, Chinese names, and basic weapon/tool use are normal.

## Milestone 41: Basic Trinket Items

- [x] Register basic amulet, sanguine amulet, void amulet, soulbone amulet, basic ring, enervating ring, basic belt, gravity belt, resolute belt, and mind-shielding plate as ordinary items.
- [x] Add item models, textures, client model registrations, zh_cn names, and vanilla recipes for the basic amulet/ring/belt.
- [x] Defer Baubles/Curios-style equip slots, all trinket effects, and worktable recipes for advanced trinkets.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified basic trinket creative entries, item models, Chinese names, and basic trinket recipes are normal.

## Milestone 42: Static Wand And Staff Items

- [x] Register soulfire wand, bonechill wand, and summoning staff as ordinary handheld items.
- [x] Add item models, textures, client model registrations, and zh_cn names.
- [x] Defer worktable recipes, spell casting, summoning, projectiles, cooldowns, particles, and networking.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified wand/staff creative entries, handheld item models, and Chinese names are normal.

## Milestone 43: Static Special Utility Items

- [x] Register alchemist's tongs, archangel's sight, mirror of terminus, prestigious palm, and coalfired engine as ordinary items.
- [x] Add item models, textures, client model registrations, and zh_cn names.
- [x] Keep the coalfired engine as an item-only 3D model for now; no placed block or machine behavior is added.
- [x] Defer all right-click abilities, machine behavior, worktable recipes, and integration logic.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified special utility creative entries, item models, and Chinese names are normal.

## Milestone 44: Basic Robe And Armor Items

- [x] Register warlock hat/cloak/boots, bone paladin helm/chestplate/greaves, raven cloak, top hat, and warded mail as basic 1.12 armor items.
- [x] Map partial sets to vanilla equipment slots: cloaks/mail to chest, greaves to legs, hats/helms to head, boots to feet.
- [x] Add item models, item textures, client model registrations, zh_cn names, and simplified 1.12 armor-layer textures.
- [x] Defer worktable recipes, robe/cloak special effects, custom renderers, and exact source texture conversion.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified robe/armor creative entries, item models, Chinese names, equipping slots, and worn armor textures are normal.

## Milestone 45: Parousia Music Disc

- [x] Register the Parousia sound event and music disc item.
- [x] Add disc model, texture, `sounds.json`, `parousia.ogg`, zh_cn name, and disc description.
- [x] Defer loot/injection or custom acquisition path.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified Parousia disc creative entry, item model, Chinese name, tooltip description, and jukebox playback are normal after `ItemRecord` constructor fix.

## Milestone 46: Static Spawn Egg Items

- [x] Register wraith, zombie brute, necromancer, slimy slug, and raven spawn eggs as ordinary items.
- [x] Add model JSON, client model registrations, generated spawn egg textures, and zh_cn names.
- [x] Replace the vanilla `template_spawn_egg` dependency with generated static egg textures after user found the template rendered purple/black.
- [x] Defer actual entity registration and right-click spawn behavior until the entity system is ported.
- [ ] Compile/runtime verification is handled by the user.
- [x] User verified static spawn egg creative entries, generated egg icons, and Chinese names are normal after replacing the template model.

## Milestone 47: Static Resource Closure Scan

- [x] Compare 1.20 item model names against the 1.12 port; no missing item models remain.
- [x] Compare 1.20 blockstate names against the 1.12 port; no missing blockstates remain.
- [x] Run static JSON parsing across 1.12 Eidolon assets; `JSON OK`.
- [x] Scan `zh_cn.lang` for obvious replacement characters and question-mark corruption; no new matches found.
- [x] Identify remaining recipe gap as mostly deferred system recipes: worktable, crucible, stonecutting, alchemy, world/loot acquisition, and version-specific smelting/blasting conversions.
- [x] Confirm the next major implementation phase should start with worktable/reagent recipe infrastructure before advanced recipes and GUI behavior.

## Milestone 48: Worktable Recipe Data Layer

- [x] Copy the 1.20 worktable recipe JSON files into `assets/eidolon/worktable_recipes`.
- [x] Add `WorktableRecipe` as a 3x3 grid plus four-reagent data object with basic matching helpers.
- [x] Add `WorktableRecipes` loader/parser for `eidolon:worktable` JSON recipes.
- [x] Map 1.20 Forge tag names to the existing 1.12 OreDictionary keys used by the port.
- [x] Add missing vanilla OreDictionary aliases needed by the copied worktable recipes.
- [x] Map version-specific vanilla item ids such as bone meal, skulls, crying obsidian, smooth stone slab, smooth stone, and white wool to 1.12 equivalents.
- [x] Load worktable recipes during common initialization after OreDictionary registration.
- [x] Keep this milestone data-only: no worktable TileEntity, GUI, inventory, crafting execution, or HEI category yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 49: Worktable TileEntity Skeleton

- [x] Register a `worktable` TileEntity during preInit.
- [x] Add a fixed 13-slot Worktable inventory: 9 grid slots plus 4 reagent slots.
- [x] Save and load Worktable inventory contents through NBT.
- [x] Replace the static Worktable block implementation with a TileEntity-backed block while keeping the existing static model/render behavior.
- [x] Add temporary no-GUI validation interaction: right-click with an item inserts one item, sneaking empty-hand right-click removes the last inserted item.
- [x] Drop stored Worktable contents when the block is broken.
- [x] Defer GUI/container, slot layout rendering, recipe execution, output slot, sync packets, and HEI category.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 50: Temporary Worktable Recipe Execution

- [x] Add server-side Worktable recipe lookup across loaded `eidolon:worktable` recipes.
- [x] Match the 9 grid slots and 4 reagent slots against parsed recipe ingredients.
- [x] Add temporary no-GUI crafting interaction: empty-hand non-sneaking right-click crafts one matching recipe.
- [x] Consume one item from each non-empty Worktable input slot when crafting succeeds.
- [x] Give the result to the player inventory, or drop it if the inventory is full.
- [x] Keep this as a validation bridge only; proper GUI/container, output preview, slot-specific insertion, remainder items, particles/sounds, and HEI category are still deferred.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 51: Worktable GUI Validation Pass

- [x] Copy the source Worktable GUI background texture into the 1.12 asset tree.
- [x] Register a Forge `IGuiHandler` for Worktable.
- [x] Add a 1.12 `WorktableContainer` with one output slot, nine grid slots, four reagent slots, player inventory, and hotbar.
- [x] Add a 1.12 `WorktableGui` using the source Worktable background texture.
- [x] Open the Worktable GUI on right-click instead of using the temporary blind insertion interaction.
- [x] Show a live output preview when the visible grid and reagent slots match a loaded Worktable recipe.
- [x] Explicitly sync output slot changes to container listeners so recipe results refresh without reopening the GUI.
- [x] Change the Worktable output slot to compute its displayed stack directly from current inputs after the synced logical result existed but did not render.
- [x] Consume one item from each non-empty Worktable input slot when the output is taken.
- [x] Add `docs/worktable-recipes.md` as a temporary readable recipe validation list.
- [x] Defer HEI category until the HadEnoughItems/JEI API dependency is added to the 1.12 build.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 52: Worktable HEI Category

- [x] Detect that HadEnoughItems 4.28.0 exposes the standard `mezz.jei.api` compatibility API.
- [x] Add the local HEI jar from `run/client/mods` as a compile-only dependency so the port can compile without bundling HEI.
- [x] Add a JEI/HEI plugin entry for Eidolon.
- [x] Add a Worktable recipe category using the copied `jei_worktable.png` background texture.
- [x] Add wrappers for loaded Worktable recipes, including 9 grid ingredients, 4 reagent ingredients, and output stack.
- [x] Register the Worktable block as the recipe catalyst.
- [x] Register the Worktable GUI arrow click area to open the Worktable recipe category.
- [x] Register HEI recipe transfer support for Worktable input slots.
- [x] Align the HEI Worktable category to the source `jei_worktable.png` 190x139 background and source slot coordinates after the first pass rendered offset.
- [x] Filter empty Worktable slots out of the HEI ingredient list while still rendering fixed empty positions, so recipes with empty pattern slots are not dropped from search/indexing.
- [x] Ignore non-empty Worktable ingredients with no resolved matching stacks in the HEI ingredient index to avoid dropping the entire recipe from HEI search.
- [x] Add temporary HEI registration diagnostics, then remove the noisy per-recipe diagnostics after the 20/23 mismatch was fixed.
- [x] Switch HEI registration to pass raw `WorktableRecipe` objects plus a wrapper factory, matching the usual JEI 4 registration path.
- [x] Fix the actual 20/23 root cause: `planter`, `stone_altar`, and `unholy_effigy` failed to load because 1.12 `CraftingHelper.getItemStack` required explicit `data` for `minecraft:dirt` and `minecraft:stone`.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 53: Crucible Recipe Data Layer

- [x] Add a 1.12 `CrucibleRecipe` data model with ordered steps, stir counts, unordered per-step ingredient matching, and output stack.
- [x] Add `CrucibleRecipes` registry/query helpers with id lookup and step-list matching.
- [x] Register the first 18 built-in Crucible recipes that can be represented with current 1.12 items/blocks and OreDictionary aliases.
- [x] Add vanilla OreDictionary aliases needed by Crucible recipes: redstone dust, gold ingot, lapis gem, all plank variants, and mushrooms.
- [x] Load Crucible recipes during common initialization after OreDictionary registration.
- [x] Skip 1.20-only crimson/warped nether recipes for now because their vanilla source ingredients do not exist in 1.12.
- [x] Keep this milestone data-only: no Crucible TileEntity behavior, item throwing, stirring, rendering, GUI, HEI category, or particles/sounds yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 54: Crucible HEI Category

- [x] Copy the source `jei_page_bg.png` and `codex_crucible_page.png` GUI textures into the 1.12 asset tree.
- [x] Add a Crucible HEI wrapper that exposes all Crucible step inputs and outputs to HEI search.
- [x] Add a Crucible HEI category so recipes can be viewed before the in-world Crucible behavior is ported.
- [x] Render Crucible recipes as ordered steps with condensed repeated ingredients for readability.
- [x] Display each step number and stir count text in the recipe wrapper.
- [x] Register the Crucible block as the HEI recipe catalyst.
- [x] Keep this milestone display-only: no recipe transfer, no in-world Crucible behavior, no particles/sounds, and no block interaction changes yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 55: Crucible Minimal Interaction

- [x] Add a `crucible` TileEntity for storing completed recipe steps, current step items, and current stir count.
- [x] Register the Crucible TileEntity during preInit.
- [x] Replace the static Crucible block implementation with a TileEntity-backed block while keeping the existing static model/render behavior.
- [x] Add temporary no-GUI validation interaction: right-click with a non-stirrer item inserts one item into the current Crucible step.
- [x] Add temporary stirring interaction: right-click with the recipe-defined stirrer item increments the current step stir count without consuming the stirrer.
- [x] Add temporary step control: empty-hand right-click commits the current step; sneaking empty-hand right-click clears the Crucible.
- [x] Match committed steps against loaded `CrucibleRecipes`; when a full recipe matches, spawn the result above the Crucible and clear stored contents.
- [x] Save and load completed steps, current step contents, current stir count, and the current/committed stirrer item through NBT.
- [x] Do not drop already inserted Crucible input items when the block is broken.
- [x] Add per-recipe stirrer definitions to Crucible recipes, defaulting to a vanilla stick.
- [x] Display the stirrer item in the Crucible HEI recipe layout for steps that require stirring.
- [x] Keep this milestone intentionally minimal: no liquid rendering, level rendering, item-in-bowl rendering, particles, sounds, failed recipe effects, special stirring item, or GUI.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 56: Crucible Operation Feedback

- [x] Add lightweight sound and particle feedback when an item is inserted into the Crucible.
- [x] Add lightweight sound and particle feedback when a valid stirrer item is used.
- [x] Add success feedback when a committed Crucible recipe produces an output item.
- [x] Add failure feedback when an empty-hand commit does not produce an output.
- [x] Add clear/reset feedback for sneaking empty-hand Crucible interaction.
- [x] Keep this feedback pass visual/audio only: no recipe matching changes, no liquid/item rendering, no failure damage, and no custom sounds yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 57: Crucible Simplified Contents Rendering

- [x] Add a `has_contents` actual-state property to the Crucible block based on its TileEntity contents.
- [x] Notify clients when Crucible contents, stir count, or completed steps change.
- [x] Add TileEntity update packet/tag support so client-side rendering can see the stored Crucible state.
- [x] Convert the Crucible blockstate to multipart rendering.
- [x] Keep the existing Crucible model as the always-rendered base model.
- [x] Add a simple `crucible_contents` overlay model using the existing `crucible_full` texture when the Crucible has any stored contents.
- [x] Keep this pass intentionally simple: no animated liquid, no per-item rendering, no stirrer model, no color blending, and no TESR yet.
- [x] Static JSON parse check passed for `blockstates/crucible.json` and `models/block/crucible_contents.json`.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 58: Crucible Required Fluid State

- [x] Add a required fluid field to Crucible recipes, defaulting to one bucket of water.
- [x] Keep the recipe fluid configurable through the Crucible recipe registration helpers.
- [x] Require a valid fluid to be inserted before Crucible item insertion, stirring, or step submission can proceed.
- [x] Use Forge fluid-container handling for filling the Crucible, so future non-water fluid recipes can use their matching containers.
- [x] Save and load the Crucible's current fluid through TileEntity NBT and sync it to the client.
- [x] Clear the Crucible fluid when the player sneaking-empty-hand clears the Crucible.
- [x] Clear the Crucible fluid when a recipe succeeds and produces its output.
- [x] Clear the Crucible fluid when a submitted step no longer matches any loaded recipe prefix.
- [x] Preserve the Crucible fluid after valid intermediate steps in multi-step recipes.
- [x] Add the required fluid container to the Crucible HEI ingredient list and display it to the right of the output item.
- [x] Keep this pass state/interaction-only: no TESR water plane, floating item rendering, stirred animation, or fluid color blending yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 59: Crucible TESR Water and Floating Inputs

- [x] Add a client-side TileEntitySpecialRenderer for the Crucible.
- [x] Register the Crucible TESR from `ClientProxy.preInit`.
- [x] Expose the Crucible's current fluid and current-step input stacks to client rendering.
- [x] Render a simple semi-transparent fluid surface when the Crucible has fluid.
- [x] Render current-step input item stacks floating above the fluid surface.
- [x] Add slight bobbing and rotation to floating item rendering.
- [x] Return the static Crucible blockstate to the base model only so TESR owns the fluid/item visual layer.
- [x] Keep this first TESR pass simple: no real fluid texture atlas sampling, no stir animation state, and no per-fluid bucket icon rendering beyond HEI's current fallback.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 60: Crucible Step Color Feedback

- [x] Expose the number of completed Crucible steps to the TESR.
- [x] Continue rendering already submitted step inputs so visual ingredients do not disappear after confirming a step.
- [x] Remove the previous floating item count limit so all inserted ingredients can be shown.
- [x] Change the TESR fluid surface color based on completed step count.
- [x] Use a simple water color progression: blue before any step, then pale purple, purple, and dark purple for later steps.
- [x] Keep this pass visual-only: no per-recipe custom colors, no animated transitions, and no stir animation yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 61: Crucible Stir Animation Feedback

- [x] Track the last world time when the Crucible is stirred.
- [x] Save and sync the last stir time through TileEntity NBT/update packets.
- [x] Add a short TESR stir progress window after each stir action.
- [x] Make floating input items orbit and bob faster during the stir window.
- [x] Slightly expand and brighten the fluid surface while stirring.
- [x] Keep this pass visual-only: no recipe behavior changes, no custom stirring model, and no persistent animation state beyond last stir time.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 62: Crucible Completion Feedback

- [x] Give successful Crucible outputs a small upward pop velocity instead of spawning motionless.
- [x] Add a default pickup delay to the spawned output item so the pop is visible.
- [x] Strengthen successful completion feedback with level-up sound plus witch/happy particles.
- [x] Make failed submission feedback distinct with extinguish sound and larger smoke particles.
- [x] Keep this pass feedback-only: no recipe matching changes and no inventory insertion behavior yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 63: Research Table Minimal GUI

- [x] Copy the source `research_table.png` GUI texture into the 1.12 asset tree.
- [x] Add a `research_table` TileEntity with persistent inventory slots.
- [x] Register the Research Table TileEntity during preInit.
- [x] Replace the static Research Table block with a TileEntity-backed horizontal-facing block.
- [x] Open a Research Table GUI on right-click while preserving the existing facing/model behavior.
- [x] Initially align the player inventory and hotbar slots against the source Research Table GUI texture.
- [x] Replace the temporary three-slot validation layout with the source-style fixed slots: Research Notes/Completed Research and Arcane Seal.
- [x] Add player inventory and hotbar slots to the Research Table container.
- [x] Add slot validation for Research Notes/Completed Research and Arcane Seal in both the Slot class and TileEntity.
- [x] Drop stored Research Table contents when the block is broken.
- [x] Keep this pass intentionally minimal: no research tree, no unlock logic, no dynamic task slots, no stamp action, no HEI category, and no TESR/tabletop item rendering yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 64: Research Data Skeleton

- [x] Add a lightweight 1.12 `Research` data object with id, star count, translated name, deterministic task selection, and optional special task support.
- [x] Add a lightweight `ResearchTask` hierarchy covering the currently used source task types: item offerings and XP-level offerings.
- [x] Add a `Researches` registry and initialize the source-visible research entries: `gluttony` and `test_block_research`.
- [x] Register the source-style task pool with four scrivener item tasks and two XP tasks.
- [x] Add Chinese names for the initial research ids.
- [x] Keep this pass data-only: no note NBT generation, no right-side dynamic task slots, no task rendering, no task submission, and no stamp action yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 65: Research Notes Task Preview

- [x] Give creative-tab Research Notes a default research NBT payload targeting `eidolon:gluttony` with `stepsDone=0`.
- [x] Backfill default research NBT when a plain Research Notes stack is inserted into the Research Table notes slot.
- [x] Render the active research name and star progress in the Research Table GUI when a Research Notes stack is present.
- [x] Render the current step's generated task choices on the right side of the Research Table GUI.
- [x] Display item-offering tasks with their required item stacks and XP tasks with the required level count.
- [x] Replace the temporary Research Table task preview with source-style task strip rendering using `research_table.png` atlas regions.
- [x] Add temporary dynamic Research Table task input slots for item-offering tasks, matching the source slot positions inside each task strip.
- [x] Restrict task input slots to their expected item stack and return inserted task items to the player when the GUI closes.
- [x] Keep Research Table task slots stable and refresh their expected inputs when Research Notes or `stepsDone` changes, returning mismatched pending inputs instead of deleting them.
- [x] Add source-style hover tooltips on task icons for item requirements and XP-level requirements.
- [x] Hide Research Table task strips when no active task input slots remain, and force task slots to notify changes when cleared to avoid stale client-side item icons.
- [x] Add a minimal `SimpleNetworkWrapper` packet for Research Table task actions.
- [x] Wire the green task submit buttons to server-side validation for item-offering and XP tasks.
- [x] Consume matching task inputs or XP, increment Research Notes `stepsDone`, and refresh task slots after a successful submit.
- [x] Clear task input slots without dropping already-consumed contents after successful submission, preventing stale dark task-strip overlays.
- [x] Render the source-style stamp button area after all Research Notes stars are completed and an Arcane Seal is present.
- [x] Keep this pass task-submit-only: no progress countdown, no completed research stamping, and no full source animation yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 66: Research Table Completion Flow

- [x] Add a 200-tick Research Table progress step before a submitted task increments `stepsDone`.
- [x] Sync Research Table progress to the GUI and render the source-style progress bar.
- [x] Convert fully completed Research Notes plus an Arcane Seal into a Completed Research item.
- [x] Store the completed research id in item NBT so completed research stacks remain research-specific.
- [x] Limit Completed Research stack size to 1.
- [x] Add Completed Research tooltip text using the translated research name.
- [x] Allow right-clicking Completed Research to learn that research and consume the item only when newly learned.
- [x] Keep already-known Completed Research from being consumed again.
- [x] Add player persisted knowledge storage and login/client sync for known research ids.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 67: Notetaking Tools Research Sources

- [x] Move Notetaking Tools behavior into a dedicated `NotetakingToolsItem` class.
- [x] Register Notetaking Tools entity interaction handling on the Forge event bus.
- [x] Generate Research Notes from right-clicking registered blocks.
- [x] Generate Research Notes from right-clicking registered living entities.
- [x] Generate Research Notes from right-clicking air in registered dimensions.
- [x] Generate Research Notes from right-clicking registered fluids.
- [x] Generate notes whenever the matching interaction succeeds, regardless of whether the player already knows the research or already owns a matching note.
- [x] Keep source matching dependency-gated: a research can only be generated when its prerequisites are already known.
- [x] Current source mappings: Test Stone -> `eidolon:test_block_research`, Pig -> `eidolon:gluttony`, Overworld/Nether/End air -> dimension research, Water/Lava -> fluid research.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 68: Research Dependencies and Initial Chain

- [x] Add prerequisite support to `Research` via `requires(...)`, `getPrerequisites()`, and `isUnlockedFor(...)`.
- [x] Register initial research ids: `gluttony`, `test_block_research`, `overworld`, `nether`, `end`, `water`, and `lava`.
- [x] Add Chinese translations for the initial research names and research-use tooltip text.
- [x] Current dependency chain: `test_block_research` requires `overworld`; `nether` requires `overworld`; `end` requires `nether`; `lava` requires `nether`.
- [x] Keep research unlocking minimal for now: no codex page tree, no icons/categories, no hidden conditions beyond prerequisites.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 69: Codex Known Research List

- [x] Add a minimal Codex GUI opened by right-clicking the Codex item.
- [x] Show the current player's known research entries from persisted knowledge data.
- [x] Sort known research ids deterministically and render translated research names.
- [x] Render a simple star count next to each known research.
- [x] Add Chinese GUI strings for the Codex title, known research count, and empty state.
- [x] Keep this first Codex pass display-only: no chapter navigation, no page content, no icons, no unlock tree, and no recipe pages yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 70: Research Debug Command

- [x] Add `/eidolon_research clear` to clear the executing player's known Eidolon research.
- [x] Add `/eidolon_research grant <research_id>` to grant a known research id to the executing player.
- [x] Add `/eidolon_research grant_all` to grant every currently registered Eidolon research id to the executing player.
- [x] Add `/eidolon_research remove <research_id>` to remove a known research id from the executing player.
- [x] Sync grant/remove/grant_all/clear changes back to the client so the Codex list updates without requiring relog.
- [x] Add tab completion for command actions and registered research ids.
- [x] Keep this command as a validation/debug helper: permission level 2 and self-target only for now.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 71: Codex Research Detail Page

- [x] Make known research entries in the Codex selectable.
- [x] Add a minimal always-visible detail panel for the selected research entry.
- [x] Automatically select the first known research when the Codex opens.
- [x] Highlight the selected research entry in the known research list.
- [x] Show the selected research name, registry id, star count, and prerequisite research list.
- [x] Mark prerequisite names with a different color when the player does not currently know them.
- [x] Add Chinese GUI strings for research id, star count, prerequisites, and empty prerequisite text.
- [x] Keep this pass data-only: no page art, icons, categories, source metadata, scrolling, or recipe pages yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 72: Codex Research Source Details

- [x] Add optional source-description metadata to `Research`.
- [x] Add source descriptions for the current initial research entries.
- [x] Show each selected research's source description in the Codex detail panel.
- [x] Change the Codex detail panel research id display to use the short path id so it fits the available width.
- [x] Change the Codex detail panel star display to use a numeric value instead of repeated star glyphs.
- [x] Add Chinese source-description strings for entity, block, dimension, and fluid research sources.
- [x] Keep this pass descriptive-only: source metadata does not change research generation behavior.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 73: Codex Unknown Research Placeholders

- [x] Change the Codex list to include all registered research entries, not only known research.
- [x] Keep the Codex count as the number of known research entries.
- [x] Render unknown research entries as `???` in the list.
- [x] Hide unknown research id, name, source, prerequisites, and star count from the detail panel.
- [x] Show a minimal unknown detail message when an unknown research entry is selected.
- [x] Add Chinese strings for unknown research list/detail display.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 74: Codex Research State Sorting

- [x] Split Codex research entries into known, available, and locked states.
- [x] Sort the Codex list by state first, then by research id.
- [x] Keep known research entries at the top with their translated names.
- [x] Show available but unknown research entries as `???` with a readable muted color.
- [x] Show locked unknown research entries as darker `???`.
- [x] Show different detail text for available unknown research and prerequisite-locked research.
- [x] Keep unknown entries from revealing id, source, prerequisites, star count, or translated name.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 75: Codex Research List Scrolling

- [x] Add mouse-wheel scrolling to the Codex research list.
- [x] Render only the currently visible window of research entries.
- [x] Keep clicked entries mapped to their scrolled list index.
- [x] Add simple up/down indicators when additional list entries exist outside the visible area.
- [x] Clamp scroll offset so the list cannot scroll past the first or last registered research entry.
- [x] Keep the selected research and right-side details independent from the current scroll position.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 76: Codex Source Background Texture

- [x] Copy the source `codex_bg.png` GUI texture from the 1.20 asset tree into the 1.12 asset tree.
- [x] Change the 1.12 Codex GUI to use the source Codex background texture instead of temporary solid-color rectangles.
- [x] Match the source Codex GUI base size: 312 x 208.
- [x] Draw the source background layers from the 512 x 512 Codex texture atlas.
- [x] Reposition the temporary research list and detail text into the left and right page areas.
- [x] Move the Codex title into the left page content area and draw it with a darker page-text color so it is readable on the source texture.
- [x] Keep current research-list behavior intact: state sorting, unknown placeholders, details, and scrolling.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 77: Codex Minimal Chapter Index

- [x] Add a minimal Codex view state with an index view and a research view.
- [x] Make the Codex open to a chapter index instead of directly opening the research list.
- [x] Add a clickable Research chapter entry on the index page.
- [x] Copy the source `codex_index_page.png` GUI texture from the 1.20 asset tree into the 1.12 asset tree.
- [x] Use the source index-entry row background for the Research chapter entry.
- [x] Render the Research Notes item as the Research chapter icon.
- [x] Remove the research-known count from the index view; keep it only inside the Research chapter view.
- [x] Move the existing research list/detail interface behind the Research chapter entry.
- [x] Add a simple back link from the Research chapter view to the index view.
- [x] Disable research-list mouse-wheel scrolling while the Codex is on the index view.
- [x] Add Chinese strings for the Research chapter entry and description.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 78: Codex Research Chapter Page Styling

- [x] Use the source index-entry row background for each research-list entry.
- [x] Match the source `ListPage` structure for research entries: row background, left item icon, and right text label.
- [x] Render the Research Notes item as the icon for each research-list entry instead of using a separate star/unknown marker column.
- [x] Highlight the selected research entry with a subtle overlay on top of the source row background.
- [x] Increase research-list row height to match the source index-entry spacing.
- [x] Keep research-list click mapping aligned with the new row height and scrolling behavior.
- [x] Rework the research detail panel into a more page-like layout with a centered title and section headings.
- [x] Keep all existing research state behavior intact: known, available unknown, locked unknown, and hidden details for unknown entries.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 79: Codex Research Split-Page Layout

- [x] Keep the Research chapter as a split-page layout: research list on the left page and selected research details on the right page.
- [x] Make clicking a research entry immediately refresh the right-side detail page.
- [x] Remove the experimental research-page arrow navigation because it added an unnecessary extra step.
- [x] Keep the source-style Codex arrows available for future multi-page chapters, but do not draw them for the current Research chapter.
- [x] Keep research-list mouse-wheel scrolling active while the Research chapter is open.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 80: First Real Research Data Expansion

- [x] Confirm the 1.20 `Researches` class only registers `gluttony` and `test_block_research`; additional source content lives in Codex chapters rather than the research-note registry.
- [x] Add first 1.12-specific real system research entries: `codex`, `worktable`, `crucible`, `research_table`, `altar`, and `enchanted_ash`.
- [x] Add prerequisite links for the new entries: Worktable/Research Table after Codex, Crucible after Worktable + Water, Altar after Worktable, Enchanted Ash after Altar.
- [x] Add block research sources for Worktable, Crucible, Research Table, Stone/Wooden Altar, and Enchanted Ash.
- [x] Add Codex intro as an additional Overworld air research source.
- [x] Add Chinese research names and source descriptions for the new entries.
- [x] Adjust Notetaking Tools source selection to prefer unlocked research the player does not yet know, while still allowing repeated note generation after all matching research is known.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 81: Codex Research Directory Paging

- [x] Replace mouse-wheel scrolling in the Codex research directory with page-based navigation.
- [x] Draw source-style left/right page arrows for changing the visible research directory page.
- [x] Keep research entry clicks mapped to the current directory page.
- [x] Keep the selected research and right-side detail panel independent from directory page changes.
- [x] Preserve the split-page Codex flow: left side is the research directory, right side updates immediately with selected research details.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 82: Codex Source Page Backgrounds

- [x] Copy the source `codex_blank_page.png` GUI texture into the 1.12 asset tree.
- [x] Render the Research chapter left directory over the source index-page background.
- [x] Render the selected research detail panel over the source blank-page background.
- [x] Keep this pass visual-only: no research state, note generation, or detail-content behavior changes.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 83: Codex Research Description Text

- [x] Add a derived description translation key for each research entry.
- [x] Render known research description text near the top of the selected Codex detail page.
- [x] Keep unknown available/locked research entries hidden behind the existing `？？？` detail states.
- [x] Add Chinese description text for all currently registered research entries.
- [x] Keep this pass content-only: no new research unlock rules, note sources, or Codex navigation behavior.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 84: Codex Research Detail Paging

- [x] Split known research detail content into two right-page views.
- [x] Put the research title and description text on the first detail page.
- [x] Put star count, source text, and prerequisite research on the second detail page.
- [x] Add small right-page arrows for switching only the selected research detail page.
- [x] Reset the selected research detail page back to the description page when a different research is clicked.
- [x] Keep the left research directory paging independent from the right detail paging.
- [x] Keep unknown available/locked research entries on the existing single `？？？` detail view.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 85: Codex Worktable And Crucible Recipe Directories

- [x] Add separate Codex index entries for Worktable recipes and Crucible recipes.
- [x] Move recipe browsing out of ordinary research detail pages and into dedicated recipe directory views.
- [x] Copy and use the source `codex_worktable_page.png` texture for Worktable recipe display.
- [x] Use the source `codex_crucible_page.png` texture for Crucible recipe display.
- [x] Render the left page as a recipe output directory and the right page as the selected recipe.
- [x] Render Worktable recipe pages with source-style 3x3 inputs, four reagent inputs, and output item.
- [x] Render Crucible recipe pages with source-style ordered step rows, stirrer icons, and output item.
- [x] Use the Worktable and Crucible block items as the Codex index icons for their recipe directories.
- [x] Keep the source Crucible result-area texture under the output item while resetting render state before drawing it.
- [x] Add spacing between Crucible step rows and the result-area texture so the output UI does not overlap the final step.
- [x] Show the required Crucible fluid bucket above the first Codex step row.
- [x] Change the HEI Crucible recipe category to use the source Codex Crucible page background and matching step/result layout.
- [x] Reuse the existing Worktable and Crucible recipe registries rather than duplicating recipe data.
- [x] Keep recipe directory page arrows on the left directory page so they do not overlap the right recipe UI.
- [x] Reset GUI render state after item rendering and draw recipe selection highlights behind item/text content to avoid dark overlay artifacts.
- [x] Keep this pass display-only: no Codex recipe transfer, no HEI behavior changes, and no new crafting logic.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 86: Altar Minimal Offering Interaction

- [x] Add `AltarTileEntity` with one persisted offering stack.
- [x] Register the altar tile entity.
- [x] Convert Stone Altar and Wooden Altar to TileEntity blocks while preserving their existing connected visual states.
- [x] Add minimal right-click interaction: place one held item onto an empty altar.
- [x] Add minimal sneak-empty-hand interaction: remove the current altar offering.
- [x] Drop the stored offering when the altar block is broken.
- [x] Add a simple altar TileEntity renderer so the stored offering is visible on top of the altar.
- [x] Keep this pass as altar infrastructure only: no ritual matching, no reagent network, no particles, and no altar power calculation yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 87: Altar Info And Offering Attribute Scan

- [x] Add a lightweight `AltarEntry` data object for capacity and power contributions.
- [x] Add `AltarEntries` with initial offering mappings for candles, goblets, skulls, hearts, and essence items.
- [x] Add `AltarInfo` to scan horizontally connected same-type altar blocks.
- [x] Count stored offerings across the connected altar group.
- [x] Aggregate source-style per-key capacity and power values using the highest value per key.
- [x] Initialize altar entries during common init.
- [x] Add a temporary empty-hand altar status readout showing altar count, offering count, capacity, and power.
- [x] Localize the temporary altar status readout text in Chinese.
- [x] Add client tooltips for registered altar offering items showing their capacity and power values.
- [x] Keep this pass data-only: no ritual matching, no offering consumption, no particles, and no Codex/HEI altar pages yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 88: Minimal Altar Ritual Pipeline

- [x] Add a lightweight `AltarRitual` object with required capacity, required power, required offering count, and result item.
- [x] Add `AltarRituals` registry and initialize it during common init.
- [x] Register one minimal validation ritual: at least 2 capacity, 2 power, and 2 offerings produce a Lesser Soul Gem.
- [x] Make empty-hand altar activation attempt ritual matching before showing the altar status readout.
- [x] Consume offerings from the connected altar group when the ritual succeeds.
- [x] Spawn the ritual result item above the activated altar.
- [x] Keep this pass minimal: no particles, no sound, no timed casting, no entity sacrifice, no Codex/HEI ritual display, and no data-driven ritual loading yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 89: Codex Altar Ritual Directory

- [x] Add an Altar Rituals entry to the Codex chapter index.
- [x] Reuse the existing split-page recipe-directory flow for altar rituals.
- [x] List altar rituals by their result item on the left page.
- [x] Render the selected altar ritual on the right page with result item, required capacity, required power, and required offering count.
- [x] Add Chinese Codex strings for the altar ritual chapter and requirement labels.
- [x] Keep this pass display-only: no HEI altar category, no recipe transfer, no data-driven ritual loading, and no custom source altar page texture yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 90: Codex Altar Offering Directory

- [x] Add an Altar Offerings entry to the Codex chapter index.
- [x] Expose the registered altar offering table for display without duplicating altar data.
- [x] List registered altar offering items on the left Codex page.
- [x] Render the selected offering on the right page with its item name, provided capacity, and provided power.
- [x] Add Chinese Codex strings for the altar offering chapter and value labels.
- [x] Keep this pass display-only: no altar mechanics changes, no HEI altar category, and no new offering data.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 91: Altar Ritual Required Offerings

- [x] Extend altar ritual data so each ritual can define concrete required offering ingredients.
- [x] Store scanned altar offerings by altar position so rituals can match specific items.
- [x] Match rituals by required capacity, required power, and the full required offering list.
- [x] Consume only the matched required offering items when a ritual succeeds.
- [x] Treat the displayed required offerings as mandatory consumable materials while still allowing them to contribute capacity and power if they have altar values.
- [x] Add several first-pass altar ritual entries using currently ported items.
- [x] Update the Codex altar ritual page to show the required offering item icons.
- [x] Keep this pass focused on item-output rituals: no timed ritual state, particles, sounds, entity summoning, sacrifice logic, or non-consumable focus providers yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 92: HEI Altar Ritual Display

- [x] Copy the source `codex_ritual_page.png` texture from the 1.20 reference resources.
- [x] Add an HEI altar ritual category using the source ritual page background.
- [x] Add an altar ritual wrapper that exposes required offering item inputs and ritual result outputs.
- [x] Render required offerings in a source-style arc around the ritual page.
- [x] Render required capacity and required power on the ritual page.
- [x] Register altar rituals with HEI and add stone/wooden altars as recipe catalysts.
- [x] Update the Codex altar ritual page to use the same source ritual page texture and arc offering layout.
- [x] Keep this pass display-only: no recipe transfer, no automatic altar placement, no ritual symbols/particles, and no entity/sacrifice behavior.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 93: Altar Ritual Behavior Types

- [x] Add altar ritual behavior types for item-result rituals and item-transform rituals.
- [x] Route ritual execution through behavior-type dispatch instead of hardcoding one perform path.
- [x] Keep item-transform rituals functionally equivalent to item-result rituals for now: consume matched offerings and spawn the configured result.
- [x] Mark sword and amulet style rituals as item-transform rituals in the registry.
- [x] Add an optional focus ingredient for item-transform rituals and display it in the center ritual slot.
- [x] Add short Codex/HEI behavior labels so the current ritual behavior is visible.
- [x] Keep this pass structural: no held-item focus logic, no non-consumable focus providers, no entity summoning, no particles, and no timed ritual state yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 94: Item Transform Ritual Execution

- [x] Split altar ritual matching into a structured match result with an optional focus altar position and separate offering altar positions.
- [x] Make item-result rituals consume only their matched offering positions before spawning the result.
- [x] Make item-transform rituals operate on the matched focus position separately from matched offering positions.
- [x] Change item-transform rituals to replace the matched focus altar offering with the result item instead of spawning the result as a drop.
- [x] Keep item-result ritual behavior as an item spawn above the activated altar.
- [x] Keep this pass execution-only: no focus NBT/repair/enchantment transfer, no non-consumable focus providers, no particles, and no timed ritual state yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 95: Altar Ritual Success Feedback

- [x] Play a success sound when an altar ritual completes.
- [x] Spawn item-result ritual particles above the activated altar.
- [x] Spawn item-transform ritual particles above the matched focus altar so the transformed item location is emphasized.
- [x] Use vanilla server-side particle spawning without adding custom network packets.
- [x] Keep this pass success-only: no failure sound, no sustained ritual animation, no ritual symbols, and no custom particle assets yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 96: Altar Ritual JSON Recipes

- [x] Add `assets/eidolon/altar_rituals` as the built-in altar ritual recipe directory.
- [x] Load altar rituals from JSON resources during common initialization.
- [x] Support JSON fields for behavior, capacity, power, result, optional focus, and offering list.
- [x] Move the current five built-in altar rituals into JSON files.
- [x] Keep Codex and HEI reading the same `AltarRituals` registry after JSON loading.
- [x] Keep this pass resource-only: no external datapack reload integration, no GUI recipe editing, and no new ritual behavior types.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 97: Altar Ritual Recipe Expansion

- [x] Add additional JSON altar rituals using items already present in the 1.12 port.
- [x] Add item-result rituals for Wraith Heart, Wicked Weave, and Imbued Bones.
- [x] Add an item-transform ritual for Soulbone Amulet using Basic Amulet as the focus.
- [x] Keep the new rituals within existing behavior types so Codex, HEI, and altar execution pick them up automatically.
- [x] Keep this pass content-only: no entity summoning, no wand charging behavior, no sacrifice logic, and no new ritual behavior types.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 98: Altar Ritual Display Tooltips

- [x] Add HEI slot-role tooltips for ritual result, transform focus, and consumed offerings.
- [x] Add Codex hover tooltips for ritual result, transform focus, and consumed offerings.
- [x] Add Chinese strings for altar ritual slot roles.
- [x] Keep this pass display-only: no ritual matching, execution, JSON loading, or recipe content changes.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 99: Altar Wand Recharging Rituals

- [x] Add a minimal rechargeable wand item path for Soulfire Wand and Bonechill Wand.
- [x] Represent wand charge with item durability for now; recharging restores the wand to full durability.
- [x] Add an `item_charge` altar ritual behavior that recharges the matched focus item instead of spawning or transforming it.
- [x] Make charge ritual focus matching ignore item damage so partially depleted wands can be recharged.
- [x] Add JSON altar rituals for Soulfire Wand and Bonechill Wand recharging.
- [x] Show charge rituals in Codex and HEI using the existing ritual page, including the focus wand and consumed offerings.
- [x] Add Chinese labels for charge behavior, charge focus slot, and wand charge tooltip.
- [x] Keep this pass focused on recharging only: no wand spell casting, projectile entities, cooldowns, custom particles, or failure feedback yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 100: Altar Entity Summon Rituals

- [x] Add an `entity_summon` altar ritual behavior.
- [x] Support an `entity` field in altar ritual JSON, using vanilla entity ids such as `minecraft:zombie`.
- [x] Make summon rituals consume their focus sacrifice and matched offerings, then spawn the configured entity above the activated altar.
- [x] Add first summon ritual JSON entries for Zombie and Skeleton.
- [x] Expand 1.12-compatible summon ritual JSON entries with Husk, Stray, and Wither Skeleton.
- [x] Skip Drowned for now because it is not present in vanilla Minecraft 1.12.
- [x] Use spawn eggs as the Codex/HEI display output for summon rituals.
- [x] Add Chinese behavior and focus-slot labels for summon rituals.
- [x] Keep this pass minimal: no custom summon effects, no custom Eidolon entities, no sacrifice health requirements, and no failure feedback.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 101: Altar Ritual Health Costs

- [x] Add an optional `health` field to altar ritual JSON.
- [x] Include player health in altar ritual matching so health-cost rituals only run when the activating player has enough health.
- [x] Consume the configured health from the activating player when a health-cost ritual succeeds.
- [x] Add health costs to Sapping Sword and Sanguine Amulet rituals.
- [x] Show health requirements in Codex and HEI altar ritual pages.
- [x] Keep this pass minimal and player-focused: no nearby entity sacrifice pool, no ritual-specific damage source, no failure feedback, and no custom health-consume particles.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 102: Altar Absorption Ritual

- [x] Add a minimal `SummoningStaffItem` with persisted absorbed-undead count.
- [x] Add an `absorption` altar ritual behavior.
- [x] Make absorption rituals require a Summoning Staff focus and consume configured offerings while leaving the staff in place.
- [x] Absorb nearby low-health undead entities and store five absorbed-undead charges per absorbed entity on the Summoning Staff.
- [x] Add a JSON altar ritual for Absorption using Death Essence, Tattered Cloth, Bone, and Soul Shards.
- [x] Add Codex/HEI behavior and focus-slot labels for absorption rituals.
- [x] Show `目标血量较高，无法吸收` instead of ritual-complete feedback when absorption has matching materials but no low-health undead target.
- [x] Keep this pass minimal: no Summoning Staff use behavior, no serialized captured-entity list, no custom packets, and no special source particles.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 103: Minimal Summoning Staff Use

- [x] Add right-click behavior to Summoning Staff.
- [x] Consume one absorbed-undead count when the staff is used.
- [x] Spawn a basic Skeleton in front of the player as the first minimal summoned undead.
- [x] Mark staff-summoned undead with `EidolonSummoned` and exclude them from Absorption so the staff cannot self-loop charges.
- [x] Store the summoner UUID on staff-summoned undead.
- [x] Prevent staff-summoned undead from targeting or damaging their summoner.
- [x] Make nearby staff-summoned undead target the entity their summoner attacks.
- [x] Let staff-summoned undead persist until they naturally die instead of despawning on a timer.
- [x] Store absorbed undead charges by entity id and default right-click summoning to the most recently absorbed type.
- [x] Fall back to another stored undead type when the selected type runs out of charges.
- [x] Add sneak-right-click cycling between stored undead types.
- [x] Show each stored undead type and remaining charges in the Summoning Staff tooltip.
- [x] Add a tooltip line explaining the current temporary staff use behavior.
- [x] Keep this pass minimal: no full entity NBT restoration, no enthrall/follow AI beyond owner protection, no cooldown, and no custom summon particles.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 104: Minimal Soulfire Wand Casting

- [x] Add a `SoulfireWandItem` subclass for Soulfire Wand.
- [x] Make Soulfire Wand right-click fire a vanilla small fireball in the player's look direction.
- [x] Consume one durability/charge per cast.
- [x] Prevent casting when the wand has no remaining charge.
- [x] Keep altar recharging compatible with the existing rechargeable wand interface.
- [x] Store wand charge in NBT instead of vanilla item damage so the tooltip shows charge without the vanilla durability line.
- [x] Keep this pass minimal: no custom Soulfire projectile entity, no custom particles, no custom damage type, and no advanced targeting.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 105: Minimal Bonechill Wand Casting

- [x] Add a `BonechillWandItem` subclass for Bonechill Wand.
- [x] Make Bonechill Wand right-click raycast along the player's crosshair.
- [x] Damage the first living target hit and apply a short Slowness effect.
- [x] Consume one NBT charge per successful cast.
- [x] Prevent casting when the wand has no remaining charge.
- [x] Keep altar recharging compatible with the existing rechargeable wand interface.
- [x] Keep this pass minimal: no custom Bonechill projectile entity, no custom particles, no freezing blocks, and no advanced target logic.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 106: Wand Feedback And Charge Notes

- [x] Add status feedback when Soulfire Wand or Bonechill Wand has no charge.
- [x] Add miss feedback when Bonechill Wand does not hit a target.
- [x] Add vanilla casting sounds for Soulfire Wand and Bonechill Wand.
- [x] Add simple snowball particles when Bonechill Wand hits a target.
- [x] Add Codex/HEI charge ritual text explaining that altar rituals restore wand charge.
- [x] Keep this pass feedback-only: no custom sounds, no custom particles, no new projectile entities, and no charge UI widget.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 107: Wand Worktable Recipes

- [x] Confirm Soulfire Wand and Bonechill Wand Worktable recipes are present in the 1.12 `assets/eidolon/worktable_recipes` data set.
- [x] Confirm both recipes match the 1.20 source Worktable recipe definitions.
- [x] Confirm the required Worktable tag mappings exist for arcane gold ingots, pewter ingots, wooden rods, blaze powder, and bone meal.
- [x] Confirm Worktable recipe JSON syntax with a case-sensitive JSON parser; all 23 Worktable recipes parse successfully.
- [x] Keep this pass data-only because Worktable execution, Codex display, and HEI display already read from the shared Worktable recipe registry.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 108: Custom Wand Projectiles

- [x] Add 1.12 custom projectile entities for Soulfire Wand and Bonechill Wand.
- [x] Register the new projectiles through Forge entity registration during preInit.
- [x] Register client projectile renderers; later visual pass changed them to invisible renderers so the projectile is represented by particles instead of item sprites.
- [x] Change Soulfire Wand from vanilla small fireball to the custom Soulfire projectile.
- [x] Change Bonechill Wand from instant raycast targeting to a custom Bonechill projectile.
- [x] Add lightweight flight and impact particles for both projectiles; later visual pass strengthened the particle trails and impact bursts.
- [x] Match source-style first-pass behavior: Soulfire deals magic damage and sets targets on fire; Bonechill deals magic damage and applies Slowness on hit.
- [x] Keep this pass minimal: no custom model projectile renderer, no custom damage source registry, and no source particle system port.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 109: Wand Visual And Sound Polish

- [x] Copy source sound assets for Soulfire Wand and Bonechill Wand casting and impact into the 1.12 asset tree.
- [x] Add `cast_soulfire`, `splash_soulfire`, `cast_bonechill`, and `splash_bonechill` entries to `sounds.json`.
- [x] Register the four new wand `SoundEvent` entries in `ModSounds`.
- [x] Replace borrowed vanilla casting and impact sounds with the source Eidolon wand sounds.
- [x] Replace visible item-sprite projectile rendering with invisible projectile rendering so the flight visuals come from particles.
- [x] Strengthen Soulfire projectile trails with pink/gold spell particles and flame particles.
- [x] Strengthen Bonechill projectile trails with snowball, pale blue spell, and white spell particles.
- [x] Strengthen Soulfire and Bonechill impact bursts with larger differentiated particle clusters.
- [x] Add Chinese subtitles for the four wand sounds.
- [x] Keep this pass visual/audio-only: projectile damage, charge use, cooldowns, and recipe data are unchanged.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 110: Wand Advanced Impact Logic

- [x] Make Soulfire projectiles try to place vanilla fire on the face adjacent to a hit block.
- [x] Remove the first-pass Soulfire splash and Bonechill area-control effects after runtime validation showed they were not noticeable enough to justify the added behavior.
- [x] Keep Soulfire direct-hit magic damage/fire and Bonechill direct-hit magic damage/Slowness as the active advanced impact behavior.
- [x] Skip Codex/HEI text updates for this pass by user request.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 111: Athame Behavior

- [x] Replace the static Athame `ItemSword` registration with a dedicated `AthameItem`.
- [x] Add source-style Looting support: when the attacker holds an Athame, effective looting becomes `looting * 2 + 1`.
- [x] Add source-style bonus damage against Enderman, Endermite, Ender Dragon, and Shulker targets.
- [x] Add right-click plant harvesting for soft plants and leaves, including source-style random plant removal.
- [x] Add herb drops from fern, oxeye daisy, lily pad, and jungle leaves.
- [x] Adjust herb drop chance to 1/3 after a successful Athame plant break by user request.
- [x] Use existing 1.12 herb block item drops for Avennian Sprig, Merammer Root, Oanna Bloom, and Sildrian Seed.
- [x] Add an Athame harvest Codex chapter showing the four herb acquisition methods.
- [x] Add an Athame harvest HEI category showing source block, Athame, output herb, and 1/3 chance.
- [x] Keep this pass focused on Athame behavior and acquisition display only: no new loot tables or broader tool behaviors.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 112: Sapping Sword Behavior

- [x] Replace the static Sapping Sword `ItemSword` registration with a dedicated `SappingSwordItem`.
- [x] Add 2 bonus damage through the 1.12 final damage event so repeat hits are not swallowed by vanilla hurt resistance timing.
- [x] Heal the player by 2 health whenever they attack a living target with Sapping Sword, per user request.
- [x] Keep this pass behavior-only: no custom lifesteal particles, no network effect packet, no new recipe data, and no Codex/HEI text.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 113: Reaper Scythe Behavior

- [x] Replace the static Reaper Scythe `ItemSword` registration with a dedicated `ReaperScytheItem`.
- [x] Port and adjust the source behavior from the 1.20 global drops event: undead monsters killed by Reaper Scythe drop Soul Shards without removing their normal drops.
- [x] Use 1.12 `LivingDropsEvent`, undead creature attributes, and the existing `SOUL_SHARD` item for the drop.
- [x] Adjust Soul Shard drops by user request: always drop at least 1, while keeping Looting as an upper-bound increase.
- [x] Allow Reaper Scythe to harvest any target currently carrying Deathbringer's Undeath effect, not only hostile mobs.
- [x] Add the source-style Reaper Scythe lore tooltip key to `zh_cn.lang`.
- [x] Add Soul Shard acquisition display to Codex: Reaper Scythe plus undead target yields Soul Shards, preserving normal drops.
- [x] Add a HEI Soul Shard harvest category with Reaper Scythe as the catalyst and a one-entry acquisition display.
- [x] Replace placeholder item inputs in Soul Shard acquisition displays with undead spawn eggs so Codex and HEI communicate that the source is undead entities while staying item-based.
- [x] Add Codex index pagination and hover-state page arrows so newly added acquisition chapters do not overflow the first page.
- [x] Keep this pass focused on Reaper Scythe kill drops and acquisition display only: no particles and no custom crystallize network effect.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 114: Deathbringer Scythe Behavior

- [x] Replace the static Deathbringer Scythe `ItemSword` registration with a dedicated `DeathbringerScytheItem`.
- [x] Port the source core behavior in a 1.12-friendly form: hitting a non-undead living target applies the custom Undeath potion for 900 ticks.
- [x] Register the custom Undeath potion and use it as the reapable marker for Deathbringer targets.
- [x] Let Reaper Scythe treat targets with the Undeath potion as reapable so they can produce Soul Shards if killed during the effect duration.
- [x] Give Deathbringer Scythe the same Soul Shard harvest behavior for undead or Undeath-marked targets, with triple the Reaper Scythe shard amount.
- [x] Add a first-pass Deathbringer slash network packet that spawns client-side slash particles around the hit target.
- [x] Add the source-style Deathbringer Scythe lore tooltip key to `zh_cn.lang`.
- [x] Keep this pass focused on the core source behavior and first-pass visuals: no custom potion icon texture, no sweep-attack suppression, and no advanced source particle system yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 115: Cleaving Axe And Reversal Pick Behavior

- [x] Replace the static Cleaving Axe registration with a dedicated `CleavingAxeItem`.
- [x] Port the source Cleaving Axe drop-head event: skeletons, wither skeletons, zombies, creepers, dragons, and players can drop matching skulls when killed by the axe.
- [x] Adjust Cleaving Axe head drops by user request: base 1/2, plus one extra 1/5 roll per Looting level, while avoiding duplicate head drops.
- [x] Replace the static Reversal Pick registration with a dedicated `ReversalPickItem`.
- [x] Port the source Reversal Pick break-speed behavior so harder blocks mine faster and softer blocks resist more.
- [x] Give Reversal Pick a source-style magic tool material with harvest level 3 so obsidian can drop normally.
- [x] Add lore tooltip keys for Cleaving Axe and Reversal Pick to `zh_cn.lang`.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 116: Alchemist's Tongs And Glass Tube Direction

- [x] Replace the static Alchemist's Tongs registration with a dedicated `TongsItem`.
- [x] Port the source interaction target in a 1.12-friendly form: Alchemist's Tongs right-clicks glass tubes to change their output direction.
- [x] Add input and output direction state to glass tubes, initialized from placement face and persisted through a glass tube tile entity.
- [x] Update the glass tube blockstate to render a single output end model so Alchemist's Tongs direction changes have clear visible feedback without duplicate protruding caps.
- [x] Keep this pass direction-only: no reagent pipe tile entity, no transfer network, no cistern/crucible transport behavior yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 117: Codex Source-Style Side Tabs

- [x] Compare the 1.20 Codex layout and confirm categories are rendered as colored side tabs before the book background is drawn.
- [x] Add source-style colored side tabs to the 1.12 Codex using the existing `codex_bg.png` tab region.
- [x] Route tab clicks directly to the existing 1.12 Codex chapter views while keeping current research, recipe, altar, Athame, and Soul Shard pages intact.
- [x] Add hover tooltips for the side tabs and keep the active tab visually extended/brightened.
- [x] Keep this pass layout-focused: no full 1.20 `Category/Chapter/Page` class rewrite, no chant/rune bar, and no spell casting integration yet.
- [ ] Compile/runtime verification is handled by the user.

## Milestone 118: World Generation

- [x] Register a 1.12 `IWorldGenerator` during common pre-init.
- [x] Add lead, deep lead, silver, and deep silver ore generation for new Overworld chunks.
- [x] Port Illwood tree generation in a 1.12-friendly form and wire it to sapling growth/bonemeal.
- [x] Add natural Illwood tree placement in forest/swamp-style Overworld biomes.
- [x] Copy the source lab, stray tower, and catacomb NBT templates into Legacy resources.
- [x] Add a modern structure-template reader that converts 1.20 palette block ids/properties into 1.12 block states.
- [x] Add source-inspired random-spread placement for lab and stray tower surface structures.
- [x] Add a lightweight 1.12 template-pool approximation for catacombs using the copied catacomb room templates.
- [ ] Runtime verification is handled by the user in newly generated chunks.

## Milestone 119: Reagent Network First Pass

- [x] Make reagent tanks capacity-aware so fills return the accepted amount and transfer cannot silently overfill destinations.
- [x] Complete Cistern storage as a column aggregate with local per-block rendering, pressure/status feedback, container fill, and bucket extraction.
- [x] Complete Glass Tube input/output direction persistence, pressure transfer, Alchemist's Tongs output switching, status feedback, and reagent rendering.
- [x] Connect Glass Tube output into Crucible reagent input; 1000 mB Steam converts into a water-filled Crucible and partial steam progress renders/statuses.
- [x] Connect Glass Tube output into Altar reagent input; altar scans include stored offerings and reagent-derived capacity/power bonuses.
- [x] Restore direct altar ritual matching by scanning altar offerings and iterating loaded ritual definitions.
- [x] Add Altar reagent persistence/status feedback and a subtle reagent render layer.
- [x] `gradlew.bat compileJava` passed after this implementation pass.
- [ ] Runtime verification is handled by the user after this implementation pass.
