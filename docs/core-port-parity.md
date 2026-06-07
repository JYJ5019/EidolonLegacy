# Eidolon 核心移植等价性

本文件基于 `eidolon-1.20x` 与 `EidolonLegacy` 的源码侧对照生成。

## 自动化覆盖

- `tools/coverage_matrix.js` 现在会抽取注册名、配方输出、loot 输出、JEI 分类和语言 key。
- `tools/sync_legacy_lang.js` 会从 1.20 语言文件补齐 Legacy 缺失的英文 key，并追加 1.12 专用运行时 key。
- 当前矩阵状态：缺失注册 `0`，资源问题 `0`，配方输出缺口 `0`，loot 输出缺口 `0`，标准化后的 `en_us` 缺口 `0`。

## 工作台（Worktable）

源码侧行为：
- `WorktableContainer` 会先检查 `WorktableRegistry.find(core, extras)`。
- 如果没有匹配的 Eidolon 工作台配方，则回退到原版 `RecipeType.CRAFTING`，只使用 3x3 核心格。
- `WorktableResultSlot` 对 Eidolon 配方会消耗核心格与试剂格；对原版合成回退只消耗 3x3 核心格。
- 合成返还物会回到槽位、背包，或掉落到世界中。

Legacy 当前状态：
- 专用工作台配方已经从 `assets/eidolon/worktable_recipes` 加载。
- 本轮已移植：当没有 Eidolon 工作台配方匹配时，回退到原版 3x3 合成。
- 本轮已移植：Eidolon 配方与原版回退配方的合成返还物处理。

涉及文件：
- `src/main/java/elucent/eidolon/tile/WorktableTileEntity.java`
- `src/main/java/elucent/eidolon/gui/WorktableResultSlot.java`

验证：
- `gradlew compileJava` 通过。

## 坩埚（Crucible）

源码侧行为：
- 记录水量、沸腾状态、搅拌次数、步骤计时器和已完成步骤。
- 有效热源包括火、岩浆、岩浆块和现代版本的营火。
- 掉落物实体会在定时步骤结束后按步骤消耗。
- 成功配方会生成输出并发送成功粒子；失败或无操作步骤会清空坩埚。

Legacy 当前状态：
- Legacy 实现的是 1.12 交互模型，包括右键直接放入物品、搅拌器物品、流体桶装填，以及试剂罐蒸汽输入。
- 配方匹配包含有序步骤、流体匹配和搅拌器校验。
- 视觉反馈通过 `VisualEffectPacket` 与 1.12 原生粒子由代码驱动。
- 本轮已移植：沸腾坩埚现在会阻止附近掉落的 `EntityItem` 堆叠被拾取；物品掉入碗内后会启动源码风格的定时步骤；每个堆叠按单个物品作为材料消耗，并自动推进、完成或判定配方失败。
- 本轮已移植：自动坩埚步骤进度会保存到 tile NBT 中，因此存档重载会保留进行中的计时器，不再丢失已安排的步骤。
- 本轮已移植：空手手动提交步骤时，合法的中间配方前缀不再显示失败视觉效果；失败视觉效果只保留给无操作或无效配方路径。

涉及文件：
- `src/main/java/elucent/eidolon/tile/CrucibleTileEntity.java`
- `src/main/java/elucent/eidolon/registries/ModBlocks.java`

后续需要复核的差异：
- 源码有现代营火热源；Legacy 只映射到 1.12 可用的热源方块。
- Legacy 额外加入了蒸汽和试剂支持，这在 1.20 中没有完全相同的形态。

## 祭坛仪式（Altar Rituals）

源码侧行为：
- 仪式由类实现，包含步骤需求和持续需求。
- 祭品匹配可以包含多物品 focus 检查。
- `HealthRequirement` 会检查附近非亡灵生物和玩家，然后用仪式伤害累计消耗生命值。
- `PurifyRitual` 使用闪烁西瓜片作为中心祭品，配合两个附魔灰、治疗药水和两个灵魂碎片，然后转化附近的僵尸村民和僵尸猪灵。
- `RechargingRitual` 会在仪式范围内查找 `IRitualItemFocus`，给第一个 `IRechargeableWand` 充能，原位替换并结束仪式。
- `SanguineRitual` 是血量消耗仪式，会消耗所需 focus 物品和供品，然后在仪式中心生成结果物品。
- 召唤仪式使用木炭作为中心祭品，生物材料作为多物品 focus，并用灵魂碎片和生物材料作为匹配的步骤需求。
- 活跃场域仪式按世界状态效果 tick：allure、repelling、deceit、daylight、moonlight。allure 和 repelling 会附加临时 `GoToPositionGoal`，并在实体越过源距离阈值后移除。

Legacy 当前状态：
- 仪式通过 `AltarRituals`、`AltarRitual`、`BrazierTileEntity` 和 `ActiveRituals` 由数据与代码共同驱动。
- JSON 祭坛仪式和缺失的内置仪式都会加载。
- provider 流程通过附近物品 provider 与 focus provider 实现，最后由火盆完成。
- 活跃场域仪式持久化到 `WorldSavedData`。
- 本轮已移植：吸收现在会把完整的被吸收实体 NBT charge 存到召唤法杖上，相比之前的实体 id 计数器，更接近 1.20 `AbsorptionRitual`/`SummoningStaffItem` 语义。
- 本轮已移植：`purify` 现在使用源码风格的中心祭品和治疗药水需求，并将闪烁西瓜片按 1.12 映射为 `minecraft:speckled_melon`。
- 本轮已移植：血量消耗仪式现在会扫描仪式范围内“当前有效属性”为非亡灵的普通生物，并把玩家单独加入目标列表，然后通过 `Eidolon.RITUAL_DAMAGE` 累计消耗生命值，不再只要求并抽取激活者的生命值；这对齐 1.20 `HealthRequirement` 的 `!isInvertedHealAndHarm()` 与玩家列表分离逻辑。
- 本轮已移植：源码命名的 sanguine 仪式现在使用专用 `SANGUINE` 行为，消耗 focus 物品并在仪式中心生成结果，相比物品转换更接近 1.20 `SanguineRitual`。
- 本轮已移植：`SANGUINE` 行为的结果物生成高度现在使用源码 `SanguineRitual` 的中心上方 `2.5` 格，不再复用 Legacy 普通 `ITEM_RESULT` 的 `1.1` 格掉落高度；普通物品结果仪式保持原有 1.12 展示位置。
- 本轮已移植：召唤仪式现在将火盆中心祭品与 necrotic focus 物品分离。Legacy JSON 和内置兜底使用木炭作为 `sacrifice`，生物材料作为 `focus`，只把剩余的源码 `ItemRequirement` 条目作为 provider 供品。
- 本轮已移植：`allure` 与 `repelling` 现在会附加并移除 1.12 `GoToPositionGoal`，不再每 200 tick 下发一次性寻路命令。阈值与 1.20 对齐：allure 在 12 格外启动、8 格内清除；repelling 在 80 格内启动、88 格外清除。
- 本轮已移植：`crystal` 现在使用 `entity.getCreatureAttribute() == UNDEAD`，让 Undeath 药水通过 Legacy 属性 hook 影响目标过滤，对齐 1.20 `CrystalRitual` 的 `isInvertedHealAndHarm()` 语义；吸收仪式仍保留 `Eidolon.getTrueCreatureAttribute(...)`，因为 1.20 `AbsorptionRitual` 使用真实亡灵类型并绕过 Undeath。
- 本轮已移植：`crystal` 击杀亡灵时现在使用 `Eidolon.RITUAL_DAMAGE`，不再使用普通 `DamageSource.MAGIC`，对齐 1.20 `Registry.RITUAL_DAMAGE` 的 ritual damage 语义，也让 reaper scythe/ritual 击杀掉落逻辑能正确识别仪式击杀。
- 本轮已移植：`crystal` 目标过滤不再排除玩家。1.20 `CrystalRitual` 会处理所有 `isInvertedHealAndHarm()` 的 `LivingEntity`；Legacy 现在通过有效亡灵属性映射后也会包含受 Undeath 影响的玩家。
- 本轮已移植：daylight/moonlight 推进时间后现在会向当前服务端世界的所有 `EntityPlayerMP` 发送 `SPacketTimeUpdate`，对齐 1.20 每次 `setDayTime` 后发送 `ClientboundSetTimePacket` 的同步行为。
- 本轮已移植：召唤法杖施法现在遵循 1.20 charge/release 交互。右键开始弓式使用，至少蓄力 20 tick 后松开，会在 16 格 raytrace 命中位置召唤选中的已捕获亡灵；过短蓄力或无目标松开不会消耗 charge。
- 本轮已移植：召唤法杖在创造模式释放召唤时不再消耗新 `charges` 或旧 `AbsorbedUndeadTypes`/`AbsorbedUndead` 计数，对齐 1.20 `instabuild ? stack : consumeCharge(...)` 行为。中文 tooltip 也同步改为蓄力释放说明。
- 本轮已确认：`soulfire_recharging` 和 `bonechill_recharging` 已经符合 1.20 `RechargingRitual` 结构：小型灵魂宝石中心祭品、法杖 focus 不变量、两个材料供品、两个红石供品，以及原位法杖替换。
- 兼容说明：旧召唤法杖 `AbsorbedUndead` 和 `AbsorbedUndeadTypes` 计数器仍会显示并按实体类型召唤；新的吸收 charge 使用 `charges` 列表，保留被捕获实体 NBT，并会优先于旧计数器消耗。
- 兼容说明：旧 Legacy `sapping_sword` 与 `sanguine_amulet` JSON 仪式仍保持物品转换；1.20 风格的 `sanguine_sapping_sword` 与 `sanguine_sanguine_amulet` 使用新行为。
- 兼容说明：1.12 没有 drowned 或 phantom，因此 `summon_drowned` 仍映射为 guardian，`summon_phantom` 仍映射为 vex；材料结构仍遵循源码仪式形态，并使用 1.12 时代的替代材料。

