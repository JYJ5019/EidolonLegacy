# Eidolon Particle Effect Porting Status

Source reference: `../eidolon-1.20x` targets Forge 1.19.2 APIs. This port targets Minecraft 1.12.2 / Cleanroom, so source `ParticleType`, `ParticleOptions`, `RenderType`, and core shader code are behavior references, not direct copy targets.

## 1. 1.12 Particle Runtime

| Source particle behavior | 1.12 implementation | Status |
| --- | --- | --- |
| Generic color/alpha/scale/spin particles | `particle/EidolonParticle.java` | Ported; color now uses HSV interpolation like source `GenericParticle` |
| Source-style particle builder | `EidolonParticles.ParticleBuilder` | Ported for color, alpha, scale, lifetime, spin, gravity, offset, velocity, repeat |
| Wisp | `EidolonParticles.spawnWisp` | Ported |
| Sparkle | `EidolonParticles.spawnSparkle` | Ported |
| Flame | `EidolonParticles.spawnFlame` | Ported |
| Smoke | `EidolonParticles.spawnSmoke`, `EidolonParticles.create(SMOKE)` | Ported: source-style non-linear smoke trait curve and Y damping |
| Steam | `EidolonParticles.spawnSteam`, selected `EidolonParticles.create(SMOKE)` steam call sites | Ported: source-style non-linear steam trait curve and gentler Y damping |
| Bubble | `EidolonParticles.spawnBubble`, `EidolonParticles.create(BUBBLE)` | Ported: source-style Y damping and late-life burst sprite swap |
| Sign | `EidolonParticles.spawnSign` | Ported, verify all sign textures |
| Rune | `EidolonParticles.spawnRune` | Ported: rune textures register from `Runes.getRunes()` so added runes stitch their sprites automatically |
| Line wisp | `EidolonLineWispParticle`, `EidolonParticles.ParticleBuilder#lineTarget` | Ported closer to source: particles interpolate from source to target |
| Slash / glowing slash | `EidolonSlashParticle`, `EidolonParticles.spawnGlowingSlash` | Ported for Deathbringer; reusable for other slash effects |

## 2. Networked Effects

`VisualEffectPacket` intentionally combines many 1.19 effect packets into one 1.12 `SimpleNetworkWrapper` packet.

| Source packet | 1.12 implementation | Status |
| --- | --- | --- |
| `MagicBurstEffectPacket` | `VisualEffectPacket.MAGIC_BURST` | Ported closer to source: wisp, sparkle, and smoke groups now use source-style builder parameters |
| `LifestealEffectPacket` | `VisualEffectPacket.LIFESTEAL` | Ported closer to source: target-bound line wisps plus 1.12 heart accent |
| `RitualConsumePacket` | `VisualEffectPacket.RITUAL_CONSUME` | Ported closer to source: source-to-target line wisps |
| `RitualCompletePacket` | `VisualEffectPacket.RITUAL_COMPLETE` | Ported closer to source: flame and sparkle groups use source parameters |
| `CrucibleSuccessPacket` | `VisualEffectPacket.CRUCIBLE_SUCCESS` | Ported closer to source: steam and sparkle groups use source parameters |
| `CrucibleFailPacket` | `VisualEffectPacket.CRUCIBLE_FAIL` | Ported closer to source: smoke group uses source parameters |
| `FlameEffectPacket` | `VisualEffectPacket.FLAME` | Ported closer to source: flame and sparkle groups use source parameters |
| `IgniteEffectPacket` | `VisualEffectPacket.IGNITE` | Ported closer to source: flame and sparkle groups use source parameters |
| `ExtinguishEffectPacket` | `VisualEffectPacket.EXTINGUISH` | Ported closer to source: smoke group uses source parameters |
| `CrystallizeEffectPacket` | `VisualEffectPacket.CRYSTALLIZE` | Ported closer to source: pink sparkle burst uses source parameters |
| `ChilledEffectPacket` | `VisualEffectPacket.CHILLED` | Ported closer to source: glass break sound plus ice `BLOCK_CRACK` particles with gaussian velocity |
| Projectile impact `MagicBurstEffectPacket` uses | `SOULFIRE_IMPACT`, `BONECHILL_IMPACT`, `NECROMANCER_BURST`, `SUMMON_BURST` | Ported closer to source: all now route through source-style magic burst using explicit primary/secondary colors |
| `DeathbringerSlashEffectPacket` | dedicated packet plus `EidolonSlashParticle` | Ported closer to source: four colors, source-style rolled smoke fan, five layered arc slashes; removed the legacy extra vanilla `SPELL_MOB` arc and target `MAGIC_BURST` overlay |
| Ritual field pulses and completion | `ActiveRituals`, `AltarRitual`, `VisualEffectPacket.MAGIC_BURST`, `VisualEffectPacket.RITUAL_COMPLETE` | Ported closer to source: direct server-side vanilla pulse/success particles removed in favor of networked Eidolon effects |
| Crucible interaction feedback | `ModBlocks.CrucibleBlock`, `VisualEffectPacket` | Ported closer to source: direct `WATER_SPLASH`/`SPELL`/`SPELL_WITCH` interaction particles and residual `MAGIC_BURST` overlays removed; fill, stir, add, continue, and reset interactions are sound/state only, while success/fail route through custom packet effects |
| Dark Touch conversion burst | `DarkTouchSpell`, `SpellHelper.sendMagicBurst`, `VisualEffectPacket.MAGIC_BURST` | Ported closer to source: item conversion emits only the Wicked/Blood magic burst at the item position; extra shared chant success overlays removed |
| Chant prayer/sacrifice effigy flames | `SpellHelper`, `VisualEffectPacket.CHANT_FLAME` | Ported closer to source: prayer uses deity color, animal sacrifice uses Blood, and villager sacrifice uses Soul two-sided effigy flame clusters; extra `MAGIC_BURST`/`RITUAL_COMPLETE` overlays removed |
| Candle and candlestick ambient flames | `ModBlocks.CandleBlock`, `ModBlocks.CandlestickBlock` | Ported: source vanilla smoke/flame ambient particles restored for both normal and magic candles/candlesticks |

