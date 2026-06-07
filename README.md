# EidolonLegacy

中文 | [English](#english)

EidolonLegacy 是 Eidolon 的 Minecraft 1.12.2 / Cleanroom 移植版本。项目仍在开发中，但已经不再是空壳：当前版本包含研究、秘典、工作台、坩埚、祭坛仪式、部分工具武器、魔杖、基础材料与大量装饰方块。

- Mod 名称：`EidolonLegacy`
- Mod id：`eidolon`
- 当前版本：`1.0.0`
- 目标环境：Minecraft `1.12.2` / Cleanroom Loader
- 作者：GingerYJ
- 移植仓库：[GingerYJ/EidolonLegacy](https://github.com/GingerYJ/EidolonLegacy)
- 原项目：[Eidolon by elucent](https://github.com/elucent/eidolon)

## 已移植功能

- 基础材料与矿物：铅、银、白镴、奥术金、暗影宝石、粗矿掉落与熔炼。
- 世界生成：铅/银矿物、深层矿物、Illwood 树、实验室、Stray 塔和地下墓穴。
- 装饰与结构方块：平滑石砖、远古砖块、病木、骨堆、方尖碑、基座、火盆、祭坛、石手、暗蚀焦点、祭品盘、玻璃管等。
- 秘典 Codex：研究目录、配方目录、祭坛供物、仪式匕首采集、灵魂碎片获取说明。
- 研究系统：笔记工具、研究笔记、研究桌、分阶段提交任务、完成研究和学习研究。
- Worktable 工作台：3x3 主输入和 4 个试剂槽，支持 HEI / Codex 显示。
- Crucible 坩埚：流体需求、分步骤投料、搅拌、步骤确认、产物生成、长配方滚动显示。
- Altar Ritual 祭坛仪式：普通产物、转化、充能、召唤、吸收，以及火盆、石手、暗蚀焦点的交互流程。
- 供物系统：祭坛容量和力量、方块供物、祭品盘供物、供物分组数值。
- 工具与武器：仪式匕首、汲取之剑、劈裂之斧、反转之镐、收割者镰刀、死亡使者镰刀。
- 魔杖与法杖：魂火魔杖、寒骨魔杖、召唤法杖，支持充能值显示和相关交互。
- 饰品系统：接入 Bubbles/Baubles 风格装备栏，护符、戒指、腰带、护身符、头部饰品均可右键装备并触发对应效果。
- HEI 兼容：工作台、坩埚、祭坛仪式、祭坛供物、仪式匕首采集、灵魂碎片获取等分类。

更详细的物品和方块使用方法见：[docs/use.md](docs/use.md)；给玩家直接阅读的 TXT 教程见：[docs/player-guide.txt](docs/player-guide.txt)。

## CraftTweaker 支持

当前已接入 CraftTweaker 2，整合包作者可以新增、删除或替换以下内容：

- `mods.eidolon.Worktable`：添加、删除 Worktable 配方。
- `mods.eidolon.Crucible`：添加、删除坩埚配方。坩埚每一步最多 6 个投入物。
- `mods.eidolon.Altar`：添加、删除祭坛仪式；添加、删除祭坛供物数值。
- `mods.eidolon.Athame`：添加、删除仪式匕首采集规则。
- `mods.eidolon.Research`：添加、删除研究；添加、删除方块、实体、维度、流体触发来源。
- `mods.eidolon.Incubator`：添加、删除培养器配方。

祭坛接口当前覆盖普通产物、转化、血契制作、充能、召唤、吸收、净化、结晶、诱引、驱离、欺瞒、昼光和月光仪式。

脚本放入实例的 `scripts` 目录即可，例如：

```text
.minecraft/scripts/eidolon_legacy.zs
```

当前完整接口参数和中文逐行注释示例见：[docs/crafttweaker2-guide.txt](docs/crafttweaker2-guide.txt)。旧版 Markdown 参考见：[docs/crafttweaker-recipes.md](docs/crafttweaker-recipes.md)。

开发环境中还提供了 CT2 冒烟测试脚本：

```text
run/client/scripts/eidolon_ct2_smoke_test.zs
```

启动客户端后可查看 `run/client/crafttweaker.log`，确认脚本是否成功加载。

## 尚未实现或暂缓内容

- 玻璃管只完成模型和方向交互，尚未完成完整传输网络。
- 储罐、灵魂附魔台等部分方块目前未完成完整功能；培养器已具备基础配方处理和 CraftTweaker2 接口，但仍可能继续调整交互和表现。
- 部分实体、药水、视觉效果和高级仪式反馈仍不完整。
- 与 1.20 源码相比，仍有大量原版 Eidolon 后期内容尚未移植。

## 构建

正式构建：

```powershell
.\gradlew.bat clean build
```

构建成功后，正式运行 jar 位于：

```text
build/libs/EidolonLegacy-1.0.0.jar
```

不要把 `-dev.jar` 或 `-sources.jar` 当作正式运行 jar 放入 `mods`。

## 文档

- 使用教程：[docs/use.md](docs/use.md)
- CraftTweaker 接口：[docs/crafttweaker-recipes.md](docs/crafttweaker-recipes.md)
- 玩家 TXT 教程：[docs/player-guide.txt](docs/player-guide.txt)
- CraftTweaker2 中文魔改教程：[docs/crafttweaker2-guide.txt](docs/crafttweaker2-guide.txt)
- 移植进度：[docs/feature-status.md](docs/feature-status.md)

## 许可证

本移植项目代码见 [LICENSE](LICENSE)。原 Eidolon 源码许可证见 [LICENSE-EIDOLON.md](LICENSE-EIDOLON.md)。

---

## English

EidolonLegacy is a Minecraft 1.12.2 / Cleanroom port of Eidolon. The project is still in development, but it is no longer an empty shell: the current version includes research, Codex pages, Worktable recipes, Crucible recipes, Altar rituals, several tools and weapons, wands, base materials, and many decorative blocks.

- Mod name: `EidolonLegacy`
- Mod id: `eidolon`
- Version: `1.0.0`
- Target: Minecraft `1.12.2` / Cleanroom Loader
- Author: GingerYJ
- Port repository: [GingerYJ/EidolonLegacy](https://github.com/GingerYJ/EidolonLegacy)
- Original project: [Eidolon by elucent](https://github.com/elucent/eidolon)

## Ported Features

- Base materials and ores: lead, silver, pewter, arcane gold, shadow gems, raw ore drops, and smelting.
- World generation: lead/silver ores, deep ores, Illwood trees, labs, stray towers, and catacombs.
- Decorative and structure blocks: smooth stone sets, elder stone sets, illwood sets, bone piles, obelisks, plinths, braziers, altars, stone hands, necrotic focuses, offertory plates, glass tubes, and more.
- Codex: research index, recipe directories, altar offerings, athame harvest entries, and soul shard harvest entries.
- Research system: notetaking tools, research notes, research table, staged tasks, completed research, and learning research.
- Worktable: 3x3 main grid plus 4 reagent slots, with HEI and Codex display support.
- Crucible: required fluid, ordered input steps, stirring, step submission, output generation, and scrolling display for long recipes.
- Altar rituals: item output, transformation, charging, summoning, and absorption, using braziers, stone hands, and necrotic focuses.
- Altar offerings: capacity and power values, block offerings, plate offerings, and grouped offering values.
- Tools and weapons: Athame, Sapping Sword, Cleaving Axe, Reversal Pick, Reaper Scythe, and Deathbringer Scythe.
- Wands and staff: Soulfire Wand, Bonechill Wand, and Summoning Staff, with charge display and related interactions.
- Curio/trinket system: Bubbles/Baubles-style slots are wired for amulets, rings, belts, charms, and head curios, with right-click equip and gameplay effects.
- HEI integration: Worktable, Crucible, Altar Rituals, Altar Offerings, Athame Harvest, and Soul Shard Harvest categories.

For detailed item and block usage, see [docs/use.md](docs/use.md). A player-facing TXT guide is also available at [docs/player-guide.txt](docs/player-guide.txt).

## CraftTweaker Support

CraftTweaker 2 support is available for pack authors. You can add, remove, or replace:

- `mods.eidolon.Worktable`: Worktable recipes.
- `mods.eidolon.Crucible`: Crucible recipes. Each Crucible step supports up to 6 inputs.
- `mods.eidolon.Altar`: Altar rituals and altar offering values.
- `mods.eidolon.Athame`: Athame harvest rules.
- `mods.eidolon.Research`: Research definitions and block/entity/dimension/fluid triggers.
- `mods.eidolon.Incubator`: Incubator recipes.

The Altar API currently covers item output, item transformation, sanguine crafting, item charging, entity summoning, absorption, purify, crystal, allure, repelling, deceit, daylight, and moonlight rituals.

Place scripts in the instance `scripts` folder, for example:

```text
.minecraft/scripts/eidolon_legacy.zs
```

For the current complete API parameters and Chinese line-by-line examples, see [docs/crafttweaker2-guide.txt](docs/crafttweaker2-guide.txt). The older Markdown reference remains available at [docs/crafttweaker-recipes.md](docs/crafttweaker-recipes.md).

A development smoke-test script is included at:

```text
run/client/scripts/eidolon_ct2_smoke_test.zs
```

After launching the client, check `run/client/crafttweaker.log` to confirm whether the script loaded.

## Not Implemented Yet

- Glass tubes currently have model and direction interaction only; the full transfer network is not implemented.
- Some blocks such as cistern and soul enchanter do not yet have their full gameplay behavior. The incubator has basic recipe processing and CraftTweaker2 support, but may still receive interaction and presentation refinements.
- Some entities, potion behavior, visuals, and advanced ritual feedback are incomplete.
- Compared with the 1.20 source, many late-game Eidolon features are still not ported.

## Building

Build the release jar with:

```powershell
.\gradlew.bat clean build
```

The release jar will be generated at:

```text
build/libs/EidolonLegacy-1.0.0.jar
```

Do not use `-dev.jar` or `-sources.jar` as the normal runtime mod jar.

## Documentation

- User guide: [docs/use.md](docs/use.md)
- CraftTweaker API: [docs/crafttweaker-recipes.md](docs/crafttweaker-recipes.md)
- Player TXT guide: [docs/player-guide.txt](docs/player-guide.txt)
- CraftTweaker2 Chinese TXT guide: [docs/crafttweaker2-guide.txt](docs/crafttweaker2-guide.txt)
- Porting status: [docs/feature-status.md](docs/feature-status.md)

## License

This port is licensed under [LICENSE](LICENSE). The original Eidolon source license is available at [LICENSE-EIDOLON.md](LICENSE-EIDOLON.md).