涉及文件：
- `src/main/java/elucent/eidolon/Eidolon.java`
- `src/main/java/elucent/eidolon/compat/crafttweaker/AltarTweaker.java`
- `src/main/java/elucent/eidolon/entity/ai/GoToPositionGoal.java`
- `src/main/java/elucent/eidolon/registries/ModBlocks.java`
- `src/main/java/elucent/eidolon/spell/AltarRitual.java`
- `src/main/java/elucent/eidolon/spell/AltarRituals.java`
- `src/main/java/elucent/eidolon/spell/ActiveRituals.java`
- `src/main/java/elucent/eidolon/item/SummoningStaffItem.java`
- `src/main/resources/assets/eidolon/altar_rituals/purify.json`
- `src/main/resources/assets/eidolon/altar_rituals/sanguine_sapping_sword.json`
- `src/main/resources/assets/eidolon/altar_rituals/sanguine_sanguine_amulet.json`
- `src/main/resources/assets/eidolon/altar_rituals/summon_drowned.json`
- `src/main/resources/assets/eidolon/altar_rituals/summon_husk.json`
- `src/main/resources/assets/eidolon/altar_rituals/summon_phantom.json`
- `src/main/resources/assets/eidolon/altar_rituals/summon_skeleton.json`
- `src/main/resources/assets/eidolon/altar_rituals/summon_stray.json`
- `src/main/resources/assets/eidolon/altar_rituals/summon_wither_skeleton.json`
- `src/main/resources/assets/eidolon/altar_rituals/summon_wraith.json`
- `src/main/resources/assets/eidolon/altar_rituals/summon_zombie.json`
- `src/main/resources/assets/eidolon/lang/en_us.lang`
- `src/main/resources/assets/eidolon/lang/zh_cn.lang`

后续需要复核的差异：
- `deceit` 是有意采用的 1.12 近似实现：1.20 会衰减村民 gossip 数据，而 Legacy 会把附近村庄声望向 0 调整，因为 1.12 没有 gossip 存储。
- daylight/moonlight 仍需要游戏内观察时间推进的视觉节奏和结束点，但源码侧的服务端到客户端时间包同步已经补齐。

## 声望、祈祷与吟唱（Reputation, Prayers And Chanting）

源码侧行为：
- `IReputation` 按玩家 UUID 和神明 id 存储 reputation、lock 与 prayer cooldown；`setReputation` 在未锁定时可写入，锁定后只允许负数写穿。
- 普通祈祷会查找最近可用 effigy，检查 prayer cooldown 与 effigy ready 状态，然后按 `1.0 + 0.25 * altarPower` 增加声望。
- 动物献祭要求黑暗声望至少 `3`、goblet 捕获 animal、effigy ready；成功后清空 goblet、解锁 mob lock，并按 `3.0 + 0.5 * altarPower` 增加声望。
- 村民献祭要求黑暗声望至少 `15`、unholy effigy 放在 stone altar 上、goblet 捕获 villager 或 player；成功后解锁 villager lock，并按 `6.0 + altarPower` 增加声望。
- `ChantCasterEntity` 每 5 tick 执行一个 rune，执行前后用 `SignSequence.getAverageColor()` 驱动 rune 粒子的颜色过渡，随后同步新的 sign 序列。

Legacy 当前状态：
- Legacy 使用 `ReputationData`/`ReputationEntry` 的 `WorldSavedData` 映射 1.20 `IReputation`，调用方仍在服务端读取，不需要客户端 reputation sync 包。
- 本轮已移植：`ReputationData.setReputation` 的锁定写入规则已改为源码语义；锁定状态下只有负数 amount 可以覆盖写入，不再允许“比当前值更低”的任意正数写入。
- 本轮已确认：普通祈祷、动物献祭与村民献祭的声望门槛、goblet 条件、effigy/altar 检查、unlock 和声望增量已从代码侧对齐源码。
- 本轮已移植：`SignSequence` 补上源码式平均色计算；`ChantCasterEntity` 的空闲 wisp、rune 粒子和 1.12 `SPELL_MOB` 步进粒子现在使用当前 sign 序列平均色，不再固定使用最后一个 sign 或旧紫色。
- 本轮已保留：ChantCaster 的符号环渲染仍按每个 sign 自身颜色绘制，这与 1.20 renderer 的主要符号/环颜色来源一致；1.12 端只用本地自定义粒子近似 1.20 `RuneParticleData` 的前后颜色过渡。
- 本轮已移植：正常吟唱施法不再消耗 Legacy 专有 soul magic。1.20 源码的 `ISoul.magic` 字段当前只有 HUD/能力存储路径，没有 `ChantCasterEntity` 或 spell cast 扣费调用；因此 Legacy 移除了 `Spell.getMagicCost`、`Chanting.castSigns` 扣费、每秒自动恢复，以及 Codex/JEI 的“灵魂魔力消耗”显示。
- 本轮已保留：`SoulData` 仍保留 `magic/maxMagic` 字段和命令调试入口，因为 1.20 `ISoul` 也保留 magic 字段；默认值收窄为 `0/0`，避免它参与正常施法玩法。
- 本轮已移植：Codex 的符文页现在可以把已知 rune 加入最多 18 个条目的咏唱队列，并在底部显示源码风格的咏唱条、执行按钮和清空按钮。
- 本轮已移植：点击执行会通过 `AttemptCastPacket` 把 rune 注册名列表发到服务端，随后关闭 Codex；服务端会重新解码 rune、校验非创造玩家是否已知对应 rune，并在玩家视线前方生成 delayed `ChantCasterEntity`。
- 本轮已加固：`AttemptCastPacket` 在客户端构造、序列化和服务端反序列化时都会限制最多 18 个 rune；反序列化仍会读完整包内容，只丢弃超过上限的条目，避免异常包留下未消费字节。