## 3. Entity Particle Trails

| Source entity behavior | 1.12 implementation | Status |
| --- | --- | --- |
| `SoulfireProjectileEntity.tick` | `SoulfireProjectileEntity.onUpdate` | Ported: 8 interpolated trail points, sparkle + wisp source parameters, no extra vanilla client trail |
| `BonechillProjectileEntity.tick` | `BonechillProjectileEntity.onUpdate` | Ported: 8 interpolated trail points, two wisp layers with source colors/scales/lifetimes |
| `NecromancerSpellEntity.tick` | `NecromancerSpellEntity.onUpdate` | Ported: delayed phase stays quiet, active phase uses 8 interpolated wisp + smoke trail points |
| `NecromancerEntity.tick` casting hands | `NecromancerEntity.spawnCastingParticles` | Ported: 1.12 AI tracks attack vs summon casting and emits source-style hand sparkle/wisp colors |
| `ChantCasterEntity.tick` rune steps | `ChantCasterEntity` | Ported closer to source: removed extra vanilla `CRIT_MAGIC`/`SPELL_MOB`/smoke support particles; client now emits two source-style rune step particles with start/end average colors when the synced rune index advances |
| `BrazierTileEntity.tick` burning/searching | `BrazierTileEntity` | Ported closer to source: searching wisp ring plus source-style flame/smoke/sparkle builder parameters; removed duplicate vanilla flame/smoke/sparkle |
| `SummoningStaffItem.onUsingTick` | `SummoningStaffItem` | Ported closer to source: charging smoke uses custom smoke particle builder colors/alpha/scale |
| `NotetakingToolsItem.inventoryTick` research highlights | `NotetakingToolsItem.onUpdate` | Ported closer to source: held note-taking tools highlight nearby blocks/entities with non-empty research lists, independent of whether the player has unlocked that research |
| Chilled effect packet | `VisualEffectPacket.CHILLED` | Ported as a reusable 1.12 substitute, but strict source parity removes Bonechill/Wraith send sites because the 1.20x source does not trigger `ChilledEffectPacket` there |

## 4. Runtime Notes

1. Keep source-required 1.12 substitutes documented: `VisualEffectPacket.CHILLED` uses vanilla ice `BLOCK_CRACK` if a future source call site needs it; Athame plant cutting uses vanilla block crack because it represents real block fragments, not an Eidolon custom particle; candles/candlesticks intentionally use vanilla `SMOKE_NORMAL` and `FLAME` because the 1.20x source uses vanilla smoke/flame there.
2. Remaining direct `world.spawnParticle` calls are intentionally limited to source vanilla ambient/substitute paths: candle/candlestick smoke/flame, Athame plant `BLOCK_CRACK`, and the reusable `VisualEffectPacket.CHILLED` helper overloads.
3. Smoke, steam, and bubble now carry their source-specific motion/animation behavior through the shared 1.12 runtime.
4. Fullbright particles are stable but not a perfect shader/additive render-type match for 1.20x. Treat missing additive bloom as a renderer limitation unless a 1.12 additive layer is explicitly scheduled.
5. Visual QA is intentionally left to in-game player testing.

