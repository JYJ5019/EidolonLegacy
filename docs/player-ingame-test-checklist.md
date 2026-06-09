# 玩家进游戏测试清单

范围：本清单只收集必须进入游戏才能确认的移植验收项。代码侧已经按“不改变现有玩法、不添加 mixin”的边界处理或文档化；以下内容输出到本文件后，在 `remaining-porting-tasks.md` 中对应的“手测/QA”动作可暂标为完成，后续由实际游戏测试结果再决定是否开新修复任务。

## 测试原则

- 不把测试失败直接解释为需要新增 mixin；先判断是否属于已记录的 1.12 替代或无 mixin 限制。
- 优先使用新建世界或备份世界测试世界生成、结构、矿物和自然生成实体。
- 记录测试时的 Minecraft 版本、Cleanroom/Forge 环境、模组列表、世界种子、坐标和配置文件差异。
- 如果测试项涉及玩家可见文字，统一使用“召唤守卫者”和“召唤恼鬼”。

## P1 必测项

| 模块 | 游戏内测试内容 | 通过标准 | 依据 |
| --- | --- | --- | --- |
| 僵尸村民净化仪式 | 摆放净化仪式，使用僵尸村民作为目标，完整运行仪式。 | 仪式结束后僵尸村民能完成转化；若失败，先查看 runtime dump 中 `finishConversion`/`func_190738_dp` 可用性。 | `src/main/java/elucent/eidolon/spell/AltarRitual.java`、`src/main/java/elucent/eidolon/diagnostics/RuntimeDiagnostics.java` |
| 术士靴减速方块 | 穿术士靴分别经过灵魂沙、蛛网，并对照未穿时速度。 | 灵魂沙补偿生效；蛛网无法无 mixin 精准等价时，按已知限制记录，不作为本轮代码缺口。 | `src/main/java/elucent/eidolon/event/GameplayEvents.java`、`docs/remaining-porting-tasks.md` |
| 玩家可见召唤替代名 | 打开秘典、JEI/HEI、语言文本和召唤仪式结果说明。 | 玩家看见的是“召唤守卫者”“召唤恼鬼”，不是带替代关系括注，也不是直接承诺 1.12 不存在的溺尸/幻翼实体。 | `src/main/resources/assets/eidolon/lang/zh_cn.lang`、`docs/ritual-recipe-porting-status.md` |

## 仪式与配方

| 模块 | 游戏内测试内容 | 通过标准 | 依据 |
| --- | --- | --- | --- |
| 魔法工作台 | 按 HEI/秘典放入 3x3 主材料和 4 个试剂槽，分别测试普通工作台配方、带试剂配方、smooth_stone 切石替代配方、原版 3x3 配方不回退和破坏掉落。 | 输出槽刷新正确，试剂槽只参与 Eidolon 工作台配方，原版工作台配方不会在 Eidolon 工作台产出，破坏后内部物品正常掉落。 | `docs/player-guide.txt`、`src/main/java/elucent/eidolon/recipes/WorktableRecipes.java` |
| 坩埚步骤流程 | 测试加液体、投入多步材料、搅拌次数、空手确认、错误步骤失败、潜行清空和破坏掉落。 | 必须手动确认步骤；错误步骤清空并反馈；最后一步生成产物；已投入和已记录物品的掉落行为符合说明。 | `docs/player-guide.txt`、`src/main/java/elucent/eidolon/tile/CrucibleTileEntity.java` |
| 培养器流程 | 测试腐肉 + 灵魂碎片、僵尸之心 + 死亡精华等培养器配方，观察输入、输出、进度和切换配方。 | 输入匹配时推进，输出槽满或不匹配时停止/归零，切换配方会重新开始。 | `docs/player-guide.txt`、`src/main/java/elucent/eidolon/recipes/IncubatorRecipes.java` |
| 现代配方替代 | 在 JEI/HEI 或实际合成中检查高炉替代熔炉、smooth_stone 切石替代工作台配方、条件粉尘熔炼。 | 1.20 对应材料仍可在 1.12 获得；不要求高炉速度、切石台 UI 或 datapack 条件加载 1:1。 | `docs/ritual-recipe-porting-status.md`、`src/main/java/elucent/eidolon/registries/ModRecipes.java` |
| 全部现代仪式 ID | 逐个运行或用创造模式快速验证 `absorption`、`allure`、`bonechill_recharging`、`crystal`、`daylight`、`deceit`、`moonlight`、`purify`、`repelling`、两个 sanguine、两个法杖充能和全部召唤仪式。 | 仪式可触发、消耗物品正确、结果符合 1.12 替代文档。 | `docs/ritual-recipe-porting-status.md`、`src/main/resources/assets/eidolon/altar_rituals` |
| 祭坛供物与祭品盘 | 摆放石祭坛/木祭坛、火盆、石手、暗蚀焦点和祭品盘，测试容量/力量供给、相同分组取最高值、焦点物品和材料消耗位置。 | 祭品盘只提供容量/力量，不被当作仪式材料消耗；石手、火盆和暗蚀焦点各自承担正确材料角色。 | `docs/player-guide.txt`、`src/main/java/elucent/eidolon/spell/AltarRitual.java` |
| Sanguine 血契仪式 | 测试汲取之剑血契、血红护符血契。 | 生命消耗分别符合文档中的 `20` 和 `40`；结果物品正确。 | `src/main/resources/assets/eidolon/altar_rituals/sanguine_sapping_sword.json`、`src/main/resources/assets/eidolon/altar_rituals/sanguine_sanguine_amulet.json` |
| 仪式匕首植物采集 | 用仪式匕首右键蕨、滨菊、睡莲、丛林树叶等来源。 | 来源方块被正确处理，掉落阿文尼亚嫩枝、梅拉默根、奥安娜花、希尔德里安种子等对应产物；植物方块碎屑可见。 | `docs/player-guide.txt`、`src/main/java/elucent/eidolon/event/GameplayEvents.java` |