涉及文件：
- `src/main/java/elucent/eidolon/capability/ReputationData.java`
- `src/main/java/elucent/eidolon/capability/SoulData.java`
- `src/main/java/elucent/eidolon/research/KnowledgeEvents.java`
- `src/main/java/elucent/eidolon/spell/Chanting.java`
- `src/main/java/elucent/eidolon/spell/Spell.java`
- `src/main/java/elucent/eidolon/spell/PrayerSpell.java`
- `src/main/java/elucent/eidolon/spell/AnimalSacrificeSpell.java`
- `src/main/java/elucent/eidolon/spell/VillagerSacrificeSpell.java`
- `src/main/java/elucent/eidolon/spell/SignSequence.java`
- `src/main/java/elucent/eidolon/entity/ChantCasterEntity.java`
- `src/main/java/elucent/eidolon/particle/EidolonParticles.java`
- `src/main/java/elucent/eidolon/network/AttemptCastPacket.java`
- `src/main/java/elucent/eidolon/network/ModNetwork.java`
- `src/main/java/elucent/eidolon/compat/jei/ChantRecipeWrapper.java`
- `src/main/java/elucent/eidolon/gui/CodexGui.java`
- `src/main/resources/assets/eidolon/lang/en_us.lang`
- `src/main/resources/assets/eidolon/lang/zh_cn.lang`

后续需要复核的差异：
- 游戏内验证需要覆盖 prayer cooldown、effigy ready、goblet 捕获类型、unlock fact 授予、altar power 加成，以及重进服务器后 reputation/prayer time 的持久化。
- Codex 咏唱入口需要在客户端实机验证：点击已知 rune 入队、未知 rune 不入队、队列满 18 个后不再追加、清空按钮清队列、执行按钮发包生成 `ChantCasterEntity`，以及 Codex 关闭后实体位置和玩家视线一致。
- ChantCaster 需要在客户端实机观察 rune 粒子颜色、失败音效、成功施法、不扣 soul magic、实体死亡淡出，以及跨区块保存/加载后的 runes/signs/look 同步。

## 灵魂附魔台（Soul Enchanter）

源码侧行为：
- 两槽容器：可附魔物品或书，以及灵魂碎片。
- 根据玩家附魔种子生成三个确定性 offer。
- offer 会添加一级附魔，或把已有附魔提升一级。
- 会过滤 treasure、curse、不兼容和已满级附魔。
- 消耗一个灵魂碎片和经验等级。

Legacy 当前状态：
- 相同的两个逻辑槽保存在 `SoulEnchanterTileEntity` 中。
- offer 生成对齐源码的 seed/tier 逻辑，并过滤 curse、treasure、不兼容和已满级附魔。
- 已实现书到附魔书转换、碎片消耗、经验消耗和音效。
- 本轮已移植：Soul Enchanter 的两个输入槽现在由 `SoulEnchanterContainer` 持有的临时 `InventoryBasic` 提供，关闭 GUI 时会通过 1.12 `clearContainer` 返还玩家或掉落，匹配 1.20 `SimpleContainer` + `removed()` 的会话库存语义。
- 兼容说明：旧存档或旧版本中残留在 `SoulEnchanterTileEntity` 内的输入物品，会在玩家首次打开 GUI 时迁移到临时容器槽位，然后从 tile 中移除，避免旧物品被困在方块实体里。
- 本轮已移植：GUI hover 提示现在会显示实际 offer 附魔名、灵魂碎片需求和等级需求，接近 1.20 `SoulEnchanterScreen` 的 offer tooltip 信息结构；同时补齐中文 `container.eidolon.enchant.shard.*` 语言 key。

后续需要复核的差异：
- 1.12 缺少 1.20 相同的 glyph/random-name 渲染路径，因此按钮上的随机文字风格仍是 1.12 近似显示；实际 offer 信息已经通过 hover tooltip 对齐。

## 法杖（Wands）

源码侧行为：
- `soulfire_wand` 和 `bonechill_wand` 是单堆叠物品，耐久为 `253`，`setNoRepair`，附魔值 `20`。
- `WandItem.recharge` 会清除物品 damage，把法杖恢复到满 charge。
- `WandItem.canApplyAtEnchantingTable` 明确允许 Unbreaking 和 Mending。
- `SoulfireWandItem` 与 `BonechillWandItem` 会从源码指定位置生成弹射物，朝玩家视线目标瞄准，施加 10 tick 冷却，挥动手臂，并在发射后调用 `hurtAndBreak(1)`。

Legacy 当前状态：
- 本轮已移植：法杖现在使用物品 damage 作为 charge 来源，因此耐久条、Unbreaking 掷骰、破坏行为和祭坛充能语义都比之前的自定义 NBT 计数器更接近 1.20 物品模型。
- 本轮已移植：旧 `EidolonCharge` NBT 会在首次读取时迁移为物品 damage，保留旧存档 charge 数值并移除旧标签。
- 本轮已移植：充能仪式仍调用 `IRechargeableWand.recharge`，现在它会像 1.20 `WandItem.recharge` 一样清除物品 damage。
- 本轮已移植：两个法杖弹射物现在使用 1.20 生成位置和视线速度公式，并映射到 1.12 `EntityThrowable.shoot`，耐久消耗通过原版 `damageItem` 完成。
- 兼容说明：Legacy 保留零 charge 状态消息和 tooltip charge 文本，作为 1.12 易用性辅助；源码依赖原版受损物品行为，而不是专门的“无 charge”消息。

涉及文件：
- `src/main/java/elucent/eidolon/item/WandItem.java`
- `src/main/java/elucent/eidolon/item/SoulfireWandItem.java`
- `src/main/java/elucent/eidolon/item/BonechillWandItem.java`

后续需要复核的差异：
- 游戏内验证需要确认两个法杖的弹射物起点与瞄准手感、Unbreaking 耐久掷骰、0 charge 时的破坏处理、祭坛充能，以及旧 `EidolonCharge` 迁移。

## 特殊工具（Special Tools）

源码侧行为：
- `AthameItem` 使用 `PewterTier`，攻击伤害加成 `1`，攻击速度 `-1.6`；它会把 looting 翻倍再加一，对 Endermen、Endermites、Ender Dragon、Shulkers 增加伤害，并能采集柔软植物。
- `ReaperScytheItem` 使用 `PewterTier`，攻击伤害加成 `5`，攻击速度 `-2.9`。
- `CleavingAxeItem` 使用 `PewterTier`，攻击伤害加成 `7`，攻击速度 `-3.2`。
- `SappingSwordItem` 使用 `SanguineTier`，攻击伤害加成 `1`，攻击速度 `-2.4`。
- `DeathbringerScytheItem` 使用源码的 `Tiers.NecroticTier.INSTANCE` 字段；该字段实际初始化为 `MagicToolTier`，攻击伤害加成 `7`，攻击速度 `-2.9`；它还会在 1.20 中禁用 sword sweeping。
- Pewter 工具用 pewter ingot 修复，silver 工具用 silver ingot 修复，magic/sanguine tier 没有修复材料。