### Fullbright / Shader Approximation Audit

Source references:

- `../eidolon-1.20x/src/main/java/elucent/eidolon/particle/GlowParticleRenderType.java`
- `../eidolon-1.20x/src/main/java/elucent/eidolon/particle/SpriteParticleRenderType.java`
- `../eidolon-1.20x/src/main/java/elucent/eidolon/mixin/LevelRendererMixin.java`

Legacy references:

- `src/main/java/elucent/eidolon/particle/EidolonParticle.java`
- `src/main/java/elucent/eidolon/particle/EidolonParticles.java`
- `src/main/java/elucent/eidolon/particle/EidolonSlashParticle.java`
- `src/main/java/elucent/eidolon/client/render/shader/LegacyShaders.java`
- `src/main/java/elucent/eidolon/network/VisualEffectPacket.java`

| Source render behavior | 1.12 replacement | Approximation status | QA focus |
| --- | --- | --- | --- |
| `GlowParticleRenderType` uses additive blending, `SRC_ALPHA, ONE`, the glowing sprite shader, and no depth writes. | Eidolon particles use vanilla particle layer 1 with `getBrightnessForRender` returning `0xF000F0` when `fullbright` is true. | Accepted approximation. Particles stay visible in darkness, but overlapping particles do not accumulate source-style additive bloom. | Check night/dark-cave visibility and high-density overlap in ritual completion, magic burst, and Deathbringer slash effects. |
| `SpriteParticleRenderType` uses a custom sprite shader with normal alpha blending and no depth writes. | Legacy particles use vanilla textured particle rendering with HSV color interpolation, alpha/scale curves, spin, and source-style motion traits. | Accepted approximation. Color, shape, timing, and motion are the parity targets; shader-specific sprite modulation is not exact. | Compare wisp/sparkle/flame readability in daylight, night, and rainy weather. |
| `LevelRendererMixin` flushes delayed glowing/vapor batches during level render. | No mixin is installed. Legacy does not inject delayed particle render batches into `LevelRenderer`; custom `LegacyShaders` exists for explicit render callers, not as a global particle render type replacement. | Documented limitation. Adding an equivalent render stage would require a larger 1.12 render pipeline change or mixin-like hook. | Treat missing delayed-batch glow/vapor compositing as known unless a dedicated 1.12 render-layer task is scheduled. |
| Source render types disable depth writes while drawing particle quads. | Vanilla 1.12 particle layer behavior is used; this port does not add per-particle depth-mask control. | Accepted approximation. Sorting/occlusion may differ from source in dense scenes or near blocks. | Check particles near walls, altars, crucibles, and crowded ritual setups for unacceptable clipping or overdraw. |

Low-risk conclusion: no code patch is recommended for this pass. The current fullbright substitute is intentionally documented as a readability-preserving approximation, not a shader/additive parity implementation.

### Focused Remaining Acceptance Items