## 饰品、装备与实体能力

| 模块 | 游戏内测试内容 | 通过标准 | 依据 |
| --- | --- | --- | --- |
| Angel's Sight 普通箭 | 佩戴 Angel's Sight，用普通箭射击。 | 箭矢追踪、伤害、消耗、弓耐久和落地拾取均正常。 | `docs/angels-sight-arrow-matrix.md`、`src/main/java/elucent/eidolon/item/curio/CurioEvents.java` |
| Angel's Sight 附魔弓 | 使用满蓄力、Power、Punch、Flame、Infinity 和创造模式分别测试。 | 暴击、增伤、击退、点燃、无限/创造拾取规则符合文档；事件取消后不生成重复原版箭。 | `docs/angels-sight-arrow-matrix.md` |
| Angel's Sight 药水箭/光灵箭 | 使用至少一种可见药水箭和光灵箭；落地后保存重进再拾取。 | 药水效果保留，光灵箭命中给予 200 ticks 发光，`EidolonPickupStack` 和 `EidolonSpectral` 行为正确。 | `docs/angels-sight-arrow-matrix.md`、`src/main/java/elucent/eidolon/entity/AngelArrowEntity.java` |
| Angel's Sight 模组箭 | 如环境中有其他模组箭，分别测试 vanilla 状态型箭和依赖 protected hit 的自定义箭。 | vanilla 状态型箭尽量保留基础状态；protected hit 差异按无 mixin 限制记录。 | `docs/angels-sight-arrow-matrix.md` |
| Raven Cloak | 装备 Raven Cloak 后测试拍翼、蓄力冲刺、潜行缓降、落地、换装备、死亡、重登、第三人称观察。 | 服务器运动、摔落保护、状态清理和第三人称翅膀/披风同步正确；不要求原版 `isFallFlying` 状态 1:1。 | `docs/raven-cloak-mixin-replacement-audit.md`、`src/main/java/elucent/eidolon/item/RavenCloakItem.java` |
| Baubles/Curios 替代槽位 | 测试 `angels_sight`、`mind_shielding_plate`、`glass_hand`、`terminus_mirror`、`warded_mail`、`raven_cloak` 的实际装备槽位和效果触发。 | 保持 Legacy 已有 Baubles/护甲槽行为，不因 1.20 Curios 标签改变玩家存档预期。 | `docs/tags-oredict-harvest-porting-status.md` |
| Undeath 相关判定 | 测试亡灵状态下武器、仪式、治疗/伤害反转、附魔差值或其他 Eidolon 内部亡灵判定。 | Eidolon 内部效果按 `Eidolon.getCreatureAttribute` 包装结果工作；其他模组直接读原版属性的差异按限制记录。 | `docs/undeath-creature-attribute-audit.md`、`src/main/java/elucent/eidolon/Eidolon.java` |
| 研究桌固定任务槽 | 完成默认研究流程，检查 9 个固定任务槽的显示、shift-click、提交、关闭掉落和客户端刷新。 | 任务槽不溢出、不吞物品、不显示错位；若未来研究需要超过 9 个物品槽，再开分页或扩展布局任务。 | `docs/research-task-slot-diagnostics.md`、`src/main/java/elucent/eidolon/gui/ResearchTableContainer.java` |
| 笔记工具与研究笔记 | 用笔记工具右键已绑定的方块、实体、流体或空气来源，生成研究笔记并推进研究桌流程，最后右键学习完成研究。 | 研究笔记可生成、任务可提交、完成研究可学习，前置研究会影响秘典解锁显示。 | `docs/player-guide.txt`、`src/main/java/elucent/eidolon/item/NotetakingToolsItem.java` |
| 镰刀灵魂碎片掉落 | 用收割者镰刀和死亡使者镰刀击杀僵尸、骷髅、怨灵等亡灵实体，并测试抢夺附魔差异。 | 额外灵魂碎片掉落符合当前实现，原本掉落物不被删除，死亡使者镰刀收益高于收割者镰刀。 | `docs/player-guide.txt`、`src/main/java/elucent/eidolon/item/ReaperScytheItem.java`、`src/main/java/elucent/eidolon/item/DeathbringerScytheItem.java` |
| 魔杖与召唤法杖玩法 | 测试魂火魔杖、寒骨魔杖释放和充能；召唤法杖吸收亡灵、潜行切换类型、普通右键召唤并消耗次数。 | 弹射物、充能、吸收记录、类型切换和召唤消耗都能按玩家指南完成。 | `docs/player-guide.txt`、`src/main/java/elucent/eidolon/item/SoulfireWandItem.java`、`src/main/java/elucent/eidolon/item/BonechillWandItem.java`、`src/main/java/elucent/eidolon/item/SummoningStaffItem.java` |