Legacy 当前状态：
- 本轮已移植：加入与 1.20 等价的 Pewter 和 Sanguine 工具材料，并让 Athame、Cleaving Axe、Reaper Scythe、Deathbringer Scythe、Sapping Sword 不再使用旧的 silver-tool 兜底。
- 本轮已移植：加入 `EidolonSwordItem`，让源码构造器的攻击伤害和攻击速度数值能在 1.12 上精确应用；否则 `ItemSword` 会硬编码剑类默认值。
- 本轮已移植：Cleaving Axe 现在使用源码攻击/速度组合 `9.0/-3.2`，并映射到 1.12 最终 `ItemAxe` 属性构造器。
- 本轮已移植：pewter 和 silver 工具修复物品通过 1.12 `ToolMaterial#setRepairItem` API 注册；magic 和 sanguine 工具仍不通过材料修复，匹配源码中的空修复材料。
- 本轮已移植：Deathbringer 现在通过定向 `EntityPlayer` mixin 禁用原版 1.12 剑横扫，匹配 1.20 `canPerformAction(... SWORD_SWEEP) == false` hook，同时不改变普通命中、Undeath、耐久或视觉效果流程。
- 本轮已确认：Warded Mail 已经通过 Legacy 胸甲槽物品映射 1.20 的 magic-damage 重分类行为；触发时会取消原魔法攻击，并用原始 damage type 构造一个非 magic 的 `DamageSource` 重新造成同等伤害，而不是固定改成 `generic`。Mind Shielding Plate 已经通过 Baubles 事件覆盖 nausea 阻止以及死亡经验保留/恢复。
- 本轮已移植：Athame 柔软植物采集现在要求方块硬度 `0 <= hardness < 5`，剪切粒子使用点击位置附近的源码式参数；植物破坏仍为 `1/5` 概率，特殊产物改为破坏后再 `1/10` 掷骰，并且源码中这次掷骰命中时即使没有特殊产物也会损耗耐久，Legacy 现在同步该行为。

涉及文件：
- `src/main/java/elucent/eidolon/item/EidolonSwordItem.java`
- `src/main/java/elucent/eidolon/item/AthameItem.java`
- `src/main/java/elucent/eidolon/item/CleavingAxeItem.java`
- `src/main/java/elucent/eidolon/item/DeathbringerScytheItem.java`
- `src/main/java/elucent/eidolon/mixin/EntityPlayerMixin.java`
- `src/main/java/elucent/eidolon/item/ReaperScytheItem.java`
- `src/main/java/elucent/eidolon/item/SappingSwordItem.java`
- `src/main/java/elucent/eidolon/registries/ModItems.java`
- `src/main/resources/eidolon.default.mixin.json`

后续需要复核的差异：
- 游戏内验证需要确认 Deathbringer 不再触发范围横扫攻击，同时保留直接命中的 Undeath 施加、slash packet 和普通物品 damage。

## 银制装备（Silver Equipment）

源码侧行为：
- `SilverTier` 耐久 `193`，挖掘速度 `7.0`，攻击伤害加成 `2`，采掘等级 `2`，附魔值 `20`，用 silver ingot 修复。
- 源码 silver 工具使用显式构造值：剑 `+3/-2.4`，镐 `+1/-2.4`，斧 `+6/-2.4`，铲 `+1.5/-2.4`，锄 `+0/-2.4`；适用时再叠加 tier 攻击加成。
- `SilverArmorItem.Material` 使用耐久倍率 `17`，头/胸/腿/脚防御值 `2/6/4/2`，附魔值 `20`，gold 装备音效，无 toughness，并用 silver ingot 修复。

Legacy 当前状态：
- 本轮已移植：`SILVER_TOOL` 现在匹配源码 `SilverTier` 的耐久、挖掘速度、攻击加成、附魔值、采掘等级和 silver-ingot 修复。
- 本轮已移植：silver pickaxe、axe、shovel、hoe 现在使用源码攻击伤害和攻击速度属性，不再使用 1.12 原版默认值（`-2.8`、`-3.0` 或材料派生的 hoe 速度）。
- 本轮已移植：`SILVER_ARMOR` 现在使用源码耐久倍率、防御数组、附魔值、gold 装备音效和 silver-ingot 修复。

涉及文件：
- `src/main/java/elucent/eidolon/registries/ModItems.java`

后续需要复核的差异：
- 游戏内验证需要检查装备 tooltip 的攻击速度/伤害、用 silver ingot 铁砧修复、护甲耐久和装备音效。

## 护甲材料（Armor Materials）

源码侧行为：
- `WarlockRobesItem.Material` 使用耐久倍率 `21`，头/胸/脚防御值 `3/7/2`，附魔值 `25`，leather 装备音效，无 toughness，并用 wicked weave 修复。
- `BonelordArmorItem.Material` 使用耐久倍率 `38`，头/胸/腿防御值 `4/9/7`，附魔值 `25`，toughness `2`，turtle 装备音效，并用 imbued bone 修复。
- `TopHatItem.Material` 使用耐久倍率 `7`，一点护甲值，附魔值 `12`，leather 装备音效，并用 black wool 修复。
- 1.20 `RavenCloakItem` 与 `WardedMailItem` 是 Curio 承载的普通物品，而不是护甲材料。

Legacy 当前状态：
- 本轮已移植：Warlock robe 护甲材料现在匹配源码耐久、防御分布、附魔值和 wicked-weave 修复。
- 本轮已移植：Bonelord armor 现在匹配源码耐久、防御分布、附魔值、toughness 和 imbued-bones 修复。
- 本轮已移植：Top Hat 现在匹配源码耐久、附魔值和 black-wool 修复。
- 本轮已移植：Raven Cloak 的 flap、dash 和 glide 不再损耗物品。源码 `IWingsItem`/`IPlayerData` 会更新 wing charge 与 dash tick，但不会为 wing movement 消耗物品耐久。
- 本轮已移植：Raven Cloak 控制改为源码式跳跃键蓄力/释放。空中短按释放触发 flap，蓄满约 20 tick 后释放触发 dash；独立 R/V 快捷键入口已移除，不再保留源码没有的控制路径。
- 本轮已移植：Raven Cloak 的 wing charge、dash tick 和潜行慢落现在按 1.20 `IPlayerData` 语义映射到物品 NBT。charge 默认从 `0` 开始，落地回满 `12`，flap 和 dash 各消耗一格；dash 持续 `100` tick，dash 中 flap 会把 dash tick 重置为满值；骑乘、游泳或创造飞行时不能 flap/dash。进入 flying 状态后，Legacy 还会在下落 tick 中近似源码 `WINGS_SLOWFALL` 的 `ENTITY_GRAVITY -0.60` 临时属性；潜行下落触发 flying 时仍先把下落速度截到 `-0.10`。
- 本轮已移植：Raven Cloak 移除了 Legacy 额外的 dash 冷却、水/岩浆中自动回充、常驻 fall event 坠落减伤，以及 flap/dash 时的额外蝙蝠/龙翼音效；这些在 1.20 `RavenCloakItem`/`PlayerDataImpl` 中没有对应行为。
- 本轮已移植：Raven Cloak 和 Warded Mail 现在共享一个 Legacy `EidolonCurioArmorItem` 适配器。它们仍可占用 1.12 胸甲装备槽，以便渲染、控制和事件查询，但不再提供原版护甲/toughness 属性、物品耐久、铁砧修复或护甲式附魔值，从而更接近 1.20 Curio 承载普通物品的形态。
- 本轮已移植：Bonelord 的灵体生命恢复只保留源码式事件节奏：玩家满血且每 `80` tick 恢复 1 点。`ArmorEvents` 现在只维护 1.12 需要的 Bonelord 最大 ethereal-health 装备加成，不再额外每 `40` tick 恢复，避免比 1.20 快一倍。
- 兼容说明：Bonelord 使用最接近的 1.12 装备音效，因为 1.12 没有 turtle-armor 装备音效。
- 兼容说明：Raven Cloak 和 Warded Mail 仍保留为 1.12 装备模型下的 Legacy 胸甲槽映射；它们的行为等价性通过 cloak 控制和 Warded Mail 事件处理追踪，而不是通过源码护甲材料追踪。