| Item | Source reference | 1.12 reference | Current state | Acceptance check |
| --- | --- | --- | --- | --- |
| Chilled effect retention | `../eidolon-1.20x/src/main/java/elucent/eidolon/network/ChilledEffectPacket.java`: glass sound plus five ice block particles around lines 45-48 | `src/main/java/elucent/eidolon/network/VisualEffectPacket.java`: `CHILLED` effect and ice `BLOCK_CRACK` around lines 176-177 and 294-302 | Packet behavior is ported, but current strict-parity pass removed Bonechill/Wraith sends because those source classes do not trigger `ChilledEffectPacket`. | Keep the packet implementation for future/source call sites. Do not add Bonechill/Wraith application feedback unless accepting an intentional 1.12 UX divergence. |
| SpellCastPacket replay | `../eidolon-1.20x/src/main/java/elucent/eidolon/entity/ChantCasterEntity.java`: server casts and sends `SpellCastPacket` around lines 107-108; `SpellCastPacket` replays `spell.cast` client-side around line 60 | `src/main/java/elucent/eidolon/network/SpellCastPacket.java`, `src/main/java/elucent/eidolon/spell/SpellHelper.java`, `ModNetwork.java`, and `ChantCasterEntity.java` | Safe client-only visual replay is implemented without calling `spell.cast()`: prayer/sacrifice chant flames replay on the client, Dark Touch replays the enchantment sound, and server-side chant flame sends were removed to avoid duplicates. | Cast each chant spell in multiplayer or with a second observing client and verify the caster plus observers see exactly one effigy flame replay/sound event. |
| Smoke/steam motion curve | `../eidolon-1.20x/src/main/java/elucent/eidolon/particle/SmokeParticle.java`, `SteamParticle.java`: custom coefficient and vertical motion damping | `src/main/java/elucent/eidolon/particle/EidolonParticle.java`, `EidolonParticles.java`, `VisualEffectPacket.java`, `CrucibleTileEntity.java` | Ported: `EidolonParticle` now supports the source smoke/steam non-linear trait curve plus Y damping; smoke defaults to 0.98 damping and steam call sites override to 0.99. | Visual QA should compare brazier smoke, crucible steam, Deathbringer smoke, and item-holder consume smoke. |
| Bubble animation/drag | `../eidolon-1.20x/src/main/java/elucent/eidolon/particle/BubbleParticle.java`: age-based sprite set and upward damping | `src/main/java/elucent/eidolon/particle/EidolonParticle.java`, `EidolonParticles.java`, `CrucibleTileEntity.java` | Ported: `create(BUBBLE)` now applies 0.8 Y damping and swaps to the `burst` sprite in the last fifth of lifetime, matching source `bubble_particle.json` ordering. | Visual QA should compare crucible boil bubbles. |
| Fullbright vs shader/additive rendering | `../eidolon-1.20x/src/main/java/elucent/eidolon/particle/GlowParticleRenderType.java`: additive `SRC_ALPHA, ONE` and glowing shader around lines 24-28; `SpriteParticleRenderType.java` uses custom sprite shader around lines 23-27 | `src/main/java/elucent/eidolon/particle/EidolonParticle.java`: fullbright flag and `0xF000F0` lightmap around lines 23-25, 106-107, 174-175; `EidolonSlashParticle.java`: slash fullbright around lines 90-91 | Accepted runtime approximation for now. 1.12 particles use vanilla translucent rendering with fullbright lightmap rather than source shader/additive render types, so glow intensity and overlap blending will differ. | Visual QA should check readability in daylight, night, and high-particle scenes. Treat color/shape/timing mismatches as parity bugs, but treat missing additive bloom as a known renderer limitation unless a legacy additive render path is explicitly scheduled. |

## 5. Verification

Static check command:

```text
.\gradlew.bat compileJava
```

Passed checkpoints:

- Added `EidolonSlashParticle` and upgraded `DeathbringerSlashEffectPacket`.
- Added `EidolonParticles.ParticleBuilder` and retuned `VisualEffectPacket.MAGIC_BURST`.
- Added `EidolonLineWispParticle` and retuned crucible, ritual, flame, ignite, extinguish, crystallize, lifesteal, and ritual consume effects.
- Added HSV color interpolation to `EidolonParticle`.
- Added source-specific smoke/steam non-linear trait curves, Y-axis damping, and bubble burst-sprite behavior to the shared 1.12 particle runtime.
- Retuned `CHILLED`, projectile impact bursts, soulfire/bonechill/necromancer projectile trails, and necromancer casting particles.
- Retuned Brazier search/burn particles, Summoning Staff charge smoke, ChantCaster rune visuals, ItemHolder consume particles, and removed source-mismatched reagent/AngelArrow/RavenCloak extra particles.
- Removed source-mismatched vanilla and `MAGIC_BURST` particle overlays from Deathbringer slashes, ritual pulses/completions, purify feedback, and crucible interactions.
- Added source-style candle/candlestick ambient flame/smoke, note-taking research sparkle hints, and chant prayer/sacrifice effigy flames; strict-parity pass removed extra Bonechill/Wraith chilled packet sends.
- Replaced hard-coded rune particle sprite stitching with registry-driven rune sprite registration.
- Split shared chant success visuals so Dark Touch now only emits the source Wicked/Blood magic burst, while prayer/animal sacrifice/villager sacrifice only emit their source effigy flame clusters.
- Retuned ChantCaster rune visuals to fire source-style two-particle rune steps on synced index changes instead of relying on repeated idle rune particles.
- Retuned Crucible boiling particles to match source: seeded step colors, two bubble particles per tick, 1/8 steam chance per bubble, no extra sparkle layer, and success packets use the pre-commit steam color snapshot.
- Added a 1.12 `SpellCastPacket` registration, ChantCaster success send hook, and safe client-only per-spell visual dispatcher for prayer/sacrifice effigy flames plus Dark Touch enchantment sound replay.
- Retuned Deathbringer slash smoke to use the source local-axis roll math, and restored Notetaking Tools highlight filtering to source-style non-empty research lists.
- `.\gradlew.bat compileJava` passed after the latest particle migration pass.