## 世界生成

| 模块 | 游戏内测试内容 | 通过标准 | 依据 |
| --- | --- | --- | --- |
| Lab 结构 | 新世界运行 `/eidolon_locate_structure lab 512 load`，传送检查结构。 | 结构在地下、可进入、箱子战利品存在。 | `docs/worldgen-porting-status.md`、`src/main/java/elucent/eidolon/world/EidolonWorldGenerator.java` |
| Stray Tower 结构 | 在针叶林/雪地附近运行 `/eidolon_locate_structure stray_tower 512 load`。 | 塔在地表合理生成，不明显悬空或深埋。 | `docs/worldgen-porting-status.md` |
| Catacomb 结构 | 运行 `/eidolon_locate_structure catacomb 512 load`，传送检查走廊与房间。 | 中心走廊接近报告坐标，房间连接合理，战利品正常。 | `docs/worldgen-porting-status.md`、`src/main/java/elucent/eidolon/world/EidolonStructureGenerator.java` |
| 铅/银矿 | 在新生成矿区检查 lead/silver/deep 变体。 | 分布符合 1.12 可玩范围；不要求复制 1.20 负 Y 高度。 | `docs/worldgen-porting-status.md` |
| 自然生成实体 | 在森林、巨型针叶林/红木针叶林、普通主世界怪物生成环境观察 raven、slimy_slug、wraith、zombie_brute。 | 默认权重下能自然生成；若没有生成，先检查 `eidolon-common.cfg` 中对应权重是否为 0。 | `docs/worldgen-porting-status.md`、`src/main/java/elucent/eidolon/registries/ModEntities.java` |
| Illwood 树 | 种植或自然生成 Illwood，观察树干、树冠、树苗成长和掉落。 | 接受当前 Legacy 树形时记录为通过；若不接受，再开单独树形调整任务。 | `src/main/java/elucent/eidolon/world/IllwoodTreeGenerator.java` |

## 视觉与渲染

