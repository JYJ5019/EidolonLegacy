# 剩余移植任务

范围：在不改变现有玩法、不添加 mixin 的前提下，对比
`../eidolon-1.20x` 与当前 1.12 Legacy 移植。核心注册项、仪式、坩埚配方、
工作台配方、战利品表和粒子触发点已经基本覆盖；剩余工作主要是 Minecraft
版本/API 差异造成的非 1:1 等价。

需要玩家进入游戏确认的手测内容已拆分到 `player-ingame-test-checklist.md`。
这些“手测/QA”动作在本文中暂标为完成；若后续测试发现问题，再按具体失败项
新开代码或资源修复任务。

## 边界

- 不添加 mixin。
- 不修改 `build.gradle`、`gradle.properties` 或任何 mixin 配置。
- 不把 1.20 专属实体/物品强行搬进 1.12；继续使用已文档化替代项，例如守卫者替代溺尸、恼鬼替代幻翼、海晶砂粒/海晶晶体替代现代海洋材料。
- 不移除 Legacy 兼容路径，除非后续单独确认玩法策略。

## 优先任务

| 优先级 | 模块 | 当前状态 | 下一步任务 |
| --- | --- | --- | --- |
| P1 | 玩家可见命名 | 语言文件已使用守卫者/恼鬼替代名，但文档容易漂移。 | 保持玩家指南、秘典、HEI/JEI 说明和 QA 清单统一使用“召唤守卫者”“召唤恼鬼”；替代关系只放在技术说明里。 |
| P1 | 术士靴减速方块补偿 | 已用无 mixin world tick 补偿术士靴在灵魂沙上的水平速度，并保留缓慢药水免疫和术士套伤害规则。 | 蛛网减速仍是 1.12 `Entity.move` 内部写死分支，无法不加 mixin 精准替代；已转入 `player-ingame-test-checklist.md` 作为已知限制验收项。 |
| P1 | 僵尸村民净化 | 源码用 mixin invoker 调 `finishConversion`；Legacy 用反射寻找 `finishConversion`/`func_190738_dp`，并已由 RuntimeDiagnostics 输出方法可用性。 | 游戏内验收已转入 `player-ingame-test-checklist.md`；若后续测试或 runtime dump 证明反射不可用，再修正反射名称。 |
| P1 | RuntimeDiagnostics | 诊断已覆盖多个注册、资源和世界生成缺口，并固定了仪式、配方、研究槽、世界生成说明、召唤替代关系、僵尸村民转化方法可用性。 | 后续新增现代替代路径时同步加入 dump/summary/failure，避免只靠文档说明。 |
| P2 | 现代配方替代 | 已在 `ritual-recipe-porting-status.md` 补充高炉、条件配方、切石配方到 Legacy 熔炉/OreDictionary/Eidolon 工作台的映射。 | 后续新增配方时同步维护映射表；玩家侧可获得性验收已转入 `player-ingame-test-checklist.md`。 |
| P2 | Tags 与 Curios 槽位 | 已在 `tags-oredict-harvest-porting-status.md` 补充 tag/OreDictionary、`eidolon:zombie_food` 配置替代、Curios/Baubles 槽位差异。 | 后续新增饰品或配置项时同步维护对照表；实际事件触发槽位验收已转入 `player-ingame-test-checklist.md`。 |
| P2 | Angel's Sight 箭矢 | 已在 `angels-sight-arrow-matrix.md` 收口矩阵：原版箭、药水箭、光灵箭覆盖低风险等价；公开字段/NBT 可复制项和 `EidolonPickupStack`、`EidolonSpectral` 已文档化；模组自定义箭的 protected 命中逻辑仍是无 mixin 限制。 | 后续只在确认玩法策略后再改代码；玩家侧箭矢矩阵验收已转入 `player-ingame-test-checklist.md`。 |
| P2 | Undeath 生物属性 | Eidolon 内部调用已改用 `Eidolon.getCreatureAttribute`；原版或其他模组直接调用仍看到原生属性。 | 未来新增 Eidolon 亡灵判定必须走 wrapper；补武器、仪式、附魔差值、治疗/伤害反转的回归检查。 |
| P2 | 研究任务槽 | 1.20 可动态追加槽位；Legacy 固定 9 个任务槽并有溢出诊断。 | 保持溢出诊断严格；新增可能超过 9 个物品槽的研究任务前，先设计分页或更大固定槽布局。 |
| P3 | Raven Cloak 状态模型 | Legacy 使用护甲 NBT 与客户端控制替代源码玩家能力状态和 fall-flying mixin。 | 玩家侧状态同步验收已转入 `player-ingame-test-checklist.md`；严格 1:1 fall-flying 状态仍为无 mixin 限制。 |
| P3 | Shader/发光粒子渲染 | Legacy fullbright 粒子保证可读性，但不是源码 additive/depth-mask 渲染类型。 | 决定是否接受 fullbright 近似；若不接受，设计基于 `RenderWorldLastEvent` 的无 mixin 延迟发光/蒸汽批次渲染。 |
| P3 | Codex/GUI 发光 | 符号/符文图标已通过 `LegacyShaders.beginSprite` 叠加轻量发光；源码仍有更完整的 `RenderType`/shader 页面渲染。 | 页面视觉验收已转入 `player-ingame-test-checklist.md`；不在本任务里重写整套渲染管线。 |
| P3 | 试剂管/蓄水罐渲染 | Legacy TESR 近似液体/蒸汽排序和混合。 | 玩家侧视觉验收已转入 `player-ingame-test-checklist.md`；只有视觉缺陷明显时再扩展 shader。 |
| P3 | 世界结构 | 模板、间距和战利品已近似；`worldgen-porting-status.md` 已补 biome_modifier 到 Legacy 矿物/自然生成逻辑的映射。 | 种子 QA 已转入 `player-ingame-test-checklist.md`；若追求更严格等价，先设计简化 jigsaw placer。 |
| P3 | Illwood 树 | Legacy 使用手写树生成器，且可能保留 Legacy 自然生成行为。 | 玩家侧树形验收已转入 `player-ingame-test-checklist.md`；若不接受当前树形，再按源码 trunk/foliage 行为调树形并记录差异。 |

## 验证门槛

- 运行 `.\gradlew.bat compileJava`。
- 搜索 Java 源码中的 mixin 注解和 SpongePowered mixin 包引用，预期无结果。
- 确认禁区文件没有变更：`build.gradle`、`gradle.properties`、`src/main/resources/*.mixins.json`、`src/main/resources/*.mixin.json`、`src/main/resources/META-INF/mods.toml`。
- 搜索玩家可见文档和语言文件，确认召唤替代名没有退回旧说法。
- 玩家进游戏验收统一按 `player-ingame-test-checklist.md` 执行；该文档已经承接
  Angel's Sight、Raven Cloak、世界生成、粒子/渲染、仪式与配方可获得性等手测项。