## 6. 玩家视觉检测清单

代码侧粒子移植已完成；以下清单用于玩家进入游戏后做视觉确认。

| 检测范围 | 需要检查的效果 |
| --- | --- |
| 核心粒子 | 灵火光点、闪光、火焰、烟雾、蒸汽、气泡、符号、符文、线状灵火、斩击/发光斩击 |
| 坩埚 | 待机沸腾气泡、1/8 概率蒸汽、成功时蒸汽/闪光爆发、失败时烟雾爆发 |
| 火盆 | 点燃、常规火焰/烟雾/闪光、寻找仪式时的灵火环、仪式火焰、仪式完成、熄灭 |
| 祭坛仪式：通用消耗 | 线状灵火：石手材料、暗蚀焦点材料或生命祭品被吸入仪式中心/焦点。覆盖全部会消耗材料的祭坛仪式：次级灵魂宝石、死亡精华、灌注之骨、邪纹布、怨灵之心、魂骨护符、魂火魔杖充能、寒骨魔杖充能、净化仪式、结晶仪式、诱引仪式、驱离仪式、欺瞒仪式、昼光仪式、月光仪式、血红护符血契、汲取之剑血契、吸收仪式，以及召唤僵尸、召唤骷髅、召唤尸壳、召唤流浪者、召唤凋灵骷髅、召唤怨灵、召唤守卫者、召唤恼鬼。 |
| 祭坛仪式：完成反馈 | 仪式完成火焰/闪光：次级灵魂宝石、死亡精华、灌注之骨、邪纹布、怨灵之心、魂骨护符、魂火魔杖充能、寒骨魔杖充能、净化仪式、结晶仪式、诱引仪式、驱离仪式、欺瞒仪式、昼光仪式、月光仪式、血红护符血契、汲取之剑血契；昼光仪式和月光仪式到达目标时间时还要检查结束闪光。吸收仪式和各类召唤仪式不走此完成反馈，分别看吸收效果和召唤结晶。 |
| 祭坛仪式：魔法爆发 | 魔法爆发脉冲：诱引仪式、驱离仪式、欺瞒仪式、昼光仪式、月光仪式；吸收仪式额外检查亡灵被吸收时的深色魔法爆发。 |
| 祭坛仪式：结晶/吸收/召唤 | 结晶仪式：附近亡灵结晶爆发并掉落灵魂碎片；吸收仪式：亡灵吸收爆发、亡灵到召唤法杖/暗蚀焦点的吸收线状灵火；召唤僵尸、召唤骷髅、召唤尸壳、召唤流浪者、召唤凋灵骷髅、召唤怨灵、召唤守卫者、召唤恼鬼：召唤完成结晶爆发。 |
| 战斗物品 | 汲取之剑吸血线、收割者镰刀结晶爆发、死亡使者镰刀弧光斩击和烟雾扇 |
| 法杖/投射物 | 魂火魔杖/魂火弹轨迹和命中、寒骨魔杖/寒骨弹轨迹和命中、死灵法术延迟/激活轨迹和命中 |
| 生物 | 死灵法师手部施法粒子、死灵法师召唤完成爆发 |
| 咏唱 | 吟唱施法者符文步进粒子、符号/符文贴图、黑暗祈祷/光明祈祷替身火焰、动物献祭替身火焰、村民献祭替身火焰、黑暗之触转换爆发和音效 |
| 工具/环境 | 召唤法杖蓄力烟雾和召唤爆发、笔记工具研究闪光、石手/暗蚀焦点/祭品盘消耗烟雾和线状灵火、蜡烛/烛台原版烟雾/火焰、仪式匕首植物方块碎屑 |
| 可复用替代效果 | 若未来或源码触发点使用寒冷替代效果，检查玻璃破碎音效和冰方块碎屑 |
| 渲染观感 | 白天、夜晚、雨云天气和高粒子场景下，符号、符文、灵火光点、斩击的自发光/辉光可读性 |