涉及文件：
- `src/main/java/elucent/eidolon/item/EidolonCurioArmorItem.java`
- `src/main/java/elucent/eidolon/item/ArmorEvents.java`
- `src/main/java/elucent/eidolon/client/RavenCloakControls.java`
- `src/main/java/elucent/eidolon/item/RavenCloakItem.java`
- `src/main/java/elucent/eidolon/registries/ModItems.java`
- `src/main/java/elucent/eidolon/item/WardedMailItem.java`

后续需要复核的差异：
- 游戏内验证需要检查护甲 tooltip、护甲耐久、铁砧修复材料、Bonelord toughness 和装备音效。
- 游戏内验证需要确认 Raven Cloak 和 Warded Mail 仍能装备到胸甲槽、仍能渲染、仍能触发跳跃蓄力飞行/magic-damage 行为，并且不再显示护甲点或因普通伤害损耗耐久。

## 研究台（Research Table）

源码侧行为：
- 两个基础槽：research notes 和 arcane seal。
- 动态任务槽由 research、step count 和基于世界种子的值生成。
- 提交任务会启动 200 tick 进度倒计时。
- 给已完成笔记盖章会消耗一个 arcane seal，并输出已完成 research。

Legacy 当前状态：
- 已实现两个基础槽和固定任务槽。
- 提交任务会消耗任务输入，启动 200 tick 倒计时，并增加 `stepsDone`。
- 给已完成笔记盖章会消耗一个 arcane seal，并输出已完成 research。
- 本轮已移植：任务生成现在使用 1.20 的世界种子派生值。
- 本轮已移植：seed 会保存在 tile 中，并通过两个 16-bit container field 同步，让 1.12 网络下的客户端任务显示与服务端任务校验保持一致。
- 兼容说明：新创建的 research notes 会打上源码风格世界种子任务标签；没有该标签的旧 notes 会保留之前固定的 Legacy seed，避免玩家进行中的任务发生变化。

涉及文件：
- `src/main/java/elucent/eidolon/tile/ResearchTableTileEntity.java`
- `src/main/java/elucent/eidolon/gui/ResearchTableContainer.java`
- `src/main/java/elucent/eidolon/gui/ResearchTableGui.java`
- `src/main/java/elucent/eidolon/research/Researches.java`

验证：
- `gradlew compileJava` 通过。

## 能力与同步层（Ability And Sync Layer）

源码侧行为：
- 1.20 会给 living entities 附加 `ISoul`，给玩家附加 `IKnowledge` 与 `IPlayerData`，给世界附加 `IReputation`。
- `PlayerEvent.Clone` 会从原玩家向新玩家序列化/反序列化 knowledge、soul 和 player-data capability。
- clone 之后，服务端会向玩家发送完整 knowledge 与 soul 更新包。
- Knowledge 是 set-based：signs、runes、facts 和 research 会序列化为 id 列表。
- Soul data 保存 `maxMagic`、`magic`、`maxEtherealHealth` 和 `etherealHealth`。

Legacy 当前状态：
- Soul 语义映射到 `eidolonSoul` 下的持久玩家 NBT，包含相同的四个标量字段和相同的 clamp/heal/hurt 行为。
- Knowledge 语义映射到持久玩家 NBT 根节点，用于 research、signs、facts 和 runes。
- Reputation 语义映射到 `ReputationData` `WorldSavedData`，按玩家 UUID 与 deity id 索引，并包含 prayer cooldown 和 lock。
- 本轮已移植：login、dimension change、respawn 和 clone 现在共用一条服务端玩家同步路径，确保 soul 默认值存在，发送完整 knowledge sync，并发送 `SoulSyncPacket`。
- 本轮已移植：完整 knowledge sync 现在以 `KnowledgeResetPacket` 开头，因此客户端会先清除旧的 research/sign/rune/fact cache，再接收服务端真实集合。
- 1.12 有意映射：`IPlayerData` wing/dash 状态由现有 Raven cloak 物品与控制路径表示，而不是单独的玩家 capability。

涉及文件：
- `src/main/java/elucent/eidolon/event/GameplayEvents.java`
- `src/main/java/elucent/eidolon/network/KnowledgeResetPacket.java`
- `src/main/java/elucent/eidolon/network/ModNetwork.java`
- `src/main/java/elucent/eidolon/proxy/ClientProxy.java`
- `src/main/java/elucent/eidolon/proxy/IProxy.java`
- `src/main/java/elucent/eidolon/util/KnowledgeUtil.java`

后续需要复核的差异：
- 游戏内验证需要覆盖 login、changing dimension、death/respawn，以及在授予/移除 knowledge、改变 soul 数值后 reconnect。
- Reputation 没有客户端同步包，因为 Legacy 调用方使用服务端 `WorldSavedData`；只有当未来客户端 UI 开始直接读取 reputation 时，才需要重新评估。

## 药水与木制酿造台（Potions And Wooden Brewing Stand）

源码侧行为：
- 1.20 注册 Undeath、Vulnerable、Reinforced、Anchored、Chilled 五个效果，以及 decay/wither 系列 potion type。
- `Potions.addBrewingRecipes` 通过 `PotionBrewingMixin.callAddMix` 注册水到 awkward、awkward 到 Eidolon 药水、redstone 延长、glowstone 强化等配方。
- `WoodenStandTileEntity` 使用四槽容器，前三槽为药水输入，第四槽为材料输入；只在下方坩埚 boiling 时以 `800` tick 酿造。
- 木制酿造台运行时只锁定当前 ingredient 的 `Item`，热源状态每 20 tick 从下方坩埚重新采样，NBT 只保存 `BrewTime` 和物品栏。
- 自动化规则为：上方只插 ingredient，侧面只插前三个药水槽，下方可抽前三槽和 ingredient 槽；ingredient 槽只允许抽出 glass bottle。

Legacy 当前状态：
- 药水效果、potion type 时长、强化等级和 redstone/glowstone 链路已映射到 1.12 `Potion`/`PotionType`/`PotionHelper`。
- 1.20 的 nautilus shell 被映射为 1.12 可用的 `prismarine_crystals`，其余 Eidolon 输入沿用已移植物品。
- 木制酿造台使用 1.12 `MachineInventoryTileEntity` + Forge `BrewingRecipeRegistry` 承载，GUI 同步 `BrewTime` 与 heat 字段。
- 本轮已移植：木制酿造台进行中配方现在只按 ingredient `Item` 锁定，不再按完整 `ItemStack` 比较，匹配源码 `ingredientID != itemstack1.getItem()` 行为。
- 本轮已移植：木制酿造台热源状态改为每 20 tick 从下方坩埚采样，不再每 tick 更新，减少与源码节奏差异。
- 本轮已移植：木制酿造台 NBT 现在只保存 `BrewTime`；heat 和进行中 ingredient 锁定状态在加载后从当前世界状态/物品栏重建，不再额外持久化 Legacy 专有字段。
- 本轮已移植：木制酿造台实现 1.12 `ISidedInventory`，将源码 `WorldlyContainer` 的上下/侧面插入抽取规则映射到 hopper 自动化路径；瓶槽自动插入只允许放入空槽。
- 本轮已移植：木制酿造台药水输出槽现在在玩家取出药水时触发 `ForgeEventFactory.onPlayerBrewedPotion` 和 `CriteriaTriggers.BREWED_POTION`，对齐 1.20 `WoodenBrewingStandContainer.PotionSlot.onTake` 与当前 1.12 原版酿造台输出槽的取出回调语义。
- 本轮已移植：玩家打开木制酿造台 GUI 时现在会记录 1.12 `StatList.BREWINGSTAND_INTERACTION`，映射 1.20 `Stats.INTERACT_WITH_BREWINGSTAND` 的方块交互统计。
- 本轮已移植：木制酿造台现在支持命名物品放置后的自定义 GUI 标题，并把 `CustomName` 持久化到机器 tile NBT 中，对齐 1.20 `WoodenStandBlock.setPlacedBy` 与 `BaseContainerBlockEntity` 的命名语义。