| 模块 | 游戏内测试内容 | 通过标准 | 依据 |
| --- | --- | --- | --- |
| 粒子总体验收 | 在白天、夜晚、雨天、洞穴和高粒子密度场景观察仪式、坩埚、火盆、法杖、汲取之剑、收割者/死亡使者武器、召唤法杖、咏唱、笔记工具和实体施法粒子。 | 颜色、形状、时机、运动可读；缺少 1.20 additive bloom 只作为渲染近似记录。 | `docs/effects-porting-status.md`、`src/main/java/elucent/eidolon/particle`、`src/main/java/elucent/eidolon/network/VisualEffectPacket.java` |
| 坩埚粒子 | 检查待机沸腾气泡、1/8 蒸汽、成功蒸汽/闪光爆发、失败烟雾爆发。 | 能看出状态变化和成功/失败反馈；不额外要求源码 shader 亮度。 | `docs/effects-porting-status.md` |
| 火盆状态粒子 | 检查火盆点燃、常规火焰/烟雾/闪光、寻找仪式时的灵火环、仪式火焰、仪式完成和熄灭。 | 每个状态能被玩家分辨，吸收前粒子可见但弱于吸收后的火柱。 | `docs/effects-porting-status.md` |
| 石手/焦点/祭品盘消耗 | 仪式进行中观察石手材料、暗蚀焦点材料、祭品盘或生命祭品被吸入中心/焦点的线状灵火和烟雾。 | 吸收材料时方向和目标明确；次级灵魂宝石、死亡精华、灌注之骨、邪纹布、怨灵之心、魂骨护符、法杖充能、净化、结晶、诱引、驱离、欺瞒、昼光、月光、血契、吸收和全部召唤仪式都至少抽样覆盖。 | `docs/effects-porting-status.md` |
| 仪式完成与特殊仪式反馈 | 检查仪式完成火焰/闪光、crystallize 爆发、absorption consume/burst、summon crystallize、daylight/moonlight 到时闪光。 | 关键完成反馈明显；召唤与吸收走各自特殊反馈，不强行套普通完成反馈。 | `docs/effects-porting-status.md` |
| 武器和法杖反馈 | 测试 soulfire/bonechill wand、summoning staff、sapping sword、reaper scythe、deathbringer scythe。 | 投射物轨迹、命中、吸血线、结晶爆发、弧光斩击能被玩家看清。 | `docs/effects-porting-status.md` |
| 生物施法粒子 | 观察死灵法师手部施法粒子、召唤完成爆发、死灵法术延迟/激活轨迹和命中。 | 施法阶段、召唤完成和投射物状态可读，没有明显重复或缺失。 | `docs/effects-porting-status.md` |
| 咏唱多人同步 | 在多人或第二观察客户端下测试黑暗祈祷、光明祈祷、动物献祭、村民献祭、黑暗之触。 | 施法者和旁观者都能看到对应火焰/音效，且不会出现重复播放。 | `docs/effects-porting-status.md`、`src/main/java/elucent/eidolon/network/SpellCastPacket.java` |
| 工具/环境粒子 | 测试笔记工具研究闪光、蜡烛/烛台原版烟雾和火焰、仪式匕首植物碎屑、可复用寒冷替代效果的玻璃破碎音效和冰方块碎屑。 | 各效果在对应触发点出现；寒冷替代效果若当前没有源码触发点，则作为未来复用效果记录。 | `docs/effects-porting-status.md` |
| 秘典 GUI 发光 | 打开符号页、符文页、吟唱页、仪式页和当前吟唱栏。 | 符号/符文轻量发光可见，后续 GUI 元素不被 blend 状态污染；不要求 1.20 `RenderType` 1:1。 | `docs/codex-gui-rendering-audit.md`、`src/main/java/elucent/eidolon/gui/CodexGui.java` |
| 试剂管/蓄水罐 TESR | 在白天、夜晚、雨天和高密度方块旁观察液体、蒸汽、排序和混合。 | 没有明显穿帮、错误排序或不可读透明混合；若缺陷明显再开 shader/TESR 调整任务。 | `docs/remaining-porting-tasks.md` |

## 回归记录模板

每次测试建议记录：

```text
测试日期：
测试环境：
世界种子/坐标：
配置差异：
测试项：
结果：通过 / 失败 / 已知限制
截图或视频：
备注：
```