涉及文件：
- `src/main/java/elucent/eidolon/registries/ModPotions.java`
- `src/main/java/elucent/eidolon/registries/ModBlocks.java`
- `src/main/java/elucent/eidolon/tile/MachineInventoryTileEntity.java`
- `src/main/java/elucent/eidolon/tile/WoodenBrewingStandTileEntity.java`
- `src/main/java/elucent/eidolon/gui/WoodenBrewingStandContainer.java`
- `src/main/java/elucent/eidolon/gui/WoodenBrewingStandGui.java`

后续需要复核的差异：
- 游戏内验证需要确认下方坩埚 boiling 切换时 GUI heat 图标、brew 进度取消/完成、hopper 插入抽取、ingredient 容器物品掉落，以及 redstone/glowstone 被木制酿造台材料槽拒绝但仍可用于原版酿造链路。
- Reinforced 的 1.20 输入为 nautilus shell；Legacy 映射为 prismarine crystals，这是 1.12 无 nautilus shell 的版本差异。

## 事件与 Mixin（Events And Mixins）

源码侧行为：
- 1.20 使用 `Events` 处理 capability attachment/clone sync、goblet capture、codex barter AI 注册、wing/player ticking、potion restrictions、亡灵日照燃烧、drops、soul shard crystallization、head drops、warlock damage rules、ethereal-health absorption，以及卡在方块中时的速度处理。
- 1.20 mixin 覆盖 arrow invokers、container accessors、delayed render flushing、undead mob type override、Raven dash fall-flying、local player jump-charge UI、player renderer Raven layer insertion、potion brewing access 和 zombie-villager conversion。

Legacy 当前状态：
- `GameplayEvents`、`CurioEvents`、`ArmorEvents`、`KnowledgeEvents`、`SummonedEntityEvents` 和现有 mixin 覆盖了 1.12 Forge 中面向玩法的等价行为。
- 现有 Legacy mixin 覆盖亡灵生物属性（`EntityLivingBaseMixin`）、僵尸村民转化（`EntityZombieVillagerMixin`）和 Raven cloak dash flight（`RavenCloakFlightMixin`）。
- Legacy 有意不直接移植 1.20 `PotionBrewingMixin`/container accessor，因为 1.12 使用 Forge brewing recipe 注册和不同的 container API。
- Legacy 有意将 `AngelArrowEntity` 作为 1.12 自定义追踪箭处理，而不是包装任意内部 `AbstractArrow`，因此不需要 1.20 arrow invoker mixin。
- 本轮已移植：player data sync 事件围绕 Legacy 现有事件拆分收紧。login、dimension change 和 respawn 通过 `KnowledgeEvents` 同步；clone sync 仍保留在 `GameplayEvents`；完整 knowledge sync 现在会先清除客户端旧 cache。
- 本轮已移植：Codex barter AI 现在镜像源码事件注册。女巫和 cleric villager 会搜索附近掉落的 Codex 物品，按源码风格 barter delay 持有它们，然后返回带有 wicked 或 sacred sign 的 Codex。带标签的 Codex 进入玩家背包时会授予 sign，女巫/村民死亡时会掉落正在持有的 Codex。
- 本轮已移植：Goblet 捕获使用的 tile NBT 主键现在改为源码同名 `type`，同时保留旧 Legacy `EntityType` 读取兼容；非怪物死亡时最近 goblet 记录实体类型的事件路径仍由 `GameplayEvents` 处理。
- 本轮已移植：Villager sacrifice 现在要求找到的 unholy effigy 实际放在 stone altar 上，不再只检查施法点附近是否存在任意 stone altar，对齐 1.20 `AltarInfo.getAltarInfo(...).getAltar() == STONE_ALTAR` 的意图。
- 本轮已移植：death/drop 处理现在镜像 1.20 事件规则，覆盖 withered zombie brute、soul shard crystallization 和 cleaving axe head drop。Withered zombie brute 会把 zombie heart 掉落替换为 withered heart；reaper scythe/ritual 击杀会清除非玩家亡灵掉落，并用 `rand.nextInt(2 + looting)` 掷 soul shard；cleaving axe head drop 现在使用源码 1/20 基础概率，并按 looting 每级追加 1/40 掷骰。
- 本轮已移植：Warlock damage modifier 和 ethereal-health absorption 现在与源码一样运行在同一个 `LivingHurtEvent` 流程中。旧的 `ArmorEvents` 重复局部吸收已移除，因此 ethereal health 会消耗完整调整后的命中，并且只扣除一次调整后的数值。
- 本轮已移植：`AngelArrowEntity` 仍是 1.12 原生追踪箭，但现在保留源码可观察的箭行为：tipped-arrow 药水数据、spectral-arrow 命中发光行为、原始 pickup stack、creative/infinity pickup 限制，以及追踪目标不额外排除隐身实体的过滤语义。
- 本轮已移植：Undeath 使用限制现在遵循源码 `zombie_food` tag 意图。受影响实体只能开始使用 rotten flesh 或 zombie heart；其他可使用 stack 会被取消，不再只取消原版 `ItemFood`。
- 本轮已移植：Soulbone amulet 击杀现在通过 `SoulData` 授予 Eidolon ethereal health，匹配 1.20 capability 行为，不再给予普通治疗和原版 absorption hearts。
- 本轮已移植：Gravity belt 跌落结算现在只在 `LivingFallEvent` 把 fall distance 除以四，匹配源码事件；佩戴 tick 只用 1.12 motion 近似源码 Curios 的 `ENTITY_GRAVITY -0.60` 属性，不再每 tick 额外缩小 `fallDistance`，避免和摔落事件双重削减。
- 本轮已移植：Sanguine amulet 治疗现在只按实际应用的整数治疗量扣除 charge，匹配源码在极小缺失生命值附近的行为。
- 本轮已移植：Terminus mirror 现在也会阻挡 Legacy Eidolon spell projectile，即使它们的 1.12 damage source 没有标记为 `projectile`，匹配源码 `Projectile || SpellProjectileEntity` 检查。
- 本轮已移植：Void amulet 已收窄回源码语义：它只阻挡特定 projectile class 加 Eidolon spell projectile，而不是吸收全部 magic damage 和爆炸。
- 本轮已移植：spell projectile 命中行为已对齐源码。Soulfire 不再点燃命中实体或方块，Bonechill 现在使用 frost indirect damage source，Necromancer bolt 现在造成 `3 + difficulty`，并使用 Wither indirect damage source，而不是固定 magic damage 加 Wither 药水效果。
- 本轮已移植：Sapping sword 现在遵循源码命中流程。普通命中先建立无敌帧，然后清除目标 hurt resistance，施加独立的 2 点穿甲 wither hit，并只按该额外命中实际移除的生命值治疗攻击者。
- 本轮已移植：Warlock Boots 将 1.20 `StuckInBlockEvent` speed-penalty reduction 映射到 1.12 的具体减速方块路径。Soul sand 水平减速从原版 `0.4` 修正为源码等价 `0.7`；web 移动常量从 `0.25/0.05/0.25` 提高到 `0.625/0.525/0.625`，且只在实体穿着 Warlock Boots 时生效。
- 本轮已移植：Deathbringer 的 1.20 sword-sweep disable 通过定向 player attack mixin 处理，因此 1.12 不再仅因它继承 `ItemSword` 就把它视为可横扫。
- 本轮已移植：Angel's Sight 射击现在更接近源码/原版弓的行为包络。Legacy 不再在被包装箭的基础上额外添加固定 `+2` 伤害，使用源码风格 `1.0F` 不精准度，并恢复原版随机弓射击音调，同时保留 potion/spectral pickup 语义。
- 本轮已移植：Void Amulet 阻挡范围进一步从 1.12 `EntityFireball` 父类收窄到源码列出的 Large Fireball 与 Small Fireball 等价物，加上 potion、shulker bullet、llama spit 和 Eidolon spell projectile。这样可以避免阻挡 1.12 中 1.20 源码未包含的 fireball 子类，例如 Wither Skull 或 Dragon Fireball。
- 本轮已移植：Enervating Ring 不再在命中时施加 Legacy 专有 Weakness 和 Slowness。源码搜索显示 1.20 物品只是一个可右键装备的 Curio，带有 lore/registration，因此 Legacy 现在把它视为惰性物品，除非未来发现源码中还有其他行为。
- 本轮已移植：Undeath 日照燃烧补上源码的骑船检查。受 Undeath 影响的实体骑船时会用上方一格做天空可见性判断，和 1.20 `Events.onTarget` 中的 Boat 分支一致；食物限制仍只允许 rotten flesh 与 zombie heart，Hunger 与 Undeath 互斥逻辑保持不变。
- 本轮已移植：附魔灰现在保留 Legacy 原有薄片显示，并补齐源码风格的同层、上爬和下接连接状态，使已有 `enchanted_ash_up` 模型能被实际状态触发；同时对“当前有效属性”为亡灵的实体以及载有亡灵乘客的实体添加纵向屏障碰撞，对齐 1.20 `EnchantedAshBlock` 的 `isInvertedHealAndHarm()`/passenger 判定；受 Undeath 影响的玩家或生物也会被该屏障阻挡。

涉及文件：
- `src/main/java/elucent/eidolon/Eidolon.java`
- `src/main/java/elucent/eidolon/mixin/BlockSoulSandMixin.java`
- `src/main/java/elucent/eidolon/mixin/EntityMixin.java`
- `src/main/java/elucent/eidolon/mixin/EntityPlayerMixin.java`
- `src/main/java/elucent/eidolon/entity/AngelArrowEntity.java`
- `src/main/java/elucent/eidolon/entity/BonechillProjectileEntity.java`
- `src/main/java/elucent/eidolon/entity/NecromancerSpellEntity.java`
- `src/main/java/elucent/eidolon/entity/SoulfireProjectileEntity.java`
- `src/main/java/elucent/eidolon/entity/ai/GenericBarterGoal.java`
- `src/main/java/elucent/eidolon/event/GameplayEvents.java`
- `src/main/java/elucent/eidolon/spell/SpellHelper.java`
- `src/main/java/elucent/eidolon/spell/VillagerSacrificeSpell.java`
- `src/main/java/elucent/eidolon/tile/GobletTileEntity.java`
- `src/main/java/elucent/eidolon/item/ArmorEvents.java`
- `src/main/java/elucent/eidolon/item/CleavingAxeItem.java`
- `src/main/java/elucent/eidolon/item/ReaperScytheItem.java`
- `src/main/java/elucent/eidolon/item/SappingSwordItem.java`
- `src/main/java/elucent/eidolon/item/curio/CurioEvents.java`
- `src/main/java/elucent/eidolon/item/curio/SanguineAmuletItem.java`
- `src/main/java/elucent/eidolon/registries/ModBlocks.java`
- `src/main/java/elucent/eidolon/registries/ModItems.java`
- `src/main/java/elucent/eidolon/research/KnowledgeEvents.java`
- `src/main/java/elucent/eidolon/network/KnowledgeResetPacket.java`
- `src/main/java/elucent/eidolon/network/ModNetwork.java`
- `src/main/java/elucent/eidolon/proxy/ClientProxy.java`
- `src/main/java/elucent/eidolon/proxy/IProxy.java`
- `src/main/java/elucent/eidolon/util/KnowledgeUtil.java`
- `src/main/resources/assets/eidolon/lang/en_us.lang`
- `src/main/resources/eidolon.default.mixin.json`

后续需要复核的差异：
- 游戏内事件回归仍需要检查 angel arrow 射击/pickup/命中行为、warlock damage modifier、ethereal-health absorption、Undeath 食物/使用与日照行为、goblet capture、Codex barter sign grant，以及 Raven cloak 跳跃蓄力飞行手感；Undeath 的源码侧骑船日照分支已经补齐，剩余主要是实际客户端/服务端行为验证。
- Client delayed render batching 和 Raven player render layer 通过 Legacy renderer 实现，而不是源码 mixin；这里应在游戏内视觉验证，而不是逐字移植 1.20 render mixin。

## 实体与 AI（Entities And AI）

源码侧行为：
- `wraith` 是亡灵，会按亮度在白天燃烧，靠近流体时减缓下落/悬浮，攻击玩家，命中附加 Chilled，并使用 stray 声音。
- `zombie_brute` 是亡灵，会按亮度在白天燃烧，攻击玩家/村民/铁傀儡，属性为 40 生命、0.28 速度、5 伤害、6 护甲。
- `necromancer` 是亡灵，不会因距离自然消失，会施放三连 necromancer bolt，召唤 enthralled skeleton/zombie，并且只会主动索敌黑暗神明声望至少 50 的玩家。
- `raven` 是可驯服、可上肩、可飞行动物，会随时间掉落羽毛，吃 beetroot seed，并避免推动玩家。
- `slimy_slug` 是小型被动生物，会被 pumpkin seed 吸引，带 variant 数据；源码还包含一个特定 UUID 的索敌/反伤彩蛋。

Legacy 当前状态：
- Wraith、zombie brute、necromancer、raven、slimy slug、angel arrow、chant caster 和各类 projectile 都已注册 renderer 与 loot 兜底。
- Legacy wraith 与 zombie brute 的属性、AI 目标集、loot 兜底掉落、声音、命中 Chilled 和亡灵属性均由代码支撑。
- Legacy necromancer 有自定义 spell AI，用于延迟三连 projectile 和亡灵召唤，并带 visual packet。
- Legacy raven 映射到 1.12 `EntityParrot`，通过最接近的原版基类保留驯服/上肩行为，并额外实现 raven 羽毛计时和掉落。
- Legacy slimy slug 保留被动 AI、使用 pumpkin seed 繁殖、variant NBT/data sync 和 slime-ball 掉落。
- 本轮已移植：necromancer 最近玩家索敌现在会检查黑暗神明声望 `>= 50`，追踪距离降为 `12`，与 1.20 的索敌门槛一致。`HurtByTarget` 仍保留，所以被攻击后的 necromancer 仍会反击。
- 本轮已移植：necromancer 不再因距离过远自然消失，对齐源码 `removeWhenFarAway(false)`。
- 本轮已移植：necromancer 召唤的亡灵现在会获得与 Legacy 召唤法杖相同的 owner UUID 标记；共享的召唤实体事件会阻止其攻击主人、清除死亡掉落，并避免被吸收仪式消耗。
- 本轮已移植：necromancer 召唤亡灵现在直接在施法者当前位置生成 skeleton/zombie，并移除 Legacy 额外的随机偏移和 `onInitialSpawn` 变体/装备初始化，更贴近 1.20 `type.create`、`setPos`、`addFreshEntity`、`setTarget`、`EntityUtil.enthrall` 的流程；1.12 没有源码同名的 evoker prepare/cast spell 声音常量，因此施法音效仍使用现有 evocation illager 近似声音。
- 本轮已移植：wraith 和 zombie_brute 的日照燃烧现在使用源码的亮度阈值与概率判断，不再使用固定的可见天空判定。
- 本轮已移植：slimy_slug 保留源码的特定 UUID 彩蛋：匹配玩家会被设为可攻击目标，slug 攻击伤害为 `999`，且该玩家造成的伤害会反弹给玩家本人，同时 slug 忽略这次伤害。Legacy 没有额外添加近战 AI，因为 1.20 源码也没有。
- 本轮已移植：slimy_slug 不再保留 Legacy 额外的繁殖行为。1.20 源码只把 pumpkin seed 用作 `TemptGoal` 诱引物品，没有 `BreedGoal`、`FollowParentGoal` 或子实体创建流程；Legacy 现在移除 mate/follow-parent AI，并禁用繁殖食物判定，避免种子右键进入 1.12 动物繁殖路径。
- 本轮已移植：wraith 命中附加 Chilled 的持续时间改为源码公式 `100 + difficultyId * 100`，替代之前的 `* 80` 近似值。
- 本轮已移植：wraith 和 zombie_brute 的日照燃烧补上源码的骑船检查；实体骑船时会检查上方一格，再决定天空光是否点燃它。
- 本轮已移植：necromancer 施法节奏对齐源码数值：bolt 施法 `40/80`，召唤 `40/200`；移除 Legacy 额外的召唤随机门槛，bolt 纵向瞄准也改用源码的 `0.04 * distance / 2` 抬升项。
- 本轮已移植：raven 定时掉落羽毛前会播放源码里的鸡下蛋音效。
- 本轮已移植：raven AI 与交互从 1.12 `EntityParrot` 默认逻辑收紧为源码行为。诱引目标改为兔肉和甜菜种子；未驯服 Raven 只有甜菜种子能尝试驯服，驯服概率仍为 `1/10`；驯服后甜菜种子可进入繁殖状态并生成 Raven 子实体；主人空手/非食物交互会在落地时切换坐下；Raven 不再推动玩家。
- 本轮已移植：Raven 的羽毛掉落计时现在只作为运行时字段存在，不再写入实体 NBT，匹配 1.20 源码没有持久化 `featherTime` 的行为；同时通过 1.12 `fall(0, multiplier)` 映射把 Raven 坠落伤害收敛为 `0`，对齐源码 `calculateFallDamage`。
- 本轮已移植：slimy_slug 初始变体保持源码默认值 `0`。1.20 的生物群系变体逻辑仍是 TODO/注释状态，所以 Legacy 不再在自然生成时随机变体。
- 本轮已移植：slimy_slug 自然生成群系从宽泛的所有 taiga/spruce/pine/redwood/lush 名称匹配收窄为源码 `biome_modifier/slimy_slug.json` 的等价映射；1.12 中对应 old-growth taiga 的 redwood/mega taiga 系列仍保留，普通 taiga 不再生成，`lush_caves` 仅在存在同名模组群系时匹配。
- 本轮已移植：实体碰撞箱尺寸按 1.20 `Entities` 注册值收敛。wraith 为 `0.6 x 1.9`，zombie_brute 为 `1.2 x 2.5`，necromancer 为 `0.6 x 1.9`，raven 为 `0.375 x 0.5`，slimy_slug 为 `0.5 x 0.25`；这会影响命中框、推挤、生成空间、肩膀交互距离和近战距离等运行时表现。
- 本轮已移植：projectile 与 chant caster 的碰撞箱也按 1.20 注册值收敛。soulfire、bonechill、necromancer_spell 现在显式设为 `0.4 x 0.4`，chant_caster 从旧 `0.2 x 0.2` 收窄为 `0.1 x 0.1`，减少 1.12 默认实体尺寸对命中/射线检测的影响。

涉及文件：
- `src/main/java/elucent/eidolon/entity/BonechillProjectileEntity.java`
- `src/main/java/elucent/eidolon/entity/ChantCasterEntity.java`
- `src/main/java/elucent/eidolon/entity/NecromancerEntity.java`
- `src/main/java/elucent/eidolon/entity/NecromancerSpellEntity.java`
- `src/main/java/elucent/eidolon/entity/RavenEntity.java`
- `src/main/java/elucent/eidolon/entity/SlimySlugEntity.java`
- `src/main/java/elucent/eidolon/entity/SoulfireProjectileEntity.java`
- `src/main/java/elucent/eidolon/registries/ModEntities.java`
- `src/main/java/elucent/eidolon/spell/SummonedEntityEvents.java`
- `src/main/java/elucent/eidolon/entity/WraithEntity.java`
- `src/main/java/elucent/eidolon/entity/ZombieBruteEntity.java`

后续需要复核的差异：
- 游戏内实体回归需要验证自然生成/刷怪蛋生成、属性、攻击目标、掉落、特殊攻击、renderer animation 和 despawn 行为；raven 还要重点检查收窄碰撞箱后的上肩、跟随主人和坐下切换。
- Necromancer 召唤的亡灵需要在游戏内检查 owner-target 清理、无掉落死亡行为，以及跨区块保存/加载后的持久化；召唤代码生成 skeleton/zombie 兜底，因为 1.20 源码中的 TODO biome variant 本身也未完成。

## 资源与数据收口（Resource And Data Close-Out）

源码侧行为：
- `assets/eidolon/models/block/aludel.json`、`cabinet_bottom.json`、`cabinet_top.json`、`censer.json` 和 `coalfired_engine_on.json` 存在于 1.20 资源中，但代码搜索没有找到 1.20 运行时对 `aludel`、`censer` 或 `coalfired_engine` 的 block/item/tile 注册；`cabinet` 只找到一个独立的 `CabinetModel` 类。
- 1.20 中 `cabinet` 和 `coalfired_engine` 的 item model 作为资源文件存在，但没有源码 recipe、loot table、item registration 或 block registration 引用它们。
- 源码声音完全通过 `assets/eidolon/sounds.json` 和七个被引用的 `.ogg` 文件数据驱动。
- 源码结构保存在 `data/eidolon/structures` 下，共 14 个 NBT 模板。

Legacy 当前状态：
- `tools/check_data_resources.js` 现在会检查 blockstate、block model、item model、worktable recipe、crucible recipe、vanilla-style recipe、sounds、被引用 sound file、structure NBT file、modern item fallback 和 particle strategy。
- 自动化数据/资源检查当前通过：缺失 blockstate `0`，缺失 block model `0`，缺失 item model `0`，缺失 worktable/crucible recipe `0`，缺失 sounds `0`，缺失被引用 sound file `0`，缺失 structures `0`。
- `aludel` 和 `censer` 仍视为源码资源残留，因为两边都没有运行时注册或玩法代码。
- `cabinet` 和 `coalfired_engine` 仍视为 Legacy-only item extras/placeholders：Legacy 注册了物品形态并提供 model/lang，但没有 block/tile/recipe/loot 运行时路径。它们不计为缺失的 1.20 玩法，因为 1.20 源码没有对应的已注册功能。
- 1.20 particle JSON 被有意映射为 Legacy 代码驱动的 `VisualEffectPacket` 与 1.12 原生粒子，而不是复制为在 1.12 中无效的 1.20 particle asset。

涉及文件：
- `tools/check_data_resources.js`
- `docs/core-port-parity.md`

后续需要复核的差异：
- 游戏内验证仍应打开真实客户端日志，捕获静态 JSON 对比无法发现的缺失 texture/model/language 警告。
- `cabinet` 和 `coalfired_engine` 这类 Legacy-only placeholder item 应保持在配方和进度之外，除非未来设计明确把它们恢复为 1.12 功能。

## 后续移植步骤

1. AltarRitual 子类审计：随着游戏内验证推进，持续更新每个仪式的行为检查清单。
2. 游戏内召唤法杖回归：验证召唤法杖 hold/release 时机、无目标不消耗行为、旧计数器兼容性，以及 captured-NBT charge 兼容性。
3. 游戏内最小回归：Codex 获取、research 完成、worktable 原版回退、worktable Eidolon 配方、crucible 配方、altar ritual、soul enchanter、research table、JEI 视图、结构、sync event，以及所有已恢复实体。
